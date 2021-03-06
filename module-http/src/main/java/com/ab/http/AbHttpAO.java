package com.ab.http;

import android.app.Activity;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.debug.AbDebug;
import com.ab.dialog.AbDialogOnItemClickListener;
import com.ab.dialog.AbSampleDialogFragment;
import com.ab.global.AbDevice;
import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.global.Global;
import com.ab.module.IHttp;
import com.ab.module.MM;
import com.ab.thread.ThreadCollector;
import com.ab.thread.ValueRunnable;
import com.ab.util.Dialogs;
import com.ab.util.JsonUtils;
import com.ab.util.MD5;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.MalformedJsonException;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.BuglyLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.PostFileBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * ?????? http??????
 *
 * @author Lee
 */
public class AbHttpAO implements IHttp {
    public static final String TAG = AbHttpAO.class.getName();

    private static final String[] arrCookieKey = new String[]{"JSESSIONID", "sso_session_uid", "sso_session_uid_sign", "gr_user_id"};
    private static final String[] arrCookieValue = new String[arrCookieKey.length];

    public static final String MODE_STANDARD = "mode_standard";
    public static final String MODE_COMPLEX = "mode_complex";

    private static String mode = MODE_STANDARD;
    //    private static String STANDARD_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1 ToucaiH5";
    private static String STANDARD_AGENT = "";

    /**
     * post json???????????????
     **/
    public static final MediaType APPLICATION_JSON = MediaType.parse("application/json; charset=utf-8");
    private CountDownTimer mockTimer;

    private static IHttp mInstance;
    private String host = "";
    private boolean usePrimaryHttp = false;
    private ThreadLocal<Integer> specialVersionParam = new ThreadLocal<>();
    private AbHttpHandler handler;
    private PersistentCookieJar cookieJar;
    private Interceptor interceptor;

    private AbHttpAO() {
        //AbDebug.setLocalDebug(AbHttpAO.TAG, AbDebug.TAG_HTTP);
        //Logger.t("AbHttpAO");
    }

    public static IHttp getIns() {
        if (mInstance == null) {
            mInstance = new AbHttpAO();
        }
        return mInstance;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        AbHttpAO.mode = mode;
    }


    /**
     * ?????????????????????
     *
     * @param host
     */
    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setHandler(AbHttpHandler handler) {
        this.handler = handler;
    }

    /**
     * ????????????http??????
     */
    @Override
    public void usePrimaryHttp() {
        usePrimaryHttp = true;
    }

    @Override
    public void clearCookies() {
        cookieJar.clear();
    }

