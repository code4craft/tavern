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
public class TavernSpringObjectFactory extends StrutsSpringObjectFactory {

    private boolean useClassCache;

    private Map<String,Object> classes = new HashMap<String, Object>();

    @Inject
    public TavernSpringObjectFactory(
            @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE,required=false) String autoWire,
            @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE_ALWAYS_RESPECT,required=false) String alwaysAutoWire,
            @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE,required=false) String useClassCacheStr,
            @Inject ServletContext servletContext,
            @Inject(StrutsConstants.STRUTS_DEVMODE) String devMode,
            @Inject Container container) {

        super(autoWire,alwaysAutoWire,useClassCacheStr,servletContext,devMode,container);
        useClassCache = "true".equals(useClassCacheStr);
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

    public Class getClassInstance(String className) throws ClassNotFoundException {
        Class clazz = null;
        if (useClassCache) {
            synchronized(classes) {
                // this cache of classes is needed because Spring sucks at dealing with situations where the
                // class instance changes
                clazz = (Class) classes.get(className);
            }
        }

        if (clazz == null) {
            ApplicationContext applicationContext = Tavern.getCurrentApplication(className).getApplicationContext();
            if (applicationContext.containsBean(className)) {
                clazz = applicationContext.getBean(className).getClass();
            } else {
                clazz = super.getClassInstance(className);
            }

            if (useClassCache) {
                synchronized(classes) {
                    classes.put(className, clazz);
                }
            }
        }

        return clazz;
    }

}
