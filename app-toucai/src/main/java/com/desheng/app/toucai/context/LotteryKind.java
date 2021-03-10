package com.desheng.app.toucai.context;

import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.context.ILotteryType;
import com.desheng.base.context.OriginLotteryType;
import com.desheng.base.model.LotteryInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lee on 2018/3/5.
 */

public enum LotteryKind implements ILotteryKind {
    ChongQing_SSC(11, "10002", "重庆时时彩", OriginLotteryType.SSC, LotteryType.SSC, "CQSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{CtxLottery.FUNC_ISSUE_BuyPlan, CtxLottery.FUNC_ISSUE_KillNums, CtxLottery.FUNC_ISSUE_LatestIssues, CtxLottery.FUNC_ISSUE_Analysis},
            "lottery_logo_11@3x.png"),

    GuangDong_11S5(24, "10006", "广东11选5", OriginLotteryType.T11S5, LotteryType.T11S5, "GD11Y", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{CtxLottery.FUNC_ISSUE_BuyPlan, CtxLottery.FUNC_ISSUE_KillNums, CtxLottery.FUNC_ISSUE_LatestIssues, CtxLottery.FUNC_ISSUE_Analysis},
            "lottery_logo_24@3x.png"),

    FUCAI_3D(41, "10041", "3D福彩", OriginLotteryType.OTHER, LotteryType.DPC, "3DFC", "3DFC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_41@3x.png"),

