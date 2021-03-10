package com.desheng.app.toucai.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2018/3/14.
 */

public class UserBankEntity implements Parcelable {
    private String userBankId;
    private String accountName;
    private String bankName;
    private String bankNo;
    private String openBankName;


    protected UserBankEntity(Parcel in) {

        this.userBankId = in.readString();
        this.accountName = in.readString();
        this.bankName = in.readString();
        this.bankNo = in.readString();
        this.openBankName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userBankId);
        dest.writeString(this.accountName);
        dest.writeString(this.bankName);
        dest.writeString(this.bankNo);
        dest.writeString(this.openBankName);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserBankEntity> CREATOR = new Creator<UserBankEntity>() {
        @Override
        public UserBankEntity createFromParcel(Parcel in) {

            return new UserBankEntity(in);
        }

        @Override
        public UserBankEntity[] newArray(int size) {
            return new UserBankEntity[size];
        }
    };

    public String getUserBankId() {
        return userBankId;
    }

    public void setUserBankId(String userBankId) {
        this.userBankId = userBankId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getOpenBankName() {
        return openBankName;
    }

    public void setOpenBankName(String openBankName) {
        this.openBankName = openBankName;
    }

    public static Creator<UserBankEntity> getCREATOR() {
        return CREATOR;
    }
}
