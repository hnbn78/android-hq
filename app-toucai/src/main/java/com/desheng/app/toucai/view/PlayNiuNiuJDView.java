package com.desheng.app.toucai.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.Strs;
import com.ab.util.Views;
import com.desheng.base.model.LotteryPlayJD;
import com.desheng.base.model.LotteryPlayUserInfoJD;
import com.shark.tc.R;

import java.util.ArrayList;

/**
 * 经典玩法数字
 * Created by lee on 2018/3/13.
 */
public class PlayNiuNiuJDView extends PlayDigitsJDView {


    public PlayNiuNiuJDView(@NonNull Context context) {
        super(context);
    }

    public PlayNiuNiuJDView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayNiuNiuJDView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
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
            layoutParams.rightMargin = Views.dp2px(12);
        }
        glDigits.setLayoutParams(layoutParams);

        for (int i = 0; i < layoutBean.getBalls().size(); i++) {
            int resid = R.layout.view_digit_jd_rect;

            RelativeLayout digitVg = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_niuniu_jd, glDigits, false);
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
            glDigits.addView(digitVg);
        }

        glDigits.setColumnCount(5);
        arrBallSelected = new boolean[listBallUsed.size()];
        arrBallSelectedLast = new boolean[listBallUsed.size()];
    }

    @Override
    public void syncSelection() {
        super.syncSelection();
        for (int i = 0; i < arrBallSelected.length; i++) {
            TextView ballView = listBallUsed.get(i);
            TextView numView = listNumUsed.get(i);
            listNumBallGroup.get(i).setSelected(arrBallSelected[i]);
            listNumBallGroup.get(i).setBackgroundResource(R.drawable.bg_lottery_play_view_niuniu_jd_item);
        }
    }
}
