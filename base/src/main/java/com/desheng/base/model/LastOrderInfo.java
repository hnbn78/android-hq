package com.desheng.base.model;

public class LastOrderInfo {
  
    public RechargeOrderBean rechargeOrder;
    public QrCodePayInfoBean qrCodePayInfo;
    public RemitBankInfoBean remitBankInfo;
    
   
    public static class RechargeOrderBean {
        
        public int orderId;
        public double fee;
        public String payWay;
        public String spsn;
        public String way;
        public String uid;
        public double rcash;
        public int ralationType;
        public int bankId;
        public int seconds;
        public String errCode;
        public String recordInfo;
        public String clientIp;
        public int currency;
        public double cash;
        public long orderDate;
        public int status;
        public long lastUpdateTime;
        
    }
    
    public static class QrCodePayInfoBean {
        
        public String pcPayPrompt;
        public double minAmount;
        public int amountInputType;
        public int codeType;
        public String fileByte;
        public String nickName;
        public String mobileLogoPicture;
        public String mobileActivityPicture;
        public String mobileFriendlyPrompt;
        public String fixAmount;
        public String pcPromptMessage;
        public String pcActivityPicture;
        public String pcLogoPicture;
        public String id;
        public int state;
        public int payChannelCategoryId;
        public String pcFriendlyPrompt;
        public int phoneState;
        public double maxAmount;
        public String account;
        public String fileId;
        public String mobilePayPrompt;
    }
    
    public static class RemitBankInfoBean {
       
        public String pcPayPrompt;
        public double minAmount;
        public int amountInputType;
        public int bankType;
        public String mobileLogoPicture;
        public String mobileActivityPicture;
        public String mobileFriendlyPrompt;
        public String fixAmount;
        public String bankName;
        public String pcPromptMessage;
        public String remitBankNo;
        public String pcActivityPicture;
        public int remitBankId;
        public String pcLogoPicture;
        public int phoneStatus;
        public String sn;
        public String place;
        public String bankAlias;
        public int payChannelCategoryId;
        public String pcFriendlyPrompt;
        public double maxAmount;
        public int status;
        public String mobilePayPrompt;
    }
}
