package com.hzren.http;



import com.google.common.base.Charsets;
import com.hzren.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author chenwei
 */
public class SimpleHttpExecutor implements HttpExecutor {
    public static final int DEFAULT_TIMEOUT = 100000;
    public static final String USER_AGENT_HEAD = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36";
    private static final PoolingHttpClientConnectionManager CONNMGR;
    private static final HttpClient CLIENT;

    static {
        SSLConnectionSocketFactory ssl = null;

        try {
            SSLContext sfr = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial((KeyStore)null, (chain, authType) -> {
                return true;
            }).build();
            ssl = new SSLConnectionSocketFactory(sfr, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        } catch (Exception var7) {
            try {
                SSLContext r = SSLContext.getInstance("TLS");
                r.init((KeyManager[])null, (TrustManager[])null, (SecureRandom)null);
                ssl = new SSLConnectionSocketFactory(r);
            } catch (SecurityException var4) {
                ;
            } catch (KeyManagementException var5) {
                ;
            } catch (NoSuchAlgorithmException var6) {
                ;
            }
        }

        Registry sfr1 = RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", ssl != null?ssl:SSLConnectionSocketFactory.getSocketFactory()).build();
        class EasySpecProvider implements CookieSpecProvider {
            EasySpecProvider() {
            }

            @Override
            public CookieSpec create(HttpContext context) {
                class EasyCookieSpec extends DefaultCookieSpec {
                    EasyCookieSpec() {
                    }

                    @Override
                    public void validate(Cookie arg0, CookieOrigin arg1) throws MalformedCookieException {
                    }

                    @Override
                    public boolean match(Cookie cookie, CookieOrigin origin) {
                        Args.notNull(cookie, "Cookie");
                        Args.notNull(origin, "Cookie origin");
                        String host = origin.getHost();
                        String domain = cookie.getDomain();
                        return StringUtils.isEmpty(domain)?false:host.endsWith(domain);
                    }
                }

                return new EasyCookieSpec();
            }
        }

        Registry r1 = RegistryBuilder.create().register("easy", new EasySpecProvider()).build();
        CONNMGR = new PoolingHttpClientConnectionManager(sfr1);
        CONNMGR.setDefaultMaxPerRoute(50);
        CONNMGR.setMaxTotal(200);
        CONNMGR.setValidateAfterInactivity(1);
        CLIENT = HttpClientBuilder.create().setConnectionManager(CONNMGR).setDefaultCookieSpecRegistry(r1).setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(Charsets.UTF_8).build()).setRetryHandler((exception, executionCount, context) -> {
            return executionCount < 3 && exception instanceof IOException;
        }).setDefaultRequestConfig(RequestConfig.custom().setCookieSpec("easy").setSocketTimeout(DEFAULT_TIMEOUT).setExpectContinueEnabled(false).build()).setUserAgent(USER_AGENT_HEAD).evictIdleConnections(1L,TimeUnit.SECONDS)
                .evictExpiredConnections().setRedirectStrategy(new LaxRedirectStrategy()).build();
    }


    private final HttpClient httpclient;
    private String userAgent;
    private final HttpClientContext localContext = HttpClientContext.create();
    private HttpHost proxy;

    public SimpleHttpExecutor(HttpHost proxy, CookieStore cookieStore) {
        this.httpclient = CLIENT;
        this.proxy = proxy;
        if (cookieStore == null){
            cookieStore = new BasicCookieStore();
        }
        this.localContext.setAttribute("http.cookie-store", cookieStore);
        this.localContext.setCookieStore(cookieStore);
    }

    @Override
    public void setCookieStore(CookieStore cookieStore) {
        this.localContext.setCookieStore(cookieStore);
    }

    @Override
    public SimpleHttpExecutor addCookies(Cookie... cookies) {
        if(null != cookies && cookies.length > 0) {
            Cookie[] var2 = cookies;
            int var3 = cookies.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Cookie cookie = var2[var4];
                this.localContext.getCookieStore().addCookie(cookie);
            }
        }

        return this;
    }

    @Override
    public <T> T requestAsObject(Request request, Class<T> clazz) {
        return JsonUtil.fromJson(this.requestAsSting(request), clazz);
    }

    @Override
    public String requestAsSting(Request request) {
        try {
            return this.execute(request).returnContent().asString();
        } catch (IOException var3) {
            throw new HttpClientException(var3);
        }finally{
            request.abort();
        }
    }

    @Override
    public byte[] requestAsByte(Request request) {
        try {
            return this.execute(request).returnContent().asBytes();
        } catch (IOException var3) {
            throw new HttpClientException(var3);
        }finally{
            request.abort();
        }
    }

    @Override
    public Content requestAsContent(Request request) {
        try {
            return this.execute(request).returnContent();
        } catch (IOException var3) {
            throw new HttpClientException(var3);
        }finally{
            request.abort();
        }

    }

    @Override
    public Response request(Request request) {
            return this.execute(request);
    }

    Response execute(Request request) {
        try {
            if(null != this.proxy && null != request.getProxy()) {
                request.viaProxy(this.proxy);
            }

            if(StringUtils.isNotEmpty(this.userAgent) && null == request.getHeader("User-Agent")) {
                request.userAgent(this.userAgent);
            }

            return new Response(request.internalExecute(this.httpclient, this.localContext));
        } catch (IOException var3) {
            throw new HttpClientException(var3);
        }
    }

    @Override
    public Optional<Cookie> getCookie(String key) {
        Iterator var2 = this.getCookies().iterator();

        Cookie cookie;
        do {
            if(!var2.hasNext()) {
                return Optional.empty();
            }

            cookie = (Cookie)var2.next();
        } while(!cookie.getName().equals(key));

        return Optional.of(cookie);
    }

    @Override
    public List<Cookie> getCookies() {
        return this.getCookieStore().getCookies();
    }

    @Override
    public CookieStore getCookieStore() {
        return this.localContext.getCookieStore();
    }

    @Override
    public Optional<URI> getRedirectUrl() {
        return null != this.localContext.getRedirectLocations() && this.localContext.getRedirectLocations().size() != 0?Optional.of(this.localContext.getRedirectLocations().get(this.localContext.getRedirectLocations().size() - 1)):Optional.empty();
    }

    @Override
    public Optional<Header> getHeader(String headerName) {
        return Optional.ofNullable(this.localContext.getResponse().getFirstHeader(headerName));
    }

    @Override
    public void setDefaultProxy(HttpHost proxy) {
        this.proxy = proxy;
    }

    @Override
    public void setDefaultUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }


}