package com.desheng.base.util;

import android.os.CountDownTimer;
import android.widget.Button;

import com.desheng.base.R;

public class CountDownTimerUtil {

    private static String until;
    /**
     *
     * @param btn Button
     * @param millisInFuture 总倒计时
     * @return CountDownTimer
     */
    public static CountDownTimer countDown(final Button btn, final long millisInFuture) {
        return new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btn.setEnabled(false);
                until = millisUntilFinished / 1000 + " s";
                btn.setText(until);
            }

            @Override
            public void onFinish() {
                btn.setEnabled(true);
                btn.setText(R.string.resend);
            }
        }.start();
    }

}
