package com.desheng.app.toucai.controller;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ab.global.Global;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.ArraysAndLists;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.panel.FragLotteryPlayFragmentDigitsJD;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.PlayDigitsJDView;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlayJD;
import com.desheng.base.model.LotteryPlayUserInfoJD;
import com.google.gson.Gson;
import com.pearl.view.SimpleCollapse.SimpleNestedScrollView;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import okhttp3.Request;

/**
 * 五星直选玩法controller
 * <p>
 * 说明:"从万位、千位、百位、十位、个位中选择一个5位数号码组成一注，所选号码与开奖号码全部相同，且顺序一致，即为中奖。"
 * Created by lee on 2018/3/13.
 */
public class CtrlPlay_Digits_JD extends BaseCtrlPlayJD implements PlayDigitsJDView.OnBallsChangeListener {

    ArrayList<PlayDigitsJDView> listBallGroup;

    private boolean[][] arrAllBallsSelected = null;
    private String[][] arrAllBalls = null;
    private String[][] arrAllNums = null;
    private int[][] arrAllIds = null;
    private String[] arrAllTitles = null;

    private LotteryPlayUserInfoJD userPlayInfo;

    public CtrlPlay_Digits_JD(Context ctx, FragLotteryPlayFragmentDigitsJD frag, LotteryInfo info, LotteryPlayJD uiConfig) {
        super(ctx, frag, info, uiConfig);
    }

    //设置号码组
    @Override
    public void setContentGroup() {
        userPlayInfo = frag.getUserPlayInfoJD();
        listBallGroup = frag.getListBallGroupsJD();
        arrAllBallsSelected = new boolean[listBallGroup.size()][];
        arrAllBalls = new String[listBallGroup.size()][];
        arrAllNums = new String[listBallGroup.size()][];
        arrAllIds = new int[listBallGroup.size()][];
        arrAllTitles = new String[listBallGroup.size()];
        for (int i = 0; i < listBallGroup.size(); i++) {
            PlayDigitsJDView playDigitJDView = listBallGroup.get(i);

            //初始化绑定数据
            playDigitJDView.setConfig(getUiConfig().getLayout().get(i), frag.getUserPlayInfoJD(), frag.getMode());
            playDigitJDView.setBallMultiSelection();
            playDigitJDView.setOnBallsChangeListener(this);

            //初始化各组数据
            arrAllBallsSelected[i] = playDigitJDView.getArrBallSelected();
            arrAllBalls[i] = ArraysAndLists.toStringArray(playDigitJDView.getListBalls());
            arrAllNums[i] = ArraysAndLists.toStringArray(playDigitJDView.getListNum());
            arrAllTitles[i] = playDigitJDView.getTitle();
            arrAllIds[i] = playDigitJDView.getArrIds();

        }
    }

