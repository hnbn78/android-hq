package com.desheng.app.toucai.global;

import com.ab.global.Config;
import com.desheng.base.global.BaseConfig;

/**
 * Created by lee on 2017/4/6 0006.
 */

public class ConfigTouCai extends BaseConfig {

    static {
        //TODO 正式
//        env = Config.ENV_DEVELOP;
//        ForceHost = "http://seuofuse.com";
//        HOST_PUBLISH = "http://seuofuse.com";
//        HOST_PUBLISH = "http://47.52.97.97";

        //TODO 正式 (打log调试模式)
//        env = Config.ENV_DEVELOP;
//        ForceHost = "http://seuofuse.com";
//        HOST_PUBLISH = "http://seuofuse.com";

        //TODO 调试正式
        /*env = Config.ENV_DEVELOP;
        ForceHost = "https://www.tc508.com";*/

        //TODO 调试
//        env = Config.ENV_DEVELOP;
//        ForceHost = "http://test.tc508.com";

        //TODO 验收
        env = Config.ENV_DEVELOP;
        ForceHost = "http://vip.tc508.com";//http://uat.touc988.com
        HOST_TEST = "http://vip.tc508.com";
        //HOST_TEST = "http://119.28.138.254";//


        isSelfMonitor = !isPublish();

        Config.DEFAULT_DB_NAME = "green-dao3.db";

        //网络地址访问
        HOST_LOCAL = "http://127.0.0.1:8080";

        HOST_USER_PREFIX = "yx";

        isUsePush = true;

        isNetBanner = true;

        isFloatBtnEnabled = false;

        if (isPublish()) {
            ForceHost = "";
            //TODO 正式
            SWITCH_LINE = "http://tc.jinchanzc.com/upload/json/app3.json";
        } else {
            //TODO 验收
            SWITCH_LINE = "http://uat.touc988.com/upload/json/app3.json";
        }
        CustomServiceLink = "https://v88.live800.com/live800/chatClient/chatbox.jsp?companyID=925834&configID=8015&jid=8547731414&s=1";
    }
}
