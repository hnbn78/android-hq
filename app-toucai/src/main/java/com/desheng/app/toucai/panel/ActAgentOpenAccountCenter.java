package com.desheng.app.toucai.panel;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.adapter.MyFragmentAdapter;
import com.desheng.app.toucai.fragment.BasePageFragment;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.OpenAccount;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActAgentOpenAccountCenter extends AbAdvanceActivity {
    private TextView tv_title;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    List<BasePageFragment> mFragments = new ArrayList<>(0);
    private List<String> listTabTitle = new ArrayList<>(0);
    private MyFragmentAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_open_account_center;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("开户中心");
        initView();
    }

    private void initView() {
        mTabLayout = ((TabLayout) findViewById(R.id.tabLayout));
        mViewPager = ((ViewPager) findViewById(R.id.viewPager));
        initThisData();
    }


    private void initTabLayout(OpenAccount openAccountInfo) {
        mFragments.clear();
        mTabLayout.removeAllTabs();
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//设置tab模式，当前为系统默认模式

        listTabTitle.add("普通开户");
        listTabTitle.add("链接开户");
        listTabTitle.add("链接管理");
        mFragments.add(FragCommonOpenAccount.newInstance(openAccountInfo));
        mFragments.add(FragLinkOpenAccount.newInstance(openAccountInfo));
        mFragments.add(FragOpenAccountLinkManage.newInstance());

        mAdapter = new MyFragmentAdapter(getSupportFragmentManager(), mFragments, listTabTitle);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        //反射修改TabLayout指示器宽度
        //绑定ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(0);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                int position = tab.getPosition();
                if (position == 2) {
                    mFragments.get(position).fetchData();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initThisData() {
        HttpAction.prepareAddAccount(this, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActAgentOpenAccountCenter.this, "");
                    }
                });
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", OpenAccount.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && extra.get("data") != null) {
                    OpenAccount openAccountInfo = getFieldObject(extra, "data", null);
                    initTabLayout(openAccountInfo);
                } else {
                    Toasts.show(ActAgentOpenAccountCenter.this, msg, false);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActAgentOpenAccountCenter.this, content, false);
                finish();
                return super.onError(status, content);
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActAgentOpenAccountCenter.this);
            }
        });
    }

    public boolean isHideLink() {
        long mainUserLevel = UserManager.getIns().getMainUserLevel();
        if (BaseConfig.Org != null) {
            return !BaseConfig.Org.canOpenLink(mainUserLevel);
        }
        return false;
    }

    public boolean isHidePlayer() {
        long mainUserLevel = UserManager.getIns().getMainUserLevel();
        if (BaseConfig.Org != null) {
            return !BaseConfig.Org.canOpenUser(mainUserLevel);
        }
        return false;
    }
}
