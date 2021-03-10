package com.desheng.app.toucai.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.ab.thread.ThreadCollector;
import com.ab.util.Snackbars;
import com.ab.util.Strs;
import com.desheng.app.toucai.panel.FragLotteryPlayFragmentInput;
import com.desheng.base.algorithm.DSBetDetailAlgorithmUtil;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayUIConfig;

import java.util.Random;

/**
 * 五星直选玩法controller
 * <p>
 * 说明:"从万位、千位、百位、十位、个位中选择一个5位数号码组成一注，所选号码与开奖号码全部相同，且顺序一致，即为中奖。"
 * Created by lee on 2018/3/13.
 */
public class CtrlPlay_Input extends BaseCtrlPlay implements TextWatcher {

    private int needNum;
    private int numDigits;
    private String regex;
    private String splitReg;

    private String currInput = null;

    private EditText etInput;
    private String rightArrayStr;
    private String wrongArrayStr;
    private Activity mActivity;

    private String lastStr = "";
    private long reGenDelta = 0;
    private boolean isCalculationDone = true;

    public CtrlPlay_Input(Context ctx, FragLotteryPlayFragmentInput frag, LotteryInfo info, LotteryPlayUIConfig uiConfig, LotteryPlay play) {
        super(ctx, frag, info, uiConfig, play);
        this.mActivity = ((Activity) ctx);
    }

    //设置号码组
    @Override
    public void setContentGroup() {
        needNum = getUiConfig().getNeedNum();
        numDigits = getUiConfig().getInputNumDegits();
        regex = "(.{" + needNum * numDigits + "})";
        splitReg = "(.{2})";
        etInput = (EditText) frag.getInputGroup().get("etInput");
        etInput.addTextChangedListener(this);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                isCalculationDone = true;
                setHitCount(0);
            } else {
                calculationHit();
            }
        }
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String ori = s.toString().trim().replaceAll("[^0-9,]+", "");
        new Thread(() -> onCalculation(ori, s)).start();
    }

    private void onCalculation(final String ori, final Editable s) {
        if (Strs.isNotEmpty(ori)) {
            //向前删除
            if (lastStr.endsWith(",") && (s.toString() + ",").equals(lastStr)) {
                return;
            }

            //黏贴非法数据
            if (!ori.equals(s.toString())) {
                mActivity.runOnUiThread(() -> {
                    s.clear();
                    s.append(ori);
                });
                return;
            }

            StringBuffer buffer = new StringBuffer();
            //预处理符号标准化,
            String pre = ori.replace(" ", "").replace(",", "").replace(";", "");

            //自动分段
            String spaned = pre.replaceAll(regex, "$1,");

            //计算
            if (pre.length() >= needNum * numDigits) {
                //过滤纠正
                String filted = DSBetDetailAlgorithmUtil.getCorrectStr(spaned, getUiConfig().getLotteryID(), getLotteryInfo().getId());
                if (pre.length() % (needNum * numDigits) == 0) {
                    buffer.append(filted + ",");
                } else {
                    buffer.append(spaned);
                }
                currInput = buffer.toString();
                lastStr = buffer.toString();
                //号码矫正和提示
                //号码矫正和提示
                if (!ori.equals(buffer.toString())) {
                    mActivity.runOnUiThread(() -> {
                        s.clear();
                        s.append(lastStr);
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

            } else if (pre.length() < needNum * numDigits) {
                handler.sendEmptyMessage(0);
            }
        }
    }

    private void calculationHit() {
        //*******计算注数
        long count = DSBetDetailAlgorithmUtil.getPutInBetNoteFromBetNum(rightArrayStr, getUiConfig().getLotteryID(), getLotteryInfo().getId(), needNum * numDigits);
        setHitCount(count);
        isCalculationDone = true;
        if (getLotteryPanel().getFootView() != null) {
            getLotteryPanel().getFootView().refreshBonus();
        }
    }


    @Override
    protected void clearAllSelected() {
        rightArrayStr = null;
        wrongArrayStr = null;
        etInput.setText("");
        setHitCount(0);
        if (getLotteryPanel().getFootView() != null) getLotteryPanel().getFootView().refreshBonus();
        ;
    }


    @Override
    public void autoGenerate() {
        etInput.setText("");
        StringBuilder builder = new StringBuilder();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < needNum; i++) {
            int rand = 0;
            if (getUiConfig().getValueFrom() > 0 && getUiConfig().getInputNumDegits() > 1) {
                rand = random.nextInt(getUiConfig().getValueTo() - getUiConfig().getValueFrom()) + getUiConfig().getValueFrom();
                if (rand < 10) {
                    builder.append("0" + rand);
                } else {
                    builder.append(rand);
                }
            } else {
                rand = random.nextInt(10);
                builder.append(rand);
            }
        }
        etInput.setText(builder.toString());
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (",".equals(etInput.getText().toString())) {
                    if (reGenDelta == 0) {
                        reGenDelta = 1;
                        ThreadCollector.getIns().runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbars.showShort(frag.getView(), "选号重复, 正在重选!");
                            }
                        });
                    }
                    autoGenerate();
                } else {
                    reGenDelta = 0;
                }
            }
        });
    }

    @Override
    public boolean isAllGroupReady() {
        return Strs.isNotEmpty(rightArrayStr) && Strs.isEmpty(wrongArrayStr);
    }

    @Override
    public void refreshCurrentUserPlayInfo(Object userPlayInfo) {
        //一般玩法无刷新
    }

    @Override
    public String getRequestText() {
        String result = "";
        if (Strs.isEmpty(rightArrayStr)) {
            return "";
        }
        if (getLotteryPanel().getCurrentHit() <= 0) {
            return "";
        }
        if (Strs.isNotEmpty(wrongArrayStr)) {
            result = "";
        } else if ("rowSemicolon_columnSpace".equals(getUiConfig().getGangType())) {
            result = rightArrayStr.replace(",", ";").trim();
            String[] arr = result.split(";");
            for (int i = 0; i < arr.length; i++) {
                //自动分段
                arr[i] = arr[i].replaceAll(splitReg, "$1 ");
                arr[i] = arr[i].substring(0, arr[i].length() - 1);
            }
            result = "";
            for (int i = 0; i < arr.length; i++) {
                result += result.length() == 0 ? arr[i] : ";" + arr[i];
            }
        } else if (Strs.isNotEmpty(rightArrayStr)) {
            result = rightArrayStr.replace(",", " ").trim();
        } else {
            result = "";
        }
        return result;
    }

    @Override
    public boolean canSubmitOrder() {
        return isCalculationDone;
    }

    @Override
    public void reset() {
        clearAllSelected();
    }
    
    
    /*public static void main ( String[] args )
        {
            String input = "abcdefghijk";
            String regex = "(.{3})";
            input = input.replaceAll (regex, "$1,");
            System.out.println (input);
        }*/
}
