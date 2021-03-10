package com.desheng.app.toucai.model;

import java.util.List;

public class BonusPoolWinJsonDataMode {

    /**
     * level : 3
     * bonusAmount : 50
     * bonusName : 一个大西瓜
     * bonusType : 2
     * firstLevelAmount : 9
     * firstLevelUsers : ["test0001"]
     * hasPrize : false
     * secondLevelAmount : 300
     * distributeAmount : 959
     * secondLevelUsers : ["cctv10","test0001","cctv999"]
     */

    private String level;
    private String bonusAmount;
    private String bonusName;
    private String bonusType;
    private boolean hasPrize;

    private String firstLevelAmount;
    private String secondLevelAmount;
    private String distributeAmount;
    private List<String> firstLevelUsers;
    private List<String> secondLevelUsers;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(String bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public String getBonusName() {
        return bonusName;
    }

    public void setBonusName(String bonusName) {
        this.bonusName = bonusName;
    }

    public String getBonusType() {
        return bonusType;
    }

    public void setBonusType(String bonusType) {
        this.bonusType = bonusType;
    }

    public boolean isHasPrize() {
        return hasPrize;
    }

    public void setHasPrize(boolean hasPrize) {
        this.hasPrize = hasPrize;
    }

    public String getFirstLevelAmount() {
        return firstLevelAmount;
    }

    public void setFirstLevelAmount(String firstLevelAmount) {
        this.firstLevelAmount = firstLevelAmount;
    }

    public String getSecondLevelAmount() {
        return secondLevelAmount;
    }

    public void setSecondLevelAmount(String secondLevelAmount) {
        this.secondLevelAmount = secondLevelAmount;
    }

    public String getDistributeAmount() {
        return distributeAmount;
    }

    public void setDistributeAmount(String distributeAmount) {
        this.distributeAmount = distributeAmount;
    }

    public List<String> getFirstLevelUsers() {
        return firstLevelUsers;
    }

    public void setFirstLevelUsers(List<String> firstLevelUsers) {
        this.firstLevelUsers = firstLevelUsers;
    }

    public List<String> getSecondLevelUsers() {
        return secondLevelUsers;
    }

    public void setSecondLevelUsers(List<String> secondLevelUsers) {
        this.secondLevelUsers = secondLevelUsers;
    }
}
