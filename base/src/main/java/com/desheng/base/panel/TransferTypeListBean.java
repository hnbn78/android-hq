package com.desheng.base.panel;

import android.os.Parcel;
import android.os.Parcelable;

public class TransferTypeListBean implements Parcelable {
    /**
     * name : 充值
     * type : 2
     */

    private String name;
    private String type;

    protected TransferTypeListBean(Parcel in) {
        name = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransferTypeListBean> CREATOR = new Creator<TransferTypeListBean>() {
        @Override
        public TransferTypeListBean createFromParcel(Parcel in) {
            return new TransferTypeListBean(in);
        }

        @Override
        public TransferTypeListBean[] newArray(int size) {
            return new TransferTypeListBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
