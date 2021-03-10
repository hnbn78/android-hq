package com.desheng.app.toucai.controller;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Maps;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.panel.FragLotteryPlayFragmentLongHuHeAllInOne;
import com.desheng.app.toucai.panel.LotteryPlayPanel;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.PlayLongHuHeView;
import com.desheng.base.algorithm.DSBetDetailAlgorithmUtil;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayConfigCategoryTouCai;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.pearl.view.SimpleCollapse.SimpleNestedScrollView;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
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
public class CtrlPlay_LongHuHe_AllInOne extends BaseCtrlPlay implements PlayLongHuHeView.OnBallsChangeListener {

    private final LotteryInfo mLotteryInfo;
    ArrayList<PlayLongHuHeView> listBallGroup;
    //所有选择的球组 万 -> 个
    private boolean[][] arrAllBallsSelected = null;
    //所有选择求组value 万 -> 个
    private String[][] arrAllBallsValue = null;

    private List<LotteryPlayUIConfig> realUiConfig;
    private List<LotteryPlayConfigCategoryTouCai.CatBean.DataBean> realPlay;

    public CtrlPlay_LongHuHe_AllInOne(Context ctx, FragLotteryPlayFragmentLongHuHeAllInOne frag, LotteryInfo info, LotteryPlayUIConfig uiConfig, LotteryPlay play, List<LotteryPlayUIConfig> realUiConfig, List<LotteryPlayConfigCategoryTouCai.CatBean.DataBean> realPlay) {
        super(ctx, frag, info, uiConfig, play);
        this.realUiConfig = realUiConfig;
        this.realPlay = realPlay;
        this.mLotteryInfo = info;
        getLotteryPanel().getFootView().setLotteryInfo(UserManagerTouCai.getIns().getLotteryPlay(getLotteryPlay().category,
                realPlay.get(0).getLotteryCode()), getLotteryPanel().getLotteryPlayUserInfo());
    }

    //设置号码组
    @Override
    public void setContentGroup() {
        //球
        listBallGroup = frag.getListLHHGroups();
        arrAllBallsSelected = new boolean[listBallGroup.size()][];
        arrAllBallsValue = new String[listBallGroup.size()][];
        for (int i = 0; i < listBallGroup.size(); i++) {
            PlayLongHuHeView playDigitView = listBallGroup.get(i);
            playDigitView.setBallMultiSelection();
            playDigitView.setOnBallsChangeListener(this);
            arrAllBallsSelected[i] = playDigitView.getArrBallSelected();
            arrAllBallsValue[i] = playDigitView.getArrBallValue();
        }
    }


    public void onBallsChanged(String power, String[] arrAllValue, ArrayList<String> selectedValue, boolean[] arrBallSelected, int ballCount) {
        //每次变化都扫下所有球组, 如果发现全部都有选定项, 则计算注数, 否则注数清0
        int count = 0;
        if (isAllGroupReady()) { //都选择了球
            caculateHit();
        } else {
            setHitCount(0);
        }
    }

