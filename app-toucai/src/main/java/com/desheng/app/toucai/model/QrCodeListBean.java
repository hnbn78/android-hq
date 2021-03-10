package com.desheng.app.toucai.model;

import android.os.Parcel;
import android.os.Parcelable;

public class QrCodeListBean implements Parcelable {

    private double maxUnitRecharge;
    private String pcPayPrompt;
    private int amountInputType;
    private String codeType;
    private String fileByte;
    private String nickName;
    private String mobileLogoPicture;
    private String mobileActivityPicture;
    private String fixAmount;
    private String mobileFriendlyPrompt;
    private int sort;
    private String pcPromptMessage;
    private String pcActivityPicture;
    private String pcLogoPicture;
    private String id;
    private String state;
    private String payChannelCategoryId;
    private String pcFriendlyPrompt;
    private double minUnitRecharge;
    private String account;
    private Object fileId;
    private String mobilePayPrompt;

    protected QrCodeListBean(Parcel in) {
        maxUnitRecharge = in.readDouble();
        pcPayPrompt = in.readString();
        amountInputType = in.readInt();
        codeType = in.readString();
        fileByte = in.readString();
        nickName = in.readString();
        mobileLogoPicture = in.readString();
        mobileActivityPicture = in.readString();
        fixAmount = in.readString();
        mobileFriendlyPrompt = in.readString();
        sort = in.readInt();
        pcPromptMessage = in.readString();
        pcActivityPicture = in.readString();
        pcLogoPicture = in.readString();
        id = in.readString();
        state = in.readString();
        payChannelCategoryId = in.readString();
        pcFriendlyPrompt = in.readString();
        minUnitRecharge = in.readDouble();
        account = in.readString();
        mobilePayPrompt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(maxUnitRecharge);
        dest.writeString(pcPayPrompt);
        dest.writeInt(amountInputType);
        dest.writeString(codeType);
        dest.writeString(fileByte);
        dest.writeString(nickName);
        dest.writeString(mobileLogoPicture);
        dest.writeString(mobileActivityPicture);
        dest.writeString(fixAmount);
        dest.writeString(mobileFriendlyPrompt);
        dest.writeInt(sort);
        dest.writeString(pcPromptMessage);
        dest.writeString(pcActivityPicture);
        dest.writeString(pcLogoPicture);
        dest.writeString(id);
        dest.writeString(state);
        dest.writeString(payChannelCategoryId);
        dest.writeString(pcFriendlyPrompt);
        dest.writeDouble(minUnitRecharge);
        dest.writeString(account);
        dest.writeString(mobilePayPrompt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QrCodeListBean> CREATOR = new Creator<QrCodeListBean>() {
        @Override
        public QrCodeListBean createFromParcel(Parcel in) {
            return new QrCodeListBean(in);
        }

        @Override
        public QrCodeListBean[] newArray(int size) {
            return new QrCodeListBean[size];
        }
    };

    public double getMaxUnitRecharge() {
        return maxUnitRecharge;
    }

    public void setMaxUnitRecharge(double maxUnitRecharge) {
        this.maxUnitRecharge = maxUnitRecharge;
    }

    public String getPcPayPrompt() {
        return pcPayPrompt;
    }

    public void setPcPayPrompt(String pcPayPrompt) {
        this.pcPayPrompt = pcPayPrompt;
    }

    public int getAmountInputType() {
        return amountInputType;
    }

    public void setAmountInputType(int amountInputType) {
        this.amountInputType = amountInputType;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getFileByte() {
        return fileByte;
    }

    public void setFileByte(String fileByte) {
        this.fileByte = fileByte;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobileLogoPicture() {
        return mobileLogoPicture;
    }

    public void setMobileLogoPicture(String mobileLogoPicture) {
        this.mobileLogoPicture = mobileLogoPicture;
    }

    public String getMobileActivityPicture() {
        return mobileActivityPicture;
    }

    public void setMobileActivityPicture(String mobileActivityPicture) {
        this.mobileActivityPicture = mobileActivityPicture;
    }

    public String getFixAmount() {
        return fixAmount;
    }

    public void setFixAmount(String fixAmount) {
        this.fixAmount = fixAmount;
    }

    public String getMobileFriendlyPrompt() {
        return mobileFriendlyPrompt;
    }

    public void setMobileFriendlyPrompt(String mobileFriendlyPrompt) {
        this.mobileFriendlyPrompt = mobileFriendlyPrompt;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getPcPromptMessage() {
        return pcPromptMessage;
    }

    public void setPcPromptMessage(String pcPromptMessage) {
        this.pcPromptMessage = pcPromptMessage;
    }

    public String getPcActivityPicture() {
        return pcActivityPicture;
    }

    public void setPcActivityPicture(String pcActivityPicture) {
        this.pcActivityPicture = pcActivityPicture;
    }

    public String getPcLogoPicture() {
        return pcLogoPicture;
    }

    public void setPcLogoPicture(String pcLogoPicture) {
        this.pcLogoPicture = pcLogoPicture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPayChannelCategoryId() {
        return payChannelCategoryId;
    }

    public void setPayChannelCategoryId(String payChannelCategoryId) {
        this.payChannelCategoryId = payChannelCategoryId;
    }

    public String getPcFriendlyPrompt() {
        return pcFriendlyPrompt;
    }

    public void setPcFriendlyPrompt(String pcFriendlyPrompt) {
        this.pcFriendlyPrompt = pcFriendlyPrompt;
    }

    public double getMinUnitRecharge() {
        return minUnitRecharge;
    }

    public void setMinUnitRecharge(double minUnitRecharge) {
        this.minUnitRecharge = minUnitRecharge;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Object getFileId() {
        return fileId;
    }

    public void setFileId(Object fileId) {
        this.fileId = fileId;
    }

    public String getMobilePayPrompt() {
        return mobilePayPrompt;
    }

    public void setMobilePayPrompt(String mobilePayPrompt) {
        this.mobilePayPrompt = mobilePayPrompt;
    }

}
