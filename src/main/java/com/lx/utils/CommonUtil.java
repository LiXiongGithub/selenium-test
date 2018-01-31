package com.lx.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil {

	public static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

	public static String PATTERN_FULL = "yyyy-MM-dd HH:mm:ss";
	public static String PATTERN_FULL_TRANSNO = "yyyyMMddHHmmss";
	public static String PATTERN_FULL_DATE = "yyyyMMdd";
	public static String PATTERN_SHORT = "yyyy-MM-dd";

	// 返回流水号，跟踪日志
	public static String getTransNo(String transNoPrefix) {
		return transNoPrefix + "_" + getTimeStr() + "_" + RandomStringUtils.randomAlphanumeric(6);
	}

	public static String getTimeStr() {
		return DateFormatUtils.format(new Date(), CommonConstants.PATTERN_FULL_TRANSNO);
	}

	public static String getFullTimeStr() {
		return DateFormatUtils.format(new Date(), CommonConstants.PATTERN_FULL);
	}

	// 日志处理方法
	public static String getExceptionTrace(Throwable e) {
		if (e != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		}
		return null;
	}

	// 返回任务号 TaskNo规则：yyMMdd+接口名称缩写4+随机字符6+MD5计算校验码4 =20位
	public static String getTaskNo(String interfaceName) {
		String str = DateFormatUtils.format(new Date(), "yyMMddHH") + interfaceName
				+ RandomStringUtils.randomAlphanumeric(6);
		return str + MD5(str).substring(0, 2);
	}

	// 生成channelId
	public static String creatChannelId(String softName) {
		return MD5(softName + System.currentTimeMillis());
	}

	// 检查任务号-最后4位为校验位
	public static boolean isTaskNo(String taskNo) {
		if (StringUtils.isBlank(taskNo)) {
			return false;
		}
		String startStr = taskNo.substring(0, taskNo.length() - 4);
		String lastStr = taskNo.substring(taskNo.length() - 4);
		String md5Str = MD5(startStr).substring(0, 4);
		if (lastStr.equals(md5Str)) {
			return true;
		}
		return false;
	}

	// MD5摘要
	public static String MD5(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sourceStr.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}
		return result;
	}

	// 是否手机号
	public static boolean isMobile(String str) {
		if (StringUtils.isEmpty(str))
			return false;
		String regex = "(^1\\d{10}$)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	// 是否邮箱格式
	public static boolean isEmail(String str) {
		if (StringUtils.isBlank(str))
			return false;
		String regex = "^(\\w-*\\.*)+@(\\w-?)+(\\.\\w{2,})+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	// 验证是否18位身份证号
	public static boolean isIdCard18(String str) {
		if (StringUtils.isEmpty(str))
			return false;
		String regex = "(^\\d{18}$)|(^\\d{17}[Xx]$)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		boolean flag = false;
		if (matcher.matches()) {
			String oVerifyBit = str.substring(17);
			String nVerifyBit = getVerifyBit(str.substring(0, 17));
			Calendar cal = Calendar.getInstance();
			int sysYear = cal.get(Calendar.YEAR);
			int year = Integer.parseInt(str.substring(6, 10));
			int month = Integer.parseInt(str.substring(10, 12));
			int day = Integer.parseInt(str.substring(12, 14));
			if (year >= (sysYear - INTERVAL) && year <= sysYear && month > 0 && month < 13 && day > 0 && day < 32
					&& StringUtils.equalsIgnoreCase(oVerifyBit, nVerifyBit)) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 获取18位身份证的校验位
	 * 
	 * @param str
	 *            身份证前17位
	 * @return 结果
	 */
	private static final int[] WI = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
	private static final String[] VI = { "1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2" };
	private static final int INTERVAL = 120;

	private static String getVerifyBit(String str) {
		if (StringUtils.isBlank(str) || str.length() != 17)
			return null;
		int[] ai = new int[str.length()];
		for (int i = 0; i < str.length(); i++) {
			ai[i] = Integer.parseInt(str.substring(i, i + 1));
		}
		int num = 0;
		for (int i = 0; i < 17; i++) {
			num += ai[i] * WI[i];
		}
		int remaining = num % 11;
		return VI[remaining];
	}

	/**
	 * 字符串大小写转换 默认大写
	 * 
	 * @param str
	 * @param toLowerCase
	 *            true则小写转换
	 * @return
	 */
	public static String strCaseConvert(String str, boolean toLowerCase) {
		if (StringUtils.isBlank(str)) {
			return null;
		}

		if (toLowerCase == true) {
			return str.toLowerCase();
		}
		return str.toUpperCase();
	}

	public static String strToUpperCase(String str) {
		return strCaseConvert(str, false);
	}

	// 判断是否是IP地址
	public static boolean isIpV4(String str) {
		if (StringUtils.isEmpty(str))
			return false;
		String regex = "(((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.matches();
	}

	// 判断是否中文
	public static boolean isChinese(String str) {
		if (StringUtils.isEmpty(str))
			return false;
		String regex = "^[\u2E80-\u9FFF]+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	// 判断是否是姓名（2-20个汉字）
	public static boolean isName(String str) {
		if (StringUtils.isEmpty(str))
			return false;
		String regex = "^[\u2E80-\u9FFF]{1,20}[·.]{0,1}[\u2E80-\u9FFF]{1,20}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	// 判断是否全部数字
	public static boolean isAllNum(String str) {
		if (StringUtils.isEmpty(str))
			return false;
		String regex = "^\\d+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	// 计算相差的秒数
	public static int calcCostSeconds(Date beginTime) {
		if (beginTime == null) {
			return -1;
		}
		return (int) (System.currentTimeMillis() - beginTime.getTime()) / 1000;
	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件，一次读一整行
	 */
	public static String readByLinesToString(String filePath, String charsetName) {
		if (StringUtils.isBlank(filePath)) {
			return null;
		}
		File file = new File(filePath);
		BufferedReader reader = null;
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), charsetName);
			reader = new BufferedReader(isr);

			StringBuffer sbf = new StringBuffer();
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				sbf.append(tempString + "\n");
			}
			reader.close();
			return sbf.toString();
		} catch (IOException e) {
			logger.debug("读取文件异常：" + CommonUtil.getExceptionTrace(e));
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}

		return null;
	}

	// 删除空白字符，中文、英文空格等
	public static String removeBlank(String str) {
		return str.replaceAll("\\s*", "");
	}

	// Date 转 String
	public static String getFormatDateStr(Date date, String format) {
		return DateFormatUtils.format(date, format);
	}

	// String转Date
	public static Date parseStrToDate(String dateStr, String format) {
		Date date = null;
		try {
			date = DateUtils.parseDate(dateStr, new String[] { format });
		} catch (Exception e) {
			// 转换异常
		}
		return date;
	}

	// 月份String偏移，如输入"201411","yyyyMM",5 -->"201504"
	public static String monthStringAmount(String monthStr, String format, int amount) {
		// String转date
		Date date = parseStrToDate(monthStr, format);

		// date偏移
		date = DateUtils.addMonths(date, amount);

		// date转String
		return DateFormatUtils.format(date, format);
	}

	/* 用正则表达式匹配是否是数字 */
	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("^[0-9]*$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * double转换
	 * 
	 * @param doubleStr
	 * @return
	 */
	public static double parseDouble(String doubleStr) {
		double res = 0;
		try {
			res = Double.parseDouble(doubleStr);
		} catch (Exception e) {
			// 转换失败
		}

		return res;
	}

	/**
	 * 字符串是否是日期
	 * 
	 * @author
	 * @date 2015-7-28
	 * @param strDate
	 *            日期字符串
	 * @param format
	 *            日期格式
	 * @return true:是, false:否
	 */
	public static boolean isDate(String strDate, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			sdf.setLenient(false);
			sdf.parse(strDate);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/**
	 * 根据日期判断是否需要抓取月份 --yyyyMM 最低抓取range个月，且包含当前月，比如201602-201608
	 * 
	 * @param yyyyMM
	 *            传入日期
	 * @param range
	 *            判断月份范围
	 * @return
	 */
	public static boolean isCrawlerDate(String yyyyMM, int range) {
		try {
			Date date = DateUtils.parseDate(yyyyMM, new String[] { "yyyyMM" });
			Date today = new Date();
			if (today.after(date) && today.before(DateUtils.addMonths(date, range + 1))) {
				return true;
			}
		} catch (ParseException e) {
			return false;
		}
		return false;
	}

	// 移除&nbsp;的空格
	public static String removeNbsp(String str) {
		if (StringUtils.isNotBlank(str)) {
			return str.replaceAll(" ", "");
		}
		return null;
	}

	// 移除无效字符如各种空格
	public static String removeInvalidStr(String str) {
		if (StringUtils.isNotBlank(str)) {
			return str.replaceAll(" ", "").replaceAll("　", "").replaceAll("\\s*", "");
		}
		return null;
	}

	// 从html中找到指定的变量所在的最近一行
	public static String getLineFromHtml(String targetStr, String htmlStr) {
		if (StringUtils.isBlank(htmlStr)) {
			return null;
		}
		String[] arrays = htmlStr.split("\n");
		for (String str : arrays) {
			if (str.contains(targetStr)) {
				return str;
			}
		}

		return null;
	}

	// 从文档中找到指定的变量所在的最后一行
	public static String getLastLineFromStr(String targetStr, String htmlStr) {
		if (StringUtils.isBlank(htmlStr)) {
			return null;
		}
		String[] arrays = htmlStr.split("\n");

		for (int i = arrays.length - 1; i >= 0; i--) {
			if (arrays[i].contains(targetStr)) {
				return arrays[i];
			}
		}
		return null;
	}

	// 从html中找到指定的变量所在的行 的集合
	public static List<String> getLineListFromHtml(String targetStr, String htmlStr) {
		if (StringUtils.isBlank(htmlStr)) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		String[] arrays = htmlStr.split("\n");
		for (int i = 0; i < arrays.length; i++) {
			if (arrays[i].contains(targetStr)) {
				list.add(arrays[i]);
			}
		}
		return list;
	}

	/**
	 * 获取之前6个月的第一天:格式:2016-12-01
	 * 
	 * @return
	 */
	public static List<String> getFirstSixMonthFirstDay() {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

		for (int i = 0; i < 6; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
			gcLast.setTime(calendar.getTime());
			gcLast.set(Calendar.DAY_OF_MONTH, 1);
			lst.add(dfdate.format(gcLast.getTime()));
		}

		return lst;
	}

	/**
	 * 获取之前6个月的第一天:自定义个格式
	 * 
	 * @return
	 */
	public static List<String> getSixMonthFirstDayFormat(String format) {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat(format);

		for (int i = 0; i < 6; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
			gcLast.setTime(calendar.getTime());
			gcLast.set(Calendar.DAY_OF_MONTH, 1);
			lst.add(dfdate.format(gcLast.getTime()));
		}

		return lst;
	}

	/**
	 * 获取之前6个月的第一天,自定义格式
	 * 
	 * @return
	 */
	public static List<String> getBeforSixMonthFirFormat(String format) {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat(format);

		for (int i = 0; i < 6; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
			gcLast.setTime(calendar.getTime());
			gcLast.set(Calendar.DAY_OF_MONTH, 1);
			lst.add(dfdate.format(gcLast.getTime()));
		}
		return lst;
	}

	/**
	 * 包含本月 获得当前日期之前6月份yyyy.MM格式
	 * 
	 * @return
	 */
	public static List<String> getFirstSixMonth() {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat("yyyyMM");

		for (int i = 0; i < 6; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			lst.add(dfdate.format(calendar.getTime()));
		}
		return lst;
	}

	/**
	 * 包含本月 获得当前日期之前6月份yyyy-MM格式
	 * 
	 * @return
	 */
	public static List<String> getBeforSixMonth() {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat("yyyy-MM");

		for (int i = 0; i < 6; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			lst.add(dfdate.format(calendar.getTime()));
		}
		return lst;
	}

	/**
	 * 获得当前日期之前6月份yyyy年MM月格式
	 * 
	 * @return
	 */
	public static List<String> getBeforSixMonthCN() {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat("yyyy年MM月");

		for (int i = 0; i < 6; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			lst.add(dfdate.format(calendar.getTime()));
		}
		return lst;
	}

	/**
	 * 月账单信息获取前6个月不包含本月 获得当前日期之前6月份yyyy.MM格式
	 * 
	 * @return
	 */
	public static List<String> getSixMonth() {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat("yyyyMM");

		for (int i = 1; i < 7; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			lst.add(dfdate.format(calendar.getTime()));
		}
		return lst;
	}

	/**
	 * 月账单信息获取前6个月不包含本月 获得当前日期之前6月份yyyy-MM格式
	 * 
	 * @return
	 */
	public static List<String> getBeforMonthWithoutNow() {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat("yyyy-MM");

		for (int i = 1; i < 7; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			lst.add(dfdate.format(calendar.getTime()));
		}
		return lst;
	}

	/**
	 * 获得当前日期之前6月份yyyy/MM格式,包含本月
	 * 
	 * @return
	 */
	public static List<String> getFirstSixMonthLastDay() {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat("yyyy/MM");

		for (int i = 0; i < 6; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			lst.add(dfdate.format(calendar.getTime()));
		}
		return lst;
	}

	/**
	 * 获取之前6个月的最后一天,格式:2016-12-31
	 * 
	 * @return
	 */
	public static List<String> getBeforSixMonthLast() {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

		for (int i = -1; i < 5; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
			gcLast.setTime(calendar.getTime());
			gcLast.set(Calendar.DATE, 1);
			gcLast.add(Calendar.DATE, -1);
			if (gcLast.getTime().after(new Date())) {
				lst.add(dfdate.format(new Date()));
			} else {
				lst.add(dfdate.format(gcLast.getTime()));
			}

		}

		return lst;
	}

	/**
	 * 获取之前6个月的最后一天,格式:2016-12-31 不包含本月
	 * 
	 * @return
	 */
	public static List<String> getBeforMonthLast() {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

		for (int i = 0; i < 6; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
			gcLast.setTime(calendar.getTime());
			gcLast.set(Calendar.DATE, 1);
			gcLast.add(Calendar.DATE, -1);
			if (gcLast.getTime().after(new Date())) {
				lst.add(dfdate.format(new Date()));
			} else {
				lst.add(dfdate.format(gcLast.getTime()));
			}

		}

		return lst;
	}

	/**
	 * 获取之前6个月的最后一天,自定义格式
	 *
	 * @return
	 */
	public static List<String> getBeforSixMonthLastFormat(String format) {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat(format);

		for (int i = -1; i < 5; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
			gcLast.setTime(calendar.getTime());
			gcLast.set(Calendar.DATE, 1);
			gcLast.add(Calendar.DATE, -1);
			if (gcLast.getTime().after(new Date())) {
				lst.add(dfdate.format(new Date()));
			} else {
				lst.add(dfdate.format(gcLast.getTime()));
			}

		}

		return lst;
	}

	/**
	 * 获取之前6个月的最后一天日期
	 * 
	 * @return
	 */
	public static List<String> getFirstSixMonthLastDate() {

		List<String> lst = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();

		DateFormat dfdate = new SimpleDateFormat("dd");

		for (int i = -1; i < 5; i++) {

			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1 * i);

			GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
			gcLast.setTime(calendar.getTime());
			gcLast.set(Calendar.DATE, 1);
			gcLast.add(Calendar.DATE, -1);
			if (gcLast.getTime().after(new Date())) {
				lst.add(dfdate.format(new Date()));
			} else {
				lst.add(dfdate.format(gcLast.getTime()));
			}

		}

		return lst;
	}

	/**
	 * 获取之前12个月的第一天
	 * 
	 * @return
	 */
	public static String getFirstTwelveMonthFirstDay() {

		DateFormat dfdate = new SimpleDateFormat(PATTERN_FULL_DATE);
		Calendar calendar = new GregorianCalendar();

		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1 * 11);

		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		gcLast.setTime(calendar.getTime());
		gcLast.set(Calendar.DAY_OF_MONTH, 1);

		return dfdate.format(gcLast.getTime());
	}

	/**
	 * 获取之前6个月的当天
	 * 
	 * @return
	 */
	public static String getBefor6Month() {

		DateFormat dfdate = new SimpleDateFormat(PATTERN_SHORT);
		Calendar calendar = new GregorianCalendar();

		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1 * 5);

		return dfdate.format(calendar.getTime());
	}

	/**
	 * 获取之前6个月的当天不包含本月； 即上月的前六个月
	 * 
	 * @return
	 */
	public static String getBeforSixsMonth() {

		DateFormat dfdate = new SimpleDateFormat(PATTERN_SHORT);
		Calendar calendar = new GregorianCalendar();

		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1 * 6);

		return dfdate.format(calendar.getTime());
	}

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static String getNowDate() {

		DateFormat dfdate = new SimpleDateFormat(PATTERN_FULL_DATE);

		return dfdate.format(new Date());
	}

	/**
	 * 手机号加空格
	 * 
	 * @param mobile
	 * @return
	 */
	public static String backSpaceSplit(String mobile) {
		StringBuffer sb = new StringBuffer(mobile);
		sb.insert(3, " ");
		sb.insert(8, " ");
		return String.valueOf(sb);
	}

	public static void main(String[] args) {
		// String readStr
		// =readByLinesToString("C:\\Users\\dzp\\Desktop\\银行爬虫\\浦发银行\\积分查询页面.html","utf-8");
		//
		// System.out.println(readStr);
		// System.out.println(isNumber("ooo"));

		// System.out.println(monthStringAmount("20160606", "yyyyMMdd", -1));

		// System.out.println(isDate("2007-02-28 12:00:00",
		// "yyyy-MM-dd HH:mm:ss"));
		System.out.println(isCrawlerDate("201608", 6));

	}

	public static boolean isNum16Bit(String number) {
		if (StringUtils.isBlank(number))
			return false;
		String regex = "^\\d{16}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(number);
		return matcher.matches();
	}

	public static boolean isNum18Bit(String number) {
		if (StringUtils.isBlank(number))
			return false;
		String regex = "^\\d{18}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(number);
		return matcher.matches();
	}

	public static boolean isNum6Bit(String number) {
		if (StringUtils.isBlank(number))
			return false;
		String regex = "^\\d{5}([0-9]|X|x)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(number);
		return matcher.matches();
	}

}
