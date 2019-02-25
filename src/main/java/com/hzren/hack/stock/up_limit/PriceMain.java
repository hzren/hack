package com.hzren.hack.stock.up_limit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hzren.hack.stock.api.StockInfo;
import com.hzren.hack.stock.api.StockUtils;
import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author tuomasi
 * Created on 2018/9/20.
 */
public class PriceMain {

    private static SimpleHttpExecutor executor = new SimpleHttpExecutor(null, null);

    public static void main(String[] args) {
        List<StockInfo> infos = rankDFCF(300);
        ArrayList<String> lines = new ArrayList<>();
        for (StockInfo info : infos) {
            lines.add(info.getCode());
        }
        StockUtils.saveZtCode(lines);

        List<StockInfo> stocks = rankDFCF(20);
        List<StockInfo> topVolume = getTopVolume(stocks);
        StockInfo lowest = getLowestExchange(topVolume);
        System.out.println(lowest.getName() + " : " + lowest.getCode());
    }

    private static void rankSZ(){
        Request request = Request.Get("http://stock.gtimg.cn/data/view/rank.php?t=rankasz/chr&p=1&o=0&l=80&v=list_data");
        request.addHeader("Referer", "http://stockapp.finance.qq.com/mstats/");
        String resp = executor.requestAsSting(request);
        System.out.println(resp);
    }

    private static void rankSH(){
        Request request = Request.Get("http://stock.gtimg.cn/data/view/rank.php?t=rankash/chr&p=1&o=0&l=80&v=list_data");
        request.addHeader("Referer", "http://stockapp.finance.qq.com/mstats/");
        String resp = executor.requestAsSting(request);
        System.out.println(resp);
    }

    private static List<StockInfo> rankDFCF(int size){
        Request request = Request.Get("http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?" +
                "cb=jQuery112407252246451897015_1537421014827" +
                "&type=CT&token=4f1862fc3b5e77c150a2b985b12db0fd" +
                "&sty=FCOIATC&js=(%7Bdata%3A%5B(x)%5D%2CrecordsFiltered%3A(tot)%7D)" +
                "&cmd=C._A&st=(ChangePercent)&sr=-1&p=1&ps=" + size + "&_=1537421014828");
        request.addHeader("Referer", "http://stockapp.finance.qq.com/mstats/");
        request.addHeader("Upgrade-Insecure-Requests", "1");
        request.addHeader("Pragma", "no-cache");
        String resp = executor.requestAsSting(request);
        System.out.println(resp);
        int start = resp.indexOf("[");
        int end = resp.indexOf("]") + 1;

        JSONArray array = JSON.parseArray(resp.substring(start, end));
        ArrayList<StockInfo> selectedStocks = new ArrayList<>();

        for (Object o : array) {
            String info = (String) o;
            System.out.println(info);
            //2,300071,华谊嘉信,4.02,0.37,10.14,198700,76757907,10.68,4.02,3.63,3.66,3.65,0.00,1.08,4.76,35.04,2.95,2727535769,1676357809,1.77%,-42.74%,0.00,2010-04-21,2018-09-20 13:45:33,198700
            String[] infos = info.split(",");
            StockInfo st = new StockInfo(infos[1], infos[2]);
            BigDecimal startPerice = new BigDecimal(infos[12]);
            BigDecimal nowPerice = new BigDecimal(infos[3]);
            BigDecimal ztPerice = StockUtils.zhangTingPerice(startPerice);
            if (ztPerice.compareTo(nowPerice) != 0){
                continue;
            }
            st.setPrice(nowPerice);
//            st.setVolume(new BigDecimal(infos[14]));
            st.setExchange(new BigDecimal(infos[15]));
//            if (st.getVolume().compareTo(BigDecimal.TEN) >= 0){
//                continue;
//            }
            selectedStocks.add(st);
        }
        return selectedStocks;
    }

    public static List<StockInfo> getTopVolume(List<StockInfo> src){
        int size = src.size() / 4 + 1;
        StockInfo[] array = src.toArray(new StockInfo[src.size()]);
        Arrays.parallelSort(array, new Comparator<StockInfo>() {
            @Override
            public int compare(StockInfo o1, StockInfo o2) {
//                return o2.getVolume().compareTo(o1.getVolume());
                return 0;
            }
        });
        return Arrays.asList(array).subList(0, size);
    }

    public static StockInfo getLowestExchange(List<StockInfo> src){
        StockInfo lowest = null;
        for (StockInfo stockInfo : src) {
            if (lowest == null){
                lowest = stockInfo;
                continue;
            }
            if (stockInfo.getExchange().compareTo(lowest.getExchange()) < 0){
                lowest = stockInfo;
            }
        }
        return lowest;
    }

}
