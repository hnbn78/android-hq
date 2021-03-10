package com.ab.http;

/**
 * Created by lee on 2017/9/24.
 */

public enum CODEC_HTTP {
    FAIL(Integer.MIN_VALUE, "fail"),
    
    INVALID_SIGN(-10001, "验签无效"),
    
    SERVER_EXCEPTION(-10002, "服务端异常发生"),
    
    NET_WORK_FAIL(-10004, "网络访问失败"),
    
    INVALID_PARAM(-10003, "非法参数"),
    
    RESP_DECODE_EXCEPTION(-10005, "返回数据解析异常"),
    
    RESP_HANDLE_EXCEPTION(-10006, "返回数据处理异常"),
    
    NO_CONNECTION(-10007, "设备无网络"),
    
    NO_SERVER(-10008, "数据请求超时"),
    
    CANCELLED_OR_INTERUPTED(-10009, "访问被取消或线程被打断"),
    
    ERROR_NEED_VERIFY(36, "验证码"),
    ERROR_VERIFY_FAULT(37,  "验证码错误"),
    ERROR_CODE_BUSY(-2, "服务器负载过高"),
    ERROR_CODE_LOGIN(-1,"异地登陆,或登录过期"),
    ERROR_CODE_NEED_MODIFY_PWD(-4,"需要修改登录密码"),
    
    
    /**
     * 几乎不可能, 没给定义或协议解析错误
     */
    UNKNOWN((byte)0xEF, "未知错误码"),
    NULL((byte) 0xDF, "空");
    
    public int code;
    public String desc;
    
    private CODEC_HTTP(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
    
    public static CODEC_HTTP find(int code){
        CODEC_HTTP res = null;
        for (CODEC_HTTP codec : values()) {
            if(codec.code == code){
                res = codec;
                break;
            }
        }
        if(res == null){
            res = UNKNOWN;
            res.code = code;
            res.desc = "未知错误码:" + code;
        }
        return res;
    }
    
    public static String getDesc(int code){
        String desc = null;
        for (CODEC_HTTP codec : values()) {
            if(codec.code == code){
                desc = codec.desc;
                break;
            }
        }
        if(desc == null){
            desc = "未知错误码:" + code;
        }
        return desc;
    }
}
