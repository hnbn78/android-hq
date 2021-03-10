package com.desheng.app.toucai.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class ChaseDetailBean implements Serializable{

    /**
     * id : 0
     * billno : 26715
     * account : test08
     * lottery : 重庆时时彩
     * startIssue : 20180830-049
     * endIssue : null
     * totalCount : 10
     * method : 后三码直选复式
     * content : 6|9|2,3
     * compress : null
     * nums : 2
     * model : yuan
     * code : null
     * point : 0
     * totalMoney : 20
     * orderTime : 1535609241000
     * status : 0
     * clearCount : 6
     * winMoney : 0
     * allowCancel : false
     * statusStr : 已完成
     * chaseList : [{"id":null,"billno":"20180830010Hek7Egk0001","account":"test08","type":0,"lottery":"重庆时时彩","issue":"20180830-049","method":"后三码直选复式","content":"6|9|2,3","compress":false,"nums":2,"multiple":1,"code":1960,"point":0,"money":2,"orderTime":1535609241000,"stopTime":0,"openTime":0,"status":1,"openCode":"4,9,5,3,7","winMoney":0,"clearTime":null,"allowCancel":false,"statusRemark":"已下单","model":"yuan","methodCode":"hsm_zx_fs","lotteryId":null,"statusMsg":null},{"id":null,"billno":"20180830010HlJ7Egk0002","account":"test08","type":0,"lottery":"重庆时时彩","issue":"20180830-050","method":"后三码直选复式","content":"6|9|2,3","compress":false,"nums":2,"multiple":1,"code":1960,"point":0,"money":2,"orderTime":1535609471000,"stopTime":0,"openTime":0,"status":1,"openCode":"3,6,9,2,9","winMoney":0,"clearTime":null,"allowCancel":false,"statusRemark":"已下单","model":"yuan","methodCode":"hsm_zx_fs","lotteryId":null,"statusMsg":null}]
     */

    public int id;
    public String billno;
    public String account;
    public String lottery;
    public String startIssue;
    public Object endIssue;
    public int totalCount;
    public String method;
    public String content;
    public Object compress;
    public int nums;
    public String model;
    public Object code;
    public int point;
    public double totalMoney;
    public long orderTime;
    public int status;
    public int clearCount;
    public double winMoney;
    public boolean allowCancel;
    public String statusStr;
    public ArrayList<ChaseListBean> chaseList;

  
    public static class ChaseListBean implements Parcelable {
        /**
         * id : null
         * billno : 20180830010Hek7Egk0001
         * account : test08
         * type : 0
         * lottery : 重庆时时彩
         * issue : 20180830-049
         * method : 后三码直选复式
         * content : 6|9|2,3
         * compress : false
         * nums : 2
         * multiple : 1
         * code : 1960
         * point : 0
         * money : 2
         * orderTime : 1535609241000
         * stopTime : 0
         * openTime : 0
         * status : 1
         * openCode : 4,9,5,3,7
         * winMoney : 0
         * clearTime : null
         * allowCancel : false
         * statusRemark : 已下单
         * model : yuan
         * methodCode : hsm_zx_fs
         * lotteryId : null
         * statusMsg : null
         */

        public String id;
        public String billno;
        public String account;
        public int type;
        public String lottery;
        public String issue;
        public String method;
        public String content;
        public boolean compress;
        public int nums;
        public int multiple;
        public int code;
        public int point;
        public double money;
        public long orderTime;
        public int stopTime;
        public int openTime;
        public int status;
        public String openCode;
        public double winMoney;
        public String clearTime;
        public boolean allowCancel;
        public String statusRemark;
        public String model;
        public String methodCode;
        public String lotteryId;
        public String statusMsg;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.billno);
            dest.writeString(this.account);
            dest.writeInt(this.type);
            dest.writeString(this.lottery);
            dest.writeString(this.issue);
            dest.writeString(this.method);
            dest.writeString(this.content);
            dest.writeByte(this.compress ? (byte) 1 : (byte) 0);
            dest.writeInt(this.nums);
            dest.writeInt(this.multiple);
            dest.writeInt(this.code);
            dest.writeInt(this.point);
            dest.writeDouble(this.money);
            dest.writeLong(this.orderTime);
            dest.writeInt(this.stopTime);
            dest.writeInt(this.openTime);
            dest.writeInt(this.status);
            dest.writeString(this.openCode);
            dest.writeDouble(this.winMoney);
            dest.writeString(this.clearTime);
            dest.writeByte(this.allowCancel ? (byte) 1 : (byte) 0);
            dest.writeString(this.statusRemark);
            dest.writeString(this.model);
            dest.writeString(this.methodCode);
            dest.writeString(this.lotteryId);
            dest.writeString(this.statusMsg);
        }

        public ChaseListBean() {
        }

        protected ChaseListBean(Parcel in) {
            this.id = in.readString();
            this.billno = in.readString();
            this.account = in.readString();
            this.type = in.readInt();
            this.lottery = in.readString();
            this.issue = in.readString();
            this.method = in.readString();
            this.content = in.readString();
            this.compress = in.readByte() != 0;
            this.nums = in.readInt();
            this.multiple = in.readInt();
            this.code = in.readInt();
            this.point = in.readInt();
            this.money = in.readDouble();
            this.orderTime = in.readLong();
            this.stopTime = in.readInt();
            this.openTime = in.readInt();
            this.status = in.readInt();
            this.openCode = in.readString();
            this.winMoney = in.readDouble();
            this.clearTime = in.readString();
            this.allowCancel = in.readByte() != 0;
            this.statusRemark = in.readString();
            this.model = in.readString();
            this.methodCode = in.readString();
            this.lotteryId = in.readString();
            this.statusMsg = in.readString();
        }

        public static final Parcelable.Creator<ChaseListBean> CREATOR = new Parcelable.Creator<ChaseListBean>() {
            @Override
            public ChaseListBean createFromParcel(Parcel source) {
                return new ChaseListBean(source);
            }

            @Override
            public ChaseListBean[] newArray(int size) {
                return new ChaseListBean[size];
            }
        };
    }
}
