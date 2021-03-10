package com.ab.push;

/**
 * Created by lee on 2017/10/19.
 */

public interface IPush {
    boolean isInited();
    AbPushMessageHandler getPushMessageHandler();
    void bindAccount(String account, String groupName, final IPushCallback outerCallback);
    void unBindAccount(String account, String groupName, final IPushCallback outerCallback);
}
