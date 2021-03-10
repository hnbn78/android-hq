package com.desheng.base.panel;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.AbStrUtil;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.airsaid.pickerviewlibrary.CityPickerView;
import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.airsaid.pickerviewlibrary.listener.OnSimpleCitySelectListener;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.PrepareBindCard;
import com.desheng.base.util.DeviceUtil;
import com.desheng.base.view.BottomInputFundPwdDialog;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import okhttp3.Request;

/**
 * 绑定银行卡
 */

public class ActBindBankCard extends AbAdvanceActivity {

    public static void launch(Context ctx, boolean isFirstTime) {
        Intent itt = new Intent(ctx, ActBindBankCard.class);
        itt.putExtra("isFirstTime", isFirstTime);
        ctx.startActivity(itt);
    }

    private TextView tvBank, tv_card_type;

    private EditText et_open_bank_address, etName, etCardNum, etConfirmCardNum, etFundsPwd;
    private Button btSubmit;
    private Dialog chooseDialog;
    private RelativeLayout rl_card_type_layout;

    private TextView tv_address;
    private ImageView iv_arrow_address;

    private static final ArrayList<String> bankNameList = new ArrayList<>();
    private ArrayList<String> bankTypeList = new ArrayList<>();

    private RelativeLayout rlSelect, rlfunds_layout;

