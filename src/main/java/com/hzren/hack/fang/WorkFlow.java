package com.hzren.hack.fang;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WorkFlow {
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int sendHour = 22;

        executor.scheduleAtFixedRate(saveImgTask, 0, 1, TimeUnit.HOURS);
        executor.scheduleAtFixedRate(sendToKsls, sendHour - hour, 24, TimeUnit.HOURS);
    }

    public static final Runnable saveImgTask = new Runnable() {
        @Override
        public void run() {
            try {
                Dailydeal.saveDailyDeal();
            }catch (Exception e){
                LocalDateTime now = LocalDateTime.now();
                System.out.println("---------" + now.toString() + "保存图片出错------------");
                e.printStackTrace();
            }
        }
    };

    public static final Runnable sendToKsls = new Runnable() {

        public final Set<String> SENDED = new HashSet<>();

        @Override
        public void run() {
            try {
                LocalDateTime now = LocalDateTime.now();
                String tday = now.format(DateTimeFormatter.BASIC_ISO_DATE);
                if (SENDED.contains(tday)){
                    return;
                }
                SendKsls.doPostKsls(tday);
                SENDED.add(tday);
            }catch (Exception e){
                LocalDateTime now = LocalDateTime.now();
                System.out.println("---------" + now.toString() + "发帖到口水出错------------");
                e.printStackTrace();
            }
        }
    };
}