    @Override
    public void onBallsChanged(String power, ArrayList<String> selectedNum, ArrayList<String> selectedBall, boolean[] arrBallSelected, int ballCount) {
        int count = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    count++;
                }
            }
        }

        setHitCount(count);
    }


    @Override
    protected void clearAllSelected() {
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                arrAllBallsSelected[i][j] = false;
            }
        }
        for (int i = 0; i < listBallGroup.size(); i++) {
            listBallGroup.get(i).syncSelection();
        }
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        if (frag.getFragContainer().getFootView() != null)
            frag.getFragContainer().getFootView().refreshBonus();
        ;
    }


    @Override
    public void autoGenerate() {
        Random random = new Random(System.currentTimeMillis());
        int r = random.nextInt(arrAllBallsSelected.length);
        //先滚到顶
        final SimpleNestedScrollView scrollView = (SimpleNestedScrollView) getLotteryPanel().getScrollView();
        scrollView.realSmoothScrollTo(0, 300, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //随机各组
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        arrAllBallsSelected[i][j] = false;
                    }
                    listBallGroup.get(i).syncSelection();
                }

                int ranInt = random.nextInt(arrAllBallsSelected[r].length);
                for (int j = 0; j < arrAllBallsSelected[r].length; j++) {
                    if (j == ranInt)
                        arrAllBallsSelected[r][j] = true;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //刷新各组
                if (mDisposable != null) {
                    mDisposable.dispose();
                }
                mDisposable = Flowable.interval(400, TimeUnit.MILLISECONDS)
                        .take(1)
                        .doOnNext(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                                if (mDisposable != null && !mDisposable.isDisposed())
                                    mDisposable.dispose();

                                final PlayDigitsJDView LotteryPlayDigitJDView = listBallGroup.get(r);
                                scrollView.realSmoothScrollTo(LotteryPlayDigitJDView.getTopInScrollView(), 300, null);
                                LotteryPlayDigitJDView.syncSelection();
                            }
                        });

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public boolean isAllGroupReady() {
        int groupHasSelectionCount = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    groupHasSelectionCount++;
                    break;
                }
            }
        }
        boolean isAllGroupHasBall = groupHasSelectionCount > 0;

        return isAllGroupHasBall;
    }


    private boolean hasRequestText() {
        return !"".equals(getRequestText());
    }

    @Override
    public void refreshCurrentUserPlayInfo(Object userPlayInfo) {
        if (isStoped)
            return;

        setContentGroup();
    }

    @Override
    public String getRequestText() {
        if (!isAllGroupReady()) {
            return "";
        } else {
            return "ok";
        }
    }

    @Override
    public void reset() {
        clearAllSelected();
    }

    @Override
    protected void buy() {
        double money = Views.getValue((TextView) getLotteryPanel().getBottomGroup().findViewById(R.id.etMoney), 0.0);
        if (money <= 0) {
            Toasts.show(getContext(), "请输入正确金额!", false);
            return;
        }
        if (!hasRequestText()) {
            DialogsTouCai.showDialog(getContext(), "温馨提醒", "请至少投一注", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            return;
        }

        final List<Pair<Pair<Integer, Integer>, String>> items = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    count++;
                    String rate = "";
                    String req = getUiConfig().getLayout().get(i).getBalls().get(j);
                    if (userPlayInfo != null) {
                        rate = userPlayInfo.getObj().getLines().get(getUiConfig().getLayout().get(i).getBalls().get(j));
                    }
                    String content = getUiConfig().getLayout().get(i).getTitle();
                    content += "【" + getUiConfig().getLayout().get(i).getNum().get(j);
                    content += "】@" + rate;
                    content += " *" + String.format("%.2f", money);
                    items.add(new Pair<>(new Pair<>(i, j), content));
                }
            }
        }

        final Dialog dialog = new Dialog(getContext(), R.style.custom_dialog_style);
        final ViewGroup root = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.dialog_lhc_buy_confirm, null);
        ((TextView) root.findViewById(R.id.tv_title1)).setText(String.format("确认加入第%s期？", getLotteryPanel().getNextIssue().getIssue()));
        ((TextView) root.findViewById(R.id.tv_title2)).setText(String.format("共计 ￥%.2f/%d 注", money * count, count));
        final ViewGroup container = root.findViewById(R.id.ll_content);

        for (int i = 0; i < items.size(); i++) {
            final int pos = i;
            final View item = LayoutInflater.from(getContext()).inflate(R.layout.dialog_lhc_buy_confirm_item, container, false);
            item.setTag(items.get(pos));
            ((TextView) item.findViewById(R.id.tv_lottery_content)).setText(items.get(i).second);
            double finalMoney = money;
            item.findViewById(R.id.ib_del).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pair<Pair<Integer, Integer>, String> pair = (Pair<Pair<Integer, Integer>, String>) item.getTag();
                    arrAllBallsSelected[pair.first.first][pair.first.second] = false;
                    items.remove(pair);
                    container.removeView(item);
                    frag.syncSelection();

                    int count = 0;
                    for (int i = 0; i < arrAllBallsSelected.length; i++) {
                        for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                            if (arrAllBallsSelected[i][j]) {
                                count++;
                            }
                        }
                    }
                    ((TextView) root.findViewById(R.id.tv_title2)).setText(String.format("共计 ￥%.2f/%d 注", finalMoney * count, count));

                    if (count == 0)
                        dialog.dismiss();
                }
            });
            container.addView(item);
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
                if (items.size() > 0) {
                    _buy();
                    dialog.dismiss();
                } else {
                    Toasts.show(getContext(), "没有选择号码", false);
                }
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }

    private void _buy() {
        TextView etMoney = (TextView) getLotteryPanel().getBottomGroup().findViewById(R.id.etMoney);
        double money = Views.getValue(etMoney, 0.0);
        if (money <= 0) {
            Toasts.show(getContext(), "请输入正确金额!", false);
            return;
        }
        etMoney.setText(Strs.of(money));
        if (!hasRequestText()) {
            Toasts.show(getContext(), "请至少选择一个号码!", false);
            return;
        }
        ArrayList<HttpActionTouCai.ReqBetParameter> listParams = new ArrayList<>();
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    HttpActionTouCai.ReqBetParameter param = new HttpActionTouCai.ReqBetParameter();
                    param.BetContext = arrAllNums[i][j];
                    param.BetType = 1;
                    param.IsForNumber = false;
                    param.IsTeMa = false;
                    param.Lines = arrAllBalls[i][j];
                    param.Money = String.valueOf(money);
                    param.gname = arrAllTitles[i];
                    param.id = arrAllIds[i][j];
                    param.mingxi_1 = 0;
                    listParams.add(param);
                }
            }
        }
        HttpActionTouCai.getAddOrderJD(getContext(), getLotteryInfo().getCode(), listParams, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog((Activity) getCtx(), "");
            }

            @Override
            public boolean onGetString(String str) {
                HttpActionTouCai.RespAddOrderJD resp = new Gson().fromJson(str, HttpActionTouCai.RespAddOrderJD.class);
                if (resp.result == 1) {
                    DialogsTouCai.showLotteryBuyOkDialog(getContext());

                    //刷新投注记录
                    ThreadCollector.getIns().postDelayOnUIThread(300, new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new BuyedLotteryEvent());
                        }
                    });

                    refreshUserPlayInfo();
                } else {
                    if (Strs.isEqual("余额不足", resp.msg)
                            || Strs.isEqual("没有足够的余额", resp.msg)) {
                        DialogsTouCai.showBalanceNotEnough(getContext());
                    } else {
                        DialogsTouCai.showDialog(getContext(), "温馨提醒", resp.msg, null);
                    }
                    DialogsTouCai.hideProgressDialog((Activity) getCtx());
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                DialogsTouCai.hideProgressDialog((Activity) getCtx());
                return false;
            }
        });

    }

    private void refreshUserPlayInfo() {
        HttpActionTouCai.getLotteryPlayInfoJD(this, getLotteryInfo().getCode(), new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {

            }

            @Override
            public boolean onGetString(String str) {
                clearAllSelected();
                if (str != null) {
                    userPlayInfo = new Gson().fromJson(str, LotteryPlayUserInfoJD.class);
                    getLotteryPanel().setCurrentUserPlayInfo(userPlayInfo);
                    UserManagerTouCai.getIns().setLotteryAvailableBalance(Nums.parse(userPlayInfo.getObj().getBalance(), 0.0));
                    UserManagerTouCai.getIns().sendBroadcaset(Global.app, UserManagerTouCai.EVENT_USER_INFO_UNPDATED, null);
                    ;
                } else {
                    Toasts.show(getCtx(), "获取数据失败!", false);
                }
                return true;
            }


            @Override
            public void onAfter(int id) {
                DialogsTouCai.hideProgressDialog((Activity) getCtx());
            }
        });
    }

}
