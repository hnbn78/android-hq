package com.desheng.app.toucai.model;

import java.util.List;

public class LotteryProfitLoss {

    /**
     * recordMap : {"awardAmountSum":238.2445,"profitAmountSum":9094.7555,"confirmAmountSum":9333}
     * recordList : [{"awardAmountSum":148.5245,"profitAmountSum":8263.4755,"confirmAmountSum":8412,"dataAccountFor":0.9086,"lotteryName":"重庆时时彩"},{"awardAmountSum":0,"profitAmountSum":575,"confirmAmountSum":575,"dataAccountFor":0.0632,"lotteryName":"广东11选5"},{"awardAmountSum":64.68,"profitAmountSum":172.32,"confirmAmountSum":237,"dataAccountFor":0.0189,"lotteryName":"北京PK10"},{"awardAmountSum":0,"profitAmountSum":40,"confirmAmountSum":40,"dataAccountFor":0.0044,"lotteryName":"江苏快3"},{"awardAmountSum":0,"profitAmountSum":24,"confirmAmountSum":24,"dataAccountFor":0.0026,"lotteryName":"排列三/排列五"},{"awardAmountSum":9.8,"profitAmountSum":11.2,"confirmAmountSum":21,"dataAccountFor":0.0012,"lotteryName":"新疆时时彩"},{"awardAmountSum":0,"profitAmountSum":6,"confirmAmountSum":6,"dataAccountFor":7.0E-4,"lotteryName":"新加坡2分彩"},{"awardAmountSum":5.44,"profitAmountSum":1.56,"confirmAmountSum":7,"dataAccountFor":2.0E-4,"lotteryName":"天津时时彩"},{"awardAmountSum":0,"profitAmountSum":1,"confirmAmountSum":1,"dataAccountFor":1.0E-4,"lotteryName":"吉林快3"},{"awardAmountSum":7.84,"profitAmountSum":0.16,"confirmAmountSum":8,"dataAccountFor":0,"lotteryName":"经典东京1.5分彩"},{"awardAmountSum":1.96,"profitAmountSum":0.04,"confirmAmountSum":2,"dataAccountFor":0,"lotteryName":"多伦多30秒"}]
     * recordCount : 11
     * dataAccountFor : {"awardAmountSum":1.96,"profitAmountSum":0.04,"confirmAmountSum":2,"dataAccountFor":"0.0001","lotteryName":"其他"}
     */

    private RecordMapBean recordMap;
    private int recordCount;
    private DataAccountForBean dataAccountFor;
    private List<RecordListBean> recordList;

    public RecordMapBean getRecordMap() {
        return recordMap;
    }

    public void setRecordMap(RecordMapBean recordMap) {
        this.recordMap = recordMap;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public DataAccountForBean getDataAccountFor() {
        return dataAccountFor;
    }

    public void setDataAccountFor(DataAccountForBean dataAccountFor) {
        this.dataAccountFor = dataAccountFor;
    }

    public List<RecordListBean> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<RecordListBean> recordList) {
        this.recordList = recordList;
    }

    public static class RecordMapBean {
        /**
         * awardAmountSum : 238.2445
         * profitAmountSum : 9094.7555
         * confirmAmountSum : 9333
         */

        private double awardAmountSum;
        private double profitAmountSum;
        private int confirmAmountSum;
        private double profitAmount;

        public double getProfitAmount() {
            return profitAmount;
        }

        public void setProfitAmount(double profitAmount) {
            this.profitAmount = profitAmount;
        }

        public double getAwardAmountSum() {
            return awardAmountSum;
        }

        public void setAwardAmountSum(double awardAmountSum) {
            this.awardAmountSum = awardAmountSum;
        }

        public double getProfitAmountSum() {
            return profitAmountSum;
        }

        public void setProfitAmountSum(double profitAmountSum) {
            this.profitAmountSum = profitAmountSum;
        }

        public int getConfirmAmountSum() {
            return confirmAmountSum;
        }

        public void setConfirmAmountSum(int confirmAmountSum) {
            this.confirmAmountSum = confirmAmountSum;
        }
    }

    public static class DataAccountForBean {
        /**
         * awardAmountSum : 1.96
         * profitAmountSum : 0.04
         * confirmAmountSum : 2
         * dataAccountFor : 0.0001
         * lotteryName : 其他
         */

        private double awardAmountSum;
        private double profitAmountSum;
        private int confirmAmountSum;
        private String dataAccountFor;
        private String lotteryName;

        public double getAwardAmountSum() {
            return awardAmountSum;
        }

        public void setAwardAmountSum(double awardAmountSum) {
            this.awardAmountSum = awardAmountSum;
        }

        public double getProfitAmountSum() {
            return profitAmountSum;
        }

        public void setProfitAmountSum(double profitAmountSum) {
            this.profitAmountSum = profitAmountSum;
        }

        public int getConfirmAmountSum() {
            return confirmAmountSum;
        }

        public void setConfirmAmountSum(int confirmAmountSum) {
            this.confirmAmountSum = confirmAmountSum;
        }

        public String getDataAccountFor() {
            return dataAccountFor;
        }

        public void setDataAccountFor(String dataAccountFor) {
            this.dataAccountFor = dataAccountFor;
        }

        public String getLotteryName() {
            return lotteryName;
        }

        public void setLotteryName(String lotteryName) {
            this.lotteryName = lotteryName;
        }
    }

    public static class RecordListBean {
        /**
         * awardAmountSum : 148.5245
         * profitAmountSum : 8263.4755
         * confirmAmountSum : 8412
         * dataAccountFor : 0.9086
         * lotteryName : 重庆时时彩
         */

        private double awardAmountSum;
        private double profitAmountSum;
        private int confirmAmountSum;
        private double dataAccountFor;
        private String lotteryName;

        public double getAwardAmountSum() {
            return awardAmountSum;
        }

        public void setAwardAmountSum(double awardAmountSum) {
            this.awardAmountSum = awardAmountSum;
        }

        public double getProfitAmountSum() {
            return profitAmountSum;
        }

        public void setProfitAmountSum(double profitAmountSum) {
            this.profitAmountSum = profitAmountSum;
        }

        public int getConfirmAmountSum() {
            return confirmAmountSum;
        }

        public void setConfirmAmountSum(int confirmAmountSum) {
            this.confirmAmountSum = confirmAmountSum;
        }

        public double getDataAccountFor() {
            return dataAccountFor;
        }

        public void setDataAccountFor(double dataAccountFor) {
            this.dataAccountFor = dataAccountFor;
        }

        public String getLotteryName() {
            return lotteryName;
        }

        public void setLotteryName(String lotteryName) {
            this.lotteryName = lotteryName;
        }
    }
}
