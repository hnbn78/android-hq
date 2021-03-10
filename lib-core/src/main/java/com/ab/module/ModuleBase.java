package com.ab.module;

import android.app.Application;

/**
 * 模组基类
 * Created by lee on 2017/9/12.
 */

public abstract class ModuleBase {
    /**
     * 对那些单独process的所有进程均初始化
     */
    private boolean isAllProcessInit;
    private String name;
    private int version;
    private String desc;
    
    public ModuleBase(String name, int version, String desc) {
        this.name = name;
        this.version = version;
        this.desc = desc;
    }
    
    public boolean isAllProcessInit() {
        return isAllProcessInit;
    }
    
    public void setAllProcessInit(boolean allProcessInit) {
        isAllProcessInit = allProcessInit;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    //模组初始化
    public abstract void onCreate(Application app);
    
    //create前配置
    public abstract void onPreConfig();
    
    //create前配置
    public abstract void onPostConfig();
    
    //模组销毁
    public abstract void onDestroy();
    
    
    @Override
    public String toString() {
        return "ModuleBase{" +
                "name='" + name + '\'' +
                ", version=" + version +
                ", desc='" + desc + '\'' +
                '}';
    }
}
