package com.pearl.data;

import android.support.annotation.Nullable;

import com.ab.cashe.ICache;
import com.ab.debug.AbDebug;
import com.ab.global.Global;
import com.ab.util.Strs;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pearl.data.model.MyObjectBox;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Callable;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.android.AndroidObjectBrowser;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.Query;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

/**
 * Created by Leo on 16/3/21.
 */
public class AbCache implements ICache {
    public static final String KEY_TAG = "keyTag";
    // Constants==================================================

// ===========================================================
// Static Fields==============================================
    private static AbCache ins;
// ===========================================================


// Fields=====================================================
    /**
     * 总缓存器.
     */
    private LoadingCache<String, Object> globalCache;
    /**
     * 关系数据库.
     */
    private BoxStore boxStore;
    
// ===========================================================


// Static Methods=============================================
    public static AbCache getIns(){
        if(ins == null){
            ins = new AbCache();
        }
        return ins;
    }
// ===========================================================


// Constructors===============================================
    private AbCache(){
        globalCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(
                        new CacheLoader<String, Object>() {
                            public Object load(String key)  {
                                return key + "-value";
                            }
                        });
        openDB();
    }

// ===========================================================


// Methods from SuperClass/Interfaces=====================
//supper:
//interface:
// ===========================================================


// Public Methods=============================================
    @Override
    public <T> void putInCacheWithContext(Class ctx, String key, T value){
        if(ctx == null || Strs.isEmpty(key) || value == null){
            return ;
        }
        key = ctx.getName() + "_" + key;
        globalCache.put(key, value);
    }

    @Override
    public <T> T getInCacheWithContext(Class ctx, String key, T def, Callable<String> loader){
        Object value = def;
        String actName = "";
        if(ctx == null || Strs.isEmpty(key)){
            return (T)value;
        }
        key = key + "_" + ctx.getName();
        try {
            value = globalCache.get(key, loader);
        } catch (Exception e) {
            return def;
        }

        return (T)value;
    }
    
    @Override
    public void refreshCache(Class ctx, String key){
        key = ctx.getName() + "_" + key;
        globalCache.refresh(key);
    }
    
    @Override
    public void clearCache(Class ctx, String key){
        key = ctx.getName() + "_" + key;
        globalCache.put(key, null);
    }
    
    @Override
    public <T> void putInDB(Class<?> beanClazz, String key, T value){
        if(beanClazz == null || Strs.isEmpty(key) ){
            return ;
        }
        openDB();
        setTag(value, key);
        try{
            // query all notes, sorted a-z by their text (http://greenrobot.org/objectbox/documentation/queries/)
            Box box = boxStore.boxFor(beanClazz);
            box.put(value);
        }catch (Exception e){
            AbDebug.error(AbDebug.TAG_DATA, Thread.currentThread(), e);
        }
    }
    
    @Override
    public <T> void putListInDB(Class<?> beanClazz, String key, List<T> value){
        if(beanClazz == null || Strs.isEmpty(key) ){
            return ;
        }
        openDB();
        for (T bean : value) {
            setTag(bean, key);
        }
        try{
            Box box = boxStore.boxFor(beanClazz);
            Property property = getGenerateProperty(beanClazz);
            box.query().equal(property, key).build().remove();
            box.put(value);
        }catch (Exception e){
            AbDebug.error(AbDebug.TAG_DATA, Thread.currentThread(), e);
        }
    }
    
    
    @Override
    public  <T> DataSubscription getInDB(Class<?> beanClazz, String key, DataObserver<T> observer){
        if(Strs.isEmpty(key) || beanClazz == null){
            return null;
        }
        openDB();
        Property property = getGenerateProperty(beanClazz);
        if(property == null){
            return null;
        }
        Box box = boxStore.boxFor(beanClazz);
        Query query = box.query().equal(property, key).build();
        DataSubscription subscription = null;
        try {
            if (observer != null) {
                subscription = query.subscribe().on(AndroidScheduler.mainThread())
                        .observer(observer);
            }
        } catch (Exception e) {
            AbDebug.error(AbDebug.TAG_DATA, Thread.currentThread(), e);
            return null;
        }

        return subscription;
    }
    
    @Override
    public  <T> DataSubscription getListInDB(Class<?> beanClazz, final String key, DataObserver<List<T>>observer){
        if(Strs.isEmpty(key) || beanClazz == null){
            return null;
        }
        openDB();
        Property property = getGenerateProperty(beanClazz);
        if(property == null){
            return null;
        }
        Box box = boxStore.boxFor(beanClazz);
        Query query = box.query().equal(property, key).build();
        DataSubscription subscription = null;
        try {
            if (observer != null) {
                //只注册一次.
                subscription = query.subscribe().on(AndroidScheduler.mainThread())
                            .observer(observer);
            }
        } catch (Exception e) {
            AbDebug.error(AbDebug.TAG_DATA, Thread.currentThread(), e);
            return null;
        }
        
        return subscription;
    }
    
    @Override
    public void clearDB(Class<?> clazz, String key){
        openDB();
        Property tagProperty = getGenerateProperty(clazz);
        Query<?> query = boxStore.boxFor(clazz).query().equal(tagProperty, key).build();
        List old = (List) query.find();
        boxStore.boxFor(clazz).remove(old);
    }
    
    private void openDB() {
        if(boxStore == null || boxStore.isClosed()){
            boxStore = MyObjectBox.builder().androidContext(Global.app).build();
            if (BuildConfig.DEBUG) {
                new AndroidObjectBrowser(boxStore).start(Global.app);
            }
        }
    }
    
    /**
     * 清空SP
     */
    @Override
    public void clean() {
        globalCache.cleanUp();
        boxStore.close();
        boxStore.deleteAllFiles();
    }
// ===========================================================


// Private Methods============================================
    @Nullable
    private Property getGenerateProperty(Class<?> clazz) {
        Class generated = null;
        Property property = null;
        try {
            generated = Class.forName(MyObjectBox.class.getPackage().getName() + "." + clazz.getSimpleName() + "_");
            Field field = generated.getField(KEY_TAG);
            property = (Property)field.get(clazz);
        } catch (Exception e) {
            AbDebug.error(AbDebug.TAG_DATA, Thread.currentThread(), e);
        }
        return property;
    }
    
    private void setTag(Object value, String key) {
        try {
            value.getClass().getField(KEY_TAG).set(value, key);
        } catch (Exception e) {
            AbDebug.error(AbDebug.TAG_DATA, Thread.currentThread(), e);
        }
    }
// ===========================================================


// Inner and Anonymous Classes================================

// ===========================================================
}
