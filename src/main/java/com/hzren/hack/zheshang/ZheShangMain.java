package com.hzren.hack.zheshang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hzren.http.HttpUtil;
import com.hzren.http.Request;
import com.hzren.http.SimpleHttpExecutor;
import com.hzren.util.ExecutorUtil;
import com.hzren.util.Logger;
import com.hzren.util.NetUtil;
import com.hzren.util.WebDriverUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author hzren
 * Created on 2017/11/10.
 */
public class ZheShangMain {

    private static final RemoteWebDriver driver = WebDriverUtil.newChromeDriver();
    private static final BasicCookieStore cookieStore = new BasicCookieStore();
    private static SimpleHttpExecutor httpExecutor = new SimpleHttpExecutor(null, cookieStore);

    public static final String LOGIN_PAGE_URL = "https://web.stocke.com.cn/deskProduct/views/login.html";
    public static final String LOGIN_SUCCESS_URL = "https://web.stocke.com.cn/deskProduct/views/trade/AStockTrade/tradeStockA.html?action=buy&isLogin=true";
    public static final String BIZ_URL = "https://web.stocke.com.cn/servlet/json";

    public static final String LOCAL_IP = NetUtil.getLocalIp();
    public static final String MAC = "48-BA-4E-40-D3-6F|CC-2F-71-3D-F7-6B|CC-2F-71-3D-F7-6C";
    public static final String HARD_INFO = "INTEL SSDPEKKF256G7H(00000_00_010000_00_04E2D5_7C_0B63A4_0D.1)|TOSHIBA MQ01ACF050(7 17WD7MTC)";
    private static final String OP_STATION = "SDPC/{nw_ip}/" + LOCAL_IP + "/" + MAC + "/" + HARD_INFO + "/pc/cpu/hdp/ser/v2.0";

    private static final String ZJZH = "资金账号";
    private static final String PASSWD = "密码";

    private static final String APPLY_STOCK = "300721";
    private static final String APPLY_PRICE = "26.47";
    private static final String APPLY_AMOUNT = "100";

    private static String APPLY_EXCHANGE_TYPE;
    private static String APPLY_STOCK_ACCOUNT;

    static String publicExponent = "";
    static String modulus = "";
    static JSONObject loginResp;

    public static void main(String[] args) throws Exception{
        try {
            driver.get(LOGIN_PAGE_URL);
            HttpUtil.syncCookieFromWebDriver(driver, cookieStore);

            getRSAKeyFromServer();

            tryLogin();
            HttpUtil.syncCookieFromHttpClient(cookieStore, driver);

            driver.get(LOGIN_SUCCESS_URL);
            getStockDetail();

            ExecutorUtil.EXECUTOR_SERVICE.scheduleAtFixedRate(REFRESH_PAGE_TASK, 3, 60, TimeUnit.SECONDS);
            ExecutorUtil.EXECUTOR_SERVICE.scheduleAtFixedRate(SYNC_COOKIE_TASK, 4, 120, TimeUnit.SECONDS);
            ExecutorUtil.EXECUTOR_SERVICE.scheduleAtFixedRate(APPLY_STOSK_TASK, 10, 30,TimeUnit.SECONDS);

            //睡16个小时吧
            Thread.sleep(16 * 3600 * 1000L);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            driver.close();
        }
    }

    private static void addCookieStep1(){
        BasicClientCookie ip = new BasicClientCookie("ip", LOCAL_IP);
        BasicClientCookie mac = new BasicClientCookie("mac",MAC);
        BasicClientCookie hardInfo = new BasicClientCookie("hardInfo", HARD_INFO);
        cookieStore.addCookie(ip);
        cookieStore.addCookie(mac);
        cookieStore.addCookie(hardInfo);
    }

    private static void addCookieStep2(){
        BasicClientCookie pe = new BasicClientCookie("publicExponent", publicExponent);
        BasicClientCookie m = new BasicClientCookie("modulus", modulus);
        cookieStore.addCookie(pe);
        cookieStore.addCookie(m);
    }

