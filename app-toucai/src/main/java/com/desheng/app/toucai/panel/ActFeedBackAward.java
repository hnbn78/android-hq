package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Toasts;
import com.desheng.base.action.HttpAction;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.HashMap;

import okhttp3.Request;

public class ActFeedBackAward extends AbAdvanceActivity {

    private TextView tv_date;
    private EditText et_content, et_phone;
    private Button btn_confirm;

    public static void launcher(Activity activity) {
        activity.startActivity(new Intent(activity, ActFeedBackAward.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_feed_back_award;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "有奖反馈");
        setStatusBarTranslucentAndLightContentWithPadding();

        setToolbarButtonRightText("取消");
        setToolbarButtonRightTextColor(R.color.white);
        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_date = findViewById(R.id.tv_date);
        et_content = findViewById(R.id.et_content);
        et_phone = findViewById(R.id.et_phone);
        btn_confirm = findViewById(R.id.btn_confirm);

        tv_date.setText(Dates.getCurrentDate(Dates.dateFormatYMDHM));
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 10) {
                    btn_confirm.setBackgroundResource(R.drawable.shape_round_corner_primary);
                } else {
                    btn_confirm.setBackgroundResource(R.drawable.shape_round_corner_gray);
                }
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_content.getText().toString();
                String contact = et_phone.getText().toString();
                onSubmitFeedback(content, contact);
            }
        });
    }

    private void onSubmitFeedback(String content, String contact) {
        HttpAction.submitFeedback(this, content, contact, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                btn_confirm.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActFeedBackAward.this, "正在提交...");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show("反馈成功", true);
                    finish();
                } else
                    Toasts.show(msg, true);
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                btn_confirm.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActFeedBackAward.this);
                    }
                });
            }
        });
    }
}
