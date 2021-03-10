package com.desheng.app.toucai.panel;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.pearl.act.base.AbBaseFragment;
import com.shark.tc.R;

/**
 * 记录
 */
public class FragRecordLottery extends AbBaseFragment {
    private String[] TITLES = {"今日", "三日", "七日"};
    int curr = 0;

    private Fragment[] FRAGMENTS = new Fragment[]{
            new FragTabBetRecordOne(), new FragTabBetRecordTwo(), new FragTabBetRecordThree()
    };

    public static Fragment newIns() {
        return new FragRecordLottery();
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_bet_record;
    }

    @Override
    public void init(View root) {
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.frag_record_container, new FragTabBetRecordOne())
                .commitAllowingStateLoss();
        root.findViewById(R.id.btn_today).setSelected(true);
        root.findViewById(R.id.btn_today).setOnClickListener(this::onTodayClick);
        root.findViewById(R.id.btn_three_day).setOnClickListener(this::onThreeClick);
        root.findViewById(R.id.btn_seven_day).setOnClickListener(this::onSevenClick);
        root.findViewById(R.id.btn_search).setOnClickListener(this::onSearchClick);
    }

    public void refresh() {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.frag_record_container, FRAGMENTS[curr])
                .commitAllowingStateLoss();
        getContentView().findViewById(R.id.btn_today).setSelected(curr == 0 ? true : false);
        getContentView().findViewById(R.id.btn_three_day).setSelected(curr == 1 ? true : false);
        getContentView().findViewById(R.id.btn_seven_day).setSelected(curr == 2 ? true : false);
        ((SwipeRefreshLayout.OnRefreshListener) FRAGMENTS[curr]).onRefresh();
    }

    public void onTodayClick(View view) {
        curr = 0;
        refresh();
    }

    public void onThreeClick(View view) {
        curr = 1;
        refresh();
    }


    public void onSevenClick(View view) {
        curr = 2;
        refresh();
    }

    public void onSearchClick(View view) {
        ActBetRecordSearch.launch(getActivity());
    }
}
