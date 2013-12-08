package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
import com.dianping.tavern.plugin.TavernWebPlugin;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * @author code4crafter@gmail.com
 */
public class SpringTavernPlugin implements TavernWebPlugin {

	@Override
	public void init(ServletContext servletContext, Application application) {
		this.contextLoader = createContextLoader(application);
        WebApplicationContext webApplicationContext = this.contextLoader.initWebApplicationContext(servletContext);
        application.setApplicationContext(webApplicationContext);
    }

    @Override
    public void resolved(ServletContext servletContext, Application application) {
        ConfigurableWebApplicationContext applicationContext = (ConfigurableWebApplicationContext) application.getApplicationContext();
        if (application.getParent()!=null){
            applicationContext.setParent(application.getParent().getApplicationContext());
            applicationContext.refresh();
        }
    }

    @Override
	public void destroy(ServletContext servletContext, Application application) {
		if (this.contextLoader != null) {
			this.contextLoader.closeWebApplicationContext(servletContext);
		}
	}

	private SpringTavernContextLoader contextLoader;

	/**
	 * Create the ContextLoader to use. Can be overridden in subclasses.
	 * 
	 * @return the new ContextLoader
	 */
	protected SpringTavernContextLoader createContextLoader(Application application) {
		return new SpringTavernContextLoader(application);
	}

	/**
	 * Return the ContextLoader used by this listener.
	 * 
	 * @return the current ContextLoader
	 */
	public SpringTavernContextLoader getContextLoader() {
		return this.contextLoader;
	}

}
