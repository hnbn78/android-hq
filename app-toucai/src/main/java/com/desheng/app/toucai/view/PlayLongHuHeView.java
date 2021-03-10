package com.desheng.app.toucai.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ab.util.Strs;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.shark.tc.R;

import java.util.ArrayList;

/**
 * 玩法数字
 * Created by lee on 2018/3/13.
 */
public class PlayLongHuHeView extends FrameLayout {

    private TextView tvDigitPowerLab;
    private View glDigits;

    public static final int[] arrVgIds = new int[]{R.id.vgDigitCell0, R.id.vgDigitCell1, R.id.vgDigitCell2};
    public static final int[] arrNumBallsIds = new int[]{R.id.vgNumBalls0, R.id.vgNumBalls1, R.id.vgNumBalls2};
    public static final int[] arrNumIds = new int[]{R.id.tvNum0, R.id.tvNum1, R.id.tvNum2};
    public static final int[] arrBallIds = new int[]{R.id.tvBall0, R.id.tvBall1, R.id.tvBall2};

    private String power = "";
    private LotteryPlayUIConfig.LayoutBean layoutBean;
    //启用的组
    private ArrayList<ViewGroup> listNumBallGroup = new ArrayList<ViewGroup>();
    //启用的球数字列表
    private ArrayList<TextView> listNumUsed = new ArrayList<>();
    //启用的球列表
    private ArrayList<TextView> listBallUsed = new ArrayList<>();
    //启用球的选择状态
    private boolean[] arrBallSelected;
    //上一次球的选中状态
    private boolean[] arrBallSelectedLast;
    //启用球的值
    private String[] arrBallValue;
    //是否特殊
    private boolean isUseSpecialValue;
    //球选中状态变化监听
    private OnBallsChangeListener onBallsChangeListener;

    public PlayLongHuHeView(@NonNull Context context) {
        super(context);
    }

    public PlayLongHuHeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayLongHuHeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void init() {
        Context context = getContext();
        View inner = LayoutInflater.from(context).inflate(R.layout.view_lottery_play_long_hu_he, this, false);
        tvDigitPowerLab = (TextView) inner.findViewById(R.id.tvDigitPowerLab);
        glDigits = inner.findViewById(R.id.glDigits);

        this.addView(inner);
    }

    public void showDragon() {
        findViewById(R.id.iv_long).setVisibility(VISIBLE);
        findViewById(R.id.iv_hu).setVisibility(INVISIBLE);
    }

    public void showTiger() {
        findViewById(R.id.iv_long).setVisibility(INVISIBLE);
        findViewById(R.id.iv_hu).setVisibility(VISIBLE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setConfig(LotteryPlayUIConfig.LayoutBean layoutBean) {
        this.layoutBean = layoutBean;
        this.power = getPowerByTitle(layoutBean.getTitle());
        //号码标题
        String title = layoutBean.getTitle();
        try {
            String[] substrs = title.split("VS");
            tvDigitPowerLab.setText(substrs[0]);
            SpannableString vs = new SpannableString("VS");
            vs.setSpan(new ForegroundColorSpan(0x917a7a7a), 0, vs.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvDigitPowerLab.append("\n");
            tvDigitPowerLab.append(vs);
            tvDigitPowerLab.append("\n");
            tvDigitPowerLab.append(substrs[1]);
        } catch (Exception e) {
            tvDigitPowerLab.setText(title);
            e.printStackTrace();
        }

        //球显示
        listBallUsed = new ArrayList<>();
        for (int i = 0; i < layoutBean.getBalls().size(); i++) {
            int pos = 0;
            if (Strs.isEqual(layoutBean.getBalls().get(i), "龙")) {
                pos = 0;
            } else if (Strs.isEqual(layoutBean.getBalls().get(i), "虎")) {
                pos = 2;
            } else if (Strs.isEqual(layoutBean.getBalls().get(i), "和")) {
                pos = 1;
            } else {
                continue;
            }

            this.findViewById(arrVgIds[pos]).setVisibility(View.VISIBLE);
            ViewGroup vgNumBalls = (ViewGroup) this.findViewById(arrNumBallsIds[pos]);
            vgNumBalls.setTag(i);
            listNumBallGroup.add(vgNumBalls);
            vgNumBalls.setVisibility(View.VISIBLE);

            TextView tvNum = (TextView) this.findViewById(arrNumIds[pos]);
            tvNum.setVisibility(View.VISIBLE);
            tvNum.setTag(i);
            tvNum.setText(layoutBean.getBalls().get(i));
            listNumUsed.add(tvNum);

            TextView tvBall = (TextView) this.findViewById(arrBallIds[pos]);
//            tvBall.setVisibility(View.VISIBLE);
            tvBall.setTag(i);
            tvBall.setText(layoutBean.getBallBets().get(i));
            listBallUsed.add(tvBall);
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

    }

    public boolean isUseSpecialValue() {
        return isUseSpecialValue;
    }


    private String getPowerByTitle(String title) {
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
            listNumBallGroup.get(i).setSelected(arrBallSelected[i]);
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
                for (String s : getSelectedValues()) {
                }
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
        void onBallsChanged(String power, String[] arrAllValue, ArrayList<String> selectedValue, boolean[] arrBallSelected, int ballCount);
    }
}
