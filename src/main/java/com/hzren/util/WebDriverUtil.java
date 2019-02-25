package com.hzren.util;

/**
 * @author hzren
 * Created on 2017/11/13.
 */

import com.hzren.http.HttpUtil;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;

import java.util.concurrent.TimeUnit;

/***
 *
 * 负责WebDriver对象的实例化
 *
 */
public class WebDriverUtil {

    static {
        //init ie configuration
        System.setProperty("webdriver.ie.driver", "E://dev_soft//web_driver//IEDriverServer.exe");
        System.setProperty("webdriver.ie.driver.host", "127.0.0.1");
        System.setProperty("webdriver.ie.driver.loglevel", "TRACE");
        System.setProperty("webdriver.ie.driver.logfile", "E://dev_soft//web_driver//IEDriver.log");

        //init chrome configuration
        System.setProperty("webdriver.chrome.driver", "E://dev_soft//web_driver//chromedriver.exe");
        //init firefox configuration
        System.setProperty("webdriver.gecko.driver", "E://dev_soft//web_driver//geckodriver.exe");
        System.setProperty("webdriver.chrome.driver", "/Users/hongzhenren/Applications/chromedriver");
    }

    public static ChromeDriver newChromeDriver(){
        ChromeOptions options = new ChromeOptions();
        // TODO
        options.setBinary("C://Program Files (x86)//Google//Chrome//Application//chrome.exe");
//        String setUA = "-user-agent=" + HttpUtil.HEADER_IE;
//        options.addArguments("-incognito", setUA);
        //options.setBinary("C://Users//hzren//AppData//Local//Google//Chrome//Application//chrome.exe");
        String setUA = "-user-agent=" + HttpUtil.HEADER_IE;
        options.addArguments("-incognito", setUA);
        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    public static FirefoxDriver newFirefoxDriver(){
        FirefoxOptions options = new FirefoxOptions();
        // TODO
        options.setBinary("C://Program Files (x86)//Mozilla Firefox//firefox.exe");
        FirefoxDriver driver = new FirefoxDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    public static InternetExplorerDriver newIEDriver(){
        InternetExplorerOptions options = new InternetExplorerOptions();
//        options.setCapability("ie.forceCreateProcessApi", "true");
//        options.setCapability("ie.browserCommandLineSwitches", "-private");
//        options.setCapability("ie.ensureCleanSession", "true");
        options.ignoreZoomSettings().useShellWindowsApiToAttachToIe();
        options.addCommandSwitches("--port=5555", "");
        InternetExplorerDriver driver = new InternetExplorerDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }
}
