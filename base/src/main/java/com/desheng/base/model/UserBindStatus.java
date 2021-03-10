package com.desheng.base.model;

import java.io.Serializable;

/**
 * Created by user on 2018/3/14.
 */

public class UserBindStatus implements Serializable {


    private boolean isBindWithdrawName;
    private boolean isBindSecurity;
    private boolean isBindCellphone;
    private boolean isBindQQ;
    private boolean isBindCard;
    private boolean isBindEmail;
    private boolean isBindBirthday;
    private boolean isBindWeChat;
    private boolean isBindGoogleAuthenticator;
    private boolean isBindWithdrawPassword;
    private boolean isPhoneRegister;
    private boolean isCnReset;
    private boolean isCanUpdatePwdWechat;
    private boolean isBindGender;
    private boolean isBalanceChangedAlert;
    private boolean isUpdateWithdrawPassword;
    private boolean isModifiedPassword;

    public boolean isModifiedPassword() {
        return isModifiedPassword;
    }

    public void setModifiedPassword(boolean modifiedPassword) {
        isModifiedPassword = modifiedPassword;
    }

    public boolean isBalanceChangedAlert() {
        return isBalanceChangedAlert;
    }

    public void setBalanceChangedAlert(boolean balanceChangedAlert) {
        isBalanceChangedAlert = balanceChangedAlert;
    }

    public boolean isUpdateWithdrawPassword() {
        return isUpdateWithdrawPassword;
    }

    public void setUpdateWithdrawPassword(boolean updateWithdrawPassword) {
        isUpdateWithdrawPassword = updateWithdrawPassword;
    }

    public boolean isBindGender() {
        return isBindGender;
    }

    public void setBindGender(boolean bindGender) {
        isBindGender = bindGender;
    }

    public boolean isBindWithdrawName() {
        return isBindWithdrawName;
    }

    public void setBindWithdrawName(boolean bindWithdrawName) {
        isBindWithdrawName = bindWithdrawName;
    }

    public boolean isBindSecurity() {
        return isBindSecurity;
    }

    public void setBindSecurity(boolean bindSecurity) {
        isBindSecurity = bindSecurity;
    }

    public boolean isBindCellphone() {
        return isBindCellphone;
    }

    public void setBindCellphone(boolean bindCellphone) {
        isBindCellphone = bindCellphone;
    }

    public boolean isBindQQ() {
        return isBindQQ;
    }

    public void setBindQQ(boolean bindQQ) {
        isBindQQ = bindQQ;
    }

    public boolean isBindCard() {
        return isBindCard;
    }

    public void setBindCard(boolean bindCard) {
        isBindCard = bindCard;
    }

    public boolean isBindEmail() {
        return isBindEmail;
    }

    public void setBindEmail(boolean bindEmail) {
        isBindEmail = bindEmail;
    }

    public boolean isBindBirthday() {
        return isBindBirthday;
    }

    public void setBindBirthday(boolean bindBirthday) {
        isBindBirthday = bindBirthday;
    }

    public boolean isBindGoogleAuthenticator() {
        return isBindGoogleAuthenticator;
    }

    public void setBindGoogleAuthenticator(boolean bindGoogleAuthenticator) {
        isBindGoogleAuthenticator = bindGoogleAuthenticator;
    }

    public boolean isBindWithdrawPassword() {
        return isBindWithdrawPassword;
    }

    public void setBindWithdrawPassword(boolean bindWithdrawPassword) {
        isBindWithdrawPassword = bindWithdrawPassword;
    }

    public boolean isBindWeChat() {
        return isBindWeChat;
    }

    public void setBindWeChat(boolean bindWeChat) {
        isBindWeChat = bindWeChat;
    }

    public boolean isPhoneRegister() {
        return isPhoneRegister;
    }

    public void setPhoneRegister(boolean phoneRegister) {
        isPhoneRegister = phoneRegister;
    }

    public boolean isBindPhone() {
        return isBindCellphone;
    }

    public void setBindPhone(boolean bindPhone) {
        isBindCellphone = bindPhone;
    }


    public boolean isCnReset() {
        return isCnReset;
    }

    public void setCnReset(boolean cnReset) {
        isCnReset = cnReset;
    }

    public boolean isCanUpdatePwdWechat() {
        return isCanUpdatePwdWechat;
    }

    public void setCanUpdatePwdWechat(boolean canUpdatePwdWechat) {
        isCanUpdatePwdWechat = canUpdatePwdWechat;
    }
}
