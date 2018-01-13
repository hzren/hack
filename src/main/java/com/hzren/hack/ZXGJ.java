package com.hzren.hack;

import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;
import org.apache.http.entity.ContentType;

import java.nio.charset.StandardCharsets;

/**
 * @author hzren
 * Created on 2017/11/17.
 */
public class ZXGJ {

    public static void main(String[] args) {
        SimpleHttpExecutor executor = new SimpleHttpExecutor(null, null);

        String body = "{\"gddm\":\"\",\"zqdm\":\"\",\"wtxh\":\"\",\"cxfx\":\"1\",\"qqhs\":\"1000\",\"dwc\":\"\",\"khbz\":\"56138011\",\"khbzlx\":\"Z\",\"jymm\":\"123321\",\"sessionid\":\"0\",\"token\":\"0\",\"yybdm\":\"1651\",\"lhxx\":\"18042000452,865736034356025,2.4.2.20171027.66099+3.1.2.20171027132708+xiaomi,460110193673976,02:00:00:00:00:00,5369a912be1f71f,100.123.170.148\"}";
        Request request = Request.Post("https://sjzqali.csc108.com:9801/api/trade/ptjy/ptyw/drwtcx")
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 7.1.1; MIX 2 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/59.0.3071.125 Mobile Safari/537.36")
                .addHeader("X-Requested-With", "zhongxinjiantou.szkingdom.android.newphone")
                .bodyString(body, ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8));

        System.out.println(executor.requestAsSting(request));
    }
}
