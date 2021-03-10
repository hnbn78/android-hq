package com.desheng.base.model;

import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;

import java.util.HashMap;

/**
 * Created by lee on 2018/3/7.
 */

public class LotteryInfo {
    
    /**
     {
     "code" : "GD11Y",
     "showName" : "广东11选5",
     "adver" : 0,
     "hot" : 0,
     "jumpUrl" : null,
     "appIcon" : null,
     "des" : null,
     "desIcon" : null,
     "pcIcon" : null,
     "showType" : 3,
     "id" : 24,
     "adverIcon" : null,
     "favorite" : 0
     },
     */
    
    private String code;
    private String showName;
    private int adver;
    private String adverIcon;
    private int favorite;
    private int hot;
    private String jumpUrl;
    private String appIcon;
    private String des;
    private String desIcon;
    private String pcIcon;
    private int showType;
    private int id;
    private String playCategory;
    private String category;
    private ILotteryKind kind;
    private HashMap<String, Object> extra;
    private String icon;

    @Override
    public String toString() {
        return "LotteryInfo{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                ", adver=" + adver +
                ", adverIcon='" + adverIcon + '\'' +
                ", favorite=" + favorite +
                ", hot=" + hot +
                ", jumpUrl='" + jumpUrl + '\'' +
                ", appIcon='" + appIcon + '\'' +
                ", des='" + des + '\'' +
                ", desIcon='" + desIcon + '\'' +
                ", pcIcon='" + pcIcon + '\'' +
                ", showType=" + showType +
                ", id=" + id +
                ", playCategory='" + playCategory + '\'' +
                ", category='" + category + '\'' +
                ", kind=" + kind +
                ", extra=" + extra +
                ", icon='" + icon + '\'' +
                '}';
    }

    public LotteryInfo(int id, String code, String showName, String icon) {
        this.code = code;
        this.showName = showName;
        this.id = id;
        this.icon = icon;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

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
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public ILotteryKind getKind() {
        if (kind == null) {
            kind = CtxLottery.getIns().findLotteryKind(id);
        }
        return kind;
    }
    
    public void setKind(ILotteryKind kind){
        this.kind = kind;
    }
    
    public HashMap<String, Object> getExtra() {
        if (extra == null) {
            extra = new HashMap<>();
        }
        return extra;
    }
    
    public String getPlayCategory() {
        return playCategory;
    }
    
    public void setPlayCategory(String playCategory) {
        this.playCategory = playCategory;
    }
    
    public void setExtra(HashMap<String, Object> extra) {
        this.extra = extra;
    }
    
    public String getIcon() {
        return appIcon;
    }
    
    
    public int getAdver() {
        return adver;
    }
    
    public void setAdver(int adver) {
        this.adver = adver;
    }
    
    public String getAdverIcon() {
        return adverIcon;
    }
    
    public void setAdverIcon(String adverIcon) {
        this.adverIcon = adverIcon;
    }
    
    public int getFavorite() {
        return favorite;
    }
    
    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
    
    public int getHot() {
        return hot;
    }
    
    public void setHot(int hot) {
        this.hot = hot;
    }
    
    public String getJumpUrl() {
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
    
    public int getShowType() {
        return showType;
    }
    
    public void setShowType(int showType) {
        this.showType = showType;
    }
}
