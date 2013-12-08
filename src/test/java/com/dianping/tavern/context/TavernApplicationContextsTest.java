package com.dianping.tavern.context;

import com.dianping.tavern.Tavern;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author code4crafter@gmail.com
 */
public class TavernApplicationContextsTest {

    @Test
    public void testContext() throws IOException {
        Enumeration<URL> resources = getClass().getClassLoader().getResources("app.xml");
        while (resources.hasMoreElements()){
            System.out.println(resources.nextElement());
        }
    }
}
