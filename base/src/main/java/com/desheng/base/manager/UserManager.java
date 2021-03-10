package com.desheng.base.manager;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ab.dao.SharedPreferencesDAO;
import com.ab.debug.AbDebug;
import com.ab.global.AbDevice;
import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.global.Global;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.http.Callback;
import com.ab.module.MM;
import com.ab.push.IPushCallback;
import com.ab.thread.ThreadCollector;
import com.ab.util.AbAes;
import com.ab.util.Dialogs;
import com.ab.util.Files;
import com.ab.util.MD5;
import com.ab.util.Maps;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.util.aes.AESUtils;
import com.ab.view.MaterialDialog;
import com.bumptech.glide.Glide;
import com.desheng.base.R;
import com.desheng.base.action.DBAction;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.model.BonusSalaryBean;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayCategory;
import com.desheng.base.model.LotteryPlayJD;
import com.desheng.base.model.LotteryPlayLHCCategory;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.desheng.base.model.MemberManagementBean;
import com.desheng.base.model.PersonInfo;
import com.desheng.base.model.User;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.model.UserData;
import com.desheng.base.model.VersionUpdateInfo;
import com.desheng.base.panel.ActWebX5;
import com.desheng.base.util.FingerprintUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.AppUpdateService;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lee on 2018/4/13.
 */

public abstract class UserManager {

    private static final String TAG = "UserManager";

    public static final int REGIST_TYPE_ACCOUNT = 0;
    public static final int REGIST_TYPE_PHONE = 1;

    public static final String KEY_SELECTED_LOTTERYS = "selected_lotterys";
    public static final String KEY_PLAYED_LOTTERYS = "played_lotterys";
    public static final String KEY_LAST_LOTTERY = "last_lottery";

    public static String ACTION_LOGIN = "";
    public static String ACTION_REGIST = "";
    public static String DEFAULT_SELECTED_LATTERYS = "";
    public static Class<? extends Activity> LOGIN_ACTIVITY;
    public static Class<? extends Activity> QZ_MODIFY_PWD_ACTIVITY;

    protected static UserManager ins;

    //版本升级
    protected String lastVersion;
    protected MaterialDialog updateDialog;
    protected MaterialDialog updateDialogWithSwitch;
    protected BroadcastReceiver updateReceiver;

    public static UserManager setIns(UserManager manager) {
        ins = manager;
        return ins;
    }

    public static UserManager getIns() {
        return ins;
    }

    public static <T extends UserManager> T getIns(Class<T> clazz) {
        return (T) ins;
    }

    //-------------------用户事件------------------
    //用户已登录了
    public static final String EVENT_USER_LOGINED = BaseConfig.custom_flag + ".user.logined";
    //用户登录失效了, 相当于登出
    public static final String EVENT_USER_INVALIDE = BaseConfig.custom_flag + ".user.logouted";
    //用户登出了
    public static final String EVENT_USER_LOGOUTED = BaseConfig.custom_flag + ".user.logouted";
    //用户资料刷新了, 收到消息的面板仅更新ui
    public static final String EVENT_USER_INFO_UNPDATED = BaseConfig.custom_flag + ".user.infoupdated";
    //用户选择彩种刷新
    public static final String EVENT_USER_SELETE_LOTTERY = BaseConfig.custom_flag + ".user.selectelottery";
    //彩种名称等网络端数据刷新
    public static final String EVENT_USER_UPDATE_LOTTERY = BaseConfig.custom_flag + ".user.updatelottery ";
    //充值完成
    public static final String EVENT_USER_DEPOSIT_OK = BaseConfig.custom_flag + ".user.deposit_ok";
    //所有event加入EVENTS列表才会发送.
    public static String[] EVENTS = new String[]{
            EVENT_USER_LOGINED,
            EVENT_USER_LOGOUTED,
            EVENT_USER_INVALIDE,
            EVENT_USER_INFO_UNPDATED,
            EVENT_USER_SELETE_LOTTERY,
            EVENT_USER_UPDATE_LOTTERY,
            EVENT_USER_DEPOSIT_OK
    };

    //余额变动key
    public static final String AMOUNT_KEY = BaseConfig.custom_flag + "amount_change_key ";

    //-------------------用户数据---------------------
    private User ssouser;
    private UserData userData;

    protected SharedPreferencesDAO spUser;
    //--------------------全局数据--------------------
    protected SharedPreferencesDAO spPersistent;
    /**
     * 彩票分类索引
     */
    private List<String> listLotteryInfoIndex;
    private Map<String, List<LotteryInfo>> mapLotteryInfoIndex;
    private Map<Integer, LotteryInfo> mapLotteryInfo;
    /**
     * 玩法分类索引
     */
    private Map<String, Map<String, LotteryPlay>> mapLotteryPlayCategory;
    private Map<String, List<LotteryPlayCategory>> mapLotteryPlayCategoryList;
    private Map<String, String> mapLotteryPlayCategorySpecial;

    /**
     * 玩法配置索引
     */
    private Map<String, Map<String, LotteryPlayUIConfig>> mapLotteryPlayUIConfigWithCategory;
    private Map<Integer, LotteryPlayUIConfig> mapLotteryPlayUIConfigWithId;
    /**
     * 经典玩法配置
     */
    private Map<String, ArrayList<LotteryPlayJD>> mapLotteryPlayJD;
    /**
     * 六合彩玩法配置
     */
    private Map<String, LotteryPlayLHCUI> mapLotteryPlayLHC;
    private List<LotteryPlayLHCCategory> listLotteryPlayLHCCategory;
    private Map<Integer, String> mapZodiacName;
    private List<Integer> listLHCBallColor;

    /**
     * 银行数据
     */
    private Map<String, String> mapBank;

    private Map<String, String> mapBankFace;

    private Map<String, String> mapBankBg;

    public static String encrypt(String str) {
        //6位随机
        String encrpied = AbAes.encrypt(str, "xdexshengx");
        return encrpied;
    }

    private static String decrypt(String str) {
        String encrpied = str;
        if (Strs.isEmpty(encrpied)) {
            return "";
        } else {

            String clear = AbAes.decrypt(encrpied, "xdexshengx");
            return clear;
        }
    }

    public void refreshUserData() {
        if (UserManager.getIns().isNotLogined()) {
            return;
        }
        initUserData(new IUserDataSyncCallback() {
            @Override
            public void onUserDataInited() {

            }

            @Override
            public void afterUserDataInited() {

            }

            @Override
            public void onUserDataInitFaild() {
            }

            @Override
            public void onAfter() {

            }
        });
    }

