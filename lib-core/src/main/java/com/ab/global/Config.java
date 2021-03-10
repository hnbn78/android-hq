package com.ab.global;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public class Config {
    public static final String SDCARD_PATH = "/mnt/sdcard";
    
    /**定制版本标志**/
    public static String custom_flag = "";
    
    
    /**工控机测试*/
    public static final String ENV_EMULATOR = "env_emulator";

    /**售货机测试*/
    public static final String ENV_DEVELOP = "env_develop";

    /**售货机正式*/
    public static final String ENV_PUBLISH = "env_publish";
    
    
    /**运行环境, 正式版必须更改为ENV_PUBLISH*/
    public static String env = ENV_DEVELOP;
    public static boolean isSelfMonitor = false;

    //网络地址访问
    public static  String HOST_LOCAL = "http://127.0.0.1:8080";
    //public static  String HOST_TEST = "http://119.28.215.73";//http://119.28.134.218
    public static  String HOST_TEST = "http://119.28.227.116";// http://119.28.134.218
    //public static final String HOST_TEST = "http://k5wk7b.natappfree.cc/sulaigo";
    public static  String HOST_PUBLISH = "http://127.0.0.1:8080";
    public static  String HOST_USER_PREFIX = "";
    public static  String SWITCH_LINE = "";
    
    //服务端授发appid
    public static final String APP_ID_LOCAL = "000000";
    public static final String APP_ID_TEST = "000000";
    public static final String APP_ID_PUBLISH  = "000000";
    public static final String APP_SECRECT_LOCAL = "123456";
    public static final String APP_SECRECT_TEST = "123456";
    public static final String APP_SECRECT_PUBLISH  = "123456";
    
    //接口兼容版本
    public static final int HTTP_VERSION = 1;
    
    public static final String TAG_SERIAL = "tag_serial";
    public static final String TAG_MACHINE = "tag_machine";
    public static final String TAG_SALE = "tag_sale";
    public static final String TAG_TIME = "tag_time";
    public static final String TAG_CASH = "tag_cash";
    
    
    public static  String DEFAULT_DB_NAME = "green-dao3.db";
    
    /**
     * 是否测试版
     * @return
     */
    public static boolean isDebug() {
        return !ENV_PUBLISH.equals(env);
    }
    
    public static boolean isPublish(){
        return ENV_PUBLISH.equals(env);
    }

    /**
     * 是否只工控机运行的模拟版本
     * @return
     */
    public static boolean isEmulatorDebug(){
        return ENV_EMULATOR.equals(env);
    }
    /**
     * 是否只工控机运行的模拟版本
     * @return
     */
    public static boolean isDevelopDebug(){
        return ENV_DEVELOP.equals(env);
    }
    
    
}
