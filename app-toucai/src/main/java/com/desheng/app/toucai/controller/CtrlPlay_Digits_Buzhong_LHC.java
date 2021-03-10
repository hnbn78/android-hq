package com.desheng.app.toucai.controller;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.AbMathUtil;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.panel.FragLotteryPlayFragmentDigitsLHC;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.PlayDigitsLHCView;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.google.gson.Gson;
import com.pearl.view.SimpleCollapse.SimpleNestedScrollView;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class CtrlPlay_Digits_Buzhong_LHC extends CtrlPlay_Digits_LHC {

    int buzhongCount = 0;

    public CtrlPlay_Digits_Buzhong_LHC(Context ctx, FragLotteryPlayFragmentDigitsLHC frag, LotteryInfo info, LotteryPlayLHCUI uiConfig, LHCQuickPlayController controller) {
        super(ctx, frag, info, uiConfig, controller);
        if (Strs.isEqual("lhc_BZ1", frag.getPlayCode())) {
            buzhongCount = 5;
        } else if (Strs.isEqual("lhc_BZ2", frag.getPlayCode())) {
            buzhongCount = 6;
        } else if (Strs.isEqual("lhc_BZ3", frag.getPlayCode())) {
            buzhongCount = 7;
        } else if (Strs.isEqual("lhc_BZ4", frag.getPlayCode())) {
            buzhongCount = 8;
        } else if (Strs.isEqual("lhc_BZ5", frag.getPlayCode())) {
            buzhongCount = 9;
        } else if (Strs.isEqual("lhc_BZ6", frag.getPlayCode())) {
            buzhongCount = 10;
        }
    }

    @Override
    public void onBallsChanged(String power, ArrayList<String> selectedNum, ArrayList<String> selectedBall,boolean[] arrBallSelected, int ballCount) {
        int count = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    count++;
                }
            }
        }

        int betCount = (int) (AbMathUtil.factorial(count) / (AbMathUtil.factorial(count - buzhongCount) * AbMathUtil.factorial(buzhongCount)));
        setHitCount(betCount);
    }

    @Override
    public void autoGenerate() {
        int count = 5;
        if (Strs.isEqual(frag.getPlayCode(), "lhc_BZ1")) {
            count = 5;
        } else if (Strs.isEqual(frag.getPlayCode(), "lhc_BZ2")) {
            count = 6;
        } else if (Strs.isEqual(frag.getPlayCode(), "lhc_BZ3")) {
            count = 7;
        } else if (Strs.isEqual(frag.getPlayCode(), "lhc_BZ4")) {
            count = 8;
        } else if (Strs.isEqual(frag.getPlayCode(), "lhc_BZ5")) {
            count = 9;
        } else if (Strs.isEqual(frag.getPlayCode(), "lhc_BZ6")) {
            count = 10;
        }

        //先滚到顶
        final SimpleNestedScrollView scrollView = (SimpleNestedScrollView) getLotteryPanel().getScrollView();
        final int finalCount = count;
        scrollView.realSmoothScrollTo(0, 300, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //随机各组
                Random random = new Random(System.currentTimeMillis());

       /*        final int[] ranInt = new int[count];
        int maxLoop = 0;
        for (int i = 0; i < count; i++, maxLoop++) {
            ranInt[i] = random.nextInt(arrAllBallsSelected.length);

            boolean duplicate = false;

            for (int j = 0; j < i; j++) {
                if (ranInt[j] == ranInt[i]) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate)
                i--;

            if (maxLoop > 100)
                break; // 防止死循环
        }
*/         for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    int ranInt = random.nextInt(arrAllBallsSelected[i].length);

                    int[] arr = new int[finalCount];
                    int maxLoop = 0;

                    for (int j = 0; j < finalCount; j++, maxLoop++) {
                        arr[j] = random.nextInt(arrAllBallsSelected[i].length);
                        boolean duplicate = false;
                        for (int k = 0; k < j; k++) {
                            if (arr[k] == arr[j]) {
                                duplicate = true;
                                break;
                            }
                        }

                        if (duplicate)
                            j--;

                        if (maxLoop > 100)
                            break;
                    }

                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                            arrAllBallsSelected[i][j] = false;
                    }

                    for (int j = 0; j < finalCount; j++) {
                        arrAllBallsSelected[i][arr[j]] = true;
                    }
                    }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //刷新各组
                if(mDisposable != null){
                    mDisposable.dispose();
                }
                mDisposable = Flowable.interval(400, TimeUnit.MILLISECONDS)
                        .onBackpressureBuffer()
                        .doOnNext(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                                if (aLong >= arrAllBallsSelected.length) {
                                    mDisposable.dispose();
                                    return;
                                }
                                final PlayDigitsLHCView LotteryPlayDigitLHCView = listBallGroup.get(aLong.intValue());
                                scrollView.realSmoothScrollTo(LotteryPlayDigitLHCView.getTopInScrollView() + 20/*余量*/, 300, null);
                                LotteryPlayDigitLHCView.syncSelection();
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
    protected void buy() {
        TextView etMoney = (TextView) getLotteryPanel().getBottomGroup().findViewById(R.id.etMoney);
        double money = Views.getValue(etMoney, 0);
        if(money <= 0){
            money = 1;
        }
        etMoney.setText(Strs.of((int)money));
        if(!hasRequestText()){
            showDialog("温馨提醒", "请至少投一注", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            return;
        }

        if (Strs.isEqual(frag.getCategory(), "不中")) {
            int count = 0;
            for (int i = 0; i < arrAllBallsSelected.length; i++) {
                for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                    if (arrAllBallsSelected[i][j]) {
                        count++;
                    }
                }
            }

            if (count < buzhongCount) {
                showDialog("温馨提醒",  "最少选择" + buzhongCount + "个号码!", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                return;
            }
        }

        Pattern pattern = Pattern.compile("[\\d\\.]+");
        String req = getUiConfig().getCellUI().get(0).getLhcIDReq();
        String lineStr = frag.getUserPlayInfoLHC().getObj().getLines().get(req);
        Matcher matcher = pattern.matcher(lineStr);
        String line = "";
        String rate = "";

        if(matcher.find()) {
            line = matcher.group();
        }

        if (matcher.find()) {
            rate = matcher.group();
        }

        final Dialog dialog = new Dialog(getContext(), R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.dialog_lhc_buy_confirm, null);
        ((TextView) root.findViewById(R.id.tv_title1)).setText(String.format("确认加入第%s期？", getLotteryPanel().getNextIssue().getIssue()));
        final ViewGroup container = root.findViewById(R.id.ll_content);

        int ballCount = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    ballCount++;
                }
            }
        }

        int betCount = (int) (AbMathUtil.factorial(ballCount) / (AbMathUtil.factorial(ballCount - buzhongCount) * AbMathUtil.factorial(buzhongCount)));

        String context = "";
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    if (context.length() != 0)
                        context += ",";
                    context += getUiConfig().getCellUI().get(i).getShowNums().get(j);
                }
            }
        }
        String content = getUiConfig().getCellUI().get(0).getGname();
        content += "【" + context;
        content += "】@" + rate;
        content += " *" + String.format("%.2f", money);

        ((TextView) root.findViewById(R.id.tv_title2)).setText(String.format("共计 ￥%.2f/%d 注", money * betCount, betCount));
        final View item = LayoutInflater.from(getContext()).inflate(R.layout.dialog_lhc_buy_confirm_item, container, false);
        ((TextView) item.findViewById(R.id.tv_lottery_content)).setText(content);
        item.findViewById(R.id.ib_del).setVisibility(View.GONE);
        container.addView(item);


        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    _buy();
                    dialog.dismiss();
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }

    private void _buy() {
        double money = Views.getValue((TextView) getLotteryPanel().getBottomGroup().findViewById(R.id.etMoney), 0.0);
        if(money <= 0){
            money = 1.0f;
        }
        ArrayList<HttpActionTouCai.ReqBetParameter> listParams = new ArrayList<>();
        Pattern pattern = Pattern.compile("[\\d\\.]+");
        String req = getUiConfig().getCellUI().get(0).getLhcIDReq();
        String lineStr = frag.getUserPlayInfoLHC().getObj().getLines().get(req);
        Matcher matcher = pattern.matcher(lineStr);
        String line = "";
        String rate = "";

        if (matcher.find()) {
            line = matcher.group();
        }
        if (matcher.find()) {
            rate = matcher.group();
        }

        HttpActionTouCai.ReqBetParameter param = new HttpActionTouCai.ReqBetParameter();
        param.BetType = 1;
        param.IsForNumber = false;
        param.IsTeMa = false;
        param.Lines = line;
        param.rate = rate;
        param.Money = String.valueOf(money);
        param.gname = getUiConfig().getCellUI().get(0).getGname();
        param.id = Strs.parse(getUiConfig().getCellUI().get(0).getLhcID(), 0);
        param.mingxi_1 = 0;

        String context = "";
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    if (context.length() != 0)
                        context += ",";
                    context += getUiConfig().getCellUI().get(i).getShowNums().get(j);
                }
            }
        }

        param.BetContext = context;
        listParams.add(param);
        HttpActionTouCai.getAddOrderLHC(getContext(), getLotteryInfo().getCode(), listParams, new AbHttpResult(){
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog((Activity) getCtx(), "");
            }
    
            @Override
            public boolean onGetString(String str) {
                HttpActionTouCai.RespAddOrderLHC resp = new Gson().fromJson(str, HttpActionTouCai.RespAddOrderLHC.class);
                if(resp.result == 1){
                    DialogsTouCai.showLotteryBuyOkDialog(getContext());
                    ThreadCollector.getIns().postDelayOnUIThread(300, new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new BuyedLotteryEvent());
                        }
                    });
                    refreshUserPlayInfo();
                }else{
                    if (Strs.isEqual("余额不足", resp.msg)
                            || Strs.isEqual("没有足够的余额", resp.msg)) {
                        DialogsTouCai.showBalanceNotEnough(getContext());
                    } else {
                        showDialog("温馨提醒", resp.msg, null);
                    }
                    DialogsTouCai.hideProgressDialog((Activity) getCtx());
                }
                return true;
            }
    
            @Override
            public boolean onError(int status, String content) {
                Toasts.show(getCtx(), content, false);
                DialogsTouCai.hideProgressDialog((Activity) getCtx());
                return false;
            }
        });
        
    }

}
