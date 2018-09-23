package com.hzren.hack.stock.guoyuan;

import com.hzren.hack.stock.StockInfo;
import com.hzren.hack.stock.StockUtils;
import com.hzren.http.HttpUtil;
import com.hzren.http.SimpleHttpExecutor;
import com.hzren.util.WebDriverUtil;
import org.apache.http.impl.client.BasicCookieStore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author hzren
 * Created on 2017/11/10.
 */
public class GuoYuanService {
    private static final BigDecimal MONEY = BigDecimal.valueOf(10000);
    public static final String LOGIN_PAGE = "https://trade.gyzq.com.cn/deskProduct/views/login.html";
    public static final String LOGIN_SUCCESS_PAGE = "https://trade.gyzq.com.cn/deskProduct/views/trade/AStockTrade/tradeStockA.html?action=buy&isLogin=true";

    private final BasicCookieStore cookieStore = new BasicCookieStore();
    private SimpleHttpExecutor httpExecutor = new SimpleHttpExecutor(null, cookieStore);
    private ChromeDriver chromeDriver = WebDriverUtil.newChromeDriver();
    private FirefoxDriver firefoxDriver = WebDriverUtil.newFirefoxDriver();

    public void login() throws Exception{
        saveCookie();
        Thread.sleep(1000);
        chromeLogin(chromeDriver);
        Thread.sleep(1000);
    }

    private void chromeLogin(ChromeDriver chromeDriver){
        chromeDriver.get(LOGIN_PAGE);
        HttpUtil.syncCookieFromHttpClient(cookieStore, chromeDriver);
        chromeDriver.get(LOGIN_SUCCESS_PAGE);
    }

    private void saveCookie() throws Exception{
        FirefoxDriver firefoxDriver = WebDriverUtil.newFirefoxDriver();
        firefoxDriver.get(LOGIN_PAGE);
        doLogin(firefoxDriver);
        HttpUtil.syncCookieFromWebDriver(firefoxDriver, cookieStore);
    }

    private void doLogin(FirefoxDriver firefoxDriver) throws Exception{
        boolean login = false;
        String ticket = "";
        while (!login){
            String url = firefoxDriver.getCurrentUrl();
            if (Objects.equals(url, LOGIN_SUCCESS_PAGE)){
                login = true;
                return;
            }else {
                Thread.sleep(1000);
            }
        }
    }

    public void buy(StockInfo info){
        WebElement codeEl = chromeDriver.findElement(By.id("tabBuy")).findElement(By.id("stock_code"));
        codeEl.click();
        codeEl.sendKeys(info.getCode());

        WebElement numEl = chromeDriver.findElement(By.id("num"));
        numEl.click();
        numEl.sendKeys(StockUtils.calCanByNum(MONEY, info.getPrice()));

        WebElement submit = chromeDriver.findElement(By.id("submitBtn"));
        submit.click();
    }

    public void close(){
        chromeDriver.quit();
        firefoxDriver.quit();
    }

    public void refreshPage(){
        chromeDriver.get(LOGIN_SUCCESS_PAGE);
    }

}
