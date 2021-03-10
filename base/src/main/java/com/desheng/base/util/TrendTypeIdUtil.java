package com.desheng.base.util;

//获取趋势图具体类型
public class TrendTypeIdUtil {
//    { "11,151,119,42,161,191,51,601,711,811,911,46,200,201,202,203,6,205,206", "31,32,33,35,36", "24,21,23,22,26,28", "43,204,47", "41", "" };

    public static final int TYPE_SSC = 1;
    public static final int TYPE_KUAISAN = 2;
    public static final int TYPE_11X5 = 3;
    public static final int TYPE_PK10 = 4;
    public static final int TYPE_3D = 5;

    public static final String TYPE_SSC_STR = "type_ssc_str";
    public static final String TYPE_KUAI_SAN_STR = "type_kuai_san_str";
    public static final String TYPE_11XUAN_5_STR = "type_11xuan_5_str";
    public static final String TYPE_PK10_STR = "type_pk10_str";
    public static final String TYPE_3D_STR = "type_3d_str";

    public static final String TYPE_DRAW_LHH_SSC = "type_draw_lhh_ssc";
    public static final String TYPE_DRAW_SUM_DXDS_SSC = "type_draw_sum_dxds_ssc";
    public static final String TYPE_DRAW_SUM_DXDS_KL8 = "type_draw_sum_dxds_kl8";
    public static final String TYPE_DRAW_SINGLE_TREND = "type_draw_single_trend";
    public static final String TYPE_DRAW_CODE_DX_11X5 = "type_draw_code_dx_11x5";
    public static final String TYPE_DRAW_SUM_SAME_CODE_3D = "type_draw_sum_same_code_3d";
    public static final String TYPE_DRAW_SUM_FIVE_KL18 = "type_draw_sum_five_kl8";
    public static final String TYPE_DRAW_SUM_SAME_CODE = "type_draw_sum_same_code";
    public static final String TYPE_DRAW_SUM_SAME_SSC = "type_draw_sum_ssc";
    public static final String TYPE_DRAW_CODE_COUNT_JOSX_KL8 = "type_draw_code_count_josx_kl8";
    public static final String TYPE_DRAW_MULTIPLE_TREND = "type_draw_multiple_trend";
    public static final String TYPE_DRAW_DIFF_SSC = "type_draw_diff_ssc";
    public static final String TYPE_DRAW_CODE_DX = "type_draw_code_dx";
    public static final String TYPE_DRAW_CODE_DS = "type_draw_code_ds";

    public static int getTrendTypeId(int lotteryId) {
        int trendType = -1;
        switch (lotteryId) {
//     时时彩       "11,151,119,42,161,191,51,601,711,811,911,46,200,201,202,203,6,205,206"    其中80  85 86 87为经典系类
            case 6:
            case 11:
            case 42:
            case 46:
            case 51:
            case 61:
            case 80:
            case 85:
            case 86:
            case 87:
            case 119:
            case 151:
            case 161:
            case 191:
            case 200:
            case 201:
            case 202:
            case 203:
            case 205:
            case 206:
            case 210:
            case 211:
            case 212:
            case 213:
            case 214:
            case 215:
            case 216:
            case 217:
            case 218:
            case 301:
            case 601:
            case 711:
            case 811:
            case 911:
            case 300:

            case 302:
            case 303:
            case 304:
            case 305:
            case 307:
            case 308:
            case 309:
            case 315:
            case 318:
            case 319:

            case 320:
            case 321:
            case 322:
            case 325:
            case 326:
            case 327:

            case 332:
            case 333:
            case 334:
            case 335:
            case 336:
            case 350:
            case 351:
            case 352:
            case 353:
            case 354:
            case 355:

                trendType = TYPE_SSC;
                break;
//      快三      "31,32,33,35,36"     34 90为经典
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 90:
                trendType = TYPE_KUAISAN;
                break;
//    11选5        "24,21,23,22,26,28"   25,100为经典
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 28:
            case 100:
                trendType = TYPE_11X5;
                break;
//     pk10       "43,204,47"
            case 43:
            case 47:
            case 110:
            case 204:
            case 306:
                trendType = TYPE_PK10;
                break;
//     3D福彩           "41"
            case 41:
                trendType = TYPE_3D;
                break;
            default:
                break;
        }

        //LogUtil.logE("getTrendTypeId trendType", "" + trendType);
        return trendType;
    }

    public static String getTrendType(int lotteryId) {
        String trendType = "";
        switch (lotteryId) {
//     时时彩       "11,151,119,42,161,191,51,601,711,811,911,46,200,201,202,203,6,205,206"    其中80  85 86 87为经典系类
            case 6:
            case 11:
            case 42:
            case 46:
            case 51:
            case 61:
            case 80:
            case 85:
            case 86:
            case 87:
            case 119:
            case 151:
            case 161:
            case 191:
            case 200:
            case 201:
            case 202:
            case 203:
            case 205:
            case 206:

            case 210:
            case 211:
            case 212:
            case 213:
            case 214:
            case 215:
            case 216:
            case 217:
            case 218:
            case 301:
            case 601:
            case 711:
            case 811:
            case 911:
            case 300:

            case 302:
            case 303:
            case 304:
            case 305:
            case 307:
            case 308:
            case 309:
            case 315:
            case 318:
            case 319:

            case 320:
            case 321:
            case 322:
            case 325:
            case 326:
            case 327:

            case 332:
            case 333:
            case 334:
            case 335:
            case 336:
            case 350:
            case 351:
            case 352:
            case 353:
            case 354:
            case 355:
                trendType = TYPE_SSC_STR;
                break;
//      快三      "31,32,33,35,36"     34 90为经典
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 90:
                trendType = TYPE_KUAI_SAN_STR;
                break;
//    11选5        "24,21,23,22,26,28"   25,100为经典
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 28:
            case 100:
                trendType = TYPE_11XUAN_5_STR;
                break;
//     pk10       "43,204,47"
            case 43:
            case 47:
            case 110:
            case 204:
            case 306:
                trendType = TYPE_PK10_STR;
                break;
//     3D福彩           "41"
            case 41:
                trendType = TYPE_3D_STR;
                break;
            default:
                break;
        }

        //LogUtil.logE("getTrendTypeId trendType", "" + trendType);
        return trendType;
    }

    public static int getType(String typeStr) {

//        LogUtil.logE("getType typeStr:",typeStr);

        switch (typeStr) {
            default:
                return -1;
            case TYPE_DRAW_SINGLE_TREND://单号走势
                return 0;
            case TYPE_DRAW_MULTIPLE_TREND://多号走势
                return 1;
            case TYPE_DRAW_CODE_DX://大小走势
                return 2;
            case TYPE_DRAW_CODE_DS://单双走势
                return 3;
            case TYPE_DRAW_SUM_DXDS_SSC://和值 大小单双
                return 4;
            case TYPE_DRAW_SUM_SAME_SSC://和值
                return 5;
            case TYPE_DRAW_DIFF_SSC:
                return 6;
            case TYPE_DRAW_LHH_SSC://
                return 7;
            case TYPE_DRAW_SUM_SAME_CODE:
                return 8;
            case TYPE_DRAW_SUM_SAME_CODE_3D:
                return 9;
            case TYPE_DRAW_CODE_DX_11X5:
                return 10;
            case TYPE_DRAW_SUM_DXDS_KL8:
                return 11;
            case TYPE_DRAW_SUM_FIVE_KL18:
                return 12;
            case TYPE_DRAW_CODE_COUNT_JOSX_KL8:
                return 13;


        }
    }
}
