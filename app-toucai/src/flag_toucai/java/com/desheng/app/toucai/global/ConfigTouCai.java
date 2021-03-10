package com.desheng.app.toucai.global;

import com.ab.global.Config;
import com.desheng.base.global.BaseConfig;

/**
 * Created by lee on 2017/4/6 0006.
 */

public class ConfigTouCai extends BaseConfig {

    static {
        //TODO 正式
        env = Config.ENV_PUBLISH;
        HOST_PUBLISH = "http://tc.xsj280.com";

//        //TODO 测试
//        env = Config.ENV_DEVELOP;
//        HOST_TEST = "http://test.tc508.com";

        isSelfMonitor = !isPublish();
        Config.DEFAULT_DB_NAME = "green-dao3.db";
        //网络地址访问
        HOST_LOCAL = "http://127.0.0.1:8080";

        HOST_USER_PREFIX = "yx";

        isUsePush = true;

        isNetBanner = true;

        isFloatBtnEnabled = true;//TODO 正式打包需求改成 true

        if (isPublish()) {
            ForceHost = HOST_PUBLISH;
            //TODO 正式
            SWITCH_LINE = HOST_PUBLISH + "/upload/json/app3.json";
        } else {
            //TODO 验收
            ForceHost = HOST_TEST;
            SWITCH_LINE = HOST_TEST + "/upload/json/app3.json";
        }
        CustomServiceLink = "https://v88.live800.com/live800/chatClient/chatbox.jsp?companyID=925834&configID=8015&jid=8547731414&s=1";
    }
}
