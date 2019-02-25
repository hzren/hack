package com.hzren.hack.stock.up_limit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzren.hack.stock.api.StockEnums;
import com.hzren.hack.stock.api.StockInfo;
import com.hzren.hack.stock.api.StockUtils;
import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author tuomasi
 * Created on 2018/9/21.
 */
public class TxStockSelectService {

    private SimpleHttpExecutor executor = new SimpleHttpExecutor(null, null);

    public static void main(String[] args) throws Exception {
        TxStockSelectService selectMain = new TxStockSelectService();
        for (int i = 1; i < 10; i++){
            selectMain.filterStocks();
        }
    }

    public StockInfo filterStocks() throws Exception{
        long start = System.currentTimeMillis();
        List<StockInfo> allLimitUp = getAllLimitUpStocks();
        List<StockInfo> filterInfos = TxStockZtSaveMain.rejectLastDayLimitUpStock(allLimitUp);
        StockInfo target = select(filterInfos);
        if (target != null){
            System.out.println(target.getName() + " : " + target.getCode() + " : " + target.getPrice());
        }else {
            System.out.println("-------未发现符合条件的股票代码-------");
        }
        long end = System.currentTimeMillis();
        System.out.println("筛选股票耗时:" + (end - start) + "ms");
        return target;
    }

    public StockInfo select(List<StockInfo> stockInfos){
        StockInfo res = null;
        BigDecimal rate = BigDecimal.ZERO;
        for (StockInfo stockInfo : stockInfos) {
            //忽略新股第一天涨停
            BigDecimal ztPerice = StockUtils.zhangTingPerice(stockInfo.getYPerice());
            if (ztPerice.compareTo(stockInfo.getPrice()) < 0){
                continue;
            }
            if (stockInfo.getDealAmount() == 0){
                res = stockInfo;
                rate = BigDecimal.valueOf(Integer.MAX_VALUE);
            }
            if (BigDecimal.valueOf(stockInfo.getDealAmount()).compareTo(BigDecimal.ZERO) == 0){
                continue;
            }
            BigDecimal siRate = BigDecimal.valueOf(stockInfo.getWaitAmount())
                    .divide(BigDecimal.valueOf(stockInfo.getDealAmount()), 2, RoundingMode.HALF_DOWN);
            if (siRate.compareTo(rate) > 0){
                res = stockInfo;
                rate = siRate;
            }
        }
        return res;
    }



    public List<StockInfo> getAllLimitUpStocks() throws Exception{
        List<StockInfo> sh = getAreaTopLimitStocks(StockEnums.AREA_SH);
        List<StockInfo> sz = getAreaTopLimitStocks(StockEnums.AREA_SZ);
        ArrayList<StockInfo> all = new ArrayList<>();
        all.addAll(sh);
        all.addAll(sz);
        return all;
    }

    public List<StockInfo> getAreaTopLimitStocks(String area) throws Exception{
        List<StockInfo> result = new ArrayList<>();
        int page = 1, pageSize = 20, lastPageSize = pageSize;
        while (lastPageSize == pageSize){
            List<String> codes = getRankStocks(area, page, pageSize);
            List<StockInfo> limitUp = doQueryLimitUpStocks(codes);
            for (StockInfo info : limitUp) {
                result.add(info);
            }
            page++;
            lastPageSize = limitUp.size();
        }
        return result;
    }

    public List<String> getRankStocks(String area, int page,int size) throws Exception{
        Request request = Request.Get("http://stock.gtimg.cn/data/view/rank.php?" +
                "t=ranka" + area + "/chr" +
                "&p=" + page +
                "&o=0" +
                "&l=" + size +
                "&v=list_data");
        request.addHeader("Referer", "http://stockapp.finance.qq.com/mstats/");
        request.addHeader("Upgrade-Insecure-Requests", "1");
        request.addHeader("Pragma", "no-cache");
        String resp = executor.requestAsSting(request);
        String prefix = "var list_data=";
        resp = resp.substring(prefix.length(), resp.length() - 1);
        JSONObject jsonObject = JSON.parseObject(resp);
        String codes = jsonObject.getString("data");
        String[] codeArray = codes.split(",");
        return Arrays.asList(codeArray);
    }

    public List<StockInfo> queryLimitUpStocks(List<String> codes) throws Exception{
        List<StockInfo> result = new ArrayList<>();
        int start = 0, end = 10;
        for (;;){
            List<StockInfo> limitUp = doQueryLimitUpStocks(codes.subList(start, end));
            result.addAll(limitUp);
            start = end;
            end = end + 10;
            if (start >= codes.size()){
                break;
            }
            if (end >= codes.size()){
                end = codes.size();
            }
        }
        return result;
    }

    public List<StockInfo> doQueryLimitUpStocks(List<String> codes) throws Exception{
        String url = "http://qt.gtimg.cn/q=" + String.join(",", codes) + "&r=" + System.currentTimeMillis();
        Request request = Request.Get(url);
        request.addHeader("Referer", "http://stockapp.finance.qq.com/ms/pushiframe.html?_u=" + System.currentTimeMillis());
        String resp = executor.requestAsSting(request);
        //处理转化
        BufferedReader reader = new BufferedReader(new StringReader(resp));
        List<StockInfo> stocks = new ArrayList<>();
        String line = null;
        while ((line = reader.readLine()) != null){
            line = line.trim();
            line = line.substring(line.indexOf("\""), line.length() - 2);
            String[] infos = line.split("~");
            String stockName = infos[1];
            String code = infos[2];
            BigDecimal nowPerice = new BigDecimal(infos[3]);
            BigDecimal yPerice = new BigDecimal(infos[4]);
            int dealAmount = Integer.valueOf(infos[6]);
            //排除不是涨停的
            BigDecimal ztPerice = StockUtils.zhangTingPerice(yPerice);
            //每涨停忽略
            if (ztPerice.compareTo(nowPerice) > 0){
                continue;
            }
            StockInfo si = new StockInfo(code, stockName);
            si.setPrice(nowPerice);
            si.setYPerice(yPerice);
            si.setExchange(new BigDecimal(infos[38]));
            si.setWaitAmount(Integer.parseInt(infos[10]));
            si.setDealAmount(dealAmount);
            si.setStockValue(new BigDecimal(infos[45]));
            si.setUnlimitStockValue(new BigDecimal(infos[44]));
            si.setOuter(Integer.parseInt(infos[7]));
            si.setInner(Integer.parseInt(infos[8]));
            stocks.add(si);
        }
        return stocks;
    }
}
