package com.desheng.app.toucai.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.Strs;
import com.ab.util.Views;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.desheng.base.model.LotteryPlayUserInfoLHC;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 经典玩法数字
 * Created by lee on 2018/3/13.
 */
public class PlayDigitsLHCView extends FrameLayout {
    
    private GridLayout glDigits;
    
    
    private String power = "";
    private LotteryPlayLHCUI.CellUIBean layoutBean;
    
    //启用的组
    private ArrayList<ViewGroup> listNumBallGroup = new ArrayList<ViewGroup>();
    //启用的球数字列表
    private ArrayList<TextView> listNumUsed = new ArrayList<>();
    //启用的球值列表
    private ArrayList<TextView> listBallUsed = new ArrayList<>();
    //所有的ballid
    private int[] arrIds;
    
    //启用球的选择状态
    private boolean[] arrBallSelected;
    //上一次球的选中状态
    private boolean[] arrBallSelectedLast;
    
    private String[] arrNum;
    private String[] arrBall;
    private int[] arrColor;

    private int[] colorRes = {
            R.drawable.ic_bg_lhc_play_digit_num_unselected,
            R.drawable.ic_bg_lhc_play_digit_num_red,
            R.drawable.ic_bg_lhc_play_digit_num_blue,
            R.drawable.ic_bg_lhc_play_digit_num_green,
    };

//    private int bgs[] = {
//            R.mipmap.ic_lhc_digits_selected,
//            R.mipmap.ic_lhc_digits_red,
//            R.mipmap.ic_lhc_digits_blue,
//            R.mipmap.ic_lhc_digits_red_green,
//    };

    private int[] textColor = {
            0xFFFFFFFF,
            0xFFF14A40,
            0xFF2D7FDA,
            0xFF47A33E,
    };

    //球选中状态变化监听
    private OnBallsChangeListener onBallsChangeListener;
    private String title;
    
    
    public PlayDigitsLHCView(@NonNull Context context) {
        super(context);
    }
    
    public PlayDigitsLHCView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    
    public PlayDigitsLHCView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    
    public void init() {
        Context context = getContext();
        RelativeLayout inner = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.view_lottery_play_digits_lhc, this, false);
        glDigits = (GridLayout) inner.findViewById(R.id.glDigits);
        
        
        this.addView(inner);
    }
    
    
    public void setConfig(LotteryPlayLHCUI.CellUIBean layoutBean, LotteryPlayUserInfoLHC userInfoLHC) {
        this.layoutBean = layoutBean;
        //this.power = getPowerByTitle(layoutBean.getTitle());
        //号码标题
        this.title = layoutBean.getGname();
        //球显示
        listNumUsed = new ArrayList<>();
        listBallUsed = new ArrayList<>();
        arrNum = new String[layoutBean.getShowNums().size()];
        arrBall = new String[layoutBean.getShowNums().size()];
        arrIds = new int[layoutBean.getShowNums().size()];
        arrColor = new int[layoutBean.getShowNums().size()];

        glDigits.removeAllViews();
        listNumBallGroup.clear();
        Pattern pattern = Pattern.compile("@[\\d\\.]+");
        for (int i = 0; i < layoutBean.getShowNums().size(); i++) {
            RelativeLayout digitVg = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_digit_lhc, glDigits, false);
            ViewGroup vgNumBalls = (ViewGroup) digitVg.findViewById(R.id.vgNumBalls);
            vgNumBalls.setTag(i);
            listNumBallGroup.add(vgNumBalls);

            arrColor[i] = layoutBean.getShowColors().get(i);

            TextView tvNum = (TextView) digitVg.findViewById(R.id.tvNum);
            tvNum.setVisibility(View.VISIBLE);
            tvNum.setTag(i);
            arrNum[i] = layoutBean.getShowNums().get(i);
            tvNum.setText(arrNum[i]);
            listNumUsed.add(tvNum);
            
            TextView tvBall = (TextView) digitVg.findViewById(R.id.tvBall);
            tvBall.setTag(i);
            arrIds[i] = Strs.parse(layoutBean.getLhcID(), 0);
            if(userInfoLHC == null || userInfoLHC.getObj() == null || userInfoLHC.getObj().getLines() == null){
                arrBall[i] = layoutBean.getLhcIDReq();
            }else{
                String line = userInfoLHC.getObj().getLines().get(layoutBean.getLhcIDReq());
                Matcher matcher = null;
                if (!Strs.isEmpty(line)) {
                    matcher = pattern.matcher(line);
                }
                String str = null;
                if(matcher != null && matcher.find()) {
                    arrBall[i] = matcher.group().replace("@", "");
                } else {
                    arrBall[i] = layoutBean.getLhcIDReq();
                }
            }
            tvBall.setText(arrBall[i]);
            listBallUsed.add(tvBall);
            vgNumBalls.setBackgroundResource(colorRes[0]);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(Views.dp2px(14));
            drawable.setStroke(Views.dp2px(1.5f),textColor[arrColor[i]]);
            tvNum.setBackgroundDrawable(drawable);

            tvNum.setTextColor(textColor[arrColor[i]]);
            tvBall.setTextColor(Color.GRAY);
            ViewGroup.LayoutParams param = vgNumBalls.getLayoutParams();
            param.height = Views.dp2px(35);
            param.width = Views.dp2px(140);
            glDigits.addView(digitVg);
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
            if (arrBallSelected[i]) { //已选定状态
                listNumBallGroup.get(i).setBackgroundResource(colorRes[arrColor[i]]);
                ballView.setTextColor(textColor[0]);
                numView.setTextColor(textColor[0]);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(Views.dp2px(14));
                drawable.setStroke(Views.dp2px(1.5f),textColor[0]);
                numView.setBackgroundDrawable(drawable);
            } else {
                listNumBallGroup.get(i).setBackgroundResource(colorRes[0]);
                ballView.setTextColor(Color.GRAY);
                numView.setTextColor(textColor[arrColor[i]]);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(Views.dp2px(14));
                drawable.setStroke(Views.dp2px(1.5f),textColor[arrColor[i]]);
                numView.setBackgroundDrawable(drawable);
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

    public String getTitle(){
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

    public LotteryPlayLHCUI.CellUIBean getLayoutBean() {
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

        ViewGroup viewGroup = listNumBallGroup.get(last);
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
