package com.desheng.base.model;

import java.util.List;

/**
 * Created by lee on 2018/3/31.
 */

public class AnalysisSides {
    
    public int businessCode;
    public String message;
    public List<DataBean> data;
    
    public static class DataBean {
        public long startIssue;
        public long endIssue;
        public String date;
        public List<ListBean> list;
        
        public static class ListBean {
            public int rank;
            public int singleCount;
            public int doubleCount;
            public int bigCount;
            public int smallCount;
        }
    }
}
