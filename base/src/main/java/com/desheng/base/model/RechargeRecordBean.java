package com.desheng.base.model;

/**
 * Created by lee on 2018/4/27.
 */

public class RechargeRecordBean {

    /**
     * {
     "id" : 6159,
     "billno" : "CZ_201809011056394763169",
     "accountId" : null,
     "amount" : 100.0,
     "feeAmount" : 0.0,
     "actualAmount" : 100.0,
     "balanceBefore" : null,
     "balanceAfter" : null,
     "method" : 0,
     "orderStatus" : 1,
     "orderTime" : 1535770599000,
     "payTime" : 0,
     "infos" : null,
     "remarks" : null
     }
     */
    public int id;
    public String billno;
    public String accountId;
    public double amount;
    public double feeAmount;
    public double actualAmount;
    public double balanceBefore;
    public double balanceAfter;
    public int method;
    public int orderStatus;// 0：成功  6或者7：失败  不等于6并且不等于7并且不等于0：待处理
    public long orderTime;
    public int payTime;
    public Object infos;
    public Object remarks;
    public String channelName;
}
