package com.hzren.util;

import java.time.LocalDateTime;

/**
 * @author hzren
 * Created on 2017/11/13.
 */
public class Logger {

    public static void log(Object object){
        System.out.println(LocalDateTime.now() + ": " + object);
    }
}
