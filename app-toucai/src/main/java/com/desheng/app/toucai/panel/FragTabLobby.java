package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.http.AbHttpResult;
import com.ab.thread.ValueRunnable;
import com.ab.util.ArraysAndLists;
import com.ab.util.Strs;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.context.LotteryTypeCustom;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.LotteryInfoCustom;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.manager.UserManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.SpaceTopDecoration;
import com.pearl.view.ViewHelper;
import com.pearl.view.expandablelayout.SimpleExpandableLayout;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Request;

/**
 * 彩厅
 * Created by lee on 2018/4/11.
 */
@Deprecated
public class FragTabLobby extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final int[] arrResId = new int[]{
            R.mipmap.bg_type_10,
            R.mipmap.bg_type_11,
            R.mipmap.bg_type_12,
            R.mipmap.bg_type_13};

    public static final String[] arrRefresh = {UserManager.EVENT_USER_LOGINED,
            UserManager.EVENT_USER_LOGOUTED};

    private UserManager.EventReceiver receiver;
    private SwipeRefreshLayout srLobbyRefresh;
    private RecyclerView rvLottery;
    private View loginGroup;
    private View vEmpty;

    private AdapterLotteryCategory adapter;
    private LotteryTypeCustom hotCustom;
    private ArrayList<LotteryTypeCustom> listType = new ArrayList<LotteryTypeCustom>();

    public static double IMG_ASPACT = 300 / 1035.0;
    private boolean left_adv_cancled_flag;
    private boolean right_adv_cancled_flag;
    private Dialog mAdvDialog;

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_lobby;
    }

    @Override
    public void init(final View root) {

        srLobbyRefresh = (SwipeRefreshLayout) root.findViewById(R.id.srLobbyRefresh);
        srLobbyRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srLobbyRefresh.setOnRefreshListener(this);

        vEmpty = root.findViewById(R.id.vEmpty);
        rvLottery = (RecyclerView) root.findViewById(R.id.rvLottery);
        rvLottery.setLayoutManager(Views.genLinearLayoutManagerV(getActivity()));
        adapter = new AdapterLotteryCategory(getActivity(), listType);

        loginGroup = root.findViewById(R.id.vgOperation);
        loginGroup.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager.getIns().redirectToLogin(getActivity());
            }
        });
        loginGroup.findViewById(R.id.btnRegist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActRegistTouCai.launch(getActivity());
            }
        });
        rvLottery.setAdapter(adapter);
        ((SimpleItemAnimator) rvLottery.getItemAnimator()).setSupportsChangeAnimations(false);

        receiver = new UserManager.EventReceiver().register(getActivity(), new UserManager.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (ArraysAndLists.findIndexWithEquals(eventName, arrRefresh) != -1) {
                    updateUserInfo();
                }
            }
        });
    }


    @Override
    public void onShow() {
        super.onShow();
        updateUserInfo();
    }


    @Override
    public void onRefresh() {
        updateUserInfo();
    }

    public void updateUserInfo() {
        if (UserManager.getIns().isLogined()) {
            vEmpty.setVisibility(View.GONE);
            loginGroup.setVisibility(View.GONE);
        } else {
            vEmpty.setVisibility(View.VISIBLE);
            loginGroup.setVisibility(View.VISIBLE);
        }
        initLotteryInfo();
    }

    private void initLotteryInfo() {
        HttpActionTouCai.getLotteryRoom(this, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                srLobbyRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        srLobbyRefresh.setRefreshing(true);
                    }
                });
            }
        
            /*@Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<LotteryTypeCustom>>() {
                }.getType());
            }*/

            @Override
            public boolean onGetString(String str) {
                if (Strs.isNotEmpty(str)) {
                    listType.clear();
                    listType.addAll((Collection<? extends LotteryTypeCustom>) new Gson().fromJson(str.replace("\\n", ""), new TypeToken<ArrayList<LotteryTypeCustom>>() {
                    }.getType()));
                    Collections.sort(listType, new Comparator<LotteryTypeCustom>() {
                        @Override
                        public int compare(LotteryTypeCustom o1, LotteryTypeCustom o2) {
                            return o1.getSort() - o2.getSort();
                        }
                    });
                    UserManager.getIns(UserManagerTouCai.class).updateLotteryType(listType);
                    adapter.setDataSize(listType.size());
                    adapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                srLobbyRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srLobbyRefresh.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            receiver.unregister(getActivity());
        }
    }


    protected static class AdapterLotteryCategory extends BaseQuickAdapter<LotteryTypeCustom, AdapterLotteryCategory.LotteryCategoryVH> {
        private Context ctx;
        private boolean[] expandState;

        public AdapterLotteryCategory(Context ctx, List<LotteryTypeCustom> data) {
            super(R.layout.item_lobby, data);
            this.ctx = ctx;
        }

        public void setDataSize(int len) {
            expandState = new boolean[len];
        }

        @Override
        protected void convert(LotteryCategoryVH helper, LotteryTypeCustom item) {
            if (helper.gvLottery.getLayoutManager() == null) {
                helper.gvLottery.setLayoutManager(Views.genGridLayoutManager(ctx, 4));
                helper.gvLottery.addItemDecoration(new SpaceTopDecoration(Views.dp2px(10)));
            }

            helper.tvTitle.setText(item.getShowName());
            helper.tvTitleInfo.setText(item.getMeno());

            AdapterLotteryKindIconNameRecycler childAdapter = new AdapterLotteryKindIconNameRecycler(ctx, item.getLotteryList());
            childAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    LotteryInfoCustom lottery = (LotteryInfoCustom) adapter.getData().get(position);
                    ILotteryKind kind = LotteryKind.find(lottery.getId());
                    if (UserManager.getIns().isLogined()) {
                        DialogsTouCai.showProgressDialog((Activity) ctx, "");
                        CtxLotteryTouCai.launchLotteryPlay(ctx, kind);
                    } else {
                        UserManager.getIns().redirectToLogin((Activity) ctx);
                    }
                }
            });

            helper.gvLottery.setAdapter(childAdapter);
            int position = ViewHelper.getQuickAdapterPosition(this, helper);
            helper.elPlayExtra.setExpanded(expandState[position]);
            Views.loadImage(ctx, helper.ivTitleGroupBg, item.getImage(), AbDevice.SCREEN_WIDTH_PX - Views.dp2px(20));
            helper.ivTitleGroupBg.setTag(R.id.id_int_tag, position);
            helper.ivTitleGroupBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag(R.id.id_int_tag);
                    expandState[index] = !expandState[index];
                    v.post(new ValueRunnable<Integer>(index) {
                        @Override
                        public void run() {
                            notifyItemChanged((Integer) getValue());
                        }
                    });
                    for (int i = 0; i < expandState.length; i++) {
                        if (index != i) {
                            if (expandState[i]) {
                                expandState[i] = false;
                                v.post(new ValueRunnable<Integer>(i) {
                                    @Override
                                    public void run() {
                                        notifyItemChanged(getValue());
                                    }
                                });
                            }
                        }
                    }
                }
            });


        }

        protected static class LotteryCategoryVH extends BaseViewHolder {
            private ImageView ivTitleGroupBg;
            private TextView tvTitle, tvTitleInfo;
            //private NestedScrollView nsvPlayExtra;
            private RecyclerView gvLottery;
            private SimpleExpandableLayout elPlayExtra;

            public LotteryCategoryVH(View view) {
                super(view);
                if (((ViewGroup) view).getChildAt(0).getId() != R.id.vgOperation) {
                    tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                    tvTitleInfo = (TextView) view.findViewById(R.id.tvTitleInfo);
                    ivTitleGroupBg = (ImageView) view.findViewById(R.id.ivTitleGroupBg);
                    Views.setHeightByRatio(ivTitleGroupBg, IMG_ASPACT);
                    //nsvPlayExtra = (NestedScrollView) view.findViewById(R.id.nsvPlayExtra);
                    elPlayExtra = (SimpleExpandableLayout) view.findViewById(R.id.elPlayExtra);
                    gvLottery = (RecyclerView) view.findViewById(R.id.gvLottery);
                }

            }
        }
    }


    protected static class AdapterLotteryKindIconNameRecycler extends BaseQuickAdapter<LotteryInfoCustom, AdapterLotteryKindIconNameRecycler.LotteryVH> {

        private Context context;
        private List<LotteryInfoCustom> listData;

        public AdapterLotteryKindIconNameRecycler(Context context, List<LotteryInfoCustom> list) {
            super(R.layout.item_lottery_kind_icon_name, list);
            this.context = context;
            this.listData = list;
        }

        @Override
        protected void convert(AdapterLotteryKindIconNameRecycler.LotteryVH holder, LotteryInfoCustom item) {
            //网络图片和本地图片
            if (Strs.isEmpty(item.getAppIcon())) {
                item.setAppIcon(LotteryKind.find(item.getId()).getIconActive());
            }
            String icon = item.getAppIcon();
            Views.loadImage(context, holder.ivIcon, icon);
            holder.tvName.setText(item.getShowName().replace("经典", ""));
            holder.itemView.setTag(item);
        }

        public class LotteryVH extends BaseViewHolder {
            public ImageView ivIcon;
            public TextView tvName;

            public LotteryVH(View view) {
                super(view);
                tvName = (TextView) view.findViewById(R.id.tvName);
                ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
            }
        }
    }
}
