package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
import com.ab.thread.ValueRunnable;
import com.ab.util.MD5;
import com.ab.util.Maps;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.ThirdPocketInfo;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import okhttp3.Request;

/**
 * 游戏第三方平台间转账
 * Created by lee on 2018/4/11.
 */
@Deprecated
public class ActThirdTransferFast extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {


    private ThirdPocketInfo main;
    private String password;
    // private MaterialDialog dialogRetrive;
    private SwipeRefreshLayout srlRefresh;
    private ImageView ivEye;

    public static void launch(Activity act) {
        Intent itt = new Intent(act, ActThirdTransferFastNew.class);
        act.startActivity(itt);
    }

    private TextView tvMainBalance;
    private Button btnRetrive;
    private Queue<Runnable> qAllRetrive;

    private ViewGroup[] arrVgGroup = new ViewGroup[3];
    private ImageView[] arrIvIcons = new ImageView[3];
    private TextView[] arrTvTitles = new TextView[3];
    private TextView[] arrTvBalances = new TextView[3];
    private TextView[] arrTvTransfers = new TextView[3];
    private ImageView[] arrIvRefreshs = new ImageView[3];

    private ArrayList<ThirdPocketInfo> listPockets;

    @Override
    protected int getBaseLayoutId() {
        return R.layout.ab_activity_base_overlay_toolbar_front;
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_third_transfer_fast;
    }

