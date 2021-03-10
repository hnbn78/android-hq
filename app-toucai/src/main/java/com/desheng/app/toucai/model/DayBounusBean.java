package com.desheng.app.toucai.model;

import java.util.List;

public class DayBounusBean {

    private int totalCount;
    private List<ListBean> list;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * activityTitle : 金烽Ⅲ3
         * award : 2149.2
         * awardTime : 2020-11-01 01:00
         * createTime : 2020-11-01 01:00
         * drawStatusStr : 已发放
         * endTime : 2020-10-31
         * orderAmount : 376222.0
         * profitAmount : 71640.0011
         * remitStateStr : 不需要审核
         * roundNo : 18
         * startTime : 2020-10-31
         * title : 盈亏量分红
         */

        private String activityTitle;
        private double award;
        private String awardTime;
        private String createTime;
        private String drawStatusStr;
        private String endTime;
        private double orderAmount;
        private double profitAmount;
        private String remitStateStr;
        private String roundNo;
        private String startTime;
        private String title;

        public String getActivityTitle() {
            return activityTitle;
        }

        public void setActivityTitle(String activityTitle) {
            this.activityTitle = activityTitle;
        }

        public double getAward() {
            return award;
        }

        public void setAward(double award) {
            this.award = award;
        }

        public String getAwardTime() {
            return awardTime;
        }

        public void setAwardTime(String awardTime) {
            this.awardTime = awardTime;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getDrawStatusStr() {
            return drawStatusStr;
        }

        public void setDrawStatusStr(String drawStatusStr) {
            this.drawStatusStr = drawStatusStr;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public double getOrderAmount() {
            return orderAmount;
        }

        public void setOrderAmount(double orderAmount) {
            this.orderAmount = orderAmount;
        }

        public double getProfitAmount() {
            return profitAmount;
        }

        public void setProfitAmount(double profitAmount) {
            this.profitAmount = profitAmount;
        }

        public String getRemitStateStr() {
            return remitStateStr;
        }

        public void setRemitStateStr(String remitStateStr) {
            this.remitStateStr = remitStateStr;
        }

        public String getRoundNo() {
            return roundNo;
        }

        public void setRoundNo(String roundNo) {
            this.roundNo = roundNo;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
