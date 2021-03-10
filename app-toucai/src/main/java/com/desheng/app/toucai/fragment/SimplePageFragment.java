package com.desheng.app.toucai.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.bumptech.glide.Glide;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.HomeTimerMode;
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

public class SimplePageFragment extends BasePageFragment {

    public static final String KEY_GAME_TAB_DATA = "key_game_tab_data";
    private static final int KEY_GAME_TYPE = 9;
    List<LotteryInfoCustom> mList = new ArrayList<>(0);
    private RecyclerView mRvHall;
    private NormalAdapter adapter;

    public static SimplePageFragment newInstance(ArrayList<LotteryInfoCustom> data) {
        SimplePageFragment fragment = new SimplePageFragment();
        Bundle args = new Bundle();
        ArrayList<LotteryInfoCustom> gameList = new ArrayList<>(0);
        for (LotteryInfoCustom datum : data) {
            if (datum.getThirdPlatformId() != ThirdGamePlatform.PT.getPlatformId()
                    && datum.getThirdPlatformId() != ThirdGamePlatform.CQ.getPlatformId()) {
                gameList.add(datum);
            }
        }
        args.putParcelableArrayList(KEY_GAME_TAB_DATA, gameList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void fetchData() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        if (null == mList) {
            return;
        }
        for (LotteryInfoCustom infoCustom : mList) {
            if (infoCustom.getShowType() != KEY_GAME_TYPE) {
                stringBuilder.append(infoCustom.getId()).append(",");
                Log.d("SimplePageFragment", "infoCustom.getId():" + infoCustom.getId());
            }
        }
        boolean b = stringBuilder.toString().endsWith(",");
        String realStr;
        if (b) {
            realStr = stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        } else {
            realStr = stringBuilder.toString();
        }

//        HttpActionTouCai.getCaizhongAwardTimers(this, realStr, new AbHttpResult() {
//
//            @Override
//            public void setupEntity(AbHttpRespEntity entity) {
//                entity.putField("data", new TypeToken<ArrayList<HomeTimerMode>>() {
//                }.getType());
//            }
//
//            @Override
//            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
//                if (code == 0 && error == 0) {
//                    mHomeTimerModeList = getField(extra, "data", null);
//                    adapter.notifyDataSetChanged();
//                }
//                return super.onSuccessGetObject(code, error, msg, extra);
//            }
//        });
    }

    List<HomeTimerMode> mHomeTimerModeList;

    @Override
    protected int setContentView() {
        return R.layout.simple_layout;
    }

    @Override
    protected void initView(View rootview) {
        mList = getArguments().getParcelableArrayList(KEY_GAME_TAB_DATA);
        mRvHall = ((RecyclerView) rootview.findViewById(R.id.rvHall));
        mRvHall.setLayoutManager(new GridLayoutManager(mActivity, 4));
        adapter = new NormalAdapter(mActivity, mList);
        mRvHall.setAdapter(adapter);
    }

    public void hideRv() {
        if (mRvHall != null) {
            mRvHall.setVisibility(View.GONE);
        }
    }

    public void showRv() {
        if (mRvHall != null) {
            mRvHall.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    class NormalAdapter extends RecyclerView.Adapter<VH> {

        private List<LotteryInfoCustom> mDatas;
        Context mContext;

        public void setData(List<LotteryInfoCustom> data) {
            mDatas = data;
        }

        public NormalAdapter(Context context, List<LotteryInfoCustom> data) {
            this.mDatas = data;
            this.mContext = context;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            if (Strs.isNotEmpty(mDatas.get(position).getAppIcon())) {
                Log.d("NormalAdapter", mDatas.get(position).getShowName() + "----" + ENV.curr.host + "----" + mDatas.get(position).getAppIcon());
                Glide.with(mContext).load(ENV.curr.host + mDatas.get(position).getAppIcon()).asBitmap().into(holder.logo);
            } else {
                if (mDatas.get(position).getShowType() != KEY_GAME_TYPE) {
                    ILotteryKind kind = LotteryKind.find(mDatas.get(position).getId());
                    String path = "file:///android_asset/lottery_kind/active/" + kind.getIcon();
                    Glide.with(mContext).load(path).into(holder.logo);
                }
            }
            holder.name.setText(mDatas.get(position).getShowName());

            if (mDatas.get(position).getShowType() == KEY_GAME_TYPE) {
                holder.timer.setVisibility(View.GONE);
                holder.caijianging.setVisibility(View.GONE);
            }

            if (mHomeTimerModeList != null) {
                int id = -1;
                if (mDatas.get(position).getShowType() != KEY_GAME_TYPE) {
                    id = mDatas.get(position).getId();

                    for (HomeTimerMode timerMode : mHomeTimerModeList) {

                        if (timerMode.getLotteryId().equals(String.valueOf(id))) {
                            holder.mrl.setBackgroundResource(R.drawable.shape_yellow_r15_solid);
                            holder.timer.setVisibility(View.VISIBLE);
                            holder.caijianging.setVisibility(View.GONE);
                            new CountDownTimer(timerMode.getTimer() * 1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    holder.timer.setText(Html.fromHtml("<font color='#FF0000'>" + secToTime(millisUntilFinished / 1000) + "</font>" + "后开奖"));
                                }

                                @Override
                                public void onFinish() {
                                    holder.timer.setVisibility(View.GONE);
                                    holder.caijianging.setVisibility(View.VISIBLE);
                                    getSingleCaizhongTimer(holder, timerMode);
                                }
                            }.start();
                        }
                    }

                } else {
                    holder.mrl.setBackgroundResource(0);
                    holder.timer.setVisibility(View.GONE);
                    holder.caijianging.setVisibility(View.GONE);
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ILotteryKind kind = LotteryKind.find(mDatas.get(position).getId());
                    if (UserManager.getIns().isLogined()) {
                        DialogsTouCai.showProgressDialog((Activity) mContext, "");

                        if (mDatas.get(position).getShowType() == KEY_GAME_TYPE) {//进入游戏
                            getH5GameUrl(mContext, mDatas.get(position));
                        } else {
                            CtxLotteryTouCai.launchLotteryPlay(mContext, mDatas.get(position));//进入彩票
                        }
                    } else {
                        UserManager.getIns().redirectToLogin((Activity) mContext);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_game_hall, parent, false);
            return new VH(v);
        }
    }

    private void getSingleCaizhongTimer(VH holder, HomeTimerMode timerMode) {
        holder.caijianging.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.mrl.setBackgroundResource(R.drawable.shape_yellow_r15_solid);
                holder.timer.setVisibility(View.VISIBLE);
                holder.caijianging.setVisibility(View.GONE);
                new CountDownTimer((timerMode.getCycle() - 10) * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        holder.timer.setText(Html.fromHtml("<font color='#FF0000'>" + secToTime(millisUntilFinished / 1000) + "</font>" + "后开奖"));
                    }

                    @Override
                    public void onFinish() {
                        holder.timer.setVisibility(View.GONE);
                        holder.caijianging.setVisibility(View.VISIBLE);
                        getSingleCaizhongTimer(holder, timerMode);
                    }
                }.start();
            }
        }, 10 * 1000);
    }

