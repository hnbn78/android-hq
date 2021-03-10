package com.pearl.data;

import android.app.Application;

import com.ab.cashe.ICache;
import com.ab.module.ModuleBase;

import java.util.List;
import java.util.concurrent.Callable;

import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

/**
 * Created by lee on 2017/9/12.
 */

public class CacheModule extends ModuleBase implements ICache{
    
    public static final String NAME = CacheModule.class.getSimpleName();
    public static final int VERSION = 100;
    public static final String DESC = "使用guava缓存, 缓存热点数据.  使用ObjectBox缓存Activity数据等,同时实现数据改变监听";
    
    public CacheModule() {
        super(NAME, VERSION, DESC);
    }
    
    @Override
    public void onCreate(Application app) {
        AbCache.getIns().clean();
    }
    
    @Override
    public void onPreConfig() {
        
    }
    
    @Override
    public void onPostConfig() {
    
    }
    
    @Override
    public void onDestroy() {
        AbCache.getIns().clean();
    }
    
    @Override
    public <T> void putInCacheWithContext(Class ctx, String key, T value) {
        AbCache.getIns().putInCacheWithContext(ctx, key, value);
    }
    
    @Override
    public <T> T getInCacheWithContext(Class ctx, String key, T def, Callable<String> callable) {
        return AbCache.getIns().getInCacheWithContext(ctx, key, def, callable);
    }
    
    @Override
    public void refreshCache(Class ctx, String key) {
        AbCache.getIns().refreshCache(ctx, key);
    }
    
    @Override
    public void clearCache(Class ctx, String key) {
        AbCache.getIns().clearCache(ctx, key);
    }
    
    @Override
    public <T> void putInDB(Class<?> beanClazz, String key, T value) {
        AbCache.getIns().putInDB(beanClazz, key, value);
    }
    
    @Override
    public <T> void putListInDB(Class<?> beanClazz, String key, List<T> value) {
        AbCache.getIns().putListInDB(beanClazz, key, value);
    }
    
    @Override
    public <T> DataSubscription getInDB(Class<?> beanClazz, String key, DataObserver<T> observer) {
        return AbCache.getIns().getInDB(beanClazz, key, observer);
    }
    
    @Override
    public <T> DataSubscription getListInDB(Class<?> beanClazz, String key, DataObserver<List<T>> observer) {
        return AbCache.getIns().getListInDB(beanClazz, key, observer);
    }
    
    @Override
    public void clearDB(Class<?> clazz, String key) {
        AbCache.getIns().clearDB(clazz, key);
    }
    
    @Override
    public void clean() {
        AbCache.getIns().clean();
    }
}
