package com.dianping.tavern.plugin;

import com.dianping.tavern.Application;
import com.dianping.tavern.context.TavernApplicationContainer;

import javax.servlet.ServletContext;

/**
 * @author yihua.huang@dianping.com
 */
public class PluginContext {

	private TavernApplicationContainer tavernApplicationContainer;

	private Application application;

	private ServletContext servletContext;

	public PluginContext(Application application, TavernApplicationContainer tavernApplicationContainer,
			ServletContext servletContext) {
		this.servletContext = servletContext;
		this.application = application;
		this.tavernApplicationContainer = tavernApplicationContainer;
	}

	public TavernApplicationContainer getTavernApplicationContainer() {
		return tavernApplicationContainer;
	}

	public void setTavernApplicationContainer(TavernApplicationContainer tavernApplicationContainer) {
		this.tavernApplicationContainer = tavernApplicationContainer;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
