package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.ab.http.AbHttpResult;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.base.controller.FloatActionController;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.util.ResUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

public class ActNoticeSetting extends AbAdvanceActivity implements ToggleButton.OnCheckedChangeListener {

    private ToggleButton tbLine, tbSystemNotice, tbActivityNotice, tbVoice, tbVibration, tbSms;

    public static void launcher(Activity activity) {
        Intent intent = new Intent(activity, ActNoticeSetting.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_notice_setting;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), ResUtil.getString(R.string.new_notice));
        setStatusBarTranslucentAndLightContentWithPadding();
        findViewById();
        tbLine.setChecked(UserManager.getIns().isShowFloat());
        tbLine.setOnCheckedChangeListener(this);
        tbSystemNotice.setChecked(UserManager.getIns().isOpenNoticeWithSystem());
        tbSystemNotice.setOnCheckedChangeListener(this);
        tbActivityNotice.setChecked(UserManager.getIns().isOpenNoticeWithActivity());
        tbActivityNotice.setOnCheckedChangeListener(this);
        tbVoice.setChecked(UserManager.getIns().isOpenVoice());
        tbVoice.setOnCheckedChangeListener(this);
        tbVibration.setChecked(UserManager.getIns().isOpenVibration());
        tbVibration.setOnCheckedChangeListener(this);
        tbSms.setChecked(UserManager.getIns().isSmsNotice());
        tbSms.setOnCheckedChangeListener(this);
    }

    private void findViewById() {
        tbLine = findViewById(R.id.tbLine);
        tbSystemNotice = findViewById(R.id.tbSystemNotice);
        tbActivityNotice = findViewById(R.id.tbActivityNotice);
        tbVoice = findViewById(R.id.tbVoice);
        tbVibration = findViewById(R.id.tbVibration);
        tbSms = findViewById(R.id.tbBalanceNotice);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FloatActionController.checkAndShowFloat(ActNoticeSetting.this);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tbLine:
                UserManager.getIns().setShowFloat(isChecked);
                if (isChecked) {
                    FloatActionController.getInstance().show(ActNoticeSetting.this);
                } else {
                    FloatActionController.getInstance().hide();
                }
                break;
            case R.id.tbSystemNotice:
                UserManager.getIns().setOpenNoticeWithSystem(isChecked);
                break;
            case R.id.tbActivityNotice:
                UserManager.getIns().setOpenNoticeWithActivity(isChecked);
                break;
            case R.id.tbVoice:
                UserManager.getIns().setOpenVoice(isChecked);
                break;
            case R.id.tbVibration:
                UserManager.getIns().setOpenVibration(isChecked);
                break;

            case R.id.tbBalanceNotice:
                HttpActionTouCai.setBalanceChangedAlert(this, new AbHttpResult() {
                    @Override
                    public void onAfter(int id) {
                        updateBalanceAlertToggle();
                    }
                });
                break;
        }
    }

    private void updateBalanceAlertToggle() {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onUserBindChecked(UserBindStatus status) {

            }

            @Override
            public void onUserBindCheckFailed(String msg) {

            }

            @Override
            public void onAfter() {
                tbSms.setChecked(UserManager.getIns().isSmsNotice());
            }
        });
    }
}
