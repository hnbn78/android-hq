package com.desheng.base.model;

import java.util.List;

/**
 * Created by lee on 2018/3/28.
 */
public class LotteryBuyPlan {
    
    public int businessCode;
    public String message;
    public List<DataBean> data;
    
    public static class DataBean {
        public int countTime;
        public String preDrawCode;
        public long preDrawIssue;
        public String preDrawTime;
        public int lotteryCostC;
        public int lotteryCostB;
        public int lotteryCostA;
        public int lotteryCostAllC;
        public int lotteryCostAllB;
        public int lotteryCostAllA;
        public String profitC;
        public String profitB;
        public String profitA;
        public String planC;
        public String planB;
        public String planA;
    }
}
