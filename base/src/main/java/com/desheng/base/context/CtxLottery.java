package com.desheng.base.context;

import com.ab.global.ENV;
import com.ab.util.Strs;
import com.desheng.base.model.LotteryPlayUserInfo;

/**
 * Created by lee on 2018/4/10.
 */
public abstract class CtxLottery {
    public static final String PWD_MOCK = "*******";
    public static final int BET_NUM_ERROE = -1;

    public static final int TYPE_ISSUE_SHOW_BallBlue = 1;
    public static final int TYPE_ISSUE_SHOW_BallMisc = 2;
    public static final int TYPE_ISSUE_SHOW_SquareMisc = 3;

    public static final int FUNC_ISSUE_BuyPlan = 1;
    public static final int FUNC_ISSUE_KillNums = 2;
    public static final int FUNC_ISSUE_LatestIssues = 3;
    public static final int FUNC_ISSUE_Analysis = 4;

    public static final int ShowType_Digis = 1; //数字
    public static final int ShowType_DetailShowTypeWri = 2; //写入
    public static final int ShowType_DetailShowTypeWriAndNum = 3; //选位 写入
    public static final int ShowType_DetailShowTypeBitAndNum = 4; //选位 选号
    public static final int ShowType_DetailShowTypeLongHU = 5; //龙虎和
    public static final int ShowType_Digis_K3 = 6; //快三
    public static final int ShowType_DetailShowTypeLongHU_AllInOne = 7; //龙虎和
    public static final int ShowType_DetailShowTypeDaXiaoDanShuang_AllInOne = 8; //大小单双

    public static final int ShowType_DualDigits_JD = 1; //方形
    public static final int ShowType_Digits_JD = 2; //数字
    public static final int ShowType_Board_JD = 3; //龙虎和
    public static final int ShowType_SMALL_Digits_JD = 4; //小字号数字
    public static final int ShowType_NIUNIU_JD = 5; //牛牛

    public static final int ShowType_DualDigits_LHC = 1; //方形
    public static final int ShowType_Digits_LHC = 2; //数字
    public static final int ShowType_Board_LHC = 3; //龙虎和


    public static final String POWER_WAN = "万位";
    public static final String POWER_QIAN = "千位";
    public static final String POWER_BAI = "百位";
    public static final String POWER_SHI = "十位";
    public static final String POWER_GE = "个位";

    public static final String MAIN_POCKET_CBID = "sobet_01";
    public static final String MAIN_POCKET_NAME = "主账户";

    public static CtxLottery ins;

    public static void setIns(CtxLottery ctx) {
        ins = ctx;
    }

    public static CtxLottery getIns() {
        return ins;
    }


    public static class Order {
        public static final String LOTTERY = "lottery";
        public static final String ISSUE = "issue";
        public static final String METHOD = "method";
        public static final String CONTENT = "content";
        public static final String MODEL = "model";
        public static final String MULTIPLE = "multiple";
        public static final String CODE = "code";
        public static final String MONEY = "money";
        public static final String PLAY_NAME = "play_name";
        public static final String HIT = "hit";
        public static final String COMPRESS = "compress";
        public static final String AWARD = "award";
    }

    public static class Chase {
        public static final String LOTTERY = "lottery";
        public static final String METHOD = "method";
        public static final String CONTENT = "content";
        public static final String MODEL = "model";
        public static final String CODE = "code";
        public static final String COMPRESS = "compress";
        public static final String MULTIPLE = "multiple";
        public static final String ISSUE = "issue";

        public static final String IS_AVAILABLE = "is_available";

        public static final String INTEVAL = "inteval";
        public static final String SIGN = "sign";
        public static final String MULTI = "multi";
        public static final String AWARD = "award";

    }


    //NORMAL(0, "未开奖"), NON_WIN(1, "未中奖"), AWARDED(2, "已派奖"), WAIT_AWARD(3, "等待派奖"), CANCELED(4, "个人撤单"), CANCELD_SYSTEM(
    //5, "系统撤单"), ORDER_REFUND(6, "已退款"), WIN(7, "已中奖"), UNKNOWN(8, "异常状态");
    public static String formatUserBetsStatus(int status) {
        return BetStatus.find(status).getDesc();
    }

    public static int getUserBetsStatus(String status) {
        return BetStatus.find(status).getCode();
    }


    public static String formatUserWithdrawalsStatus(int status) {
		/*if(status == 0) {
			return "待处理";
		}
		if(status == 1) {
			return "已完成";
		}
		if(status == -1) {
			return "已拒绝";
		}*/

        if (status == 0) {
            return "成功";
        }
        if (status == 6 || status == 7 || status == 2) {
            return "失败";
        }
        //  if(status != 0 && status != 6 && status != 7){
        return "待处理";
    }