    /**
     * 获取分红显示配置
     */
    public void getQuerySalaryBonus() {
        HttpAction.getQuerySalaryBonus(new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BonusSalaryBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                BonusSalaryBean bonusSalaryBean;
                if (error == 0 && code == 0) {
                    bonusSalaryBean = getField(extra, "data", null);
                    setHasBonus(bonusSalaryBean.getBonus());
                    setHasDandanFh(bonusSalaryBean.getDandanFh());
                    setHasSalary(bonusSalaryBean.getSalary());
                    setHasDandanRgz(bonusSalaryBean.getDandanRgz());
                    setHasDandanZjjj(bonusSalaryBean.getDandanZjjj());
                    setHasDandanGZ(bonusSalaryBean.getShowdandan());
                    setShowDandanFh(bonusSalaryBean.getShowdandanFh());
                    setShowDandanZjjj(bonusSalaryBean.getShowdandanZjjj());
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    /**
     * 初始化
     *
     * @param app
     */
    public void init(Application app) {
        spUser = new SharedPreferencesDAO(app, "user");
        spPersistent = new SharedPreferencesDAO(app, "persistent");
        new Thread(new Runnable() {
            @Override
            public void run() {
                initBankInfo();
                initLotteryInfo();
                initLotteryPlayCategory();
                initLotteryPlayUIConfig();
                initLotteryPlayUIConfigJD();
                initLotteryPlayUIConfigLHC();
                initLotteryPlayCategoryLHC();
                initLotteryPlayUIBallColor();
                initLHCMapZodicName();
                AbDebug.log(AbDebug.TAG_APP, "********解析初始化数据结束!*********");
            }
        }).start();
    }

    /**
     * 启动检查登录
     *
     * @param callback
     */
    public void validateLogin(IUserLoginCallback callback) {
        loginWithPassword(getAccount(), getPassword(), null, callback);
    }

    /**
     * 跳到登录
     */
    public void redirectToLogin() {
        Intent itt = new Intent(ACTION_LOGIN);
        itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        itt.addCategory(Intent.CATEGORY_DEFAULT);
        Global.app.startActivity(itt);
    }

    /**
     * 跳到登录
     */
    public void redirectToRegist() {
        Intent itt = new Intent(ACTION_REGIST);
        itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        itt.addCategory(Intent.CATEGORY_DEFAULT);
        Global.app.startActivity(itt);
    }

    /**
     * 跳到登录
     */
    public void redirectToLoginForLotteryPlay(String to, String lotteryId) {
        Intent itt = new Intent(ACTION_LOGIN);
        itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        itt.addCategory(Intent.CATEGORY_DEFAULT);
        itt.putExtra("to", to);
        itt.putExtra("lotteryId", lotteryId);
        Global.app.startActivity(itt);
    }


    public void redirectToLoginForRecheck() {
        Intent itt = new Intent(ACTION_LOGIN);
        itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        itt.addCategory(Intent.CATEGORY_DEFAULT);
        itt.putExtra("isFromCheck", true);
        Global.app.startActivity(itt);
    }

    /**
     * 从activity跳到登录
     *
     * @param act
     */
    public void redirectToLogin(Activity act) {
        Intent itt = new Intent(act, LOGIN_ACTIVITY);
        act.startActivity(itt);
    }

    /**
     * 从修改密码跳到登录
     *
     * @param act
     */
    public void reLogin(Activity act, String userName) {
        Intent itt = new Intent(act, LOGIN_ACTIVITY);
        itt.putExtra("userName", userName);
        setLogin(false);
        UserManager.getIns().setBroadCount(0);
        act.startActivity(itt);
    }

    /**
     * 从退出登录跳到登录
     *
     * @param act
     */
    public void reLogin(Activity act, String userName, String password) {
        Intent itt = new Intent(act, LOGIN_ACTIVITY);
        itt.putExtra("userName", userName);
        itt.putExtra("password", password);
        setLogin(false);
        UserManager.getIns().setBroadCount(0);
        act.startActivity(itt);
    }

    public void redirectToModifyPwd(Context context) {
        Intent itt = new Intent(context, QZ_MODIFY_PWD_ACTIVITY);
        itt.putExtra("redirectToModifyPwd", true);
        Global.app.startActivity(itt);
    }

    public void registUser(Object tag, final int registType, final String phoneNum, final String name, String yanzCode, final String phYzCode, final String pwd, final IUserRegistCallback registCallback, final IUserLoginCallback loginCallback, String inviteCode) {
        final String pwdStr = MD5.md5(pwd).toLowerCase();
        HttpAction.registUser(tag, registType, phoneNum, name, yanzCode, phYzCode, pwdStr, "", new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                registCallback.onBefore();
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespRegist.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    registCallback.onUserRegisted();
                    HttpAction.RespRegist req = getFieldObject(extra, "data", HttpAction.RespRegist.class);
                    if (registType == REGIST_TYPE_PHONE) {
                        loginWithPassword(req.userName, pwdStr, null, loginCallback);
                        setAccount(req.userName);
                    } else if (registType == REGIST_TYPE_ACCOUNT) {
                        loginWithPassword(name, pwdStr, null, loginCallback);
                        setAccount(name);
                    }
                } else {
                    registCallback.onUserRegistFailed(msg);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                registCallback.onUserRegistFailed(content);
                return true;
            }

            @Override
            public void onAfter(int id) {
                registCallback.onAfter();
            }
        }, inviteCode);
    }


    /**
     * 用户密码登录
     *
     * @param account
     * @param password
     * @param capchaCode
     * @param callback
     */
    public void loginWithPassword(final String account, final String password, @Nullable final String capchaCode, final IUserLoginCallback callback) {
        //1.请求登录
        //MM.http.clearCookies();
        HttpAction.loginWithPassword(this, account, password, capchaCode, null, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("user", User.class);
                entity.putField("needCapchaCode", Boolean.TYPE);
                entity.putField("loginFailedCount", Integer.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0) { //登录成功
                    //Logger.t(TAG).e("-----------登录成功-----------");
                    spUser.clear();
                    setUser(getFieldObject(extra, "user", User.class));
                    setNeedCapchaCode(Maps.value(extra, "needCapchaCode", false));
                    //2. 再次请求接口, redirect给出ticket
                    if ("sso".equals(BaseConfig.HOST_USER_PREFIX)) {
                        //Logger.t(TAG).e("再次请求接口, redirect给出ticket");
                        HttpAction.loginNext(this, new AbHttpResult() {
                            @Override
                            public void setupEntity(AbHttpRespEntity entity) {
                                entity.putField("code", String.class);
                                entity.putField("userName", String.class);
                            }

                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                setUserName(getField(extra, "userName", ""));

                                //3. 获取用户数据
                                initUserData(new IUserDataSyncCallback() {
                                    @Override
                                    public void onUserDataInited() {
                                        callback.onUserLogined(true);
                                        setAccount(account);
                                        setPassword(password);
                                    }

                                    @Override
                                    public void afterUserDataInited() {

                                    }

                                    @Override
                                    public void onUserDataInitFaild() {
                                        callback.onUserLoginFailed("用户数据下载失败,请重试!");
                                    }

                                    @Override
                                    public void onAfter() {
                                        callback.onAfter();
                                    }
                                });
                                getQuerySalaryBonus();
                                sendBroadcaset(Global.app, EVENT_USER_LOGINED, null);
                                callback.onUserLogined(false);
                                return true;
                            }

                            @Override
                            public boolean onError(int status, String content) {
                                Toasts.show(Global.app, "用户获取Ticket失败!");
                                //Logger.t(TAG).e("用户获取Ticket失败!");
                                callback.onUserNeedValidateCode();
                                callback.onAfter();
                                return true;
                            }
                        });
                    } else { //新版yx
                        //3. 获取用户数据
                        initUserData(new IUserDataSyncCallback() {
                            @Override
                            public void onUserDataInited() {
                                callback.onUserLogined(true);
                                sendBroadcaset(Global.app, EVENT_USER_LOGINED, null);
                                setAccount(account);
                                setPassword(password);
                            }

                            @Override
                            public void afterUserDataInited() {
                            }

                            @Override
                            public void onUserDataInitFaild() {
                                callback.onUserLoginFailed("用户数据下载失败!");
                            }

                            @Override
                            public void onAfter() {
                                callback.onAfter();
                            }
                        });
                        getQuerySalaryBonus();
                    }

                    //同时开始绑定推送
                    if (BaseConfig.isUsePush && MM.push != null) {
                        MM.push.bindAccount(null, null, new IPushCallback() {
                            @Override
                            public void onSuccess(String account, String group, String pushDeviceId) {
                                //Logger.t(TAG).e("设备token:(" + pushDeviceId + ")获取成功, 开始绑定账号");
                                if (Strs.isNotEmpty(pushDeviceId)) {
                                    HttpAction.setPushToken(UserManager.this, pushDeviceId, new AbHttpResult() {
                                        @Override
                                        public void setupEntity(AbHttpRespEntity entity) {
                                            //super.setupEntity(entity);
                                        }

                                        @Override
                                        public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                            return true;
                                        }

                                        @Override
                                        public boolean onError(int status, String content) {
                                            return true;
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailed(String var1, String var2) {
                                //Logger.t(TAG).e("设备token获取失败," + var1 + " ," + var2);
                            }
                        });
                    }
                } else if (code == 36) {//需要验证码
                    callback.onUserNeedValidateCode();
                } else if (code == 37) {//验证码输入错误
                    callback.onUserLoginFailed(msg);
                    callback.onUserNeedValidateCode();
                } else {
                    /* 账号密码连续输错5次  */
                    int loginFailedCount = getFieldObject(extra, "loginFailedCount", Integer.class);
                    if (Config.custom_flag.equals(BaseConfig.FLAG_UNION) && loginFailedCount >= 5) {
                        msg = "@" + String.valueOf(loginFailedCount);
                    }
                    //Logger.t(TAG).e("--------------登录失败--------------:\nmsg=" + msg);
                    callback.onUserLoginFailed(msg);
                    callback.onAfter();
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                //Logger.t(TAG).e("--------------登录失败--------------:\ncontent=" + content);
                callback.onUserLoginFailed(content);
                callback.onAfter();
                return true;
            }
        });

    }

    /**
     * 主动验证用户登录有效
     *
     * @param callback
     */
    public void checkLoginedValide(IUserLoginCallback callback) {
        /*if(getUIN() > 0 && Strs.isNotEmpty())
        String account = get*/
    }

    /**
     * 获取校验码图片
     *
     * @param ivCode
     */
    public void getVerifyImage(final Activity activity, final ImageView ivCode) {
        String suff = "" + System.currentTimeMillis();
        HttpAction.getImgCode(this, new FileCallBack(Global.app.getCacheDir().getAbsolutePath(), "verifyImage" + suff) {

            @Override
            public void onError(Call call, Exception e, int id) {
                System.out.println("verify");
            }

            @Override
            public void onResponse(File response, int id) {
                if (!isDestroy(activity)) {
                    Glide.with(activity).load(response).into(ivCode);
                }
            }
        });
    }

    public boolean isDestroy(Activity activity) {
        if (activity == null || activity.isFinishing() || (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 点击按钮，请求服务器给填你填写的邮箱发送一个验证码
     *
     * @param tag
     * @param email
     * @param verifyCode
     * @param callBack
     */
    public void getEmailCode(Object tag, String email, String verifyCode, final AbCodeCallBack callBack) {

        HttpAction.getEmailCode(tag, email, verifyCode, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<HashMap<String, Object>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    callBack.onSendSuccessfully();
                } else if (code == 1 && error == 1 && extra.get("data") != null) {
                    HashMap<String, Object> data = (HashMap<String, Object>) extra.get("data");
                    boolean needCapchaCode = Maps.value(data, "needCapchaCode", false);
                    if (needCapchaCode) {
                        callBack.onNeedImageCode();
                    }
                } else {
                    callBack.onSendCodeField(msg);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(content, false);
                return true;
            }
        });
    }

    /**
     * 获取短信验证码
     *
     * @param tag
     * @param phoneNum
     * @param yanzCode
     */
    public void getSmsCode2(Object tag, String phoneNum, String yanzCode, final AbCodeCallBack callBack) {
        HttpAction.getSmsCode2(tag, phoneNum, yanzCode, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<HashMap<String, Object>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (extra.get("data") != null) {
                    HashMap<String, Object> data = (HashMap<String, Object>) extra.get("data");
                    boolean needCapchaCode = Maps.value(data, "needCapchaCode", false);
                    if (needCapchaCode) {
                        callBack.onNeedImageCode();
                    } else {
                        callBack.onSendSuccessfully();
                    }
                } else {
                    callBack.onSendCodeField(msg);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(content, false);
                return true;
            }
        });
    }

    public void getGlobleSkinSettings(Object tag) {
        HttpAction.getGlobleSkinSettings(tag, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<HashMap<String, Object>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && extra.get("data") != null) {
                    HashMap<String, Object> data = (HashMap<String, Object>) extra.get("data");
                    String mobile = Maps.value(data, "mobile", "");
                    Log.d("UserManager", "mobile:" + mobile);
                    if (Strs.isNotEmpty(mobile)) {
                        int skinSettingMode = Integer.parseInt(mobile);
                        setSkinSetting(skinSettingMode);
                    } else {
                        setSkinSetting(0);
                    }
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }
        });
    }


    /**
     * 获取用户数据
     */
    public void initUserDataForceCookie(final IUserDataSyncCallback callback) {
        HttpAction.initUserDataForceCookie(this, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (callback != null) {
                    callback.onUserDataInited();
                }
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", UserData.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && extra.get("data") != null && getFieldObject(extra, "data", UserData.class).getMain() != null) {
                    setUserData(getFieldObject(extra, "data", UserData.class));
                    sendBroadcaset(Global.app, EVENT_USER_INFO_UNPDATED, null);
                    if (callback != null) {
                        callback.afterUserDataInited();
                    }
                } else {
                    if (callback != null) {
                        callback.onUserDataInitFaild();
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                if (callback != null) {
                    callback.onUserDataInitFaild();
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                callback.onAfter();
            }
        });
    }

    /**
     * 获取用户数据
     *
     * @param callback
     */
    public void initUserData(final IUserDataSyncCallback callback) {
        HttpAction.initUserData(this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", UserData.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && extra.get("data") != null && getFieldObject(extra, "data", UserData.class).getMain() != null) {
                    setUserData(getFieldObject(extra, "data", UserData.class));
                    if (callback != null) {
                        callback.onUserDataInited();
                    }
                    sendBroadcaset(Global.app, EVENT_USER_INFO_UNPDATED, null);
                    if (callback != null) {
                        callback.afterUserDataInited();
                    }
                } else {
                    if (callback != null) {
                        callback.onUserDataInitFaild();
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                if (callback != null) {
                    callback.onUserDataInitFaild();
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                callback.onAfter();
            }
        });
    }

    /**
     * 获取用户数据
     *
     * @param callback
     */
    public void initUserDataNew(final IUserDataSyncCallback callback) {
        HttpAction.initUserData(this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", UserData.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && extra.get("data") != null && getFieldObject(extra, "data", UserData.class).getMain() != null) {
                    setUserData(getFieldObject(extra, "data", UserData.class));
                    if (callback != null) {
                        callback.onUserDataInited();
                    }
                    //sendBroadcaset(Global.app, EVENT_USER_INFO_UNPDATED, null);
                    if (callback != null) {
                        callback.afterUserDataInited();
                    }
                } else {
                    if (callback != null) {
                        callback.onUserDataInitFaild();
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                if (callback != null) {
                    callback.onUserDataInitFaild();
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                callback.onAfter();
            }
        });
    }


    public void logout(final IUserLogoutCallback callback) {
        HttpAction.logout(this, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                callback.onBefore();
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0) {
                    callback.onUserLogouted();
                    clearUserInfo();
                    sendBroadcaset(Global.app, EVENT_USER_LOGOUTED, null);
                    msg = "用户已登出!";
                } else {
                    callback.onUserLogoutFailed(msg);
                }
                Toasts.show(Global.app, msg, true);
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                callback.onUserLogoutFailed(content);
                return true;
            }

            @Override
            public void onAfter(int id) {
                callback.onAfter();
            }
        });
    }

    public void bindPersonInfo(final int type, final String value, final IUserInfoSyncCallBack syncCallBack) {
        HttpAction.bindPersonInfo(this, type, value, new AbHttpResult() {
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {

                    switch (type) {
                        case 1://性别

                            setIsBindGender(true);
                            setInfoGender(value);
                            break;

                        case 2://生日
                            setIsBindBirthday(true);
                            setInfoBirthday(value);
                            break;

                        case 3://微信
                            setIsBindQQ(true);
                            setInfoQq(value);
                            break;

                        case 4://QQ
                            setIsBindWeChat(true);
                            setWeChatId(value);
                            break;


                    }

                    syncCallBack.onSync(msg);
                } else {
                    Toasts.show(msg);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

        });
    }

    public void isFirstRecharge(final IGuideCallBack guideCallBack) {
        HttpAction.isFirstRecharge(new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onGetString(String str) {
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    guideCallBack.onCallBack(jsonObject.getBoolean("data"));

                } catch (JSONException je) {
                    je.printStackTrace();
                }
                return super.onGetString(str);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }

        });
    }

    /**
     * 获取用户绑定信息
     *
     * @param getCallBack
     */
    public void getPersonSettingInfo(final IUserInfoGetCallBack getCallBack) {
        HttpAction.getPersonInfo(this, new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
                entity.putField("data", PersonInfo.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                PersonInfo personInfo = getFieldObject(extra, "data", PersonInfo.class);

                setInfoQq(personInfo.getQq());
                setInfoGender(personInfo.getGender());
                setInfoBirthday(personInfo.getBirthday());
                setInfoCellphone(personInfo.getCellphone());
                setInfoEmail(personInfo.getEmail());
                setWeChatId(personInfo.getWechatId());
                setEmail(personInfo.getEmail());
                setWithDrawName(personInfo.getWithdrawName());
                setInvateCode(personInfo.getInviteCode());
                setWithDrawPwd(personInfo.getWithdrawPassword());

                if (null != getCallBack) {
                    getCallBack.onCallBack(personInfo);
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(content, false);
                return super.onError(status, content);
            }
        });
    }

    /**
     * 返回用户是否绑定信息
     *
     * @param callback
     */
    public void checkUserBindStatus(final IUserBindCheckCallback callback) {
        if (isLogined()) {
            HttpAction.getUserBindStatus(new AbHttpResult() {
                @Override
                public void onBefore(Request request, int id, String host, String funcName) {
                    callback.onBefore();
                }

                @Override
                public void setupEntity(AbHttpRespEntity entity) {
                    entity.putField("data", new TypeToken<UserBindStatus>() {
                    }.getType());
                }

                @Override
                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                    if (code == 0 && error == 0) {
                        UserBindStatus status = getFieldObject(extra, "data", UserBindStatus.class);

                        setIsBindCellphone(status.isBindCellphone());
                        setIsBindBirthday(status.isBindBirthday());
                        setIsBindCard(status.isBindCard());
                        setIsBindEmail(status.isBindEmail());
                        setIsBindQQ(status.isBindQQ());
                        setIsBindWeChat(status.isBindWeChat());
                        setIsBindPhone(status.isBindCellphone());
                        setIsBindSecurity(status.isBindSecurity());
                        setIsBindWithdrawName(status.isBindWithdrawName());
                        setIsBindWithdrawPassword(status.isBindWithdrawPassword());
                        setIsCnReset(status.isCnReset());
                        setIsBindGender(status.isBindGender());
                        setIsPhoneRegister(status.isPhoneRegister());
                        setIsBindGoogleAuthenticator(status.isBindGoogleAuthenticator());
                        setSmsNotice(status.isBalanceChangedAlert());
                        setIsUpdateWithdrawPassword(status.isUpdateWithdrawPassword());
                        setIsModifiedPassword(status.isModifiedPassword());

                        if (status != null) {
                            callback.onUserBindChecked(status);
                        } else {
                            callback.onUserBindCheckFailed("无用户绑定数据");
                        }
                    } else {
                        callback.onUserBindCheckFailed("无用户绑定数据");
                    }

                    return true;
                }

                @Override
                public boolean onError(int status, String content) {
                    callback.onUserBindCheckFailed("无用户绑定数据");
                    return true;
                }

                @Override
                public void onAfter(int id) {
                    callback.onAfter();
                }
            });
        }

    }

    //*************************************全局本地信息 用户退出或重登录不会清除 start*****************************************

    public void setHasBonus(int bonus) {
        spPersistent.put("bonus", bonus);
    }

    public int hasBonus() {
        return spPersistent.getInt("bonus", 0);
    }

    public void setOpenVoiceTips(boolean isOpen) {
        spPersistent.put("open_voice", isOpen);
    }

    public boolean isOpenVoiceOpen() {
        return spPersistent.getBoolean("open_voice", true);
    }

    public void setHasDandanRgz(int dandanRgz) {
        spPersistent.put("dandanRgz", dandanRgz);
    }

    public void setHasDandanGZ(int showdandan) {
        spPersistent.put("showdandangz", showdandan);
    }


    public void setShowDandanFh(int showDandanFh) {
        spPersistent.put("showDandanFh", showDandanFh);
    }

    public boolean getShowDandanFh() {
        return spPersistent.getInt("showDandanFh", 0) == 1 ? true : false;
    }


    public void setShowDandanZjjj(int showDandanZjjj) {
        spPersistent.put("showDandanZjjj", showDandanZjjj);
    }

    public boolean getShowDandanZjjj() {
        return spPersistent.getInt("showDandanZjjj", 0) == 1 ? true : false;
    }


    public boolean getHasDandanGZ() {
        return spPersistent.getInt("showdandangz", 0) == 1 ? true : false;
    }

    public int hasDandanRgz() {
        return spPersistent.getInt("dandanRgz", 0);
    }

    public void setHasDandanFh(int dandanFh) {
        spPersistent.put("dandanFh", dandanFh);
    }

    public int hasDandanFh() {
        return spPersistent.getInt("dandanFh", 0);
    }

    public void setHasDandanZjjj(int dandanZjjj) {
        spPersistent.put("dandanZjjj", dandanZjjj);
    }

    public int hasDandanZjjj() {
        return spPersistent.getInt("dandanZjjj", 0);
    }

    public int hasSalary() {
        return spPersistent.getInt("salary", 0);
    }

    public void setHasSalary(int salary) {
        spPersistent.put("salary", salary);
    }

    public void setHideGuide(boolean hasShowGuide) {
        spPersistent.put("hasShowGuide", hasShowGuide);
    }

    public boolean getHideGuide() {
        return spPersistent.getBoolean("hasShowGuide", false);
    }

    public void setShowFloat(boolean isShowFloat) {
        spPersistent.put("isShowFloat", isShowFloat);
    }

    public boolean isShowFloat() {
        return spPersistent.getBoolean("isShowFloat", BaseConfig.isFloatBtnEnabled);
    }

    public void setShowVideo(boolean isShowFloat) {
        spPersistent.put("hasShowVideo", isShowFloat);
    }

    public boolean getShowVideo() {
        return spPersistent.getBoolean("hasShowVideo", false);
    }

    public void setUserFingerPrint(boolean isUserFingerPrint) {
        spPersistent.put("isUserFingerPrint", isUserFingerPrint);
    }

    public boolean isUserFingerPrint() {
        return spPersistent.getBoolean("isUserFingerPrint", false);
    }

    public void setAutoSwitch(boolean isAutoSwitch) {
        spPersistent.put("isAutoSwitch", isAutoSwitch);
    }

    public void setSkinSetting(int skinSetting) {
        spPersistent.put("SkinSetting", skinSetting);
    }

    public int getSkinSetting() {
        return spPersistent.getInt("SkinSetting", 0);
    }

    /**
     * @param isOpen 声音开关
     */
    public void setOpenVoice(boolean isOpen) {
        spPersistent.put("isOpenVoice", isOpen);
    }

    public boolean isOpenVoice() {
        return spPersistent.getBoolean("isOpenVoice", false);
    }

    /**
     * @param isOpen 振动开关
     */
    public void setOpenVibration(boolean isOpen) {
        spPersistent.put("isOpenVibration", isOpen);
    }

    public boolean isOpenVibration() {
        return spPersistent.getBoolean("isOpenVibration", false);
    }

    /**
     * @param isOpen 系统通知开关
     */
    public void setOpenNoticeWithSystem(boolean isOpen) {
        spPersistent.put("isOpenNoticeWithSystem", isOpen);
    }

    public boolean isOpenNoticeWithSystem() {
        return spPersistent.getBoolean("isOpenNoticeWithSystem", false);
    }

    /**
     * @param isOpen 活动通知开关
     */
    public void setOpenNoticeWithActivity(boolean isOpen) {
        spPersistent.put("isOpenNoticeWithActivity", isOpen);
    }

    public boolean isOpenNoticeWithActivity() {
        return spPersistent.getBoolean("isOpenNoticeWithActivity", false);
    }

    public boolean isSmsNotice() {
        return spPersistent.getBoolean("isSmsNotice", false);
    }

    public void setSmsNotice(boolean sms) {
        spPersistent.put("isSmsNotice", sms);
    }

    public boolean isUpdateWithdrawPassword() {
        return spPersistent.getBoolean("isUpdateWithdrawPassword", false);
    }

    public void setIsUpdateWithdrawPassword(boolean isUpdateWithdrawPassword) {
        spPersistent.put("isUpdateWithdrawPassword", isUpdateWithdrawPassword);
    }

    public boolean isModifiedPassword() {
        return spPersistent.getBoolean("isModifiedPassword", false);
    }

    public void setIsModifiedPassword(boolean isUpdateWithdrawPassword) {
        spPersistent.put("isModifiedPassword", isUpdateWithdrawPassword);
    }

    /**
     * @return 是否隐藏升点
     */
    public boolean isHideImprovePoint() {
        switch (Config.custom_flag) {
            case BaseConfig.FLAG_LEYOU:
                return getMainUserLevel() < 5;
            case BaseConfig.FLAG_CAISHIJI:
                return getMainUserLevel() < 5;

            case BaseConfig.FLAG_JINDU:
                return getMainUserLevel() < 5;
            default:
                return false;
        }
    }


    public boolean isHideDandanGongzi() {
        if (BaseConfig.FLAG_LEYOU.equals(Config.custom_flag) &&
                (getMainUserLevel() == 1
                        || getMainUserLevel() == 2
                        || getMainUserLevel() == 3
                        || getMainUserLevel() == 4)) {
            return true;
        }

        return false;
    }

    public boolean isHideQuota() {
        if (BaseConfig.FLAG_LONGTENG.equals(Config.custom_flag) &&
                (getMainUserLevel() == 1
                        || getMainUserLevel() == 2
                        || getMainUserLevel() == 3
                        || getMainUserLevel() == 4
                        || getMainUserLevel() == 5)) {
            return true;
        }
        return false;
    }

    public boolean isAutoSwitch() {
        return spPersistent.getBoolean("isAutoSwitch", false);
    }

    public void setUrlFevorite(String line) {
        spPersistent.put("urlFevorite", line);
    }

    public String getUrlFevorite() {
        return spPersistent.getString("urlFevorite", "");
    }

    /**
     * 添加用户选择的彩种
     *
     * @param lotteryId
     */
    public void addUserSelectedLottery(int lotteryId) {
        if (lotteryId <= 0) {
            return;
        }

        //如果之前已包含, 则不添加
        String str = spPersistent.getString(getUIN() + "_" + KEY_SELECTED_LOTTERYS, "[]");
        String[] arrStr = str.replace("[", "").replace("]", "").split(",");
        boolean isSelect = false;
        for (int i = 0; i < arrStr.length; i++) {
            if (arrStr[i].equals(Strs.of(lotteryId))) {
                isSelect = true;
                break;
            }
        }
        if (isSelect) {
            return;
        }

        Gson gson = new Gson();
        ArrayList<Integer> listIds = gson.fromJson(str, new TypeToken<ArrayList<Integer>>() {
        }.getType());
        listIds.add(lotteryId);
        spPersistent.setString(getUIN() + "_" + KEY_SELECTED_LOTTERYS, gson.toJson(listIds, new TypeToken<ArrayList<Integer>>() {
        }.getType()));
    }

    /**
     * 删除用户选择的彩种
     *
     * @param lotteryId
     */
    public void removeUserSelectedLottery(int lotteryId) {
        if (lotteryId <= 0) {
            return;
        }

        //如果之前未包含
        String str = spPersistent.getString(getUIN() + "_" + KEY_SELECTED_LOTTERYS, "[]");
        if (!str.contains(String.valueOf(lotteryId))) {
            return;
        }

        Gson gson = new Gson();
        ArrayList<Integer> listIds = gson.fromJson(str, new TypeToken<ArrayList<Integer>>() {
        }.getType());
        Integer delete = null;
        for (int i = 0; i < listIds.size(); i++) {
            if (listIds.get(i).intValue() == lotteryId) {
                delete = listIds.get(i);
            }
        }
        listIds.remove(delete);
        spPersistent.setString(getUIN() + "_" + KEY_SELECTED_LOTTERYS, gson.toJson(listIds, new TypeToken<ArrayList<Integer>>() {
        }.getType()));
    }

    /**
     * 用户选择的所有彩种
     *
     * @return
     */
    public ArrayList<Integer> getUserSelectedLotterys() {
        String str = spPersistent.getString(getUIN() + "_" + KEY_SELECTED_LOTTERYS, "[]");
        if (str.equals("[]")) {
            str = DEFAULT_SELECTED_LATTERYS;
        }
        ArrayList<Integer> listIds = new Gson().fromJson(str, new TypeToken<ArrayList<Integer>>() {
        }.getType());
        return listIds;
    }

    /**
     * 用户是否选择了彩种
     *
     * @param id
     * @return
     */
    public boolean isLotteryUserSelected(int id) {
        String str = spPersistent.getString(getUIN() + "_" + KEY_SELECTED_LOTTERYS, "[]");
        boolean isSelect = false;
        String[] arrStr = str.replace("[", "").replace("]", "").split(",");
        for (int i = 0; i < arrStr.length; i++) {
            if (arrStr[i].equals(Strs.of(id))) {
                isSelect = true;
                break;
            }
        }
        return isSelect;
    }

    /**
     * 添加用户玩过的彩种
     *
     * @param lotteryCode
     */
    public void addUserPlayedLottery(String category, String lotteryCode) {
        //如果之前已包含, 则不添加
        String str = spPersistent.getString(getUIN() + "_" + KEY_PLAYED_LOTTERYS, "[]");
        if (str.contains(String.valueOf(category + "#" + lotteryCode))) {
            return;
        }

        Gson gson = new Gson();
        ArrayList<String> listIds = gson.fromJson(str, new TypeToken<ArrayList<String>>() {
        }.getType());
        listIds.add(category + "#" + lotteryCode);
        spPersistent.setString(getUIN() + "_" + KEY_PLAYED_LOTTERYS, gson.toJson(listIds, new TypeToken<ArrayList<Integer>>() {
        }.getType()));
    }

    public void removeUserPlayedLottery(String category, String lotteryCode) {
        String str = spPersistent.getString(getUIN() + "_" + KEY_PLAYED_LOTTERYS, "[]");
        Gson gson = new Gson();
        ArrayList<String> listIds = gson.fromJson(str, new TypeToken<ArrayList<String>>() {
        }.getType());

        String val = null;
        for (String id : listIds) {
            if (Strs.isEqual(id, category + "#" + lotteryCode)) {
                val = id;
                break;
            }
        }

        if (val == null) {
            return;
        }

        listIds.remove(val);
        spPersistent.setString(getUIN() + "_" + KEY_PLAYED_LOTTERYS, gson.toJson(listIds, new TypeToken<ArrayList<Integer>>() {
        }.getType()));
    }

    /**
     * 用户玩过的所有彩种
     *
     * @return
     */
    public ArrayList<String> getUserPlayedLotterys() {
        String str = spPersistent.getString(getUIN() + "_" + KEY_PLAYED_LOTTERYS, "[]");
        ArrayList<String> listIds = new Gson().fromJson(str, new TypeToken<ArrayList<String>>() {
        }.getType());
        return listIds;
    }

    /**
     * 获取用户选择彩种记录
     */
    public Pair<String, String> getUserLastSelectedLottery() {
        String str = spPersistent.getString(getUIN() + "_" + KEY_LAST_LOTTERY, "");
        String[] val = str.split("#");

        if (val.length == 2)
            return new Pair<>(val[0], val[1]);
        else
            return null;
    }

    /**
     * 记录用户选择彩种
     */
    public void setUserLastSelectedLottery(String category, String code) {
        if (Strs.isEmpty(category) || Strs.isEmpty(code))
            return;

        spPersistent.setString(getUIN() + "_" + KEY_LAST_LOTTERY, category + "#" + code);
    }


    private void initBankInfo() {
        mapBank = new LinkedHashMap<String, String>();
        mapBankFace = new LinkedHashMap<String, String>();
        mapBankBg = new LinkedHashMap<>();

        mapBank.put("中国邮政储蓄银行", "PSBC");
        mapBankFace.put("中国邮政储蓄银行", "PSBC@2x.png");
        mapBankBg.put("中国邮政储蓄银行", "PSBC_BG.png");

        mapBank.put("中信银行", "CITIC");
        mapBankFace.put("中信银行", "CITIC@2x.png");
        mapBankBg.put("中信银行", "CITIC_BG.png");

        mapBank.put("兴业银行", "CIB");
        mapBankFace.put("兴业银行", "CIB@2x.png");
        mapBankBg.put("兴业银行", "CIB_BG.png");

        mapBank.put("平安银行", "PINGAN");
        mapBankFace.put("平安银行", "PINGAN@2x.png");
        mapBankBg.put("平安银行", "PINGAN_BG.png");

        mapBank.put("民生银行", "CMBC");
        mapBankFace.put("民生银行", "CMBC@2x.png");
        mapBankBg.put("民生银行", "CMBC_BG.png");

        mapBank.put("浦发银行", "SPDB");
        mapBankFace.put("浦发银行", "SPDB@2x.png");
        mapBankBg.put("浦发银行", "SPDB_BG.png");

        mapBank.put("光大银行", "CEB");
        mapBankFace.put("光大银行", "CEB@2x.png");
        mapBankBg.put("光大银行", "CEB_BG.png");

        mapBank.put("广发银行", "GDB");
        mapBankFace.put("广发银行", "CGB@2x.png");
        mapBankBg.put("广发银行", "CGB_BG.png");

        //老命字
        mapBank.put("广州发展银行", "GDB");
        mapBankFace.put("广州发展银行", "CGB@2x.png");
        mapBankBg.put("广州发展银行", "ic_bank_bg.png");

        mapBank.put("交通银行", "COMM");
        mapBankFace.put("交通银行", "COMM@2x.png");
        mapBankBg.put("交通银行", "COMM_BG.png");


        mapBank.put("中国银行", "BOC");
        mapBankFace.put("中国银行", "BOC@2x.png");
        mapBankBg.put("中国银行", "BOC_BG.png");

        mapBank.put("建设银行", "CCB");
        mapBankFace.put("建设银行", "CCB@2x.png");
        mapBankBg.put("建设银行", "CCB_BG.png");

        mapBank.put("农业银行", "ABC");
        mapBankFace.put("农业银行", "ABC@2x.png");
        mapBankBg.put("农业银行", "ABC_BG.png");

        mapBank.put("工商银行", "ICBC");
        mapBankFace.put("工商银行", "ICBC@2x.png");
        mapBankBg.put("工商银行", "ICBC_BG.png");

        mapBank.put("招商银行", "CMB");
        mapBankFace.put("招商银行", "CMB@2x.png");
        mapBankBg.put("招商银行", "CMB_BG.png");

        mapBank.put("北京银行", "BOB");
        mapBankFace.put("北京银行", "BOB@2x.png");
        mapBankBg.put("北京银行", "BOB_BG.png");

        mapBank.put("华夏银行", "HXB");
        mapBankFace.put("华夏银行", "HXB@2x.png");
        mapBankBg.put("华夏银行", "HXB_BG.png");

        mapBank.put("宁波银行", "NBBANK");
        mapBankFace.put("宁波银行", "NBBANK@2x.png");
        mapBankBg.put("宁波银行", "NBBANK_BG.png");

        mapBank.put("上海银行", "BOS");
        mapBankFace.put("上海银行", "BOS@2x.png");
        mapBankBg.put("上海银行", "BOS_BG.png");

//        mapBank.put("民生银行", "CMSB");
//        mapBankFace.put("民生银行", "CMSB@2x.png");
//        mapBankBg.put("民生银行", "CMSB_BG.png");

        mapBank.put("平安银行", "SZD");
        mapBankFace.put("平安银行", "SZD@2x.png");
        mapBankBg.put("平安银行", "SZD_BG.png");

        mapBank.put("杭州银行", "HZCB");
        mapBankFace.put("杭州银行", "HZCB@2x.png");
        mapBankBg.put("杭州银行", "HZCB_BG.png");

        mapBank.put("交通银行", "BOCM");
        mapBankFace.put("交通银行", "BOCM@2x.png");
        mapBankBg.put("交通银行", "BOCM_BG.png");

        mapBank = Collections.unmodifiableMap(mapBank);
        mapBankFace = Collections.unmodifiableMap(mapBankFace);
    }

    protected void initLotteryInfo() {

        String json = Files.readAssets("lottery_all_infos/allLotteryInfos.json");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        listLotteryInfoIndex = gson.fromJson(jsonObject.get("index"), new TypeToken<ArrayList<String>>() {
        }.getType());
        mapLotteryInfo = new HashMap<>();
        mapLotteryInfoIndex = new HashMap<>();
        ArrayList<LotteryInfo> listLotterys;
        for (int i = 0; i < listLotteryInfoIndex.size(); i++) {
            String key = listLotteryInfoIndex.get(i);
            listLotterys = gson.fromJson(jsonObject.get(key), new TypeToken<ArrayList<LotteryInfo>>() {
            }.getType());
            mapLotteryInfoIndex.put(key, listLotterys);
            for (int j = 0; j < listLotterys.size(); j++) {
                mapLotteryInfo.put(listLotterys.get(j).getId(), listLotterys.get(j));
                listLotterys.get(j).setKind(CtxLottery.getIns().findLotteryKind(listLotterys.get(j).getId()));
            }
            mapLotteryInfoIndex.put(key, Collections.unmodifiableList(mapLotteryInfoIndex.get(key)));
        }
        mapLotteryInfoIndex = Collections.unmodifiableMap(mapLotteryInfoIndex);
        mapLotteryInfo = Collections.unmodifiableMap(mapLotteryInfo);
        listLotteryInfoIndex = Collections.unmodifiableList(listLotteryInfoIndex);
    }

    /**
     * 更新所有的数据.
     *
     * @param netList
     */
    public void updateLotteryInfoAndKind(List<LotteryInfo> netList) {
        if (netList == null || netList.size() == 0) {
            return;
        }
        HashMap<Integer, LotteryInfo> mapNet = new HashMap<>();
        for (int i = 0; i < netList.size(); i++) {
            mapNet.put(netList.get(i).getId(), netList.get(i));
        }
        for (Map.Entry<Integer, LotteryInfo> entry : mapLotteryInfo.entrySet()) {
            LotteryInfo netInfo = mapNet.get(entry.getKey());
            if (netInfo != null && !entry.getValue().getShowName().equals(netInfo.getShowName())) {
                entry.getValue().setShowName(netInfo.getShowName());
            }
        }
        for (int i = 0; i < netList.size(); i++) {
            ILotteryKind kind = CtxLottery.getIns().findLotteryKind(netList.get(i).getId());
            if (kind != null && !kind.getShowName().equals(netList.get(i).getShowName())) {
                kind.setShowName(netList.get(i).getShowName());
            }
        }
        sendBroadcaset(Global.app, EVENT_USER_UPDATE_LOTTERY, null);
    }

    private void initLotteryPlayCategory() {
        mapLotteryPlayCategory = new HashMap<>();
        mapLotteryPlayCategoryList = new HashMap<>();

        String json = Files.readAssets("lottery_all_infos/betDetailLotteryCategory.json");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        List<String> arrKeys = gson.fromJson(jsonObject.get("index"), new TypeToken<ArrayList<String>>() {
        }.getType());

        for (int i = 0; i < arrKeys.size(); i++) {
            String key = arrKeys.get(i);
            JsonArray value = jsonObject.get(key).getAsJsonArray();
            mapLotteryPlayCategoryList.put(key, new ArrayList<LotteryPlayCategory>());
            mapLotteryPlayCategory.put(key, new HashMap<String, LotteryPlay>());
            for (int j = 0; j < value.size(); j++) {
                JsonObject playCategory = value.get(j).getAsJsonObject();
                LotteryPlayCategory subCategory = gson.fromJson(playCategory, LotteryPlayCategory.class);
                subCategory.setCategoryCode(key);
                //添加玩法数据
                for (int k = 0; k < subCategory.getData().size(); k++) {
                    LotteryPlay lotteryPlay = subCategory.getData().get(k);
                    lotteryPlay.category = key;
                    mapLotteryPlayCategory.get(key).put(lotteryPlay.getPlayId(), lotteryPlay);
                }
                //添加
                subCategory.setData(subCategory.getData());
                mapLotteryPlayCategoryList.get(key).add(subCategory);
            }
            mapLotteryPlayCategoryList.put(key, Collections.unmodifiableList(mapLotteryPlayCategoryList.get(key)));
            mapLotteryPlayCategory.put(key, Collections.unmodifiableMap(mapLotteryPlayCategory.get(key)));
        }

        //初始化特殊
        if (jsonObject.get("specialMap") != null) {
            mapLotteryPlayCategorySpecial = gson.fromJson(jsonObject.get("specialMap"), new TypeToken<HashMap<String, String>>() {
            }.getType());
            for (String key : mapLotteryPlayCategorySpecial.values()) {
                String commonKey = key.split("#")[0];
                String specialKey = key.split("#")[1];
                JsonArray value = jsonObject.get(specialKey).getAsJsonArray();
                //仅使用这两个集合使用特殊key
                mapLotteryPlayCategoryList.put(specialKey, new ArrayList<LotteryPlayCategory>());
                mapLotteryPlayCategory.put(specialKey, new HashMap<String, LotteryPlay>());
                //其他使用通用
                for (int j = 0; j < value.size(); j++) {
                    JsonObject playCategory = value.get(j).getAsJsonObject();
                    LotteryPlayCategory subCategory = gson.fromJson(playCategory, LotteryPlayCategory.class);
                    subCategory.setCategoryCode(commonKey);
                    //添加玩法数据
                    for (int k = 0; k < subCategory.getData().size(); k++) {
                        LotteryPlay lotteryPlay = subCategory.getData().get(k);
                        lotteryPlay.category = commonKey;
                        mapLotteryPlayCategory.get(specialKey).put(lotteryPlay.getPlayId(), lotteryPlay);
                    }
                    //添加
                    mapLotteryPlayCategoryList.get(specialKey).add(subCategory);
                }
                mapLotteryPlayCategoryList.put(key, Collections.unmodifiableList(mapLotteryPlayCategoryList.get(key)));
                mapLotteryPlayCategory.put(key, Collections.unmodifiableMap(mapLotteryPlayCategory.get(key)));
            }
        }

        mapLotteryPlayCategoryList = Collections.unmodifiableMap(mapLotteryPlayCategoryList);
        mapLotteryPlayCategory = Collections.unmodifiableMap(mapLotteryPlayCategory);
        mapLotteryPlayCategorySpecial = Collections.unmodifiableMap(mapLotteryPlayCategorySpecial);
    }

    private void initLotteryPlayUIConfig() {
        mapLotteryPlayUIConfigWithCategory = new HashMap<>();
        mapLotteryPlayUIConfigWithId = new HashMap<>();
        String json = Files.readAssets("lottery_all_infos/betDetaillotteryUIInfo.json");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        for (String categoryKey : jsonObject.keySet()) {
            mapLotteryPlayUIConfigWithCategory.put(categoryKey, new HashMap<String, LotteryPlayUIConfig>());
            JsonObject value = jsonObject.get(categoryKey).getAsJsonObject();
            for (String lotteryKey : value.keySet()) {
                LotteryPlayUIConfig config = gson.fromJson(value.get(lotteryKey), LotteryPlayUIConfig.class);
                mapLotteryPlayUIConfigWithCategory.get(categoryKey).put(lotteryKey, config);
                mapLotteryPlayUIConfigWithId.put(config.getLotteryID(), config);
            }
            mapLotteryPlayUIConfigWithCategory.put(categoryKey, Collections.unmodifiableMap(mapLotteryPlayUIConfigWithCategory.get(categoryKey)));
        }

        mapLotteryPlayUIConfigWithCategory = Collections.unmodifiableMap(mapLotteryPlayUIConfigWithCategory);
        mapLotteryPlayUIConfigWithId = Collections.unmodifiableMap(mapLotteryPlayUIConfigWithId);
    }

    private void initLotteryPlayUIConfigJD() {
        mapLotteryPlayJD = new HashMap<>();
        String json = Files.readAssets("lottery_all_infos/betJDLotteryUI.json");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        for (String categoryKey : jsonObject.keySet()) {
            String playJson = jsonObject.get(categoryKey).getAsJsonArray().toString();
            ArrayList<LotteryPlayJD> config = gson.fromJson(playJson, new TypeToken<ArrayList<LotteryPlayJD>>() {
            }.getType());
            mapLotteryPlayJD.put(categoryKey, config);
        }

        mapLotteryPlayJD = Collections.unmodifiableMap(mapLotteryPlayJD);
    }

    private void initLotteryPlayUIBallColor() {
        listLHCBallColor = new ArrayList<>();
        String json = Files.readAssets("lottery_all_infos/betLHCDetailLotteryCategory.json");
        if (Strs.isEmpty(json)) {
            return;
        }
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        JsonElement lhc = jsonObject.get("ballColor");
        ArrayList<Integer> list = gson.fromJson(lhc, new TypeToken<ArrayList<Integer>>() {
        }.getType());
        listLHCBallColor = Collections.unmodifiableList(list);
    }

    private void initLotteryPlayUIConfigLHC() {
        mapLotteryPlayLHC = new HashMap<>();
        String json = Files.readAssets("lottery_all_infos/betLHCLotteryUI.json");
        if (Strs.isEmpty(json)) {
            return;
        }
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        for (String categoryKey : jsonObject.keySet()) {
            String playJson = jsonObject.get(categoryKey).getAsJsonObject().toString();
            LotteryPlayLHCUI config = gson.fromJson(playJson, new TypeToken<LotteryPlayLHCUI>() {
            }.getType());
            mapLotteryPlayLHC.put(categoryKey, config);
        }
        mapLotteryPlayLHC = Collections.unmodifiableMap(mapLotteryPlayLHC);
    }

    private void initLotteryPlayCategoryLHC() {
        String json = Files.readAssets("lottery_all_infos/betLHCDetailLotteryCategory.json");
        if (Strs.isEmpty(json)) {
            return;
        }
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        JsonElement lhc = jsonObject.get("lhc");
        ArrayList<LotteryPlayLHCCategory> list = gson.fromJson(lhc, new TypeToken<ArrayList<LotteryPlayLHCCategory>>() {
        }.getType());
        listLotteryPlayLHCCategory = Collections.unmodifiableList(list);
    }

    public Map<Integer, String> getMapZodiacName() {
        return mapZodiacName;
    }

    public List<Integer> getListLHCBallColor() {
        return listLHCBallColor;
    }

    private void initLHCMapZodicName() {
        String json = Files.readAssets("lottery_all_infos/betLHCDetailLotteryCategory.json");
        if (Strs.isEmpty(json)) {
            return;
        }
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        JsonElement lhc = jsonObject.get("ballsx");
        Map<Integer, String> map = gson.fromJson(lhc, new TypeToken<HashMap<Integer, String>>() {
        }.getType());
        mapZodiacName = Collections.unmodifiableMap(map);
    }

    public List<LotteryPlayLHCCategory> getListLotteryPlayLHCCategory() {
        return listLotteryPlayLHCCategory;
    }

    public List<String> getLotteryInfoIndexList() {
        return listLotteryInfoIndex;
    }

    public Map<String, List<LotteryInfo>> getLotteryInfoIndexMap() {
        return mapLotteryInfoIndex;
    }

    /**
     * 获取lotteryInfo数据, key为大写lottery code
     *
     * @return
     */
    public Map<Integer, LotteryInfo> getLotteryInfoMap() {
        return mapLotteryInfo;
    }

    public Map<String, List<LotteryPlayCategory>> getLotteryPlayCategoryListMap() {
        return mapLotteryPlayCategoryList;
    }

    public Map<String, Map<String, LotteryPlay>> getLotteryPlayCategoryMap() {
        return mapLotteryPlayCategory;
    }

    public Map<String, Map<String, LotteryPlayUIConfig>> getLotteryPlayUIConfigWithCategoryMap() {
        return mapLotteryPlayUIConfigWithCategory;
    }

    public Map<Integer, LotteryPlayUIConfig> getLotteryPlayUIConfigWithIdMap() {
        return mapLotteryPlayUIConfigWithId;
    }

    public Map<String, ArrayList<LotteryPlayJD>> getLotteryPlayJDMap() {
        return mapLotteryPlayJD;
    }

    public Map<String, LotteryPlayLHCUI> getLotteryPlayLHCMap() {
        return mapLotteryPlayLHC;
    }

    public LotteryPlayJD getLotteryPlayJD(String category, String playShowName) {
        ArrayList<LotteryPlayJD> listPlays = mapLotteryPlayJD.get(category);
        if (listPlays == null) {
            return null;
        }
        LotteryPlayJD playJD = null;
        for (int i = 0; i < listPlays.size(); i++) {
            if (listPlays.get(i).getShowName().equals(playShowName)) {
                playJD = listPlays.get(i);
                break;
            }
        }
        return playJD;
    }

    public LotteryPlayLHCCategory.DataBean getLotteryPlayLHCCategoryDataBean(String category, String playName) {
        LotteryPlayLHCCategory cat = getLotteryPlayLHCCategory(category);

        if (cat == null)
            return null;

        LotteryPlayLHCCategory.DataBean bean = null;
        for (LotteryPlayLHCCategory.DataBean data : cat.getData()) {
            if (Strs.isEqual(data.getName(), playName)) {
                bean = data;
            }
        }
        return bean;
    }

    public LotteryPlayLHCCategory.DataBean getLotteryPlayLHCCategoryDataBeanByPlayCode(String category, String playCode) {
        LotteryPlayLHCCategory cat = getLotteryPlayLHCCategory(category);

        if (cat == null)
            return null;

        LotteryPlayLHCCategory.DataBean bean = null;
        for (LotteryPlayLHCCategory.DataBean data : cat.getData()) {
            Log.e("UserManager", data.getLotteryCode());
            if (Strs.isEqual(data.getLotteryCode(), playCode)) {
                bean = data;
            }
        }
        return bean;
    }

    public LotteryPlayLHCCategory getLotteryPlayLHCCategory(String category) {
        LotteryPlayLHCCategory categoryData = null;

        for (LotteryPlayLHCCategory c : listLotteryPlayLHCCategory) {
            if (Strs.isEqual(c.getTitleName(), category)) {
                return c;
            }
        }
        return null;
    }

    public LotteryPlayLHCUI getLotteryPlayLHCUI(String category, String playName) {
        LotteryPlayLHCCategory.DataBean bean = getLotteryPlayLHCCategoryDataBean(category, playName);

        if (bean == null)
            return null;

        LotteryPlayLHCUI playLhc = mapLotteryPlayLHC.get(bean.getLotteryCode());
        return playLhc;
    }

    public LotteryPlayLHCUI getLotteryPlayLHCUIByCode(String category, String playCode) {
        LotteryPlayLHCUI playLhc = mapLotteryPlayLHC.get(playCode);
        return playLhc;
    }

    public boolean isRememberedPassword() {
        return spUser.getBoolean("isRememberedPassword", false);
    }

    public void setRememberedPassword(boolean RememberedPassword) {
        spUser.put("isRememberedPassword", RememberedPassword);
    }

    public ArrayList<String> getUrls() {
        String urls = spPersistent.getString("urls", "");
        return Strs.split(urls, ",");
    }

    public void setUrls(String urls) {
        spPersistent.put("urls", urls);
    }

    public ArrayList<String> getApUrls() {
        String apUrls = spPersistent.getString("apUrls", "");
        return Strs.split(apUrls, ",");
    }

    public void setApUrls(String apUrls) {
        spPersistent.put("apUrls", apUrls);
    }

    public String getMainUrl() {
        return spPersistent.getString("main_url", "");
    }

    public void setMainUrl(String url) {
        spPersistent.put("main_url", url);
    }

    public String getSoketIOUrl() {
        return spPersistent.getString("sockio_url", "");
    }

    public void setSoketIOUrl(String url) {
        spPersistent.put("sockio_url", url);
    }

    public boolean isSoketIOopen() {
        return spPersistent.getBoolean("isOpenChat", false);
    }

    public void setSoketIOopen(boolean b) {
        spPersistent.put("isOpenChat", b);
    }

    public String getHostUserPrefix() {
        return spPersistent.getString("hostUserPrefix", "");
    }

    public void setHostUserPrefix(String hostUserPrefix) {
        spPersistent.put("hostUserPrefix", hostUserPrefix);
    }

    public String getCustomServiceLink() {
        return spPersistent.getString("customServiceLink", "");
    }

    public void setCustomServiceLink(String customServiceLink) {
        spPersistent.put("customServiceLink", customServiceLink);
    }

    public Map<String, String> getMapBank() {
        return mapBank;
    }

    public ArrayList<String> getListBank() {
        ArrayList<String> list = new ArrayList<String>();
        Iterator<Map.Entry<String, String>> it = mapBank.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entity = (Map.Entry<String, String>) it.next();
            list.add(entity.getKey());
        }
        return list;
    }

    public String getBankCode(String bankName) {
        String bankCode = "";
        Iterator<Map.Entry<String, String>> it = mapBank.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entity = (Map.Entry<String, String>) it.next();
            if (entity.getKey().contains(bankName)) {
                bankCode = entity.getValue();
                break;
            }
        }
        return bankCode;
    }

    public String findBankName(String bankCode) {
        Iterator<Map.Entry<String, String>> it = mapBank.entrySet().iterator();
        String bankName = "";
        while (it.hasNext()) {
            Map.Entry<String, String> entity = (Map.Entry<String, String>) it.next();
            if (entity.getValue().equalsIgnoreCase(bankCode)) {
                bankName = entity.getKey();
                break;
            }
        }
        return bankName;
    }


    public String getBankBG(String bankName) {
        String bankFace = null;
        Iterator<Map.Entry<String, String>> it = mapBank.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entity = (Map.Entry<String, String>) it.next();
            if (bankName.contains(entity.getKey())) {
                bankFace = mapBankBg.get(entity.getKey());
                break;
            }
        }
        if (Strs.isNotEmpty(bankFace)) {
            return "file:///android_asset/BankImage/" + bankFace;
        } else {
            return "file:///android_asset/BankImage/CEB_BG.png";
        }
    }

    public String getBankFace(String bankName) {
        String bankFace = null;
        Iterator<Map.Entry<String, String>> it = mapBank.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entity = (Map.Entry<String, String>) it.next();
            if (bankName.contains(entity.getKey())) {
                bankFace = mapBankFace.get(entity.getKey());
                break;
            }
        }
        if (Strs.isNotEmpty(bankFace)) {
            return "file:///android_asset/BankImage/" + bankFace;
        } else {
            return "file:///android_asset/BankImage/CEB@2x.png";
        }
    }


    public String getBankBgTop(String bankName) {
        String bankFace = null;
        Iterator<Map.Entry<String, String>> it = mapBank.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entity = (Map.Entry<String, String>) it.next();
            if (bankName.contains(entity.getKey())) {
                bankFace = mapBankFace.get(entity.getKey());
                break;
            }
        }
        if (Strs.isNotEmpty(bankFace)) {
            return "file:///android_asset/BankImage/" + bankFace.replace("@2x.png", "") + "_BG_TOP@2x.png";
        } else {
            return "file:///android_asset/BankImage/CEB_BG_TOP@2x.png";
        }
    }


    public String getBankBgBottom(String bankName) {
        String bankFace = null;
        Iterator<Map.Entry<String, String>> it = mapBank.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entity = (Map.Entry<String, String>) it.next();
            if (bankName.contains(entity.getKey())) {
                bankFace = mapBankFace.get(entity.getKey());
                break;
            }
        }
        if (Strs.isNotEmpty(bankFace)) {
            return "file:///android_asset/BankImage/" + bankFace.replace("@2x.png", "") + "_BG_BOTTOM@2x.png";
        } else {
            return "file:///android_asset/BankImage/CEB_BG_BOTTOM@2x.png";
        }
    }


    public Map<String, String> getMapLotteryPlayCategorySpecial() {
        return mapLotteryPlayCategorySpecial;
    }

    public static String encryptMoneyPasswd(String cn, String pwd) {
        try {
            //return EncryptUtil.encryptPassword(cn, pwd);
            return MD5.md5(pwd).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
//*************************************全局信息 end*****************************************


    //*************************************用户信息 start*****************************************
    public boolean isLogined() {
        return Strs.isNotEmpty(getAccount());
    }

    public boolean isNotLogined() {
        return Strs.isEmpty(getAccount());
    }

    private void setUserData(UserData data) {
        setLogin(data.isLogin());
        setMsgCount(data.getMsgCount());

        setMainUserName(data.getMain().getUsername());
        setUserName(data.getMain().getUsername());

        setMainNickname(data.getMain().getUsername());
        setMainType(data.getMain().getType());
        setMainZdType(data.getMain().getZdType());
        setMainRegistTime(data.getMain().getRegistTime());
        setMainLoginTime(data.getMain().getLockTime());
        setLoginTime(data.getMain().getLoginTime());
        setMainLockTime(data.getMain().getLockTime());
        setMainStatus(data.getMain().getStatus());
        setMainOnlineStatus(data.getMain().getOnlineStatus());
        setMainBindStatus(data.getMain().getBindStatus());
        setMainTypeCode(data.getMain().getTypeCode());
        setMainAllowTransfer(data.getMain().getAllowTransfer());
        setMainGoogleBind(data.getMain().isGoogleBind());
        setMainVipCode(data.getMain().getVipCode());
        setMainUserLevel(data.getMain().getUserLevel());
        setMainUserAgentType(data.getMain().getUserAgentType());

        setLotteryAvailableBalance(data.getLottery().getAvailableBalance());
        setLotteryBlockedBalance(data.getLottery().getBlockedBalance());
        setLotteryCode(data.getLottery().getCode());
        setLotteryPoint(data.getLottery().getPoint());
        setLotteryCodeType(data.getLottery().getCodeType());
        //setLotteryExtraPoint(data.getLottery().extraPoint());
        setLotteryPlayStatus(data.getLottery().getPlayStatus());
        setLotteryAllowEqualCode(data.getLottery().isAllowEqualCode());
        setLotteryIsDividendAccount(data.getLottery().isDividendAccount());

//        这条信息从getPersonSettingInfo
//        setInfoGender(""+data.getInfo().getGender());
        /*setInfoBirthday(data.getInfo().getBirthday());*/
//        setInfoCellphone(data.getInfo().getCellphone());
//        setInfoQq(data.getInfo().getQq());
//        setInfoEmail(data.getInfo().getEmail());
        setInfoAvatar(data.getInfo().getAvatar());
        setInfoNavigate(data.getInfo().getNavigate());
        setWcType(data.getWcType());

    }

    private void setMainUserAgentType(double userAgentType) {
        spUser.setDouble("userAgentType", userAgentType);
    }

    public double getMainUserAgentType() {
        return spUser.getDouble("userAgentType");
    }

    public boolean isAgent() {
        return getMainType() >= 1;
    }

    public void setNeedCapchaCode(boolean needCapchaCode) {
        spUser.setBoolean("needCapchaCode", needCapchaCode);
    }

    public double getLotteryBlockedBalance() {
        return spUser.getDouble("lottery.blockedBalanceD");
    }

    private void setLotteryBlockedBalance(double blockedBalance) {
        spUser.setDouble("lottery.blockedBalanceD", blockedBalance);
    }

    private void setLotteryCode(int code) {
        spUser.setLong("lottery.code", code);
    }

    public double getLotteryPoint() {
        return spUser.getFloat("lottery.point");
    }

    private void setLotteryPoint(double point) {
        spUser.setFloat("lottery.point", (float) point);
    }

    private void setLotteryCodeType(int codeType) {
        spUser.setLong("lottery.codeType", codeType);
    }

    private void setLotteryPlayStatus(int playStatus) {
        spUser.setLong("lottery.playStatus", playStatus);
    }

    private void setLotteryAllowEqualCode(boolean allowEqualCode) {
        spUser.setBoolean("lottery.allowEqualCode", allowEqualCode);
    }

    private void setLotteryIsDividendAccount(boolean dividendAccount) {
        spUser.setBoolean("lottery.dividendAccount", dividendAccount);
    }

    public double getLotteryAvailableBalance() {
        return spUser.getDouble("lottery.availableBalanceD");
    }

    public void setLotteryAvailableBalance(double availableBalance) {
        spUser.setDouble("lottery.availableBalanceD", availableBalance);
    }

    private void setInfoGender(String gender) {
        spUser.setString("info.gender", gender);
    }

    public String getGender() {
        return spUser.getString("info.gender", "");
    }

    private void setInfoBirthday(String birthday) {
        spUser.setString("info.birthday", birthday);
    }

    public String getBirthday() {
        return spUser.getString("info.birthday", "");
    }

    private void setWeChatId(String birthday) {
        spUser.setString("info.wechatId", birthday);
    }

    public String getWeChatId() {
        return spUser.getString("info.wechatId", "");
    }

    public String getCellphone() {
        return spUser.getString("info.cellphone", "");
    }

    public void setEmail(String birthday) {
        spUser.setString("info.email", birthday);
    }

    public String getEmail() {
        return spUser.getString("info.email", "");
    }

    public void setWithDrawName(String birthday) {
        spUser.setString("info.withdrawname", birthday);
    }

    public String getWithDrawName() {
        return spUser.getString("info.withdrawname", "");
    }

    public void setWithDrawPwd(String birthday) {
        spUser.setString("info.withdrawpwd", birthday);
    }

    public String getWithDrawPwd() {
        return spUser.getString("info.withdrawpwd", "");
    }


    private void setWcType(int wcType) {
        spUser.setLong("wcType", wcType);
    }

    public int getWcType() {
        return spUser.getInt("wcType", 0);
    }

    public void setInfoCellphone(String cellphone) {
        spUser.setString("info.cellphone", cellphone);
    }

    private void setInfoQq(String qq) {
        spUser.setString("info.qq", qq);
    }

    public boolean getRechargeShowGuideIndex() {
        return spUser.getBoolean("show_guide_index", true);
    }

    public void setRechargeSHowGuideIndex(boolean isShow) {
        spUser.setBoolean("show_guide_index", isShow);
    }


    public boolean getRechargeShowGuideInSide() {
        return spUser.getBoolean("show_guide_inside", true);
    }

    public void setRechargeSHowGuideInSide(boolean isShow) {
        spUser.setBoolean("show_guide_inside", isShow);
    }

    public boolean getDepositQrStage1Complete() {
        return spUser.getBoolean("deposit_guide_qr_complete_1", false);
    }

    public void setDepositQrStage1Complete(boolean isShow) {
        spUser.setBoolean("deposit_guide_qr_complete_1", isShow);
    }

    public boolean getDepositQrStage2Complete() {
        return spUser.getBoolean("deposit_guide_qr_complete_2", false);
    }

    public void setDepositQrStage2Complete(boolean isShow) {
        spUser.setBoolean("deposit_guide_qr_complete_2", isShow);
    }

    public boolean getDepositGuideComplete() {
        return spUser.getBoolean("deposit_guide_complete", false);
    }

    public void setDepositGuideComplete(boolean isShow) {
        spUser.setBoolean("deposit_guide_complete", isShow);
    }

    public boolean getTransferGuideComplete() {
        return spUser.getBoolean("transfer_guide_complete", false);
    }

    public void setTransferGuideComplete(boolean isShow) {
        spUser.setBoolean("transfer_guide_complete", isShow);
    }

    public boolean getStageGuideComplete() {
        return spUser.getBoolean("transfer_input_guide_complete", false);
    }

    public void setStageGuideComplete(boolean isShow) {
        spUser.setBoolean("transfer_input_guide_complete", isShow);
    }

    public String getQQ() {
        return spUser.getString("info.qq", "");
    }

    private void setInfoEmail(String email) {
        spUser.setString("info.email", email);
    }

    private void setInfoAvatar(String avatar) {
        spUser.setString("info.avatar", avatar);
    }

    private void setInfoNavigate(int navigate) {
        spUser.setLong("info.navigate", navigate);
    }

    private void setMainVipCode(String vipCode) {
        spUser.setString("main.vipCode", vipCode);
    }

    private void setMainUserLevel(int userLevel) {
        spUser.setLong("main.userLevel", userLevel);
    }

    private void setMainGoogleBind(boolean googleBind) {
        spUser.setBoolean("main.googleBind", googleBind);
    }

    private void setMainAllowTransfer(int allowTransfer) {
        spUser.setLong("main.allowTransfer", allowTransfer);
    }

    private void setMainTypeCode(String typeCode) {
        spUser.setString("main.typeCode", typeCode);
    }

    private void setMainBindStatus(int bindStatus) {
        spUser.setLong("main.bindStatus", bindStatus);
    }

    private void setMainOnlineStatus(int onlineStatus) {
        spUser.setLong("main.onlineStatus", onlineStatus);
    }

    private void setMainStatus(int status) {
        spUser.setLong("main.status", status);
    }

    private void setMainLockTime(int lockTime) {
        spUser.setLong("main.lockTime", lockTime);
    }

    private void setMainLoginTime(int lockTime) {
        spUser.setLong("main.lockTime", lockTime);
    }

    private void setLoginTime(long loginTime) {
        spUser.setLong("main.loginTime", loginTime);
    }

    public long getLoginTime() {
        return spUser.getLong("main.loginTime", System.currentTimeMillis());
    }

    private void setMainRegistTime(long registTime) {
        spUser.setLong("main.registTime", registTime);
    }

    public long getMainRegisterTime() {
        return spUser.getLong("main.registTime", System.currentTimeMillis());
    }

    private void setMainZdType(int type) {
        spUser.setLong("main.zdType", type);
    }

    public long getMainZdType() {
        return spUser.getLong("main.zdType", 0);
    }

    private void setMainType(int type) {
        spUser.setLong("main.type", type);
    }

    public long getMainType() {
        return spUser.getLong("main.type", 0);
    }

    public long getMainUserLevel() {
        return spUser.getLong("main.userLevel", 0);
    }

    private void setMainNickname(String username) {
        spUser.setString("main.username", username);
    }

    public String getMainUserName() {
        return spUser.getString("main.username", "");
    }

    private void setMainUserName(String username) {
        spUser.setString("main.username", username);
    }

    private void setMsgCount(int msgCount) {
        spUser.setLong("msgCount", msgCount);
    }

    public int getMsgCount() {
        return (int) spUser.getLong("msgCount", 0l);
    }

    public void setLogin(boolean login) {
        spUser.setBoolean("isLogin", login);
    }

    public boolean getLogin() {
        return spUser.getBoolean("isLogin", false);
    }

    public void setUserName(String userName) {
        spUser.setString("userName", userName);
    }

    public void setInvateCode(String invateCode) {
        spUser.setString("invateCode", invateCode);
    }

    public String getInvateCode() {
        return spUser.getString("invateCode", "");
    }

    public void setBroadCount(int count) {
        spPersistent.setInt("broad_count", count);
    }

    public long getBroadCount() {
        return spPersistent.getInt("broad_count", 0);
    }

    public void setGameAward(int count) {
        spPersistent.setInt("game_rate", count);
    }

    public int getGameAward() {
        return spPersistent.getInt("game_rate", -1);
    }

    public void setBounsProgress(int progress) {
        spUser.setInt("bouns_progress", progress);
    }

    public int getBounsProgress() {
        return spUser.getInt("bouns_progress", -1);
    }

    public void setHintTimes(int times) {
        spUser.setInt("money_times", times);
    }

    public int getHintTimes() {
        return spUser.getInt("money_times", 1);
    }

    public void setMoneyMode(int mode) {
        spUser.setInt("money_mode", mode);
    }

    public int getMoneyMode() {
        return spUser.getInt("money_mode", 3);
    }

    public int getPposition(String lotteryType) {
        return spUser.getInt(lotteryType, 0);
    }

    public void setPposition(String lotteryType, int pPosition) {
        spUser.put(lotteryType, pPosition);
    }

    public int getItemPosition(String lotteryType) {
        return spUser.getInt(lotteryType + "item", 0);
    }

    public void setItemPosition(String lotteryType, int pPosition) {
        spUser.put(lotteryType + "item", pPosition);
    }

    /**
     * @return 单单日结
     */
    public boolean showDanDanClear() {
        switch (Config.custom_flag) {
            case BaseConfig.FLAG_WANSHANG:
                return UserManager.getIns().hasDandanRgz() == 1;
            case BaseConfig.FLAG_DREAM_ONE:
            case BaseConfig.FLAG_DREAM_TWO:
            case BaseConfig.FLAG_LEYOU:
                return true;
            case BaseConfig.FLAG_HETIANXIA:
                if (getMainUserLevel() == 3)
                    return true;
                else if (getMainUserLevel() < 3)
                    return false;
                return UserManager.getIns().hasDandanRgz() == 1;
            case BaseConfig.FLAG_ZHONGXIN:
                if (getMainUserLevel() == 3
                        || getMainUserLevel() == 2)
                    return true;
                else if (getMainUserLevel() == 1)
                    return false;
                return UserManager.getIns().hasDandanRgz() == 1;
            default:
                return false;
        }

    }

    /**
     * @return true 为显示
     */
    public boolean showDanDanGZ() {
        return spPersistent.getInt("danDanGZ", 0) == 1;
    }

    public boolean showFeeAmount() {
        switch (Config.custom_flag) {
            case BaseConfig.FLAG_TOUCAI:
                return true;
            default:
                return false;
        }
    }

    public User getUser() {
        return ssouser;
    }

    public void setUser(User ssouser) {
        this.ssouser = ssouser;
        setUIN(ssouser.getUin());
        setCN(ssouser.getCn());
        setUserType(ssouser.getUserType());
        setSourceAppId(ssouser.getSourceAppId());
    }

    public boolean isShowBoard() {

        //青峰和龙腾显示弹窗，其他则显示跑马灯广播
        return BaseConfig.FLAG_LONGTENG.equals(Config.custom_flag)
                || BaseConfig.FLAG_QINGFENG.equals(Config.custom_flag);
    }

    /**
     * @return 万尚&龙腾 下单时显示经典玩法的dialog
     */
    public boolean isShowClassicalDialog() {
        return BaseConfig.FLAG_LONGTENG.equals(Config.custom_flag) || BaseConfig.FLAG_WANSHANG.equals(Config.custom_flag);
    }

    public boolean isShowImageTitle() {
        if (BaseConfig.FLAG_WANSHANG.equals(Config.custom_flag)
                || BaseConfig.FLAG_HETIANXIA.equals(Config.custom_flag)
                || BaseConfig.FLAG_ZHONGXIN.equals(Config.custom_flag)
                || BaseConfig.FLAG_CAIYING.equals(Config.custom_flag)
                || Config.custom_flag.equals(BaseConfig.FLAG_JINDU)
                || Config.custom_flag.equals(BaseConfig.FLAG_LETIANTANG)) {
            return true;
        }
        return false;
    }

    public boolean showTXBettAmount() {

        return (Config.custom_flag.equals(BaseConfig.FLAG_QINGFENG)
                || Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)
                || Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN) || Config.custom_flag.equals(BaseConfig.FLAG_JINDU));
    }

    public boolean showDanDanSalary() {
        if (Config.custom_flag.equals(BaseConfig.FLAG_WANSHANG)) {
            return true;
        } else if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)) {
            if (getMainUserLevel() == 3)
                return true;

            if (getMainUserLevel() < 3)
                return false;

            if (UserManager.getIns().hasDandanRgz() == 1)
                return true;
        } else if (Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN)) {
            if (getMainUserLevel() == 3
                    || getMainUserLevel() == 2)
                return true;

            if (getMainUserLevel() == 1)
                return false;

            if (UserManager.getIns().hasDandanRgz() == 1)
                return true;
        } else if (Config.custom_flag.equals(BaseConfig.FLAG_MENGXIANG)) {
            return UserManager.getIns().hasDandanRgz() == 1;
        }
        return false;
    }


    public boolean showBonusAndOrderSalary(MemberManagementBean memberManagementBean) {
        if (Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN)) {
            return UserManager.getIns().getMainUserLevel() > 2
                    && UserManager.getIns().isAgent();
        }
        return memberManagementBean.userType >= 1;
    }

    /**
     * @return 显示分红按钮
     */
    public boolean showBonus() {
        switch (Config.custom_flag) {
            case BaseConfig.FLAG_JINYANG:
            case BaseConfig.FLAG_JINDU:
                return false;
            case BaseConfig.FLAG_CAISHIJI:
                return UserManager.getIns().getMainUserLevel() < 5;
            default:
                return true;
        }
    }

    private void setSourceAppId(int sourceAppId) {
        spUser.put("sourceAppId", sourceAppId);
    }

    public String getUserTypeName() {
        return CtxLottery.formatUserType((int) spUser.getLong("userType", 0));
    }

    private void setUserType(long userType) {
        spUser.put("userType", userType);
    }

    private void setCN(String cn) {
        spUser.put("cn", cn);
    }

    public String getCN() {
        return spUser.getString("cn", "");
    }

    public void setUIN(long id) {
        spUser.put("uid", id);
    }

    public long getUIN() {
        return spUser.getLong("uid", 0);
    }

    public void setPassword(String pwd) {
        if (Strs.isEmpty(pwd)) {
            spUser.setString("pwd", "");
        } else {
            spUser.setString("pwd", encrypt(pwd));
            Log.d("UserManager", "encrypt(pwd):" + encrypt(pwd));
        }
    }

    /**
     * @param pwd 存储明文密码
     */
    public void setClearTextPassword(String pwd) {
        if (Strs.isEmpty(pwd)) {
            spPersistent.setString("clearTextPassword", "");
        } else {
            spPersistent.setString("clearTextPassword", pwd);
        }
    }

    public String getClearTextPassword() {
        return spPersistent.getString("clearTextPassword", "");
    }

    public void setIsBindGoogleAuthenticator(boolean cn) {
        spUser.put("isBindGoogleAuthenticator", cn);
    }

    public boolean getIsBindGoogleAuthenticator() {
        return spUser.getBoolean("isBindGoogleAuthenticator", false);
    }

    public void setIsCanUpdatePwdWechat(boolean cn) {
        spUser.put("isCanUpdatePwdWechat", cn);
    }

    public boolean getIsCanUpdatePwdWechat() {
        return spUser.getBoolean("isCanUpdatePwdWechat", false);
    }

    public void setIsCnReset(boolean cn) {
        spUser.put("isCnReset", cn);
    }

    public boolean getIsCnReset() {
        return spUser.getBoolean("isCnReset", false);
    }

    public void setTrendShunxu(boolean isZhengxu) {
        spUser.put("isZhengxu", isZhengxu);
    }

    public boolean getTrendShunxu() {
        return spUser.getBoolean("isZhengxu", true);
    }

    public void setIsBindCard(boolean cn) {
        spUser.put("isBindCard", cn);
    }

    public boolean getIsBindCard() {
        return spUser.getBoolean("isBindCard", false);
    }

    public void setIsBindPhone(boolean cn) {
        spUser.put("isBindPhone", cn);
    }

    public boolean getIsBindPhone() {
        return spUser.getBoolean("isBindPhone", false);
    }

    public void setIsBindWithdrawName(boolean cn) {
        spUser.put("isBindWithdrawName", cn);
    }

    public boolean getIsBindWithdrawName() {
        return spUser.getBoolean("isBindWithdrawName", false);
    }

    public void setIsBindWithdrawPassword(boolean cn) {
        spUser.put("isBindWithdrawPassword", cn);
    }

    public boolean getIsBindWithdrawPassword() {
        return spUser.getBoolean("isBindWithdrawPassword", false);
    }

    public void setIsPhoneRegister(boolean cn) {
        spUser.put("isPhoneRegister", cn);
    }

    public boolean getIsPhoneRegister() {
        return spUser.getBoolean("isPhoneRegister", false);
    }

    public void setIsBindGender(boolean cn) {
        spUser.put("isBindGender", cn);
    }

    public boolean getIsBindGender() {
        return spUser.getBoolean("isBindGender", false);
    }

    public void setIsBindBirthday(boolean cn) {
        spUser.put("isBindBirthday", cn);
    }

    public boolean getIsBindBirthday() {
        return spUser.getBoolean("isBindBirthday", false);
    }


    public void setIsBindEmail(boolean cn) {
        spUser.put("isBindEmail", cn);
    }

    public boolean getIsBindEmail() {
        return spUser.getBoolean("isBindEmail", false);
    }

    public void setIsBindQQ(boolean cn) {
        spUser.put("isBindQQ", cn);
    }

    public boolean getIsBindQQ() {
        return spUser.getBoolean("isBindQQ", false);
    }

    public void setIsBindCellphone(boolean cn) {
        spUser.put("isBindCellphone", cn);
    }

    public boolean getIsBindCellphone() {
        return spUser.getBoolean("isBindCellphone", false);
    }

    public void setIsBindSecurity(boolean cn) {
        spUser.put("isBindSecurity", cn);
    }

    public boolean getIsBindSecurity() {
        return spUser.getBoolean("isBindSecurity", false);
    }

    public void setIsBindWeChat(boolean cn) {
        spUser.put("isBindWeChat", cn);
    }

    public boolean getIsBindWeChat() {
        return spUser.getBoolean("isBindWeChat", false);
    }

    public String getPassword() {
        return decrypt(spUser.getString("pwd", ""));
    }

    public void setAccount(String account) {
        spUser.setString("account", encrypt(account));
    }

    public void setFundPwd(String fundpwd) {
        spUser.setString("fundpwd", fundpwd);
    }

    public String getFundPwd() {
        return spUser.getString("fundpwd", "");
    }

    public void setBankCardRealName(String name) {
        spUser.setString("bankCardRealName", name);
    }

    public String getBankCardRealName() {
        return spUser.getString("bankCardRealName", "");
    }

    public void setQR(String qrString) {
        spUser.setString("qrString", qrString);
    }

    public String getQR() {
        return spUser.getString("qrString", "");
    }

    public String getAccount() {
        return decrypt(spUser.getString("account", ""));
    }


    public void setLastAccount(String lastAccount) {
        spPersistent.setString("lastAccount", lastAccount);
    }

    public String getLastAccount() {
        return spPersistent.getString("lastAccount", "");
    }


    public void setLastRememberCheck(boolean setLastRememberCheck) {
        spPersistent.setBoolean("setLastRememberCheck", setLastRememberCheck);
    }

    public boolean getLastRememberCheck() {
        return spPersistent.getBoolean("setLastRememberCheck", true);
    }

    public String getLastPassword() {
        return decrypt(spPersistent.getString("lastPassword", ""));
    }

    public String getLastPasswordForAes() {
        return spPersistent.getString("lastPassword", "");
    }

    public void setLastPassword(String pwd) {
        if (Strs.isEmpty(pwd)) {
            spPersistent.setString("lastPassword", "");
        } else {
            spPersistent.setString("lastPassword", encrypt(pwd));
        }
    }

    public void setLastPasswordForAes(String pwd) {
        if (Strs.isEmpty(pwd)) {
            spPersistent.setString("lastPassword", "");
        } else {
            spPersistent.setString("lastPassword", pwd);
        }
    }


    public boolean isDowntime() {
        return spPersistent.getBoolean("isDowntime", false);
    }

    public void setIsDowntime(boolean isDowntime) {
        spPersistent.setBoolean("isDowntime", isDowntime);
    }

    public String getWeiHutime() {
        return spPersistent.getString("weiHuiTime", "");
    }

    public void setWeiHutime(String weiHuiTime) {
        spPersistent.setString("weiHuiTime", weiHuiTime);
    }
