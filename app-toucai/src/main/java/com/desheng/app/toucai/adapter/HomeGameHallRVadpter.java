package com.desheng.app.toucai.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.LotteryInfoCustom;
import com.desheng.app.toucai.panel.ActDeposit;
import com.desheng.app.toucai.panel.ActThirdGameMain;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.manager.UserManager;
import com.google.gson.reflect.TypeToken;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class HomeGameHallRVadpter extends BaseQuickAdapter<ArrayList<LotteryInfoCustom>, BaseViewHolder> {
    private static final int KEY_GAME_TYPE = 9;
    private Context mContext;

    public HomeGameHallRVadpter(Context context, int layoutResId) {
        super(layoutResId);
        this.mContext = context;
    }

    public HomeGameHallRVadpter(int layoutResId, @Nullable List<ArrayList<LotteryInfoCustom>> data) {
        super(layoutResId, data);
    }

    public HomeGameHallRVadpter(@Nullable List<ArrayList<LotteryInfoCustom>> data) {
        super(data);
    }

    public HomeGameHallRVadpter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ArrayList<LotteryInfoCustom> item) {
        RecyclerView innerRv = (RecyclerView) helper.getView(R.id.gameHallOutRc);
        innerRv.setLayoutManager(new GridLayoutManager(mContext, 4));
        BaseQuickAdapter<LotteryInfoCustom, BaseViewHolder> innerAdpter = new BaseQuickAdapter<LotteryInfoCustom, BaseViewHolder>(R.layout.item_layout_game_hall) {
            @Override
            protected void convert(BaseViewHolder helper, LotteryInfoCustom item) {
                if (Strs.isNotEmpty(item.getAppIcon())) {
                    Log.d("NormalAdapter", item.getShowName() + "----" + ENV.curr.host + "----" + item.getAppIcon());
                    Glide.with(mContext).load(ENV.curr.host + item.getAppIcon()).asBitmap().into(((ImageView) helper.getView(R.id.logo)));
                } else {
                    if (item.getShowType() != KEY_GAME_TYPE) {
                        ILotteryKind kind = LotteryKind.find(item.getId());
                        String path = "file:///android_asset/lottery_kind/active/" + kind.getIcon();
                        Glide.with(mContext).load(path).into(((ImageView) helper.getView(R.id.logo)));
                    }
                }
                helper.setText(R.id.name, item.getShowName());
            }
        };
        innerRv.setAdapter(innerAdpter);
        innerAdpter.setNewData(item);

        innerAdpter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LotteryInfoCustom lotteryInfoCustom = (LotteryInfoCustom) adapter.getData().get(position);
                ILotteryKind kind = LotteryKind.find(lotteryInfoCustom.getId());
                if (UserManager.getIns().isLogined()) {
                    DialogsTouCai.showProgressDialog((Activity) mContext, "");

                    if (lotteryInfoCustom.getShowType() == KEY_GAME_TYPE) {//进入游戏
                        getH5GameUrl(mContext, lotteryInfoCustom);
                    } else {
                        CtxLotteryTouCai.launchLotteryPlay(mContext, lotteryInfoCustom);//进入彩票
                    }
                } else {
                    UserManager.getIns().redirectToLogin((Activity) mContext);
                }
            }
        });
    }

    private void getH5GameUrl(Context context, LotteryInfoCustom lotteryInfoCustom) {
        HttpActionTouCai.getGetThirdGameUrl(this, lotteryInfoCustom.getThirdPlatformId(), lotteryInfoCustom.getCode(), new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<String>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    String data = (String) extra.get("data");
                    if (data != null) {
                        Log.d("SimplePageFragment", data);
                        checkQipaiAccount(((Activity) context), lotteryInfoCustom, data);
                    }
                } else {
                    Toasts.show(context, msg, false);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogsTouCai.hideProgressDialog(((Activity) context));
            }
        });
    }

    private void checkQipaiAccount(Activity mContext, LotteryInfoCustom lotteryListdata, String dataJson) {

        //查询用户余额
        HttpActionTouCai.getThirdPocketAmount(mContext, lotteryListdata.getThirdPlatformId(), new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", Double.TYPE);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    Double balance = getField(extra, "data", 0.0);

                    if (balance >= 50) {//棋牌资金大于等于50元，直接进入游戏
                        ActThirdGameMain.launch(mContext, lotteryListdata.getThirdPlatformId(),
                                ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getScreenOrintation(), dataJson);
                        UserManagerTouCai.getIns().setUserChangwanGameData(lotteryListdata);
                    } else {
                        //先检查棋牌账户资金是否充足
                        double mainAc = UserManagerTouCai.getIns().getLotteryAvailableBalance();
                        String cbId = ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getCbId();
                        String gametitle = ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getTitle();
                        DialogsTouCai.showDSchessAcountCheckDialog(mContext, balance, mainAc, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {//跳转到充值页
                                ActDeposit.launch(mContext);
                            }
                        }, new DialogsTouCai.IChargeAndGameListener() {//转账后进入游戏
                            @Override
                            public boolean onChargeAndGameListener(View v, int chargeMoney, String pwd, Dialog dialog) {
                                Log.e("SimplePageFragment", chargeMoney + "");
                                return recharge(mContext, v, chargeMoney, cbId, lotteryListdata, dataJson, pwd, dialog);
                            }
                        }, new View.OnClickListener() {//直接进入游戏
                            @Override
                            public void onClick(View v) {
                                //ActWebX5.launch(mContext, "DS棋牌", dataJson, true);
                                ActThirdGameMain.launch(mContext, lotteryListdata.getThirdPlatformId(),
                                        ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getScreenOrintation(), dataJson);
                                UserManagerTouCai.getIns().setUserChangwanGameData(lotteryListdata);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogsTouCai.hideProgressDialog(mContext);
                            }
                        }, gametitle);
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });

    }

    private boolean recharge(Activity mContext, View contentView, int chargeMoneyamount, String cbAccount,
                             LotteryInfoCustom lotteryListdata, String dataJson, String pwd, Dialog dialog) {

        if (chargeMoneyamount <= 0) {
            Toasts.show(mContext, "单次转入金额最低1元, 请重试", false);
            return false;
        }

        double outBalance = UserManager.getIns().getLotteryAvailableBalance();
        if (chargeMoneyamount > outBalance) {
            Toasts.show(mContext, "转账金额超过转出账户余额, 请重试", false);
            return false;
        }

        if (chargeMoneyamount > 100000) {
            Toasts.show(mContext, "单次转入金额最高100000元, 请重试", false);
            return false;
        }

        final String cbIdOut = CtxLottery.MAIN_POCKET_CBID;
        String cbIdIn = cbAccount;
        HttpAction.getPlayerTransferRefreshToken(mContext, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                contentView.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(mContext, "");
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

                        HttpAction.commitPlayerTransferTC(mContext, cbIdOut, cbIdIn, chargeMoneyamount, pwd, token, new AbHttpResult() {
                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                if (code == 0 && error == 0) {

                                    ActThirdGameMain.launch(mContext, lotteryListdata.getThirdPlatformId(),
                                            ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getScreenOrintation(), dataJson);
                                    UserManagerTouCai.getIns().setUserChangwanGameData(lotteryListdata);

                                    Toasts.show(mContext, "转账成功, 请稍候查询余额!", true);
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

                                    return true;

                                } else {
                                    Toasts.show(mContext, msg, false);
                                }
                                return true;
                            }

                            @Override
                            public boolean onError(int status, String content) {
                                Dialogs.hideProgressDialog(mContext);
                                Toasts.show(mContext, content, false);
                                return true;
                            }

                            @Override
                            public void onAfter(int id) {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                Dialogs.hideProgressDialog(mContext);
                            }
                        });

                    }
                } else {

                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Dialogs.hideProgressDialog(mContext);
                Toasts.show(mContext, content, false);
                return true;
            }
        });

        return false;
    }
}
