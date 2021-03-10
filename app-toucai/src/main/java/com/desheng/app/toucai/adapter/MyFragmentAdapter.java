package com.desheng.app.toucai.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.desheng.app.toucai.fragment.BasePageFragment;

import java.util.List;

public class MyFragmentAdapter extends FragmentStatePagerAdapter {

    private List<BasePageFragment> fragmentList;
    private List<String> list_Title;

    public MyFragmentAdapter(FragmentManager fragmentManager, List<BasePageFragment> data, List<String> listTitle) {
        super(fragmentManager);
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
