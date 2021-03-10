package com.desheng.base.model;

public enum ThirdGamePlatform {
    IM(6, "IM体育", "IM Sports", "file:///android_asset/ic_IM.webp"),
    AG(5, "AGIN视讯", "Asia Gaming", "file:///android_asset/ic_AG.webp"),
    GM(22, "GM棋牌", "Chess Card", "file:///android_asset/ic_AG.webp"),
    KY(7, "KY棋牌", "KY Chess", "file:///android_asset/ic_KY.webp");
 
    private int platformId = 0;
    private String title = "";
    private String titleEn = "";
    private String faceImg = "";
    
    ThirdGamePlatform(int platformId, String title, String titleEn, String faceImg) {
        this.platformId = platformId;
        this.title = title;
        this.titleEn = titleEn;
        this.faceImg = faceImg;
    }
    
    public static ThirdGamePlatform findById(int platformId) {
        ThirdGamePlatform type = null;
        for (int i = 0; i < values().length; i++) {
            ThirdGamePlatform bean = values()[i];
            if (bean.platformId == platformId) {
                type = bean;
                break;
            }
        }
        return type;
    }
    
    public static String []  getFaces(){
        String [] images = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            images[i] = values()[i].faceImg;
        }
        return images;
    }
    
    public int getPlatformId() {
        return platformId;
    }
    
    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitleEn() {
        return titleEn;
    }
    
    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }
    
    public String getFaceImg() {
        return faceImg;
    }
    
    public void setFaceImg(String faceImg) {
        this.faceImg = faceImg;
    }
}
