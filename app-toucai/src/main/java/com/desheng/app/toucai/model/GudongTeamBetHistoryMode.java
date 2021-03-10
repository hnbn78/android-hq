package com.desheng.app.toucai.model;

import java.util.List;

public class GudongTeamBetHistoryMode {


    /**
     * list : [{"amount":10,"bonus":19.6,"cn":"test0023","issue_no":"20190319-1619","lottery_id":46,"lottery_name":"多伦多30秒","lottery_status":"已发放","method_id":6041,"method_name":"总和大小单双","rebound_amount":19.6,"user_id":67493},{"amount":10,"bonus":19.6,"cn":"test0023","issue_no":"20190319-1619","lottery_id":46,"lottery_name":"多伦多30秒","lottery_status":"已发放","method_id":6041,"method_name":"总和大小单双","rebound_amount":19.6,"user_id":67493},{"amount":10,"bonus":19.6,"cn":"test0023","issue_no":"20190319-1619","lottery_id":46,"lottery_name":"多伦多30秒","lottery_status":"已发放","method_id":6041,"method_name":"总和大小单双","rebound_amount":19.6,"user_id":67493},{"amount":10,"bonus":19.6,"cn":"test0023","issue_no":"20190319-1619","lottery_id":46,"lottery_name":"多伦多30秒","lottery_status":"已发放","method_id":6041,"method_name":"总和大小单双","rebound_amount":19.6,"user_id":67493},{"amount":10,"bonus":19.6,"cn":"test0023","issue_no":"20190319-1619","lottery_id":46,"lottery_name":"多伦多30秒","lottery_status":"已发放","method_id":6041,"method_name":"总和大小单双","rebound_amount":19.6,"user_id":67493}]
     * subTotalList : [{"sub_total_amount":"50.00","sub_total_bonus":"95.00","sub_total_rebound_amount":"95.00"}]
     * totalCount : 5
     * totalList : [{"total_amount":50,"total_bonus":98,"total_rebound_amount":98}]
     */

    private int totalCount;
    private List<GudongTeamBetHistoryListBean> list;
    private List<SubTotalListBean> subTotalList;
    private List<TotalListBean> totalList;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<GudongTeamBetHistoryListBean> getList() {
        return list;
    }

    public void setList(List<GudongTeamBetHistoryListBean> list) {
        this.list = list;
    }

    public List<SubTotalListBean> getSubTotalList() {
        return subTotalList;
    }

    public void setSubTotalList(List<SubTotalListBean> subTotalList) {
        this.subTotalList = subTotalList;
    }

    public List<TotalListBean> getTotalList() {
        return totalList;
    }

    public void setTotalList(List<TotalListBean> totalList) {
        this.totalList = totalList;
    }

    public static class SubTotalListBean {
        /**
         * sub_total_amount : 50.00
         * sub_total_bonus : 95.00
         * sub_total_rebound_amount : 95.00
         */

        private String sub_total_amount;
        private String sub_total_bonus;
        private String sub_total_rebound_amount;

        public String getSub_total_amount() {
            return sub_total_amount;
        }

        public void setSub_total_amount(String sub_total_amount) {
            this.sub_total_amount = sub_total_amount;
        }

        public String getSub_total_bonus() {
            return sub_total_bonus;
        }

        public void setSub_total_bonus(String sub_total_bonus) {
            this.sub_total_bonus = sub_total_bonus;
        }

        public String getSub_total_rebound_amount() {
            return sub_total_rebound_amount;
        }

        public void setSub_total_rebound_amount(String sub_total_rebound_amount) {
            this.sub_total_rebound_amount = sub_total_rebound_amount;
        }
    }

    public static class TotalListBean {
        /**
         * total_amount : 50.0
         * total_bonus : 98.0
         * total_rebound_amount : 98.0
         */

        private double total_amount;
        private double total_bonus;
        private double total_rebound_amount;

        public double getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(double total_amount) {
            this.total_amount = total_amount;
        }

        public double getTotal_bonus() {
            return total_bonus;
        }

        public void setTotal_bonus(double total_bonus) {
            this.total_bonus = total_bonus;
        }

        public double getTotal_rebound_amount() {
            return total_rebound_amount;
        }

        public void setTotal_rebound_amount(double total_rebound_amount) {
            this.total_rebound_amount = total_rebound_amount;
        }
    }
}
