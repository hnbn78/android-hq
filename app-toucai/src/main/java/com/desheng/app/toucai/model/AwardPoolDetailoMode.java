package com.desheng.app.toucai.model;

public class AwardPoolDetailoMode {

    /**
     * activityIssueNo : 2019001
     * lotteryIssueNo : 20190115-120
     * nextAmountSub : 50.0
     * poolBonus : 0.0
     * takeCount : 0
     * takeType : 1
     * timeSub : 42192326
     */

    private String activityIssueNo;
    private String lotteryIssueNo;
    private double nextAmountSub;
    private double poolBonus;
    private int takeCount;
    private int takeType;
    private int timeSub;

    public String getActivityIssueNo() {
        return activityIssueNo;
    }

    public void setActivityIssueNo(String activityIssueNo) {
        this.activityIssueNo = activityIssueNo;
    }

    public String getLotteryIssueNo() {
        return lotteryIssueNo;
    }

    public void setLotteryIssueNo(String lotteryIssueNo) {
        this.lotteryIssueNo = lotteryIssueNo;
    }

    public double getNextAmountSub() {
        return nextAmountSub;
    }

    public void setNextAmountSub(double nextAmountSub) {
        this.nextAmountSub = nextAmountSub;
    }

    public double getPoolBonus() {
        return poolBonus;
    }

    public void setPoolBonus(double poolBonus) {
        this.poolBonus = poolBonus;
    }

    public int getTakeCount() {
        return takeCount;
    }

    public void setTakeCount(int takeCount) {
        this.takeCount = takeCount;
    }

    public int getTakeType() {
        return takeType;
    }

    public void setTakeType(int takeType) {
        this.takeType = takeType;
    }

    public int getTimeSub() {
        return timeSub;
    }

    public void setTimeSub(int timeSub) {
        this.timeSub = timeSub;
    }
}
