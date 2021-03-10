package com.desheng.app.toucai.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.ArraysAndLists;
import com.ab.util.Dialogs;
import com.ab.util.Maps;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.panel.BaseLotteryPlayFragment;
import com.desheng.app.toucai.panel.LotteryPlayPanel;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.PlayHeadView;
import com.desheng.app.toucai.view.PlayOrderListView;
import com.desheng.base.controller.ControllerBase;
import com.desheng.base.controller.IControlled;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.event.ClearLotteryPlaySelectedEvent;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.desheng.base.panel.ActLotteryCart;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.Disposable;
import okhttp3.Request;

/**
 * 玩法controller基类
 * Created by lee on 2018/3/13.
 */
public abstract class BaseCtrlPlay extends ControllerBase implements PlayHeadView.OnShakeForAutoGenerate, PlayOrderListView.OnOneKeyBuyListener, PlayHeadView.OnInstructionClickListener {
    private LotteryPlayUIConfig uiConfig;
    private LotteryInfo lotteryInfo;
    public LotteryPlay lotteryPlay;

    BaseLotteryPlayFragment frag;
    UserManagerTouCai.EventReceiver receiver;

    Disposable mDisposable;

    protected boolean isStoped = false;

    public BaseCtrlPlay(Context ctx, BaseLotteryPlayFragment frag, LotteryInfo info, LotteryPlayUIConfig uiConfig, LotteryPlay play) {
        super(ctx);
        this.frag = frag;
        this.lotteryInfo = info;
        this.uiConfig = uiConfig;
        this.lotteryPlay = play;
    }


