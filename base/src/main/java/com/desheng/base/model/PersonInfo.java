package com.desheng.base.model;

import java.io.Serializable;

public class PersonInfo implements Serializable {


    /**
     * birthday : null
     * qq : 5456566
     * gender : 0
     * bankBranch : null
     * cellphone : null
     * wechatId : null
     * bankName : null
     * withdrawName : null
     * email : null
     * withdrawPassword : ******
     * bankCardId : null
     */

    private String birthday;
    private String qq;
    private String gender;
    private String bankBranch;
    private String cellphone;
    private String wechatId;
    private String bankName;
    private String withdrawName;
    private String email;
    private String withdrawPassword;
    private String bankCardId;
    private boolean vipFlag;
    private String vipChannelUrl;
    private String inviteCode;

    public boolean isVipFlag() {
        return vipFlag;
    }

    public void setVipFlag(boolean vipFlag) {
        this.vipFlag = vipFlag;
    }

    public String getVipChannelUrl() {
        return vipChannelUrl;
    }

    public void setVipChannelUrl(String vipChannelUrl) {
        this.vipChannelUrl = vipChannelUrl;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getWithdrawName() {
        return withdrawName;
    }

    public void setWithdrawName(String withdrawName) {
        this.withdrawName = withdrawName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWithdrawPassword() {
        return withdrawPassword;
    }

    public void setWithdrawPassword(String withdrawPassword) {
        this.withdrawPassword = withdrawPassword;
    }

    public String getBankCardId() {
        return bankCardId;
    }

    public void setBankCardId(String bankCardId) {
        this.bankCardId = bankCardId;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
