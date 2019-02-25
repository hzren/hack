package com.hzren.hack.stock.guoyuan;

/**
 * @author tuomasi
 * Created on 2018/9/28.
 */
class RefreshPageTask implements Runnable {

    private GuoYuanService guoYuanService;

    public RefreshPageTask(GuoYuanService guoYuanService){
        this.guoYuanService = guoYuanService;
    }

    @Override
    public void run() {
        guoYuanService.refreshPage();
    }
}