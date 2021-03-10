package com.desheng.app.toucai.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BanklistBean implements Parcelable {
    /**
     * bankId : 2
     * code : CCB
     */

    private String bankId;
    private String code;

    protected BanklistBean(Parcel in) {
        bankId = in.readString();
        code = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bankId);
        dest.writeString(code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BanklistBean> CREATOR = new Creator<BanklistBean>() {
        @Override
        public BanklistBean createFromParcel(Parcel in) {
            return new BanklistBean(in);
        }

        @Override
        public BanklistBean[] newArray(int size) {
            return new BanklistBean[size];
        }
    };

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
