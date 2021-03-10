package com.pearl.webview;

import android.app.Application;

import com.ab.module.IWebView;
import com.ab.module.ModuleBase;
import com.ab.webview.IOverrideUrlFilter;
import com.tencent.smtt.sdk.QbSdk;

public class WebViewModule extends ModuleBase implements IWebView{
    public IOverrideUrlFilter filter;
    
    
    public WebViewModule(IOverrideUrlFilter filter) {
        super("腾讯x4引擎v3.6.0.1234_43608", 360, "替换webview");
        this.filter = filter;
    }
    
    @Override
    public void onCreate(Application app) {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
    
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
        
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                System.out.println("");
            }
        
            @Override
            public void onCoreInitFinished() {
            
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(app,  cb);
    }
    
    @Override
    public void onPreConfig() {
    
    }
    
    @Override
    public void onPostConfig() {
    
    }
    
    public IOverrideUrlFilter getFilter() {
        return filter;
    }
    
    @Override
    public void onDestroy() {
    
    }
}
