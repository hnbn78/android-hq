package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayout;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ArraysAndLists;
import com.ab.util.Dates;
import com.ab.util.InputUtils;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.app.hubert.guide.core.Controller;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.DepositCategory;
import com.desheng.base.model.RechargeInfo;
import com.desheng.base.model.RechargeResult;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.GuidePageHelper;
import com.pearl.act.util.UIHelper;
import com.pearl.view.SoftKeyInput.KeyboardLayout;
import com.pearl.view.ToggleImageButton;
import com.pearl.view.expandablelayout.SimpleExpandableLayout;
import com.shark.tc.R;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import okhttp3.Request;

/**
 * 充值详情
 */
public class ActDepositDetail extends AbAdvanceActivity {
    public static final String MODE_THIRD = "mode_third";
    public static final String MODE_TRANS = "mode_trans";
    public static final String MODE_QR = "mode_qr";

    private List<RechargeInfo.CommonChargeListBean> commonList;
    private ImageView ivExpandbleArrow;
    private TextWatcher limitWatcher;
    private FlowLayout flFixedGroup;
    private ViewGroup vgFioxedGroup;
    private ViewGroup vgAmount;
    private KeyboardLayout vgDepositContent;
    private TextView tvFraction;
    private TextView tvAmountHint;
    private TextView tvPayPrompt;
    private TextView tvFriendlyPrompt;

    private SimpleExpandableLayout elPlayExtra;
    private View vgDragView;
    private Button btnNext;
    private EditText etAmount, etMessage;
    private Button btnMoney0, btnMoney1, btnMoney2, btnMoney3;
    private Button[] buttons;
    private ImageView[] arrIvPromotion;

    private GridLayout glPayFour;
    private GridLayout glPayExtra;
    private TextView tvBankSelect;
    private ViewGroup vgBank;
    private ViewGroup vgMessage;
    private NestedScrollView vgRechargeContent;

    private DepositCategory category;
    private RechargeInfo.CommonChargeListBean currEntity;
    private RechargeInfo rechargeInfo;
    private ToggleImageButton[] arrToggleBg;
    private ImageView[] arrChargeTypeIcon;
    private TextView[] arrChargeTypeTxt;
    private ArrayList<String> bankNameList;
    private ArrayList<Integer> fixedMoneyList;
    private ArrayList<RechargeInfo.BanklistBean> bankList;
    private List<RechargeInfo.CommonChargeListBean> listPayFour;
    private List<RechargeInfo.CommonChargeListBean> listPayExtra;

    private String mode = MODE_THIRD;
    private TextView tvAnnounce;
    private int fixedMoney;

    private ImageView guide_input_bg, guide_sure_bg;
    private TextView guide_input_tip, guide_sure_tip;
    private ScaleAnimation scaleAnimation;
    private boolean server_show;
    private boolean isComplete;

