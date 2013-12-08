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

    private Application parent;

    private String jarFilePath;

    private boolean root;

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

    public String getJarFilePath() {
        return jarFilePath;
    }

    public void setJarFilePath(String jarFilePath) {
        this.jarFilePath = jarFilePath;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public Application getParent() {
        return parent;
    }

    public void setParent(Application parent) {
        this.parent = parent;
    }
}
