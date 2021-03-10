package com.desheng.app.toucai.model;

import java.io.Serializable;

/**
 * Created by user on 2018/3/27.
 */

public class TansferRecordBean   implements Serializable{
   private String id;
    private String uin;
    private String cn;
    private String spsn;
    private String amount;
    private String point;
    private String lockPoint;
    private String note;
    private String changeType;
    private String typeName;
    private String optType;
    private String page;
    private String pageSize;
    private String startDate;
    private String endDate;
    private String createTime;
    private String changeDetailType;
    private String changeTypeStr;
    private String issueNo;
    private String content;
    private String ruleNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getSpsn() {
        return spsn;
    }

    public void setSpsn(String spsn) {
        this.spsn = spsn;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getLockPoint() {
        return lockPoint;
    }

    public void setLockPoint(String lockPoint) {
        this.lockPoint = lockPoint;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getOptType() {
        return optType;
    }

    public void setOptType(String optType) {
        this.optType = optType;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getChangeDetailType() {
        return changeDetailType;
    }

    public void setChangeDetailType(String changeDetailType) {
        this.changeDetailType = changeDetailType;
    }

    public String getChangeTypeStr() {
        return changeTypeStr;
    }

    public void setChangeTypeStr(String changeTypeStr) {
        this.changeTypeStr = changeTypeStr;
    }

    public String getIssueNo() {
        return issueNo;
    }

    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRuleNo() {
        return ruleNo;
    }

    public void setRuleNo(String ruleNo) {
        this.ruleNo = ruleNo;
    }
}
