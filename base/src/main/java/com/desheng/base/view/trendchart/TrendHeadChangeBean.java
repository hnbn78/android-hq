package com.desheng.base.view.trendchart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desheng.base.R;
import com.desheng.base.util.ChangdiptopxUtil;
import com.desheng.base.util.DeviceUtil;
import com.desheng.base.util.TrendTypeIdUtil;
import com.desheng.base.view.trendchart.adapter.GameTrendDataAdapter;
import com.pearl.view.trendchart.HeadCustomGridView;

import java.util.Arrays;
import java.util.List;


public class TrendHeadChangeBean {
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    String[] llhwq = {"万", "千", "百", "十", "个"};

    private View backLHHView(View view, List<int[]> posList) {

        TextView localTextView1 = view.findViewById(R.id.trend_head_lhh_1);
        TextView localTextView2 = view.findViewById(R.id.trend_head_lhh_2);
        TextView localTextView3 = view.findViewById(R.id.trend_head_lhh_3);
        TextView localTextView4 = view.findViewById(R.id.trend_head_lhh_4);

        String temp1 = llhwq[posList.get(0)[0]];
        String temp2 = llhwq[posList.get(0)[1]];
        String temp3 = llhwq[posList.get(1)[0]];
        String temp4 = llhwq[posList.get(1)[1]];

        localTextView1.setText(temp1);
        localTextView2.setText(temp2);
        localTextView3.setText(temp3);
        localTextView4.setText(temp4);
        return view;
    }

    private View backView(Context context, int[] posArray, int position, ChangdiptopxUtil changdiptopxUtil) {
        View view = LayoutInflater.from(context).inflate(R.layout.include_trend_head_dx, null);
        TextView trend_head_dx_1 = view.findViewById(R.id.trend_head_dx_1);
        TextView trend_head_dx_2 = view.findViewById(R.id.trend_head_dx_2);
        TextView trend_head_dx_3 = view.findViewById(R.id.trend_head_dx_3);
        TextView trend_head_dx_4 = view.findViewById(R.id.trend_head_dx_4);
        TextView trend_head_dx_5 = view.findViewById(R.id.trend_head_dx_5);
        TextView trend_head_dx_6 = view.findViewById(R.id.trend_head_dx_6);
        int i = changdiptopxUtil.dip2pxInt(TrendView.HEIGHT);
        int j = changdiptopxUtil.dip2pxInt(TrendView.HEIGHT);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i, j);
        trend_head_dx_1.setLayoutParams(layoutParams);
        trend_head_dx_2.setLayoutParams(layoutParams);
        trend_head_dx_3.setLayoutParams(layoutParams);
        layoutParams = new LinearLayout.LayoutParams(i * 2, j);
        trend_head_dx_4.setLayoutParams(layoutParams);
        trend_head_dx_5.setLayoutParams(layoutParams);
        trend_head_dx_6.setLayoutParams(layoutParams);


        trend_head_dx_1.setText(backName(posArray[0], position));
        trend_head_dx_2.setText(backName(posArray[1], position));
        trend_head_dx_3.setText(backName(posArray[2], position));

