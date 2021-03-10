package com.desheng.base.model;

import java.util.List;

/**
 * Created by lee on 2018/3/29.
 */

public class AnalysisLuZhu {
    
    public int businessCode;
    public String message;
    public List<DataBean> data;
    
    public static class DataBean {
        public int rank;
        public int state;
        public String date;
        public List<Integer> roadBeads;
        public List<Integer> totals;
    }
}
