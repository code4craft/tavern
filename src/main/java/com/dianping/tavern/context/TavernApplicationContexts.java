package com.dianping.tavern.context;

import com.dianping.tavern.Application;
import com.dianping.tavern.config.ApplicationConfig;
import org.springframework.context.ApplicationContext;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author code4crafter@gmail.com
 */
public class TavernApplicationContexts {

	public static final String GLOBAL_APPLICATION = "_global";

	public static final TavernApplicationContexts INSTANCE = new TavernApplicationContexts();

	private TavernApplicationContexts() {
	}

    public static TavernApplicationContexts getInstance(){
        return INSTANCE;
    }

	private Map<String, Application> applicationMap = new ConcurrentHashMap<String, Application>();

	public Application getApplication(Class<?> clazz) {
		Iterator<Application> iterator = applicationMap.values().iterator();
		while (iterator.hasNext()) {
			Application application = iterator.next();
			if (application.getConfig() != null
					&& clazz.getCanonicalName().startsWith(application.getConfig().getPackageBase())) {
				return application;
			}
		}
		return applicationMap.get(GLOBAL_APPLICATION);
	}

	public void register(String name, ApplicationContext context, ApplicationConfig config) {
		applicationMap.put(name, new Application(context, config));
	}

	public void register(String name, ApplicationContext context) {
		register(name, context, null);
	}

}
