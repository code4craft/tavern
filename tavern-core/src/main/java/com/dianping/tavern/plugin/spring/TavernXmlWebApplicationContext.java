package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
import com.dianping.tavern.Tavern;
import com.dianping.tavern.plugin.PluginContext;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * @author code4crafter@gmail.com
 */
public class TavernXmlWebApplicationContext extends XmlWebApplicationContext {

	private PluginContext pluginContext;

	private TavernServletContextResourcePatternResolver tavernServletContextResourcePatternResolver;

	public TavernXmlWebApplicationContext(PluginContext pluginContext) {
		this.pluginContext = pluginContext;
		tavernServletContextResourcePatternResolver.setPluginContext(pluginContext);
	}

	@Override
	protected ResourcePatternResolver getResourcePatternResolver() {
        tavernServletContextResourcePatternResolver = new TavernServletContextResourcePatternResolver(
                this);
		return tavernServletContextResourcePatternResolver;
	}
}
