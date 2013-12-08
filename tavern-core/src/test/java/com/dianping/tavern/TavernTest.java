package com.dianping.tavern;

import org.junit.Test;
import org.springframework.mock.web.MockServletContext;

import java.io.IOException;
import java.util.Map;

/**
 * @author code4crafter@gmail.com
 */
public class TavernTest {

	private Tavern tavern = new Tavern();

	@Test
	public void testInit() throws IOException {
        MockServletContext servletContext = new MockServletContext();
        servletContext.addInitParameter("contextConfigLocation","classpath*:spring/appcontext-*.xml");
        tavern.setServletContext(servletContext);
		tavern.init();
        tavern.resolve();
        Map<String,Application> applicationMap = Tavern.contexts().getApplicationMap();
        Application currentApplication = Tavern.getRoottApplication();
        TestBean bean = currentApplication.getBean(TestBean.class);
        System.out.println(applicationMap);
    }
}
