//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hzren.http;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Request {
    private final HttpRequestBase request;
    private Integer socketTimeout;
    private Integer connectTimeout;
    private HttpHost proxy;

    public Request(HttpRequestBase request) {
        this.request = request;
    }

    public static Request Get(URI uri) {
        return new Request(new HttpGet(uri));
    }

    public static Request Get(String uri) {
        return new Request(new HttpGet(URI.create(uri)));
    }

    public static Request Post(URI uri) {
        return new Request(new HttpPost(uri));
    }

    public static Request Post(String uri) {
        return new Request(new HttpPost(URI.create(uri)));
    }

    HttpResponse internalExecute(HttpClient client, HttpContext localContext) throws IOException {
        Builder builder;
        if(client instanceof Configurable) {
            builder = RequestConfig.copy(((Configurable)client).getConfig());
        } else {
            builder = RequestConfig.custom();
        }

        if(this.socketTimeout != null) {
            builder.setSocketTimeout(this.socketTimeout.intValue());
        }

        if(this.connectTimeout != null) {
            builder.setConnectTimeout(this.connectTimeout.intValue());
        }

        if(this.proxy != null) {
            builder.setProxy(this.proxy);
        }

        RequestConfig config = builder.build();
        this.request.setConfig(config);
        return client.execute(this.request, localContext);
    }

    public void abort() throws UnsupportedOperationException {
        this.request.abort();
    }

    public Request ajax(){
        this.request.addHeader("X-Requested-With", "XMLHttpRequest");
        return this;
    }

    public Request addHeader(Header header) {
        this.request.addHeader(header);
        return this;
    }

    public Request setHeader(Header header) {
        this.request.setHeader(header);
        return this;
    }

    public Request addHeader(String name, String value) {
        this.request.addHeader(name, value);
        return this;
    }

    public Request setHeader(String name, String value) {
        this.request.setHeader(name, value);
        return this;
    }

    public Request removeHeader(Header header) {
        this.request.removeHeader(header);
        return this;
    }

    public Request removeHeaders(String name) {
        this.request.removeHeaders(name);
        return this;
    }

    public Request setHeaders(Header... headers) {
        this.request.setHeaders(headers);
        return this;
    }

    public Request userAgent(String agent) {
        this.request.setHeader("User-Agent", agent);
        return this;
    }

    public Request socketTimeout(int timeout) {
        this.socketTimeout = Integer.valueOf(timeout);
        return this;
    }

    public Request connectTimeout(int timeout) {
        this.connectTimeout = Integer.valueOf(timeout);
        return this;
    }

    public Request viaProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public Request viaProxy(String proxy) {
        this.proxy = HttpHost.create(proxy);
        return this;
    }

    public Request body(HttpEntity entity) {
        if(this.request instanceof HttpEntityEnclosingRequest) {
            ((HttpEntityEnclosingRequest)this.request).setEntity(entity);
            return this;
        } else {
            throw new IllegalStateException(this.request.getMethod() + " request cannot enclose an entity");
        }
    }

    public Request bodyForm(Map<String, String> formParams, Charset charset) {
        ArrayList paramList = new ArrayList();
        Iterator var4 = formParams.entrySet().iterator();

        while(var4.hasNext()) {
            Entry entry = (Entry)var4.next();
            paramList.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue()));
        }

        return this.bodyForm((Iterable)paramList, charset);
    }

    public Request bodyForm(Map<String, String> formParams) {
        return this.bodyForm(formParams, Consts.UTF_8);
    }

    public Request bodyForm(Iterable<? extends NameValuePair> formParams, Charset charset) {
        ArrayList paramList = new ArrayList();
        Iterator contentType = formParams.iterator();

        while(contentType.hasNext()) {
            NameValuePair s = (NameValuePair)contentType.next();
            paramList.add(s);
        }

        ContentType contentType1 = ContentType.create("application/x-www-form-urlencoded", charset);
        String s1 = URLEncodedUtils.format(paramList, charset != null?charset.name():null);
        return this.bodyString(s1, contentType1);
    }

    public Request bodyForm(Iterable<? extends NameValuePair> formParams) {
        return this.bodyForm(formParams, Consts.UTF_8);
    }

    public Request bodyForm(NameValuePair... formParams) {
        return this.bodyForm((Iterable) Arrays.asList(formParams), Consts.UTF_8);
    }

    public Request bodyFormGBK(NameValuePair... formParams) {
        return this.bodyForm((Iterable) Arrays.asList(formParams), Charset.forName("GBK"));
    }

    public Request bodyString(String s, ContentType contentType) {
        Charset charset = contentType != null?contentType.getCharset():null;
        byte[] raw = charset != null?s.getBytes(charset):s.getBytes();
        return this.body(new ByteArrayEntity(raw, contentType));
    }

    public Request bodyByteArray(byte[] b) {
        return this.body(new ByteArrayEntity(b));
    }

    public Request bodyByteArray(byte[] b, ContentType contentType) {
        return this.body(new ByteArrayEntity(b, contentType));
    }

    HttpHost getProxy() {
        return this.proxy;
    }

    Header getHeader(String name) {
        return this.request.getFirstHeader(name);
    }

    @Override
    public String toString() {
        return this.request.getRequestLine().toString();
    }

    public HttpRequestBase getBaseRequest(){
        return this.request;
    }
}
