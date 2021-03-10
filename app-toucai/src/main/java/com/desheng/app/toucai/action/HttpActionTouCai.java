package com.desheng.app.toucai.action;

import android.text.TextUtils;
import android.util.Log;

import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
import com.ab.util.MD5;
import com.ab.util.Maps;
import com.ab.util.Strs;
import com.desheng.app.toucai.panel.ActThirdGameRecordNew;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.panel.ActThirdGameRecord;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * http访问对象
 * Created by lee on 2017/10/2.
 */
public class HttpActionTouCai extends HttpAction {


    /**
     * 获取游戏连接
     */
    public static final String GET_LOGIN_URL = "/yx/thirdGameApi/common/getLoginUrl";

    public static void getThirdGameLink(Object tag, int platformId, String subGame, AbHttpResult result) {
        Maps.gen();
        Maps.put("platformId", platformId);
        Maps.put("subGame", subGame);
        Maps.put("isMobile", 1);
        MM.http.post(null, GET_LOGIN_URL, Maps.get(), result);
    }

    /**
     * 第三方游戏记录
     */
    public static final String IM_REPORT = "/yx/u/api/report/im-report";
    private static final String AGIN_REPORT = "/yx/u/api/report/agin-report";
    private static final String KY_REPORT = "/yx/u/api/report/kyori-report";
    private static final String GM_REPORT = "/yx/u/api/report/gm-report";
    private static final String PT_REPORT = "/yx/u/api/report/pt-report";
    private static final String LEG_REPORT = "/yx/u/api/report/leg-report";
    private static final String CQ_REPORT = "/yx/u/api/report/cq-report";
    private static final String BBIN_REPORT = "/yx/u/api/report/bbin-report";
    private static final String OG_REPORT = "/yx/u/api/report/og-report";
    private static final String DT_REPORT = "/yx/u/api/report/dt-report";

    public static void getThirdReport(Object tag, String gameType, String betDateBegin, String betDateEnd, int page, int size, String game, AbHttpResult result) {
        Maps.gen();
        Maps.put("gameType", gameType);
        Maps.put("betDateBegin", betDateBegin);
        Maps.put("betDateEnd", betDateEnd);
        Maps.put("page", page);
        Maps.put("size", size);

        if (game.equals(ActThirdGameRecord.THIRD_TYPE_IM)) {
            MM.http.post(tag, IM_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecord.THIRD_TYPE_AGIN)) {
            MM.http.post(tag, AGIN_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecord.THIRD_TYPE_KY)) {
            MM.http.post(tag, KY_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecord.THIRD_TYPE_DS)) {
            MM.http.post(tag, GM_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecord.THIRD_TYPE_PT)) {
            MM.http.post(tag, PT_REPORT, Maps.get(), result);
        } else {
            MM.http.post(tag, "", Maps.get(), result);
        }
    }


    public static void getThirdReportNew(Object tag, String gameType, String betDateBegin, String betDateEnd, int page, int size, String game, AbHttpResult result) {
        Maps.gen();
        Maps.put("gameType", gameType);
        Maps.put("betDateBegin", betDateBegin);
        Maps.put("betDateEnd", betDateEnd);
        Maps.put("page", page);
        Maps.put("size", size);

        if (game.equals(ActThirdGameRecordNew.THIRD_TYPE_IM)) {
            MM.http.post(tag, IM_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecordNew.THIRD_TYPE_AGIN)) {
            MM.http.post(tag, AGIN_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecordNew.THIRD_TYPE_KY)) {
            MM.http.post(tag, KY_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecordNew.THIRD_TYPE_PT)) {
            MM.http.post(tag, PT_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecordNew.THIRD_TYPE_CQ)) {
            MM.http.post(tag, CQ_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecordNew.THIRD_TYPE_LEG)) {
            MM.http.post(tag, LEG_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecordNew.THIRD_TYPE_BBIN)) {
            MM.http.post(tag, BBIN_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecordNew.THIRD_TYPE_OG)) {
            MM.http.post(tag, OG_REPORT, Maps.get(), result);
        } else if (game.equals(ActThirdGameRecordNew.THIRD_TYPE_DT)) {
            MM.http.post(tag, DT_REPORT, Maps.get(), result);
        }else {
            MM.http.post(tag, "", Maps.get(), result);
        }
    }

