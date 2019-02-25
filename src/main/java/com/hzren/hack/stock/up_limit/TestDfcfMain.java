package com.hzren.hack.stock.up_limit;

import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;

import java.util.Objects;

/**
 * @author tuomasi
 * Created on 2018/9/21.
 */
public class TestDfcfMain {

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
        Request request = Request.Get("http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?" +
                "cb=jQuery112407252246451897015_1537421014827" +
                "&type=CT&token=4f1862fc3b5e77c150a2b985b12db0fd" +
                "&sty=FCOIATC&js=(%7Bdata%3A%5B(x)%5D%2CrecordsFiltered%3A(tot)%7D)" +
                "&cmd=C._A&st=(ChangePercent)&sr=-1&p=1&ps=" + 40 + "&_=1537421014828");
        request.addHeader("Referer", "http://stockapp.finance.qq.com/mstats/");
        request.addHeader("Upgrade-Insecure-Requests", "1");
        request.addHeader("Pragma", "no-cache");
        return executor.requestAsSting(request);
    }
}
