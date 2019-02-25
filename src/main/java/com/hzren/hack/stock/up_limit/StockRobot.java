package com.hzren.hack.stock.up_limit;

import com.alibaba.fastjson.JSON;
import com.hzren.hack.stock.api.StockFilter;
import com.hzren.hack.stock.api.StockInfo;
import com.hzren.hack.stock.api.StockUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static com.hzren.hack.stock.api.StockUtils.*;

/**
 * @author tuomasi
 * Created on 2018/9/23.
 */
@Slf4j
public class StockRobot {

    public static void main(String[] args) throws Exception {
        TxStockSelectService selectMain = new TxStockSelectService();
        //10s刷新一次页面

        //开始计算,查找合适的涨停板股票,25之前和之后两种逻辑
        StockInfo nowBest = null;
        LocalTime now = null;
        while (now == null || now.compareTo(_15_00) <= 0){
            Thread.sleep(1000);
            now = LocalTime.now();
            //9.15之前还没开盘,休息下
            int i = _9_15.compareTo(now);
            if (now.compareTo(_9_15) <= 0){
                Thread.sleep(10000);
                continue;
            }
            //9.15-9.30
            if (now.compareTo(_9_30) < 0){
                List<StockInfo> infos = selectMain.getAllLimitUpStocks();
                log.info(now + "--查询涨停板--");
                for (StockInfo info : infos) {
                    log.info(info.getArea() + info.getCode() + " -- " + info.getName()
                            + " -- " + info.getPrice() + " --" + info.getWaitAmount() + " -- " + info.getDealAmount());
                }
                StockInfo best = StockFilter.filterByAllBuy1Money(infos);
                if (best != null){
                    log.info("筛选出最好-- " + best.getArea() + best.getCode() + " -- " + best.getName()
                            + " -- " + best.getPrice() + " --" + best.getWaitAmount() + " -- " + best.getDealAmount());
                }

                Thread.sleep(5000);
                continue;
            }
            //9.30 -- 11.30, 11.30-15.00
            if (now.compareTo(_11_30) < 0 || (now.compareTo(_13_00) >= 0 && now.compareTo(_15_00) < 0)){
                List<StockInfo> infos = selectMain.getAllLimitUpStocks();
                log.info(now + "--查询涨停板");
                for (StockInfo info : infos) {
                    log.info(info.getArea() + info.getCode() + " -- " + info.getName()
                            + " -- " + info.getPrice() + " --" + info.getWaitAmount() + " -- " + info.getDealAmount());
                }
                StockInfo best = selectMain.select(infos);
                if (best != null){
                    log.info("最好 -- " + best.getArea() + best.getCode() + " -- " + best.getName()
                            + " -- " + best.getPrice() + " --" + best.getWaitAmount() + " -- " + best.getDealAmount());
                }

                if (nowBest == null || !Objects.equals(nowBest.getCode(), best.getCode())){
                    FileUtils.write(StockUtils.getExchangeFile(), JSON.toJSONString(best));
                    nowBest = best;
                }
                continue;
            }
            //11.30 - 13.00
            if (now.compareTo(_11_30) > 0 && now.compareTo(_13_00) < 0){
                Thread.sleep(10000);
                continue;
            }
            //15.00以后保持涨停板退出
            TxStockZtSaveMain.doSaveLimitUpCodes();
            return;

        }
    }




}