    @Override
    public void start(IControlled controlled) {
        if (isStoped)
            return;

        receiver = new UserManagerTouCai.EventReceiver().register(getContext(), new UserManagerTouCai.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (eventName.equals(UserManagerTouCai.EVENT_USER_INFO_UNPDATED)) {
                    PlayHeadView head = frag.getHead();
                    if (head==null) {
                        return;
                    }
                    head.setMoney(UserManagerTouCai.getIns().getLotteryAvailableBalance());
                }
            }
        });
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentGroup();
        setBottomGroup();
    }

    @Override
    public void stop() {
        isStoped = true;
        stopping();
        EventBus.getDefault().unregister(this);
        receiver.unregister(getContext());
    }


    private void setBottomGroup() {
        getLotteryPanel().getBottomBuyBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyOneKey();
            }
        });
    }


    @Override
    public void onAutoGenerate() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
        autoGenerate();
    }


    protected abstract void autoGenerate();


    public LotteryPlayUIConfig getUiConfig() {
        return uiConfig;
    }


    public LotteryInfo getLotteryInfo() {
        return lotteryInfo;
    }

    public LotteryPlay getLotteryPlay() {
        return lotteryPlay;
    }

    public LotteryPlayPanel getLotteryPanel() {
        return frag.getLotteryPlayPanel();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClearLotteryPlaySelected(ClearLotteryPlaySelectedEvent event) {
        clearAllSelected();
    }

    //设置号码组
    public abstract void setContentGroup();


    public void setHitCount(long count) {
        getLotteryPanel().getSnakeBar().setHitCount(count);
    }

    protected void buyOneKey() {
        if (!isAllGroupReady() || Strs.isEmpty(getRequestText())) {
            Toasts.show(getContext(), "您还没有选择号码或所选号码不全", false);
            return;
        }
        if (getLotteryPanel().getNextIssue() == null) {
            Toasts.show(getContext(), "尚无新一期信息, 往期已封单, 请稍候重试", false);
            return;
        }
        if (Math.abs(getLotteryPanel().getSnakeBar().getMoneyTotal()) < 1e-6) {
            Toasts.show(getContext(), "尚无投注倍数!", false);
            return;
        }

        if (!canSubmitOrder()) {
            Toasts.show("正在计算，请稍等!", false);
            return;
        }

        onBuyOneKey(getLotteryInfo().getCode(),
                getLotteryPanel().getNextIssue().getIssue(),
                getLotteryPlay().getPlayId(),
                getRequestText());
    }

    @Override
    public void onBuyOneKey(String lottery, String issue, String method, String requestText) {

        HttpActionTouCai.maxBonus(this, lotteryInfo.getId(), new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", String.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                String data = getField(extra, "data", "0");
                List<Runnable> runnables = new ArrayList<>();

                if (getLotteryPanel() != null && getLotteryPanel().getNextIssue() != null) {
                    _buy(getLotteryInfo().getCode(),
                            getLotteryPanel().getNextIssue().getIssue(),
                            getLotteryPlay().getPlayId(),
                            getRequestText());
                }

//                Dialog dialog = DialogsTouCai.showLotteryListBuyConfirmDialog(
//                        getContext(),
//                        issue,
//                        String.valueOf(getLotteryPanel().getSnakeBar().getHitCount()),
//                        Nums.formatDecimal(getLotteryPanel().getSnakeBar().getMoneyTotal(), 2),
//                        "1",
//                        data,
//                        new BaseAdapter() {
//                            @Override
//                            public int getCount() {
//                                return 1;
//                            }
//
//                            @Override
//                            public Object getItem(int position) {
//                                return requestText;
//                            }
//
//                            @Override
//                            public long getItemId(int position) {
//                                return 0;
//                            }
//
//                            @Override
//                            public View getView(int position, View convertView, ViewGroup parent) {
//                                if (convertView == null)
//                                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_lottery_list_buy_confirm_item, parent, false);
//
//                                TextView method = convertView.findViewById(R.id.tv_lottery_method);
//                                method.setText(getLotteryPlay().showName);
//                                TextView count = convertView.findViewById(R.id.tv_lottery_count);
//                                count.setText(String.format("@%d倍*%s注", getLotteryPanel().getFootView().getNumTimes(), getLotteryPanel().getSnakeBar().getHitCount()));
//
//                                TextView content = convertView.findViewById(R.id.tv_lottery_content);
//                                content.setText(requestText);
//                                convertView.findViewById(R.id.ib_del).setVisibility(View.GONE);
//                                convertView.findViewById(R.id.ib_del).setOnClickListener(v -> {
//                                    reset();
//                                    if (runnables.size() > 0)
//                                        runnables.get(0).run();
//                                });
//                                return convertView;
//                            }
//                        },
//                        () -> {
//                            if (getLotteryPanel() != null && getLotteryPanel().getNextIssue() != null) {
//                                _buy(getLotteryInfo().getCode(),
//                                        getLotteryPanel().getNextIssue().getIssue(),
//                                        getLotteryPlay().getPlayId(),
//                                        getRequestText());
//                            }
//                        });
//                runnables.add(() -> dialog.dismiss());
                return true;
            }
        });


    }

    private void _buy(String lottery, String issue, String method, String requestText) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        Maps.gen();
        Maps.put(CtxLotteryTouCai.Order.LOTTERY, lottery);
        if (Strs.isNotEmpty(issue)) {
            Maps.put(CtxLotteryTouCai.Order.ISSUE, issue);
        } else {
            Maps.put(CtxLotteryTouCai.Order.ISSUE, getLotteryPanel().getNextIssue().getIssue());
        }
        Maps.put(CtxLotteryTouCai.Order.METHOD, method);
        Maps.put(CtxLotteryTouCai.Order.CONTENT, requestText);
        Maps.put(CtxLotteryTouCai.Order.MODEL, frag.getFoot().getModel());
        Maps.put(CtxLotteryTouCai.Order.MULTIPLE, frag.getFoot().getNumTimes());
        Maps.put(CtxLotteryTouCai.Order.CODE, frag.getFoot().getCurrentPoint());
        Maps.put("compress", false);
        list.add(Maps.get());

        HttpActionTouCai.addOrder(this,
                list,
                new AbHttpResult() {
                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        entity.putField("data", Double.TYPE);
                    }

                    @Override
                    public void onBefore(Request request, int id, String host, String funcName) {
                        getLotteryPanel().showLoading();
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        if (code == 0 && error == 0) {
                            DialogsTouCai.showLotteryBuyOkDialog(getContext());

                            //刷新用户资金
                            UserManagerTouCai.getIns().setLotteryAvailableBalance((getField(extra, "data", 0.0)));
                            UserManagerTouCai.getIns().sendBroadcaset(getContext(), UserManagerTouCai.EVENT_USER_INFO_UNPDATED, null);

                            //清除选球数据
                            clearAllSelected();

                            //刷新投注记录
                            ThreadCollector.getIns().postDelayOnUIThread(300, new Runnable() {
                                @Override
                                public void run() {
                                    EventBus.getDefault().post(new BuyedLotteryEvent());
                                }
                            });
                        } else if (code == -1000) {
                            showTimerDialogs();
                        } else {
                            if (Strs.isEqual("余额不足", msg)
                                    || Strs.isEqual("没有足够的余额", msg)) {
                                DialogsTouCai.showBalanceNotEnough(getContext());
                            } else {
                                DialogsTouCai.showDialog(getContext(), "温馨提醒", msg, null);
                            }
                        }

                        return true;
                    }

                    @Override
                    public void onAfter(int id) {
                        getLotteryPanel().hideLoading();
                    }
                });
    }

    public void showTimerDialogs() {
        Dialogs.showProgressDialog(((Activity) getCtx()), "");
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                Dialogs.hideProgressDialog(((Activity) getCtx()));
            }
        }, 5000);
    }

    protected abstract void clearAllSelected();

    public abstract boolean isAllGroupReady();

    public abstract void refreshCurrentUserPlayInfo(Object userPlayInfo);

    public abstract String getRequestText();

    /**
     * @return 检查是否已经计算完成
     */
    public abstract boolean canSubmitOrder();

    public static String convertRequestText(String methodName, String methodCode, String serverStr) {
        if (Strs.isEmpty(serverStr) || Strs.isEmpty(methodCode)) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        try {
            if (methodCode.contains("fs") && methodName.endsWith("复式") && !methodName.contains("组选")) {
                if (methodName.contains("中三")) {
                    str.append("-,");
                } else if (methodName.contains("后二")) {
                    str.append("-,-,-,");
                } else if (methodName.contains("后三")) {
                    str.append("-,-,");
                } else if (methodName.contains("后四")) {
                    str.append("-,");
                }
                str.append(serverStr.replace(",", "").replace('|', ','));
                if (methodName.contains("中三")) {
                    str.append(",-");
                } else if (methodName.contains("前二")) {
                    str.append(",-,-,-");
                } else if (methodName.contains("前三")) {
                    str.append(",-,-");
                } else if (methodName.contains("前四")) {
                    str.append(",-");
                }
            } else if (methodCode.contains("fs") && methodName.endsWith("复式") && methodName.contains("组选")) {
                str.append(serverStr);
            } else if (methodCode.contains("ds") && methodName.endsWith("单式") && methodName.contains("三码组六")) {
                str.append(serverStr.replace(",", "").replace('|', ' '));
            } else if (ArraysAndLists.findIndexWithEqualsOfArray(methodName, new String[]{
                    "前三直选单式", "3D直选单式"
            }) > -1) {
                str.append(serverStr.replace(",", " ").replace('|', ';'));
            }
        } catch (Exception e) {
            str.append("");  //容错
        }

        return str.toString();
    }

    public abstract void reset();

    @Override
    public void onInstructionClick() {
        DialogsTouCai.showInstructionDialog(getContext(), getUiConfig().getHelp());
    }

    public boolean addOrder(ArrayList<HashMap<String, Object>> list, LotteryPlayPanel actLotteryPlay) {

        if (actLotteryPlay == null) {
            return false;
        }

        if (!actLotteryPlay.hasCurrentRequestText()) {
            Toasts.show(actLotteryPlay.getContext(), "您还没有选择号码或所选号码不全", false);
            return false;
        }

        if (Math.abs(actLotteryPlay.getSnakeBar().getMoneyTotal()) < 1e-6) {
            Toasts.show(actLotteryPlay.getContext(), "尚无投注倍数!", false);
            return false;
        }

        if (!canSubmitOrder()) {
            Toasts.show("正在计算，请稍等!", false);
            return false;
        }

        if (actLotteryPlay.getNextIssue() == null) {
            Toasts.show(actLotteryPlay.getContext(), "请稍候, 尚无新期!", false);
            return false;
        }
        if (actLotteryPlay.getCurrentHit() <= 0) {
            Toasts.show(actLotteryPlay.getContext(), "尚未投注!", false);
            return false;
        }

        Maps.gen();
        Maps.put(CtxLotteryTouCai.Order.LOTTERY, CtxLotteryTouCai.getIns().findLotteryKind(actLotteryPlay.getLotteryId()).getCode());
        Maps.put(CtxLotteryTouCai.Order.ISSUE, actLotteryPlay.getNextIssue().getIssue());
        Maps.put(CtxLotteryTouCai.Order.METHOD, actLotteryPlay.getCurrentPlayId());
        Maps.put(CtxLotteryTouCai.Order.CONTENT, actLotteryPlay.getCurrentRequestText());
        Maps.put(CtxLotteryTouCai.Order.MODEL, frag.getFoot().getModel());
        Maps.put(CtxLotteryTouCai.Order.MULTIPLE, frag.getFoot().getNumTimes());
        Maps.put(CtxLotteryTouCai.Order.CODE, frag.getFoot().getCurrentPoint());
        Maps.put(CtxLotteryTouCai.Order.COMPRESS, false);
        Maps.put(CtxLotteryTouCai.Order.AWARD, actLotteryPlay.getAward());

        Maps.put(CtxLotteryTouCai.Order.PLAY_NAME, actLotteryPlay.getCurrentPlayFullName());
        Maps.put(CtxLotteryTouCai.Order.MONEY, actLotteryPlay.getSnakeBar().getMoneyTotal());
        Maps.put(CtxLotteryTouCai.Order.HIT, actLotteryPlay.getCurrentHit());
        list.add(Maps.get());
        return true;
    }
}
