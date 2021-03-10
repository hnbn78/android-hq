package com.desheng.app.toucai.model;

public class TeamAgentShareLink {


    /**
     * id : 3
     * code : /register/fc769c6ec8d22ecf.html
     * accountId : null
     * type : 0
     * point : 0.0
     * expireTime : null
     * amount : null
     * teamName : null
     * qq : null
     * addTime : 2019-03-20 14:14:26
     * status : 0
     * statutsRemark : 失效
     */

    private int id;
    private String code;
    private Object accountId;
    private int type;
    private double point;
    private Object expireTime;
    private Object amount;
    private Object teamName;
    private Object qq;
    private String addTime;
    private int status;
    private String statutsRemark;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getAccountId() {
        return accountId;
    }

    public void setAccountId(Object accountId) {
        this.accountId = accountId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public Object getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Object expireTime) {
        this.expireTime = expireTime;
    }

    public Object getAmount() {
        return amount;
    }

    public void setAmount(Object amount) {
        this.amount = amount;
    }

    public Object getTeamName() {
        return teamName;
    }

    public void setTeamName(Object teamName) {
        this.teamName = teamName;
    }

    public Object getQq() {
        return qq;
    }

    public void setQq(Object qq) {
        this.qq = qq;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatutsRemark() {
        return statutsRemark;
    }

    public void setStatutsRemark(String statutsRemark) {
        this.statutsRemark = statutsRemark;
    }
}
