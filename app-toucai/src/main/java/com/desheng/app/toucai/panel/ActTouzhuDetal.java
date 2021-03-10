package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.model.GudongTeamBetHistoryMode;
import com.desheng.app.toucai.model.TeamBetHistoryItemBean;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.Plays;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActTouzhuDetal extends AbAdvanceActivity {

    private RecyclerView recycleView;
    private BaseQuickAdapter quickAdapter;
    private String mOrderItemId;

    public static void launch(Activity act, String orderItemId) {
        Intent itt = new Intent(act, ActTouzhuDetal.class);
        itt.putExtra("orderItemId", orderItemId);
        act.startActivity(itt);
    }

    @Override
    public void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_team_touzhu_detail;
    }

    public void initView() {
        recycleView = findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));

        mOrderItemId = getIntent().getStringExtra("orderItemId");

        quickAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_team_touzhu_detail) {

            @Override
            protected void convert(BaseViewHolder helper, String item) {

                helper.setText(R.id.tvLeft, titleStr[helper.getPosition()]);
                if (Strs.isEqual(titleStr[helper.getPosition()], "0") || Strs.isEqual(titleStr[helper.getPosition()], "1")) {
                    helper.getView(R.id.tvcenter).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tvLeft).setVisibility(View.GONE);
                    helper.getView(R.id.tvright).setVisibility(View.GONE);
                    if (Strs.isEqual(titleStr[helper.getPosition()], "1")) {
                        ((TextView) helper.getView(R.id.tvcenter)).setTextSize(Plays.ScreenUtil.dip2px(ActTouzhuDetal.this, 10));
                        ((TextView) helper.getView(R.id.tvcenter)).setTextColor(Color.RED);
                    } else {
                        ((TextView) helper.getView(R.id.tvcenter)).setTextColor(Color.BLACK);
                    }
                    helper.setText(R.id.tvcenter, item);
                } else {
                    ((TextView) helper.getView(R.id.tvcenter)).setTextColor(Color.BLACK);
                    helper.getView(R.id.tvcenter).setVisibility(View.GONE);
                    helper.getView(R.id.tvLeft).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tvright).setVisibility(View.VISIBLE);
                }

                helper.setText(R.id.tvright, item);
            }
        };
        recycleView.setAdapter(quickAdapter);
        if (Strs.isNotEmpty(mOrderItemId)) {
            getData(mOrderItemId);
        }
    }

    String[] titleStr = {"0", "1", "订单编号", "期号", "彩种", "玩法", "订单时间", "投注金额", "中奖彩金", "状态", "返点额", "返点状态"};
    String[] statusStr = {"未开奖", "未中奖", "已发放"};
    List<String> titleStrVlaue = new ArrayList<>();

    private void getData(String orderItemId) {
        HttpActionTouCai.getLotteryOrderByOrderId(this, orderItemId, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(ActTouzhuDetal.this, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<TeamBetHistoryItemBean>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    TeamBetHistoryItemBean bean = getField(extra, "data", null);

                    if (bean != null) {
                        titleStrVlaue.add("投注号码");
                        titleStrVlaue.add(bean.getContent());
                        titleStrVlaue.add(bean.getOrderItemId());
                        titleStrVlaue.add(bean.getIssueNo());
                        titleStrVlaue.add(bean.getLottery());
                        titleStrVlaue.add(bean.getMethod());
                        titleStrVlaue.add(bean.getOrderTime());
                        titleStrVlaue.add(String.valueOf(bean.getAmount()));
                        titleStrVlaue.add(String.valueOf(bean.getAwardMoney()));
                        titleStrVlaue.add(statusStr[bean.getStatus()]);
                        titleStrVlaue.add(bean.getPoint());
                        titleStrVlaue.add("已发放");
                    }
                    quickAdapter.setNewData(titleStrVlaue);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActTouzhuDetal.this, content, false);
                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                DialogsTouCai.hideProgressDialog(ActTouzhuDetal.this);
            }
        });
    }

}
