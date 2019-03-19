package com.hzren.hack.lian_jia;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author tuomasi
 * Created on 2019/3/19.
 */
public class LianjiaMain {

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        GetLianjiaDataTask task = new GetLianjiaDataTask();
        executor.scheduleAtFixedRate(task, 10, 24L * 3600, TimeUnit.SECONDS);
        for (;;){
            try {
                Thread.sleep(3600L * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
