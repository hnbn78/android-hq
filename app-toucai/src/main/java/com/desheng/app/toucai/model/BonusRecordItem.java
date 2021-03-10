package com.desheng.app.toucai.model;

import java.util.List;

public class BonusRecordItem {
    /**
     * statistics : {"createTime":"总计","cnt":0,"realAmount":200}
     * pageStatistics : {"createTime":"小计","cnt":0,"realAmount":200}
     * totalCount : 22
     * list : [{"realAmount":200,"remitState":1,"createTime":"2018-10-22 18:33:27","remitTime":"2018-10-22 18:39:52","isSingleAward":1},{"realAmount":0,"remitState":3,"createTime":"2018-10-22 18:38:53","remitTime":"","isSingleAward":0}]
     */

    private StatisticsBean statistics;
    private PageStatisticsBean pageStatistics;
    private int totalCount;
    private List<ListBean> list;

    public StatisticsBean getStatistics() {
        return statistics;
    }

    public void setStatistics(StatisticsBean statistics) {
        this.statistics = statistics;
    }

    public PageStatisticsBean getPageStatistics() {
        return pageStatistics;
    }

    public void setPageStatistics(PageStatisticsBean pageStatistics) {
        this.pageStatistics = pageStatistics;
    }

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

    public static class StatisticsBean {
        /**
         * createTime : 总计
         * cnt : 0
         * realAmount : 200
         */

        private String createTime;
        private int cnt;
        private double realAmount;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getCnt() {
            return cnt;
        }

        public void setCnt(int cnt) {
            this.cnt = cnt;
        }

        public double getRealAmount() {
            return realAmount;
        }

        public void setRealAmount(double realAmount) {
            this.realAmount = realAmount;
        }
    }

    public static class PageStatisticsBean {
        /**
         * createTime : 小计
         * cnt : 0
         * realAmount : 200
         */

        private String createTime;
        private int cnt;
        private double realAmount;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getCnt() {
            return cnt;
        }

        public void setCnt(int cnt) {
            this.cnt = cnt;
        }

        public double getRealAmount() {
            return realAmount;
        }

        public void setRealAmount(double realAmount) {
            this.realAmount = realAmount;
        }
    }

    public static class ListBean {
        /**
         * realAmount : 200
         * remitState : 1
         * createTime : 2018-10-22 18:33:27
         * remitTime : 2018-10-22 18:39:52
         * isSingleAward : 1
         */

        private double realAmount;
        private int remitState;
        private String createTime;
        private String remitTime;
        private int isSingleAward;

        public double getRealAmount() {
            return realAmount;
        }

        public void setRealAmount(double realAmount) {
            this.realAmount = realAmount;
        }

        public int getRemitState() {
            return remitState;
        }

        public void setRemitState(int remitState) {
            this.remitState = remitState;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getRemitTime() {
            return remitTime;
        }

        public void setRemitTime(String remitTime) {
            this.remitTime = remitTime;
        }

        public int getIsSingleAward() {
            return isSingleAward;
        }

        public void setIsSingleAward(int isSingleAward) {
            this.isSingleAward = isSingleAward;
        }
    }
}
