package com.desheng.app.toucai.model;

public class TeamBetHistoryItemBean {

    /**
     * amount : 5.0
     * awardMoney : 2.395
     * orderTime : 2019-03-22 10:52:04.0
     * method : 趣味一帆风顺
     * orderItemId : 201903220k1ZAnv09TM0001
     * statusRemark : 未开奖
     * lottery : 多伦多30秒
     * issueNo : 20190322-1185
     * content : 0,2,4,6,8
     * point : 0.0242
     */

    private double amount;
    private double awardMoney;
    private String orderTime;
    private String method;
    private String orderItemId;
    private int status;
    private String lottery;
    private String issueNo;
    private String content;
    private String point;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAwardMoney() {
        return awardMoney;
    }

    public void setAwardMoney(double awardMoney) {
        this.awardMoney = awardMoney;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLottery() {
        return lottery;
    }

    public void setLottery(String lottery) {
        this.lottery = lottery;
    }

    public String getIssueNo() {
        return issueNo;
    }

    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
