package com.desheng.app.toucai.model;

import android.os.Parcel;
import android.os.Parcelable;

public class XunibiAddressBean implements Parcelable {

    /**
     * address : 0xf4C5F928d485d60091f9FEB1d494e666e831Ae7b
     * createTime : 1599301326000
     * id : 6
     * uin : 5927
     */

    private String address;
    private long createTime;
    private String id;
    private String uin;

    protected XunibiAddressBean(Parcel in) {
        address = in.readString();
        createTime = in.readLong();
        id = in.readString();
        uin = in.readString();
    }

    public static final Creator<XunibiAddressBean> CREATOR = new Creator<XunibiAddressBean>() {
        @Override
        public XunibiAddressBean createFromParcel(Parcel in) {
            return new XunibiAddressBean(in);
        }

        @Override
        public XunibiAddressBean[] newArray(int size) {
            return new XunibiAddressBean[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeLong(createTime);
        dest.writeString(id);
        dest.writeString(uin);
    }
}
