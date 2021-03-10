package com.desheng.app.toucai.model;

import java.util.List;

public class GudongTeamDetailBean {

    /**
     * totalAllReboundAmount : 0.0000
     * totalCount : 1
     * list : [{"award_amount":1960.98,"rebound_amount":0,"total_reboun_amount":0,"user_id":13729,"cn":"bill1234","confirm_amount":1950.94}]
     * totalTodayReboundAmount : 0.0000
     */

    private String totalAllReboundAmount;
    private int totalCount;
    private String totalTodayReboundAmount;
    private List<GudongTeamDetailListBean> list;

    public String getTotalAllReboundAmount() {
        return totalAllReboundAmount;
    }

    public void setTotalAllReboundAmount(String totalAllReboundAmount) {
        this.totalAllReboundAmount = totalAllReboundAmount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getTotalTodayReboundAmount() {
        return totalTodayReboundAmount;
    }

    public void setTotalTodayReboundAmount(String totalTodayReboundAmount) {
        this.totalTodayReboundAmount = totalTodayReboundAmount;
    }

    public List<GudongTeamDetailListBean> getList() {
        return list;
    }

    public void setList(List<GudongTeamDetailListBean> list) {
        this.list = list;
    }

}
