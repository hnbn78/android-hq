package com.desheng.app.toucai.panel;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.app.toucai.fragment.BasePageFragment;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.LotteryInfoCustom;
import com.desheng.app.toucai.model.PtGameInfoMode;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class FragCQ9hotGame extends BasePageFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private BaseQuickAdapter<LotteryInfoCustom, BaseViewHolder> adapter;
    private static final String GAME_THIRDPLATFORM_ID = "thirdPlatformId";
    private RelativeLayout emptyview;
    private int mPageIndex;
    private int mThirdPlatformId;

    public static FragCQ9hotGame newInstance(int thirdPlatformId) {
        FragCQ9hotGame fragment = new FragCQ9hotGame();
        Bundle args = new Bundle();
        args.putInt(GAME_THIRDPLATFORM_ID, thirdPlatformId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setContentView() {
        return R.layout.layout_frag_cq9allgame;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initView(View rootview) {
        mThirdPlatformId = getArguments().getInt(GAME_THIRDPLATFORM_ID);
        swipeRefreshLayout = ((SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefreshLayout));
        recyclerView = ((RecyclerView) rootview.findViewById(R.id.recyclerView));
        emptyview = ((RelativeLayout) rootview.findViewById(R.id.emptyview));

        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        adapter = new BaseQuickAdapter<LotteryInfoCustom, BaseViewHolder>(R.layout.item_pt_game_tuijian) {
            @Override
            protected void convert(BaseViewHolder helper, LotteryInfoCustom item) {
                helper.setText(R.id.tvName, item.getShowName());
                Views.loadImage(mActivity, ((ImageView) helper.getView(R.id.ivIcon)), ENV.curr.host + item.getAppIcon());
                int layoutPosition = helper.getLayoutPosition();
                if (layoutPosition % 3 == 0) {
                    helper.setBackgroundRes(R.id.bg, R.mipmap.cq9_item_bg_yellow);
                } else if (layoutPosition % 3 == 1) {
                    helper.setBackgroundRes(R.id.bg, R.mipmap.cq9_item_bg_orange);
                } else if (layoutPosition % 3 == 2) {
                    helper.setBackgroundRes(R.id.bg, R.mipmap.cq9_item_bg_blue);
                }
                helper.getView(R.id.ishot).setVisibility(item.getHot() == 1 ? View.VISIBLE : View.GONE);
                if ((item.getHot() == 1)) {
                    Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.hot_game_animate);
                    ((ImageView) helper.getView(R.id.ishot)).startAnimation(animation);
                }
                helper.setText(R.id.tvpersongaming, String.valueOf(1400 - (int) (500 / adapter.getItemCount()) * layoutPosition - (int) (Math.random() * 50)) + "人在玩");
            }
        };
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LotteryInfoCustom lotteryInfoCustom = (LotteryInfoCustom) adapter.getData().get(position);
                if (lotteryInfoCustom != null) {
                    getH5GameUrl(lotteryInfoCustom);
                }
            }
        });
    }

    @Override
    public void fetchData() {
        mPageIndex = 0;
        mGamename = null;
        getData(mThirdPlatformId);
    }
    List<LotteryInfoCustom> mDatas = new ArrayList<>();
    PtGameInfoMode mPtGameInfoMode;
    String mGamename;

    private void getData(int thirdPlatformId) {
        HttpActionTouCai.getPTgameData(this, mPageIndex, 50, mGamename, null,thirdPlatformId, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(mActivity, "");
            }

            @Override
            public boolean onGetString(String str) {
                if (Strs.isNotEmpty(str)) {
                    mPtGameInfoMode = new Gson().fromJson(str, PtGameInfoMode.class);
                    if (mPtGameInfoMode != null) {

                        List<LotteryInfoCustom> hotThirdGameList = mPtGameInfoMode.getHotThirdGameList();
                        if (hotThirdGameList != null && hotThirdGameList.size() > 0) {

                            adapter.loadMoreComplete();
                            emptyview.setVisibility(View.GONE);

                            if (mPageIndex == 0) {
                                mDatas.clear();
                            }

                            mDatas.addAll(hotThirdGameList);

                            if (adapter != null) {
                                adapter.setNewData(mDatas);
                            }

                        } else {
                            if (mPageIndex == 0) {
                                emptyview.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Dialogs.hideProgressDialog(mActivity);
                swipeRefreshLayout.setRefreshing(false);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
                Dialogs.hideProgressDialog(mActivity);
            }
        });
    }

    @Override
    public void onRefresh() {
        fetchData();
    }

    private void getH5GameUrl(LotteryInfoCustom listEntity) {
        HttpActionTouCai.getGetThirdGameUrl(this, listEntity.getThirdPlatformId(), listEntity.getCode(), new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(mActivity, "");
            }

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
                        checkQipaiAccount(mActivity, listEntity, data);
                    }
                } else {
                    Toasts.show(mActivity, msg, false);
                }
                return true;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(mActivity);
            }
        });
    }

    private void checkQipaiAccount(Context mContext, LotteryInfoCustom lotteryListdata, String dataJson) {

        //查询用户余额
        HttpActionTouCai.getThirdPocketAmount(mContext,mThirdPlatformId, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", Double.TYPE);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    Double balance = getField(extra, "data", 0.0);
                    if (balance >= 50) {//棋牌资金大于等于50元，直接进入游戏
                        ActThirdGameMain.launch(mActivity, mThirdPlatformId,
                                CommonConsts.setThirdGameScreenOrintation(BaseConfig.custom_flag).getScreenOrintation(), dataJson);
                        UserManagerTouCai.getIns().setUserChangwanGameData(lotteryListdata);
                    } else {
                        //先检查棋牌账户资金是否充足
                        double mainAc = UserManagerTouCai.getIns().getLotteryAvailableBalance();
                        String cbId = ThirdGamePlatform.findByPlatformId(mThirdPlatformId).getCbId();
                        String gametitle = ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getTitle();
                        DialogsTouCai.showDSchessAcountCheckDialog(mActivity, balance, mainAc, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {//跳转到充值页
                                ActDeposit.launch(mActivity);
                            }
                        }, new DialogsTouCai.IChargeAndGameListener() {//转账后进入游戏
                            @Override
                            public boolean onChargeAndGameListener(View v, int chargeMoney,String pwd, Dialog dialog) {
                                Log.e("SimplePageFragment", chargeMoney + "");
                                return recharge(v, chargeMoney, cbId, lotteryListdata, dataJson,pwd, dialog);
                            }
                        }, new View.OnClickListener() {//直接进入游戏
                            @Override
                            public void onClick(View v) {
                                //ActWebX5.launch(mContext, "DS棋牌", dataJson, true);
                                ActThirdGameMain.launch(mActivity, mThirdPlatformId,
                                        CommonConsts.setThirdGameScreenOrintation(BaseConfig.custom_flag).getScreenOrintation(), dataJson);
                                UserManagerTouCai.getIns().setUserChangwanGameData(lotteryListdata);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogsTouCai.hideProgressDialog(mActivity);
                            }
                        }, gametitle);
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });

    }

    private boolean recharge(View contentView, int chargeMoneyamount, String cbAccount,
                             LotteryInfoCustom lotteryListdata, String dataJson,String pwd, Dialog dialog) {

        if (chargeMoneyamount <= 0) {
            Toasts.show(mActivity, "单次转入金额最低1元, 请重试", false);
            return false;
        }

        double outBalance = UserManager.getIns().getLotteryAvailableBalance();
        if (chargeMoneyamount > outBalance) {
            Toasts.show(mActivity, "转账金额超过转出账户余额, 请重试", false);
            return false;
        }

        if (chargeMoneyamount > 100000) {
            Toasts.show(mActivity, "单次转入金额最高100000元, 请重试", false);
            return false;
        }

        final String cbIdOut = CtxLottery.MAIN_POCKET_CBID;
        String cbIdIn = cbAccount;
        HttpAction.getPlayerTransferRefreshToken(mActivity, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                contentView.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(mActivity, "");
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

                        HttpAction.commitPlayerTransferTC(mActivity, cbIdOut, cbIdIn, chargeMoneyamount, pwd,token, new AbHttpResult() {
                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                if (code == 0 && error == 0) {
                                    ActThirdGameMain.launch(mActivity, mThirdPlatformId,
                                            CommonConsts.setThirdGameScreenOrintation(BaseConfig.custom_flag).getScreenOrintation(), dataJson);
                                    UserManagerTouCai.getIns().setUserChangwanGameData(lotteryListdata);
                                    Toasts.show(mActivity, "转账成功, 请稍候查询余额!", true);
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
                                    Toasts.show(mActivity, msg, false);
                                }
                                return true;
                            }

                            @Override
                            public boolean onError(int status, String content) {
                                Dialogs.hideProgressDialog(mActivity);
                                Toasts.show(mActivity, content, false);
                                return true;
                            }

                            @Override
                            public void onAfter(int id) {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                Dialogs.hideProgressDialog(mActivity);
                            }
                        });

                    }
                } else {

                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Dialogs.hideProgressDialog(mActivity);
                Toasts.show(mActivity, content, false);
                return true;
            }
        });

        return false;
    }
}
