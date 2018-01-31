package com.lx.selenium;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lx.utils.CommonUtil;
import com.lx.utils.HtmlStorageUtil;
import com.lx.utils.TimeUtil;

public class Test {
	public static Logger logger = LoggerFactory.getLogger(TimeUtil.class);
	public static void main(String[] args) {
//		 System.setProperty("webdriver.firfox.driver",
//		 "D:\\java\\selenium\\geckodriver.exe");
//		 WebDriver webDriver = new FirefoxDriver();
//		 webDriver.manage().window().maximize();
//		 webDriver.get("http://www.baidu.com");
//		 WebElement kw = webDriver.findElement(By.id("kw"));
//		 kw.sendKeys("暗算");
//		 WebElement su = webDriver.findElement(By.id("su"));
//		 su.click();
//		 // webDriver.close();
//		 System.out.println("Hello World!");
		// TODO Auto-generated method stub
		System.setProperty("webdriver.chrome.driver", "C:\\browser\\chromedriver.exe");
		WebDriver driver3 = new ChromeDriver();
		try {
			String str = getBaiDu(driver3);
			HtmlStorageUtil.writeHtmlToFile(str);
			Thread.sleep(3000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driver3.quit();
	}
	
	public static String getBaiDu(WebDriver driver3){
		driver3.get("https://www.baidu.com");
		driver3.findElement(By.id("kw")).sendKeys("hello Selenium");
		driver3.findElement(By.id("su")).click();
		String str = driver3.getPageSource();
		return str;
	}
	
}
