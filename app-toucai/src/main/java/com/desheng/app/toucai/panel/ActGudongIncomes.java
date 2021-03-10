package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.desheng.app.toucai.fragment.BasePageFragment;
import com.desheng.app.toucai.fragment.FragGudongIncomesTeamDetail;
import com.desheng.app.toucai.fragment.FragGudongIncomesTeamLotterHisory;
import com.desheng.app.toucai.util.ViewUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.List;

public class ActGudongIncomes extends AbAdvanceActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyFragmentAdapter mAdapter;
    private List<String> listTabTitle = new ArrayList<>(0);
    List<BasePageFragment> mFragments = new ArrayList<>(0);
    private int tabIndex = 0;

    public static void launch(Activity act) {
        simpleLaunch(act, ActGudongIncomes.class);
    }

    public static void launch(Activity ctx, int tabIndex) {
        Intent itt = new Intent(ctx, ActGudongIncomes.class);
        itt.putExtra("tabIndex", tabIndex);
        ctx.startActivity(itt);
    }

    @Override
    public void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_team_gudongshouyi;
    }

    public void initView() {
        mTabLayout = ((TabLayout) findViewById(R.id.tabLayout));
        mViewPager = ((ViewPager) findViewById(R.id.viewPager));
        initTabLayout();
    }

    private void initTabLayout() {
        mFragments.clear();
        mTabLayout.removeAllTabs();
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式

        tabIndex = getIntent().getIntExtra("tabIndex", 0);

        listTabTitle.add("团队详情");
        listTabTitle.add("团队投注记录");
        mFragments.add(FragGudongIncomesTeamDetail.newInstance());
        mFragments.add(FragGudongIncomesTeamLotterHisory.newInstance());
        mAdapter = new MyFragmentAdapter(getSupportFragmentManager(), mFragments, listTabTitle, this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        //反射修改TabLayout指示器宽度
        //绑定ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
        ViewUtil.setIndicator(this, mTabLayout, 30, 30);
        mViewPager.setCurrentItem(tabIndex);

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
