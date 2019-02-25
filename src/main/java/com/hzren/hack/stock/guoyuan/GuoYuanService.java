package com.hzren.hack.stock.guoyuan;

import com.alibaba.fastjson.JSON;
import com.hzren.hack.stock.api.StockUtils;
import com.hzren.hack.stock.api.StockInfo;
import com.hzren.http.HttpUtil;
import com.hzren.http.SimpleHttpExecutor;
import com.hzren.util.WebDriverUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.nio.file.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author hzren
 * Created on 2017/11/10.
 */
public class GuoYuanService {

    public static final String LOGIN_PAGE = "https://trade.gyzq.com.cn/deskProduct/views/login.html";
    public static final String LOGIN_SUCCESS_PAGE = "https://trade.gyzq.com.cn/deskProduct/views/trade/AStockTrade/tradeStockA.html?action=buy&isLogin=true";
    public static final String CANCEL_PAGE = "https://trade.gyzq.com.cn/deskProduct/views/trade/AStockTrade/stockCancellations.html";

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
        chromeDriver.get(LOGIN_SUCCESS_PAGE);
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
        //测试期间暂时设置为只买一手
//        numEl.sendKeys(buyNum);
        numEl.sendKeys("100");

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

    public void cancelBuy(StockInfo stockInfo){
        chromeDriver.get(CANCEL_PAGE);
        String code = stockInfo.getCode();
        WebElement tbody = chromeDriver.findElement(By.id("queryList"));
        List<WebElement> trs = tbody.findElements(By.tagName("tr"));
        for (WebElement tr : trs) {
            List<WebElement> tds = tr.findElements(By.tagName("td"));
            if (tds.size() != 10){
                continue;
            }
            WebElement codeEl = tds.get(2);
            if (!code.equals(codeEl.getText().trim())){
                continue;
            }
            tds.get(0).click();
            WebElement xubox_botton = chromeDriver.findElement(By.className("xubox_botton"));
            WebElement button = xubox_botton.findElement(By.className("xubox_yes"));
            button.click();
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        GuoYuanService service = new GuoYuanService();
        service.login();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new RefreshPageTask(service), 10, 10, TimeUnit.SECONDS);

        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(StockUtils.getExchangeDir().toURI());
        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        StockInfo old = null;
        while (LocalTime.now().compareTo(StockUtils._15_00) <= 0){
            WatchKey key = watchService.take();
            List<WatchEvent<?>> events = key.pollEvents();
            for (WatchEvent event : events){
                if (event.kind() != StandardWatchEventKinds.ENTRY_MODIFY){
                    continue;
                }
            }
            if (!key.reset()){
                System.exit(-1);
            }
            //解析文件内容
            String json = FileUtils.readFileToString(StockUtils.getExchangeFile()).trim();
            if (StringUtils.isBlank(json)){
                continue;
            }
            StockInfo stockInfo = JSON.parseObject(json, StockInfo.class);
            if (old == null){
                executorService.submit(new BuyStockTask(service, stockInfo));
                old = stockInfo;
                continue;
            }
            if (Objects.equals(stockInfo.getCode(), old.getCode())){
                continue;
            }
            executorService.submit(new CancelStockTask(service, old));
            executorService.submit(new BuyStockTask(service, stockInfo));
            old = stockInfo;
        }
        service.close();
        watchService.close();
        executorService.shutdownNow();
    }

}
