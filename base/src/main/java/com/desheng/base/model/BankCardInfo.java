package com.desheng.base.model;

/**
 * Created by lee on 2018/4/25.
 */
public class BankCardInfo {
    
    private String bankId;
    private boolean isDefault;
    private Object addTime;
    private String bankBranch;
    private Object lockTime;
    private String bankName;
    private int id;
    private String bankCardName;
    private String cardStatus;
    private String bankCardId;
    private int cardType;
    private boolean isXunibiCardType;

    public boolean getIsXunibiCardType() {
        return isXunibiCardType;
    }

    public void setIsXunibiCardType(boolean isXunibiCardType) {
        this.isXunibiCardType = isXunibiCardType;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getBankId() {
        return bankId;
    }
    
    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
    
    public boolean isIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public Object getAddTime() {
        return addTime;
    }
    
    public void setAddTime(Object addTime) {
        this.addTime = addTime;
    }
    
    public String getBankBranch() {
        return bankBranch;
    }
    
    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }
    
    public Object getLockTime() {
        return lockTime;
    }
    
    public void setLockTime(Object lockTime) {
        this.lockTime = lockTime;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getBankCardName() {
        return bankCardName;
    }
    
    public void setBankCardName(String bankCardName) {
        this.bankCardName = bankCardName;
    }
    
    public String getCardStatus() {
        return cardStatus;
    }
    
    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }
    
    public String getBankCardId() {
        return bankCardId;
    }
    
    public void setBankCardId(String bankCardId) {
        this.bankCardId = bankCardId;
    }
}
