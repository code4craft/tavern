package com.dianping.tavern.plugin;

import com.dianping.tavern.Application;

import javax.servlet.ServletContext;

/**
 * @author code4crafter@gmail.com
 */
public interface TavernWebPlugin {

    /**
     * Called after application is inited.
     * @param servletContext
     * @param application
     */
	public void init(ServletContext servletContext, Application application);

    /**
     * Called after all dependencies of application is resolved.
     * @param servletContext
     * @param application
     */
	public void resolved(ServletContext servletContext, Application application);

    /**
     * Called after application is destroyed.
     * @param servletContext
     * @param application
     */
	public void destroy(ServletContext servletContext, Application application);

}
