package com.desheng.app.toucai.panel;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Views;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.UIHelperTouCai;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.model.OpenLottery;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shark.tc.R;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 彩票开奖页面
 * Created by  on 17/9/5.
 */
public class FragTabOpen extends AbBaseFragment implements View.OnClickListener, IRefreshableFragment {
    private SmartRefreshLayout srlRefresh;
    private RecyclerView rvOpen;

    private ArrayList<OpenLottery> listData = new ArrayList<>();
    private AdapterOpenLottery adapter;

    public static FragTabOpen newIns(Bundle bundle) {
        FragTabOpen fragment = new FragTabOpen();
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_open;
    }

    @Override
    public void init(View root) {
        srlRefresh = (SmartRefreshLayout) root.findViewById(R.id.srlRefresh);
        srlRefresh.setEnableLoadMore(false);
        srlRefresh.setEnableRefresh(true);
        srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh(null, null);
            }
        });

        rvOpen = (RecyclerView) root.findViewById(R.id.rvOpen);
        rvOpen.setLayoutManager(Views.genLinearLayoutManagerV(getActivity()));
        adapter = new AdapterOpenLottery(getActivity(), listData);
        rvOpen.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (listData.size() != 0 && position < listData.size()) {
                    CtxLotteryTouCai.launchLotteryPlay(getActivity(), listData.get(position).getLotteryId());//进入彩票
                }
            }
        });
    }

    @Override
    public void onShow() {
        refresh(null, null);
    }

    @Override
    public void refresh(String eventName, Bundle newBundle) {
        if (!UserManagerTouCai.getIns().isLogined()) {
            return;
        }
        HttpActionTouCai.getLastOpen(this, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(context,"");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<OpenLottery>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                listData = getField(extra, "data", null);
                if (code == 0 && listData != null) {
                    for (int i = 0; i < listData.size(); i++) {
                        listData.get(i).setItemType(OpenLottery.TYPE_LOTTERY);
                    }

                    adapter.setNewData(listData);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(context);
                srlRefresh.finishRefresh();
            }
        });
    }

    @Override
    public void onHide() {
        super.onHide();
    }

    @Override
    public void onClick(View v) {

    }

    protected static class AdapterOpenLottery extends BaseQuickAdapter<OpenLottery, AdapterOpenLottery.OpenLotteryHolder> {

        private Context ctx;

        public AdapterOpenLottery(Context ctx, List<OpenLottery> data) {
            super(R.layout.item_open_lottery, data);
            this.ctx = ctx;

            //setMultiTypeDelegate(new OpenLotteryTypeDelegate());
        }

        @Override
        protected void convert(OpenLotteryHolder helper, OpenLottery item) {
            helper.tvTitle.setText(item.getName());
            helper.tvIssueNo.setText(item.getIssueNo() + "期");

            ILotteryKind kind = LotteryKind.find(item.getLotteryId());

            //开奖号球
            String[] arrCode = item.getOpenCode().split(",");
            int num = 0;
            TextView tvNum = null;
            Glide.with(mContext).load(Uri.parse(kind.getIconActive())).into(helper.mLogo);
            helper.llBallsBlue.removeAllViews();
            for (int i = 0; i < arrCode.length; i++) {
                tvNum = (TextView) LayoutInflater.from(ctx).inflate(R.layout.view_ball_blue, helper.llBallsBlue, false);
                tvNum.setText(arrCode[i]);
                tvNum.setBackgroundDrawable(Views.fromDrawables(R.drawable.sh_bg_ball_blue_solid));
                GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                //bg.setColor(Views.fromColors(R.color.colorPrimary));
                bg.setColor(ctx.getResources().getColor(R.color.red_ff2c66));
                //bg.setStroke(1, UIHelperTouCai.getNumBgColor(num));
                helper.llBallsBlue.addView(tvNum);
            }
            helper.llBallsBlue.requestLayout();

        }

        public static class OpenLotteryHolder extends BaseViewHolder {
            public TextView tvTitle;
            public TextView tvIssueNo;
            public FlowLayout llBallsBlue;
            public View vDevider;
            public ImageView mLogo;

            public OpenLotteryHolder(View view) {
                super(view);
                tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                tvIssueNo = (TextView) view.findViewById(R.id.tvIssueNoLab);
                llBallsBlue = (FlowLayout) view.findViewById(R.id.llBallsBlue);
                vDevider = (View) view.findViewById(R.id.vDevider);
                mLogo = (ImageView) view.findViewById(R.id.logo);
            }
        }
    
       /* public static class OpenLotteryTypeDelegate extends MultiTypeDelegate<OpenLottery> {
            public OpenLotteryTypeDelegate() {
                registerItemType(OpenLottery.TYPE_LOTTERY_GROUP, R.layout.item_open_lottery);
            }
        
            @Override
            protected int getItemType(OpenLottery userAttentionItem) {
                return userAttentionItem.getItemType();
            }
        }*/
    }

}




