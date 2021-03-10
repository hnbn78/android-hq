package com.desheng.base.model;

/**
 * Created by lee on 2018/3/15.
 */

public class LotteryOpenHistory {
    
    /**
     * code : 5,6,9,7,9
     * issue : 20180315-060
     * lottery : CQSSC
     * time : 1521100865
     */
    //开奖号码
    private String code;
    //奖期
    private String issue;
    //菜种编码
    private String lottery;
    //开奖时间
    private long time;
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getIssue() {
        return issue;
    }
    
    public void setIssue(String issue) {
        this.issue = issue;
    }
    
    public String getLottery() {
        return lottery;
    }
    
    public void setLottery(String lottery) {
        this.lottery = lottery;
    }
    
    public long getTime() {
        return time;
    }
    
    public void setTime(long time) {
        this.time = time;
    }
}
