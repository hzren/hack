package com.hzren.hack.stock;

import com.hzren.hack.stock.guoyuan.GuoYuanService;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author tuomasi
 * Created on 2018/9/23.
 */
public class StockRobot {

    public static final LocalTime _9_15 = LocalTime.of(9, 15, 0);
    public static final LocalTime _9_25 = LocalTime.of(9, 25, 0);
    public static final LocalTime _9_30 = LocalTime.of(9, 30, 0);
    public static final LocalTime _11_30 = LocalTime.of(11, 30, 0);
    public static final LocalTime _13_00 = LocalTime.of(13, 0, 0);
    public static final LocalTime _15_00 = LocalTime.of(15, 0, 0);

    public static void main(String[] args) throws Exception {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        GuoYuanService guoYuanService = new GuoYuanService();
        TxStockSelectService selectMain = new TxStockSelectService();

        guoYuanService.login();
        //10s刷新一次页面
        executorService.scheduleAtFixedRate(new RefreshPageTask(guoYuanService), 10, 10, TimeUnit.SECONDS);
        //开始计算,查找合适的涨停板股票,25之前和之后两种逻辑
        boolean hasBuy = false;
        while (!hasBuy){
            LocalTime now = LocalTime.now();
            //9.15之前还没开盘,休息下
            int i = _9_15.compareTo(now);
            if (now.compareTo(_9_15) <= 0){
                Thread.sleep(10000);
                continue;
            }
            //9.15-9.25
            if (now.compareTo(_9_25) < 0){
                List<StockInfo> infos = selectMain.getAllLimitUpStocks();
                System.out.println(now + "--查询涨停板");
                for (StockInfo info : infos) {
                    System.out.println(info.getArea() + info.getCode() + " -- " + info.getName() + " -- " + info.getPrice() + " --" + info.getWaitAmount());
                }
                Thread.sleep(5000);
                continue;
            }
            //9.25 -- 11.30
            if (now.compareTo(_11_30) < 0){
                StockInfo info = selectMain.filterStocks();
                if (info != null){
                    executorService.submit(new BuyStockTask(guoYuanService, info));
                    hasBuy = true;
                }
                continue;
            }
            //11.30 - 13.00
            if (now.compareTo(_13_00) < 0){
                Thread.sleep(10000);
                continue;
            }
            //13.00 -- 15.00
            if (now.compareTo(_15_00) < 0){
                StockInfo info = selectMain.filterStocks();
                if (info != null){
                    executorService.submit(new BuyStockTask(guoYuanService, info));
                    hasBuy = true;
                }
                continue;
            }else {
                TxStockZtSaveMain.doSaveLimitUpCodes();
                return;
            }

        }
    }

    public static class BuyStockTask implements Runnable{

        private GuoYuanService guoYuanService;
        private StockInfo stockInfo;

        public BuyStockTask(GuoYuanService guoYuanService, StockInfo stockInfo){
            this.guoYuanService = guoYuanService;
            this.stockInfo = stockInfo;
        }

        @Override
        public void run() {
            guoYuanService.buy(stockInfo);
        }
    }

    public static class RefreshPageTask implements Runnable {

        private GuoYuanService guoYuanService;

        public RefreshPageTask(GuoYuanService guoYuanService){
            this.guoYuanService = guoYuanService;
        }

        @Override
        public void run() {
            guoYuanService.refreshPage();
        }
    }


}
