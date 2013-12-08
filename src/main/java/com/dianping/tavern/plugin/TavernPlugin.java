package com.dianping.tavern.plugin;

import com.dianping.tavern.Application;

/**
 * @author code4crafter@gmail.com
 */
public interface TavernPlugin {

    public void init(Application application);

    public void destroy(Application application);
}
