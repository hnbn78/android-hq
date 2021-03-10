package com.desheng.base.view.trendchart;


import android.util.Log;

import com.desheng.base.util.TrendTypeIdUtil;

import java.util.ArrayList;
import java.util.List;

public class TrendTypeChooseBean {

    public List<TrendTypeBean> chooseTrendType(int lotteryId) {
        ArrayList<TrendTypeBean> localArrayList = new ArrayList();
        //获取趋势图类型
        int typeId = TrendTypeIdUtil.getTrendTypeId(lotteryId);
        TrendTypeBean localTrendTypeBean;
        switch (typeId) {
            case 5:
                Log.d("TrendTypeChooseBean", "step5");
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("单号走势");
                localTrendTypeBean.setType("type_draw_single_trend");
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(0);
                localTrendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百位", "0"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("十位", "1"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("个位", "2"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("多号走势");
                localTrendTypeBean.setType("type_draw_multiple_trend");
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(0);
                localTrendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三码", "0,1,2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前二", "0,1"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后二", "1,2"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("大小走势");
                localTrendTypeBean.setType("type_draw_code_dx");
                localTrendTypeBean.setHeadNum(new String[]{"5", "1", "2", "大", "小", "大", "小", "大", "小"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(1);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百十个", "0,1,2"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("单双走势");
                localTrendTypeBean.setType("type_draw_code_ds");
                localTrendTypeBean.setHeadNum(new String[]{"5", "1", "2", "单", "双", "单", "双", "单", "双"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(1);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百十个", "0,1,2"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("和值|形态");
                localTrendTypeBean.setType("type_draw_sum_same_code_3d");
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setHeadNum(new String[]{"5", "1", "2", "3", "4", "5", "豹子", "组六", "组三"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setShowList(6);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三码", "0,1,2"));
                localArrayList.add(localTrendTypeBean);
                break;
            case 4:
                Log.d("TrendTypeChooseBean", "step4");
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("单号走势");
                localTrendTypeBean.setType("type_draw_single_trend");
                localTrendTypeBean.setHeadNum(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(0);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第一名", "0"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第二名", "1"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第三名", "2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第四名", "3"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第五名", "4"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第六名", "5"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第七名", "6"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第八名", "7"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第九名", "8"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第十名", "9"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("多号走势");
                localTrendTypeBean.setType("type_draw_multiple_trend");
                localTrendTypeBean.setHeadNum(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(0);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前二", "0,1"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前三", "0,1,2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前五", "0,1,2,3,4"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后五", "5,6,7,8,9"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("大小走势");
                localTrendTypeBean.setType("type_draw_code_dx");
                localTrendTypeBean.setHeadNum(new String[]{"06", "1", "2", "大", "小", "大", "小", "大", "小"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(1);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("单双走势");
                localTrendTypeBean.setType("type_draw_code_ds");
                localTrendTypeBean.setHeadNum(new String[]{"06", "1", "2", "单", "双", "单", "双", "单", "双"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(1);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
                localArrayList.add(localTrendTypeBean);
                break;
            case 3:
                Log.d("TrendTypeChooseBean", "step3");
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("单号走势");
                localTrendTypeBean.setType("type_draw_single_trend");
                localTrendTypeBean.setHeadNum(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(0);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第一名", "0"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第二名", "1"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第三名", "2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第四名", "3"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("第五名", "4"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("中位", "0,1,2,3,4-2"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("多号走势");
                localTrendTypeBean.setType("type_draw_multiple_trend");
                localTrendTypeBean.setHeadNum(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(0);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("五星", "0,1,2,3,4"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前二", "0,1"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前三", "0,1,2"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("大小走势");
                localTrendTypeBean.setType("type_draw_code_dx");
                localTrendTypeBean.setHeadNum(new String[]{"06", "1", "2", "大", "小", "大", "小", "大", "小"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(1);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三四五", "2,3,4"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("单双走势");
                localTrendTypeBean.setType("type_draw_code_ds");
                localTrendTypeBean.setHeadNum(new String[]{"06", "1", "2", "单", "双", "单", "双", "单", "双"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(1);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三四五", "2,3,4"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("号码单双");
                localTrendTypeBean.setType("type_draw_code_dx_11x5");
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setHeadNum(new String[]{"06", "1", "2", "3", "4", "5:0", "4:1", "3:2", "2:3", "1:4", "0:5"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setShowList(7);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("个数比", "0,1,2,3,4"));
                localArrayList.add(localTrendTypeBean);
                break;
            case 2:
                Log.d("TrendTypeChooseBean", "step2");
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("单号走势");
                localTrendTypeBean.setType("type_draw_single_trend");
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(0);
                localTrendTypeBean.setHeadNum(new String[]{"1", "2", "3", "4", "5", "6"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一位", "0"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("二位", "1"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三位", "2"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("多号走势");
                localTrendTypeBean.setType("type_draw_multiple_trend");
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(0);
                localTrendTypeBean.setHeadNum(new String[]{"1", "2", "3", "4", "5", "6"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三码", "0,1,2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前二", "0,1"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后二", "1,2"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("大小走势");
                localTrendTypeBean.setType("type_draw_code_dx");
                localTrendTypeBean.setHeadNum(new String[]{"4", "1", "2", "大", "小", "大", "小", "大", "小"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(1);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("单双走势");
                localTrendTypeBean.setType("type_draw_code_ds");
                localTrendTypeBean.setHeadNum(new String[]{"4", "1", "2", "单", "双", "单", "双", "单", "双"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(1);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("一二三", "0,1,2"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("和值|形态");
                localTrendTypeBean.setType("type_draw_sum_same_code");
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setHeadNum(new String[]{"4", "1", "2", "3", "三同号", "三不同号", "二同号", "三同号", "三不同号", "二同号"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setShowList(5);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("三码", "0,1,2"));
                localArrayList.add(localTrendTypeBean);
                break;
            case 1:
                Log.d("TrendTypeChooseBean", "step1");
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("单号走势");
                localTrendTypeBean.setType("type_draw_single_trend");
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setShowList(0);
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("万位", "0"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("千位", "1"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百位", "2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("十位", "3"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("个位", "4"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("多号走势");
                localTrendTypeBean.setType("type_draw_multiple_trend");
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setShowList(0);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("五星", "0,1,2,3,4"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后四", "1,2,3,4"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前四", "0,1,2,3"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后三", "2,3,4"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("中三", "1,2,3"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前三", "0,1,2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("后二", "3,4"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("前二", "0,1"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("大小走势");
                localTrendTypeBean.setType("type_draw_code_dx");
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setHeadNum(new String[]{"5", "1", "2", "大", "小", "大", "小", "大", "小"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setShowList(1);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("万千百", "0,1,2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百十个", "2,3,4"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("单双走势");
                localTrendTypeBean.setType("type_draw_code_ds");
                localTrendTypeBean.setHeadNum(new String[]{"5", "1", "2", "单", "双", "单", "双", "单", "双"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(1);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("万千百", "0,1,2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百十个", "2,3,4"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("五星和值");
                localTrendTypeBean.setType("type_draw_sum_dxds_ssc");
                localTrendTypeBean.setHeadNum(new String[]{"23", "1", "2", "3", "4", "五星和值", "大", "小", "单", "双"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(2);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("大小单双", "0,1,2,3,4"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("和值");
                localTrendTypeBean.setType("type_draw_sum_ssc");
                localTrendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(3);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("各类", "0,1,2,3,4"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("跨度");
                localTrendTypeBean.setType("type_draw_diff_ssc");
                localTrendTypeBean.setHeadNum(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(3);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("各类", "0,1,2,3,4"));
                localArrayList.add(localTrendTypeBean);
                localTrendTypeBean = new TrendTypeBean();
                localTrendTypeBean.setName("龙虎和");
                localTrendTypeBean.setType("type_draw_lhh_ssc");
                localTrendTypeBean.setHeadNum(new String[]{"0", "1", "龙", "和", "虎", "5", "6", "龙", "和", "虎"});
                localTrendTypeBean.setBlueMissBallNum(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                localTrendTypeBean.setTypeId(typeId);
                localTrendTypeBean.setShowList(4);
                localTrendTypeBean.setIndex_list(localTrendTypeBean.getIndex_list());
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("万千\n万百", "0,1-0,2"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("万十\n万个", "0,3-0,4"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("千百\n千十", "1,2-1,3"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("千个\n百十", "1,4-2,3"));
                localTrendTypeBean.getIndex_list().add(new TrendTypeIndexBean("百个\n十个", "2,4-3,4"));
                localArrayList.add(localTrendTypeBean);
                break;
            default:
                Log.d("TrendTypeChooseBean", "step6");
                break;
        }
        Log.d("TrendTypeChooseBean", "localArrayList.size():" + localArrayList.size());
        return localArrayList;
    }

    //    paramString  TrendTypeIndexBean的第二个pos参数   例如"千百\n千十", "1,2-1,3"  中的"1,2-1,3"

    public List<int[]> posList(String paramString) {
        ArrayList posList = new ArrayList();
        boolean bool = paramString.contains("-");
        int[] arrayOfInt;
        if (bool) {
            String[] arrayString = paramString.split("-");

            for(int i=0;i<arrayString.length;i++){
                if (arrayString[i].contains(",")) {
                    String[] arrayOfString2 = arrayString[i].split(",");
                    arrayOfInt = new int[arrayOfString2.length];
                    for (int j=0;j< arrayOfInt.length; j++) {
                        if (j >= arrayOfInt.length) {
                            break;
                        }
                        arrayOfInt[j] = Integer.parseInt(arrayOfString2[j]);
                    }
                }
                int[] tempInts = new int[1];
                tempInts[0] = Integer.parseInt(arrayString[i]);
                posList.add(paramString);
            }
        }
        if (paramString.contains(",")) {
            String[] strs = paramString.split(",");
            arrayOfInt = new int[strs.length];

            for(int i=0;i<strs.length;i++){
                arrayOfInt[i] = Integer.parseInt(strs[i]);
            }

            posList.add(arrayOfInt);
            return posList;
        }
        posList.add(new int[]{Integer.parseInt(paramString)});
        return posList;
    }
}
