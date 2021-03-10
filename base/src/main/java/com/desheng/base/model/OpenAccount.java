package com.desheng.base.model;

import java.io.Serializable;
import java.util.List;

/**
 * 代理开户信息
 * Created by lee on 2018/4/30.
 */
public class OpenAccount implements Serializable {
    public LotteryAgentRangeBean lotteryAgentRange;
    public LotteryPlayerRangeBean lotteryPlayerRange;
    public int userBonus;
    public boolean equalSub;
    public List<?> lotteryCodeQuotaList;

    public boolean allowAddAgent;
    public boolean allowAddUser;

    public static class LotteryAgentRangeBean implements Serializable{

        public double minPoint;
        public double maxPoint;
    }
    
    public static class LotteryPlayerRangeBean implements Serializable{
        public double minPoint;
        public double maxPoint;
    
    }   
}
