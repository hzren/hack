package com.hzren.hack.stock.guoyuan;

import com.hzren.hack.stock.StockInfo;
import com.hzren.http.HttpUtil;
import com.hzren.http.SimpleHttpExecutor;
import com.hzren.util.WebDriverUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Objects;

/**
 * @author hzren
 * Created on 2017/11/10.
 */
public class GuoYuanService {

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
        WebElement tabBuy = chromeDriver.findElement(By.id("tabBuy"));
        WebElement codeEl = tabBuy.findElement(By.id("stock_code"));
        codeEl.click();
        codeEl.sendKeys(info.getCode());
        //找到页面上的最大可买元素,获取其值
        String buyNum = null;
        WebElement maxCanBuy = tabBuy.findElement(By.id("maxNum"));
        while (true){
            buyNum = maxCanBuy.getAttribute("value");
            if (StringUtils.isBlank(buyNum)){
                continue;
            }
            break;
        }
        WebElement numEl = chromeDriver.findElement(By.id("num"));
        numEl.click();
        numEl.sendKeys(buyNum);

        WebElement submit = chromeDriver.findElement(By.id("submitBtn"));
        submit.click();

        WebElement dialog = chromeDriver.findElement(By.id("entrust"));
        WebElement sureButton = dialog.findElement(By.className("dialog_btn")).findElements(By.tagName("a")).get(1);
        sureButton.click();
    }

    public void close(){
        chromeDriver.quit();
        firefoxDriver.quit();
    }

    public void refreshPage(){
        chromeDriver.get(LOGIN_SUCCESS_PAGE);
    }

    public static void main(String[] args) throws Exception {
        GuoYuanService service = new GuoYuanService();
        service.login();
        StockInfo stockInfo = new StockInfo("002530", "金财互联");
        service.buy(stockInfo);
        Thread.sleep(3600L * 1000);
    }

}
