package com.desheng.app.toucai.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.util.Dialogs;
import com.ab.view.MaterialDialog;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.shark.tc.R;

public class PictureCodeDialog {
    private MaterialDialog materialDialog;
    private Builder builder;


    PictureCodeDialog(Builder builder) {
        this.builder = builder;
    }

    public void onCreate() {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(builder.activity).inflate(R.layout.dialog_picture_verify_code, null);
        final TextView tvContent = viewGroup.findViewById(R.id.tvMessage);
        final TextView tvTitle = viewGroup.findViewById(R.id.tvTitle);
        final TextView tvFormat = viewGroup.findViewById(R.id.tvFormat);
        final EditText etCode = viewGroup.findViewById(R.id.etVerifyCode);
        Button btnNegative = viewGroup.findViewById(R.id.btnNegative);
        Button btnConfirm = viewGroup.findViewById(R.id.btnPositive);
        ImageView imgPicCode = viewGroup.findViewById(R.id.imgPicCode);
        tvTitle.setText(builder.title);
        tvContent.setText(builder.message);
        btnConfirm.setText(builder.positiveText);
        UserManagerTouCai.getIns().getVerifyImage(builder.activity,imgPicCode);
        btnNegative.setOnClickListener(v -> dismiss());
        btnConfirm.setOnClickListener(v -> {
            String code = etCode.getText().toString();
            if (TextUtils.isEmpty(code) || code.length() != 5 ) {
                tvFormat.setVisibility(View.VISIBLE);
                return;
            }
            if (builder.onPositiveClickListener != null)
                builder.onPositiveClickListener.onPositive(this,code);
        });
        materialDialog = Dialogs.showCustomDialog(builder.activity, viewGroup, true);
        materialDialog.setCanceledOnTouchOutside(builder.onTouchOutsideCancel);
        imgPicCode.setOnClickListener(v -> UserManagerTouCai.getIns().getVerifyImage(builder.activity,imgPicCode));
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
        private String title;
        private String message;
        private Activity activity;
        private OnPositiveClickListener onPositiveClickListener;

        private PictureCodeDialog tipDialog;
        private boolean onTouchOutsideCancel;


        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setPositiveButton(String positiveText, OnPositiveClickListener onPositiveClickListener) {
            this.positiveText = positiveText;
            this.onPositiveClickListener = onPositiveClickListener;
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

        public PictureCodeDialog create() {
            tipDialog = new PictureCodeDialog(this);
            tipDialog.onCreate();
            return tipDialog;
        }

        public void show() {
            if (tipDialog == null)
                create().show();
        }
    }

    public interface OnPositiveClickListener {
        void onPositive(PictureCodeDialog dialog, String code);
    }

}
