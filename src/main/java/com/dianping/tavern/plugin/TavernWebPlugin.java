package com.dianping.tavern.plugin;

import com.dianping.tavern.Application;

import javax.servlet.ServletContext;

/**
 * @author code4crafter@gmail.com
 */
public interface TavernWebPlugin {

	public void init(ServletContext servletContext, Application application);

	public void destroy(ServletContext servletContext, Application application);

}
