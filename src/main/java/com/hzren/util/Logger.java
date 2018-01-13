package com.hzren.util;

import javafx.util.converter.LocalDateTimeStringConverter;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author hzren
 * Created on 2017/11/13.
 */
public class Logger {

    public static void log(Object object){
        System.out.println(LocalDateTime.now() + ": " + object);
    }
}
