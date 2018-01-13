package com.hzren.stock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hzren.http.HttpExecutor;
import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;
import com.hzren.util.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author hzren
 * Created on 2017/11/14.
 */
public class FindStock {

    private static final Double RATE_UP = Double.parseDouble("11");
    private static final Double RATE_DOWN = Double.parseDouble("9.96");


    private static final SimpleHttpExecutor EXECUTOR = new SimpleHttpExecutor(null, null);

    public static void main(String[] args) throws Exception {
        System.out.println(EXECUTOR.requestAsSting(Request.Get("https://xueqiu.com/")));

        String host = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx";

        for (int k = 1; k < 3; k++) {
            String query = "type=CT&cmd=C.BK05011&sty=FCOIATA&sortType=L&sortRule=-1&page=" + k +
                    "&pageSize=20&js=var%20quote_123%3d{rank:[(x)],pages:(pc)}&token=44c9d251add88e27b65ed86506f6e5da&jsName=quote_123&_g=0.5908637586854584";

            URIBuilder builder = new URIBuilder(host);
            builder.setQuery(query);
            String data = EXECUTOR.requestAsSting(Request.Get(builder.build()));
            String prefix = "var quote_123={rank:", suffix = ",pages:26}";
            data = data.substring(prefix.length(), data.length() - suffix.length());

            JSONArray rank = JSON.parseArray(data);

            for (int i = 0; i < rank.size(); i++){
                String stock = rank.getString(i);
                String[] details = stock.split(",");
                String stockCode = details[1], stockName = details[2];
                String dayUp = details[5];
                Double dayUpRate = Double.parseDouble(dayUp.substring(0, dayUp.length() - 1));
                if (dayUpRate.compareTo(RATE_DOWN) < 0 || dayUpRate.compareTo(RATE_UP) > 0){
                    //continue;
                }
                getStockDetail(stockCode, stockName);
            }
        }
    }

    private static void getStockDetail(String stockCode, String stockName){
        if (stockCode.startsWith("6")){
            stockCode = "SH" + stockCode;
        }else {
            stockCode = "SZ" + stockCode;
        }

        String url = "https://xueqiu.com/stock/forchartk/stocklist.json?symbol=" + stockCode +
                "&period=1day&type=normal&begin=1479113658247&end=" + System.currentTimeMillis();

        Logger.log("https://xueqiu.com/S/" + stockCode);

        JSONArray array = JSON.parseObject(EXECUTOR.requestAsSting(Request.Get(url))).getJSONArray("chartlist");
        long totalVolume = 0;
        Double totalRate = 0.00;
        ArrayList<Long> dayVolumes = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
             JSONObject day = array.getJSONObject(i);
            totalVolume = totalVolume + day.getLong("volume");
            dayVolumes.add(day.getLong("volume") / 100);
            totalRate = totalRate + day.getDouble("turnrate");
        }
        //计算手数
        long total = totalVolume / 100;
        if (total < 1000){
            //return;
        }
        Logger.log(stockCode + "-" + stockName + ", 总成交:" + total + "手, 总成交量:" + totalRate + "%");
        Logger.log("上市天数:" + dayVolumes.size() + "历史每日成交手数: " + dayVolumes);
    }
}
