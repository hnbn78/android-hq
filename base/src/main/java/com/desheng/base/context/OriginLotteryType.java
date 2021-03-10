package com.desheng.base.context;

/**
 * 原始分类, 与彩宏一直, 彩票第三方功能等使用
 * Created by lee on 2018/3/8.
 */

public enum OriginLotteryType implements ILotteryType{
    
    SSC("ssc", "时时彩", ""),
    T11S5("11s5", "11选5", ""),
    KUAI3("kuai3", "快三", ""),
    JD("jd", "经典", ""),
    LHC("lhc", "六合彩", ""),
    OTHER("other", "其他", "");
    
    private String showName;
    private String code;
    private String image;
    
    
    OriginLotteryType(String code, String name, String image) {
        this.showName = name;
        this.code = code;
        this.image = image;
    }
    
    public static ILotteryType find(String code) {
        OriginLotteryType type = OTHER;
        for (int i = 0; i < values().length; i++) {
            OriginLotteryType bean = values()[i];
            if (bean.code.equals(code)) {
                type = bean;
                break;
            }
        }
        return type;
    }
    
    public String getShowName() {
        return showName;
    }
    
    public void setShowName(String showName) {
        this.showName = showName;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    @Override
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
}
