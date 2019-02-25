package com.hzren.hack.stock.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author tuomasi
 * Created on 2018/9/25.
 */
public class StockFilter {
    public static final int THRESHOLD = 5;

    /**
     *买一单数/成交数最高的
     * */
    public static final StockInfo filterByRate(List<StockInfo> stockInfos){
        if (stockInfos.size() < THRESHOLD){
            return null;
        }
        StockInfo res = null;
        BigDecimal rate = BigDecimal.ZERO;
        for (StockInfo stockInfo : stockInfos) {
            //忽略新股第一天涨停
            BigDecimal ztPerice = StockUtils.zhangTingPerice(stockInfo.getYPerice());
            if (ztPerice.compareTo(stockInfo.getPrice()) < 0){
                continue;
            }
            if (BigDecimal.valueOf(stockInfo.getDealAmount()).compareTo(BigDecimal.ZERO) == 0){
                return null;
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

    public static final StockInfo filterByBuy1Num(List<StockInfo> stockInfos){
        if (stockInfos.size() < THRESHOLD){
            return null;
        }
        StockInfo res = null;
        for (StockInfo stockInfo : stockInfos) {
            //忽略新股第一天涨停
            BigDecimal ztPerice = StockUtils.zhangTingPerice(stockInfo.getYPerice());
            if (ztPerice.compareTo(stockInfo.getPrice()) < 0){
                continue;
            }
            if (res == null){
                res = stockInfo;
                continue;
            }
            if (stockInfo.getWaitAmount().compareTo(res.getWaitAmount()) > 0){
                res = stockInfo;
            }
        }
        return res;
    }

    public static final StockInfo filterByBuy1Money(List<StockInfo> stockInfos){
        if (stockInfos.size() < THRESHOLD){
            return null;
        }
        StockInfo res = null;
        BigDecimal money = null;
        for (StockInfo stockInfo : stockInfos) {
            //忽略新股第一天涨停
            BigDecimal ztPerice = StockUtils.zhangTingPerice(stockInfo.getYPerice());
            if (ztPerice.compareTo(stockInfo.getPrice()) < 0){
                continue;
            }
            BigDecimal thisMoney = stockInfo.getPrice().multiply(BigDecimal.valueOf(stockInfo.getWaitAmount()));
            if (res == null){
                res = stockInfo;
                money = thisMoney;
                continue;
            }
            if (thisMoney.compareTo(money) > 0){
                res = stockInfo;
                money = thisMoney;
            }
        }
        return res;
    }

    public static final StockInfo filterByAllBuy1Money(List<StockInfo> stockInfos){
        if (stockInfos.size() < THRESHOLD){
            return null;
        }
        StockInfo res = null;
        BigDecimal money = null;
        for (StockInfo stockInfo : stockInfos) {
            //忽略新股第一天涨停
            BigDecimal ztPerice = StockUtils.zhangTingPerice(stockInfo.getYPerice());
            if (ztPerice.compareTo(stockInfo.getPrice()) < 0){
                continue;
            }
            BigDecimal thisMoney = stockInfo.getPrice().multiply(BigDecimal.valueOf(stockInfo.getWaitAmount()));
            if (res == null){
                res = stockInfo;
                money = thisMoney;
                continue;
            }
            if (thisMoney.compareTo(money) > 0){
                res = stockInfo;
                money = thisMoney;
            }
        }
        return res;
    }
}
