package com.ab.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ab.core.R;
import com.ab.global.Global;
import com.ab.util.Strs;
import com.ab.util.Views;

public class ActMaterialDialog extends Activity {

    public static  String ACTION_SHOW = "com.ab.action.showDialog";
    public static  String ACTION_HIDE = "com.ab.action.hideDialog";
    
    private Button mPositiveButton;
    private Button mNegativeButton;
    private TextView mTitleView;
    private TextView mMessageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);//需要添加的语句
        setContentView(R.layout.act_material_dialog);
        mTitleView = (TextView) findViewById(R.id.title);
        mMessageView = (TextView) findViewById(R.id.message);
        mPositiveButton = (Button) findViewById(R.id.btn_p);
        mNegativeButton = (Button) findViewById(R.id.btn_n);

        if (Builder.mTitle != null) {
            mTitleView.setText(Builder.mTitle);
        }else{
            mTitleView.setVisibility(View.GONE);
        }
        if (Builder.mMessage != null) {
            mMessageView.setText(Builder.mMessage);
        }
        if (Strs.isNotEmpty(Builder.pText)) {
            mPositiveButton.setVisibility(View.VISIBLE);
            mPositiveButton.setText(Builder.pText);
            mPositiveButton.setOnClickListener(Builder.pListener);
        }else{
            mPositiveButton.setVisibility(View.GONE);
        }
    
        if (Strs.isNotEmpty(Builder.nText)) {
            mNegativeButton.setVisibility(View.VISIBLE);
            mNegativeButton.setText(Builder.nText);
            mNegativeButton.setOnClickListener(Builder.nListener);
        }else{
            mNegativeButton.setVisibility(View.GONE);
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(ACTION_HIDE)) {
            finish();
            if (Builder.mOnDismissListener != null) {
                Builder.mOnDismissListener.onDismiss(null);
            }
        }
        setIntent(intent);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Builder.mTitle = null;
        Builder.mMessage = null;
        Builder.pText = null;
        Builder.nText = null;
        Builder.pListener = null;
        Builder.nListener = null;
        Builder.mOnDismissListener = null;
    }
    
    public static class Builder {
        
        private static CharSequence mTitle;
        private static CharSequence mMessage;
        private static String pText, nText;
        private static View.OnClickListener pListener, nListener;
        private static DialogInterface.OnDismissListener mOnDismissListener;
    
        public Builder() {
        
        }
    
        public static void show() {
            Intent itt = new Intent(ACTION_SHOW);
            itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itt.addCategory(Intent.CATEGORY_DEFAULT);
            Global.app.startActivity(itt);
        }
    
        public static void dismiss() {
            Intent itt = new Intent(ACTION_HIDE);
            itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itt.addCategory(Intent.CATEGORY_DEFAULT);
            Global.app.startActivity(itt);
        }
    
        public static void setTitle(int resId) {
            mTitle = Views.fromStrings(resId);
        }
    
        public static void setTitle(CharSequence title) {
            mTitle = title;
        }
    
        public static void setMessage(int resId) {
            mMessage = Views.fromStrings(resId);
        }
    
        public static void setMessage(CharSequence msg) {
            mMessage = msg;
        }
    
        /**
         * set positive material_dialog_button
         *
         * @param text the name of material_dialog_button
         */
        public static void setPositiveButton(String text, final View.OnClickListener listener) {
            pText = text;
            pListener = listener;
        }
    
        /**
         * set positive material_dialog_button
         *
         * @param text the name of material_dialog_button
         */
        public static void setPositiveButton(String text) {
            pText = text;
        }
    
    
        /**
         * set negative material_dialog_button
         *
         * @param text the name of material_dialog_button
         */
        public static void setNegativeButton(String text, final View.OnClickListener listener) {
            nText = text;
            nListener = listener;
        }
    }
    
    
}