package com.desheng.base.model;

public class SalaryBean {

    /**
     * agentId : 272
     * userId : 273
     * setMaxPoint : 0.005
     * setMinPoint : 0.001
     * mixPercent : [{'amount':1,'activity':1,'point':0.001}]
     * haveSalaryPoint : true
     */

    private int agentId;
    private int userId;
    private double setMaxPoint;
    private double setMinPoint;
    private String mixPercent;
    private boolean haveSalaryPoint;
    /**
     * setMaxPoint : 0.005
     * setMinPoint : 0.001
     */
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

    public double getSetMaxPoint() {
        return setMaxPoint;
    }

    public void setSetMaxPoint(double setMaxPoint) {
        this.setMaxPoint = setMaxPoint;
    }

    public double getSetMinPoint() {
        return setMinPoint;
    }

    public void setSetMinPoint(double setMinPoint) {
        this.setMinPoint = setMinPoint;
    }

    public String getMixPercent() {
        return mixPercent;
    }

    public void setMixPercent(String mixPercent) {
        this.mixPercent = mixPercent;
    }

    public boolean isHaveSalaryPoint() {
        return haveSalaryPoint;
    }

    public void setHaveSalaryPoint(boolean haveSalaryPoint) {
        this.haveSalaryPoint = haveSalaryPoint;
    }

}
