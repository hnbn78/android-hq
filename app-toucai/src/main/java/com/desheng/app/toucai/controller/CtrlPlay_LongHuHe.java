package com.desheng.app.toucai.controller;

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
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.panel.FragLotteryPlayFragmentLongHuHe;
import com.desheng.app.toucai.panel.LotteryPlayPanel;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.PlayLongHuHeView;
import com.desheng.base.algorithm.DSBetDetailAlgorithmUtil;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.Request;

/**
 * 五星直选玩法controller
 * <p>
 * 说明:"从万位、千位、百位、十位、个位中选择一个5位数号码组成一注，所选号码与开奖号码全部相同，且顺序一致，即为中奖。"
 * Created by lee on 2018/3/13.
 */
public class CtrlPlay_LongHuHe extends BaseCtrlPlay implements PlayLongHuHeView.OnBallsChangeListener {
    int needBall = getUiConfig().getNeedBall();


    ArrayList<PlayLongHuHeView> listBallGroup;
    //所有选择的球组 万 -> 个
    private boolean[][] arrAllBallsSelected = null;
    //所有选择求组value 万 -> 个
    private String[][] arrAllBallsValue = null;


    public CtrlPlay_LongHuHe(Context ctx, FragLotteryPlayFragmentLongHuHe frag, LotteryInfo info, LotteryPlayUIConfig uiConfig, LotteryPlay play) {
        super(ctx, frag, info, uiConfig, play);
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
        long count = DSBetDetailAlgorithmUtil.getBetNoteFromBetNum(betNumStr, getUiConfig().getLotteryID(), getLotteryInfo().getId());
        if (count < 0) {
            count = 0;
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
    }


    @Override
    public void autoGenerate() {
        //随机各组
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < arrAllBallsSelected[0].length; i++) {
            arrAllBallsSelected[0][i] = false;
        }
        for (int i = 0; i < needBall; i++) {
            arrAllBallsSelected[0][nextInstinct(random, arrAllBallsSelected[0].length, arrAllBallsSelected[0])] = true;

        }
        listBallGroup.get(0).syncSelection();
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
        for (int j = 0; j < arrAllBallsSelected[0].length; j++) {
            if (arrAllBallsSelected[0][j]) {
                groupHasSelectionCount++;
            }
        }
        return groupHasSelectionCount >= 0;
    }

    @Override
    public void refreshCurrentUserPlayInfo(Object userPlayInfo) {
        //一般玩法无刷新
    }

