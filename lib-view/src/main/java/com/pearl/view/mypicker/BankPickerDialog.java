package com.pearl.view.mypicker;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by o on 2018/4/4.
 */

public class BankPickerDialog extends Dialog {
    private DataPickerDialog.Params params;
    public BankPickerDialog(@NonNull Context context) {
        super(context);
    }





    private static final class Params {
        private boolean shadow = true;
        private boolean canCancel = true;
        private LoopView loopData;
        private String title;
        private String unit;
        private int initSelection;
        private DataPickerDialog.OnDataSelectedListener callback;
        private final List<String> dataList = new ArrayList<>();
    }

    public static class Builder {
        private final Context context;
        private final DataPickerDialog.Params params;

        public Builder(Context context) {
            this.context = context;
            params = new DataPickerDialog.Params();
        }


    }
}
