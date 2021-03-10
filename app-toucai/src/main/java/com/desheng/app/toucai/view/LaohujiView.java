package com.desheng.app.toucai.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ab.util.Strs;
import com.desheng.app.toucai.adapter.WheelAdapter;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.util.ScreenUtils;
import com.shark.tc.R;
import com.wx.wheelview.widget.WheelView;

import java.util.Arrays;

public class LaohujiView extends LinearLayout {

    private View rootview;
    Context mContext;

    public LaohujiView(Context context) {
        this(context, null);
    }

    public LaohujiView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public LaohujiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        rootview = LayoutInflater.from(context).inflate(R.layout.layout_laohuji, this);
        initview();
    }

    WheelView wheelView1;
    WheelView wheelView2;
    WheelView wheelView3;
    WheelView wheelView4;
    WheelView wheelView5;

    private void initview() {
        wheelView1 = (WheelView) rootview.findViewById(R.id.wheel1);
        wheelView2 = (WheelView) rootview.findViewById(R.id.wheel2);
        wheelView3 = (WheelView) rootview.findViewById(R.id.wheel3);
        wheelView4 = (WheelView) rootview.findViewById(R.id.wheel4);
        wheelView5 = (WheelView) rootview.findViewById(R.id.wheel5);

        wheelView1.setWheelAdapter(new WheelAdapter(mContext));
        wheelView2.setWheelAdapter(new WheelAdapter(mContext));
        wheelView3.setWheelAdapter(new WheelAdapter(mContext));
        wheelView4.setWheelAdapter(new WheelAdapter(mContext));
        wheelView5.setWheelAdapter(new WheelAdapter(mContext));


        wheelView1.setWheelData(Arrays.asList(Consitances.nums));
        wheelView2.setWheelData(Arrays.asList(Consitances.nums));
        wheelView3.setWheelData(Arrays.asList(Consitances.nums));
        wheelView4.setWheelData(Arrays.asList(Consitances.nums));
        wheelView5.setWheelData(Arrays.asList(Consitances.nums));
        wheelView1.setLoop(true);
        wheelView2.setLoop(true);
        wheelView3.setLoop(true);
        wheelView4.setLoop(true);
        wheelView5.setLoop(true);

        wheelView1.setSelection(1);
        wheelView2.setSelection(1);
        wheelView3.setSelection(1);
        wheelView4.setSelection(1);
        wheelView5.setSelection(1);

        //startlaohuji(1, 3, 5, 7, 9);
    }

    public void startlaohuji(String code) {
        if (Strs.isEmpty(code)) {
            return;
        }

        char[] numbers = code.toCharArray();
        int num1, num2, num3, num4, num5;
        num1 = Integer.parseInt(String.valueOf(numbers[0]));
        num2 = Integer.parseInt(String.valueOf(numbers[1]));
        num3 = Integer.parseInt(String.valueOf(numbers[2]));
        num4 = Integer.parseInt(String.valueOf(numbers[3]));
        num5 = Integer.parseInt(String.valueOf(numbers[4]));

        rootview.postDelayed(new Runnable() {
            @Override
            public void run() {
                wheelView1.smoothScrollBy(((int) ScreenUtils.dpToPixel(25)) * (20 + num1), 6000);
            }
        }, 1000);

        rootview.postDelayed(new Runnable() {
            @Override
            public void run() {
                wheelView2.smoothScrollBy(((int) ScreenUtils.dpToPixel(25)) * (20 + num2), 6000);
            }
        }, 1300);

        rootview.postDelayed(new Runnable() {
            @Override
            public void run() {
                wheelView3.smoothScrollBy(((int) ScreenUtils.dpToPixel(25)) * (20 + num3), 6000);
            }
        }, 1600);

        rootview.postDelayed(new Runnable() {
            @Override
            public void run() {
                wheelView4.smoothScrollBy(((int) ScreenUtils.dpToPixel(25)) * (20 + num4), 6000);
            }
        }, 1900);

        rootview.postDelayed(new Runnable() {
            @Override
            public void run() {
                wheelView5.smoothScrollBy(((int) ScreenUtils.dpToPixel(25)) * (20 + num5), 6000);
            }
        }, 2200);
    }
}
