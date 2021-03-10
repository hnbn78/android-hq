package com.desheng.app.toucai.model;

public class RedPacketGetDetail {


    /**
     * activityId : 137
     * surplusCount : 0
     * bonusAmount : 1.65
     * levelId : 3
     * id : a36f0e50-ba48-425b-b35b-330fe8f599c7
     */

    private int activityId;
    private int surplusCount;
    private double bonusAmount;
    private int levelId;
    private String id;

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getSurplusCount() {
        return surplusCount;
    }

    public void setSurplusCount(int surplusCount) {
        this.surplusCount = surplusCount;
    }

    public double getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(double bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
