package com.hzren.packet.route.front;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
@Slf4j
public class StartFrontServerMain {

    public static void main(String[] args) {
        FrontServer frontServer = new FrontServer();
        try {
            frontServer.startClientServer();
            log.info("成功启动Front server ...");
            Thread.sleep(365L * 24 * 3600 * 1000);
        } catch (Exception e) {
            System.exit(-1);
        }finally {
            frontServer.close();
        }
    }
}