    /**
     * 用户消息
     */
    public static final String GET_USER_PUSH_MESSAGE = "/message-push/getNoticeMessageList";

    public static void getUserPushMessage(Object tag, int page, int size, AbHttpResult result) {
        Maps.gen();
        Maps.put("pageNo", page);
        Maps.put("pageSize", size);
        MM.http.post(tag, GET_USER_PUSH_MESSAGE, Maps.get(), result);
    }


    /**
     * 用户消息
     */
    public static final String READ_MESSAGE = "/yx/u/api/account/read-message";

    public static void readMessage(Object tag, String ids, AbHttpResult result) {
        Maps.gen();
        Maps.put("ids", ids);
        MM.http.post(tag, READ_MESSAGE, Maps.get(), result);
    }

    /**
     * 用户消息
     */
    public static final String CHECK_FIRST_CHARGE = "/yx/pay/isFirstRecharge";

    public static void checkFirstCharge(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.post(tag, CHECK_FIRST_CHARGE, Maps.get(), result);
    }

    /**
     * 用户卡真实姓名
     */
    public static final String LIST_CARD = "/yx/u/api/account/list-card";

    public static void listCard(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.post(tag, LIST_CARD, Maps.get(), result);
    }

    public static final String GET_SMS_CODE_2 = "/yx/send-verify-code2";


    /**
     * 获取短信验证码新
     */
    public static void getSmsCodeNew(Object tag, boolean checkUnique, String phoneNum, String yanzCode, AbHttpResult callBack) {
        Maps.gen();
        Maps.put("phoneNum", phoneNum);
        Maps.put("checkUnique", checkUnique);
        if (!TextUtils.isEmpty(yanzCode))
            Maps.put("capchaCode", yanzCode);
        Maps.put("type", 1);
        MM.http.post(tag, GET_SMS_CODE_2, true, Maps.get(), callBack);
    }

    public static final String getLotteryRoom = "/yx/api/lotterySort/lotteryRoom";

    /**
     * 获取彩厅数据
     */
    public static void getLotteryRoom(Object tag, AbHttpResult callBack) {
        Maps.gen();
        Maps.put("showTerminalType", 2);
        MM.http.get(tag, getLotteryRoom, Maps.get(), callBack);
    }

    public static final String GET_LOTTERY_PROMOTIONS = "/yx/api/lotterySort/lotteryAdv";

    /**
     * 获取彩厅数据
     */
    public static void getLotteryPromotions(Object tag, AbHttpResult callBack) {
        Maps.gen();
        MM.http.get(tag, GET_LOTTERY_PROMOTIONS, Maps.get(), callBack);
    }

    public static final String GET_FAVOURITE_GAME = "/yx/u/api/game-lottery/get-favourite-game";

    /**
     * 获取用户喜爱
     */
    public static void getUserFavoriteLottery(Object tag, AbHttpResult callBack) {
        Maps.gen();
        MM.http.get(tag, GET_FAVOURITE_GAME, Maps.get(), callBack);
    }

    public static final String UPDATE_FAVORITE_GAME = "/yx/u/api/game-lottery/upd-favourite-game";

    /**
     * 设置用户喜爱
     */
    public static void updateFavoriteGame(Object tag, List<Integer> listLotteryIds, AbHttpResult callBack) {
        Maps.gen();
        String lotteryIds = "";
        for (Integer lotteryId : listLotteryIds) {
            lotteryIds += Strs.isEmpty(lotteryIds) ? lotteryId : "," + lotteryId;
        }
        Maps.put("lotteryIds", lotteryIds);
        MM.http.get(tag, UPDATE_FAVORITE_GAME, Maps.get(), callBack);
    }

    public static final String VALIDATE_RESET_LOGIN_PWD = "/yx/validate-reset-login-pwd";

    /**
     * 设置用户喜爱
     */
    public static void validateResetLoginPwd(Object tag, String name, String yanzCode, AbHttpResult callBack) {
        Maps.gen();
        Maps.put("name", name);
        Maps.put("yanzCode", yanzCode);
        MM.http.get(tag, VALIDATE_RESET_LOGIN_PWD, Maps.get(), callBack);
    }

    public static final String resetPwd = "/yx/reset-pwd";