//*************************************用户信息 end*******************************************


//*************************************用户事件 start*******************************************

    /**
     * 发送用户事件
     *
     * @param ctx
     * @param event
     * @param bundle
     */
    public void sendBroadcaset(final Context ctx, final String event, final Bundle bundle) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Intent itt = new Intent();
                itt.setAction(event);
                if (bundle != null) {
                    itt.putExtras(bundle);
                }
                //在application中使用
                LocalBroadcastManager.getInstance(ctx).sendBroadcastSync(itt);
                ctx.sendBroadcast(itt);
            }
        });
    }

    /**
     * 延时发送用户事件
     *
     * @param millis
     * @param event
     * @param bundle
     */
    public void sendBroadcasetDelay(long millis, final String event, final Bundle bundle) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent itt = new Intent();
                itt.setAction(event);
                if (bundle != null) {
                    itt.putExtras(bundle);
                }
                //在application中使用
                LocalBroadcastManager.getInstance(Global.app).sendBroadcastSync(itt);
                Global.app.sendBroadcast(itt);
            }
        }, millis);
    }

    public void clearUserInfo() {
        ssouser = null;
        userData = null;
        spUser.clear();
        DBAction.clearUserInfo();
    }


    /*
     * 用户事件广播接收器.
     */
    public static class EventReceiver extends BroadcastReceiver {
        private UserEventHandler mHandler = null;

        public EventReceiver register(Context ctx, UserEventHandler handler) {
            this.mHandler = handler;
            IntentFilter i = new IntentFilter();
            for (int j = 0; j < EVENTS.length; j++) {
                i.addAction(EVENTS[j]);
            }
            LocalBroadcastManager.getInstance(ctx).registerReceiver(this, i);
            return this;
        }

        public void unregister(Context ctx) {
            if (ctx == null) {
                return;
            }
            mHandler = null;
            LocalBroadcastManager.getInstance(ctx).unregisterReceiver(this);
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getAction();
            Bundle bundle = intent.getExtras();
            if (mHandler != null) {
                mHandler.handleEvent(event, bundle);
            }
        }
    }

    //****************事件监听接口 begin**************
    public interface UserEventHandler {
        void handleEvent(String eventName, Bundle bundle);
    }

    public interface IUserRegistCallback {
        void onBefore();

        void onUserRegisted();

        void onUserRegistFailed(String msg);

        void onAfter();
    }

    public interface IUserBindCheckCallback {
        void onBefore();

        void onUserBindChecked(UserBindStatus status);

        void onUserBindCheckFailed(String msg);

        void onAfter();
    }

    public interface IUserLoginCallback {
        void onBefore();

        void onUserLogined(boolean isDataInited);

        void onUserNeedValidateCode();

        void onUserLoginFailed(String msg);

        void onAfter();
    }

    public interface IUserDataSyncCallback {
        void onUserDataInited();

        void afterUserDataInited();

        void onUserDataInitFaild();

        void onAfter();
    }

    public interface IUserInfoSyncCallBack {
        void onSync(String msg);
    }

    public interface IUserInfoGetCallBack {
        void onCallBack(PersonInfo personInfo);
    }

    public interface IGuideCallBack {
        void onCallBack(boolean show);
    }

    public interface IUserLogoutCallback {
        void onBefore();

        void onUserLogouted();

        void onUserLogoutFailed(String msg);

        void onAfter();
    }
    //****************事件监听接口 end**************
