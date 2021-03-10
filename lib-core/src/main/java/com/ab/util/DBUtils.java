package com.ab.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;

import java.util.List;

public class DBUtils {
	public static void fillContentValuesByType(ContentValues collect, String key, Object element){
    	if(collect == null){
    		return;
    	}
    	if(element == null){
    		collect.putNull(key);
    	}else if(element instanceof Byte){
    		collect.put(key, (Byte)element);
    	}else if(element instanceof byte[]){
    		collect.put(key, (byte[])element);;
    	}else if(element instanceof Short){
    		collect.put(key, (Short)element);
    	}else if(element instanceof Integer){
    		collect.put(key, (Integer)element);
    	}else if(element instanceof Long){
    		collect.put(key, (Long)element);
    	}else if(element instanceof Float){
    		collect.put(key, (Float)element);
    	}else if(element instanceof Double){
    		collect.put(key, (Double)element);
    	}else if(element instanceof String){
    		collect.put(key, (String)element);
    	}else if(element instanceof Boolean){
    		collect.put(key, (Boolean)element);
    	}
    }
	

	
	/**
	 * 依照对象属性的类型从游标中取值并设置
	 * @param clazz
	 * @param c
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@SuppressLint("DefaultLocale")
	public static void fillObjectListByType(List<Object> list, Class clazz, android.database.Cursor c) throws Exception{
		if(list == null || clazz == null){
			return;
		}
		try {
			int columnsSize=c.getColumnCount();  
			String [] columns=c.getColumnNames();  
			String columnName=""; 
			String lowerName="";
			String typeStr = null;
			//获取表的内容  
			while(c.moveToNext()){  
				Object obj = clazz.newInstance();
				for(int i=0;i<columnsSize;i++){  
					columnName = columns[i];
					lowerName = columnName.toLowerCase();
                    try{
                        typeStr = ReflectUtils.getFieldTypeString(obj.getClass(), lowerName);
                    }catch (Exception e){
                        //找不到某个字段就跳过， 因为class与表字段不一定全对应。
                        continue;
                    }
					if(typeStr.equals("class java.lang.Short") || typeStr.equals("short")){
						ReflectUtils.setFieldValue(obj, lowerName, c.getShort(c.getColumnIndex(columnName)));
					}else if(typeStr.equals("class java.lang.Integer") || typeStr.equals("int")){
						ReflectUtils.setFieldValue(obj, lowerName, c.getInt(c.getColumnIndex(columnName)));
					}else if(typeStr.equals("class java.lang.Long") || typeStr.equals("long")){
						ReflectUtils.setFieldValue(obj, lowerName, c.getLong(c.getColumnIndex(columnName)));
					}else if(typeStr.equals("class java.lang.String")){
						ReflectUtils.setFieldValue(obj, lowerName, c.getString(c.getColumnIndex(columnName)));
					}else if(typeStr.equals("class java.lang.Float") || typeStr.equals("float")){
						ReflectUtils.setFieldValue(obj, lowerName, c.getFloat(c.getColumnIndex(columnName)));
					}else if(typeStr.equals("class java.lang.Double") || typeStr.equals("double")){
						ReflectUtils.setFieldValue(obj, lowerName, c.getDouble(c.getColumnIndex(columnName)));
					}
				}
				list.add(obj);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 依照对象属性的类型从游标中取值并设置
	 * @param c
	 * @throws Exception
	 */
	@SuppressLint("DefaultLocale")
	public static void fillObjectByType(Object obj, android.database.Cursor c) throws Exception{
		if(obj == null){
			return;
		}

        int columnsSize=c.getColumnCount();
        String [] columns=c.getColumnNames();
        String columnName="";
        String lowerName="";
        String typeStr = null;
        //获取表的内容
        while(c.moveToNext()){
            for(int i=0;i<columnsSize;i++){
                try {
                    columnName = columns[i];
                    lowerName = columnName.toLowerCase();
                    typeStr = ReflectUtils.getFieldTypeString(obj.getClass(), lowerName);
                    if(typeStr.equals("class java.lang.Short") || typeStr.equals("short")){
                        ReflectUtils.setFieldValue(obj, lowerName, c.getShort(c.getColumnIndex(columnName)));
                    }else if(typeStr.equals("class java.lang.Integer") || typeStr.equals("int")){
                        ReflectUtils.setFieldValue(obj, lowerName, c.getInt(c.getColumnIndex(columnName)));
                    }else if(typeStr.equals("class java.lang.Long") || typeStr.equals("long")){
                        ReflectUtils.setFieldValue(obj, lowerName, c.getLong(c.getColumnIndex(columnName)));
                    }else if(typeStr.equals("class java.lang.String") || typeStr.equals("String")){
                        ReflectUtils.setFieldValue(obj, lowerName, c.getString(c.getColumnIndex(columnName)));
                    }else if(typeStr.equals("class java.lang.Float") || typeStr.equals("float")){
                        ReflectUtils.setFieldValue(obj, lowerName, c.getFloat(c.getColumnIndex(columnName)));
                    }else if(typeStr.equals("class java.lang.Double") || typeStr.equals("double")){
                        ReflectUtils.setFieldValue(obj, lowerName, c.getDouble(c.getColumnIndex(columnName)));
                    }else{
                        ReflectUtils.setFieldValue(obj, lowerName, c.getString(c.getColumnIndex(columnName)));
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
        }
	}
	

}
