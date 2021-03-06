package com.desheng.app.toucai.panel;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Nums;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.model.BonusRecordItem;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.mypicker.DateUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragSuperPartnerBonusRecord extends AbBaseFragment {
    int currentPage = 0;
    final int size = 20;
    private TextView amount;
    private RecyclerView recyclerView;
    private View emptyView;
    private SmartRefreshLayout smartRefreshLayout;
    BonusRecordAdapter bonusRecordAdapter = new BonusRecordAdapter();

    @Override
    public int getLayoutId() {
        return R.layout.frag_super_partner_bonus_record;
    }

    @Override
    public void init(View root) {
        amount = root.findViewById(R.id.tv_amount);
        emptyView = root.findViewById(R.id.layout_nodata);
        smartRefreshLayout = ((SmartRefreshLayout) root.findViewById(R.id.smartRefreshLayout));
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setOnLoadMoreListener(l -> {
            initBonusRecord(currentPage + 1);
        });
        recyclerView = ((RecyclerView) root.findViewById(R.id.recycleView));
        recyclerView.setAdapter(bonusRecordAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        initBonusRecord(currentPage);
    }

    public void initBonusRecord(int page) {
//        Calendar today = Calendar.getInstance();
//        Calendar yesterday = (Calendar) today.clone();
//        yesterday.set(Calendar.HOUR_OF_DAY,0);
//        yesterday.set(Calendar.MINUTE,0);
//        yesterday.set(Calendar.SECOND,0);
//        initBonusRecord(
//                null,
//                DateUtil.formatDate(yesterday.getTimeInMillis(), DateUtil.ymdhms),
//                DateUtil.formatDate(today.getTimeInMillis(), DateUtil.ymdhms),
//                page, null);
        initBonusRecord(
                mBonusType,
                mBonusStartDate,
                mBonusEndDate,
                page, mBonusStatus);

    }

    String mBonusType;
    String mBonusStatus;
    String mBonusStartDate;
    String mBonusEndDate;

    public void initBonusRecord(String type, String startDate, String endDate, int page, String status) {
        mBonusType = type;
        mBonusStatus = status;
        mBonusStartDate = startDate;
        mBonusEndDate = endDate;
        HttpActionTouCai.getRecommendAwardList(
                this,
                mBonusStartDate,
                mBonusEndDate,
                null,
                null,
                mBonusType,
                mBonusStatus,
                page,
                size,
                new AbHttpResult() {
                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        entity.putField("data", new TypeToken<BonusRecordItem>() {
                        }.getType());
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        if (code == 0 && error == 0) {
                            if (page == 0) {
                                bonusRecordAdapter.data.clear();
                                bonusRecordAdapter.notifyDataSetChanged();
                            }

                            BonusRecordItem list = getField(extra, "data", null);

                            if (list != null && list.getList() != null && list.getList().size() > 0) {
                                bonusRecordAdapter.data.addAll(list.getList());
                                bonusRecordAdapter.notifyDataSetChanged();
                                amount.setText(Nums.formatDecimal(list.getStatistics().getRealAmount(), 2));
                                currentPage = page;
                            }
                        }

                        return true;
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        smartRefreshLayout.finishLoadMore();
                        if (bonusRecordAdapter.getItemCount() == 0) {
                            smartRefreshLayout.setEnableLoadMore(false);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            smartRefreshLayout.setEnableLoadMore(true);
                            emptyView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    class BonusRecordAdapter extends RecyclerView.Adapter<BonusRecordAdapter.BonusRecordViewHolder> {
        List<BonusRecordItem.ListBean> data = new ArrayList<>();

        @Override
        public BonusRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview;
            itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_super_partner_bonus_record, parent, false);

            return new BonusRecordViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(BonusRecordViewHolder holder, int position) {
            BonusRecordItem.ListBean item = data.get(position);
            if (TextUtils.isEmpty(item.getRemitTime())) {
                holder.date.setVisibility(View.GONE);
                holder.time.setText("-");
            } else {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(DateUtil.formatDateStr(item.getRemitTime(), DateUtil.ymdhms, DateUtil.ymd));
                holder.time.setText(DateUtil.formatDateStr(item.getRemitTime(), DateUtil.ymdhms, DateUtil.hms));
            }
            holder.money.setText(Nums.formatDecimal(item.getRealAmount(), 3));

            switch (item.getIsSingleAward()) {
            case 0:
                holder.type.setText("?????????/???????????????");
                break;
            case 1:
                holder.type.setText("??????????????????");
                break;
            default:
                holder.type.setText("");
            }

            switch (item.getRemitState()) {
            case 1:
                holder.status.setText("??????");
                holder.status.setTextColor(0xFF323232);
                break;
            case 2:
                holder.status.setText("?????????");
                holder.status.setTextColor(0xFFaaaaaa);
                break;
            case 3:
                holder.status.setText("?????????");
                holder.status.setTextColor(0xFFff2638);
                break;
            case 4:
                holder.status.setText("???????????????");
                holder.status.setTextColor(0xFF323232);
                break;
            default:
                holder.status.setText("");
                holder.status.setTextColor(0xFF323232);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class BonusRecordViewHolder extends RecyclerView.ViewHolder {
            TextView time;
            TextView date;
            TextView money;
            TextView type;
            TextView status;

            public BonusRecordViewHolder(View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.tv_time);
                date = itemView.findViewById(R.id.tv_date);
                money = itemView.findViewById(R.id.tv_money);
                type = itemView.findViewById(R.id.tv_type);
                status = itemView.findViewById(R.id.tv_status);
            }
        }
    }
}
