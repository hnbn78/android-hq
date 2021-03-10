package com.desheng.base.model;

import java.util.List;

public class PrepareBindCard {

    /**
     * bankList : [{"code":"CEB","name":"光大银行","allowBindCard":true,"withdrawMessage":null,"id":"CEB","withdrawStatus":0,"url":""},{"code":"PSBC","name":"中国邮政储蓄银行","allowBindCard":true,"withdrawMessage":null,"id":"PSBC","withdrawStatus":0,"url":""}]
     * count : 1
     * name : 大**
     * maxCount : 5
     */

    private int count;
    private String name;
    private int maxCount;
    private List<BankListBean> bankList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public List<BankListBean> getBankList() {
        return bankList;
    }

    public void setBankList(List<BankListBean> bankList) {
        this.bankList = bankList;
    }

    public static class BankListBean {
        /**
         * code : CEB
         * name : 光大银行
         * allowBindCard : true
         * withdrawMessage : null
         * id : CEB
         * withdrawStatus : 0
         * url :
         */

        private String code;
        private String name;
        private boolean allowBindCard;
        private Object withdrawMessage;
        private String id;
        private int withdrawStatus;
        private String url;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isAllowBindCard() {
            return allowBindCard;
        }

        public void setAllowBindCard(boolean allowBindCard) {
            this.allowBindCard = allowBindCard;
        }

        public Object getWithdrawMessage() {
            return withdrawMessage;
        }

        public void setWithdrawMessage(Object withdrawMessage) {
            this.withdrawMessage = withdrawMessage;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getWithdrawStatus() {
            return withdrawStatus;
        }

        public void setWithdrawStatus(int withdrawStatus) {
            this.withdrawStatus = withdrawStatus;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
