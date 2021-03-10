package com.desheng.base.model;

import java.io.Serializable;

public class RechargeSuccessBean implements Serializable{

    /**
     * amount : 56.0
     * attach : 张燕;德国
     * billno : CZ_201805300943173727693
     */

    private double amount;
    private String attach;
    private String billno;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }
}
