package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Nums;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.model.SuperPartnerStatistics;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.HashMap;

public class ActSuperPartner extends AbAdvanceActivity implements FragSuperPartnerSubMemberSearch.OnSearchListener, FragSuperPartnerBonusRecordSearch.OnSearchListener {
    private TextView bonus;
    private TextView memberCount;
    private FragSuperPartnerBonusRecord fragSuperPartnerBonusRecord;
    private FragSuperPartnerSubMember fragSuperPartnerSubMember;
    private Fragment searchfragment;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ActSuperPartner.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_super_partner;
    }

    @Override
    protected void init() {
        fragSuperPartnerBonusRecord = new FragSuperPartnerBonusRecord();
        fragSuperPartnerSubMember = new FragSuperPartnerSubMember();

        hideToolbar();
        paddingHeadOfStatusHeight(findViewById(R.id.vgToolbarGroup));
        setStatusBarTranslucentAndLightContent();
        setStatusBarTranslucentAndLightContentWithPadding();
        ((TextView) findViewById(R.id.tvSearchBtn)).setText("搜索");
        findViewById(R.id.tvSearchBtn).setOnClickListener(v -> {
            if (searchfragment == null) {
                ((TextView) v).setText("取消");
                if (findViewById(R.id.btn_record).isSelected()) {
                    FragSuperPartnerBonusRecordSearch bonusRecordSearch = new FragSuperPartnerBonusRecordSearch();
                    searchfragment = bonusRecordSearch;
                    bonusRecordSearch.setOnSearchListener(this);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.rootfragContainer, bonusRecordSearch)
                            .commit();
                } else {
                    FragSuperPartnerSubMemberSearch superPartnerSubMemberSearch = new FragSuperPartnerSubMemberSearch();
                    searchfragment = superPartnerSubMemberSearch;
                    superPartnerSubMemberSearch.setOnSearchListener(this);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.rootfragContainer, superPartnerSubMemberSearch)
                            .commit();

                }
            } else {
                ((TextView) v).setText("搜索");
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(searchfragment)
                        .commit();
                searchfragment = null;
            }
        });

        bonus = findViewById(R.id.tv_bonus);
        memberCount = findViewById(R.id.tv_member_count);

        findViewById(R.id.btn_my_member).setOnClickListener(v -> {
            v.setSelected(true);
            findViewById(R.id.btn_record).setSelected(false);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragContainer, fragSuperPartnerSubMember)
                    .commit();
        });
        findViewById(R.id.btn_record).setOnClickListener(v -> {
            v.setSelected(true);
            findViewById(R.id.btn_my_member).setSelected(false);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragContainer, fragSuperPartnerBonusRecord)
                    .commit();
        });

        initStatistics();
        findViewById(R.id.btn_record).performClick();
    }

    public void initStatistics() {
        HttpActionTouCai.getRecommendInfo(this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<SuperPartnerStatistics>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    SuperPartnerStatistics superPartnerStatistics = getField(extra, "data", null);

                    if (superPartnerStatistics != null) {
                        bonus.setText(Nums.formatDecimal(superPartnerStatistics.getAwardAmount(), 2));
                        memberCount.setText(String.valueOf(superPartnerStatistics.getValidUserCount()));
                    } else {
                        bonus.setText("0.00");
                        memberCount.setText("0");
                    }
                }
                return true;
            }
        });
    }


    @Override
    public void searchBonusRecord(Integer type, String startDate, String endDate, Integer status) {
        fragSuperPartnerBonusRecord.initBonusRecord(type == null ? null : String.valueOf(type),
                startDate, endDate, 0, status == null ? null : String.valueOf(status));

        if (searchfragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(searchfragment)
                    .commit();
            searchfragment = null;
            ((TextView) findViewById(R.id.tvSearchBtn)).setText("搜索");
        }
    }

    @Override
    public void searchSubUser(String user) {
        fragSuperPartnerSubMember.initMember(user, 0);

        if (searchfragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(searchfragment)
                    .commit();
            searchfragment = null;
            ((TextView) findViewById(R.id.tvSearchBtn)).setText("搜索");
        }
    }
}
