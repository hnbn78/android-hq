package com.desheng.app.toucai.model;

public class GudongTeamBetHistoryListBean {
    /**
     * amount : 10.0
     * bonus : 19.6
     * cn : test0023
     * issue_no : 20190319-1619
     * lottery_id : 46
     * lottery_name : 多伦多30秒
     * lottery_status : 已发放
     * method_id : 6041
     * method_name : 总和大小单双
     * rebound_amount : 19.6
     * user_id : 67493
     */

    private double amount;
    private double bonus;
    private String cn;
    private String issue_no;
    private int lottery_id;
    private String order_item_id;
    private String lottery_name;
    private String lottery_status;
    private int method_id;
    private String method_name;
    private double rebound_amount;
    private int user_id;

    public String getOrder_item_id() {
        return order_item_id;
    }

    public void setOrder_item_id(String order_item_id) {
        this.order_item_id = order_item_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getIssue_no() {
        return issue_no;
    }

    public void setIssue_no(String issue_no) {
        this.issue_no = issue_no;
    }

    public int getLottery_id() {
        return lottery_id;
    }

    public void setLottery_id(int lottery_id) {
        this.lottery_id = lottery_id;
    }

    public String getLottery_name() {
        return lottery_name;
    }

    public void setLottery_name(String lottery_name) {
        this.lottery_name = lottery_name;
    }

    public String getLottery_status() {
        return lottery_status;
    }

    public void setLottery_status(String lottery_status) {
        this.lottery_status = lottery_status;
    }

    public int getMethod_id() {
        return method_id;
    }

    public void setMethod_id(int method_id) {
        this.method_id = method_id;
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }

    public double getRebound_amount() {
        return rebound_amount;
    }

    public void setRebound_amount(double rebound_amount) {
        this.rebound_amount = rebound_amount;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
