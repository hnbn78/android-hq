package com.desheng.app.toucai.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.Strs;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.Fomulars;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.shark.tc.R;

import java.util.ArrayList;

/**
 * 玩法数字
 * Created by lee on 2018/3/13.
 */
public class PlayDigitView extends FrameLayout {

    protected TextView tvDigitPowerLab;
    protected GridLayout glDigits;

    protected LinearLayout vgHotKey;
    protected TextView vKeyQuan;
    protected TextView vKeyDa;
    protected TextView vKeyXiao;
    protected TextView vKeyJi;
    protected TextView vKeyOu;
    protected TextView vKeyQing;
    protected TextView[] arrKeys;

    protected String power = "";
    protected LotteryPlayUIConfig.LayoutBean layoutBean;
    //启用的球列表
    protected ArrayList<TextView> listBallUsed = new ArrayList<>();
    //启用球的选择状态
    protected boolean[] arrBallSelected;
    //上一次球的选中状态
    protected boolean[] arrBallSelectedLast;
    //启用球的值
    protected String[] arrBallValue;
    //是否特殊
    protected boolean isUseSpecialValue;

    //球选中状态变化监听
    protected OnBallsChangeListener onBallsChangeListener;

    public PlayDigitView(@NonNull Context context) {
        super(context);
    }

    public PlayDigitView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayDigitView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void init() {
        Context context = getContext();
        View inner = LayoutInflater.from(context).inflate(R.layout.view_lottery_play_digits, this, false);
        tvDigitPowerLab = (TextView) inner.findViewById(R.id.tvDigitPowerLab);
        glDigits = (GridLayout) inner.findViewById(R.id.glDigits);

        vgHotKey = (LinearLayout) inner.findViewById(R.id.vgHotKey);
        vKeyQuan = (TextView) inner.findViewById(R.id.vKeyQuan);
        vKeyDa = (TextView) inner.findViewById(R.id.vKeyDa);
        vKeyXiao = (TextView) inner.findViewById(R.id.vKeyXiao);
        vKeyJi = (TextView) inner.findViewById(R.id.vKeyJi);
        vKeyOu = (TextView) inner.findViewById(R.id.vKeyOu);
        vKeyQing = (TextView) inner.findViewById(R.id.vKeyQing);
        arrKeys = new TextView[]{vKeyQuan, vKeyDa, vKeyXiao, vKeyJi, vKeyOu, vKeyQing};
        this.addView(inner);
    }


    public void setConfig(LotteryPlayUIConfig.LayoutBean layoutBean) {
        this.layoutBean = layoutBean;
        this.power = getPowerByTitle(layoutBean.getTitle());
        //号码标题
        tvDigitPowerLab.setText(layoutBean.getTitle());
        //球显示
        listBallUsed = new ArrayList<>();

        glDigits.removeAllViews();

        for (int i = 0; i < layoutBean.getBalls().size(); i++) {
            RelativeLayout digitVg = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_digit_circle, glDigits, false);
            TextView tvDigit = (TextView) digitVg.findViewById(R.id.tvDigit);
            tvDigit.setVisibility(View.VISIBLE);
            tvDigit.setTag(i);
            tvDigit.setText(layoutBean.getBalls().get(i));
            glDigits.addView(digitVg);
            listBallUsed.add(tvDigit);
        }
        arrBallSelected = new boolean[listBallUsed.size()];
        arrBallSelectedLast = new boolean[listBallUsed.size()];
        arrBallValue = new String[listBallUsed.size()];

        //数值使用特殊的
        isUseSpecialValue = (layoutBean.getIsUseSpecialValue() == 1);
        if (isUseSpecialValue) {
            for (int i = 0; i < layoutBean.getBalls().size(); i++) {
                String ballValue = layoutBean.getBallBets().get(i);
                arrBallValue[i] = ballValue;
            }
        } else {
            for (int i = 0; i < layoutBean.getBalls().size(); i++) {
                String ballValue = layoutBean.getBalls().get(i);
                arrBallValue[i] = ballValue;
            }
        }

