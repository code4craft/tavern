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
import java.util.*;

/**
 * <pre>
 * 主要逻辑
 * 一共三个阶段：
 * 1. init() 扫描jar包，读取配置文件，初始化Application
 * 2. resolve() 根据父子关系分析依赖，并进行注入
 * 3. destroy() 销毁
 *
 * 保存在TavernApplicationContainer里
 * </pre>
 * @author code4crafter@gmail.com
 */
public class Tavern {

	private static List<TavernPlugin> plugins = new ArrayList<TavernPlugin>();

    /**
     * 去重
     */
    private static Set<Class<?>> pluginClazzSet = new HashSet<Class<?>>();

	static {
        //TODO:目前与struts整合还有些问题，先disable
//		plugins.add(new SpringTavernPlugin());
	}

    /**
     * 全局类，没有配置app.xml的所有bean都进入此类
     */
	public static final String GLOBAL_APPLICATION = "_global";

    /**
     * 容器
     */
	private TavernApplicationContainer tavernApplicationContainer = TavernApplicationContainer.getInstance();

	private List<Application> applicationList = new ArrayList<Application>();

	private ApplicationConfigParser parser = getParser();

	private ServletContext servletContext;

	public static TavernApplicationContainer contexts() {
		return TavernApplicationContainer.getInstance();
	}

    /**
     * 获取某个类应该对应的Application<br>
     * 根据定义的classpath决定
     * @param clazz
     * @return
     */
	public static Application getCurrentApplication(Class<?> clazz) {
		return contexts().getCurrentApplication(clazz);
	}

	public static Application getRoottApplication() {
		return contexts().getApplicationMap().get(GLOBAL_APPLICATION);
	}

    public void init() throws IOException {
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

	public void run() throws IOException {
		init();
		resolve();
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

    public static void addPlugin(TavernPlugin plugin){
        if (pluginClazzSet.add(plugin.getClass())){
            plugins.add(plugin);
        }
    }

	protected ApplicationConfigParser getParser() {
		return new XmlApplicationConfigParser();
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
