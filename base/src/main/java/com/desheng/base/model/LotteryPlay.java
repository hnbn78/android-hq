package com.desheng.base.model;

import java.io.Serializable;

/**
 * Created by lee on 2018/3/12.
 */
public class LotteryPlay implements Serializable{
    /**
     * showName : 五星直选复式
     * name : 直选复式
     * lotteryCode : wxzhixfs
     */

    public String showName;
    public String name;
    public boolean isEnable = false;
    public String lotteryCode;
    public String category;
    
  
    public String getPlayId() {
        return lotteryCode;
    }

    @Override
    public String toString() {
        return "LotteryPlay{" +
                "showName='" + showName + '\'' +
                ", name='" + name + '\'' +
                ", isEnable=" + isEnable +
                ", lotteryCode='" + lotteryCode + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
