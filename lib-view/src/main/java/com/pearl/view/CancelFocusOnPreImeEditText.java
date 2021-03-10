package com.pearl.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class CancelFocusOnPreImeEditText extends android.support.v7.widget.AppCompatEditText {
    public CancelFocusOnPreImeEditText(Context context) {
        super(context);
    }

    public CancelFocusOnPreImeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CancelFocusOnPreImeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            setFocusable(false);
            setFocusableInTouchMode(true);
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
