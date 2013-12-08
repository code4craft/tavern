package com.dianping.tavern.context;

import com.dianping.tavern.Application;
import com.dianping.tavern.Tavern;
import com.dianping.tavern.config.ApplicationConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author code4crafter@gmail.com
 */
public class TavernApplicationContainer {

	public static final TavernApplicationContainer INSTANCE = new TavernApplicationContainer();

	private TavernApplicationContainer() {
	}

	public static TavernApplicationContainer getInstance() {
		return INSTANCE;
	}

	private Map<String, Application> applicationMap = new ConcurrentHashMap<String, Application>();

	public Application getCurrentApplication(Class<?> clazz) {
		Iterator<Application> iterator = applicationMap.values().iterator();
		while (iterator.hasNext()) {
			Application application = iterator.next();
			if (application.getConfig() != null
					&& clazz.getCanonicalName().startsWith(application.getConfig().getPackageBase())) {
				return application;
			}
		}
		return applicationMap.get(Tavern.GLOBAL_APPLICATION);
	}

	public void register(Application application) {
		applicationMap.put(application.getName(), application);
	}

    public Map<String, Application> getApplicationMap() {
        return applicationMap;
    }

    public void resolveParents() {
		for (Application application : applicationMap.values()) {
			if (application.isRoot()) {
				continue;
			}
			String parent = application.getConfig().getParent();
			if (parent == null) {
				application.setParent(applicationMap.get(Tavern.GLOBAL_APPLICATION));
			} else {
				Application parentApp = applicationMap.get(parent);
				Assert.notNull(parentApp, "Parent " + parent + " of " + application + " is error!");
                application.setParent(parentApp);
			}
		}
		resolveCircleDependency();
	}

	private void resolveCircleDependency() {
        //TODO
	}
}
