package com.desheng.app.toucai.model;

/**
 * Created by lee on 2018/3/15.
 */

public class LotteryOpenNext {
    
    //奖期
    private String issue;
    //菜种编码
    private String startTime; //"2017-08-14 14:39:30", //售卖开始时间
    private String stopTime; //2017-08-14 14:49:30", //封单时间
    private String openTime; //2017-08-14 14:50:00", //官方预计开奖时间
    private long surplusTime; //318  //购彩剩余时间 单位：秒
    
    public String getIssue() {
        return issue;
    }
    
    public void setIssue(String issue) {
        this.issue = issue;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getStopTime() {
        return stopTime;
    }
    
    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }
    
    public String getOpenTime() {
        return openTime;
    }
    
    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }
    
    public long getSurplusTime() {
        return surplusTime;
    }
    
    public void setSurplusTime(long surplusTime) {
        this.surplusTime = surplusTime;
    }
}
