package com.dianping.tavern;

import com.dianping.tavern.config.ApplicationConfig;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;

/**
 * @author code4crafter@gmail.com
 */
public class Application {

	/**
	 * for Spring
	 */
	private ApplicationContext applicationContext;

	private ApplicationConfig config;

	public Application(ApplicationContext applicationContext, ApplicationConfig config) {
		this.applicationContext = applicationContext;
		this.config = config;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationConfig getConfig() {
		return config;
	}

	public void setConfig(ApplicationConfig config) {
		this.config = config;
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> clazz) {
		return (T) BeanFactoryUtils.beanOfType(applicationContext, clazz);
	}
}
