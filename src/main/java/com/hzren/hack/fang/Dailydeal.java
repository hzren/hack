package com.hzren.hack.fang;

import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;
import com.hzren.util.WebDriverUtil;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hzren
 * Created on 2018/1/14.
 */
public class Dailydeal {

    public static final SimpleHttpExecutor EXECUTOR = new SimpleHttpExecutor(null, null);

    /**
     * 获取今日成交图片,返回突变保存地址
     *
     * */
    public static final void saveDailyDeal()  throws Exception{
        String date = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String url = "http://www.hzfc.gov.cn/scxx/";
        Document document = Jsoup.parse(new URL("http://www.hzfc.gov.cn/scxx/"), 1000);
        Elements elements = document.getElementsByTag("img");
        for (Element element : elements) {
            String address = element.absUrl("src");
            if (!address.contains("getScxxPic.php")){
                continue;
            }
            String savepath = null;
            if (address.contains("jrspfksxx")){
                savepath = "/home/soft-files/ksls/" + date + "_keshou" + ".png";
            }else if (address.contains("spfljcjxxfwlxjr")){
                savepath = "/home/soft-files/ksls/" + date + "_leixing" + ".png";
            }else if (address.contains("spfljcjxxqyjr")){
                savepath = "/home/soft-files/ksls/" + date + "_diqu" + ".png";
            }else if (address.contains("esfljcjxxjr")){
                savepath = "/home/soft-files/ksls/" + date + "_ershou" + ".png";
            }else {
                continue;
            }

            byte[] data = EXECUTOR.requestAsByte(Request.Get(address));
            FileUtils.writeByteArrayToFile(new File(savepath), data, true);
        }

    }

    public static void main(String[] args) throws Exception {
        saveDailyDeal();
    }

}
