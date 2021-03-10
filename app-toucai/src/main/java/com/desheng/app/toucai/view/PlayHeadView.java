package com.desheng.app.toucai.view;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.util.Nums;
import com.shark.tc.R;

/**
 * 玩法头
 * Created by lee on 2018/3/13.
 */
public class PlayHeadView extends FrameLayout {
    private TextView tvMoney;
    private OnShakeForAutoGenerate onShakeForAutoGenerate;
    private OnInstructionClickListener onInstructionClickListener;
    private MySensorEventListener sensorListener;
    private ImageView ivAuSelect;
    private TextView tv_instruction, tvWanfaContent;

    public PlayHeadView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PlayHeadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayHeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        View inner = LayoutInflater.from(context).inflate(R.layout.view_lottery_play_head, this, false);
        tvMoney = (TextView) inner.findViewById(R.id.tvMoney);
        ivAuSelect = (ImageView) inner.findViewById(R.id.ivAuSelect);
        tv_instruction = (TextView) inner.findViewById(R.id.tv_instruction);
        tvWanfaContent = (TextView) inner.findViewById(R.id.tvWanfaContent);
        sensorListener = new MySensorEventListener();
        /**
         * 手动点击震动
         */
        ivAuSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shakeFormAuto();
            }
        });
        ivAuSelect.post(new Runnable() {
            @Override
            public void run() {
                ivAuSelect.requestFocus();
            }
        });
        inner.findViewById(R.id.tv_instruction).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onInstructionClickListener != null)
                    onInstructionClickListener.onInstructionClick();
            }
        });
        this.addView(inner);
    }

    public void setShakeVisible(boolean isVisible) {
        //ivAuSelect.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setMoney(double money) {
        String string = String.format("<font color=#ff2b65>%s</font>", Nums.formatDecimal(money, 3));
        tvMoney.setText(Html.fromHtml(string));
    }

    public void setMoney(String money) {
        String string = String.format("<font color=#ff2b65>%s</font>", Nums.formatDecimal(money, 3));
        tvMoney.setText(Html.fromHtml(string));
    }

    public void registShake(Context ctx, OnShakeForAutoGenerate onShakeForAutoGenerate) {
        SensorManager manager = (SensorManager) ctx.getSystemService(Service.SENSOR_SERVICE);
        manager.registerListener(sensorListener,
                manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        this.onShakeForAutoGenerate = onShakeForAutoGenerate;
    }

    public void unregistShake(Context ctx) {
        SensorManager manager = (SensorManager) ctx.getSystemService(Service.SENSOR_SERVICE);
        manager.unregisterListener(sensorListener);
        this.onShakeForAutoGenerate = null;
    }

    public TextView getHeadBounsText() {
        return tvMoney;
    }

    public void setTvInstructionStatus(int visibility) {
        if (tv_instruction == null) {
            return;
        }
        tv_instruction.setVisibility(visibility);
    }


    public class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            //获取传感器类型
            int sensorType = event.sensor.getType();
            //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
            float[] values = event.values;
            //如果传感器类型为加速度传感器，则判断是否为摇一摇
            if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math
                        .abs(values[2]) > 17)) {
                    shakeFormAuto();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public void shakeFormAuto() {
        if (onShakeForAutoGenerate != null) {
            onShakeForAutoGenerate.onAutoGenerate();
        }
    }

    public OnShakeForAutoGenerate getOnShakeForAutoGenerate() {
        return onShakeForAutoGenerate;
    }

    public void setOnShakeForAutoGenerate(OnShakeForAutoGenerate onShakeForAutoGenerate) {
        this.onShakeForAutoGenerate = onShakeForAutoGenerate;
    }

    public void setOnInstructionClickListener(OnInstructionClickListener onInstructionClickListener) {
        this.onInstructionClickListener = onInstructionClickListener;
    }

    public void setWanfaShuomingContent(String content) {
        if (tvWanfaContent == null) {
            return;
        }
        tvWanfaContent.setText(content);
    }

    public interface OnShakeForAutoGenerate {
        void onAutoGenerate();
    }

    public interface OnInstructionClickListener {
        void onInstructionClick();
    }
}
