package com.hzren.hack.zheshang;

import com.alibaba.fastjson.JSON;
import com.hzren.http.HttpUtil;
import com.hzren.util.NetUtil;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.concurrent.TimeUnit;

/**
 * @author hzren
 * Created on 2017/11/13.
 */
public class ZheShangMain2 {

    static RemoteWebDriver driver ;
    static final String ZJZH = "zijinzhanghao";
    static final String PASSWD = "mima";

    public static void main(String[] args) throws Exception {
        initChromeDriver();

        driver.get("https://web.stocke.com.cn/deskProduct/views/login.html");
        addAllCookie();

        driver.get("https://web.stocke.com.cn/deskProduct/views/trade/AStockTrade/tradeStockA.html?action=buy&isLogin=true");

        Thread.sleep(1500 * 1000L);

    }


    static void initChromeDriver(){
        System.setProperty("webdriver.chrome.driver", "E://dev_soft//web_driver//chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        // TODO
        options.setBinary("C://Users//hzren//AppData//Local//Google//Chrome//Application//chrome.exe");
        String setUA = "-user-agent=" + HttpUtil.HEADER_IE;
        options.addArguments("-incognito", setUA);
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    static void addAllCookie(){
        Cookie ip = new Cookie("ip", NetUtil.getLocalIp());
        Cookie mac = new Cookie("mac","48-BA-4E-40-D3-6F|CC-2F-71-3D-F7-6B|CC-2F-71-3D-F7-6C");
        Cookie hardInfo = new Cookie("hardInfo", "INTEL SSDPEKKF256G7H(00000_00_010000_00_04E2D5_7C_0B63A4_0D.1)|TOSHIBA MQ01ACF050(7 17WD7MTC)");

        Cookie pe = new Cookie("publicExponent", "10001");

        String modules = "dd6be3d4de56287b8c3616b33bc1b7a5a2bb9148252140262420ee047f83b3165fb76" +
                "74a759d60c24b71fd5437c7810f127f2c4370c2d4bdfcb55c08f1b3c715b7b2f57228e78e34039d2b96" +
                "7f54a58e345bc91e3dd54c7bea86d73c9e2de968736bf2b97f50bea891aa3519ae7238d76dff57cabba7c" +
                "c0d370775657f3b5c83";
        Cookie m = new Cookie("modulus", modules);

        String info = "{\"branch_no\":\"83\",\"fund_account\":\"2070012282\",\"cust_code\":\"2070012282\"" +
                ",\"risk_level\":\"4\",\"exchange_type\":\"2\",\"stock_account\":\"A809189868\"" +
                ",\"client_name\":\"%E4%BB%BB%E7%BA%A2%E9%98%B5\"}";
        Cookie clientinfo = new Cookie("clientinfo", info);

        Cookie clientAccount = new Cookie("clientAccount", ZJZH);

        driver.manage().addCookie(ip);
        driver.manage().addCookie(mac);
        driver.manage().addCookie(hardInfo);

        driver.manage().addCookie(pe);

        driver.manage().addCookie(m);

        driver.manage().addCookie(clientinfo);
        driver.manage().addCookie(clientAccount);
    }
}
