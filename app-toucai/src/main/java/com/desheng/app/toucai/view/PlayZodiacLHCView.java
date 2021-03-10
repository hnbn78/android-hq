package com.desheng.app.toucai.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.desheng.base.model.LotteryPlayLHCUI;
import com.desheng.base.model.LotteryPlayUserInfoLHC;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 经典玩法数字
 * Created by lee on 2018/3/13.
 */
public class PlayZodiacLHCView extends LinearLayout {


    private String power = "";
    private List<LotteryPlayLHCUI.CellUIBean> layoutBeans;

    public static final int[] arrVgIds = new int[]{R.id.vgDigitCell0, R.id.vgDigitCell1, R.id.vgDigitCell2};
    public static final int[] arrNumBallsIds = new int[]{R.id.vgNumBalls0, R.id.vgNumBalls1, R.id.vgNumBalls2};
    public static final int[] arrNumIds = new int[]{R.id.tvNum0, R.id.tvNum1, R.id.tvNum2};
    public static final int[] arrBallIds = new int[]{R.id.tvBall0, R.id.tvBall1, R.id.tvBall2};

    //启用的组
    private ArrayList<PlayZodiacLHCItemView> listNumBallGroup = new ArrayList<>();

    //启用球的选择状态
    private boolean[] arrBallSelected;
    //上一次球的选中状态
    private boolean[] arrBallSelectedLast;


    //球选中状态变化监听
    private OnBallsChangeListener onBallsChangeListener;

    public PlayZodiacLHCView(@NonNull Context context) {
        super(context);
    }

    public PlayZodiacLHCView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayZodiacLHCView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void init() {
        setOrientation(VERTICAL);
    }


    public void setConfig(List<LotteryPlayLHCUI.CellUIBean> layoutBeans, LotteryPlayUserInfoLHC userInfoLHC) {
        removeAllViews();
        listNumBallGroup.clear();
        this.layoutBeans = layoutBeans;
        //this.power = getPowerByTitle(layoutBean.getTitle());
        arrBallSelected = new boolean[layoutBeans.size()];
        arrBallSelectedLast = new boolean[layoutBeans.size()];


        for (int i = 0; i < layoutBeans.size(); i++) {
            LotteryPlayLHCUI.CellUIBean bean = layoutBeans.get(i);
            PlayZodiacLHCItemView view = new PlayZodiacLHCItemView(getContext());
            view.setUIConfig(bean, userInfoLHC);
            addView(view);
            view.setTag(i);
            listNumBallGroup.add(view);
        }
    }


    public void setBallSingleSelection() {
        for (int i = 0; i < layoutBeans.size(); i++) {
            listNumBallGroup.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    if (arrBallSelected[index]) { //已选定状态, 取消选择
                        arrBallSelected[index] = false;
                    } else {
                        for (int j = 0; j < layoutBeans.size(); j++) {
                            arrBallSelected[j] = false;
                        }
                        arrBallSelected[index] = true;
                    }
                    syncSelection();

                }
            });
        }
    }

    public void setBallMonoSelection() {
        for (int i = 0; i < layoutBeans.size(); i++) {
            listNumBallGroup.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    if (arrBallSelected[index]) { //已选定状态, 取消选择
                        for (int j = 0; j < layoutBeans.size(); j++) {
                            arrBallSelected[j] = false;
                        }
                    } else {
                        for (int j = 0; j < layoutBeans.size(); j++) {
                            arrBallSelected[j] = true;
                        }
                    }
                    syncSelection();
                }
            });
        }
    }

    public void setBallMultiSelection() {
        for (int i = 0; i < layoutBeans.size(); i++) {
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
        int count = 0;
        for (int i = 0; i < arrBallSelected.length; i++) {
            if (arrBallSelected[i])
                count++;
        }

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
                onBallsChangeListener.onBallsChanged(power, getSelectedNum(), getSelectedBalls(), arrBallSelected, arrBallSelected.length);
            }
        }
    }

//    public ArrayList<String> getListBalls() {
//        ArrayList<String> listSelected = new ArrayList<>();
//        for (int i = 0; i < layoutBeans.size(); i++) {
//            listSelected.add(arrBall[i]);
//        }
//        return listSelected;
//    }
//
//    public ArrayList<String> getListNum() {
//        ArrayList<String> listSelected = new ArrayList<>();
//        for (int i = 0; i < layoutBeans.size(); i++) {
//            listSelected.add(arrNum[i]);
//        }
//        return listSelected;
//    }
//
    public ArrayList<String> getSelectedBalls() {
        ArrayList<String> listSelected = new ArrayList<>();
        for (int i = 0; i < arrBallSelected.length; i++) {
            if (arrBallSelected[i]) {
                listSelected.addAll(layoutBeans.get(i).getShowNums());
            }
        }
        return listSelected;
    }

    public ArrayList<String> getSelectedNum() {
        ArrayList<String> listSelected = new ArrayList<>();
        for (int i = 0; i < arrBallSelected.length; i++) {
            if (arrBallSelected[i]) {
                listSelected.add(layoutBeans.get(i).getLhcID());
            }
        }
        return listSelected;
    }


    public boolean[] getArrBallSelected() {
        return arrBallSelected;
    }

//    public int[] getArrIds() {
//        return arrIds;
//    }

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
            if (getLastSelectBottom() > parent.getMeasuredHeight()) {
                allCount.add(getLastSelectBottom() - parent.getMeasuredHeight());
            }
            return view;
        } else {
            allCount.add(view.getTop());
            return innerGetParentNestedInScrollView(parent, allCount);
        }
    }

    private int     getLastSelectBottom() {
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
}
