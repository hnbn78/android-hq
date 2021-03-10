package com.ab.cashe;

import java.util.List;
import java.util.concurrent.Callable;

import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

/**
 * Created by lee on 2017/10/16.
 */

public interface ICache {
    /**
     * 数据放入缓存, 原有数据则更新, 一直保存
     * @param ctx
     * @param key
     * @param value
     * @param <T>
     */
    <T> void putInCacheWithContext(Class ctx, String key, T value);
    
    /**
     * 从缓存中获取数据, 没有则调用callable加载数据
     * @param ctx
     * @param key
     * @param def
     * @param callable
     * @param <T>
     * @return
     */
    <T> T getInCacheWithContext(Class ctx, String key, T def, Callable<String> callable);
    
    // Public Methods=============================================
    
    
    /**
     * 刷新缓存
     * @param key
     */
    void refreshCache(Class ctx, String key);
    
    /**
     * 清除某缓存
     * @param ctx
     * @param key
     */
    void clearCache(Class ctx, String key);
    
    
    /**
     * 数据放入关系数据库
     * @param beanClazz
     * @param key
     * @param value
     * @param <T> 可以是列表
     */
    <T> void putInDB(Class<?> beanClazz, String key, T value);
    
    <T> void putListInDB(Class<?> beanClazz, String key, List<T> value);
    
    /**
     * 从关系数据库获取单个数据. observer一直有效.
     * @param <T>
     * @param beanClazz
     * @param key
     * @param observer
     * @return
     */
    <T> DataSubscription getInDB(Class<?> beanClazz, String key, DataObserver<T> observer);
    
    /**
     * 从关系数据库获取列表数据. observer一直有效.
     * @param <T>
     * @param beanClazz
     * @param key
     * @param observer
     * @return
     */
    <T> DataSubscription getListInDB(Class<?> beanClazz, String key, DataObserver<List<T>> observer);
    
    void clearDB(Class<?> clazz, String key);
    
    void clean();
}
