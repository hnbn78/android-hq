package com.desheng.base.context;

import com.desheng.base.model.LotteryInfo;

/**
 * Created by lee on 2018/4/13.
 */

public interface ILotteryKind {
    
    int getId();
    
    void setId(int id);
    
    String getId168();
    
    void setId168(String id168);
    
    String getShowName();
    
    void setShowName(String showName);
    
    String getCode();
    
    void setCode(String code);
    
    boolean isEnabled();
    
    LotteryInfo getLotteryInfo();
    
    void updateAndEnable(LotteryInfo info);
    
    int getIssueShowType();
    
    void setIssueShowType(int issueShowType);
    
    int[] getArrIssueShowFunc();
    
    void setArrIssueShowFunc(int[] arrIssueShowFunc);
    
    ILotteryType getOriginType();
    
    void setOriginType(ILotteryType originType);
    
    String getIcon();
    
    String getIconActive();
    
    String getIconInactive();
    
    void setIcon(String icon);

    String getPlayCategory();
    
}
