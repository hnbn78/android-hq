package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.callback.AbCallback;
import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.app.toucai.global.ConfigTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.Utils;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.ThirdPocketInfo;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.view.TransferBottomDialog;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import okhttp3.Request;

import static com.desheng.app.toucai.panel.ActBindBankCardToucai.BIND_OK_RESULT_CODE;

/**
 * 游戏第三方平台间转账
 * Created by lee on 2018/4/11.
 */
public class ActThirdTransferFastNew extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {


    private ThirdPocketInfo main;
    private String password;
    private SwipeRefreshLayout srlRefresh;
    private ImageView ivEye;
    private int WITHDRAW_REQUEST_CODE = 10;
    private int requestCode = 0;

    public static void launch(Activity act, String pwd) {
        Intent itt = new Intent(act, ActThirdTransferFastNew.class);
        itt.putExtra("password", pwd);
        act.startActivity(itt);
    }

    public static void launch(Activity act) {
        Intent itt = new Intent(act, ActThirdTransferFastNew.class);
        act.startActivity(itt);
    }


    private TextView tvMainBalance;
    private Button btnRecharge, btnTransfer, btnWithdraw;
    private Queue<Runnable> qAllRetrive;

    private ViewGroup[] arrVgGroup = new ViewGroup[7];
    private ImageView[] arrIvIcons = new ImageView[7];
    private TextView[] arrTvTitles = new TextView[7];
    private TextView[] arrTvBalances = new TextView[7];
    private TextView[] arrTvTransfers = new TextView[7];
    private ImageView[] arrIvRefreshs = new ImageView[7];

    private LinearLayout vgAG, vgIM, vgKY, vgDS, vgPT, vgCq9, vgDatang;

    private ArrayList<ThirdPocketInfo> listPockets;

    @Override
    protected int getBaseLayoutId() {
        return R.layout.ab_activity_base_overlay_toolbar_front;
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_third_transfer_fast_new;
    }

