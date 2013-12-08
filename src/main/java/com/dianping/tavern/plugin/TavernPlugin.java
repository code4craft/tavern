package com.dianping.tavern.plugin;

import com.dianping.tavern.Application;

/**
 * @author code4crafter@gmail.com
 */
public interface TavernPlugin {

    /**
     * Called after application is inited.
     * @param application
     */
    public void init(Application application);

    /**
     * Called after all dependencies of application is resolved.
     *
     * @param application
     */
    public void resolved(Application application);

    /**
     * Called after application is destroyed.
     * @param application
     */
    public void destroy(Application application);
}
