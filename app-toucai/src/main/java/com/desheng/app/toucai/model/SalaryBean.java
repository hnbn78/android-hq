package com.desheng.app.toucai.model;

public class SalaryBean {

    private String day;
    private int userId;
    private int fromUserId;
    private double teamAmount;
    private double point;
    private double bonus;
    private double wholeBonus;
    private int status;
    private String statusStr;
    private String createTime;
    private String updateTime;
    private String userName;
    private boolean summary;
    private double teamAmountSum;
    private double bonusSum;


    public double getTeamAmountSum() {
        return teamAmountSum;
    }

    public double getBonusSum() {
        return bonusSum;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public double getTeamAmount() {
        return teamAmount;
    }

    public void setTeamAmount(double teamAmount) {
        this.teamAmount = teamAmount;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public double getWholeBonus() {
        return wholeBonus;
    }

    public void setWholeBonus(double wholeBonus) {
        this.wholeBonus = wholeBonus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(boolean summary) {
        this.summary = summary;
    }
}
