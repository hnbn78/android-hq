package com.desheng.base.model;

import java.math.BigDecimal;

public class BonusBean {

    /**
     * agentId : 10
     * userId : 1226
     * agentMaxPoint : 0.018
     * agentMinPoint : 0.018
     * userMaxPoint : null
     * userMinPoint : null
     * setMaxPoint : 0.016
     * setMinPoint : 0.001
     * canModify : true
     * type : 3
     * mixPercent : null
     */

    private int agentId;
    private int userId;
    private BigDecimal agentMaxPoint;
    private BigDecimal agentMinPoint;
    private BigDecimal userMaxPoint;
    private BigDecimal userMinPoint;
    private BigDecimal setMaxPoint;
    private BigDecimal setMinPoint;
    private boolean canModify;
    private int type;
    private double mixPercent;

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

    public BigDecimal getAgentMaxPoint() {
        return agentMaxPoint;
    }

    public void setAgentMaxPoint(BigDecimal agentMaxPoint) {
        this.agentMaxPoint = agentMaxPoint;
    }

    public BigDecimal getAgentMinPoint() {
        return agentMinPoint;
    }

    public void setAgentMinPoint(BigDecimal agentMinPoint) {
        this.agentMinPoint = agentMinPoint;
    }

    public BigDecimal getUserMaxPoint() {
        return userMaxPoint;
    }

    public void setUserMaxPoint(BigDecimal userMaxPoint) {
        this.userMaxPoint = userMaxPoint;
    }

    public BigDecimal getUserMinPoint() {
        return userMinPoint;
    }

    public void setUserMinPoint(BigDecimal userMinPoint) {
        this.userMinPoint = userMinPoint;
    }

    public BigDecimal getSetMaxPoint() {
        return setMaxPoint;
    }

    public void setSetMaxPoint(BigDecimal setMaxPoint) {
        this.setMaxPoint = setMaxPoint;
    }

    public BigDecimal getSetMinPoint() {
        return setMinPoint;
    }

    public void setSetMinPoint(BigDecimal setMinPoint) {
        this.setMinPoint = setMinPoint;
    }

    public boolean isCanModify() {
        return canModify;
    }

    public void setCanModify(boolean canModify) {
        this.canModify = canModify;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getMixPercent() {
        return mixPercent;
    }

    public void setMixPercent(double mixPercent) {
        this.mixPercent = mixPercent;
    }
}
