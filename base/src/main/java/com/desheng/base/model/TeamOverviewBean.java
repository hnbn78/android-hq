package com.desheng.base.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lee on 2018/5/2.
 */

public class TeamOverviewBean {

    public int registerSum;
    public int betCount;
    public ReportsBean reports;
    public int userCount;
    public BigDecimal teamBalance;
    public int memberCount;
    public int userOnlineCount;
    public int agentCount;
    public List<RegisterListBean> registerList;

    public List<RegisterListBean> getRegisterList() {
        return registerList;
    }

    public void setRegisterList(List<RegisterListBean> registerList) {
        this.registerList = registerList;
    }

    public ReportsBean getReports() {
        return reports;
    }

    public void setReports(ReportsBean reports) {
        this.reports = reports;
    }

    public static class ReportsBean {
        public int depositAmountSum;
        public int awardAmountSum;
        public int pointAmountSum;
        public int confirmAmountSum;
        public int activityAmountSum;
        public int withdrawAmountSum;
        public List<DayReportListBean> dayReportList;


        public static class DayReportListBean {

            public String userName;
            public String userType;
            public float confirmAmount;
            public float txsscConfirmAmount;
            public float awardAmount;
            public float pointAmount;
            public float profitAmount;
            public float activityAmount;
            public float depositAmount;
            public float withdrawAmount;
            public float feeAmount;
            public float orderBonusAmount;
            public String date;
            public String teamBalance;
            public String balance;

            public Object getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getUserType() {
                return userType;
            }

            public void setUserType(String userType) {
                this.userType = userType;
            }

            public float getConfirmAmount() {
                return confirmAmount;
            }

            public void setConfirmAmount(float confirmAmount) {
                this.confirmAmount = confirmAmount;
            }

            public float getTxsscConfirmAmount() {
                return txsscConfirmAmount;
            }

            public void setTxsscConfirmAmount(float txsscConfirmAmount) {
                this.txsscConfirmAmount = txsscConfirmAmount;
            }

            public float getAwardAmount() {
                return awardAmount;
            }

            public void setAwardAmount(float awardAmount) {
                this.awardAmount = awardAmount;
            }

            public float getPointAmount() {
                return pointAmount;
            }

            public void setPointAmount(float pointAmount) {
                this.pointAmount = pointAmount;
            }

            public float getProfitAmount() {
                return profitAmount;
            }

            public void setProfitAmount(float profitAmount) {
                this.profitAmount = profitAmount;
            }

            public float getActivityAmount() {
                return activityAmount;
            }

            public void setActivityAmount(float activityAmount) {
                this.activityAmount = activityAmount;
            }

            public float getDepositAmount() {
                return depositAmount;
            }

            public void setDepositAmount(float depositAmount) {
                this.depositAmount = depositAmount;
            }

            public float getWithdrawAmount() {
                return withdrawAmount;
            }

            public void setWithdrawAmount(float withdrawAmount) {
                this.withdrawAmount = withdrawAmount;
            }

            public float getFeeAmount() {
                return feeAmount;
            }

            public void setFeeAmount(float feeAmount) {
                this.feeAmount = feeAmount;
            }

            public float getOrderBonusAmount() {
                return orderBonusAmount;
            }

            public void setOrderBonusAmount(float orderBonusAmount) {
                this.orderBonusAmount = orderBonusAmount;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getTeamBalance() {
                return teamBalance;
            }

            public void setTeamBalance(String teamBalance) {
                this.teamBalance = teamBalance;
            }

            public String getBalance() {
                return balance;
            }

            public void setBalance(String balance) {
                this.balance = balance;
            }
        }
    }

    public static class RegisterListBean {
        public String registerDate;
        public float count;

        public String getRegisterDate() {
            return registerDate;
        }

        public void setRegisterDate(String registerDate) {
            this.registerDate = registerDate;
        }

        public void setCount(float count) {
            this.count = count;
        }

        public float getCount() {
            return count;
        }
    }
}
