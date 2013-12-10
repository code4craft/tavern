package com.dianping.tavern.plugin.spring;

import com.dianping.tavern.Application;
import com.dianping.tavern.Tavern;
import com.dianping.tavern.plugin.PluginContext;
import com.dianping.tavern.plugin.spring.meta.AutowiredExternal;
import org.springframework.util.Assert;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yihua.huang@dianping.com
 */
public class InitBean {

	private AtomicBoolean inited = new AtomicBoolean(false);

	protected void init() {
		if (!inited.compareAndSet(false, true)) {
			return;
		}
		for (Field field : this.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(AutowiredExternal.class)) {
				field.setAccessible(true);
				AutowiredExternal annotation = field.getAnnotation(AutowiredExternal.class);
				Application application = Tavern.contexts().getApplication(annotation.value());
				Assert.notNull(application, "Application " + annotation.value() + " does not exist!");
				try {
					field.set(this, application.getBean(field.getType()));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
