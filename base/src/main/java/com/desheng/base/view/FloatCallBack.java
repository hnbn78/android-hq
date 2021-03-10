package com.desheng.base.view;


import android.app.Activity;

/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:暴露一些与悬浮窗交互的接口
 */
public interface FloatCallBack {
    void guideUser(int type);
    
    void show(Activity act);
    
    void hide();
    
    void addObtainNumer();

    void setObtainNumber(int number);
}
