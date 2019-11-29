package com.aem.community.util;

import java.io.FileInputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager {
	public static Properties ps = null;
	private static final Logger log = LoggerFactory
			.getLogger(ConfigManager.class);
	//private static final String CSUF_AEM_FILE = "D:\\CSUF\\AEM\\application.properties";
	private static final String CSUF_AEM_FILE = "E:\\CSUF\\AEM\\application.properties";
	
	static {
		try {
			FileInputStream is = new FileInputStream(CSUF_AEM_FILE);
			ps = new Properties();
			ps.load(is);
			is.close();
			// LogManager.initialize();
		} catch (Exception e) {
			log.error("Properties file failed to load. Check your class path for Properties file::: CSUF_AEM_FILE");
			e.printStackTrace();
		}
	}
	public static String getValue(String elementPropertyName) {
		return (String) ps.getProperty(elementPropertyName);
	}
}
