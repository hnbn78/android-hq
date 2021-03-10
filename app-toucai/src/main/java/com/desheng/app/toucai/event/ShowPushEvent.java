package com.desheng.app.toucai.event;

import com.desheng.app.toucai.global.AppUmengPushHandler;

import java.io.Serializable;

/**
 * 玩法页面投注事件
 * Created by lee on 2018/3/19.
 */
public class ShowPushEvent implements Serializable {
    public AppUmengPushHandler.CustomMsg msg;
    
    public ShowPushEvent(AppUmengPushHandler.CustomMsg msg) {
        this.msg = msg;
    }
}
