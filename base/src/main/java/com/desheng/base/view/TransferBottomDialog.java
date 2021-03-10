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
import android.widget.TextView;

import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.base.R;


public class TransferBottomDialog extends Dialog {
    private static OnConfirmListener callback;

    public TransferBottomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface OnConfirmListener {
        void onClick(String amount, String pwd);

        void onWalletClick(double amount, String pwd);
    }

    public static class Builder {
        private final Context context;
        private String transfer_in_name;
        private String transfer_out_name;
        private double transfer_in_amount;
        private double transfer_out_amount;

        public Builder(Context context) {
            this.context = context;
        }


        public void setTransfer_in_name(String transfer_in_name) {
            this.transfer_in_name = transfer_in_name;
        }

        public void setTransfer_out_name(String transfer_out_name) {
            this.transfer_out_name = transfer_out_name;
        }

        public void setTransfer_in_amount(double transfer_in_amount) {
            this.transfer_in_amount = transfer_in_amount;
        }

        public void setTransfer_out_amount(double transfer_out_amount) {
            this.transfer_out_amount = transfer_out_amount;
        }

        public Builder setOnConfirmClickListener(OnConfirmListener onConfirmListener) {
            callback = onConfirmListener;
            return this;
        }

        //: R.style.Theme_Light_NoTitle_NoShadow_Dialog
        public TransferBottomDialog create() {
            final TransferBottomDialog dialog = new TransferBottomDialog(context, R.style.Theme_Light_NoTitle_Dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_transfer, null);

            Button btn_confirm_transfer = view.findViewById(R.id.btn_confirm_transfer);
            TextView tv_transfer_in_amount = view.findViewById(R.id.tv_transfer_in_amount);
            final TextView tv_transfer_out_amount = view.findViewById(R.id.tv_transfer_out_amount);
            TextView tv_transfer_all = view.findViewById(R.id.tv_transfer_all);
            final EditText et_transfer = view.findViewById(R.id.et_transfer);
            TextView tv_title = view.findViewById(R.id.tv_title);
            TextView tv_transfer_in = view.findViewById(R.id.tv_transfer_in);
            final TextView et_transfer_pwd = view.findViewById(R.id.et_transfer_pwd);

            tv_transfer_in_amount.setText(Nums.formatDecimal(transfer_in_amount, 4));
            tv_transfer_out_amount.setText(String.valueOf(Math.floor(transfer_out_amount)));
            tv_title.setText("转入" + transfer_in_name);
            tv_transfer_in.setText(transfer_in_name);
            et_transfer.setHint("可转入金额" + Nums.formatDecimal(transfer_out_amount, 4));


            view.findViewById(R.id.ic_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            view.findViewById(R.id.tv_wallet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pwd = et_transfer_pwd.getText().toString().trim();
                    if (Strs.isNotEmpty(pwd)) {
                        callback.onWalletClick(transfer_in_amount, pwd);
                    } else if (Strs.isEmpty(pwd)) {
                        Toasts.show("请输入资金密码");
                    }

                }
            });

            btn_confirm_transfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pwd = et_transfer_pwd.getText().toString().trim();
                    String money = et_transfer.getText().toString();
                    if (Strs.isNotEmpty(money) && Strs.isNotEmpty(pwd)) {
                        callback.onClick(money, pwd);
                    } else if (Strs.isEmpty(pwd)) {
                        Toasts.show("请输入资金密码");
                    } else if (Strs.isEmpty(money)) {
                        Toasts.show("请输入金额");
                    }
                }
            });

            tv_transfer_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_transfer.setText(tv_transfer_out_amount.getText().toString());
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
