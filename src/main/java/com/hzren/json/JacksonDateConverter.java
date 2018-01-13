package com.hzren.json;


import com.fasterxml.jackson.databind.util.StdConverter;
import com.hzren.util.DateUtil;

import java.util.Date;

/**
 * @author chan
 * @version $Id: JasonDateSerialize.java, v 0.1 2015年3月13日 下午3:05:09 chan Exp $
 */
public class JacksonDateConverter {

    /**
     * <p>转换为 yyyy-MM-dd</p>
     *
     * <pre>
       * <code>@JsonSerialize(converter = BacklashDateConverter.class)</code>
     * </pre>
     *
     * @author chan.
     * @version V1.0
     */
    public static class BacklashDateConverter extends StdConverter<Date, String> {

        private final String FORMAT = "yyyy-MM-dd";

        @Override
        public String convert(Date value) {
            return DateUtil.formatDate(value, FORMAT);
        }
    }

    /**
     * 转换为 yyyy-MM-dd HH:mm:ss
     *
     * <pre>
       * <code>@JsonSerialize(converter = BacklashDateTimeConverter.class)</code>
     * </pre>
     *
     * @author chan.
     * @version V1.0
     */
    public static class BacklashDateTimeConverter extends StdConverter<Date, String> {

        private final String FORMAT = "yyyy-MM-dd HH:mm:ss";

        @Override
        public String convert(Date value) {
            return DateUtil.formatDate(value, FORMAT);
        }

    }

    public static class BacklashDateTimeDeConverter extends StdConverter<String, Date> {

        private final String FORMAT = "yyyy-MM-dd HH:mm:ss";

        @Override
        public Date convert(String value) {
            return DateUtil.parseDate(value, FORMAT);
        }
    }
}
