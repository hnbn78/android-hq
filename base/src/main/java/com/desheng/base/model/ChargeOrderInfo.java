package com.desheng.base.model;

public class ChargeOrderInfo {

    /**
     * bankId : 110
     * amount : 500.0
     * link : /py/recharge
     * text : e8d03cda2be8fbf98df617045119876e90eb2988564b00b7f6bcd7b0525a7ded20fe016b36342e30f7945018b1c0d729447eb90bb3fdf282ae057994b4ed88ab1c7ee4629af49194f5c74ad5a69f7a9f63a39d21a0cc3fc3bb650717aaae3d770c7f259cfe19d69610365249f56be99f239260373e2e1d8ba17eb4f75eaff033ea52809ff3517313996a6b9691c34873e32ada1f7f469dbc618364bb0ec2627a992c5d0093c3fb97966c4e09aaea15ecdc35c9442d6b3f757574fabfb9eb8a5a3b74bb2524f877e37f1b5de92cdd30b1
     * billno : CZ_201805141734002168403
     */

    private String bankId;
    private String amount;
    private String link;
    private String text;
    private String billno;

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }
}
