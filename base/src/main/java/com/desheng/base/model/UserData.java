package com.desheng.base.model;

/**
 * Created by lee on 2018/2/28.
 */

public class UserData {
    /**
     * isLogin : true
     * msgCount : 0
     * main : {"username":"test0001","nickname":"test0001","type":1,"registTime":1501700384000,"loginTime":1502687519000,"lockTime":0,"status":0,"onlineStatus":1,"bindStatus":1,"typeCode":"0004","allowTransfer":1,"googleBind":false,"vipCode":null}
     * lottery : {"availableBalance":18699.8766,"blockedBalance":203,"code":1956,"point":12.8,"codeType":0,"extraPoint":null,"playStatus":0,"allowEqualCode":false,"isDividendAccount":true}
     * info : {"gender":0,"birthday":null,"cellphone":null,"qq":null,"email":null,"avatar":null,"navigate":0}
     */
    
 
    /**
     * 是否登录成功
      */
    private boolean isLogin;
    /**
     * 未读站内信条数
     */
    private int msgCount;
    private Main main;
    private Lottery lottery;
    private Info info;
    private int  wcType;

    public int getWcType() {
        return wcType;
    }

    public void setWcType(int wcType) {
        this.wcType = wcType;
    }

    public boolean isLogin() {
        return isLogin;
    }
    
    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
    
