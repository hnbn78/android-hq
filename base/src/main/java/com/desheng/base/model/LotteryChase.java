package com.desheng.base.model;

/**
 * Created by lee on 2018/3/25.
 */

public class LotteryChase {
    
    /**
     * issue : 20180325-114
     * startTime : 2018-03-25 23:24:30
     * stopTime : 2018-03-25 23:29:30
     * openTime : 2018-03-25 23:30:00
     * surplusTime : 29033
     */
    
    private String issue;
    private String startTime;
    private String stopTime;
    private String openTime;
    private int surplusTime;
    private int multi = 0;
    private double money = 0;
    
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
    
    public int getSurplusTime() {
        return surplusTime;
    }
    
    public void setSurplusTime(int surplusTime) {
        this.surplusTime = surplusTime;
    }
    
    public int getMulti() {
        return multi;
    }
    
    public void setMulti(int multi) {
        this.multi = multi;
    }
    
    public double getMoney() {
        return money;
    }
    
    public void setMoney(double money) {
        this.money = money;
    }
    
    @Override
    public String toString() {
        return "LotteryChase{" +
                "issue='" + issue + '\'' +
                ", startTime='" + startTime + '\'' +
                ", stopTime='" + stopTime + '\'' +
                ", openTime='" + openTime + '\'' +
                ", surplusTime=" + surplusTime +
                ", multi=" + multi +
                ", money=" + money +
                '}';
    }
}