    @Override
    public void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "第三方转账");
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

        btnRetrive = findViewById(R.id.btnRetrive);

        int[] ids = new int[]{R.id.vgAG, R.id.vgIM, R.id.vgKY};
        for (int i = 0; i < arrTvBalances.length; i++) {
            arrVgGroup[i] = root.findViewById(ids[i]);
        }

        ids = new int[]{R.id.ivAG, R.id.ivIM, R.id.ivKY};
        for (int i = 0; i < arrIvIcons.length; i++) {
            arrIvIcons[i] = root.findViewById(ids[i]);
        }

        ids = new int[]{R.id.tvAG, R.id.tvIM, R.id.tvKY};
        for (int i = 0; i < arrTvTitles.length; i++) {
            arrTvTitles[i] = root.findViewById(ids[i]);
        }

        ids = new int[]{R.id.tvBalanceAG, R.id.tvBalanceIM, R.id.tvBalanceKY};
        for (int i = 0; i < arrTvBalances.length; i++) {
            arrTvBalances[i] = root.findViewById(ids[i]);
        }

        ids = new int[]{R.id.tvTransferAG, R.id.tvTransferIM, R.id.tvTransferKY};
        for (int i = 0; i < arrTvTransfers.length; i++) {
            arrTvTransfers[i] = root.findViewById(ids[i]);
        }

        ids = new int[]{R.id.ivRefreshAG, R.id.ivRefreshIM, R.id.ivRefreshKY};
        for (int i = 0; i < arrIvRefreshs.length; i++) {
            arrIvRefreshs[i] = root.findViewById(ids[i]);
        }

        listPockets = new ArrayList<>();

        initPocketInfo();

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
    public void onRefresh() {
        initPocketInfo();
    }

    private void initPocketInfo() {
        HttpAction.getThirdPocketList(ActThirdTransferFast.this, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        getLayout().post(new Runnable() {
                            @Override
                            public void run() {
                                srlRefresh.setRefreshing(true);
                            }
                        });
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
                            if (datum.platformId == 5 || datum.platformId == 6 || datum.platformId == 7 || datum.platformId == 22) {
                                listPockets.add(datum);
                            }
                        }
                        btnRetrive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogsTouCai.showDialog(ActThirdTransferFast.this, "温馨提醒", "是否确定从所有平台收回余额(整数)? 转账后数额显示有延迟, 请在转账5分钟后刷新余额", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        checkPoket();
                                    }
                                });
                            }
                        });
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
                            arrTvTitles[i].setText(platform.getTitle());
                            queryAccountMoney(arrTvBalances[i], platform.getPlatformId());
                            arrTvTransfers[i].setTag(i);
                            arrTvTransfers[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (main.balance - 0.0 < 1e-6) {
                                        Toasts.show(ActThirdTransferFast.this, "主账户无整数余额", false);
                                        return;
                                    }
                                    final int index = (Integer) v.getTag();
                                    DialogsTouCai.showDialog(ActThirdTransferFast.this, "温馨提醒", "是否确定将所有余额(整数)转至第三方平台? 转账后数额显示有延迟, 请在转账5分钟后刷新余额", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            commitTransfer(main.cbId, listPockets.get(index).cbId, (int) main.balance, password);
                                        }
                                    });
                                }
                            });
                            arrIvRefreshs[i].setTag(i);
                            arrIvRefreshs[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int index = (Integer) v.getTag();
                                    queryAccountMoney(arrTvBalances[index], listPockets.get(index).platformId);
                                }
                            });
                        } else {
                            arrVgGroup[i].setVisibility(View.GONE);
                        }
                    }

                } else {
                    Toasts.show(ActThirdTransferFast.this, msg, false);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActThirdTransferFast.this, content, false);
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


    private void checkPoket() {
        for (int i = 0; i < listPockets.size(); i++) {
            int money = (int) Math.round(Nums.parse(Views.getText(arrTvBalances[i]), 0.0));
            if (money == 0) {
                continue;
            }
            Maps.gen();
            Maps.put("cbId", listPockets.get(i).cbId);
            Maps.put("money", money);
            qAllRetrive.offer(new ValueRunnable(Maps.get()) {
                @Override
                public void run() {
                    int money = (int) ((HashMap<String, Object>) getValue()).get("money");
                    String cbId = (String) (((HashMap<String, Object>) getValue()).get("cbId"));
                    commitTransfer(cbId, main.cbId, money, password);
                }
            });
        }
        if (qAllRetrive.size() > 0) {
            qAllRetrive.poll().run();
        } else {
            Toasts.show(ActThirdTransferFast.this, "尚无可一键回收金额.", false);
        }
    }

    private void commitTransfer(final String cbIdOut, final String cbIdIn, int amount, final String password) {
        if (CtxLottery.MAIN_POCKET_CBID.equals(cbIdOut) && amount <= 0) {
            Toasts.show(ActThirdTransferFast.this, "尚无可转账金额", false);
            //检查队列
            if (qAllRetrive.peek() != null) {
                btnRetrive.postDelayed(qAllRetrive.poll(), 2000);
            }
            return;
        }

        if (BaseConfig.FLAG_TOUCAI_DEMO.equals(Config.custom_flag)) {
            if (amount > 50) {
                amount = 50;
            }
        }

        final int finalAmount = amount;
        final String outAccount = CtxLottery.MAIN_POCKET_CBID.equals(cbIdOut) ? CtxLottery.MAIN_POCKET_NAME :
                ThirdGamePlatform.findByCbId(cbIdOut).getTitle();

        HttpAction.getPlayerTransferRefreshToken(ActThirdTransferFast.this, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActThirdTransferFast.this, "");
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
                        HttpAction.commitPlayerTransfer(ActThirdTransferFast.this, cbIdOut, cbIdIn, finalAmount, MD5.md5(password).toLowerCase(), token, new AbHttpResult() {
                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                if (code == 0 && error == 0) {
                                    Toasts.show(ActThirdTransferFast.this, outAccount + "转出结果:成功!", true);
                                    //同步用户数据
                                    UserManager.getIns().initUserData(new UserManager.IUserDataSyncCallback() {
                                        @Override
                                        public void onUserDataInited() {

                                        }

                                        @Override
                                        public void afterUserDataInited() {
                                            //同步当前余额数据
                                            initPocketInfo();

                                            //检查队列
                                            if (qAllRetrive.peek() != null) {
                                                btnRetrive.postDelayed(qAllRetrive.poll(), 1000);
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
                                    Toasts.show(ActThirdTransferFast.this, outAccount + "转出结果:" + msg, false);
                                    //检查队列
                                    if (qAllRetrive.peek() != null) {
                                        btnRetrive.postDelayed(qAllRetrive.poll(), 2000);
                                    }
                                }
                                return true;
                            }

                            @Override
                            public boolean onError(int status, String content) {
                                DialogsTouCai.hideProgressDialog(ActThirdTransferFast.this);
                                Toasts.show(ActThirdTransferFast.this, content, false);
                                //检查队列
                                if (qAllRetrive.peek() != null) {
                                    btnRetrive.postDelayed(qAllRetrive.poll(), 2000);
                                }
                                return true;
                            }

                            @Override
                            public void onAfter(int id) {
                                DialogsTouCai.hideProgressDialog(ActThirdTransferFast.this);
                            }
                        });
                    }
                } else {
                    DialogsTouCai.hideProgressDialog(ActThirdTransferFast.this);
                    Toasts.show(ActThirdTransferFast.this, msg, false);
                    //检查队列
                    if (qAllRetrive.peek() != null) {
                        btnRetrive.postDelayed(qAllRetrive.poll(), 2000);
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                DialogsTouCai.hideProgressDialog(ActThirdTransferFast.this);
                Toasts.show(ActThirdTransferFast.this, content, false);
                //检查队列
                if (qAllRetrive.peek() != null) {
                    qAllRetrive.poll().run();
                }
                return true;
            }
        });
    }

    private void queryAccountMoney(final TextView tvAccountAmount, int platformId) {
        //MM.http.cancellAllByTag(ActThirdTransfer.this);

        HttpAction.getThirdPocketAmount(ActThirdTransferFast.this, platformId, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActThirdTransferFast.this, "");
                    }
                });
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", Double.TYPE);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                Double money = getField(extra, "data", 0.0);
                tvAccountAmount.setText(Nums.formatDecimal(money, 3));
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                tvAccountAmount.setText("");
                return true;
            }

            @Override
            public void onAfter(int id) {
                getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.hideProgressDialog(ActThirdTransferFast.this);
                    }
                }, 500);
            }
        });

    }


    @Override
    public void onDestroy() {
        MM.http.cancellAllByTag(ActThirdTransferFast.this);
        super.onDestroy();
    }

}