    BinCheng_11S5(21, "", "智利11选5", OriginLotteryType.T11S5, LotteryType.T11S5, "ZY11Y", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_21@3x.png"),

    JiangXi_11S5(23, "10015", "江西11选5", OriginLotteryType.T11S5, LotteryType.T11S5, "JX11Y", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{},
            "lottery_logo_23@3x.png"),

    LiaoNing_11S5(25, "10015", "辽宁11选5", OriginLotteryType.T11S5, LotteryType.T11S5, "LN11Y", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{},
            "lottery_logo_25@3x.png"),

    XinJiang_SSC(151, "10004", "新疆时时彩", OriginLotteryType.SSC, LotteryType.SSC, "XJSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{CtxLottery.FUNC_ISSUE_BuyPlan, CtxLottery.FUNC_ISSUE_KillNums, CtxLottery.FUNC_ISSUE_LatestIssues, CtxLottery.FUNC_ISSUE_Analysis},
            "lottery_logo_151@3x.png"),

    ShanDong_11S5(22, "10006", "山东11选5", OriginLotteryType.T11S5, LotteryType.T11S5, "SD11Y", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{CtxLottery.FUNC_ISSUE_BuyPlan, CtxLottery.FUNC_ISSUE_KillNums, CtxLottery.FUNC_ISSUE_LatestIssues, CtxLottery.FUNC_ISSUE_Analysis},
            "lottery_logo_22@3x.png"),

    PAILIE3_PAILIE5(42, "10044", "排列三/排列五", OriginLotteryType.OTHER, LotteryType.DPC, "pl3", "pl3",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_42@3x.png"),

    ShangHai_11S5(26, "10018", "上海11选5", OriginLotteryType.T11S5, LotteryType.T11S5, "SH11Y", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{CtxLottery.FUNC_ISSUE_BuyPlan, CtxLottery.FUNC_ISSUE_KillNums, CtxLottery.FUNC_ISSUE_LatestIssues, CtxLottery.FUNC_ISSUE_Analysis},
            "lottery_logo_26@3x.png"),

    ANHUI_KUAI3(33, "10030", "安徽快3", OriginLotteryType.OTHER, LotteryType.OTHER, "AHK3", "K3",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_33@3x.png"),

    HuBei_KUAI3(35, "10032", "湖北快3", OriginLotteryType.OTHER, LotteryType.OTHER, "HBK3", "K3",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_35@3x.png"),

    NeiMeng_KUAI3(36, "10029", "内蒙快3", OriginLotteryType.OTHER, LotteryType.OTHER, "NMK3", "K3",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_36@3x.png"),

    JiLin_KUAI3(32, "", "吉林快3", OriginLotteryType.OTHER, LotteryType.OTHER, "JLK3", "K3",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_32@3x.png"),

    JiangSu_KUAI3(31, "", "江苏快3", OriginLotteryType.OTHER, LotteryType.OTHER, "JSK3", "K3",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_31@3x.png"),

    GuangXi_KUAI3(34, "", "广西快3", OriginLotteryType.OTHER, LotteryType.OTHER, "GXK3", "K3",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_34@3x.png"),

    TianJin_SSC(161, "10003", "天津时时彩", OriginLotteryType.SSC, LotteryType.SSC, "TJSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{CtxLottery.FUNC_ISSUE_BuyPlan, CtxLottery.FUNC_ISSUE_KillNums, CtxLottery.FUNC_ISSUE_LatestIssues, CtxLottery.FUNC_ISSUE_Analysis},
            "lottery_logo_161@3x.png"),

    HanGuo_15FEN(191, "", "韩国1.5分彩", OriginLotteryType.OTHER, LotteryType.SSC, "HGSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_191@3x.png"),

    BeiJing_PK10(43, "10001", "北京PK10", OriginLotteryType.OTHER, LotteryType.PK10, "BJPK10", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_SquareMisc,
            new int[]{CtxLottery.FUNC_ISSUE_BuyPlan, CtxLottery.FUNC_ISSUE_KillNums, CtxLottery.FUNC_ISSUE_LatestIssues, CtxLottery.FUNC_ISSUE_Analysis},
            "lottery_logo_43@3x.png"),

    BeiJing_SSC(711, "10014", "北京时时彩", OriginLotteryType.SSC, LotteryType.SSC, "BJKL8SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_711@3x.png"),

    DongJing_15FEN(601, "", "东京1.5分彩", OriginLotteryType.OTHER, LotteryType.SSC, "DJSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_601@3x.png"),

    TaiWan_BinGuo(811, "10047", "台湾时时彩", OriginLotteryType.OTHER, LotteryType.SSC, "TWSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_811@3x.png"),
    JiangSU_11S5(28, "10016", "江苏11选5", OriginLotteryType.T11S5, LotteryType.T11S5, "JS11Y", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_SquareMisc,
            new int[]{CtxLottery.FUNC_ISSUE_BuyPlan, CtxLottery.FUNC_ISSUE_KillNums, CtxLottery.FUNC_ISSUE_LatestIssues, CtxLottery.FUNC_ISSUE_Analysis},
            "lottery_logo_28@3x.png"),

    Tencent_FFC(911, "", "腾讯分分彩", OriginLotteryType.OTHER, LotteryType.SSC, "TXSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_911@3x.png"),

    Tencent_30(50, "", "腾讯30秒", OriginLotteryType.OTHER, LotteryType.SSC, "TX30SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_50@3x.png"),

    Tencent_QQ(61, "", "QQ分分彩", OriginLotteryType.OTHER, LotteryType.SSC, "QQ60SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_61@3x.png"),

    Hawaii_FFC(119, "", "腾讯1分彩", OriginLotteryType.OTHER, LotteryType.SSC, "ZYFFSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_119@3x.png"),

    FeiLvBin_1FEN(59, "", "菲律宾1分彩", OriginLotteryType.OTHER, LotteryType.FLB, "FLB1SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_59@3x.png"),

    FeiLvBin_15FEN(200, "", "菲律宾1.5分彩", OriginLotteryType.OTHER, LotteryType.FLB, "FLB15SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_200@3x.png"),

    XinDeLi_15FEN(6, "", "新德里1.5分彩", OriginLotteryType.OTHER, LotteryType.SSC, "TGSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_6@3x.png"),

    FeiLvBin_2FEN(201, "", "菲律宾2分彩", OriginLotteryType.OTHER, LotteryType.FLB, "FLB2SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_201@3x.png"),

    London_2FEN(203, "", "伦敦2分彩", OriginLotteryType.OTHER, LotteryType.SSC, "LD2SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_203@3x.png"),

    XinJiaPo_2FEN(51, "", "新加坡2分彩", OriginLotteryType.OTHER, LotteryType.SSC, "XJPSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_51@3x.png"),

    FeiLvBin_5FEN(202, "", "菲律宾5分彩", OriginLotteryType.OTHER, LotteryType.FLB, "FLB5SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_202@3x.png"),

    AoLanKe_PK10(204, "", "佛罗里达PK10", OriginLotteryType.OTHER, LotteryType.PK10, "AKLPK10", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_204@3x.png"),

    XingYunFeiTing_PK10(47, "", "幸运飞艇", OriginLotteryType.OTHER, LotteryType.PK10, "XYFTPK10", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_47@3x.png"),

    ShouEr_15FEN(205, "", "首尔1.5分彩", OriginLotteryType.OTHER, LotteryType.SSC, "SHOUERSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_205@3x.png"),

    NewYork_15FEN(206, "", "纽约1.5分彩", OriginLotteryType.OTHER, LotteryType.SSC, "NEWYOSSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_206@3x.png"),

    DuoLunDuo_30SEC(46, "", "多伦多30秒", OriginLotteryType.OTHER, LotteryType.SSC, "TG30SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{},
            "lottery_logo_46@3x.png"),

    ChongQing_SSC_JD(80, "", "经典重庆时时彩", OriginLotteryType.JD, LotteryType.JD, "CQSSCXJW", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_80@3x.png"),

    Tencent_FFC_JD(60, "", "经典腾讯分分彩", OriginLotteryType.JD, LotteryType.JD, "TXSSCXJW", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_911@3x.png"),

    FeiLvBin_15Fen_JD(85, "", "经典菲律宾1.5分彩", OriginLotteryType.JD, LotteryType.JD, "FLB15SSCXJW", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_85@3x.png"),
    FeiLvBin_2Fen_JD(86, "", "经典菲律宾2分彩", OriginLotteryType.JD, LotteryType.JD, "FLB2SSCXJW", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_86@3x.png"),
    FeiLvBin_5Fen_JD(87, "", "经典菲律宾5分彩", OriginLotteryType.JD, LotteryType.JD, "FLB5SSCXJW", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_87@3x.png"),
    JiangSu_K3_JD(90, "", "经典江苏快3", OriginLotteryType.JD, LotteryType.JD, "JSK3XJW", "K3",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_90@3x.png"),
    GuangDong_11S5_JD(100, "", "经典广东11选5", OriginLotteryType.JD, LotteryType.JD, "GD11YXJW", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_100@3x.png"),
    BeiJing_PK10_JD(110, "", "经典北京PK10", OriginLotteryType.JD, LotteryType.JD, "BJPK10XJW", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_110@3x.png"),
    DongJing_PK10_JD(70, "", "经典东京1.5分彩", OriginLotteryType.JD, LotteryType.JD, "DJSSCXJW", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_70@3x.png"),
    ShouEr_15Fen_JD(71, "", "经典首尔1.5分彩", OriginLotteryType.JD, LotteryType.JD, "SHOUERSSCXJW", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_71@3x.png"),
    NewYork_15Fen_JD(72, "", "经典纽约1.5分彩", OriginLotteryType.JD, LotteryType.JD, "NEWYOSSCXJW", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_72@3x.png"),
    XiJiang_SSH_JD(82, "", "经典新疆时时彩", OriginLotteryType.JD, LotteryType.JD, "XJSSCXJW", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_82@3x.png"),
    HanGuo_15Fen_JD(88, "", "经典韩国1.5分彩", OriginLotteryType.JD, LotteryType.JD, "HGSSCXJW", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_88@3x.png"),
    ShanDong_11S5_JD(102, "", "经典山东11选5", OriginLotteryType.JD, LotteryType.JD, "SD11YXJW", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_102@3x.png"),
    ShangHai_11S5_JD(103, "", "经典上海11选5", OriginLotteryType.JD, LotteryType.JD, "SH11YXJW", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_103@3x.png"),
    JiangXi_11S5_JD(104, "", "经典江西11选5", OriginLotteryType.JD, LotteryType.JD, "JX11YXJW", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_104@3x.png"),
    JiangSu_11S5_JD(105, "", "经典江苏11选5", OriginLotteryType.JD, LotteryType.JD, "JS11YXJW", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_105@3x.png"),

    LHC(250, "", "香港六合彩", OriginLotteryType.LHC, LotteryType.LHC, "LHECXJW", "LHC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_250@3x.png"),

    Beijing_Kuai3(54, "", "北京快3", OriginLotteryType.OTHER, LotteryType.OTHER, "BEIJINGK3", "K3",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_54@3x.png"),

    Hebei_Kuai3(56, "", "河北快3", OriginLotteryType.OTHER, LotteryType.OTHER, "HEBEIK3", "K3",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_56@3x.png"),

    Hebei_11S5(55, "", "河北11选5", OriginLotteryType.T11S5, LotteryType.T11S5, "HEBEI11Y", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{CtxLottery.FUNC_ISSUE_BuyPlan, CtxLottery.FUNC_ISSUE_KillNums, CtxLottery.FUNC_ISSUE_LatestIssues, CtxLottery.FUNC_ISSUE_Analysis},
            "lottery_logo_55@3x.png"),

    Beijing_11S5(53, "", "北京11选5", OriginLotteryType.T11S5, LotteryType.T11S5, "BEIJING11Y", "11X5",
            CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{CtxLottery.FUNC_ISSUE_BuyPlan, CtxLottery.FUNC_ISSUE_KillNums, CtxLottery.FUNC_ISSUE_LatestIssues, CtxLottery.FUNC_ISSUE_Analysis},
            "lottery_logo_53@3x.png"),


    DEFAULT(0, "", "", OriginLotteryType.OTHER, LotteryType.OTHER, "", "", CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{},
            ""),
    //V2.3增加 相应彩种（缅甸一分彩210、加拿大一分彩211、柬埔寨一分彩212、缅甸2分彩213、菲律宾45秒彩214、柬埔寨45秒彩215、新加坡45秒彩216、缅甸3分彩217、柬埔寨5分彩218）
    Miandian_1fencai(210, "", "缅甸一分彩", OriginLotteryType.SSC, LotteryType.SSC, "MD60SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_210@3x.png"),

    Miandian_2fencai(213, "", "缅甸2分彩", OriginLotteryType.SSC, LotteryType.SSC, "MD120SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_213@3x.png"),

    Miandian_3fencai(217, "", "缅甸3分彩", OriginLotteryType.SSC, LotteryType.SSC, "MD180SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_217@3x.png"),

    Jianpuzhai_1fencai(212, "", "柬埔寨一分彩", OriginLotteryType.SSC, LotteryType.SSC, "JPZ60SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_212@3x.png"),

    Jianpuzhai_5fencai(218, "", "柬埔寨5分彩", OriginLotteryType.SSC, LotteryType.SSC, "JPZ300SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_218@3x.png"),

    Jianpuzhai_45miaocai(215, "", "柬埔寨45秒彩", OriginLotteryType.SSC, LotteryType.SSC, "JPZ45SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_215@3x.png"),

    Feilubin_45miaocai(214, "", "菲律宾45秒彩", OriginLotteryType.SSC, LotteryType.SSC, "FLB45SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_214@3x.png"),

    Singerpor_45miaocai(216, "", "新加坡45秒彩", OriginLotteryType.SSC, LotteryType.SSC, "XJP45SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_216@3x.png"),

    Canada_1fencai(211, "", "加拿大一分彩", OriginLotteryType.SSC, LotteryType.SSC, "CND60SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_211@3x.png"),

    XingYunFeiTing_JD(116, "", "经典幸运飞艇", OriginLotteryType.JD, LotteryType.PK10, "XYFTPK10XJW", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_116@3x.png"),

    JSSC_JD(112, "", "经典极速赛车", OriginLotteryType.JD, LotteryType.PK10, "AKLPK10XJW", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_112@3x.png"),

    JSSC_5FEN(310, "", "极速赛车5分", OriginLotteryType.OTHER, LotteryType.PK10, "JS300PK10", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_310@3x.png"),

    JSSC_5FEN_JD(311, "", "经典极速赛车5分", OriginLotteryType.JD, LotteryType.PK10, "JS300PK10XJW", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_311@3x.png"),

    JSSC_3FEN(312, "", "极速赛车3分", OriginLotteryType.OTHER, LotteryType.PK10, "JS180PK10", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_312@3x.png"),

    JSSC_3FEN_JD(313, "", "经典极速赛车3分", OriginLotteryType.JD, LotteryType.PK10, "JS180PK10XJW", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_313@3x.png"),

    TENCENT_SSC(307, "", "腾讯时时彩", OriginLotteryType.SSC, LotteryType.SSC, "TX600SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_307@3x.png"),

    LUCKY_FFC(315, "", "幸运分分彩", OriginLotteryType.SSC, LotteryType.SSC, "XY60SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_315@3x.png"),

    QIQU_FFC(318, "", "奇趣分分彩", OriginLotteryType.SSC, LotteryType.SSC, "QIQU60SSC", "SSC",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_318@3x.png"),

    XYSC_PK10(330, "", "幸运赛车", OriginLotteryType.OTHER, LotteryType.PK10, "XY300PK10", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_330@3x.png"),

    XYSC_PK10_JD(331, "", "经典幸运赛车", OriginLotteryType.JD, LotteryType.PK10, "XY300PK10XJW", "PK10",
            CtxLottery.TYPE_ISSUE_SHOW_BallMisc, new int[]{}, "lottery_logo_331@3x.png"),

    LaoChongQing_SSC(300, "10002", "老重庆时时彩", OriginLotteryType.SSC, LotteryType.SSC, "LCQSSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallBlue,
            new int[]{CtxLottery.FUNC_ISSUE_BuyPlan, CtxLottery.FUNC_ISSUE_KillNums, CtxLottery.FUNC_ISSUE_LatestIssues, CtxLottery.FUNC_ISSUE_Analysis},
            "lottery_logo_11@3x.png"),

    TengXun_2F_SSC(308, "", "腾讯2分彩", OriginLotteryType.SSC, LotteryType.SSC, "TX120SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{}, "lottery_logo_308@3x.png"),

    TenCent_5FC(301, "", "腾讯5分彩", OriginLotteryType.OTHER, LotteryType.SSC, "TX300SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_301@3x.png"),

    QIQU_3FC(325, "", "奇趣三分彩", OriginLotteryType.SSC, LotteryType.SSC, "QIQU180SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_325@3x.png"),
    QIQU_5FC(326, "", "奇趣五分彩", OriginLotteryType.SSC, LotteryType.SSC, "QIQU300SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_326@3x.png"),
    QIQU_10FC(327, "", "奇趣十分彩", OriginLotteryType.SSC, LotteryType.SSC, "QIQU600SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_327@3x.png"),

    HeiNei_1FC(302, "", "河内分分彩", OriginLotteryType.SSC, LotteryType.SSC, "HENEI60SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_302@3x.png"),
    HeiNei_5FC(303, "", "河内5分彩", OriginLotteryType.SSC, LotteryType.SSC, "HENEI300SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_303@3x.png"),
    EOS_FFC(319, "", "EOS分分彩", OriginLotteryType.SSC, LotteryType.SSC, "EOS60SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{},
            "lottery_logo_319@3x.png"),

    XingYun_3FC(320, "", "幸运3分彩", OriginLotteryType.SSC, LotteryType.SSC, "XY180SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{}, "lottery_logo_320@3x.png"),

    XingYun_5FC(321, "", "幸运5分彩", OriginLotteryType.SSC, LotteryType.SSC, "XY300SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{}, "lottery_logo_321@3x.png"),

    XingYun_SSC(322, "", "幸运时时彩", OriginLotteryType.SSC, LotteryType.SSC, "XY600SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{}, "lottery_logo_322@3x.png"),

    Steam_FFC(350, "", "steam分分彩", OriginLotteryType.SSC, LotteryType.SSC, "ST60SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{}, "lottery_logo_350@3x.png"),

    Steam_5FC(351, "", "steam5分彩", OriginLotteryType.SSC, LotteryType.SSC, "ST300SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{}, "lottery_logo_351@3x.png"),
    Steam_XQQFFC(352, "", "新奇趣分分彩", OriginLotteryType.SSC, LotteryType.SSC, "NQIQU60SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{}, "lottery_logo_352@3x.png"),
    Steam_XQQ3FC(353, "", "新奇趣3分彩", OriginLotteryType.SSC, LotteryType.SSC, "NQIQU180SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{}, "lottery_logo_353@3x.png"),
    Steam_XQQ5FC(354, "", "新奇趣5分彩", OriginLotteryType.SSC, LotteryType.SSC, "NQIQU300SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{}, "lottery_logo_354@3x.png"),
    Steam_XQQSSC(355, "", "新奇趣时时彩", OriginLotteryType.SSC, LotteryType.SSC, "NQIQU600SSC", "SSC", CtxLottery.TYPE_ISSUE_SHOW_BallMisc,
            new int[]{}, "lottery_logo_355@3x.png");

    /**
     * 我平台id
     */
    private int id = 0;
    /**
     * 168开奖网id
     */
    private String id168 = "";
    /**
     * 名称
     */
    private String showName = "";
    /**
     * 原始类别, 依彩宏标准, 除玩法外其他功能使用如 购彩计划, 走势图, 稳赢杀号等.
     * 区别各flavor的界面配置的显示分类,
     * 以及菜单玩法配置中依真实彩票玩法判断做的分类.
     */
    private ILotteryType originType = OriginLotteryType.OTHER;

    /**
     * 显示类别, 依彩宏标准, 除玩法外其他功能使用如 购彩计划, 走势图, 稳赢杀号等.
     * 区别各flavor的界面配置的显示分类,
     * 以及菜单玩法配置中依真实彩票玩法判断做的分类.
     */
    private ILotteryType showType = LotteryType.OTHER;

    /**
     * 简写
     */
    private String code = "";
    /**
     * 玩法分类, 依分类获取玩法菜单
     */
    private final String playCategory;
    /**
     * 开奖球样式
     */
    private int issueShowType = 0;
    /**
     * 开奖附加功能
     */
    private int[] arrIssueShowFunc = new int[10];
    /**
     * 本地对应图标
     */
    private String icon = "";

    /**
     * 是否启用
     */
    private boolean enabled;

    private static HashMap<Integer, LotteryKind> mMapIds;
    private static HashMap<String, LotteryKind> mMapFullNames;

    LotteryKind(int id, String id168, String showName, ILotteryType originType, ILotteryType showType, String code, String playCategory, int issueShowType, int[] arrShowFunc, String icon) {
        this.id = id;
        this.id168 = id168;
        this.showName = showName;
        this.originType = originType;
        this.showType = showType;
        this.code = code;
        this.playCategory = playCategory;
        this.issueShowType = issueShowType;
        this.arrIssueShowFunc = arrShowFunc;
        this.icon = icon;
    }

    public static ILotteryKind find(int id) {
        if (mMapIds == null) {
            mMapIds = new HashMap<>();
            for (int i = 0; i < LotteryKind.values().length; i++) {
                mMapIds.put(LotteryKind.values()[i].getId(), LotteryKind.values()[i]);
            }
        }
        LotteryKind kind = mMapIds.get(id);
        if (kind == null) {
            kind = DEFAULT;
        }
        return kind;
    }

    public static ILotteryKind find(String fullName) {
        if (mMapFullNames == null) {
            mMapFullNames = new HashMap<>();
            for (int i = 0; i < LotteryKind.values().length; i++) {
                mMapFullNames.put(LotteryKind.values()[i].getShowName(), LotteryKind.values()[i]);
            }
        }
        LotteryKind kind = mMapFullNames.get(fullName);
        if (kind == null) {
            kind = DEFAULT;
        }
        return kind;
    }

    /***
     * 获取栏目的lotterykind列表
     * @param showTypeCode
     * @return
     */
    public static ArrayList<ILotteryKind> getKindsOfShowType(String showTypeCode) {
        ArrayList<ILotteryKind> list = new ArrayList<ILotteryKind>();
        for (int i = 0; i < LotteryKind.values().length; i++) {
            if (LotteryKind.values()[i].getShowType().getCode().toLowerCase().equals(showTypeCode.toLowerCase())) {
                list.add(LotteryKind.values()[i]);
            }
        }
        return list;
    }

    /**
     * 获取栏目的lotteryInfo列表
     *
     * @param showTypeCode
     * @return
     */
    public static ArrayList<LotteryInfo> getInfosOfShowType(String showTypeCode) {
        ArrayList<LotteryInfo> list = new ArrayList<LotteryInfo>();
        for (int i = 0; i < LotteryKind.values().length; i++) {
            if (LotteryKind.values()[i].getShowType().getCode().toLowerCase().equals(showTypeCode.toLowerCase())) {
                list.add(LotteryKind.values()[i].getLotteryInfo());
            }
        }
        return list;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getId168() {
        return id168;
    }

    @Override
    public void setId168(String id168) {
        this.id168 = id168;
    }

    @Override
    public String getShowName() {
        return showName;
    }

    @Override
    public void setShowName(String showName) {
        this.showName = showName;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 获取生成的lettery info
     *
     * @return
     */
    @Override
    public LotteryInfo getLotteryInfo() {
        LotteryInfo info = new LotteryInfo(id, code, showName, getIconActive());
        return info;
    }

    @Override
    public void updateAndEnable(LotteryInfo info) {
        showName = info.getShowName();
        enabled = true;
    }

    public ILotteryType getShowType() {
        return showType;
    }

    public void setShowType(ILotteryType showType) {
        this.showType = showType;
    }

    @Override
    public String getPlayCategory() {
        return playCategory;
    }

    @Override
    public int getIssueShowType() {
        return issueShowType;
    }

    @Override
    public void setIssueShowType(int issueShowType) {
        this.issueShowType = issueShowType;
    }

    @Override
    public int[] getArrIssueShowFunc() {
        return arrIssueShowFunc;
    }

    @Override
    public void setArrIssueShowFunc(int[] arrIssueShowFunc) {
        this.arrIssueShowFunc = arrIssueShowFunc;
    }

    @Override
    public ILotteryType getOriginType() {
        return originType;
    }

    @Override
    public void setOriginType(ILotteryType originType) {
        this.originType = originType;
    }

    @Override
    public String getIcon() {
        return "file:///android_asset/lottery_kind/active/" + icon;
    }

    @Override
    public String getIconActive() {
        return "file:///android_asset/lottery_kind/active/" + icon;
    }

    @Override
    public String getIconInactive() {
        return "file:///android_asset/lottery_kind/inactive/" + icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }
}
