package com.ab.db;


import android.database.sqlite.SQLiteDatabase;

import com.ab.callback.AbCallback;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;

/**
 * Created by lee on 2017/9/28.
 */
public interface IOrmDb {
    SQLiteDatabase getReadableDB();
    SQLiteDatabase getWritableDB();
    
    <T> void create(Class<T> clazz, T item);
    
    <T> void createArray(Class<T> clazz, T... items);
    
    <T> void createList(Class<T> clazz, List<T> items);
    
    <T> void createOrUpdate(Class<T> clazz, T item);
    
    <T> void createOrUpdateArray(Class<T> clazz, T... items);
    
    <T> void createOrUpdateList(Class<T> clazz, List<T> items);
    
    <T, ID> void deleteByKey(Class<T> clazz, ID key);
    
    <T> void delete(Class<T> clazz, T item);
    
    <T> void deleteArray(Class<T> clazz, T... items);
    
    <T> void deleteList(Class<T> clazz, List<T> items);
    
    <T> void deleteAll(Class<T> clazz);
    
    void deleteAllArray (Class ... clazz);
    
    <T> void update(Class<T> clazz, T item);
    
    <T> void updateArray(Class<T> clazz, T... items);
    
    <T> void updateList(Class<T> clazz, List<T> items);
    
    <T, ID> T query(Class<T> clazz, ID key);
    
    <T> List<T> queryAll(Class<T> clazz);
    
    <T> List<T> queryForFieldValues(Class<T> clazz, String[] keys, String[] params);
    
    <T> List<T> queryListByBuilder(Class<T> clazz, AbCallback<QueryBuilder> builderProvider);
    
    <T> long count(Class<T> clazz);
}
