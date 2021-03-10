package com.desheng.base.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by lee on 2018/1/18.
 */

public class OpenLottery implements MultiItemEntity {
    public static final int TYPE_LOTTERY = 1;
    
    private int itemType = 0;
    
    
    /**
     * openCode : 1,9,6,8,9
     * name : 东京1.5分彩
     * issueNo : 20180305-445
     * lotteryId : 601
     * status : 0
     */
    
    private String openCode;
    private String name;
    private String issueNo;
    private int lotteryId;
    private int status;
    private String appIcon;

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getOpenCode() {
        return openCode;
    }
    
    public void setOpenCode(String openCode) {
        this.openCode = openCode;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIssueNo() {
        return issueNo;
    }
    
    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }
    
    public int getLotteryId() {
        return lotteryId;
    }
    
    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public int getItemType() {
        return itemType;
    }
    
    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
