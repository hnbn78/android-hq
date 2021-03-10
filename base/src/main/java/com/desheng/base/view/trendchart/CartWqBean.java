package com.desheng.base.view.trendchart;


import java.util.ArrayList;
import java.util.List;

public class CartWqBean {
    private String code;//号码
    private String issue;//期号
    private List<Integer> nativeCodeInt = new ArrayList();
    private String native_code;
    private String time;//日期

    public String getCode() {
        return this.code;
    }

    public String getIssue() {
        return this.issue;
    }

    public List<Integer> getNativeCodeInt() {
        return this.nativeCodeInt;
    }

    public String getNative_code() {
        return this.native_code;
    }

    public String getTime() {
        return this.time;
    }

    public void setCode(String paramString) {
        this.code = paramString;
    }

    public void setIssue(String paramString) {
        this.issue = paramString;
    }

    public void setNativeCodeInt(List<Integer> paramList) {
        this.nativeCodeInt = paramList;
    }

    public void setNative_code(String paramString) {
        this.native_code = paramString;
    }

    public void setTime(String paramString) {
        this.time = paramString;
    }

    @Override
    public String toString() {
        return "CartWqBean{" +
                "code='" + code + '\'' +
                ", issue='" + issue + '\'' +
                ", nativeCodeInt=" + nativeCodeInt +
                ", native_code='" + native_code + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
