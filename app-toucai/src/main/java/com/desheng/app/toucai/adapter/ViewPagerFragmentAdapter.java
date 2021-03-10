package com.desheng.app.toucai.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Arrays;
import java.util.List;

public class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fms;
    private List<String> titles;

    public ViewPagerFragmentAdapter(FragmentManager fm, List<Fragment> fms) {
        super(fm);
        this.fms = fms;
    }

    public ViewPagerFragmentAdapter(FragmentManager fm, List<Fragment> fms, List<String> titles) {
        super(fm);
        this.fms = fms;
        this.titles = titles;
    }

    public ViewPagerFragmentAdapter(FragmentManager fm, Fragment[] fms, String[] args) {
        super(fm);
        this.fms = Arrays.asList(fms);
        this.titles = Arrays.asList(args);
    }

    @Override
    public Fragment getItem(int position) {
        return fms.get(position);
    }

    @Override
    public int getCount() {
        return fms.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null)
            return titles.get(position);
        return super.getPageTitle(position);
    }
}
