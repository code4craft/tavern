package com.dianping.tavern;

import com.dianping.tavern.config.ApplicationConfig;
import com.dianping.tavern.config.ApplicationConfigParser;
import com.dianping.tavern.config.XmlApplicationConfigParser;
import com.dianping.tavern.context.TavernApplicationContainer;
import com.dianping.tavern.plugin.PluginContext;
import com.dianping.tavern.plugin.TavernPlugin;
import com.dianping.tavern.plugin.spring.SpringTavernPlugin;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author code4crafter@gmail.com 入口Facade类
 */
public class Tavern {

	private static List<TavernPlugin> plugins = new ArrayList<TavernPlugin>();

	static {
		plugins.add(new SpringTavernPlugin());
	}

	public static final String GLOBAL_APPLICATION = "_global";

	private TavernApplicationContainer tavernApplicationContainer = TavernApplicationContainer.getInstance();

	private List<Application> applicationList = new ArrayList<Application>();

	private ApplicationConfigParser parser = getParser();

	private ServletContext servletContext;

	public static TavernApplicationContainer contexts() {
		return TavernApplicationContainer.getInstance();
	}

	public static Application getCurrentApplication(Class<?> clazz) {
		return contexts().getCurrentApplication(clazz);
	}

	public static Application getRoottApplication() {
		return contexts().getApplicationMap().get(GLOBAL_APPLICATION);
	}

	public void run() throws IOException {
		init();
		resolve();
	}

	public void init() throws IOException {
		initApplication();
	}

	private void initApplication() throws IOException {
		Enumeration<URL> resources = getClass().getClassLoader().getResources("app.xml");
		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			ApplicationConfig config = parser.parse(urlConnection.getInputStream());
			Assert.notNull(config.getName(), "Name of application must not be null!");
			Application application = new Application(config, ResourceUtils.extractJarFileURL(url).toString());
			tavernApplicationContainer.register(application);
            applicationList.add(application);
		}
        ApplicationConfig globalConfig = new ApplicationConfig();
        globalConfig.setName(GLOBAL_APPLICATION);
        Application globalApplication = new Application(globalConfig, null);
        globalApplication.setRoot(true);
        tavernApplicationContainer.register(globalApplication);
        applicationList.add(globalApplication);
		for (Application application : applicationList) {
			for (TavernPlugin plugin : plugins) {
				plugin.init(new PluginContext(application, tavernApplicationContainer, servletContext));
			}
		}
	}

	public void resolve() {
		tavernApplicationContainer.resolveParents();
		for (Application application : applicationList) {
			for (TavernPlugin plugin : plugins) {
				plugin.resolve(new PluginContext(application, tavernApplicationContainer, servletContext));
			}
		}
		for (Application application : applicationList) {
			for (TavernPlugin plugin : plugins) {
				plugin.resolved(new PluginContext(application, tavernApplicationContainer, servletContext));
			}
		}
	}

	public void destroy() {
		// todo
	}

	protected ApplicationConfigParser getParser() {
		return new XmlApplicationConfigParser();
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
