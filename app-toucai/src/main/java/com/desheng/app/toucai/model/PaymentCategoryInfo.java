package com.desheng.app.toucai.model;

public class PaymentCategoryInfo {

    /**
     * payHint : null
     * mobileLogoPicture : /upload/category/file_29aad19f-e17c-4bce-a262-0209dedb9f56
     * mobileActivityPicture : /upload/category/file_77b7ae25-d635-4273-a5dc-551af041e4f5
     * sort : 1
     * categoryName : 推荐渠道
     * mobileStatus : 0
     * mobileCategoryPicture :
     * rechargeHint : 1、充值金额：单笔最低充值金额为{{minAmount}}元，最高{{maxAmount}}元。|2、充值收取手续费:{{minRate}}%。|3、玩家操作个人转款软件转账至我司账户时，输入银行卡信息和金额后，必须将【附言】内容复制或完整输入至转账页面的摘要、留言或备注中，方可继续操作转账。|4、转账金额与提交订单金额需一致，如不是造成充值不到账需自行承担。|5、充值网银转账只能用网银进行充值，充值支付宝转账只能用支付宝进行充值，如不按照对应充值方式进行充值，造成充值不到账需自行承担。|6、转账充值一次，仅需进入该页面一次。|7、平台收款卡“不定时”更换，请玩家每次转账前在本页面查看银行账号后操作转账，如充值过期或停用账户造成损失需自行承担。
     * id : 1
     * status : 0
     * mobilePayPrompt : null
     */

    private String payHint;
    private String mobileLogoPicture;
    private String mobileActivityPicture;
    private String sort;
    private String categoryName;
    private String mobileStatus;
    private String mobileCategoryPicture;
    private String rechargeHint;
    private String id;
    private String status;
    private String mobilePayPrompt;

    public String getPayHint() {
        return payHint;
    }

    public void setPayHint(String payHint) {
        this.payHint = payHint;
    }

    public String getMobileLogoPicture() {
        return mobileLogoPicture;
    }

    public void setMobileLogoPicture(String mobileLogoPicture) {
        this.mobileLogoPicture = mobileLogoPicture;
    }

    public String getMobileActivityPicture() {
        return mobileActivityPicture;
    }

    public void setMobileActivityPicture(String mobileActivityPicture) {
        this.mobileActivityPicture = mobileActivityPicture;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMobileStatus() {
        return mobileStatus;
    }

    public void setMobileStatus(String mobileStatus) {
        this.mobileStatus = mobileStatus;
    }

    public String getMobileCategoryPicture() {
        return mobileCategoryPicture;
    }

    public void setMobileCategoryPicture(String mobileCategoryPicture) {
        this.mobileCategoryPicture = mobileCategoryPicture;
    }

    public String getRechargeHint() {
        return rechargeHint;
    }

    public void setRechargeHint(String rechargeHint) {
        this.rechargeHint = rechargeHint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobilePayPrompt() {
        return mobilePayPrompt;
    }

    public void setMobilePayPrompt(String mobilePayPrompt) {
        this.mobilePayPrompt = mobilePayPrompt;
    }
}
