package com.ab.http;

import android.app.Activity;
import android.app.Application;

import com.ab.global.ENV;
import com.ab.module.IHttp;
import com.ab.module.ModuleBase;

import java.io.File;
import java.util.Map;

/**
 * Created by lee on 2017/9/12.
 */

public  class HttpModule extends ModuleBase implements IHttp {
    
    public static final String NAME = HttpModule.class.getSimpleName();
    public static final int VERSION = 100;
    public static final String DESC = "目前对应okhttp版本3.3.1 https://github.com/hongyangAndroid/okhttputils.git";
    
    private AbHttpHandler handler;
    
    public HttpModule(AbHttpHandler handler) {
        super(NAME, VERSION, DESC);
        this.handler = handler;
    }
    
    
    @Override
    public void onPreConfig() {
        //网络具体配置
        setHost(ENV.curr.host);
        setMode(AbHttpAO.MODE_STANDARD);
        setHandler(handler);
    }
    
    @Override
    public void onPostConfig() {
    
    }
    
    @Override
    public void onCreate(Application app) {
       init();
    }
    
    @Override
    public void onDestroy() {
        
    }
    
    @Override
    public void setHost(String host) {
        AbHttpAO.getIns().setHost(host);
    }
    
    @Override
    public void setHandler(AbHttpHandler handler) {
        AbHttpAO.getIns().setHandler(handler);
    }
    
    @Override
    public void setMode(String mode) {
        AbHttpAO.getIns().setMode(mode);
    }
    
    
    @Override
    public void usePrimaryHttp() {
        AbHttpAO.getIns().usePrimaryHttp();
    }
    
    @Override
    public void clearCookies() {
        AbHttpAO.getIns().clearCookies();
    }
    
    @Override
    public AbHttpTicket get(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result) {
        return AbHttpAO.getIns().get(tag, funcName, mapParam, result);
    }
    
    @Override
    public AbHttpTicket get(Object tag, String funcName, int specifalVersion, Map<String, Object> mapParam, AbHttpResult result) {
        return AbHttpAO.getIns().get(tag, funcName, specifalVersion, mapParam, result);
    }
    
    @Override
    public AbHttpTicket get(Object tag, String funcName, boolean forceLastCookie, Map<String, Object> mapParam, AbHttpResult result) {
        return AbHttpAO.getIns().get(tag, funcName, forceLastCookie, mapParam, result);
    }
    
    @Override
    public void get(Object tag, String funcName, Map<String, Object> mapParam, Callback callback) {
        AbHttpAO.getIns().get(tag, funcName, mapParam, callback);
    }
    
    @Override
    public AbHttpTicket post(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result) {
        return AbHttpAO.getIns().post(tag, funcName, mapParam, result);
    }

    @Override
    public AbHttpTicket post(Object tag, String funcName, Map<String, Object> mapParam, File file, AbHttpResult result) {
        return AbHttpAO.getIns().post(tag, funcName, mapParam,file, result);
    }

    @Override
    public AbHttpTicket postJson(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result) {
        return AbHttpAO.getIns().postJson(tag, funcName, mapParam, result);
    }
    
    @Override
    public AbHttpTicket post(Object tag, String funcName, int specifalVersion, Map<String, Object> mapParam, AbHttpResult result) {
        return AbHttpAO.getIns().post(tag, funcName, specifalVersion, mapParam, result);
    }
    
    @Override
    public AbHttpTicket post(Object tag, String funcName, boolean forceLastCookie, Map<String, Object> mapParam, AbHttpResult result) {
        return AbHttpAO.getIns().post(tag, funcName, forceLastCookie, mapParam, result);
    }
    
    @Override
    public void post(Object tag, String funcName, Map<String, Object> mapParam, Callback result) {
        AbHttpAO.getIns().post(tag, funcName, mapParam, result);
    }
    
    @Override
    public void mock(Activity act, String funcName, Map<String, Object> params, String[] arrResult, AbHttpResult callback) {
        AbHttpAO.getIns().mock(act, funcName, params, arrResult, callback);
    }
    
    @Override
    public AbHttpTicket syncGet(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result) {
        return AbHttpAO.getIns().syncGet(tag, funcName, mapParam, result);
    }
    
    @Override
    public void syncGet(Object tag, String url, Map<String, Object> mapParam, Callback callback) {
        AbHttpAO.getIns().syncGet(tag, url, mapParam, callback);
    }
    
    @Override
    public AbHttpTicket syncGet(Object tag, String funcName, int specifalVersion, Map<String, Object> mapParam, AbHttpResult result) {
        return AbHttpAO.getIns().syncGet(tag, funcName, specifalVersion, mapParam, result);
    }
    
    @Override
    public AbHttpTicket syncPost(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result) {
        return AbHttpAO.getIns().syncPost(tag, funcName, mapParam, result);
    }
    
    @Override
    public void syncPost(Object tag, String url, Map<String, Object> mapParam, Callback callback) {
        AbHttpAO.getIns().syncPost(tag, url, mapParam, callback);
    }
    
    @Override
    public AbHttpTicket syncPost(Object tag, String funcName, int specifalVersion, Map<String, Object> mapParam, AbHttpResult result) {
        return AbHttpAO.getIns().syncPost(tag, funcName,specifalVersion, mapParam, result);
    }
    
    @Override
    public void syncMock(String funcName, Map<String, Object> params,  final String [] arrResult, AbHttpResult callback) throws InterruptedException {
        AbHttpAO.getIns().syncMock(funcName, params, arrResult, callback);
    }
    
    @Override
    public void cancellAllByTag(Object tag) {
        AbHttpAO.getIns().cancellAllByTag(tag);
    }
    
    @Override
    public boolean isConnected() {
        return AbHttpAO.getIns().isConnected();
    }
    
    @Override
    public void init() {
        AbHttpAO.getIns().init();
    }
    
    @Override
    public String getHostWithoutHttp() {
        return AbHttpAO.getIns().getHostWithoutHttp();
    }
    
    @Override
    public String getLastCookie() {
        return AbHttpAO.getIns().getLastCookie();
    }
}
