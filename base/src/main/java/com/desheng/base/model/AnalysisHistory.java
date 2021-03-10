package com.desheng.base.model;

import java.util.List;

/**
 * Created by lee on 2018/3/29.
 */

public class AnalysisHistory {

    public int businessCode;
    public String message;
    public List<DataBean> data;
    
    public static class DataBean {
        public String preDrawCode;
        public long preDrawIssue;
        public int groupCode;
        public String preDrawTime;
        public int sumNum = -1;
        public int sumBigSmall;
        public int sumSingleDouble;
        public int behindThree;
        public int betweenThree;
        public int dragonTiger = -1;
        public int fifthBigSmall;
        public int fifthSingleDouble;
        public int firstBigSmall;
        public int firstSingleDouble;
        public int fourthBigSmall;
        public int fourthSingleDouble;
        public int secondBigSmall;
        public int secondSingleDouble;
        public int thirdBigSmall;
        public int thirdSingleDouble;
        public int lastThree;
        public int fifthDT;
        public int firstDT;
        public int fourthDT;
        public int secondDT;
        public int sumFS;
        public int thirdDT;
        public int sumBigSamll;
    }
}
