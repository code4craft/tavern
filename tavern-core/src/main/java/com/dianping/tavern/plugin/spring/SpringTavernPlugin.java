package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
import com.dianping.tavern.plugin.PluginContext;
import com.dianping.tavern.plugin.TavernPlugin;
import com.dianping.tavern.plugin.spring.meta.AutowiredExternal;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.lang.reflect.Field;

/**
 * @author code4crafter@gmail.com
 */
public class SpringTavernPlugin implements TavernPlugin {

	@Override
	public void init(PluginContext pluginContext) {
		this.contextLoader = createContextLoader(pluginContext.getApplication());
		WebApplicationContext webApplicationContext = this.contextLoader.initWebApplicationContext(pluginContext
				.getServletContext());
		pluginContext.getApplication().setApplicationContext(webApplicationContext);
	}

	@Override
	public void resolve(PluginContext pluginContext) {
		ConfigurableWebApplicationContext applicationContext = (ConfigurableWebApplicationContext) pluginContext
				.getApplication().getApplicationContext();
		if (pluginContext.getApplication().getParent() != null) {
			applicationContext.setParent(pluginContext.getApplication().getParent().getApplicationContext());
			applicationContext.refresh();
		}
	}

	@Override
	public void resolved(PluginContext pluginContext) {
		injectAutowiredExternal(pluginContext);

	}

	/**
	 * 注入其他ApplicationContext的bean
	 * 
	 * @param pluginContext
	 */
	private void injectAutowiredExternal(PluginContext pluginContext) {
		// 先这样写，反正只注入一次，性能什么的玩儿蛋去吧
		ConfigurableWebApplicationContext applicationContext = (ConfigurableWebApplicationContext) pluginContext
				.getApplication().getApplicationContext();
		for (String beanName : applicationContext.getBeanDefinitionNames()) {
			Object bean = applicationContext.getBean(beanName);
			for (Field field : bean.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(AutowiredExternal.class)) {
					field.setAccessible(true);
					AutowiredExternal annotation = field.getAnnotation(AutowiredExternal.class);
					Application application = pluginContext.getTavernApplicationContainer().getApplication(
							annotation.value());
					Assert.notNull(application, "Application " + annotation.value() + " does not exist!");
					try {
						field.set(bean, application.getBean(field.getType()));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void destroy(PluginContext pluginContext) {
		if (this.contextLoader != null) {
			this.contextLoader.closeWebApplicationContext(pluginContext.getServletContext());
		}
	}

	private SpringTavernContextLoader contextLoader;

	/**
	 * Create the ContextLoader to use. Can be overridden in subclasses.
	 * 
	 * @return the new ContextLoader
	 */
	protected SpringTavernContextLoader createContextLoader(Application application) {
		return new SpringTavernContextLoader(application);
	}

	/**
	 * Return the ContextLoader used by this listener.
	 * 
	 * @return the current ContextLoader
	 */
	public SpringTavernContextLoader getContextLoader() {
		return this.contextLoader;
	}

}
