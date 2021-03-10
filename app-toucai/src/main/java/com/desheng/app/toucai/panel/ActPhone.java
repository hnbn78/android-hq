package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Maps;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.dialog.PictureCodeDialog;
import com.desheng.base.model.PersonInfo;
import com.desheng.base.panel.ActWeb;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.util.StatusBarHelper;
import com.shark.tc.R;

import java.util.HashMap;

public class ActPhone extends AppCompatActivity {

    public static void launcher(Activity activity, PersonInfo info) {
        Intent intent = new Intent(activity, ActPhone.class);
        intent.putExtra("info", info);
        activity.startActivity(intent);
    }

    PersonInfo info;
    String cell;
    String cellMasked;
    String verifyCode;
    EditText sms;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarHelper.setStatusBar(this, false, false);
        setContentView(R.layout.act_phone);
        sms = findViewById(R.id.tv_sms);

        info = (PersonInfo) getIntent().getSerializableExtra("info");

        if (!TextUtils.isEmpty(info.getCellphone())) {
            String cell = info.getCellphone();

            if (info.getCellphone().startsWith("86")) {
                cell = cell.substring(2, cell.length());
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cell.length(); i++) {
                if (i > 2 && i < 9) {
                    sb.append("*");
                } else {
                    sb.append(String.valueOf(cell.charAt(i)));
                }
            }

            ((TextView) findViewById(R.id.tv_phone_label)).setText(sb.toString());
        } else {
            finish();
        }

        cell = info.getCellphone();

        if (info.getCellphone().startsWith("86")) {
            cell = cell.substring(2, cell.length());
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cell.length(); i++) {
            if (i > 2 && i < 9) {
                sb.append("*");
            } else {
                sb.append(String.valueOf(cell.charAt(i)));
            }
        }

        cellMasked = sb.toString();
    }

    public void onChangePhoneClick(View view) {
        if (sms.getText().toString().isEmpty()) {
            Toasts.show("请输入验证码", false);
        }

        String cell = info.getCellphone();

        if (info.getCellphone().startsWith("86")) {
            cell = cell.substring(2, cell.length());
        }

        HttpActionTouCai.verifySmsCode(this, Strs.parse(cell, 0l), sms.getText().toString(), new AbHttpResult() {
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    ActPhoneModifyOld.launch(ActPhone.this);
                    finish();
                } else {
                    Toasts.show(msg, false);
                }

                return true;
            }
        });

    }

    public void sendSmsCode(View view) {
        TextView v = (TextView) view;
        HttpActionTouCai.getSmsCodeNew(this, false, cell, verifyCode, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<HashMap<String, Object>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (extra.get("data") != null) {
                    HashMap<String, Object> data = (HashMap<String, Object>) extra.get("data");
                    boolean needCapchaCode = Maps.value(data, "needCapchaCode", false);
                    if (needCapchaCode) {
                        showPicCodeDialog(() -> v.performClick());
                    } else {
                        Toasts.show("短信发送成功", true);
                        v.setText("60秒后重新发送");
                        v.setEnabled(false);
                        new CountDownTimer(60 * 1000, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                v.setText(millisUntilFinished / 1000 + "秒后重新发送");
                                v.setTextColor(Color.parseColor("#A8A8A8"));
                            }

                            @Override
                            public void onFinish() {
                                v.setText("获取验证码");
                                v.setEnabled(true);
                                v.setTextColor(0xff333333);
                            }

                        }.start();
                    }
                } else {
                    Toasts.show(msg, false);
                }
                return true;

            }
        });
    }

    public void onOnLineClientClick(View view) {
        ActWeb.launchCustomService(this);
    }

    private void showPicCodeDialog(Runnable runnable) {
        PictureCodeDialog.Builder builder = new PictureCodeDialog.Builder(this);
        builder.setTitle("请输入验证码")
                .setMessage("验证完成后继续进行操作")
                .setOnTouchOutsideCancel(false)
                .setPositiveButton("确认", new PictureCodeDialog.OnPositiveClickListener() {
                    @Override
                    public void onPositive(PictureCodeDialog dialog, String code) {
                        dialog.dismiss();
                        verifyCode = code;
                        runnable.run();
                    }
                }).show();
    }

    public void titleLeftClick(View view) {
        this.onBackPressed();
    }
}
