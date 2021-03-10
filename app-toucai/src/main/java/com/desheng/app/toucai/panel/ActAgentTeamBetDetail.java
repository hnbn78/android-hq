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
import com.ab.util.Dates;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.Plays;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.BetOrder;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActAgentTeamBetDetail extends AbAdvanceActivity {

    private TextView tv_title;
    private RecyclerView recycleView;
    private String billno;
    private BaseQuickAdapter<String, BaseViewHolder> quickAdapter;

    public static void launch(Activity act, String billno) {
        Intent intent = new Intent(act, ActAgentTeamBetDetail.class);
        intent.putExtra("billno", billno);
        act.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_team_betdetail;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("投注详情");
        initView();
    }

    public void initView() {
        recycleView = findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));

        billno = getIntent().getStringExtra("billno");
        quickAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_team_touzhu_detail) {

            @Override
            protected void convert(BaseViewHolder helper, String item) {

                helper.setText(R.id.tvLeft, titleStr[helper.getPosition()]);
                if (Strs.isEqual(titleStr[helper.getPosition()], "0") || Strs.isEqual(titleStr[helper.getPosition()], "1")) {
                    helper.getView(R.id.tvcenter).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tvLeft).setVisibility(View.GONE);
                    helper.getView(R.id.tvright).setVisibility(View.GONE);
                    if (Strs.isEqual(titleStr[helper.getPosition()], "1")) {
                        ((TextView) helper.getView(R.id.tvcenter)).setTextSize(Plays.ScreenUtil.dip2px(ActAgentTeamBetDetail.this, 10));
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

        if (Strs.isNotEmpty(billno)) {
            getData(billno);
        }
    }

    String[] titleStr = {"0", "1", "订单编号", "玩法", "订单时间", "投注状态", "投注金额", "中奖金额", "返点", "注数", "资金模式", "倍数", "奖金模式", "开奖号码", "盈亏"};
    String[] statusStr = {"未开奖", "未中奖", "已发放","个人撤单"};
    List<String> titleStrVlaue = new ArrayList<>();

    private void getData(String billNo) {
        HttpAction.getOrderDetail(ActAgentTeamBetDetail.this, billNo, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(ActAgentTeamBetDetail.this, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BetOrder.class);
            }


            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    BetOrder bean = getField(extra, "data", null);
                    tv_title.setText(bean.lottery);
                    if (bean != null) {
                        titleStrVlaue.add("投注号码");
                        titleStrVlaue.add(bean.content);
                        titleStrVlaue.add(bean.billno);
                        titleStrVlaue.add(bean.method);
                        titleStrVlaue.add(Dates.getStringByFormat(bean.orderTime, Dates.dateFormatYMDHMS));
                        titleStrVlaue.add(bean.statusRemark);
                        titleStrVlaue.add(String.valueOf(bean.money) + "元");
                        titleStrVlaue.add(String.valueOf(bean.winMoney) + "元");
                        titleStrVlaue.add(String.valueOf(bean.point) + " %");
                        titleStrVlaue.add(String.valueOf(bean.nums) + "注");
                        String model = "";
                        if ("yuan".equals(bean.model))
                            model = "元";
                        else if ("jiao".equals(bean.model))
                            model = "角";
                        else if ("fen".equals(bean.model))
                            model = "分";
                        else if ("li".equals(bean.model))
                            model = "厘";
                        titleStrVlaue.add(String.valueOf(model));
                        titleStrVlaue.add(String.valueOf(bean.multiple) + " 倍");
                        titleStrVlaue.add(String.valueOf(bean.code));
                        titleStrVlaue.add(String.valueOf(bean.openCode));
                        titleStrVlaue.add(String.valueOf(bean.winMoney));
                    }
                    quickAdapter.setNewData(titleStrVlaue);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActAgentTeamBetDetail.this, content, false);
                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                DialogsTouCai.hideProgressDialog(ActAgentTeamBetDetail.this);
            }
        });
    }
}
