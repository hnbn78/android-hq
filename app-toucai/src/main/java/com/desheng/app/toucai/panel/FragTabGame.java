package com.desheng.app.toucai.panel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.PagerSlidingTabStrip;
import com.shark.tc.R;

/**
 * 笑开花操场页面
 * Created by  on 17/9/5.
 */
@Deprecated
public class FragTabGame extends AbBaseFragment implements View.OnClickListener {
    
    private PagerSlidingTabStrip tsTabs;
    private ViewPager vpContent;
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_game;
    }
    
    @Override
    public void init(View root) {
        tsTabs = (PagerSlidingTabStrip) root.findViewById(R.id.tsTabs);
        
        vpContent = (ViewPager) root.findViewById(R.id.vpContent);
        vpContent.setAdapter(new RecommendAttentionAdapter(getChildFragmentManager()));
        tsTabs.setViewPager(vpContent);
    }
 
    
    @Override
    public void onShow() {
        super.onShow();
    }
    
    @Override
    public void onHide() {
        super.onHide();
    }
    
    @Override
    public void onClick(View v) {
    
    }
    
 
    public class RecommendAttentionAdapter extends FragmentPagerAdapter {

        String[] TITLES = {"彩票大厅", "真人娱乐", "体育竞技", "电子游艺"};
        Fragment [] FRAGMENTS = new Fragment[]{FragTabGameLottery.newIns(null), FragTabGameReal.newIns(null),
                FragTabGameSport.newIns(null), FragTabGameElectronic.newIns(null)};

        public RecommendAttentionAdapter(FragmentManager fm) {
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
