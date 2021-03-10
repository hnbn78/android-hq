package com.desheng.base.model;

import android.os.Parcel;

import com.ab.util.Nums;
import com.ab.util.Strs;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lee on 2018/4/25.
 */

public class RechargeInfo implements Serializable {
    public static final int CHARGE_TYPE_THIRD = 1;
    public static final int CHARGE_TYPE_TRANS = 2;
    public static final int CHARGE_TYPE_QR = 3;
    
    public RechargeConfigBean rechargeConfig;
    public List<TransferListBean> transferList;
    public List<ThridListBean> thridList;
    public List<QrCodeListBean> qrCodeList;
    
    public static class RechargeConfigBean implements Serializable {
        /**
         * isOpen : true
         * serviceMsg : 平台充值已关闭
         * serviceTime : 0:00~0:00
         */
        
        public boolean isOpen;
        public String serviceMsg;
        public String serviceTime;
        
        public RechargeConfigBean() {
        }
        
        protected RechargeConfigBean(Parcel in) {
            this.isOpen = in.readByte() != 0;
            this.serviceMsg = in.readString();
            this.serviceTime = in.readString();
        }
        
    }
    
    public static class TransferListBean implements Serializable {
        
        public double maxUnitRecharge;
        public String bankId;
        public String cardName;
        public String cardId;
        public String link;
        public String bankName;
        public int id;
        public double minUnitRecharge;
        public int bankType;
        public String frontName;

        public int sort;
        
        public int amountInputType;
        public String mobileLogoPicture;
        public String mobileActivityPicture;
        public String fixAmount;
        public String mobileFriendlyPrompt;
        public String mobilePayPrompt;
        public int payChannelCategoryId;
        
        public TransferListBean() {
        }
        
        public CommonChargeListBean getCommon() {
            CommonChargeListBean bean = new CommonChargeListBean();
            bean.chargeType = CHARGE_TYPE_TRANS;
            bean.maxUnitRecharge = maxUnitRecharge;
            bean.bankId = bankId;
            bean.cardName = cardName;
            bean.cardId = cardId;
            bean.link = link;
            bean.bankName = bankName;
            bean.id = Strs.of(id);
            bean.minUnitRecharge = minUnitRecharge;
            bean.bankType = bankType;
            bean.frontName = frontName;
            bean.name = frontName;
            bean.sort = sort;
    
            bean.amountInputType = amountInputType;
            bean.mobileLogoPicture = mobileLogoPicture;
            bean.mobileActivityPicture = mobileActivityPicture;
            bean.fixAmount = fixAmount;
            bean.mobileFriendlyPrompt = mobileFriendlyPrompt;
            bean.mobilePayPrompt = mobilePayPrompt;
            bean.payChannelCategoryId = payChannelCategoryId;
            return bean;
        }
    }
    
    public static class ThridListBean implements Serializable {
        
        public double maxUnitRecharge;
        public boolean aliPayH5;
        public String code;
        public double rate;
        public String name;
        public String link;
        public String phoneStatus;
        public String id;
        public String fixAmount;
        public int minUnitRecharge;
        public List<BanklistBean> banklist;
    
        public int amountInputType;
        public String mobileLogoPicture;
        public String mobileActivityPicture;
        public String mobileFriendlyPrompt;
        public String mobilePayPrompt;
        public int payChannelCategoryId;
        public String channelCode;
        public int sort;
        
        public ThridListBean() {
        }
        
        public CommonChargeListBean getCommon() {
            CommonChargeListBean bean = new CommonChargeListBean();
            bean.chargeType = CHARGE_TYPE_THIRD;
            bean.maxUnitRecharge = maxUnitRecharge;
            bean.aliPayH5 = aliPayH5;
            bean.code = code;
            bean.rate = rate;
            bean.name = name;
            bean.link = link;
            bean.sort = sort;
            bean.phoneStatus = phoneStatus;
            bean.id = id;
            bean.minUnitRecharge = minUnitRecharge;
            bean.banklist = banklist;
            bean.amountInputType = amountInputType;
            bean.mobileLogoPicture = mobileLogoPicture;
            bean.mobileActivityPicture = mobileActivityPicture;
            bean.fixAmount = fixAmount;
            bean.mobileFriendlyPrompt = mobileFriendlyPrompt;
            bean.mobilePayPrompt = mobilePayPrompt;
            bean.payChannelCategoryId = payChannelCategoryId;
            bean.channelCode = channelCode;
            return bean;
        }
    }
    
