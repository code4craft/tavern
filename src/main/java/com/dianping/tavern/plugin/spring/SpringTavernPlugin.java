package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
import com.dianping.tavern.plugin.TavernWebPlugin;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * @author code4crafter@gmail.com
 */
public class SpringTavernPlugin implements TavernWebPlugin {

	@Override
	public void init(ServletContext servletContext, Application application) {
		this.contextLoader = createContextLoader(application);
		this.contextLoader.initWebApplicationContext(servletContext);
	}

	@Override
	public void destroy(ServletContext servletContext, Application application) {
		if (this.contextLoader != null) {
			this.contextLoader.closeWebApplicationContext(servletContext);
		}
	}

	private ContextLoader contextLoader;

	/**
	 * Create the ContextLoader to use. Can be overridden in subclasses.
	 * 
	 * @return the new ContextLoader
	 */
	protected ContextLoader createContextLoader(Application application) {
		return new SpringTavernContextLoader(application);
	}

	/**
	 * Return the ContextLoader used by this listener.
	 * 
	 * @return the current ContextLoader
	 */
	public ContextLoader getContextLoader() {
		return this.contextLoader;
	}

}
