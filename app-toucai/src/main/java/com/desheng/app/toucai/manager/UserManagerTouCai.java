package com.desheng.app.toucai.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ab.callback.AbCallback;
import com.ab.global.AbDevice;
import com.ab.global.ENV;
import com.ab.global.Global;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Files;
import com.ab.util.Maps;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.context.LotteryType;
import com.desheng.app.toucai.context.LotteryTypeCustom;
import com.desheng.app.toucai.event.DajiangPushMode;
import com.desheng.app.toucai.global.AppUmengPushHandler;
import com.desheng.app.toucai.model.ContactsBackMsgMode;
import com.desheng.app.toucai.model.ContactsMode;
import com.desheng.app.toucai.model.LatestMsgComeRefreshMode;
import com.desheng.app.toucai.model.LotteryInfoCustom;
import com.desheng.app.toucai.model.LotteryJump;
import com.desheng.app.toucai.model.UpdateContentList;
import com.desheng.app.toucai.panel.ActLoginPasswordTouCai;
import com.desheng.app.toucai.panel.ActMain;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayConfigCategoryTouCai;
import com.desheng.base.panel.ActChangeLoginPassword;
import com.desheng.base.util.SetList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 用户数据类. 保存登录信息, 事件通知, 工具方法.
 */
public class UserManagerTouCai extends UserManager {
    public static String TAG = UserManagerTouCai.class.getSimpleName();
    private Map<String, List<LotteryPlayConfigCategoryTouCai>> categorys = new HashMap<>();
    private Map<String, Map<String, List<LotteryPlayConfigCategoryTouCai.CatBean.DataBean>>> allInOne = new HashMap<>();
    private Map<String, LotteryJump> jumpInfo;

    static {
        ACTION_LOGIN = AbDevice.appPackageName + ".login";
        ACTION_REGIST = AbDevice.appPackageName + ".regist";
        DEFAULT_SELECTED_LATTERYS = "[11, 24, 41]";
        LOGIN_ACTIVITY = ActLoginPasswordTouCai.class;
        QZ_MODIFY_PWD_ACTIVITY = ActChangeLoginPassword.class;
    }

    public static UserManagerTouCai getIns() {
        return (UserManagerTouCai) ins;
    }

    @Override
    public void init(Application app) {
        super.init(app);
        initCategoryToucai();
        initAllInOne();
        initJump();
    }

    public void redirectToLoginForRecheck() {

        UserManagerTouCai.getIns().setUserChangwanGameData("[]");
        EventBus.getDefault().post(new DajiangPushMode(1));

        Intent itt = new Intent(ACTION_LOGIN);
        itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        itt.addCategory(Intent.CATEGORY_DEFAULT);
        itt.putExtra("isFromCheck", true);
        Global.app.startActivity(itt);
    }

    //弃用allLotteryInfos.json改为服务端获取, KotteryKind更新
    protected void initLotteryInfo() {

    }

    //弃用allLotteryInfos.json改为服务端获取, KotteryKind更新
    @Deprecated
    public List<String> getLotteryInfoIndexList() {
        return null;
    }

    //弃用allLotteryInfos.json改为服务端获取, KotteryKind更新
    @Deprecated
    public Map<Integer, LotteryInfo> getLotteryInfoMap() {
        return null;
    }

    //弃用allLotteryInfos.json改为服务端获取, KotteryKind更新
    @Deprecated
    public Map<String, List<LotteryInfo>> getLotteryInfoIndexMap() {
        return null;
    }

    public void updateLotteryType(ArrayList<LotteryTypeCustom> netList) {
        List<LotteryInfo> lotteryList = new ArrayList<>();
        for (int i = 0; i < netList.size(); i++) {
            LotteryTypeCustom custom = netList.get(i);
            if (custom.getShowName().contains("热门")) {
                continue;
            }

            LotteryType.customMap.put(custom.getCode(), custom);

            if (custom.getLotteryList() == null) {
                custom.setLotteryList(new ArrayList<LotteryInfoCustom>());
            }
            for (int j = 0; j < custom.getLotteryList().size(); j++) {
                LotteryInfoCustom lottery = custom.getLotteryList().get(j);
                LotteryInfo info = new LotteryInfo(lottery.getId(), lottery.getCode(), lottery.getShowName(), lottery.getAppIcon());
                lotteryList.add(info);
            }
        }
        updateLotteryInfoAndKind(lotteryList);
    }

