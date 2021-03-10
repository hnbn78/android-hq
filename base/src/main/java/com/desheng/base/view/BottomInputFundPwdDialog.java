package com.desheng.base.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.desheng.base.R;


public class BottomInputFundPwdDialog extends Dialog {
    private static OnConfirmListener callback;

    public BottomInputFundPwdDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface OnConfirmListener {
         void onClick(String pwd);
    }

    public static class Builder {
        private final Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOnConfirmClickListener(OnConfirmListener onConfirmListener) {
            callback = onConfirmListener;
            return this;
        }

        public BottomInputFundPwdDialog create() {
            final BottomInputFundPwdDialog dialog = new BottomInputFundPwdDialog(context,  R.style.Theme_Light_NoTitle_Dialog );
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_input_pwd, null);

            Button btn_confirm_transfer=view.findViewById(R.id.btn_confirm);
            final EditText et_pwd = view.findViewById(R.id.et_pwd);

            view.findViewById(R.id.ic_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btn_confirm_transfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClick(et_pwd.getText().toString());
                }
            });

            Window win = dialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.Animation_Bottom_Rising);

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);

            return dialog;
        }

    }
}
