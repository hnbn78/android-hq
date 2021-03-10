package com.desheng.base.view.trendchart;

import com.desheng.base.util.LogUtil;
import com.desheng.base.util.TrendTypeIdUtil;

import java.util.ArrayList;
import java.util.List;

public class TrendTypeChooseBeanNew {
    private String[] typeId = {
            "11,151,119,42,161,191,51,601,711,811,911,46,200,201,202,203,6,205,206",
            "31,32,33,35,36",
            "24,21,23,22,26,28",
            "43,204,47",
            "41",
            ""};

    public List<TrendTypeBean> chooseTrendType(int lotteryId) {

        LogUtil.logE("3.获取走势图配置", "+++++++++++++++++++++");
        switch (TrendTypeIdUtil.getTrendTypeId(lotteryId)) {
            case 6:
                LogUtil.logE("chooseTrendType type:" + 6, "未知类型彩种");
                return getTypeNoKnow(lotteryId, 6);
            case TrendTypeIdUtil.TYPE_3D:
                LogUtil.logE("chooseTrendType type:" + 5, "3D福彩类型");
                return getType3DFuCai(lotteryId, 5);
            case TrendTypeIdUtil.TYPE_PK10:
                LogUtil.logE("chooseTrendType type:" + 4, "北京PK10");
                return getTypeBeiJingPk10(lotteryId, 4);
            case TrendTypeIdUtil.TYPE_11X5:
                LogUtil.logE("chooseTrendType type:" + 3, "11选5");
                return getType11Xuan5(lotteryId, 3);
            case TrendTypeIdUtil.TYPE_KUAISAN:
                LogUtil.logE("chooseTrendType type:" + 2, "快3");
                return getTypeKuai3(lotteryId, 2);
            case TrendTypeIdUtil.TYPE_SSC:
                LogUtil.logE("chooseTrendType type:" + 1, "时时彩");
                return getTypeSsc(lotteryId, 1);
            default:
                LogUtil.logE("chooseTrendType type:default", "默认 时时彩");
                return getTypeSsc(lotteryId, 0);
        }
    }

