package com.desheng.app.toucai.panel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.pearl.act.base.AbBaseFragment;

public class FragSlidingMenu extends AbBaseFragment  implements SwipeRefreshLayout.OnRefreshListener{
    @Override
    public void onRefresh() {
    
    }
    
    @Override
    public int getLayoutId() {
        return 0;
    }
    
    @Override
    public void init(View root) {
    
    }
 /*
    private RelativeLayout list;
    private RelativeLayout vgTop;
    private TextView tvUserType;
    private ImageView ivPotrait;
    private TextView tvSlideAccount;
    private TextView tvSlideBalance;
    private LinearLayout vgMiddle;
    private RelativeLayout vgSlideDeposit;
    private TextView tvSlideDeposit;
    private RelativeLayout vgSlideWidthdraw;
    private TextView tvSlideWidthdraw;
    
    private RecyclerView rvSlideLottery;
    
    private List<SlidingMenuBean> listUI;
    private WidthDrawAdapter adapter;
    private SwipeRefreshLayout srlSlideRefresh;

    private boolean isInited;
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_sliding_menu;
    }
    
    @Override
    public void init(View root) {
        listUI = new ArrayList<>();
        initListUIBean();
        
        vgTop = (RelativeLayout) root.findViewById(R.id.vgSlideTop);
        ivPotrait = (ImageView) root.findViewById(R.id.ivSlidePotrait);
        tvUserType = (TextView) root.findViewById(R.id.tvSlideUserType);
        tvSlideAccount = (TextView) root.findViewById(R.id.tvSlideAccount);
        tvSlideBalance = (TextView) root.findViewById(R.id.tvSlideBalance);
        
        vgMiddle = (LinearLayout) root.findViewById(R.id.vgSlideMiddle);
        tvSlideDeposit = (TextView) root.findViewById(R.id.tvSlideDeposit);
        vgSlideDeposit = (RelativeLayout) root.findViewById(R.id.vgSlideDeposit);
        
        tvSlideWidthdraw = (TextView) root.findViewById(R.id.tvSlideWidthdraw);
        vgSlideWidthdraw = (RelativeLayout) root.findViewById(R.id.vgSlideWidthdraw);
    
    
        srlSlideRefresh = (SwipeRefreshLayout)root.findViewById(R.id.srlSlideRefresh);
        srlSlideRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlSlideRefresh.setOnRefreshListener(this);
        rvSlideLottery = (RecyclerView)root.findViewById(R.id.rvSlideLottery);
        rvSlideLottery.setLayoutManager(Views.genLinearLayoutManagerV(getActivity()));
    
        adapter = new WidthDrawAdapter(getActivity(), listUI);
        adapter.bindToRecyclerView(rvSlideLottery);
        rvSlideLottery.setAdapter(adapter);
        
        isInited = true;
    }
    
    
    @Override
    public void onShow() {
        super.onShow();
        refresh();
    }
    
    @Override
    public void onPrepareRefresh() {
        refresh();
    }
    
    
    
    private void initListUIBean() {
        List<String> arrKeys = Arrays.asList(LotteryType.CODE_INDEX);
        ArrayList<LotteryInfo> listLotterys = null;
        for (int i = 0; i < arrKeys.size(); i++) {
            ILotteryType byCode = LotteryType.find(arrKeys.get(i));
            listUI.add(SlidingMenuBean.createTitle(byCode.getShowName(), byCode.getImage()));
            listLotterys = (ArrayList<LotteryInfo>)LotteryKind.getInfosOfShowType(arrKeys.get(i));
            listUI.add(SlidingMenuBean.createLotteryGroup(listLotterys));
        }
        listUI.add(SlidingMenuBean.createActivity("优惠活动", ENV.curr.host + "/activity/1"));
    }
    
    public void refresh() {
        if(!UserManagerTouCai.getIns().isLogined() || !isInited){
            return;
        }
        tvUserType.setText("会员类型 : " + UserManagerTouCai.getIns().getUserTypeName());
        tvSlideAccount.setText(UserManagerTouCai.getIns().getCN());
        String str = String.format("<font color=\"#FFFF2C\">%s </font> 元",
                Nums.formatDecimal(UserManagerTouCai.getIns().getLotteryAvailableBalance(), 3));
        tvSlideBalance.setText(Html.fromHtml(str));
        vgSlideDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbBaseActivity.simpleLaunch(getActivity(), ActDeposit.class);
            }
        });
        vgSlideWidthdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActWithdrawalsTouCai.launchForStage1(getActivity());
            }
        });
        HttpActionTouCai.getOpenLotterys(this, new AbHttpResult() {
            
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                srlSlideRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        srlSlideRefresh.setRefreshing(true);
                    }
                });
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
                        SlidingMenuBean slidingMenuBean = listUI.get(i);
                        if(slidingMenuBean.getItemType() == SlidingMenuBean.TYPE_LOTTERY_GROUP){
                            for (int j = 0; j < slidingMenuBean.getListLottery().size(); j++) {
                                LotteryInfo lottery = slidingMenuBean.getListLottery().get(j);
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
                srlSlideRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlSlideRefresh.setRefreshing(false);
                    }
                }, 400);
            }
        });
    }
    
    
    public static class WidthDrawAdapter extends BaseQuickAdapter<SlidingMenuBean, WidthDrawAdapter.ViewHolder> {
        private Context ctx;
        
        public WidthDrawAdapter(Context ctx, @Nullable List<SlidingMenuBean> data) {
            super(data);
            this.ctx = ctx;
            setMultiTypeDelegate(new UITypeDelegate());
        }
        
        @Override
        protected void convert(final ViewHolder helper, final SlidingMenuBean item) {
            if(item.getItemType() == SlidingMenuBean.TYPE_TITLE){
                helper.tvTitle.setText(item.getTitle());
                String icon = item.getTitleImage();
                if (icon.startsWith("http://") || icon.startsWith("https://")) {
                    Glide.with(ctx).load(icon).into(helper.ivType);
                } else if (icon.startsWith("file:///")) {
                    Glide.with(ctx).load(Uri.parse(icon)).into(helper.ivType);
                }
                helper.setVisibility(true);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(helper.getAdapterPosition() + 1 < getItemCount()){
                            SlidingMenuBean slidingMenuBean = getData().get(helper.getAdapterPosition() + 1);
                            slidingMenuBean.isVisible = !(slidingMenuBean.isVisible);
                            notifyDataSetChanged();
                        }
                    }
                });
            }else if(item.getItemType() == SlidingMenuBean.TYPE_LOTTERY_GROUP){
                AdapterLotteryKindNameRecycler childAdapter = new AdapterLotteryKindNameRecycler(ctx, item.getListLottery());
                helper.rvLotteryGroup.setAdapter(childAdapter);
                helper.rvLotteryGroup.addItemDecoration(new ItemDeviderDecoration(ctx, ItemDeviderDecoration.VERTICAL_LIST, R.drawable.sh_devider_h_dark));
                childAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        LotteryInfo info = (LotteryInfo) view.getTag();
                        if(Maps.value(info.getExtra(), "isOpened", false)){
                            if(ArraysAndLists.findIndexWithEqualsOfArray(info.getKind().getOriginType().getCode(), new String[]{"jd"}) != -1) {
                                ActLotteryPlayJD.launchForStage1((Activity) ctx, info.getId());
                            }else if (ArraysAndLists.findIndexWithEqualsOfArray(info.getKind().getOriginType().getCode(), new String[]{"lhc"}) != -1) {
                                ActLotteryPlayLHC.launchForStage1((Activity) ctx, info.getId());
                            }else{
                                ActLotteryPlay.launchForStage1((Activity) ctx, info.getId());
                            }
                        }else{
                            Toasts.show(ctx, "游戏未开放!", false);
                        }
                    }
                });
                helper.setVisibility(item.isVisible);
            } else if (item.getItemType() == SlidingMenuBean.TYPE_ACTIVITY) {
                helper.tvText.setText(item.activityTitle);
                helper.tvText.setPadding(0, Views.dp2px(15), 0, 0);
                helper.tvText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActWebX5.launchForStage1(ctx, "优惠活动", item.activityUrl, true);
                    }
                });
            }
        }
        
        public class ViewHolder extends BaseViewHolder {
            public TextView tvTitle;
            public TextView tvText;
            public ImageView ivType;
            public RecyclerView rvLotteryGroup;
            
            public ViewHolder(View view) {
                super(view);
                if(view.getId() == R.id.vgLotteryGroup){
                    rvLotteryGroup = (RecyclerView) view.findViewById(R.id.rvLotteryGroup);
                    rvLotteryGroup.setLayoutManager(Views.genLinearLayoutManagerV(ctx));
                } else if (view.getId() == R.id.vgLotteryTitle) {
                    tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                    ivType = (ImageView) view.findViewById(R.id.ivType);
                } else if (view.getId() == R.id.vgTextGroup) {
                    tvText = (TextView) view.findViewById(R.id.tvText);
                }
            }
    
            public void setVisibility(boolean isVisible){
                RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
                if (isVisible){
                    param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    itemView.setVisibility(View.VISIBLE);
                }else{
                    itemView.setVisibility(View.GONE);
                    param.height = 0;
                    param.width = 0;
                }
                itemView.setLayoutParams(param);
            }
        }
    }
    
    
    public static class UITypeDelegate extends MultiTypeDelegate<SlidingMenuBean> {
        public UITypeDelegate() {
            registerItemType(SlidingMenuBean.TYPE_TITLE, R.layout.item_game_lottery_title);
            registerItemType(SlidingMenuBean.TYPE_LOTTERY_GROUP, R.layout.item_game_lottery_group);
            registerItemType(SlidingMenuBean.TYPE_ACTIVITY, R.layout.item_black_center_text);
        }
        
        @Override
        protected int getItemType(SlidingMenuBean bean) {
            return bean.getItemType();
        }
    }
    
    protected static class SlidingMenuBean {
        public static final int TYPE_TITLE = 1;
        public static final int TYPE_LOTTERY_GROUP = 2;
        public static final int TYPE_ACTIVITY = 3;
        
        private int itemType = 0;
        
        private String title;
        private String titleImage;
        private String activityTitle;
        private String activityUrl;
        List<LotteryInfo> listLottery;
        public boolean isVisible;
    
        public static SlidingMenuBean createActivity(String activityTitle, String activityUrl) {
            SlidingMenuBean bean = new SlidingMenuBean();
            bean.itemType = TYPE_ACTIVITY;
            bean.activityTitle = activityTitle;
            bean.activityUrl= activityUrl;
            return bean;
        }
        
        public static SlidingMenuBean createTitle(String title, String titleImage) {
            SlidingMenuBean bean = new SlidingMenuBean();
            bean.itemType = TYPE_TITLE;
            bean.title = title;
            bean.titleImage = titleImage;
            return bean;
        }
        public static SlidingMenuBean createLotteryGroup(List<LotteryInfo> listLottery) {
            SlidingMenuBean bean = new SlidingMenuBean();
            bean.itemType = TYPE_LOTTERY_GROUP;
            bean.listLottery = listLottery;
            return bean;
        }
        
        
        public SlidingMenuBean() {
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
    
        public String getTitleImage() {
            return titleImage;
        }
    
        public void setListLottery(ArrayList<LotteryInfo> listLottery) {
            this.listLottery = listLottery;
        }
        
    }
    */
    
}