    /**
     * 设置用户喜爱
     */
    public static void resetPwd(Object tag, String name, String yanzCode, String pwd, AbHttpResult callBack) {
        Maps.gen();
        if (name.startsWith("86")) {
            name = name.substring(2);
        }
        Maps.put("phoneNum", name);
        Maps.put("phYzCode", yanzCode);
        Maps.put("pwd", MD5.md5(pwd).toLowerCase());
        MM.http.get(tag, resetPwd, Maps.get(), callBack);
    }


    public static final String VERIFICATION_PHONE_CODE = "/yx/verification-phone-code";

    /**
     * 设置用户喜爱
     */
    public static void verificationPhoneCode(Object tag, String name, String yanzCode, AbHttpResult callBack) {
        Maps.gen();
        if (name.startsWith("86")) {
            name = name.substring(2);
        }
        Maps.put("phoneNum", name);
        Maps.put("phYzCode", yanzCode);
        MM.http.get(tag, VERIFICATION_PHONE_CODE, Maps.get(), callBack);
    }


    public static final String REQUEST_ALL_CATEGORY = "/yx/u/api/payment/request-all-category";

    /**
     * 查询支付大类
     */
    public static void requestAllCategory(Object tag, AbHttpResult callBack) {
        Maps.gen();
        MM.http.get(tag, REQUEST_ALL_CATEGORY, Maps.get(), callBack);
    }


    public static final String CANCEL_RECHARGE_ORDER = "/yx/u/api/payment/cancelRechargeOrder";

    /**
     * 取消订单接口
     */
    public static void cancelRechargeOrder(Object tag, String spsn, AbHttpResult callBack) {
        Maps.gen();
        Maps.put("spsn", spsn);
        MM.http.get(tag, CANCEL_RECHARGE_ORDER, Maps.get(), callBack);
    }

    public static final String GET_RECORD_BY_CATEGORY_ID = "/yx/u/api/payment/getRecordByCategoryId";

    /**
     * 获取最后订单接口
     */
    public static void getRecordByCategoryId(Object tag, String payChannelCategoryId, AbHttpResult callBack) {
        Maps.gen();
        Maps.put("payChannelCategoryId", payChannelCategoryId);
        MM.http.get(tag, GET_RECORD_BY_CATEGORY_ID, Maps.get(), callBack);
    }

    public static final String GET_MISSION_LIST = "/yx/api/activity/getList";

    /**
     * 活动列表
     */
    public static void getMissionList(Object tag, int bizType, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("isMobile", true);
        Maps.put("bizType", bizType);
        MM.http.get(tag, GET_MISSION_LIST, Maps.get(), abHttpResult);
    }

    /**
     * 获取活动配置信息
     * activityId:活动id
     */
    public static final String ATIVITY_CONFIG_DETAIL = "yx/api/activity/getLotteryDrawActivityDetail";

    public static void getActivityConfigDetail(Object tag, String activityId, AbHttpResult result) {
        Maps.gen();
        Maps.put("activityId", activityId);
        MM.http.get(tag, ATIVITY_CONFIG_DETAIL, Maps.get(), result);
    }


    /**
     * 获取剩余可领取红包个数
     * activityId:活动id
     */
    public static final String OBTAIN_DRAW_COUNT = "yx/api/activity/getLotteryDrawCount";

    public static void getObtainDrawCount(Object tag, String activityId, AbHttpResult result) {
        Maps.gen();
        Maps.put("activityId", activityId);
        MM.http.get(tag, OBTAIN_DRAW_COUNT, Maps.get(), result);
    }

    /**
     * 广告管理
     * showTerminalType: 终端类型：1(PC),2(H5),3(APP)
     * activityPosition: 1 (手机投注) 2(休闲游戏) 5 (官方彩票) 4 (手机下载) 6 (经典彩票) 3 (优惠活动) 0 (首页) 13 (投注页) 12 (我的) 11 (彩厅) 10 (活动) 9 (充值) 8 (手机首页)
     */
    public static final String OBTAIN_ADVS_MANAGMENT = "yx/api/advertisement/getList";

    public static void getObtainAdvsManagment(Object tag, int activityPosition, AbHttpResult result) {
        //Logger.e("广告管理--------------host: " + ENV.curr.host);
        Maps.gen();
        Maps.put("showTerminalType", 3);
        Maps.put("activityPosition", activityPosition);
        MM.http.get(tag, OBTAIN_ADVS_MANAGMENT, Maps.get(), result);
    }

