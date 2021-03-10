package com.desheng.app.toucai.panel;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayCategory;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.ItemDeviderDecoration;
import com.shark.tc.R;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.ArrayList;


public class ActPlayMethod extends AbAdvanceActivity {


    @Override
    protected void init() {
        setToolbarLeftBtn(UIHelper.BACK_WHITE_ARROR);
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarTitleCenterDefault(getToolbar(), "玩法选择");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setBackgroundColor(Color.WHITE);
        ArrayList<LotteryPlayCategory> listPlayCategory = (ArrayList<LotteryPlayCategory>) getIntent().getSerializableExtra("listPlayCategory");
        recyclerView.setLayoutManager(Views.genLinearLayoutManagerV(this));
        recyclerView.addItemDecoration(new ItemDeviderDecoration(this, ItemDeviderDecoration.VERTICAL_LIST));
        LotteryPlayMenuAdapter adapter = new LotteryPlayMenuAdapter(listPlayCategory);
        adapter.bindToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.refresh_recycler_method_select;
    }

    public class LotteryPlayMenuAdapter extends BaseQuickAdapter<LotteryPlayCategory, LotteryPlayMenuAdapter.PlayMenuVH> {


        private LayoutInflater layoutInflater;

        LotteryPlayMenuAdapter(ArrayList<LotteryPlayCategory> listPlayCategory) {
            super(R.layout.item_lottery_play_method_menu, listPlayCategory);
            this.layoutInflater = LayoutInflater.from(ActPlayMethod.this);
        }

        @Override
        protected void convert(PlayMenuVH helper, final LotteryPlayCategory item) {
            helper.tvCategoryName.setText(item.getTitleName());// 26.
            helper.sflCategory.removeAllViews();
            for (int i = 0; i < item.getData().size(); i++) {
                LotteryPlay lotteryPlay = item.getData().get(i);
                final TextView textView = (TextView) layoutInflater.inflate(R.layout.view_text_border, helper.sflCategory, false);
                ((ViewGroup.MarginLayoutParams) textView.getLayoutParams()).setMargins(Views.dp2px(5), Views.dp2px(5), Views.dp2px(5), Views.dp2px(5));
                textView.setText(lotteryPlay.name);
                textView.setTag(lotteryPlay);
                textView.setSelected(lotteryPlay.isEnable);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LotteryPlay play = (LotteryPlay) v.getTag();
                        if (play.isEnable) {
                            play.isEnable = false;
                            textView.setSelected(false);
                            UserManagerTouCai.getIns().addUserPlayedLottery(play.category, play.getPlayId());
                        } else {
                            play.isEnable = true;
                            textView.setSelected(true);
                            UserManagerTouCai.getIns().removeUserPlayedLottery(play.category, play.getPlayId());
                        }
                        notifyItemChanged(getParentPosition(item));
                        setResult(111);
                    }
                });
                helper.sflCategory.addView(textView);
            }
        }


        protected class PlayMenuVH extends BaseViewHolder {
            public TextView tvCategoryName;
            public FlowLayout sflCategory;

            public PlayMenuVH(View view) {
                super(view);
                tvCategoryName = view.findViewById(R.id.tvCategoryName);
                sflCategory = view.findViewById(R.id.sflCategory);
            }
        }
    }
}
