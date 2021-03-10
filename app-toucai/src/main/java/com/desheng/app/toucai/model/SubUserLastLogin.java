package com.desheng.app.toucai.model;

import java.util.List;

public class SubUserLastLogin {

    /**
     * totalCount : 1
     * statistics : null
     * list : [{"loginTime":"2017-10-07 00:34:48","index":1,"cn":"cctv30"}]
     */

    private int totalCount;
    private Object statistics;
    private List<ListBean> list;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public Object getStatistics() {
        return statistics;
    }

    public void setStatistics(Object statistics) {
        this.statistics = statistics;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * loginTime : 2017-10-07 00:34:48
         * index : 1
         * cn : cctv30
         */

        private String loginTime;
        private int index;
        private String cn;

        public String getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(String loginTime) {
            this.loginTime = loginTime;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getCn() {
            return cn;
        }

        public void setCn(String cn) {
            this.cn = cn;
        }

        @Override
        public String toString() {
            return "ListBean{" +
                    "loginTime='" + loginTime + '\'' +
                    ", index=" + index +
                    ", cn='" + cn + '\'' +
                    '}';
        }
    }
}
