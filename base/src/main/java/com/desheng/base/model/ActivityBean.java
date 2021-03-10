package com.desheng.base.model;

import com.desheng.base.action.HttpAction;

import java.util.List;

public class ActivityBean {

    /**
     * title : 绑定手机奖励
     * receiveTime : 2018-03-16 12:51
     * award : 100.0
     * drawStatusStr : 已发放
     */
    public int totalCount;
    public List<ActivityModel> list;
    public Statistics statistics;

    public static class Statistics{
        public String totalAmount;
        public String refuseAmount;
        public String successAmount;
        public String pendingAmount;
    }
    public static class ActivityModel{
        public String title;
        public String receiveTime;
        public double award;
        public String drawStatusStr;
    }



}
