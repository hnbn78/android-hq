package com.ab.global;

/**
 * Created by lee on 2017/9/24.
 */

public enum ENV {
    
    LOCAL(Config.HOST_LOCAL, Config.APP_ID_LOCAL, Config.APP_SECRECT_LOCAL, 1),
    
    PUBLISH(Config.HOST_PUBLISH, Config.APP_ID_PUBLISH, Config.APP_SECRECT_PUBLISH, 1),
    
    TEST(Config.HOST_TEST, Config.APP_ID_TEST, Config.APP_SECRECT_TEST, 1);
    
    public static ENV curr;
    
    public String host = "";
    public String appId = "";
    public String appSecrect = "";
    public int httpVersion = 1;
    
    ENV(String host, String appId, String appSecrect, int httpVersion) {
        this.host = host;
        this.appId = appId;
        this.appSecrect = appSecrect;
        this.httpVersion = httpVersion;
    }
}
