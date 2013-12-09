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
public class TavernSpringObjectFactory extends ObjectFactory {

    protected ApplicationContext appContext;
    protected AutowireCapableBeanFactory autoWiringFactory;
    protected int autowireStrategy = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
    private final Map<String, Object> classes = new HashMap<String, Object>();
    private boolean useClassCache = true;
    private boolean alwaysRespectAutowireStrategy = false;

    @Inject(value="applicationContextPath",required=false)
    public void setApplicationContextPath(String ctx) {
        if (ctx != null) {
            setApplicationContext(new ClassPathXmlApplicationContext(ctx));
        }
    }

    /**
     * Set the Spring ApplicationContext that should be used to look beans up with.
     *
     * @param appContext The Spring ApplicationContext that should be used to look beans up with.
     */
    public void setApplicationContext(ApplicationContext appContext)
            throws BeansException {
        this.appContext = appContext;
        autoWiringFactory = findAutoWiringBeanFactory(this.appContext);
    }

    /**
     * Sets the autowiring strategy
     *
     * @param autowireStrategy
     */
    public void setAutowireStrategy(int autowireStrategy) {
        switch (autowireStrategy) {
            case AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT:
                if (LOG.isInfoEnabled()) {
                    LOG.info("Setting autowire strategy to autodetect");
                }
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_BY_NAME:
                if (LOG.isInfoEnabled()) {
                    LOG.info("Setting autowire strategy to name");
                }
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE:
                if (LOG.isInfoEnabled()) {
                    LOG.info("Setting autowire strategy to type");
                }
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR:
                if (LOG.isInfoEnabled()) {
                    LOG.info("Setting autowire strategy to constructor");
                }
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_NO:
                if (LOG.isInfoEnabled()) {
                    LOG.info("Setting autowire strategy to none");
                }
                this.autowireStrategy = autowireStrategy;
                break;
            default:
                throw new IllegalStateException("Invalid autowire type set");
        }
    }

    public int getAutowireStrategy() {
        return autowireStrategy;
    }


    /**
     * If the given context is assignable to AutowireCapbleBeanFactory or contains a parent or a factory that is, then
     * set the autoWiringFactory appropriately.
     *
     * @param context
     */
    protected AutowireCapableBeanFactory findAutoWiringBeanFactory(ApplicationContext context) {
        if (context instanceof AutowireCapableBeanFactory) {
            // Check the context
            return (AutowireCapableBeanFactory) context;
        } else if (context instanceof ConfigurableApplicationContext) {
            // Try and grab the beanFactory
            return ((ConfigurableApplicationContext) context).getBeanFactory();
        } else if (context.getParent() != null) {
            // And if all else fails, try again with the parent context
            return findAutoWiringBeanFactory(context.getParent());
        }
        return null;
    }


    /**
     * @param clazz
     * @param extraContext
     * @throws Exception
     */
    @Override
    public Object buildBean(Class clazz, Map<String, Object> extraContext) throws Exception {
        Object bean;

        try {
            // Decide to follow autowire strategy or use the legacy approach which mixes injection strategies
            if (alwaysRespectAutowireStrategy) {
                // Leave the creation up to Spring
                bean = autoWiringFactory.createBean(clazz, autowireStrategy, false);
                injectApplicationContext(bean);
                return injectInternalBeans(bean);
            } else {
                bean = autoWiringFactory.autowire(clazz, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
                bean = autoWiringFactory.applyBeanPostProcessorsBeforeInitialization(bean, bean.getClass().getName());
                // We don't need to call the init-method since one won't be registered.
                bean = autoWiringFactory.applyBeanPostProcessorsAfterInitialization(bean, bean.getClass().getName());
                return autoWireBean(bean, autoWiringFactory);
            }
        } catch (UnsatisfiedDependencyException e) {
            if (LOG.isErrorEnabled())
                LOG.error("Error building bean", e);
            // Fall back
            return autoWireBean(super.buildBean(clazz, extraContext), autoWiringFactory);
        }
    }

    public Object autoWireBean(Object bean) {
        return autoWireBean(bean, autoWiringFactory);
    }


    private void injectApplicationContext(Object bean) {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(appContext);
        }
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
            if (appContext.containsBean(className)) {
                clazz = appContext.getBean(className).getClass();
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

    /**
     * This method sets the ObjectFactory used by XWork to this object. It's best used as the "init-method" of a Spring
     * bean definition in order to hook Spring and XWork together properly (as an alternative to the
     * org.apache.struts2.spring.lifecycle.SpringObjectFactoryListener)
     * @deprecated Since 2.1 as it isn't necessary
     */
    @Deprecated public void initObjectFactory() {
        // not necessary anymore
    }

    /**
     * Allows for ObjectFactory implementations that support
     * Actions without no-arg constructors.
     *
     * @return false
     */
    @Override
    public boolean isNoArgConstructorRequired() {
        return false;
    }

    /**
     *  Enable / disable caching of classes loaded by Spring.
     *
     * @param useClassCache
     */
    public void setUseClassCache(boolean useClassCache) {
        this.useClassCache = useClassCache;
    }

    /**
     * Determines if the autowire strategy is always followed when creating beans
     *
     * @param alwaysRespectAutowireStrategy True if the strategy is always used
     */
    public void setAlwaysRespectAutowireStrategy(boolean alwaysRespectAutowireStrategy) {
        this.alwaysRespectAutowireStrategy = alwaysRespectAutowireStrategy;
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

    private static final Logger LOG = LoggerFactory.getLogger(TavernSpringObjectFactory.class);

    //@Inject
    //public StrutsSpringObjectFactory(
    //        @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE,required=false) String autoWire,
    //        @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE,required=false) String useClassCacheStr,
    //        @Inject ServletContext servletContext) {
    //    this(autoWire, "false", useClassCacheStr, servletContext);
    //}

    /**
     * Constructs the spring object factory
     * @param autoWire The type of autowiring to use
     * @param alwaysAutoWire Whether to always respect the autowiring or not
     * @param useClassCacheStr Whether to use the class cache or not
     * @param servletContext The servlet context
     * @since 2.1.3
     */
    @Inject
    public TavernSpringObjectFactory(
            @Inject(value= StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE,required=false) String autoWire,
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
}