    public String getNumFromCell() {
        StringBuilder numBetStr = new StringBuilder();
        boolean isGotValidNum = true;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            StringBuffer rowStr = new StringBuffer();
            boolean lineHaveBall = false;
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[i][j] : " " + arrAllBallsValue[i][j]);
                    if (!lineHaveBall) {
                        lineHaveBall = true;
                    }
                }
            }
            if (!lineHaveBall) {
                isGotValidNum = false;
                break;
            }
            numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
        }
        if (isGotValidNum) {
            return numBetStr.toString();
        } else {
            return "";
        }
    }

    @Override
    public String getRequestText() {
        if ("".equals(getNumFromCell())) {
            return "";
        }

        StringBuilder numBetStr = new StringBuilder();

        for (int i = 0; i < arrAllBallsSelected[0].length; i++) {
            String str = arrAllBallsValue[0][i];
            if (arrAllBallsSelected[0][i]) {
                numBetStr.append(str);
            }
        }
        return numBetStr.toString().substring(0, numBetStr.length() - 1);
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
        List<Runnable> runnables = new ArrayList<>();

        _buy(getLotteryInfo().getCode(),
                getLotteryPanel().getNextIssue().getIssue(),
                getLotteryPlay().getPlayId(),
                getRequestText());

//        Dialog dialog = DialogsTouCai.showLotteryListBuyConfirmDialog(
//                getContext(),
//                issue,
//                String.valueOf(getLotteryPanel().getSnakeBar().getHitCount()),
//                Nums.formatDecimal(getLotteryPanel().getSnakeBar().getMoneyTotal(), 2),
//                "1",
//                String.valueOf(getLotteryPanel().getLotteryPlayUserInfo().getInfo().getMaxBonus()),
//                new BaseAdapter() {
//                    @Override
//                    public int getCount() {
//                        return 1;
//                    }
//
//                    @Override
//                    public Object getItem(int position) {
//                        return requestText;
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
//                        method.setText(getLotteryPlay().showName);
//                        TextView count = convertView.findViewById(R.id.tv_lottery_count);
//                        count.setText(String.format("@%d倍*%s注", getLotteryPanel().getFootView().getNumTimes(), getLotteryPanel().getSnakeBar().getHitCount()));
//
//                        TextView content = convertView.findViewById(R.id.tv_lottery_content);
//                        content.setText(requestText);
//                        convertView.findViewById(R.id.ib_del).setVisibility(View.GONE);
//                        convertView.findViewById(R.id.ib_del).setOnClickListener(v -> {
//                            reset();
//                            if (runnables.size() > 0)
//                                runnables.get(0).run();
//                        });
//                        return convertView;
//                    }
//                },
//                () -> {
//                    _buy(getLotteryInfo().getCode(),
//                            getLotteryPanel().getNextIssue().getIssue(),
//                            getLotteryPlay().getPlayId(),
//                            getRequestText());
//                });
//        runnables.add(() -> dialog.dismiss());
    }

    public void _buy(String lottery, String issue, String method, String requestText) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        //content 一条参数变多条
        String[] split = requestText.split(",");
        for (int i = 0; i < split.length; i++) {
            Maps.gen();
            Maps.put(CtxLotteryTouCai.Order.LOTTERY, lottery);
            Maps.put(CtxLotteryTouCai.Order.ISSUE, issue);
            Maps.put(CtxLotteryTouCai.Order.METHOD, method);
            Maps.put(CtxLotteryTouCai.Order.CONTENT, split[i]);
            Maps.put(CtxLotteryTouCai.Order.MODEL, frag.getFoot().getModel());
            Maps.put(CtxLotteryTouCai.Order.MULTIPLE, frag.getFoot().getNumTimes());
            Maps.put(CtxLotteryTouCai.Order.CODE, frag.getFoot().getCurrentPoint());
            Maps.put("compress", false);
            list.add(Maps.get());
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

        String[] methodArr = actLotteryPlay.getCurrentRequestText().split(",");
        for (int i = 0; i < methodArr.length; i++) {
            Maps.gen();
            Maps.put(CtxLotteryTouCai.Order.LOTTERY, CtxLotteryTouCai.getIns().findLotteryKind(actLotteryPlay.getLotteryId()).getCode());
            Maps.put(CtxLotteryTouCai.Order.ISSUE, actLotteryPlay.getNextIssue().getIssue());
            Maps.put(CtxLotteryTouCai.Order.METHOD, actLotteryPlay.getCurrentPlayId());
            Maps.put(CtxLotteryTouCai.Order.CONTENT, methodArr[i]);
            Maps.put(CtxLotteryTouCai.Order.MODEL, actLotteryPlay.getCurrentModel());
            Maps.put(CtxLotteryTouCai.Order.MULTIPLE, actLotteryPlay.getCurrentNumTimes());
            Maps.put(CtxLotteryTouCai.Order.CODE, actLotteryPlay.getCurrentPoint());
            Maps.put(CtxLotteryTouCai.Order.COMPRESS, false);
            Maps.put(CtxLotteryTouCai.Order.AWARD, actLotteryPlay.getAward());

            Maps.put(CtxLotteryTouCai.Order.PLAY_NAME, actLotteryPlay.getCurrentPlayFullName());
            Maps.put(CtxLotteryTouCai.Order.MONEY, actLotteryPlay.getSnakeBar().getMoneyTotal());
            Maps.put(CtxLotteryTouCai.Order.HIT, 1l);
        }

        list.add(Maps.get());
        return true;
    }

    /*public static void main ( String[] args )
        {
            String input = "abcdefghijk";
            String regex = "(.{3})";
            input = input.replaceAll (regex, "$1,");
            System.out.println (input);
        }*/
}
