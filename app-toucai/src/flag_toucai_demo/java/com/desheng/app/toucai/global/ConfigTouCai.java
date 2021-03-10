package com.desheng.app.toucai.global;

import com.ab.global.Config;
import com.desheng.base.global.BaseConfig;

/**
 * Created by lee on 2017/4/6 0006.
 */

public class ConfigTouCai extends BaseConfig{
    
    static {
        //TODO 正式
        //env = Config.ENV_PUBLISH;
        
        env = Config.ENV_DEVELOP;
    
        //TODO 正式
        //ForceHost = "";
    
        //TODO 调试
        ForceHost = "http://test.tc508.com";
        //ForceHost = "http://119.28.134.82";
        //ForceHost = "http://192.168.0.177";
        
        isSelfMonitor = !isPublish();
        Config.DEFAULT_DB_NAME = "green-dao3.db";
    
        //网络地址访问
        HOST_LOCAL = "http://127.0.0.1:8080";
        
        //头彩
        //HOST_TEST = "https://test.tc508.com";
        HOST_TEST = "https://test.tc508.com";
        
        //TODO 正式
        HOST_PUBLISH = "https://test.tc508.com";
        
        HOST_USER_PREFIX = "yx";
        isUsePush = true;
        isNetBanner = true;
        if(isPublish()){
            ForceHost = "";
    
            //TODO 正式
            SWITCH_LINE = "http://test.tc508.com/json/app.json";
        }else{
    
            //TODO 调试
            SWITCH_LINE = "http://test.tc508.com/json/app.json";
            
            //SWITCH_LINE = "https://tc09.com/json/app.json";
            //SWITCH_LINE = "http://tc508.com/json/app.json";
        }
        CustomServiceLink = "https://v88.live800.com/live800/chatClient/chatbox.jsp?companyID=925834&configID=8015&jid=8547731414&s=1";
    }
    
    
}
