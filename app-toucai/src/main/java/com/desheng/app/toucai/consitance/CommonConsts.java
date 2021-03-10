package com.desheng.app.toucai.consitance;


import com.ab.util.Strs;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.shark.tc.R;

public class CommonConsts {

    public static int[] setMainButtomNavSkinUnselect(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return bottomNavigation.mIconUnselectIds;
            case 1://对应新年皮肤
                return bottomNavigationNewyear.mIconUnselectIds;
            default:
                return null;
        }
    }

    public static int[] setMainButtomNavSkinSelect(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return bottomNavigation.mIconSelectIds;
            case 1://对应新年皮肤
                return bottomNavigationNewyear.mIconSelectIds;
            default:
                return null;
        }
    }

    public static int setMainButtomNavIconSize(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return 30;
            case 1://对应新年皮肤
                return 30;
            default:
                return 30;
        }
    }

    public static int setMainButtomNavTextSize(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return 11;
            case 1://对应新年皮肤
                return 11;
            default:
                return 11;
        }
    }

    public static int setActLoadingBg(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.mipmap.bg_loading_1;
            case 1://对应新年皮肤
                return R.mipmap.bg_loading_1;
            default:
                return -1;
        }
    }

    public static int setActLoadingTheme(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.style.Splash;
            case 1://对应新年皮肤
                return R.style.Splash_1;
            default:
                return -1;
        }
    }

    public static Integer[] setActGuideBg(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return guideBg.imageIds;
            case 1://对应新年皮肤
                return guideBg.imageIdsNewYear;
            default:
                return null;
        }
    }

    public static int setPersonalHeadBgRes(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return personalRes.headBgRes;
            case 1://对应新年皮肤
                return personalResNewYear.headBgRes;
            default:
                return -1;
        }
    }

    public static int setPersonalIconRes(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return personalRes.headIconRes;
            case 1://对应新年皮肤
                return personalResNewYear.headIconRes;
            default:
                return -1;
        }
    }

    public static int setPersonalTextColor(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.color.group_bg;
            case 1://对应新年皮肤
                return R.color.deep_red;//text_9D6D52
            default:
                return -1;
        }
    }

    public static int setPersonalTextColor2(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.color.white;
            case 1://对应新年皮肤
                return R.color.text_9D6D52;
            default:
                return -1;
        }
    }

    public static int setPersonalNameBg(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return 0;
            case 1://对应新年皮肤
                return R.drawable.personal_name_bg;
            default:
                return -1;
        }
    }

    public static int setLoginHeadBg(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.mipmap.bg_login_top;
            case 1://对应新年皮肤
                return 0;
            default:
                return -1;
        }
    }

    public static int setLoginScrollBg(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.drawable.shape_login_bg_default;
            case 1://对应新年皮肤
                return R.mipmap.login_bg;
            default:
                return -1;
        }
    }

    public static int setLoginCancelIcon(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.mipmap.ic_circle_close_1;
            case 1://对应新年皮肤
                return R.mipmap.ic_circle_close_1;
            default:
                return -1;
        }
    }

    public static int setLoginUserIocn(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.mipmap.ic_regist_user;
            case 1://对应新年皮肤
                return R.mipmap.ic_regist_user_1;
            default:
                return -1;
        }
    }

    public static int setLoginUserPwd(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.mipmap.ic_regist_pwd;
            case 1://对应新年皮肤
                return R.mipmap.ic_regist_pwd_1;
            default:
                return -1;
        }
    }

    public static int setPhoneLogin(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.mipmap.ic_regist_phone;
            case 1://对应新年皮肤
                return R.mipmap.ic_login_phone_1;
            default:
                return -1;
        }
    }

    public static int setLoginCode(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.mipmap.ic_login_verify;
            case 1://对应新年皮肤
                return R.mipmap.iv_sms_code_1;
            default:
                return -1;
        }
    }


    public static int setLoginBtn(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.drawable.sl_btn_login_red_oval;
            case 1://对应新年皮肤
                return R.drawable.sl_btn_login_red_oval_newyear;
            default:
                return -1;
        }
    }

    public static int setRejistBtn(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.drawable.sh_bd_rec_red_white_oval;
            case 1://对应新年皮肤
                return R.drawable.sh_bd_rec_red_white_oval_1;
            default:
                return -1;
        }
    }

    public static int setRejistTextBtn(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.color.colorPrimary;
            case 1://对应新年皮肤
                return R.color.red_FF6867;
            default:
                return -1;
        }
    }

    public static int setForgotPwdBtn(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                if (Strs.isEqual(BaseConfig.custom_flag, BaseConfig.FLAG_FEIYU)||
                        Strs.isEqual(BaseConfig.custom_flag, BaseConfig.FLAG_JINFENG_2) ) {
                    return R.color.blue_light;
                } else {
                    return R.color.white;
                }
            case 1://对应新年皮肤
                return R.color.white;
            default:
                return -1;
        }
    }

    public static int setChargeBg(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.mipmap.bg_recharge_new;
            case 1://对应新年皮肤
                return R.mipmap.sh_bd_black_white_1;
            default:
                return -1;
        }
    }

    public static int setChargeIconBg(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return 0;
            case 1://对应新年皮肤
                return R.mipmap.bg_czmian_1;
            default:
                return -1;
        }
    }


    public static int setAwadDialogResId(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.layout.dialog_award;
            case 1://对应新年皮肤
                return R.layout.dialog_award_newyear;
            default:
                return -1;
        }
    }

    /**
     * 配置首页--常玩左边图片
     * id: R.id.iv_changwan
     *
     * @param Skinmode
     * @return
     */
    public static int setHomeTabResId(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.mipmap.bg_changwan;
            case 1://对应新年皮肤
                return R.mipmap.home_tap_changwan_new;
            default:
                return -1;
        }
    }

    /**
     * 配置首页--游戏大厅图
     * id: R.id.iv_changwan
     *
     * @param Skinmode
     * @return
     */
    public static int setHomeGameHallResId(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.mipmap.ic_yxdting;
            case 1://对应新年皮肤
                return R.mipmap.home_game_hall;
            default:
                return -1;
        }
    }

    /**
     * 配置首页--优惠活动图
     * id: R.id.iv_changwan
     *
     * @param Skinmode
     * @return
     */
    public static int setHomeyouhuiResId(int Skinmode) {
        switch (Skinmode) {
            case 0://默认皮肤
                return R.mipmap.bg_yhhdong;
            case 1://对应新年皮肤
                return R.mipmap.home_youhui_mission;
            default:
                return -1;
        }
    }


    /**
     * 配置首页--三方游戏logo
     * id: R.id.iv_changwan
     *
     * @param Skinmode
     * @return
     */
    public static int[] setThirdGameLogoResId(int Skinmode) {
        switch (Skinmode) {
            case 1://对应新年皮肤
                return thirdGameLogo.imageIds;
            default:
                return null;
        }
    }


    //普通底部导航
    interface bottomNavigation {
        //未选中
//        int[] mIconUnselectIds = {R.mipmap.ic_tab_home_home_n, R.mipmap.ic_tab_home_withdraw_n, R.mipmap.ic_tab_home_deposit,
//                R.mipmap.ic_tab_home_activity_n, R.mipmap.ic_tab_home_person_n};
        int[] mIconUnselectIds = {R.mipmap.ic_tab_home_home, R.mipmap.ic_tab_home_withdraw_p, R.mipmap.ic_tab_home_deposit,
                R.mipmap.ic_tab_home_activity_p, R.mipmap.ic_tab_home_person_1};

        //选中
        int[] mIconSelectIds = {R.mipmap.ic_tab_home_home_p, R.mipmap.ic_tab_home_withdraw, R.mipmap.ic_tab_home_deposit,
                R.mipmap.ic_tab_home_activity, R.mipmap.ic_tab_home_person};

    }

    //新年版底部导航
    interface bottomNavigationNewyear {
        //未选中
//        int[] mIconUnselectIds = {R.mipmap.ic_tab_home_home_n_1, R.mipmap.ic_tab_home_withdraw_n_1, R.mipmap.ic_tab_home_deposit_n_1,
//                R.mipmap.ic_tab_home_activity_n_1, R.mipmap.ic_tab_home_person_n_1};
        int[] mIconUnselectIds = {R.mipmap.ic_tab_home_home_p_1, R.mipmap.ic_tab_home_withdraw_p_1, R.mipmap.ic_tab_home_deposit_p_1,
                R.mipmap.ic_tab_home_activity_p_1, R.mipmap.ic_tab_home_person_p_1};

        //选中
        int[] mIconSelectIds = {R.mipmap.ic_tab_home_home_p_1, R.mipmap.ic_tab_home_withdraw_p_1, R.mipmap.ic_tab_home_deposit_p_1,
                R.mipmap.ic_tab_home_activity_p_1, R.mipmap.ic_tab_home_person_p_1};

    }


    //新年版引导页
    interface guideBg {
        //普通
        Integer[] imageIds = {
                R.mipmap.welcome_page_1,
                R.mipmap.welcome_page_2,
                R.mipmap.welcome_page_3
        };

        Integer[] imageIdsNewYear = {
                R.mipmap.welcome_page_1_1,
                R.mipmap.welcome_page_2_1,
                R.mipmap.welcome_page_3_1
        };

    }

    //新年版引导页
    interface personalRes {
        //头部背景
        int headBgRes = R.mipmap.myaccount_background;
        //头部icon
        int headIconRes = R.mipmap.ic_default_potrait;
    }

    //新年版引导页
    interface personalResNewYear {
        //头部背景
        int headBgRes = R.mipmap.myaccount_background_1;
        //头部icon
        int headIconRes = R.mipmap.ic_default_potrait_1;
    }


    //新年版引导页
    interface thirdGameLogo {
        //普通
        int[] imageIds = {
                R.mipmap.home_game_ag,
                R.mipmap.home_game_im,
                R.mipmap.home_game_ky,
                R.mipmap.home_game_vr
        };
    }

    /**
     * 首页第三方游戏配置
     *
     * @param config
     * @return
     */
    public static int setHomeThirdGameType(String config) {
        switch (config) {
            case BaseConfig.FLAG_TOUCAI://pt电子
                return 2;
            case BaseConfig.FLAG_TIANMAO://使用cq9
            case BaseConfig.FLAG_HERO://使用cq9
            case BaseConfig.FLAG_FEIYU://使用cq9
            case BaseConfig.FLAG_JINFENG_2://使用cq9
                return 10;
            default:
                return 27;
        }
    }

    public static ThirdGamePlatform setThirdGameScreenOrintation(String config) {
        switch (config) {
            case BaseConfig.FLAG_TOUCAI://pt电子
                return ThirdGamePlatform.PT;
            case BaseConfig.FLAG_TIANMAO://使用cq9
            case BaseConfig.FLAG_HERO://使用cq9
            case BaseConfig.FLAG_FEIYU://使用cq9
            case BaseConfig.FLAG_JINFENG_2://使用cq9
                return ThirdGamePlatform.CQ;
            default:
                return ThirdGamePlatform.PT;
        }
    }

    /**
     * 我的页面 我的联系人 显示配置
     *
     * @param config
     * @return
     */
    public static boolean setContactsConfig(String config) {
        switch (config) {
            case BaseConfig.FLAG_TOUCAI:
            case BaseConfig.FLAG_FEIYU:
            case BaseConfig.FLAG_JINFENG_2:
                return true;
            case BaseConfig.FLAG_TIANMAO:
                return false;
            default:
                return false;
        }
    }

    /**
     * 配置投注页是否需要开奖视频
     *
     * @param code
     * @param isJD
     * @return
     */
    public static boolean setBetNeedMvConfig(String code, boolean isJD) {
        if (isJD) {
            switch (code) {
                case "BJPK10XJW":
                case "XYFTPK10XJW":
                case "AKLPK10XJW":
                case "JS300PK10XJW":
                case "JS180PK10XJW":
                case "XY300PK10XJW":
                    return true;
                default:
                    return false;
            }
        } else {
            switch (code) {
                case "BJPK10":
                case "XYFTPK10":
                case "AKLPK10":
                case "JS300PK10":
                case "JS180PK10":
                case "XY300PK10":
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * 配置投注页是否需要新 UI
     *
     * @param code
     * @return
     */
    public static boolean setBetNeedNewUIConfig(String code) {
        switch (code) {
            case "BJPK10XJW":
            case "XYFTPK10XJW":
            case "AKLPK10XJW":
            case "JS300PK10XJW":
            case "JS180PK10XJW":
            case "XY300PK10XJW":
                return true;
            default:
                return false;
        }
    }

    public static boolean needOpenLotterySite(int lotteryId) {
        switch (lotteryId) {
            case 315:
            case 318:
            case 319:
            case 911:
            case 350:
            case 351:
                return true;
            default:
                return false;
        }
    }

    public static String setOpenLotterySiteConfig(int lotteryId) {
        switch (lotteryId) {
            case 315:
                return Consitances.XY_FFC_TOGUANWANG_URL;
            case 318:
                return Consitances.QU_FFC_TOGUANWANG_URL;
            case 319:
                return Consitances.EOS_FFC_TOGUANWANG_URL;
            case 911:
                return Consitances.TX_FFC_TOGUANWANG_URL;
            case 350:
                return Consitances.STEAM_FFC_TOGUANWANG_URL;
            case 351:
                return Consitances.STEAM_5FC_TOGUANWANG_URL;
            default:
                return null;
        }
    }
}
