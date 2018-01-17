package com.hzren.hack.fang;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkFlow {
    public static final int START = 22;
    public static final int END = 23;

    public static final Set<String> SENDED = new HashSet<>();

    public static void main(String[] args) throws Exception {

        while (true){
            LocalDateTime now = LocalDateTime.now();
            String tday = now.format(DateTimeFormatter.BASIC_ISO_DATE);
            if (SENDED.contains(tday)){
                Thread.sleep(30L * 60 * 1000);
                continue;
            }
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
            SendKsls.doPostKsls(tday);
            SENDED.add(tday);
        }

    }
}
