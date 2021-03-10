package com.desheng.app.toucai.model;

import java.io.Serializable;

/**
 * Created by user on 2018/3/27.
 */

public class UserDepositRecordbean implements Serializable {
  private String id ;
    private String billno ;
    private String accountId ;
    private String amount ;
    private String feeAmount ;
    private String actualAmount;
    private String balanceBefore ;
    private String balanceAfter ;

    private String method ;
    private String orderStatus ;
    private String orderTime ;
    private String payTime ;
    private String infos ;
    private String remarks;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBillno() {
    return billno;
  }

  public void setBillno(String billno) {
    this.billno = billno;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getFeeAmount() {
    return feeAmount;
  }

  public void setFeeAmount(String feeAmount) {
    this.feeAmount = feeAmount;
  }

  public String getActualAmount() {
    return actualAmount;
  }

  public void setActualAmount(String actualAmount) {
    this.actualAmount = actualAmount;
  }

  public String getBalanceBefore() {
    return balanceBefore;
  }

  public void setBalanceBefore(String balanceBefore) {
    this.balanceBefore = balanceBefore;
  }

  public String getBalanceAfter() {
    return balanceAfter;
  }

  public void setBalanceAfter(String balanceAfter) {
    this.balanceAfter = balanceAfter;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(String orderTime) {
    this.orderTime = orderTime;
  }

  public String getPayTime() {
    return payTime;
  }

  public void setPayTime(String payTime) {
    this.payTime = payTime;
  }

  public String getInfos() {
    return infos;
  }

  public void setInfos(String infos) {
    this.infos = infos;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }
}
