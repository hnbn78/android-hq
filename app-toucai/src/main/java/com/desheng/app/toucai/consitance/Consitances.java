package com.desheng.app.toucai.consitance;

import com.ab.global.ENV;
import com.desheng.app.toucai.panel.ActAgentDividentManagent;
import com.desheng.app.toucai.panel.ActAgentMemberManage;
import com.desheng.app.toucai.panel.ActAgentOpenAccountCenter;
import com.desheng.app.toucai.panel.ActAgentTeamAccountChangeRecord;
import com.desheng.app.toucai.panel.ActAgentTeamLotteryCenter;
import com.desheng.app.toucai.panel.ActAgentTeamReport;
import com.desheng.app.toucai.panel.ActAgentThirdTeamReport;
import com.desheng.app.toucai.panel.ActBankCardListTouCai;
import com.desheng.app.toucai.panel.ActBetRecord;
import com.desheng.app.toucai.panel.ActChaseRecord;
import com.desheng.app.toucai.panel.ActContacts;
import com.desheng.app.toucai.panel.ActDayDivident;
import com.desheng.app.toucai.panel.ActFundRecord;
import com.desheng.app.toucai.panel.ActLotteryReport;
import com.desheng.app.toucai.panel.ActOnlineMember;
import com.desheng.app.toucai.panel.ActPersonalAccountChangeRecord;
import com.desheng.app.toucai.panel.ActSalaryMenage;
import com.desheng.app.toucai.panel.ActSettingNew;
import com.desheng.app.toucai.panel.ActTeamOverlook;
import com.desheng.app.toucai.panel.ActThirdGameRecordNew;
import com.desheng.base.manager.FloatWindowManager;
import com.desheng.base.panel.ActMessageList;
import com.shark.tc.R;

public interface Consitances {

    //(APP) 13投注页 12我的 11彩厅 10活动 9充值 8手机首页
    interface AdvertisementSet {
        int TABHOME = 8;
        int TABPERSONNEW = 12;
        int TABDEPOSIT = 9;
        int TOUZHU = 13;
        int TABLOBBY = 11;
        int TABACTIVITY = 10;
    }

    interface HttpRequestUrl {
        //红包雨
        String redPacketRa = ENV.curr.host + "/yx/home?redEnvelope=1";
    }

    String RECOMMEND_LINK = "/registerLogin/index.html#/register/";

    String PK10_ANIMATE = ENV.curr.host + "/static/pk10/index.html?id=";

    String XY_FFC_TOGUANWANG_URL = "http://xyc.77qqtj.com/";//
    String QU_FFC_TOGUANWANG_URL = "http://qiqu.77txtj.com/";//
    String EOS_FFC_TOGUANWANG_URL = "http://sc.eoslottery188.com/";//
    String TX_FFC_TOGUANWANG_URL = "http://77txtj.com/";//
    String STEAM_FFC_TOGUANWANG_URL = "http://steamff.org/";//
    String STEAM_5FC_TOGUANWANG_URL = "http://steamtj.org/";//

    interface contentManager {
        String BG_ACT_LAUNCHER = "bg_act_launcher";
        String BG_ACT_GUIDE = "bg_act_guide";
        String BG_LOGIN = "bg_login";
        String BG_REGISTER = "bg_register";
        String BG_RECHARGE = "bg_recharge";
        String TEANM_SHARE_LINK = "go-six";//团队推广复制链接
    }

    interface ImassageType {
        int MASSAGE_TYPE_DAJIANG = 3;
        int MASSAGE_TYPE_BONUS = 5;
    }

    Integer[] nums = {R.mipmap.num_0,
            R.mipmap.num_1,
            R.mipmap.num_2,
            R.mipmap.num_3,
            R.mipmap.num_4,
            R.mipmap.num_5,
            R.mipmap.num_6,
            R.mipmap.num_7,
            R.mipmap.num_8,
            R.mipmap.num_9,
    };

    Integer[] numsText = {R.string.text_0,
            R.string.text_1,
            R.string.text_2,
            R.string.text_3,
            R.string.text_4,
            R.string.text_5,
            R.string.text_6,
            R.string.text_7,
            R.string.text_8,
            R.string.text_9,
    };

    interface ImissionTypeConfig {
        //奖金池活动
        int AWARD_POOL = 24;
        //团队推广中心
        int TEAM_GUIGUANG = 70;
    }

    String[] IbonusLevelArray = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};


    /**
     * 0x001-接受消息  0x002-发送消息
     **/
    int CHAT_ITEM_TYPE_LEFT = 0x001;
    int CHAT_ITEM_TYPE_RIGHT = 0x002;
    /**
     * 0x003-发送中  0x004-发送失败  0x005-发送成功
     **/
    int CHAT_ITEM_SENDING = 0x003;
    int CHAT_ITEM_SEND_ERROR = 0x004;
    int CHAT_ITEM_SEND_SUCCESS = 0x005;

    Integer[] PT_GAME_ITEM_BG = {
            R.mipmap.pt_game_item_bg_yellow,
            R.mipmap.pt_game_item_bg_red,
            R.mipmap.pt_game_item_bg_blue,
            R.mipmap.pt_game_item_bg_red,
            R.mipmap.pt_game_item_bg_red,
            R.mipmap.pt_game_item_bg_blue,
            R.mipmap.pt_game_item_bg_yellow,
    };


    Class[] HOME_INTEND_CLASSES = {
            ActTeamOverlook.class,
            ActAgentOpenAccountCenter.class,
            ActAgentTeamReport.class,
            ActAgentMemberManage.class,
            ActAgentTeamAccountChangeRecord.class,
            ActAgentTeamLotteryCenter.class,
            ActAgentThirdTeamReport.class,
            ActOnlineMember.class,
            ActAgentDividentManagent.class,
            ActSalaryMenage.class,
            ActDayDivident.class
    };

    Class[] HOME_MY_ACCOUNT_CLASSES = {
            ActBetRecord.class,
            ActChaseRecord.class,
            ActBankCardListTouCai.class,
            ActLotteryReport.class,
            ActPersonalAccountChangeRecord.class,
            ActThirdGameRecordNew.class,
            ActFundRecord.class,
            ActMessageList.class,
            ActSettingNew.class,
            FloatWindowManager.class,
            ActContacts.class
    };

    Class[] HOME_MY_ACCOUNT_CLASSES_NO_IM = {
            ActBetRecord.class,
            ActChaseRecord.class,
            ActBankCardListTouCai.class,
            ActLotteryReport.class,
            ActPersonalAccountChangeRecord.class,
            ActThirdGameRecordNew.class,
            ActFundRecord.class,
            ActMessageList.class,
            ActSettingNew.class,
            FloatWindowManager.class,
    };
}
