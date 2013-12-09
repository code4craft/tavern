package com.dianping.tavern.plugin.struts;

import freemarker.cache.TemplateLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author code4crafter@gmail.com
 */
public class ClassPathTemplateLoader implements TemplateLoader {

	protected final Log logger = LogFactory.getLog(getClass());

	private final PathMatchingResourcePatternResolver resourceLoader;

	private final String templateLoaderPath;

	/**
	 * Create a new SpringTemplateLoader.
	 * 
	 * @param resourceLoader
	 *            the Spring ResourceLoader to use
	 * @param templateLoaderPath
	 *            the template loader path to use
	 */
	public ClassPathTemplateLoader(PathMatchingResourcePatternResolver resourceLoader, String templateLoaderPath) {
		this.resourceLoader = resourceLoader;
		if (!templateLoaderPath.endsWith("/")) {
			templateLoaderPath += "/";
		}
		this.templateLoaderPath = templateLoaderPath;
		if (logger.isInfoEnabled()) {
			logger.info("SpringTemplateLoader for FreeMarker: using resource loader [" + this.resourceLoader
					+ "] and template loader path [" + this.templateLoaderPath + "]");
		}
	}

	public Object findTemplateSource(String name) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking for FreeMarker template with name [" + name + "]");
		}
		Resource[] resources = this.resourceLoader.getResources(this.templateLoaderPath + name);
		if (resources != null && resources.length == 1) {
			return resources[0];
		}
		return null;
	}

	public Reader getReader(Object templateSource, String encoding) throws IOException {
		Resource resource = (Resource) templateSource;
		try {
			return new InputStreamReader(resource.getInputStream(), encoding);
		} catch (IOException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not find FreeMarker template: " + resource);
			}
			throw ex;
		}
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {

	}

	public long getLastModified(Object templateSource) {
		return -1;
	}

}
