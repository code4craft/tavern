package com.dianping.tavern.plugin.struts;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.apache.struts2.views.freemarker.FreemarkerResult;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.ui.freemarker.SpringTemplateLoader;

/**
 * @author code4crafter@gmail.com
 */
public class TavernFreemarkerResult extends FreemarkerResult {

	@Override
	protected Configuration getConfiguration() throws TemplateException {
		Configuration configuration1 = super.getConfiguration();
		configuration1.setTemplateLoader(new ClassPathTemplateLoader(new PathMatchingResourcePatternResolver(), "classpath*:/ftl"));
		return configuration1;
	}
}
