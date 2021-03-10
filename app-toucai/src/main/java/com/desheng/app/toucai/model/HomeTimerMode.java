package com.desheng.app.toucai.model;

public class HomeTimerMode {


    /**
     * cycle : 300
     * issueNo : 20181224-028
     * lotteryId : 11
     * startTime : 2018-12-24 10:29:30
     * stopTime : 2018-12-24 10:39:30
     * timer : 70
     */

    private int cycle;
    private String issueNo;
    private String lotteryId;
    private String startTime;
    private String stopTime;
    private int timer;

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public String getIssueNo() {
        return issueNo;
    }

    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
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

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}
