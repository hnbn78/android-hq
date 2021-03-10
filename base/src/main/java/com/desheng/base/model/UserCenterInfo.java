package com.desheng.base.model;

/**
 * Created by lee on 2018/5/2.
 */

public class UserCenterInfo {
    
    public AccountLoginLogBean accountLoginLog;
    public AccountBean account;
    
    
    public static class AccountLoginLogBean {
     
        public String address;
        public long loginTime;
        public String ip;
        public String client;
       
    }
    
    public static class AccountBean {
        
        public long registTime;
        public boolean googleBind;
        public long loginTime;
        public int onlineStatus;
        public String nickname;
        public boolean allowTransfer;
        public int type;
        public Object vipCode;
        public int bindStatus;
        public String username;
        public Object typeCode;
        
       
    }
}
