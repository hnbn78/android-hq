package com.desheng.base.view.trendchart;

import java.util.List;

/**
 * Created by fuqiang on 2014/12/29.
 */
public class TrendModel {
    private String blueMiss;
    private String issueNo;
    private List<Integer> nativeCodeInt;
    private TrendColorBean trendColorBean = new TrendColorBean();
    private String winNumber;

    public String getBlueMiss() {
        return this.blueMiss;
    }

    public String getIssueNo() {
        return this.issueNo;
    }

    public List<Integer> getNativeCodeInt() {
        return this.nativeCodeInt;
    }

    public TrendColorBean getTrendColorBean() {
        return this.trendColorBean;
    }

    public String getWinNumber() {
        return this.winNumber;
    }

    public void setBlueMiss(String paramString) {
        this.blueMiss = paramString;
    }

    public void setIssueNo(String paramString) {
        this.issueNo = paramString;
    }

    public void setNativeCodeInt(List<Integer> paramList) {
        this.nativeCodeInt = paramList;
    }

    public void setTrendColorBean(TrendColorBean paramTrendColorBean) {
        this.trendColorBean = paramTrendColorBean;
    }

    public void setWinNumber(String paramString) {
        this.winNumber = paramString;
    }
}
