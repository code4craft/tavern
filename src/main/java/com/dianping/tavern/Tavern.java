package com.dianping.tavern;

import com.dianping.tavern.context.TavernApplicationContexts;

/**
 * @author code4crafter@gmail.com
 * 入口Facade类
 */
public abstract class Tavern {

	public static TavernApplicationContexts contexts() {
		return TavernApplicationContexts.getInstance();
	}

    public static Application getApplication(Class<?> clazz) {
        return contexts().getApplication(clazz);
    }
}
