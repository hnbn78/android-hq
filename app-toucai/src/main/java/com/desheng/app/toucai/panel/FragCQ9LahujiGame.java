package com.desheng.app.toucai.panel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.desheng.app.toucai.fragment.BasePageFragment;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.List;

public class FragCQ9LahujiGame extends BasePageFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    BaseMultiItemQuickAdapter<MyMutiEntity, BaseViewHolder> adapter;

    public static FragCQ9LahujiGame newInstance() {
        FragCQ9LahujiGame fragment = new FragCQ9LahujiGame();
        return fragment;
    }

    @Override
    protected int setContentView() {
        return R.layout.layout_frag_cq9allgame;
    }

    @Override
    protected void initView(View rootview) {

        swipeRefreshLayout = ((SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefreshLayout));
        recyclerView = ((RecyclerView) rootview.findViewById(R.id.recyclerView));

        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 4));
        adapter = new myMutilAdapter(cq9s);
        recyclerView.setAdapter(adapter);
    }

    List<MyMutiEntity> cq9s = new ArrayList<>();

    @Override
    public void fetchData() {
        for (int i = 0; i < 50; i++) {
            MyMutiEntity myMutiEntity = new MyMutiEntity();
            myMutiEntity.setGameName("第" + i + "款游戏");
            myMutiEntity.setItemType(myMutiEntity.TYPE_NORMAL);
            cq9s.add(myMutiEntity);

        }

        if (adapter != null) {
            adapter.setNewData(cq9s);
        }

    }

    class myMutilAdapter extends BaseMultiItemQuickAdapter<MyMutiEntity, BaseViewHolder> {

        public myMutilAdapter(List<MyMutiEntity> data) {
            super(data);
            addItemType(MyMutiEntity.TYPE_TUIJIAN, R.layout.item_pt_game_tuijian);
            addItemType(MyMutiEntity.TYPE_NORMAL, R.layout.item_thrid_game_icon);
        }

        @Override
        protected void convert(BaseViewHolder helper, MyMutiEntity item) {
            switch (helper.getItemViewType()) {
                case MyMutiEntity.TYPE_TUIJIAN:
                    helper.setText(R.id.tvName, item.getGameName());
                    helper.setBackgroundRes(R.id.ivIcon, R.mipmap.ic_other);
                    int layoutPosition = helper.getLayoutPosition();
                    if (layoutPosition < 3) {
                        if (layoutPosition % 3 == 0) {
                            helper.setBackgroundRes(R.id.bg, R.mipmap.cq9_item_bg_yellow);
                        } else if (layoutPosition % 3 == 1) {
                            helper.setBackgroundRes(R.id.bg, R.mipmap.cq9_item_bg_orange);
                        } else if (layoutPosition % 3 == 2) {
                            helper.setBackgroundRes(R.id.bg, R.mipmap.cq9_item_bg_blue);
                        }
                    }
                    break;
                case MyMutiEntity.TYPE_NORMAL:
                    helper.setText(R.id.tvName, item.getGameName());
                    helper.setBackgroundRes(R.id.icon, R.mipmap.ic_other);
                    break;
            }
        }
    }

    class MyMutiEntity implements MultiItemEntity {

        public static final int TYPE_TUIJIAN = 0;
        public static final int TYPE_NORMAL = 1;
        String gameName;
        String gameIconUrl;
        int itemType;

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }

        public void setGameIconUrl(String gameIconUrl) {
            this.gameIconUrl = gameIconUrl;
        }

        public String getGameName() {
            return gameName;
        }

        public String getGameIconUrl() {
            return gameIconUrl;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        @Override
        public int getItemType() {
            return itemType;
        }
    }
}
