package com.desheng.app.toucai.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.Strs;
import com.ab.util.Views;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.base.model.LotteryPlayJD;
import com.desheng.base.model.LotteryPlayUserInfoJD;
import com.noober.background.view.BLTextView;
import com.shark.tc.R;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 经典玩法数字
 * Created by lee on 2018/3/13.
 */
public class PlayDigitsJDView extends FrameLayout {

    protected TextView tvDigitPowerLab;
    protected GridLayout glDigits;


    protected String power = "";
    protected LotteryPlayJD.LayoutBean layoutBean;

    //启用的组
    protected ArrayList<ViewGroup> listNumBallGroup = new ArrayList<ViewGroup>();
    //启用的球数字列表
    protected ArrayList<TextView> listNumUsed = new ArrayList<>();
    //启用的球值列表
    protected ArrayList<TextView> listBallUsed = new ArrayList<>();
    //所有的ballid
    protected int[] arrIds;

    //启用球的选择状态
    protected boolean[] arrBallSelected;
    //上一次球的选中状态
    protected boolean[] arrBallSelectedLast;

    protected String[] arrNum;
    protected String[] arrBall;

    //球选中状态变化监听
    protected OnBallsChangeListener onBallsChangeListener;
    //显示模式
    protected int mode_boll1_dual2;
    protected String title;

    protected int mLotteryId;


    public PlayDigitsJDView(@NonNull Context context) {
        super(context);
    }

