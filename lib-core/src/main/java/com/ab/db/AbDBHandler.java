package com.ab.db;

import java.util.List;

public interface AbDBHandler {
    /**
     * 返回当前数据库版本
     * @return
     */
    int getCurrentDBVersion();
    
    /**
     * 返回所有需要建表的class
     * @return
     */
    List<Class> getTables();
    
    /**
     * 根据版本变更, 返回数据库升级脚本语句列表
     * @param oldVersion
     * @param newVersion
     * @return
     */
    List<String> getUpdateStatement(int oldVersion, int newVersion);
    
}
