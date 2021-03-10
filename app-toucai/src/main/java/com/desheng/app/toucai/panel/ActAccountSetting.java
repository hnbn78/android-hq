package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ab.callback.AbCallback;
import com.ab.util.AbDateUtil;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.PersonInfo;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.panel.ActWeb;
import com.desheng.base.util.ResUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ActAccountSetting extends AbAdvanceActivity {

    private TextView tvRealName, tvPhoneNum, tvEmailAddress, tvBirthDate, tvGender, tvPhoneModify;
    private RadioGroup rg_gender;
    private FrameLayout layout_birthday;
    private static final String MALE = "1";
    private static final String LADY = "0";
    private static final String SECRECY = "2";
    private String GENDER = SECRECY;
    private String birthDay;
    private PersonInfo info;
    private int dialog_type;
    private RadioButton rbMen, rbLady, rbSecrecy;
    private final int TYPE_GENDER = 1;
    private final int TYPE_BIRTHDAY = 2;
    private TextView tvEmailAddressModify;

    public static void launcher(Activity activity) {
        activity.startActivity(new Intent(activity, ActAccountSetting.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_account_setting;
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), ResUtil.getString(R.string.account_setting));
        setToolbarRightButtonImg(R.mipmap.ic_customer_service, 48, 48, v -> ActWeb.launchCustomService(ActAccountSetting.this));
        ((TextView) findViewById(R.id.tvMemberName)).setText(UserManager.getIns().getMainUserName());
        ((TextView) findViewById(R.id.tvRegisterDate)).setText(AbDateUtil.getStringByFormat(UserManager.getIns().getMainRegisterTime(), AbDateUtil.dateFormatYMD));

        tvBirthDate = findViewById(R.id.tvBirthDate);
        rg_gender = findViewById(R.id.rg_gender);
        layout_birthday = findViewById(R.id.layout_birthday);
        tvGender = findViewById(R.id.tvGender);
        tvRealName = findViewById(R.id.tvRealName);
        tvPhoneNum = findViewById(R.id.tvPhoneNum);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        tvEmailAddress = findViewById(R.id.tvEmailAddress);
        tvPhoneModify = findViewById(R.id.tvPhoneModify);
        rbSecrecy = findViewById(R.id.check_secret);
        rbMen = findViewById(R.id.check_secret);
        rbLady = findViewById(R.id.check_secret);
        tvEmailAddressModify = findViewById(R.id.tvEmailAddressModify);

        onLoadData();
//        tvRealName.setOnClickListener(this);
//        tvPhoneNum.setOnClickListener(this);
//        tvEmailAddress.setOnClickListener(this);
//        tvBirthDate.setOnClickListener(this);
//        tvMore.setOnClickListener(this);

        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.check_secret:
                        GENDER = SECRECY;
                        break;
                    case R.id.check_man:
                        GENDER = MALE;
                        break;
                    case R.id.check_woman:
                        GENDER = LADY;
                        break;
                }

                showBindTip("性别设置后不可修改", "请确认性别是否正确", "确定", TYPE_GENDER);
            }
        });
    }

    private void showBindTip(String tip1, String tip2, String right, int type) {

        dialog_type = type;

        DialogsTouCai.showBindDialog(ActAccountSetting.this, tip1, tip2, "", right, true, new AbCallback<Object>() {
            @Override
            public boolean callback(Object obj) {

                if (type == TYPE_GENDER) {
                    bindPersonInfo(type, GENDER);
                } else if (type == TYPE_BIRTHDAY) {
                    bindPersonInfo(type, birthDay);
                }

                DialogsTouCai.hideBindTipDialog();
                tvBirthDate.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                return true;
            }
        });

        DialogsTouCai.getTipDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dialog_type == TYPE_GENDER) {
                    rg_gender.clearCheck();
                }
            }
        });

    }

    private void onLoadData() {
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

            }
        });

        UserManager.getIns().getPersonSettingInfo(new UserManager.IUserInfoGetCallBack() {
            @Override
            public void onCallBack(PersonInfo personInfo) {
                info = personInfo;
                bindPerson();
            }
        });
    }

    private void bindPersonInfo(int type, String value) {
        UserManager.getIns().bindPersonInfo(type, value, new UserManager.IUserInfoSyncCallBack() {
            @Override
            public void onSync(String msg) {
                Toasts.show(ActAccountSetting.this, msg, true);
                switch (type) {
                    case 1:
                        info.setGender(GENDER);
                        setGender();
                        break;
                    case TYPE_BIRTHDAY:
                        info.setBirthday(value);
                        break;
                }
            }
        });

    }

    private void bindPerson() {
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
            tvPhoneNum.setText(sb.toString());
//            tvPhoneNum.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_common_arrow_right, 0);
            tvPhoneModify.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(info.getEmail())) {

            StringBuilder sb = new StringBuilder();
            String email = info.getEmail();
            int index = email.indexOf("@");
            for (int i = 0; i < email.length(); i++) {
                if (i > (index - 3) && i < index) {
                    sb.append("*");
                } else {
                    sb.append(String.valueOf(email.charAt(i)));
                }
            }

            tvEmailAddress.setText(sb.toString());
//            tvEmailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_common_arrow_right, 0);
            tvEmailAddressModify.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(info.getWithdrawName())) {
            tvRealName.setText(info.getWithdrawName());
            tvRealName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            tvRealName.setEnabled(false);
        } else {
            tvRealName.setEnabled(true);
        }

        if (!TextUtils.isEmpty(info.getBirthday())) {
            tvBirthDate.setText(info.getBirthday());
            tvBirthDate.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        if (Strs.isNotEmpty(info.getBirthday())) {
            layout_birthday.setEnabled(false);
        } else {
            layout_birthday.setEnabled(true);
        }

        if (UserManager.getIns().getIsBindGender()) {
            setGender();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UserManager.getIns().getIsBindEmail()) {
            StringBuilder sb = new StringBuilder();
            String email = UserManager.getIns().getEmail();
            int index = email.indexOf("@");
            for (int i = 0; i < email.length(); i++) {
                if (i > (index - 3) && i < index) {
                    sb.append("*");
                } else {
                    sb.append(String.valueOf(email.charAt(i)));
                }
            }

            tvEmailAddress.setText(sb.toString());
            tvEmailAddressModify.setVisibility(View.VISIBLE);
        } else {
            tvEmailAddress.setText("立即设置");
            tvEmailAddressModify.setVisibility(View.GONE);
        }

        if (UserManager.getIns().getIsBindCellphone()) {
            String cell = UserManager.getIns().getCellphone();
            if (cell.startsWith("86")) {
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
            tvPhoneNum.setText(sb.toString());
            tvPhoneModify.setVisibility(View.VISIBLE);
        } else {
            tvPhoneNum.setText("立即设置");
            tvPhoneModify.setVisibility(View.GONE);
        }

        if (UserManager.getIns().getIsBindWithdrawName()) {
            tvRealName.setText(UserManager.getIns().getWithDrawName());
            tvRealName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            tvRealName.setEnabled(false);
        } else {
            tvRealName.setEnabled(true);
        }
    }

    public void onAddClick(View view) {
        switch (view.getId()) {
            case R.id.tvRealName:
                if (TextUtils.isEmpty(info.getWithdrawName()))
                    ActEditAccount.launcher(this);
                break;
            case R.id.tvPhoneNum:
                if (!UserManager.getIns().getIsBindPhone()) {
                    ActBindPhone.launch(this);
                }
                break;
            case R.id.tvEmailAddress:
                if (!UserManager.getIns().getIsBindEmail()) {
                    ActBindEmail.launcher(this);
                }
                break;
            case R.id.tvEmailAddressModify:
                ActEditPhoneOrEmail.launch(this);
                break;
            case R.id.layout_birthday:
                if (TextUtils.isEmpty(info.getBirthday())) {
                    Calendar calendar = Calendar.getInstance();
                    showDateDialog(Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                }
                break;

            case R.id.tvMore:
                ActMoreSetting.launcher(ActAccountSetting.this, info);
                break;
            case R.id.tvPhoneModify:
                ActPhone.launcher(this, info);
                break;
        }
    }


    private void showDateDialog(List<Integer> date) {
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(this);
        builder.setSelectYear(date.get(0) - 1)
                .setSelectMonth(date.get(1) - 1)
                .setSelectDay(date.get(2) - 1)
                .setOnDateSelectedListener(new DatePickerDialog.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int[] dates) {
                        String text = dates[0] + "-" + (dates[1] > 9 ? dates[1] : ("0" + dates[1])) + "-"
                                + (dates[2] > 9 ? dates[2] : ("0" + dates[2]));
                        tvBirthDate.setText(text);
                        birthDay = text;
                        showBindTip("出生日期设置后无法修改", "请确认出生日期是否正确", "确定", TYPE_BIRTHDAY);
                    }

                    @Override
                    public void onCancel() {

                    }
                });

        builder.setMaxYear(DateUtil.getYear());
        builder.setMaxMonth(DateUtil.getDateForString(DateUtil.getToday()).get(1));
        builder.setMaxDay(DateUtil.getDateForString(DateUtil.getToday()).get(2));
        DatePickerDialog dateDialog = builder.create();
        dateDialog.show();
    }

    private void setGender() {
        switch (info.getGender()) {
            case LADY:
                tvGender.setText("女");
                rg_gender.setVisibility(View.GONE);
                break;
            case MALE:
                tvGender.setText("男");
                rg_gender.setVisibility(View.GONE);
                break;
            case SECRECY:
                tvGender.setText("保密");
                rg_gender.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActEditAccount.RESULT_CODE) {
            onLoadData();
        }

    }

}
