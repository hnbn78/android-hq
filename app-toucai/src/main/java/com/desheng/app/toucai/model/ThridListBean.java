package com.desheng.app.toucai.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.desheng.base.model.RechargeInfo;

import java.util.List;


public class ThridListBean implements Parcelable {

    /**
     * amountInputType : 2
     * banklist : [{"bankId":"2","code":"CCB"},{"bankId":"1","code":"ICBC"},{"bankId":"3","code":"ABC"},{"bankId":"5","code":"BOC"},{"bankId":"6","code":"BOCM"},{"bankId":"4","code":"CMB"},{"bankId":"18","code":"CMBC"},{"bankId":"19","code":"CEB"},{"bankId":"20","code":"HXB"},{"bankId":"8","code":"CIB"},{"bankId":"7","code":"SPDB"},{"bankId":"9","code":"CITIC"},{"bankId":"15","code":"GDB"},{"bankId":"21","code":"BOB"},{"bankId":"12","code":"HZCB"},{"bankId":"10","code":"NBBANK"},{"bankId":"16","code":"PSBC"}]
     * code : 18
     * fixAmount :
     * id : 18
     * link : url
     * maxUnitRecharge : 50000.0
     * minUnitRecharge : 70.0
     * mobileActivityPicture :
     * mobileFriendlyPrompt :
     * mobileLogoPicture : /upload/channel/file_e98e12f8-7a6e-4458-b503-61fb731fe9e4
     * mobilePayPrompt :
     * name : 三方京东小数7
     * payChannelCategoryId : 6
     * pcActivityPicture :
     * pcFriendlyPrompt :
     * pcLogoPicture : /upload/channel/file_bf859817-022f-4430-b818-453161258080
     * pcPayPrompt :
     * pcPromptMessage :
     * phoneStatus : 0
     * rate : 0.01
     * sort : 7
     */

    private int amountInputType;
    private int code;
    private String fixAmount;
    private int id;
    private String link;
    private double maxUnitRecharge;
    private double minUnitRecharge;
    private String mobileActivityPicture;
    private String mobileFriendlyPrompt;
    private String mobileLogoPicture;
    private String mobilePayPrompt;
    private String name;
    private String payChannelCategoryId;
    private String pcActivityPicture;
    private String pcFriendlyPrompt;
    private String pcLogoPicture;
    private String pcPayPrompt;
    private String pcPromptMessage;
    private String phoneStatus;
    private String extraPayPrompt;
    private double rate;
    private int sort;
    private List<BanklistBean> banklist;
    private int isExtra;

    public int getIsExtra() {
        return isExtra;
    }

    public void setIsExtra(int isExtra) {
        this.isExtra = isExtra;
    }

    protected ThridListBean(Parcel in) {
        amountInputType = in.readInt();
        code = in.readInt();
        fixAmount = in.readString();
        id = in.readInt();
        link = in.readString();
        maxUnitRecharge = in.readDouble();
        minUnitRecharge = in.readDouble();
        mobileActivityPicture = in.readString();
        mobileFriendlyPrompt = in.readString();
        mobileLogoPicture = in.readString();
        mobilePayPrompt = in.readString();
        name = in.readString();
        payChannelCategoryId = in.readString();
        pcActivityPicture = in.readString();
        pcFriendlyPrompt = in.readString();
        pcLogoPicture = in.readString();
        pcPayPrompt = in.readString();
        pcPromptMessage = in.readString();
        phoneStatus = in.readString();
        extraPayPrompt = in.readString();
        rate = in.readDouble();
        sort = in.readInt();
        isExtra = in.readInt();
        banklist = in.createTypedArrayList(BanklistBean.CREATOR);
    }

    public String getExtraPayPrompt() {
        return extraPayPrompt;
    }

    public void setExtraPayPrompt(String extraPayPrompt) {
        this.extraPayPrompt = extraPayPrompt;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(amountInputType);
        dest.writeInt(code);
        dest.writeString(fixAmount);
        dest.writeInt(id);
        dest.writeString(link);
        dest.writeDouble(maxUnitRecharge);
        dest.writeDouble(minUnitRecharge);
        dest.writeString(mobileActivityPicture);
        dest.writeString(mobileFriendlyPrompt);
        dest.writeString(mobileLogoPicture);
        dest.writeString(mobilePayPrompt);
        dest.writeString(name);
        dest.writeString(payChannelCategoryId);
        dest.writeString(pcActivityPicture);
        dest.writeString(pcFriendlyPrompt);
        dest.writeString(pcLogoPicture);
        dest.writeString(pcPayPrompt);
        dest.writeString(pcPromptMessage);
        dest.writeString(phoneStatus);

        dest.writeString(extraPayPrompt);
        dest.writeDouble(rate);
        dest.writeInt(sort);
        dest.writeInt(isExtra);
        dest.writeTypedList(banklist);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ThridListBean> CREATOR = new Creator<ThridListBean>() {
        @Override
        public ThridListBean createFromParcel(Parcel in) {
            return new ThridListBean(in);
        }

        @Override
        public ThridListBean[] newArray(int size) {
            return new ThridListBean[size];
        }
    };

    public int getAmountInputType() {
        return amountInputType;
    }

    public void setAmountInputType(int amountInputType) {
        this.amountInputType = amountInputType;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getFixAmount() {
        return fixAmount;
    }

    public void setFixAmount(String fixAmount) {
        this.fixAmount = fixAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhoneStatus() {
        return phoneStatus;
    }

    public void setPhoneStatus(String phoneStatus) {
        this.phoneStatus = phoneStatus;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<BanklistBean> getBanklist() {
        return banklist;
    }

    public void setBanklist(List<BanklistBean> banklist) {
        this.banklist = banklist;
    }
}