    private void caculateHit() {
        String betNumStr = getNumFromCell();
        String[] betNumStrs = betNumStr.split("%");
        long count = 0;
        boolean isNewLonghu = false;
        for (int i = 0; i < betNumStrs.length; i++) {
            int lotteryID = realUiConfig.get(i).getLotteryID();
            isNewLonghu = lotteryID > 1111 && lotteryID <= 1121;
            long c = DSBetDetailAlgorithmUtil.getBetNoteFromBetNum(betNumStrs[i], lotteryID, getLotteryInfo().getId());
            if (c < 0)
                continue;

            count += c;
        }

        if (count < 0) {
            count = 0;
        }

        int mLotteryInfoId = mLotteryInfo.getId();
        switch (mLotteryInfoId) {
            case 200:
            case 201:
            case 202:
                if ((betNumStr.contains("龙") || betNumStr.contains("虎")) && !betNumStr.contains("和")) {
                    //getLotteryPanel().getFootView().snakeView.setBonus(2.22);
                    //getLotteryPanel().getFootView().snakeView.setBonus(2.22, 9.98);
                }
//                else if (betNumStr.contains("和") && !(betNumStr.contains("龙") || betNumStr.contains("虎"))) {
//                    getLotteryPanel().getFootView().snakeView.setBonus(9.98);
//
//                } else if ((betNumStr.contains("龙") && betNumStr.contains("和")
//                        || betNumStr.contains("虎") && betNumStr.contains("和")
//                        || betNumStr.contains("龙") && betNumStr.contains("和") && betNumStr.contains("虎"))) {
//                    getLotteryPanel().getFootView().snakeView.setBonus(2.22, 9.98);
//                }

                break;
            default://其他情况的龙虎和 玩法
                if (!isNewLonghu) {
                    if ((betNumStr.contains("龙") || betNumStr.contains("虎")) && !betNumStr.contains("和")) {
                       // getLotteryPanel().getFootView().snakeView.setBonus(2.22, 9.98);
                    }
//                    else if (betNumStr.contains("和") && !(betNumStr.contains("龙") || betNumStr.contains("虎"))) {
//                        getLotteryPanel().getFootView().snakeView.setBonusForLH(9.98, true);
//
//                    } else if ((betNumStr.contains("龙") && betNumStr.contains("和")
//                            || betNumStr.contains("虎") && betNumStr.contains("和")
//                            || betNumStr.contains("龙") && betNumStr.contains("和") && betNumStr.contains("虎"))) {
//
//                    }
                }
                break;

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
        getLotteryPanel().getFootView().refreshBonus();
        ;
    }


    @Override
    public void autoGenerate() {
        //随机各组
        Random random = new Random(System.currentTimeMillis());
        int rowR = random.nextInt(realPlay.size());

        //先滚到顶
        final SimpleNestedScrollView scrollView = (SimpleNestedScrollView) getLotteryPanel().getScrollView();
        scrollView.realSmoothScrollTo(0, 300, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                for (int j = 0; j < realPlay.size(); j++) {
                    for (int i = 0; i < arrAllBallsSelected[j].length; i++) {
                        arrAllBallsSelected[j][i] = false;
                    }
                    listBallGroup.get(j).syncSelection();
                }

                int needBall = realUiConfig.get(rowR).getNeedBall();
                for (int i = 0; i < needBall; i++) {
                    arrAllBallsSelected[rowR][nextInstinct(random, arrAllBallsSelected[rowR].length, arrAllBallsSelected[rowR])] = true;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //刷新各组
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

                                final PlayLongHuHeView view = listBallGroup.get(rowR);
                                scrollView.realSmoothScrollTo(view.getTopInScrollView(), 300, null);
                                view.syncSelection();
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

    public static int nextInstinct(Random random, int lenght, boolean[] array) {
        int index = random.nextInt(lenght);
        if (array[index]) {
            return nextInstinct(random, lenght, array);
        } else {
            return index;
        }
    }

    @Override
    public boolean isAllGroupReady() {
        int groupHasSelectionCount = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    groupHasSelectionCount++;
                }
            }
        }
        return groupHasSelectionCount > 0;
    }

    @Override
    public void refreshCurrentUserPlayInfo(Object userPlayInfo) {
        getLotteryPanel().getFootView().setLotteryInfo(UserManagerTouCai.getIns().getLotteryPlay(getLotteryPlay().category, realPlay.get(0).getLotteryCode()), (LotteryPlayUserInfo) userPlayInfo);
    }

    public String getNumFromCell() {
        StringBuilder numBetStr = new StringBuilder();
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            StringBuffer rowStr = new StringBuffer();
            boolean lineHaveBall = false;
            if (i > 0)
                rowStr.append("%");
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[i][j] : " " + arrAllBallsValue[i][j]);
                    if (!lineHaveBall) {
                        lineHaveBall = true;
                    }
                }
            }
            numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
        }
        return numBetStr.toString();
    }

    @Override
    public String getRequestText() {
        if ("".equals(getNumFromCell())) {
            return "";
        }

        StringBuilder numBetStr = new StringBuilder();

        for (int j = 0; j < arrAllBallsSelected.length; j++) {
            for (int i = 0; i < arrAllBallsSelected[j].length; i++) {
                String str = arrAllBallsValue[j][i];
                if (arrAllBallsSelected[j][i]) {
                    numBetStr.append(str);
                }
            }
            numBetStr.append("%");
        }

        int lastMark = numBetStr.toString().lastIndexOf("%");
        if (lastMark > 0) {
            return numBetStr.substring(0, lastMark);
        } else {
            return numBetStr.toString();
        }
    }

    @Override
    public boolean canSubmitOrder() {
        return true;
    }

    @Override
    public void reset() {
        clearAllSelected();
    }


    @Override
    public void onBuyOneKey(String lottery, String issue, String method, String requestText) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        String[] arr = getRequestText().split("%");
        for (int j = 0; j < arr.length; j++) {
            String s = arr[j];
            String[] split = s.split(",");

            for (int i = 0; i < split.length; i++) {
                if (Strs.isEmpty(split[i]))
                    continue;
                Maps.gen();
                Maps.put(CtxLotteryTouCai.Order.LOTTERY, lottery);
                Maps.put(CtxLotteryTouCai.Order.ISSUE, issue);
                String lotteryCode = realPlay.get(j).getLotteryCode();
                if (lotteryCode.contains("gflonghuhe")) {//表明是新龙虎
                    String temtStr = "";
                    if (Strs.isEqual(split[i], "龙")) {
                        temtStr = "_long";
                    } else if (Strs.isEqual(split[i], "虎")) {
                        temtStr = "_hu";
                    } else if (Strs.isEqual(split[i], "和")) {
                        temtStr = "_he";
                    }
                    Maps.put(CtxLotteryTouCai.Order.METHOD, realPlay.get(j).getLotteryCode() + temtStr);
                } else {
                    Maps.put(CtxLotteryTouCai.Order.METHOD, realPlay.get(j).getLotteryCode());
                }
                Maps.put(CtxLotteryTouCai.Order.PLAY_NAME, realPlay.get(j).getShowName());
                Maps.put(CtxLotteryTouCai.Order.CONTENT, split[i]);
                Maps.put(CtxLotteryTouCai.Order.MODEL, frag.getFoot().getModel());
                Maps.put(CtxLotteryTouCai.Order.MULTIPLE, frag.getFoot().getNumTimes());
                Maps.put(CtxLotteryTouCai.Order.CODE, frag.getFoot().getCurrentPoint());
                Maps.put("compress", false);
                list.add(Maps.get());
            }
        }

        _buy(getLotteryInfo().getCode(),
                getLotteryPanel().getNextIssue() == null ? "" : getLotteryPanel().getNextIssue().getIssue(),
                getLotteryPlay().getPlayId(),
                getRequestText());

