package com.ab.util;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ReflectUtils {
	@SuppressWarnings("rawtypes")
	public static Class getClass(Type type, int i) {     
        if (type instanceof ParameterizedType) { // 处理泛型类型     
            return getGenericClass((ParameterizedType) type, i);     
        } else if (type instanceof TypeVariable) {     
            return (Class) getClass(((TypeVariable) type).getBounds()[0], 0); // 处理泛型擦拭对象     
        } else {// class本身也是type，强制转型     
            return (Class) type;     
        }     
    }     
    
    @SuppressWarnings("rawtypes")
	public static Class getGenericClass(ParameterizedType parameterizedType, int i) {     
        Object genericClass = parameterizedType.getActualTypeArguments()[i];     
        if (genericClass instanceof ParameterizedType) { // 处理多级泛型     
            return (Class) ((ParameterizedType) genericClass).getRawType();     
        } else if (genericClass instanceof GenericArrayType) { // 处理数组泛型     
            return (Class) ((GenericArrayType) genericClass).getGenericComponentType();     
        } else if (genericClass instanceof TypeVariable) { // 处理泛型擦拭对象     
            return (Class) getClass(((TypeVariable) genericClass).getBounds()[0], 0);     
        } else {     
            return (Class) genericClass;     
        }     
    }  
    
    /**
     * 获取某属性的类型字符串
     */
    @SuppressWarnings("rawtypes")
	public static String getFieldTypeString(Class clazz, String fieldName) throws Exception{
		Field field = clazz.getField(fieldName);
        String type = field.getGenericType().toString();
        return type;
    }

    
    /**
     * 只设置原始类型的值. 会有当value为null时, 对象中的属性也为null
     * @param obj
     * @param value
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception{
    		Field field = obj.getClass().getField(fieldName);
            String type = field.getGenericType().toString();
            try {
	            if(type.equals("class java.lang.Byte") || type.equals("byte")){
					field.set(obj, (Byte)value);
	            }else if(type.equals("class java.lang.Short") || type.equals("short")){
	            	field.set(obj, (Short)value);
	            }else if(type.equals("class java.lang.Integer") || type.equals("int")){
	            	field.set(obj, (Integer)value);
	            }else if(type.equals("class java.lang.Long") || type.equals("long")){
	            	field.set(obj, (Long)value);
	            }else if(type.equals("class java.lang.Float") || type.equals("float")){
	            	field.set(obj, (Float)value);
	            }else if(type.equals("class java.lang.Double") || type.equals("double")){
	            	field.set(obj, (Double)value);
	            }else if(type.equals("class java.lang.Boolean") || type.equals("boolean")){
	            	field.set(obj, (Boolean)value);
	            }else if(type.equals("class java.lang.Character") || type.equals("char")){
	            	field.set(obj, (Character) value);
	            }else if(type.equals("class java.lang.String")){
	            	field.set(obj, value.toString());
	            }else{
	            	//Debug.log(Constants.PETOTO_DEBUG, "setFieldValue 找不到类型!");
	            }
            } catch (IllegalAccessException e) {
            	//Debug.log(Constants.PETOTO_DEBUG, "setFieldValue 设置属性异常!");
				e.printStackTrace();
				throw new RuntimeException("setFieldValue 设置属性异常!");
			} catch (IllegalArgumentException e) {
				//Debug.log(Constants.PETOTO_DEBUG, "setFieldValue 设置属性异常!");
				e.printStackTrace();
				throw new RuntimeException("setFieldValue 设置属性异常!");
			} catch (Exception e){
				//此属性保持null
			}
     
        }
   
}
