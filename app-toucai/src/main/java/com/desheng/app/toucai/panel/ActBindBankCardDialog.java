package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.callback.AbCallback;
import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.MD5;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.airsaid.pickerviewlibrary.CityPickerView;
import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.airsaid.pickerviewlibrary.listener.OnSimpleCitySelectListener;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.PrepareBindCard;
import com.desheng.base.util.DeviceUtil;
import com.desheng.base.view.BottomInputFundPwdDialog;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.Request;

/**
 * 绑定银行卡
 */

public class ActBindBankCardDialog extends AbAdvanceActivity {

    public static final int BIND_OK_RESULT_CODE = 102;
    private EditText etPassword, etRePassword;
    private boolean isFirstTime;
    private RelativeLayout rlPassword_layout, rlRePassword_layout;
    private String missionId;

    public static void launch(Activity ctx, boolean isFirstTime, String missionId) {
        Intent itt = new Intent(ctx, ActBindBankCardDialog.class);
        itt.putExtra("isFirstTime", isFirstTime);
        itt.putExtra("missionId", missionId);
        ctx.startActivityForResult(itt, 0);
    }


    private TextView tvBank, tv_card_type, cancel,Gotouzhu;

    private EditText et_open_bank_address, etName, etCardNum, etConfirmCardNum, etFundsPwd;
    private Button btSubmit;
    private Dialog chooseDialog;
    private RelativeLayout rl_card_type_layout, RvBindCard, RvGetSuccess;

    private TextView tv_address;
    private ImageView iv_arrow_address;

    private ArrayList<String> bankNameList = new ArrayList<>();
    private ArrayList<String> bankTypeList = new ArrayList<>();
    private List<PrepareBindCard.BankListBean> bankList;

    private RelativeLayout rlSelect, rlfunds_layout;
    private static final int DEBIT_CARD = 1;
    private static final int CREDIT_CARD = 2;
    private boolean isWithdraw;

    @Override
    protected int getLayoutId() {
        return R.layout.act_bind_bank_card_dialog;
    }

    @Override
    protected void init() {
        getToolbar().setVisibility(View.GONE);
        getVgRoot().setBackgroundColor(Color.TRANSPARENT);
        isWithdraw = getIntent().getBooleanExtra("isWithdraw", false);
        isFirstTime = getIntent().getBooleanExtra("isFirstTime", false);
        missionId = getIntent().getStringExtra("missionId");
        bankNameList.addAll(UserManager.getIns().getListBank());
        bankTypeList.add("借记卡");
        bankTypeList.add("信用卡");
        initView();
    }


    private void initView() {
        rlSelect = findViewById(R.id.rlSelect_layout);
        rlfunds_layout = findViewById(R.id.rlfunds_layout);
        rlSelect.setOnClickListener(this);
        btSubmit = findViewById(R.id.submit_bt);
        cancel = findViewById(R.id.cancel);
        btSubmit.setOnClickListener(this);
        tvBank = findViewById(R.id.tvBank);
        tv_card_type = findViewById(R.id.tv_card_type);
        et_open_bank_address = findViewById(R.id.et_open_bank_address);
        etName = findViewById(R.id.etName);
        rl_card_type_layout = findViewById(R.id.rl_card_type_layout);
        iv_arrow_address = findViewById(R.id.iv_arrow_address);
        tv_address = findViewById(R.id.tv_address);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);
        rlPassword_layout = findViewById(R.id.rlPassword_layout);
        rlRePassword_layout = findViewById(R.id.rlRePassword_layout);
        RvBindCard = findViewById(R.id.RvBindCard);
        RvGetSuccess = findViewById(R.id.RvGetSuccess);
        Gotouzhu = findViewById(R.id.Gotouzhu);

        tv_address.setOnClickListener(this);
        rl_card_type_layout.setOnClickListener(this);

        prepareData();
        etCardNum = findViewById(R.id.etCardNum);
        etConfirmCardNum = findViewById(R.id.etConfirmCardNum);
        etFundsPwd = findViewById(R.id.etFundsPwd);

        if (Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN)) {
            etCardNum.setHint("请填写15-19位数字银行卡号");
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Gotouzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CtxLotteryTouCai.launchLotteryPlay(ActBindBankCardDialog.this, 911);//跳腾讯分分彩
                finish();
            }
        });
