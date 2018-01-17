package com.hzren.hack.fang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.hzren.http.HttpUtil;
import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;
import com.hzren.util.ExecutorUtil;
import com.hzren.util.WebDriverUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author hzren
 * Created on 2018/1/14.
 */
public class SendKsls {

    private static final BasicCookieStore cookieStore = new BasicCookieStore();
    private static final SimpleHttpExecutor EXECUTOR = new SimpleHttpExecutor(null, cookieStore);

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
            JSONObject jc = (JSONObject) o;

            BasicClientCookie bcc = new BasicClientCookie(jc.getString("name"), jc.getString("value"));
            bcc.setPath(jc.getString("path"));
            bcc.setDomain(jc.getString("domain"));
            bcc.setSecure(jc.getBoolean("secure"));
            bcc.setVersion(jc.getInteger("version"));
            bcc.setExpiryDate(jc.getDate("expiryDate"));

            cookieStore.addCookie(bcc);
        }
        System.out.println(cookieStore);
    }

    static void doPostKsls(String tday) throws Exception{
        initCookie();
        String thread = "http://zzhzbbs.zjol.com.cn/thread-21476149-1-1.html";
        Document document = Jsoup.parse(EXECUTOR.requestAsSting(Request.Get(thread)));
        Element form = document.selectFirst("#fastpostform");
        String url = form.absUrl("action") + "&inajax=1";
        String formahash = form.select("input[name=formhash]").val();
        String usesig = form.select("input[name=usesig]").val();
        String subject = form.select("input[name=subject]").val();
        String posttime = form.select("input[name=posttime]").val();
        String message = "20180114 数据\n"
                + "可售:\n" + "[img]http://116.62.21.152:8080/ksls/20180114_keshou.png[/img]\n"
                + "类型成交:\n" + "[img]http://116.62.21.152:8080/ksls/20180114_leixing.png[/img]\n"
                + "地区成交:\n" + "[img]http://116.62.21.152:8080/ksls/20180114_diqu.png[/img]\n"
                + "二手成交:\n" + "[img]http://116.62.21.152:8080/ksls/20180114_ershou.png[/img]\n";

        message = message.replace("20180114", tday);

        Request request = Request.Post(url)
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("Origin", "http://zzhzbbs.zjol.com.cn")
                .addHeader("Referer", thread)
                .bodyFormGBK(
                new BasicNameValuePair("formhash", formahash),
                new BasicNameValuePair("usesig", usesig),
                new BasicNameValuePair("subject", subject),
                new BasicNameValuePair("message", message),
                new BasicNameValuePair("posttime", posttime));

        String resp = EXECUTOR.requestAsSting(request);

        System.out.println(resp);
    }



    public static void main(String[] args) throws Exception {
        //String tday = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        //doPostKsls("20180114");
        doPostKsls("20180115");
        Thread.sleep(4000);
        doPostKsls("20180116");
    }

}
