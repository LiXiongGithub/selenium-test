package com.lx.utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlStorageUtil {


	protected static Logger logger = LoggerFactory.getLogger(HtmlStorageUtil.class);

	/**
	 * 是否存在,如果存在则不允许写入，不存在可以写入
	 */
	private boolean exist;
	private String bankCode;
	/**
	 * 抓取方法， 分为pc、mobile两种
	 */
	private String crawlMethod = "pc";
	/**
	 * 任务文件夹
	 */
	private String taskKey;
	/**
	 * 配置文件名
	 */
	public static final String CONFIG_FILE = "htmlStorage";
	/**
	 * 存放文件的根目录
	 */
	public static String HOME_PATH;
	/**
	 * 是否启用
	 */
	public static boolean ENABLED = false;
	static {
		init();
	}

	/**
	 * 初始化路径参数
	 */
	private static void init() {
		ENABLED = Boolean.parseBoolean(PropertyManager.getRB(CONFIG_FILE, "enabled"));
		HOME_PATH = PropertyManager.getRB(CONFIG_FILE, "home_path");
		HOME_PATH = new File(HOME_PATH).getPath();
	}

	private HtmlStorageUtil() {
	}

	private HtmlStorageUtil(String bankCode, boolean exist, String crawlMethod) {
		this.bankCode = bankCode;
		this.exist = exist;
		this.crawlMethod = crawlMethod;
	}
	
	private HtmlStorageUtil(String bankCode, boolean exist, String crawlMethod,String taskKey) {
		this.bankCode = bankCode;
		this.exist = exist;
		this.crawlMethod = crawlMethod;
		this.taskKey = taskKey;
	}
	
	/**
	 * 取得PC版抓取实例-新版
	 * 
	 * @param bankCode
	 * @return
	 */
	public static HtmlStorageUtil getPcInstance(String bankCode,
			String taskKey) {
		if (ENABLED) {
				return new HtmlStorageUtil(bankCode, false, "pc",taskKey);
		}
		return new HtmlStorageUtil(bankCode, true, "pc",taskKey);
	}

	/**
	 * 取得PC版抓取实例
	 * 
	 * @param bankCode
	 * @return
	 */
	public static HtmlStorageUtil getPcInstance(String bankCode) {

		if (ENABLED) {
			boolean exist_ = exists(bankCode, "pc");
			if (!exist_) {
				return new HtmlStorageUtil(bankCode, false, "pc");
			}
		}
		return new HtmlStorageUtil(bankCode, true, "pc");
	}

	/**
	 * 取得手机版抓取实例
	 * 
	 * @param bankCode
	 * @return
	 */
	public static HtmlStorageUtil getMobileInstance(String bankCode) {

		if (ENABLED) {
			boolean exist_ = exists(bankCode, "mobile");
			if (!exist_) {
				return new HtmlStorageUtil(bankCode, false, "mobile");
			}
		}
		return new HtmlStorageUtil(bankCode, true, "mobile");
	}

	/**
	 * 存储待缓存的文件
	 * 
	 * @param fileType
	 *            文件类型
	 * @param data
	 *            文件内容
	 * @param fileName
	 *            文件名
	 * @param encoding
	 *            编码
	 * @return
	 */
	public boolean store(String fileType, String data, String fileName, String encoding) {
		if (this.exist) {
			return false;
		}
		String path = generateFileName(this.bankCode, this.crawlMethod,this.taskKey, fileType, fileName);
		System.out.println("路径:"+path);
		File file = new File(path);
		try {
			FileUtils.write(file, data, encoding);
			logger.info("write html to " + path);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String generateFileName(String bankCode, String crawlMethod,
			String taskKey, String fileType, String fileName) {
		Date d = new Date();
		String dStr = DateFormatUtils.format(d, "yyyyMMdd_HHmmss_");
		dStr += (d.getTime() % 1000);
		if (fileName == null) {
			fileName = "";
		}
		String path = HOME_PATH + "/" + bankCode + "/" + crawlMethod + "/"+taskKey+"/" + fileType + "/" + fileName + "___" + dStr + ".html";
		return path;
	}

	private static boolean exists(String bankCode, String crawlMethod) {
		File file = new File(HOME_PATH + "/" + bankCode + "/" + crawlMethod);
		boolean exist_ = file.exists();
		return exist_;
	}

	/**
	 * 生成文件名
	 * 
	 * @param bankCode
	 * @param fileType
	 * @param fileName
	 * @return
	 */
	private static String generateFileName(String bankCode, String crawlMethod, String fileType, String fileName) {
		Date d = new Date();
		String dStr = DateFormatUtils.format(d, "yyyyMMdd_HHmmss_");
		dStr += (d.getTime() % 1000);
		if (fileName == null) {
			fileName = "";
		}
		String path = HOME_PATH + "/" + bankCode + "/" + crawlMethod + "/" + fileType + "/" + fileName + "___" + dStr + ".html";
		return path;
	}

	/**
	 * 文件类型
	 * 
	 * @title:
	 * @description：
	 * @author xuwanhai
	 * @date 2016年11月16日
	 */
	public static class FileType {
		/**
		 * 账单
		 */
		public static final String BILL = "bill";
		/**
		 * 账单明细
		 */
		public static final String BILL_DETAIL = "bill_detail";
		/**
		 * 额度、卡号等卡基本信息
		 */
		public static final String CARD = "card";
		/**
		 * 短信验证码
		 */
		public static final String SMS = "sms";
		/**
		 * 登录页面
		 */
		public static final String LOGIN = "login";
	}

	/**
	 * 写入文件
	 * @param str
	 */
	public static void writeHtmlToFile(String str){
		try {
			String day = TimeUtil.getTimeStr(new Date(), "yyyy" + File.separator + "MM" + File.separator + "dd");
			String path =  CommonUtil.getTransNo("Selenium");;
			String fName = HtmlStorageUtil.HOME_PATH + File.separator + "INSUhtml" + File.separator + day + File.separator + path + ".html";
			FileUtils.writeStringToFile(new File(fName), str, "utf-8");
			logger.info("write html to " + fName);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) throws Exception {
		String bankCode = "SPD";
		String fileType = FileType.BILL;
		String encoding = "utf-8";
		String FileName = "F:\\爬虫项目材料\\浦发银行\\历史_账单列表.html";
		File file = new File(FileName);
		String data = FileUtils.readFileToString(file, "GB2312");
		HtmlStorageUtil htmlStorageUtil = HtmlStorageUtil.getPcInstance(bankCode);
		boolean storageResult = htmlStorageUtil.store(fileType, data, "2016-09", encoding);
		System.out.println(storageResult);

		storageResult = htmlStorageUtil.store(fileType, data, "2016-08", encoding);
		System.out.println(storageResult);
	}

}
