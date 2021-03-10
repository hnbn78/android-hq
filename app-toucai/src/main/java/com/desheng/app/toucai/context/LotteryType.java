package com.desheng.app.toucai.context;

import com.desheng.base.context.ILotteryType;

import java.util.LinkedHashMap;

/**
 * 彩种栏目信息
 * 固定栏目废弃, 采用网络获取的动态栏目 customMap.
 * Created by lee on 2018/3/8.
 */
public enum LotteryType implements ILotteryType {
    SSC("ssc", "时时彩", "ssc.png"),
    PK10("pk10", "PK10", "pk10.png"),
    FLB("flb", "菲律宾", "flb.png"),
    T11S5("11s5", "11选5", "11x5.png"),
    DPC("dpc", "低频彩", "dpc.png"),
    JD("jd", "经典", "jd.png"),
    OTHER("other", "其他", "other.png"),
    LHC("lhc", "六合彩", "other.png");
    
    public static final String [] CODE_INDEX = new String [] {
            "ssc",
            "pk10",
            "flb",
            "11s5",
            "dpc",
            "other",
            "jd",
            "lhc"
    };
    
    public static LinkedHashMap<String, ILotteryType> customMap = new LinkedHashMap<String, ILotteryType>();

    private String showName;
    private String code;
    private String image;
    
    LotteryType(String code, String name, String imgName) {
        this.showName = name;
        this.code = code;
        this.image = imgName;
    }
    
    public static ILotteryType find(String code) {
        ILotteryType type = OTHER;
        for (int i = 0; i < values().length; i++) {
            ILotteryType bean = values()[i];
            if (bean.getCode().equals(code)) {
                type = bean;
                break;
            }
        }
        return type;
    }
    
    @Override
    public String getShowName() {
        return showName;
    }
    
    @Override
    public void setShowName(String showName) {
        this.showName = showName;
    }
    
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public void setCode(String code) {
        this.code = code;
    }
    
    @Override
    public String getImage() {
        return "file:///android_asset/lottery_type/" + image;
    }
    
    
}