//*************************************用户事件 end*******************************************


    //*************************************指纹识别 start*******************************************
    AlertDialog fingerPrintDialog;

    public void fingerPrint(final Activity act, final IUserFingerPrintCallback callback) {
        if (fingerPrintDialog != null) {
            if (fingerPrintDialog.isShowing()) {
                return;
            } else {
                fingerPrintDialog.dismiss();
                fingerPrintDialog = null;
            }
        }
        ThreadCollector.getIns().runOnUIThread(new Runnable() {

            public int postion;
            private Handler handler = new Handler() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 0) {
                        int i = postion % 5;
                        if (i == 0) {
                            tv[4].setBackground(null);
                            tv[i].setBackgroundColor(act.getResources().getColor(R.color.colorPrimary));
                        } else {
                            tv[i].setBackgroundColor(act.getResources().getColor(R.color.colorPrimary));
                            tv[i - 1].setBackground(null);
                        }
                        postion++;
                        handler.sendEmptyMessageDelayed(0, 100);
                    }
                }
            };
            TextView[] tv = new TextView[5];

            private void initfinget(View view) {
                postion = 0;
                tv[0] = (TextView) view.findViewById(R.id.tv_1);
                tv[1] = (TextView) view.findViewById(R.id.tv_2);
                tv[2] = (TextView) view.findViewById(R.id.tv_3);
                tv[3] = (TextView) view.findViewById(R.id.tv_4);
                tv[4] = (TextView) view.findViewById(R.id.tv_5);
                handler.sendEmptyMessageDelayed(0, 100);
            }


            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    FingerprintUtil.callFingerPrint(new FingerprintUtil.OnCallBackListenr() {


                        @Override
                        public void onSupportFailed() {
                            callback.onFail("当前设备不支持指纹");
                        }

                        @Override
                        public void onInsecurity() {
                            callback.onFail("请设置指纹解锁");
                        }

                        @Override
                        public void onEnrollFailed() {
                            callback.onFail("请到设置中设置指纹");
                        }

                        @Override
                        public void onAuthenticationStart() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(act);
                            View view = LayoutInflater.from(act).inflate(R.layout.layout_fingerprint, null);
                            initfinget(view);
                            builder.setView(view);
                            builder.setCancelable(false);
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handler.removeMessages(0);
                                    FingerprintUtil.cancel();
                                    handler = null;
                                    callback.onFail("用户已取消");
                                }
                            });
                            fingerPrintDialog = builder.create();
                            fingerPrintDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    handler = null;
                                }
                            });
                            fingerPrintDialog.show();
                        }

                        @Override
                        public void onAuthenticationError(int errMsgId, CharSequence errString) {
                            if (fingerPrintDialog != null && fingerPrintDialog.isShowing()) {
                                handler.removeMessages(0);
                                fingerPrintDialog.dismiss();
                            }
                            FingerprintUtil.cancel();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            if (fingerPrintDialog != null && fingerPrintDialog.isShowing()) {
                                handler.removeMessages(0);
                                fingerPrintDialog.dismiss();
                            }
                            FingerprintUtil.cancel();
                            callback.onFail("指纹验证失败!");
                        }

                        @Override
                        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                            Toasts.show(act, helpString.toString());
                        }

                        @Override
                        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                            if (fingerPrintDialog != null && fingerPrintDialog.isShowing()) {
                                handler.removeMessages(0);
                                fingerPrintDialog.dismiss();
                                callback.onSuccess();
                            }
                        }
                    });
                } else {
                    callback.onFail("当前设备系统版本过低!");
                }
            }
        });
    }

    public interface IUserFingerPrintCallback {
        void onSuccess();

        void onFail(String msg);
    }
