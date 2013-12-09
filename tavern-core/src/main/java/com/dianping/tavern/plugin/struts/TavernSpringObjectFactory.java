package com.dianping.tavern.plugin.struts;

import com.dianping.tavern.Tavern;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.spring.SpringObjectFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.spring.ClassReloadingXMLWebApplicationContext;
import org.apache.struts2.spring.StrutsSpringObjectFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yihua.huang@dianping.com
 */
public class TavernSpringObjectFactory extends SpringObjectFactory {

    private static final Logger LOG = LoggerFactory.getLogger(StrutsSpringObjectFactory.class);

    //@Inject
    //public StrutsSpringObjectFactory(
    //        @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE,required=false) String autoWire,
    //        @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE,required=false) String useClassCacheStr,
    //        @Inject ServletContext servletContext) {
    //    this(autoWire, "false", useClassCacheStr, servletContext);
    //}

    @Inject
    public TavernSpringObjectFactory(
            @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE,required=false) String autoWire,
            @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE_ALWAYS_RESPECT,required=false) String alwaysAutoWire,
            @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE,required=false) String useClassCacheStr,
            @Inject ServletContext servletContext,
            @Inject(StrutsConstants.STRUTS_DEVMODE) String devMode,
            @Inject Container container) {

        super();
        boolean useClassCache = "true".equals(useClassCacheStr);
        if (LOG.isInfoEnabled()) {
            LOG.info("Initializing Struts-Spring integration...");
        }

        Object rootWebApplicationContext =  servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        if(rootWebApplicationContext instanceof RuntimeException){
            RuntimeException runtimeException = (RuntimeException)rootWebApplicationContext;
            LOG.fatal(runtimeException.getMessage());
            return;
        }

        ApplicationContext appContext = (ApplicationContext) rootWebApplicationContext;
        if (appContext == null) {
            // uh oh! looks like the lifecycle listener wasn't installed. Let's inform the user
            String message = "********** FATAL ERROR STARTING UP STRUTS-SPRING INTEGRATION **********\n" +
                    "Looks like the Spring listener was not configured for your web app! \n" +
                    "Nothing will work until WebApplicationContextUtils returns a valid ApplicationContext.\n" +
                    "You might need to add the following to web.xml: \n" +
                    "    <listener>\n" +
                    "        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>\n" +
                    "    </listener>";
            LOG.fatal(message);
            return;
        }

        String watchList = container.getInstance(String.class, "struts.class.reloading.watchList");
        String acceptClasses = container.getInstance(String.class, "struts.class.reloading.acceptClasses");
        String reloadConfig = container.getInstance(String.class, "struts.class.reloading.reloadConfig");

        if ("true".equals(devMode)
                && StringUtils.isNotBlank(watchList)
                && appContext instanceof ClassReloadingXMLWebApplicationContext) {
            //prevent class caching
            useClassCache = false;

            ClassReloadingXMLWebApplicationContext reloadingContext = (ClassReloadingXMLWebApplicationContext) appContext;
            reloadingContext.setupReloading(watchList.split(","), acceptClasses, servletContext, "true".equals(reloadConfig));
            if (LOG.isInfoEnabled()) {
                LOG.info("Class reloading is enabled. Make sure this is not used on a production environment!", watchList);
            }

            setClassLoader(reloadingContext.getReloadingClassLoader());

            //we need to reload the context, so our isntance of the factory is picked up
            reloadingContext.refresh();
        }

        this.setApplicationContext(appContext);

        int type = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;   // default
        if ("name".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
        } else if ("type".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
        } else if ("auto".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;
        } else if ("constructor".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;
        } else if ("no".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_NO;
        }
        this.setAutowireStrategy(type);

        this.setUseClassCache(useClassCache);

        this.setAlwaysRespectAutowireStrategy("true".equalsIgnoreCase(alwaysAutoWire));

        if (LOG.isInfoEnabled()) {
            LOG.info("... initialized Struts-Spring integration successfully");
        }
    }

    @Override
	public Object buildBean(String beanName, Map<String, Object> extraContext, boolean injectInternal) throws Exception {
		Object o;
		ApplicationContext applicationContext = Tavern.getCurrentApplication(beanName).getApplicationContext();
		if (applicationContext.containsBean(beanName)) {
			o = applicationContext.getBean(beanName);
		} else {
			Class beanClazz = getClassInstance(beanName);
			o = buildBean(beanClazz, extraContext);
		}
		if (injectInternal) {
			injectInternalBeans(o);
		}
		return o;
	}

	public Object autoWireBean(Object bean, AutowireCapableBeanFactory autoWiringFactory) {
		autoWiringFactory = findAutoWiringBeanFactory(Tavern.getCurrentApplication(bean.getClass())
				.getApplicationContext());
		if (autoWiringFactory != null) {
			autoWiringFactory.autowireBeanProperties(bean, autowireStrategy, false);
		}
		if (bean instanceof ApplicationContextAware) {
			((ApplicationContextAware) bean).setApplicationContext(appContext);
		}

		injectInternalBeans(bean);

		return bean;
	}

}
