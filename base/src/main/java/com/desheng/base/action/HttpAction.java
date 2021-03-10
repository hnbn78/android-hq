package com.desheng.base.action;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.http.AbHttpResult;
import com.ab.http.Callback;
import com.ab.module.MM;
import com.ab.util.Dates;
import com.ab.util.MD5;
import com.ab.util.Maps;
import com.ab.util.Strs;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.AccountChangeRecordBean;
import com.desheng.base.model.AnalysisHistory;
import com.desheng.base.model.AnalysisHotCold;
import com.desheng.base.model.AnalysisLuZhu;
import com.desheng.base.model.AnalysisSides;
import com.desheng.base.model.ChaseRecordBean;
import com.desheng.base.model.GameRecord;
import com.desheng.base.model.LotteryBuyPlan;
import com.desheng.base.model.LotteryKillNum;
import com.desheng.base.model.LotteryOrderInfo;
import com.desheng.base.model.MemberManagementBean;
import com.desheng.base.model.RechargeRecordBean;
import com.desheng.base.model.TeamAccountChangeRecord;
import com.desheng.base.model.TeamReport;
import com.desheng.base.model.UserPoints;
import com.desheng.base.model.UserSalary;
import com.desheng.base.model.WithdrawRecord;
import com.desheng.base.panel.ActThirdGameRecord;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.BuglyLog;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

/**
 * http访问对象
 * Created by lee on 2017/10/2.
 */
public class HttpAction {

    private static final String TAG = "HttpAction";

    /**
     * 手机号验证是否存在
     */
    private static final String CHECK_PHONE_NUMBER = "/" + Config.HOST_USER_PREFIX + "/rg/exist";


    /**
     * 手机号验证是否存在
     */
    public static void checkPhone(Object tag, String phoneNum, AbHttpResult result) {
        Maps.gen();
        Maps.put("userName", phoneNum);
        MM.http.get(tag, CHECK_PHONE_NUMBER, Maps.get(), result);
    }

    public static final String LOGIN_WITH_PASSWORD = "/" + Config.HOST_USER_PREFIX + "/login";

    public static final String LOGIN_WITH_PASSWORD_AES = "/" + Config.HOST_USER_PREFIX + "/loginAes";
    public static final String LOGIN_WITH_PASSWORD_AES4_APP = "/" + Config.HOST_USER_PREFIX + "/loginAes4App";

    /**
     * 手机号验证码登录
     * /yx/login?type=1&phoneNum=&phoneYzCode=&capchaCode=
     */
    public static final String LOGIN_WITH_PHONE_CODE = "/" + Config.HOST_USER_PREFIX + "/login";


    /**
     * 用户登录验证权限
     */
    public static void loginWithPhone(Object tag, String phoneNum, String phoneYzCode, @Nullable String capchaCode, AbHttpResult result) {

        String urlFevorite = UserManager.getIns().getUrlFevorite();
        if (Strs.isNotEmpty(urlFevorite)) {
            BuglyLog.e("HttpAction", "偏好线路--------------best-host: " + urlFevorite);
            ENV.curr.host = urlFevorite;
        }

        BuglyLog.e("HttpAction", "用户登录--------------host: " + ENV.curr.host);
        //Logger.t(TAG).e("用户登录--------------host: " + ENV.curr.host);

        Maps.gen();
        Maps.put("type", 1);
        Maps.put("phoneNum", phoneNum);
        Maps.put("phoneYzCode", phoneYzCode);
        if (Strs.isNotEmpty(capchaCode)) {
            Maps.put("capchaCode", capchaCode);
        }
        MM.http.get(tag, LOGIN_WITH_PHONE_CODE, Maps.get(), result);
    }

    /**
     * 用户登录验证权限
     */
    public static void loginWithPassword(Object tag, String userName, String pwd, @Nullable String capchaCode, @Nullable String pushToken, AbHttpResult result) {

        String urlFevorite = UserManager.getIns().getUrlFevorite();
        if (Strs.isNotEmpty(urlFevorite)) {
            BuglyLog.e("HttpAction", "偏好线路--------------best-host: " + urlFevorite);
            //Logger.t(TAG).e("偏好线路--------------host: " + urlFevorite);
            ENV.curr.host = urlFevorite;
            MM.http.setHost(urlFevorite);
        }

        BuglyLog.e("HttpAction", "用户登录--------------host: " + ENV.curr.host);
        //Logger.t(TAG).e("用户登录--------------host: " + ENV.curr.host);

        Maps.gen();
        if (Strs.isEmpty(userName) || Strs.isEmpty(pwd)) {
            return;
        }
        Maps.put("cn", userName);
        Maps.put("password", pwd);
        if (Strs.isEqual(BaseConfig.custom_flag, BaseConfig.FLAG_FEIYU)
                || Strs.isEqual(BaseConfig.custom_flag, BaseConfig.FLAG_JINFENG_3)) {
            Maps.put("zdType", "3");
        } else {
            Maps.put("zdType", "2");
        }
        if (Strs.isNotEmpty(capchaCode)) {
            Maps.put("capchaCode", capchaCode);
        }
        if (Strs.isNotEmpty(pushToken)) {
            Maps.put("pushToken", pushToken);
        }

        if (Config.custom_flag.equals(BaseConfig.FLAG_JINFENG_2)||Config.custom_flag.equals(BaseConfig.FLAG_JINFENG_3)) {
            MM.http.get(tag, BaseConfig.USE_AES_LOGIN ? LOGIN_WITH_PASSWORD_AES4_APP : LOGIN_WITH_PASSWORD, Maps.get(), result);
        } else {
            MM.http.get(tag, BaseConfig.USE_AES_LOGIN ? LOGIN_WITH_PASSWORD_AES : LOGIN_WITH_PASSWORD, Maps.get(), result);
        }
    }

    public static final String MODIFY_LOGIN_PASSWORD = "/yx/u/api/account/modify-password";

    /**
     * 修改登录密码
     */
    public static void modifyLoginPassword(String oldPassword, String newPassword,
                                           boolean isFromRedirectToModifyPwd, String withdrawPassword, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("oldPassword", MD5.md5(oldPassword).toLowerCase());
        Maps.put("newPassword", MD5.md5(newPassword).toLowerCase());
        if (isFromRedirectToModifyPwd) {
            Maps.put("payPassword", UserManager.encryptMoneyPasswd(UserManager.getIns().getAccount(), withdrawPassword));
        }
        MM.http.get(null, MODIFY_LOGIN_PASSWORD, Maps.get(), abHttpResult);
    }

    public static void modifyLoginPassword(String oldPassword, String newPassword, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("oldPassword", MD5.md5(oldPassword).toLowerCase());
        Maps.put("newPassword", MD5.md5(newPassword).toLowerCase());
        MM.http.get(null, MODIFY_LOGIN_PASSWORD, Maps.get(), abHttpResult);
    }


    public static final String GET_IMG_CODE = "/" + Config.HOST_USER_PREFIX + "/getImgCode";

    /**
     * 获取验证码文件老
     */
    public static void getImgCode(Object tag, FileCallBack callBack) {
        Maps.gen();
        MM.http.get(tag, ENV.curr.host + GET_IMG_CODE, Maps.get(), callBack);
    }


    public static final String GET_SMS_CODE_2 = "/yx/send-verify-code2";

    /**
     * 获取短信验证码新
     * 绑定手机号发送验证码
     */
    public static void getSmsCode2(Object tag, String phoneNum, String yanzCode, AbHttpResult callBack) {
        Maps.gen();
        Maps.put("phoneNum", phoneNum);
        if (Strs.isNotEmpty(yanzCode)) {
            Maps.put("capchaCode", yanzCode);
        }
        Maps.put("type", 1);
        Maps.put("checkUnique", true);
        MM.http.post(tag, GET_SMS_CODE_2, true, Maps.get(), callBack);
    }


    public static final String getSmsCode = "/yx/send-verify-code";


    /**
     * 获取短信验证码新
     */
    public static void getSmsCode(Object tag, String phoneNum, String yanzCode, AbHttpResult callBack) {
        Maps.gen();
        Maps.put("phoneNum", phoneNum);
        if (!TextUtils.isEmpty(yanzCode))
            Maps.put("yanzCode", yanzCode);
        Maps.put("type", 1);
        MM.http.get(tag, getSmsCode, true, Maps.get(), callBack);
    }

    public static final String LOGIN_NEXT = "/yx/u/login";