//*************************************指纹识别 end*******************************************

    //*************************************版本控制 start******************************************
    //请求获取服务端配置信息
    public void checkOnlineConfig(final Context ctx, final IConfigUpdateCallback callback) {
        HttpAction.getUpdateInfo(ctx, new Callback<VersionUpdateInfo>() {
            @Override
            public VersionUpdateInfo parseNetworkResponse(Response response, int id) throws Exception {
                if (response.code() == 200) {
                    return new Gson().fromJson(response.body().string(), VersionUpdateInfo.class);
                }
                return null;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                callback.onFail();
            }

            @Override
            public void onResponse(VersionUpdateInfo response, int id) {

                if (response != null) {
                    UserManager.getIns().setIsDowntime(response.isDowntime);
                    if (Strs.isNotEmpty(response.beforeDowntime) && Strs.isNotEmpty(response.afterDowntime)) {
                        UserManager.getIns().setWeiHutime(response.beforeDowntime + " - " + response.afterDowntime);
                    } else {
                        UserManager.getIns().setWeiHutime("");
                    }
                }

                if (Strs.isEqual(BaseConfig.FLAG_TOUCAI, BaseConfig.custom_flag)) {
                    if (response != null) {

                        UserManager.getIns().setUrls(response.urls);
                        UserManager.getIns().setMainUrl(response.api);
                        UserManager.getIns().setSoketIOUrl(response.sockio_url);

                        if (Strs.isNotEmpty(response.hostUserPrefix)) {
                            Config.HOST_USER_PREFIX = response.hostUserPrefix;
                            setHostUserPrefix(Config.HOST_USER_PREFIX);
                        }
                        if (Strs.isNotEmpty(response.kefu)) {
                            UserManager.getIns().setCustomServiceLink(response.kefu.replace("&amp;", "&"));
                        }
                        callback.onGetConfig(response);
                    } else {
                        callback.onFail();
                    }
                } else {
                    UserManager userManager = UserManager.getIns();
                    if (response != null) {
                        String apUrls = null;

                        String resUrls = response.apurls;
                        if (!TextUtils.isEmpty(BaseConfig.AES_KEY) && !Strs.isEmpty(resUrls)) {
                            String url = AESUtils.aesDecrypt(resUrls, BaseConfig.AES_KEY);
                            apUrls = Strs.isEmpty(url) ? response.api : url;

                            Log.e("UserManager", "apUrls-----11-" + apUrls);
                        } else {
                            apUrls = response.urls;

                            Log.e("UserManager", "apUrls--22-" + apUrls);
                        }

                        userManager.setApUrls(apUrls);
                        userManager.setUrls(response.urls);
                        userManager.setMainUrl(response.api);
                        userManager.setSoketIOUrl(response.sockio_url);
                        userManager.setSoketIOopen(response.isOpenChat);

                        if (Strs.isNotEmpty(response.hostUserPrefix)) {
                            Config.HOST_USER_PREFIX = response.hostUserPrefix;
                            setHostUserPrefix(Config.HOST_USER_PREFIX);
                        }

                        if (Strs.isNotEmpty(response.kefu)) {
                            userManager.setCustomServiceLink(response.kefu.replace("&amp;", "&"));
                        }

                        if (userManager.getApUrls().size() > 0) {
                            String s = userManager.getApUrls().get(0);
                            Config.HOST_PUBLISH = s;
                            ENV.curr.host = s;
                            MM.http.setHost(s);
                            UserManager.getIns().setUrlFevorite(s);
                            Log.e("UserManager", "没测试速度之前---默认为第一条线路" + s);
                        }
                        callback.onGetConfig(response);
                    } else {
                        userManager.setApUrls(BaseConfig.ForceHost);
                        callback.onFail();
                    }
                }
            }

        });
    }


    //请求获取服务端最新版本
    public void checkLatestVersion(final Context ctx, final IVersionUpdateCallback callback) {
        HttpAction.getUpdateInfo(ctx, new Callback<VersionUpdateInfo>() {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
            }

            @Override
            public VersionUpdateInfo parseNetworkResponse(Response response, int id) throws Exception {
                if (response.code() == 200) {
                    String str = response.body().string();
                    AbDebug.log(AbDebug.TAG_HTTP, "****************自动更新检查*******************\n" + str);
                    return new Gson().fromJson(str, VersionUpdateInfo.class);
                }
                return null;
            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(VersionUpdateInfo response, int id) {
                if (Strs.isEqual(BaseConfig.FLAG_TOUCAI, BaseConfig.custom_flag)) {
                    if (response != null) {
                        Log.d("UserManager", "--------------response:" + response.toString());
                        UserManager.getIns().setUrls(response.urls);
                        UserManager.getIns().setIsDowntime(response.isDowntime);
                        if (Strs.isNotEmpty(response.hostUserPrefix)) {
                            Config.HOST_USER_PREFIX = response.hostUserPrefix;
                            setHostUserPrefix(Config.HOST_USER_PREFIX);
                        }
                        validVersion(ctx, response, callback);
                    }
                } else {
                    if (response != null) {
                        UserManager userManager = UserManager.getIns();
                        String apUrls = null;
                        if (!TextUtils.isEmpty(BaseConfig.AES_KEY) && !Strs.isEmpty(response.apurls)) {
                            String url = AESUtils.aesDecrypt(response.apurls, BaseConfig.AES_KEY);
                            apUrls = Strs.isEmpty(url) ? response.api : url;
                        } else {
                            apUrls = response.urls;
                        }

                        userManager.setApUrls(apUrls);
                        userManager.setUrls(response.urls);
                        if (Strs.isNotEmpty(response.hostUserPrefix)) {
                            Config.HOST_USER_PREFIX = response.hostUserPrefix;
                            setHostUserPrefix(Config.HOST_USER_PREFIX);
                        }
                        validVersion(ctx, response, callback);
                    }
                }
            }

        });
    }

    private void validVersion(Context ctx, VersionUpdateInfo entity, IVersionUpdateCallback callback) {
        int localVersion = Integer.parseInt(AbDevice.appVersionName.replace(".", ""));
        Log.w("UserManager", "localVersion:" + localVersion);
        Log.w("UserManager", entity.toString());
        if (entity.android != null) {
            try {
                int resultVersionCode = Integer.parseInt(entity.android.version.replace(".", ""));
                //本地版本小于接口返回的版本，做更新
                if (localVersion < resultVersionCode) {
                    //是否强制更新1强制，0不强制
                    showUpdateDialog(ctx, false, entity.android.version, entity.android.note, entity.android.url, callback);
                } else {
                    if (callback != null) {
                        callback.noNeedUpdate();
                    }
                }
            } catch (Exception exception) {
                AbDebug.error(AbDebug.TAG_APP, exception.getMessage());
            }
        }
    }

    protected void showUpdateDialog(final Context ctx, final boolean isForceUpdate,
                                    final String version, String info, final String apkUrl, final IVersionUpdateCallback callback) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_update_version, null);
        TextView tv_update_msg = view.findViewById(R.id.tv_update_msg);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        final Button btnConfirm = view.findViewById(R.id.btnConfirm);
        final RadioGroup rg = view.findViewById(R.id.rg_switch);
        final TextView tv_process = view.findViewById(R.id.tv_process);
        final TextView tvNewVersion = view.findViewById(R.id.tvNewVersion);

        //重新起dialog
        updateDialogWithSwitch = Dialogs.showCustomDialog((Activity) ctx, view, false);

        if (isForceUpdate) { //强制更新禁止用户进入旧版, 只能安装新版进入使用
            updateDialogWithSwitch.setCanceledOnTouchOutside(false);
        } else { //可取消继续使用旧版
            updateDialogWithSwitch.setCanceledOnTouchOutside(true);
        }

        tvNewVersion.setText("V " + version);

        updateDialogWithSwitch.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateDialog = null;
                lastVersion = null;
                updateReceiver = null;
                ctx.unregisterReceiver(updateReceiver);
            }
        });
