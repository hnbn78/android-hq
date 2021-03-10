package com.desheng.app.toucai.model;

import java.util.List;

public class BonusPoolNoetWinMode {


    /**
     * firstLevelAmount : 9
     * firstLevelUsers : ["test0001"]
     * hasPrize : false
     * secondLevelAmount : 300
     * distributeAmount : 959
     * secondLevelUsers : ["cctv10","test0001","cctv999"]
     */

    private String firstLevelAmount;
    private boolean hasPrize;
    private String secondLevelAmount;
    private String distributeAmount;
    private List<String> firstLevelUsers;
    private List<String> secondLevelUsers;

    public String getFirstLevelAmount() {
        return firstLevelAmount;
    }

    public void setFirstLevelAmount(String firstLevelAmount) {
        this.firstLevelAmount = firstLevelAmount;
    }

    public boolean isHasPrize() {
        return hasPrize;
    }

    public void setHasPrize(boolean hasPrize) {
        this.hasPrize = hasPrize;
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