    @Override
    public void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "转账");
        setToolbarBgColor(R.color.ab_translucent);
        setStatusBarBackgroundAndContentWithPadding(Color.TRANSPARENT, 0, false);
        password = getIntent().getStringExtra("password");

        final View root = getLayout();
        root.post(new Runnable() {
            @Override
            public void run() {
                paddingHeadOfAllHeight(findViewById(R.id.vgHead));
            }
        });

        srlRefresh = findViewById(R.id.srlRefresh);
        srlRefresh.setOnRefreshListener(this);

        tvMainBalance = findViewById(R.id.tvMainBalance);

        btnWithdraw = findViewById(R.id.btnWithdraw);
        btnRecharge = findViewById(R.id.btnRecharge);
        btnTransfer = findViewById(R.id.btnTransfer);

        btnRecharge.setOnClickListener(this);
        btnWithdraw.setOnClickListener(this);
        btnTransfer.setOnClickListener(this);

        vgKY = findViewById(R.id.vgKY);
        vgIM = findViewById(R.id.vgIM);
        vgAG = findViewById(R.id.vgAG);
        vgDS = findViewById(R.id.vgDS);
        vgPT = findViewById(R.id.vgPT);
        vgCq9 = findViewById(R.id.vgCq9);
        vgDatang = findViewById(R.id.vgDatang);

        if (Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_2)
                || Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_3)) {
            vgPT.setVisibility(View.VISIBLE);
            vgCq9.setVisibility(View.VISIBLE);
            vgDatang.setVisibility(View.VISIBLE);
        } else {
            if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.PT.getPlatformId()) {
                vgPT.setVisibility(View.VISIBLE);
                vgCq9.setVisibility(View.GONE);
            } else if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.CQ.getPlatformId()) {
                vgPT.setVisibility(View.GONE);
                vgCq9.setVisibility(View.VISIBLE);
            }
        }

        vgKY.setOnClickListener(this);
        vgIM.setOnClickListener(this);
        vgAG.setOnClickListener(this);
        vgDS.setOnClickListener(this);
        vgPT.setOnClickListener(this);
        vgCq9.setOnClickListener(this);
        vgDatang.setOnClickListener(this);

        int[] ids = null;
        if (Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_2)
                || Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_3)) {
            ids = new int[]{R.id.vgAG, R.id.vgIM, R.id.vgKY, R.id.vgDS, R.id.vgPT, R.id.vgCq9, R.id.vgDatang};
        } else {
            if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.PT.getPlatformId()) {
                ids = new int[]{R.id.vgAG, R.id.vgIM, R.id.vgKY, R.id.vgDS, R.id.vgPT};
            } else if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.CQ.getPlatformId()) {
                ids = new int[]{R.id.vgAG, R.id.vgIM, R.id.vgKY, R.id.vgDS, R.id.vgCq9};
            }

        }

        for (int i = 0; i < arrTvBalances.length; i++) {
            arrVgGroup[i] = root.findViewById(ids[i]);
        }

        if (Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_2)
                || Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_3)) {
            ids = new int[]{R.id.ivAG, R.id.ivIM, R.id.ivKY, R.id.ivDS, R.id.ivPT, R.id.ivCq9, R.id.ivDatang};
        } else {
            if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.PT.getPlatformId()) {
                ids = new int[]{R.id.ivAG, R.id.ivIM, R.id.ivKY, R.id.ivDS, R.id.ivPT};
            } else if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.CQ.getPlatformId()) {
                ids = new int[]{R.id.ivAG, R.id.ivIM, R.id.ivKY, R.id.ivDS, R.id.ivCq9};
            }

        }

        for (int i = 0; i < arrIvIcons.length; i++) {
            arrIvIcons[i] = root.findViewById(ids[i]);
        }

        if (Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_2)
                || Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_3)) {
            ids = new int[]{R.id.tvAG, R.id.tvIM, R.id.tvKY, R.id.tvDS, R.id.tvPT, R.id.tvCq9, R.id.tvDatang};
        } else {
            if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.PT.getPlatformId()) {
                ids = new int[]{R.id.tvAG, R.id.tvIM, R.id.tvKY, R.id.tvDS, R.id.tvPT};
            } else if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.CQ.getPlatformId()) {
                ids = new int[]{R.id.tvAG, R.id.tvIM, R.id.tvKY, R.id.tvDS, R.id.tvCq9};
            }

        }
        for (int i = 0; i < arrTvTitles.length; i++) {
            arrTvTitles[i] = root.findViewById(ids[i]);
        }

        if (Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_2)
                || Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_3)) {
            ids = new int[]{R.id.tvBalanceAG, R.id.tvBalanceIM, R.id.tvBalanceKY, R.id.tvBalanceDS, R.id.tvBalancePT, R.id.tvBalanceCq9, R.id.tvBalanceDatang};
        } else {
            if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.PT.getPlatformId()) {
                ids = new int[]{R.id.tvBalanceAG, R.id.tvBalanceIM, R.id.tvBalanceKY, R.id.tvBalanceDS, R.id.tvBalancePT};
            } else if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.CQ.getPlatformId()) {
                ids = new int[]{R.id.tvBalanceAG, R.id.tvBalanceIM, R.id.tvBalanceKY, R.id.tvBalanceDS, R.id.tvBalanceCq9};
            }
        }

        for (int i = 0; i < arrTvBalances.length; i++) {
            arrTvBalances[i] = root.findViewById(ids[i]);
        }

        if (Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_2)
                || Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_3)) {
            ids = new int[]{R.id.tvTransferAG, R.id.tvTransferIM, R.id.tvTransferKY, R.id.tvTransferDS, R.id.tvTransferPT, R.id.tvTransferCq9, R.id.tvTransferDatang};
        } else {
            if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.PT.getPlatformId()) {
                ids = new int[]{R.id.tvTransferAG, R.id.tvTransferIM, R.id.tvTransferKY, R.id.tvTransferDS, R.id.tvTransferPT};
            } else if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.CQ.getPlatformId()) {
                ids = new int[]{R.id.tvTransferAG, R.id.tvTransferIM, R.id.tvTransferKY, R.id.tvTransferDS, R.id.tvTransferCq9};
            }
        }
        for (int i = 0; i < arrTvTransfers.length; i++) {
            arrTvTransfers[i] = root.findViewById(ids[i]);
        }

        if (Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_2)
                || Strs.isEqual(BaseConfig.custom_flag, ConfigTouCai.FLAG_JINFENG_3)) {
            ids = new int[]{R.id.ivRefreshAG, R.id.ivRefreshIM, R.id.ivRefreshKY, R.id.ivRefreshDS, R.id.ivRefreshPT, R.id.ivRefreshCq9, R.id.ivRefreshDatang};
        } else {
            if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.PT.getPlatformId()) {
                ids = new int[]{R.id.ivRefreshAG, R.id.ivRefreshIM, R.id.ivRefreshKY, R.id.ivRefreshDS, R.id.ivRefreshPT};
            } else if (CommonConsts.setHomeThirdGameType(BaseConfig.custom_flag) == ThirdGamePlatform.CQ.getPlatformId()) {
                ids = new int[]{R.id.ivRefreshAG, R.id.ivRefreshIM, R.id.ivRefreshKY, R.id.ivRefreshDS, R.id.ivRefreshCq9};
            }
        }
        for (int i = 0; i < arrIvRefreshs.length; i++) {
            arrIvRefreshs[i] = root.findViewById(ids[i]);
        }

        listPockets = new ArrayList<>();

        qAllRetrive = new ConcurrentLinkedQueue<>();

        ivEye = (ImageView) findViewById(R.id.ivEye);
        ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"******".equals(tvMainBalance.getText())) {
                    ivEye.setImageResource(R.mipmap.ic_eye_activited);
                    tvMainBalance.setText("******");
                } else {
                    ivEye.setImageResource(R.mipmap.ic_eye_normal);
                    tvMainBalance.setText(Nums.formatDecimal(UserManager.getIns().getLotteryAvailableBalance(), 3));
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        initPocketInfo(true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btnRecharge) {
            ActDeposit.launch(ActThirdTransferFastNew.this);
        } else if (v == btnTransfer && main != null) {
            if (main.balance == 0) {
                DialogsTouCai.showBindDialog(this, "您可转出余额为0", "无法进行转账",
                        "确认", "充值", true, new AbCallback<Object>() {
                            @Override
                            public boolean callback(Object obj) {
                                // ActTransferThird.launch(ActThirdTransferFastNew.this, null);
                                ActDeposit.launch(ActThirdTransferFastNew.this);
                                return false;
                            }
                        });
            } else {
                ActTransferThird.launch(ActThirdTransferFastNew.this, null);
            }

        } else if (v == btnWithdraw) {
            //         if (UserManager.getIns().getIsBindCellphone()) {
//
            requestCode = WITHDRAW_REQUEST_CODE;
            checkBindStatus();
//            } else {
//                showBindPhoneTip();
//            }
        }

//        else if (v == vgKY) {
//            ActTransferThird.launch(this);
//        } else if (v == vgAG) {
//            ActTransferThird.launch(this);
//        } else if (v == vgIM) {
//            ActTransferThird.launch(this);
//        }
    }

    private void checkBindStatus() {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onUserBindChecked(UserBindStatus status) {
                if (!status.isBindWithdrawName() || !status.isBindWithdrawPassword() || !status.isBindCard()) {
                    showBindTip(status, "您暂时未绑定银行卡相关信息", "请您先绑定银行卡，真实姓名，资金密码，再实行操作", "前往绑定");
                } else {
                    if (UserManager.getIns().getLotteryAvailableBalance() <= 0) {
                        showBindTip(status, "无法进行提现交易", "您当前可提现金额小于最低要求", "确认");
                    } else {
                        ActWithdrawals.launch(ActThirdTransferFastNew.this);
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
        DialogsTouCai.showBindDialog(ActThirdTransferFastNew.this, tip1, tip2, "", right, true, new AbCallback<Object>() {
            @Override
            public boolean callback(Object obj) {

                if (!status.isBindWithdrawName() || !status.isBindWithdrawPassword() || !status.isBindCard()) {
                    ActBindBankCardToucai.launch(ActThirdTransferFastNew.this, true);
                } else if (!status.isBindCellphone()) {
                    launcherAct(ActBindPhone.class);
                } else if (UserManager.getIns().getLotteryAvailableBalance() == 0) {
                    launcherAct(ActBankCardListTouCai.class);
                }
                DialogsTouCai.hideBindTipDialog();
                return true;
            }
        });
    }

    private void launcherAct(Class cls) {
        Intent intent = new Intent(ActThirdTransferFastNew.this, cls);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onRefresh() {
        initPocketInfo(true);
    }

    private void initPocketInfo(boolean showLoading) {
        HttpAction.getThirdPocketList(ActThirdTransferFastNew.this, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (!showLoading) return;
                getLayout().post(new Runnable() {
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
                    if (data == null || data.size() == 0) {
                        for (int i = 0; i < arrVgGroup.length; i++) {
                            arrVgGroup[i].setVisibility(View.GONE);
                        }

                    } else {
                        listPockets.clear();
                        for (ThirdPocketInfo datum : data) {
                            if (datum.platformId == 5 || datum.platformId == 6 ||
                                    datum.platformId == 23 || datum.platformId == 24 ||
                                    datum.platformId == 10 || datum.platformId == 27 || datum.platformId == 28) {
                                listPockets.add(datum);
                            }
                        }

                    }

                    main = new ThirdPocketInfo();
                    main.cbId = CtxLottery.MAIN_POCKET_CBID;
                    main.balance = UserManager.getIns().getLotteryAvailableBalance();
                    main.cbName = CtxLottery.MAIN_POCKET_NAME;
                    main.platformId = -1;
                    tvMainBalance.setText(Nums.formatDecimal(main.balance, 3));

                    for (int i = 0; i < arrTvBalances.length; i++) {
                        if (i < listPockets.size()) {
                            ThirdPocketInfo bean = listPockets.get(i);
                            ThirdGamePlatform platform = ThirdGamePlatform.findByPlatformId(bean.platformId);
                            if (platform == null) {
                                continue;
                            }
                            arrVgGroup[i].setVisibility(View.VISIBLE);
                            arrIvIcons[i].setImageResource(platform.getIcon());
                            arrTvTitles[i].setText(bean.cbName);
                            queryAccountMoney(arrTvBalances[i], platform.getPlatformId(), false);
                            arrTvTransfers[i].setTag(i);
                            arrTvTransfers[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (main.balance - 0.0 < 1e-6) {
                                        Toasts.show(ActThirdTransferFastNew.this, "主账户无整数余额", false);
                                        return;
                                    }
                                    final int index = (Integer) v.getTag();
                                    DialogsTouCai.showDialog(ActThirdTransferFastNew.this, "温馨提醒", String.format("是否确定将余额%s元转至%s? 转账后余额显示有延迟, 如无更新请手工刷新余额", String.valueOf(Math.floor(main.balance)), platform.getTitle()), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            commitTransfer(main.cbId, listPockets.get(index).cbId, Math.floor(main.balance), password);
                                        }
                                    });
                                }
                            });
                            arrIvRefreshs[i].setTag(i);
                            arrIvRefreshs[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int index = (Integer) v.getTag();
//                                    queryAccountMoney(arrTvBalances[index], listPockets.get(index).platformId);
                                    //                 showTransferDialog(listPockets.get(index).cbId, listPockets.get(index).cbName, main.cbName, bean.balance, UserManager.getIns().getLotteryAvailableBalance());
                                    queryAccountMoney(arrTvBalances[index], listPockets.get(index).platformId, true);
                                }
                            });
                            arrVgGroup[i].setTag(i);
                            arrVgGroup[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //         ActTransferThird.launch(ActThirdTransferFastNew.this, platform);
                                    int index = (Integer) v.getTag();
//                                    queryAccountMoney(arrTvBalances[index], listPockets.get(index).platformId);
                                    showTransferDialog(listPockets.get(index).cbId, listPockets.get(index).cbName, main.cbName, Strs.parse(arrTvBalances[index].getText().toString(), 0.0f), UserManager.getIns().getLotteryAvailableBalance());
                                }
                            });

                        } else {
                            arrVgGroup[i].setVisibility(View.GONE);
                        }
                    }

                } else {
                    Toasts.show(ActThirdTransferFastNew.this, msg, false);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActThirdTransferFastNew.this, content, false);
                return true;
            }

            @Override
            public void onAfter(int id) {
                getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                }, 500);
            }
        });
    }


    private void showTransferDialog(String id, String in_name, String out_name, double amount_in, double amount_out) {
        TransferBottomDialog.Builder builder = new TransferBottomDialog.Builder(this);
        builder.setTransfer_in_name(in_name);
        builder.setTransfer_out_name(out_name);
        builder.setTransfer_in_amount(amount_in);
        builder.setTransfer_out_amount(amount_out);

        final TransferBottomDialog dateDialog = builder.create();

        builder.setOnConfirmClickListener(new TransferBottomDialog.OnConfirmListener() {
            @Override
            public void onClick(String amount, String pwd) {
                try {
                    commitTransfer(main.cbId, id, Math.floor(Double.parseDouble(amount)), pwd);
                    dateDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onWalletClick(double amount, String pwd) {

                double doubleNotRound = Utils.getDoubleNotRound1(amount);

                if (doubleNotRound > 0) {
                    String content = "是否确定将" + doubleNotRound + "元，转至钱包？转账后余额显示有延迟，如无更新请手工刷新余额 ";
                    DialogsTouCai.showDialog(ActThirdTransferFastNew.this, "温馨提示", content,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    commitTransfer(id, main.cbId, doubleNotRound, pwd);
                                    dateDialog.dismiss();
                                }
                            });
                } else {
                    DialogsTouCai.showDialog(ActThirdTransferFastNew.this, "温馨提示",
                            "您可带回的余额为0无法进行转账",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                }
            }
        });
        dateDialog.show();
    }


    private void commitTransfer(final String cbIdOut, final String cbIdIn, double amount, final String password) {
        if (CtxLottery.MAIN_POCKET_CBID.equals(cbIdOut) && amount <= 0) {
            Toasts.show(ActThirdTransferFastNew.this, "尚无可转账金额", false);
            //检查队列
            if (qAllRetrive.peek() != null) {
//                btnRetrive.postDelayed(qAllRetrive.poll(), 2000);
            }
            return;
        }

        if (BaseConfig.FLAG_TOUCAI_DEMO.equals(Config.custom_flag)) {
            if (amount > 50) {
                amount = 50;
            }
        }

        if (Strs.isEmpty(password)) {
            Toasts.show(ActThirdTransferFastNew.this, "请输入资金密码", false);
            return;
        }

        final double finalAmount = amount;
        final String outAccount = CtxLottery.MAIN_POCKET_CBID.equals(cbIdOut) ? CtxLottery.MAIN_POCKET_NAME :
                ThirdGamePlatform.findByCbId(cbIdOut).getTitle();

        HttpAction.getPlayerTransferRefreshToken(ActThirdTransferFastNew.this, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActThirdTransferFastNew.this, "");
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

                        HttpAction.commitPlayerTransferTC(ActThirdTransferFastNew.this, cbIdOut, cbIdIn, finalAmount, password, token, new AbHttpResult() {
                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                if (code == 0 && error == 0) {

                                    //同步用户数据
                                    UserManager.getIns().initUserData(new UserManager.IUserDataSyncCallback() {
                                        @Override
                                        public void onUserDataInited() {

                                        }

                                        @Override
                                        public void afterUserDataInited() {
                                            //同步当前余额数据
                                            initPocketInfo(false);
                                            //检查队列
                                            if (qAllRetrive.peek() != null) {
//                                                btnRetrive.postDelayed(qAllRetrive.poll(), 1000);
                                            }
                                        }

                                        @Override
                                        public void onUserDataInitFaild() {

                                        }

                                        @Override
                                        public void onAfter() {

                                        }
                                    });
                                    DialogsTouCai.showTransferThirdSuccess(Act, cbIdIn);
                                } else if ("平台钱包余额不足".equals(msg)) {
                                    DialogsTouCai.showTransferBalanceNotEnough(Act);
                                } else {
                                    DialogsTouCai.showDialog(ActThirdTransferFastNew.this, "温馨提醒", outAccount + "转出结果: " + msg, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                                    //检查队列
                                    if (qAllRetrive.peek() != null) {
//                                        btnRetrive.postDelayed(qAllRetrive.poll(), 2000);
                                    }
                                }
                                return true;
                            }

                            @Override
                            public boolean onError(int status, String content) {
                                DialogsTouCai.hideProgressDialog(ActThirdTransferFastNew.this);
                                Toasts.show(ActThirdTransferFastNew.this, content, false);
                                //检查队列
                                if (qAllRetrive.peek() != null) {
//                                    btnRetrive.postDelayed(qAllRetrive.poll(), 2000);
                                }
                                return true;
                            }

                            @Override
                            public void onAfter(int id) {
                                DialogsTouCai.hideProgressDialog(ActThirdTransferFastNew.this);
                            }
                        });
                    }
                } else {
                    DialogsTouCai.hideProgressDialog(ActThirdTransferFastNew.this);
                    Toasts.show(ActThirdTransferFastNew.this, msg, false);
                    //检查队列
                    if (qAllRetrive.peek() != null) {
//                        btnRetrive.postDelayed(qAllRetrive.poll(), 2000);
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                DialogsTouCai.hideProgressDialog(ActThirdTransferFastNew.this);
                Toasts.show(ActThirdTransferFastNew.this, content, false);
                //检查队列
                if (qAllRetrive.peek() != null) {
                    qAllRetrive.poll().run();
                }
                return true;
            }
        });
    }

    private void queryAccountMoney(final TextView tvAccountAmount, int platformId, boolean showLoading) {
        //MM.http.cancellAllByTag(ActThirdTransfer.this);
        HttpAction.getThirdPocketAmount(ActThirdTransferFastNew.this, platformId, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (showLoading)
                    DialogsTouCai.showProgressDialog(ActThirdTransferFastNew.this, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", Double.TYPE);
//                entity.putField("data", new TypeToken<ArrayList<ThirdPocketInfo>>() {
//                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                Double money = getField(extra, "data", 0.0);
                tvAccountAmount.setText(Nums.formatDecimal(money, 3));
//                ArrayList<ThirdPocketInfo> data = getField(extra, "data", null);
//                if (data != null && data.size() > 0) {
//                    for (ThirdPocketInfo pocketInfo : data) {
//                        if (pocketInfo.platformId == platformId) {
//                            tvAccountAmount.setText(Nums.formatDecimal(pocketInfo.balance, 3));
//                        }
//                    }
//                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                tvAccountAmount.setText("");
                return true;
            }

            @Override
            public void onAfter(int id) {
                if (showLoading) {
                    DialogsTouCai.hideProgressDialog(ActThirdTransferFastNew.this);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WITHDRAW_REQUEST_CODE && resultCode == BIND_OK_RESULT_CODE
                || resultCode == ActEditAccount.RESULT_CODE) {
            this.requestCode = 0;
            if (UserManager.getIns().getIsBindPhone())
                launcherAct(ActWithdrawals.class);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MM.http.cancellAllByTag(ActThirdTransferFastNew.this);
    }

}
