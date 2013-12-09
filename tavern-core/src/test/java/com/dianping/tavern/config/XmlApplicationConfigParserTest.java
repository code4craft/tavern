package com.dianping.tavern.config;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author code4crafter@gmail.com
 */
public class XmlApplicationConfigParserTest {

    private XmlApplicationConfigParser xmlApplicationConfigParser = new XmlApplicationConfigParser();

    @Test
    public void testParser(){
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("app.xml");
        ApplicationConfig config = xmlApplicationConfigParser.parse(resourceAsStream);
        assertEquals("tavern", config.getName());
        assertEquals("classpath*:spring/appcontext-*.xml", config.getContextPath());
        assertEquals("com.dianping.tavern", config.getPackageBase());
        assertNull(config.getParent());
    }
}
