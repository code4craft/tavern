package com.dianping.tavern.config;

import java.io.InputStream;

/**
 * @author code4crafter@gmail.com
 */
public interface ApplicationConfigParser {

    public ApplicationConfig parse(InputStream inputStream);
}
