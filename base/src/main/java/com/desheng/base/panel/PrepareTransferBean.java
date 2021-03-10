package com.desheng.base.panel;

import java.util.List;

public class PrepareTransferBean {

    /**
     * transferTypeList : [{"name":"充值","type":"2"},{"name":"活动","type":"1"}]
     * uAccount : {}
     */

    private UAccountBean uAccount;
    private List<TransferTypeListBean> transferTypeList;

    public UAccountBean getUAccount() {
        return uAccount;
    }

    public void setUAccount(UAccountBean uAccount) {
        this.uAccount = uAccount;
    }

    public List<TransferTypeListBean> getTransferTypeList() {
        return transferTypeList;
    }

    public void setTransferTypeList(List<TransferTypeListBean> transferTypeList) {
        this.transferTypeList = transferTypeList;
    }

    public static class UAccountBean {
    }
}