    /**
     * 更新所有的数据.
     *
     * @param netList
     */
    @Override
    public void updateLotteryInfoAndKind(List<LotteryInfo> netList) {
        if (netList == null || netList.size() == 0) {
            return;
        }
        for (int i = 0; i < netList.size(); i++) {
            LotteryKind.find(netList.get(i).getId()).updateAndEnable(netList.get(i));
        }
        sendBroadcaset(Global.app, EVENT_USER_UPDATE_LOTTERY, null);
    }

    /**
     * 获取最新保存的推送消息
     *
     * @param msg
     */
    public void setLatestPushMsg(AppUmengPushHandler.CustomMsg msg) {
        if (msg == null) {
            spPersistent.setString("latest_push_msg", "{}");
        } else {
            spPersistent.setString("latest_push_msg", new Gson().toJson(msg));
        }
    }

    /**
     * 从退出登录跳到登录
     *
     * @param act
     */
    public void reLogin(Activity act, String userName, String password) {
        Intent itt = new Intent(act, ActMain.class);
        ActMain.mainKillSwitch = false;
        ActMain.navigatePage = 0;
        UserManager.getIns().setBroadCount(0);
        act.startActivity(itt);
    }

    /**
     * 从修改密码跳到登录
     *
     * @param act
     */
    public void reLogin(Activity act, String userName) {
        reLogin(act, userName, null);
    }

    /**
     * 获取短信验证码
     *
     * @param tag
     * @param phoneNum
     * @param yanzCode
     */
    public void getSmsCodeNoTimer(Activity tag, boolean checkUnique, String phoneNum, String yanzCode, AbCallback<String> callback) {
        HttpActionTouCai.getSmsCodeNew(tag, checkUnique, phoneNum, yanzCode, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<HashMap<String, Object>>() {
                }.getType());
            }

