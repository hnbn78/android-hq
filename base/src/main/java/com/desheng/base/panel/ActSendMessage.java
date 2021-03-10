package com.desheng.base.panel;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.util.DeviceUtil;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActSendMessage extends AbAdvanceActivity implements View.OnClickListener {

    private EditText et_input_topic, et_input_content;
    private Button btn_send, btn_add_account;
    private TextView tv_receiver;
    private LinearLayout layout_receiver;
    private String target = "up";
    private ArrayList<String> directList;
    private RadioButton rbAgent, rbMember;
    private RadioGroup rgShoujianren;

    public static void launch(Activity ctx) {
        simpleLaunch(ctx, ActSendMessage.class);
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "发消息");
        et_input_topic = (EditText) findViewById(R.id.et_input_topic);
        et_input_content = (EditText) findViewById(R.id.et_input_content);
        layout_receiver = (LinearLayout) findViewById(R.id.layout_receiver);
        tv_receiver = (TextView) findViewById(R.id.tv_receiver_name);
        btn_send = (Button) findViewById(R.id.btn_send);
        rgShoujianren = (RadioGroup) findViewById(R.id.rgShoujianren);
        rbAgent = (RadioButton) findViewById(R.id.rbAgent);
        rbMember = (RadioButton) findViewById(R.id.rbMember);
        btn_add_account = (Button) findViewById(R.id.btn_add_account);
        btn_send.setOnClickListener(this);
        btn_add_account.setOnClickListener(this);
        getDirectAccountList();
//        if (!UserManager.getIns().isAgent())
//            findViewById(R.id.layout_check).setVisibility(View.GONE);
//        else
//            ((TextView) findViewById(R.id.agency_tv)).setText("上级");

        rgShoujianren.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbAgent) {
                    layout_receiver.setVisibility(View.GONE);
                    target = "up";
                } else if (checkedId == R.id.rbMember) {
                    layout_receiver.setVisibility(View.VISIBLE);
                    target = "down";
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btn_add_account) {
            showBankList();
        } else if (v == btn_send) {
            if (Strs.isEmpty(et_input_topic.getText().toString())) {
                Toasts.show(ActSendMessage.this, "请输入主题");
                return;
            }

            if (Strs.isEmpty(et_input_content.getText().toString())) {
                Toasts.show(ActSendMessage.this, "请输入内容");
                return;
            }

            if (target.equals("down") && Strs.isEmpty(tv_receiver.getText().toString())) {
                Toasts.show(ActSendMessage.this, "请添加用户");
                return;
            }

            sendMessage();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_send_message;
    }

    private void sendMessage() {
        HttpAction.sendMessage(null, target, tv_receiver.getText().toString(), et_input_topic.getText().toString(),
                et_input_content.getText().toString(), new AbHttpResult() {

                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        super.setupEntity(entity);
                    }

                    @Override
                    public void onBefore(Request request, int id, String host, String funcName) {
                        Dialogs.showProgressDialog(ActSendMessage.this,"发送中...");
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                        Toasts.show(ActSendMessage.this, msg);
                        if (code == 0 && error == 0) {
                            et_input_content.setText("");
                            et_input_topic.setText("");
                            tv_receiver.setText("");
                        }

                        return true;
                    }

                    @Override
                    public boolean onGetString(String str) {
                        return super.onGetString(str);
                    }

                    @Override
                    public boolean onError(int status, String content) {
                        return super.onError(status, content);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        Dialogs.hideProgressDialog(ActSendMessage.this);
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                    }
                });
    }

    private void getDirectAccountList() {
        HttpAction.getDirectAccountList(null, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActSendMessage.this,"");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
                entity.putField("data", new TypeToken<List<String>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    directList = getField(extra, "data", null);
                    intBankList();
                } else {
                    Toasts.show(ActSendMessage.this, msg);
                }


                return true;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(ActSendMessage.this);
            }

        });
    }

    OptionsPickerView pvOptions;

    private void intBankList() {
        pvOptions = new OptionsPickerView(this);
        pvOptions.setSubmitText("确定");//确定按钮文字
        pvOptions.setCancelText("取消");//取消按钮文字
        pvOptions.setTitle("");
        pvOptions.setCancelText("取消");
        pvOptions.setCancelTextColor(Color.GRAY);
        pvOptions.setCancelTextSize(14f);
        pvOptions.setSubmitText("确定");
        pvOptions.setSubmitTextColor(Color.BLACK);
        pvOptions.setSubmitTextSize(14f);
        pvOptions.setHeadBackgroundColor(Color.WHITE);

        pvOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {
                //返回的分别是三个级别的选中位置

                String s = directList.get(option1);
                tv_receiver.setTag(s);
                tv_receiver.setText(s);
            }
        });
        pvOptions.setPicker(directList);
    }

    private void showBankList() {
        if (directList == null || directList.size() == 0) {
            Toasts.show(this, "没有收件人", false);
            return;
        }

        DeviceUtil.hideInputKeyboard(this);
        if (pvOptions == null) {
            return;
        }
        pvOptions.show();
    }

}
