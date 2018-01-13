package com.hzren.http;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Content;
import org.apache.http.cookie.Cookie;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface HttpExecutor {
    void setCookieStore(CookieStore var1);

    SimpleHttpExecutor addCookies(Cookie... var1);

    <T> T requestAsObject(Request var1, Class<T> var2);

    String requestAsSting(Request var1);

    byte[] requestAsByte(Request var1);

    Content requestAsContent(Request var1);

    Response request(Request var1);

    Optional<Cookie> getCookie(String var1);

    List<Cookie> getCookies();

    CookieStore getCookieStore();

    Optional<URI> getRedirectUrl();

    Optional<Header> getHeader(String var1);

    void setDefaultProxy(HttpHost var1);

    void setDefaultUserAgent(String var1);
}