    public int getMsgCount() {
        return msgCount;
    }
    
    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }
    
    public Main getMain() {
        return main;
    }
    
    public void setMain(Main main) {
        this.main = main;
    }
    
    public Lottery getLottery() {
        return lottery;
    }
    
    public void setLottery(Lottery lottery) {
        this.lottery = lottery;
    }
    
    public Info getInfo() {
        return info;
    }
    
    public void setInfo(Info info) {
        this.info = info;
    }
    
    public static class Main {
        /**
         * username : test0001
         * nickname : test0001
         * type : 1
         * registTime : 1501700384000
         * loginTime : 1502687519000
         * lockTime : 0
         * status : 0
         * onlineStatus : 1
         * bindStatus : 1
         * typeCode : 0004
         * allowTransfer : 1
         * googleBind : false
         * vipCode : null
         */
        //
        //
        //
        //
        //
        //
        //
     
        /**
         * 用户名
         */
        private String username;
        /**
         * 昵称
         */
        private String nickname;
        /**
         * 用户类型  1：代理  0：普通用户
         */
        private int type;
        /**
         * 注册时间
         */
        private long registTime;
        /**
         * 最后一次登录时间
         */
        private long loginTime;
        
        private int lockTime;
        private int status;
        private int onlineStatus;
        private int bindStatus;
        private String typeCode;
        /**
         * 是否可以上下级转账  1：可以 0 不可以
         */
        private int allowTransfer;
        private boolean googleBind;
        private String vipCode;
        private int userLevel;
        private int zdType;

        public int getZdType() {
            return zdType;
        }

        public void setZdType(int zdType) {
            this.zdType = zdType;
        }

        private double userAgentType;

        public double getUserAgentType() {
            return userAgentType;
        }

        public void setUserAgentType(double userAgentType) {
            this.userAgentType = userAgentType;
        }

        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getNickname() {
            return nickname;
        }
        
        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
        
        public int getType() {
            return type;
        }
        
        public void setType(int type) {
            this.type = type;
        }
        
        public long getRegistTime() {
            return registTime;
        }
        
        public void setRegistTime(long registTime) {
            this.registTime = registTime;
        }
        
        public long getLoginTime() {
            return loginTime;
        }
        
        public void setLoginTime(long loginTime) {
            this.loginTime = loginTime;
        }
        
        public int getLockTime() {
            return lockTime;
        }
        
        public void setLockTime(int lockTime) {
            this.lockTime = lockTime;
        }
        
        public int getStatus() {
            return status;
        }
        
        public void setStatus(int status) {
            this.status = status;
        }
        
        public int getOnlineStatus() {
            return onlineStatus;
        }
        
        public void setOnlineStatus(int onlineStatus) {
            this.onlineStatus = onlineStatus;
        }
        
        public int getBindStatus() {
            return bindStatus;
        }
        
        public void setBindStatus(int bindStatus) {
            this.bindStatus = bindStatus;
        }
        
        public String getTypeCode() {
            return typeCode;
        }
        
        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }
        
        public int getAllowTransfer() {
            return allowTransfer;
        }
        
        public void setAllowTransfer(int allowTransfer) {
            this.allowTransfer = allowTransfer;
        }
        
        public boolean isGoogleBind() {
            return googleBind;
        }
        
        public void setGoogleBind(boolean googleBind) {
            this.googleBind = googleBind;
        }
        
        public String getVipCode() {
            return vipCode;
        }
        
        public void setVipCode(String vipCode) {
            this.vipCode = vipCode;
        }

        public int getUserLevel() {
            return userLevel;
        }

        public void setUserLevel(int userLevel) {
            this.userLevel = userLevel;
        }
    }
    
    public static class Lottery {
        /**
         * availableBalance : 18699.8766
         * blockedBalance : 203
         * code : 1956
         * point : 12.8
         * codeType : 0
         * extraPoint : null
         * playStatus : 0
         * allowEqualCode : false
         * isDividendAccount : true
         */
    
        /**
         * 可用余额
         */
        private double availableBalance;
        /**
         * 冻结余额
         */
        private double blockedBalance;
        /**
         * 自己对应的奖金
         */
        private int code;
        /**
         * 自身返点
         */
        private double point;
        private int codeType;
        private Object extraPoint;
        private int playStatus;
        private boolean allowEqualCode;
        private boolean isDividendAccount;
        
        public double getAvailableBalance() {
            return availableBalance;
        }
        
        public void setAvailableBalance(double availableBalance) {
            this.availableBalance = availableBalance;
        }
        
        public double getBlockedBalance() {
            return blockedBalance;
        }
        
        public void setBlockedBalance(double blockedBalance) {
            this.blockedBalance = blockedBalance;
        }
        
        public int getCode() {
            return code;
        }
        
        public void setCode(int code) {
            this.code = code;
        }
        
        public double getPoint() {
            return point;
        }
        
        public void setPoint(double point) {
            this.point = point;
        }
        
        public int getCodeType() {
            return codeType;
        }
        
        public void setCodeType(int codeType) {
            this.codeType = codeType;
        }
        
        public Object getExtraPoint() {
            return extraPoint;
        }
        
        public void setExtraPoint(Object extraPoint) {
            this.extraPoint = extraPoint;
        }
        
        public int getPlayStatus() {
            return playStatus;
        }
        
        public void setPlayStatus(int playStatus) {
            this.playStatus = playStatus;
        }
        
        public boolean isAllowEqualCode() {
            return allowEqualCode;
        }
        
        public void setAllowEqualCode(boolean allowEqualCode) {
            this.allowEqualCode = allowEqualCode;
        }
        
        public boolean isDividendAccount() {
            return isDividendAccount;
        }
        
        public void setIsDividendAccount(boolean isDividendAccount) {
            this.isDividendAccount = isDividendAccount;
        }
    }
    
    public static class Info {
        /**
         * gender : 0
         * birthday : null
         * cellphone : null
         * qq : null
         * email : null
         * avatar : null
         * navigate : 0
         */
        
        private int gender;
        private Object birthday;
        private String cellphone;
        private String qq;
        private String email;
        private String avatar;
        private int navigate;
        
        public int getGender() {
            return gender;
        }
        
        public void setGender(int gender) {
            this.gender = gender;
        }
        
        public Object getBirthday() {
            return birthday;
        }
        
        public void setBirthday(Object birthday) {
            this.birthday = birthday;
        }
        
        public String getCellphone() {
            return cellphone;
        }
    
        public void setCellphone(String cellphone) {
            this.cellphone = cellphone;
        }
    
        public String getQq() {
            return qq;
        }
    
        public void setQq(String qq) {
            this.qq = qq;
        }
    
        public String getEmail() {
            return email;
        }
    
        public void setEmail(String email) {
            this.email = email;
        }
    
        public String getAvatar() {
            return avatar;
        }
    
        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    
        public int getNavigate() {
            return navigate;
        }
    
        public void setNavigate(int navigate) {
            this.navigate = navigate;
        }
    }
   
    
}