    /**
     * 主页活动tab
     */
    public static void getActivityList(Object tag, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("isMobile", true);
        MM.http.get(tag, GET_MISSION_LIST, Maps.get(), abHttpResult);
    }


    public static final String GET_TOTAL_PRIZE_AMOUNT = "/yx/api/activity/getTotalPrizeAmount";

    public static void getTotalPrizeAmount(Object tag, String activityId, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("activityId", activityId);
        MM.http.get(tag, GET_TOTAL_PRIZE_AMOUNT, Maps.get(), abHttpResult);
    }


    public static final String VERIFY_SMS_CODE = "/yx/u/api/account/modifyPhoneVerifyOld";

    public static void verifySmsCode(Object tag, Long phoneNum, String phYzCode, AbHttpResult abHttpResult) {
        Maps.gen();
        Maps.put("phoneNum", phoneNum);
        Maps.put("phYzCode", phYzCode);
        MM.http.get(tag, VERIFY_SMS_CODE, Maps.get(), abHttpResult);
    }

    public static final String SET_BALANCE_CHANGED_ALERT = "/yx/u/api/account/set-balance-changed-alert";

    public static void setBalanceChangedAlert(Object tag, AbHttpResult abHttpResult) {
        Maps.gen();
        MM.http.get(tag, SET_BALANCE_CHANGED_ALERT, Maps.get(), abHttpResult);
    }

    /**
     * 最高限红
     */
    public static final String MAX_BONUS = "/yx/u/api/game-lottery/search-issue-maxBonus";

    public static void maxBonus(Object tag, int id, AbHttpResult result) {
        Maps.gen();
        Maps.put("id", id);
        MM.http.post(tag, MAX_BONUS, Maps.get(), result);
    }

    public static final String UPDATE_CONTENT_LIST = "/yx/u/api/notice/contentList";

    public static void getUpdateContentList(Object tag, AbHttpResult result) {
        Maps.gen();
        Maps.put("platform", "mobileShow");
        Maps.put("typeCode", "updateContent");
        MM.http.post(tag, UPDATE_CONTENT_LIST, Maps.get(), result);
    }

    public static void getUpdateContentList(Object tag, String typeCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("platform", "mobileShow");
        Maps.put("typeCode", typeCode);
        MM.http.post(tag, UPDATE_CONTENT_LIST, Maps.get(), result);
    }

    public static final String CHECK_JOIN_ACTIVITY = "/yx/api/activity/joinActivity";

    public static void checkJoinActivity(Object tag, String activityId, AbHttpResult result) {
        Maps.gen();
        Maps.put("activityId", activityId);
        MM.http.post(tag, CHECK_JOIN_ACTIVITY, Maps.get(), result);
    }

    public static final String GET_RECOMMEND_INFO = "/yx/api/activity/recommend/getRecommendInfo";

    public static void getRecommendInfo(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.post(tag, GET_RECOMMEND_INFO, Maps.get(), result);
    }

    public static final String GET_RECOMMEND_AWARD_LIST = "/yx/api/activity/recommend/getRecommendAwardList";

    public static void getRecommendAwardList(Object tag,
                                             String startDate,
                                             String endDate,
                                             String startDateRemit,
                                             String endDateRemit,
                                             String awardType,
                                             String state,
                                             long page,
                                             long size,
                                             AbHttpResult result) {
        Maps.gen();
        Maps.put("startDate", startDate);
        Maps.put("endDate", endDate);
        Maps.put("startDateRemit", startDateRemit);
        Maps.put("endDateRemit", endDateRemit);
        Maps.put("awardType", awardType);
        Maps.put("state", state);
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.post(tag, GET_RECOMMEND_AWARD_LIST, Maps.get(), result);
    }

    public static final String GET_SUB_USER_LAST_LOGIN = "/yx/api/activity/recommend/getSubUserLastLogin";

    public static void getSubUserLastLogin(Object tag,
                                           String startDate,
                                           String endDate,
                                           String userName,
                                           long page,
                                           long size,
                                           AbHttpResult result) {
        Maps.gen();
        Maps.put("startDate", startDate);
        Maps.put("endDate", endDate);
        Maps.put("userName", userName);
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.post(tag, GET_SUB_USER_LAST_LOGIN, Maps.get(), result);
    }

