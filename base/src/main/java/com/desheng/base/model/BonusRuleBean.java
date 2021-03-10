package com.desheng.base.model;


public class BonusRuleBean {

    /**
     * activeCount : 5
     * checkLast : true
     * profitAmount : -3541850.0
     * startDay : 2019-10-01
     * deservedBonus : 1239647.5000
     * percent : 0.35
     * yetDistributBonus : 1419137.5000
     * alreadyDistributBonus : 1239647.5000
     * shouleDistributBonus : 2658785.0000
     * endDay : 2019-10-15
     * receivedBonus : 1239647.5000
     * mixPercents : [{"amount": 0,"percent": 25,"profit": 0, "activity": 0},{"amount": 750000,"percent": 30,"profit": 1000000, "activity": 2},{"amount": 2250000,"percent": 35,"profit": 3000000, "activity": 5},{"amount": 7500000,"percent": 40,"profit": 5000000, "activity": 10},{"amount": 22500000,"percent": 42,"profit": 10000000, "activity": 10}]
     * bonusType : 0
     */
    private int activeCount;
    private boolean checkLast;
    private double profitAmount;
    private String startDay;
    private String deservedBonus;
    private String percent;
    private String yetDistributBonus;
    private String alreadyDistributBonus;
    private String shouleDistributBonus;
    private String endDay;
    private String receivedBonus;
    private String mixPercents;
    private int bonusType;

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public void setCheckLast(boolean checkLast) {
        this.checkLast = checkLast;
    }

    public void setProfitAmount(double profitAmount) {
        this.profitAmount = profitAmount;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public void setDeservedBonus(String deservedBonus) {
        this.deservedBonus = deservedBonus;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public void setYetDistributBonus(String yetDistributBonus) {
        this.yetDistributBonus = yetDistributBonus;
    }

    public void setAlreadyDistributBonus(String alreadyDistributBonus) {
        this.alreadyDistributBonus = alreadyDistributBonus;
    }

    public void setShouleDistributBonus(String shouleDistributBonus) {
        this.shouleDistributBonus = shouleDistributBonus;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public void setReceivedBonus(String receivedBonus) {
        this.receivedBonus = receivedBonus;
    }

    public void setMixPercents(String mixPercents) {
        this.mixPercents = mixPercents;
    }

    public void setBonusType(int bonusType) {
        this.bonusType = bonusType;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public boolean isCheckLast() {
        return checkLast;
    }

    public double getProfitAmount() {
        return profitAmount;
    }

    public String getStartDay() {
        return startDay;
    }

    public String getDeservedBonus() {
        return deservedBonus;
    }

    public String getPercent() {
        return percent;
    }

    public String getYetDistributBonus() {
        return yetDistributBonus;
    }

    public String getAlreadyDistributBonus() {
        return alreadyDistributBonus;
    }

    public String getShouleDistributBonus() {
        return shouleDistributBonus;
    }

    public String getEndDay() {
        return endDay;
    }

    public String getReceivedBonus() {
        return receivedBonus;
    }

    public String getMixPercents() {
        return mixPercents;
    }

    public int getBonusType() {
        return bonusType;
    }
}
