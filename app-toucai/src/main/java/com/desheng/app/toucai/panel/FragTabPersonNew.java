package com.desheng.app.toucai.panel;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.callback.AbCallback;
import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ArraysAndLists;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.global.ConfigTouCai;
import com.desheng.app.toucai.helper.BonusPoolActivityHelper;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.AwardPoolDetailoMode;
import com.desheng.app.toucai.model.ContactsBackMsgMode;
import com.desheng.app.toucai.model.ContactsMode;
import com.desheng.app.toucai.model.Event;
import com.desheng.app.toucai.model.NewUserMission;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.NoFastClickUtils;
import com.desheng.app.toucai.util.Utils;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.FloatWindowManager;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.BonusSalaryBean;
import com.desheng.base.model.ThirdPocketInfo;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.panel.ActChangeFundPassword;
import com.desheng.base.panel.ActWeb;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shark.tc.R;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

import static com.desheng.app.toucai.panel.ActBindBankCardToucai.BIND_OK_RESULT_CODE;

/**
 * 账户页面
 */

public class FragTabPersonNew extends AbBaseFragment implements View.OnClickListener, IRefreshableFragment {
    public static final String[] arrRefresh = {UserManagerTouCai.EVENT_USER_LOGINED,
            UserManagerTouCai.EVENT_USER_INFO_UNPDATED,
            UserManagerTouCai.EVENT_USER_LOGOUTED};
    private final int WITHDRAW_CLICK = 1;
    private final int BANKLIST_CLICK = 2;
    private final int RECHARGE_CLICK = 3;
    RxPermissions rxPermissions;
    AwardPoolDetailoMode mPoolDetailo;
    String mActivityId;
    private UserManager.EventReceiver receiver;
    private ImageView ivPotrait;
    private TextView tvLastLoginTime;
    private TextView tvAccount;
    private TextView tv_balance_amount, tv_third_amount;
    private ImageView iv_withdraw, iv_recharge, iv_transfer;
    private int requestCode = 0;
    private int WITHDRAW_REQUEST_CODE = 10;
    private int BANK_REQUEST_CODE = 11;
    private ImageView mIvSaoMa;
    private int REQUEST_CODE_SAOMA = 12;
    private RelativeLayout ivPersonalHeadBg;
    private double total_balance = 0;
    private RecyclerView myAccountRv;
    private RecyclerView myAgentAccountRv;
    private BaseQuickAdapter<AgentFeature, BaseViewHolder> myAccountRvadapter;
    private BaseQuickAdapter<AgentFeature, BaseViewHolder> myAgentAccountRvadapter;
    private boolean flag_QuerySalaryBonus = false;
    private TextView mTvAgent;
    private ImageView mRefreshIv;
    private SmartRefreshLayout swipeRefresh;

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_person_new;
    }

    @Override
    public void init(View view) {
        EventBus.getDefault().register(this);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setEnableRefresh(true);
        swipeRefresh.setEnableLoadMore(false);
        ivPotrait = view.findViewById(R.id.ivPotrait);
        tvAccount = view.findViewById(R.id.tvAccount);
        tvLastLoginTime = view.findViewById(R.id.tvLastLoginTime);
        tv_balance_amount = view.findViewById(R.id.tv_balance_amount);
        tv_third_amount = view.findViewById(R.id.tv_third_amount);
        mIvSaoMa = ((ImageView) view.findViewById(R.id.ivSaoMa));
        iv_transfer = view.findViewById(R.id.iv_transfer);
        iv_recharge = view.findViewById(R.id.iv_recharge);
        iv_withdraw = view.findViewById(R.id.iv_withdraw);
        myAccountRv = view.findViewById(R.id.myAccountRv);
        mRefreshIv = view.findViewById(R.id.refreshIv);
        mTvAgent = view.findViewById(R.id.tvAgent);
        myAgentAccountRv = view.findViewById(R.id.myAgentAccountRv);

        ivPersonalHeadBg = view.findViewById(R.id.ivPersonalHeadBg);

        iv_transfer.setOnClickListener(this);
        iv_recharge.setOnClickListener(this);
        iv_withdraw.setOnClickListener(this);
        mIvSaoMa.setOnClickListener(this);

        mRefreshIv.setOnClickListener(this);

        swipeRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshMoney();
                getQuerySalaryBonus();
            }
        });

        if (UserManagerTouCai.getIns().isAgent()) {
            mTvAgent.setVisibility(View.VISIBLE);
            myAgentAccountRv.setVisibility(View.VISIBLE);
        } else {
            mTvAgent.setVisibility(View.GONE);
            myAgentAccountRv.setVisibility(View.GONE);
        }

        myAccountRv.setLayoutManager(new GridLayoutManager(context, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        myAgentAccountRv.setLayoutManager(new GridLayoutManager(context, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        myAccountRvadapter = new BaseQuickAdapter<AgentFeature, BaseViewHolder>(R.layout.item_layout_agent_center_red_msg) {
            @Override
            protected void convert(BaseViewHolder helper, AgentFeature item) {
                helper.setText(R.id.name, item.name);
                helper.setVisible(R.id.msgNotReadCount, item.getNotRead() > 0 ? true : false);
                helper.setText(R.id.msgNotReadCount, String.valueOf(item.getNotRead()));
                helper.setImageResource(R.id.logo, item.iconRes);
            }
        };
        myAccountRv.setAdapter(myAccountRvadapter);

        myAgentAccountRvadapter = new BaseQuickAdapter<AgentFeature, BaseViewHolder>(R.layout.item_layout_agent_center) {
            @Override
            protected void convert(BaseViewHolder helper, AgentFeature item) {
                helper.setText(R.id.name, item.name);
                helper.setImageResource(R.id.logo, item.iconRes);
            }
        };
        myAgentAccountRv.setAdapter(myAgentAccountRvadapter);
        getQuerySalaryBonus();
        initMyAccountData();
        refreshMoney();

        ivPotrait.setOnClickListener(this);

        if (UserManager.getIns().isLogined()) {
            UserManager.getIns().getPersonSettingInfo(null);
            total_balance = 0;
            initPocketInfo();
        }

        receiver = new UserManagerTouCai.EventReceiver().register(getActivity(), new UserManagerTouCai.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (ArraysAndLists.findIndexWithEquals(eventName, arrRefresh) != -1) {
                    if (UserManagerTouCai.EVENT_USER_INFO_UNPDATED.equals(eventName) ||
                            UserManagerTouCai.EVENT_USER_LOGOUTED.equals(eventName)) { //数剧已更新则主动刷新ui
                        updateUserInfo();
                    } else {
                        refresh(null, null); //登录登出则重新拉取接口
                    }
                }
            }
        });

        //配置皮肤
        int skinSetting = UserManager.getIns().getSkinSetting();
        ivPersonalHeadBg.setBackgroundResource(CommonConsts.setPersonalHeadBgRes(skinSetting));
        ivPotrait.setBackgroundResource(CommonConsts.setPersonalIconRes(skinSetting));
        tv_balance_amount.setTextColor(getResources().getColor(CommonConsts.setPersonalTextColor(skinSetting)));
        tv_third_amount.setTextColor(getResources().getColor(CommonConsts.setPersonalTextColor(skinSetting)));
        tvAccount.setTextColor(getResources().getColor(CommonConsts.setPersonalTextColor2(skinSetting)));
        tvAccount.setBackgroundResource(CommonConsts.setPersonalNameBg(skinSetting));
        tvLastLoginTime.setTextColor(getResources().getColor(CommonConsts.setPersonalTextColor2(skinSetting)));
    }

    private List<AgentFeature> mAgentFeatures = new ArrayList<>();

    private void initThisData(BonusSalaryBean salaryBean) {
        String[] centerFeatures = getResources().getStringArray(R.array.agent_center_features);
        int length = centerFeatures.length;
        mAgentFeatures.clear();
        for (int i = 0; i < length - 3; i++) {
            int resourceId = getResources().obtainTypedArray(R.array.agent_center_features_icon_res).getResourceId(i, 0);
            mAgentFeatures.add(new AgentFeature(centerFeatures[i], resourceId, Consitances.HOME_INTEND_CLASSES[i]));
        }
        if (salaryBean != null && salaryBean.getShowBonusTab() == 1) {
            mAgentFeatures.add(new AgentFeature(centerFeatures[8], R.mipmap.agent_center_features_icon8, Consitances.HOME_INTEND_CLASSES[8]));
        }
        if (salaryBean != null && salaryBean.getShowSalaryTab() == 1) {
            mAgentFeatures.add(new AgentFeature(centerFeatures[9], R.mipmap.agent_center_features_icon9, Consitances.HOME_INTEND_CLASSES[9]));
        }

        if (BaseConfig.FLAG_JINFENG_3.equals(Config.custom_flag) && UserManager.getIns().getMainUserLevel() == 5) {
            mAgentFeatures.add(new AgentFeature(centerFeatures[10], R.mipmap.agent_center_features_icon11, Consitances.HOME_INTEND_CLASSES[10]));
        }
        myAgentAccountRvadapter.setNewData(mAgentFeatures);
        myAgentAccountRvadapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AgentFeature agentFeature = (AgentFeature) adapter.getData().get(position);
                if (agentFeature == null) {
                    return;
                }
                simpleLaunch(context, agentFeature.clazzGoto);
            }
        });
    }

    private List<AgentFeature> mMyAccountFeatures = new ArrayList<>();

    private void initMyAccountData() {
        boolean soketIOopen = UserManagerTouCai.getIns().isSoketIOopen();
        String[] centerFeatures = getResources().getStringArray(soketIOopen ? R.array.home_myaccount_features : R.array.home_myaccount_features_no_im);
        int length = 0;
        length = centerFeatures.length;
        for (int i = 0; i < length; i++) {
            int resourceId = getResources().obtainTypedArray(
                    soketIOopen ? R.array.home_myaccount_features_icon_res : R.array.home_myaccount_features_icon_res_no_im).getResourceId(i, 0);
            mMyAccountFeatures.add(new AgentFeature(centerFeatures[i], resourceId,
                    soketIOopen ? Consitances.HOME_MY_ACCOUNT_CLASSES[i] : Consitances.HOME_MY_ACCOUNT_CLASSES_NO_IM[i]));
        }
        myAccountRvadapter.setNewData(mMyAccountFeatures);
        myAccountRvadapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AgentFeature agentFeature = (AgentFeature) adapter.getData().get(position);
                if (agentFeature == null) {
                    return;
                }

                if (agentFeature.clazzGoto == ActBankCardListTouCai.class) {
                    requestCode = BANK_REQUEST_CODE;
                    checkBindStatus(BANKLIST_CLICK);
                } else if (agentFeature.clazzGoto == FloatWindowManager.class) {
                    FloatWindowManager.showPopupWindow(context, null);
                } else {
                    simpleLaunch(context, agentFeature.clazzGoto);
                }
            }
        });
    }

    class AgentFeature {
        String name;
        int iconRes;
        int notRead = 0;
        Class clazzGoto;

        public AgentFeature(String name, int iconRes, Class clazzGoto) {
            this.name = name;
            this.iconRes = iconRes;
            this.clazzGoto = clazzGoto;
        }

        public int getNotRead() {
            return notRead;
        }

        public void setNotRead(int notRead) {
            this.notRead = notRead;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //initRecommendActiExist();
        getMissionList();
        boolean soketIOopen = UserManagerTouCai.getIns().isSoketIOopen();
        if (soketIOopen) {
            initStatistics();
        }
        if (!flag_QuerySalaryBonus) {
            getQuerySalaryBonus();
        }

        if (UserManagerTouCai.getIns().isAgent()) {
            mTvAgent.setVisibility(View.VISIBLE);
            myAgentAccountRv.setVisibility(View.VISIBLE);
        } else {
            mTvAgent.setVisibility(View.GONE);
            myAgentAccountRv.setVisibility(View.GONE);
        }
    }

    @Override
    public void refresh(String eventName, Bundle newBundle) {
        if (UserManager.getIns().isLogined()) {
            UserManagerTouCai.getIns().refreshUserData();
            UserManager.getIns().getPersonSettingInfo(null);
            total_balance = 0;
            initPocketInfo();
        }
    }

    private void updateUserInfo() {
        if (UserManager.getIns().isLogined()) {
            tvAccount.setText(UserManagerTouCai.getIns().getMainUserName());
            String logintime = Dates.getStringByFormat(UserManager.getIns().getLoginTime(), Dates.dateFormatYMD);
            tvLastLoginTime.setText("上次登陆时间 " + logintime);
            tv_balance_amount.setText(Utils.getDoubleNotRound2Str(UserManagerTouCai.getIns().getLotteryAvailableBalance()));
            // tv_unread_msg_count.setText(UserManager.getIns().getMsgCount() + "封消息未阅读");
        } else {
            tvAccount.setText("掘金者");
            tvLastLoginTime.setText("上次登陆时间 " + "");
        }
    }

    public void onClick(View v) {
        requestCode = 0;
        switch (v.getId()) {
            case R.id.iv_withdraw:
                requestCode = WITHDRAW_REQUEST_CODE;
                checkBindStatus(WITHDRAW_CLICK);
                break;
            case R.id.iv_recharge:
                checkBindStatus(RECHARGE_CLICK);
                break;
            case R.id.iv_transfer:
                ActThirdTransferFastNew.launch(getActivity());
                break;
            case R.id.ivSaoMa://扫码登录
                rxPermissions = new RxPermissions(getActivity());
                rxPermissions.requestEach(Manifest.permission.CAMERA)
                        .subscribe(permission -> {
                            if (permission.granted) {
                                Intent intent = new Intent(context, CaptureActivity.class);
                                startActivityForResult(intent, REQUEST_CODE_SAOMA);
                                return;
                            }
                            if (permission.shouldShowRequestPermissionRationale) {
                                return;
                            }
                        });

                break;
            case R.id.ivPotrait:
                break;
            case R.id.refreshIv:
                if (NoFastClickUtils.isFastClick()) {
                    Toasts.show(context, "刷新频率过快~", false);
                } else {
                    refreshMoney();
                }
                break;
            default:
        }
    }

    private void refreshMoney() {
        initPocketInfo();
        UserManager.getIns().initUserData(new UserManager.IUserDataSyncCallback() {

            @Override
            public void onUserDataInited() {
                Dialogs.showLoadingDialog(context, "");
            }

            @Override
            public void afterUserDataInited() {
                tv_balance_amount.setText(Utils.getDoubleNotRound2Str(UserManagerTouCai.getIns().getLotteryAvailableBalance()));
            }

            @Override
            public void onUserDataInitFaild() {

            }

            @Override
            public void onAfter() {
                tv_balance_amount.setText(Utils.getDoubleNotRound2Str(UserManagerTouCai.getIns().getLotteryAvailableBalance()));
                updateUserInfo();
                Dialogs.hideProgressDialog(context);
            }
        });
    }


    private void showBindTip(UserBindStatus status, String tip1, String tip2, String right, int type) {
        DialogsTouCai.showBindDialog(getActivity(), tip1, tip2, "", right, true, new AbCallback<Object>() {
            @Override
            public boolean callback(Object obj) {

                if (!status.isBindWithdrawName() || !status.isBindWithdrawPassword() || !status.isBindCard()) {
                    ActBindBankCardToucai.launch(context, !status.isBindWithdrawName());
                } else if (UserManager.getIns().getLotteryAvailableBalance() == 0) {
                    if (type == WITHDRAW_CLICK) {
                        Toasts.show("账户余额不足无法提现", false);
                    } else if (type == BANKLIST_CLICK) {
                        launcherAct(ActBankCardListTouCai.class);
                    }
                }
                DialogsTouCai.hideBindTipDialog();

                return true;
            }
        });
    }

    private void showUpdateTip(String tip2, String left, String right) {
        DialogsTouCai.showUpdateDialog(getActivity(), tip2, left, right, true, new AbCallback<Object>() {
            @Override
            public boolean callback(Object obj) {
                launcherAct(ActChangeFundPassword.class);
                return true;
            }
        }, new AbCallback<Object>() {
            @Override
            public boolean callback(Object obj) {
                ActWeb.launchCustomService(getActivity());
                return true;
            }
        });
    }

    public void launchBindBankCardToucai(Class cls) {
        Intent itt = new Intent(getActivity(), cls);
        itt.putExtra("isFirstTime", true);
        startActivityForResult(itt, requestCode);
    }

    private void launcherAct(Class cls) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra("isWithdraw", requestCode == WITHDRAW_REQUEST_CODE);
        startActivityForResult(intent, requestCode);
    }

    private void checkBindStatus(int type) {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {
                Dialogs.showProgressDialog(context, "");
            }

            @Override
            public void onUserBindChecked(UserBindStatus status) {
                Dialogs.hideProgressDialog(context);
                if (!status.isBindWithdrawName() || !status.isBindWithdrawPassword() || !status.isBindCard()) {
                    showBindTip(status, "您暂时未绑定银行卡相关信息", "请您先绑定银行卡，真实姓名，资金密码，再实行操作", "前往绑定", -1);
                } else if (type == BANKLIST_CLICK) {
                    if (!status.isBindCard()) {
                        launchBindBankCardToucai(ActBindBankCardToucai.class);
                    } else {
                        ActBankCardListTouCai.launch(getActivity());
                    }
                } else if (!status.isBindCard()) {
                    showBindTip(status, "您暂时未绑定银行卡", "请您先绑定银行卡再实行操作", "前往绑定", 2);
                } else if (type == WITHDRAW_CLICK) {
                    if (!status.isBindCard()) {
                        showBindTip(status, "您暂时未绑定银行卡", "请您先绑定银行卡再实行操作", "前往绑定", 1);
                    } else {
                        if (UserManager.getIns().getLotteryAvailableBalance() == 0) {
                            Toasts.show("账户余额不足无法提现", false);
                        } else {
                            launcherAct(ActWithdrawalsList.class);
                        }
                    }
                } else if (type == RECHARGE_CLICK) {
                    ActDeposit.launch(getActivity());
                }
            }

            @Override
            public void onUserBindCheckFailed(String msg) {
                Dialogs.hideProgressDialog(context);
            }

            @Override
            public void onAfter() {
                Dialogs.hideProgressDialog(context);
            }
        });
    }

    public void getQuerySalaryBonus() {
        HttpAction.getQuerySalaryBonus(new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BonusSalaryBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                Dialogs.showProgressDialog(context, "加载中...");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (error == 0 && code == 0) {
                    BonusSalaryBean bonusSalaryBean = getField(extra, "data", null);
                    flag_QuerySalaryBonus = true;
                    initThisData(bonusSalaryBean);
                } else {
                    initThisData(null);
                    flag_QuerySalaryBonus = false;
                    Toasts.show(context, msg);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }


            @Override
            public boolean onError(int status, String content) {
                flag_QuerySalaryBonus = false;
                initThisData(null);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(context);
                swipeRefresh.finishRefresh();
            }
        });
    }

    private void initPocketInfo() {
        HttpAction.getThirdPocketList(getActivity(), new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<ThirdPocketInfo>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    ArrayList<ThirdPocketInfo> data = getField(extra, "data", null);
                    total_balance = 0;
                    for (ThirdPocketInfo pocketInfo : data) {
                        queryAccountMoney(tv_third_amount, pocketInfo.platformId);
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return true;
            }
        });
    }

    private void queryAccountMoney(final TextView tvAccountAmount, int platformId) {
        //MM.http.cancellAllByTag(ActThirdTransfer.this);
        HttpAction.getThirdPocketAmount(context, platformId, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", Double.TYPE);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                Double money = getField(extra, "data", 0.0);
                total_balance += money;
                tvAccountAmount.setText(Nums.formatDecimal(total_balance, 3));
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return true;
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActWithdrawals.WITHDRAW_OK_RESULT_OK) {
            updateUserInfo();
        } else if (requestCode == WITHDRAW_REQUEST_CODE && resultCode == BIND_OK_RESULT_CODE
                || resultCode == ActEditAccount.RESULT_CODE) {
            this.requestCode = 0;
            if (UserManager.getIns().getIsBindPhone())
                launcherAct(ActWithdrawals.class);
        } else if (requestCode == BANK_REQUEST_CODE && resultCode == BIND_OK_RESULT_CODE) {
            this.requestCode = 0;
            launcherAct(ActBankCardListTouCai.class);
        } else if (requestCode == REQUEST_CODE_SAOMA) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    if (Strs.isEmpty(result)) {
                        Toasts.show("解析二维码错误", false);
                    } else {
                        saoMaLoginPc(result);
                    }
//                    DialogsTouCai.showDialog(context, "温馨提示", "点击允许即可同步登录PC端", "允许一次", "拒绝",
//                            new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    saoMaLoginPc(result, v);
//                                }
//                            }, null, true, true, false);


                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toasts.show("解析二维码失败", false);
                }
            }
        }
    }

    private void saoMaLoginPc(String result) {
        HttpAction.saoMaLoginPc(context, result, new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
                entity.putField("data", String.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    String content = getField(extra, "data", "");
                    Toasts.show(content, true);
                } else {
                    Toasts.show(msg, false);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(content, false);
                return super.onError(status, content);
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            receiver.unregister(getActivity());
        }
        EventBus.getDefault().unregister(this);
    }

    private void getMissionList() {
        BonusPoolActivityHelper.getMissionList(new BonusPoolActivityHelper.IBonusPoolCallback() {
            @Override
            public void onBonusPoolIDExist(List<NewUserMission> listMission) {
//                line_layout_bonus_pool.setVisibility(listMission.size() > 0 ? View.VISIBLE : View.GONE);
//                layout_bonus_pool.setVisibility(listMission.size() > 0 ? View.VISIBLE : View.GONE);
                if (listMission != null && listMission.size() > 0) {
                    mActivityId = listMission.get(0).id;
                }
            }

            @Override
            public void onGetBonusPoolConfig(AwardPoolDetailoMode poolDetail) {
                mPoolDetailo = poolDetail;
//                if (Strs.isEqual("-1", poolDetail.getActivityIssueNo())) {
//                    line_layout_bonus_pool.setVisibility(View.GONE);
//                    layout_bonus_pool.setVisibility(View.GONE);
//                } else {
//                    line_layout_bonus_pool.setVisibility(View.VISIBLE);
//                    layout_bonus_pool.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onGetBonusPoolConfigFaild() {
//                line_layout_bonus_pool.setVisibility(View.GONE);
//                layout_bonus_pool.setVisibility(View.GONE);
            }
        });

    }

    //showMsg
    //有新的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(ContactsBackMsgMode backmessageInfo) {
        if (backmessageInfo == null) {
            return;
        }
        initStatistics();
    }

    //showMsg
    //有新的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.LoginEvent loginEvent) {
        if (loginEvent == null) {
            return;
        }
        getQuerySalaryBonus();
    }

    private void updateNotReadMsgCount() {
        int msgNotReadCountSum = UserManagerTouCai.getIns().getMsgNotReadCountSum();
        if (mAgentFeatures != null) {
            int size = mAgentFeatures.size();
            for (int i = 0; i < size; i++) {
                if (i == size - 1) {
                    mAgentFeatures.get(i).setNotRead(msgNotReadCountSum);
                }
            }
        }
        if (myAccountRvadapter != null) {
            myAccountRvadapter.notifyDataSetChanged();
        }
    }

    /**
     * 从http拉取
     */
    public void initStatistics() {
        HttpActionTouCai.getcontactsList(this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<List<ContactsMode>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    List<ContactsMode> contactslist = getField(extra, "data", null);

                    UserManagerTouCai.getIns().setNotReadMsgPool(contactslist);

                    int msgNotReadSum = 0;
                    if (contactslist.size() > 0) {
                        for (ContactsMode contactsMode : contactslist) {
                            msgNotReadSum += contactsMode.getUnReadCount();
                        }
                    }


                    if (mMyAccountFeatures != null) {
                        int size = mMyAccountFeatures.size();
                        for (int i = 0; i < size; i++) {
                            if (i == size - 1) {
                                mMyAccountFeatures.get(i).setNotRead(msgNotReadSum);
                            }
                        }
                    }
                    if (myAccountRvadapter != null) {
                        myAccountRvadapter.setNewData(mMyAccountFeatures);
                    }
                }
                return true;
            }
        });
    }
}