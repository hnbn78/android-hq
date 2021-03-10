package com.desheng.app.toucai.model;

public class ZhongjiangMode {

    /**
     * amount : 10
     * bonus : 11.975
     * issue : 20181224-2510
     * lottery : 多伦多30秒
     * lotteryCode : TG30SSC
     * lotteryId : 46
     * method : 趣味一帆风顺
     * orderId : 201812240k1ZACN000a0001
     * type : 0
     */

    private double amount;
    private double bonus;
    private String issue;
    private String lottery;
    private String lotteryCode;
    private int lotteryId;
    private String method;
    private String orderId;
    private int type;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
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

    public String getLotteryCode() {
        return lotteryCode;
    }

    public void setLotteryCode(String lotteryCode) {
        this.lotteryCode = lotteryCode;
    }

    public int getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
