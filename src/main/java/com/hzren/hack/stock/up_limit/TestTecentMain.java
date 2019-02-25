package com.hzren.hack.stock.up_limit;

import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;

import java.util.Objects;

/**
 * @author tuomasi
 * Created on 2018/9/21.
 */
public class TestTecentMain {

    private static SimpleHttpExecutor executor = new SimpleHttpExecutor(null, null);

    public static void main(String[] args) throws Exception {
        String lastResp = null;
        int i = 0;
        while (i++ < 120){
            String resp = getResp();
            if (lastResp != null && !Objects.equals(lastResp, resp)){
                System.out.println(lastResp);
                System.out.println(resp);
                System.out.println(i);
            }
            lastResp = resp;
            Thread.sleep(1000L);
        }
    }

    public static String getResp(){
        Request request = Request.Get("http://qt.gtimg.cn/q=sz300748,sz300005,sz000428,sz300004,sz002936,sz002780,sz300526,sz002858,sz300340,sz300173,sz002667,sz002229,sz000955,sz300490,sz002359,sz002163,sz000017,sz300247,sz002702,sz300715,sz002181,sz300132,sz002002,sz002800,sz300162&r=486997043");
        request.addHeader("Referer", "http://stockapp.finance.qq.com/ms/pushiframe.html?_u=424330.5133971869510956");
        request.addHeader("Upgrade-Insecure-Requests", "1");
        request.addHeader("Pragma", "no-cache");
        return executor.requestAsSting(request);
    }
}