    private static void addCookieStep3(){
        JSONObject clients = new JSONObject();
        JSONObject obj = loginResp.getJSONArray("results").getJSONObject(0);
        clients.put("branch_no", obj.getString("branch_no"));
        clients.put("fund_account", obj.getString("fund_account"));
        clients.put("cust_code", obj.getString("cust_code"));
        clients.put("risk_level", obj.getString("risk_level"));
        clients.put("exchange_type", obj.getString("exchange_type"));
        clients.put("stock_account", obj.getString("stock_account"));
        try {
            clients.put("client_name", URLEncoder.encode(obj.getString("client_name"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        BasicClientCookie clientinfo = new BasicClientCookie("clientinfo", JSON.toJSONString(clients));
        String ca = (String)(driver.executeScript("return "));
        BasicClientCookie clientAccount = new BasicClientCookie("clientAccount", ZJZH);

        cookieStore.addCookie(clientinfo);
        cookieStore.addCookie(clientAccount);
    }

    private static void tryLogin() throws Exception{
        for (; !doLogin(); ){}
        addCookieStep3();
    }

    private static boolean doLogin() throws Exception{
        driver.get(LOGIN_PAGE_URL);
        //验证码
        boolean get_code = false;
        String ticket = "";
        while (!get_code){
            String script = "return $('#code').val();";
            ticket = (String)(driver.executeScript(script));
            Logger.log("GET Login IMG Code : " + ticket);
            if (ticket != null){
                ticket = ticket.trim();
            }
            if (ticket == null || ticket.length() != 4){
                Logger.log("GET Login IMG Code Fail, sleep 2s. ");
                Thread.sleep(2000L);
            }else {
                get_code = true;
            }
        }

        String password = getEncryptedPasswd(PASSWD);
        Logger.log("Encrypted Passwd: " + password);

        Request request = Request.Post(BIZ_URL)
                .ajax()
                .bodyForm(new BasicNameValuePair("funcNo", "300100"),
                        new BasicNameValuePair("entrust_way", "6"),
                        new BasicNameValuePair("branch_no", ""),
                        new BasicNameValuePair("input_content", ZJZH),
                        new BasicNameValuePair("password", password),
                        new BasicNameValuePair("content_type", ""),
                        new BasicNameValuePair("auth_type", ""),
                        new BasicNameValuePair("auth_source", ""),
                        new BasicNameValuePair("auth_key", ""),
                        new BasicNameValuePair("auth_bind_station", OP_STATION),
                        new BasicNameValuePair("OP_STATION", OP_STATION),
                        new BasicNameValuePair("op_source", "0"),
                        new BasicNameValuePair("ticket", ticket),
                        new BasicNameValuePair("mobileKey", ""),
                        new BasicNameValuePair("ticketFlag", "1"),
                        new BasicNameValuePair("input_type", "0"));

        String body = httpExecutor.requestAsSting(request);
        Logger.log("--------------Login Resp Body START-----------------");
        Logger.log(body);
        Logger.log("--------------Login Resp Body End-----------------");
        loginResp = JSON.parseObject(body);
        if (!StringUtils.equals("0", loginResp.getString("error_no"))){
            Logger.log("---------------------登陆失败---------------");
            return false;
        }
        return true;
    }

    private static void getRSAKeyFromServer() throws Exception{
        addCookieStep1();

        Request request = Request.Post(BIZ_URL)
                .ajax()
                .bodyForm(new BasicNameValuePair("funcNo", "1000000"));

        String body = httpExecutor.requestAsSting(request);
        JSONObject object = JSON.parseObject(body).getJSONArray("results").getJSONObject(0);
        publicExponent = object.getString("publicExponent");
        modulus = object.getString("modulus");

        Logger.log("publicExponent: " + publicExponent);
        Logger.log("modulus: " + modulus);

        addCookieStep2();
    }

    private static String getEncryptedPasswd(String passwd){
        String script = "return $.crypto.rsa.encrypt('" + modulus + "', '" + publicExponent + "', '" + passwd + "')";
        Logger.log(script);

        Object result = driver.executeScript(script, modulus, publicExponent, passwd);
        Logger.log(result);

        return (String) result;
    }

    private static void getStockDetail(){
        String reqBody = "funcNo=1000004" +
                "&type=0%253A1%253A2%253A3%253A4%253A5%253A6%253A8%253A9%253A10%253A11%253A12%253A13%253A14%253A16%253A17%253A18%253A19%253A20%253A21%253A22%253A23%253A24%253A25%253A26%253A27%253A30%253A64%253A65%253A66" +
                "&q=" + APPLY_STOCK +
                "&count=5";
        Request request = Request.Post(BIZ_URL)
                .ajax()
                .bodyString(reqBody, ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8));
        JSONObject resp = JSON.parseObject(httpExecutor.requestAsSting(request));
        APPLY_EXCHANGE_TYPE = resp.getJSONArray("results").getJSONObject(0).getString("market");
        Logger.log("获取- APPLY_EXCHANGE_TYPE: " + APPLY_EXCHANGE_TYPE);

        reqBody = "funcNo=301514" +
                "&entrust_way=6" +
                "&branch_no=83" +
                "&fund_account=" + ZJZH +
                "&cust_code=" + ZJZH +
                "&password=" +
                "&op_station=" + NetUtil.urlEncode(NetUtil.urlEncode(OP_STATION, "UTF-8"), "UTF-8") +
                "&sessionid=" +
                "&entrust_bs=0" +
                "&stock_code=" + APPLY_STOCK +
                "&entrust_price=1" +
                "&op_source=0";
        request = Request.Post(BIZ_URL)
                .ajax()
                .bodyString(reqBody, ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8));
        resp = JSON.parseObject(httpExecutor.requestAsSting(request));
        APPLY_STOCK_ACCOUNT = resp.getJSONArray("results").getJSONObject(0).getString("stock_account");
        Logger.log("获取- APPLY_STOCK_ACCOUNT: " + APPLY_STOCK_ACCOUNT);
    }

    private static boolean applyBuyStock(){
        String reqBody = "funcNo=301501" +
                "&entrust_way=6" +
                "&branch_no=" +
                "&fund_account=" + ZJZH +
                "&cust_code=" + ZJZH +
                "&password=" +
                "&op_station=" + NetUtil.urlEncode(NetUtil.urlEncode(OP_STATION, "UTF-8"), "UTF-8") +
                "&sessionid=" +
                "&entrust_bs=0" +
                "&exchange_type=" + APPLY_EXCHANGE_TYPE +
                "&stock_account=" + APPLY_STOCK_ACCOUNT +
                "&stock_code=" + APPLY_STOCK +
                "&entrust_price=" + APPLY_PRICE +
                "&entrust_amount=" + APPLY_AMOUNT +
                "&batch_no=";

        Request request = Request.Post(BIZ_URL)
                .ajax()
                .bodyString(reqBody, ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8));

        String respBody = httpExecutor.requestAsSting(request);
        Logger.log("Apply Stock Resp:" + respBody);
        JSONObject resp = JSON.parseObject(respBody);
        String error_no = resp.getString("error_no");
        if (!StringUtils.equals(error_no, "0")){
            Logger.log("Apply Stock Fail: " + error_no);
            return false;
        }
        return true;
    }

    private static final Runnable REFRESH_PAGE_TASK = new Runnable() {
        @Override
        public void run() {
            try {
                Logger.log("---------Refresh Page At : " + new Date());
                driver.get(LOGIN_SUCCESS_URL);
            }catch (Exception e){
                Logger.log(e);
            }
        };
    };

    private static final Runnable SYNC_COOKIE_TASK = new Runnable() {
        @Override
        public void run() {
            Logger.log("-------------Sync Cookie At: " + new Date());
            HttpUtil.syncCookieFromWebDriver(driver, cookieStore);
        }
    };

    private static final Runnable APPLY_STOSK_TASK = new Runnable() {
        @Override
        public void run() {
            String reqBody = "funcNo=301508" +
                    "&entrust_way=6" +
                    "&branch_no=83" +
                    "&fund_account=2070012282" +
                    "&cust_code=2070012282" +
                    "&password=" +
                    "&op_station=" +
                    "&sessionid=" +
                    "&exchange_type=" +
                    "&stock_account=" +
                    "&stock_code=" +
                    "&entrust_bs=" +
                    "&op_source=0";
            Request request = Request.Post(BIZ_URL)
                    .ajax()
                    .addHeader("Referer", LOGIN_SUCCESS_URL)
                    .addHeader("Origin", "https://web.stocke.com.cn")
                    .addHeader("Connection", "keep-alive")
                    .bodyString(reqBody, ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8));

            JSONArray applys = JSON.parseObject(httpExecutor.requestAsSting(request)).getJSONArray("results");
            boolean applied = false;
            for (int i = 0; i < applys.size(); i++) {
                JSONObject each = applys.getJSONObject(i);
                String code = each.getString("stock_code");
                if (!Objects.equals(code, APPLY_STOCK)){
                    continue;
                }
                String status = each.getString("entrust_state_name");
                if (!Objects.equals("已撤", status)){
                    applied = true;
                }
            }
            if (applied){
                Logger.log("委托已存在, 忽略!");
                return;
            }else {
                applyBuyStock();
            }
        }
    };

}
