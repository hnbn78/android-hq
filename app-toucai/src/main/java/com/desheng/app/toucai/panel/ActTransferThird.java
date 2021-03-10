package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.ThirdPocketInfo;
import com.desheng.base.view.WalletListPopupWindow;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActTransferThird extends AbAdvanceActivity implements WalletListPopupWindow.OnItemSelectedListener {

    private ImageView iv_transfer_in, iv_transfer_out;
    private TextView tv_transfer_in, tv_transfer_out;
    private EditText et_transfer_amount;
    private Button btn_recharge, btn_confirm_transfer;
    private LinearLayout layout_choose_trans_out, layout_choose_trans_in;
    private WalletListPopupWindow walletPop;

    private int type;
    private final int IN = 1;
    private final int OUT = 2;

    private List<String> wallet_names;
    private int[] wallet_images;
    private String[] cbIds;
    private double[] balance;

    private String inId;
    private String outId;

    private ArrayList<ThirdPocketInfo> listPockets;
    private ThirdGamePlatform platform;
    private EditText et_transfer_pwd;

    public static void launch(Activity act, ThirdGamePlatform platform) {
        Intent itt = new Intent(act, ActTransferThird.class);
        itt.putExtra("IN_PLATFORM", platform);
        act.startActivity(itt);
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "转账");
        setStatusBarTranslucentAndLightContentWithPadding();
        listPockets = new ArrayList<>();
        wallet_names = new ArrayList<>();
        platform = (ThirdGamePlatform) getIntent().getSerializableExtra("IN_PLATFORM");

        iv_transfer_in = findViewById(R.id.iv_transfer_in);
        iv_transfer_out = findViewById(R.id.iv_transfer_out);
        tv_transfer_in = findViewById(R.id.tv_transfer_in);
        tv_transfer_out = findViewById(R.id.tv_transfer_out);
        et_transfer_amount = findViewById(R.id.et_transfer_amount);//
        btn_recharge = findViewById(R.id.btn_recharge);
        btn_confirm_transfer = findViewById(R.id.btn_confirm_transfer);
        et_transfer_pwd = findViewById(R.id.et_transfer_pwd);
        btn_recharge.setOnClickListener(this);
        btn_confirm_transfer.setOnClickListener(this);

        layout_choose_trans_out = findViewById(R.id.layout_choose_trans_out);
        layout_choose_trans_in = findViewById(R.id.layout_choose_trans_in);

        layout_choose_trans_out.setOnClickListener(this);
        layout_choose_trans_in.setOnClickListener(this);

        getThirdPocketList();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_third;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == layout_choose_trans_in) {
            if (walletPop != null && wallet_names != null && wallet_names.size() > 0) {
                walletPop.showAsDropDown(tv_transfer_in, -100, 0);
                type = IN;
            }
        } else if (v == layout_choose_trans_out) {
            if (walletPop != null && wallet_names != null && wallet_names.size() > 0) {
                walletPop.showAsDropDown(tv_transfer_out, -100, 0);
                type = OUT;
            }
        } else if (v == btn_confirm_transfer) {

            String amount = et_transfer_amount.getText().toString();
            String pwd = et_transfer_pwd.getText().toString();
            if (Strs.isNotEmpty(amount) && Strs.isNotEmpty(pwd)) {
                if (amount.substring(amount.length() - 1).equals("."))
                    amount = amount.replace(".", "");
                commitTransfer(outId, inId, Double.parseDouble(amount), pwd);
            } else if (Strs.isEmpty(pwd)) {
                Toasts.show("请输入资金密码");
            } else if (Strs.isEmpty(amount)) {
                Toasts.show("请输入金额");
            }

        } else if (v == btn_recharge) {

            ActDeposit.launch(ActTransferThird.this);
        }
    }


    @Override
    public void onItemSelected(int position) {
        if (type == OUT) {
            outId = cbIds[position];
            if (outId.equals("sobet_01") && outId.equals(inId)) {
                inId = cbIds[1];
                tv_transfer_in.setText(wallet_names.get(1));
                iv_transfer_in.setImageResource(wallet_images[1]);

            } else if (!outId.equals("sobet_01")) {
                inId = cbIds[0];
                tv_transfer_in.setText(wallet_names.get(0));
                iv_transfer_in.setImageResource(wallet_images[0]);
            }
            if (balance != null) {
                et_transfer_amount.setHint("可转账余额:" + balance[position]);
            }
            tv_transfer_out.setText(wallet_names.get(position));
            iv_transfer_out.setImageResource(wallet_images[position]);
        } else {
            inId = cbIds[position];
            if (inId.equals("sobet_01") && inId.equals(outId)) {
                outId = cbIds[1];
                tv_transfer_out.setText(wallet_names.get(1));
                iv_transfer_out.setImageResource(wallet_images[1]);
            } else if (!inId.equals("sobet_01")) {
                outId = cbIds[0];
                tv_transfer_out.setText(wallet_names.get(0));
                iv_transfer_out.setImageResource(wallet_images[0]);
                if (balance != null) {
                    et_transfer_amount.setHint("可转账余额:" + balance[0]);
                }
            }
            tv_transfer_in.setText(wallet_names.get(position));
            iv_transfer_in.setImageResource(wallet_images[position]);
        }
    }

    private void queryAccountMoney(int platformId, int index) {
        HttpAction.getThirdPocketAmount(this, platformId, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", Double.TYPE);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                Double money = getField(extra, "data", 0.0);
                balance[index] = money;
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return true;
            }

        });
    }

    private void getThirdPocketList() {
        HttpAction.getThirdPocketList(ActTransferThird.this, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {

                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActTransferThird.this, "");
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

                    listPockets.clear();

                    for (ThirdPocketInfo datum : data) {
                        if (datum.platformId == 5 || datum.platformId == 6 ||
                                datum.platformId == 23 || datum.platformId == 24 ||
                                datum.platformId == 10||datum.platformId == 27||datum.platformId == 28) {
                            listPockets.add(datum);
                        }
                    }

                    for (int i = 0; i < listPockets.size(); i++) {
                        queryAccountMoney(listPockets.get(i).platformId, i + 1);
                    }

                    wallet_images = new int[listPockets.size() + 1];
                    cbIds = new String[listPockets.size() + 1];
                    balance = new double[listPockets.size() + 1];

                    wallet_names.add(0, "钱包");
                    wallet_images[0] = R.mipmap.ic_transfer_wallet;
                    cbIds[0] = CtxLottery.MAIN_POCKET_CBID;
                    balance[0] = UserManager.getIns().getLotteryAvailableBalance();

                    for (int i = 0; i < listPockets.size(); i++) {
                        ThirdPocketInfo bean = listPockets.get(i);
                        ThirdGamePlatform platform = ThirdGamePlatform.findByPlatformId(bean.platformId);
                        if (platform != null) {
                            wallet_names.add(i + 1, platform.getTitle());
                            wallet_images[i + 1] = platform.getTransferImag();
                            //balance[i + 1] = listPockets.get(i).balance;
                            cbIds[i + 1] = listPockets.get(i).cbId;
                        }
                    }

                    outId = CtxLottery.MAIN_POCKET_CBID;
                    if (platform == null) {
                        inId = listPockets.get(0).cbId;
                    } else {
                        inId = platform.getCbId();

                        tv_transfer_in.setText(platform.getTitle());
                        iv_transfer_in.setImageResource(platform.getTransferImag());
                    }

                    walletPop = new WalletListPopupWindow(ActTransferThird.this, AbDevice.SCREEN_WIDTH_PX / 3, wallet_names, ActTransferThird.this);
                    if (balance != null) {
                        et_transfer_amount.setHint("可转账余额:" + balance[0]);
                    }

                } else {
                    DialogsTouCai.showDialog(ActTransferThird.this, "温馨提醒", msg, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActTransferThird.this, content, false);
                return true;
            }

            @Override
            public void onAfter(int id) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActTransferThird.this);
                    }
                });
            }
        });
    }

    private void commitTransfer(final String cbIdOut, final String cbIdIn, double amount, String pwd) {
        if (CtxLottery.MAIN_POCKET_CBID.equals(cbIdOut) && amount <= 0) {
            Toasts.show(ActTransferThird.this, "尚无可转账金额", false);
            //检查队列
            return;
        }

        if (Strs.isEmpty(pwd)) {
            Toasts.show(ActTransferThird.this, "请输入资金密码", false);
            return;
        }

        final double finalAmount = amount;
//      final String outAccount = CtxLottery.MAIN_POCKET_CBID.equals(cbIdOut) ? CtxLottery.MAIN_POCKET_NAME :
//      ThirdGamePlatform.findByCbId(cbIdOut).getTitle();

        HttpAction.getPlayerTransferRefreshToken(ActTransferThird.this, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {

                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActTransferThird.this, "");
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
                        HttpAction.commitPlayerTransferTC(ActTransferThird.this, cbIdOut, cbIdIn, finalAmount, pwd, token, new AbHttpResult() {
                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                if (code == 0 && error == 0) {
                                    DialogsTouCai.showTransferThirdSuccess(Act, cbIdIn);

                                    //同步用户数据
                                    UserManager.getIns().initUserData(new UserManager.IUserDataSyncCallback() {
                                        @Override
                                        public void onUserDataInited() {

                                        }

                                        @Override
                                        public void afterUserDataInited() {

                                        }

                                        @Override
                                        public void onUserDataInitFaild() {

                                        }

                                        @Override
                                        public void onAfter() {

                                        }
                                    });
                                } else if ("平台钱包余额不足".equals(msg)) {
                                    DialogsTouCai.showTransferBalanceNotEnough(Act);
                                } else {
                                    DialogsTouCai.showDialog(ActTransferThird.this, "温馨提醒", tv_transfer_out.getText().toString() + "转出结果：" + msg, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });


                                }
                                return true;
                            }

                            @Override
                            public boolean onError(int status, String content) {
                                Log.d("ActTransferThird", "status:" + status);
                                DialogsTouCai.hideProgressDialog(ActTransferThird.this);
                                Toasts.show(ActTransferThird.this, content, false);
                                return true;
                            }

                            @Override
                            public void onAfter(int id) {
                                DialogsTouCai.hideProgressDialog(ActTransferThird.this);
                            }
                        });
                    }
                } else {
                    DialogsTouCai.hideProgressDialog(ActTransferThird.this);
                    Toasts.show(ActTransferThird.this, msg, false);

                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                DialogsTouCai.hideProgressDialog(ActTransferThird.this);
                Toasts.show(ActTransferThird.this, content, false);
                return true;
            }
        });
    }


}
