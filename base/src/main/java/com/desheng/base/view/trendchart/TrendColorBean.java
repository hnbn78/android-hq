package com.desheng.base.view.trendchart;

import java.util.ArrayList;
import java.util.List;

public class TrendColorBean {

    private List<Integer> trendBgColor = new ArrayList();
    private List<String> trendName = new ArrayList();
    private List<Integer> trendTextColor = new ArrayList();

    public List<Integer> getTrendBgColor() {
        return this.trendBgColor;
    }

    public List<String> getTrendName() {
        return this.trendName;
    }

    public List<Integer> getTrendTextColor() {
        return this.trendTextColor;
    }

    public void setTrendBgColor(List<Integer> paramList) {
        this.trendBgColor = paramList;
    }

    public void setTrendName(List<String> paramList) {
        this.trendName = paramList;
    }

    public void setTrendTextColor(List<Integer> paramList) {
        this.trendTextColor = paramList;
    }
}
