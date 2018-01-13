package com.hzren.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期工具类
 *
 * Created by Yang Tengfei on 10/14/14.
 */
public final class DateUtil {

    public static final long ONE_DAY = 24 * 60 * 60 * 1000;

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * yyyyMMddHHmmss
     */
    public static final String LDATETIME_FORMAT = "yyyyMMddHHmmss";
    /**
     * yyyyMMddHHmmss
     */
    public static final String LLDATETIME_FORMAT = "yyyyMMddHHmmssSSS";
    /**
     * yyyy-MM-dd
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * yyyyMMdd
     */
    public static final String LDATE_FORMAT = "yyyyMMdd";
    /**
     * HHmmss
     */
    public static final String LSHORTTIME_FORMAT = "HHmmss";
    /**
     * HH:mm:ss
     */
    public static final String SHORTTIME_FORMAT = "HH:mm:ss";
    public static final String YEAR_MONTH_FORMAT = "yyyyMM";
    private static final ThreadLocal<SimpleDateFormat> dateTimeThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATETIME_FORMAT);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> lDateTimeThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(LDATETIME_FORMAT);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> llDateTimeThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(LLDATETIME_FORMAT);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dateThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> lDateThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(LDATE_FORMAT);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> lShortTimeThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(LSHORTTIME_FORMAT);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> shortTimeThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(SHORTTIME_FORMAT);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> yearMonthFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(YEAR_MONTH_FORMAT);
        }
    };

    public static SimpleDateFormat getYearMonthDate() {
        return yearMonthFormatThreadLocal.get();
    }

    /**
     * @param date
     * @return yyyyMMdd
     */
    public static int getLdate(Date date) {
        return Integer.parseInt(formatLShortDate(date));
    }

    /**
     * @param date
     * @return HHmmss
     */
    public static int getLShortTime(Date date) {
        return Integer.parseInt(formatLShortTime(date));
    }

    /**
     * @param date
     * @return yyyyMMddHHmmss
     */
    public static long getLDateTime(Date date) {
        return Long.parseLong(formatLDateTime(date));
    }

    public static boolean isBefore(Date date1, Date date2) {
        return date2.getTime() - date1.getTime() > 0;
    }

    private static Date add(Date date, int zoom, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (amount == 0) {
            return date;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(zoom, amount);
        return cal.getTime();
    }

    public static Date addDays(Date date, int amount) {
        return add(date, Calendar.DATE, amount);
    }

    public static Date addHours(Date date, int amount) {
        return add(date, Calendar.HOUR, amount);
    }

    public static Date addMinutes(Date date, int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    public static Date addMonth(Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    public static Date addYear(Date date, int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    private static Date paser(DateFormat format, String dateString) {
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期格式错误:" + dateString);
        }
    }

    public static String format(DateFormat format, Date date) {
        return format.format(date);
    }

    public static String formatGMT(Date date) {
        DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static Date parseDateTime(String date) {
        return paser(dateTimeThreadLocal.get(), date);
    }

    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static Date parseDate(String date) {
        return paser(dateThreadLocal.get(), date);
    }

    /**
     * yyyyMMdd
     *
     * @param date
     * @return
     */
    public static Date parseLShortDate(String date) {
        return paser(lDateThreadLocal.get(), date);
    }

    public static Date parseDate(String date, String format) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return paser(new SimpleDateFormat(format), date);
    }

    /**
     * @param date
     * @return HH:mm:ss
     */
    public static String formatShortTime(Date date) {
        return format(shortTimeThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(Date date) {
        return format(dateTimeThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return yyyy-MM-dd
     */
    public static String formatDate(Date date) {
        return format(dateThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return converter
     */
    public static String formatDate(Date date, String format) {
        return format(new SimpleDateFormat(format), date);
    }

    /**
     * @param date
     * @return yyyyMMdd
     */
    public static String formatLDate(Date date) {
        return format(lDateThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return yyyyMMddHHmmss
     */
    public static String formatLDateTime(Date date) {
        return format(lDateTimeThreadLocal.get(), date);
    }

    public static String formatLLDateTime(Date date) {
        return format(llDateTimeThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return HHmmss
     */
    public static String formatLShortTime(Date date) {
        return format(lShortTimeThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return yyyyMMdd
     */
    public static String formatLShortDate(Date date) {
        return format(lDateThreadLocal.get(), date);
    }

    //将String格式转换为Date类型
    public static Date formatDate(String date,int type){

        if (date.length() == 10 && type==1) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date lastPayDate = dateFormat.parse(date);
                return lastPayDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (date.length() == 7 && type==2) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                Date lastPayDate = dateFormat.parse(date);
                return lastPayDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (date.length() == 6 && type==3) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
                Date lastPayDate = dateFormat.parse(date);
                return lastPayDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    //将String格式转换为Date类型
    public static Date formatDate(String date){
        date= date.replaceAll("\\u00A0|\\b| ", "");
        if (date.length() == 11 ) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                Date lastPayDate = dateFormat.parse(date);
                return lastPayDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (date.length() == 10 ) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date lastPayDate = dateFormat.parse(date);
                return lastPayDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (date.length() == 7 ) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                Date lastPayDate = dateFormat.parse(date);
                return lastPayDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (date.length() == 6) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
                Date lastPayDate = dateFormat.parse(date);
                return lastPayDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (date.length() == 8) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                Date lastPayDate = dateFormat.parse(date);
                return lastPayDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getCurrentYearMonth() {
        Date date =new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
        return sdf.format(date);
    }
    public static String getCurrentDate() {
        Date date =new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }
    public static String getCurrentYear() {
        Date date =new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(date);
    }
    
	public static Integer nowYearMounth() {
		YearMonth now = YearMonth.now();
		return Integer.parseInt(now.format(DateTimeFormatter.ofPattern("yyyyMM")));
	}

	public static Integer lastMounths(YearMonth start, int mounth) {
		YearMonth yearMonth = start.minusMonths(mounth);
		return Integer.parseInt(yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")));
	}

}
