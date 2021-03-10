package com.desheng.app.toucai.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2018/3/7.
 */

public class MyAccountBean implements Parcelable {
    private String myAccountType;
    private  String title;




    protected MyAccountBean(Parcel in) {
    }

    public static final Creator<MyAccountBean> CREATOR = new Creator<MyAccountBean>() {
        @Override
        public MyAccountBean createFromParcel(Parcel in) {
            return new MyAccountBean(in);
        }

        @Override
        public MyAccountBean[] newArray(int size) {
            return new MyAccountBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public String getMyAccountType() {
        return myAccountType;
    }

    public void setMyAccountType(String myAccountType) {
        this.myAccountType = myAccountType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static Creator<MyAccountBean> getCREATOR() {
        return CREATOR;
    }
}