    @Override
    public void init() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(Global.app));

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            private StringBuilder mMessage = new StringBuilder();

            @Override
            public void log(String message) {
                // ????????????????????????
                if (message.startsWith("--> POST") || message.startsWith("--> GET")) {
                    mMessage.setLength(0);
                }
                // ???{}??????[]?????????????????????????????????json??????????????????????????????
                if ((message.startsWith("{") && message.endsWith("}"))
                        || (message.startsWith("[") && message.endsWith("]"))) {
                    message = JsonUtils.formatJson(JsonUtils.decodeUnicode(message));
                }
                try {
                    mMessage.append(message.concat("\n"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // ?????????????????????????????????
                if (message.startsWith("<-- END HTTP")) {
                    Logger.i(mMessage.toString()+"");
                    //??????cookie, ???????????? Set-Cookie Cookie

                    for (int i = 0; i < arrCookieKey.length; i++) {
                        if (mMessage.toString().contains(arrCookieKey[i] + "=")) {
                            try {
                                Pattern pattern = Pattern.compile("(" + arrCookieKey[i] + "=(\\S+)\\s*)");
                                Matcher matcher = pattern.matcher(mMessage);
                                while (matcher.find()) {
                                    arrCookieValue[i] = matcher.group(2).replace(";", "");
                                    //Log.d(TAG, "sessionId:-------" + i + "---" + arrCookieKey[i] + "=" + arrCookieValue[i]);
                                }
                            } catch (Exception e) {
                                if (e != null) {
                                    Logger.d(e.getMessage()+"");
                                }
                            }
                        }
                    }

                }

            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                
               /* if(Strs.isNotEmpty(STANDARD_AGENT) && Strs.isEmpty(request.header("User-Agent"))){
                    request.headers("User-Agent").add(STANDARD_AGENT);
                }*/
    
              /*  long t1 = System.nanoTime();
                logger.info(String.format("Sending request %s on %s%n%s",
                        request.url(), chain.connection(), request.headers()));*/

                Response response = chain.proceed(request);

                // long t2 = System.nanoTime();
//                logger.info(String.format("Received response for %s in %.1fms%n%s",
//                        response.request().url(), (t2 - t1) / 1e6d, response.headers()));
//
                return response;

            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .cookieJar(cookieJar)
                .addNetworkInterceptor(logInterceptor)
                .addInterceptor(interceptor)
                .readTimeout(15000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }


    //**************GET  BEGIN**************/

    /**
     * ??????get, ???????????????
     */
    @Override
    public AbHttpTicket get(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result) {
        AbHttpTicket ticket = null;
        if (tag != null) {
            ticket = new AbHttpTicket(tag);
        }

        //????????????
        if (!isConnected()) {
            Toast.makeText(Global.app, "????????????, ???????????????", Toast.LENGTH_SHORT).show();
            if (ticket != null && ticket.isCancelled()) {  //?????????????????????????????????, ???????????????????????????.
                return ticket;
            } else {  //?????????,?????????????????????, ????????????????????????ticket, http???????????????isCancelled??????true;
                Logger.d("AbHttpAO.get() + host(" + host + ") funcName(" + funcName + ") + ??????????????????");
                if (ticket != null) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    ticket.clear();
                }
                processNoConnection(result, CODEC_HTTP.NO_CONNECTION);
                return ticket;
            }
        }

        //????????????
        Map<String, Object> totalParamMap = null;
        AbHttpReqEntity reqEntity = null;
        if (mode.equals(MODE_STANDARD)) {
            totalParamMap = assebleStandardParams(mapParam);
        } else if (mode.equals(MODE_COMPLEX)) {
            reqEntity = handler.genRequestEntity();
            totalParamMap = assebleComplexParams(reqEntity, mapParam);
        }
        if (totalParamMap == null) {
            Logger.d("AbHttpAO.get() + host(" + host + ") funcName(" + funcName + ") + ????????????????????????");
            //??????????????????, ??????????????????, ??????????????????, ???????????????.
            if (ticket != null) {
                ticket.setResult(AbHttpTicket.RESULT_FAIL);
                ticket.clear();
            }
            return ticket;
        }
        String totalParamJson = new GsonBuilder().disableHtmlEscaping().create().toJson(totalParamMap, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        Logger.i("AbHttpAO.get() + host(" + host + ") funcName(" + funcName + ") + ????????????-->\nparam:" + totalParamJson);

        //????????????
        try {
            String path = (funcName.startsWith("/") ? funcName : "/" + funcName);
            GetBuilder builder = OkHttpUtils.get().url(host + path).tag(tag);
            buildHeader(builder);
            for (Iterator<Map.Entry<String, Object>> iterator = totalParamMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Object> entry = iterator.next();
                if (entry.getKey().equals("data")) {
                    builder.addParams(entry.getKey(), URLEncoder.encode(entry.getValue().toString(), "utf-8"));
                } else {
                    if (entry.getValue() != null) {
                        builder.addParams(entry.getKey(), entry.getValue().toString());
                    } else {
                        //((PostFormBuilder)builder).addParams(entry.getKey(), null);
                    }
                }
            }
            RequestCall request = builder.build();
            if (ticket != null) {
                ticket.setCall(request.getCall());
            }
            request.execute(new AbHttpCallBack(ticket, tag, host, funcName, reqEntity, result, handler));
        } catch (Exception e) {
            BuglyLog.e("AbHttpAO.get() + host(" + host + ") funcName(" + funcName + ") ????????????, ????????????!", e.getMessage());
            Log.d(TAG, "AbHttpAO.get() + host(" + host + ") funcName(" + funcName + ") ????????????, ????????????!" + e.getMessage());
            if (e instanceof ConnectException) {
                if (ticket != null && !ticket.isCancelled()) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    processNoConnection(result, CODEC_HTTP.NO_SERVER);
                }
            } else {
                if (ticket != null && !ticket.isCancelled()) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    if (!result.onError(CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc)) {
                        handler.processCommonFailure(null, CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc);
                    }
                    result.onFinish();
                }
            }
            if (ticket != null) {
                ticket.clear();
            }
        }
        return ticket;
    }


    /**
     * ??????get, ???????????????
     */
    @Override
    public AbHttpTicket get(Object tag, String funcName, int specifalVersion, Map<String, Object> mapParam, AbHttpResult result) {
        specialVersionParam.set(specifalVersion);
        return get(tag, funcName, mapParam, result);
    }

    /**
     * ??????get, ???????????????
     */
    @Override
    public AbHttpTicket get(Object tag, String funcName, boolean forceLastCookie, Map<String, Object> mapParam, AbHttpResult result) {
        if (forceLastCookie) {
            HttpUrl url = HttpUrl.parse(ENV.curr.host);
            ArrayList<Cookie> cookies = new ArrayList<>();
            for (int i = 0; i < arrCookieKey.length; i++) {
                if (Strs.isNotEmpty(arrCookieValue[i])) {
                    Cookie cookie = new Cookie.Builder()
                            .domain(getHostWithoutHttp())
                            .name(arrCookieKey[i])
                            .value(arrCookieValue[i])
                            .build();
                    cookies.add(cookie);
                }
            }

            cookieJar.clear();
            cookieJar.saveFromResponse(url, cookies);
        }
        return get(tag, funcName, mapParam, result);
    }

    /**
     * ????????????get
     */
    @Override
    public void get(Object tag, String url, Map<String, Object> mapParam, Callback callback) {
        //????????????
        if (!isConnected()) {
            Toast.makeText(Global.app, "????????????, ???????????????", Toast.LENGTH_SHORT).show();
            callback.onError(null, new ConnectException("???????????????"), -1);
            Logger.d("AbHttpAO.get() + url(" + url + ") + ??????????????????");
            return;
        }

        //????????????
        usePrimaryHttp();
        Map<String, Object> totalParamMap = assebleStandardParams(mapParam);
        if (totalParamMap == null) {
            Logger.d("AbHttpAO.get() + url(" + url + ") + ????????????????????????");
            return;
        }

        Logger.i("AbHttpAO.get() + url(" + url + ") + ????????????-->...");

        //????????????
        try {
            GetBuilder builder = OkHttpUtils.get().url(url).tag(tag);
            buildHeader(builder);
            for (Iterator<Map.Entry<String, Object>> iterator = totalParamMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Object> entry = iterator.next();
                builder.addParams(entry.getKey(), entry.getValue().toString());
            }
            builder.build().execute(callback);
        } catch (Exception e) {
            Logger.d("AbHttpAO.get() + host(" + url + ") ????????????, ????????????!", e.getMessage());
        }
    }

//**************GET  END**************/

//**************POST  BEGIN**************/

    /**
     * post???????????? ???????????????
     */
    @Override
    public AbHttpTicket post(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result) {
        AbHttpTicket ticket = null;
        if (tag != null) {
            ticket = new AbHttpTicket(tag);
        }
        //????????????
        if (!isConnected()) {
            Toast.makeText(Global.app, "????????????, ???????????????", Toast.LENGTH_SHORT).show();
            if (ticket != null && ticket.isCancelled()) {  //?????????????????????????????????, ???????????????????????????.
                return ticket;
            } else {  //?????????,?????????????????????, ????????????????????????ticket, http???????????????isCancelled??????true;
                Logger.d("AbHttpAO.post() + host(" + host + ") funcName(" + funcName + ") + ??????????????????");
                if (ticket != null) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    ticket.clear();
                }
                processNoConnection(result, CODEC_HTTP.NO_CONNECTION);
                return ticket;
            }
        }

        //????????????
        Map<String, Object> totalParamMap = null;
        AbHttpReqEntity reqEntity = null;
        if (mode.equals(MODE_STANDARD)) {
            totalParamMap = assebleStandardParams(mapParam);
        } else if (mode.equals(MODE_COMPLEX)) {
            reqEntity = handler.genRequestEntity();
            totalParamMap = assebleComplexParams(reqEntity, mapParam);
        }
        if (totalParamMap == null) {
            Logger.d("AbHttpAO.post() + host(" + host + ") funcName(" + funcName + ") + ????????????????????????");
            //??????????????????, ??????????????????, ??????????????????, ???????????????.
            if (ticket != null) {
                ticket.setResult(AbHttpTicket.RESULT_FAIL);
                ticket.clear();
            }
            return ticket;
        }
        String totalParamJson = new GsonBuilder().disableHtmlEscaping().create().toJson(totalParamMap, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        Logger.i("AbHttpAO.post() + host(" + host + ") funcName(" + funcName + ") + ????????????--> \nparam:" + totalParamJson);

        //????????????
        try {
            OkHttpRequestBuilder builder = null;
            String path = (funcName.startsWith("/") ? funcName : "/" + funcName);
            if (MODE_STANDARD.equals(mode)) {
                builder = OkHttpUtils.post().url(host + path).tag(tag);
                buildHeader(builder);
                for (Iterator<Map.Entry<String, Object>> iterator = totalParamMap.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if (entry.getValue() != null) {
                        ((PostFormBuilder) builder).addParams(entry.getKey(), entry.getValue().toString());
                    } else {
                        //((PostFormBuilder)builder).addParams(entry.getKey(), null);
                    }
                }
            } else if (MODE_COMPLEX.equals(mode)) {
                builder = OkHttpUtils.postString().content(totalParamJson).mediaType(APPLICATION_JSON).url(host + path).tag(tag);
                buildHeader(builder);
            }
            RequestCall request = builder.build();
            if (ticket != null) {
                ticket.setCall(request.getCall());
            }
            request.execute(new AbHttpCallBack(ticket, tag, host, funcName, reqEntity, result, handler));
        } catch (Exception e) {
            Logger.d("AbHttpAO.post() + host(" + host + ") funcName(" + funcName + ") ????????????, ????????????!", e.getMessage());
            if (e instanceof ConnectException) {
                if (ticket != null && !ticket.isCancelled()) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    processNoConnection(result, CODEC_HTTP.NO_SERVER);
                }
            } else {
                if (ticket != null && !ticket.isCancelled()) {
                    if (!result.onError(CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc)) {
                        ticket.setResult(AbHttpTicket.RESULT_FAIL);
                        handler.processCommonFailure(null, CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc);
                    }
                    result.onFinish();
                }
            }
            if (ticket != null) {
                ticket.clear();
            }
        }
        return ticket;
    }

    /**
     * post???????????? ???????????????
     */
    @Override
    public AbHttpTicket post(Object tag, String funcName, Map<String, Object> mapParam,File file, AbHttpResult result) {
        AbHttpTicket ticket = null;
        if (tag != null) {
            ticket = new AbHttpTicket(tag);
        }
        //????????????
        if (!isConnected()) {
            Toast.makeText(Global.app, "????????????, ???????????????", Toast.LENGTH_SHORT).show();
            if (ticket != null && ticket.isCancelled()) {  //?????????????????????????????????, ???????????????????????????.
                return ticket;
            } else {  //?????????,?????????????????????, ????????????????????????ticket, http???????????????isCancelled??????true;
                Logger.d("AbHttpAO.post() + host(" + host + ") funcName(" + funcName + ") + ??????????????????");
                if (ticket != null) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    ticket.clear();
                }
                processNoConnection(result, CODEC_HTTP.NO_CONNECTION);
                return ticket;
            }
        }

        //????????????
        Map<String, Object> totalParamMap = null;
        AbHttpReqEntity reqEntity = null;
        if (mode.equals(MODE_STANDARD)) {
            totalParamMap = assebleStandardParams(mapParam);
        } else if (mode.equals(MODE_COMPLEX)) {
            reqEntity = handler.genRequestEntity();
            totalParamMap = assebleComplexParams(reqEntity, mapParam);
        }
        if (totalParamMap == null) {
            Logger.d("AbHttpAO.post() + host(" + host + ") funcName(" + funcName + ") + ????????????????????????");
            //??????????????????, ??????????????????, ??????????????????, ???????????????.
            if (ticket != null) {
                ticket.setResult(AbHttpTicket.RESULT_FAIL);
                ticket.clear();
            }
            return ticket;
        }
        String totalParamJson = new GsonBuilder().disableHtmlEscaping().create().toJson(totalParamMap, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        Logger.i("AbHttpAO.post() + host(" + host + ") funcName(" + funcName + ") + ????????????--> \nparam:" + totalParamJson);

        //????????????
        try {
            OkHttpRequestBuilder builder = null;
            String path = (funcName.startsWith("/") ? funcName : "/" + funcName);
            if (MODE_STANDARD.equals(mode)) {
                builder = OkHttpUtils.post().url(host + path).tag(tag);
                buildHeader(builder);
                for (Iterator<Map.Entry<String, Object>> iterator = totalParamMap.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if (entry.getValue() != null) {
                        ((PostFormBuilder) builder).addParams(entry.getKey(), entry.getValue().toString());
                    } else {
                        //((PostFormBuilder)builder).addParams(entry.getKey(), null);
                    }
                }
                ((PostFormBuilder) builder).addHeader("Content-Type","multipart/form-data").addFile("file","im_image.jpg",file);
            } else if (MODE_COMPLEX.equals(mode)) {
                builder = OkHttpUtils.postString().content(totalParamJson).mediaType(APPLICATION_JSON).url(host + path).tag(tag);
                buildHeader(builder);
            }
            RequestCall request = builder.build();
            if (ticket != null) {
                ticket.setCall(request.getCall());
            }
            request.execute(new AbHttpCallBack(ticket, tag, host, funcName, reqEntity, result, handler));
        } catch (Exception e) {
            Logger.d("AbHttpAO.post() + host(" + host + ") funcName(" + funcName + ") ????????????, ????????????!", e.getMessage());
            if (e instanceof ConnectException) {
                if (ticket != null && !ticket.isCancelled()) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    processNoConnection(result, CODEC_HTTP.NO_SERVER);
                }
            } else {
                if (ticket != null && !ticket.isCancelled()) {
                    if (!result.onError(CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc)) {
                        ticket.setResult(AbHttpTicket.RESULT_FAIL);
                        handler.processCommonFailure(null, CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc);
                    }
                    result.onFinish();
                }
            }
            if (ticket != null) {
                ticket.clear();
            }
        }
        return ticket;
    }

    @Override
    public AbHttpTicket postJson(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result) {
        AbHttpTicket ticket = null;
        if (tag != null) {
            ticket = new AbHttpTicket(tag);
        }
        //????????????
        if (!isConnected()) {
            Toast.makeText(Global.app, "????????????, ???????????????", Toast.LENGTH_SHORT).show();
            if (ticket != null && ticket.isCancelled()) {  //?????????????????????????????????, ???????????????????????????.
                return ticket;
            } else {  //?????????,?????????????????????, ????????????????????????ticket, http???????????????isCancelled??????true;
                Logger.d("AbHttpAO.postJson() + host(" + host + ") funcName(" + funcName + ") + ??????????????????");
                if (ticket != null) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    ticket.clear();
                }
                processNoConnection(result, CODEC_HTTP.NO_CONNECTION);
                return ticket;
            }
        }

        //????????????
        Map<String, Object> totalParamMap = null;
        AbHttpReqEntity reqEntity = null;
        if (mode.equals(MODE_STANDARD)) {
            totalParamMap = assebleStandardParams(mapParam);
        } else if (mode.equals(MODE_COMPLEX)) {
            reqEntity = handler.genRequestEntity();
            totalParamMap = assebleComplexParams(reqEntity, mapParam);
        }
        if (totalParamMap == null) {
            Logger.d("AbHttpAO.postJson() + host(" + host + ") funcName(" + funcName + ") + ????????????????????????");
            //??????????????????, ??????????????????, ??????????????????, ???????????????.
            if (ticket != null) {
                ticket.setResult(AbHttpTicket.RESULT_FAIL);
                ticket.clear();
            }
            return ticket;
        }
        String totalParamJson = new GsonBuilder().disableHtmlEscaping().create().toJson(totalParamMap, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        Logger.i("AbHttpAO.post() + postJson(" + host + ") funcName(" + funcName + ") + ????????????--> \nparam:" + totalParamJson);

        //????????????
        try {
            OkHttpRequestBuilder builder = null;
            String path = (funcName.startsWith("/") ? funcName : "/" + funcName);

            builder = OkHttpUtils.postString().content(totalParamJson).mediaType(APPLICATION_JSON).url(host + path).tag(tag);
            buildHeader(builder);

            RequestCall request = builder.build();
            if (ticket != null) {
                ticket.setCall(request.getCall());
            }
            request.execute(new AbHttpCallBack(ticket, tag, host, funcName, reqEntity, result, handler));
        } catch (Exception e) {
            Logger.d("AbHttpAO.postJson() + host(" + host + ") funcName(" + funcName + ") ????????????, ????????????!", e.getMessage());
            if (e instanceof ConnectException) {
                if (ticket != null && !ticket.isCancelled()) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    processNoConnection(result, CODEC_HTTP.NO_SERVER);
                }
            } else {
                if (ticket != null && !ticket.isCancelled()) {
                    if (!result.onError(CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc)) {
                        ticket.setResult(AbHttpTicket.RESULT_FAIL);
                        handler.processCommonFailure(null, CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc);
                    }
                    result.onFinish();
                }
            }
            if (ticket != null) {
                ticket.clear();
            }
        }
        return ticket;
    }


    /**
     * post???????????? ???????????????
     */
    @Override
    public AbHttpTicket post(Object tag, String funcName, int specifalVersion, Map<String, Object> mapParam, AbHttpResult result) {
        specialVersionParam.set(specifalVersion);
        return post(tag, funcName, mapParam, result);
    }

    /**
     * ??????post, ???????????????
     */
    @Override
    public AbHttpTicket post(Object tag, String funcName, boolean forceLastCookie, Map<String, Object> mapParam, AbHttpResult result) {
        if (forceLastCookie) {
            HttpUrl url = HttpUrl.parse(ENV.curr.host);
            ArrayList<Cookie> cookies = new ArrayList<>();
            for (int i = 0; i < arrCookieKey.length; i++) {
                if (Strs.isNotEmpty(arrCookieValue[i])) {
                    Cookie cookie = new Cookie.Builder()
                            .domain(getHostWithoutHttp())
                            .name(arrCookieKey[i])
                            .value(arrCookieValue[i])
                            .build();
                    cookies.add(cookie);
                }
            }

            cookieJar.clear();
            cookieJar.saveFromResponse(url, cookies);
        }
        return get(tag, funcName, mapParam, result);
    }


    /**
     * ??????post??????????????????
     */
    @Override
    public void post(Object tag, String url, Map<String, Object> mapParam, Callback callback) {
        //????????????
        if (!isConnected()) {
            Toast.makeText(Global.app, "????????????, ???????????????", Toast.LENGTH_SHORT).show();
            callback.onError(null, new ConnectException("???????????????"), -1);
            Logger.d("AbHttpAO.post() + url(" + url + ") + ??????????????????");
            return;
        }

        //????????????, ?????????????????????
        usePrimaryHttp();
        Map<String, Object> totalParamMap = assebleStandardParams(mapParam);
        if (totalParamMap == null) {
            Logger.d("AbHttpAO.post() + url(" + url + ") + ????????????????????????");
            return;
        }

        Logger.i("AbHttpAO.post() + url(" + url + ") + ????????????-->...");

        //????????????
        try {
            PostFormBuilder builder = OkHttpUtils.post().url(url).tag(tag);
            buildHeader(builder);
            for (Iterator<Map.Entry<String, Object>> iterator = totalParamMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Object> entry = iterator.next();
                builder.addParams(entry.getKey(), entry.getValue().toString());
            }
            builder.build().execute(callback);
        } catch (Exception e) {
            Logger.d("AbHttpAO.post() + url(" + url + ") ????????????, ????????????!", e.getMessage());
        }
    }
