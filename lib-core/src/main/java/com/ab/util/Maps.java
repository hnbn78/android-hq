package com.ab.util;

import com.ab.model.AbKeepBaseEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Maps {
	@SuppressWarnings("unchecked")
	public static  <T> T value(Map<String, Object> map, String key, T def){
        if (map == null) {
            return null;
        }
		T value = def;
		Object obj = null;
		if(map != null){
			obj = map.get(key);
			if(obj != null){
				value = (T)obj;
			}
		}
		return value;
	}
	
	
	@SuppressWarnings("unchecked")
	public static  <T> T parse(Map<String, String> map, String key, T def){
        if (map == null) {
            return null;
        }
		Object value = def;
		String str = null;
		if(map != null){
			str = map.get(key);
			if(str != null && !str.equals("")){
				value = Strs.parse(str, def);
			}
		} 
		return (T) value;
	}
	
	public static <T extends AbKeepBaseEntity> HashMap<String, Object> fromObject(T obj){
		HashMap<String, Object> map = new HashMap<String, Object>();
		Class<? extends AbKeepBaseEntity> clazz = obj.getClass();
		Field [] filds = clazz.getDeclaredFields();
		String key = "";
		try {
			for(Field f: filds) {
				key = f.getName();
				map.put(key, f.get(obj));
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static <T extends AbKeepBaseEntity> Object toObject(HashMap<String, Object> map, Class<T> clazz){
		T obj = null;
		try {
			obj = clazz.newInstance();
			Field [] filds = clazz.getDeclaredFields();
			String key = "";
			for(Field f: filds) {
				key = f.getName();
				f.set(key, map.get(key));
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static HashMap<String, String> objmapToStrmap(HashMap<String, Object> map){
		HashMap<String, String> res = new HashMap<String, String>();
		for (Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, Object> entity = (Map.Entry<String, Object>) iterator.next();
			res.put(entity.getKey(), entity.getValue().toString());
		}
		return res;
	}
    
    //**************便捷生成Map方法***********
    public static final ThreadLocal<HashMap<String, Object>> mapLocal = new ThreadLocal<HashMap<String, Object>>();
    
    public static HashMap<String, Object> newIns(){
        return new HashMap<String, Object>();
    }
    
    public static HashMap<String, Object> gen(){
        mapLocal.remove();
        mapLocal.set(new HashMap<String, Object>());
        return mapLocal.get();
    }
    
    public static HashMap<String, Object> see(){
        HashMap<String, Object> param = mapLocal.get();
        return param;
    }
    
    public static HashMap<String, Object> get(){
        HashMap<String, Object> param = mapLocal.get();
        mapLocal.remove();
        return param;
    }
    
    public static HashMap<String, Object> put(String key, Object value){
        mapLocal.get().put(key, value);
        return mapLocal.get();
    }





}