//        info = "";

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateDialogWithSwitch != null) {
                    callback.notTipUpdate();
                    updateDialogWithSwitch.dismiss();
                }
            }
        });

        String msg = info.replaceAll("&lt;br&gt;", "<br/>");
        tv_update_msg.setText(Html.fromHtml(msg));


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Strs.isNotEmpty(apkUrl) && !apkUrl.contains(".apk")) {
                    ActWebX5.launchOutside(ctx, apkUrl);
                } else if (Strs.isNotEmpty(apkUrl)) {
                    rg.setVisibility(View.GONE);
                    tv_process.setVisibility(View.VISIBLE);
                    updateLocal(ctx, apkUrl, version, isForceUpdate, btnConfirm, tv_process);
                }
            }
        });
    }

    private void updateLocal(final Context ctx, final String apkUrl, String version,
                             boolean isForceUpdate, final Button btnConfirm, final TextView tvProcess) {
        //即使不请求网络也设置进度, 防止下载不动不开始问题
        tvProcess.setText("下载进度:" + 0 + "%");
        //更改升级按钮为不可用
        btnConfirm.setText("重新下载");
        btnConfirm.setTextColor(Views.fromColors(R.color.gray));
        btnConfirm.setClickable(false);

        //如果发现有lastVersion说明启动过升级, 先尝试停掉下载
        if (Strs.isNotEmpty(lastVersion)) {
            AppUpdateService.stopUpdate(ctx, lastVersion, apkUrl);
        }

//        //非强更下可解散升级窗
//        if (!isForceUpdate) {
//            updateDialogWithSwitch.dismiss();
//            return;
//        }

        //使用时间戳防止版本上传错误导致一直安装错误文件问题
        lastVersion = version + "_" + System.currentTimeMillis();

        //注册监听进度
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int percent = intent.getIntExtra("progress", 0);
                tvProcess.setText("下载进度:" + percent + "%");

                //下载成功后,允许用户再次重新下载
                if (percent == 100) {
                    btnConfirm.setText("重新下载");
                    btnConfirm.setTextColor(Views.fromColors(R.color.blue_light));
                    btnConfirm.setClickable(true);
                    ctx.unregisterReceiver(updateReceiver);
                    btnConfirm.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }, 1000);
                }
            }
        };

        IntentFilter filter = new IntentFilter(AppUpdateService.EVENT_PROGRESS);
        ctx.registerReceiver(updateReceiver, filter);

        //启动下载
        ThreadCollector.getIns().postDelayOnUIThread(1000, new Runnable() {
            @Override
            public void run() {
                AppUpdateService.startUpdate(ctx, lastVersion, apkUrl);
            }
        });
    }

    /**
     * 用户手机登录
     *
     * @param phone      手机号
     * @param smsCode    短信验证码
     * @param capchaCode 图片验证吗
     * @param callback   回调
     */
    public void loginWithPhone(final String phone, final String smsCode, @Nullable final String capchaCode, final IUserLoginCallback callback) {
        //1.请求登录
        //MM.http.clearCookies();
        HttpAction.loginWithPhone(this, phone, smsCode, capchaCode, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                callback.onBefore();
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("user", User.class);
                entity.putField("needCapchaCode", Boolean.TYPE);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0) { //登录成功
                    spUser.clear();
                    setUser(getFieldObject(extra, "user", User.class));
                    setNeedCapchaCode(Maps.value(extra, "needCapchaCode", false));

                    //2. 再次请求接口, redirect给出ticket
                    if ("sso".equals(BaseConfig.HOST_USER_PREFIX)) {
                        HttpAction.loginNext(this, new AbHttpResult() {
                            @Override
                            public void setupEntity(AbHttpRespEntity entity) {
                                entity.putField("code", String.class);
                                entity.putField("userName", String.class);
                            }

                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                setUserName(getField(extra, "userName", ""));

                                //3. 获取用户数据
                                initUserData(new IUserDataSyncCallback() {
                                    @Override
                                    public void onUserDataInited() {
                                        callback.onUserLogined(true);
                                    }

                                    @Override
                                    public void afterUserDataInited() {

                                    }

                                    @Override
                                    public void onUserDataInitFaild() {
                                        callback.onUserLoginFailed("用户数据下载失败,请重试!");
                                    }

                                    @Override
                                    public void onAfter() {
                                        callback.onAfter();
                                    }
                                });
                                //getQuerySalaryBonus();
                                sendBroadcaset(Global.app, EVENT_USER_LOGINED, null);
                                callback.onUserLogined(false);
                                return true;
                            }

                            @Override
                            public boolean onError(int status, String content) {
                                Toasts.show(Global.app, "用户获取Ticket失败!");
                                callback.onUserNeedValidateCode();
                                callback.onAfter();
                                return true;
                            }
                        });
                    } else { //新版yx
                        //3. 获取用户数据
                        initUserData(new IUserDataSyncCallback() {
                            @Override
                            public void onUserDataInited() {
                                callback.onUserLogined(true);
                                sendBroadcaset(Global.app, EVENT_USER_LOGINED, null);
                            }

                            @Override
                            public void afterUserDataInited() {
                            }

                            @Override
                            public void onUserDataInitFaild() {
                                callback.onUserLoginFailed("用户数据下载失败!");
                            }

                            @Override
                            public void onAfter() {
                                callback.onAfter();
                            }
                        });
                        //getQuerySalaryBonus();
                    }

                    //同时开始绑定推送
                    if (BaseConfig.isUsePush && MM.push != null) {
                        MM.push.bindAccount(null, null, new IPushCallback() {
                            @Override
                            public void onSuccess(String account, String group, String pushDeviceId) {
                                AbDebug.log(AbDebug.TAG_PUSH, "设备token:(" + pushDeviceId + ")获取成功, 开始绑定账号");
                                if (Strs.isNotEmpty(pushDeviceId)) {
                                    HttpAction.setPushToken(UserManager.this, pushDeviceId, new AbHttpResult() {
                                        @Override
                                        public void setupEntity(AbHttpRespEntity entity) {
                                            //super.setupEntity(entity);
                                        }

                                        @Override
                                        public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                            return true;
                                        }

                                        @Override
                                        public boolean onError(int status, String content) {
                                            return true;
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailed(String var1, String var2) {
                                AbDebug.log(AbDebug.TAG_PUSH, "设备token获取失败," + var1 + " ," + var2);
                            }
                        });
                    }
                } else if (code == 36) {//需要验证码
                    callback.onUserNeedValidateCode();
                } else if (code == 37) {//验证码输入错误
                    callback.onUserLoginFailed(msg);
                    callback.onUserNeedValidateCode();
                } else if (code == 1004) {
                    callback.onUserLoginFailed("账号未注册，请先前往注册");
                } else {
                    callback.onUserLoginFailed(msg);
                    callback.onAfter();
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                callback.onUserLoginFailed(content);
                callback.onAfter();
                return true;
            }
        });
    }


    public interface IVersionUpdateCallback {
        void noNeedUpdate();

        void notTipUpdate();
    }

    public interface IConfigUpdateCallback {
        void onGetConfig(VersionUpdateInfo info);

        void onFail();
    }

//*************************************版本控制 end******************************************
}