    /**
     * 用户登录再请求
     */
    public static void loginNext(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, LOGIN_NEXT, Maps.get(), result);
    }


    public static final String SET_PUSH_TOKEN = "/yx/setPushToken";

    /**
     * 用户登录后绑定推送
     */
    public static void setPushToken(Object tag, String pushToken, AbHttpResult result) {
        Maps.gen();
        Maps.put("pushToken", pushToken);
        Maps.put("isIOS", false);
        MM.http.get(tag, SET_PUSH_TOKEN, Maps.get(), result);
    }

    /**
     * // 手机注册
     * PROPERTY_STRONG     NSString   *phoneNum;
     * PROPERTY_STRONG     NSString   *yanzCode;
     * PROPERTY_STRONG     NSString   *phYzCode;
     * PROPERTY_STRONG     NSString   *pwd;
     * // 账号
     * PROPERTY_STRONG     NSString   *name;
     * PROPERTY_ASSIGN     NSInteger   needLogin;  // 1
     * PROPERTY_ASSIGN     NSInteger   type;  // 1-手机 0-账号
     */
    public static final String REGIST = "/yx/zyrg";

    /**
     * 用户手机注册
     */
    public static void registUser(Object tag, int type, String phoneNum, String name, String yanzCode, String phoneYzCode, String pwd, String parentInviteCode, AbHttpResult result, String inviteCode) {
        Maps.gen();
        if (Strs.isNotEmpty(yanzCode)) {
            Maps.put("yanzCode", yanzCode);
        }

        //推荐码
        if (Strs.isNotEmpty(inviteCode)) {
            Maps.put("parentInviteCode", inviteCode);
        }

        if (type == 1) { //手机注册
            Maps.put("phoneNum", phoneNum);
            Maps.put("phYzCode", phoneYzCode);
            Maps.put("pwd", pwd);
            Maps.put("needLogin", 1);
        } else if (type == 0) {
            Maps.put("name", name);
            Maps.put("pwd", pwd);
            Maps.put("needLogin", 1);
            Maps.put("type", 0);
        }

        if (!Strs.isEmpty(parentInviteCode))
            Maps.put("parentInviteCode", parentInviteCode);

        Maps.put("type", type);

        if (BaseConfig.FLAG_TOUCAI_CHANNEL.equals(Config.custom_flag)) {
            Maps.put("parentId", 14918);
        }
        MM.http.post(tag, REGIST, Maps.get(), result);
    }


    public static class RespRegist {
        public String tk;
        public String needLogin;
        public String userName;
        public String type;
    }

    public static final String MODIFY_FUNDS_PASSWORD = "/yx/u/api/account/modify-withdraw-password";

    /**
     * 修改资金密码
     */
    public static void modifyFundsPassword(String oldPassword, String newPassword, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("oldPassword", MD5.md5(oldPassword).toLowerCase());
        Maps.put("newPassword", MD5.md5(newPassword).toLowerCase());
        MM.http.get(null, MODIFY_FUNDS_PASSWORD, Maps.get(), abHttpResult);
    }

    public static final String INIT_USER_DATA = "/yx/game-lottery/init-page";

    /**
     * 初始化用户数据
     */
    public static void initUserData(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, INIT_USER_DATA, Maps.get(), result);
    }


    /**
     * 用户中心获取用户所有资料接口
     */
    public static final String GET_USER_CENTER_INFORMATION = "/yx/u/api/account/list-full-info";

    public static void getUserCenterInfo(Object tag, AbHttpResult result) {
        MM.http.get(tag, GET_USER_CENTER_INFORMATION, null, result);

    }

    /**
     * 初始化用户数据
     */
    public static void initUserDataForceCookie(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.post(tag, INIT_USER_DATA, true, Maps.get(), result);
    }

    /***
     *获取用户绑定资料状态接口
     */
    public static final String BIND_USER_STATUS = "/yx/u/api/account/get-bind-status";

    public static void getUserBindStatus(AbHttpResult abHttpResult) {
        if (UserManager.getIns().isNotLogined()) {
            return;
        }
        Maps.gen();
        MM.http.get(null, BIND_USER_STATUS, Maps.get(), abHttpResult);
    }

    /***
     *设置默认银行卡
     */
    public static final String SET_DEFAULT_CARD = "yx/u/api/account/set-default-card";

    public static void setDefaultCard(int id, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("id", id);
        MM.http.get(null, SET_DEFAULT_CARD, Maps.get(), abHttpResult);
    }

    /***
     *获取用户提款相关信息
     */
    public static final String PREPARE_WITHDRAW = "/yx/u/api/payment/prepare-withdraw";

    public static void prepareWithdraw(Object tag, AbHttpResult abHttpResult) {
        Maps.gen();
        MM.http.get(null, PREPARE_WITHDRAW, Maps.get(), abHttpResult);
    }


    /**
     * 用户绑定绑定的银行卡列表
     */
    public static final String GET_USER_BIND_BANK = "/yx/u/api/account/list-card";

    public static void getUserBankCardList(Object tag, AbHttpResult result) {
        MM.http.get(tag, GET_USER_BIND_BANK, null, result);
    }


    private static final String GET_PERSON_INFO = "/yx/u/api/account/get-bind-info";

    /**
     * 获取用户的其它资料
     */
    public static void getPersonInfo(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, GET_PERSON_INFO, Maps.get(), result);
    }

    /**
     * 绑定真实姓名
     */
    public static final String BIND_TRUE_NAME = "/yx/u/api/account/apply-bind-withdrawName";

    public static void bindTrueName(Object tag, String truename, AbHttpResult result) {
        Maps.gen();
        Maps.put("withdrawName", truename);
        MM.http.get(tag, BIND_TRUE_NAME, Maps.get(), result);
    }

    /**
     * 设置资金密码
     */
    public static final String BIND_FUND_MONEY = "/yx/u/api/account/apply-bind-withdrawPwd";

    public static void bindFunPwd(Object tag, String withdrawPassword, AbHttpResult result) {
        Maps.gen();
        Maps.put("withdrawPassword", withdrawPassword);
        MM.http.get(tag, BIND_FUND_MONEY, Maps.get(), result);
    }

    /**
     * 绑定手机号
     */
    public static final String BIND_PHONE = "/yx/u/api/account/bind-phone";

    public static void bindPhone(Object tag, String phone, String phYzCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("phone", phone);
        Maps.put("phYzCode", phYzCode);
        MM.http.get(tag, BIND_PHONE, Maps.get(), result);
    }

    /**
     * 绑定手机号
     */
    public static final String UPDATE_PHONE = "/yx/u/api/account/update-phone";

    public static void updatePhone(Object tag, String phone, String phYzCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("phone", phone);
        Maps.put("phYzCode", phYzCode);
        MM.http.get(tag, UPDATE_PHONE, Maps.get(), result);
    }

    /**
     * 绑定邮箱
     */
    public static final String BIND_EMAIL = "/yx/u/api/personal/bind-email";

    public static void bindMail(Object tag, String email, String emailCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("email", email);
        Maps.put("phYzCode", emailCode);
        MM.http.get(tag, BIND_EMAIL, Maps.get(), result);
    }

    /**
     * 点击按钮，请求服务器给填你填写的邮箱发送一个验证码
     */
    public static final String GET_EMAIL_CODE = "/yx/u/api/personal/send-email-code";

    public static void getEmailCode(Object tag, String email, String capchaCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("email", email);
        if (!Strs.isEmpty(capchaCode)) {
            Maps.put("capchaCode", capchaCode);//当弹出图片验证码框时 需要带上此参数
        }
        MM.http.get(tag, GET_EMAIL_CODE, Maps.get(), result);
    }

    /**
     * 解绑邮箱
     */
    public static final String UNBIND_ENAIL = "/yx/u/api/personal/unbind-email";

    public static void unBindMail(Object tag, String email, String phYzCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("email", email);
        Maps.put("phYzCode", phYzCode);
        MM.http.get(tag, UNBIND_ENAIL, Maps.get(), result);
    }


    /**
     * 获得当前是否有可用的推荐送活动
     */
    public static final String RECOMMEND_ACTI_EXIST = "/yx/api/activity/recommend/havingEnabledRecommendActivities";

    public static void updateRecommendActiExist(Object tag, AbHttpResult result) {
        if (UserManager.getIns().isNotLogined()) {
            return;
        }
        Maps.gen();
        MM.http.get(tag, RECOMMEND_ACTI_EXIST, Maps.get(), result);
    }

    /**
     * 扫码登录pc端
     */
    public static final String SAOMA_LOGIN = "/yx/qrLogin";

    public static void saoMaLoginPc(Object tag, String qrStr, AbHttpResult result) {
        Maps.gen();
        Maps.put("qrStr", qrStr);
        MM.http.get(tag, SAOMA_LOGIN, Maps.get(), result);
    }


    private static final String BIND_PERSON_INFO = "/yx/u/api/personal/bind";

    /**
     * @param type  修改类型 (1-绑定性别 2-绑定生日 3-绑定微信号 4-绑定QQ)
     * @param value 性别 (0-女，1-男) / 生日（格式: yyyy-MM-dd）/ 微信号 / QQ号
     */
    public static void bindPersonInfo(Object tag, int type, String value, AbHttpResult result) {
        Maps.gen();
        Maps.put("category", type);
        String key = "";
        switch (type) {
            case 1:
                key = "sex";
                break;
            case 2:
                key = "birthday";
                break;
            case 3:
                key = "wechatNum";
                break;
            case 4:
                key = "qqNum";
                break;
        }
        Maps.put(key, value);
        MM.http.get(tag, BIND_PERSON_INFO, Maps.get(), result);
    }

    private static final String FEEDBACK = "yx/u/api/personal/prize-feedback";

    public static void submitFeedback(Object tag, String content, String contact, AbHttpResult result) {
        Maps.gen();
        Maps.put("context", content);
        if (!TextUtils.isEmpty(contact))
            Maps.put("contact", contact);
        MM.http.post(tag, FEEDBACK, Maps.get(), result);
    }

    /**
     * 获取换肤配置
     */
    private static final String GET_SKIN_SETTINGS = "/yx/skin/getCurrentSkin";

    public static void getGlobleSkinSettings(Object tag, AbHttpResult result) {
        Log.d("HttpActionTouCai", "getGlobleSkinSettings------" + MM.http.getHostWithoutHttp());
        Maps.gen();
        MM.http.post(tag, GET_SKIN_SETTINGS, Maps.get(), result);
    }

    /**
     * 用户绑定 银行卡 接口
     */
    public static final String BIND_CARD_FIRST = "/yx/u/api/account/apply-bind";
    public static final String BIND_CARD_NEXT = "/yx/u/api/account/bind-card";

    public static void bindBankCard(Object tag,
                                    String bankId,
                                    String bankBranch,
                                    String withdrawName,
                                    String bankCardId,
                                    String withdrawPassword,
                                    int cardType,
                                    String province,
                                    String city,
                                    String county,
                                    boolean isFirst,
                                    AbHttpResult result) {
        Maps.gen();
        Maps.put("bankId", bankId);
        if (isFirst) {
            Maps.put("withdrawName", withdrawName);
        }
        Maps.put("withdrawPassword", withdrawPassword);
        Maps.put("bankBranch", bankBranch);
        Maps.put("bankCardId", bankCardId);
        //Maps.put("cardType", cardType);
        Maps.put("province", province);
        Maps.put("city", city);
        Maps.put("county", county);
        if (isFirst) {
            MM.http.get(tag, BIND_CARD_FIRST, Maps.get(), result);
        } else {
            MM.http.get(tag, BIND_CARD_NEXT, Maps.get(), result);
        }
    }

    /**
     * 获取系统可用的第三方支付和线下转账渠道接口
     */
    public static final String GET_THIRD_PAYMENT = "/yx/u/api/payment/request-all-method";

    public static void getAllThirdPayment(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, GET_THIRD_PAYMENT, Maps.get(), result);
    }

    /**
     * 第三方充值接口
     */
    public static final String GET_THIRD_RECHARGE = "/yx/u/api/payment/request-thrid-pay";

    public static void rechargeThird(Object tag, String pid, String bankco, String amount, AbHttpResult result) {
        Maps.gen();
        Maps.put("pid", pid);
        Maps.put("bankco", bankco);
        Maps.put("amount", amount);
        MM.http.get(tag, GET_THIRD_RECHARGE, Maps.get(), result);
    }

    /**
     * 第三方充值接口
     */
    public static void rechargeThird(Object tag, String pid, String bankco, String bankno, String amount, String fuYan, AbHttpResult result) {
        Maps.gen();
        Maps.put("pid", pid);
        Maps.put("bankco", bankco);
        Maps.put("bankno", bankno);
        Maps.put("amount", amount);
        Maps.put("fuYan", fuYan);
        MM.http.get(tag, GET_THIRD_RECHARGE, Maps.get(), result);
    }

    /**
     * 支付宝充值接口
     */
    public static void rechargeThirdAlipay(Object tag, String link, String text, AbHttpResult result) {
        Maps.gen();
        Maps.put("text", text);
        MM.http.post(tag, link, Maps.get(), result);
    }

    /**
     * USDT充值 获取二维码地址
     */
    public static final String GET_USDT_RECHARGE = "/yx/usdt/getAddress";
    public static final String GET_USDT_WITHDRAW_ADDRESS = "/yx/usdt/getWithdrawAddressList";
    public static final String ADD_USDT_WITHDRAW_ADDRESS = "/yx/usdt/addWithdrawAddress";

    public static void getUsdtPocketInfo(Object tag, String pid, AbHttpResult result) {
        Maps.gen();
        Maps.put("pid", pid);
        MM.http.post(tag, GET_USDT_RECHARGE, Maps.get(), result);
    }

    public static void getUsdtWithdrawAddress(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, GET_USDT_WITHDRAW_ADDRESS, Maps.get(), result);
    }

    public static void addUsdtWithdrawAddress(Object tag, String address, String withdrawPassword, AbHttpResult result) {
        Maps.gen();
        Maps.put("drawType", "1");
        Maps.put("address", address);
        Maps.put("withdrawPassword", UserManager.encryptMoneyPasswd(null, withdrawPassword));
        MM.http.get(tag, ADD_USDT_WITHDRAW_ADDRESS, Maps.get(), result);
    }

    /**
     * 扫码充值接口
     */
    public static final String GET_OFFLINE_RECHARGE = "/yx/u/api/payment/request-transfer-pay";

    public static void rechargeQR(Object tag, String pid, String amount, String payWay, String qrid, AbHttpResult result) {
        Maps.gen();
        Maps.put("pid", "0");
        Maps.put("amount", amount);
        Maps.put("payWay", payWay);
        Maps.put("qrid", qrid);
        MM.http.get(tag, GET_OFFLINE_RECHARGE, Maps.get(), result);
    }

    /**
     * @param result 线下扫码
     */
    public static void rechargeQR(Object tag, String amount, int payWay, String qrid, AbHttpResult result) {
        Maps.gen();
        //Maps.put("pid", "0");
        Maps.put("amount", amount);
        Maps.put("payWay", payWay);
        Maps.put("qrid", qrid);
        MM.http.get(tag, GET_OFFLINE_RECHARGE, Maps.get(), result);
    }

    /**
     * pid: 22
     * amount: 100
     * fuYan: user345:我爱一条柴
     * payWay: 0
     * 线下转账充值接口
     */
    public static void rechargeTrans(Object tag, String pid, String amount, String payWay, String fuYan, AbHttpResult result) {
        Maps.gen();
        Maps.put("pid", pid);
        Maps.put("amount", amount);
        Maps.put("payWay", payWay);
        Maps.put("fuYan", fuYan);
        MM.http.get(tag, GET_OFFLINE_RECHARGE, Maps.get(), result);
    }

    /**
     * pid: 22
     * amount: 100
     * fuYan: user345:我爱一条柴
     * payWay: 0
     * 线下转账充值接口
     */
    public static void rechargeTransfer(Object tag, String pid, String amount, String fuYan, int payWay, AbHttpResult result) {
        Maps.gen();
        Maps.put("pid", pid);
        Maps.put("amount", amount);
        if (payWay >= 0) {
            Maps.put("payWay", payWay);
        }
        Maps.put("fuYan", fuYan);
        MM.http.get(tag, GET_OFFLINE_RECHARGE, Maps.get(), result);
    }

    /**
     * pid: 22
     * amount: 100
     * fuYan: user345:我爱一条柴
     * payWay: 0
     * 线下转账充值接口
     */
    public static void rechargeOnline(Object tag, String pid, String amount, String fuYan, AbHttpResult result) {
        Maps.gen();
        Maps.put("pid", pid);
        Maps.put("amount", amount);
        Maps.put("fuYan", fuYan);
        MM.http.get(tag, GET_OFFLINE_RECHARGE, Maps.get(), result);
    }

    /**
     * 用户查找订单
     */
    public static final String SEARCH_ORDER = "/yx/u/api/game-lottery/search-order";

    public static void searchOrder(Context ctx, String lottery, String optStatus, String sTime, String eTime, int page,
                                   int size, String optIssue, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("lottery", lottery);
        Maps.put("status", optStatus);
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("page", page);
        Maps.put("size", size);
        Maps.put("issue", optIssue);
        MM.http.post(ctx, SEARCH_ORDER, Maps.get(), abHttpResult);
    }

    public static void searchOrderWithTeam(Context ctx, String lottery, String optStatus, String sTime, String eTime, int page,
                                           int size, String subName, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("lottery", lottery);
        Maps.put("status", optStatus);
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("page", page);
        Maps.put("size", size);
        Maps.put("subName", subName);
        MM.http.post(ctx, SEARCH_ORDER, Maps.get(), abHttpResult);
    }

    public static void searchOrderTouCai(Context ctx, String lottery, Integer optStatus, String gametype, String sTime, String eTime, int page,
                                         int size, String optIssue, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("lottery", lottery);
        Maps.put("status", optStatus);
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("page", page);
        Maps.put("size", size);
        Maps.put("issue", optIssue);
        Maps.put("game_type", gametype);
        MM.http.get(ctx, SEARCH_ORDER, Maps.get(), abHttpResult);
    }

    public static class RespSearchOrder {
        public int totalCount;
        public List<LotteryOrderInfo> list;
        public Statistics statistics;

        public static class Statistics {
            public double confirmAmount;
            public double betAmount;
            public double awardAmount;
        }
    }

    /**
     * 追号详情
     */
    public static final String GET_CHASE = "/yx/u/api/game-lottery/get-chase";

    public static void getChaseDetail(Object tag, String billno, AbHttpResult result) {
        Maps.gen();
        Maps.put("billno", billno);
        MM.http.get(tag, GET_CHASE, Maps.get(), result);
    }

    /**
     * 4
     * 追号记录
     */
    public static final String CHARSE_RECORD = "/yx/u/api/game-lottery/search-chase";

    public static void searchChaseRecord(String sTime, String eTime, String lottery, int page, int size, AbHttpResult abHttpResult) {
        searchChaseRecord(null, sTime, eTime, lottery, page, size, abHttpResult);
    }

    public static void searchChaseRecord(Integer status, String sTime, String eTime, String lottery, int page, int size, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("status", status);
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("lottery", lottery);
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.get(null, CHARSE_RECORD, Maps.get(), abHttpResult);
    }

    /**
     * 4
     * 追号记录
     */

    public static void searchChaseRecordWithType(String sTime, String eTime, String lottery, int page, int size, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("lottery", lottery);
        Maps.put("page", page);
        Maps.put("size", size);
        Maps.put("type", "general");
        MM.http.get(null, CHARSE_RECORD, Maps.get(), abHttpResult);
    }


    public static class RespSearchChaseRecord {
        public int totalCount;
        public List<ChaseRecordBean> list;
        public Statistics statistics;

        public static class Statistics {
            public double betAmount;
            public double awardAmount;
        }
    }

    /**
     * 追号记录
     */
    public static final String SEARCH_RECHARGE = "/yx/u/api/account/search-recharge";

    public static void searchRechargeRecord(Object tag, String userName, String status, String sTime, String eTime, int page, int size, AbHttpResult abHttpResult) {
        searchRechargeRecord(tag, null, userName, status, sTime, eTime, page, size, abHttpResult);
    }

    public static void searchRechargeRecord(Object tag, String billno, String userName, String status, String sTime, String eTime, int page, int size, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("billno", billno);
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("username", userName);
        Maps.put("status", status);
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.get(tag, SEARCH_RECHARGE, Maps.get(), abHttpResult);
    }

    public static class RespSearchRechargeRecord {
        public int totalCount;
        public List<RechargeRecordBean> list;
        public Statistics statistics;

        public static class Statistics {
            public double failCash;
            public double totalCash;
            public double successCash;
        }
    }

    /**
     * /yx/pay/isFirstRecharge
     */

    public static final String IS_FIRST_RECHARGE = "/yx/pay/isFirstRecharge";

    public static void isFirstRecharge(AbHttpResult result) {
        Maps.gen();
        MM.http.get(null, IS_FIRST_RECHARGE, Maps.get(), result);
    }

    /**
     * 个人中心-用户取款记录查询接口
     */
    public static final String SEARCH_WITHDRAW = "/yx/u/api/account/search-withdraw";

    public static void searchWithdrawRecord(Object tag, String userName, String sTime, String eTime, int page, int size, AbHttpResult result) {
        Maps.gen();
        Maps.put("username", userName);
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("size", size);
        Maps.put("page", page);
        MM.http.get(tag, SEARCH_WITHDRAW, Maps.get(), result);
    }

    public static class RespSearchWithdrawRecord {
        public int totalCount;
        public List<WithdrawRecord> list;
        public Statistics statistics;

        public static class Statistics {
            public double totalAmount;
            public double feeAmount;
            public double practicalAmount;
        }
    }


    /**
     * 彩票报表
     */
    public static final String LOTTERY_REPORT = "/yx/u/api/report/report-game-lottery";

    public static void searchLotteryReport(String username, String sTime, String eTime, String qbId,
                                           int page, int size, int type, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("username", username);
        Maps.put("page", page);
        Maps.put("size", size);
        Maps.put("type", type);
        if (!TextUtils.isEmpty(qbId))
            Maps.put("qbId", qbId);
        else
            Maps.put("qbId", "RMB");
        if (type == 0) {
            MM.http.get(null, LOTTERY_REPORT, Maps.get(), abHttpResult);
        } else if (type == 1) {
            MM.http.get(null, GET_TEAM_LIst, Maps.get(), abHttpResult);
        }

    }

    public static void searchLotteryReport(String username, String sTime, String eTime,
                                           int page, int size, int type, AbHttpResult abHttpResult) {
        searchLotteryReport(username, sTime, eTime, "RMB", page, size, type, abHttpResult);
    }

    public static class RespSearchLotteryReport {
        public int totalCount;
        public List<TeamReport> list;

    }


    /**
     * 用户提现接口
     */
    public static final String APPLY_WITHDRAW = "/yx/u/api/payment/apply-withdraw";

    public static void applyWithdrawal(String userName, String cardId, String amount, String withdrawPassword, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("withdrawPassword", UserManager.encryptMoneyPasswd(userName, withdrawPassword));
        Maps.put("cardId", cardId);
        Maps.put("amount", amount);
        MM.http.get(null, APPLY_WITHDRAW, Maps.get(), abHttpResult);
    }

    public static void applyWithdrawal(String userName, String cardId, String amount, String withdrawPassword, int withdrawtype, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("withdrawPassword", UserManager.encryptMoneyPasswd(userName, withdrawPassword));
        Maps.put("cardId", cardId);
        Maps.put("amount", amount);
        Maps.put("isVip", withdrawtype == 1 ? 1 : 0);
        MM.http.get(null, APPLY_WITHDRAW, Maps.get(), abHttpResult);
    }

    public static void applyWithdrawal(String withdrawPassword, String usdtId, String amount, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("withdrawPassword", UserManager.encryptMoneyPasswd(null, withdrawPassword));
        Maps.put("drawType", "1");
        Maps.put("amount", amount);
        Maps.put("usdtId", usdtId);
        Maps.put("cardId", "0");
        MM.http.post(null, APPLY_WITHDRAW, Maps.get(), abHttpResult);
    }

    /**
     * 退书登录
     */
    public static final String LOGOUT = "/" + Config.HOST_USER_PREFIX + "/logout";

    public static void logout(Object tag, AbHttpResult result) {
        MM.http.get(null, LOGOUT, Maps.get(), result);
    }

    /**
     * 初始化彩种
     * https://caihong119.com/yx/u/api/game-lottery/openLotterys
     */
    public static final String GET_OPEN_LOTTERYS = "/yx/u/api/game-lottery/openLotterys";

    /**
     * 获取所有可用彩票数据
     */
    public static void getOpenLotterys(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, GET_OPEN_LOTTERYS, Maps.get(), result);
    }

    public static final String GET_LAST_OPEN = "/yx/u/api/game-lottery/get-last-open";

    /**
     * 初始化用户数据
     */
    public static void getLastOpen(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, GET_LAST_OPEN, Maps.get(), result);
    }

    /**
     * 帐变记录
     */
    public static final String ACCOUNT_CHANGE_RECORE = "/yx/u/api/account/search-zbrecord";

    public static void getAccountChange(Object tag, String userName, String zbType, String accountType, String sTime, String eTime, int page, int size, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("zbType", zbType);
        Maps.put("accountType", accountType);
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        if (!Strs.isEmpty(userName)) {
            Maps.put("userName", userName);
        }
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.get(tag, ACCOUNT_CHANGE_RECORE, Maps.get(), abHttpResult);
    }

    public static class RespAccountChange {
        public int totalCount;
        public List<AccountChangeRecordBean> list;
        public Statistics statistics;

        public static class Statistics {
            public double sobetAmount;
            public double kyAmount;
            public double aginAmount;
            public double imAmount;
            public double gmAmount;
            public double ptAmount;
            public double cqAmount;
        }
    }

    public static class RespTeamAccountChange {
        public int totalCount;
        public List<TeamAccountChangeRecord> list;
    }

    /**
     * 代理中心开户接口
     */
    public static final String PREPARE_ADD_ACCOUNT = "/yx/u/api/agent/prepare-add-account";

    public static void prepareAddAccount(Object tag, AbHttpResult abHttpResult) {
        Maps.gen();
        MM.http.get(tag, PREPARE_ADD_ACCOUNT, Maps.get(), abHttpResult);
    }


    /**
     * 代理中心开户接口
     */
    public static final String OPEN_ACCOUNT = "/yx/u/api/agent/add-account";

    public static void addAccount(String username, String passwd, String lotteryPoint, int type, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("username", username);
        Maps.put("passwd", MD5.md5(passwd).toLowerCase());
        Maps.put("lotteryPoint", lotteryPoint);
        Maps.put("type", type);
        MM.http.get(null, OPEN_ACCOUNT, Maps.get(), abHttpResult);
    }

    /**
     * 代理中心开户接口  AES加密
     */
    public static final String OPEN_ACCOUNT_AES = "/yx/u/api/agent/add-account-aes";

    public static void addAccountAes(String Aesjson, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("bodyParams", Aesjson);
        MM.http.get(null, OPEN_ACCOUNT_AES, Maps.get(), abHttpResult);
    }

    /**
     * 代理中心链接开户
     */
    public static final String LINK_ACCOUNT = "/yx/u/api/agent/add-regist-link";

    /**
     * @param type         用户类型
     * @param validDays    过期时间
     * @param lotteryPoint 返点
     */
    public static void addRegistLink(int type, int validDays, String lotteryPoint, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("type", type);
        Maps.put("validDays", validDays);
        Maps.put("point", lotteryPoint);
        MM.http.get(null, LINK_ACCOUNT, Maps.get(), abHttpResult);
    }

    /**
     * 会员管理
     */
    public static final String MEMBER_MANAGEMENT = "/yx/u/api/agent/list-team-account";

    public static void getMemberManagement(String userName, String subName, int page, int size, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("action", "User");
        Maps.put("userName", userName);
        Maps.put("subName", subName);
        Maps.put("sTime", "00:00:00");
        Maps.put("eTime", "23:59:59");
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.get(null, MEMBER_MANAGEMENT, Maps.get(), abHttpResult);
    }

    /**
     * 会员管理--转账准备
     */
    public static final String MEMBER_MANAGEMENT_PREPARE_TRANSFER = "/yx/u/api/agent/prepare-transfer";

    public static void prepareTransfer(String userName, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("userName", userName);
        MM.http.get(null, MEMBER_MANAGEMENT_PREPARE_TRANSFER, Maps.get(), abHttpResult);
    }

    /**
     * 会员管理--转账请求
     */
    public static final String MEMBER_MANAGEMENT_APPLY_TRANSFER = "/yx/u/api/agent/apply-transfer";

    public static void applyTransfer(String userName, String qbId, float amount, int type, String remark, String withdrawPassword, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("username", userName);
        if (Strs.isNotEmpty(qbId))
            Maps.put("qbId", qbId);
        else
            Maps.put("qbId", "RMB");
        Maps.put("amount", amount);
        Maps.put("type", type);
        Maps.put("remark", remark);
        Maps.put("withdrawPassword", withdrawPassword);
        MM.http.get(null, MEMBER_MANAGEMENT_APPLY_TRANSFER, Maps.get(), abHttpResult);
    }

    public static class RespSearchMember {
        public int totalCount;
        public List<String> statistics;
        public List<MemberManagementBean> list;
    }

    /**
     * 升点准备接口
     */
    public static final String PREPARE_EDIT_POINT = "/yx/u/api/agent/prepare-edit-point-by-quota";

    public static void prepareEditPoint(String username, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("username", username);
        MM.http.get(null, PREPARE_EDIT_POINT, Maps.get(), abHttpResult);
    }

    /**
     * 升点接口
     */
    public static final String EDIT_USER_POINT = "/yx/u/api/agent/edit-point-by-quota";

    public static void editUserPoint(String username, double point, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("username", username);
        Maps.put("point", point);
        MM.http.get(null, EDIT_USER_POINT, Maps.get(), abHttpResult);
    }


    /**
     * 报表中心、代理中心-团队报表查询接口
     */
    public static final String GET_TEAM_LIst = "/yx/u/api/report/report-team-lottery";

    public static void getTeamReport(Object tag, String username, String sTime, String eTime, int page, int size, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("username", username);
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.get(tag, GET_TEAM_LIst, Maps.get(), abHttpResult);
    }

    public static class RespTeamReport {
        public int totalCount;
        public List<String> statistics;
        public List<TeamReport> list;
    }

    /**
     * 游戏记录
     * lottery: TWSSC
     * issue: 20180501-074
     * sTime: 2018-05-01 00:00:00
     * eTime: 2018-05-02 23:59:59
     * subName: agent123
     * status: 0
     * page: 0
     * size: 10
     */
    public static final String SEARCH_GAME_RECORD = "/yx/u/api/game-lottery/search-order";

    public static void searchGameRecord(String lottery, Integer status, String subName, String issue, String sTime, String eTime, int page, int size, AbHttpResult result) {
        Maps.gen();
        Maps.put("lottery", lottery);
        Maps.put("status", status);
        Maps.put("subName", subName);
        Maps.put("issue", issue);
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("size", size);
        Maps.put("page", page);
        MM.http.get(null, SEARCH_GAME_RECORD, Maps.get(), result);
    }

    public static class RespGameRecord {
        public int totalCount;
        public List<GameRecord> list;
    }


    /**
     * 团队总揽
     */
    public static final String GET_TEAM_OVERVIEW = "/yx/u/api/agent/teamOverview";

    public static void getTeamOver(String start, String end, AbHttpResult result) {
        Maps.gen();
        Maps.put("start", start);
        Maps.put("end", end);
        MM.http.get(null, GET_TEAM_OVERVIEW, Maps.get(), result);
    }


    public static final String GET_TREND = "/yx/u/api/game-lottery/openIssues";

    /**
     * 获取走势数据
     */
    public static void getTrend(Object tag, int lotteryId, AbHttpResult result) {
        Maps.gen();
        Maps.put("id", lotteryId);
        Maps.put("issueCount", 50);
        MM.http.get(tag, GET_TREND, Maps.get(), result);
    }

    /**
     * 重载楼上
     */
    public static void getTrend(Object tag, int lotteryId, int issueCount, AbHttpResult result) {
        Maps.gen();
        Maps.put("id", lotteryId);
        Maps.put("issueCount", issueCount);
        MM.http.get(tag, GET_TREND, Maps.get(), result);
    }

    /**
     * 获取菜种过去期开奖号码接口
     */
    public static final String GET_LOTTERY_PURSUE = "/yx/u/api/game-lottery/static-chase-time";

    public static void getLotteryPursue(Object tag, String lotteryInfoCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("name", lotteryInfoCode);
        MM.http.get(tag, GET_LOTTERY_PURSUE, Maps.get(), result);
    }


    /**
     * 获取菜种过去期开奖号码接口
     */
    public static final String GET_LOTTERY_OEPN_CODE_HISTORY = "/yx/u/api/game-lottery/static-open-code";

    public static void getLotteryOpenCodeHistory(Object tag, String name, boolean history, AbHttpResult result) {
        Maps.gen();
        Maps.put("name", name);
        Maps.put("history", history);
        MM.http.get(tag, GET_LOTTERY_OEPN_CODE_HISTORY, Maps.get(), result);
    }


    /**
     * 追号提交
     */
    public static final String ADD_CHASE = "/yx/u/api/game-lottery/add-chase";

    public static void addChase(Object tag, ArrayList<HashMap<String, Object>> orderList, ArrayList<HashMap<String, Object>> planList, boolean winStop, AbHttpResult result) {
        HashMap<String, Object> textMap = new HashMap<>();
        textMap.put("orderList", orderList);
        textMap.put("planList", planList);
        textMap.put("winStop", winStop);

        Maps.gen();
        Maps.put("text", new Gson().toJson(textMap));
        MM.http.get(tag, ADD_CHASE, Maps.get(), result);
    }

    /**
     * 购彩计划
     * rows=10&date=2018-3-28&lotCode=10002
     */
    public static final String GET_BUY_PLAN_SSC = "https://api.api68.com/LotteryPlan/getSscPlanList.do";
    public static final String GET_BUY_PLAN_PKS = "https://api.api68.com/LotteryPlan/getPksPlanList.do";
    public static final String GET_BUY_PLAN_EF = "https://api.api68.com/LotteryPlan/getEfPlanList.do";
    public static final String GET_BUY_PLAN_KS = "https://api.api68.com/LotteryPlan/getKsPlan.do";

    public static void getBuyPlan(Object tag, ILotteryKind kind, Callback callback) {
        Maps.gen();
        Maps.put("rows", 20);
        Maps.put("date", Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD));
        Maps.put("lotCode", kind.getId168());
        String url = "";

        if (kind.getOriginType().getCode().equalsIgnoreCase("kuai3")) {
            url = GET_BUY_PLAN_KS;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("ssc")) {
            url = GET_BUY_PLAN_SSC;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("11s5")) {
            url = GET_BUY_PLAN_EF;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("other")) {
            url = GET_BUY_PLAN_PKS;
        }

        MM.http.get(tag, url, Maps.get(), callback);
    }

    public static class RespBuyPlan {
        public int errorCode;
        public String message;
        public LotteryBuyPlan result;

    }

    /**
     * 杀号
     * lotCode=10002&rows=10&date=2018-3-29
     */
    public static final String GET_KILL_NUM_SSC = "https://api.api68.com/KillNum/getSscKillNumList.do";
    public static final String GET_KILL_NUM_PKS = "https://api.api68.com/KillNum/getPksKillNumList.do";
    public static final String GET_KILL_NUM_EF = "https://api.api68.com/KillNum/getEfKillNumList.do";
    public static final String GET_KILL_NUM_KS = "https://api.api68.com/KillNum/getKsKillNumList.do";

    public static void getKillNum(Object tag, ILotteryKind kind, Callback callback) {
        Maps.gen();
        Maps.put("rows", 10);
        Maps.put("date", Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD));
        Maps.put("lotCode", kind.getId168());
        String url = "";

        if (kind.getOriginType().getCode().equalsIgnoreCase("kuai3")) {
            url = GET_KILL_NUM_KS;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("ssc")) {
            url = GET_KILL_NUM_SSC;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("11s5")) {
            url = GET_KILL_NUM_EF;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("other")) {
            url = GET_KILL_NUM_PKS;
        }

        MM.http.get(tag, url, Maps.get(), callback);
    }

    public static class RespKillNum {
        public int errorCode;
        public String message;
        public LotteryKillNum result;
    }


    /**
     * 历史开奖
     * lotCode=10002&rows=10&date=2018-3-29
     */
    public static final String GET_ANALYSIS_HISTORY_SSC = "https://api.api68.com/CQShiCai/getBaseCQShiCaiList.do";
    public static final String GET_ANALYSIS_HISTORY_PKS = "https://api.api68.com/pks/getPksHistoryList.do";
    public static final String GET_ANALYSIS_HISTORY_EF = "https://api.api68.com/ElevenFive/getElevenFiveList.do";
    public static final String GET_ANALYSIS_HISTORY_KS = "https://api.api68.com/lotteryJSFastThree/getJSFastThreeList.do";

    public static void getAnalysisHistory(Object tag, ILotteryKind kind, Callback callback) {
        Maps.gen();
        Maps.put("lotCode", kind.getId168());
        String url = "";

        if (kind.getOriginType().getCode().equalsIgnoreCase("kuai3")) {
            url = GET_ANALYSIS_HISTORY_KS;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("ssc")) {
            url = GET_ANALYSIS_HISTORY_SSC;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("11s5")) {
            url = GET_ANALYSIS_HISTORY_EF;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("other")) {
            url = GET_ANALYSIS_HISTORY_PKS;
        }


        MM.http.get(tag, url, Maps.get(), callback);
    }

    public static class RespAnalysisHistory {
        public int errorCode;
        public String message;
        public AnalysisHistory result;
    }

    /**
     * 路珠
     * lotCode=10002&rows=10&date=2018-3-29
     */
    public static final String GET_ANALYSIS_LUZHU_SSC = "https://api.api68.com/CQShiCai/queryComprehensiveRoadBead.do";
    public static final String GET_ANALYSIS_LUZHU_PKS = "https://api.api68.com/pks/queryComprehensiveRoadBead.do";
    public static final String GET_ANALYSIS_LUZHU_EF = "https://api.api68.com/ElevenFive/queryComprehensiveRoadBead.do";
    public static final String GET_ANALYSIS_LUZHU_KS = "https://api.api68.com/lotteryJSFastThree/getRoadOfBeadTotal.do";

    public static void getAnalysisLuZhu(Object tag, ILotteryKind kind, Callback callback) {
        Maps.gen();
        Maps.put("date", Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD));
        Maps.put("lotCode", kind.getId168());
        String url = "";

        if (kind.getOriginType().getCode().equalsIgnoreCase("kuai3")) {
            url = GET_ANALYSIS_LUZHU_KS;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("ssc")) {
            url = GET_ANALYSIS_LUZHU_SSC;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("11s5")) {
            url = GET_ANALYSIS_LUZHU_EF;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("other")) {
            url = GET_ANALYSIS_LUZHU_PKS;
        }

        MM.http.get(tag, url, Maps.get(), callback);
    }

    public static class RespAnalysisLuZhu {
        public int errorCode;
        public String message;
        public AnalysisLuZhu result;
    }

    /**
     * 冷热分析
     */
    public static final String GET_ANALYSIS_LENGRE_SSC = "https://api.api68.com/CQShiCai/queryDrawCodeHeatState.do";
    public static final String GET_ANALYSIS_LENGRE_PKS = "https://api.api68.com/pks/queryDrawCodeHeatState.do";
    public static final String GET_ANALYSIS_LENGRE_EF = "https://api.api68.com/ElevenFive/queryDrawCodeHeatState.do";
    public static final String GET_ANALYSIS_LENGRE_KS = "";

    public static void getAnalysisHotCold(Object tag, ILotteryKind kind, Callback
            callback) {
        Maps.gen();
        Maps.put("recentPeriods", 20);
        Maps.put("lotCode", kind.getId168());
        String url = "";


        if (kind.getOriginType().getCode().equalsIgnoreCase("kuai3")) {
            url = GET_ANALYSIS_LENGRE_KS;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("ssc")) {
            url = GET_ANALYSIS_LENGRE_SSC;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("11s5")) {
            url = GET_ANALYSIS_LENGRE_EF;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("other")) {
            url = GET_ANALYSIS_LENGRE_PKS;
        }

        MM.http.get(tag, url, Maps.get(), callback);
    }

    public static class RespAnalysisHotCold {
        public int errorCode;
        public String message;
        public AnalysisHotCold result;
    }

    /**
     * 两面分析
     */
    public static final String GET_ANALYSIS_SIDES_SSC = "https://api.api68.com/CQShiCai/queryNewestDataForDsdx.do";
    public static final String GET_ANALYSIS_SIDES_PKS = "https://api.api68.com/pks/queryNewestDataForDsdx.do";
    public static final String GET_ANALYSIS_SIDES_EF = "";
    public static final String GET_ANALYSIS_SIDES_KS = "";

    public static void getAnalysisSides(Object tag, ILotteryKind kind, Callback
            callback) {
        Maps.gen();
        Maps.put("lotCode", kind.getId168());
        String url = "";


        if (kind.getOriginType().getCode().equalsIgnoreCase("kuai3")) {
            url = GET_ANALYSIS_SIDES_KS;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("ssc")) {
            url = GET_ANALYSIS_SIDES_SSC;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("11s5")) {
            url = GET_ANALYSIS_SIDES_EF;
        } else if (kind.getOriginType().getCode().equalsIgnoreCase("other")) {
            url = GET_ANALYSIS_SIDES_PKS;
        }

        MM.http.get(tag, url, Maps.get(), callback);
    }

    public static class RespAnalysisSides {
        public int errorCode;
        public String message;
        public AnalysisSides result;
    }


    /**
     * 获取菜种过去期开奖号码接口
     * [{"lottery":"XJSSC","issue":"20180316-048","method":"sxzhixfsh","content":"-,-,2,2,2","model":"jiao","multiple":2,"code":1900,"compress":false}]
     * <p>
     * <p>
     * 提交彩票订单接口
     */
    public static final String ADD_ORDER = "/yx/u/api/game-lottery/add-order";

    public static void addOrder(Object tag, ArrayList<HashMap<String, Object>> listHit, AbHttpResult result) {
        ArrayList<HashMap<String, Object>> request = new ArrayList<>();
        for (int i = 0; i < listHit.size(); i++) {
            Maps.gen();
            HashMap<String, Object> map = listHit.get(i);
            Maps.put(CtxLottery.Order.LOTTERY, map.get(CtxLottery.Order.LOTTERY));

            Maps.put(CtxLottery.Order.ISSUE, map.get(CtxLottery.Order.ISSUE));
            Maps.put(CtxLottery.Order.METHOD, map.get(CtxLottery.Order.METHOD));
            Maps.put(CtxLottery.Order.CONTENT, map.get(CtxLottery.Order.CONTENT));
            Maps.put(CtxLottery.Order.MODEL, map.get(CtxLottery.Order.MODEL));
            Maps.put(CtxLottery.Order.MULTIPLE, map.get(CtxLottery.Order.MULTIPLE));
            Maps.put(CtxLottery.Order.CODE, map.get(CtxLottery.Order.CODE));
            Maps.put("compress", map.get("compress"));

            Maps.put(CtxLottery.Order.ISSUE, map.get(CtxLottery.Order.ISSUE));
            Maps.put(CtxLottery.Order.METHOD, map.get(CtxLottery.Order.METHOD));
            Maps.put(CtxLottery.Order.CONTENT, map.get(CtxLottery.Order.CONTENT));
            Maps.put(CtxLottery.Order.MODEL, map.get(CtxLottery.Order.MODEL));
            Maps.put(CtxLottery.Order.MULTIPLE, map.get(CtxLottery.Order.MULTIPLE));
            Maps.put(CtxLottery.Order.CODE, map.get(CtxLottery.Order.CODE));
            Maps.put(CtxLottery.Order.COMPRESS, map.get(CtxLottery.Order.COMPRESS));

            request.add(Maps.get());
        }
        Maps.gen();
        Maps.put("text", new Gson().toJson(request));
        MM.http.post(tag, ADD_ORDER, Maps.get(), result);
    }

    public static final String ADD_ORDER_JD = "/yx/u/api/xjw-lottery/bet";

    /**
     * 经典提交彩种
     */
    public static void getAddOrderJD(Object tag, String
            lotteryCode, ArrayList<ReqBetParameter> betParameters, AbHttpResult
                                             result) {
        Maps.gen();
        Maps.put("lotteryId", lotteryCode);
        Maps.put("betParameters", betParameters);
        MM.http.postJson(tag, ADD_ORDER_JD, Maps.get(), result);
    }

    public static final String ADD_ORDER_LHC = "/yx/u/api/xjw-lottery/bet";

    public static class RespAddOrderJD {
        public int result;
        public String msg;
    }

    /**
     * 经典提交彩种
     */
    public static void getAddOrderLHC(Object tag, String
            lotteryCode, ArrayList<ReqBetParameter> betParameters, AbHttpResult
                                              result) {
        Maps.gen();
        Maps.put("lotteryId", lotteryCode);
        Maps.put("betParameters", betParameters);
        MM.http.postJson(tag, ADD_ORDER_LHC, Maps.get(), result);
    }

    public static class ReqBetParameter {
        public int id;
        public String gname;
        public String BetContext;
        public String Lines;
        public int BetType;
        public String Money;
        public String rate;
        public boolean IsTeMa;
        public boolean IsForNumber;
        public int mingxi_1;
    }


    public static class RespAddOrderLHC {
        public int result;
        public String msg;
    }


    /**
     * 撤销订单
     */
    public static final String CANCEL_ORDER = "/yx/u/api/game-lottery/cancel-order";

    public static void cancelOrder(Object tag, String billno, AbHttpResult
            result) {
        Maps.gen();
        Maps.put("billno", billno);

        MM.http.get(tag, CANCEL_ORDER, Maps.get(), result);
    }

    /**
     * 撤销追号订单
     */
    public static final String CANCEL_CHASE = "/yx/u/api/game-lottery/cancel-chase";

    public static void cancelChase(Object tag, String billno, AbHttpResult
            result) {
        Maps.gen();
        Maps.put("type", "chase");
        Maps.put("billno", billno);
        MM.http.get(tag, CANCEL_CHASE, Maps.get(), result);
    }

    public static final String CANCEL_SINGLE_CHASE = "/yx/u/api/game-lottery/cancel-single-chase";

    public static void cancelSingleChase(Object tag, String billno, AbHttpResult
            result) {
        Maps.gen();
        Maps.put("type", "general");
        Maps.put("billno", billno);
        MM.http.get(tag, CANCEL_SINGLE_CHASE, Maps.get(), result);
    }

    /**
     * 获取更新信息, 线路切换信息
     */
    public static final String GET_UPDATE_INFO = Config.SWITCH_LINE;

    /**
     * 初始化用户数据
     */
    public static void getUpdateInfo(Object tag, Callback result) {
        Maps.gen();
        MM.http.get(tag, GET_UPDATE_INFO, Maps.get(), result);
    }

    /**
     * 消息中心-消息查询接口
     */
    public static final String GET_MESSAGE_CENTER = "/yx/u/api/notice/list";

    public static void getMessageCenter(Object tag, int page,
                                        int size, AbHttpResult result) {
        Maps.gen();
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.get(tag, GET_MESSAGE_CENTER, Maps.get(), result);
    }

    /**
     * banner查询接口
     */
    public static final String GET_BANNER_INFO = "/yx/api/bannerInfo/getList";

    public static void getBannerInfo(Object tag, AbHttpResult result) {
        getBannerInfo(tag, 3, result);
    }

    public static void getBannerInfo(Object tag, int type, AbHttpResult result) {
        Maps.gen();
        Maps.put("showTerminalType", type);
        MM.http.get(tag, GET_BANNER_INFO, Maps.get(), result);
    }


    /**
     * 金洋 开户中心 链接管理
     */
    public static final String LINK_MANAGE = "/yx/u/api/agent/list-regist-link";

    public static void linkManage(Object tag, int page, int size, AbHttpResult result) {
        Maps.gen();
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.get(tag, LINK_MANAGE, Maps.get(), result);
    }

    /**
     * 消息列表
     */
    public static final String LIST_MESSAGE = "/yx/u/api/account/list-message";

    public static void listMessage(Object tag, int page, int size, AbHttpResult result) {
        Maps.gen();
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.get(tag, LIST_MESSAGE, Maps.get(), result);
    }


    /**
     * 发消息
     */
    public static final String SEND_MESSAGE = "/yx/u/api/account/send-message";

    public static void sendMessage(Object tag, String target, String tos, String subject, String content, AbHttpResult result) {
        Maps.gen();
        Maps.put("target", target);
        Maps.put("tos", tos);
        Maps.put("subject", subject);
        Maps.put("content", content);
        MM.http.get(tag, SEND_MESSAGE, Maps.get(), result);
    }

    /**
     * 获取发送消息目标
     */
    public static final String MESSAGE_MEMBER = "/yx/u/api/agent/list-direct-account";

    public static void getDirectAccountList(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, MESSAGE_MEMBER, Maps.get(), result);
    }


    /**
     * 读取消息内容
     */
    public static final String READ_MESSAGE = "/yx/u/api/account/read-message";

    public static void setReadMessage(Object tag, String ids, AbHttpResult result) {
        Maps.gen();
        Maps.put("ids", ids);
        MM.http.get(tag, READ_MESSAGE, Maps.get(), result);
    }


    public static final String DELETE_LINK = "/yx/u/api/agent/delete-regist-link";

    public static void deleteLink(Object tag, String id, AbHttpResult result) {
        Maps.gen();
        Maps.put("id", id);
        MM.http.get(tag, DELETE_LINK, Maps.get(), result);
    }

    /**
     * 删除消息
     */
    public static final String DELETE_MESSAGE = "/yx/u/api/account/delete-message";

    public static final void deleteMessage(Object tag, int id, AbHttpResult result) {
        Maps.gen();
        Maps.put("ids", id);
        MM.http.get(tag, DELETE_MESSAGE, Maps.get(), result);
    }

    /**
     * 升点
     */
    public static final String IMPROVE_POINT = "/yx/u/api/agent/edit-point-by-quota";

    public static void improvePoint(Object tag, String username, String point, AbHttpResult result) {
        Maps.gen();
        Maps.put("username", username);
        Maps.put("point", point);
        MM.http.get(tag, IMPROVE_POINT, Maps.get(), result);
    }


    /**
     * 获取菜种过去期开奖号码接口
     */
    public static final String GET_LOTTERY_OEPN_TIME = "/yx/u/api/game-lottery/static-open-time";

    public static void getLotteryOepnTime(Object tag, String name, AbHttpResult result) {
        Maps.gen();
        Maps.put("name", name);
        MM.http.get(tag, GET_LOTTERY_OEPN_TIME, Maps.get(), result);
    }

    /**
     * 初始化彩种
     */
    public static final String GET_LOTTERY_PLAY_INFO = "/yx/u/api/game-lottery/init-game-lottery";

    public static void getLotteryPlayInfo(Object tag, int lotteryId, AbHttpResult result) {
        Maps.gen();
        Maps.put("name", lotteryId);
        MM.http.get(tag, GET_LOTTERY_PLAY_INFO, Maps.get(), result);
    }

    /**
     * 初始化彩种
     */
    public static final String GET_LOTTERY_PLAY_INFO_JD = "/yx/u/api/xjw-lottery/init-game-lottery";

    public static void getLotteryPlayInfoJD(Object tag, String lotteryCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("lotteryId", lotteryCode);
        Maps.put("numberPostion", "");
        MM.http.postJson(tag, GET_LOTTERY_PLAY_INFO_JD, Maps.get(), result);
    }

    /**
     * 初始化彩种
     */
    public static final String GET_LOTTERY_PLAY_INFO_LHC = "/yx/u/api/xjw-lottery/init-game-lottery";

    public static void getLotteryPlayInfoLHC(Object tag, String lotteryCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("lotteryId", lotteryCode);
        Maps.put("numberPostion", "");
        MM.http.postJson(tag, GET_LOTTERY_PLAY_INFO_LHC, Maps.get(), result);
    }

    /**
     * 初始化
     */
    public static final String INIT_XJ_BONUS = "/yx/u/api/bonus/init-xj-bonus";

    public static void initXjBonus(Object tag, String username, AbHttpResult result) {
        Maps.gen();
        Maps.put("username", username);
        MM.http.get(tag, INIT_XJ_BONUS, Maps.get(), result);
    }

    /**
     * 初始化单单工资
     */
    /**
     * type 1亏损  2中奖  3日工资
     */
    public static final String INIT_ORDER_BONUS = "/yx/u/api/bonus/init-order-bonus";

    public static void initOrderBonus(Object tag, String username, int type, AbHttpResult result) {
        Maps.gen();
        Maps.put("username", username);
        Maps.put("type", type);
        MM.http.get(tag, INIT_ORDER_BONUS, Maps.get(), result);
    }

    /**
     * 设置单单工资
     */
    public static final String UPDATE_BONUS = "/yx/u/api/bonus/update-order-bonus";

    public static void updateBonus(Object tag, String userId, int type, String toPercent, AbHttpResult result) {
        Maps.gen();
        Maps.put("userId", userId);
        Maps.put("type", type);
        Maps.put("toPercent", toPercent);
        MM.http.get(tag, UPDATE_BONUS, Maps.get(), result);
    }

    /**
     * 工资管理
     */
    public static final String INIT_SALARY = "/yx/u/api/salary/init-xj-salary";

    public static void initSalary(Object tag, String username, AbHttpResult result) {
        Maps.gen();
        Maps.put("username", username);
        MM.http.get(tag, INIT_SALARY, Maps.get(), result);
    }

    public static void initSalary(Object tag, String username, String timeType, AbHttpResult result) {
        Maps.gen();
        Maps.put("username", username);
        if (!Strs.isEmpty(timeType)) {
            Maps.put("timeType", timeType);
        }
        MM.http.get(tag, INIT_SALARY, Maps.get(), result);
    }

    public static final String UPDATE_BONUSCFG = "/yx/u/api/bonus/update-bonusCfg";

    public static void update_BonusCFG(Object tag, String userid, int checklast, List<UserPoints> userPointsList, AbHttpResult result) {

        Maps.gen();
        String json = new Gson().toJson(userPointsList);
        Maps.put("userId", userid);
        Maps.put("checkLast", checklast);
        Maps.put("mixPercents", json.replace("\\", ""));
        MM.http.get(tag, UPDATE_BONUSCFG, Maps.get(), result);

    }

    /**
     * 分红管理
     */
    public static final String ZJ_BONUS = "/yx/u/api/bonus/zj-bonus";

    public static void Zj_Bonus(String tag, AbHttpResult result) {
        Maps.gen();
        Maps.put("qbId", "RMB");
        MM.http.get(tag, ZJ_BONUS, Maps.get(), result);
    }

    public static final String XJ_BONUS = "/yx/u/api/bonus/xiaji-bonus";
    public static final String XJ_BONUS_2 = "/yx/u/api/bonus/init-xj-bonus";
    public static final String DAY_BONUS = "/yx/u/api/account/activity-reward-record";//5级用户专有

    public static void Xj_Bonus(Object tag, String username, String sTime, String eTime, int page, int size, AbHttpResult result) {
        Maps.gen();
        Maps.put("start", sTime);
        Maps.put("end", eTime);
        Maps.put("posttype", "get");
        Maps.put("currPage", page);
        Maps.put("qbId", "RMB");
        Maps.put("pageSize", size);
        if (!Strs.isEmpty(username)) {
            Maps.put("userName", username);
        }
        MM.http.get(tag, XJ_BONUS, Maps.get(), result);
    }

    public static void getDayBonus(Object tag,String sTime, String eTime, int page, int size, AbHttpResult result) {
        Maps.gen();
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.get(tag, DAY_BONUS, Maps.get(), result);
    }

    public static final String PALL_BONUS = "/yx/u/api/bonus/pall-bonus";

    public static void pallBonus(String tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, PALL_BONUS, Maps.get(), result);
    }

    /**
     * 更新单单分红
     */
    public static final String UPDATE_BONUS_CFG = "/yx/u/api/bonus/update-bonusCfg";

    public static void updateBonusCFG(Object tag, String userid, int checklast, List<UserSalary> userSalaryList, AbHttpResult result) {

        Maps.gen();
        String json = new Gson().toJson(userSalaryList);
        Maps.put("userId", userid);
        Maps.put("checkLast", checklast);
        Maps.put("mixPercents", json.replace("\\", ""));
        MM.http.get(tag, UPDATE_BONUS_CFG, Maps.get(), result);

    }

    /**
     * 更新单单分红
     */
    public static final String UPDATE_SALARY = "/yx/u/api/salary/update-salary";

    public static void updateSalary(Object tag, String userid, List<UserSalary> userSalaryList, AbHttpResult result) {

        Maps.gen();
        String json = new Gson().toJson(userSalaryList);
        Maps.put("userId", userid);
        Maps.put("mixPercent", json.replace("\\", ""));
        MM.http.get(tag, UPDATE_SALARY, Maps.get(), result);
    }

    public static void updateSalary(Object tag, String userid, List<UserSalary> userSalaryList, String timeType, AbHttpResult result) {

        Maps.gen();
        String json = new Gson().toJson(userSalaryList);
        Maps.put("userId", userid);
        Maps.put("mixPercent", json);
        if (!Strs.isEmpty(timeType))
            Maps.put("timeType", timeType);
        MM.http.get(tag, UPDATE_SALARY, Maps.get(), result);
    }

    /**
     * 获取会员管理配置
     */

    public static final String QUERY_SALARY_BONUS = "/yx/u/api/account/querySalaryBonus";

    public static void getQuerySalaryBonus(AbHttpResult result) {
        Maps.gen();
        MM.http.get(null, QUERY_SALARY_BONUS, Maps.get(), result);
    }


    /**
     * 获取钱包列表
     */

    public static final String GET_PCODECB_BASE_LIST = "/yx/api/i/u/bank/getPcodeCbBaseList";

    public static void getThirdPocketList(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, GET_PCODECB_BASE_LIST, Maps.get(), result);
    }

    public static void getThirdPocketAmount(Object tag, int platformId, AbHttpResult result) {
        Maps.gen();
        Maps.put("platformId", platformId);
        /* 后台强制刷新标示符*/
        Maps.put("isForced", true);
        MM.http.get(tag, SHOW_THIRD_AMOUNT, Maps.get(), result);
    }

    /**
     * 获取游戏链接
     */
    public static final String GET_LOGIN_URL = "yx/thirdGameApi/common/getLoginUrl";

    public static void getLoginUrl(Object tag, int platformId, String subGame, AbHttpResult result) {
        Maps.gen();
        Maps.put("platformId", platformId);
        Maps.put("subGame", subGame);
        Maps.put("isMobile", 1);
        MM.http.get(tag, GET_LOGIN_URL, Maps.get(), result);
    }

    public static final String GET_GAME_TYPE = "/yx/u/api/report/get-game-type";

    public static void getGameType(Object tag, String gameType, AbHttpResult result) {
        Maps.gen();
        Maps.put("gameType", gameType);
        MM.http.get(tag, GET_GAME_TYPE, Maps.get(), result);
    }

    public static final String GET_RECORD_VR = "/yx/u/api/game/vr/order";

    /**
     * 获取VR游戏记录  VR金星1.5分彩
     *
     * @param tag
     * @param vr     VR金星1.5分彩 =1
     * @param state
     * @param sTime
     * @param eTime
     * @param page
     * @param size
     * @param result
     */
    public static void getRecordVR(Object tag, int vr, String state, String sTime, String eTime, int page, int size, AbHttpResult result) {
        Maps.gen();
        Maps.put("vr", vr);
        Maps.put("lottery", "VR");
        Maps.put("state", state);
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("page", page);
        Maps.put("size", size);

        MM.http.get(tag, GET_RECORD_VR, Maps.get(), result);
    }


    public static final String TCG_REPORT = "/yx/u/api/report/tcg-report";

    /**
     * 天彩VR游戏记录
     *
     * @param tag
     * @param betDateBegin
     * @param betDateEnd
     * @param page
     * @param size
     * @param result
     */
    public static void getTcVRReport(Object tag, String betDateBegin, String betDateEnd, int page, int size, AbHttpResult result) {
        Maps.gen();
        Maps.put("betDateBegin", betDateBegin);
        Maps.put("betDateEnd", betDateEnd);
        Maps.put("page", page);
        Maps.put("size", size);

        MM.http.get(tag, TCG_REPORT, Maps.get(), result);
    }

    /**
     * 第三方游戏记录
     */
    public static final String IM_REPORT = "/yx/u/api/report/im-report";
    private static final String AGIN_REPORT = "/yx/u/api/report/agin-report";
    private static final String KY_REPORT = "/yx/u/api/report/ky-report";

    public static void getThirdReport(Object tag, String gameType, String betDateBegin, String betDateEnd, int page, int size, String game, AbHttpResult result) {
        Maps.gen();
        Maps.put("gameType", gameType);
        Maps.put("betDateBegin", betDateBegin);
        Maps.put("betDateEnd", betDateEnd);
        Maps.put("page", page);
        Maps.put("size", size);

        if (game.equals(ActThirdGameRecord.THIRD_TYPE_IM)) {
            MM.http.get(tag, IM_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecord.THIRD_TYPE_AGIN)) {
            MM.http.get(tag, AGIN_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecord.THIRD_TYPE_KY)) {
            MM.http.get(tag, KY_REPORT, Maps.get(), result);
        } else {
            MM.http.get(tag, IM_REPORT, Maps.get(), result);
        }

    }

    /**
     * 活动记录
     */
    public static final String ACTIVITY_RECORD = "/yx/u/api/account/activity-Record";

    public static void getActivityRecord(Object tag, String startTime, String endTime, int page, AbHttpResult result) {
        Maps.gen();
        Maps.put("sTime", startTime);
        Maps.put("eTime", endTime);
        Maps.put("status", 0);
        Maps.put("size", 20);
        Maps.put("page", page);
        MM.http.post(tag, ACTIVITY_RECORD, Maps.get(), result);
    }


    public static final String SHOW_THIRD_AMOUNT = "/yx/thirdGameApi/common/showThirdAmount";


    /**
     * 获取订单详情
     */
    public static final String GET_ORDER = "/yx/u/api/game-lottery/get-order";

    public static void getOrderDetail(Object tag, String billno, AbHttpResult result) {
        Maps.gen();
        Maps.put("billno", billno);
        MM.http.get(tag, GET_ORDER, Maps.get(), result);
    }

    /**
     * 获取转账标识
     */
    public static final String GET_PLAYER_TRANSFER_REFRESH_TOKEN = "/yx/api/i/u/bank/playerTransferRefreshToken";

    public static void getPlayerTransferRefreshToken(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, GET_PLAYER_TRANSFER_REFRESH_TOKEN, Maps.get(), result);
    }

    /**
     * 三方平台转账
     */
    public static final String PLAYER_TRANSFER = "/yx/api/i/u/bank/playerTransfer";

    public static void commitPlayerTransfer(Object tag,
                                            String turnOut,
                                            String turnIn,
                                            double cash,
                                            String payPasswd,
                                            String token,
                                            AbHttpResult result) {
        Maps.gen();
        Maps.put("turnOut", turnOut);
        Maps.put("turnIn", turnIn);
        Maps.put("cash", cash);
        Maps.put("payPasswd", payPasswd);
        Maps.put("token", token);
        MM.http.get(tag, PLAYER_TRANSFER, Maps.get(), result);
    }

    /**
     * @param tag
     * @param turnOut
     * @param turnIn
     * @param cash
     * @param token
     * @param result
     */
    public static void commitPlayerTransferTC(Object tag,
                                              String turnOut,
                                              String turnIn,
                                              double cash,
                                              String payPasswd,
                                              String token,
                                              AbHttpResult result) {
        Maps.gen();
        Maps.put("turnOut", turnOut);
        Maps.put("turnIn", turnIn);
        Maps.put("cash", cash);
        Maps.put("payPasswd", MD5.md5(payPasswd).toLowerCase());
        Maps.put("token", token);
        MM.http.get(tag, PLAYER_TRANSFER, Maps.get(), result);
    }

    /**
     * 第三方团队报表
     */
    public static final String THIRD_TEAM_REPORTS = "/yx/u/api/report/third-team-report";

    public static void getThirdTeamReport(Object tag,
                                          String userName,
                                          String betDateBegin,
                                          String betDateEnd,
                                          int page,
                                          int size,
                                          AbHttpResult result) {
        Maps.gen();
        Maps.put("betDateBegin", betDateBegin);
        Maps.put("betDateEnd", betDateEnd);
        Maps.put("userName", userName);
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.get(tag, THIRD_TEAM_REPORTS, Maps.get(), result);
    }

    public static final String PREPARE_BIND_CARD = "yx/u/api/account/prepare-bind-card";

    public static void prepareBindCard(Object tag,
                                       AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, PREPARE_BIND_CARD, Maps.get(), result);
    }

    public static final String GET_BIND_INFO = "yx/u/api/account/get-bind-info";

    public static void getBindInfo(Object tag,
                                   AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, GET_BIND_INFO, Maps.get(), result);
    }

    /**
     * 公共测试接口
     *
     * @param tag
     * @param url
     * @param paramsMap
     * @param result
     */
    public static void commonRequest(Object tag, String url, String method, HashMap paramsMap, AbHttpResult result) {
        switch (method) {
            case "GET":
                MM.http.get(tag, url, paramsMap, result);
                break;
            case "POST":
                MM.http.get(tag, url, paramsMap, result);
                break;
            default:
                MM.http.get(tag, url, paramsMap, result);
                break;
        }

    }


    /**
     * 测试host 是否可访问
     */
    public static String TESTHOST = "upload/teapp.json";

    public static void getTeappJson(Object tag, String host, AbHttpResult abHttpResult) {
        MM.http.setHost(host);
        MM.http.get(tag, TESTHOST, Maps.get(), abHttpResult);
    }

    /**
     * 测试host 是否可访问
     */
    public static void getTeappJson(Object tag, AbHttpResult abHttpResult) {
        MM.http.get(tag, TESTHOST, Maps.get(), abHttpResult);
    }

    /**
     * 获取后台配置的充值类型
     */
    public static final String GET_ALL_PAY_CATEGORY = "/yx/u/api/payment/request-all-category";

    public static void getAllPayCategory(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, GET_ALL_PAY_CATEGORY, Maps.get(), result);
    }

    private static final String SWITCH_COIN = "/yx/u/api/switch-coin";

    public static void switchCoin(Object tag, int id, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("qbId", id);
        MM.http.get(tag, SWITCH_COIN, Maps.get(), abHttpResult);
    }

    /**
     * 报表中心、代理中心-团队报表查询接口
     */
    private static final String SEFT_SALARY_URL = "/yx/u/api/salary/agent-salary";

    public static void getSelfSalaryData(Object tag, String username, String sTime, String eTime, AbHttpResult abHttpResult) {
        getSelfSalaryData(tag, username, sTime, eTime, "0", abHttpResult);
    }

    public static void getSelfSalaryData(Object tag, String username, String sTime, String eTime, String timeType, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("userName", username);
        Maps.put("sTime", sTime);
        Maps.put("eTime", eTime);
        Maps.put("page", "1");
        Maps.put("size", "10");
        if (!Strs.isEmpty(timeType))
            Maps.put("timeType", timeType);
        MM.http.post(tag, SEFT_SALARY_URL, Maps.get(), abHttpResult);
    }

    /**
     * 报表中心、代理中心-团队报表查询接口
     */
    private static final String SALARY_URL = "/yx/u/api/salary/xiaji-salary";

    public static void getSalaryData(Object tag, String username, String sTime, String eTime, int currPage,
                                     int size, String timeType, String qbId, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("userName", username);
        Maps.put("start", sTime);
        Maps.put("end", eTime);
        Maps.put("currPage", currPage);
        Maps.put("pageSize", size);
        //Maps.put("timeType", timeType);
        Maps.put("qbId", qbId);
        MM.http.post(tag, SALARY_URL, Maps.get(), abHttpResult);
    }

    private static final String ONLINE_MENMBER_URL = "/yx/u/api/agent/list-online-account";

    public static void getOnlineMember(Object tag, int currPage, int size, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("currPage", currPage);
        Maps.put("pageSize", size);
        MM.http.get(tag, ONLINE_MENMBER_URL, Maps.get(), abHttpResult);
    }

    private static final String IM_SEND_IMAGE_MSG = "/file/upload";

    public static void upLoadImageMsgFile(Object tag, File file, String targetUin, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("targetUin", targetUin);
        MM.http.post(tag, IM_SEND_IMAGE_MSG, Maps.get(), file, abHttpResult);
    }

    private static final String IM_GET_IMAGE_MSG = "/file/upload";

    public static void getImageMsgFile(Object tag, String id, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("id", id);
        MM.http.get(tag, IM_SEND_IMAGE_MSG, Maps.get(), abHttpResult);
    }
}
