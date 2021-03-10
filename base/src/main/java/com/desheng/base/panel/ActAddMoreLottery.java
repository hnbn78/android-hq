package com.desheng.base.panel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Views;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryInfo;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.ItemDeviderDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 添加更多彩种
 * Created by lee on 2018/3/7.
 */
public class ActAddMoreLottery extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {

    private AddLotteryAdapter adapter;
    private RecyclerView rvLottery;
    private SwipeRefreshLayout srlRefresh;

    public static void launch(Context act) {
        Intent itt = new Intent(act, ActAddMoreLottery.class);
        act.startActivity(itt);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_more_lottery;
    }

    @Override
    protected void init() {
        setToolbarLeftBtn(UIHelper.BACK_WHITE_ARROR);
        if (UIHelper.toolbarBgResId > 0)
            setToolbarBgImage(UIHelper.toolbarBgResId);
        else
            setToolbarBgColor(R.color.colorPrimary);
        setToolbarTitleCenter("添加游戏");
        setToolbarTitleCenterColor(R.color.white);
        setStatusBarTranslucentAndLightContentWithPadding();

        srlRefresh = (SwipeRefreshLayout) findViewById(R.id.srlRefresh);
        srlRefresh.setOnRefreshListener(this);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);

        rvLottery = (RecyclerView) findViewById(R.id.rvLottery);
        adapter = new AddLotteryAdapter(this);
        rvLottery.setLayoutManager(Views.genLinearLayoutManagerV(this));
        rvLottery.addItemDecoration(new ItemDeviderDecoration(this, ItemDeviderDecoration.VERTICAL_LIST));
        rvLottery.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int id = ((LotteryInfo) adapter.getItem(position)).getId();
                if (UserManager.getIns().isLotteryUserSelected(id)) {
                    UserManager.getIns().removeUserSelectedLottery(id);
                } else {
                    UserManager.getIns().addUserSelectedLottery(id);
                }
                adapter.notifyDataSetChanged();
                UserManager.getIns().sendBroadcaset(ActAddMoreLottery.this, UserManager.EVENT_USER_SELETE_LOTTERY, null);
            }
        });

        refresh();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    public void refresh() {
        HttpAction.getOpenLotterys(this, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                srlRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                });
                srlRefresh.setEnabled(false);
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<LotteryInfo>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && getField(extra, "data", null) != null) {
                    List<LotteryInfo> listOpened = getFieldObject(extra, "data", ArrayList.class);
                    adapter.setNewData(listOpened);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                srlRefresh.setRefreshing(false);
                srlRefresh.setEnabled(true);
            }
        });
    }


    protected static class AddLotteryAdapter extends BaseQuickAdapter<LotteryInfo, AddLotteryAdapter.AddLotteryVH> {

        private final Context ctx;

        public AddLotteryAdapter(Context ctx) {
            super(R.layout.item_add_lottery, new ArrayList<LotteryInfo>());
            this.ctx = ctx;
        }

        @Override
        protected void convert(AddLotteryVH holder, LotteryInfo item) {
            ILotteryKind kind = item.getKind();

            Glide.with(ctx).load(Uri.parse(kind.getIconActive())).placeholder(R.drawable.ic_lottery_default).into(holder.ivIcon);

            holder.tvName.setText(item.getShowName());

            holder.ivChecked.setVisibility(UserManager.getIns().isLotteryUserSelected(item.getId()) ? View.VISIBLE : View.INVISIBLE);
        }

        protected static class AddLotteryVH extends BaseViewHolder {
            private ImageView ivIcon;
            private TextView tvName;
            private ImageView ivChecked;

            public AddLotteryVH(View view) {
                super(view);
                ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
                tvName = (TextView) view.findViewById(R.id.tvName);
                ivChecked = (ImageView) view.findViewById(R.id.ivChecked);
            }
        }
    }
}
