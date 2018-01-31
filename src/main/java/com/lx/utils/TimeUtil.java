package com.lx.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class TimeUtil {


	public static Logger logger = LoggerFactory.getLogger(TimeUtil.class);
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String defaultPattern = "yyyy-MM-dd HH:mm:ss";
	public static final String yyyy_MM_dd_HH_mm_ss = "yyyy_MM_dd_HH_mm_ss";
	public static final String yyyyMMddPattern = "yyyy-MM-dd";
	public static final String MMddyyyyHHmmssPattern = "MM/dd/yyyy HH:mm:ss";

	/**
	 * 返回yyyy-MM-dd HH:mm:ss形式的当前时间
	 *
	 * @return
	 */
	public static final String getTimeStr() {
		return getTimeStr(new Date());
	}

	/**
	 * 返回yyyy-MM-dd HH:mm:ss形式的时间
	 *
	 * @return
	 */
	public static final String getTimeStr(Date date) {
		return getTimeStr(date, defaultPattern);
	}

	/**
	 * 字符串转换成时间
	 *
	 * @return
	 */
	public static final String getTimeStr(Date date, String pattern) {
		if (date == null) {
			return DateFormatUtils.format(new Date(), pattern);
		}
		return DateFormatUtils.format(date, pattern);
	}

	/**
	 * 时间转换成字符串
	 *
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static final Date getTime(String dateStr, String pattern) {
		DateFormat format = new SimpleDateFormat(pattern);
		Date d = null;
		try {
			d = format.parse(dateStr);
		} catch (ParseException e) {
			logger.error("时间转换出错 : [ " + dateStr + " ]");
		}
		return d;
	}

	/**
	 * 时间转换成字符串 yyyy-MM-dd HH:mm:ss 格式
	 *
	 * @param dateStr
	 * @return
	 */
	public static final Date getTime(String dateStr) {
		return getTime(dateStr, defaultPattern);
	}

	/**
	 * 获取当前年
	 *
	 * @return
	 */
	public static final int getYear() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * 把前一种格式日期转成后一种格式
	 */
	public static final String getTimeStrFormat(String time, String origin, String target){
		if (StringUtils.isBlank(time)) {
			return "";
		}
		Date date = getTime(time, origin);
		return getTimeStr(date, target);
	}

}
