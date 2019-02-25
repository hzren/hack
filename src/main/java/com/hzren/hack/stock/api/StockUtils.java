package com.hzren.hack.stock.api;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author tuomasi
 * Created on 2018/9/20.
 */
public class StockUtils {

    public static final LocalTime _9_15 = LocalTime.of(9, 15, 0);
    public static final LocalTime _9_30 = LocalTime.of(9, 29, 59);
    public static final LocalTime _11_30 = LocalTime.of(11, 30, 0);
    public static final LocalTime _13_00 = LocalTime.of(12, 59, 59);
    public static final LocalTime _15_00 = LocalTime.of(15, 0, 0);


    public static final BigDecimal LIMIT = BigDecimal.valueOf(0.1);
    public static final BigDecimal HAND = BigDecimal.valueOf(100);

    public static BigDecimal zhangTingPerice(BigDecimal perice){
        BigDecimal up = perice.multiply(LIMIT);
        return perice.add(up).setScale(2, RoundingMode.HALF_UP);
    }

    public static void main(String[] args) {
        System.out.println(zhangTingPerice(BigDecimal.valueOf(3.65)));
        System.out.println(zhangTingPerice(BigDecimal.valueOf(13.84)));
        System.out.println(calCanByNum(BigDecimal.valueOf(10000), BigDecimal.valueOf(1.1)));
    }


    public static List<String> getLastDayZtCode(){
        File folder = new File("E:\\工作\\stock\\zt\\");
        File[] files = folder.listFiles();
        File res = null;
        for (File file : files) {
            if (res == null){
                res = file;
                continue;
            }
            if (file.getName().compareTo(res.getName()) > 0){
                res = file;
            }
        }
        try {
            return FileUtils.readLines(res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveZtCode(List<String> lines){
        String tday = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String fpath = "E:\\工作\\stock\\zt\\" + tday + ".txt";
        try {
            FileUtils.writeLines(new File(fpath), lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String calCanByNum(BigDecimal money, BigDecimal perice){
        BigDecimal hands = money.divide(HAND, 2, RoundingMode.DOWN).divide(perice, 2, RoundingMode.DOWN);
        return String.valueOf(hands.intValue() * 100);
    }

    public static File getExchangeFile(){
        LocalDate tday = LocalDate.now();
        String fname = tday.format(DateTimeFormatter.BASIC_ISO_DATE) + ".txt";
        String fpath = "E:\\工作\\stock\\exchange\\" + fname;
        File file = new File(fpath);
        if (!file.exists()){
            try {
                FileUtils.write(file, "");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }

    public static File getExchangeDir(){
        return new File("E:\\工作\\stock\\exchange\\");
    }




}
