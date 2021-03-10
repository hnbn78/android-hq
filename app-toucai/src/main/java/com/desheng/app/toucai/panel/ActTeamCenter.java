package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.AgenLinkInitBean;
import com.desheng.app.toucai.model.GetDesignHostMode;
import com.desheng.app.toucai.model.NewUserMission;
import com.desheng.app.toucai.model.SixActivityRuleBean;
import com.desheng.app.toucai.model.TeamAgentShareLink;
import com.desheng.app.toucai.model.TeamZonglanMode;
import com.desheng.app.toucai.model.UpdateContentList;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActTeamCenter extends AbAdvanceActivity {

    private RecyclerView rcTeamList;
    private BaseQuickAdapter quickAdapter;
    private View headview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvTuiguang;
    List<SixActivityRuleBean> mTeamdata = new ArrayList<>();
    private TeamZonglanMode.UserInfoBean mUserInfo;
    String mAgentShareLink = "";
    private View footview;

    public static void launch(Activity act) {
        simpleLaunch(act, ActTeamCenter.class);
    }

    String[] agentConfig = {"代理会员", "总代理会员", "股东会员", "大股东会员"};
    String[] agentConfig2 = {"effectivePersonalBets", "effectiveTeamBets", "totalEffectiveBets", "activeNumber", "numberOfNewUsers"};
    int[] userLogoRes = {R.mipmap.icon_agent, R.mipmap.icon_zongagent, R.mipmap.icon_gudong, R.mipmap.icon_dagudong};

    @Override
    public void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        TextView btnRight = headRoot.findViewById(R.id.btnRight);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActGudongIncomes.launch(ActTeamCenter.this);
            }
        });
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_team_center;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.tvTuiguang:
                if (Strs.isNotEmpty(mAgentShareLink)) {
                    ActTeamErweima.launch(this);
                } else {
                    Toast.makeText(this, "链接未生成,刷新重试再进入", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void initView() {
        rcTeamList = findViewById(R.id.rcTeamCenter);
        tvTuiguang = findViewById(R.id.tvTuiguang);
        tvTuiguang.setOnClickListener(this);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        rcTeamList.setLayoutManager(new LinearLayoutManager(this));
        quickAdapter = new BaseQuickAdapter<SixActivityRuleBean, BaseViewHolder>(R.layout.item_team_center) {

            @Override
            protected void convert(BaseViewHolder helper, SixActivityRuleBean item) {
                if (item == null) {
                    return;
                }
                // 对这种接口设计很无语，但是还是得保持微笑

                if (Strs.isEqual(item.getType(), agentConfig2[0])) {//个人投注
                    if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[0])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getPersonalConfirmAmount() + "/" + item.getAgency());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getAgency()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getPersonalConfirmAmount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getAgency()) <= ((int) mUserInfo.getPersonalConfirmAmount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[1])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getPersonalConfirmAmount() + "/" + item.getGeneralAgent());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getGeneralAgent()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getPersonalConfirmAmount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getGeneralAgent()) <= ((int) mUserInfo.getPersonalConfirmAmount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[2])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getPersonalConfirmAmount() + "/" + item.getShareholder());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getShareholder()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getPersonalConfirmAmount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getShareholder()) <= ((int) mUserInfo.getPersonalConfirmAmount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[3])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getPersonalConfirmAmount() + "/" + item.getMajorShareholder());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getMajorShareholder()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getPersonalConfirmAmount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getMajorShareholder()) <= ((int) mUserInfo.getPersonalConfirmAmount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    }

                    helper.setText(R.id.btn, "立即投注");

                } else if (Strs.isEqual(item.getType(), agentConfig2[1])) {//团队投注
                    if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[0])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getTeamConfirmAmount() + "/" + item.getAgency());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getAgency()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getTeamConfirmAmount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getAgency()) <= ((int) mUserInfo.getTeamConfirmAmount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.btn, "已完成");
                            ((TextView) helper.getView(R.id.btn)).setBackgroundResource(R.mipmap.team_center_btn);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                            helper.setText(R.id.btn, "未完成");
                            ((TextView) helper.getView(R.id.btn)).setBackgroundResource(R.drawable.shape_r15_gray);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[1])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getTeamConfirmAmount() + "/" + item.getGeneralAgent());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getGeneralAgent()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getTeamConfirmAmount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getGeneralAgent()) <= ((int) mUserInfo.getTeamConfirmAmount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.btn, "已完成");
                            ((TextView) helper.getView(R.id.btn)).setBackgroundResource(R.mipmap.team_center_btn);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                            helper.setText(R.id.btn, "未完成");
                            ((TextView) helper.getView(R.id.btn)).setBackgroundResource(R.drawable.shape_r15_gray);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[2])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getTeamConfirmAmount() + "/" + item.getShareholder());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getShareholder()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getTeamConfirmAmount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getShareholder()) <= ((int) mUserInfo.getTeamConfirmAmount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.btn, "已完成");
                            ((TextView) helper.getView(R.id.btn)).setBackgroundResource(R.mipmap.team_center_btn);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                            helper.setText(R.id.btn, "未完成");
                            ((TextView) helper.getView(R.id.btn)).setBackgroundResource(R.drawable.shape_r15_gray);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[3])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getTeamConfirmAmount() + "/" + item.getMajorShareholder());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getMajorShareholder()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getTeamConfirmAmount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getMajorShareholder()) <= ((int) mUserInfo.getTeamConfirmAmount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.btn, "已完成");
                            ((TextView) helper.getView(R.id.btn)).setBackgroundResource(R.mipmap.team_center_btn);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                            helper.setText(R.id.btn, "未完成");
                            ((TextView) helper.getView(R.id.btn)).setBackgroundResource(R.drawable.shape_r15_gray);
                        }
                    }


                } else if (Strs.isEqual(item.getType(), agentConfig2[2])) {//总投注（个人加团队总和）
                    double sumtouzhu = mUserInfo.getTeamConfirmAmount() + mUserInfo.getPersonalConfirmAmount();
                    if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[0])) {
                        helper.setText(R.id.progressShuju, sumtouzhu + "/" + item.getAgency());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getAgency()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) sumtouzhu));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getAgency()) <= ((int) sumtouzhu)) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[1])) {
                        helper.setText(R.id.progressShuju, sumtouzhu + "/" + item.getGeneralAgent());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getGeneralAgent()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) sumtouzhu));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getGeneralAgent()) <= ((int) sumtouzhu)) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[2])) {
                        helper.setText(R.id.progressShuju, sumtouzhu + "/" + item.getShareholder());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getShareholder()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) sumtouzhu));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getShareholder()) <= ((int) sumtouzhu)) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[3])) {
                        helper.setText(R.id.progressShuju, sumtouzhu + "/" + item.getMajorShareholder());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getMajorShareholder()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) sumtouzhu));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getMajorShareholder()) <= ((int) sumtouzhu)) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    }

                    helper.setText(R.id.btn, "立即投注");

                } else if (Strs.isEqual(item.getType(), agentConfig2[3])) {//新增团员
                    if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[0])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getActiveCount() + "/" + item.getAgency());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getAgency()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getActiveCount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getAgency()) <= ((int) mUserInfo.getActiveCount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[1])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getActiveCount() + "/" + item.getGeneralAgent());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getGeneralAgent()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getActiveCount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getGeneralAgent()) <= ((int) mUserInfo.getActiveCount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[2])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getActiveCount() + "/" + item.getShareholder());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getShareholder()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getActiveCount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getShareholder()) <= ((int) mUserInfo.getActiveCount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[3])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getActiveCount() + "/" + item.getMajorShareholder());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getMajorShareholder()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getActiveCount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getMajorShareholder()) <= ((int) mUserInfo.getActiveCount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    }

                    helper.setText(R.id.btn, "复制链接");

                } else if (Strs.isEqual(item.getType(), agentConfig2[4])) {//团队总人数
                    if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[0])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getAddCount() + "/" + item.getAgency());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getAgency()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getAddCount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getAgency()) <= ((int) mUserInfo.getAddCount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[1])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getAddCount() + "/" + item.getGeneralAgent());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getGeneralAgent()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getAddCount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getGeneralAgent()) <= ((int) mUserInfo.getAddCount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[2])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getAddCount() + "/" + item.getShareholder());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getShareholder()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getAddCount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getShareholder()) <= ((int) mUserInfo.getAddCount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    } else if (Strs.isEqual(mUserInfo.getUserAgentType(), agentConfig[3])) {
                        helper.setText(R.id.progressShuju, mUserInfo.getAddCount() + "/" + item.getMajorShareholder());
                        ((ProgressBar) helper.getView(R.id.progress)).setMax(Integer.parseInt(item.getMajorShareholder()));
                        ((ProgressBar) helper.getView(R.id.progress)).setProgress(((int) mUserInfo.getAddCount()));
                        //小图标显示与否（根据进度）
                        if (Integer.parseInt(item.getMajorShareholder()) <= ((int) mUserInfo.getAddCount())) {
                            helper.getView(R.id.complete).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tvtips, "恭喜你，已完成保级任务!");
                        } else {
                            helper.setText(R.id.tvtips, item.getCopywriting());
                            helper.getView(R.id.complete).setVisibility(View.GONE);
                        }
                    }

                    helper.setText(R.id.btn, "复制链接");
                }

                helper.getView(R.id.btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = ((TextView) helper.getView(R.id.btn)).getText().toString();
                        if (Strs.isEqual(text, "立即投注")) {
                            CtxLotteryTouCai.launchLotteryPlay(ActTeamCenter.this, 911);//跳腾讯分分彩
                        } else if (Strs.isEqual(text, "复制链接")) {
                            if (Strs.isNotEmpty(mAgentShareLink)) {
                                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                // 创建普通字符型ClipData
                                ClipData mClipData = ClipData.newPlainText("mAgentShareLink", mAgentShareLink);
                                // 将ClipData内容放到系统剪贴板里。
                                cm.setPrimaryClip(mClipData);
                                Toast.makeText(mContext, "链接复制成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "链接未生成,刷新重试", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        };

        headview = LayoutInflater.from(this).inflate(R.layout.team_center_head, null);
        quickAdapter.addHeaderView(headview);
        quickAdapter.setEnableLoadMore(true);

        footview = LayoutInflater.from(this).inflate(R.layout.team_center_footview, null);
        quickAdapter.addFooterView(footview);

        rcTeamList.setAdapter(quickAdapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }, 1000);
            }
        });

        initHeadView();
        getData();
    }

    private void initHeadView() {
        View goErweima = headview.findViewById(R.id.goErweima);
        goErweima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Strs.isNotEmpty(mAgentShareLink)) {
                    ActTeamErweima.launch(ActTeamCenter.this);
                } else {
                    Toast.makeText(ActTeamCenter.this, "链接未生成,刷新重试再进入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getData() {
        HttpActionTouCai.getMissionList(this, 25, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(ActTeamCenter.this, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<NewUserMission>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    List<NewUserMission> listMission = getField(extra, "data", null);
                    if (listMission.size() > 0) {
                        NewUserMission latestMission = listMission.get(0);//默认取最新活动
                        String missionId = listMission.get(0).id;
                        getTeamTuiguangData(missionId);
                    }
                }
                return true;
            }
        });


        HttpActionTouCai.getDesignedHost(this, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(ActTeamCenter.this, "");
            }

            @Override
            public boolean onGetString(String str) {
                if (Strs.isNotEmpty(str)) {
                    try {
                        GetDesignHostMode designHostMode = new Gson().fromJson(str, GetDesignHostMode.class);
                        if (designHostMode != null) {
                            getTeamAgentRegistLink(designHostMode);
                        }
                    } catch (Exception e) {
                    }
                }
                return super.onGetString(str);
            }
        });
    }

    private void getTeamTuiguangData(String missionId) {
        HttpActionTouCai.getTeamTuiguangData(this, missionId, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<TeamZonglanMode>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    TeamZonglanMode teamZonglanMode = getField(extra, "data", null);
                    swipeRefreshLayout.setRefreshing(false);
                    if (teamZonglanMode != null) {
                        mUserInfo = teamZonglanMode.getUserInfo();
                        updateUserInfoView(mUserInfo);
                        updateFootView(teamZonglanMode);
                        quickAdapter.setNewData(teamZonglanMode.getSixActivityRule());
                    }

                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActTeamCenter.this, content, false);
                swipeRefreshLayout.setRefreshing(false);
                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                DialogsTouCai.hideProgressDialog(ActTeamCenter.this);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getTeamAgentRegistLink(GetDesignHostMode designHostMode) {
        HttpActionTouCai.getTeamAgentRegistLink(this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<TeamAgentShareLink>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    TeamAgentShareLink linkdata = getField(extra, "data", null);//fc769c6ec8d22ecf.html

                    //奇葩的字段，需要这样处理！！！
                    String linkcode = linkdata.getCode().replace("/register/", "").replace(".html", "");
                    getTeamAgentRegistLinkInit(designHostMode, linkcode);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });
    }

    private void getTeamAgentRegistLinkInit(GetDesignHostMode designHostMode, String linkcode) {
        HttpActionTouCai.getTeamAgentRegistLinkInit(this, linkcode, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<AgenLinkInitBean>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    AgenLinkInitBean agenLinkInitBean = getField(extra, "data", null);

                    if (agenLinkInitBean != null) {
                        String link = designHostMode.getUrls() + "/rg/" + agenLinkInitBean.getParentId();
                        updateContentConfig(link);
                    }
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });
    }

    private void updateContentConfig(String agentShareLink) {
        HttpActionTouCai.getUpdateContentList(this, Consitances.contentManager.TEANM_SHARE_LINK, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<UpdateContentList>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    UpdateContentList list = getField(extra, "data", null);

                    if (list != null && list.getList() != null && list.getList().size() > 0) {
                        mAgentShareLink = Utils.stripHtml(list.getList().get(0).getContent()
                                .replace("XXXXXXXXXXX", agentShareLink)).replace("&darr;", "");
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                DialogsTouCai.hideProgressDialog(ActTeamCenter.this);
            }
        });
    }

    private void updateFootView(TeamZonglanMode teamZonglanMode) {
        if (footview == null) {
            return;
        }
        String tips = "完成其中" + teamZonglanMode.getPrecondition() +
                "个任务，即可继续享受" + teamZonglanMode.getUserInfo().getUserAgentType().replace("会员", "") + "权限";
        ((TextView) footview.findViewById(R.id.tv)).setText(tips);
        ((TextView) footview.findViewById(R.id.tv)).setText(tips);
    }

    private void updateUserInfoView(TeamZonglanMode.UserInfoBean userInfo) {
        if (headview == null) {
            return;
        }
        ImageView userLogo = headview.findViewById(R.id.userLogo);
        TextView username = headview.findViewById(R.id.username);
        TextView userloginDate = headview.findViewById(R.id.userloginDate);
        TextView team_num = headview.findViewById(R.id.team_num);
        TextView incomeToday = headview.findViewById(R.id.incomeToday);
        TextView incomeAll = headview.findViewById(R.id.incomeAll);
        TextView tvAll = headview.findViewById(R.id.tvAll);
        TextView tvToday = headview.findViewById(R.id.tvToday);


        for (int i = 0; i < agentConfig.length; i++) {
            if (Strs.isEqual(userInfo.getUserAgentType(), agentConfig[i])) {
                userLogo.setBackgroundResource(userLogoRes[i]);
            }
        }

        username.setText(userInfo.getUserAgentType());
        userloginDate.setText("最后登录时间 " + Utils.getDate("yyyy-MM-dd"));
        team_num.setText(String.valueOf(userInfo.getTeamCount()));
        incomeToday.setText(String.valueOf(userInfo.getDayProceeds()));
        incomeAll.setText(String.valueOf(userInfo.getAllProceeds()));

        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActGudongIncomes.launch(ActTeamCenter.this, 1);
            }
        });

        tvToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActGudongIncomes.launch(ActTeamCenter.this, 1);
            }
        });

        team_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActGudongIncomes.launch(ActTeamCenter.this);
            }
        });
    }
}
