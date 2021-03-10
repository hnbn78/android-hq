package com.desheng.app.toucai.panel;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ab.callback.AbCallback;
import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.model.CommonPaymentEntity;
import com.desheng.app.toucai.model.PaymentCategoryInfo;
import com.desheng.app.toucai.model.QrCodeListBean;
import com.desheng.app.toucai.model.Testbean;
import com.desheng.app.toucai.model.ThridListBean;
import com.desheng.app.toucai.model.TransferListBean;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.panel.ActBindBankCard;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by lee on 2018/4/11.
 */
public class FragTabDepositIndexNew extends AbBaseFragment implements View.OnClickListener {

    List<CommonPaymentEntity> commonPaymentEntities = new ArrayList<>();
    private MaterialDialog materialDialog;
    private RecyclerView mPayCategoryRc;
    private BaseQuickAdapter<PaymentCategoryInfo, BaseViewHolder> adapter;
    private LinearLayout llEmptyLayout;
    private Button tvRetry;
    private SmartRefreshLayout swipeRefresh;
    private String mUsdt_exchange_rate;

    @Override
    public void init(View root) {
        //EventBus.getDefault().register(this);
        //getAllThirdPayment();
        swipeRefresh = ((SmartRefreshLayout) root.findViewById(R.id.swipeRefresh));
        llEmptyLayout = ((LinearLayout) root.findViewById(R.id.llEmptyLayout));
        tvRetry = ((Button) root.findViewById(R.id.tvRetry));
        mPayCategoryRc = ((RecyclerView) root.findViewById(R.id.payRc));

        swipeRefresh.setEnableRefresh(true);
        swipeRefresh.setEnableLoadMore(false);
        swipeRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                checkBindStatus();
            }
        });

        mPayCategoryRc.setLayoutManager(new GridLayoutManager(context, 2));
        adapter = new BaseQuickAdapter<PaymentCategoryInfo, BaseViewHolder>(R.layout.item_payment_category_layout) {
            @Override
            protected void convert(BaseViewHolder helper, PaymentCategoryInfo item) {
                int layoutPosition = helper.getLayoutPosition();
                helper.setVisible(R.id.ivTuijianTag, layoutPosition == 0);
                helper.setText(R.id.paymentCategory, item.getCategoryName());
                ImageView pamentLogo = (ImageView) helper.getView(R.id.paymentIcon);
                Views.loadImage(context, pamentLogo, ENV.curr.host + item.getMobileLogoPicture());
            }
        };
        mPayCategoryRc.setAdapter(adapter);
        tvRetry.setOnClickListener(this);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PaymentCategoryInfo categoryInfo = (PaymentCategoryInfo) adapter.getData().get(position);
                ArrayList<CommonPaymentEntity> commonPayments = mlistHashMap.get(categoryInfo.getId());
                if (commonPayments == null || commonPayments.size() == 0) {
                    Toast.makeText(context, "数据加载中...", Toast.LENGTH_SHORT).show();
                    return;
                }

                DialogsTouCai.showPaymentDetailDialog(context, commonPayments, categoryInfo, mUsdt_exchange_rate);
            }
        });
        checkBindStatus();
    }

    private boolean request_flag_1 = false;
    private boolean request_flag_2 = false;

    public void getAllPaymentCategory() {
        HttpAction.getAllPayCategory(null, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<PaymentCategoryInfo>>() {
                }.getType());
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(context, "");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    llEmptyLayout.setVisibility(View.GONE);
                    List<PaymentCategoryInfo> paymentCategoryInfos = getFieldObject(extra, "data", null);
                    if (paymentCategoryInfos != null && paymentCategoryInfos.size() > 0) {
                        adapter.setNewData(paymentCategoryInfos);
                    }
                } else {
                    llEmptyLayout.setVisibility(View.VISIBLE);
                    Toasts.show(context, msg, false);
                }

                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                if (adapter != null && adapter.getData().size() == 0) {
                    llEmptyLayout.setVisibility(View.VISIBLE);
                }
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

    private HashMap<String, ArrayList<CommonPaymentEntity>> mlistHashMap = new HashMap<>();

    public void getAllThirdPayment() {
        HttpAction.getAllThirdPayment(null, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", Testbean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(context, "");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    Testbean testbean = getFieldObject(extra, "data", null);
                    commonPaymentEntities.clear();

                    mUsdt_exchange_rate = testbean.getRechargeConfig().getUsdt_exchange_rate();
                    List<QrCodeListBean> qrCodeList = testbean.getQrCodeList();
                    List<ThridListBean> thridList = testbean.getThridList();
                    List<TransferListBean> transferList = testbean.getTransferList();

                    for (QrCodeListBean qrCodeListBean : qrCodeList) {
                        CommonPaymentEntity entity = new CommonPaymentEntity(qrCodeListBean.getNickName(), ENV.curr.host + qrCodeListBean.getMobileLogoPicture(),
                                qrCodeListBean.getPayChannelCategoryId(), 1, qrCodeListBean.getSort());
                        entity.setQrCodeListBean(qrCodeListBean);
                        commonPaymentEntities.add(entity);
                    }

                    for (ThridListBean thridListBean : thridList) {
                        CommonPaymentEntity entity = new CommonPaymentEntity(thridListBean.getName(), ENV.curr.host + thridListBean.getMobileLogoPicture(),
                                String.valueOf(thridListBean.getPayChannelCategoryId()), 2, thridListBean.getSort());
                        entity.setThridListBean(thridListBean);
                        commonPaymentEntities.add(entity);
                    }

                    for (TransferListBean transferListBean : transferList) {
                        CommonPaymentEntity entity = new CommonPaymentEntity(transferListBean.getFrontName(), ENV.curr.host + transferListBean.getMobileLogoPicture(),
                                String.valueOf(transferListBean.getPayChannelCategoryId()), 3, transferListBean.getSort());
                        entity.setTransferListBean(transferListBean);
                        commonPaymentEntities.add(entity);
                    }

                    List<String> payChannelIdList = new ArrayList<>();

                    for (CommonPaymentEntity entity : commonPaymentEntities) {
                        payChannelIdList.add(entity.getPayChannelCategoryId());
                    }

                    for (String s : payChannelIdList) {
                        ArrayList<CommonPaymentEntity> templist = new ArrayList<>();
                        for (CommonPaymentEntity entity : commonPaymentEntities) {
                            if (Strs.isEqual(entity.getPayChannelCategoryId(), s)) {
                                templist.add(entity);
                            }
                        }
                        Collections.sort(templist, new Comparator<CommonPaymentEntity>() {
                            @Override
                            public int compare(CommonPaymentEntity o1, CommonPaymentEntity o2) {
                                return o1.getSort().compareTo(o2.getSort());
                            }
                        });
                        mlistHashMap.put(s, templist);
                    }

                } else if (code == -10) {//code=-10 表示用户未绑卡 不展示充值渠道 并且自动跳转至绑卡界面
                    checkBankBindAndShowBankList();
                } else if (code == 1 && error == 1) {//code=-10 表示用户未绑卡 不展示充值渠道 并且自动跳转至绑卡界面
                    Toasts.show(context, msg, false);
                }

                return true;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(context);
                swipeRefresh.finishRefresh();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_toup_main;
    }

    /**
     * 验证用户是否绑定资料
     */
    public void checkBankBindAndShowBankList() {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onUserBindChecked(UserBindStatus status) {
                if (status.isBindCard() && status.isBindWithdrawPassword()) {
                    //ActBankCardList.launch(context);
                } else {
                    if (!status.isBindWithdrawName() || !status.isBindWithdrawPassword() || !status.isBindCard()) {
                        showBindTip(status, "您暂时未绑定银行卡相关信息", "请您先绑定银行卡，真实姓名，资金密码，再实行操作", "前往绑定");
                    }
                }
            }

            @Override
            public void onUserBindCheckFailed(String msg) {

            }

            @Override
            public void onAfter() {

            }
        });
    }

    private void showBindTip(UserBindStatus status, String tip1, String tip2, String right) {
        DialogsTouCai.showBindDialog(context, tip1, tip2, "", right, true, new AbCallback<Object>() {
            @Override
            public boolean callback(Object obj) {
                if (!status.isBindWithdrawName() || !status.isBindWithdrawPassword() || !status.isBindCard()) {
                    ActBindBankCardToucai.launch(context, !status.isBindWithdrawName());
                }
                DialogsTouCai.hideBindTipDialog();
                return true;
            }
        });
    }

    public void showCheckBankDialog() {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
        materialDialog = new MaterialDialog(context);
        materialDialog.setMessage("亲，先绑定取款人");
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setPositiveButton("确定", v -> {
            materialDialog.dismiss();
            ActBindBankCard.launch(context, true);
        });
        materialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRetry:
                checkBindStatus();
                break;
            default:
        }
    }

    public void checkBindStatus() {
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
                } else if (!status.isBindCard()) {
                    showBindTip(status, "您暂时未绑定银行卡", "请您先绑定银行卡再实行操作", "前往绑定", 2);
                } else {
                    getAllThirdPayment();
                    getAllPaymentCategory();
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

    private void showBindTip(UserBindStatus status, String tip1, String tip2, String right, int type) {
        DialogsTouCai.showBindDialog(getActivity(), tip1, tip2, "", right, true, new AbCallback<Object>() {
            @Override
            public boolean callback(Object obj) {
                if (!status.isBindWithdrawName() || !status.isBindWithdrawPassword() || !status.isBindCard()) {
                    ActBindBankCardToucai.launch(context, !status.isBindWithdrawName());
                }
                DialogsTouCai.hideBindTipDialog();
                return true;
            }
        });
    }
}