//**************POST  END**************/


//***************?????? BEGIN*****************/

    /**
     * ??????get
     */
    @Override
    public synchronized AbHttpTicket syncGet(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result) {
        AbHttpTicket ticket = null;
        if (tag != null) {
            ticket = new AbHttpTicket(tag);
        }
        //????????????
        if (!isConnected()) {
            Logger.d("AbHttpAO.syncGet() + host(" + host + ") funcName(" + funcName + ") + ??????????????????");
            ThreadCollector.getIns().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Global.app, "????????????, ???????????????", Toast.LENGTH_SHORT).show();
                }
            });
            if (ticket != null && !ticket.isCancelled()) {
                ticket.setResult(AbHttpTicket.RESULT_FAIL);
                ticket.clear();
            }
            if (result != null) {
                processNoConnection(result, CODEC_HTTP.NO_CONNECTION);
            }
            return ticket;
        }

        //????????????
        AbHttpReqEntity reqEntity = handler.genRequestEntity();
        Map<String, Object> totalParamMap = null;
        if (mode.equals(MODE_STANDARD)) {
            totalParamMap = assebleStandardParams(mapParam);
        } else if (mode.equals(MODE_COMPLEX)) {
            totalParamMap = assebleComplexParams(reqEntity, mapParam);
        }
        if (totalParamMap == null) {
            Logger.d("AbHttpAO.syncGet() + host(" + host + ") funcName(" + funcName + ") + ????????????????????????");
            if (ticket != null) {
                ticket.setResult(AbHttpTicket.RESULT_FAIL);
                ticket.clear();
            }
            return ticket;
        }

        Logger.i("AbHttpAO.syncGet() + host(" + host + ") funcName(" + funcName + ") + ????????????-->...");

        //????????????
        try {
            GetBuilder builder = OkHttpUtils.get().url(host + "/" + funcName).tag(tag);
            buildHeader(builder);
            for (Iterator<Map.Entry<String, Object>> iterator = totalParamMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Object> entry = iterator.next();
                if (entry.getKey().equals("data")) {
                    builder.addParams(entry.getKey(), URLEncoder.encode(entry.getValue().toString(), "utf-8"));
                } else {
                    builder.addParams(entry.getKey(), entry.getValue().toString());
                }
            }
            RequestCall request = builder.build();
            if (ticket != null) {
                ticket.setCall(request.getCall());
            }
            if (result != null) {
                result.onBefore(null, 0, host, funcName);
            }
            Response syncResp = request.execute();
            if (ticket != null && ticket.isCancelled()) {
                ticket.clear();
                return ticket;
            }
            processSyncResponse(ticket, funcName, result, reqEntity, syncResp);
        } catch (Exception e) {
            if (e instanceof ConnectException) {
                ticket.setResult(AbHttpTicket.RESULT_FAIL);
                if (result != null) {
                    processNoConnection(result, CODEC_HTTP.NO_SERVER);
                }
            } else if (e instanceof InterruptedException || e instanceof InterruptedIOException
                    || (e instanceof SocketException && e.getMessage().contains("Socket closed"))
                    || (e instanceof IOException && e.getMessage().contains("Canceled"))) {
                //??????????????????, ??????????????????
                if (ticket != null) {
                    ticket.setResult(AbHttpTicket.RESULT_CANCELLED);
                }
            } else {
                //?????????????????????, ??????failuer
                Logger.d("AbHttpAO.syncGet() + host(" + host + ") funcName(" + funcName + ") ????????????, ????????????!", e.getMessage());
                if (ticket != null && !ticket.isCancelled()) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    if (result != null) {
                        if (!result.onError(CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc)) {
                            handler.processCommonFailure(null, CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc);
                        }
                        result.onFinish();
                    }
                }
            }
            if (ticket != null) {
                ticket.clear();
            }
        }
        return ticket;
    }

    /**
     * ??????????????????
     *
     * @param tag
     * @param url
     * @param mapParam
     * @param callback
     */
    @Override
    public void syncGet(Object tag, String url, Map<String, Object> mapParam, Callback callback) {
        //????????????
        if (!isConnected()) {
            callback.onError(null, new ConnectException("???????????????"), -1);
            ThreadCollector.getIns().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Global.app, "????????????, ???????????????", Toast.LENGTH_SHORT).show();
                }
            });
            Logger.d("AbHttpAO.syncGet() + url(" + url + ") + ??????????????????");
            return;
        }

        //????????????
        AbHttpReqEntity reqEntity;
        Map<String, Object> totalParamMap = null;
        if (mode.equals(MODE_STANDARD)) {
            totalParamMap = assebleStandardParams(mapParam);
        } else if (mode.equals(MODE_COMPLEX)) {
            reqEntity = handler.genRequestEntity();
            totalParamMap = assebleComplexParams(reqEntity, mapParam);
        }
        if (totalParamMap == null) {
            Logger.d("AbHttpAO.syncGet() + url(" + url + ") + ????????????????????????");
            return;
        }

        Logger.i("AbHttpAO.syncGet() + url(" + url + ") + ????????????-->...");

        //????????????
        try {
            GetBuilder builder = OkHttpUtils.get().url(url).tag(tag);
            buildHeader(builder);
            for (Iterator<Map.Entry<String, Object>> iterator = totalParamMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Object> entry = iterator.next();
                builder.addParams(entry.getKey(), entry.getValue().toString());
            }

            Response syncResp = builder.build().execute();
            if (callback instanceof FileCallBack) {
                ((FileCallBack) callback).parseNetworkResponse(syncResp, 0);
            } else {
                callback.onResponse(syncResp, 0);
            }
        } catch (Exception e) {
            Logger.d("AbHttpAO.syncGet() + url(" + url + ") ????????????, ????????????!", e.getMessage());
        }
    }


    @Override
    public AbHttpTicket syncGet(Object tag, String funcName, int specifalVersion, Map<String, Object> mapParam, AbHttpResult result) {
        specialVersionParam.set(specifalVersion);
        return syncGet(tag, funcName, mapParam, result);
    }

    /**
     * sync????????????
     */
    @Override
    public synchronized AbHttpTicket syncPost(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result) {
        AbHttpTicket ticket = null;
        if (tag != null) {
            ticket = new AbHttpTicket(tag);
        }
        //????????????
        if (!isConnected()) {
            Logger.d("AbHttpAO.syncPost() + host(" + host + ") funcName(" + funcName + ") + ??????????????????");
            ThreadCollector.getIns().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Global.app, "????????????, ???????????????", Toast.LENGTH_SHORT).show();
                }
            });
            if (ticket != null && !ticket.isCancelled()) {
                ticket.setResult(AbHttpTicket.RESULT_FAIL);
                ticket.clear();
            }
            if (result != null) {
                processNoConnection(result, CODEC_HTTP.NO_CONNECTION);
            }
            return ticket;
        }

        //????????????
        AbHttpReqEntity reqEntity = null;
        Map<String, Object> totalParamMap = null;
        if (mode.equals(MODE_STANDARD)) {
            totalParamMap = assebleStandardParams(mapParam);
        } else if (mode.equals(MODE_COMPLEX)) {
            reqEntity = handler.genRequestEntity();
            totalParamMap = assebleComplexParams(reqEntity, mapParam);
        }
        String totalParamJson = new GsonBuilder().disableHtmlEscaping().create().toJson(totalParamMap, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        if (totalParamMap == null) {
            Logger.d("AbHttpAO.syncPost() + host(" + host + ") funcName(" + funcName + ") + ????????????????????????");
            if (ticket != null) {
                ticket.setResult(AbHttpTicket.RESULT_FAIL);
                ticket.clear();
            }
            return ticket;
        }

        Logger.i("AbHttpAO.syncPost() + host(" + host + ") funcName(" + funcName + ") + ????????????--> \nparam:" + totalParamJson);

        //????????????
        try {
            OkHttpRequestBuilder builder = null;
            String path = (funcName.startsWith("/") ? funcName : "/" + funcName);
            if (MODE_STANDARD.equals(mode)) {
                builder = OkHttpUtils.post().url(host + path).tag(tag);
                buildHeader(builder);
                for (Iterator<Map.Entry<String, Object>> iterator = totalParamMap.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if (entry.getValue() != null) {
                        ((PostFormBuilder) builder).addParams(entry.getKey(), entry.getValue().toString());
                    } else {
                        //((PostFormBuilder)builder).addParams(entry.getKey(), null);
                    }
                }
            } else if (MODE_COMPLEX.equals(mode)) {
                builder = OkHttpUtils.postString().content(totalParamJson).mediaType(APPLICATION_JSON).url(host + path).tag(tag);
                buildHeader(builder);
            }
            RequestCall request = builder.build();
            if (ticket != null) {
                ticket.setCall(request.getCall());
            }
            if (result != null) {
                result.onBefore(null, 0, host, funcName);
            }
            Response syncResp = request.execute();
            if (ticket != null && ticket.isCancelled()) {
                ticket.clear();
                return ticket;
            }
            processSyncResponse(ticket, funcName, result, reqEntity, syncResp);
        } catch (Exception e) {
            if (e instanceof ConnectException) {
                if (ticket != null) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                }
                if (result != null) {
                    processNoConnection(result, CODEC_HTTP.NO_SERVER);
                }
            } else if (e instanceof InterruptedException || e instanceof InterruptedIOException
                    || (e instanceof SocketException && e.getMessage().contains("Socket closed"))
                    || (e instanceof IOException && e.getMessage().contains("Canceled"))) {
                //??????????????????, ??????????????????
                if (ticket != null) {
                    ticket.setResult(AbHttpTicket.RESULT_CANCELLED);
                }
            } else {
                //?????????????????????, ??????failuer
                Logger.d("AbHttpAO.syncPost() + host(" + host + ") funcName(" + funcName + ") ????????????, ????????????!: ", e.getMessage());
                Log.e(TAG, "AbHttpAO.syncPost() + host(" + host + ") funcName(" + funcName + ") ????????????, ????????????!: " + e.getMessage());
                if (ticket != null && !ticket.isCancelled()) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    if (result != null) {
                        if (!result.onError(CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc)) {
                            handler.processCommonFailure(null, CODEC_HTTP.NET_WORK_FAIL.code, CODEC_HTTP.NET_WORK_FAIL.desc);
                        }
                        result.onFinish();
                    }
                }
            }
            if (ticket != null) {
                ticket.clear();
            }
        }
        return ticket;
    }

    @Override
    public void syncPost(Object tag, String url, Map<String, Object> mapParam, Callback callback) {
        //????????????
        if (!isConnected()) {
            callback.onError(null, new ConnectException("???????????????"), -1);
            ThreadCollector.getIns().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Global.app, "????????????, ???????????????", Toast.LENGTH_SHORT).show();
                }
            });
            Logger.d("AbHttpAO.syncPost() + url(" + url + ") + ??????????????????");
            return;
        }

        //????????????
        Map<String, Object> totalParamMap = assebleStandardParams(mapParam);
        if (totalParamMap == null) {
            Logger.d("AbHttpAO.syncPost() + url(" + url + ") + ????????????????????????");
            return;
        }

        Logger.i("AbHttpAO.syncPost() + url(" + url + ") + ????????????-->...");

        //????????????
        try {
            PostFormBuilder builder = OkHttpUtils.post().url(url).tag(tag);
            buildHeader(builder);
            for (Iterator<Map.Entry<String, Object>> iterator = totalParamMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Object> entry = iterator.next();
                builder.addParams(entry.getKey(), entry.getValue().toString());
            }
            Response syncResp = builder.build().execute();
            callback.onResponse(syncResp, 0);
        } catch (Exception e) {
            Logger.d("AbHttpAO.syncPost() + url(" + url + ") ????????????, ????????????!", e.getMessage());
        }
    }

    @Override
    public AbHttpTicket syncPost(Object tag, String funcName, int specifalVersion, Map<String, Object> mapParam, AbHttpResult result) {
        specialVersionParam.set(specifalVersion);
        return syncPost(tag, funcName, mapParam, result);
    }

