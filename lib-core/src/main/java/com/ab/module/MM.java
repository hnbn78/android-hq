package com.ab.module;

import com.ab.cashe.ICache;
import com.ab.db.IOrmDb;
import com.ab.debug.AbDebug;
import com.ab.global.Config;
import com.ab.global.Global;
import com.ab.push.IPush;
import com.ab.util.AbAppUtil;
import com.facebook.stetho.Stetho;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lee on 2017/9/12.
 */

public class MM {
    
    public static IHttp http;
    public static IAct act;
    public static IOrmDb db;
    public static IPush push;
    public static ICache cache;
    public  static IWebView webView;
    
    private static MM ins;
    
    public static MM getIns() {
        if (ins == null) {
            synchronized (MM.class) {
                if (ins == null) {
                    ins = new MM();
                }
            }
        }
        return ins;
    }
    
    private MM() {
        
    }
    
    /**
     * 所有模组
     */
    private ConcurrentHashMap<String, ModuleBase> mapModules = new ConcurrentHashMap<String, ModuleBase>();
    
    /**
     * 配置使用模组
     * @param modules
     */
    public void configModules(ModuleBase ... modules) {
        for (int i = 0; i < modules.length; i++) {
            mapModules.put(modules[i].getName(), modules[i]);
            if(modules[i] instanceof IHttp){
                http = (IHttp) modules[i];
            }else if(modules[i] instanceof IOrmDb){
                db = (IOrmDb) modules[i];
            }else if(modules[i] instanceof IAct){
                act = (IAct) modules[i];
            }else if(modules[i] instanceof IPush){
                push = (IPush) modules[i];
            } else if(modules[i] instanceof ICache){
                cache = (ICache) modules[i];
            } else if(modules[i] instanceof IWebView){
                webView = (IWebView) modules[i];
            }
        }
    }
    
    /**
     * 初始化所有模组
     */
    public void initAll(){
        String processName = AbAppUtil.getProcessName(Global.app, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals(Global.appPackageName);
            if (defaultProcess) {
                if(Config.isDebug()){
                    Stetho.initialize(
                            Stetho.newInitializerBuilder(Global.app)
                                    .enableDumpapp(
                                            Stetho.defaultDumperPluginsProvider(Global.app))
                                    .enableWebKitInspector(
                                            Stetho.defaultInspectorModulesProvider(Global.app))
                                    .build());
                }
                for (Map.Entry<String, ModuleBase> entry :
                        mapModules.entrySet()) {
                    entry.getValue().onPreConfig();
                    entry.getValue().onCreate(Global.app);
                    entry.getValue().onPostConfig();
                }
            }else{
                for (Map.Entry<String, ModuleBase> entry :
                        mapModules.entrySet()) {
                    if(entry.getValue().isAllProcessInit()){
                        AbDebug.log(AbDebug.TAG_APP, "*********模组[" + entry.getValue() + "] 在进程[" + processName + "]初始化");
                        entry.getValue().onPreConfig();
                        entry.getValue().onCreate(Global.app);
                        entry.getValue().onPostConfig();
                    }
                }
            }
        }
    }
    
    
    /**
     * 销毁所有模组
     */
    public void destroyAll(){
        for (Map.Entry<String, ModuleBase> entry :
                mapModules.entrySet()) {
            entry.getValue().onDestroy();
        }
        mapModules.clear();
    }
    
}