//        List<Dialog> dialogs = new ArrayList<>();
//        Dialog dialog = DialogsTouCai.showLotteryListBuyConfirmDialog(
//                getContext(),
//                issue,
//                String.valueOf(getLotteryPanel().getSnakeBar().getHitCount()),
//                Nums.formatDecimal(getLotteryPanel().getSnakeBar().getMoneyTotal(), 2),
//                String.valueOf(list.size()),
//                String.valueOf(getLotteryPanel().getLotteryPlayUserInfo().getInfo().getMaxBonus()),
//                new BaseAdapter() {
//                    @Override
//                    public int getCount() {
//                        return list.size();
//                    }
//
//                    @Override
//                    public Object getItem(int position) {
//                        return list.get(position);
//                    }
//
//                    @Override
//                    public long getItemId(int position) {
//                        return 0;
//                    }
//
//                    @Override
//                    public View getView(int position, View convertView, ViewGroup parent) {
//                        if (convertView == null)
//                            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_lottery_list_buy_confirm_item, parent, false);
//
//                        TextView method = convertView.findViewById(R.id.tv_lottery_method);
//                        method.setText(String.valueOf(list.get(position).get(CtxLotteryTouCai.Order.PLAY_NAME)));
//                        TextView count = convertView.findViewById(R.id.tv_lottery_count);
//                        count.setText(String.format("@%d倍*%d注", getLotteryPanel().getFootView().getNumTimes(), 1));
//
//                        TextView content = convertView.findViewById(R.id.tv_lottery_content);
//                        content.setText(String.valueOf(list.get(position).get(CtxLotteryTouCai.Order.CONTENT)));
//                        if (list.size() <= 1) {
//                            convertView.findViewById(R.id.ib_del).setVisibility(View.GONE);
//                        }
//
//                        convertView.findViewById(R.id.ib_del).setOnClickListener(v -> {
//                            for (int i = 0; i < realPlay.size(); i++) {
//                                if (Strs.isEqual(realPlay.get(i).getLotteryCode(), String.valueOf(list.get(position).get(CtxLotteryTouCai.Order.METHOD)))) {
//                                    int pos = 0;
//                                    switch (String.valueOf(list.get(position).get(CtxLotteryTouCai.Order.CONTENT))) {
//                                        case "龙":
//                                            pos = 0;
//                                            break;
//                                        case "虎":
//                                            pos = 1;
//                                            break;
//                                        case "和":
//                                            pos = 2;
//                                            break;
//                                    }
//
//                                    arrAllBallsSelected[i][pos] = false;
//                                    listBallGroup.get(i).syncSelection();
//                                    list.remove(position);
//                                    notifyDataSetChanged();
//                                    ((TextView) dialogs.get(0).findViewById(R.id.tv_title2)).setText(
//                                            String.format(
//                                                    "共计:¥%s元/%s注",
//                                                    Nums.formatDecimal(getLotteryPanel().getSnakeBar().getMoneyTotal(), 2),
//                                                    String.valueOf(getLotteryPanel().getSnakeBar().getHitCount())));
//                                    break;
//                                }
//                            }
//
//                            if (dialogs.size() > 0 && list.size() == 0)
//                                dialogs.get(0).dismiss();
//                        });
//                        return convertView;
//                    }
//                },
//                () -> {
//                    _buy(getLotteryInfo().getCode(),
//                            getLotteryPanel().getNextIssue() == null ? "" : getLotteryPanel().getNextIssue().getIssue(),
//                            getLotteryPlay().getPlayId(),
//                            getRequestText());
//                });
//        dialogs.add(dialog);
    }

    public void _buy(String lottery, String issue, String method, String requestText) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        //content 一条参数变多条
        String[] arr = getRequestText().split("%");
        for (int j = 0; j < arr.length; j++) {
            String s = arr[j];
            String[] split = s.split(",");

            for (int i = 0; i < split.length; i++) {
                if (Strs.isEmpty(split[i]))
                    continue;
                Maps.gen();
                Maps.put(CtxLotteryTouCai.Order.LOTTERY, lottery);
                Maps.put(CtxLotteryTouCai.Order.ISSUE, issue);

                String lotteryCode = realPlay.get(j).getLotteryCode();
                if (lotteryCode.contains("gflonghuhe")) {//表明是新龙虎
                    String temtStr = "";
                    if (Strs.isEqual(split[i], "龙")) {
                        temtStr = "_long";
                    } else if (Strs.isEqual(split[i], "虎")) {
                        temtStr = "_hu";
                    } else if (Strs.isEqual(split[i], "和")) {
                        temtStr = "_he";
                    }
                    Maps.put(CtxLotteryTouCai.Order.METHOD, realPlay.get(j).getLotteryCode() + temtStr);
                } else {
                    Maps.put(CtxLotteryTouCai.Order.METHOD, realPlay.get(j).getLotteryCode());
                }
                Maps.put(CtxLotteryTouCai.Order.CONTENT, split[i]);
                Maps.put(CtxLotteryTouCai.Order.MODEL, frag.getFoot().getModel());
                Maps.put(CtxLotteryTouCai.Order.MULTIPLE, frag.getFoot().getNumTimes());
                Maps.put(CtxLotteryTouCai.Order.CODE, frag.getFoot().getCurrentPoint());
                Maps.put("compress", false);
                list.add(Maps.get());
            }
        }

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

    @Override
    public boolean addOrder(ArrayList<HashMap<String, Object>> list, LotteryPlayPanel actLotteryPlay) {
        if (!actLotteryPlay.hasCurrentRequestText()) {
            Toasts.show(actLotteryPlay.getContext(), "您还没有选择号码或所选号码不全", false);
            return false;
        }

        if (Math.abs(actLotteryPlay.getSnakeBar().getMoneyTotal()) < 1e-6) {
            Toasts.show(actLotteryPlay.getContext(), "尚无投注倍数!", false);
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

        String[] arr = getRequestText().split("%");
        for (int j = 0; j < arr.length; j++) {
            String s = arr[j];
            String[] methodArr = s.split(",");

            for (int i = 0; i < methodArr.length; i++) {
                if (Strs.isEmpty(methodArr[i]))
                    continue;

                Maps.gen();
                Maps.put(CtxLotteryTouCai.Order.LOTTERY, CtxLotteryTouCai.getIns().findLotteryKind(actLotteryPlay.getLotteryId()).getCode());
                Maps.put(CtxLotteryTouCai.Order.ISSUE, actLotteryPlay.getNextIssue() == null ? "" : actLotteryPlay.getNextIssue().getIssue());

                String lotteryCode = realPlay.get(j).getLotteryCode();
                if (lotteryCode.contains("gflonghuhe")) {//表明是新龙虎
                    String temtStr = "";
                    if (Strs.isEqual(methodArr[i], "龙")) {
                        temtStr = "_long";
                    } else if (Strs.isEqual(methodArr[i], "虎")) {
                        temtStr = "_hu";
                    } else if (Strs.isEqual(methodArr[i], "和")) {
                        temtStr = "_he";
                    }
                    Maps.put(CtxLotteryTouCai.Order.METHOD, realPlay.get(j).getLotteryCode() + temtStr);
                } else {
                    Maps.put(CtxLotteryTouCai.Order.METHOD, realPlay.get(j).getLotteryCode());
                }
                Maps.put(CtxLotteryTouCai.Order.CONTENT, methodArr[i]);
                Maps.put(CtxLotteryTouCai.Order.MODEL, actLotteryPlay.getCurrentModel());
                Maps.put(CtxLotteryTouCai.Order.MULTIPLE, actLotteryPlay.getCurrentNumTimes());
                Maps.put(CtxLotteryTouCai.Order.CODE, actLotteryPlay.getCurrentPoint());
                Maps.put(CtxLotteryTouCai.Order.COMPRESS, false);
                Maps.put(CtxLotteryTouCai.Order.AWARD, actLotteryPlay.getAward());

                Maps.put(CtxLotteryTouCai.Order.PLAY_NAME, realPlay.get(j).getShowName());
                Maps.put(CtxLotteryTouCai.Order.MONEY, actLotteryPlay.getSnakeBar().getMoneyTotal() / actLotteryPlay.getCurrentHit());
                Maps.put(CtxLotteryTouCai.Order.HIT, 1l);
                list.add(Maps.get());
            }
        }

        return true;
    }
}
