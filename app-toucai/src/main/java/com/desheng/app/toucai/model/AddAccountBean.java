package com.desheng.app.toucai.model;

public class AddAccountBean {
    private String username;
    private String passwd;
    private String lotteryPoint;
    private int type;

    public AddAccountBean(String username, String passwd, String lotteryPoint, int type) {
        this.username = username;
        this.passwd = passwd;
        this.lotteryPoint = lotteryPoint;
        this.type = type;
    }
}
