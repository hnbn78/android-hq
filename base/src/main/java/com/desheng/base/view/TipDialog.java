package com.desheng.base.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ab.util.Dialogs;
import com.ab.view.MaterialDialog;
import com.desheng.base.R;

public class TipDialog {

    private MaterialDialog materialDialog;
    private Builder builder;

    TipDialog(Builder builder) {
        this.builder = builder;
    }

    public void onCreate() {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(builder.activity).inflate(R.layout.dialog_custom_tip, null);
        final TextView tvContent = viewGroup.findViewById(R.id.tvContent);
        final TextView tvTitle = viewGroup.findViewById(R.id.tvTitle);
        Button btnNegative = viewGroup.findViewById(R.id.btnNegative);
        Button btnConfirm = viewGroup.findViewById(R.id.btnPositive);

        tvTitle.setText(builder.title);
        tvContent.setText(builder.message);
        btnNegative.setText(builder.negativeText);
        btnConfirm.setText(builder.positiveText);

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                if (builder.onNegativeClickListener != null)
                    builder.onNegativeClickListener.onNegative(TipDialog.this);

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                materialDialog.dismiss();
                if (builder.onPositiveClickListener != null)
                    builder.onPositiveClickListener.onPositive();

            }
        });
        materialDialog = Dialogs.showCustomDialog(builder.activity, viewGroup, true);
        materialDialog.setCanceledOnTouchOutside(builder.onTouchOutsideCancel);
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
        private String title;
        private String message;
        private Activity activity;
        private OnPositiveClickListener onPositiveClickListener;
        private OnNegativeClickListener onNegativeClickListener;
        private TipDialog tipDialog;
        private boolean onTouchOutsideCancel;


        public Builder(Activity activity) {
            this.activity = activity;
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

        public TipDialog create() {
            tipDialog = new TipDialog(this);
            tipDialog.onCreate();
            return tipDialog;
        }

        public void show() {
            if (tipDialog == null)
                create().show();
        }
    }

    public interface OnPositiveClickListener {
        void onPositive();
    }

    public interface OnNegativeClickListener {
        void onNegative(TipDialog dialog);
    }

}
