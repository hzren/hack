package com.hzren.hack.stock.guoyuan;

import com.hzren.hack.stock.api.StockInfo;

/**
 * @author tuomasi
 * Created on 2018/9/28.
 */
class CancelStockTask implements Runnable{
    private GuoYuanService guoYuanService;
    private StockInfo stockInfo;

    public CancelStockTask(GuoYuanService guoYuanService, StockInfo stockInfo){
        this.guoYuanService = guoYuanService;
        this.stockInfo = stockInfo;
    }

    @Override
    public void run() {
        guoYuanService.buy(stockInfo);
    }
}