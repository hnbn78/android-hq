package com.desheng.base.panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.databinding.ActLotteryOpenBinding;
import com.desheng.base.model.LotteryOpenHistory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.ItemDeviderDecoration;
import com.pearl.view.SpaceTopDecoration;

import java.util.ArrayList;

import okhttp3.Request;

/**
 * 彩票开奖历史
 * Created by lee on 2018/3/7.
 */
public class ActLotteryOpen extends AbAdvanceActivity<ActLotteryOpenBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private OpenAdapter adapter;

    private int lotteryId;
    private ILotteryKind lottery;

    /**
     * @param activity
     * @param lotteryId 彩票id
     */
    public static void launch(Activity activity, int lotteryId) {
        Intent itt = new Intent(activity, ActLotteryOpen.class);
        itt.putExtra("lotteryId", lotteryId);
        activity.startActivity(itt);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_open;
    }

    @Override
    protected void init() {
        lotteryId = getIntent().getIntExtra("lotteryId", 0);
        lottery = (ILotteryKind) CtxLottery.getIns().findLotteryKind(lotteryId);

        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), lottery.getShowName());
        setStatusBarTranslucentAndLightContentWithPadding();

        getBinding().srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        getBinding().srlRefresh.setOnRefreshListener(this);

        adapter = new OpenAdapter(this);
        getBinding().rvLottery.setLayoutManager(Views.genLinearLayoutManagerV(this));
        getBinding().rvLottery.addItemDecoration(new SpaceTopDecoration(Views.dp2px(10)));
        getBinding().rvLottery.addItemDecoration(new ItemDeviderDecoration(this, ItemDeviderDecoration.VERTICAL_LIST));

        getBinding().rvLottery.setAdapter(adapter);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    public void refresh() {
        HttpAction.getLotteryOpenCodeHistory(this, lottery.getCode(), true, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getBinding().srlRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        getBinding().srlRefresh.setRefreshing(true);
                    }
                });
                getBinding().srlRefresh.setEnabled(false);
            }

            @Override
            public boolean onGetString(String str) {
                ArrayList<LotteryOpenHistory> listOpen = new Gson().fromJson(str.replace("\n", ""), new TypeToken<ArrayList<LotteryOpenHistory>>() {
                }.getType());
                adapter.setNewData(listOpen);
                return true;
            }

            @Override
            public void onAfter(int id) {
                getBinding().srlRefresh.setRefreshing(false);
                getBinding().srlRefresh.setEnabled(true);
            }
        });
    }


    protected class OpenAdapter extends BaseQuickAdapter<LotteryOpenHistory, OpenAdapter.OpenVH> {

        private final Context ctx;

        public OpenAdapter(Context ctx) {
            super(R.layout.item_lottery_open, new ArrayList<LotteryOpenHistory>());
            this.ctx = ctx;
        }

        @Override
        protected void convert(OpenVH holder, LotteryOpenHistory item) {
            String text = String.valueOf(Dates.getStringByFormat((item.getTime() <= 0 ? System.currentTimeMillis() / 1000 : item.getTime()) * 1000, Dates.dateFormatYMD));
            holder.tvTime.setText(text);
            holder.tvIssueNo.setText(item.getIssue());
            if ((item.getLottery().contains("11Y")||item.getLottery().contains("PK10")) && item.getCode() != null) {
                holder.tvIssueResult.setText(Nums.formatOpenCode(item.getCode()));
            } else {
                holder.tvIssueResult.setText(item.getCode());
            }

            if (holder.tvIssueResult.getText().toString().isEmpty()) {
                holder.tvIssueResult.setText("开奖中");
            }
        }

        protected class OpenVH extends BaseViewHolder {
            public TextView tvTime;
            public TextView tvIssueNo;
            public TextView tvIssueResult;

            public OpenVH(View view) {
                super(view);
                tvTime = (TextView) view.findViewById(R.id.tvTime);
                tvIssueNo = (TextView) view.findViewById(R.id.tvIssueNo);
                tvIssueResult = (TextView) view.findViewById(R.id.tvIssueResult);
            }
        }
    }
}