    public static void launch(Activity act, DepositCategory cate) {
        Intent itt = new Intent(act, ActDepositDetail.class);
        itt.putExtra("cate", cate);
        act.startActivity(itt);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_deposit_detail;
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();

        UserManager.getIns().isFirstRecharge(new UserManager.IGuideCallBack() {
            @Override
            public void onCallBack(boolean show) {
                server_show = show;
                setGuideUI(show);
            }
        });

        scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(ActDepositDetail.this, R.anim.scale_animation_big);
        category = (DepositCategory) getIntent().getSerializableExtra("cate");
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), category.categoryName + "充值");
        setToolbarLeftBtn(UIHelper.BACK_WHITE_CIRCLE_ARROW);
        elPlayExtra = (SimpleExpandableLayout) findViewById(R.id.elPlayExtra);
        vgDragView = findViewById(R.id.vgDragView);
        ivExpandbleArrow = findViewById(R.id.ivExpandbleArrow);
        vgDragView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elPlayExtra.toggle(true);
            }
        });
        elPlayExtra.setOnExpansionUpdateListener(new SimpleExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                if (state == SimpleExpandableLayout.State.EXPANDED) {
                    ivExpandbleArrow.setImageResource(R.mipmap.ic_half_arrow_up);
                } else if (state == SimpleExpandableLayout.State.COLLAPSED) {
                    ivExpandbleArrow.setImageResource(R.mipmap.ic_half_arrow_down);
                }
            }
        });

        bankNameList = new ArrayList<>();
        fixedMoneyList = new ArrayList<>();

        ImageView ivIcon = findViewById(R.id.ivIcon);
        Views.loadImageAny(Act, ivIcon, category.mobileCategoryPicture);

        vgBank = findViewById(R.id.vgBank);
        glPayFour = (GridLayout) findViewById(R.id.glPayFour);
        vgRechargeContent = findViewById(R.id.vgRechargeContent);
        glPayExtra = (GridLayout) findViewById(R.id.glPayExtra);
        vgMessage = (ViewGroup) findViewById(R.id.vgMessage);
        tvFraction = findViewById(R.id.tvFraction);
        tvAmountHint = findViewById(R.id.tvAmountHint);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setShowSureGuide(false);

                UserManager.getIns().setDepositGuideComplete(true);

                recharge();
            }
        });

        guide_input_bg = findViewById(R.id.guide_input_bg);
        guide_input_tip = findViewById(R.id.guide_tv_top_tip);
        guide_sure_bg = findViewById(R.id.guide_sure_bg);
        guide_sure_tip = findViewById(R.id.guide_sure_tip);

        tvBankSelect = (TextView) findViewById(R.id.tvBankSelect);

        vgAmount = (ViewGroup) findViewById(R.id.vgAmount);
        etAmount = (EditText) findViewById(R.id.etAmount);
        tvPayPrompt = (TextView) findViewById(R.id.tvPayPrompt);
        tvFriendlyPrompt = (TextView) findViewById(R.id.tvFriendlyPrompt);
        etMessage = (EditText) findViewById(R.id.etMessage);

        vgFioxedGroup = (ViewGroup) findViewById(R.id.vgFioxedGroup);
        flFixedGroup = (FlowLayout) findViewById(R.id.flFixedGroup);

        commonList = category.listCommon;
        int high = AbDevice.SCREEN_WIDTH_PX / 4;
        listPayFour = new ArrayList<>();
        listPayExtra = new ArrayList<>();
        //补齐4个
        if (commonList.size() <= 4) {
            listPayFour = commonList;
            int patchCnt = 4 - commonList.size();
            for (int i = 0; i < patchCnt; i++) {
                commonList.add(new RechargeInfo.CommonChargeListBean());
            }
            Views.setHeight(glPayFour, high);
            elPlayExtra.setVisibility(View.GONE);
            vgDragView.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < 4; i++) {
                listPayFour.add(commonList.get(i));
            }
            for (int i = 4; i < commonList.size(); i++) {
                listPayExtra.add(commonList.get(i));
            }
            int row = (listPayExtra.size()) / 4;
            if (listPayExtra.size() % 4 >= 1) {
                row += 1;
                int patchCnt = 4 - listPayExtra.size() % 4;
                for (int i = 0; i < patchCnt; i++) {
                    RechargeInfo.CommonChargeListBean nilBean = new RechargeInfo.CommonChargeListBean();
                    listPayExtra.add(nilBean);
                    commonList.add(nilBean);
                }
            }
            Views.setHeight(glPayFour, high);
            Views.setHeight(glPayExtra, row * high);
        }

        currEntity = commonList.get(0);
        setMode();
        setUI();
    }

    private void setMode() {
        switch (currEntity.chargeType) {
            case RechargeInfo.CHARGE_TYPE_THIRD:
                mode = MODE_THIRD;
                break;
            case RechargeInfo.CHARGE_TYPE_TRANS:
                mode = MODE_TRANS;
                break;
            case RechargeInfo.CHARGE_TYPE_QR:
                mode = MODE_QR;
                break;
        }
    }

    private void setUI() {

        LayoutInflater inflater = LayoutInflater.from(this);
        arrToggleBg = new ToggleImageButton[listPayFour.size() + listPayExtra.size()];
        arrChargeTypeIcon = new ImageView[arrToggleBg.length];
        arrIvPromotion = new ImageView[arrToggleBg.length];
        arrChargeTypeTxt = new TextView[arrToggleBg.length];
        //添加
        for (int i = 0; i < 4; i++) {
            RechargeInfo.CommonChargeListBean data = listPayFour.get(i);
            ViewGroup item = (ViewGroup) inflater.inflate(R.layout.item_deposit_type, glPayFour, false);
            arrToggleBg[i] = ((ToggleImageButton) item.findViewById(R.id.tibBg));
            arrChargeTypeIcon[i] = ((ImageView) item.findViewById(R.id.ivChargeType));
            arrIvPromotion[i] = ((ImageView) item.findViewById(R.id.ivPromotion));
            arrChargeTypeTxt[i] = ((TextView) item.findViewById(R.id.tvChargeType));
            if (data.chargeType == -1) {
                item.setVisibility(View.INVISIBLE);
            } else {
                item.setVisibility(View.VISIBLE);
                Views.loadImageAny(Act, arrChargeTypeIcon[i], getThirdPayCategoryIcon(data, false));
                arrChargeTypeTxt[i].setText(data.name);
                Views.loadImageAny(Act, arrIvPromotion[i], data.mobileActivityPicture);
            }
            item.setTag(i);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int) v.getTag();
                    for (int j = 0; j < arrToggleBg.length; j++) {
                        if (j != tag) {
                            if (commonList.get(j).chargeType != -1) {
                                arrToggleBg[j].setChecked(false);
                                Views.loadImageAny(Act, arrChargeTypeIcon[j], getThirdPayCategoryIcon(commonList.get(j), false));
                                arrChargeTypeTxt[j].setTextColor(Color.parseColor("#888888"));
                            }
                        } else {
                            arrToggleBg[j].setChecked(true);
                            Views.loadImageAny(Act, arrChargeTypeIcon[j], getThirdPayCategoryIcon(commonList.get(j), true));
                            arrChargeTypeTxt[j].setTextColor(Color.parseColor("#f95a74"));
                            syncSelection(j, false);
                        }
                    }
                }
            });
            if (i == 0) {
                final ViewGroup firstItem = item;
                glPayFour.post(new Runnable() {
                    @Override
                    public void run() {
                        firstItem.performClick();
                    }
                });
            }
            glPayFour.addView(item,
                    Views.genGridLayoutItemParam());
        }

        //添加
        for (int i = 0; i < listPayExtra.size(); i++) {
            RechargeInfo.CommonChargeListBean data = listPayExtra.get(i);
            ViewGroup item = (ViewGroup) inflater.inflate(R.layout.item_deposit_type, glPayExtra, false);
            arrToggleBg[i + 4] = ((ToggleImageButton) item.findViewById(R.id.tibBg));
            arrChargeTypeIcon[i + 4] = ((ImageView) item.findViewById(R.id.ivChargeType));
            arrIvPromotion[i + 4] = ((ImageView) item.findViewById(R.id.ivPromotion));
            arrChargeTypeTxt[i + 4] = ((TextView) item.findViewById(R.id.tvChargeType));
            if (data.chargeType == -1) {
                item.setVisibility(View.INVISIBLE);
            } else {
                item.setVisibility(View.VISIBLE);
                Views.loadImageAny(Act, arrChargeTypeIcon[i + 4], getThirdPayCategoryIcon(data, false));
                Views.loadImageAny(Act, arrIvPromotion[i + 4], data.mobileActivityPicture);
                arrChargeTypeTxt[i + 4].setText(data.name);
            }

            item.setTag(i + 4);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int) v.getTag();
                    for (int j = 0; j < arrToggleBg.length; j++) {
                        if (j != tag) {
                            if (commonList.get(j).chargeType != -1) {
                                arrToggleBg[j].setChecked(false);
                                Views.loadImageAny(Act, arrChargeTypeIcon[j], getThirdPayCategoryIcon(commonList.get(j), false));
                                arrChargeTypeTxt[j].setTextColor(Color.parseColor("#888888"));
                            }
                        } else {
                            arrToggleBg[j].setChecked(true);
                            Views.loadImageAny(Act, arrChargeTypeIcon[j], getThirdPayCategoryIcon(commonList.get(j), true));
                            arrChargeTypeTxt[j].setTextColor(Color.parseColor("#f95a74"));
                            syncSelection(j, true);
                        }
                    }
                }
            });

            glPayExtra.addView(item,
                    Views.genGridLayoutItemParam());
        }

        //limitWatcher = Views.setTextDecimalCountWatcher(etAmount, 2);
        vgAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Views.setEditTextFocus(etAmount);
            }
        });


        etAmount.addTextChangedListener(new TextWatcher() {
            int frag = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {

                if (server_show && !isComplete) {
                    setShowInputGuide(!Strs.isNotEmpty(s.toString()));
                    setShowSureGuide(Strs.isNotEmpty(s.toString()));
                }

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (currEntity.amountInputType == RechargeInfo.CommonChargeListBean.INPUT_TYPE_DECEMAL) {
                    if (Strs.isNotEmpty(s.toString()) && Strs.isEmpty(tvFraction.getText().toString())) {
                        if (frag == 0) {
                            frag = new Random().nextInt(99) + 1;
                        }
                        if (frag < 10) {
                            tvFraction.setText(".0" + frag);
                        } else {
                            tvFraction.setText("." + frag);
                        }
                    } else if (Strs.isEmpty(s.toString())) {
                        frag = 0;
                        tvFraction.setText("");
                    }
                } else if (currEntity.amountInputType == RechargeInfo.CommonChargeListBean.INPUT_TYPE_INTEGER) {
                    frag = 0;
                    tvFraction.setText("");
                }

                if (Strs.isNotEmpty(s.toString()) || Strs.isNotEmpty(tvFraction.getText().toString())) {
                    tvAmountHint.setVisibility(View.INVISIBLE);
                } else if (Strs.isEmpty(s.toString())) {
                    tvAmountHint.setVisibility(View.VISIBLE);
                }


            }
        });

        vgDepositContent = (KeyboardLayout) findViewById(R.id.vgDepositContent);
        vgDepositContent.setKeyboardListener(new KeyboardLayout.KeyboardLayoutListener() {
            @Override
            public void onKeyboardStateChanged(boolean isActive, int keyboardHeight) {
                if (!isActive) {
                    //checkAndSetAmountFraction();
                }
            }
        });

        etAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm != null && imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
                    if (timer != null) {
                        timer.cancel();
                    }
                    //checkAndSetAmountFraction();
                    return true;
                }
                return false;
            }
        });
    }

    private void setShowSureGuide(boolean show) {
        if (show) {
            guide_sure_bg.setVisibility(View.VISIBLE);
            guide_sure_bg.startAnimation(scaleAnimation);
        } else {
            guide_sure_bg.clearAnimation();
            guide_sure_bg.setVisibility(View.GONE);
        }

        guide_sure_tip.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setShowInputGuide(boolean show) {
        if (show) {
            guide_input_bg.setVisibility(View.VISIBLE);
            guide_input_bg.startAnimation(scaleAnimation);
        } else {
            guide_input_bg.clearAnimation();
            guide_input_bg.setVisibility(View.GONE);
        }
        guide_input_tip.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setGuideUI(boolean show) {
        isComplete = UserManager.getIns().getDepositGuideComplete();
        if (show && !isComplete) {
            guide_input_bg.setVisibility(View.VISIBLE);
            guide_input_tip.setVisibility(View.VISIBLE);
            guide_input_bg.startAnimation(scaleAnimation);

            guide_sure_bg.clearAnimation();
            guide_sure_bg.setVisibility(View.GONE);
            guide_sure_tip.setVisibility(View.GONE);

            setGuideWholePage();

        } else {
            guide_input_bg.clearAnimation();
            guide_input_bg.setVisibility(View.GONE);
            guide_input_tip.setVisibility(View.GONE);
            guide_sure_bg.clearAnimation();
            guide_sure_bg.setVisibility(View.GONE);
            guide_sure_tip.setVisibility(View.GONE);

        }
    }

    //判断用户输入完成begin
    private Timer timer = new Timer();
    private final long DELAY = 1000; // in ms
    // 判断用户输入完成end


    private void syncSelection(int position, boolean isExtra) {
        currEntity = commonList.get(position);
        setMode();
        tvAmountHint.setText("单笔限额: " + Nums.formatDecimal(currEntity.minUnitRecharge, 2) +
                "~" + Nums.formatDecimal(currEntity.maxUnitRecharge, 2) + "元");
        etAmount.setText("");
        Views.setEditorEnable(etAmount, true);
        etAmount.setOnClickListener(null);
        vgMessage.setVisibility(View.GONE);
        vgBank.setVisibility(View.GONE);
        etMessage.setText("");
        fixedMoneyList.clear();
        fixedMoney = 0;
        flFixedGroup.removeAllViews();
        tvFriendlyPrompt.setText(currEntity.mobilePayPrompt);
        tvPayPrompt.setText(currEntity.mobileFriendlyPrompt);

        if (currEntity.amountInputType == RechargeInfo.CommonChargeListBean.INPUT_TYPE_DECEMAL && null != guide_input_tip) {
            guide_input_tip.setText("输入金额自动形成小数点");
        } else if (currEntity.amountInputType == RechargeInfo.CommonChargeListBean.INPUT_TYPE_INTEGER && null != guide_input_tip) {
            guide_input_tip.setText("请输入金额");
        }

        if (currEntity.chargeType == RechargeInfo.CHARGE_TYPE_THIRD) {//third list处理
            bankList = new ArrayList<>(currEntity.banklist);

            if (bankList != null && bankList.size() != 0) {
                vgBank.setVisibility(View.VISIBLE);
                vgBank.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputUtils.hideKeyboard(v);
                        for (int i = 0; i < bankList.size(); i++) {
                            bankList.get(i).name = UserManagerTouCai.getIns().findBankName(bankList.get(i).code);
                        }
                        RechargeInfo.BanklistBean preBand = null;
                        if (!Views.fromStrings(R.string.please_choose_bank).equals(tvBankSelect.getText().toString())) {
                            preBand = (RechargeInfo.BanklistBean) tvBankSelect.getTag();
                        }
                        ActBankChooseList.launchForResult(ActDepositDetail.this, preBand, bankList);
                    }
                });
            }
        } else if (currEntity.chargeType == RechargeInfo.CHARGE_TYPE_TRANS) {
//            if (isShowFuYan(category.categoryName)) {
            vgMessage.setVisibility(View.VISIBLE);
            //etMessage.setText(currEntity.cardName);
            if (Strs.isNotEmpty(UserManagerTouCai.getIns().getWithDrawName())) {
                etMessage.setText(UserManagerTouCai.getIns().getWithDrawName());
            }
//            }
        } else if (currEntity.chargeType == RechargeInfo.CHARGE_TYPE_QR) {//qr list处理
            //nothing
        }

        if (currEntity.amountInputType == RechargeInfo.CommonChargeListBean.INPUT_TYPE_FIXED && Strs.isNotEmpty(currEntity.fixAmount)) {
            vgFioxedGroup.setVisibility(View.VISIBLE);
            vgAmount.setVisibility(View.GONE);
            String[] arr = currEntity.fixAmount.split("\\|");

            for (int i = 0; i < arr.length; i++) {
                if (Strs.isNotEmpty(arr[i])) {
                    fixedMoneyList.add(Nums.parse(arr[i], 0));
                    final TextView tvNum = new TextView(ActDepositDetail.this);
                    tvNum.setTextColor(Color.parseColor("#888888"));
                    tvNum.setText(arr[i]);
                    tvNum.setBackgroundResource(R.drawable.sh_bd_text_black_gray);
                    tvNum.setGravity(Gravity.CENTER);
                    tvNum.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int j = 0; j < flFixedGroup.getChildCount(); j++) {
                                TextView textView = (TextView) flFixedGroup.getChildAt(j);
                                if (v == textView) {
                                    textView.setTextColor(Color.parseColor("#f95a74"));
                                    fixedMoney = Nums.parse(textView.getText().toString(), 0);
                                } else {
                                    textView.setTextColor(Color.parseColor("#888888"));
                                }
                            }
                        }
                    });
                    ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams((int) ((AbDevice.SCREEN_WIDTH_PX - Views.dp2px(80)) / 4.0), Views.dp2px(50));
                    params.leftMargin = Views.dp2px(5);
                    params.rightMargin = Views.dp2px(5);
                    params.topMargin = Views.dp2px(5);
                    params.bottomMargin = Views.dp2px(5);
                    flFixedGroup.addView(tvNum, params);

                    if (i == 0) {
                        flFixedGroup.post(new Runnable() {
                            @Override
                            public void run() {
                                tvNum.performClick();
                            }
                        });
                    }
                }
            }
        } else {
            vgAmount.setVisibility(View.VISIBLE);
            vgFioxedGroup.setVisibility(View.GONE);
        }
    }

    public void recharge() {
        String pid = "";
        RechargeInfo.CommonChargeListBean bean = null;
        for (int j = 0; j < arrToggleBg.length; j++) {
            if (arrToggleBg[j].isChecked()) {
                pid = commonList.get(j).id;
                bean = commonList.get(j);
            }
        }
        if (Strs.isEmpty(pid) || bean == null) {
            Toasts.show(ActDepositDetail.this, "请选择支付渠道", false);
        } else if (bean.chargeType == RechargeInfo.CHARGE_TYPE_THIRD) {
            rechargeThird(pid, bean);
        } else if (bean.chargeType == RechargeInfo.CHARGE_TYPE_QR) {
            rechargeQR(pid, bean);
        } else if (bean.chargeType == RechargeInfo.CHARGE_TYPE_TRANS) {
            rechargTrans(pid, bean);
        }
    }


    public void rechargeThird(String pid, RechargeInfo.CommonChargeListBean bean) {
        if ("".equals(tvBankSelect.getText().toString()) && vgBank.getVisibility() == (View.VISIBLE)) {
            Toasts.show(ActDepositDetail.this, Views.fromStrings(R.string.please_choose_bank), false);
            return;
        }

        String bankco = "";
        if (bean.banklist == null ||
                bean.banklist.size() == 0) {
            bankco = "";
        } else {
            bankco = ((RechargeInfo.BanklistBean) tvBankSelect.getTag()).code;
        }

        double amount = -1.0;
        if (fixedMoneyList.size() > 0) {
            if (fixedMoney <= 0) {
                Toasts.show(ActDepositDetail.this, "请选择正确金额", false);
                return;
            } else {
                amount = fixedMoney;
            }
        } else {
            String strAmount = Views.getText(etAmount) + Views.getText(tvFraction);
            amount = Nums.parse(strAmount, -1.0);
            if (amount <= 0 || amount < currEntity.minUnitRecharge || amount > currEntity.maxUnitRecharge) {
                Toasts.show(ActDepositDetail.this, "请填写正确金额", false);
                return;
            }
            if (!Nums.isFractionDigits(strAmount, 2)
                    && currEntity.amountInputType == RechargeInfo.CommonChargeListBean.INPUT_TYPE_DECEMAL) {
                Toasts.show(ActDepositDetail.this, "请填写两位小数的金额", false);
                return;
            }
        }

        String finalBankco = bankco;
        double finalAmount = amount;
        showRechargePointHintOrShow(bean, Strs.of(amount), v -> {
            HttpAction.rechargeThird(ActDepositDetail.this, pid, finalBankco, Strs.of(finalAmount), new AbHttpResult() {
                @Override
                public void setupEntity(AbHttpRespEntity entity) {
                    entity.putField("data", RechargeResult.class);
                }

                @Override
                public void onBefore(Request request, int id, String host, String funcName) {
                    etAmount.post(new Runnable() {
                        @Override
                        public void run() {
                            DialogsTouCai.showProgressDialog(ActDepositDetail.this, "");
                        }
                    });
                }

                @Override
                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                    if (code == 0 && error == 0 && getFieldObject(extra, "data", null) != null) {
                        RechargeResult data = (RechargeResult) getFieldObject(extra, "data", null);
                        data.date = Dates.getCurrentDate(Dates.dateFormatYMDHMS);
                        ActDepositNextThird.launch(ActDepositDetail.this, currEntity, data, bean.name);
                        resetInput();
                    } else {
                        Toasts.show(ActDepositDetail.this, msg, false);
                    }
                    return true;
                }

                @Override
                public boolean onError(int status, String content) {
                    Toasts.show(ActDepositDetail.this, content, false);
                    return true;
                }

                @Override
                public void onAfter(int id) {
                    etAmount.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DialogsTouCai.hideProgressDialog(ActDepositDetail.this);
                        }
                    }, 400);
                }
            });
        });
    }

    public void rechargeQR(String pid, final RechargeInfo.CommonChargeListBean bean) {
        double amount = -1.0;
        if (fixedMoneyList.size() > 0) {
            if (fixedMoney <= 0) {
                Toasts.show(ActDepositDetail.this, "请选择正确金额", false);
                return;
            } else {
                amount = fixedMoney;
            }
        } else {
            String strAmount = Views.getText(etAmount) + Views.getText(tvFraction);
            amount = Nums.parse(strAmount, -1.0);
            if (amount <= 0 || amount < currEntity.minUnitRecharge || amount > currEntity.maxUnitRecharge) {
                Toasts.show(ActDepositDetail.this, "请填写正确金额", false);
                return;
            }
            if (!Nums.isFractionDigits(strAmount, 2)
                    && currEntity.amountInputType == RechargeInfo.CommonChargeListBean.INPUT_TYPE_DECEMAL) {
                Toasts.show(ActDepositDetail.this, "请填写两位小数的金额", false);
                return;
            }
        }

        double finalAmount = amount;
        showRechargePointHintOrShow(bean, Strs.of(amount), v -> {
            HttpAction.rechargeQR(ActDepositDetail.this, pid, Strs.of(finalAmount), Strs.of(bean.codeType), bean.id, new AbHttpResult() {
                @Override
                public void setupEntity(AbHttpRespEntity entity) {
                    entity.putField("data", RechargeResult.class);
                }

                @Override
                public void onBefore(Request request, int id, String host, String funcName) {
                    etAmount.post(new Runnable() {
                        @Override
                        public void run() {
                            DialogsTouCai.showProgressDialog(ActDepositDetail.this, "");
                        }
                    });
                }

                @Override
                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                    if (code == 0 && error == 0 && getFieldObject(extra, "data", null) != null) {
                        RechargeResult data = (RechargeResult) getFieldObject(extra, "data", null);
                        data.date = Dates.getCurrentDate(Dates.dateFormatYMDHMS);
                        ActDepositNextQR.launchForStage1(ActDepositDetail.this, category, data, bean.fileByte, bean.name);
                        resetInput();
                    } else {
                        Toasts.show(ActDepositDetail.this, msg, false);
                    }
                    return true;
                }

                @Override
                public boolean onError(int status, String content) {
                    Toasts.show(ActDepositDetail.this, content, false);
                    return true;
                }

                @Override
                public void onAfter(int id) {
                    etAmount.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DialogsTouCai.hideProgressDialog(ActDepositDetail.this);
                        }
                    }, 400);
                }
            });
        });
    }

    /**
     * pid: 22
     * amount: 100
     * fuYan: user345:我爱一条柴
     * payWay: 0
     *
     * @param pid
     * @param bean
     */
    private void rechargTrans(String pid, final RechargeInfo.CommonChargeListBean bean) {
        String message = "";
        if (vgMessage.getVisibility() == (View.VISIBLE)) {
            message = Views.getText(etMessage);
            if (Strs.isEmpty(message)) {
                Toasts.show(ActDepositDetail.this, "请填写取款人姓名", false);
                return;
            }
        }

        double amount = -1.0;
        if (fixedMoneyList.size() > 0) {
            if (fixedMoney <= 0) {
                Toasts.show(ActDepositDetail.this, "请选择正确金额", false);
                return;
            } else {
                amount = fixedMoney;
            }
        } else {
            String strAmount = Views.getText(etAmount) + Views.getText(tvFraction);
            amount = Nums.parse(strAmount, -1.0);
            if (amount <= 0 || amount < currEntity.minUnitRecharge || amount > currEntity.maxUnitRecharge) {
                Toasts.show(ActDepositDetail.this, "请填写正确金额", false);
                return;
            }
            if (!Nums.isFractionDigits(strAmount, 2)
                    && currEntity.amountInputType == RechargeInfo.CommonChargeListBean.INPUT_TYPE_DECEMAL) {
                Toasts.show(ActDepositDetail.this, "请填写两位小数的金额", false);
                return;
            }
        }

        double finalAmount = amount;
        String finalMessage = message;
        showRechargePointHintOrShow(bean, Strs.of(amount), v -> {
            HttpAction.rechargeTrans(ActDepositDetail.this, pid, Strs.of(finalAmount), Strs.of(bean.bankType), UserManager.getIns().getAccount() + ":" + finalMessage, new AbHttpResult() {
                @Override
                public void setupEntity(AbHttpRespEntity entity) {
                    entity.putField("data", RechargeResult.class);
                }

                @Override
                public void onBefore(Request request, int id, String host, String funcName) {
                    etAmount.post(new Runnable() {
                        @Override
                        public void run() {
                            DialogsTouCai.showProgressDialog(ActDepositDetail.this, "");
                        }
                    });
                }

                @Override
                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                    if (code == 0 && error == 0 && getFieldObject(extra, "data", null) != null) {
                        RechargeResult data = (RechargeResult) getFieldObject(extra, "data", null);
                        data.date = Dates.getCurrentDate(Dates.dateFormatYMDHMS);
                        ActDepositNextTrans.launchForStage1(ActDepositDetail.this, category, data, bean.getTransferBean(), bean.name);
                        resetInput();
                    } else {
                        Toasts.show(ActDepositDetail.this, msg, false);
                    }
                    return true;
                }

                @Override
                public boolean onError(int status, String content) {
                    Toasts.show(ActDepositDetail.this, content, false);
                    return true;
                }

                @Override
                public void onAfter(int id) {
                    etAmount.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DialogsTouCai.hideProgressDialog(ActDepositDetail.this);
                        }
                    }, 400);
                }
            });
        });
    }

    public void resetInput() {
        etAmount.setText("");
        tvBankSelect.setText("");
        tvBankSelect.setTag(null);
        etMessage.setText("");
        tvFraction.setText("");
        tvAmountHint.setVisibility(View.INVISIBLE);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        RechargeInfo.BanklistBean bean = (RechargeInfo.BanklistBean) data.getSerializableExtra("result");
        if (bean != null) {
            tvBankSelect.setTag(bean);
            tvBankSelect.setText(bean.name);
        }
    }

    private int getThirdPayCategoryIcon(RechargeInfo.CommonChargeListBean cateName, boolean isActive) {
        if (cateName.chargeType == RechargeInfo.CHARGE_TYPE_THIRD) {
            return isActive ? R.mipmap.ic_dianzizhifu_activited : R.mipmap.ic_dianzizhifu_normal;
        } else if (cateName.chargeType == RechargeInfo.CHARGE_TYPE_QR) {
            return isActive ? R.mipmap.ic_saoma_activited : R.mipmap.ic_saoma_normal;
        } else if (cateName.chargeType == RechargeInfo.CHARGE_TYPE_TRANS) {
            return isActive ? R.mipmap.ic_zhuangzhang_activited : R.mipmap.ic_zhuangzhang_normal;
        } else {
            return isActive ? R.mipmap.ic_pay_wangyin_activited : R.mipmap.ic_pay_wangyin_normal;
        }
    }


    /**
     * 网银转账  微信银行  支付宝银行  5 1 0
     *
     * @return
     */
    private boolean isShowFuYan(String code) {
        return ArraysAndLists.findIndexWithEqualsOfArray(code,
                new String[]{"微信", "银联", "网银"}) > -1;
    }

    private void showRechargePointHintOrShow(RechargeInfo.CommonChargeListBean bean, String money, View.OnClickListener listener) {
        if (bean.name != null && bean.amountInputType == RechargeInfo.CommonChargeListBean.INPUT_TYPE_DECEMAL) {
            final Dialog dialog = new Dialog(this, R.style.custom_dialog_style);
            ViewGroup root = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.dialog_msg, null);
            ((TextView) root.findViewById(R.id.tv_title1)).setText("温馨提示");
            TextView container = root.findViewById(R.id.tv_content);
            container.setText("请按照订单金额");
            SpannableString m = new SpannableString("￥" + money);
            m.setSpan(new ForegroundColorSpan(0xFFFF3939), 0, m.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            m.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, m.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            container.append(m);
            container.append("进行充值否则无法正常到账");

            root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v);
                    dialog.dismiss();
                }
            });

            dialog.setContentView(root);
            dialog.show();
        } else {
            listener.onClick(null);
        }
    }

    private static final String GUIDE_DEPOSIT_INSIDE = "guide_deposit_inside";

    private void setGuideWholePage() {
        GuidePageHelper.Builder builder = GuidePageHelper.build(this, UserManager.getIns().getMainUserName(), GUIDE_DEPOSIT_INSIDE);
        GuidePageHelper.TouCaiGuidePage guidePagePlus = GuidePageHelper.TouCaiGuidePage.newInstance()
                .addHighLight(new RectF())
                .setLayoutAnchor(R.id.content, Gravity.TOP | Gravity.RIGHT)
                .setLayoutRes(R.layout.view_guide_page_deposit_inside, R.id.iv_guide);

        builder.addGuidePage(GUIDE_DEPOSIT_INSIDE, guidePagePlus);
        builder.setLastPageCancelable(true);
        builder.setOnPageChangedListener(new GuidePageHelper.OnPageChangeListener() {
            @Override
            public void onInflate(Controller controller, String tag, int pos, View root) {

            }

            @Override
            public void onPageChanged(String tag, int pos, GuidePageHelper.TouCaiGuidePage guidePage, GuidePageHelper.TouCaiGuideController controller) {
                if ("plus".equals(tag)) {

                }

            }

            @Override
            public void onShow() {

            }

            @Override
            public void onRemove() {
//                UserManager.getIns().setRechargeSHowGuideInSide(false);
            }
        });

        builder.show();

    }
}
