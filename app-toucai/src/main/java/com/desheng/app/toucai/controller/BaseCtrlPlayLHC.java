package com.desheng.app.toucai.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.ArraysAndLists;
import com.ab.util.Dialogs;
import com.ab.util.Maps;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.panel.BaseLotteryPlayFragmentLHC;
import com.desheng.app.toucai.panel.LotteryPlayPanelLHC;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.PlayHeadViewLHC;
import com.desheng.app.toucai.view.PlayOrderListView;
import com.desheng.base.controller.ControllerBase;
import com.desheng.base.controller.IControlled;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.event.ClearLotteryPlaySelectedEvent;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.Disposable;
import okhttp3.Request;

/**
 * 玩法controller基类
 * Created by lee on 2018/3/13.
 */
public abstract class BaseCtrlPlayLHC extends ControllerBase implements PlayHeadViewLHC.OnShakeForAutoGenerate, PlayHeadViewLHC.OnInstructionClickListener, PlayOrderListView.OnOneKeyBuyListener, TextWatcher {
    private LotteryPlayLHCUI uiConfig;
    private LotteryInfo lotteryInfo;

    BaseLotteryPlayFragmentLHC frag;
    UserManagerTouCai.EventReceiver receiver;

    Disposable mDisposable;

    LHCQuickPlayController quickPlayController;

    protected boolean isStoped = false;

    public BaseCtrlPlayLHC(Context ctx, BaseLotteryPlayFragmentLHC frag, LotteryInfo info, LotteryPlayLHCUI uiConfig, LHCQuickPlayController controller) {
        super(ctx);
        this.frag = frag;
        this.lotteryInfo = info;
        this.uiConfig = uiConfig;
        quickPlayController = controller;
    }


    @Override
    public void start(IControlled controlled) {
        if (isStoped)
            return;

        receiver = new UserManagerTouCai.EventReceiver().register(getContext(), new UserManagerTouCai.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (eventName.equals(UserManagerTouCai.EVENT_USER_INFO_UNPDATED)) {
                    frag.getHead().setMoney(UserManagerTouCai.getIns().getLotteryAvailableBalance());
                }
            }
        });
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (quickPlayController != null) {
            getLotteryPanel().getQuickPlay().setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLotteryPanel().getScrollView().getLayoutParams();
            params.bottomMargin = 0;
//            params.bottomMargin /= 2; // scrollview设置bottomMargin后，内部子view也应用了这个margin，这里除以2
            getLotteryPanel().getScrollView().setLayoutParams(params);

            quickPlayController.setLayout(getLotteryPanel().getQuickPlay());
        } else {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLotteryPanel().getScrollView().getLayoutParams();
            params.bottomMargin = 0;
            getLotteryPanel().getScrollView().setLayoutParams(params);
        }

        setContentGroup();
        getLotteryPanel().getMoneyInput().addTextChangedListener(this);
        setHitCount(0);
        getLotteryPanel().getBottomGroup().findViewById(R.id.tvBuy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
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

                    buy();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toasts.show(getContext(), "投注失败", false);
                }
            }
        });

        getLotteryPanel().getBottomGroup().findViewById(R.id.tvClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelected();
            }
        });
    }

    @Override
    public void stop() {
        isStoped = false;
        stopping();
        EventBus.getDefault().unregister(this);
        receiver.unregister(getContext());
        getLotteryPanel().getMoneyInput().removeTextChangedListener(this);
    }

    @Override
    public void onAutoGenerate() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
        autoGenerate();
    }


    protected abstract void autoGenerate();

    protected abstract void buy();

    public LotteryPlayLHCUI getUiConfig() {
        return uiConfig;
    }

    public LotteryInfo getLotteryInfo() {
        return lotteryInfo;
    }

    public LHCQuickPlayController getQuickPlayController() {
        return quickPlayController;
    }

    public LotteryPlayPanelLHC getLotteryPanel() {
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

    @Override
    public void onBuyOneKey(String lottery, String issue, String method, String requestText) {
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
        Maps.put(CtxLotteryTouCai.Order.MODEL, frag.getFragContainer().getFootView().getModel());
        Maps.put(CtxLotteryTouCai.Order.MULTIPLE, frag.getFragContainer().getFootView().getNumTimes());
        Maps.put(CtxLotteryTouCai.Order.CODE, frag.getFragContainer().getFootView().getCurrentPoint());
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
                            Toasts.show(getContext(), msg, false);
                        }

                        return true;
                    }

                    @Override
                    public void onAfter(int id) {
                        getLotteryPanel().hideLoading();
                    }
                });
    }

    private void showTimerDialogs() {
        Dialogs.showProgressDialog(((Activity) getCtx()), "");
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                Dialogs.hideProgressDialog(((Activity) getCtx()));
            }
        }, 5000);
    }

    protected void showDialog(String title, String content, final View.OnClickListener onConfirm) {
        final Dialog dialog = new Dialog(getContext(), R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.dialog_lhc_msg, null);
        ((TextView) root.findViewById(R.id.tv_title1)).setText(title);
        TextView container = root.findViewById(R.id.tv_content);
        container.setText(content);

        if (onConfirm == null) {
            root.findViewById(R.id.btn_ok).setVisibility(View.GONE);
            root.findViewById(R.id.horizontal_divider).setVisibility(View.GONE);
        }

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirm != null)
                    onConfirm.onClick(v);
                dialog.dismiss();
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }

    protected abstract void clearAllSelected();

    public abstract boolean isAllGroupReady();

    public abstract void refreshCurrentUserPlayInfo(Object userPlayInfo);

    public abstract String getRequestText();

    @Override
    public void onInstructionClick() {
        DialogsTouCai.showInstructionDialog(getContext(), getUiConfig().getGameIntroduce());
    }

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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        double money = Strs.parse(s.toString(), 0.0);
        getLotteryPanel().getSnakeBar().setMoney(money > 0 ? money : 0);
    }
}
