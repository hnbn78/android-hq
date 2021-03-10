package com.desheng.app.toucai.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.util.Strs;
import com.desheng.base.model.LotteryPlayJD;
import com.desheng.base.model.LotteryPlayUserInfoJD;
import com.shark.tc.R;

import java.util.ArrayList;

/**
 * 经典玩法数字
 * Created by lee on 2018/3/13.
 */
public class PlayBoardJDView extends FrameLayout {

    private TextView tvDigitPowerLab;
    private ConstraintLayout glDigits;


    private String power = "";
    private LotteryPlayJD.LayoutBean layoutBean;

    public static final int[] arrVgIds = new int[]{R.id.vgDigitCell0, R.id.vgDigitCell1, R.id.vgDigitCell2};
    public static final int[] arrNumBallsIds = new int[]{R.id.vgNumBalls0, R.id.vgNumBalls1, R.id.vgNumBalls2};
    public static final int[] arrNumIds = new int[]{R.id.tvNum0, R.id.tvNum1, R.id.tvNum2};
    public static final int[] arrBgIds = new int[]{R.id.tvBg0, R.id.tvBg1, R.id.tvBg2};
    public static final int[] arrBallIds = new int[]{R.id.tvBall0, R.id.tvBall1, R.id.tvBall2};

    //启用的组
    private ArrayList<ViewGroup> listNumBallGroup = new ArrayList<ViewGroup>();
    //启用的球数字列表
    private ArrayList<TextView> listNumUsed = new ArrayList<>();
    //启用的球值列表
    private ArrayList<TextView> listBallUsed = new ArrayList<>();
    private ArrayList<ImageView> listBgUsed = new ArrayList<>();
    //所有的ballid
    private int[] arrIds;

    //启用球的选择状态
    private boolean[] arrBallSelected;
    //上一次球的选中状态
    private boolean[] arrBallSelectedLast;

    private String[] arrNum;
    private String[] arrBall;

    //球选中状态变化监听
    private OnBallsChangeListener onBallsChangeListener;
    //显示模式
    private int mode_boll1_dual2;
    private String title;


    public PlayBoardJDView(@NonNull Context context) {
        super(context);
    }

    public PlayBoardJDView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayBoardJDView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void init() {
        Context context = getContext();
        View inner = LayoutInflater.from(context).inflate(R.layout.view_lottery_play_board_jd, this, false);
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

    public void setConfig(LotteryPlayJD.LayoutBean layoutBean, LotteryPlayUserInfoJD userInfoJD, int boll1_dual2) {
        this.layoutBean = layoutBean;
        mode_boll1_dual2 = boll1_dual2;
        //this.power = getPowerByTitle(layoutBean.getTitle());
        //号码标题
        this.title = layoutBean.getTitle();
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
        listNumUsed = new ArrayList<>();
        listBallUsed = new ArrayList<>();
        arrNum = new String[layoutBean.getBalls().size()];
        arrBall = new String[layoutBean.getBalls().size()];
        arrIds = new int[layoutBean.getBalls().size()];
        for (int i = 0; i < layoutBean.getBalls().size(); i++) {
            int pos = 0;
            if (Strs.isEqual(layoutBean.getNum().get(i), "龙")) {
                pos = 0;
            } else if (Strs.isEqual(layoutBean.getNum().get(i), "虎")) {
                pos = 2;
            } else if (Strs.isEqual(layoutBean.getNum().get(i), "和")) {
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
            arrNum[i] = layoutBean.getNum().get(i);
            tvNum.setText(arrNum[i]);
            listNumUsed.add(tvNum);

            ImageView iv = this.findViewById(arrBgIds[pos]);
            listBgUsed.add(iv);


            TextView tvBall = (TextView) this.findViewById(arrBallIds[pos]);
//            tvBall.setVisibility(View.VISIBLE);
            tvBall.setTag(i);
            arrIds[i] = Strs.parse(layoutBean.getBalls().get(i).replace("j", ""), 0);
            if (userInfoJD == null || userInfoJD.getObj() == null || userInfoJD.getObj().getLines() == null)
                arrBall[i] = layoutBean.getBalls().get(i);
            else
                arrBall[i] = userInfoJD.getObj().getLines().get(layoutBean.getBalls().get(i));
            tvBall.setText(arrBall[i]);
            listBallUsed.add(tvBall);
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
}
