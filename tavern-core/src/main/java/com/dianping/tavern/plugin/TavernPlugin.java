package com.dianping.tavern.plugin;

import com.dianping.tavern.Application;

import javax.servlet.ServletContext;

/**
 * @author code4crafter@gmail.com
 */
public interface TavernPlugin {

	/**
	 * Called after application is inited.
	 * 
	 * @param pluginContext
	 */
	public void init(PluginContext pluginContext);

	/**
	 * Called when dependency of application is resolving.
	 * 
	 * @param pluginContext
	 */
	public void resolve(PluginContext pluginContext);

	/**
	 * Called after all dependencies of application is resolved.
	 * 
	 * @param pluginContext
	 */
	public void resolved(PluginContext pluginContext);

	/**
	 * Called after application is destroyed.
	 * 
	 * @param pluginContext
	 */
	public void destroy(PluginContext pluginContext);

}
