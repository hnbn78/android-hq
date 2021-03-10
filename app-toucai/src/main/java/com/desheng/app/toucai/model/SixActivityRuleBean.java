package com.desheng.app.toucai.model;

public class SixActivityRuleBean {
    /**
     * agency : 1000
     * copywriting : 本月个人再投注8000.0元，即可继续享受大股东会员权限
     * generalAgent : 2000
     * majorShareholder : 8000
     * shareholder : 4000
     * sortNo : 1
     * type : effectivePersonalBets
     */

    private String agency;
    private String copywriting;
    private String generalAgent;
    private String majorShareholder;
    private String shareholder;
    private String sortNo;
    private String type;

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getCopywriting() {
        return copywriting;
    }

    public void setCopywriting(String copywriting) {
        this.copywriting = copywriting;
    }

    public String getGeneralAgent() {
        return generalAgent;
    }

    public void setGeneralAgent(String generalAgent) {
        this.generalAgent = generalAgent;
    }

    public String getMajorShareholder() {
        return majorShareholder;
    }

    public void setMajorShareholder(String majorShareholder) {
        this.majorShareholder = majorShareholder;
    }

    public String getShareholder() {
        return shareholder;
    }

    public void setShareholder(String shareholder) {
        this.shareholder = shareholder;
    }

    public String getSortNo() {
        return sortNo;
    }

    public void setSortNo(String sortNo) {
        this.sortNo = sortNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
