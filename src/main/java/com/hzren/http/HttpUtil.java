package com.hzren.http;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author hzren
 * Created on 2017/11/13.
 */
public class HttpUtil {

    public static final String HEADER_CHROME = "";
    public static final String HEADER_IE = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
    public static final String HEADER_FIREFOX = "";

    /**
     *
     *把cookie从HttpClient同步到WebDriver
     *
     * */
    public static void syncCookieFromHttpClient(BasicCookieStore cookieStore, RemoteWebDriver driver){
        List<Cookie> cookies = cookieStore.getCookies();
        Iterator<Cookie> iterator = cookies.iterator();
        while (iterator.hasNext()) {
            Cookie cookie =  iterator.next();
            org.openqa.selenium.Cookie sCookie = new org.openqa.selenium.Cookie.Builder(cookie.getName(), cookie.getValue())
                    .domain(cookie.getDomain())
                    .expiresOn(cookie.getExpiryDate())
                    .isHttpOnly(false)
                    .isSecure(cookie.isSecure())
                    .path(cookie.getPath())
                    .build();

            driver.manage().addCookie(sCookie);
        }
    }

    /**
     *
     * 把cookie从Web Driver同步到Http Client Cookie Store
     *
     * */
    public static void syncCookieFromWebDriver(RemoteWebDriver driver, BasicCookieStore cookieStore){
        Set<org.openqa.selenium.Cookie> sCookies = driver.manage().getCookies();
        Iterator<org.openqa.selenium.Cookie> iterator = sCookies.iterator();
        while (iterator.hasNext()) {
            org.openqa.selenium.Cookie next =  iterator.next();
            System.out.println(next.getName() + " : " + next.getValue());
            BasicClientCookie cookie = new BasicClientCookie(next.getName(), next.getValue());
            cookie.setDomain(next.getDomain());
            cookie.setExpiryDate(next.getExpiry());
            cookie.setPath(next.getPath());
            cookie.setSecure(next.isSecure());

            cookieStore.addCookie(cookie);
        }
    }

}
