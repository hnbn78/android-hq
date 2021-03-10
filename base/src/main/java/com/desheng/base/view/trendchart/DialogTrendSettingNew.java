package com.desheng.base.view.trendchart;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.desheng.base.R;
import com.desheng.base.view.trendchart.adapter.MyTrendTypeAdapter;
import com.pearl.view.SpaceTopDecoration;

import java.util.ArrayList;
import java.util.List;


public class DialogTrendSettingNew extends Dialog {

    private RecyclerView trendSettingRv;
    private MyTrendTypeAdapter myTrendTypeAdapter;
    private List<TrendTypeBean> trendTypeBeans = new ArrayList<>();
    private Context context;
    private MyTrendTypeAdapter.OnTypeItemClickListener onTypeItemClickListener;
    private String lotteryType;

    public DialogTrendSettingNew(@NonNull Context context,String lotteryType,MyTrendTypeAdapter.OnTypeItemClickListener onTypeItemClickListener) {
        super(context, R.style.DialogTheme);
        this.context = context;
        this.lotteryType = lotteryType;
        this.onTypeItemClickListener = onTypeItemClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_trend_setting_new);
        trendSettingRv = findViewById(R.id.trendSettingRv);
        trendSettingRv.addItemDecoration(new SpaceTopDecoration(5));
        trendSettingRv.setLayoutManager(new LinearLayoutManager(getContext()));
        myTrendTypeAdapter = new MyTrendTypeAdapter(trendTypeBeans,context,lotteryType,onTypeItemClickListener);
        trendSettingRv.setAdapter(myTrendTypeAdapter);

        //按空白处不能取消动画
        //setCanceledOnTouchOutside(false);
    }

    public void setTrendSettingData(String lotteryId) {
        trendTypeBeans.clear();
        if (myTrendTypeAdapter == null) {
            myTrendTypeAdapter = new MyTrendTypeAdapter(trendTypeBeans,context,lotteryType,onTypeItemClickListener);
        }
        List<TrendTypeBean> trendTypeBeansData = new TrendTypeChooseBean().chooseTrendType(Integer.parseInt(lotteryId));
        trendTypeBeans.addAll(trendTypeBeansData);
        myTrendTypeAdapter.setNewData(trendTypeBeans);
    }


}