    @Override
    protected int getLayoutId() {
        return R.layout.act_bind_bank_card;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "银行卡绑定");
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarButtonRightImage(R.mipmap.ic_customer_service);
        setToolbarButtonRightImageSize(22);
        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActWeb.launchCustomService(ActBindBankCard.this);
            }
        });
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
        btSubmit.setOnClickListener(this);
        tvBank = findViewById(R.id.tvBank);
        tv_card_type = findViewById(R.id.tv_card_type);
        et_open_bank_address = findViewById(R.id.et_open_bank_address);
        etName = findViewById(R.id.etName);
        rl_card_type_layout = findViewById(R.id.rl_card_type_layout);
        iv_arrow_address = findViewById(R.id.iv_arrow_address);
        tv_address = findViewById(R.id.tv_address);

        tv_address.setOnClickListener(this);
        rl_card_type_layout.setOnClickListener(this);

        prepareData();
        etCardNum = findViewById(R.id.etCardNum);
        etConfirmCardNum = findViewById(R.id.etConfirmCardNum);
        etFundsPwd = findViewById(R.id.etFundsPwd);

        if (Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN)) {
            etCardNum.setHint("请填写16-19位数字银行卡号");
        }

        if (UserManager.getIns().getIsBindWithdrawName() && Strs.isNotEmpty(UserManager.getIns().getWithDrawName())) {
            etName.setText(UserManager.getIns().getWithDrawName());
            etName.setEnabled(false);
        } else {
            etName.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == rl_card_type_layout) {
            selectBankType();
        } else if (v == btSubmit) {

            showBottomPwdDialog();

        } else if (v == rlSelect) {
            selectBank();
        } else if (v == tv_address) {
            selectCity();
        }
    }

    private String province;
    private String city;
    private String county;
    private int cardType = 1;

    private void selectCity() {
        DeviceUtil.hideInputKeyboard(this);
        CityPickerView mCityPickerView = new CityPickerView(ActBindBankCard.this);

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
//                    Toast.makeText(ActBindBankCard.this, "选择了：" + str, Toast.LENGTH_SHORT).show();
            }
        });

        mCityPickerView.show();
    }

    private void selectBank() {
        //如果有软键盘则隐藏
        DeviceUtil.hideInputKeyboard(this);

        OptionsPickerView pvOptions = new OptionsPickerView(ActBindBankCard.this);
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

        OptionsPickerView pvOptions = new OptionsPickerView(ActBindBankCard.this);
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
                    etName.setText(prepareBindCard.getName());

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

//                if(!AbStrUtil.isNumber(pwd)){
//                    dateDialog.dismiss();
//                    showBindTip();
//                    return;
//                }

                submitBind(pwd);

            }
        });

        dateDialog.show();
    }

    private MaterialDialog dialog;

    private void showBindTip() {

        dialog = Dialogs.showTipDialog(ActBindBankCard.this, "温馨提示", "为了保障您的资金安全，现引用支付宝资金安全技术，需要您重新设定新的资金密码即可生效", "联系客服", "前往绑定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActWeb.launchCustomService(ActBindBankCard.this);
                dialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActChangeFundPassword.launch(ActBindBankCard.this);
                dialog.dismiss();
            }
        });

    }


    public String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }

    private void submitBind(String fundsPwd) {
        String bank = Views.getText(tvBank);
        if (Strs.isEmpty(bank) || bank.equals("请选择银行")) {
            Toast.makeText(this, "请选择银行", Toast.LENGTH_LONG).show();
            return;
        }
        String bankCode = UserManager.getIns().getBankCode(bank);

        String branch = et_open_bank_address.getText().toString();
        if (Strs.isEmpty(branch) || branch.length() < 2) {
            Toast.makeText(this, "请输入正确的支行名称", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Strs.isAllChinese(branch)) {
            Toast.makeText(this, "支行名称必须全部为中文!", Toast.LENGTH_LONG).show();
            return;
        }

//        String realName = null;
//        realName = Views.getText(etName);
//        if (Strs.isEmpty(realName) || realName.length() < 2) {
//            Toast.makeText(this, "请填写真实姓名", Toast.LENGTH_LONG).show();
//            return;
//        }
//        if (!Strs.isAllChineseWithDot(realName)) {
//            Toast.makeText(this, "真实姓名必须全为汉字", Toast.LENGTH_LONG).show();
//            return;
//        }

        String cardNum = etCardNum.getText().toString();
        if (Strs.isEmpty(cardNum) || cardNum.length() < 16) {
            Toast.makeText(this, "请输入正确的银行卡号", Toast.LENGTH_LONG).show();
            return;
        }

        String cardNumConfirm = etConfirmCardNum.getText().toString();
        if (Strs.isEmpty(cardNumConfirm)) {
            Toast.makeText(this, "请确认卡号", Toast.LENGTH_LONG).show();
            return;
        }

        if (!cardNum.equals(cardNumConfirm)) {
            Toast.makeText(this, "两次输入卡号不一致!", Toast.LENGTH_LONG).show();
            return;
        }

        if (Strs.isEmpty(fundsPwd)) {
            Toast.makeText(this, "请输入资金密码", Toast.LENGTH_LONG).show();
            return;
        }
//        Pattern pattern = Pattern.compile("[A-Z_a-z]+[0-9A-Z_a-z]{7,23}");
//        if (!pattern.matcher(fundsPwd).matches()) {
//            Toasts.show(this, "资金密码是由大小写字母开头的8-24个字符", false);
//            return;
//        }
//        Pattern pattern2 = Pattern.compile("[0-9]+"); //必须
//        if (!pattern2.matcher(fundsPwd).find()) {
//            Toasts.show(this, "资金密码是由大小写字母开头的8-24个字符,必须同时包含字符,数字", false);
//            return;
//        }

        if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA) || Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
            int len = etCardNum.getText().length();
            if (len != 16 && len != 18 && len != 19) {
                Toasts.show(this, "卡号只能为16、18或19位数字", false);
                return;
            }
        } else if (Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN)) {
            int len = etCardNum.getText().length();
            if (len > 19 || len < 16) {
                Toasts.show(this, "银行卡格式为16-19位，请填写正确格式", false);
                return;
            }
        }

        HttpAction.bindBankCard(ActBindBankCard.this, bankCode, branch, UserManager.getIns().getWithDrawName(),
                cardNum, fundsPwd, cardType, province, city, county, true, new AbHttpResult() {

                    @Override
                    public void onBefore(Request request, int id, String host, String funcName) {
                        btSubmit.post(new Runnable() {
                            @Override
                            public void run() {
                                Dialogs.showProgressDialog(ActBindBankCard.this, "");
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
                            Toasts.show(ActBindBankCard.this, "绑定成功", true);
                            finish();
                        } else {
                            Toasts.show(ActBindBankCard.this, msg, false);
                        }
                        return true;
                    }

                    @Override
                    public void onAfter(int id) {
                        btSubmit.post(new Runnable() {
                            @Override
                            public void run() {
                                Dialogs.hideProgressDialog(ActBindBankCard.this);
                            }
                        });
                    }
                });

    }


}