    public static class BanklistBean implements Serializable {
        public String bankId;
        public String code;
        public String name;
        
        public BanklistBean() {
        }
        
        public String getBankId() {
            return bankId;
        }
        
        public void setBankId(String bankId) {
            this.bankId = bankId;
        }
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class QrCodeListBean implements Serializable {
        public double maxUnitRecharge;
        public int codeType;
        public String fileByte;
        public String nickName;
        public String id;
        public String state;
        public int minUnitRecharge;
        public String account;
        public Object fileId;
        public int sort;
    
        public int amountInputType;
        public String mobileLogoPicture;
        public String mobileActivityPicture;
        public String fixAmount;
        public String mobileFriendlyPrompt;
        public String mobilePayPrompt;
        public int payChannelCategoryId;
        
        public CommonChargeListBean getCommon() {
            CommonChargeListBean bean = new CommonChargeListBean();
            bean.chargeType = CHARGE_TYPE_QR;
            bean.maxUnitRecharge = maxUnitRecharge;
            bean.codeType = codeType;
            bean.fileByte = fileByte;
            bean.nickName = nickName;
            bean.id = id;
            bean.state = state;
            bean.minUnitRecharge = minUnitRecharge;
            bean.account = account;
            bean.fileId = fileId;
    
            bean.amountInputType = amountInputType;
            bean.mobileLogoPicture = mobileLogoPicture;
            bean.mobileActivityPicture = mobileActivityPicture;
            bean.fixAmount = fixAmount;
            bean.mobileFriendlyPrompt = mobileFriendlyPrompt;
            bean.mobilePayPrompt = mobilePayPrompt;
            bean.payChannelCategoryId = payChannelCategoryId;
            bean.sort =sort;
            return bean;
        }
    }
    
    public static class CommonChargeListBean implements Serializable {
        public static final int INPUT_TYPE_INTEGER = 1;
        public static final int INPUT_TYPE_DECEMAL = 2;
        public static final int INPUT_TYPE_FIXED = 3;
        
        public int chargeType = -1;
        public double maxUnitRecharge;
        public String id;
        public double minUnitRecharge;
        public String link;
        public int sort;
        
        //third type = 1
        public boolean aliPayH5;
        public String code;
        public double rate;
        public String name;
        public String fixAmount;
        public String phoneStatus;
        public List<BanklistBean> banklist;
        
        //trans type = 2
        public String bankId;
        public String cardName;
        public String cardId;
        public String bankName;
        public String frontName;
        public int bankType;
        
        //qr type = 3
        public int codeType;
        public String fileByte;
        public String nickName;
        public String state;
        public String account;
        public Object fileId;
        
        //新增配置图文
        public int amountInputType;
        public String mobileLogoPicture;
        public String mobileActivityPicture;
        public String mobileFriendlyPrompt;
        public String mobilePayPrompt;
        public int payChannelCategoryId;
        public String channelCode;
        
        public TransferListBean getTransferBean() {
            TransferListBean bean = new TransferListBean();
            bean.maxUnitRecharge = maxUnitRecharge;
            bean.bankId = bankId;
            bean.frontName = frontName;
            bean.cardName = cardName;
            bean.cardId = cardId;
            bean.link = link;
            bean.sort = sort;

            bean.bankName = bankName;
            bean.id = Nums.parse(id, 0);
            bean.minUnitRecharge = minUnitRecharge;
            bean.bankType = bankType;
            
            bean.amountInputType = amountInputType;
            bean.mobileLogoPicture = mobileLogoPicture;
            bean.mobileActivityPicture = mobileActivityPicture;
            bean.mobileFriendlyPrompt = mobileFriendlyPrompt;
            bean.mobilePayPrompt = mobilePayPrompt;
            bean.payChannelCategoryId = payChannelCategoryId;
            return bean;
        }
    }
    
    public RechargeInfo() {
    }
    
    
}
