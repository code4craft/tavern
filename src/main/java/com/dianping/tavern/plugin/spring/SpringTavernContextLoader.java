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

	protected WebApplicationContext createWebApplicationContext(ServletContext servletContext, ApplicationContext parent)
			throws BeansException {

		ConfigurableWebApplicationContext wac = new TavernXmlWebApplicationContext(application);
		wac.setParent(parent);
		wac.setServletContext(servletContext);
		wac.setConfigLocation(application.getConfig().getContextPath() == null ? servletContext
				.getInitParameter(CONFIG_LOCATION_PARAM) : application.getConfig().getContextPath());
		customizeContext(servletContext, wac);
		wac.refresh();
		application.setApplicationContext(wac);
		return wac;
	}
}
