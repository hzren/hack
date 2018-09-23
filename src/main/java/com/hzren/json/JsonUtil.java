package com.hzren.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author chan
 * @version $Id: JsonUtil.java, v 0.1 2015年10月30日 下午3:15:00 chan Exp $
 */
public class JsonUtil {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        MAPPER.setDateFormat(format);
    }

    public static String toJson(Object obj) {
        try {
            return null == obj ? null : MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonSerializeException(e);
        }
    }

    public static <T> T fromJson(String string, Class<T> clazz) {
        try {
            return StringUtils.isEmpty(string) ? null : MAPPER.readValue(string, clazz);
        } catch (IOException e) {
            throw new JsonSerializeException(e);
        }
    }

    public static <T> List<T> fromJsonArray(String string, Class<T> clazz) {
        try {
            return StringUtils.isEmpty(string) ? null : MAPPER.readValue(string, new TypeReference<List<T>>() {
            });
        } catch (IOException e) {
            throw new JsonSerializeException(e);
        }
    }

    public static <T> List<T> fromJsonArray(String string, TypeReference<List<T>> type) {
        try {
            return StringUtils.isEmpty(string) ? null : MAPPER.readValue(string, type);
        } catch (IOException e) {
            throw new JsonSerializeException(e);
        }
    }

    public static <T> T fromJson(byte[] bytes, Class<T> clazz) {
        try {
            return null == bytes || bytes.length == 0 ? null : MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new JsonSerializeException(e);
        }
    }

    //将json 对象转为Map
    public static Map json2Map(String jsondata){

        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = new HashMap<>();

        try {
            map=mapper.readValue(jsondata, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
