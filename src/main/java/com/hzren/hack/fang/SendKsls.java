package com.hzren.hack.fang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hzren.http.HttpUtil;
import com.hzren.util.ExecutorUtil;
import com.hzren.util.WebDriverUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hzren
 * Created on 2018/1/14.
 */
public class SendKsls {

    private static final BasicCookieStore cookieStore = new BasicCookieStore();

    public static final void getCookieFile() throws Exception{
        RemoteWebDriver driver = WebDriverUtil.newChromeDriver();
        driver.get("http://zzhzbbs.zjol.com.cn/forum-2-1.html");

        while (true){
            Thread.sleep(20000L);
            HttpUtil.syncCookieFromWebDriver(driver, cookieStore);
            System.out.println();
            System.out.println(JSON.toJSONString(cookieStore));
        }
    }

    static void initCookie() throws Exception{
        String cookie = IOUtils.toString(SendKsls.class.getClassLoader().getResourceAsStream("ksls_cookie.json"));
        JSONObject object = JSON.parseObject(cookie);
        JSONArray cookies = object.getJSONArray("cookies");
        for (Object o : cookies) {
            cookieStore.addCookie(JSON.parseObject(JSON.toJSONString(o), BasicClientCookie.class));
        }
        System.out.println(cookieStore);
    }



    public static void main(String[] args) throws Exception {
        initCookie();
        String tday = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String body = "message=%B5%D8%C7%F8%B3%C9%BD%BB%3A%0D%0A%5Bimg%5Dhttp%3A%2F%2F116.62.21.152%3A8080%2Fksls%2F20180114_diqu.png" +
                "%5B%2Fimg%5D%0D%0A%B6%FE%CA%D6%B3%C9%BD%BB%3A%0D%0A%5Bimg%5Dhttp%3A%2F%2F116.62.21.152%3A8080%2Fksls%2F20180114_ershou.png" +
                "%5B%2Fimg%5D%0D%0A%BF%C9%CA%DB%3A%0D%0A%5Bimg%5Dhttp%3A%2F%2F116.62.21.152%3A8080%2Fksls%2F20180114_keshou.png" +
                "%5B%2Fimg%5D%0D%0A%C0%E0%D0%CD%3A%0D%0A%5Bimg%5Dhttp%3A%2F%2F116.62.21.152%3A8080%2Fksls%2F20180114_leixing.png%5B%2Fimg%5D&" +
                "posttime=1515936345&formhash=80b3e17a&usesig=1&subject=++";
    }

}
