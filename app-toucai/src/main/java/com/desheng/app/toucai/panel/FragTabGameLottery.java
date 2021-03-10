package com.desheng.app.toucai.panel;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Maps;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.adapter.AdapterLotteryKindIconNameRecycler;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.base.model.LotteryInfo;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.SpaceTopDecoration;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 博彩游戏页面
 * Created by lee on 2018/3/7.
 */
@Deprecated
public class FragTabGameLottery extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    
    private RecyclerView rvLottery;
    
    private List<UILotteryGroupTitledBean> listUI;
    private LotteryAdapter adapter;
    private SwipeRefreshLayout srlRefresh;
    
    public static Fragment newIns(Bundle bundle) {
        Fragment fragment = new FragTabGameLottery();
        fragment.setArguments(bundle);
        return fragment;
    }
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_game_lottery;
    }
    
    @Override
    public void init(View root) {
        listUI = new ArrayList<>();
    
        srlRefresh = (SwipeRefreshLayout)root.findViewById(R.id.srlRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        rvLottery = (RecyclerView)root.findViewById(R.id.rvLottery);
        rvLottery.setLayoutManager(Views.genLinearLayoutManagerV(getActivity()));
        
        adapter = new LotteryAdapter(getActivity(), listUI);
        adapter.bindToRecyclerView(rvLottery);
        rvLottery.setAdapter(adapter);
    }
    
    @Override
    public void onShow() {
        super.onShow();
        refresh();
    }
    
    @Override
    public void onRefresh() {
        refresh();
    }
    
    private void initListUI() {
        /*Gson gson = new Gson();
        List<String> arrKeys = Arrays.asList(LotteryType.CODE_INDEX);
        List<LotteryInfo> listLotterys = null;
        for (int i = 0; i < arrKeys.size(); i++) {
            listUI.add(new UILotteryGroupTitledBean(UILotteryGroupTitledBean.TYPE_TITLE, LotteryType.find(arrKeys.get(i)).getShowName(), null));
            listLotterys = (List<LotteryInfo>) LotteryKind.getInfosOfShowType(arrKeys.get(i));
            listUI.add(new UILotteryGroupTitledBean(UILotteryGroupTitledBean.TYPE_LOTTERY_GROUP, null, listLotterys));
        }*/
    }
    
    public void refresh() {
        HttpActionTouCai.getOpenLotterys(this, new AbHttpResult() {
        
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
                    HashMap<Integer, Boolean> mapLottery = new HashMap<>();
                    for (int i = 0; i < listOpened.size(); i++) {
                        mapLottery.put(listOpened.get(i).getId(), true);
                    }
                    //处理打开的彩票
                    for (int i = 0; i < listUI.size(); i++) {
                        UILotteryGroupTitledBean uiLotteryGroupTitledBean = listUI.get(i);
                        if(uiLotteryGroupTitledBean.getItemType() == UILotteryGroupTitledBean.TYPE_LOTTERY_GROUP){
                            for (int j = 0; j < uiLotteryGroupTitledBean.getListLottery().size(); j++) {
                                LotteryInfo lottery = uiLotteryGroupTitledBean.getListLottery().get(j);
                                if(mapLottery.get(lottery.getId()) != null){
                                    lottery.getExtra().put("isOpened", true);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
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
    
    
    public static class LotteryAdapter extends BaseQuickAdapter<UILotteryGroupTitledBean, LotteryAdapter.LotteryViewHolder> {
        private Context ctx;
        
        public LotteryAdapter(Context ctx, @Nullable List<UILotteryGroupTitledBean> data) {
            super(data);
            this.ctx = ctx;
            setMultiTypeDelegate(new UITypeDelegate());
        }
        
        @Override
        protected void convert(final LotteryViewHolder helper, final UILotteryGroupTitledBean item) {
            if(item.getItemType() == UILotteryGroupTitledBean.TYPE_TITLE){
                helper.tvTitle.setText(item.getTitle());
            }else if(item.getItemType() == UILotteryGroupTitledBean.TYPE_LOTTERY_GROUP){
                AdapterLotteryKindIconNameRecycler childAdapter = new AdapterLotteryKindIconNameRecycler(ctx, item.getListLottery());
                helper.rvLotteryGroup.setAdapter(childAdapter);
                childAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        LotteryInfo info = (LotteryInfo) view.getTag();
                        if(Maps.value(info.getExtra(), "isOpened", false)){
                            CtxLotteryTouCai.launchLotteryPlay(ctx, info.getId());
                        }else{
                            Toasts.show(ctx, "游戏未开放!", false);
                        }
                    }
                });
            }
        }
    
        public class LotteryViewHolder extends BaseViewHolder {
            public TextView tvTitle;
            public RecyclerView rvLotteryGroup;
        
            public LotteryViewHolder(View view) {
                super(view);
                if(view.getId() == R.id.vgLotteryGroup){
                    rvLotteryGroup = (RecyclerView) view.findViewById(R.id.rvLotteryGroup);
                    rvLotteryGroup.setLayoutManager(Views.genGridLayoutManager(ctx, 4));
                    rvLotteryGroup.addItemDecoration(new SpaceTopDecoration(Views.dp2px(10)));
                } else if (view.getId() == R.id.vgLotteryTitle) {
                    tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                }
            }
        
            public TextView getTvTitle() {
                return tvTitle;
            }
        
            public void setTvTitle(TextView tvTitle) {
                this.tvTitle = tvTitle;
            }
        
            public RecyclerView getRvLotteryGroup() {
                return rvLotteryGroup;
            }
        
            public void setRvLotteryGroup(RecyclerView rvLotteryGroup) {
                this.rvLotteryGroup = rvLotteryGroup;
            }
        }
    }
    
   
    
    public static class UITypeDelegate extends MultiTypeDelegate<UILotteryGroupTitledBean> {
        public UITypeDelegate() {
            registerItemType(UILotteryGroupTitledBean.TYPE_TITLE, R.layout.item_game_lottery_title);
            registerItemType(UILotteryGroupTitledBean.TYPE_LOTTERY_GROUP, R.layout.item_game_lottery_group);
        }
        
        @Override
        protected int getItemType(UILotteryGroupTitledBean bean) {
            return bean.getItemType();
        }
    }
    
    protected static class UILotteryGroupTitledBean {
        public static final int TYPE_TITLE = 1;
        public static final int TYPE_LOTTERY_GROUP = 2;
        
        private int itemType = 0;
        
        private String title;
        List<LotteryInfo> listLottery;
    
        public UILotteryGroupTitledBean(int itemType, String title, List<LotteryInfo> listLottery) {
            this.itemType = itemType;
            this.title = title;
            this.listLottery = listLottery;
        }
    
        public int getItemType() {
            return itemType;
        }
    
        public void setItemType(int itemType) {
            this.itemType = itemType;
        }
    
        public String getTitle() {
            return title;
        }
    
        public void setTitle(String title) {
            this.title = title;
        }
    
        public List<LotteryInfo> getListLottery() {
            return listLottery;
        }
    
        public void setListLottery(ArrayList<LotteryInfo> listLottery) {
            this.listLottery = listLottery;
        }
    }
}
