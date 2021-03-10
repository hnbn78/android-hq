package com.ab.util;

import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ab.global.Global;

public class InputUtils {
 
	public static void toggleKeyboard() {
		InputMethodManager imm = (InputMethodManager) Global.app.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
    
    public static void showKeyboard(View anchor) {
        if (anchor != null) {
            InputMethodManager imm = (InputMethodManager) Global.app.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(anchor, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

	public static void hideKeyboard(View anchor) {
		if (anchor != null) {
			InputMethodManager imm = (InputMethodManager) Global.app.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(anchor.getWindowToken(), 0); // 强制隐藏键盘
		}
	}

	public static InputFilter getLengthInputFilter(int maxLen) {
		return new InputFilter.LengthFilter(maxLen);
	}
}
