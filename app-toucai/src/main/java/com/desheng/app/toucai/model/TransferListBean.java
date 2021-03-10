package com.desheng.app.toucai.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TransferListBean implements Parcelable {


    /**
     * amountInputType : 2
     * bankId : 3
     * bankName : 中国农业银行
     * bankType : 0
     * cardId : 623052
     * cardName : 线下支付宝小数12
     * fixAmount :
     * frontName : 线下支付宝小数12
     * id : 1
     * maxUnitRecharge : 30000.0
     * minUnitRecharge : 120.0
     * mobileActivityPicture :
     * mobileFriendlyPrompt :
     * mobileLogoPicture : /upload/remitbank/file_31c790a5-0867-478a-b4f9-75328e828276
     * mobilePayPrompt :
     * payChannelCategoryId : 2
     * pcActivityPicture :
     * pcFriendlyPrompt :
     * pcLogoPicture : /upload/remitbank/file_c19df20b-ff62-420c-ad5e-d8c5a8bb7856
     * pcPayPrompt :
     * pcPromptMessage :
     * place : 深圳滨河支行
     * sort : 12
     */

    private int amountInputType;
    private String bankId;
    private String bankName;
    private int bankType;
    private String cardId;
    private String cardName;
    private String fixAmount;
    private String frontName;
    private int id;
    private double maxUnitRecharge;
    private double minUnitRecharge;
    private String mobileActivityPicture;
    private String mobileFriendlyPrompt;
    private String mobileLogoPicture;
    private String mobilePayPrompt;
    private String payChannelCategoryId;
    private String pcActivityPicture;
    private String pcFriendlyPrompt;
    private String pcLogoPicture;
    private String pcPayPrompt;
    private String pcPromptMessage;
    private String place;
    private int sort;

    protected TransferListBean(Parcel in) {
        amountInputType = in.readInt();
        bankId = in.readString();
        bankName = in.readString();
        bankType = in.readInt();
        cardId = in.readString();
        cardName = in.readString();
        fixAmount = in.readString();
        frontName = in.readString();
        id = in.readInt();
        maxUnitRecharge = in.readDouble();
        minUnitRecharge = in.readDouble();
        mobileActivityPicture = in.readString();
        mobileFriendlyPrompt = in.readString();
        mobileLogoPicture = in.readString();
        mobilePayPrompt = in.readString();
        payChannelCategoryId = in.readString();
        pcActivityPicture = in.readString();
        pcFriendlyPrompt = in.readString();
        pcLogoPicture = in.readString();
        pcPayPrompt = in.readString();
        pcPromptMessage = in.readString();
        place = in.readString();
        sort = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(amountInputType);
        dest.writeString(bankId);
        dest.writeString(bankName);
        dest.writeInt(bankType);
        dest.writeString(cardId);
        dest.writeString(cardName);
        dest.writeString(fixAmount);
        dest.writeString(frontName);
        dest.writeInt(id);
        dest.writeDouble(maxUnitRecharge);
        dest.writeDouble(minUnitRecharge);
        dest.writeString(mobileActivityPicture);
        dest.writeString(mobileFriendlyPrompt);
        dest.writeString(mobileLogoPicture);
        dest.writeString(mobilePayPrompt);
        dest.writeString(payChannelCategoryId);
        dest.writeString(pcActivityPicture);
        dest.writeString(pcFriendlyPrompt);
        dest.writeString(pcLogoPicture);
        dest.writeString(pcPayPrompt);
        dest.writeString(pcPromptMessage);
        dest.writeString(place);
        dest.writeInt(sort);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransferListBean> CREATOR = new Creator<TransferListBean>() {
        @Override
        public TransferListBean createFromParcel(Parcel in) {
            return new TransferListBean(in);
        }

        @Override
        public TransferListBean[] newArray(int size) {
            return new TransferListBean[size];
        }
    };

    public int getAmountInputType() {
        return amountInputType;
    }

    public void setAmountInputType(int amountInputType) {
        this.amountInputType = amountInputType;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getBankType() {
        return bankType;
    }

    public void setBankType(int bankType) {
        this.bankType = bankType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getFixAmount() {
        return fixAmount;
    }

    public void setFixAmount(String fixAmount) {
        this.fixAmount = fixAmount;
    }

    public String getFrontName() {
        return frontName;
    }

    public void setFrontName(String frontName) {
        this.frontName = frontName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMaxUnitRecharge() {
        return maxUnitRecharge;
    }

    public void setMaxUnitRecharge(double maxUnitRecharge) {
        this.maxUnitRecharge = maxUnitRecharge;
    }

    public double getMinUnitRecharge() {
        return minUnitRecharge;
    }

    public void setMinUnitRecharge(double minUnitRecharge) {
        this.minUnitRecharge = minUnitRecharge;
    }

    public String getMobileActivityPicture() {
        return mobileActivityPicture;
    }

    public void setMobileActivityPicture(String mobileActivityPicture) {
        this.mobileActivityPicture = mobileActivityPicture;
    }

    public String getMobileFriendlyPrompt() {
        return mobileFriendlyPrompt;
    }

    public void setMobileFriendlyPrompt(String mobileFriendlyPrompt) {
        this.mobileFriendlyPrompt = mobileFriendlyPrompt;
    }

    public String getMobileLogoPicture() {
        return mobileLogoPicture;
    }

    public void setMobileLogoPicture(String mobileLogoPicture) {
        this.mobileLogoPicture = mobileLogoPicture;
    }

    public String getMobilePayPrompt() {
        return mobilePayPrompt;
    }

    public void setMobilePayPrompt(String mobilePayPrompt) {
        this.mobilePayPrompt = mobilePayPrompt;
    }

    public String getPayChannelCategoryId() {
        return payChannelCategoryId;
    }

    public void setPayChannelCategoryId(String payChannelCategoryId) {
        this.payChannelCategoryId = payChannelCategoryId;
    }

    public String getPcActivityPicture() {
        return pcActivityPicture;
    }

    public void setPcActivityPicture(String pcActivityPicture) {
        this.pcActivityPicture = pcActivityPicture;
    }

    public String getPcFriendlyPrompt() {
        return pcFriendlyPrompt;
    }

    public void setPcFriendlyPrompt(String pcFriendlyPrompt) {
        this.pcFriendlyPrompt = pcFriendlyPrompt;
    }

    public String getPcLogoPicture() {
        return pcLogoPicture;
    }

    public void setPcLogoPicture(String pcLogoPicture) {
        this.pcLogoPicture = pcLogoPicture;
    }

    public String getPcPayPrompt() {
        return pcPayPrompt;
    }

    public void setPcPayPrompt(String pcPayPrompt) {
        this.pcPayPrompt = pcPayPrompt;
    }

    public String getPcPromptMessage() {
        return pcPromptMessage;
    }

    public void setPcPromptMessage(String pcPromptMessage) {
        this.pcPromptMessage = pcPromptMessage;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
