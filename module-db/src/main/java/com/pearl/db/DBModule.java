package com.pearl.db;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.ab.callback.AbCallback;
import com.ab.db.AbDBHandler;
import com.ab.db.IOrmDb;
import com.ab.debug.AbDebug;
import com.ab.global.Global;
import com.ab.module.ModuleBase;
import com.ab.util.ArraysAndLists;
import com.ab.util.Maps;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.pearl.db.helper.OrmDBHelper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;


/**
 * Created by lee on 2017/9/26.
 */
public class DBModule extends ModuleBase implements IOrmDb, AbDBHandler {
    
    static DBModule mInstance;
    private OrmDBHelper mDBhelper;
    public  AbDBHandler handler;
    
    /**
     * 占位
     */
    public DBModule() {
        super(DBModule.class.getSimpleName(), 100, "OrmLite数据库封装增删改查");
        mInstance = this;
    }
    
    public DBModule(AbDBHandler handler) {
        super(DBModule.class.getSimpleName(), 100, "OrmLite数据库封装增删改查");
        this.handler = handler;
        mInstance = this;
    }
    
    public static DBModule getIns() {
        return mInstance;
    }
    
    @Override
    public void onCreate(Application app) {
        if (handler != null) {
            mDBhelper = new OrmDBHelper(Global.app);
            mDBhelper.getWritableDatabase();
        }
    }
    
    @Override
    public void onPreConfig() {
        
    }
    
    @Override
    public void onPostConfig() {
    
    }
    
    @Override
    public void onDestroy() {
    
    }
    
    @Override
    public int getCurrentDBVersion() {
        if (handler == null) {
            return -1;
        }
        return handler.getCurrentDBVersion();
    }
    
    @Override
    public List<Class> getTables() {
        if (handler == null) {
            return null;
        }
        return handler.getTables();
    }
    
    @Override
    public List<String> getUpdateStatement(int oldVersion, int newVersion) {
        if (handler == null) {
            return null;
        }
        return handler.getUpdateStatement(oldVersion, newVersion);
    }
    
    @Override
    public SQLiteDatabase getReadableDB() {
        if (handler == null) {
            return null;
        }
        return mDBhelper.getReadableDatabase();
    }
    
    @Override
    public SQLiteDatabase getWritableDB() {
        if (handler == null) {
            return null;
        }
        return mDBhelper.getWritableDatabase();
    }
    
    @Override
    public <T> void create(Class<T> clazz, T item) {
        if (handler == null) {
            return ;
        }
        Dao<T, ?> dao = null;
        try {
            dao = mDBhelper.getDao(clazz);
            dao.create(item);
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
        }
    }
    
    @Override
    public <T> void createArray(Class<T> clazz, T... items) {
        if (handler == null) {
            return ;
        }
    
        createList(clazz, ArraysAndLists.asList(items));
    }
    
    @Override
    public <T> void createList(Class<T> clazz, List<T> items) {
        if (handler == null) {
            return ;
        }
    
        Dao<T, ?> dao = null;
        try {
            dao = mDBhelper.getDao(clazz);
            dao.create(items);
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
        }
    }
    
    @Override
    public <T> void createOrUpdate(Class<T> clazz, T item) {
        if (handler == null) {
            return ;
        }
    
        Dao<T, ?> dao = null;
        try {
            dao = mDBhelper.getDao(clazz);
            dao.createOrUpdate(item);
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
        }
    }
    
    @Override
    public <T> void createOrUpdateArray(Class<T> clazz, T... items) {
        if (handler == null) {
            return ;
        }
    
        for (int i = 0; i < items.length; i++) {
            Dao<T, ?> dao = null;
            try {
                dao = mDBhelper.getDao(clazz);
                dao.createOrUpdate(items[i]);
            } catch (SQLException e) {
                AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
            }
        }
    }
    
    @Override
    public <T> void createOrUpdateList(Class<T> clazz, List<T> items) {
        if (handler == null) {
            return ;
        }
    
        for (int i = 0; i < items.size(); i++) {
            Dao<T, ?> dao = null;
            try {
                dao = mDBhelper.getDao(clazz);
                dao.createOrUpdate(items.get(i));
            } catch (SQLException e) {
                AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
            }
        }
    }
    
