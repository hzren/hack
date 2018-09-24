package com.hzren.hack.stock;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author tuomasi
 * Created on 2018/9/20.
 */

@Getter
public class StockInfo {
    public StockInfo(String code, String name){
        if (code.startsWith("6")){
            this.area = StockEnums.AREA_SH;
        }else{
            this.area = StockEnums.AREA_SZ;
        }
        this.code = code;
        this.name = name;
    }

    //sh上海, sz深圳
    private String area;

    private String code;

    private String name;

    @Setter
    private BigDecimal price;

    //昨日收盘价格
    @Setter
    private BigDecimal yPerice;

    @Setter
    //换手率
    private BigDecimal exchange;

    @Setter
    //买一挂单数量
    private Integer waitAmount;

    @Setter
    //成交量
    private Integer dealAmount;

    @Setter
    //总市值
    private BigDecimal stockValue;

    @Setter
    //流通市值
    private BigDecimal unlimitStockValue;

    //外盘
    @Setter
    private int outer;
    //内盘
    @Setter
    private int inner;



}
