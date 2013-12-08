package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
import com.dianping.tavern.Tavern;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * @author code4crafter@gmail.com
 */
public class TavernXmlWebApplicationContext extends XmlWebApplicationContext {

	private Application application;

	private TavernServletContextResourcePatternResolver tavernServletContextResourcePatternResolver;

	public TavernXmlWebApplicationContext(Application application) {
		this.application = application;
		tavernServletContextResourcePatternResolver.setApplication(application);
	}

	@Override
	protected ResourcePatternResolver getResourcePatternResolver() {
        tavernServletContextResourcePatternResolver = new TavernServletContextResourcePatternResolver(
                this);
		return tavernServletContextResourcePatternResolver;
	}
}
