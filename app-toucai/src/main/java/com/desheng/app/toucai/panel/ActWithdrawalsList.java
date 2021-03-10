package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.ab.callback.AbCallback;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.view.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.manager.USDTbean;
import com.desheng.app.toucai.model.XunibiAddressBean;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.WithdrawInfo;
import com.desheng.base.panel.ActWithdrawals;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 取款页面
 * Created by user on 2018/4/20.
 */
public class ActWithdrawalsList extends AbAdvanceActivity {

    private static MaterialDialog materialDialog;
    private RecyclerView mRecyclerView;
    private BaseQuickAdapter<EntityTemp, BaseViewHolder> adapter;
    private USDTbean usdTbean;

    public static void launch(final Activity act) {
        Intent itt = new Intent(act, ActWithdrawalsList.class);
        act.startActivity(itt);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_frag_withdraw_layout;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "提现中心");
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
        initData();
    }

    private WithdrawInfo.WithdrawConfigBean mWithdrawConfig;

    private void initData() {
        HttpAction.prepareWithdraw(this, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActWithdrawalsList.this, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", WithdrawInfo.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    WithdrawInfo data = getField(extra, "data", null);
                    mWithdrawConfig = data.getWithdrawConfig();
                    if (mWithdrawConfig != null) {
                        EntityTemp tempvip = new EntityTemp("VIP提现", R.mipmap.withdraw_vip);
                        EntityTemp tempxunibi = new EntityTemp("虚拟币提现", R.mipmap.icon_withdraw_xunibi);
                        if (mWithdrawConfig.isVipFeeRateStatus() && !mdata.contains(tempvip)) {
                            mdata.add(tempvip);
                            adapter.notifyDataSetChanged();
                        }

                        if (mWithdrawConfig.getUsdtWithdrawRate() > 0 && !mdata.contains(tempxunibi) && mWithdrawConfig.isOpenUsdtWithDraw()) {
                            mdata.add(tempxunibi);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActWithdrawalsList.this);
            }
        });
    }

    private List<EntityTemp> mdata = new ArrayList<>();

    private void initView() {
        mRecyclerView = ((RecyclerView) findViewById(R.id.recyclerView));
        mdata.add(new EntityTemp("普通提现", R.mipmap.withdraw_common));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new BaseQuickAdapter<EntityTemp, BaseViewHolder>(R.layout.item_withdraw_method_layout) {
            @Override
            protected void convert(BaseViewHolder helper, EntityTemp item) {
                helper.setText(R.id.tvName, item.getMethodName());
                helper.setImageResource(R.id.ivIcon, item.getIconRes());
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.setNewData(mdata);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                EntityTemp entityTemp = (EntityTemp) adapter.getData().get(position);
                if (entityTemp == null) {
                    return;
                }

                if (Strs.isEqual("虚拟币提现", entityTemp.getMethodName())) {
                    getXunibiPocketAddress();
                } else if (Strs.isEqual("普通提现", entityTemp.getMethodName())) {
                    ActWithdrawals.launch(ActWithdrawalsList.this, 2);
                } else if (Strs.isEqual("VIP提现", entityTemp.getMethodName())) {
                    ActWithdrawals.launch(ActWithdrawalsList.this, 1);
                }
            }
        });
    }


    public static void showCheckBankDialog(Activity act, final AbCallback<Object> callback) {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
        materialDialog = new MaterialDialog(act);
        materialDialog.setMessage("亲，先绑定取款人");
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                callback.callback(null);
            }
        });
        materialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();

    }

    class EntityTemp {
        String methodName;
        int iconRes;

        public EntityTemp(String methodName, int iconRes) {
            this.methodName = methodName;
            this.iconRes = iconRes;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public int getIconRes() {
            return iconRes;
        }

        public void setIconRes(int iconRes) {
            this.iconRes = iconRes;
        }
    }

    private void getXunibiPocketAddress() {
        HttpAction.getUsdtWithdrawAddress(this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<XunibiAddressBean>>() {
                }.getType());
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActWithdrawalsList.this, "");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    ArrayList<XunibiAddressBean> xunibiAddressBeans = getFieldObject(extra, "data", ArrayList.class);
                    if (xunibiAddressBeans != null) {
                        if (xunibiAddressBeans.size() > 0) {
                            ActXunibiWithdraw.launch(ActWithdrawalsList.this, xunibiAddressBeans,
                                    mWithdrawConfig == null ? null : mWithdrawConfig.getUsdtWithdrawRate(),
                                    mWithdrawConfig.getUsdtDrawMinCount(),
                                    mWithdrawConfig.getWithdrawStartTime() + " ~ " + mWithdrawConfig.getWithdrawEndTime());//直接提款
                        } else {//表示用户还没有添加虚拟币提款地址
                            Toast.makeText(ActWithdrawalsList.this, "请先添加提现地址", Toast.LENGTH_SHORT).show();
                            ActAddXunibiPocketAddress.launch(ActWithdrawalsList.this);
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toast.makeText(ActWithdrawalsList.this, content + "", Toast.LENGTH_SHORT).show();
                Dialogs.hideProgressDialog(ActWithdrawalsList.this);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(ActWithdrawalsList.this);
            }
        });
    }
}

   