        //工具显示
        vgHotKey.setVisibility(layoutBean.isShowTools() ? View.VISIBLE : View.GONE);
        vKeyQuan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrBallSelected.length; i++) {
                    arrBallSelected[i] = true;
                }
                syncSelection();
                v.setSelected(true);
            }
        });
        vKeyDa.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrBallValue.length; i++) {
                    if (Strs.parse(arrBallValue[i].replace(",", ""), -1) > Strs.parse(arrBallValue[arrBallValue.length - 1].replace(",", ""), -1) / 2) {
                        arrBallSelected[i] = true;
                    } else {
                        arrBallSelected[i] = false;
                    }
                }
                syncSelection();
                v.setSelected(true);
            }
        });

        vKeyXiao.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrBallValue.length; i++) {
                    if (Strs.parse(arrBallValue[i].replace(",", ""), -1) <= Strs.parse(arrBallValue[arrBallValue.length - 1].replace(",", ""), -1) / 2) {
                        arrBallSelected[i] = true;
                    } else {
                        arrBallSelected[i] = false;
                    }
                }
                syncSelection();
                v.setSelected(true);
            }
        });

        vKeyJi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrBallValue.length; i++) {
                    if (Fomulars.isOdd(Strs.parse(arrBallValue[i].replace(",", ""), 10))) {
                        arrBallSelected[i] = true;
                    } else {
                        arrBallSelected[i] = false;
                    }
                }
                syncSelection();
                v.setSelected(true);
            }
        });

        vKeyOu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrBallValue.length; i++) {
                    if (Fomulars.isEven(Strs.parse(arrBallValue[i].replace(",", ""), 10))) {
                        arrBallSelected[i] = true;
                    } else {
                        arrBallSelected[i] = false;
                    }
                }
                syncSelection();
                v.setSelected(true);
            }
        });

        vKeyQing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrBallValue.length; i++) {
                    arrBallSelected[i] = false;
                }
                syncSelection();
                v.setSelected(true);
            }
        });
    }

    public boolean isUseSpecialValue() {
        return isUseSpecialValue;
    }


    protected String getPowerByTitle(String title) {
        String power = "";
        if (CtxLottery.POWER_WAN.equals(title)) {
            power = CtxLottery.POWER_WAN;
        } else if (CtxLottery.POWER_QIAN.equals(title)) {
            power = CtxLottery.POWER_QIAN;
        } else if (CtxLottery.POWER_BAI.equals(title)) {
            power = CtxLottery.POWER_BAI;
        } else if (CtxLottery.POWER_SHI.equals(title)) {
            power = CtxLottery.POWER_SHI;
        } else if (CtxLottery.POWER_GE.equals(title)) {
            power = CtxLottery.POWER_GE;
        }
        return power;
    }


    public void setBallSingleSelection() {
        for (int i = 0; i < listBallUsed.size(); i++) {
            listBallUsed.get(i).setOnClickListener(new OnClickListener() {
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
            listBallUsed.get(i).setOnClickListener(new OnClickListener() {
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
            listBallUsed.get(i).setOnClickListener(new OnClickListener() {
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
            TextView textView = listBallUsed.get(i);
            listBallUsed.get(i).setSelected(arrBallSelected[i]);
            if (arrBallSelected[i]) { //已选定状态
                textView.setTextColor(0xffff4746);
            } else {
                textView.setTextColor(0xff7a7a7a);
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
            for (TextView v : arrKeys) {
                v.setSelected(false);
            }

            if (onBallsChangeListener != null) {
                onBallsChangeListener.onBallsChanged(power, arrBallValue, getSelectedValues(), arrBallSelected, arrBallSelected.length);
            }
        }
    }

    public ArrayList<String> getSelectedValues() {
        ArrayList<String> listSelected = new ArrayList<>();
        for (int i = 0; i < arrBallSelected.length; i++) {
            if (arrBallSelected[i]) {
                listSelected.add(arrBallValue[i]);
            }
        }
        return listSelected;
    }

    public boolean[] getArrBallSelected() {
        return arrBallSelected;
    }

    public String[] getArrBallValue() {
        return arrBallValue;
    }

    public String getSingleSelectedValue() {
        String value = "";
        for (int i = 0; i < arrBallSelected.length; i++) {
            if (arrBallSelected[i]) {
                value = arrBallValue[i];
                break;
            }
        }
        return value;
    }

    public LotteryPlayUIConfig.LayoutBean getLayoutBean() {
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
        if (view != null) {
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
        return null;
    }

    private int getLastSelectBottom() {
        int last = 0;
        for (int i = 0; i < arrBallSelected.length; i++) {
            if (arrBallSelected[i])
                last = i;

        }

        View viewGroup = glDigits.getChildAt(last);
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
        void onBallsChanged(String power, String[] arrAllValue, ArrayList<String> selectedValue, boolean[] arrBallSelected, int ballCount);
    }
}
