package com.desheng.base.model;

import java.util.List;

public class ThirdTeamReportBean {

    /**
     * totalCount : 15
     * list : [{"userName":"test0001","transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":0},{"userName":"ray0001","transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":0},{"userName":"tomtest2","transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":0},{"userName":"test0002","transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":0},{"userName":"ray0002","transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":0},{"userName":"zsp123","transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":0},{"userName":"kevin0101","transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":0},{"userName":"ray0003","transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":0},{"userName":"cheshi2","transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":0},{"userName":"kevin0111","transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":0},{"userName":"kevin211","transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":0},{"userName":null,"transferOutAmount":0,"transferInAmount":0,"confirmAmount":0,"awardAmount":0,"profitAmount":0,"rebateAmount":0,"isCount":1}]
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
         * userName : test0001
         * transferOutAmount : 0.0
         * transferInAmount : 0.0
         * confirmAmount : 0.0
         * awardAmount : 0.0
         * profitAmount : 0.0
         * rebateAmount : 0.0
         * isCount : 0
         */

        private String userName;
        private double transferOutAmount;
        private double transferInAmount;
        private double confirmAmount;
        private double awardAmount;
        private double profitAmount;
        private double rebateAmount;
        private int isCount;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public double getTransferOutAmount() {
            return transferOutAmount;
        }

        public void setTransferOutAmount(double transferOutAmount) {
            this.transferOutAmount = transferOutAmount;
        }

        public double getTransferInAmount() {
            return transferInAmount;
        }

        public void setTransferInAmount(double transferInAmount) {
            this.transferInAmount = transferInAmount;
        }

        public double getConfirmAmount() {
            return confirmAmount;
        }

        public void setConfirmAmount(double confirmAmount) {
            this.confirmAmount = confirmAmount;
        }

        public double getAwardAmount() {
            return awardAmount;
        }

        public void setAwardAmount(double awardAmount) {
            this.awardAmount = awardAmount;
        }

        public double getProfitAmount() {
            return profitAmount;
        }

        public void setProfitAmount(double profitAmount) {
            this.profitAmount = profitAmount;
        }

        public double getRebateAmount() {
            return rebateAmount;
        }

        public void setRebateAmount(double rebateAmount) {
            this.rebateAmount = rebateAmount;
        }

        public int getIsCount() {
            return isCount;
        }

        public void setIsCount(int isCount) {
            this.isCount = isCount;
        }
    }
}
