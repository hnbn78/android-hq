package com.desheng.base.model;

import java.util.List;

public class XjBonusBean {

    /**
     * totalCount : 10
     * list : [{"userId":198,"userName":"testom6","fromUserId":197,"roundId":17,"startDay":"2018-06-29","endDay":"2018-06-29","teamAmount":0,"profitAmount":0.01,"lastProfitLoss":0,"realProfitAmount":0.01,"percent":0,"bonus":0,"status":3,"profitLoss":0,"statusStr":"已发放","summary":false},{"userId":198,"userName":"testom6","fromUserId":197,"roundId":16,"startDay":"2018-06-28","endDay":"2018-06-28","teamAmount":1,"profitAmount":0.966,"lastProfitLoss":0,"realProfitAmount":0.966,"percent":0,"bonus":0,"status":3,"profitLoss":0,"statusStr":"已发放","summary":false},{"userId":198,"userName":"testom6","fromUserId":197,"roundId":15,"startDay":"2018-06-27","endDay":"2018-06-27","teamAmount":0,"profitAmount":2.14,"lastProfitLoss":0,"realProfitAmount":2.14,"percent":0,"bonus":0,"status":3,"profitLoss":0,"statusStr":"已发放","summary":false},{"userId":198,"userName":"testom6","fromUserId":197,"roundId":14,"startDay":"2018-06-26","endDay":"2018-06-26","teamAmount":214,"profitAmount":-7.57,"lastProfitLoss":0,"realProfitAmount":-7.57,"percent":0.39,"bonus":2.9523,"status":0,"profitLoss":0,"statusStr":"未发放","summary":false},{"userId":198,"userName":"testom6","fromUserId":197,"roundId":13,"startDay":"2018-06-25","endDay":"2018-06-25","teamAmount":0,"profitAmount":0,"lastProfitLoss":0,"realProfitAmount":0,"percent":0.39,"bonus":0,"status":3,"profitLoss":0,"statusStr":"已发放","summary":false},{"userId":198,"userName":"testom6","fromUserId":197,"roundId":12,"startDay":"2018-06-24","endDay":"2018-06-24","teamAmount":0,"profitAmount":0,"lastProfitLoss":0,"realProfitAmount":0,"percent":0.39,"bonus":0,"status":3,"profitLoss":0,"statusStr":"已发放","summary":false},{"userId":198,"userName":"testom6","fromUserId":197,"roundId":11,"startDay":"2018-06-23","endDay":"2018-06-23","teamAmount":0,"profitAmount":0,"lastProfitLoss":0,"realProfitAmount":0,"percent":0.39,"bonus":0,"status":3,"profitLoss":0,"statusStr":"已发放","summary":false},{"userId":198,"userName":"testom6","fromUserId":197,"roundId":10,"startDay":"2018-06-22","endDay":"2018-06-22","teamAmount":0,"profitAmount":0,"lastProfitLoss":0,"realProfitAmount":0,"percent":0.39,"bonus":0,"status":3,"profitLoss":0,"statusStr":"已发放","summary":false},{"userId":198,"userName":"testom6","fromUserId":197,"roundId":9,"startDay":"2018-06-21","endDay":"2018-06-21","teamAmount":0,"profitAmount":1,"lastProfitLoss":0,"realProfitAmount":1,"percent":0,"bonus":0,"status":3,"profitLoss":0,"statusStr":"已发放","summary":false},{"userId":198,"userName":"testom6","fromUserId":197,"roundId":8,"startDay":"2018-06-20","endDay":"2018-06-20","teamAmount":100,"profitAmount":-2,"lastProfitLoss":0,"realProfitAmount":-2,"percent":0.39,"bonus":0.78,"status":2,"payTime":"2018-06-25 23:30:15","profitLoss":0,"statusStr":"已发放","summary":false},{"profitAmount":-5.454,"bonusSum":3.7323,"summary":true}]
     */

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
         * userId : 198
         * userName : testom6
         * fromUserId : 197
         * roundId : 17
         * startDay : 2018-06-29
         * endDay : 2018-06-29
         * teamAmount : 0.0
         * profitAmount : 0.01
         * lastProfitLoss : 0.0
         * realProfitAmount : 0.01
         * percent : 0.0
         * bonus : 0.0
         * status : 3
         * profitLoss : 0.0
         * statusStr : 已发放
         * summary : false
         * payTime : 2018-06-25 23:30:15
         * bonusSum : 3.7323
         */

        private int userId;
        private String userName;
        private int fromUserId;
        private int roundId;
        private String startDay;
        private String endDay;
        private double teamAmount;
        private double profitAmount;
        private double lastProfitLoss;
        private double realProfitAmount;
        private double percent;
        private double bonus;
        private int status;
        private double profitLoss;
        private String statusStr;
        private boolean summary;
        private String payTime;
        private double bonusSum;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getFromUserId() {
            return fromUserId;
        }

        public void setFromUserId(int fromUserId) {
            this.fromUserId = fromUserId;
        }

        public int getRoundId() {
            return roundId;
        }

        public void setRoundId(int roundId) {
            this.roundId = roundId;
        }

        public String getStartDay() {
            return startDay;
        }

        public void setStartDay(String startDay) {
            this.startDay = startDay;
        }

        public String getEndDay() {
            return endDay;
        }

        public void setEndDay(String endDay) {
            this.endDay = endDay;
        }

        public double getTeamAmount() {
            return teamAmount;
        }

        public void setTeamAmount(double teamAmount) {
            this.teamAmount = teamAmount;
        }

        public double getProfitAmount() {
            return profitAmount;
        }

        public void setProfitAmount(double profitAmount) {
            this.profitAmount = profitAmount;
        }

        public double getLastProfitLoss() {
            return lastProfitLoss;
        }

        public void setLastProfitLoss(double lastProfitLoss) {
            this.lastProfitLoss = lastProfitLoss;
        }

        public double getRealProfitAmount() {
            return realProfitAmount;
        }

        public void setRealProfitAmount(double realProfitAmount) {
            this.realProfitAmount = realProfitAmount;
        }

        public double getPercent() {
            return percent;
        }

        public void setPercent(double percent) {
            this.percent = percent;
        }

        public double getBonus() {
            return bonus;
        }

        public void setBonus(double bonus) {
            this.bonus = bonus;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public double getProfitLoss() {
            return profitLoss;
        }

        public void setProfitLoss(double profitLoss) {
            this.profitLoss = profitLoss;
        }

        public String getStatusStr() {
            return statusStr;
        }

        public void setStatusStr(String statusStr) {
            this.statusStr = statusStr;
        }

        public boolean isSummary() {
            return summary;
        }

        public void setSummary(boolean summary) {
            this.summary = summary;
        }

        public String getPayTime() {
            return payTime;
        }

        public void setPayTime(String payTime) {
            this.payTime = payTime;
        }

        public double getBonusSum() {
            return bonusSum;
        }

        public void setBonusSum(double bonusSum) {
            this.bonusSum = bonusSum;
        }
    }
}
