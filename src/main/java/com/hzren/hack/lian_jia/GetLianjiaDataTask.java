package com.hzren.hack.lian_jia;

import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author tuomasi
 * Created on 2019/3/19.
 */
public class GetLianjiaDataTask implements Runnable {

    private final SimpleHttpExecutor executor = new SimpleHttpExecutor(null, null);

    @Override
    public void run() {
        LocalDate now = LocalDate.now();
        String day = now.format(DateTimeFormatter.BASIC_ISO_DATE);
        System.out.println("抓取:" + day + "数据...");
        Request request = Request.Get("https://hz.lianjia.com/fangjia/");
        String resp = executor.requestAsSting(request);
        String prefix = "\"showAmount\":";
        String suffix = ",";
        int start = resp.indexOf(prefix) + prefix.length();
        int end = resp.indexOf(suffix, start);
        String riDaiKan = resp.substring(start, end);
        String text = day + "日带看" + riDaiKan;
        System.out.println(text);
        String fpath = "/root/lianjia/lianjia_ri_daikan.csv";
        try {
            FileWriterWithEncoding writer = new FileWriterWithEncoding(fpath, StandardCharsets.UTF_8, true);
            writer.write(System.lineSeparator());
            writer.write(" " + day + " , 日带看 , " + riDaiKan + " ");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new GetLianjiaDataTask().run();
    }
}
