package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
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

    /**
     *
     */
	private static Set<String> importedUrls = new HashSet<String>();

	private Application application;

	public TavernServletContextResourcePatternResolver(ServletContext servletContext, Application application) {
		super(servletContext);
		this.application = application;
	}

	public TavernServletContextResourcePatternResolver(ResourceLoader resourceLoader, Application application) {
		super(resourceLoader);
		this.application = application;
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		Resource[] resources = super.getResources(locationPattern);
		List<Resource> resourceList = new ArrayList<Resource>(resources.length);
		for (Resource resource : resources) {
            //All resource without application definition is considered as root application.
			if (application.isRoot()) {
				if (!importedUrls.contains(resource.getURL().toString())) {
					resourceList.add(resource);
				}
			} else {
				if (resource.getURL().toString().startsWith(application.getJarFilePath())) {
					resourceList.add(resource);
					importedUrls.add(resource.getURL().toString());
				}
			}
		}
		return (Resource[]) resourceList.toArray(new Resource[resourceList.size()]);
	}
}
