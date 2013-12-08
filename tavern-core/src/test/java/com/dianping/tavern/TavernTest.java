package com.dianping.tavern;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

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
        Application currentApplication = Tavern.getCurrentApplication(getClass());
        TestBean bean = currentApplication.getBean("testBean");
        assertNotNull(bean);
        bean = currentApplication.getBean(TestBean.class);
        assertNotNull(bean);
    }
}
