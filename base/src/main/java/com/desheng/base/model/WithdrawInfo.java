package com.desheng.base.model;

import java.util.List;

/**
 * Created by lee on 2018/4/25.
 */

public class WithdrawInfo {
    
    private MyAccountStatusBean myAccountStatus;
    private WithdrawConfigBean withdrawConfig;
    private List<AccountCardListBean> accountCardList;
    
    public MyAccountStatusBean getMyAccountStatus() {
        return myAccountStatus;
    }
    
    public void setMyAccountStatus(MyAccountStatusBean myAccountStatus) {
        this.myAccountStatus = myAccountStatus;
    }
    
    public WithdrawConfigBean getWithdrawConfig() {
        return withdrawConfig;
    }
    
    public void setWithdrawConfig(WithdrawConfigBean withdrawConfig) {
        this.withdrawConfig = withdrawConfig;
    }
    
    public List<AccountCardListBean> getAccountCardList() {
        return accountCardList;
    }
    
    public void setAccountCardList(List<AccountCardListBean> accountCardList) {
        this.accountCardList = accountCardList;
    }
    
    public static class MyAccountStatusBean {
        
        private double totalBalance;
        private int lotteryLimitAmount;
        private int baccaratLimitAmount;
        private int dailyAmount;
        private boolean hasWithdarwPwd;
        private double availableBalance;
        private int dailyCount;
        private int vipDailyCount;

        public int getVipDailyCount() {
            return vipDailyCount;
        }

        public void setVipDailyCount(int vipDailyCount) {
            this.vipDailyCount = vipDailyCount;
        }
        
        public double getTotalBalance() {
            return totalBalance;
        }
        
        public void setTotalBalance(double totalBalance) {
            this.totalBalance = totalBalance;
        }
        
        public int getLotteryLimitAmount() {
            return lotteryLimitAmount;
        }
        
        public void setLotteryLimitAmount(int lotteryLimitAmount) {
            this.lotteryLimitAmount = lotteryLimitAmount;
        }
        
        public int getBaccaratLimitAmount() {
            return baccaratLimitAmount;
        }
        
        public void setBaccaratLimitAmount(int baccaratLimitAmount) {
            this.baccaratLimitAmount = baccaratLimitAmount;
        }
        
        public int getDailyAmount() {
            return dailyAmount;
        }
        
        public void setDailyAmount(int dailyAmount) {
            this.dailyAmount = dailyAmount;
        }
        
        public boolean isHasWithdarwPwd() {
            return hasWithdarwPwd;
        }
        
        public void setHasWithdarwPwd(boolean hasWithdarwPwd) {
            this.hasWithdarwPwd = hasWithdarwPwd;
        }
        
        public double getAvailableBalance() {
            return availableBalance;
        }
        
        public void setAvailableBalance(double availableBalance) {
            this.availableBalance = availableBalance;
        }
        
        public int getDailyCount() {
            return dailyCount;
        }
        
        public void setDailyCount(int dailyCount) {
            this.dailyCount = dailyCount;
        }
    }
    
    public static class WithdrawConfigBean {
     
        private int maxDailyCount;
        private int maxDailyAmount;
        private int maxUnitAmount;
        private int dayWithdrawRateMax;
        private String withdrawStartTime;
        private boolean hasFee;
        private int maxFee;
        private int minUnitAmount;
        private String serviceTime;
        private double feeRate;
        private String withdrawEndTime;
        private int freeDailyCount;
        private int newCardLimit;
        private boolean isOpen;
        private int dayWithdrawRateMin;
        private boolean disabled;
        private String serviceMsg;

        private double vipFeeRate;
        private boolean vipFeeRateStatus;
        private int maxVipDailyCount;
        private int usdtDrawMinCount;
        private double usdtWithdrawRate;
        private boolean isOpenUsdtWithDraw;

        public boolean isOpenUsdtWithDraw() {
            return isOpenUsdtWithDraw;
        }

        public void setOpenUsdtWithDraw(boolean openUsdtWithDraw) {
            isOpenUsdtWithDraw = openUsdtWithDraw;
        }

        public int getUsdtDrawMinCount() {
            return usdtDrawMinCount;
        }

        public void setUsdtDrawMinCount(int usdtDrawMinCount) {
            this.usdtDrawMinCount = usdtDrawMinCount;
        }

        public double getUsdtWithdrawRate() {
            return usdtWithdrawRate;
        }

        public void setUsdtWithdrawRate(double usdtWithdrawRate) {
            this.usdtWithdrawRate = usdtWithdrawRate;
        }

        public int getMaxVipDailyCount() {
            return maxVipDailyCount;
        }

        public void setMaxVipDailyCount(int maxVipDailyCount) {
            this.maxVipDailyCount = maxVipDailyCount;
        }

        public boolean isVipFeeRateStatus() {
            return vipFeeRateStatus;
        }

        public void setVipFeeRateStatus(boolean vipFeeRateStatus) {
            this.vipFeeRateStatus = vipFeeRateStatus;
        }

