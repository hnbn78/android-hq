package com.desheng.base.manager;

public interface AbCodeCallBack {

    /**
     * 验证吗发送成功
     */
    void onSendSuccessfully();

    /**
     * 需要图形验证码
     */
    void onNeedImageCode();

    /**
     * 验证码发送失败
     */

    void onSendCodeField(String content);
}
