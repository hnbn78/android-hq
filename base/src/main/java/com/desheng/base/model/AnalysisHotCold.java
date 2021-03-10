package com.desheng.base.model;

import java.util.List;

/**
 * Created by lee on 2018/3/29.
 */

public class AnalysisHotCold {
    
    public int businessCode;
    public String message;
    public List<DataBean> data;
    
    public static class DataBean {
        public int rank;
        public List<ListBeanX> list;
        public static class ListBeanX {
            public int state;
            public List<ListBean> list;
    
            public static class ListBean {
                public int drawCode;
                public int count;
            }
        }
    }
}
