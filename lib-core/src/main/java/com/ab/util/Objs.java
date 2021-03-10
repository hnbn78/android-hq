package com.ab.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by lee on 2017/3/29 0029.
 */

public class Objs {
    
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T clone(T obj){
        T cloneObj = null;
        try {
            //写入字节流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream obs = new ObjectOutputStream(out);
            obs.writeObject(obj);
            obs.close();
            
            //分配内存，写入原始对象，生成新对象
            ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(ios);
            //返回生成的新对象
            cloneObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObj;
    }
    
    public static <T> boolean same(Object ob1, T ob2) {
        if (ob1 == null && ob2 == null) {
            return true;
        } else if(ob1 != null){
            return ob1.equals(ob2);
        } else if(ob2 != null){
            return ob2.equals(ob1);
        } else {
            return false;
        }
    }
    
    public static <T> ThreadLocal<T> wrapLocal(Class<T> clazz){
        return new ThreadLocal<T>();
    }
}
