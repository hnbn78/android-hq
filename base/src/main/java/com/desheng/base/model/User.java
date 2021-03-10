package com.desheng.base.model;

/**
 * Created by lee on 2018/2/28.
 */

public class User {
    
    /**
     * uin : 14
     * cn : gaoji02
     * userType : 1
     * sourceAppId : 1
     */
    
    /**
     * 用户ID
     */
    private int uin;
    
    /**
     * 用户名称
     */
    private String cn;
    
    /**
     * 用户类型  1:代理  0:用户
     */
    private int userType;
    
    /**
     *
     */
    private int sourceAppId;
    
    public int getUin() {
        return uin;
    }
    
    public void setUin(int uin) {
        this.uin = uin;
    }
    
    public String getCn() {
        return cn;
    }
    
    public void setCn(String cn) {
        this.cn = cn;
    }
    
    public int getUserType() {
        return userType;
    }
    
    public void setUserType(int userType) {
        this.userType = userType;
    }
    
    public int getSourceAppId() {
        return sourceAppId;
    }
    
    public void setSourceAppId(int sourceAppId) {
        this.sourceAppId = sourceAppId;
    }
}
