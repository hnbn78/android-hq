package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.BonusSalaryBean;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActAgentCenter extends AbAdvanceActivity {
    private TextView tv_title;
    private RecyclerView recycleView;
    private BaseQuickAdapter<AgentFeature, BaseViewHolder> adapter;

    public static void launch(Activity act) {
        simpleLaunch(act, ActAgentCenter.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_center;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("代理中心");
        initView();
    }

    private void initView() {
        recycleView = ((RecyclerView) findViewById(R.id.recycleView));
        recycleView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new BaseQuickAdapter<AgentFeature, BaseViewHolder>(R.layout.item_layout_agent_center) {
            @Override
            protected void convert(BaseViewHolder helper, AgentFeature item) {
                helper.setText(R.id.name, item.name);
                helper.setImageResource(R.id.logo, item.iconRes);
            }
        };
        recycleView.setAdapter(adapter);
        getQuerySalaryBonus();
    }

    private void initThisData(BonusSalaryBean bonusSalaryBean) {
        List<AgentFeature> agentFeatures = new ArrayList<>();
        String[] centerFeatures = getResources().getStringArray(R.array.agent_center_features);

        int length = 0;
        if (bonusSalaryBean != null && bonusSalaryBean.getShowBonusTab() == 1) {
            length = centerFeatures.length;
        } else {
            length = centerFeatures.length - 1;//不显示分红管理
        }
        for (int i = 0; i < length; i++) {
            int resourceId = getResources().obtainTypedArray(R.array.agent_center_features_icon_res).getResourceId(i, 0);
            agentFeatures.add(new AgentFeature(centerFeatures[i], resourceId, Consitances.HOME_INTEND_CLASSES[i]));
        }
        adapter.setNewData(agentFeatures);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AgentFeature agentFeature = (AgentFeature) adapter.getData().get(position);
                if (agentFeature == null) {
                    return;
                }
                simpleLaunch(ActAgentCenter.this, agentFeature.clazzGoto);
            }
        });
    }

    private void getQuerySalaryBonus() {
        HttpAction.getQuerySalaryBonus(new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BonusSalaryBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                Dialogs.showProgressDialog(ActAgentCenter.this, "加载中...");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (error == 0 && code == 0) {
                    BonusSalaryBean bonusSalaryBean = getField(extra, "data", null);
                    initThisData(bonusSalaryBean);
                } else {
                    Toasts.show(ActAgentCenter.this, msg);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(ActAgentCenter.this);
            }
        });
    }

    class AgentFeature {
        String name;
        int iconRes;
        Class clazzGoto;
        boolean isshow;

        public boolean isIsshow() {
            return isshow;
        }

        public void setIsshow(boolean isshow) {
            this.isshow = isshow;
        }

        public AgentFeature(String name, int iconRes, Class clazzGoto) {
            this.name = name;
            this.iconRes = iconRes;
            this.clazzGoto = clazzGoto;
        }
    }
}
