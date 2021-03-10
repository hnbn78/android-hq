package com.desheng.base.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.util.Dialogs;
import com.ab.view.MaterialDialog;
import com.desheng.base.R;

/**
 * 验证码弹窗
 */
public class GetCodeDialog {

    private MaterialDialog materialDialog;
    private Builder builder;
    private final long INTERVAL = 1000;
    private Button btnGetCode;
    private String downText;
    private View vgPicCode;

    private CountDownTimer countDownTimer = new CountDownTimer(INTERVAL * 60, INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
            downText = (millisUntilFinished / 1000) + "s";
            btnGetCode.setText(downText);
        }

        @Override
        public void onFinish() {
            btnGetCode.setText(R.string.resend);
            btnGetCode.setEnabled(true);
        }
    };

    GetCodeDialog(Builder builder) {
        this.builder = builder;
    }

    public void onCreate() {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(builder.activity).inflate(R.layout.dialog_get_verify_code, null);
        final TextView tvContent = viewGroup.findViewById(R.id.tvMessage);
        final TextView tvTitle = viewGroup.findViewById(R.id.tvTitle);
        final TextView tvFormat = viewGroup.findViewById(R.id.tvFormat);
        final EditText etCode = viewGroup.findViewById(R.id.etVerifyCode);
        Button btnNegative = viewGroup.findViewById(R.id.btnNegative);
        Button btnConfirm = viewGroup.findViewById(R.id.btnPositive);
        btnGetCode = viewGroup.findViewById(R.id.btnGetCode);
        vgPicCode = viewGroup.findViewById(R.id.vgPicCode);
        tvTitle.setText(builder.title);
        tvContent.setText(builder.message);
        btnNegative.setText(builder.negativeText);
        btnConfirm.setText(builder.positiveText);
        btnGetCode.setText(builder.getCodeText);

        if(builder.drawableRes != 0)
            tvTitle.setBackgroundResource(builder.drawableRes);
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder.onNegativeClickListener != null)
                    builder.onNegativeClickListener.onNegative(GetCodeDialog.this);
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etCode.getText().toString();
                if (TextUtils.isEmpty(code) || code.length() < 6) {
                    tvFormat.setVisibility(View.VISIBLE);
                    return;
                }
                if (builder.onPositiveClickListener != null)
                    builder.onPositiveClickListener.onPositive(GetCodeDialog.this, code);
            }
        });
        btnGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder.onGetCodeClickListener != null)
                    builder.onGetCodeClickListener.onGetCodeClick();
                /* 倒计时开始，并不能点击*/
                btnGetCode.setEnabled(false);
                countDownTimer.start();
            }
        });

        materialDialog = Dialogs.showCustomDialog(builder.activity, viewGroup, true);
        materialDialog.setCanceledOnTouchOutside(builder.onTouchOutsideCancel);
    }

    public void cancel() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        dismiss();
    }

    public void showPicCodeView(){
        vgPicCode.setVisibility(View.VISIBLE);
    }

    public void show() {
        materialDialog.show();
    }

    public void dismiss() {
        if (materialDialog != null)
            materialDialog.dismiss();
    }


    public static class Builder {
        private String positiveText;
        private String negativeText;
        private String getCodeText;
        private String title;
        private String message;
        private Activity activity;
        private boolean enable;
        private OnPositiveClickListener onPositiveClickListener;
        private OnNegativeClickListener onNegativeClickListener;
        private OnGetCodeClickListener onGetCodeClickListener;

        private GetCodeDialog tipDialog;
        private boolean onTouchOutsideCancel;
        private int drawableRes;


        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setGetCodeButtonEnable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public Builder setTitleBackground(int drawableRes) {
            this. drawableRes =  drawableRes;
            return this;
        }

        public Builder setGetCodeButtonText(String getCodeText) {
            this.getCodeText = getCodeText;
            return this;
        }

        public Builder setGetCodeButton(String getCodeText, OnGetCodeClickListener onGetCodeClickListener) {
            this.getCodeText = getCodeText;
            this.onGetCodeClickListener = onGetCodeClickListener;
            return this;
        }

        public Builder setPositiveButton(String positiveText, OnPositiveClickListener onPositiveClickListener) {
            this.positiveText = positiveText;
            this.onPositiveClickListener = onPositiveClickListener;
            return this;
        }

        public Builder setNegativeButton(String negativeText, OnNegativeClickListener onNegativeClickListener) {
            this.negativeText = negativeText;
            this.onNegativeClickListener = onNegativeClickListener;
            return this;
        }

        public Builder setOnTouchOutsideCancel(boolean onTouchOutsideCancel) {
            this.onTouchOutsideCancel = onTouchOutsideCancel;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public GetCodeDialog create() {
            tipDialog = new GetCodeDialog(this);
            tipDialog.onCreate();
            return tipDialog;
        }

        public void show() {
            if (tipDialog == null)
                create().show();
        }
    }

    public interface OnPositiveClickListener {
        void onPositive(GetCodeDialog dialog, String code);
    }

    public interface OnNegativeClickListener {
        void onNegative(GetCodeDialog dialog);
    }

    public interface OnGetCodeClickListener {
        void onGetCodeClick();
    }

}
