package com.hzren.hack.fang;

import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
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
        System.out.println("----HTML-------" + date + "--------START------");
        System.out.println(document.toString());
        System.out.println("----HTML-------" + date + "--------END------");
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
            FileUtils.writeByteArrayToFile(new File(savepath), data, false);
        }

    }

    public static void main(String[] args) throws Exception {
        saveDailyDeal();
    }

}
