package com.desheng.base.model;

public class BonusData {

    /**
     * agentId : 10
     * userId : 14
     * agentBonusType : 0
     * agentPoint : null
     * userPoint : null
     * maxPoint : 40
     * minPoint : 0
     * subPoint : 5
     * agentPoints : [{"amount":0,"percent":30,"profit":1,"activity":10},{"amount":0,"percent":32,"profit":200000,"activity":10},{"amount":0,"percent":34,"profit":500000,"activity":10},{"amount":0,"percent":36,"profit":1000000,"activity":10},{"amount":0,"percent":38,"profit":1500000,"activity":10},{"amount":0,"percent":40,"profit":2000000,"activity":10},{"amount":0,"percent":42,"profit":5000000,"activity":10},{"amount":0,"percent":45,"profit":10000000,"activity":10}]
     * userPoints : [{"amount":0,"percent":20,"profit":0}]
     * haveProfitBonusPoint : false
     * canModify : false
     * agentMax : 40
     * agentMin : 30
     * checkLast : 0
     * agentCheckLast : 0
     */

    private int agentId;
    private int userId;
    private int agentBonusType;
    private String agentPoint;
    private String userPoint;
    private int maxPoint;
    private int minPoint;
    private int subPoint;
    private String agentPoints;
    private String userPoints;
    private boolean haveProfitBonusPoint;
    private boolean canModify;
    private int agentMax;
    private int agentMin;
    private int checkLast;
    private int agentCheckLast;

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAgentBonusType() {
        return agentBonusType;
    }

    public void setAgentBonusType(int agentBonusType) {
        this.agentBonusType = agentBonusType;
    }

    public String getAgentPoint() {
        return agentPoint;
    }

    public void setAgentPoint(String agentPoint) {
        this.agentPoint = agentPoint;
    }

    public String getUserPoint() {
        return userPoint;
    }

    public void setUserPoint(String userPoint) {
        this.userPoint = userPoint;
    }

    public int getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(int maxPoint) {
        this.maxPoint = maxPoint;
    }

    public int getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(int minPoint) {
        this.minPoint = minPoint;
    }

    public int getSubPoint() {
        return subPoint;
    }

    public void setSubPoint(int subPoint) {
        this.subPoint = subPoint;
    }

    public String getAgentPoints() {
        return agentPoints;
    }

    public void setAgentPoints(String agentPoints) {
        this.agentPoints = agentPoints;
    }

    public String getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(String userPoints) {
        this.userPoints = userPoints;
    }

    public boolean isHaveProfitBonusPoint() {
        return haveProfitBonusPoint;
    }

    public void setHaveProfitBonusPoint(boolean haveProfitBonusPoint) {
        this.haveProfitBonusPoint = haveProfitBonusPoint;
    }

    public boolean isCanModify() {
        return canModify;
    }

    public void setCanModify(boolean canModify) {
        this.canModify = canModify;
    }

    public int getAgentMax() {
        return agentMax;
    }

    public void setAgentMax(int agentMax) {
        this.agentMax = agentMax;
    }

    public int getAgentMin() {
        return agentMin;
    }

    public void setAgentMin(int agentMin) {
        this.agentMin = agentMin;
    }

    public int getCheckLast() {
        return checkLast;
    }

    public void setCheckLast(int checkLast) {
        this.checkLast = checkLast;
    }

    public int getAgentCheckLast() {
        return agentCheckLast;
    }

    public void setAgentCheckLast(int agentCheckLast) {
        this.agentCheckLast = agentCheckLast;
    }
}
