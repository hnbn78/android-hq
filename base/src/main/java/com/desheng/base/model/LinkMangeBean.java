package com.desheng.base.model;

import java.math.BigDecimal;
import java.util.List;

public class LinkMangeBean {


    /**
     * totalCount : 2
     * list : [{"id":80,"code":"/register/677f77f8e3eb64a5.html","accountId":null,"type":1,"point":14.7,"expireTime":null,"amount":null,"teamName":null,"qq":null,"addTime":"2018-04-25 10:33:34","status":0,"statutsRemark":"失效"},{"id":40,"code":"/register/5998e8d4541ed01e.html","accountId":null,"type":1,"point":0,"expireTime":null,"amount":null,"teamName":null,"qq":null,"addTime":"2018-02-26 18:48:54","status":0,"statutsRemark":"失效"}]
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
         * id : 80
         * code : /register/677f77f8e3eb64a5.html
         * accountId : null
         * type : 1
         * point : 14.7
         * expireTime : null
         * amount : null
         * teamName : null
         * qq : null
         * addTime : 2018-04-25 10:33:34
         * status : 0
         * statutsRemark : 失效
         */

        private int id;
        private String code;
        private String accountId;
        private int type;
        private BigDecimal point;
        private String expireTime;
        private double amount;
        private String teamName;
        private String qq;
        private String addTime;
        private int status;
        private String statutsRemark;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public BigDecimal getPoint() {
            return point;
        }

        public void setPoint(BigDecimal point) {
            this.point = point;
        }

        public String getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(String expireTime) {
            this.expireTime = expireTime;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatutsRemark() {
            return statutsRemark;
        }

        public void setStatutsRemark(String statutsRemark) {
            this.statutsRemark = statutsRemark;
        }
    }
}
