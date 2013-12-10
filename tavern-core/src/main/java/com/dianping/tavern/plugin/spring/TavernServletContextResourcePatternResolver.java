package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
import com.dianping.tavern.plugin.PluginContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author code4crafter@gmail.com
 */
public class TavernServletContextResourcePatternResolver extends ServletContextResourcePatternResolver {

	private PluginContext pluginContext;

	public TavernServletContextResourcePatternResolver(ServletContext servletContext) {
		super(servletContext);
	}

	public TavernServletContextResourcePatternResolver(ResourceLoader resourceLoader) {
		super(resourceLoader);
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		Resource[] resources = super.getResources(locationPattern);
		List<Resource> resourceList = new ArrayList<Resource>(resources.length);
		for (Resource resource : resources) {
			// All resource without application definition is considered as root
			// application.
			if (pluginContext.getApplication().isRoot()) {
				boolean inAppJarFile = false;
				for (Application application : pluginContext.getTavernApplicationContainer().getApplicationMap()
						.values()) {
					if (application.getJarFilePath() != null
							&& resource.getURL().toString().contains(application.getJarFilePath())) {
						inAppJarFile = true;
						break;
					}
				}
				if (!inAppJarFile) {
					resourceList.add(resource);
				}
			} else {
				if (resource.getURL().toString().contains(pluginContext.getApplication().getJarFilePath())) {
					resourceList.add(resource);
				}
			}
		}
		return (Resource[]) resourceList.toArray(new Resource[resourceList.size()]);
	}

	public void setPluginContext(PluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}
}
