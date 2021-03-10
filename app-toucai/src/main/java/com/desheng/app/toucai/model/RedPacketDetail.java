package com.desheng.app.toucai.model;

import java.util.List;

public class RedPacketDetail {

    /**
     * times : 3
     * totalPrizeAmount : 46.21
     * prizeList : [{"award":16.86,"createTime":1544775272000},{"award":14.48,"createTime":1544775272000},{"award":14.87,"createTime":1544775372000}]
     */

    private int times;
    private double totalPrizeAmount;
    private List<PrizeListBean> prizeList;

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public double getTotalPrizeAmount() {
        return totalPrizeAmount;
    }

    public void setTotalPrizeAmount(double totalPrizeAmount) {
        this.totalPrizeAmount = totalPrizeAmount;
    }

    public List<PrizeListBean> getPrizeList() {
        return prizeList;
    }

    public void setPrizeList(List<PrizeListBean> prizeList) {
        this.prizeList = prizeList;
    }

    public static class PrizeListBean {
        /**
         * award : 16.86
         * createTime : 1544775272000
         */

        private double award;
        private long createTime;

        public double getAward() {
            return award;
        }

        public void setAward(double award) {
            this.award = award;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
