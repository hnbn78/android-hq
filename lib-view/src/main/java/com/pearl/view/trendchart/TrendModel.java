package com.pearl.view.trendchart;

/**
 * Created by fuqiang on 2014/12/29.
 */
public class TrendModel {
    private String lineNum;
    private String winNumber;
    
    public TrendModel(String lineNum, String winNumber) {
        this.lineNum = lineNum;
        this.winNumber = winNumber;
    }
    
    public String getLineNum() {
        return lineNum;
    }
    
    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }
    
    public String getWinNumber() {
        return winNumber;
    }
    
    public void setWinNumber(String winNumber) {
        this.winNumber = winNumber;
    }
}
