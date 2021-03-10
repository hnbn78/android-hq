package com.desheng.app.toucai.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;

import com.ab.util.Snackbars;
import com.ab.util.Strs;
import com.desheng.app.toucai.panel.FragLotteryPlayFragmentInputCombind;
import com.desheng.base.algorithm.DSBetDetailAlgorithmUtil;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayUIConfig;

import java.util.ArrayList;
import java.util.Random;

/**
 * 五星直选玩法controller
 * <p>
 * 说明:"从万位、千位、百位、十位、个位中选择一个5位数号码组成一注，所选号码与开奖号码全部相同，且顺序一致，即为中奖。"
 * Created by lee on 2018/3/13.
 */
public class CtrlPlay_InputCombind extends BaseCtrlPlay implements TextWatcher {

    final int needNum = getUiConfig().getNeedNum();
    final String regex = "(.{" + needNum + "})";

    private ArrayList<ViewGroup> listPowerGroup;
    private ArrayList<CheckedTextView> listToggleBtns;
    private boolean[] arrPowerSelected;
    private String[] arrPowerValue;

    private String currInput = null;

    private EditText etInput;
    private String rightArrayStr;
    private String wrongArrayStr;

    private String lastStr = "";
    private long reGenDelta = 0;
    private boolean isCalculationDone = true;
    private Activity mActivity;

    public CtrlPlay_InputCombind(Context ctx, FragLotteryPlayFragmentInputCombind frag, LotteryInfo info, LotteryPlayUIConfig uiConfig, LotteryPlay play) {
        super(ctx, frag, info, uiConfig, play);
        this.mActivity = ((Activity) ctx);
    }