        public double getVipFeeRate() {
            return vipFeeRate;
        }

        public void setVipFeeRate(double vipFeeRate) {
            this.vipFeeRate = vipFeeRate;
        }
        
        public int getMaxDailyCount() {
            return maxDailyCount;
        }
        
        public void setMaxDailyCount(int maxDailyCount) {
            this.maxDailyCount = maxDailyCount;
        }
        
        public int getMaxDailyAmount() {
            return maxDailyAmount;
        }
        
        public void setMaxDailyAmount(int maxDailyAmount) {
            this.maxDailyAmount = maxDailyAmount;
        }
        
        public int getMaxUnitAmount() {
            return maxUnitAmount;
        }
        
        public void setMaxUnitAmount(int maxUnitAmount) {
            this.maxUnitAmount = maxUnitAmount;
        }
        
        public int getDayWithdrawRateMax() {
            return dayWithdrawRateMax;
        }
        
        public void setDayWithdrawRateMax(int dayWithdrawRateMax) {
            this.dayWithdrawRateMax = dayWithdrawRateMax;
        }
        
        public String getWithdrawStartTime() {
            return withdrawStartTime;
        }
        
        public void setWithdrawStartTime(String withdrawStartTime) {
            this.withdrawStartTime = withdrawStartTime;
        }
        
        public boolean isHasFee() {
            return hasFee;
        }
        
        public void setHasFee(boolean hasFee) {
            this.hasFee = hasFee;
        }
        
        public int getMaxFee() {
            return maxFee;
        }
        
        public void setMaxFee(int maxFee) {
            this.maxFee = maxFee;
        }
        
        public int getMinUnitAmount() {
            return minUnitAmount;
        }
        
        public void setMinUnitAmount(int minUnitAmount) {
            this.minUnitAmount = minUnitAmount;
        }
        
        public String getServiceTime() {
            return serviceTime;
        }
        
        public void setServiceTime(String serviceTime) {
            this.serviceTime = serviceTime;
        }
        
        public double getFeeRate() {
            return feeRate;
        }
        
        public void setFeeRate(double feeRate) {
            this.feeRate = feeRate;
        }
        
        public String getWithdrawEndTime() {
            return withdrawEndTime;
        }
        
        public void setWithdrawEndTime(String withdrawEndTime) {
            this.withdrawEndTime = withdrawEndTime;
        }
        
        public int getFreeDailyCount() {
            return freeDailyCount;
        }
        
        public void setFreeDailyCount(int freeDailyCount) {
            this.freeDailyCount = freeDailyCount;
        }
        
        public int getNewCardLimit() {
            return newCardLimit;
        }
        
        public void setNewCardLimit(int newCardLimit) {
            this.newCardLimit = newCardLimit;
        }
        
        public boolean isIsOpen() {
            return isOpen;
        }
        
        public void setIsOpen(boolean isOpen) {
            this.isOpen = isOpen;
        }
        
        public int getDayWithdrawRateMin() {
            return dayWithdrawRateMin;
        }
        
        public void setDayWithdrawRateMin(int dayWithdrawRateMin) {
            this.dayWithdrawRateMin = dayWithdrawRateMin;
        }
        
        public boolean isDisabled() {
            return disabled;
        }
        
        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }
        
        public String getServiceMsg() {
            return serviceMsg;
        }
        
        public void setServiceMsg(String serviceMsg) {
            this.serviceMsg = serviceMsg;
        }
    }
    
    public static class AccountCardListBean {
        
        private String bankId;
        private boolean isDefault;
        private String addTime;
        private String bankBranch;
        private Object lockTime;
        private String bankName;
        private int id;
        private String bankCardName;
        private String cardStatus;
        private String bankCardId;
        
        public String getBankId() {
            return bankId;
        }
        
        public void setBankId(String bankId) {
            this.bankId = bankId;
        }
        
        public boolean isIsDefault() {
            return isDefault;
        }
        
        public void setIsDefault(boolean isDefault) {
            this.isDefault = isDefault;
        }
        
        public String getAddTime() {
            return addTime;
        }
        
        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }
        
        public String getBankBranch() {
            return bankBranch;
        }
        
        public void setBankBranch(String bankBranch) {
            this.bankBranch = bankBranch;
        }
        
        public Object getLockTime() {
            return lockTime;
        }
        
        public void setLockTime(Object lockTime) {
            this.lockTime = lockTime;
        }
        
        public String getBankName() {
            return bankName;
        }
        
        public void setBankName(String bankName) {
            this.bankName = bankName;
        }
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public String getBankCardName() {
            return bankCardName;
        }
        
        public void setBankCardName(String bankCardName) {
            this.bankCardName = bankCardName;
        }
        
        public String getCardStatus() {
            return cardStatus;
        }
        
        public void setCardStatus(String cardStatus) {
            this.cardStatus = cardStatus;
        }
        
        public String getBankCardId() {
            return bankCardId;
        }
        
        public void setBankCardId(String bankCardId) {
            this.bankCardId = bankCardId;
        }
    }
}
