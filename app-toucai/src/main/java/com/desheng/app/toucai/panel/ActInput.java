package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.ab.util.Toasts;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.PersonInfo;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

/**
 * 单纯输入内容Activity
 */
public class ActInput extends AbAdvanceActivity {

    public static final String WXREG = "^[a-zA-Z]{1}[-_a-zA-Z0-9]{5,19}$";
    public static final int QQ = 4;
    public static final int WECHAT = 3;
    private EditText editText;
    private int type;
    private boolean isQQ;

    public static void launcher(Activity act, int type) {
        Intent intent = new Intent(act, ActInput.class);
        intent.putExtra("type_key", type);
        act.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_input;
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        type = getIntent().getIntExtra("type_key", 0);
        isQQ = type == QQ;
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), isQQ ? "QQ号设置" : "微信号设置");
        setToolbarButtonRightText("确认");
        setToolbarButtonRightTextColor(R.color.white);
        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBind();
            }
        });

        editText = findViewById(R.id.etInput);
        String hint = isQQ ? "请输入QQ号码" : "请输入微信号";
        editText.setHint(hint);
        editText.setInputType(isQQ ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        if (isQQ)
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
    }


    private void onBind() {
        String account = editText.getText().toString();
        if (isQQ) {
            if (account.length() < 4) {
                Toasts.show("请输入4-18位数字！");
                return;
            }
        } else {
             if(!account.matches(WXREG)){
                 Toasts.show("请输入正确的微信号！");
                 return;
             }
        }
        UserManager.getIns().bindPersonInfo(type, editText.getText().toString(), new UserManager.IUserInfoSyncCallBack() {
            @Override
            public void onSync(String msg) {

                UserManager.getIns().getPersonSettingInfo(new UserManager.IUserInfoGetCallBack() {
                    @Override
                    public void onCallBack(PersonInfo personInfo) {
                        finish();
                    }
                });

                Toasts.show(msg);
            }
        });
    }
}