    public static final String GET_LOTTERY_PROFIT_LOSS = "/yx/u/api/report/report-lottery-profit-loss";

    public static void getLotteryProfitLoss(Object tag,
                                            String queryDate,
                                            int profitLossType,
                                            AbHttpResult result) {
        Maps.gen();
        Maps.put("queryDate", queryDate);
        Maps.put("profitLossType", profitLossType);
        MM.http.post(tag, GET_LOTTERY_PROFIT_LOSS, Maps.get(), result);
    }

    /**
     * 抽取红包
     * v3.2.2
     * activityId:活动id
     */
    public static final String ATIVITY_CHOU_JIANG = "yx/api/activity/doLotteryDraw";

    public static void getActivityDoChouJiang(Object tag, String activityId, AbHttpResult result) {
        Maps.gen();
        Maps.put("activityId", activityId);
        MM.http.get(tag, ATIVITY_CHOU_JIANG, Maps.get(), result);
    }


    /**
     * 活动结算
     * v3.2.2
     * activityId:活动id
     */
    public static final String ATIVITY_CHECKOUT = "yx/api/activity/getTotalPrizeAmount";

    public static void getActivityCheckout(Object tag, String activityId, AbHttpResult result) {
        Maps.gen();
        Maps.put("activityId", activityId);
        MM.http.get(tag, ATIVITY_CHECKOUT, Maps.get(), result);
    }


    /**
     * 再来一注
     * v3.2.2
     * String billno ：订单号
     */
    public static final String ONCE_MORE_BETTING = "/yx/u/api/game-lottery/zlyz-order";

    public static void onceMoreBetting(Object tag, String orderNo, AbHttpResult result) {
        Maps.gen();
        Maps.put("billno", orderNo);
        MM.http.get(tag, ONCE_MORE_BETTING, Maps.get(), result);
    }


    /**
     * 彩种当期信息接口
     * v3.2.2
     */
    public static final String GET_CAIZHONG_AWARD_TIME = "/yx/u/api/lottery/lottery-timer";

    public static void getCaizhongAwardTimers(Object tag, String gameIds, AbHttpResult result) {
        if (UserManager.getIns().isNotLogined()) {
            return;
        }

        Maps.gen();
        Maps.put("lotterys", gameIds);
        MM.http.get(tag, GET_CAIZHONG_AWARD_TIME, Maps.get(), result);
    }

    /**
     * 彩种期数倒计时接口
     * v3.2.2
     */
    public static final String GET_CAIZHONG_TIMER = "/yx/u/api/lottery/lottery-award-time-interval";

    public static void getCaizhongTimer(Object tag, String gameId, AbHttpResult result) {
        Maps.gen();
        Maps.put("lottery", gameId);
        MM.http.get(tag, GET_CAIZHONG_TIMER, Maps.get(), result);
    }


    /**
     * 获取第三方游戏入库url
     * v3.2.2
     */
    public static final String GET_THIRD_GAME_URL = "/yx/thirdGameApi/common/getLoginUrl";

    public static void getGetThirdGameUrl(Object tag, int platformId, String gameCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("platformId", platformId);
        Maps.put("gameCode", gameCode);
        MM.http.get(tag, GET_THIRD_GAME_URL, Maps.get(), result);
    }


    public static final String FREQUENTLY_USED_LOTTERY = "/yx/u/api/account/setFrequentlyUsedLottery";

    public static void setFrequentlyUsedLottery(Object tag, String infoJson, AbHttpResult result) {
        Maps.gen();
        Maps.put("infoJson", infoJson);
        MM.http.post(tag, FREQUENTLY_USED_LOTTERY, Maps.get(), result);
    }

    public static final String GET_FREQUENTLY_USED_LOTTERY = "/yx/u/api/account/getFrequentlyUsedLottery";

    public static void getFrequentlyUsedLottery(Object tag, AbHttpResult result) {

        Log.d("HttpActionTouCai", "getFrequentlyUsedLottery------" + MM.http.getHostWithoutHttp());

        if (UserManager.getIns().isNotLogined()) {
            return;
        }
        Maps.gen();
        MM.http.get(tag, GET_FREQUENTLY_USED_LOTTERY, Maps.get(), result);
    }