        trend_head_dx_4.setText(backNameBig(posArray[0], position));
        trend_head_dx_5.setText(backNameBig(posArray[1], position));
        trend_head_dx_6.setText(backNameBig(posArray[2], position));
        return view;
    }


    public void changeHeadView(LinearLayout game_trend_linear_header,
                               LinearLayout game_trend_linear_header_other,
                               HeadCustomGridView game_trend_grid_header,
                               Context context,
                               ChangdiptopxUtil changdiptopxUtil, List<int[]> posList, TrendTypeBean trendTypeBean) {
        int typeId = trendTypeBean.getTypeId();
        String type = trendTypeBean.getType();

        lp.width = DeviceUtil.getDeviceWidth(context) - changdiptopxUtil.dip2pxInt(TrendView.LEFT_WIDTH);
        lp.height = changdiptopxUtil.dip2pxInt(TrendView.HEIGHT);

        int showList = trendTypeBean.getShowList();
        if (showList == 0) {
            game_trend_linear_header.setVisibility(View.VISIBLE);
            game_trend_linear_header_other.removeAllViews();
            game_trend_linear_header_other.setVisibility(View.GONE);
        } else {
            game_trend_linear_header.setVisibility(View.GONE);
            game_trend_linear_header_other.removeAllViews();
            game_trend_linear_header_other.setVisibility(View.VISIBLE);
        }

        switch (TrendTypeIdUtil.getType(type)) {
            default:
                break;
            case 0:
                setAdapter(game_trend_grid_header, context, changdiptopxUtil, trendTypeBean);
//                game_trend_linear_header_other.addView(LayoutInflater.from(context).inflate(R.layout.include_trend_head_single, null));
                break;
            case 1:
                setAdapter(game_trend_grid_header, context, changdiptopxUtil, trendTypeBean);
                break;
            case 2:
                game_trend_linear_header_other.addView(backView(context, posList.get(0), 2, changdiptopxUtil));
                break;
            case 3:
                game_trend_linear_header_other.addView(backView(context, posList.get(0), 3, changdiptopxUtil));
                break;
            case 4:
                LinearLayout layout_wxhz = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.include_trend_head_wxhz, null);
                layout_wxhz.setLayoutParams(lp);
                game_trend_linear_header_other.addView(layout_wxhz);
                break;
            case 5:
                LinearLayout layout_hzgl = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.include_trend_head_hzgl, null);
                layout_hzgl.setLayoutParams(lp);
                game_trend_linear_header_other.addView(layout_hzgl);
                break;
            case 6:
                LinearLayout layout_kdgl = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.include_trend_head_kdgl, null);
                layout_kdgl.setLayoutParams(lp);
                game_trend_linear_header_other.addView(layout_kdgl);
                break;
            case 7:
                LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.include_trend_head_lhh, null);
                backLHHView(layout, posList);
                layout.setLayoutParams(lp);
                game_trend_linear_header_other.addView(layout);
                break;
            case 8:
                LinearLayout layout_same_code = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.include_trend_head_same_code, null);
                layout_same_code.setLayoutParams(lp);
                game_trend_linear_header_other.addView(layout_same_code);
                break;
            case 9:
                LinearLayout layout_same_code_3d = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.include_trend_head_same_code_3d, null);
                layout_same_code_3d.setLayoutParams(lp);
                game_trend_linear_header_other.addView(layout_same_code_3d);
                break;
            case 10:
                LinearLayout layout_same_code_11x5 = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.include_trend_head_same_code_11x5, null);
                layout_same_code_11x5.setLayoutParams(lp);
                game_trend_linear_header_other.addView(layout_same_code_11x5);
                break;
            case 11:

                LinearLayout layout_same_code_dxds_k18 = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.include_trend_head_dxds_k18, null);
                layout_same_code_dxds_k18.setLayoutParams(lp);
                game_trend_linear_header_other.addView(layout_same_code_dxds_k18);
                break;
            case 12:

                LinearLayout layout_same_code_five_k18 = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.include_trend_head_five_kl8, null);
                layout_same_code_five_k18.setLayoutParams(lp);
                game_trend_linear_header_other.addView(layout_same_code_five_k18);
                break;
            case 13:

                LinearLayout layout_same_code_josx_k182 = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.include_trend_head_josx_kl8, null);
                layout_same_code_josx_k182.setLayoutParams(lp);
                game_trend_linear_header_other.addView(layout_same_code_josx_k182);
                break;
        }

    }


    public void setAdapter(HeadCustomGridView mHeadGridView, Context context, ChangdiptopxUtil changdiptopxUtil, TrendTypeBean trendTypeBean) {
        String[] headNums = trendTypeBean.getHeadNum();

        int width = changdiptopxUtil.dip2pxInt(TrendView.WIDTH_MAX) / 10;
        if (Arrays.asList(headNums).size() > 10) {
            width = changdiptopxUtil.dip2pxInt(TrendView.WIDTH_MAX) / 11;
        }
        GameTrendDataAdapter trendDataAdapter = new GameTrendDataAdapter(context, Arrays.asList(headNums), width);

        mHeadGridView.setLayoutParams(new LinearLayout.LayoutParams(trendDataAdapter.getCount() * width, -2));
        mHeadGridView.setColumnWidth(width);
        mHeadGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        mHeadGridView.setNumColumns(trendDataAdapter.getCount());
        mHeadGridView.setAdapter(trendDataAdapter);
        trendDataAdapter.notifyDataSetChanged();
    }


    private String backName(int pos, int position) {
        switch (position) {
            default:
                return "";
            case 5:
                switch (pos) {

                    case 4:
                        return "个";
                    case 3:
                        return "十";
                    case 2:
                        return "百";
                    case 1:
                        return "千";
                    default:
                        return "万";
                }

            case 4:
                switch (pos) {
                    case 4:
                        return "五";
                    case 3:
                        return "四";
                    case 2:
                        return "三";
                    case 1:
                        return "二";
                    default:
                        return "一";
                }

            case 3:
                switch (pos) {

                    case 4:
                        return "五";
                    case 3:
                        return "四";
                    case 2:
                        return "三";
                    case 1:
                        return "二";
                    default:
                        return "一";
                }

            case 2:
                switch (pos) {

                    case 4:
                        return "五";
                    case 3:
                        return "四";
                    case 2:
                        return "三";
                    case 1:
                        return "二";
                    default:
                        return "一";
                }
        }

    }

    private String backNameBig(int pos, int position) {
        switch (position) {
            default:
                return "";
            case 5:
                switch (pos) {

                    case 4:
                        return "个";
                    case 3:
                        return "十";
                    case 2:
                        return "百";
                    case 1:
                        return "千";
                    default:
                        return "万";
                }
            case 4:
                switch (pos) {

                    case 4:
                        return "第五位";
                    case 3:
                        return "第四位";
                    case 2:
                        return "第三位";
                    case 1:
                        return "第二位";
                    default:
                        return "第一位";
                }

            case 3:
                switch (pos) {

                    case 4:
                        return "第五位";
                    case 3:
                        return "第四位";
                    case 2:
                        return "第三位";
                    case 1:
                        return "第二位";
                    default:
                        return "第一位";
                }

            case 2:
                switch (pos) {

                    case 4:
                        return "第五位";
                    case 3:
                        return "第四位";
                    case 2:
                        return "第三位";
                    case 1:
                        return "第二位";

                }
                return "第一位";
        }

    }
}
