package com.dianping.tavern.config;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author code4crafter@gmail.com
 */
public class XmlApplicationConfigParser implements ApplicationConfigParser {

	private String[] fiedlds = new String[] { "name", "packageBase", "parent", "contextPath" };

	@Override
	public ApplicationConfig parse(InputStream inputStream) {
		Digester digester = new Digester();
		digester.addObjectCreate("Application", ApplicationConfig.class);
        for (String filed : fiedlds) {
            digester.addBeanPropertySetter("Application/"+filed);
        }
        try {
			return (ApplicationConfig) digester.parse(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

}
