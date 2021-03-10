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
import com.desheng.app.toucai.panel.FragLotteryPlayFragmentZodiacLHC;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.PlayZodiacLHCView;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.google.gson.Gson;
import com.pearl.view.SimpleCollapse.SimpleNestedScrollView;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;

/**
 * 五星直选玩法controller
 * <p>
 * 说明:"从万位、千位、百位、十位、个位中选择一个5位数号码组成一注，所选号码与开奖号码全部相同，且顺序一致，即为中奖。"
 * Created by lee on 2018/3/13.
 */
public class CtrlPlay_Zodiac_Six_LHC extends CtrlPlay_Zodiac_LHC {


    public CtrlPlay_Zodiac_Six_LHC(Context ctx, FragLotteryPlayFragmentZodiacLHC frag, LotteryInfo info, LotteryPlayLHCUI uiConfig, LHCQuickPlayController controller) {
        super(ctx, frag, info, uiConfig, controller);
    }

    @Override
    public void onBallsChanged(String power, ArrayList<String> selectedNum, ArrayList<String> selectedBall,boolean[] arrBallSelected, int ballCount) {
        int count = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            if (arrAllBallsSelected[i]) {
                count++;
            }
        }

        int betCount = (int) (AbMathUtil.factorial(count) / (AbMathUtil.factorial(count - 6) * AbMathUtil.factorial(6)));
        setHitCount(betCount);
    }

    @Override
    public void autoGenerate() {
        final int count = 6;
        final SimpleNestedScrollView scrollView = (SimpleNestedScrollView) getLotteryPanel().getScrollView();
        scrollView.realSmoothScrollTo(0, 300, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //随机各组
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    arrAllBallsSelected[i] = false;
                }

                final Random random = new Random(System.currentTimeMillis());
                final int[] ranInt = new int[count];
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

                for (int i = 0; i < ranInt.length; i++) {
                    arrAllBallsSelected[ranInt[i]] = true;
                }

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //刷新各组
                final PlayZodiacLHCView LotteryPlayDigitLHCView = frag.getPlayZodiacLHCView();
                scrollView.realSmoothScrollTo(LotteryPlayDigitLHCView.getTopInScrollView() + 20/*余量*/, 300, null);
                LotteryPlayDigitLHCView.syncSelection();
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
        final TextView etMoney = (TextView) getLotteryPanel().getBottomGroup().findViewById(R.id.etMoney);
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

        // 六肖需要选6个
        int zodiacCount = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            if (arrAllBallsSelected[i]) {
                zodiacCount++;
            }
        }

        if (zodiacCount < 6) {
            showDialog("温馨提醒", "最少选择6个生肖!",  new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            return;
        }


        String req = getUiConfig().getCellUI().get(0).getLhcIDReq();
        String lineStr = frag.getUserPlayInfoLHC().getObj().getLines().get(req);
        Pattern pattern = Pattern.compile("[\\d\\.]+");
        Matcher matcher = pattern.matcher(lineStr);
        String line = "";
        String rate = "";

        if(matcher.find()) {
            line = matcher.group();
        }
        if (matcher.find()) {
            rate = matcher.group();
        }
        String context = "";
        int count = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            if (arrAllBallsSelected[i]) {
                count++;

                if (context.length() != 0)
                    context += ",";

                context += getUiConfig().getCellUI().get(i).getGname();
            }
        }

        int betCount = (int) (AbMathUtil.factorial(count) / (AbMathUtil.factorial(count - 6) * AbMathUtil.factorial(6)));

        String content = UserManager.getIns().getLotteryPlayLHCCategoryDataBeanByPlayCode(frag.getCategory(),frag.getPlayCode()).getShowName();
        content += "【" + context;
        content += "】@" + rate;
        content += " *" + String.format("%.2f", money);

        final Dialog dialog = new Dialog(getContext(), R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.dialog_lhc_buy_confirm, null);
        ((TextView) root.findViewById(R.id.tv_title1)).setText(String.format("确认加入第%s期？", getLotteryPanel().getNextIssue().getIssue()));
        ((TextView) root.findViewById(R.id.tv_title2)).setText(String.format("共计 ￥%.2f/%d 注", money * betCount, betCount));
        final ViewGroup container = root.findViewById(R.id.ll_content);

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
        Pattern pattern = Pattern.compile("[\\d\\.]+");
        ArrayList<HttpActionTouCai.ReqBetParameter> listParams = new ArrayList<>();
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
        param.gname = "六肖";
        param.id = Strs.parse(getUiConfig().getCellUI().get(0).getLhcID(), 0);
        param.mingxi_1 = 0;

        String context = "";
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            if (arrAllBallsSelected[i]) {
                if (context.length() != 0)
                    context += ",";
                context += getUiConfig().getCellUI().get(i).getBetContext();
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
