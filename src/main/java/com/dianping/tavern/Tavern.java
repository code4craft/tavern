package com.dianping.tavern;

import com.dianping.tavern.context.TavernApplicationContexts;

/**
 * @author code4crafter@gmail.com
 */
public abstract class Tavern {

	public static TavernApplicationContexts contexts() {
		return TavernApplicationContexts.getInstance();
	}

    public static Application getApplication(Class<?> clazz) {
        return contexts().getApplication(clazz);
    }
}
