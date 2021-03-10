package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.fragment.BasePageFragment;
import com.desheng.app.toucai.model.LotteryInfoCustom;
import com.desheng.app.toucai.model.PtGameInfoMode;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.google.gson.Gson;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class ActCQ9game extends AbAdvanceActivity {

    List<BasePageFragment> mFragments = new ArrayList<>(0);
    public TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyFragmentAdapter mAdapter;
    private List<String> listTabTitle = new ArrayList<>(0);
    private TextView tv_title;
    private ImageView searchbar;
    public TextView searchCancel;
    public EditText search_blank;
    private ImageButton ibLeftBtn;
    private FrameLayout searchLayout;
    public BaseQuickAdapter<LotteryInfoCustom, BaseViewHolder> mSearchHotAdapter;

    public static void launcher(Activity activity) {
        activity.startActivity(new Intent(activity, ActCQ9game.class));
    }

    public static void launcher(Activity activity, int thirdPlatformId) {
        Intent intent = new Intent(activity, ActCQ9game.class);
        intent.putExtra("thirdPlatformId", thirdPlatformId);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_cq9_layout;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        int thirdPlatformId = getIntent().getIntExtra("thirdPlatformId", 0);
        initTitleLayout(headRoot);
        getData(thirdPlatformId);
    }

    private void initTitleLayout(View headRoot) {
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        searchbar = ((ImageView) headRoot.findViewById(R.id.searchbar));
        searchCancel = ((TextView) headRoot.findViewById(R.id.searchCancel));
        search_blank = ((EditText) headRoot.findViewById(R.id.search_blank));
        ibLeftBtn = ((ImageButton) headRoot.findViewById(R.id.ibLeftBtn));
        mTabLayout = ((TabLayout) findViewById(R.id.tabLayout));
        mViewPager = ((ViewPager) findViewById(R.id.viewPager));
        searchLayout = ((FrameLayout) findViewById(R.id.searchLayout));

        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchbar.setVisibility(View.GONE);
                searchCancel.setVisibility(View.VISIBLE);
                search_blank.setVisibility(View.VISIBLE);
                ibLeftBtn.setVisibility(View.GONE);
                tv_title.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);
            }
        });

        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCancel.setVisibility(View.GONE);
                searchbar.setVisibility(View.VISIBLE);
                search_blank.setVisibility(View.GONE);
                ibLeftBtn.setVisibility(View.VISIBLE);
                tv_title.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.GONE);
            }
        });

        initSearchLayout();
    }

    private void initSearchLayout() {

        View rearchview = LayoutInflater.from(this).inflate(R.layout.layout_pt_search, null, false);
        RecyclerView rcSearch = (RecyclerView) rearchview.findViewById(R.id.rcSearch);
        rcSearch.setLayoutManager(new LinearLayoutManager(this));
        mSearchHotAdapter = new BaseQuickAdapter<LotteryInfoCustom, BaseViewHolder>(R.layout.item_pt_search) {
            @Override
            protected void convert(BaseViewHolder helper, LotteryInfoCustom item) {
                int layoutPosition = helper.getLayoutPosition();
                helper.setText(R.id.tv, item.getShowName());
                helper.setText(R.id.index, String.valueOf(layoutPosition + 1));
                if (layoutPosition == 0) {
                    helper.getView(R.id.index).setBackgroundResource(R.drawable.search_result_tv_bg_red);
                } else if (layoutPosition == 1) {
                    helper.getView(R.id.index).setBackgroundResource(R.drawable.search_result_tv_bg_red2);
                } else if (layoutPosition == 2) {
                    helper.getView(R.id.index).setBackgroundResource(R.drawable.search_result_tv_bg_red3);
                } else {
                    helper.getView(R.id.index).setBackgroundResource(R.drawable.search_result_tv_bg_gray);
                }
            }
        };
        rcSearch.setAdapter(mSearchHotAdapter);
        searchLayout.addView(rearchview);
    }

    private void initTabLayout(List<PtGameInfoMode.PtGameTypeEntity> datas, int thirdPlatformId) {

        mFragments.clear();
        mTabLayout.removeAllTabs();
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//设置tab模式，当前为系统默认模式

        listTabTitle.add("全部游戏");
        listTabTitle.add("热门游戏");
        mFragments.add(FragCQ9AllGame.newInstance(thirdPlatformId));
        mFragments.add(FragCQ9hotGame.newInstance(thirdPlatformId));

        if (datas != null && datas.size() > 0) {
            for (PtGameInfoMode.PtGameTypeEntity entity : datas) {
                listTabTitle.add(entity.getLabel());
                mFragments.add(new FragCQ9TableGame().newInstance(entity.getValue(), thirdPlatformId));
            }
        }

        mAdapter = new MyFragmentAdapter(getSupportFragmentManager(), mFragments, listTabTitle, this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        //反射修改TabLayout指示器宽度
        //绑定ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(0);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void getData(int thirdPlatformId) {
        HttpActionTouCai.getPTgameData(this, 0, 30, null, null, thirdPlatformId, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(ActCQ9game.this, "");
            }

            @Override
            public boolean onGetString(String str) {
                if (Strs.isNotEmpty(str)) {
                    PtGameInfoMode ptGameInfoMode = new Gson().fromJson(str, PtGameInfoMode.class);
                    List<PtGameInfoMode.PtGameTypeEntity> ptGameType = ptGameInfoMode.getCqGameType();
                    initTabLayout(ptGameType, thirdPlatformId);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                DialogsTouCai.hideProgressDialog(ActCQ9game.this);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogsTouCai.hideProgressDialog(ActCQ9game.this);
            }
        });
    }

    class MyFragmentAdapter extends FragmentStatePagerAdapter {

        private Context context;
        private List<BasePageFragment> fragmentList;
        private List<String> list_Title;

        public MyFragmentAdapter(FragmentManager fragmentManager, List<BasePageFragment> data, List<String> listTitle, Context context) {
            super(fragmentManager);
            this.context = context;
            this.fragmentList = data;
            this.list_Title = listTitle;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return list_Title.size();
        }

        /**
         * //此方法用来显示tab上的名字
         *
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return list_Title.get(position);
        }

    }
}
