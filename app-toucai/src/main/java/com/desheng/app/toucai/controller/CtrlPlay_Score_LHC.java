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
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.panel.FragLotteryPlayFragmentScoreLHC;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.PlayScoreLHCView;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.desheng.base.model.LotteryPlayUserInfoLHC;
import com.google.gson.Gson;
import com.pearl.view.SimpleCollapse.SimpleNestedScrollView;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
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
public class CtrlPlay_Score_LHC extends BaseCtrlPlayLHC implements PlayScoreLHCView.OnBallsChangeListener {


    private boolean[] arrAllBallsSelected = null;

    private LotteryPlayUserInfoLHC userPlayInfo;
    private FragLotteryPlayFragmentScoreLHC frag;
    public CtrlPlay_Score_LHC(Context ctx, FragLotteryPlayFragmentScoreLHC frag, LotteryInfo info, LotteryPlayLHCUI uiConfig, LHCQuickPlayController controller) {
        super(ctx, frag, info, uiConfig, controller);
        this.frag = frag;
    }

    //设置号码组
    @Override
    public void setContentGroup() {
        arrAllBallsSelected = new boolean[getUiConfig().getCellUI().size()];
        //初始化控件
        frag.getPlayScoreLHCView().setConfig(getUiConfig().getCellUI(), frag.getUserPlayInfoLHC());
        frag.getPlayScoreLHCView().setBallMultiSelection();
        frag.getPlayScoreLHCView().setOnBallsChangeListener(this);

        //初始化各组数据
        arrAllBallsSelected = frag.getPlayScoreLHCView().getArrBallSelected();
    }



    @Override
    public void onBallsChanged(String power, ArrayList<String> selectedNum, ArrayList<String> selectedBall,boolean[] arrBallSelected, int ballCount) {
        int count = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            if (arrAllBallsSelected[i]) {
                count++;
            }
        }

        setHitCount(count);
    }


    @Override
    protected void clearAllSelected() {
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
                arrAllBallsSelected[i] = false;
        }
        frag.getPlayScoreLHCView().syncSelection();
    }


    @Override
    public void autoGenerate() {
        //先滚到顶
        final SimpleNestedScrollView scrollView = (SimpleNestedScrollView) getLotteryPanel().getScrollView();
        scrollView.realSmoothScrollTo(0, 300, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //随机各组
                Random random = new Random(System.currentTimeMillis());

                int ranInt = random.nextInt(arrAllBallsSelected.length);
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    arrAllBallsSelected[i] = false;
                }
                arrAllBallsSelected[ranInt] = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                final PlayScoreLHCView view = frag.getPlayScoreLHCView();
                scrollView.realSmoothScrollTo(view.getTopInScrollView() + 20/*余量*/, 300, null);
                view.syncSelection();
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
                if (arrAllBallsSelected[i]) {
                    groupHasSelectionCount++;
                    break;
            }
        }
        boolean isAllGroupHasBall = groupHasSelectionCount > 0;

        return isAllGroupHasBall;
    }


    private boolean hasRequestText() {
        return !"".equals(getRequestText());
    }

    @Override
    public void refreshCurrentUserPlayInfo(Object userPlayInfo){
        if (isStoped)
            return;

        setContentGroup();
    }

    @Override
    public String getRequestText() {
        if (!isAllGroupReady()) {
            return "";
        }else{
            return "ok";
        }
    }

    @Override
    public void reset() {
        clearAllSelected();
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
        Pattern pattern = Pattern.compile("[\\d\\.]+");
        final List<Pair<Integer, String>> items = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            if (arrAllBallsSelected[i]) {
                count++;
                String req = getUiConfig().getCellUI().get(i).getLhcIDReq();
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

                String content = UserManager.getIns().getLotteryPlayLHCCategoryDataBeanByPlayCode(frag.getCategory(),frag.getPlayCode()).getShowName();
                content += "【" + getUiConfig().getCellUI().get(i).getGname();
                content += "】@" + rate;
                content += " *" + String.format("%.2f", money);
                items.add(new Pair<>(i, content));
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
            final double finalMoney = money;
            item.findViewById(R.id.ib_del).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pair<Integer, String> pair = (Pair<Integer, String>) item.getTag();
                    arrAllBallsSelected[pair.first] = false;
                    items.remove(pair);
                    container.removeView(item);
                    frag.syncSelection();

                    int count = 0;
                    for (int i = 0; i < arrAllBallsSelected.length; i++) {
                        if (arrAllBallsSelected[i]) {
                            count++;
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
        double money = Views.getValue((TextView) getLotteryPanel().getBottomGroup().findViewById(R.id.etMoney), 0.0);
        if(money <= 0){
            money = 1.0f;
        }
        ArrayList<HttpActionTouCai.ReqBetParameter> listParams = new ArrayList<>();
        Pattern pattern = Pattern.compile("[\\d\\.]+");
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            if(arrAllBallsSelected[i]) {
                String req = getUiConfig().getCellUI().get(i).getLhcIDReq();
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

                HttpActionTouCai.ReqBetParameter param = new HttpActionTouCai.ReqBetParameter();
                param.BetContext = getUiConfig().getCellUI().get(i).getBetContext();
                param.BetType = 1;
                param.IsForNumber = false;
                param.IsTeMa = false;
                param.Lines = line;
                param.rate = rate;
                param.Money = String.valueOf(money);
                param.gname = getUiConfig().getCellUI().get(i).getGname();
                param.id = Strs.parse(getUiConfig().getCellUI().get(i).getLhcID(), 0);
                param.mingxi_1 = 0;
                listParams.add(param);
            }
        }
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

    private void refreshUserPlayInfo(){
        HttpActionTouCai.getLotteryPlayInfoLHC(this, getLotteryInfo().getCode(), new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {

            }

            @Override
            public boolean onGetString(String str) {
                clearAllSelected();
                if (str != null) {
                    userPlayInfo = new Gson().fromJson(str, LotteryPlayUserInfoLHC.class);
                    getLotteryPanel().setCurrentUserPlayInfo(userPlayInfo);
                    UserManagerTouCai.getIns().setLotteryAvailableBalance(Nums.parse(userPlayInfo.getObj().getBalance(), 0.0));
                    UserManagerTouCai.getIns().sendBroadcaset(Global.app, UserManagerTouCai.EVENT_USER_INFO_UNPDATED, null);;
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
