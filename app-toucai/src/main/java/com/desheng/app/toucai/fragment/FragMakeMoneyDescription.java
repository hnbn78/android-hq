package com.desheng.app.toucai.fragment;

import android.view.View;
import android.widget.TextView;

import com.shark.tc.R;

public class FragMakeMoneyDescription extends BasePageFragment {

    private TextView textView;

    public static FragMakeMoneyDescription newInstance() {
        FragMakeMoneyDescription fragment = new FragMakeMoneyDescription();
        return fragment;
    }

    @Override
    protected int setContentView() {
        return R.layout.frag_makemoney_mescription;
    }

    @Override
    protected void initView(View rootview) {
    }

    @Override
    public void fetchData() {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
}
