package com.desheng.app.toucai.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.Strs;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 玩法数字
 * Created by lee on 2018/3/13.
 */
public class PlayDigitK3View extends PlayDigitView {
    private Map<String, Integer> specialDice = new HashMap<>();
    private int mode = 0;
    protected ArrayList<ImageView> listDiceUsed = new ArrayList<>();


    public PlayDigitK3View(@NonNull Context context) {
        super(context);
    }

    public PlayDigitK3View(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayDigitK3View(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        Context context = getContext();
        View inner = LayoutInflater.from(context).inflate(R.layout.view_lottery_play_digits_k3, this, false);
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

        specialDice.put("1", R.drawable.ic_dice_one);
        specialDice.put("2", R.drawable.ic_dice_two);
        specialDice.put("3", R.drawable.ic_dice_three);
        specialDice.put("4", R.drawable.ic_dice_four);
        specialDice.put("5", R.drawable.ic_dice_five);
        specialDice.put("6", R.drawable.ic_dice_six);
        specialDice.put("11", R.drawable.ic_dice_one_one);
        specialDice.put("22", R.drawable.ic_dice_two_two);
        specialDice.put("33", R.drawable.ic_dice_three_three);
        specialDice.put("44", R.drawable.ic_dice_four_four);
        specialDice.put("55", R.drawable.ic_dice_five_five);
        specialDice.put("66", R.drawable.ic_dice_six_six);
        specialDice.put("111", R.drawable.ic_dice_one_one_one);
        specialDice.put("222", R.drawable.ic_dice_two_two_two);
        specialDice.put("333", R.drawable.ic_dice_three_three_three);
        specialDice.put("444", R.drawable.ic_dice_four_four_four);
        specialDice.put("555", R.drawable.ic_dice_five_five_five);
        specialDice.put("666", R.drawable.ic_dice_six_six_six);
        specialDice.put("123", R.drawable.ic_dice_one_two_three);
        specialDice.put("234", R.drawable.ic_dice_two_three_four);
        specialDice.put("345", R.drawable.ic_dice_three_four_five);
        specialDice.put("456", R.drawable.ic_dice_four_five_six);
    }


    public void setConfig(LotteryPlayUIConfig.LayoutBean layoutBean) {
        this.layoutBean = layoutBean;
        this.power = getPowerByTitle(layoutBean.getTitle());

        //号码标题
        if (!Strs.isEmpty(layoutBean.getTitle())) {
            tvDigitPowerLab.setText(layoutBean.getTitle());
        } else {
            tvDigitPowerLab.setVisibility(GONE);
        }

        //球显示
        listBallUsed = new ArrayList<>();
        listDiceUsed = new ArrayList<>();

        glDigits.removeAllViews();

        if (layoutBean.getBalls().get(0).length() == 2) {
            glDigits.setColumnCount(3);
        } else if (layoutBean.getBalls().get(0).length() == 3) {
            glDigits.setColumnCount(6);
//            glDigits.setUseDefaultMargins(false);
        } else {
            glDigits.setColumnCount(6);
        }

        for (int i = 0; i < layoutBean.getBalls().size(); i++) {
            RelativeLayout digitVg = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_digit_dice, glDigits, false);
            TextView tvDigit = (TextView) digitVg.findViewById(R.id.tvDigit);
            tvDigit.setTag(i);
            ImageView imageView = digitVg.findViewById(R.id.iv_dice);
            switch (mode) {
                case 1: {
                    tvDigit.setVisibility(View.INVISIBLE);
                    tvDigit.setText("");
                    imageView.setImageResource(specialDice.get(layoutBean.getBalls().get(i)));
                }
                break;
                default:
                    tvDigit.setVisibility(View.VISIBLE);
                    tvDigit.setText(layoutBean.getBalls().get(i));
                    break;
            }
            glDigits.addView(digitVg);
            listBallUsed.add(tvDigit);
            listDiceUsed.add(imageView);
            imageView.setOnClickListener(v -> tvDigit.performClick());
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
        vgHotKey.setVisibility(View.GONE);
    }

    public void setMode(int mode) {
        this.mode = mode;
        for (int i = 0; i < listBallUsed.size(); i++) {
            TextView tvDigit = listBallUsed.get(i);
            switch (mode) {
                case 1: {
                    tvDigit.setText("");
                    tvDigit.setVisibility(INVISIBLE);
                    listDiceUsed.get(i).setImageResource(specialDice.get(layoutBean.getBalls().get(i)));
                }
                break;
                default:
                    tvDigit.setVisibility(VISIBLE);
                    tvDigit.setText(layoutBean.getBalls().get(i));
                    break;
            }
        }
    }

    @Override
    public void syncSelection() {
        super.syncSelection();
        for (int i = 0; i < arrBallSelected.length; i++) {
            ImageView imageView = listDiceUsed.get(i);
            listDiceUsed.get(i).setSelected(arrBallSelected[i]);
        }
    }
}
