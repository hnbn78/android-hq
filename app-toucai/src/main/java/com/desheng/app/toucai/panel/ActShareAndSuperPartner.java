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
import android.widget.TextView;

import com.desheng.app.toucai.fragment.BasePageFragment;
import com.desheng.app.toucai.fragment.FragCommissionDetail;
import com.desheng.app.toucai.fragment.FragMakeMoneyDescription;
import com.desheng.app.toucai.fragment.FragShareAndMakeMoney;
import com.desheng.app.toucai.fragment.FragTeamDetail;
import com.desheng.app.toucai.util.ViewUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.List;

public class ActShareAndSuperPartner extends AbAdvanceActivity {

    List<BasePageFragment> mFragments = new ArrayList<>(0);
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyFragmentAdapter mAdapter;
    private List<String> listTabTitle = new ArrayList<>(0);
    private TextView tv_title;
    private int toTabIndex;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ActShareAndSuperPartner.class);
        activity.startActivity(intent);
    }

    public static void launch(Activity activity, int toTabIndex) {
        Intent intent = new Intent(activity, ActShareAndSuperPartner.class);
        if (toTabIndex > 0 && toTabIndex < 5) {
            intent.putExtra("toTabIndex", toTabIndex - 1);
            activity.startActivity(intent);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_share_superpartner;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();

        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("???????????????");
        mTabLayout = ((TabLayout) findViewById(R.id.tabLayout));
        mViewPager = ((ViewPager) findViewById(R.id.viewPager));

        toTabIndex = getIntent().getIntExtra("toTabIndex", 0);

        initTabLayout();
    }

    private void initTabLayout() {
        mFragments.clear();
        mTabLayout.removeAllTabs();
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//??????tab????????????????????????????????????

        listTabTitle.add("????????????");
        listTabTitle.add("????????????");
        listTabTitle.add("????????????");
        listTabTitle.add("????????????");
        mFragments.add(FragShareAndMakeMoney.newInstance());
        mFragments.add(FragCommissionDetail.newInstance());
        mFragments.add(FragTeamDetail.newInstance());
        mFragments.add(FragMakeMoneyDescription.newInstance());
        mAdapter = new MyFragmentAdapter(getSupportFragmentManager(), mFragments, listTabTitle, this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        //????????????TabLayout???????????????
        //??????ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
        ViewUtil.setIndicator(this, mTabLayout, 10, 10);
        mViewPager.setCurrentItem(toTabIndex);

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
         * //?????????????????????tab????????????
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
