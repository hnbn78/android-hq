package com.desheng.base.panel;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ArraysAndLists;
import com.ab.util.Dialogs;
import com.ab.util.MD5;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.ThirdPocketInfo;
import com.desheng.base.util.DeviceUtil;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

/**
 * 游戏第三方平台间转账
 * Created by lee on 2018/4/11.
 */
public class ActThirdTransfer extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static void launch(Activity act) {
        simpleLaunch(act, ActThirdTransfer.class);
    }

    private TextView tvAccountOut;
    private TextView tvAccountOutAmount;
    private TextView tvAccountIn;
    private TextView tvAccountInAmount, tvAccountInBalance;
    private ArrayList<ThirdPocketInfo> listPockets;
    private ArrayList<String> bankNameList;
    private Button btnConfirm;
    private SwipeRefreshLayout srlRefresh;
    private EditText etAmount;
    private EditText etPassword;

    @Override
    public int getLayoutId() {
        return R.layout.act_third_transfer;
    }

    @Override
    public void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "第三方转账");
        setStatusBarTranslucentAndLightContentWithPadding();

        final View root = getLayout();
        srlRefresh = (SwipeRefreshLayout) root.findViewById(R.id.srlWithdrawRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);

        tvAccountOut = (TextView) root.findViewById(R.id.tvAccountOut);
        tvAccountOutAmount = (TextView) root.findViewById(R.id.tvAccountOutAmount);
        tvAccountIn = (TextView) root.findViewById(R.id.tvAccountIn);
        tvAccountInAmount = (TextView) root.findViewById(R.id.tvAccountInAmount);
        tvAccountInBalance = root.findViewById(R.id.tvAccountInBalance);

        bankNameList = new ArrayList<>();
        listPockets = new ArrayList<>();

        if(Config.custom_flag.equals(BaseConfig.FLAG_CAIYING)){
            tvAccountInBalance.setVisibility(View.INVISIBLE);
        }

        RelativeLayout vgTransferOut = (RelativeLayout) root.findViewById(R.id.vgTransferOut);
        vgTransferOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceUtil.hideInputKeyboard(ActThirdTransfer.this);

                if (listPockets == null || listPockets.size() == 0 || bankNameList.size() == 0) {
                    return;
                }

                OptionsPickerView pvOptions = new OptionsPickerView(ActThirdTransfer.this);
                pvOptions.setTitle("");
                pvOptions.setCancelText("取消");
                pvOptions.setCancelTextColor(Color.GRAY);
                pvOptions.setCancelTextSize(14f);
                pvOptions.setSubmitText("确定");
                pvOptions.setSubmitTextColor(Color.BLACK);
                pvOptions.setSubmitTextSize(14f);
                pvOptions.setHeadBackgroundColor(Color.WHITE);

                pvOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int option1, int option2, int option3) {
                        //返回的分别是三个级别的选中位置

                        tvAccountOut.setText(bankNameList.get(option1));
                        tvAccountOutAmount.setText("余额查询中");
                        queryAccountMoney(bankNameList.get(option1), tvAccountOut, tvAccountOutAmount);
                    }
                });
                pvOptions.setPicker(bankNameList);
                pvOptions.show();

            }
        });

        RelativeLayout vgTransferIn = (RelativeLayout) root.findViewById(R.id.vgTransferIn);
        vgTransferIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceUtil.hideInputKeyboard(ActThirdTransfer.this);
                if (listPockets == null || listPockets.size() == 0 || bankNameList.size() == 0) {
                    return;
                }

                OptionsPickerView pvOptions = new OptionsPickerView(ActThirdTransfer.this);
                pvOptions.setTitle("");
                pvOptions.setCancelText("取消");
                pvOptions.setCancelTextColor(Color.GRAY);
                pvOptions.setCancelTextSize(14f);
                pvOptions.setSubmitText("确定");
                pvOptions.setSubmitTextColor(Color.BLACK);
                pvOptions.setSubmitTextSize(14f);
                pvOptions.setHeadBackgroundColor(Color.WHITE);

                pvOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int option1, int option2, int option3) {
                        //返回的分别是三个级别的选中位置

                        tvAccountInAmount.setText(bankNameList.get(option1));
                        tvAccountInBalance.setText("余额查询中");

                        queryAccountMoney(bankNameList.get(option1), tvAccountInAmount, tvAccountInBalance);
                    }
                });
                pvOptions.setPicker(bankNameList);
                pvOptions.show();

            }
        });

        etAmount = (EditText) root.findViewById(R.id.etAmount);
        etPassword = (EditText) root.findViewById(R.id.etPassword);
        btnConfirm = (Button) root.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountOut = Views.getText((TextView) root.findViewById(R.id.tvAccountOut));
                if (Strs.isEmpty(accountOut)) {
                    Toasts.show(ActThirdTransfer.this, "请选择转出账户!", false);
                    return;
                }

                String accountIn = Views.getText((TextView) root.findViewById(R.id.tvAccountInAmount));
                if (Strs.isEmpty(accountIn)) {
                    Toasts.show(ActThirdTransfer.this, "请选择转入账户!", false);
                    return;
                }

                if (!(accountOut.equals(CtxLottery.MAIN_POCKET_NAME) || accountIn.equals(CtxLottery.MAIN_POCKET_NAME))) {
                    Toasts.show(ActThirdTransfer.this, "转出或转入账户必须包含主账户!", false);
                    return;
                }

                if (accountIn.equals(accountOut)) {
                    Toasts.show(ActThirdTransfer.this, "相同账户不能转账!", false);
                    return;
                }

                String amountStr = Views.getText(etAmount);
                double amount = Nums.parse(amountStr, 0.0);
                if (amount <= 0) {
                    Toasts.show(ActThirdTransfer.this, "请输入正确金额", false);
                    return;
                }
                if (!Nums.isNoMoreFractionDigits(amountStr, 1)) {
                    Toasts.show(ActThirdTransfer.this, "转账支持最多一位小数", false);
                    return;
                }

                String outBalanceStr = Views.getText(tvAccountOutAmount);
                double outBalance = Nums.parse(outBalanceStr.replace("余额:", ""), -1.0);
                if (amount > outBalance) {
                    Toasts.show(ActThirdTransfer.this, "转账金额超过转出账户余额, 或转出账户尚未查询到余额, 请重试", false);
                    return;
                }

                String password = Views.getText(etPassword);
                if (Strs.isEmpty(password)) {
                    Toasts.show(ActThirdTransfer.this, "请输入当前密码", false);
                    return;
                }

                commitTransfer(accountOut, accountIn, amount, password);
            }
        });

        initPocketInfo();
    }

    @Override
    public void onRefresh() {
        initPocketInfo();
    }


    private void initPocketInfo() {
        HttpAction.getThirdPocketList(ActThirdTransfer.this, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                srlRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                });
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<ThirdPocketInfo>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    ArrayList<ThirdPocketInfo> data = getField(extra, "data", null);
                    if (Config.custom_flag.equals(BaseConfig.FLAG_CAISHIJI)) {
                        /*去除ag钱包*/
                        for (ThirdPocketInfo info : data) {
                            if (info.cbName.equals("ag钱包")) {
                                data.remove(info);
                                break;
                            }
                        }
                    }
                    if (data == null || data.size() == 0) {
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toasts.show(ActThirdTransfer.this, "无可转账列表", false);
                            }
                        });
                    } else {
                        listPockets.clear();
                        ThirdPocketInfo main = new ThirdPocketInfo();
                        main.cbId = CtxLottery.MAIN_POCKET_CBID;
                        main.balance = UserManager.getIns().getLotteryAvailableBalance();
                        main.cbName = CtxLottery.MAIN_POCKET_NAME;
                        main.platformId = -1;
                        listPockets.add(main);
                        listPockets.addAll(data);
                    }
                    bankNameList.clear();
                    for (int i = 0; i < listPockets.size(); i++) {
                        bankNameList.add(listPockets.get(i).cbName);
                    }
                    if (Strs.isNotEmpty(Views.getText(tvAccountOut))) {
                        queryAccountMoney(Views.getText(tvAccountOut), tvAccountOut, tvAccountOutAmount);
                    }
                    if (Strs.isNotEmpty(Views.getText(tvAccountInAmount))) {
                        queryAccountMoney(Views.getText(tvAccountInAmount), tvAccountInAmount, tvAccountInBalance);
                    }
                } else {
                    Toasts.show(ActThirdTransfer.this, msg, false);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActThirdTransfer.this, content, false);
                return true;
            }

            @Override
            public void onAfter(int id) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                }, 400);
            }
        });
    }

    private void commitTransfer(String accountOut, String accountIn, final double amount, final String password) {
        final String cbIdOut = listPockets.get(ArraysAndLists.findIndexWithEqualsOfList(accountOut, bankNameList)).cbId;
        final String cbIdIn = listPockets.get(ArraysAndLists.findIndexWithEqualsOfList(accountIn, bankNameList)).cbId;
        HttpAction.getPlayerTransferRefreshToken(ActThirdTransfer.this, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                btnConfirm.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActThirdTransfer.this, "");
                    }
                });
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", String.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    String token = getField(extra, "data", "");
                    if (Strs.isNotEmpty(token)) {
                        HttpAction.commitPlayerTransfer(ActThirdTransfer.this, cbIdOut, cbIdIn, amount, MD5.md5(password).toLowerCase(), token, new AbHttpResult() {
                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                if (code == 0 && error == 0) {
                                    Toasts.show(ActThirdTransfer.this, "转账成功, 请稍候查询余额!", true);
                                    //同步用户数据
                                    UserManager.getIns().initUserData(new UserManager.IUserDataSyncCallback() {
                                        @Override
                                        public void onUserDataInited() {

                                        }

                                        @Override
                                        public void afterUserDataInited() {
                                            //同步当前余额数据
                                            if(Config.custom_flag.equals(BaseConfig.FLAG_JINDU)){
                                                initPocketInfo();
                                            }else{
//                                            或关闭当前页面, 非实时可查到余额变化
                                                finish();
                                            }

                                        }

                                        @Override
                                        public void onUserDataInitFaild() {

                                        }

                                        @Override
                                        public void onAfter() {

                                        }
                                    });
                                } else {
                                    String msgContent = msg;
                                    // FIXME: 2018/8/6/006 上线前要去掉
                                    if (Config.custom_flag.equals(BaseConfig.FLAG_HUIXIN_ONLINE) && code == 1)
                                        msgContent = "即将开启 敬请期待";
                                    Toasts.show(ActThirdTransfer.this, msgContent, false);
                                }
                                return true;
                            }

                            @Override
                            public boolean onError(int status, String content) {
                                Dialogs.hideProgressDialog(ActThirdTransfer.this);
                                Toasts.show(ActThirdTransfer.this, content, false);
                                return true;
                            }

                            @Override
                            public void onAfter(int id) {
                                Dialogs.hideProgressDialog(ActThirdTransfer.this);
                            }
                        });
                    }
                } else {

                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Dialogs.hideProgressDialog(ActThirdTransfer.this);
                Toasts.show(ActThirdTransfer.this, content, false);
                return true;
            }
        });
    }

    private void queryAccountMoney(final String platFormName, final TextView tvAccount, final TextView tvBalance) {
        //MM.http.cancellAllByTag(ActThirdTransfer.this);
        int index = ArraysAndLists.findIndexWithEqualsOfList(platFormName, bankNameList);
        if (index == 0) {
            tvAccount.setText(platFormName);
            tvBalance.setText("余额: " + Nums.formatDecimal(listPockets.get(index).balance, 3));
        } else {
            HttpAction.getThirdPocketAmount(ActThirdTransfer.this, listPockets.get(index).platformId, new AbHttpResult() {
                @Override
                public void setupEntity(AbHttpRespEntity entity) {
                    entity.putField("data", Double.TYPE);
                }

                @Override
                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                    Double money = getField(extra, "data", 0.0);
                    tvBalance.setText("余额: " + Nums.formatDecimal(money, 3));
                    tvAccount.setText(platFormName);
                    return true;
                }

                @Override
                public boolean onError(int status, String content) {
                    tvBalance.setText("");
                    tvAccount.setText("");
                    return true;
                }
            });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
