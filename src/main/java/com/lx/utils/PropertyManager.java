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

	/**
	 * 将key和value缓存到内存里
	 */
	private static final Map<String, String> properties = new LinkedHashMap<String, String>();

	/**
	 * 读取默认的config.properties文件
	 * 
	 * @param key
	 * @return value
	 */
	public static String getString(String key) {
		return getRB(CONFIG, key);
	}

	/**
	 * 读取指定为rb.properties文件
	 * 
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
		System.out.println(PropertyManager.getString("redis_maxtotal"));
		System.out.println(PropertyManager.getString("redis_maxtotal1"));
		System.out.println(PropertyManager.getString("redis_maxtotal"));
	}

}
