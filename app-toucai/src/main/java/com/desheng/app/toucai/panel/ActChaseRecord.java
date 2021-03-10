package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.desheng.app.toucai.util.UIHelperTouCai;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.view.PagerSlidingTabStrip;
import com.shark.tc.R;

public class ActChaseRecord extends AbAdvanceActivity{

    public static void launch(Activity act) {
        simpleLaunch(act, ActChaseRecord.class);
    }

    private PagerSlidingTabStrip tsTabs;
    private ViewPager vpContent;
    private String[] TITLES = {"今日", "三日", "七日"};

    private Fragment[] FRAGMENTS = new Fragment[]{
            new FragTabChaseRecordOne(),new FragTabChaseRecordTwo(),new FragTabChaseRecordThree()
    };

    @Override
    public void init() {
        UIHelperTouCai.getIns().simpleToolbarLeftBackAndCenterTitleAndeRightTitle(this, getToolbar(),
                "追号记录", "搜索");
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  ActBetRecordSearch.launch(ActBetRecord.this);
                ActChaseRecordSearch.launcher(ActChaseRecord.this);
            }
        } );
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_bet_record;
    }

    public void initView() {
        tsTabs = findViewById(R.id.tsTabs);
        vpContent = findViewById(R.id.vpContent);
        BetAdapter mAdapter=new BetAdapter(getSupportFragmentManager());
        vpContent.setAdapter(mAdapter);
        tsTabs.setViewPager(vpContent);

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
