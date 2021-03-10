package com.pearl.view.SimpleCollapse;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.ab.util.Views;
import com.pearl.view.MyLoadMoreView;

/**
 * 折叠工具栏瀑布流, 带加载更多.
 */
public class SimpleStaggeredGridView extends RecyclerView {
    public static final int STATUS_PREPARE = 0;
    private static final int STATUS_LOADING = 1;
    public static final int STATUS_EMPTY = -1;
    public static final int STATUS_END = -2;
    public static final int STATUS_DISMISS = -3;
    
    private boolean isScrollDown;
    private LoadMoreAdapter mLoadMoreAdapter;
    private OnLoadMore mOnLoadMore;
    private AdapterView.OnItemClickListener onItemClickListener;
    private boolean mIsLoadMore;
    
    public SimpleStaggeredGridView(Context context) {
        this(context, null);
        init();
    }
    
    public SimpleStaggeredGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }
    
    public SimpleStaggeredGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    private void init() {
        StaggeredGridLayoutManager layoutManager = new SimpleStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        setLayoutManager(layoutManager);
        setPadding(0,0, Views.dp2px(10), 0);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        addItemDecoration(new DividerGridItemDecoration(getContext()));
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mOnLoadMore != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int lastVisibleItem;
                        LayoutManager layoutManager = recyclerView.getLayoutManager();
                        
                        if (layoutManager instanceof LinearLayoutManager) {
                            lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                            ((StaggeredGridLayoutManager) layoutManager).invalidateSpanAssignments();
                            lastVisibleItem = findMax(into);
                        } else {
                            throw new RuntimeException("Unsupported LayoutManager used");
                        }
                        int totalItemCount = layoutManager.getItemCount();
                        if (mLoadMoreAdapter.getLoadMoreStatus() == STATUS_PREPARE
                                && lastVisibleItem >= totalItemCount - 1 && isScrollDown) {
                            mLoadMoreAdapter.setLoadMoreStatus(STATUS_LOADING);
                            mOnLoadMore.onLoad();
                        }
                    }
                }
            }
            
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isScrollDown = dy > 0;
            }
        });
    }
    
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    
    public void setOnLoadMore(OnLoadMore onLoadMore) {
        this.mIsLoadMore = true;
        if (mLoadMoreAdapter != null) {
            this.mLoadMoreAdapter.setIsLoadMore(true);
        }
        this.mOnLoadMore = onLoadMore;
    }
    
    public void setAdapter(Adapter adapter) {
        this.mLoadMoreAdapter = new LoadMoreAdapter(adapter, mIsLoadMore);
        this.mLoadMoreAdapter.setRetryListener(retryListener);
        super.setAdapter(mLoadMoreAdapter);
    }
    
    public void setLoadMoreStatus(int status) {
        if (mLoadMoreAdapter != null) {
            mLoadMoreAdapter.setLoadMoreStatus(status);
        }
    }
    
    public void notifyDataSetChanged(int positionStart, int itemCount){
        mLoadMoreAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }
    
    public void notifyDataSetChanged(){
        mLoadMoreAdapter.notifyDataSetChanged();
    }
    
    public interface OnLoadMore {
        void onLoad();
    }
    
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    
    OnClickListener retryListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mLoadMoreAdapter.setLoadMoreStatus(STATUS_LOADING);
            mOnLoadMore.onLoad();
        }
    };
    
    private  class LoadMoreAdapter extends Adapter {
        private static final int TYPE_FOOTER = -1;
        protected int mLoadMoreStatus = STATUS_PREPARE;
        protected OnClickListener mListener;
        private Adapter mAdapter;
        private boolean mIsLoadMore;
        private GridLayoutManager mGridLayoutManager;
        
        public LoadMoreAdapter(Adapter adapter, boolean isLoadMore) {
            this.mAdapter = adapter;
            this.mIsLoadMore = isLoadMore;
        }
        
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                this.mGridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            }
        }
        
        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            if (mIsLoadMore) {
                if (mGridLayoutManager != null) {
                    mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return position == getItemCount() - 1 ? mGridLayoutManager.getSpanCount() : 1;
                        }
                    });
                }
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                if (params instanceof StaggeredGridLayoutManager.LayoutParams) {
                    if (holder.getLayoutPosition() == getItemCount() - 1) {
                        ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
                    }
                }
            }
        }
        
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mIsLoadMore && viewType == TYPE_FOOTER) {
                return onCreateFooterViewHolder(parent);
            } else {
                return mAdapter.onCreateViewHolder(parent, viewType);
            }
        }
        
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (mIsLoadMore && getItemViewType(position) == TYPE_FOOTER) {
                bindFooterItem(holder);
            } else {
                mAdapter.onBindViewHolder(holder, position);
                if (onItemClickListener != null) {
                    holder.itemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(null, holder.itemView, position, 0);
                            }
                        }
                    });
                }
            }
        }
        
        @Override
        public int getItemCount() {
            return mIsLoadMore ? mAdapter.getItemCount() == 0 ? 0 : mAdapter.getItemCount() + 1 : mAdapter.getItemCount();
        }
        
        @Override
        public int getItemViewType(int position) {
            if (mIsLoadMore && position == getItemCount() - 1) {
                return TYPE_FOOTER;
            } else {
                return mAdapter.getItemViewType(position);
            }
        }
        
        public void refresh(){
            notifyDataSetChanged();
        }
        
        void setLoadMoreStatus(int status) {
            this.mLoadMoreStatus = status;
            notifyItemChanged(getItemCount() - 1);
        }
        
        public void setRetryListener(OnClickListener listener) {
            this.mListener = listener;
        }
        
        public int getLoadMoreStatus() {
            return this.mLoadMoreStatus;
        }
        
        public ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
            return new FooterViewHolder(new MyLoadMoreView(parent.getContext()));
        }
        
        public void setIsLoadMore(boolean isLoadMore) {
            this.mIsLoadMore = isLoadMore;
        }
        
        protected void bindFooterItem(ViewHolder holder) {
            MyLoadMoreView myLoadMoreView = ((FooterViewHolder) holder).myLoadMoreView;
            myLoadMoreView.setVisibility(View.VISIBLE);
            switch (mLoadMoreStatus) {
                case STATUS_LOADING:
                    myLoadMoreView.setLoadMoreStatus(MyLoadMoreView.STATUS_LOADING);
                    break;
                case STATUS_EMPTY:
                    myLoadMoreView.setLoadMoreStatus(MyLoadMoreView.STATUS_END);
                    holder.itemView.setOnClickListener(null);
                    break;
                case STATUS_END:
                    myLoadMoreView.setLoadMoreStatus(MyLoadMoreView.STATUS_END);
                    break;
                case STATUS_PREPARE:
                    myLoadMoreView.setLoadMoreStatus(MyLoadMoreView.STATUS_LOADING);
                    break;
                case STATUS_DISMISS:
                    myLoadMoreView.setVisibility(INVISIBLE);
            }
        }
    }
    
    static class FooterViewHolder extends ViewHolder {
        public MyLoadMoreView myLoadMoreView;
        public FooterViewHolder(View itemView) {
            super(itemView);
            myLoadMoreView = (MyLoadMoreView) itemView;
        }
    }
    
    private class DividerGridItemDecoration extends ItemDecoration {
        public DividerGridItemDecoration(Context context) {
        }
    
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = Views.dp2px(5);
            outRect.bottom = Views.dp2px(5);
            outRect.top = Views.dp2px(5);
        }
    
    }
}