    private List<TrendTypeBean> getTypeSsc(int lotteryId, int lotteryType) {
        List<TrendTypeBean> trendTypeBeans = new ArrayList<>();
        TrendTypeBean trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("单号走势");
        trendTypeBean.setType("type_draw_single_trend");
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setShowList(0);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("万位", "0"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("千位", "1"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百位", "2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("十位", "3"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("个位", "4"));
        trendTypeBeans.add(trendTypeBean);


        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("多号走势");
        trendTypeBean.setType("type_draw_multiple_trend");
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setShowList(0);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("五星", "0,1,2,3,4"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后四", "1,2,3,4"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前四", "0,1,2,3"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后三", "2,3,4"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("中三", "1,2,3"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前三", "0,1,2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后二", "3,4"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前二", "0,1"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("大小走势");
        trendTypeBean.setType("type_draw_code_dx");
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setHeadNum(new String[]{"5", "1", "2", "大", "小", "大", "小", "大", "小"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setShowList(1);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("万千百", "0,1,2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百十个", "2,3,4"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("单双走势");
        trendTypeBean.setType("type_draw_code_ds");
        trendTypeBean.setHeadNum(new String[]{"5", "1", "2", "单", "双", "单", "双", "单", "双"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(1);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("万千百", "0,1,2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百十个", "2,3,4"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("五星和值");
        trendTypeBean.setType("type_draw_sum_dxds_ssc");
        trendTypeBean.setHeadNum(new String[]{"23", "1", "2", "3", "4", "五星和值", "大", "小", "单", "双"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(2);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("大小单双", "0,1,2,3,4"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("和值");
        trendTypeBean.setType("type_draw_sum_ssc");
        trendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(3);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("各类", "0,1,2,3,4"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("跨度");
        trendTypeBean.setType("type_draw_diff_ssc");
        trendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(3);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("各类", "0,1,2,3,4"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("龙虎和");
        trendTypeBean.setType("type_draw_lhh_ssc");
        trendTypeBean.setHeadNum(new String[]{"0", "1", "龙", "和", "虎", "5", "6", "龙", "和", "虎"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(4);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("万千\n万百", "0,1-0,2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("万十\n万个", "0,3-0,4"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("千百\n千十", "1,2-1,3"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("千个\n百十", "1,4-2,3"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百个\n十个", "2,4-3,4"));
        trendTypeBeans.add(trendTypeBean);
        return trendTypeBeans;
    }

    private List<TrendTypeBean> getTypeKuai3(int lotteryId, int lotteryType) {
        List<TrendTypeBean> trendTypeBeans = new ArrayList<>();

        TrendTypeBean trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("单号走势");
        trendTypeBean.setType("type_draw_single_trend");
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(0);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setHeadNum(new String[]{"1", "2", "3", "4", "5", "6"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0});
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一位", "0"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("二位", "1"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三位", "2"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("多号走势");
        trendTypeBean.setType("type_draw_multiple_trend");
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(0);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setHeadNum(new String[]{"1", "2", "3", "4", "5", "6"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0});
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三码", "0,1,2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前二", "0,1"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后二", "1,2"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("大小走势");
        trendTypeBean.setType("type_draw_code_dx");
        trendTypeBean.setHeadNum(new String[]{"4", "1", "2", "大", "小", "大", "小", "大", "小"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(1);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("单双走势");
        trendTypeBean.setType("type_draw_code_ds");
        trendTypeBean.setHeadNum(new String[]{"4", "1", "2", "单", "双", "单", "双", "单", "双"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(1);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("和值|形态");
        trendTypeBean.setType("type_draw_sum_same_code");
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setHeadNum(new String[]{"4", "1", "2", "3", "三同号", "三不同号", "二同号", "三同号", "三不同号", "二同号"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setShowList(5);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三码", "0,1,2"));
        trendTypeBeans.add(trendTypeBean);

        return trendTypeBeans;
    }

    private List<TrendTypeBean> getType11Xuan5(int lotteryId, int lotteryType) {

        List<TrendTypeBean> trendTypeBeans = new ArrayList<>();
        TrendTypeBean trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("单号走势");
        trendTypeBean.setType("type_draw_single_trend");
        trendTypeBean.setHeadNum(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(0);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第一名", "0"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第二名", "2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第三名", "4"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第四名", "6"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第五名", "8"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("中位", "0,1,2,3,4-2"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("多号走势");
        trendTypeBean.setType("type_draw_multiple_trend");
        trendTypeBean.setHeadNum(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(0);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("五星", "0,1,2,3,4"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前二", "0,1"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前三", "0,1,2"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("大小走势");
        trendTypeBean.setType("type_draw_code_dx");
        trendTypeBean.setHeadNum(new String[]{"6", "1", "2", "大", "小", "大", "小", "大", "小"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(1);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三四五", "2,3,4"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("单双走势");
        trendTypeBean.setType("type_draw_code_ds");
        trendTypeBean.setHeadNum(new String[]{"6", "1", "2", "单", "双", "单", "双", "单", "双"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(1);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三四五", "2,3,4"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("号码单双");
        trendTypeBean.setType("type_draw_code_dx_11x5");
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setHeadNum(new String[]{"6", "1", "2", "3", "4", "5:0", "4:1", "3:2", "2:3", "1:4", "0:5"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setShowList(7);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("个数比", "0,1,2,3,4"));
        trendTypeBeans.add(trendTypeBean);
        return trendTypeBeans;
    }

    private List<TrendTypeBean> getTypeBeiJingPk10(int lotteryId, int lotteryType) {
        List<TrendTypeBean> trendTypeBeans = new ArrayList<>();
        TrendTypeBean trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("单号走势");
        trendTypeBean.setType("type_draw_single_trend");
        trendTypeBean.setHeadNum(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(0);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第一名", "0"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第二名", "2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第三名", "4"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第四名", "6"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第五名", "8"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第六名", "10"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第七名", "12"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第八名", "14"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第九名", "16"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第十名", "18"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("多号走势");
        trendTypeBean.setType("type_draw_multiple_trend");
        trendTypeBean.setHeadNum(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(0);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前二", "0,1"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前三", "0,1,2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前五", "0,1,2,3,4"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后五", "5,6,7,8,9"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("大小走势");
        trendTypeBean.setType("type_draw_code_dx");
        trendTypeBean.setHeadNum(new String[]{"6", "1", "2", "大", "小", "大", "小", "大", "小"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(1);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("单双走势");
        trendTypeBean.setType("type_draw_code_ds");
        trendTypeBean.setHeadNum(new String[]{"6", "1", "2", "单", "双", "单", "双", "单", "双"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(1);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
        trendTypeBeans.add(trendTypeBean);
        return trendTypeBeans;
    }

    private List<TrendTypeBean> getType3DFuCai(int lotteryId, int lotteryType) {
        List<TrendTypeBean> trendTypeBeans = new ArrayList<>();
        TrendTypeBean trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("单号走势");
        trendTypeBean.setType("type_draw_single_trend");
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(0);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百位", "0"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("十位", "1"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("个位", "2"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("多号走势");
        trendTypeBean.setType("type_draw_multiple_trend");
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(0);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三码", "0,1,2"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前二", "0,1"));
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后二", "1,2"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("大小走势");
        trendTypeBean.setType("type_draw_code_dx");
        trendTypeBean.setHeadNum(new String[]{"5", "1", "2", "大", "小", "大", "小", "大", "小"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(1);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百十个", "0,1,2"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("单双走势");
        trendTypeBean.setType("type_draw_code_ds");
        trendTypeBean.setHeadNum(new String[]{"5", "1", "2", "单", "双", "单", "双", "单", "双"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setShowList(1);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百十个", "0,1,2"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("和值|形态");
        trendTypeBean.setType("type_draw_sum_same_code_3d");
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setHeadNum(new String[]{"5", "1", "2", "3", "4", "5", "豹子", "组六", "组三"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setShowList(6);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三码", "0,1,2"));
        trendTypeBeans.add(trendTypeBean);

        return trendTypeBeans;
    }

    private ArrayList<TrendTypeBean> getTypeNoKnow(int lotteryId, int lotteryType) {
        ArrayList<TrendTypeBean> trendTypeBeans = new ArrayList();
        TrendTypeBean trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("和值");
        trendTypeBean.setType("type_draw_sum_dxds_kl8");
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setHeadNum(new String[]{"810", "单", "双", "大", "小", "和", "大单", "大双", "小单", "小双"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setShowList(8);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("大小单双", "0"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("和值");
        trendTypeBean.setType("type_draw_sum_five_kl8");
        trendTypeBean.setHeadNum(new String[]{"810", "金", "木", "水", "火", "土"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setShowList(9);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("五行", "0"));
        trendTypeBeans.add(trendTypeBean);

        trendTypeBean = new TrendTypeBean();
        trendTypeBean.setName("奇偶上下");
        trendTypeBean.setType("type_draw_code_count_josx_kl8");
        trendTypeBean.setHeadNum(new String[]{"奇偶个数", "奇", "和", "偶", "上下个数", "上", "和", "下"});
        trendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0});
        trendTypeBean.setTypeId(lotteryId);
        trendTypeBean.setLotteryType(lotteryType);
        trendTypeBean.setShowList(10);
        trendTypeBean.setIndex_list(trendTypeBean.getIndex_list());
        trendTypeBean.getIndex_list().add(new TrendTypeIndexBean("个数比", "0"));
        trendTypeBeans.add(trendTypeBean);

        return trendTypeBeans;
    }

    public int TypeId(int lotteryId) {
        int i = 0;
        int k;
        for (int j = 0; i < this.typeId.length; j = k) {
            String localObject = this.typeId[i];
            String[] types;
            if (this.typeId[i].contains(",")) {
                types = this.typeId[i].split(",");
            } else if ((localObject).length() > 0) {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append(localObject);
                types = new String[]{localStringBuilder.toString()};
            } else {
                types = new String[]{"10000"};
            }

            k = j;
            if (types.length > 0) {
                int n = types.length;
                for (int m = 0; m < n; m++) {
                    k = j;
                    if (lotteryId == Integer.parseInt(types[m])) {
                        k = i + 1;
                        break;
                    }
                }
            }
            if (k != 0) {
                return k;
            }
            i += 1;
        }
        return i;
    }

    public List<int[]> posList(String pos) {
        ArrayList<int[]> posList = new ArrayList();
        //                                          "0,1,2,3,4-2"
        if (pos.contains("-")) {
            String[] posArray = pos.split("-");

            for (int i = 0; i < posArray.length; i++) {
                if (posArray[i].contains(",")) {
                    String[] intstrs = posArray[i].split(",");
                    int[] arrayOfInt = new int[intstrs.length];
                    for (int j = 0; j < arrayOfInt.length; j++) {
                        arrayOfInt[j] = Integer.parseInt(intstrs[j]);
                    }
                    posList.add(arrayOfInt);
                } else {
                    int[] ints = new int[1];
                    ints[0] = Integer.parseInt(posArray[i]);
                    posList.add(ints);
                }

            }
        } else if (pos.contains(",")) {
            String[] strInt = pos.split(",");
            int[] ints = new int[strInt.length];
            for (int i = 0; i < strInt.length; i++) {
                ints[i] = Integer.parseInt(strInt[i]);
            }

            posList.add(ints);
            return posList;
        } else {
            posList.add(new int[]{Integer.parseInt(pos)});
        }
        return posList;
    }

//    public List<int[]> posList(String paramString) {
//        ArrayList<int[]> posArrayList = new ArrayList();
//        boolean bool = paramString.contains("-");
//        int i = 0;
//        int[] arrayOfInt;
//        if (bool) {
//            String[] arrayOfString1 = paramString.split("-");
//            i = 0;
//            while (i < arrayOfString1.length) {
//                if (arrayOfString1[i].contains(",")) {
//                    String[] arrayOfString2 = arrayOfString1[i].split(",");
//                    arrayOfInt = new int[arrayOfString2.length];
//
//                    for (int j = 0; j<arrayOfInt.length;j++ ) {
////                      int[] paramString = arrayOfInt;
//                        arrayOfInt[j] = Integer.parseInt(arrayOfString2[j]);
//                    }
//                }
//                int[] nums = new int[1];
//                nums[0] = Integer.parseInt(arrayOfString1[i]);
//                posArrayList.add(nums);
//                i += 1;
//            }
//        }
//        if (paramString.contains(",")) {
//            String[] numArray = paramString.split(",");
//            arrayOfInt = new int[numArray.length];
//            while (i < numArray.length) {
//                arrayOfInt[i] = Integer.parseInt(numArray[i]);
//                i += 1;
//            }
//            posArrayList.add(arrayOfInt);
//            return posArrayList;
//        }
//        posArrayList.add(new int[]{Integer.parseInt(paramString)});
//        return posArrayList;
//    }
}