    //v4.0.2新增
    public static final String GET_DESIGNED_HOST = "/upload/json/app4.json";
    public static final String GET_DESIGNED_HOST_SHIWAN = "/upload/json/app.json";//试玩版json

    public static void getDesignedHost(Object tag, AbHttpResult result) {
        Maps.gen();
        //根据渠道不同有所区别
        if (BaseConfig.FLAG_TOUCAI_SHIWAN.equals(Config.custom_flag)) {
            MM.http.get(tag, GET_DESIGNED_HOST_SHIWAN, Maps.get(), result);
        } else {
            MM.http.get(tag, GET_DESIGNED_HOST, Maps.get(), result);
        }
    }

    //v4.1.0新增
    public static final String GET_BONUS_POOL_ACTIVITY_DETAIL = "/yx/api/activity/getBonusPoolActivityDetail";

    public static void getBonusPoolActivityDetail(Object tag, String activityId, AbHttpResult result) {
        Maps.gen();
        Maps.put("activityId", activityId);
        MM.http.get(tag, GET_BONUS_POOL_ACTIVITY_DETAIL, Maps.get(), result);
    }

    //v4.1.0新增
    public static final String GET_BONUS_POOL_CHOUJIANG = "/yx/api/activity/bonusPoolActivityDraw";

    public static void getBonusPoolDoChoujiang(Object tag, String activityId, AbHttpResult result) {
        Maps.gen();
        Maps.put("activityId", activityId);
        MM.http.get(tag, GET_BONUS_POOL_CHOUJIANG, Maps.get(), result);
    }

    //v4.1.0新增
    public static final String GET_BONUS_POOL_CODE_lIST = "/yx/api/activity/activityNumberList";

    public static void getBonusPoolCodeList(Object tag, String activityIssueNo, String activityNumber, String activityId, String status,
                                            String startTime, String endTime, int page, int size, AbHttpResult result) {
        Maps.gen();
        Maps.put("activityIssueNo", activityIssueNo);
        Maps.put("activityNumber", activityNumber);
        Maps.put("activityId", activityId);
        Maps.put("status", status);
        Maps.put("startTime", startTime);
        Maps.put("endTime", endTime);
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.get(tag, GET_BONUS_POOL_CODE_lIST, Maps.get(), result);
    }

    //4.1.3新增

    public static final String GET_CONTACTS_lIST = "/message-push/queryRelativeUserList";

    public static void getcontactsList(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.get(tag, GET_CONTACTS_lIST, Maps.get(), result);
    }

    //4.1.3新增

    public static final String GET_TALK_HISTORY_lIST = "/message-push/getChatMessageList";

    public static void getTalkhistory(Object tag, int startIndex, String inviteCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("startIndex", startIndex);
        Maps.put("uin", inviteCode);
        Maps.put("pageSize", 20);
        MM.http.get(tag, GET_TALK_HISTORY_lIST, Maps.get(), result);
    }

    public static final String GET_LAST_MSG = "/message-push/getLastChatMessageList";

    public static void getLastMessage(Object tag, String inviteCodes, AbHttpResult result) {
        Maps.gen();
        Maps.put("uins", inviteCodes);
        MM.http.get(tag, GET_LAST_MSG, Maps.get(), result);
    }

    public static final String SET_CHATMESSAGE_READ = "/message-push/setChatMessageRead";

    public static void setChatMessageRead(Object tag, String inviteCode, String endMsgId, AbHttpResult result) {
        Maps.gen();
        Maps.put("uin", inviteCode);
        Maps.put("endMsgId", endMsgId);
        MM.http.post(tag, SET_CHATMESSAGE_READ, Maps.get(), result);
    }

    //4.2.0代理需求版本新增---------------------------------------------------------------------------------------

    /**
     * POST /u/api/agent/teamOverview
     * 团队总览接口
     */
    public static final String SET_TEAM_CENTER = "/yx/u/api/agent/teamOverview";

    public static void getTeamCenterData(Object tag, String start, String end, AbHttpResult result) {
        Maps.gen();
        Maps.put("start", start);
        Maps.put("end", end);
        MM.http.post(tag, SET_TEAM_CENTER, Maps.get(), result);
    }