//***************?????? END*****************/


//**************????????????????????????????????? BEGIN*****************/

    /**
     * ??????http???????????????????????????
     */
    public void mock(final Activity act, final String funcName, final Map<String, Object> mapParam, final String[] arrResult, final AbHttpResult callback) {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                AbHttpReqEntity reqEntity = null;
                Map<String, Object> totalParamMap = null;
                if (mode.equals(MODE_STANDARD)) {
                    totalParamMap = assebleStandardParams(mapParam);
                } else if (mode.equals(MODE_COMPLEX)) {
                    reqEntity = handler.genRequestEntity();
                    totalParamMap = assebleComplexParams(reqEntity, mapParam);
                }
                String totalParamJson = new GsonBuilder().disableHtmlEscaping().create().toJson(totalParamMap, new TypeToken<HashMap<String, Object>>() {
                }.getType());


                OkHttpRequestBuilder builder = null;
                if (MODE_STANDARD.equals(mode)) {
                    builder = OkHttpUtils.post().url("http://" + funcName);
                    for (Iterator<Map.Entry<String, Object>> iterator = totalParamMap.entrySet().iterator(); iterator.hasNext(); ) {
                        Map.Entry<String, Object> entry = iterator.next();
                        if (entry.getValue() != null) {
                            ((PostFormBuilder) builder).addParams(entry.getKey(), entry.getValue().toString());
                        } else {
                            //((PostFormBuilder)builder).addParams(entry.getKey(), null);
                        }
                    }
                } else if (MODE_COMPLEX.equals(mode)) {
                    builder = OkHttpUtils.postString().content(totalParamJson).mediaType(APPLICATION_JSON).url("http://" + funcName);
                }


                final ValueRunnable mimic = getValueRunnable(funcName, arrResult, callback, reqEntity, builder);

                //???????????????????????? ?????????[0]
                final AbSampleDialogFragment dialog = Dialogs.showOptionListDialog(act, "???????????????" + totalParamJson + "\n???????????????", new ArrayAdapter<String>(act, R.layout.ab_item_simple_text_list, arrResult),
                        true, new AbDialogOnItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position, long id) {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mimic.setValue(position);
                                        mimic.run();
                                        if (mockTimer != null) {
                                            mockTimer.cancel();
                                        }
                                        Dialogs.removeDialog(act, Dialogs.OPTION_LIST_DIALOG);
                                    }
                                });
                            }

                            @Override
                            public void onNegativeClick() {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Random random = new Random();
                                        mimic.setValue(random.nextInt(arrResult.length));
                                        mimic.run();
                                        if (mockTimer != null) {
                                            mockTimer.cancel();
                                        }
                                        Dialogs.removeDialog(act, Dialogs.OPTION_LIST_DIALOG);
                                    }
                                });
                            }
                        });

                final TextView tvBottomInfo = (TextView) dialog.getContentView().findViewById(R.id.tvBottomInfo);

                //???????????? ????????????????????? ?????????0???
                mockTimer = new CountDownTimer(10 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (tvBottomInfo != null && tvBottomInfo.getVisibility() == View.VISIBLE) {
                            tvBottomInfo.setText("?????????" + millisUntilFinished / 1000f);
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (dialog.isVisible()) {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mimic.setValue(0);
                                    mimic.run();
                                    Dialogs.removeDialog(act, Dialogs.OPTION_LIST_DIALOG);
                                }
                            });
                        }
                    }
                }.start();
            }
        });
    }

    @NonNull
    private ValueRunnable<Integer> getValueRunnable(final String funcName, final String[] arrResult, final AbHttpResult callback, final AbHttpReqEntity req, final OkHttpRequestBuilder builder) {
        return new ValueRunnable<Integer>() {
            @Override
            public void run() {
                Response response = null;
                if (getValue() < 0) {
                    response = new Response.Builder()
                            .request(builder.build().getOkHttpRequest().generateRequest(new Callback() {
                                @Override
                                public Object parseNetworkResponse(Response response, int id) throws Exception {
                                    //nothing
                                    return null;
                                }

                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    //nothing
                                }

                                @Override
                                public void onResponse(Object response, int id) {
                                    //nothing
                                }
                            }))
                            .protocol(Protocol.HTTP_1_0)
                            .code(404)
                            .body(ResponseBody.create(APPLICATION_JSON, "{}"))
                            .build();
                } else {
                    response = new Response.Builder()
                            .request(builder.build().getOkHttpRequest().generateRequest(new Callback() {
                                @Override
                                public Object parseNetworkResponse(Response response, int id) throws Exception {
                                    //nothing
                                    return null;
                                }

                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    //nothing
                                }

                                @Override
                                public void onResponse(Object response, int id) {
                                    //nothing
                                }
                            }))
                            .protocol(Protocol.HTTP_1_0)
                            .code(200)
                            .body(ResponseBody.create(APPLICATION_JSON, arrResult[getValue()]))
                            .build();
                }

                processSyncResponse(null, funcName, callback, req, response);
            }
        };
    }

    /**
     * ????????????http???????????????????????????
     */
    public void syncMock(final String funcName, Map<String, Object> mapParam, final String[] arrResult, final AbHttpResult callback) throws InterruptedException {
        Thread.sleep(3 * 1000);
        Map<String, Object> totalParamMap = null;
        AbHttpReqEntity reqEntity = null;
        if (mode.equals(MODE_STANDARD)) {
            totalParamMap = assebleStandardParams(mapParam);
        } else if (mode.equals(MODE_COMPLEX)) {
            reqEntity = handler.genRequestEntity();
            totalParamMap = assebleComplexParams(reqEntity, mapParam);
        }
        String totalParamJson = new GsonBuilder().disableHtmlEscaping().create().toJson(totalParamMap, new TypeToken<HashMap<String, Object>>() {
        }.getType());

        OkHttpRequestBuilder builder = null;
        if (MODE_STANDARD.equals(mode)) {
            builder = OkHttpUtils.post().url("http://" + funcName);
            for (Iterator<Map.Entry<String, Object>> iterator = totalParamMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Object> entry = iterator.next();
                if (entry.getValue() != null) {
                    ((PostFormBuilder) builder).addParams(entry.getKey(), entry.getValue().toString());
                } else {
                    //((PostFormBuilder)builder).addParams(entry.getKey(), null);
                }
            }
        } else if (MODE_COMPLEX.equals(mode)) {
            builder = OkHttpUtils.postString().content(totalParamJson).mediaType(APPLICATION_JSON).url("http://" + funcName);
        }

        final ValueRunnable mimic = getValueRunnable(funcName, arrResult, callback, reqEntity, builder);
        Random random = new Random();
        mimic.setValue(random.nextInt(arrResult.length));
        mimic.run();
    }