//        if (UserManager.getIns().getIsBindWithdrawName() && Strs.isNotEmpty(UserManager.getIns().getWithDrawName())) {
//            //etName.setText(UserManager.getIns().getWithDrawName());
//            //etName.setEnabled(false);
//        } else {
//            etName.setEnabled(true);
//        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == rl_card_type_layout) {
            //selectBankType();
        } else if (v == btSubmit) {
            if (checkBandInfo())
                //showBottomPwdDialog();
                submitBind();
        } else if (v == rlSelect) {
            selectBank();
        } else if (v == tv_address) {
            selectCity();
        }
    }

    private String province;
    private String city;
    private String county;
    private int cardType = DEBIT_CARD;

    private void selectCity() {
        DeviceUtil.hideInputKeyboard(this);
        CityPickerView mCityPickerView = new CityPickerView(ActBindBankCardDialog.this);

        mCityPickerView.setCancelable(true);
        mCityPickerView.setTextSize(18f);
        mCityPickerView.setTitle("选择银行");
        mCityPickerView.setCancelText("取消");
        mCityPickerView.setCancelTextColor(Color.GRAY);
        mCityPickerView.setCancelTextSize(14f);
        mCityPickerView.setSubmitText("确定");
        mCityPickerView.setSubmitTextColor(Color.BLACK);
        mCityPickerView.setSubmitTextSize(14f);
        mCityPickerView.setHeadBackgroundColor(Color.WHITE);
        mCityPickerView.setOnCitySelectListener(new OnSimpleCitySelectListener() {
            @Override
            public void onCitySelect(String prov, String cityname, String area) {
                // 省、市、区 分开获取
                province = prov;
                city = cityname;
                county = area;
                tv_address.setText(prov + " " + cityname + " " + area);
            }

            @Override
            public void onCitySelect(String str) {
                // 一起获取
//                    Toast.makeText(ActBindBankCardToucai.this, "选择了：" + str, Toast.LENGTH_SHORT).show();
            }
        });

        mCityPickerView.show();
    }

    private void selectBank() {
        //如果有软键盘则隐藏
        DeviceUtil.hideInputKeyboard(this);

        OptionsPickerView pvOptions = new OptionsPickerView(ActBindBankCardDialog.this);
        pvOptions.setSubmitText("确定");//确定按钮文字
        pvOptions.setCancelText("取消");//取消按钮文字
        pvOptions.setTitle("选择银行卡");
        pvOptions.setCancelTextColor(Color.GRAY);
        pvOptions.setCancelTextSize(14f);
        pvOptions.setSubmitTextColor(Color.BLACK);
        pvOptions.setSubmitTextSize(14f);
        pvOptions.setHeadBackgroundColor(Color.WHITE);

        pvOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {
                //返回的分别是三个级别的选中位置

                String s = bankNameList.get(option1);
                tvBank.setText(s);
            }
        });
        pvOptions.setPicker(bankNameList);
        pvOptions.show();
    }

    private void selectBankType() {
        //如果有软键盘则隐藏
        DeviceUtil.hideInputKeyboard(this);

        OptionsPickerView pvOptions = new OptionsPickerView(ActBindBankCardDialog.this);
        pvOptions.setSubmitText("确定");//确定按钮文字
        pvOptions.setCancelText("取消");//取消按钮文字
        pvOptions.setTitle("选择银行卡类型");
        pvOptions.setCancelTextColor(Color.GRAY);
        pvOptions.setCancelTextSize(14f);
        pvOptions.setSubmitTextColor(Color.BLACK);
        pvOptions.setSubmitTextSize(14f);
        pvOptions.setHeadBackgroundColor(Color.WHITE);

        pvOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {
                //返回的分别是三个级别的选中位置
                cardType = option1 == 0 ? DEBIT_CARD : CREDIT_CARD;
                String s = bankTypeList.get(option1);
                tv_card_type.setText(s);
                tv_card_type.setTag(option1 + 1);
            }
        });
        pvOptions.setPicker(bankTypeList);
        pvOptions.show();
    }

    private void prepareData() {
        HttpAction.prepareBindCard(this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<PrepareBindCard>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && getField(extra, "data", null) != null) {
                    PrepareBindCard prepareBindCard = getFieldObject(extra, "data", PrepareBindCard.class);
                    if (Strs.isNotEmpty(prepareBindCard.getName())) {
                        etName.setText(prepareBindCard.getName());
                        etName.setEnabled(false);
                        etName.setFocusable(false);
                        etName.setFocusableInTouchMode(false);
                    }
                    bankList = prepareBindCard.getBankList();
                }
                return true;
            }

        });
    }

    private void showBottomPwdDialog() {
        BottomInputFundPwdDialog.Builder builder = new BottomInputFundPwdDialog.Builder(this);
        final BottomInputFundPwdDialog dateDialog = builder.create();
        builder.setOnConfirmClickListener(new BottomInputFundPwdDialog.OnConfirmListener() {
            @Override
            public void onClick(final String pwd) {

//                if (!AbStrUtil.isNumber(pwd)) {
//                    dateDialog.dismiss();
//                    showBindTip();
//                    return;
//                }

                submitBind();

            }
        });

        dateDialog.show();
    }

    private MaterialDialog dialog;
    private String fundsPwd;
    private String realName;
    private String bankCode, branch, cardNum, cardNumConfirm;

    private boolean checkBandInfo() {
        String bank = Views.getText(tvBank);
        if (Strs.isEmpty(bank) || bank.equals("请选择银行")) {
            Toast.makeText(this, "请选择银行", Toast.LENGTH_LONG).show();
            return false;
        }

        if (bankList != null && !bankList.isEmpty()) {
            for (PrepareBindCard.BankListBean bean : bankList) {
                if (bean.getName().equals(bank)) {
                    bankCode = bean.getCode();
                }
            }
        }
        if (TextUtils.isEmpty(bankCode))
            bankCode = UserManager.getIns().getBankCode(bank);
        branch = et_open_bank_address.getText().toString();

//        if (Strs.isEmpty(branch) || branch.length() < 2) {
//            Toast.makeText(this, "请输入正确的支行名称", Toast.LENGTH_LONG).show();
//            return false;
//        }
//        if (!Strs.isAllChinese(branch)) {
//            Toast.makeText(this, "支行名称必须全部为中文!", Toast.LENGTH_LONG).show();
//            return false;
//        }

        if (isFirstTime) {
            realName = Views.getText(etName);
            if (Strs.isEmpty(realName) || realName.length() < 2) {
                Toast.makeText(this, "请填写真实姓名", Toast.LENGTH_LONG).show();
                return false;
            }
            if (!Strs.isAllChineseWithDot(realName)) {
                Toast.makeText(this, "真实姓名必须全为汉字", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        cardNum = etCardNum.getText().toString();
        if (Strs.isEmpty(cardNum) || cardNum.length() < 15) {
            Toast.makeText(this, "请输入正确的银行卡号", Toast.LENGTH_LONG).show();
            return false;
        }

        cardNumConfirm = etConfirmCardNum.getText().toString();
        if (Strs.isEmpty(cardNumConfirm)) {
            Toast.makeText(this, "请确认卡号", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!cardNum.equals(cardNumConfirm)) {
            Toast.makeText(this, "两次输入卡号不一致!", Toast.LENGTH_LONG).show();
            return false;
        }


        String pwd1 = etPassword.getText().toString().trim();
        String pwd2 = etRePassword.getText().toString().trim();
        if (Strs.isEmpty(pwd1) || Strs.isEmpty(pwd2)) {
            Toasts.show(this, "资金密码不能为空", false);
            return false;
        }

        if (!Strs.isEqual(pwd1, pwd2)) {
            Toasts.show(this, "两次资金密码不一致", false);
            return false;
        }

        fundsPwd = pwd1;

        Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$");
        if (!pattern.matcher(pwd1).matches()) {
            Toasts.show(this, "资金密码是由6-12位字母和数字组成", false);
            return false;
        }
        return true;
    }

    private void submitBind() {
        if (Strs.isEmpty(fundsPwd)) {
            Toast.makeText(this, "请输入资金密码", Toast.LENGTH_LONG).show();
            return;
        }
        fundsPwd = MD5.md5(fundsPwd).toLowerCase();
        HttpAction.bindBankCard(ActBindBankCardDialog.this, bankCode, branch, realName,
                cardNum, fundsPwd, cardType, province, city, county, isFirstTime, new AbHttpResult() {

                    @Override
                    public void onBefore(Request request, int id, String host, String funcName) {
                        btSubmit.post(new Runnable() {
                            @Override
                            public void run() {
                                Dialogs.showProgressDialog(ActBindBankCardDialog.this, "");
                            }
                        });
                    }

                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        super.setupEntity(entity);
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        if (code == 0 && error == 0) {
                            Dialog dialog = DialogsTouCai.showFunOkDialog(ActBindBankCardDialog.this, 0, "绑定成功");
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    UserManager.getIns().setIsBindCard(true);
                                    //setResult(BIND_OK_RESULT_CODE);
                                    if (Strs.isEmpty(missionId)) {
                                        return;
                                    }
                                    joinMissionById(missionId);
                                }
                            });

                        } else {
                            Toasts.show(ActBindBankCardDialog.this, msg, false);
                        }
                        return true;
                    }

                    @Override
                    public void onAfter(int id) {
                        btSubmit.post(new Runnable() {
                            @Override
                            public void run() {
                                Dialogs.hideProgressDialog(ActBindBankCardDialog.this);
                            }
                        });
                    }
                });

    }

    private void joinMissionById(String missionId) {
        HttpActionTouCai.checkJoinActivity(this, missionId, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<HashMap<String, Object>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0) {
                    HashMap<String, Object> data = getField(extra, "data", null);
                    if (data != null) {
                        RvBindCard.setVisibility(View.GONE);
                        RvGetSuccess.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                finish();
            }
        });
    }

    private void showBindTip(String tip1, String tip2, String right) {
        DialogsTouCai.showBindDialog(ActBindBankCardDialog.this, tip1, tip2, "", right,
                false, new AbCallback<Object>() {
                    @Override
                    public boolean callback(Object obj) {
                        finish();
                        return true;
                    }
                }, new AbCallback<Object>() {
                    @Override
                    public boolean callback(Object obj) {
                        launcherAct(ActBindPhone.class);
                        DialogsTouCai.hideBindTipDialog();
                        finish();
                        return true;
                    }
                });
    }

    private void launcherAct(Class cls) {
        Intent intent = new Intent(ActBindBankCardDialog.this, cls);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ActEditAccount.RESULT_CODE == resultCode) {
            setResult(ActEditAccount.RESULT_CODE);
            finish();
        }
    }


}