    public static String formatUserRechargeType(int type) {
        if (type == 0) {
            return "网银充值";
        }
        if (type == 1) {
            return "转账汇款";
        }
        if (type == 3) {
            return "系统充值";
        }
        if (type == 4) {
            return "微信充值";
        }
        return "";
    }

    public static String formatUserRechargeStatus(int type) {
        if (type == 0) {
            return "成功";
        }
        if (type == 6 || type == 7) {
            return "失败";
        }
        if (type != 0 && type != 6 && type != 7) {
            return "待处理";
        }
        return "";
    }

    public abstract ILotteryKind findLotteryKind(int id);

    public abstract ILotteryKind lotteryKind(String value);

    public abstract ILotteryType findLotteryType(String code);

    public abstract ILotteryType lotteryType(String value);

    public ILotteryType originLotteryType(String value) {
        return OriginLotteryType.valueOf(value);
    }

    /*
    剔除html标签
    &lt;span style=&quot;background-color: rgb(255, 156, 0); font-weight: bold; text-decoration-line: underline; font-style: italic;&quot;&gt;测试公告，请注意样式&lt;/span&gt;
     */
    public static String replaceHtmlSign(String ori) {
        String content = ori;

        if (Strs.isEmpty(ori))
            return "";

        content = content.replace("&lt;", "<");
        content = content.replace("&amp;nbsp;", "");
        content = content.replace("&amp;", "&");
        content = content.replace("&nbsp;", "");
        content = content.replace("&quot;", "\"");
        content = content.replace("&gt;", ">");
        content = content.replace("<br>", "\n");
        content = content.replace("<br/>", "\n");
        content = content.replaceAll("<[^>]*>", "");
        return content;
    }

    public static String transHtmlFrag(String ori) {
        String content = ori;
        content = content.replace("&lt;", "<");
        content = content.replace("&amp;nbsp;", "&nbsp;");
        content = content.replace("&quot;", "\"");
        content = content.replace("&gt;", ">");
        String wrapper = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "            <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "                <meta content=\"width=device-width, height=device-height, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\" name=\"viewport\">\n" +
                "                    <title></title>\n" +
                "                    <link rel=\"shortcut icon\" href=\"/dist/favicon.ico\" type=\"image/x-icon\">\n" +
                "                        </head>\n" +
                "    <body>\n" +
                "\n" +
                "        \n" +
                "        {{body}}\n" +
                "        \n" +
                "        \n" +
                "        \n" +
                "        \n" +
                "        \n" +
                "        \n" +
                "    </body>\n" +
                "</html>";
        return wrapper.replace("{{body}}", content);
    }

    public static String getSafeUrl(String url) {
        if (!url.startsWith("http")) {
            return ENV.curr.host + url;
        } else {
            return url;
        }
    }

    public static String formatUserType(int type) {
        if (type >= 1) {
            return "代理";
        }
        return "会员";
    }

    /**
     * 获取彩种玩法的bean
     *
     * @param category     彩种分类
     * @param lotteryCode  彩种
     * @param userPlayInfo 用户配置
     * @return 彩种玩法bean
     */
    public static LotteryPlayUserInfo.MathodBean getMethodBean(String category, String tittleName, String lotteryCode, LotteryPlayUserInfo userPlayInfo) {
        LotteryPlayUserInfo.MathodBean methodBean;
        String lcode = lotteryCode;
        if (category.equals("SSC")) {
            if (Strs.isEqual(tittleName, "龙虎")) {
                boolean containsLH = userPlayInfo.getMethod().containsKey("lhwq");//保证后台配置了龙虎玩法
                if (Strs.isEqual(lcode, "allInOne_longhu") && containsLH) {
                    /*判断是否有新龙虎的内容*/
                    LotteryPlayUserInfo.MathodBean bean = new LotteryPlayUserInfo.MathodBean();
                    bean.setName(tittleName);
                    bean.setLonghuType(1);
                    methodBean = bean;
                } else {
                    return null;
                }
            } else if (Strs.isEqual(tittleName, "新龙虎")) {
                boolean containsXLH = userPlayInfo.getMethod().containsKey("1v2gflonghuhe_he");//保证后台配置了新龙虎玩法
                if (Strs.isEqual(lcode, "allInOne_xinlonghu") && containsXLH) {
                    LotteryPlayUserInfo.MathodBean bean = new LotteryPlayUserInfo.MathodBean();
                    bean.setName(tittleName);
                    bean.setLonghuType(2);
                    methodBean = bean;
                } else {
                    return null;
                }
            } else {
                methodBean = userPlayInfo.getMethod().get(lcode);
            }
        } else {
            methodBean = userPlayInfo.getMethod().get(lcode);
        }
        return methodBean;
    }
}
