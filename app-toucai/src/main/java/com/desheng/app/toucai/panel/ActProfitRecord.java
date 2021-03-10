package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

public class ActProfitRecord extends AbAdvanceActivity {
    private Fragment searchFragment;
    private RadioGroup radioGroup;
    private TextView btnSearch;
    FragProfitRecordTendency fragProfitRecordTendency = new FragProfitRecordTendency();
    FragProfitRecordCategory fragProfitRecordCategory = new FragProfitRecordCategory();

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ActProfitRecord.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_profit_record;
    }

    @Override
    protected void init() {
        hideToolbar();
        paddingHeadOfStatusHeight(findViewById(R.id.vgToolbarGroup));
        setStatusBarTranslucentAndLightContentWithPadding();

        radioGroup = findViewById(R.id.rg_type);
        btnSearch = findViewById(R.id.tv_search);
        btnSearch.setOnClickListener(v -> {
            if (searchFragment == null) {
                btnSearch.setText("取消");
                if (radioGroup.getCheckedRadioButtonId() == R.id.rb_cat) {
                    FragProfitRecordCategorySearch fragProfitRecordCategorySearch = new FragProfitRecordCategorySearch();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.searchFragContainer, fragProfitRecordCategorySearch)
                            .commitAllowingStateLoss();
                    searchFragment = fragProfitRecordCategorySearch;
                    fragProfitRecordCategorySearch.setListener(date -> {
                        fragProfitRecordCategory.search(date);
                        getSupportFragmentManager()
                                .beginTransaction().remove(fragProfitRecordCategorySearch)
                                .commitAllowingStateLoss();
                        searchFragment = null;
                        btnSearch.setText("搜索");
                    });
                } else {
                    FragProfitRecordTendencySearch fragProfitRecordTendencySearch = new FragProfitRecordTendencySearch();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.searchFragContainer, fragProfitRecordTendencySearch)
                            .commitAllowingStateLoss();
                    searchFragment = fragProfitRecordTendencySearch;
                }
            } else {
                btnSearch.setText("搜索");
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(searchFragment)
                        .commitAllowingStateLoss();
                searchFragment = null;
            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_cat:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragContainer, fragProfitRecordCategory)
                            .commitAllowingStateLoss();
                    break;
                case R.id.rb_tendency:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragContainer, fragProfitRecordTendency)
                            .commitAllowingStateLoss();
                    break;
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragContainer, fragProfitRecordCategory)
                .commitAllowingStateLoss();
    }
}
