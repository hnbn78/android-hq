package com.desheng.base.model;

import java.util.List;

/**
 * Created by lee on 2018/3/29.
 */

public class LotteryKillNum {
    
    public int businessCode;
    public DataBean data;
    public String message;
    
    public static class DataBean {
        public List<ListBean> list;
        
        public static class ListBean {
            public String preDrawCode;
            public String preDrawIssue;
            public long preDrawDate;
            public List<Integer> firstNum;
            public List<Integer> secondNum;
            public List<Integer> thirdNum;
            public List<Integer> fourthNum;
            public List<Integer> fifthNum;
            public List<Integer> sixthNum;
            public List<Integer> sevenNum;
            public List<Integer> eightNum;
            public List<Integer> nineNum;
            public List<Integer> tenNum;
            public List<Integer> [] arrNums;
        }
    }
}
