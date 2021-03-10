package com.desheng.app.toucai.context;

import android.content.pm.ActivityInfo;

import com.shark.tc.R;

import java.io.Serializable;

public enum ThirdGamePlatform implements Serializable {
    AGIN(true, 5, "agin_01", "AGIN视讯", "Asia Gaming", "file:///android_asset/ic_AG.webp", R.mipmap.ic_ag_small, R.mipmap.ic_pocket_ag, R.mipmap.ic_transfer_ag, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT),
    IM(true, 6, "im_01", "IM体育", "IM Sports", "file:///android_asset/ic_IM.webp", R.mipmap.ic_im_small, R.mipmap.ic_pocket_im, R.mipmap.ic_transfer_im, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT),
    KY(true, 24, "ky_01", "KY棋牌", "KY Chess", "file:///android_asset/ic_KY.webp", R.mipmap.ic_ky_small, R.mipmap.ic_pocket_ky, R.mipmap.ic_transfer_ky, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
    TCVR(true, 14, "111", "天彩VR", "Tiancai VR", "file:///android_asset/ic_VR.webp", R.mipmap.ic_vr, R.mipmap.ic_vr, R.mipmap.ic_vr, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT),
    OG(true, 25, "", "OG真人", "Tiancai VR", "file:///android_asset/ic_og.png", R.mipmap.ic_og, R.mipmap.ic_og, R.mipmap.ic_og, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
    BBIN(true, 26, "", "BBIN真人", "Tiancai VR", "file:///android_asset/ic_bbin.png", R.mipmap.ic_bbin, R.mipmap.ic_bbin, R.mipmap.ic_bbin, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
    DS(true, 22, "gm_01", "DS棋牌", "DS Chess", "file:///android_asset/ic_AG.webp", R.mipmap.ds_zhuanzhang, R.mipmap.ic_pocket_ds, R.mipmap.ic_transfer_ds, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
    CQ(true, 10, "cq_01", "CQ9电游", "CQ Game", "file:///android_asset/ic_AG.webp", R.mipmap.ic_pocket_cq9, R.mipmap.ic_pocket_cq9, R.mipmap.ic_pocket_cq9, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
    PT(true, 27, "ptds_01", "PT电游", "PTDS Game", "file:///android_asset/ic_pt.png", R.mipmap.ic_pt, R.mipmap.ic_pt, R.mipmap.ic_transfer_pt, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
    LEG(true, 23, "leg_01", "LEG棋牌", "LEG Chess", "file:///android_asset/ic_leg.png", R.mipmap.ic_leg, R.mipmap.ic_leg, R.mipmap.ic_leg, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
    DT(true, 28, "dt_01", "大唐棋牌", "DT Chess", "file:///android_asset/ic_datang.png", R.mipmap.ic_datang, R.mipmap.ic_datang, R.mipmap.ic_datang, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    private int platformId = 0;
    private String cbId = "";
    private String title = "";
    private String titleEn = "";
    private String faceImg = "";
    private int icon = 0;
    private int pocketIcon = 0;
    private int transferImag = 0;
    private int screenOrintation = 0;
    private boolean enable = false;

    ThirdGamePlatform(int platformId, String title, String titleEn, String faceImg) {
        this.platformId = platformId;
        this.title = title;
        this.titleEn = titleEn;
        this.faceImg = faceImg;
    }

    ThirdGamePlatform(boolean enable, int platformId, String cbId, String title, String titleEn, String faceImg, int icon, int pocketIcon, int transferImg, int screenOrintation) {
        this.enable = enable;
        this.platformId = platformId;
        this.cbId = cbId;
        this.title = title;
        this.titleEn = titleEn;
        this.faceImg = faceImg;
        this.transferImag = transferImg;
        this.icon = icon;
        this.pocketIcon = pocketIcon;
        this.screenOrintation = screenOrintation;
    }

    public static ThirdGamePlatform findByPlatformId(int platformId) {
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

    public static ThirdGamePlatform findByCbId(String cbId) {
        ThirdGamePlatform type = null;
        for (int i = 0; i < values().length; i++) {
            ThirdGamePlatform bean = values()[i];
            if (bean.cbId.equals(cbId)) {
                type = bean;
                break;
            }
        }
        return type;
    }


    public static String[] getFaces() {
        String[] images = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            images[i] = values()[i].faceImg;
        }
        return images;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getPlatformId() {
        return platformId;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public String getFaceImg() {
        return faceImg;
    }

    public int getIcon() {
        return icon;
    }

    public String getCbId() {
        return cbId;
    }

    public int getScreenOrintation() {
        return screenOrintation;
    }

    public int getPocketIcon() {
        return pocketIcon;
    }

    public int getTransferImag() {
        return transferImag;
    }
}
