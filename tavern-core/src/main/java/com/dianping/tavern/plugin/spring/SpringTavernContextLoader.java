package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.core.CollectionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @author code4crafter@gmail.com
 * Vender from {@link org.springframework.web.context.ContextLoader}
 */
public class SpringTavernContextLoader {

	private Application application;

	public SpringTavernContextLoader(Application application) {
		this.application = application;
	}


    /**
     * Config param for the root WebApplicationContext implementation class to
     * use: "<code>contextClass</code>"
     */
    public static final String CONTEXT_CLASS_PARAM = "contextClass";

    /**
     * Name of servlet context parameter (i.e., "<code>contextConfigLocation</code>")
     * that can specify the config location for the root context, falling back
     * to the implementation's default otherwise.
     * @see org.springframework.web.context.support.XmlWebApplicationContext#DEFAULT_CONFIG_LOCATION
     */
    public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";

    /**
     * Optional servlet context parameter (i.e., "<code>locatorFactorySelector</code>")
     * used only when obtaining a parent context using the default implementation
     * of {@link #loadParentContext(ServletContext servletContext)}.
     * Specifies the 'selector' used in the
     * {@link org.springframework.context.access.ContextSingletonBeanFactoryLocator#getInstance(String selector)}
     * method call, which is used to obtain the BeanFactoryLocator instance from
     * which the parent context is obtained.
     * <p>The default is <code>classpath*:beanRefContext.xml</code>,
     * matching the default applied for the
     * {@link org.springframework.context.access.ContextSingletonBeanFactoryLocator#getInstance()} method.
     * Supplying the "parentContextKey" parameter is sufficient in this case.
     */
    public static final String LOCATOR_FACTORY_SELECTOR_PARAM = "locatorFactorySelector";

    /**
     * Optional servlet context parameter (i.e., "<code>parentContextKey</code>")
     * used only when obtaining a parent context using the default implementation
     * of {@link #loadParentContext(ServletContext servletContext)}.
     * Specifies the 'factoryKey' used in the
     * {@link org.springframework.beans.factory.access.BeanFactoryLocator#useBeanFactory(String factoryKey)} method call,
     * obtaining the parent application context from the BeanFactoryLocator instance.
     * <p>Supplying this "parentContextKey" parameter is sufficient when relying
     * on the default <code>classpath*:beanRefContext.xml</code> selector for
     * candidate factory references.
     */
    public static final String LOCATOR_FACTORY_KEY_PARAM = "parentContextKey";

    /**
     * Name of the class path resource (relative to the ContextLoader class)
     * that defines ContextLoader's default strategy names.
     */
    private static final String DEFAULT_STRATEGIES_PATH = "ContextLoader.properties";


    private static final Properties defaultStrategies;

