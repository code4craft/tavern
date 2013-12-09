package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
import com.dianping.tavern.plugin.PluginContext;
import com.dianping.tavern.plugin.TavernPlugin;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * @author code4crafter@gmail.com
 */
public class SpringTavernPlugin implements TavernPlugin {

	@Override
	public void init(PluginContext pluginContext) {
		this.contextLoader = createContextLoader(pluginContext.getApplication());
		WebApplicationContext webApplicationContext = this.contextLoader.initWebApplicationContext(pluginContext
				.getServletContext());
		pluginContext.getApplication().setApplicationContext(webApplicationContext);
	}

	@Override
	public void resolve(PluginContext pluginContext) {
		ConfigurableWebApplicationContext applicationContext = (ConfigurableWebApplicationContext) pluginContext
				.getApplication().getApplicationContext();
		if (pluginContext.getApplication().getParent() != null) {
			applicationContext.setParent(pluginContext.getApplication().getParent().getApplicationContext());
			applicationContext.refresh();
		}
	}

	@Override
	public void resolved(PluginContext pluginContext) {
	}

	@Override
	public void destroy(PluginContext pluginContext) {
		if (this.contextLoader != null) {
			this.contextLoader.closeWebApplicationContext(pluginContext.getServletContext());
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