    public PlayDigitsJDView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayDigitsJDView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void init(int lotteryId) {
        Log.e("PlayDigitsJDView", lotteryId + "");
        Context context = getContext();
        mLotteryId = lotteryId;
        RelativeLayout inner;
        if (needNewBetUi(mLotteryId)) {
            inner = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.view_lottery_play_digits_jdpk10, this, false);
        } else {
            inner = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.view_lottery_play_digits_jd, this, false);
        }
        tvDigitPowerLab = (TextView) inner.findViewById(R.id.tvDigitPowerLab);
        glDigits = (GridLayout) inner.findViewById(R.id.glDigits);
        this.addView(inner);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (listNumUsed != null) {
            for (TextView te : listNumUsed) {
                if (Strs.isEqual(te.getText().toString(), "任意豹子")) {
                    try {
                        View cell = (View) te.getParent().getParent();
                        cell.measure(MeasureSpec.makeMeasureSpec(Views.dp2px(130), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(cell.getMeasuredHeight(), MeasureSpec.EXACTLY));
                        cell.layout(cell.getLeft(), cell.getTop(), cell.getLeft() + Views.dp2px(130), cell.getBottom());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }

            if ("全五中一".equals(layoutBean.getTitle()) && listNumUsed.size() == 11) {
                TextView te = listBallUsed.get(10);
                try {
                    View cell = (View) te.getParent().getParent();
                    Method getMarginMethod = GridLayout.class.getDeclaredMethod("getMargin", View.class, Boolean.TYPE, Boolean.TYPE);
                    getMarginMethod.setAccessible(true);
                    int margin = (int) getMarginMethod.invoke(cell.getParent(), cell, true, true);
                    cell.layout(
                            cell.getLeft() + (((GridLayout) cell.getParent()).getMeasuredWidth() - cell.getMeasuredWidth()) / 2 - margin,
                            cell.getTop(),
                            cell.getRight() + (((GridLayout) cell.getParent()).getMeasuredWidth() - cell.getMeasuredWidth()) / 2 - margin,
                            cell.getBottom());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setConfig(LotteryPlayJD.LayoutBean layoutBean, LotteryPlayUserInfoJD userInfoJD, int boll1_dual2) {
        this.layoutBean = layoutBean;
        mode_boll1_dual2 = boll1_dual2;
        //this.power = getPowerByTitle(layoutBean.getTitle());
        //号码标题
        this.title = layoutBean.getTitle();
        tvDigitPowerLab.setText(layoutBean.getTitle());
        //球显示
        listNumUsed = new ArrayList<>();
        listBallUsed = new ArrayList<>();
        arrNum = new String[layoutBean.getBalls().size()];
        arrBall = new String[layoutBean.getBalls().size()];
        arrIds = new int[layoutBean.getBalls().size()];

        glDigits.removeAllViews();
        listNumBallGroup.clear();

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) glDigits.getLayoutParams();
        if (boll1_dual2 == 3) {
            layoutParams.rightMargin = Views.dp2px(0);
        } else {

            if (needNewBetUi(mLotteryId)) {
                layoutParams.rightMargin = Views.dp2px(0);
            } else {
                layoutParams.rightMargin = Views.dp2px(12);
            }

        }
        glDigits.setLayoutParams(layoutParams);

        for (int i = 0; i < layoutBean.getBalls().size(); i++) {
            int resid = R.layout.view_digit_jd_rect;

            switch (boll1_dual2) {
                case 1:
                    if (needNewBetUi(mLotteryId)) {
                        resid = R.layout.view_digit_jd_pk10_rect;
                    } else {
                        resid = R.layout.view_digit_jd_rect;
                    }
                    break;
                case 2:
                    if (needNewBetUi(mLotteryId)) {
                        resid = R.layout.view_digit_jd_round_pk10;
                    } else {
                        resid = R.layout.view_digit_jd_round;
                    }
                    break;
                case 3:
                    resid = R.layout.view_digit_jd_round_small;
                    break;
            }

            RelativeLayout digitVg = (RelativeLayout) LayoutInflater.from(getContext()).inflate(resid, glDigits, false);
            ViewGroup vgNumBalls = (ViewGroup) digitVg.findViewById(R.id.vgNumBalls);
            vgNumBalls.setTag(i);
            listNumBallGroup.add(vgNumBalls);

            TextView tvNum = (TextView) digitVg.findViewById(R.id.tvNum);
            tvNum.setVisibility(View.VISIBLE);
            tvNum.setTag(i);
            arrNum[i] = layoutBean.getNum().get(i);
            tvNum.setText(arrNum[i]);
            listNumUsed.add(tvNum);

            TextView tvBall = (TextView) digitVg.findViewById(R.id.tvBall);
            tvBall.setVisibility(View.VISIBLE);
            tvBall.setTag(i);
            arrIds[i] = Strs.parse(layoutBean.getBalls().get(i).replace("j", ""), 0);
            if (userInfoJD == null || userInfoJD.getObj() == null || userInfoJD.getObj().getLines() == null) {
                arrBall[i] = layoutBean.getBalls().get(i);
            } else {
                arrBall[i] = userInfoJD.getObj().getLines().get(layoutBean.getBalls().get(i));
            }
            tvBall.setText(arrBall[i]);
            listBallUsed.add(tvBall);

            switch (boll1_dual2) {
                case 1:
                case 2:
                case 3:
                    if (needNewBetUi(mLotteryId)) {
                        vgNumBalls.setBackgroundResource(R.drawable.bg_lottery_play_view_digit_jd_item_long_bar_pk10);
                    } else {
                        vgNumBalls.setBackgroundResource(R.drawable.bg_lottery_play_view_digit_jd_item_long_bar);
                    }
                    break;
                default:
                    break;
            }

            glDigits.addView(digitVg);
        }

        switch (boll1_dual2) {
            case 1:
                glDigits.setColumnCount(2);
                break;
            case 2:
                glDigits.setColumnCount(2);
                break;
            case 3:
                glDigits.setColumnCount(2);
                break;
            default:
                break;
        }

        arrBallSelected = new boolean[listBallUsed.size()];
        arrBallSelectedLast = new boolean[listBallUsed.size()];
    }


    public void setBallSingleSelection() {
        for (int i = 0; i < listBallUsed.size(); i++) {
            listNumBallGroup.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    if (arrBallSelected[index]) { //已选定状态, 取消选择
                        arrBallSelected[index] = false;
                    } else {
                        arrBallSelected[index] = true;
                        for (int j = 0; j < listBallUsed.size(); j++) {
                            if (v != listBallUsed.get(j)) {
                                arrBallSelected[j] = false;
                            }
                        }
                    }
                    syncSelection();

                }
            });
        }
    }

    public void setBallMonoSelection() {
        for (int i = 0; i < listBallUsed.size(); i++) {
            listNumBallGroup.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    if (arrBallSelected[index]) { //已选定状态, 取消选择
                        arrBallSelected[index] = false;
                        for (int j = 0; j < listBallUsed.size(); j++) {
                            if (v != listBallUsed.get(j)) {
                                arrBallSelected[j] = false;
                            }
                        }
                    } else {
                        arrBallSelected[index] = true;
                        for (int j = 0; j < listBallUsed.size(); j++) {
                            if (v != listBallUsed.get(j)) {
                                arrBallSelected[j] = true;
                            }
                        }
                    }
                    syncSelection();
                }
            });
        }
    }

    public void setBallMultiSelection() {
        for (int i = 0; i < listBallUsed.size(); i++) {
            listNumBallGroup.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    if (arrBallSelected[index]) { //已选定状态, 取消选择
                        arrBallSelected[index] = false;
                    } else {
                        arrBallSelected[index] = true;
                    }
                    syncSelection();
                }
            });
        }
    }

    public void syncSelection() {
        //改变ui
        for (int i = 0; i < arrBallSelected.length; i++) {
            TextView ballView = listBallUsed.get(i);
            TextView numView = listNumUsed.get(i);
            listNumBallGroup.get(i).setSelected(arrBallSelected[i]);
            switch (mode_boll1_dual2) {
                case 1:
                case 2:
                case 3:
                    if (needNewBetUi(mLotteryId)) {
                        listNumBallGroup.get(i).setBackgroundResource(R.drawable.bg_lottery_play_view_digit_jd_item_long_bar_pk10);
                    } else {
                        listNumBallGroup.get(i).setBackgroundResource(R.drawable.bg_lottery_play_view_digit_jd_item_long_bar);
                    }
                    break;
                default:
                    break;
            }
        }

        //多次改变合并为一次回调
        boolean changedFromLastTime = false;
        for (int j = 0; j < arrBallSelected.length; j++) {
            if (arrBallSelected[j] != arrBallSelectedLast[j]) {
                changedFromLastTime = true;
            }
            arrBallSelectedLast[j] = arrBallSelected[j];
        }

        //改变了, 计算上次的
        if (changedFromLastTime) {
            if (onBallsChangeListener != null) {
                onBallsChangeListener.onBallsChanged(power, getSelectedNum(), getSelectedBalls(), arrBallSelected, arrBallSelected.length);
            }
        }
    }

    public ArrayList<String> getListBalls() {
        ArrayList<String> listSelected = new ArrayList<>();
        for (int i = 0; i < listBallUsed.size(); i++) {
            listSelected.add(arrBall[i]);
        }
        return listSelected;
    }

    public ArrayList<String> getListNum() {
        ArrayList<String> listSelected = new ArrayList<>();
        for (int i = 0; i < listNumUsed.size(); i++) {
            listSelected.add(arrNum[i]);
        }
        return listSelected;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getSelectedBalls() {
        ArrayList<String> listSelected = new ArrayList<>();
        for (int i = 0; i < arrBallSelected.length; i++) {
            if (arrBallSelected[i]) {
                listSelected.add(arrBall[i]);
            }
        }
        return listSelected;
    }

    public ArrayList<String> getSelectedNum() {
        ArrayList<String> listSelected = new ArrayList<>();
        for (int i = 0; i < arrBallSelected.length; i++) {
            if (arrBallSelected[i]) {
                listSelected.add(arrNum[i]);
            }
        }
        return listSelected;
    }


    public boolean[] getArrBallSelected() {
        return arrBallSelected;
    }

    public int[] getArrIds() {
        return arrIds;
    }

    public LotteryPlayJD.LayoutBean getLayoutBean() {
        return layoutBean;
    }

    public int getTopInScrollView() {
        ArrayList<Integer> allCount = new ArrayList<>();
        //AbDebug.log(AbDebug.TAG_APP, "begin getTopInScrollView" );
        innerGetParentNestedInScrollView(this, allCount);
        //AbDebug.log(AbDebug.TAG_APP, "begin endTopInScrollView" );
        int allTop = 0;
        for (int i = 0; i < allCount.size(); i++) {
            allTop += allCount.get(i);
        }
        return allTop;
    }

    private ViewGroup innerGetParentNestedInScrollView(ViewGroup view, ArrayList<Integer> allCount) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent instanceof NestedScrollView) {
            if (getLastSelectBottom() > parent.getMeasuredHeight())
                allCount.add(getLastSelectBottom() - parent.getMeasuredHeight());
            return view;
        } else {
            allCount.add(view.getTop());
            return innerGetParentNestedInScrollView(parent, allCount);
        }
    }

    private int getLastSelectBottom() {
        int last = 0;
        for (int i = 0; i < arrBallSelected.length; i++) {
            if (arrBallSelected[i])
                last = i;

        }

        View viewGroup = listNumBallGroup.get(last);
        int bottom = viewGroup.getBottom();
        int tops = 0;
        View root = (View) viewGroup.getParent();
        while (root != null && root != this) {
            tops += root.getTop();
            root = (View) root.getParent();
        }

        return bottom + tops;
    }

    public OnBallsChangeListener getOnBallsChangeListener() {
        return onBallsChangeListener;
    }

    public void setOnBallsChangeListener(OnBallsChangeListener onBallsChangeListener) {
        this.onBallsChangeListener = onBallsChangeListener;
    }

    public interface OnBallsChangeListener {
        void onBallsChanged(String power, ArrayList<String> selectedNum, ArrayList<String> selectedBall, boolean[] arrBallSelected, int ballCount);
    }

    private boolean needNewBetUi(int lotteryId) {
        return CommonConsts.setBetNeedNewUIConfig(LotteryKind.find(lotteryId).getCode());
    }
}
