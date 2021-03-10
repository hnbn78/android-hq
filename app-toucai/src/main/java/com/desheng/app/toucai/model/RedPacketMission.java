package com.desheng.app.toucai.model;

import java.util.List;

public class RedPacketMission {

    /**
     * activeTimeList : [{"endTime":"23","startTime":"0","week":"0"}]
     * checkType : 1
     * conditionList : []
     * endTime : 2018-12-31 23:59:59
     * frontCondition :
     * startTime : 2018-12-02 00:00:00
     * surplusAmount : -0.1
     * totalAmount : 5000.0
     * totalCount : 70
     */

    private int checkType;
    private String endTime;
    private String frontCondition;
    private String startTime;
    private double surplusAmount;
    private double totalAmount;
    private int totalCount;
    private List<ActiveTimeListBean> activeTimeList;
    private List<?> conditionList;

    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFrontCondition() {
        return frontCondition;
    }

    public void setFrontCondition(String frontCondition) {
        this.frontCondition = frontCondition;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public double getSurplusAmount() {
        return surplusAmount;
    }

    public void setSurplusAmount(double surplusAmount) {
        this.surplusAmount = surplusAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ActiveTimeListBean> getActiveTimeList() {
        return activeTimeList;
    }

    public void setActiveTimeList(List<ActiveTimeListBean> activeTimeList) {
        this.activeTimeList = activeTimeList;
    }

    public List<?> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<?> conditionList) {
        this.conditionList = conditionList;
    }

    public static class ActiveTimeListBean {
        /**
         * endTime : 23
         * startTime : 0
         * week : 0
         */

        private String endTime;
        private String startTime;
        private String week;

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }
    }
}
