package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ArraysAndLists;
import com.ab.util.Maps;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.app.hubert.guide.core.Controller;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.DBActionTouCai;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.context.LotteryTypeCustom;
import com.desheng.app.toucai.model.LotteryInfoCustom;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.UIHelperTouCai;
import com.desheng.base.action.DBAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.UserFavoriteLottery;
import com.desheng.base.model.UserPlayedLottery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.GuidePageHelper;
import com.pearl.view.PagerSlidingTabStrip;
import com.pearl.view.SpaceDecoration;
import com.pearl.view.advrecyclerview.animator.DraggableItemAnimator;
import com.pearl.view.advrecyclerview.animator.GeneralItemAnimator;
import com.pearl.view.advrecyclerview.data.AbstractDataProvider;
import com.pearl.view.advrecyclerview.decoration.ItemShadowDecorator;
import com.pearl.view.advrecyclerview.draggable.DraggableItemAdapter;
import com.pearl.view.advrecyclerview.draggable.ItemDraggableRange;
import com.pearl.view.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.pearl.view.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.pearl.view.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.pearl.view.advrecyclerview.utils.WrapperAdapterUtils;
import com.shark.tc.R;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class ActAddFavorite extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rvFavorite;
    private FavoriteDraggableGridAdapter mAdapterFavorite;
    private GridLayoutManager mLayoutManagerFavorite;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManagerFavorite;
    private RecyclerView.Adapter mWrappedAdapterFavorite;

    private RecyclerView rvRecent;

    private ArrayList<LotteryTypeCustom> listType = new ArrayList<LotteryTypeCustom>();
    private LotterCategoryAdapter pagerAdapter;

    private SwipeRefreshLayout srlRefresh;
    private PagerSlidingTabStrip tsTabs;
    private ViewPager vpContent;
    private View ivEmpty;
    private FavoriteDataProvider favoriteProvider;
    private LotteryKindAdapter recentAdapter;
    private NestedScrollView scrollView;

    public static void launch(Context act) {
        Intent itt = new Intent(act, ActAddFavorite.class);
        act.startActivity(itt);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_add_favorite;
    }

    @Override
    protected void init() {
        UIHelperTouCai.getIns().simpleToolbarLeftTextAndCenterTitleAndeRightTextBtn(this, getToolbar(), "取消",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, "自定义", "完成", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HttpActionTouCai.updateFavoriteGame(ActAddFavorite.this, favoriteProvider.getListIds(), new AbHttpResult() {
                            @Override
                            public void onBefore(Request request, int id, String host, String funcName) {
                                DialogsTouCai.showProgressDialog(ActAddFavorite.this, "");
                            }

                            @Override
                            public void setupEntity(AbHttpRespEntity entity) {
                                entity.putField("data", String.class);
                            }

                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                if (code == 0 && error == 0) {
                                    Toasts.show(ActAddFavorite.this, "保存成功", true);
                                    srlRefresh.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1000);
                                } else {
                                    Toasts.show(ActAddFavorite.this, msg, false);
                                }
                                return true;
                            }

                            @Override
                            public void onAfter(int id) {
                                DialogsTouCai.hideProgressDialog(ActAddFavorite.this);
                            }
                        });

                    }
                });
        setStatusBarTranslucentAndLightContentWithPadding();

        scrollView = findViewById(R.id.sv_scroller);
        //初始化下拉刷新
        srlRefresh = findViewById(R.id.srlRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        srlRefresh.setEnabled(false);

        //初始化喜爱的
        rvFavorite = getLayout().findViewById(R.id.rvFavorite);
        rvFavorite.addItemDecoration(new SpaceDecoration(Views.dp2px(6), Views.dp2px(6), Views.dp2px(6), Views.dp2px(6)));
        favoriteProvider = new FavoriteDataProvider();
        mAdapterFavorite = new FavoriteDraggableGridAdapter(this, favoriteProvider);
        Map mapFavorite = Maps.newIns();
        initDragableGrid(rvFavorite,
                mAdapterFavorite,
                mapFavorite
        );
        mLayoutManagerFavorite = (GridLayoutManager) mapFavorite.get("mLayoutManager");
        mRecyclerViewDragDropManagerFavorite = (RecyclerViewDragDropManager) mapFavorite.get("mRecyclerViewDragDropManager");
        mWrappedAdapterFavorite = (RecyclerView.Adapter) mapFavorite.get("mWrappedAdapter");

        //初始化最近的
        rvRecent = getLayout().findViewById(R.id.rvRecent);
        rvRecent.addItemDecoration(new SpaceDecoration(Views.dp2px(6), Views.dp2px(6), Views.dp2px(6), Views.dp2px(6)));
        rvRecent.setLayoutManager(Views.genGridLayoutManager(this, 4));
        ArrayList<LotteryInfoCustom> listRecent = new ArrayList<>();
        List<UserPlayedLottery> listPlayed = DBAction.queryAllUserPlayedLottery();
        if (listPlayed != null) {
            for (UserPlayedLottery played :
                    listPlayed) {
                listRecent.add(new LotteryInfoCustom(LotteryKind.find(played.lotteryId).getLotteryInfo()));
            }
        }
        recentAdapter = new LotteryKindAdapter(this, listRecent);
        rvRecent.setAdapter(recentAdapter);
        ivEmpty = findViewById(R.id.ivEmpty);
        if (ArraysAndLists.isEmpty(listRecent)) {
            rvRecent.setVisibility(View.GONE);
            ivEmpty.setVisibility(View.VISIBLE);
        } else {
            rvRecent.setVisibility(View.VISIBLE);
            ivEmpty.setVisibility(View.GONE);
        }

        //初始化viewpager
        tsTabs = (PagerSlidingTabStrip) findViewById(R.id.tsTabs);
        vpContent = (ViewPager) findViewById(R.id.vpContent);
        onRefresh();
    }

    private void initDragableGrid(RecyclerView recyclerView,
                                  FavoriteDraggableGridAdapter mAdapter,
                                  Map<String, Object> outParam) {
        GridLayoutManager mLayoutManager = null;
        RecyclerViewDragDropManager mRecyclerViewDragDropManager = null;
        RecyclerView.Adapter mWrappedAdapter = null;

        mLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();

        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(300);

        // setup dragging item effects (NOTE: DraggableItemAnimator is required)
        //mRecyclerViewDragDropManagerRecent.setDragStartItemAnimationDuration(250);
        //mRecyclerViewDragDropManagerRecent.setDraggingItemAlpha(0.8f);
        mRecyclerViewDragDropManager.setDraggingItemScale(1.1f);
        //mRecyclerViewDragDropManagerRecent.setDraggingItemRotation(15.0f);

        //adapter
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mAdapter);      // wrap for dragging

        GeneralItemAnimator animator = new DraggableItemAnimator(); // DraggableItemAnimator is required to make item animations properly.

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        recyclerView.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody

        recyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.sh_material_shadow)));


        mRecyclerViewDragDropManager.attachRecyclerView(recyclerView);

        int mode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_SWAP;

        mRecyclerViewDragDropManager.setItemMoveMode(mode);
        mAdapter.setItemMoveMode(mode);

        outParam.put("mLayoutManager", mLayoutManager);
        outParam.put("mRecyclerViewDragDropManager", mRecyclerViewDragDropManager);
        outParam.put("mWrappedAdapter", mWrappedAdapter);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    public void refresh() {
        HttpActionTouCai.getLotteryRoom(this, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                srlRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
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
                    pagerAdapter = new LotterCategoryAdapter(ActAddFavorite.this, listType);
                    pagerAdapter.setup();
                    vpContent.setAdapter(pagerAdapter);
                    tsTabs.setViewPager(vpContent);

                    vpContent.post(() -> setupGuidePage());
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


    @Override
    public void onPause() {
        if (mRecyclerViewDragDropManagerFavorite != null) {
            mRecyclerViewDragDropManagerFavorite.cancelDrag();
        }
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecyclerViewDragDropManagerFavorite != null) {
            mRecyclerViewDragDropManagerFavorite.release();
            mRecyclerViewDragDropManagerFavorite = null;
        }

        if (rvFavorite != null) {
            rvFavorite.setItemAnimator(null);
            rvFavorite.setAdapter(null);
            rvFavorite = null;
        }

        if (mWrappedAdapterFavorite != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapterFavorite);
            mWrappedAdapterFavorite = null;
        }
        mAdapterFavorite = null;
        mLayoutManagerFavorite = null;
    }


    protected class FavoriteDraggableGridAdapter
            extends RecyclerView.Adapter<FavoriteDraggableGridAdapter.FavoriteHolder>
            implements DraggableItemAdapter<FavoriteDraggableGridAdapter.FavoriteHolder> {
        private static final String TAG = "MyDraggableItemAdapter";
        private int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;
        private Context ctx;

        private AbstractDataProvider mProvider;

        protected class FavoriteHolder extends AbstractDraggableItemViewHolder {
            private final View ivAddBtn;
            private final View ivDeleteBtn;
            public PercentRelativeLayout mContainer;
            public TextView tvName;
            public ImageView ivIcon;

            public FavoriteHolder(View v) {
                super(v);
                mContainer = v.findViewById(R.id.vgContainer);
                ivIcon = v.findViewById(R.id.ivIcon);
                tvName = v.findViewById(R.id.tvName);
                ivAddBtn = v.findViewById(R.id.ivAddBtn);
                ivDeleteBtn = v.findViewById(R.id.ivDeleteBtn);
            }
        }

        public FavoriteDraggableGridAdapter(Context ctx, AbstractDataProvider dataProvider) {
            mProvider = dataProvider;
            this.ctx = ctx;
            // DraggableItemAdapter requires stable ID, and also
            // have to implement the getItemId() method appropriately.
            setHasStableIds(true);
        }

        public void setItemMoveMode(int itemMoveMode) {
            mItemMoveMode = itemMoveMode;
        }

        @Override
        public long getItemId(int position) {
            return mProvider.getItem(position).getId();
        }

        @Override
        public int getItemViewType(int position) {
            return mProvider.getItem(position).getViewType();
        }

        @Override
        public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View v = inflater.inflate(R.layout.item_lottery_kind_edit, parent, false);
            return new FavoriteHolder(v);
        }

        @Override
        public void onBindViewHolder(FavoriteHolder holder, int position) {
            final FavoriteDataProvider.ConcreteData item = (FavoriteDataProvider.ConcreteData) mProvider.getItem(position);

            // set text
            if (Strs.isEmpty(item.info.getAppIcon())) {
                item.info.setAppIcon(LotteryKind.find(item.info.getId()).getIconActive());
            }
            String icon = item.info.getAppIcon();
            Views.loadImage(ctx, holder.ivIcon, icon);
            holder.tvName.setText(item.info.getShowName());
            holder.itemView.setTag(item);
            holder.ivAddBtn.setVisibility(View.GONE);
            holder.ivDeleteBtn.setVisibility(View.VISIBLE);
            holder.ivDeleteBtn.setTag(R.id.id_int_tag, position);
            holder.ivDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoriteProvider.mData.size() <= 1) {
                        Toasts.show(ActAddFavorite.this, "至少保留一个彩种", false);
                        return;
                    }
                    favoriteProvider.removeItem((Integer) v.getTag(R.id.id_int_tag));
                    notifyDataSetChangedAll();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mProvider.getCount();
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
            Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

            if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
                mProvider.moveItem(fromPosition, toPosition);
            } else {
                mProvider.swapItem(fromPosition, toPosition);
            }
        }

        @Override
        public boolean onCheckCanStartDrag(FavoriteHolder holder, int position, int x, int y) {
            return true;
        }

        @Override
        public ItemDraggableRange onGetItemDraggableRange(FavoriteHolder holder, int position) {
            // no drag-sortable range specified
            return null;
        }

        @Override
        public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
            return true;
        }

        @Override
        public void onItemDragStarted(int position) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
            notifyDataSetChanged();
        }
    }

    public class FavoriteDataProvider extends AbstractDataProvider {
        private List<ConcreteData> mData;
        private ConcreteData mLastRemovedData;
        private int mLastRemovedPosition = -1;

        public FavoriteDataProvider() {

            mData = new LinkedList<>();
            List<UserFavoriteLottery> list = DBActionTouCai.queryAllUserFevoriteLottery();
            for (int i = 0; i < list.size(); i++) {
                final int viewType = 0;
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;
                mData.add(new ConcreteData(viewType, LotteryKind.find(list.get(i).lotteryId).getLotteryInfo(), swipeReaction));
            }
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Data getItem(int index) {
            if (index < 0 || index >= getCount()) {
                throw new IndexOutOfBoundsException("index = " + index);
            }

            return mData.get(index);
        }

        @Override
        public int undoLastRemoval() {
            if (mLastRemovedData != null) {
                int insertedPosition;
                if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                    insertedPosition = mLastRemovedPosition;
                } else {
                    insertedPosition = mData.size();
                }

                mData.add(insertedPosition, mLastRemovedData);

                mLastRemovedData = null;
                mLastRemovedPosition = -1;

                return insertedPosition;
            } else {
                return -1;
            }
        }

        @Override
        public void moveItem(int fromPosition, int toPosition) {
            if (fromPosition == toPosition) {
                return;
            }

            final ConcreteData item = mData.remove(fromPosition);

            mData.add(toPosition, item);
            mLastRemovedPosition = -1;
        }

        @Override
        public void swapItem(int fromPosition, int toPosition) {
            if (fromPosition == toPosition) {
                return;
            }

            Collections.swap(mData, toPosition, fromPosition);
            mLastRemovedPosition = -1;
        }

        @Override
        public void removeItem(int position) {
            //noinspection UnnecessaryLocalVariable
            final ConcreteData removedItem = mData.remove(position);

            mLastRemovedData = removedItem;
            mLastRemovedPosition = position;
        }

        public void removeItem(LotteryInfo item) {
            //noinspection UnnecessaryLocalVariable
            int position = -1;
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).info.getId() == item.getId()) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                removeItem(position);
            }
        }

        public boolean isContain(Object obj) {
            if (obj instanceof LotteryInfoCustom) {
                obj = ((LotteryInfoCustom) obj).toInfo();
            }
            if (obj instanceof LotteryInfo) {
                for (int i = 0; i < mData.size(); i++) {
                    if (((LotteryInfo) obj).getId() == mData.get(i).getId()) {
                        return true;
                    }
                }
            }

            return false;
        }

        public void appendItem(LotteryInfo info) {
            if (isContain(info)) {
                return;
            }

            final int viewType = 0;
            final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;

            ConcreteData concreteData = new ConcreteData(viewType, LotteryKind.find(info.getId()).getLotteryInfo(), swipeReaction);
            if (mData.size() < 6) {
                mData.add(concreteData);
            } else {
                /*mData.remove(mData.size() - 1);
                mData.add(0, concreteData);*/
                Toasts.show(ActAddFavorite.this, "最多添加6个喜爱彩种", false);
            }
        }

        public List<Integer> getListIds() {
            ArrayList<Integer> listIds = new ArrayList<>();
            for (int i = 0; i < mData.size(); i++) {
                listIds.add((int) mData.get(i).getId());
            }
            return listIds;
        }

        public final class ConcreteData extends Data {

            private final long mId;
            private final LotteryInfo info;
            private final int mViewType;
            private boolean mPinned;

            ConcreteData(int viewType, LotteryInfo info, int swipeReaction) {
                mId = info.getId();
                mViewType = viewType;
                this.info = info;
            }


            @Override
            public boolean isSectionHeader() {
                return false;
            }

            @Override
            public int getViewType() {
                return mViewType;
            }

            @Override
            public long getId() {
                return mId;
            }

            @Override
            public String toString() {
                return info.toString();
            }

            @Override
            public boolean isPinned() {
                return mPinned;
            }

            @Override
            public void setPinned(boolean pinned) {
                mPinned = pinned;
            }
        }
    }

    public class LotterCategoryAdapter extends PagerAdapter {
        private final Context ctx;
        ArrayList<String> listTitles;
        ArrayList<NestedScrollView> listViews;
        ArrayList<LotteryTypeCustom> listCategory;

        public LotterCategoryAdapter(Context ctx, ArrayList<LotteryTypeCustom> listCategory) {
            this.listCategory = listCategory;
            this.ctx = ctx;
            listTitles = new ArrayList<>();
            listViews = new ArrayList<>();
        }

        public void setup() {
            listViews.clear();
            listTitles.clear();
            for (int i = 0; i < listCategory.size(); i++) {
                listTitles.add(listCategory.get(i).getShowName());
                NestedScrollView nestedScrollView = new NestedScrollView(ctx);
                RecyclerView recyclerView = new RecyclerView(ctx);
                recyclerView.addItemDecoration(new SpaceDecoration(Views.dp2px(6), Views.dp2px(6), Views.dp2px(6), Views.dp2px(6)));
                recyclerView.setId(R.id.id_view_0);
                recyclerView.setLayoutManager(Views.genGridLayoutManager(ctx, 4));
//                RecyclerView recyclerView = listViews.get(position).findViewById(R.id.id_view_0);
                List<LotteryInfoCustom> lotteryList = listCategory.get(i).getLotteryList();
                LotteryKindAdapter adapter =
                        new LotteryKindAdapter(ctx, lotteryList);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapter);
                nestedScrollView.addView(recyclerView, new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                listViews.add(nestedScrollView);
            }
        }

        public void notifyCurrentPage() {
            ((RecyclerView) listViews.get(vpContent.getCurrentItem()).findViewById(R.id.id_view_0)).getAdapter().notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return listTitles.get(position);
        }

        @Override
        public int getCount() {
            return listTitles.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(listViews.get(position), new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            return listViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(listViews.get(position));
        }
    }

    public class LotteryKindAdapter extends BaseQuickAdapter<LotteryInfoCustom, LotteryKindAdapter.LotteryVH> {

        private Context context;
        private List<LotteryInfoCustom> listData;

        public LotteryKindAdapter(Context context, List<LotteryInfoCustom> list) {
            super(R.layout.item_lottery_kind_edit, list);
            this.context = context;
            this.listData = list;
        }

        @Override
        protected void convert(LotteryKindAdapter.LotteryVH holder, LotteryInfoCustom item) {
            //网络图片和本地图片
            if (Strs.isEmpty(item.getAppIcon())) {
                item.setAppIcon(LotteryKind.find(item.getId()).getIconActive());
            }
            String icon = item.getAppIcon();
            Views.loadImage(context, holder.ivIcon, icon);
            holder.tvName.setText(item.getShowName());
            holder.itemView.setTag(item);
            holder.ivAddBtn.setVisibility(View.GONE);
            holder.ivDeleteBtn.setVisibility(View.GONE);
            if (favoriteProvider.isContain(item)) {
                holder.ivDeleteBtn.setVisibility(View.VISIBLE);
            } else {
                holder.ivAddBtn.setVisibility(View.VISIBLE);
            }
            holder.ivAddBtn.setTag(item);
            holder.ivAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteProvider.appendItem(((LotteryInfoCustom) v.getTag()).toInfo());
                    notifyDataSetChangedAll();
                }
            });
            holder.ivDeleteBtn.setTag(item);
            holder.ivDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoriteProvider.mData.size() <= 1) {
                        Toasts.show(ActAddFavorite.this, "至少保留一个彩种", false);
                        return;
                    }
                    favoriteProvider.removeItem(((LotteryInfoCustom) v.getTag()).toInfo());
                    notifyDataSetChangedAll();
                }
            });
        }

        public class LotteryVH extends BaseViewHolder {
            public ImageView ivIcon;
            public TextView tvName;
            public ImageView ivAddBtn;
            public ImageView ivDeleteBtn;

            public LotteryVH(View view) {
                super(view);
                tvName = (TextView) view.findViewById(R.id.tvName);
                ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
                ivAddBtn = (ImageView) view.findViewById(R.id.ivAddBtn);
                ivDeleteBtn = (ImageView) view.findViewById(R.id.ivDeleteBtn);
            }
        }
    }


    public void notifyDataSetChangedAll() {
        if (mWrappedAdapterFavorite != null) {
            mWrappedAdapterFavorite.notifyDataSetChanged();
        }
        if (recentAdapter != null) {
            recentAdapter.notifyDataSetChanged();
        }
        if (pagerAdapter != null) {
            pagerAdapter.notifyCurrentPage();
        }
    }

    private void setupGuidePage() {
        GuidePageHelper.Builder builder = GuidePageHelper.build(this, UserManager.getIns().getMainUserName(), "favorite");
        RecyclerView favorite = ((RecyclerView) pagerAdapter.listViews.get(vpContent.getCurrentItem()).findViewById(R.id.id_view_0));
        GuidePageHelper.TouCaiGuidePage guidePagePlus = GuidePageHelper.TouCaiGuidePage.newInstance()
                .addHighLight(new RectF())
                .setLayoutAnchor(R.id.content, Gravity.TOP | Gravity.RIGHT)
                .setLayoutRes(R.layout.view_guide_page_favorite_plus);
        GuidePageHelper.TouCaiGuidePage guidePageMinus = GuidePageHelper.TouCaiGuidePage.newInstance()
                .addHighLight(rvFavorite.getLayoutManager().getChildAt(rvFavorite.getLayoutManager().getChildCount() - 1).findViewById(R.id.ivDeleteBtn))
                .addHighLight(rvFavorite.getLayoutManager().getChildAt(rvFavorite.getLayoutManager().getChildCount() - 1))
                .setLayoutAnchor(R.id.content, Gravity.BOTTOM | Gravity.RIGHT)
                .setLayoutRes(R.layout.view_guide_page_favorite_minus);
        GuidePageHelper.TouCaiGuidePage guidePageHint = GuidePageHelper.TouCaiGuidePage.newInstance()
                .addHighLight(new RectF())
                .setLayoutAnchor(R.id.content, Gravity.BOTTOM)
                .setLayoutRes(R.layout.view_guide_page_favorite_hint, R.id.btn_ok);

        builder.addGuidePage("minus", guidePageMinus);
        builder.addGuidePage("plus", guidePagePlus);
        builder.addGuidePage("hint", guidePageHint);
        builder.setLastPageCancelable(false);
        builder.setOnPageChangedListener(new GuidePageHelper.OnPageChangeListener() {
            @Override
            public void onInflate(Controller controller, String tag, int pos, View root) {

            }

            @Override
            public void onPageChanged(String tag, int pos, GuidePageHelper.TouCaiGuidePage guidePage, GuidePageHelper.TouCaiGuideController controller) {
                if ("plus".equals(tag)) {
                    int favoriteIndex = 1;
//                    List<LotteryInfoCustom> favoriteList = pagerAdapter.listCategory.get(vpContent.getCurrentItem()).getLotteryList();
//                    for (int i = 1; i < favoriteList.size(); i++) {
//                        if (favoriteProvider.isContain(favoriteList.get(i))) {
//                            favoriteIndex = i;
//                            break;
//                        }
//                    }
                    for (int i = 0; i < favorite.getLayoutManager().getChildCount(); i++) {
                        if (favorite.getLayoutManager().getChildAt(i).findViewById(R.id.ivAddBtn).getVisibility() == View.VISIBLE) {
                            favoriteIndex = i;

                            if (i % 4 == 1 || i % 4 == 2)
                                break;
                        }
                    }
                    RectF rectF = GuidePageHelper.getViewLocationInWindow(favorite.getLayoutManager().getChildAt(favoriteIndex));
                    scrollView.smoothScrollTo(0, (int) (rectF.bottom - scrollView.getHeight() + 100));
                    int finalFavoriteIndex = favoriteIndex;
                    scrollView.postDelayed(() -> {
                        guidePage.addHighLight(GuidePageHelper.getViewLocationInWindow(favorite.getLayoutManager().getChildAt(finalFavoriteIndex).findViewById(R.id.ivAddBtn)));
                        guidePage.addHighLight(GuidePageHelper.getViewLocationInWindow(favorite.getLayoutManager().getChildAt(finalFavoriteIndex)));
                        controller.refreshLayout();
                        controller.getCurrentLayout().findViewById(R.id.content).setVisibility(View.VISIBLE);
                    }, 300);
                } else if ("minus".equals(tag)) {
                    controller.getCurrentLayout().findViewById(R.id.content).setVisibility(View.VISIBLE);
                } else if ("hint".equals(tag)) {
                    scrollView.scrollTo(0, 0);
                    scrollView.postDelayed(() -> {
                        guidePage.addHighLight(GuidePageHelper.getViewLocationInWindow(rvFavorite));
                        controller.refreshLayout();
                        controller.getCurrentLayout().findViewById(R.id.content).setVisibility(View.VISIBLE);
                    }, 300);
                }

            }

            @Override
            public void onShow() {

            }

            @Override
            public void onRemove() {

            }
        });

        builder.show();
    }
}
