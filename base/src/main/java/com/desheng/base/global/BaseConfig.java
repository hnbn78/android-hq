package com.desheng.base.global;

import com.ab.global.Config;
import com.desheng.base.manager.UserManager;

/**
 * Created by lee on 2018/4/30.
 */

public class BaseConfig extends Config {

    public static final String FLAG_CAIHONG = "flag_caihong";
    public static final String FLAG_JINYANG = "flag_jinyang";
    public static final String FLAG_QINGFENG = "flag_qingfeng";
    public static final String FLAG_LONGTENG = "flag_longteng";
    public static final String FLAG_LEYOU = "flag_leyou";
    public static final String FLAG_WANSHANG = "flag_wanshang";
    public static final String FLAG_HUIXIN_ONLINE = "flag_huixin_online";
    public static final String FLAG_ZHONGXIN = FLAG_HUIXIN_ONLINE;
    public static final String FLAG_HETIANXIA = "flag_hetianxia";
    public static final String FLAG_CAISHIJI = "flag_caishiji";
    public static final String FLAG_YIHAO = "flag_yihao";
    public static final String FLAG_CAIYING = "flag_caiying";
    public static final String FLAG_MENGXIANG = "flag_mengxiang";
    public static final String FLAG_JINDU = "flag_jindu";
    public static final String FLAG_LETIANTANG = "flag_letiantang";

    public static final String FLAG_TOUCAI = "flag_toucai";
    public static final String FLAG_TOUCAI_DEMO = "flag_toucai_demo";
    public static final String FLAG_TOUCAI_CHANNEL = "flag_toucai_channel";
    public static final String FLAG_DREAM_ONE = "flag_dream_one";
    public static final String FLAG_DREAM_TWO = "flag_dream_two";
    public static final String FLAG_QIANHUI = "flag_qianhui";
    public static final String FLAG_UNION = "flag_union";
    public static final String FLAG_TOUCAI_SHIWAN = "flag_shiwan";

    //头彩新开代理
    public static final String FLAG_TIANMAO = "flag_bat";
    public static final String FLAG_HERO = "flag_hero";
    public static final String FLAG_FEIYU = "flag_feiyu";
    public static final String FLAG_JINFENG_2 = "flag_jinfeng2";
    public static final String FLAG_JINFENG_3 = "flag_jinfeng3";



    //客服连接配置, 默认值, 后为app.json中的值
    public static String CustomServiceLink;

    //强制服务器, 有值即使用, 仅用于调试
    public static String ForceHost;

    //是否启用推送
    public static boolean isUsePush;

    //是否使用网络banner数据
    public static boolean isNetBanner;

    //是否默认开启悬浮按钮
    public static boolean isFloatBtnEnabled = false;

    //层级列表
    public static Level[] Levels;

    //组织功能
    public static Organization Org;

    //加密秘钥 (只需配置加密秘钥)
    public static String AES_KEY;

    //是否开启aes加密 登录保护
    public static boolean USE_AES_LOGIN = false;

    //登录保护加密秘钥 (只需配置加密秘钥)
    public static String AES_LOGIN_KEY;

    //是否开启aes加密 开户保护
    public static boolean USE_AES_OPEN_ACCOUNT = false;

    //开户保护加密秘钥 (只需配置加密秘钥)
    public static String AES_OPEN_ACCOUNT_KEY;

    //是否开启聊天
    public static boolean OPEN_IM = false;

    /**
     * 组织功能
     */
    public static abstract class Organization {
        /**
         * 是否可投注, 重写起作用
         */
        public boolean canBet(long level) {
            return true;
        }

        ;


        /**
         * 是否可玩第三方游戏, 重写起作用
         */
        public boolean canPlayGame(long level) {
            return true;
        }

        ;

        /**
         * 是否可开用户
         *
         * @param level
         * @return
         */
        public boolean canOpenUser(long level) {
            Level bean = getLevel(level);
            if (bean != null) {
                return bean.canOpenUser;
            }
            if (UserManager.getIns().isAgent()) {
                return true;
            }
            return false;
        }

        /**
         * 是否可开代理
         *
         * @param level
         * @return
         */
        public boolean canOpenAgent(long level) {
            Level bean = getLevel(level);
            if (bean != null) {
                return bean.canOpenAgent;
            }
            if (UserManager.getIns().isAgent()) {
                return true;
            }
            return false;
        }

        /**
         * 是否可链接开户
         *
         * @param level
         * @return
         */
        public boolean canOpenLink(long level) {
            Level bean = getLevel(level);
            if (bean != null) {
                return bean.canOpenLink;
            }
            if (UserManager.getIns().isAgent()) {
                return true;
            }
            return false;
        }


        public Level getLevel(long level) {
            Level bean = null;
            for (int i = 0; i < Levels.length; i++) {
                if (level == Levels[i].level) {
                    bean = Levels[i];
                    break;
                }
            }
            return bean;
        }
    }

    /**
     * 层级配置
     */
    public static class Level {
        public String name = "";
        public long level = -1;


        /**
         * 是否可开用户
         */
        public boolean canOpenUser = false;

        /**
         * 是否可开代理
         */
        public boolean canOpenAgent = false;

        /**
         * 是否可有开户连接
         */
        public boolean canOpenLink = false;

        /**
         * 是否可投注
         */
        public boolean canBet = false;

        /**
         * 是否可玩第三方游戏
         */
        public boolean canPlayGame = false;

        public Level(String name, long level, boolean canOpenUser, boolean canOpenAgent, boolean canOpenLink, boolean canBet, boolean canPlayGame) {
            this.name = name;
            this.level = level;
            this.canOpenUser = canOpenUser;
            this.canOpenAgent = canOpenAgent;
            this.canOpenLink = canOpenLink;
            this.canBet = canBet;
            this.canPlayGame = canPlayGame;
        }
    }
}