//**************????????????????????????????????? BEGIN*****************/

    /**
     * ??????????????????, ?????????
     *
     * @param mapParam
     */
    private Map<String, Object> assebleStandardParams(Map<String, Object> mapParam) {
        Map<String, Object> totalOKParamMap = new HashMap<>();
        if (mapParam != null) {
            totalOKParamMap.putAll(mapParam);
        }
        return totalOKParamMap;
    }

    /**
     * ??????????????????, ????????????????????????
     *
     * @param reqEntity
     * @param mapParam
     * @return
     */
    @Nullable
    private Map<String, Object> assebleComplexParams(AbHttpReqEntity reqEntity, Map<String, Object> mapParam) {
        Map<String, Object> totalOKParamMap = new HashMap<>();
        //?????????????????????
        if (!usePrimaryHttp) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            Map<String, Object> mapUninParam = null;
            Map<String, Map<String, Object>> dataParam = new HashMap<>();
            mapUninParam = handler.genUninParam();
            //??????????????????
            try {
                if (mapUninParam != null) {
                    dataParam.put("uninParam", mapUninParam);
                }
                if (mapParam != null) {
                    dataParam.put("bizParams", mapParam);
                }
                String jsonStr = "";
                try {
                    jsonStr = gson.toJson(dataParam, new TypeToken<HashMap<String, HashMap<String, Object>>>() {
                    }.getType());
                } catch (Exception e) {
                    Logger.d("??????json????????????", e.getMessage()+"");
                }
                if (Strs.isEmpty(jsonStr)) {
                    jsonStr = "{}";
                }
                reqEntity.setParam(jsonStr);
                if (specialVersionParam.get() != null) {
                    reqEntity.setVersion(specialVersionParam.get());
                    specialVersionParam.remove();
                }
                totalOKParamMap.put("version", reqEntity.getVersion());
                totalOKParamMap.put("timestamp", reqEntity.getTimestamp());
                totalOKParamMap.put("appId", reqEntity.getAppId());
                totalOKParamMap.put("param", reqEntity.getParam());
                String str = reqEntity.getAppId() + reqEntity.getTimestamp() + reqEntity.getAppSecrect() + reqEntity.getParam();
                String sign = MD5.md5(str);
                //Logger.i(AbDebug.TAG_HTTP, "????????????: ????????? (" + str + ")\nmd5 (" + sign + ")");
                totalOKParamMap.put("sign", sign);
            } catch (Exception e) {
                return null;
            }
        } else {
            if (mapParam != null) {
                totalOKParamMap.putAll(mapParam);
            }
            usePrimaryHttp = false;
        }

        return totalOKParamMap;
    }

    /**
     * ?????????????????????
     *
     * @param result
     */
    private void processNoConnection(AbHttpResult result, CODEC_HTTP error) {
        if (!result.onError(error.code, error.desc)) {
            handler.processCommonFailure(null, error.code, error.desc);
        }
        result.onFinish();
    }

    /**
     * ???????????????????????????
     *
     * @param funcName
     * @param result
     * @param reqEntity
     * @param syncResp
     */
    private void processSyncResponse(AbHttpTicket ticket, String funcName, AbHttpResult result, AbHttpReqEntity reqEntity, Response syncResp) {
        AbHttpCallBack callBack = new AbHttpCallBack(null, null, host, funcName, reqEntity, result, handler);

        if (ticket != null && ticket.isCancelled()) {
            ticket.clear();
            return;
        }
        if (syncResp.isSuccessful()) {
            AbHttpRespEntity resp = null;
            try {
                resp = callBack.parseNetworkResponse(syncResp, 0);
                try {
                    if (ticket != null) {
                        ticket.setResult(AbHttpTicket.RESULT_SUCCESS);
                        ticket.setData(resp);
                    }
                    callBack.onResponse(resp, 0);
                } catch (Exception e) {
                    CODEC_HTTP error = CODEC_HTTP.RESP_HANDLE_EXCEPTION;
                    if (ticket != null) {
                        ticket.setResult(AbHttpTicket.RESULT_FAIL);
                        ticket.setData(syncResp);
                    }
                    if (result != null) {
                        if (!result.onError(error.code, error.desc)) {
                            //????????????????????????. ??????????????????.
                            handler.processCommonFailure(resp, error.code, error.desc);
                        }
                    }
                    Logger.d("????????????????????????!", e.getMessage());
                }
            } catch (Exception e) {
                Logger.d("??????????????????????????????!", e.getMessage());
                if (ticket != null) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    ticket.setData(syncResp);
                }
                CODEC_HTTP decodeError = CODEC_HTTP.RESP_DECODE_EXCEPTION;
                if (result != null) {
                    if (!result.onError(decodeError.code, decodeError.desc)) {
                        handler.processCommonFailure(null, decodeError.code, decodeError.desc);
                    }
                }
            } finally {
                if (ticket != null && ticket.isCancelled()) {
                    ticket.clear();
                } else {
                    if (result != null) {
                        result.onFinish();
                    }
                }
            }
        } else {
            if (ticket != null) {
                ticket.setResult(AbHttpTicket.RESULT_FAIL);
                ticket.setData(syncResp);
            }
            CODEC_HTTP netWorkFail = CODEC_HTTP.NET_WORK_FAIL;
            if (result != null) {
                if (!result.onError(netWorkFail.code, netWorkFail.desc)) {
                    handler.processCommonFailure(null, netWorkFail.code, netWorkFail.desc);
                }
                result.onFinish();
            }
        }
    }

    /**
     * ????????????.
     */
    private static class AbHttpCallBack extends Callback<AbHttpRespEntity> {
        AbHttpReqEntity req;
        AbHttpTicket ticket;
        public Object tag;
        public String host;
        String funcName;
        AbHttpResult result;
        public AbHttpHandler handler;

        public AbHttpCallBack(AbHttpTicket ticket, Object tag, String host, String funcName, AbHttpReqEntity req, AbHttpResult result, AbHttpHandler handler) {
            this.ticket = ticket;
            this.tag = tag;
            this.host = host;
            this.funcName = funcName;
            this.req = req;
            this.result = result;
            this.handler = handler;
        }

        @Override
        public void onBefore(Request request, int id) {
            if (result != null) {
                result.onBefore(request, id, host, funcName);
            }
        }

        @Override
        public void onAfter(int id) {
            if (result != null) {
                result.onAfter(id);
            }
        }

        @Override
        public AbHttpRespEntity parseNetworkResponse(Response response, int id) throws Exception {
            String jsonStr = response.body().string();
            Logger.i("AbHttpAO.response() + host(" + host + ") funcName(" + funcName + ") ?????? <--??????:(" + response.toString() + ")\n?????????:( " + jsonStr + " )");

            //?????????, ????????????html
            if (handler.isResponceForUserInvalid(jsonStr)) {
                return new AbHttpRespEntity("abhttp user invalide");
            }

            //??????
            AbHttpRespEntity entity = new AbHttpRespEntity(jsonStr);
            if (result != null) {
                result.setupEntity(entity);
                entity.parseFields(jsonStr.trim());
                return entity;
            } else {
                return null;
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            Logger.d("AbHttpAO.response() + host(" + host + ") funcName(" + funcName + ") ?????? XXX ????????????!", e.getMessage());
            Log.e(TAG, "AbHttpAO.response() + host(" + host + ") funcName(" + funcName + ") ?????? XXX ????????????!: " + e.getMessage());
            if (ticket != null) {
                ticket.setResult(AbHttpTicket.RESULT_FAIL);
            }
            CODEC_HTTP error = null;
            if (e instanceof ConnectException || e instanceof SocketTimeoutException || e instanceof UnknownHostException) { //??????????????????, ???????????????.
                error = CODEC_HTTP.NO_SERVER;
            } else if ("Canceled!".equals(e.getMessage())) {
                //http???????????????, ???????????????????????????.
                error = CODEC_HTTP.CANCELLED_OR_INTERUPTED;
            } else if (e instanceof MalformedJsonException || e instanceof JsonSyntaxException) {
                error = CODEC_HTTP.RESP_DECODE_EXCEPTION;
            } else if (e instanceof IOException) {
                error = CODEC_HTTP.SERVER_EXCEPTION;
                Pattern pattern = Pattern.compile("reponse's code is : (\\d+)");
                String message = e.getMessage();
                if (Strs.isEmpty(message)) {
                    Matcher matcher = pattern.matcher(message);
                    String value = "";
                    while (matcher.find()) {
                        value = matcher.group(1);
                        break;
                    }
                    if (Nums.parse(value, 0) > 0) {
                        CODEC_HTTP.SERVER_EXCEPTION.code = Nums.parse(value, 0);
                    }
                }
                //CODEC_HTTP.SERVER_EXCEPTION.desc = "???????????????(" + e.getMessage() + ")";
                CODEC_HTTP.SERVER_EXCEPTION.desc = "???????????????";
            } else {
                error = CODEC_HTTP.FAIL;
            }
            if (ticket == null || !ticket.isCancelled()) {
                if (result != null) {
                    if (!result.onError(error.code, error.desc)) {
                        //????????????????????????. ??????????????????.
                        handler.processCommonFailure(null, error.code, error.desc);
                    }
                    result.onFinish();
                }
            }
            if (ticket != null) {
                ticket.clear();
            }
        }

        @Override
        public void onResponse(AbHttpRespEntity resp, int id) {
            if (resp == null) {//?????????????????????,  ???????????????????????????????????????
                return;
            }

            if (ticket != null && ticket.isCancelled()) {
                ticket.clear();
                return;
            }
            //??????
            /*
            String str = req.getAppId() + resp.getCode() + resp.getMsg()
                    + (resp.getData() == null ? "":resp.getData())
                    + req.getAppSecrect() + resp.getTimestamp()
                    + (resp.getAttach() == null ? "":resp.getAttach());
            String sign = MD5.md5(str);
            //Logger.i(AbDebug.TAG_HTTP, "????????????: ????????? (" + str + ")\nmd5 (" + sign + ")");
            if (!sign.equals(resp.getSign())) {
                //????????????????????????
                Logger.d(TAG, "????????????????????????!!!");
                if(ticket != null) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    ticket.setData(resp);
                }
                if (result != null) {
                    result.onError(CODEC_HTTP.RESP_DECODE_EXCEPTION.code, CODEC_HTTP.RESP_DECODE_EXCEPTION.desc);
                    result.onFinish();
                }
                return;
            }
            */
            try {
                if (Strs.isNotEmpty(resp.getStr()) && !"abhttp user invalide".equals(resp.getStr())) {
                    try {

                        if (!result.onSuccessGetObject(resp.getCode(), resp.getError(), resp.getMessage(), resp.getExtras())) {
                            result.onGetString(resp.getStr());
                        }
                    } catch (Exception e) {
                        result.onError(-1, "??????????????????,?????????!");
                        BuglyLog.e("?????????????????????????????????!", e.getMessage());
                        Log.e(TAG, "?????????????????????????????????!: " + e.getMessage());
                    }

                    if (Strs.isEqual("-4", resp.getStr())) {//????????????????????????
                        handler.processUserModefyPwd();
                    }

                } else if ("abhttp user invalide".equals(resp.getStr())) { //?????????????????????. ????????????????????????.
                    handler.processUserInvalid();
                }

                if (ticket != null && ticket.isCancelled()) {
                    ticket.clear();
                    return;
                }

                //????????????
                if (ticket != null) {
                    ticket.setResult(AbHttpTicket.RESULT_SUCCESS);
                    ticket.setData(resp);
                }
                /*
                if (result != null && !result.onGetString(resp.getResp())) {
                    if (resp.isSuccess()) {
                        result.onSuccessGetObject(resp.getCode(), resp.getMsg(), resp.getDataObj());
                    } else {
                        if(ticket != null) {
                            ticket.setResult(AbHttpTicket.RESULT_FAIL);
                            ticket.setData(resp);
                        }
                        if (!result.onError(resp.getCode(), resp.getMsg())) {
                            //????????????????????????. ??????????????????.
                            handler.processCommonFailure(resp, resp.getCode(), resp.getMsg());
                        }
                    }
                }
                */
            } catch (Exception e) {
                CODEC_HTTP error = CODEC_HTTP.RESP_HANDLE_EXCEPTION;
                if (ticket != null) {
                    ticket.setResult(AbHttpTicket.RESULT_FAIL);
                    ticket.setData(resp);
                }
                if (result != null) {
                    if (!result.onError(error.code, error.desc)) {
                        //????????????????????????. ??????????????????.
                        handler.processCommonFailure(resp, error.code, error.desc);
                    }
                }
                BuglyLog.e("????????????????????????!", e.getMessage());
                Log.e(TAG, "????????????????????????!: " + e.getMessage());
            } finally {
                if (ticket != null && ticket.isCancelled()) {
                    ticket.clear();
                    return;
                }
                if (resp != null) {
                    result.onFinish();
                }
            }
        }
    }

    /**
     * @param tag ==null,???????????????http??????
     */
    public void cancellAllByTag(Object tag) {
        OkHttpUtils.getInstance().cancelTag(tag);
        AbHttpTicket.clearAll(tag);
    }

    /**
     * ???????????????.
     *
     * @return
     */
    @Override
    public boolean isConnected() {
        return AbDevice.getConnectivity() >= 0;
    }

    public static String getUserAgent() {
        if (Strs.isNotEmpty(STANDARD_AGENT)) {
            return STANDARD_AGENT;
        }
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(Global.app);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();

        if (userAgent != null) {
            for (int i = 0, length = userAgent.length(); i < length; i++) {
                char c = userAgent.charAt(i);
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", (int) c));
                } else {
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }

    @Override
    public String getHostWithoutHttp() {
        String host = ENV.curr.host;
        if (Strs.isEmpty(host)) {
            ENV.curr = Config.isDebug() ? ENV.TEST : ENV.PUBLISH;
        }
        String result = ENV.curr.host.replace("http://", "").replace("https://", "");
        String temp = result;
        if (result.contains(":")) {
            String[] split = result.split(":");
            temp = split[0];
        }
        BuglyLog.e("AbHttpAO", "getHostWithoutHttp:" + temp);
        Log.d("HttpActionTouCai", "getHostWithoutHttp: " + result);
        return temp;
    }

    @Override
    public String getLastCookie() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arrCookieKey.length; i++) {
            if (Strs.isNotEmpty(arrCookieValue[i])) {
                builder.append(builder.length() == 0 ? arrCookieKey[i] + "=" + arrCookieValue[i] : ";" + arrCookieKey[i] + "=" + arrCookieValue[i]);
            }
        }
        return builder.toString();
        /*HttpUrl url = HttpUrl.parse(ENV.curr.host);
        List<Cookie> cookies = cookieJar.loadForRequest(url);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cookies.size(); i++) {
        
        }*/
    }
    
    
    /*public static void main(String [] args){
        Pattern pattern = Pattern.compile("reponse's code is : (\\d+)");
        Matcher matcher = pattern.matcher("reponse's code is : 500");
        String value = "";
        while (matcher.find()) {
            value = matcher.group(1);
            break;
        }
            System.out.println(value);
    }*/

    private OkHttpRequestBuilder buildHeader(OkHttpRequestBuilder builder) {
        builder.addHeader("User-Agent", getUserAgent());
        builder.addHeader("deviceType", "2");
        builder.addHeader("deviceCode", AbDevice.ANDROID_ID);
        return builder;
    }
}
