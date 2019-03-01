package com.hzren.packet.route.middle;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
@Slf4j
public class StartMiddleServerMain {

    public static void main(String[] args) {
        MiddleServer middleServer = new MiddleServer();
        try {
            middleServer.startMiddleServer();
            log.info("成功启动Front server ...");
            Thread.sleep(365L * 24 * 3600 * 1000);
        } catch (Exception e) {
            System.exit(-1);
        }finally {
            middleServer.close();
        }
    }
}
