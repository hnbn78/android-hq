package com.desheng.base.model;

import java.io.Serializable;

/**
 * Created by user on 2018/3/28.
 */

public class ChaseRecordBean implements Serializable{
    public int id;
    public String billno;
    public String account;
    public String lottery;
    public String startIssue;
    public Object endIssue;
    public int totalCount;
    public String method;
    public String content;
    public Object compress;
    public int nums;
    public Object model;
    public Object code;
    public double point;
    public double totalMoney;
    public long orderTime;
    public int status;
    public int clearCount;
    public double winMoney;
    public boolean allowCancel;
    public String statusStr;
    public Object chaseList;
    public int lotteryId;
    public boolean winStop;
}
