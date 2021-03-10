package com.ab.push;

/**
 * Created by lee on 2017/10/19.
 */

public interface IPushCallback {
    void onSuccess(String account, String group, String pushDeviceId);
    
    void onFailed(String var1, String var2);
}
