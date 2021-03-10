package com.ab.module;

import android.app.Activity;

import com.ab.http.AbHttpHandler;
import com.ab.http.AbHttpResult;
import com.ab.http.AbHttpTicket;
import com.ab.http.Callback;

import java.io.File;
import java.util.Map;

/**
 * Created by lee on 2017/9/27.
 */

public interface IHttp {
    void setHost(String host);
    
    void setHandler(AbHttpHandler handler);
    
    void setMode(String mode);
    
    void usePrimaryHttp();
    
    void clearCookies();
    
    AbHttpTicket get(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result);
    
    AbHttpTicket get(Object tag, String funcName, int specifalVersion, Map<String, Object> mapParam, AbHttpResult result);
    
    AbHttpTicket get(Object tag, String funcName, boolean forceLastCookie, Map<String, Object> mapParam, AbHttpResult result);
    
    void get(Object tag, String funcName, Map<String, Object> mapParam, Callback callback);
    
    AbHttpTicket post(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result);
    AbHttpTicket post(Object tag, String funcName, Map<String, Object> mapParam, File file, AbHttpResult result);

    AbHttpTicket postJson(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result);
    
    AbHttpTicket post(Object tag, String funcName, int specifalVersion, Map<String, Object> mapParam, AbHttpResult result);
    
    AbHttpTicket post(Object tag, String funcName, boolean forceLastCookie, Map<String, Object> mapParam, AbHttpResult result);
    
    void post(Object tag, String funcName, Map<String, Object> mapParam, Callback result);
    
    void mock(final Activity act, final String funcName, Map<String, Object> mapParam, final String [] arrResult, final  AbHttpResult callback);
    
    AbHttpTicket syncGet(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result);
    
    void syncGet(Object tag, String url, Map<String, Object> mapParam, Callback callback);
    
    AbHttpTicket syncGet(Object tag, String funcName, int specifalVersion,  Map<String, Object> mapParam, AbHttpResult result);
    
    AbHttpTicket syncPost(Object tag, String funcName, Map<String, Object> mapParam, AbHttpResult result);
    
    void syncPost(Object tag, String url, Map<String, Object> mapParam, Callback callback);
    
    AbHttpTicket syncPost(Object tag, String funcName, int specifalVersion, Map<String, Object> mapParam, AbHttpResult result);
    
    void syncMock(final String funcName, Map<String, Object> mapParam,  final String [] arrResult, final  AbHttpResult callback) throws InterruptedException;
    
    void cancellAllByTag(Object tag);
    
    boolean isConnected();
    
    void init();
    
    String getHostWithoutHttp();
    
    String getLastCookie();
}