            //1485
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show(Global.app, "发送验证码成功!", true);
                    DialogsTouCai.hideVerifyPhotoDialog(tag);
                } else if (code == 1 && error == 1) {
                    HashMap<String, Object> data = (HashMap<String, Object>) extra.get("data");
                    if (data != null) {
                        boolean needCapchaCode = Maps.value(data, "needCapchaCode", false);
                        if (needCapchaCode) {
                            DialogsTouCai.showPicVerifyDialog(tag, new AbCallback<String>() {
                                @Override
                                public boolean callback(String obj) {
                                    if (callback != null) {
                                        callback.callback(obj);
                                    } else {
                                        DialogsTouCai.hideVerifyPhotoDialog(tag);
                                    }
                                    return true;
                                }
                            });
                        }
                    } else {
                        Toasts.show(Global.app, msg, false);
                    }
                } else {
                    Toasts.show(Global.app, msg, false);
                }

                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(Global.app, "发送验证码失败!", false);
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
    public void getSmsCodeWithTimer(Activity tag, boolean checkUnique, String phoneNum, String yanzCode, AbCallback<String> successCallback, AbCallback<String> failCallback) {
        HttpActionTouCai.getSmsCodeNew(tag, checkUnique, phoneNum, yanzCode, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<HashMap<String, Object>>() {
                }.getType());
            }

            //1485
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show(Global.app, "发送验证码成功!", true);
                    DialogsTouCai.hideVerifyPhotoDialog(tag);
                    if (successCallback != null) {
                        successCallback.callback(msg);
                    }
                } else if (code == 1 && error == 1) {
                    HashMap<String, Object> data = (HashMap<String, Object>) extra.get("data");
                    if (data != null) {
                        boolean needCapchaCode = Maps.value(data, "needCapchaCode", false);
                        if (needCapchaCode) {
                            DialogsTouCai.showPicVerifyDialog(tag, new AbCallback<String>() {
                                @Override
                                public boolean callback(String obj) {
                                    if (failCallback != null) {
                                        failCallback.callback(obj);
                                    } else {
                                        DialogsTouCai.hideVerifyPhotoDialog(tag);
                                    }
                                    return true;
                                }
                            });
                        }
                        if (yanzCode != null) {
                            Toasts.show(tag, msg, false);
                        }
                    } else {
                        Toasts.show(tag, msg, false);
                    }
                } else {
                    Toasts.show(Global.app, msg, false);
                }
                if (failCallback != null) {
                    failCallback.callback(null);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(Global.app, "发送验证码失败!", false);
                if (failCallback != null) {
                    failCallback.callback(null);
                }
                return true;
            }
        });
    }


    public AppUmengPushHandler.CustomMsg getLatestPushMsg() {
        return new Gson().fromJson(spPersistent.getString("latest_push_msg", "{}"), AppUmengPushHandler.CustomMsg.class);
    }

    public boolean isValideAccountOrPhone(String account) {
        if (Strs.isPhoneValid(account)) {
            return true;
        } else if (isValideAccount(account)) {
            return true;
        }

        return false;
    }

    public boolean isValideAccount(String account) {
        Pattern pattern3 = Pattern.compile("^[a-zA-Z_0-9]{6,24}$");
        if (pattern3.matcher(account).matches()) {
            return true;
        }
        return false;
    }

    public boolean isValidePhotoVerifyCode(String code) {
        return Strs.isNotEmpty(code) && code.length() == 5;
    }

    public boolean isValideSmsVerifyCode(String code) {
        return Strs.isNotEmpty(code) && code.length() == 4;
    }

    public boolean isValidePwd(String pwd) {
        Pattern pattern1 = Pattern.compile("[0-9A-Za-z]{6,24}");
        if (pattern1.matcher(pwd).matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Pattern pattern1 = Pattern.compile("[0-9A-Z_a-z]{6,24}");
     * if(!pattern1.matcher(pwd).matches()){
     * Toasts.show(this, "密码为6到24位字母和数字组合", false);
     * return;
     * }
     * Pattern pattern2 = Pattern.compile("[0-9]+"); //必须
     * if(!pattern2.matcher(pwd).find()){
     * Toasts.show(this, "密码为6到24位字母和数字组合, 必须同时包含字符,数字", false);
     * return;
     * }
     *
     * @param pwd
     * @return
     */
    public boolean isValidePwdForRegist(String pwd) {
        Pattern pattern1 = Pattern.compile("[0-9A-Za-z]{6,24}");
        if (pattern1.matcher(pwd).matches()) {
//            boolean isContainNum = false;
//            Pattern pattern2 = Pattern.compile("[0-9]+"); //必须
//            isContainNum = pattern2.matcher(pwd).find();
//            boolean isContainAlpha = false;
//            Pattern pattern3 = Pattern.compile("[A-Za-z]+"); //必须
//            isContainAlpha = pattern3.matcher(pwd).find();
//            if (isContainNum && isContainAlpha) {
//                return true;
//            } else {
//                return false;
//            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidePhone(String phone) {
        return Strs.isPhoneValid(phone) && phone.length() >= 11;
    }

    private void initCategoryToucai() {
        String json = Files.readAssets("lottery_all_infos/betDetailLotteryCategoryTouCai.json");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        List<String> arrKeys = gson.fromJson(jsonObject.get("index"), new TypeToken<ArrayList<String>>() {
        }.getType());

        for (int i = 0; i < arrKeys.size(); i++) {
            String key = arrKeys.get(i);
            JsonArray value = jsonObject.get(key).getAsJsonArray();
            categorys.put(key, Collections.unmodifiableList(gson.fromJson(value, new TypeToken<List<LotteryPlayConfigCategoryTouCai>>() {
            }.getType())));
        }

        categorys = Collections.unmodifiableMap(categorys);
    }

    private void initAllInOne() {
        String json = Files.readAssets("lottery_all_infos/allInOneCategory.json");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        allInOne = gson.fromJson(jsonObject, new TypeToken<Map<String, Map<String, List<LotteryPlayConfigCategoryTouCai.CatBean.DataBean>>>>() {
        }.getType());

        allInOne = Collections.unmodifiableMap(allInOne);
    }

    private void initJump() {
        String json = Files.readAssets("lottery_all_infos/jumpClassic.json");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        jumpInfo = gson.fromJson(jsonObject, new TypeToken<Map<String, LotteryJump>>() {
        }.getType());
        jumpInfo = Collections.unmodifiableMap(jumpInfo);
    }

    public Map<String, List<LotteryPlayConfigCategoryTouCai>> getCategorys() {
        return categorys;
    }

    public Map<String, Map<String, List<LotteryPlayConfigCategoryTouCai.CatBean.DataBean>>> getAllInOne() {
        return allInOne;
    }

    public LotteryPlay getLotteryPlay(String cat, String playcode) {
        LotteryPlay play = null;
        if (getLotteryPlayCategoryMap() != null && getLotteryPlayCategoryMap().get(cat) != null) {
            play = getLotteryPlayCategoryMap().get(cat).get(playcode);
        }

        if (play != null)
            return play;

        if (getCategorys().get(cat) == null) {
            return null;
        }

        // 不走这里，多合一玩法返回没有实际配置的lotteryplay
/*        List<LotteryPlayConfigCategoryTouCai.CatBean.DataBean> list = allInOne.get(cat).get(playcode);
        if (list != null && list.size() > 0) {
            LotteryPlayConfigCategoryTouCai.CatBean.DataBean d = list.get(0);
            play = getLotteryPlayCategoryMap().get(cat).get(d.getLotteryCode());
            return play;
        }*/

        return null;
    }

    public LotteryPlay getLotteryPlay(int lotterId, String playcode) {
        LotteryPlay play = null;

        if (Strs.isNotEmpty(UserManagerTouCai.getIns().getlastLotteryPlay(lotterId))) {

        }

        return null;
    }

    public Map<String, LotteryJump> getJumpInfo() {
        return jumpInfo;
    }

    public void setRedPacketAwardPoolStatus(boolean useout) {
        spPersistent.put("RedPacketAwardPoolStatus", useout);
    }

    public boolean isRedPacketAwardPoolOver() {
        return spPersistent.getBoolean("RedPacketAwardPoolStatus", false);
    }

    public void setRedPacketMissionID(String missionID) {
        spPersistent.put("RedPacketMissionID", missionID);
    }

    public String getRedPacketMissionID() {
        return spPersistent.getString("RedPacketMissionID", "");
    }

    public void setUserChangwanGameData(String json) {
        if (Strs.isNotEmpty(json)) {
            List<LotteryInfoCustom> changwanGameData = new Gson().fromJson(json, new TypeToken<List<LotteryInfoCustom>>() {
            }.getType());

            Collections.reverse(changwanGameData);

            LinkedHashMap<Integer, LotteryInfoCustom> getchangwan = new LinkedHashMap<>();

            for (LotteryInfoCustom custom : changwanGameData) {
                //处理并存储
                getchangwan.put(custom.getId(), custom);
                Log.e(TAG, "后台获取:" + custom.getShowName());
            }

            String getchangwanjson = new Gson().toJson(getchangwan);
            spPersistent.put("latest_game", getchangwanjson);
        } else {
            spPersistent.put("latest_game", "");
        }
    }


    public void setUserChangwanGameData(LotteryInfoCustom lotteryInfoCustom) {
        if (spPersistent.contains("latest_game")) {
            String changwan = spPersistent.getString("latest_game", "");
            Gson gson = new Gson();

            LinkedHashMap<Integer, LotteryInfoCustom> hashMap = gson.fromJson(changwan, new TypeToken<LinkedHashMap<Integer, LotteryInfoCustom>>() {
            }.getType());

            if (hashMap == null) {
                hashMap = new LinkedHashMap<Integer, LotteryInfoCustom>();
            }

            if (hashMap.containsKey(lotteryInfoCustom.getId())) {
                hashMap.remove(lotteryInfoCustom.getId());
            }

            hashMap.put(lotteryInfoCustom.getId(), lotteryInfoCustom);

            String getchangwanjson = gson.toJson(hashMap);
            Log.d(TAG, getchangwanjson);
            spPersistent.put("latest_game", getchangwanjson);

        } else {
            LinkedHashMap<Integer, LotteryInfoCustom> getchangwan = new LinkedHashMap<>();

            if (getchangwan.containsKey(lotteryInfoCustom.getId())) {
                getchangwan.remove(lotteryInfoCustom.getId());
            }

            //处理并存储
            getchangwan.put(lotteryInfoCustom.getId(), lotteryInfoCustom);

            String getchangwanjson = new Gson().toJson(getchangwan);
            Log.d(TAG, getchangwanjson);
            spPersistent.put("latest_game", getchangwanjson);
        }
    }

    /**
     * 取出每个彩种进入的次数，并进行比较排序，输出list集合结果（最大展示前8个最常玩彩种）
     */
    public List<LotteryInfoCustom> UserZuijinGameData() {
        if (UserManager.getIns().isLogined()) {
            String changwan = spPersistent.getString("latest_game", "");
            //Log.w(TAG, changwan);
            if (TextUtils.isEmpty(changwan)) {
                return null;
            }
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            LinkedHashMap<Integer, LotteryInfoCustom> hashMap = gson.fromJson(changwan, new TypeToken<LinkedHashMap<Integer, LotteryInfoCustom>>() {
            }.getType());

            if (hashMap == null) {
                return null;
            }

            ArrayList<LotteryInfoCustom> infoCustoms = new ArrayList<>();
            infoCustoms.clear();
            for (Integer key : hashMap.keySet()) {
                infoCustoms.add(hashMap.get(key));
                // Log.e(TAG, "取出时:" + hashMap.get(key));
            }

            if (infoCustoms != null && infoCustoms.size() != 0) {
                Collections.reverse(infoCustoms);
                if (infoCustoms.size() < 8) {
                    //Log.w(TAG, "上报数据：1---"+gson.toJson(infoCustoms));
                    HttpActionTouCai.setFrequentlyUsedLottery(this, gson.toJson(infoCustoms), new AbHttpResult());
                    return infoCustoms;
                } else {
                    //Log.w(TAG, "上报数据：2---"+gson.toJson(infoCustoms.subList(0, 8)));
                    HttpActionTouCai.setFrequentlyUsedLottery(this, gson.toJson(infoCustoms.subList(0, 8)), new AbHttpResult());
                    return infoCustoms.subList(0, 8);
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 记录每个彩种进入的次数
     *
     * @param id
     */
    public void setUserChangwanGameData(int id) {

        if (spPersistent.contains("changwan")) {
            String changwan = spPersistent.getString("changwan", "");
            Gson gson = new Gson();

            HashMap<Integer, Integer> hashMap = gson.fromJson(changwan, new TypeToken<HashMap<Integer, Integer>>() {
            }.getType());
            boolean containsKey = hashMap.containsKey(id);

            int nums = 0;
            if (containsKey) {
                nums = hashMap.get(id);
            }

            //处理并存储
            hashMap.put(id, nums + 1);

            String getchangwanjson = gson.toJson(hashMap);
            Log.d(TAG, getchangwanjson);
            spPersistent.put("changwan", getchangwanjson);

        } else {
            HashMap<Integer, Integer> getchangwan = new HashMap<>();

            //处理并存储
            getchangwan.put(id, 1);

            String getchangwanjson = new Gson().toJson(getchangwan);
            Log.d(TAG, getchangwanjson);
            spPersistent.put("changwan", getchangwanjson);
        }

    }

    /**
     * 取出每个彩种进入的次数，并进行比较排序，输出list集合结果（最大展示前8个最常玩彩种）
     */
    public List<Integer> UserChangwanGameData() {
        String changwan = spPersistent.getString("changwan", "");
        Log.d(TAG, changwan);
        HashMap<Integer, Integer> hashMap = new Gson().fromJson(changwan, new TypeToken<HashMap<Integer, Integer>>() {
        }.getType());
        if (hashMap == null) {
            return null;
        }
        for (Object key : hashMap.keySet()) {
            Log.d(TAG, "hashMap.get(key):" + hashMap.get(key));
        }

        List<Map.Entry<Integer, Integer>> list = new ArrayList<>();
        list.addAll(hashMap.entrySet());
        ValueComparator vc = new ValueComparator();
        Collections.sort(list, vc);
        List<Integer> gameIds = new ArrayList<>();//装排序后的gameid
        for (Map.Entry<Integer, Integer> entry : list) {
            gameIds.add(entry.getKey());
        }

        //取前8个
        if (gameIds.size() >= 0 && gameIds.size() < 8) {
            return gameIds;
        } else {
            List<Integer> subList = gameIds.subList(0, 8);
            return subList;
        }
    }

    /**
     * 按玩的次数进行排序
     */
    private static class ValueComparator implements Comparator<Map.Entry<Integer, Integer>> {
        public int compare(Map.Entry<Integer, Integer> m, Map.Entry<Integer, Integer> n) {
            return n.getValue() - m.getValue();
        }
    }


    public void setlastLotteryPlay(int lotteryId, String lotteryPlayId) {
        if (spPersistent.contains("lastLotteryPlay")) {
            String changwan = spPersistent.getString("lastLotteryPlay", "");
            Log.d(TAG, "setlastLotteryPlay---changwan json:" + changwan);
            Gson gson = new Gson();
            HashMap<Integer, String> hashMap = gson.fromJson(changwan, new TypeToken<HashMap<Integer, String>>() {
            }.getType());

            if (hashMap != null) {
                hashMap.put(lotteryId, lotteryPlayId);
            }

            spPersistent.put("lastLotteryPlay", gson.toJson(hashMap));
        } else {
            Gson gson = new Gson();

            HashMap<Integer, String> hashMap = new HashMap<>();

            if (hashMap != null) {
                hashMap.put(lotteryId, lotteryPlayId);
            }

            String toJson = gson.toJson(hashMap);
            Log.d(TAG, "setlastLotteryPlay---toJson json:" + toJson);
            spPersistent.put("lastLotteryPlay", toJson);
        }


        setlastLotteryPlayNew(lotteryId, lotteryPlayId);
    }


    public void setlastLotteryPlayNew(int lotteryId, String lotteryPlayId) {

        if (spPersistent.contains("lastLotteryPlayNew")) {
            String changwan = spPersistent.getString("lastLotteryPlayNew", "");


            Log.e(TAG, "lastLotteryPlayNew---changwan json:" + changwan);
            Gson gson = new Gson();
            HashMap<Integer, SetList<String>> hashMap = gson.fromJson(changwan, new TypeToken<HashMap<Integer, SetList<String>>>() {
            }.getType());

            if (hashMap != null) {
                SetList<String> playSets = hashMap.get(lotteryId);
                if (playSets == null) {
                    SetList<String> temp = new SetList<>();
                    temp.add(lotteryPlayId);
                    hashMap.put(lotteryId, temp);
                } else {
                    playSets.add(lotteryPlayId);
                    hashMap.put(lotteryId, playSets);
                }
            }
            spPersistent.put("lastLotteryPlayNew", gson.toJson(hashMap));
        } else {
            Gson gson = new Gson();

            HashMap<Integer, SetList<String>> hashMap = new HashMap<>();

            if (hashMap != null) {
                SetList<String> temp = new SetList<>();
                temp.add(lotteryPlayId);
                hashMap.put(lotteryId, temp);
            }

            String toJson = gson.toJson(hashMap);
            Log.e(TAG, "lastLotteryPlayNew---toJson json:" + toJson);
            spPersistent.put("lastLotteryPlayNew", toJson);
        }


    }

    public List<String> getlastLotteryPlayNew(int lotteryId) {

        String changwan = spPersistent.getString("lastLotteryPlayNew", "");

        if (Strs.isEmpty(changwan)) {
            return null;
        }

        HashMap<Integer, SetList<String>> hashMap = new Gson().fromJson(changwan, new TypeToken<HashMap<Integer, SetList<String>>>() {
        }.getType());

        if (hashMap != null) {
            SetList<String> setList = hashMap.get(lotteryId);
            if (setList == null) {
                return null;
            }
            ArrayList<String> temp = new ArrayList<>();
            temp.addAll(setList);
            Collections.reverse(temp);
            return temp;
        } else {
            return null;
        }
    }

    public String getlastLotteryPlay(int lotteryId) {

        String changwan = spPersistent.getString("lastLotteryPlay", "");

        HashMap<Integer, String> hashMap = new Gson().fromJson(changwan, new TypeToken<HashMap<Integer, String>>() {
        }.getType());

        if (hashMap != null) {
            return hashMap.get(lotteryId);
        } else {
            return null;
        }
    }

    public String getBonusPoolMoneyShowFrom() {
        return spPersistent.getString("bonusPoolMoneyShowFrom", "0.00");
    }

    public void setBonusPoolMoneyShowFrom(String bonusPoolMoneyShowFrom) {
        spPersistent.put("bonusPoolMoneyShowFrom", bonusPoolMoneyShowFrom);
    }


    public void setNotReadMsgPool(List<ContactsMode> contactslist) {
        Map<String, LatestMsgComeRefreshMode> hashMap = new HashMap<>();
        Gson gson = new Gson();
        for (ContactsMode contactsMode : contactslist) {
            LatestMsgComeRefreshMode latestMsgComeRefreshMode = new LatestMsgComeRefreshMode();
            latestMsgComeRefreshMode.setNewMsgNotReadCount(contactsMode.getUnReadCount());
            hashMap.put(contactsMode.getInviteCode(), latestMsgComeRefreshMode);
        }
        String toJson = gson.toJson(hashMap);
        Log.d(TAG, "toJson:" + toJson);
        spUser.put("updateNotReadMsgPool", toJson);
    }

    /**
     * 更新数据
     *
     * @param backmessageInfo
     */
    public void updateNotReadMsgPool(ContactsBackMsgMode backmessageInfo) {

        if (spUser.contains("updateNotReadMsgPool")) {
            String notReadMsgPool = spUser.getString("updateNotReadMsgPool", "");
            Log.d(TAG, "updateNotReadMsgPool:" + notReadMsgPool);
            Gson gson = new Gson();
            HashMap<String, LatestMsgComeRefreshMode> hashMap = gson.fromJson(notReadMsgPool, new TypeToken<HashMap<String, LatestMsgComeRefreshMode>>() {
            }.getType());

            LatestMsgComeRefreshMode latestMsgComeRefreshMode = hashMap.get(backmessageInfo.getSendInviteCode());
            if (latestMsgComeRefreshMode != null) {
                latestMsgComeRefreshMode.setId(backmessageInfo.getId());
                latestMsgComeRefreshMode.setCreateTime(backmessageInfo.getCreateTime());
                latestMsgComeRefreshMode.setSendInviteCode(backmessageInfo.getSendInviteCode());
                latestMsgComeRefreshMode.setText(backmessageInfo.getText());
                latestMsgComeRefreshMode.setMsgType(backmessageInfo.getMsgType());
                Log.d(TAG, "count:" + latestMsgComeRefreshMode.getNewMsgNotReadCount());
                if (hashMap.containsKey(backmessageInfo.getSendInviteCode())) {
                    hashMap.remove(backmessageInfo.getSendInviteCode());
                    latestMsgComeRefreshMode.setNewMsgNotReadCount(latestMsgComeRefreshMode.getNewMsgNotReadCount() + 1);
                    hashMap.put(backmessageInfo.getSendInviteCode(), latestMsgComeRefreshMode);
                }

                String newJson = gson.toJson(hashMap);
                Log.d(TAG, "newJson:" + newJson);
                spUser.put("updateNotReadMsgPool", gson.toJson(hashMap));
            }
        }
    }

    /**
     * @param inviteCode
     * @return
     */
    public LatestMsgComeRefreshMode getNotReadMsgPool(String inviteCode) {

        if (spUser.contains("updateNotReadMsgPool")) {
            String notReadMsgPool = spUser.getString("updateNotReadMsgPool", "");
            Log.d(TAG, "getNotReadMsgPool:" + notReadMsgPool);
            Gson gson = new Gson();
            HashMap<String, LatestMsgComeRefreshMode> hashMap = gson.fromJson(notReadMsgPool, new TypeToken<HashMap<String, LatestMsgComeRefreshMode>>() {
            }.getType());
            return hashMap.get(inviteCode);
        } else {
            return null;
        }
    }

    /**
     * 进入对对应的聊天窗口后 把存储的对应人的未读消息数设置为0
     *
     * @param inviteCode
     */
    public void setMsgIsRead(String inviteCode) {

        if (spUser.contains("updateNotReadMsgPool")) {
            String notReadMsgPool = spUser.getString("updateNotReadMsgPool", "");
            Log.d(TAG, "getNotReadMsgPool:" + notReadMsgPool);
            Gson gson = new Gson();
            HashMap<String, LatestMsgComeRefreshMode> hashMap = gson.fromJson(notReadMsgPool, new TypeToken<HashMap<String, LatestMsgComeRefreshMode>>() {
            }.getType());

            LatestMsgComeRefreshMode refreshMode = hashMap.get(inviteCode);

            if (hashMap.containsKey(inviteCode)) {
                hashMap.remove(inviteCode);
                refreshMode.setNewMsgNotReadCount(0);
                hashMap.put(inviteCode, refreshMode);
            }

            String newJson = gson.toJson(hashMap);
            Log.d(TAG, "newJson:" + newJson);
            spUser.put("updateNotReadMsgPool", gson.toJson(hashMap));
        }
    }

    /**
     * 计算总的未读消息数
     */
    public int getMsgNotReadCountSum() {

        if (spUser.contains("updateNotReadMsgPool")) {
            String notReadMsgPool = spUser.getString("updateNotReadMsgPool", "");
            Log.d(TAG, "getNotReadMsgPool:" + notReadMsgPool);
            Gson gson = new Gson();
            HashMap<String, LatestMsgComeRefreshMode> hashMap = gson.fromJson(notReadMsgPool, new TypeToken<HashMap<String, LatestMsgComeRefreshMode>>() {
            }.getType());

            int countSum = 0;
            for (String key : hashMap.keySet()) {
                countSum += hashMap.get(key).getNewMsgNotReadCount();
            }
            Log.d(TAG, "countSum:" + countSum);
            return countSum;
        }
        return 0;
    }

    public void setSplashOnlineBg(String bgUrl) {
        if (Strs.isEmpty(bgUrl)) {
            return;
        }
        spPersistent.put("splashonlinebg", bgUrl);
    }

    public String getSplashOnlineBg() {
        if (spPersistent.contains("splashonlinebg")) {
            return spPersistent.getString("splashonlinebg", "");
        }
        return null;
    }

    public void setBgPic() {
        HttpActionTouCai.getUpdateContentList(this, Consitances.contentManager.BG_ACT_LAUNCHER, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<UpdateContentList>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    UpdateContentList list = getField(extra, "data", null);
                    List<UpdateContentList.UpdateContent> contents = list.getList();
                    if (contents != null && contents.size() > 0) {
                        UserManagerTouCai.getIns().setSplashOnlineBg(ENV.curr.host + contents.get(0).getMobilePictureUrl());
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });

        //需要展示引导页，并且后台皮肤时默认设置时 才允许考虑预加载数据
        HttpActionTouCai.getUpdateContentList(this, Consitances.contentManager.BG_ACT_GUIDE, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<UpdateContentList>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    UpdateContentList list = getField(extra, "data", null);

                    if (list == null || list.getList() == null || list.getList().size() == 0) {
                        return true;
                    }
                    List<UpdateContentList.UpdateContent> contents = list.getList();
                    if (contents != null && contents.size() > 0) {
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < contents.size(); i++) {
                            if (i == contents.size() - 1) {
                                stringBuffer.append(ENV.curr.host + contents.get(i).getMobilePictureUrl());
                            } else {
                                stringBuffer.append(ENV.curr.host + contents.get(i).getMobilePictureUrl());
                                stringBuffer.append("=@=");
                            }
                        }
                        UserManagerTouCai.getIns().setGuideOnlineBg(stringBuffer.toString());
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });
    }

    public void setGuideOnlineBg(String guideUrls) {
        if (Strs.isEmpty(guideUrls)) {
            return;
        }
        spPersistent.put("guideUrls", guideUrls);
    }

    public List<String> getGuideOnlineBg() {
        if (spPersistent.contains("guideUrls")) {
            String guideUrls = spPersistent.getString("guideUrls", "");
            if (Strs.isNotEmpty(guideUrls)) {
                return Arrays.asList(guideUrls.split("=@="));
            }
        }
        return null;
    }

    public void getChangwanData() {
        //用户常玩数据
        HttpActionTouCai.getFrequentlyUsedLottery(this, new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<String>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (error == 0 && code == 0) {
                    String data = (String) extra.get("data");
                    //存到sp
                    UserManagerTouCai.getIns().setUserChangwanGameData(data);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });
    }

}

