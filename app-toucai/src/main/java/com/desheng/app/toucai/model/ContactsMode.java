package com.desheng.app.toucai.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.desheng.app.toucai.panel.ActContacts;

public class ContactsMode implements MultiItemEntity,Parcelable {

    /**
     * cn : q01q01
     * inviteCode : 1fy7cd
     * isOnline : false
     * isParent : 0
     * unReadCount : 0
     */

    private String cn;
    private String uin;
    private boolean isOnline;
    private int isParent;
    private int isKefu;
    private int unReadCount;
    private String time;
    private String lastmsg;

    protected ContactsMode(Parcel in) {
        cn = in.readString();
        uin = in.readString();
        isOnline = in.readByte() != 0;
        isParent = in.readInt();
        isKefu = in.readInt();
        unReadCount = in.readInt();
        time = in.readString();
        lastmsg = in.readString();
    }

    public int getIsKefu() {
        return isKefu;
    }

    public void setIsKefu(int isKefu) {
        this.isKefu = isKefu;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getInviteCode() {
        return uin;
    }

    public void setInviteCode(String inviteCode) {
        this.uin = inviteCode;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getIsParent() {
        return isParent;
    }

    public void setIsParent(int isParent) {
        this.isParent = isParent;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }

    public static final Creator<ContactsMode> CREATOR = new Creator<ContactsMode>() {
        @Override
        public ContactsMode createFromParcel(Parcel in) {
            return new ContactsMode(in);
        }

        @Override
        public ContactsMode[] newArray(int size) {
            return new ContactsMode[size];
        }
    };

    @Override
    public int getItemType() {
        return ActContacts.TYPE_PERSON;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cn);
        dest.writeString(uin);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeInt(isParent);
        dest.writeInt(isKefu);
        dest.writeInt(unReadCount);
        dest.writeString(time);
        dest.writeString(lastmsg);
    }
}
