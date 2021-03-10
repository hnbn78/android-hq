package com.desheng.app.toucai.panel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.model.AccountChangeRecordBean;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.SpaceTopDecoration;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 */
public class FragTabTransferRecord extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvTransfer;
    private TransferAdapter mAdapter;
    private List<AccountChangeRecordBean> listData = new ArrayList<>();
    private LinearLayout layout_nodata;
    private TextView cbAmount;
    private TextView kyAmount;
    private TextView agAmount;
    private TextView imAmount;
    private TextView dsAmount;
    private TextView PTAmount;
    private TextView tv_thirdGame;

    private int page, size = 20;

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_transfer_record;
    }

    @Override
    public void init(View root) {
        srlRefresh = root.findViewById(R.id.srlRefresh);
        rvTransfer = root.findViewById(R.id.rvFund);
        layout_nodata = root.findViewById(R.id.layout_nodata);
        cbAmount = root.findViewById(R.id.tv_cbAmount);
        kyAmount = root.findViewById(R.id.tv_kyAmount);
        agAmount = root.findViewById(R.id.tv_agAmount);
        imAmount = root.findViewById(R.id.tv_imAmount);
        dsAmount = root.findViewById(R.id.tv_dsAmount);
        PTAmount = root.findViewById(R.id.tv_PTAmount);
        tv_thirdGame = root.findViewById(R.id.tv_thirdGame);

        tv_thirdGame.setText("转入" + CommonConsts.setThirdGameScreenOrintation(BaseConfig.custom_flag).getTitle().subSequence(0, 2) + "(元)");

        srlRefresh.setColorSchemeResources(com.desheng.base.R.color.colorPrimary, com.desheng.base.R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        rvTransfer.setLayoutManager(Views.genLinearLayoutManagerV(getActivity()));
        rvTransfer.addItemDecoration(new SpaceTopDecoration(7));

        mAdapter = new TransferAdapter(getActivity(), listData);
        rvTransfer.setAdapter(mAdapter);

        mAdapter.setEnableLoadMore(true);

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getTransferRecord(page, size);
            }
        }, rvTransfer);
        getTransferRecord(page, size);
    }

    @Override
    public void onRefresh() {
        page = 0;
        getTransferRecord(page, 20);
    }

    public void getTransferRecord(final int page, int size) {
        long currenttime = System.currentTimeMillis();
        String startTime = Dates.getStringByFormat(currenttime - 6 * 24 * 60 * 60 * 1000, Dates.dateFormatYMD);
        startTime = startTime + " 00:00:00";
        String stopTime = Dates.getStringByFormat(currenttime, Dates.dateFormatYMD) + " 23:59:59";
        HttpAction.getAccountChange(this, "", "99", "", startTime, stopTime, page, size, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                }, 100);
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespAccountChange.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    HttpAction.RespAccountChange respAccountChange = getField(extra, "data", null);

                    if (page == 0) {
                        listData.clear();
                    }

                    if (respAccountChange.statistics != null) {
                        cbAmount.setText(Nums.formatDecimal(respAccountChange.statistics.sobetAmount, 3));
                        imAmount.setText(Nums.formatDecimal(respAccountChange.statistics.imAmount, 3));
                        agAmount.setText(Nums.formatDecimal(respAccountChange.statistics.aginAmount, 3));
                        kyAmount.setText(Nums.formatDecimal(respAccountChange.statistics.kyAmount, 3));
                        dsAmount.setText(Nums.formatDecimal(respAccountChange.statistics.gmAmount, 3));
                        if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.PT.getPlatformId()) {
                            PTAmount.setText(Nums.formatDecimal(respAccountChange.statistics.ptAmount, 3));
                        } else if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.CQ.getPlatformId()) {
                            PTAmount.setText(Nums.formatDecimal(respAccountChange.statistics.cqAmount, 3));
                        }
                    }

                    listData.addAll(respAccountChange.list);

                    if (listData.size() >= respAccountChange.totalCount) {
                        mAdapter.loadMoreComplete();
                        mAdapter.setEnableLoadMore(false);
                    } else {
                        mAdapter.setEnableLoadMore(true);
                    }

                    layout_nodata.setVisibility(respAccountChange.list.size() == 0 ? View.VISIBLE : View.GONE);

                    mAdapter.notifyDataSetChanged();
                } else {
                    Toasts.show(getActivity(), msg);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    public static class TransferAdapter extends BaseQuickAdapter<AccountChangeRecordBean, TransferAdapter.ViewHolder> {
        private Context ctx;

        public TransferAdapter(Context ctx, @Nullable List<AccountChangeRecordBean> data) {
            super(R.layout.item_transfer_record, data);
            this.ctx = ctx;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);

        }

        @Override
        protected void convert(final TransferAdapter.ViewHolder helper, final AccountChangeRecordBean item) {
            helper.tv_transfer_time.setText(item.createTime);
            helper.tv_transfer_amount.setText("" + item.amount);

            if (!item.note.contains("转入到")) {
                return;
            }

            Log.d(TAG, "note---" + item.note);
            String left = item.note.substring(1, item.note.indexOf("转入到"));
            Log.d(TAG, "note---left:" + left);
            String right = item.note.substring(item.note.lastIndexOf("转入到") + 3, item.note.length());
            Log.d(TAG, "note---right:" + right);

            if (right.contains("ky")) {
                right = "KY棋牌";
            } else if (right.contains("im")) {
                right = "IM体育";
            } else if (right.contains("ag")) {
                right = "AGIN视讯";
            } else if (right.contains("ds")) {
                right = "DS棋牌";
            } else if (right.contains("pt")) {
                right = "PT电子";
            } else if (right.contains("cq")) {
                right = "CQ9电游";
            }

            if (left.contains("ky")) {
                left = "KY棋牌";
            } else if (left.contains("im")) {
                left = "IM体育";
            } else if (left.contains("ag")) {
                left = "AGIN视讯";
            } else if (left.contains("ds")) {
                left = "DS棋牌";
            } else if (left.contains("pt")) {
                left = "PT电子";
            } else if (left.contains("cq")) {
                left = "CQ9电游";
            }
            boolean isOut = item.changeTypeDetailStr.contains("转出");
            if (isOut) {
                helper.tv_transfer_left_name.setText(right);
                helper.tv_transfer_right_name.setText(left);
            } else {
                helper.tv_transfer_left_name.setText(left);
                helper.tv_transfer_right_name.setText(right);
            }
            if (right.toLowerCase().contains("ky")) {
                helper.iv_type.setImageResource(R.mipmap.ic_pocket_ky);
                helper.iv_arrow.setImageResource(isOut ? R.mipmap.ic_ky_label : R.mipmap.ic_ky_label1);
            } else if (right.toLowerCase().contains("ag")) {
                helper.iv_type.setImageResource(R.mipmap.ic_pocket_ag);
                helper.iv_arrow.setImageResource(isOut ? R.mipmap.ic_ag_label : R.mipmap.ic_ag_label1);
            } else if (right.toLowerCase().contains("im")) {
                helper.iv_type.setImageResource(R.mipmap.ic_pocket_im);
                helper.iv_arrow.setImageResource(isOut ? R.mipmap.ic_im_left : R.mipmap.ic_im_right);
            } else if (right.toLowerCase().contains("ds")) {
                helper.iv_type.setImageResource(R.mipmap.ic_pocket_ds);
                helper.iv_arrow.setImageResource(isOut ? R.mipmap.ic_im_left : R.mipmap.ic_im_right);
            } else if (right.toLowerCase().contains("pt")) {
                helper.iv_type.setImageResource(R.mipmap.ic_pt);
                helper.iv_arrow.setImageResource(isOut ? R.mipmap.ic_im_left : R.mipmap.ic_im_right);
            } else if (right.toLowerCase().contains("cq")) {
                helper.iv_type.setBackgroundResource(R.mipmap.ic_pocket_cq9);
                helper.iv_arrow.setImageResource(isOut ? R.mipmap.ic_im_left : R.mipmap.ic_im_right);
            } else {
                helper.iv_type.setImageResource(R.mipmap.ic_pocket_wallet);
                helper.iv_arrow.setImageResource(isOut ? R.mipmap.ic_ky_label : R.mipmap.ic_ky_label1);
            }

        }

        public class ViewHolder extends BaseViewHolder {

            ImageView iv_type;
            ImageView iv_arrow;
            TextView tv_transfer_left_name;
            TextView tv_transfer_right_name;
            TextView tv_transfer_amount;
            TextView tv_transfer_time;
            TextView tv_transfer_status;

            public ViewHolder(View view) {
                super(view);
                iv_type = view.findViewById(R.id.iv_type);
                iv_arrow = view.findViewById(R.id.iv_arrow);
                tv_transfer_left_name = view.findViewById(R.id.tv_transfer_left_name);
                tv_transfer_right_name = view.findViewById(R.id.tv_transfer_right_name);
                tv_transfer_amount = view.findViewById(R.id.tv_transfer_amount);
                tv_transfer_time = view.findViewById(R.id.tv_transfer_time);
                tv_transfer_status = view.findViewById(R.id.tv_transfer_status);

            }
        }
    }


}
