package com.hzren.hack.fang;

import java.time.LocalTime;

public class WorkFlow {
    public static final int START = 22;
    public static final int END = 23;

    public static void main(String[] args) throws Exception {

        while (true){
            LocalTime now = LocalTime.now();
            int hour = now.getHour();
            int minute = now.getMinute();
            if (!(hour >= START && hour < END)){
                Thread.sleep(30L * 60 * 1000);
                continue;
            }
            if (!(minute >= 0 && minute < 30)){
                Thread.sleep(30L * 60 * 1000);
                continue;
            }

            Dailydeal.saveDailyDeal();
            Thread.sleep(1000L);
            SendKsls.doPostKsls();
        }

    }
}
