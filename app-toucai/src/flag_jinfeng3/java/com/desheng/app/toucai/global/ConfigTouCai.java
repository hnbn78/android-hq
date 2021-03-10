package com.desheng.app.toucai.global;

import com.ab.global.Config;
import com.desheng.base.BuildConfig;
import com.desheng.base.global.BaseConfig;

/**
 * Created by lee on 2017/4/6 0006.
 */

public class ConfigTouCai extends BaseConfig {

    static {
        //TODO 正式
        env = Config.ENV_PUBLISH;
        //HOST_PUBLISH = "http://jf3.shenglidq.com";
        HOST_PUBLISH = "http://tc88.youx08.com";
        //备用
        //HOST_PUBLISH = "https://guangzhou-1254057873.cos.ap-guangzhou.myqcloud.com";
        //HOST_TEST = "http://jinfeng3.jinf88.com";

        //TODO 验收
//        env = Config.ENV_DEVELOP;
        HOST_TEST = "http://tc88.youx08.com";

        isSelfMonitor = !isPublish();

        Config.DEFAULT_DB_NAME = "green-dao3.db";

        //网络地址访问
        HOST_LOCAL = "http://test.tmall088.com";

        HOST_USER_PREFIX = "yx";

        //AES 密钥
        AES_KEY = "@!#youxi11688@#$";

        isUsePush = true;

        isNetBanner = true;

        isFloatBtnEnabled = true;//TODO 正式打包需求改成 true

        USE_AES_LOGIN = true;
        USE_AES_OPEN_ACCOUNT = true;
        AES_LOGIN_KEY = "YXpaRlzmD7Oirkey";
        AES_OPEN_ACCOUNT_KEY = "n5rhN9WJwgMFPtuK";

        if (!BuildConfig.DEBUG) {
            ForceHost = "http://tc88.youx08.com";
            //TODO 正式
            SWITCH_LINE = HOST_PUBLISH + "/upload/json/app.json";
            //备用
            //SWITCH_LINE = HOST_PUBLISH + "/jinfeng2/jinfeng2.json";
        } else {
            //TODO 验收
            ForceHost = HOST_TEST;
            SWITCH_LINE = HOST_TEST + "/upload/json/app.json";
        }
        CustomServiceLink = "https://v88.live800.com/live800/chatClient/chatbox.jsp?companyID=925834&configID=8015&jid=8547731414&s=1";
    }
}
