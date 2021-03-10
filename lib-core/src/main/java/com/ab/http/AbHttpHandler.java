package com.ab.http;

import java.util.Map;

/**
 * Created by lee on 2017/9/24.
 */
public interface AbHttpHandler {
    AbHttpReqEntity genRequestEntity();
    Map<String, Object> genUninParam();
    
    boolean isResponceForUserInvalid(String resp);
    void processUserModefyPwd();
    void processUserInvalid();
    void processCommonFailure(AbHttpRespEntity resp, int code, String msg);
}
