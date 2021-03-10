package com.desheng.base.model;

import com.ab.util.Strs;

import java.io.Serializable;
import java.util.List;

public class DepositCategory implements Serializable{
 
    //net
    public String mobileStatus;
    public String mobileLogoPicture;
    public String mobileActivityPicture;
    public String mobileCategoryPicture;
    public String id;
    public String sort;
    public String categoryName;
    public String status;
    
    public List<RechargeInfo.CommonChargeListBean> listCommon;
    
    public boolean isCategory(int id){
        return Strs.isEqual(this.id, Strs.of(id));
    }
    

}
