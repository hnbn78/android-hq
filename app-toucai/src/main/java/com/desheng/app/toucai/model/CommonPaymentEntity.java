package com.desheng.app.toucai.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class CommonPaymentEntity implements Parcelable, Comparable<CommonPaymentEntity> {

    private String showName;
    private String icon;
    private String payChannelCategoryId;
    private int type;
    private Integer sort;
    private boolean isSelected;
    private QrCodeListBean qrCodeListBean;
    private ThridListBean thridListBean;
    private TransferListBean transferListBean;

    public CommonPaymentEntity(String showName, String icon, String payChannelCategoryId, int type, Integer sort) {
        this.showName = showName;
        this.icon = icon;
        this.payChannelCategoryId = payChannelCategoryId;
        this.type = type;
        this.sort = sort;
    }

    protected CommonPaymentEntity(Parcel in) {
        showName = in.readString();
        icon = in.readString();
        payChannelCategoryId = in.readString();
        type = in.readInt();
        sort = in.readInt();
        isSelected = in.readByte() != 0;
        thridListBean = in.readParcelable(ThridListBean.class.getClassLoader());
        transferListBean = in.readParcelable(TransferListBean.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(showName);
        dest.writeString(icon);
        dest.writeString(payChannelCategoryId);
        dest.writeInt(type);
        dest.writeInt(sort);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeParcelable(thridListBean, flags);
        dest.writeParcelable(transferListBean, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommonPaymentEntity> CREATOR = new Creator<CommonPaymentEntity>() {
        @Override
        public CommonPaymentEntity createFromParcel(Parcel in) {
            return new CommonPaymentEntity(in);
        }

        @Override
        public CommonPaymentEntity[] newArray(int size) {
            return new CommonPaymentEntity[size];
        }
    };

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPayChannelCategoryId() {
        return payChannelCategoryId;
    }

    public void setPayChannelCategoryId(String payChannelCategoryId) {
        this.payChannelCategoryId = payChannelCategoryId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public QrCodeListBean getQrCodeListBean() {
        return qrCodeListBean;
    }

    public void setQrCodeListBean(QrCodeListBean qrCodeListBean) {
        this.qrCodeListBean = qrCodeListBean;
    }

    public ThridListBean getThridListBean() {
        return thridListBean;
    }

    public void setThridListBean(ThridListBean thridListBean) {
        this.thridListBean = thridListBean;
    }

    public TransferListBean getTransferListBean() {
        return transferListBean;
    }

    public void setTransferListBean(TransferListBean transferListBean) {
        this.transferListBean = transferListBean;
    }

    @Override
    public int compareTo(@NonNull CommonPaymentEntity o) {
        return this.sort.compareTo(o.getSort());
    }
}
