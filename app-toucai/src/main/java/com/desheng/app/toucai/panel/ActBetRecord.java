package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.desheng.app.toucai.util.UIHelperTouCai;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.view.PagerSlidingTabStrip;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;

public class ActBetRecord extends AbAdvanceActivity {

    public static void launch(Activity act) {
        simpleLaunch(act, ActBetRecord.class);
    }

    private PagerSlidingTabStrip tsTabs;
    private ViewPager vpContent;
    private String[] TITLES = {"今日", "三日", "七日"};

    private Fragment[] FRAGMENTS = new Fragment[]{
            new FragTabBetRecordOne(), new FragTabBetRecordTwo(), new FragTabBetRecordThree()
    };

    @Override
    public void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelperTouCai.getIns().simpleToolbarLeftBackAndCenterTitleAndeRightTitle(this, getToolbar(),
                "投注记录", "搜索");

        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActBetRecordSearch.launch(ActBetRecord.this);
            }
        });

        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_bet_record;
    }

    public void initView() {
        tsTabs = findViewById(R.id.tsTabs);
        vpContent = findViewById(R.id.vpContent);
        BetAdapter mAdapter = new BetAdapter(getSupportFragmentManager());
        vpContent.setAdapter(mAdapter);
        tsTabs.setViewPager(vpContent);

        tsTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (FRAGMENTS[position] != null) {
                    ((SwipeRefreshLayout.OnRefreshListener) FRAGMENTS[position]).onRefresh();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class BetAdapter extends FragmentStatePagerAdapter {

        public BetAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FRAGMENTS[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return FRAGMENTS.length;
        }
    }

}