    @Override
    public <T, ID> void deleteByKey(Class<T> clazz, ID key) {
        if (handler == null) {
            return ;
        }
    
        Dao<T, ID> dao = null;
        try {
            dao = mDBhelper.getDao(clazz);
            dao.deleteById(key);
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
        }
    }
    
    @Override
    public <T> void delete(Class<T> clazz, T item) {
        if (handler == null) {
            return ;
        }
    
        Dao<T, ?> dao = null;
        try {
            dao = mDBhelper.getDao(clazz);
            dao.delete(item);
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
        }
    }
    
    @Override
    public <T> void deleteArray(Class<T> clazz, T... items) {
        if (handler == null) {
            return ;
        }
    
        deleteList(clazz, ArraysAndLists.asList(items));
    }
    
    @Override
    public <T> void deleteList(Class<T> clazz, List<T> items) {
        if (handler == null) {
            return ;
        }
    
        Dao<T, ?> dao = null;
        try {
            dao = mDBhelper.getDao(clazz);
            dao.delete(items);
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
        }
    }
    
    @Override
    public <T> void deleteAll(Class<T> clazz) {
        if (handler == null) {
            return ;
        }
    
        Dao<T, ?> dao = null;
        try {
            dao = mDBhelper.getDao(clazz);
            String tableName = clazz.getAnnotation(DatabaseTable.class).tableName();
            dao.queryRaw("delete from " + tableName);
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
        }
    }
    
    @Override
    public void deleteAllArray(Class... clazz) {
        if (handler == null) {
            return ;
        }
    
        for (int i = 0; i < clazz.length; i++) {
            deleteAll(clazz[i]);
        }
    }
    
    
    @Override
    public <T> void update(Class<T> clazz, T item) {
        if (handler == null) {
            return ;
        }
    
        Dao<T, ?> dao = null;
        try {
            dao = mDBhelper.getDao(clazz);
            dao.update(item);
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
        }
    }
    
    @Override
    public <T> void updateArray(Class<T> clazz, T... items) {
        if (handler == null) {
            return ;
        }
    
        updateList(clazz, ArraysAndLists.asList(items));
    }
    
    @Override
    public <T> void updateList(Class<T> clazz, List<T> items) {
        if (handler == null) {
            return ;
        }
    
        Dao<T, ?> dao = null;
        try {
            dao = mDBhelper.getDao(clazz);
            for (int i = 0; i < items.size(); i++) {
                dao.update(items.get(i));
            }
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
        }
    }
    
    @Override
    public <T, ID> T query(Class<T> clazz, ID key) {
        if (handler == null) {
            return null;
        }
    
        try {
            Dao<T,  ID> dao = mDBhelper.getDao(clazz);
            return dao.queryForId(key);
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
            return null;
        }
    }
    
    @Override
    public <T> List<T> queryAll(Class<T> clazz) {
        if (handler == null) {
            return null;
        }
    
        try {
            Dao<T, ?> dao = mDBhelper.getDao(clazz);
            return dao.queryForAll();
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
            return null;
        }
    }
    
    @Override
    public <T> List<T> queryForFieldValues(Class<T> clazz, String[] keys, String[] params) {
        if (handler == null) {
            return null;
        }
    
        try {
            Dao<T, ?> dao = mDBhelper.getDao(clazz);
            String tableName = clazz.getAnnotation(DatabaseTable.class).tableName();
            HashMap<String, Object> map = Maps.newIns();
            for (int i = 0; i < keys.length; i++) {
                map.put(keys[i], params[i]);
            }
            return dao.queryForFieldValues(map);
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
            return null;
        }
    }
    
    @Override
    public <T> List<T> queryListByBuilder(Class<T> clazz, AbCallback<QueryBuilder> builderProvider) {
        if (handler == null) {
            return null;
        }
    
        try {
            
            Dao<T, ?> dao = mDBhelper.getDao(clazz);
            QueryBuilder<T, ?> builder = dao.queryBuilder();
            boolean isSuccess = builderProvider.callback(builder);
            if(isSuccess){
                return dao.query(builder.prepare());
            }else{
                return null;
            }
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
            return null;
        }
    }
    
    
    @Override
    public <T> long count(Class<T> clazz) {
        if (handler == null) {
            return -1;
        }
    
        try {
            Dao<T, ?> dao = mDBhelper.getDao(clazz);
            return dao.countOf();
        } catch (SQLException e) {
            AbDebug.error(AbDebug.TAG_DB, Thread.currentThread(), e);
            return 0;
        }
    }
}
