package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.PagerSlidingTabStrip;
import com.shark.tc.R;

public class ActFundRecord extends AbAdvanceActivity{

    public static void launch(Activity act) {
        simpleLaunch(act, ActFundRecord.class);
    }

    private PagerSlidingTabStrip tsTabs;
    private ViewPager vpContent;
    private String[] TITLES = {"充值", "转账", "提现", "活动"};

    private Fragment[] FRAGMENTS = new Fragment[]{
            new FragTabDepositRecord(),new FragTabTransferRecord(),new FragTabWithdrawRecord(),new FragTabActivityRecord()
    };

    @Override
    public void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "资金记录");
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_fund_record;
    }

    public void initView() {
        tsTabs = (PagerSlidingTabStrip) findViewById(R.id.tsTabs);
        vpContent = (ViewPager) findViewById(R.id.vpContent);
        vpContent.setAdapter(new FunAdapter(getSupportFragmentManager()));
        tsTabs.setViewPager(vpContent);
    }

    public class FunAdapter extends FragmentPagerAdapter {

        public FunAdapter(FragmentManager fm) {
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
