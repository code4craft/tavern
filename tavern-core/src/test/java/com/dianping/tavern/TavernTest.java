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
		tavern.setServletContext(new MockServletContext());
		tavern.init();
        Map<String,Application> applicationMap = Tavern.contexts().getApplicationMap();
        TestBean bean = Tavern.getCurrentApplication(getClass()).getBean(TestBean.class);
        System.out.println(applicationMap);
    }
}
