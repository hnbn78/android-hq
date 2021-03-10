package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.PrepareEditUserPoint;
import com.noober.background.view.BLTextView;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.HashMap;

import okhttp3.Request;

public class ActAgentShengdian extends AbAdvanceActivity {

    private static final String USER_NAME = "username";
    private static final String POINT_NOW = "point_now";
    private BLTextView onBnt;
    private TextView fandian_now, lotttery_fandian, tv_title, tipsView, username;
    private EditText pointAdjust;

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_shengdian;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("升点设置");
        initView();
    }


    public static void launch(Context activty, String name, String point) {
        Intent intent = new Intent(activty, ActAgentShengdian.class);
        intent.putExtra(USER_NAME, name);
        intent.putExtra(POINT_NOW, point);
        activty.startActivity(intent);
    }

    private void initView() {
        String userName = getIntent().getStringExtra(USER_NAME);
        String pointNow = getIntent().getStringExtra(POINT_NOW);

        onBnt = ((BLTextView) findViewById(R.id.ok));
        username = ((TextView) findViewById(R.id.username));
        fandian_now = ((TextView) findViewById(R.id.fandian_now));
        lotttery_fandian = ((TextView) findViewById(R.id.lotttery_fandian));
        pointAdjust = ((EditText) findViewById(R.id.pointAdjust));
        tipsView = ((TextView) findViewById(R.id.tipsView));

        fandian_now.setText(pointNow);
        username.setText(userName);
        prepareEditPoint(userName);
    }

    /**
     * 升点
     *
     * @param userName
     */
    private void prepareEditPoint(final String userName) {
        HttpAction.prepareEditPoint(userName, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", PrepareEditUserPoint.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActAgentShengdian.this, "查询中");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    PrepareEditUserPoint info = getField(extra, "data", null);
                    if (null != info && info.lotteryCodeRange != null && (info.mGameLotteryAccount.point > info.uGameLotteryAccount.point)) {
                        pointAdjust.setEnabled(true);
                        tipsView.setText("调整区间为 " + Nums.formatDecimal(info.lotteryCodeRange.minPoint, 2)
                                + "~" + Nums.formatDecimal(info.lotteryCodeRange.maxPoint, 2));
                        lotttery_fandian.setText(String.valueOf(info.uGameLotteryAccount.point + " %"));
                        onBnt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                double point = Views.getValue(pointAdjust, 0.0);
                                if (point <= 0 || point < info.lotteryCodeRange.minPoint || point > info.lotteryCodeRange.maxPoint) {
                                    Toasts.show(ActAgentShengdian.this, "请输入正确的点数!", false);
                                    return;
                                }
                                commit(userName, point);
                                Dialogs.dissmisCustomDialog();
                            }
                        });
                    } else {
                        pointAdjust.setEnabled(false);
                        lotttery_fandian.setText("" + info.lotteryCodeRange.maxPoint + " %");
                        pointAdjust.setText("" + info.lotteryCodeRange.maxPoint + " %");
                        tipsView.setText("无法调整该用户返点");
                        onBnt.setVisibility(View.GONE);
                    }
                } else {
                    Toasts.show(ActAgentShengdian.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActAgentShengdian.this);
            }
        });
    }

    private void commit(String userName, double point) {
        HttpAction.editUserPoint(userName, point, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", PrepareEditUserPoint.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActAgentShengdian.this, "查询中");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show(ActAgentShengdian.this, "修改成功", true);
                } else {
                    Toasts.show(ActAgentShengdian.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActAgentShengdian.this);
            }
        });
    }
}
