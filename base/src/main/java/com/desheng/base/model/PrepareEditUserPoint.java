package com.desheng.base.model;

import java.util.List;

/**
 * Created by lee on 2018/5/1.
 */

public class PrepareEditUserPoint {
    
    public LotteryCodeRangeBean lotteryCodeRange;
    public Object uLotteryCodeQuotaList;
    public MAccountBean mAccount;
    public UAccountBean uAccount;
    public MGameLotteryAccountBean mGameLotteryAccount;
    public UGameLotteryAccountBean uGameLotteryAccount;
    public List<?> mLotteryCodeQuotaList;
    
    
    public static class LotteryCodeRangeBean {
        /**
         * minPoint : 14.7
         * maxPoint : 14.7
         */
        
        public double minPoint;
        public double maxPoint;
        
      
    }
    
    public static class MAccountBean {
        /**
         * username : user234
         * nickname : user234
         * type : 1
         * registTime : 1524808182000
         * loginTime : 1525160357000
         * lockTime : null
         * status : 0
         * onlineStatus : 0
         * typeCode : null
         * allowTransfer : false
         * googleBind : false
         * vipCode : null
         */
        
        public String username;
        public String nickname;
        public int type;
        public long registTime;
        public long loginTime;
        public Object lockTime;
        public int status;
        public int onlineStatus;
        public Object typeCode;
        public boolean allowTransfer;
        public boolean googleBind;
        public Object vipCode;
       
    }
    
    public static class UAccountBean {
        /**
         * username : use456
         * nickname : use456
         * type : 0
         * registTime : 1525084065000
         * loginTime : null
         * lockTime : null
         * status : 0
         * onlineStatus : 0
         * typeCode : null
         * allowTransfer : false
         * googleBind : false
         * vipCode : null
         */
        
        public String username;
        public String nickname;
        public int type;
        public long registTime;
        public Object loginTime;
        public Object lockTime;
        public int status;
        public int onlineStatus;
        public Object typeCode;
        public boolean allowTransfer;
        public boolean googleBind;
        public Object vipCode;
        
        
    }
    
    public static class MGameLotteryAccountBean {
        /**
         * availableBalance : 1.0E7
         * blockedBalance : null
         * code : 1994
         * point : 14.7
         * codeType : 0
         * extraPoint : 0
         * playStatus : 0
         * allowEqualCode : false
         * dividendAccount : false
         */
        
        public double availableBalance;
        public Object blockedBalance;
        public int code;
        public double point;
        public int codeType;
        public int extraPoint;
        public int playStatus;
        public boolean allowEqualCode;
        public boolean dividendAccount;
        
    }
    
    public static class UGameLotteryAccountBean {
        /**
         * availableBalance : 0
         * blockedBalance : null
         * code : 1992
         * point : 14.6
         * codeType : 0
         * extraPoint : 0
         * playStatus : 0
         * allowEqualCode : false
         * dividendAccount : false
         */

        public int availableBalance;
        public Object blockedBalance;
        public int code;
        public double point;
        public int codeType;
        public int extraPoint;
        public int playStatus;
        public boolean allowEqualCode;
        public boolean dividendAccount;
    
    }
}
