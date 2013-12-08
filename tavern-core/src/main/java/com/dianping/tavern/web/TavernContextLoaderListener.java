package com.dianping.tavern.web;

import com.dianping.tavern.Tavern;
import org.springframework.web.context.ContextLoader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

/**
 * @author code4crafter@gmail.com
 */
public class TavernContextLoaderListener implements ServletContextListener {

    private Tavern tavern = new Tavern();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        tavern.setServletContext(sce.getServletContext());
        try {
            tavern.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        tavern.destroy();
    }
}
