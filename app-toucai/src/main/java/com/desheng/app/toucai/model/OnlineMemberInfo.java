package com.desheng.app.toucai.model;

import java.util.List;

public class OnlineMemberInfo {

    /**
     * totalCount : 1
     * list : [{"lotteryBalance":"0.0228","baccaratBalance":"0","loginTime":"2020-07-11 16:03:23.0","username":"jfcs2002"}]
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
         * lotteryBalance : 0.0228
         * baccaratBalance : 0
         * loginTime : 2020-07-11 16:03:23.0
         * username : jfcs2002
         */

        private String lotteryBalance;
        private String baccaratBalance;
        private String loginTime;
        private String username;

        public String getLotteryBalance() {
            return lotteryBalance;
        }

        public void setLotteryBalance(String lotteryBalance) {
            this.lotteryBalance = lotteryBalance;
        }

        public String getBaccaratBalance() {
            return baccaratBalance;
        }

        public void setBaccaratBalance(String baccaratBalance) {
            this.baccaratBalance = baccaratBalance;
        }

        public String getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(String loginTime) {
            this.loginTime = loginTime;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
