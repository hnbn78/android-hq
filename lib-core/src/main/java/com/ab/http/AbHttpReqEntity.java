package com.ab.http;

import java.io.Serializable;

/**
 * Created by lee on 2017/9/8.
 */
public class AbHttpReqEntity implements Serializable {
    private int version;
    private long timestamp;
    private String appId;
    private String appSecrect;
    private String param;
    private String sign;
    
    public AbHttpReqEntity() {
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getAppId() {
        return appId;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public String getParam() {
        return param;
    }
    
    public void setParam(String param) {
        this.param = param;
    }
    
    public String getAppSecrect() {
        return appSecrect;
    }

    public void setAppSecrect(String appSecrect) {
        this.appSecrect = appSecrect;
    }
}
