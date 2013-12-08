package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * @author code4crafter@gmail.com
 */
public class SpringTavernContextLoader extends ContextLoader {

	private Application application;

	public SpringTavernContextLoader(Application application) {
		this.application = application;
	}

    protected WebApplicationContext createWebApplicationContext(
            ServletContext servletContext, ApplicationContext parent) throws BeansException {

        Class contextClass = determineContextClass(servletContext);
        if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
            throw new ApplicationContextException("Custom context class [" + contextClass.getName() +
                    "] is not of type [" + ConfigurableWebApplicationContext.class.getName() + "]");
        }

        ConfigurableWebApplicationContext wac =
                (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);
        wac.setParent(parent);
        wac.setServletContext(servletContext);
        wac.setConfigLocation(application.getConfig().getContextPath());
        customizeContext(servletContext, wac);
        wac.refresh();
        application.setApplicationContext(wac);
        return wac;
    }
}
