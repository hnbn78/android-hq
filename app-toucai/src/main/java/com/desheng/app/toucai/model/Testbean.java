package com.desheng.app.toucai.model;

import java.util.List;

public class Testbean {

    private RechargeConfigBean rechargeConfig;
    private List<TransferListBean> transferList;
    private List<ThridListBean> thridList;
    private List<QrCodeListBean> qrCodeList;

    public RechargeConfigBean getRechargeConfig() {
        return rechargeConfig;
    }

    public void setRechargeConfig(RechargeConfigBean rechargeConfig) {
        this.rechargeConfig = rechargeConfig;
    }

    public List<TransferListBean> getTransferList() {
        return transferList;
    }

    public void setTransferList(List<TransferListBean> transferList) {
        this.transferList = transferList;
    }

    public List<ThridListBean> getThridList() {
        return thridList;
    }

    public void setThridList(List<ThridListBean> thridList) {
        this.thridList = thridList;
    }

    public List<QrCodeListBean> getQrCodeList() {
        return qrCodeList;
    }

    public void setQrCodeList(List<QrCodeListBean> qrCodeList) {
        this.qrCodeList = qrCodeList;
    }

    public static class RechargeConfigBean {
        /**
         * isOpen : true
         * serviceMsg : 平台充值已关闭
         * serviceTime : 0:00~0:00
         */

        private boolean isOpen;
        private String serviceMsg;
        private String usdt_exchange_rate;
        private String serviceTime;

        public String getUsdt_exchange_rate() {
            return usdt_exchange_rate;
        }

        public void setUsdt_exchange_rate(String usdt_exchange_rate) {
            this.usdt_exchange_rate = usdt_exchange_rate;
        }

        public boolean isIsOpen() {
            return isOpen;
        }

        public void setIsOpen(boolean isOpen) {
            this.isOpen = isOpen;
        }

        public String getServiceMsg() {
            return serviceMsg;
        }

        public void setServiceMsg(String serviceMsg) {
            this.serviceMsg = serviceMsg;
        }

        public String getServiceTime() {
            return serviceTime;
        }

        public void setServiceTime(String serviceTime) {
            this.serviceTime = serviceTime;
        }
    }
}
