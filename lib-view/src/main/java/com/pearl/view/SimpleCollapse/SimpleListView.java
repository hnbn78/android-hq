package com.pearl.view.SimpleCollapse;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.pearl.view.MyLoadMoreView;
import com.pearl.view.SlowScrollView;

/**
 * 折叠工具栏瀑布流, 带加载更多.
 */
public class SimpleListView extends RecyclerView {
    public static final int STATUS_PREPARE = 0;
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_EMPTY = -1;
    public static final int STATUS_END = -2;
    public static final int STATUS_DISMISS = -3;
    
    private boolean isScrollDown;
    private LoadMoreAdapter mLoadMoreAdapter;
    private LoadMoreListener mLoadMoreListener;
    private AdapterView.OnItemClickListener onItemClickListener;
    private boolean mIsLoadMore;
    private View outterScroller;
    
    public SimpleListView(Context context) {
        this(context, null);
        init();
    }
    
    public SimpleListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }
    
    public SimpleListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    private void init() {
        final LinearLayoutManager layoutManager = new SimpleLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        setLayoutManager(layoutManager);
       
            addOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (mLoadMoreListener != null) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                            int totalItemCount = layoutManager.getItemCount();
                            if (mLoadMoreAdapter.getLoadMoreStatus() == STATUS_PREPARE
                                    && lastVisibleItem >= totalItemCount - 1 && isScrollDown && mIsLoadMore) {
                                mLoadMoreAdapter.setLoadMoreStatus(STATUS_LOADING);
                                mLoadMoreListener.onLoadMore();
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
    
    public void setOutterScrollView(View scrollView){
        outterScroller = scrollView;
        setNestedScrollingEnabled(false);
        if (scrollView instanceof NestedScrollView) {
            ((NestedScrollView)outterScroller).setSmoothScrollingEnabled(true);
        } else if (scrollView instanceof ScrollView) {
        }
        outterScroller = scrollView;
        if(outterScroller instanceof NestedScrollView){
            ((NestedScrollView) outterScroller).setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                
                    if (scrollY > oldScrollY) {
                    
                    }
                    if (scrollY < oldScrollY) {
                    
                    }
                
                    if (scrollY == 0) {
                    
                    }
                
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        if(mLoadMoreAdapter.getLoadMoreStatus() == STATUS_PREPARE&& mIsLoadMore){
                            mLoadMoreAdapter.setLoadMoreStatus(STATUS_LOADING);
                            mLoadMoreListener.onLoadMore();
                        }
                    }
                }
            });
        }else if(outterScroller instanceof SlowScrollView){
            ((SlowScrollView)outterScroller).setScanScrollChangedListener(new SlowScrollView.ISmartScrollChangedListener() {
                @Override
                public void onScrolledToBottom() {
                    if(mLoadMoreAdapter.getLoadMoreStatus() == STATUS_PREPARE&& mIsLoadMore){
                        mLoadMoreAdapter.setLoadMoreStatus(STATUS_LOADING);
                        mLoadMoreListener.onLoadMore();
                    }
                }
            
                @Override
                public void onScrolledToTop() {
                
                }
            });
        }
    }
    
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    
    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.mLoadMoreListener = loadMoreListener;
    }
    
    public void setAdapter(Adapter adapter) {
        this.mLoadMoreAdapter = new LoadMoreAdapter(adapter, mIsLoadMore);
        this.mLoadMoreAdapter.setRetryListener(retryListener);
        super.setAdapter(mLoadMoreAdapter);
    }
    
    public void setLoadMoreStatus(int currPageIndexBeginFrom1, int count, int pageSize) {
        int pageCount = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        setLoadMoreStatus(currPageIndexBeginFrom1 < pageCount ? SimpleStaggeredGridView.STATUS_PREPARE : SimpleStaggeredGridView.STATUS_END);
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
    
    public void setLoadMoreEnable(boolean isEnable) {
        mIsLoadMore = isEnable;
    }
    
    public interface LoadMoreListener {
        void onLoadMore();
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
            mLoadMoreListener.onLoadMore();
        }
    };
    
    private  class LoadMoreAdapter extends Adapter {
        private static final int TYPE_FOOTER = -1;
        protected int mLoadMoreStatus = STATUS_PREPARE;
        protected OnClickListener mListener;
        private Adapter mAdapter;
        
        public LoadMoreAdapter(Adapter adapter, boolean isLoadMore) {
            this.mAdapter = adapter;
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
                if(onItemClickListener != null){
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
}