    static {
        // Load default strategy implementations from properties file.
        // This is currently strictly internal and not meant to be customized
        // by application developers.
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, ContextLoader.class);
            defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Could not load 'ContextLoader.properties': " + ex.getMessage());
        }
    }


    private static final Log logger = LogFactory.getLog(ContextLoader.class);

    /**
     * Map from (thread context) ClassLoader to WebApplicationContext.
     * Often just holding one reference - if the ContextLoader class is
     * deployed in the web app ClassLoader itself!
     */
    private static final Map currentContextPerThread = CollectionFactory.createConcurrentMapIfPossible(1);

    /**
     * The root WebApplicationContext instance that this loader manages.
     */
    private WebApplicationContext context;

    /**
     * Holds BeanFactoryReference when loading parent factory via
     * ContextSingletonBeanFactoryLocator.
     */
    private BeanFactoryReference parentContextRef;


    /**
     * Initialize Spring's web application context for the given servlet context,
     * according to the "{@link #CONTEXT_CLASS_PARAM contextClass}" and
     * "{@link #CONFIG_LOCATION_PARAM contextConfigLocation}" context-params.
     * @param servletContext current servlet context
     * @return the new WebApplicationContext
     * @throws IllegalStateException if there is already a root application context present
     * @throws BeansException if the context failed to initialize
     * @see #CONTEXT_CLASS_PARAM
     * @see #CONFIG_LOCATION_PARAM
     */
    public WebApplicationContext initWebApplicationContext(ServletContext servletContext)
            throws IllegalStateException, BeansException {

        if (application.isRoot()){
            return new ContextLoader(){
                @Override
                protected WebApplicationContext createWebApplicationContext(ServletContext servletContext, ApplicationContext parent) throws BeansException {

                    ConfigurableWebApplicationContext wac =
                            new TavernXmlWebApplicationContext(application);
                    wac.setParent(parent);
                    wac.setServletContext(servletContext);
                    wac.setConfigLocation(servletContext.getInitParameter(CONFIG_LOCATION_PARAM));
                    customizeContext(servletContext, wac);
                    wac.refresh();
                    return wac;
                }
            }.initWebApplicationContext(servletContext);
        }

        servletContext.log("Initializing Spring root WebApplicationContext");
        if (logger.isInfoEnabled()) {
            logger.info("Root WebApplicationContext: initialization started");
        }
        long startTime = System.currentTimeMillis();

        try {
            // Determine parent for root web application context, if any.
            ApplicationContext parent = loadParentContext(servletContext);

            // Store context in local instance variable, to guarantee that
            // it is available on ServletContext shutdown.
            this.context = createWebApplicationContext(servletContext, parent);

            if (logger.isDebugEnabled()) {
                logger.debug("Published root WebApplicationContext as ServletContext attribute with name [" +
                        WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE + "]");
            }
            if (logger.isInfoEnabled()) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                logger.info("Root WebApplicationContext: initialization completed in " + elapsedTime + " ms");
            }

            return this.context;
        }
        catch (RuntimeException ex) {
            logger.error("Context initialization failed", ex);
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
            throw ex;
        }
        catch (Error err) {
            logger.error("Context initialization failed", err);
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, err);
            throw err;
        }
    }


    /**
     * Return the WebApplicationContext implementation class to use, either the
     * default XmlWebApplicationContext or a custom context class if specified.
     * @param servletContext current servlet context
     * @return the WebApplicationContext implementation class to use
     * @throws ApplicationContextException if the context class couldn't be loaded
     * @see #CONTEXT_CLASS_PARAM
     * @see org.springframework.web.context.support.XmlWebApplicationContext
     */
    protected Class determineContextClass(ServletContext servletContext) throws ApplicationContextException {
        String contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
        if (contextClassName != null) {
            try {
                return ClassUtils.forName(contextClassName);
            }
            catch (ClassNotFoundException ex) {
                throw new ApplicationContextException(
                        "Failed to load custom context class [" + contextClassName + "]", ex);
            }
        }
        else {
            contextClassName = defaultStrategies.getProperty(WebApplicationContext.class.getName());
            try {
                return ClassUtils.forName(contextClassName, ContextLoader.class.getClassLoader());
            }
            catch (ClassNotFoundException ex) {
                throw new ApplicationContextException(
                        "Failed to load default context class [" + contextClassName + "]", ex);
            }
        }
    }

    /**
     * Customize the {@link ConfigurableWebApplicationContext} created by this
     * ContextLoader after config locations have been supplied to the context
     * but before the context is <em>refreshed</em>.
     * <p>The default implementation is empty but can be overridden in subclasses
     * to customize the application context.
     * @param servletContext the current servlet context
     * @param applicationContext the newly created application context
     * @see #createWebApplicationContext(ServletContext, ApplicationContext)
     */
    protected void customizeContext(
            ServletContext servletContext, ConfigurableWebApplicationContext applicationContext) {
    }

    /**
     * Template method with default implementation (which may be overridden by a
     * subclass), to load or obtain an ApplicationContext instance which will be
     * used as the parent context of the root WebApplicationContext. If the
     * return value from the method is null, no parent context is set.
     * <p>The main reason to load a parent context here is to allow multiple root
     * web application contexts to all be children of a shared EAR context, or
     * alternately to also share the same parent context that is visible to
     * EJBs. For pure web applications, there is usually no need to worry about
     * having a parent context to the root web application context.
     * <p>The default implementation uses
     * {@link org.springframework.context.access.ContextSingletonBeanFactoryLocator},
     * configured via {@link #LOCATOR_FACTORY_SELECTOR_PARAM} and
     * {@link #LOCATOR_FACTORY_KEY_PARAM}, to load a parent context
     * which will be shared by all other users of ContextsingletonBeanFactoryLocator
     * which also use the same configuration parameters.
     * @param servletContext current servlet context
     * @return the parent application context, or <code>null</code> if none
     * @throws BeansException if the context couldn't be initialized
     * @see org.springframework.context.access.ContextSingletonBeanFactoryLocator
     */
    protected ApplicationContext loadParentContext(ServletContext servletContext)
            throws BeansException {

        ApplicationContext parentContext = null;
        String locatorFactorySelector = servletContext.getInitParameter(LOCATOR_FACTORY_SELECTOR_PARAM);
        String parentContextKey = servletContext.getInitParameter(LOCATOR_FACTORY_KEY_PARAM);

        if (parentContextKey != null) {
            // locatorFactorySelector may be null, indicating the default "classpath*:beanRefContext.xml"
            BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance(locatorFactorySelector);
            if (logger.isDebugEnabled()) {
                logger.debug("Getting parent context definition: using parent context key of '" +
                        parentContextKey + "' with BeanFactoryLocator");
            }
            this.parentContextRef = locator.useBeanFactory(parentContextKey);
            parentContext = (ApplicationContext) this.parentContextRef.getFactory();
        }

        return parentContext;
    }

    /**
     * Close Spring's web application context for the given servlet context. If
     * the default {@link #loadParentContext(ServletContext)} implementation,
     * which uses ContextSingletonBeanFactoryLocator, has loaded any shared
     * parent context, release one reference to that shared parent context.
     * <p>If overriding {@link #loadParentContext(ServletContext)}, you may have
     * to override this method as well.
     * @param servletContext the ServletContext that the WebApplicationContext runs in
     */
    public void closeWebApplicationContext(ServletContext servletContext) {
        servletContext.log("Closing Spring root WebApplicationContext");
        try {
            if (this.context instanceof ConfigurableWebApplicationContext) {
                ((ConfigurableWebApplicationContext) this.context).close();
            }
        }
        finally {
            currentContextPerThread.remove(Thread.currentThread().getContextClassLoader());
            servletContext.removeAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            if (this.parentContextRef != null) {
                this.parentContextRef.release();
            }
        }
    }


    /**
     * Obtain the Spring root web application context for the current thread
     * (i.e. for the current thread's context ClassLoader, which needs to be
     * the web application's ClassLoader).
     * @return the current root web application context, or <code>null</code>
     * if none found
     * @see org.springframework.web.context.support.SpringBeanAutowiringSupport
     */
    public static WebApplicationContext getCurrentWebApplicationContext() {
        return (WebApplicationContext) currentContextPerThread.get(Thread.currentThread().getContextClassLoader());
    }

	protected WebApplicationContext createWebApplicationContext(ServletContext servletContext, ApplicationContext parent)
			throws BeansException {

		ConfigurableWebApplicationContext wac = new TavernXmlWebApplicationContext(application);
        try {
            wac.setServletContext(servletContext);
            wac.setConfigLocation(application.getConfig().getContextPath() == null ? servletContext
                    .getInitParameter(CONFIG_LOCATION_PARAM) : application.getConfig().getContextPath());
            customizeContext(servletContext, wac);
            wac.refresh();
        } catch (Exception e){
            logger.error("init fail "+application,e);
        }
		return wac;
	}
}
