package com.lx.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyManager {

	private static Logger log = LoggerFactory.getLogger(PropertyManager.class);
	private static final String CONFIG = "config";

	
	private static final Map<String, String> properties = new LinkedHashMap<String, String>();

	/**
	
	 * @param key
	 * @return value
	 */
	public static String getString(String key) {
		return getRB(CONFIG, key);
	}

	/**
	
	 * @param rb
	 *            property 文件，文件名为 rb.properties
	 * @param key
	 * @return value
	 */
	public static String getRB(String rb, String key) {
		try {
			String rbKey = rb + "_" + key;
			if (properties.containsKey(rbKey)) {
				return properties.get(rbKey);
			}
			String value = ResourceBundle.getBundle(rb).getString(key);
			properties.put(rbKey, value);
			return value;
		} catch (MissingResourceException e) {
			log.error('!' + rb + ":" + key + '!');
		}

		return null;
	}

	public static void main(String[] args) {
		
	}

}
