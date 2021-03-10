package com.desheng.base.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by o on 2018/3/30.
 */

public class MemberManagementBean implements Serializable{
    public int userId;
    public int userType;
    public String userName;
    public String balance;
    public String teamBalance;
    public int totalCount;
    public int todayRegisterCount;
    public String registerTime;
    public String point;
    public int set;
    public String lastLoginTime;
    public String onlineStatus;
    public String onlineState;
    public List<String> agentLinkList;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTeamBalance() {
        return teamBalance;
    }

    public void setTeamBalance(String teamBalance) {
        this.teamBalance = teamBalance;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTodayRegisterCount() {
        return todayRegisterCount;
    }

    public void setTodayRegisterCount(int todayRegisterCount) {
        this.todayRegisterCount = todayRegisterCount;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public int getSet() {
        return set;
    }

    public void setSet(int set) {
        this.set = set;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(String onlineState) {
        this.onlineState = onlineState;
    }

    public List<String> getAgentLinkList() {
        return agentLinkList;
    }

    public void setAgentLinkList(List<String> agentLinkList) {
        this.agentLinkList = agentLinkList;
    }
}