    //设置号码组
    @Override
    public void setContentGroup() {
        //选位
        listPowerGroup = frag.getPowerGroup();
        listToggleBtns = frag.getPowerBtnList();
        arrPowerSelected = new boolean[listPowerGroup.size()];
        arrPowerValue = new String[listPowerGroup.size()];
        for (int i = 0; i < frag.getPowerGroup().size(); i++) {
            frag.getPowerGroup().get(i).setTag(i);
            frag.getPowerGroup().get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    arrPowerSelected[index] = !arrPowerSelected[index];
                    listToggleBtns.get(index).setChecked(arrPowerSelected[index]);

                    if (getPowerBetStr().split(",").length >= needNum) {
                        caculateHit();
                    } else {
                        setHitCount(0);
                    }
                }
            });
        }
        for (int i = 0; i < getUiConfig().getValue().size(); i++) {
            arrPowerValue[i] = getUiConfig().getValue().get(i);
        }

        //输入
        etInput = (EditText) frag.getInputGroup().get("etInput");
        etInput.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String ori = s.toString().trim();
        new Thread(() -> onCalculation(ori, s)).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    isCalculationDone = true;
                    setHitCount(0);
                    break;
                case 1:
                    caculateHit();
                    break;
            }
        }
    };

    public void onCalculation(String ori, final Editable s) {
        isCalculationDone = false;
        if (Strs.isNotEmpty(ori)) {
            //向前删除
            if (lastStr.endsWith(",") && (s.toString() + ",").equals(lastStr)) {
                return;
            }

            //黏贴非法数据
            if (!ori.equals(s.toString())) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        s.clear();
                        s.append(ori);
                    }
                });
                return;
            }

            StringBuffer buffer = new StringBuffer();
            //预处理符号标准化,
            String pre = ori.replace(" ", "").replace(",", "").replace(";", "");

            //自动分段
            String spaned = pre.replaceAll(regex, "$1,");

            //计算
            if (pre.length() >= needNum) {
                //过滤纠正
                String filted = DSBetDetailAlgorithmUtil.getCorrectStr(spaned, getUiConfig().getLotteryID(), getLotteryInfo().getId());
                if (pre.length() % (needNum) == 0) {
                    buffer.append(filted + ",");
                } else {
                    buffer.append(spaned);
                }
                currInput = buffer.toString();
                lastStr = buffer.toString();
                //号码矫正和提示
                if (!ori.equals(buffer.toString())) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lastStr = buffer.toString();
                            s.clear();
                            s.append(lastStr);
                        }
                    });
                }

                //***开始筛选, 查找正确号码段
                int needDigits = getUiConfig().getNeedNum() * getUiConfig().getInputNumDegits();
                String[] correctArray = DSBetDetailAlgorithmUtil.getCorrectArray(currInput, getUiConfig().getLotteryID(), getLotteryInfo().getId(), needDigits);
                rightArrayStr = correctArray[0];
                wrongArrayStr = correctArray[1];
                if (Strs.isNotEmpty(rightArrayStr)) { //成功的
                    handler.sendEmptyMessage(1);
                }

            } else if (pre.length() < needNum) {
                handler.sendEmptyMessage(0);
            }
        }
    }

    private void caculateHit() {
        long count = 0;
        if (Strs.isNotEmpty(rightArrayStr)) {
            count = DSBetDetailAlgorithmUtil.getPutInBetNoteFromBetNum(rightArrayStr, getUiConfig().getLotteryID(), getLotteryInfo().getId(), needNum);
        }
        long powerCount = DSBetDetailAlgorithmUtil.getBetTimesFromPower(getPowerBetStr(), getUiConfig().getLotteryID(), getLotteryInfo().getId(), needNum);
        setHitCount(count * powerCount);
        isCalculationDone = true;
        getLotteryPanel().getFootView().refreshBonus();
    }

    @Override
    protected void clearAllSelected() {
        rightArrayStr = null;
        wrongArrayStr = null;
        etInput.setText("");
        setHitCount(0);
        getLotteryPanel().getFootView().refreshBonus();
    }


    @Override
    public void autoGenerate() {
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < arrPowerSelected.length; i++) {
            arrPowerSelected[i] = false;
        }
        for (int i = 0; i < needNum; i++) {
            arrPowerSelected[nextInstinctPower(random)] = true;
        }
        syncPowerSelection();

        etInput.setText("");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < needNum; i++) {
            builder.append(random.nextInt(10));
        }
        etInput.setText(builder.toString());

    }

    private void syncPowerSelection() {
        for (int i = 0; i < listToggleBtns.size(); i++) {
            listToggleBtns.get(i).setChecked(arrPowerSelected[i]);
        }
    }

    private int nextInstinctPower(Random random) {
        int index = random.nextInt(arrPowerSelected.length);
        if (arrPowerSelected[index]) {
            return nextInstinctPower(random);
        } else {
            return index;
        }
    }

    @Override
    public boolean isAllGroupReady() {
        return Strs.isNotEmpty(rightArrayStr) && Strs.isEmpty(wrongArrayStr) && getPowerBetStr().length() >= needNum;
    }

    @Override
    public void refreshCurrentUserPlayInfo(Object userPlayInfo) {
        //一般玩法无刷新
    }

    @Override
    public String getRequestText() {
        if (Strs.isEmpty(rightArrayStr)) {
            return "";
        }
        if (getLotteryPanel().getCurrentHit() <= 0) {
            return "";
        }
        int countWei = 0;
        for (int i = 0; i < arrPowerSelected.length; i++) {
            if (arrPowerSelected[i]) {
                countWei++;
            }
        }
        if (countWei < getUiConfig().getNeedWei()) {
            return "";
        }

        String powerStr = "[";
        for (int i = 0; i < arrPowerSelected.length; i++) {
            String check = (arrPowerSelected[i] ? "√" : "-");
            powerStr += (powerStr.equals("[") ? check : "," + check);
        }
        powerStr += "]";
        return powerStr + rightArrayStr;
    }

    @Override
    public boolean canSubmitOrder() {
        return isCalculationDone;
    }

    @Override
    public void reset() {
        clearAllSelected();
    }

    public String getPowerBetStr() {
        String res = "";
        for (int i = 0; i < arrPowerSelected.length; i++) {
            if (arrPowerSelected[i]) {
                res += (res.isEmpty() ? arrPowerValue[i] : "," + arrPowerValue[i]);
            }
        }
        return res;
    }
    /*public static void main ( String[] args )
        {
            String input = "abcdefghijk";
            String regex = "(.{3})";
            input = input.replaceAll (regex, "$1,");
            System.out.println (input);
        }*/
}
