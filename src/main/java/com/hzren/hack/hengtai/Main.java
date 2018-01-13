package com.hzren.hack.hengtai;

import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;
import org.apache.http.entity.ContentType;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author hzren
 * Created on 2017/11/9.
 */
public class Main {
    public static void main(String[] args)throws Exception {
        SimpleHttpExecutor executor = new SimpleHttpExecutor(null, null);

        String body = "content=%7B%22session_id%22%3A%224230ed145e8cb7d0d3dbef36925f16c1%22%2C%22" +
                "start_millis%22%3A%222017-11-17+15%3A47%3A46%22%2C%22end_millis%22%3A%222017-11-17+" +
                "15%3A47%3A52%22%2C%22duration%22%3A%225952%22%2C%22version%22%3A%22V9.00.05%22%2C%22" +
                "activities%22%3A%22jiaoyi_chaxun%22%2C%22appkey%22%3A%22284350ca6e436a86c0d46e35af5e707b" +
                "%22%2C%22userid%22%3A%2218042000452%22%2C%22deviceid%22%3A%221e5436cc743103b5da341a2823428f9b%22%7D";

        Request request = Request.Post("https://47.93.153.208:1443/cbasums/ums/postActivityLog")
                .addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 7.1.1; MIX 2 MIUI/V8.5.7.0.NDECNEF)")
                .bodyString(body, ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8));

        System.out.println(executor.requestAsSting(request));
    }


    //对密码等需要加密的数据进行数据加密
//    function encrypt(t){
//        var out = "";
//        var str_in = escape(t);
//        for(var i=0; i<str_in.length;i++) {
//            out += str_in.charCodeAt(i)-23;
//        }
//        return out;
//    }
    public static String encrypt(String origin){
        try {
            StringBuilder res = new StringBuilder();

            origin = URLEncoder.encode(origin, "UTF-8");
            for (int i = 0; i <origin.length() ; i++) {
                res.append(origin.charAt(i) - 23);
            }

            return res.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
