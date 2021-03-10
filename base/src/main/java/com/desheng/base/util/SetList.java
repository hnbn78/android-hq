package com.desheng.base.util;

import java.util.LinkedList;

/**
 * 自定义一个 有顺序（按照放入顺序） 不重复的集合
 * @param <T>
 */
public class SetList<T> extends LinkedList<T> {
    @Override
    public boolean add(T object) {
        if (size() == 0) {
            return super.add(object);
        } else {
            int count = 0;
            for (T t : this) {
                if (t.equals(object)) {
                    count++;
                    break;
                }
            }
            if (count == 0) {
                return super.add(object);
            } else if (count == 1) {
                super.remove(object);
                return super.add(object);//先删除再放入 这样保证了用户最新访问数据的顺序
            } else {
                return false;
            }
        }
    }
}
