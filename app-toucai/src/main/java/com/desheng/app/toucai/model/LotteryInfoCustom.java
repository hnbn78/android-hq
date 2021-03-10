package com.desheng.app.toucai.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.desheng.base.model.LotteryInfo;

public class LotteryInfoCustom implements Parcelable {
    public int getThirdPlatformId() {
        return thirdPlatformId;
    }

    public void setThirdPlatformId(int thirdPlatformId) {
        this.thirdPlatformId = thirdPlatformId;
    }

    /**
     * code : GD11Y
     * showName : 广东11选5
     * adver : 0
     * hot : 0
     * jumpUrl : null
     * appIcon : null
     * des : null
     * desIcon : null
     * pcIcon : null
     * showType : 3
     * id : 24
     * adverIcon : null
     * favorite : 0

     */
    
    private String code;
    private String showName;
    private int adver;
    private int hot;
    private String jumpUrl;
    private String appIcon;
    private String des;
    private String desIcon;
    private String pcIcon;
    private int showType;
    private int id;
    private String adverIcon;
    private int favorite;
    private int thirdPlatformId;

    public LotteryInfoCustom(){

    }

    public LotteryInfoCustom(LotteryInfo info) {
        this.id = info.getId();
        this.showName = info.getShowName();
        this.appIcon = info.getAppIcon();
        this.code = info.getCode();
    }

    protected LotteryInfoCustom(Parcel in) {
        code = in.readString();
        showName = in.readString();
        adver = in.readInt();
        hot = in.readInt();
        jumpUrl = in.readString();
        appIcon = in.readString();
        des = in.readString();
        desIcon = in.readString();
        pcIcon = in.readString();
        showType = in.readInt();
        id = in.readInt();
        adverIcon = in.readString();
        favorite = in.readInt();
    }

    public static final Creator<LotteryInfoCustom> CREATOR = new Creator<LotteryInfoCustom>() {
        @Override
        public LotteryInfoCustom createFromParcel(Parcel in) {
            return new LotteryInfoCustom(in);
        }

        @Override
        public LotteryInfoCustom[] newArray(int size) {
            return new LotteryInfoCustom[size];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public int getAdver() {
        return adver;
    }

    public void setAdver(int adver) {
        this.adver = adver;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public Object getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getDesIcon() {
        return desIcon;
    }

    public void setDesIcon(String desIcon) {
        this.desIcon = desIcon;
    }

    public String getPcIcon() {
        return pcIcon;
    }

    public void setPcIcon(String pcIcon) {
        this.pcIcon = pcIcon;
    }

    public void setAdverIcon(String adverIcon) {
        this.adverIcon = adverIcon;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getAdverIcon() {
        return adverIcon;
    }


    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public LotteryInfo toInfo() {
        LotteryInfo info = new LotteryInfo(id, code, showName, getAppIcon());
        return info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(showName);
        dest.writeInt(adver);
        dest.writeInt(hot);
        dest.writeString(jumpUrl);
        dest.writeString(appIcon);
        dest.writeString(des);
        dest.writeString(desIcon);
        dest.writeString(pcIcon);
        dest.writeInt(showType);
        dest.writeInt(id);
        dest.writeString(adverIcon);
        dest.writeInt(favorite);
    }
}