    /**
     * POST /api/activity/getUserSixActivityRule
     * 用户六周年活动参与信息
     */
    public static final String SET_TEAM_TUIGUANG = "/yx/api/activity/getUserSixActivityRule";

    public static void getTeamTuiguangData(Object tag, String activityId, AbHttpResult result) {
        Maps.gen();
        Maps.put("activityId", activityId);
        MM.http.post(tag, SET_TEAM_TUIGUANG, Maps.get(), result);
    }

    /**
     * POST /yx/u/api/agent/list-user-agent-regist-link
     * 获取代理推荐链接
     */
    public static final String SET_TEAM_AGENT_REGIST_LINK = "/yx/u/api/agent/list-user-agent-regist-link";

    public static void getTeamAgentRegistLink(Object tag, AbHttpResult result) {
        Maps.gen();
        MM.http.post(tag, SET_TEAM_AGENT_REGIST_LINK, Maps.get(), result);
    }

    /**
     * 获取代理推荐链接之前
     */
    public static final String SET_TEAM_AGENT_INIT = "/yx/rg/init";

    public static void getTeamAgentRegistLinkInit(Object tag, String link, AbHttpResult result) {
        Maps.gen();
        Maps.put("link", link);
        MM.http.post(tag, SET_TEAM_AGENT_INIT, Maps.get(), result);
    }

    /**
     * 获取代理推荐链接之前
     */
    public static final String TEAM_CENTRE_DETAIL_LIST = "/yx/api/sixZhouNianActivity/getTeamCentreDetailList";

    public static void getTeamCentreDetailList(Object tag, String cn, int page, int size, AbHttpResult result) {
        Maps.gen();
        if (Strs.isNotEmpty(cn)) {
            Maps.put("cn", cn);
        }
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.post(tag, TEAM_CENTRE_DETAIL_LIST, Maps.get(), result);
    }

    /**
     * 获取代理推荐链接之前
     */
    public static final String TEAM_CENTRE_BET_LIST = "/yx/api/sixZhouNianActivity/getTeamBetList";

    public static void getTeamBetList(Object tag, String cn, String createTime, String updateTime, int page, int size, AbHttpResult result) {
        Maps.gen();
        if (Strs.isNotEmpty(cn)) {
            Maps.put("cn", cn);
        }
        Maps.put("createTime", createTime);
        Maps.put("updateTime", updateTime);
        Maps.put("page", page);
        Maps.put("size", size);
        MM.http.post(tag, TEAM_CENTRE_BET_LIST, Maps.get(), result);
    }

    /**
     * POST /api/sixZhouNianActivity/getLotteryOrderByOrderId
     * 投注号码
     */
    public static final String GET_LOTTERY_ORDERBY_ORDERID = "/yx/api/sixZhouNianActivity/getLotteryOrderByOrderId";

    public static void getLotteryOrderByOrderId(Object tag, String orderItemId, AbHttpResult result) {
        Maps.gen();
        Maps.put("orderItemId", orderItemId);
        MM.http.post(tag, GET_LOTTERY_ORDERBY_ORDERID, Maps.get(), result);
    }


    /**
     * 获取pt游戏数据
     * /api/lotterySort/getThirdGameTypeList
     */
    public static final String GET_PT_GAME_DATA = "/yx/api/lotterySort/getThirdGameTypeList";

    public static void getPTgameData(Object tag, int page, int size, String name, String ganmeType, int thirdGamePlatID, AbHttpResult result) {
        Maps.gen();
        Maps.put("page", page);
        Maps.put("size", size);
        Maps.put("name", name);
        Maps.put("ganmeType", ganmeType);
        Maps.put("thirdPlatformId", thirdGamePlatID);
        MM.http.post(tag, GET_PT_GAME_DATA, Maps.get(), result);
    }


    /**
     * 获取虚拟币地址
     */
    private static final String GET_BITCOIN_ADDRESS = "/yx/vc/coin/getAddress";

    public static void getBitCoinAddress(Object tag, String coinCode, AbHttpResult result) {
        Maps.gen();
        Maps.put("userType", 1);
        Maps.put("coinCode", coinCode);
        MM.http.post(tag, GET_BITCOIN_ADDRESS, Maps.get(), result);
    }

}
