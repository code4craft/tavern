package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * @author code4crafter@gmail.com
 */
public class TavernXmlWebApplicationContext extends XmlWebApplicationContext {

    private Application application;

    public TavernXmlWebApplicationContext(Application application) {
        this.application = application;
    }

    @Override
    protected ResourcePatternResolver getResourcePatternResolver() {
        return new TavernServletContextResourcePatternResolver(this,application);
    }
}
