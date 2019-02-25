package com.hzren.hack.stock.up_limit;

import com.alibaba.fastjson.JSON;
import com.hzren.hack.stock.api.StockInfo;
import com.hzren.hack.stock.api.StockUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author tuomasi
 * Created on 2018/9/21.
 */
public class TxStockZtSaveMain {

    private static HashSet<String> lastDayCodes = new HashSet<>(StockUtils.getLastDayZtCode());

    public static void main(String[] args) throws Exception {
        //判断下时间,防止误操作
        LocalTime now = LocalTime.now();
        if (now.getHour() < 15){
            return;
        }
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            doSaveLimitUpCodes();
            long end = System.currentTimeMillis();
            System.out.println("查询所有涨停板耗时:" + (end - start) + "ms");
            Thread.sleep(1000);
        }
    }

    public static void doSaveLimitUpCodes() throws Exception{
        List<StockInfo> all = new TxStockSelectService().getAllLimitUpStocks();
        List<String> codes = new ArrayList<>(all.size());
        for (StockInfo info : all) {
            codes.add(info.getArea() + info.getCode());
        }
        System.out.println("保存涨停板代码,个数:" + codes.size());
        System.out.println("涨停板代码:" + String.join(",", codes));
        System.out.println(JSON.toJSON(all.get(0)));
        StockUtils.saveZtCode(codes);
    }

    public static List<StockInfo> rejectLastDayLimitUpStock(List<StockInfo> limitUpStocks){
        ArrayList<StockInfo> res = new ArrayList<>(limitUpStocks.size());
        for (StockInfo code : limitUpStocks) {
            if (lastDayCodes.contains(code.getArea() + code.getCode())){
                continue;
            }else {
                res.add(code);
            }
        }
        return res;
    }


}
