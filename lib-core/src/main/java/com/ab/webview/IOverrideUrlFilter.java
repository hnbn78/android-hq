package com.ab.webview;

import android.app.Activity;
import android.view.View;

public interface IOverrideUrlFilter {
    /**
     * 返回true拦截处理, 返回false继续
     * @param act
     * @param webView
     * @param url
     * @return
     */
    boolean filterUrl(Activity act, View webView, String getSubject, String url);
}