    private void checkQipaiAccount(Context mContext, LotteryInfoCustom lotteryListdata, String dataJson) {

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
                        ActThirdGameMain.launch(mActivity, lotteryListdata.getThirdPlatformId(),
                                ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getScreenOrintation(), dataJson);
                        UserManagerTouCai.getIns().setUserChangwanGameData(lotteryListdata);
                    } else {
                        //先检查棋牌账户资金是否充足
                        double mainAc = UserManagerTouCai.getIns().getLotteryAvailableBalance();
                        String cbId = ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getCbId();
                        String gametitle = ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getTitle();
                        DialogsTouCai.showDSchessAcountCheckDialog(mActivity, balance, mainAc, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {//跳转到充值页
                                ActDeposit.launch(mContext);
                            }
                        }, new DialogsTouCai.IChargeAndGameListener() {//转账后进入游戏
                            @Override
                            public boolean onChargeAndGameListener(View v, int chargeMoney, String pwd,Dialog dialog) {
                                Log.e("SimplePageFragment", chargeMoney + "");
                                return recharge(v, chargeMoney, cbId, lotteryListdata, dataJson,pwd, dialog);
                            }
                        }, new View.OnClickListener() {//直接进入游戏
                            @Override
                            public void onClick(View v) {
                                //ActWebX5.launch(mContext, "DS棋牌", dataJson, true);
                                ActThirdGameMain.launch(mActivity, lotteryListdata.getThirdPlatformId(),
                                        ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getScreenOrintation(), dataJson);
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
                             LotteryInfoCustom lotteryListdata, String dataJson, String pwd,Dialog dialog) {

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

                        HttpAction.commitPlayerTransferTC(getActivity(), cbIdOut, cbIdIn, chargeMoneyamount, pwd,token, new AbHttpResult() {
                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                if (code == 0 && error == 0) {

                                    ActThirdGameMain.launch(mActivity, lotteryListdata.getThirdPlatformId(),
                                            ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getScreenOrintation(), dataJson);
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
                        checkQipaiAccount(context, lotteryInfoCustom, data);
                    }
                } else {
                    Toasts.show(mActivity, msg, false);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogsTouCai.hideProgressDialog(mActivity);
            }
        });
    }


    class VH extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView name;
        TextView timer;
        TextView caijianging;
        RelativeLayout mrl;

        public VH(View v) {
            super(v);
            logo = (ImageView) v.findViewById(R.id.logo);
            name = (TextView) v.findViewById(R.id.name);
            timer = (TextView) v.findViewById(R.id.timer);
            caijianging = (TextView) v.findViewById(R.id.caijianging);
            mrl = (RelativeLayout) v.findViewById(R.id.rl);
        }
    }

    /**
     * 返回时分秒
     *
     * @param time
     * @return
     */
    public static String secToTime(long time) {
        String timeStr = null;
        long hour = 0;
        long minute = 0;
        long second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(long i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }


}
