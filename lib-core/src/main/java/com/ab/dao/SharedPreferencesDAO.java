package com.ab.dao;

import android.content.Context;
import android.content.SharedPreferences;

import com.ab.util.Strs;

import java.util.Map;

/**
 * 内部存储工具类
 * Created by  on 16/3/24.
 */
public class SharedPreferencesDAO {


    /**
     * 保存在手机里面的文件名
     */
    public String sFileName = "ab_prefrence";
    public SharedPreferences mSp = null;
    
    public SharedPreferencesDAO(Context ctx, String fileName) {
        sFileName = fileName;
        mSp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }
    
    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param key
     * @param object
     */
    public void put(String key, Object object) {
        
        SharedPreferences.Editor editor = mSp.edit();

        if(null==object){
            return;
        }

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.apply();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object get(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return mSp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return mSp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return mSp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return mSp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return mSp.getLong(key, (Long) defaultObject);
        }
        return defaultObject;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public  void clear() {
        SharedPreferences.Editor editor = mSp.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return mSp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public Map<String, ?> getAll(Context context) {
        return mSp.getAll();
    }
    
    
    public boolean getBoolean(String key, boolean def) {
        return mSp.getBoolean(key, def);
    }
    
    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
 
    public String getString(String key, String defaultStr) {
        String string = mSp.getString(key, defaultStr);
        return string;
    }
    
    public void setString(String key, String value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString(key, value);
        editor.commit();
    }
  
    
    public long getLong(String key, long value) {
        return mSp.getLong(key, value);
    }
    
    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public int getInt(String key, int value) {
        return mSp.getInt(key, value);
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    
    public void setFloat(String key, float value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putFloat(key, value);
        editor.commit();
    }
    
    public void setDouble(String key, double value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString(key, Strs.of(value));
        editor.commit();
    }
    
    public float getFloat(String key) {
        return mSp.getFloat(key, 0f);
    }
    
    public double getDouble(String key) {
        String str = mSp.getString(key, "0");
        return Strs.parse(str, 0.0);
    }
}
