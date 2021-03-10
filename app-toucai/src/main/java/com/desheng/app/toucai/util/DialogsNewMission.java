package com.desheng.app.toucai.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.callback.AbCallback;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.NewUserMission;
import com.desheng.app.toucai.model.TotalPrize;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.UserBindStatus;
import com.orhanobut.logger.Logger;
import com.shark.tc.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.desheng.app.toucai.panel.ActInput.WXREG;

public class DialogsNewMission {
    public static final String BIND_MOBILE = "bindMobile";
    public static final String BIND_NAME = "bindName";
    public static final String BIND_WECHAT = "bindWechat";

    private static final String STEP_NAME = "STEP_NAME";
    private static final String STEP_WECHAT = "STEP_WECHAT";
    private static final String STEP_MOBILE = "STEP_MOBILE";

    private static final String STEP_MIDDLE_RESULT = "STEP_MIDDLE_RESULT";
    private static final String STEP_FINAL_RESULT = "STEP_FINAL_RESULT";
    private static final Queue<Integer> QueueTitle = new LinkedBlockingQueue<>();

    private static HashMap<String, MaterialDialog> MapDialog = new HashMap<>();
    private static CountDownTimer Timer;
    private static NewUserMission Mission;
    private static UserBindStatus BindStatus;
    private static Activity Act;
    private static View.OnClickListener smsClickListener;
    private static String smsPhotoVerify;
    private static MissionCallBack callBack;

    public static boolean start(Activity act, NewUserMission mission, UserBindStatus bindStatus, MissionCallBack callBack) {
        mission.parseStepList();
        Act = act;
        DialogsNewMission.callBack = callBack;
        Mission = mission;
        BindStatus = bindStatus;

        QueueTitle.clear();
        QueueTitle.offer(R.mipmap.ic_mission_name);
        QueueTitle.offer(R.mipmap.ic_mission_wexin);
        QueueTitle.offer(R.mipmap.ic_mission_phoie);

        long startMillis = Dates.getDateByFormat(mission.startTime, Dates.dateFormatYMDHMS).getTime();
        long stopMillis = Dates.getDateByFormat(mission.endTime, Dates.dateFormatYMDHMS).getTime();
        long currMills = System.currentTimeMillis();
        if (currMills >= startMillis && currMills <= stopMillis) {
            if (next()) {
                callBack.onStart();
                return true;
            }
            //showFinalResult("100");
        }

        return false;
    }

    public static boolean next() {
        String step = null;
        boolean isFinal = true;
        String alreadyMoney = null;
        String nextMoney = null;
        for (int i = 0; i < Mission.listSteps.size(); i++) {
            NewUserMission.MissionStep missionStep = Mission.listSteps.get(i);
            String eventCode = missionStep.eventCode;
            if (BIND_NAME.equals(eventCode) && !BindStatus.isBindWithdrawName()) {
                if (Strs.isEmpty(step)) {
                    step = STEP_NAME;
                    alreadyMoney = missionStep.prizeAmount;
                } else if (Strs.isNotEmpty(step)) {
                    isFinal = false;
                    nextMoney = missionStep.prizeAmount;
                    break;
                }
            } else if (BIND_WECHAT.equals(eventCode) && !BindStatus.isBindWeChat()) {
                if (Strs.isEmpty(step)) {
                    step = STEP_WECHAT;
                    alreadyMoney = missionStep.prizeAmount;
                } else if (Strs.isNotEmpty(step)) {
                    isFinal = false;
                    nextMoney = missionStep.prizeAmount;
                    break;
                }
            } else if (BIND_MOBILE.equals(eventCode) && !BindStatus.isBindCellphone()) {
                if (Strs.isEmpty(step)) {
                    step = STEP_MOBILE;
                    alreadyMoney = missionStep.prizeAmount;
                } else if (Strs.isNotEmpty(step)) {
                    isFinal = false;
                    nextMoney = missionStep.prizeAmount;
                    break;
                }
            }
        }

        if (Strs.isNotEmpty(step)) {
            show(step, isFinal, alreadyMoney, nextMoney);
            return true;
        } else {
            return false;
        }

    }

    public static void stop(boolean finished) {
        Mission = null;
        if (Timer != null) {
            Timer.cancel();
            Timer = null;
        }

        for (Map.Entry<String, MaterialDialog> entry : MapDialog.entrySet()) {
            if (entry.getValue() != null) {
                hide(entry.getKey());
            }
        }

        Act = null;

        if (callBack != null) {
            callBack.onStop(finished);
            callBack = null;
        }
    }

    public static void show(String step, boolean isFinal, String alreadyMoney, String nextMoney) {
        if (STEP_NAME.equals(step)) {
            showStepName(isFinal, alreadyMoney, nextMoney);
        } else if (STEP_WECHAT.equals(step) && !BindStatus.isBindWeChat()) {
            showStepWeChat(isFinal, alreadyMoney, nextMoney);
        } else if (STEP_MOBILE.equals(step) && !BindStatus.isBindPhone()) {
            showStepMobile(isFinal, alreadyMoney, nextMoney);
        }
    }

    public static void hide(String dialogName) {
        if (MapDialog.get(dialogName) != null) {
            try {
                MapDialog.get(dialogName).dismiss();
                MapDialog.remove(dialogName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void showStepName(boolean isFinal, String alreadyMoney, String nextMoney) {
        if (MapDialog.get(STEP_NAME) != null) {
            MapDialog.get(STEP_NAME).dismiss();
        }

        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(Act).inflate(R.layout.dialog_new_mission_step_name, null);
        ImageView ivDeleteBtn = (ImageView) viewGroup.findViewById(R.id.ivDeleteBtn);
        Views.loadImageAny(Act, viewGroup.findViewById(R.id.ivTitle), QueueTitle.poll());
        ivDeleteBtn.setOnClickListener((view) -> {
            stop(false);
        });
        Button btnOpenRed = (Button) viewGroup.findViewById(R.id.btnOpenRed);
        EditText tvRealName = (EditText) viewGroup.findViewById(R.id.tvRealName);
        setupOpenRedBtn(tvRealName, btnOpenRed);
        btnOpenRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = tvRealName.getText().toString();
                if (!Views.checkTextAndToast(Act, name, "请填写姓名")) {
                    return;
                }
                if (Strs.isEmpty(name)) {
                    Toasts.show("姓名格式不正确！");
                    return;
                }
                if (name.length() < 2) {
                    Toasts.show("姓名至少是两位中文！");
                    return;
                }
                if (!Strs.isAllChineseWithDot(name)) {
                    Toasts.show("姓名必须是中文格式！");
                    return;
                }
                HttpAction.bindTrueName(null, name, new AbHttpResult() {

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        Toasts.show(msg, true);

                        if (code == 0 && error == 0) {
                            BindStatus.setBindWithdrawName(true);
                            UserManager.getIns().setIsBindWithdrawName(true);
                            UserManagerTouCai.getIns().setBankCardRealName(name);
                            UserManager.getIns().setWithDrawName(name);
                            hide(STEP_NAME);
                            if (isFinal) {
                                showFinalResult(alreadyMoney);
                            } else {
                                showStepMiddleResult(alreadyMoney, nextMoney);
                            }
                        }

                        return true;
                    }

                });
            }
        });

        MaterialDialog step1 = Dialogs.showCustomDialog(Act, viewGroup, true);
        step1.showWithAnim();
        MapDialog.put(STEP_NAME, step1);
    }

    public static void showStepWeChat(boolean isFinal, String alreadyMoney, String nextMoney) {
        if (MapDialog.get(STEP_WECHAT) != null) {
            MapDialog.get(STEP_WECHAT).dismiss();
        }

        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(Act).inflate(R.layout.dialog_new_mission_step_wechat, null);
        ImageView ivDeleteBtn = (ImageView) viewGroup.findViewById(R.id.ivDeleteBtn);
        Views.loadImageAny(Act, viewGroup.findViewById(R.id.ivTitle), QueueTitle.poll());
        ivDeleteBtn.setOnClickListener((view) -> {
            stop(false);
        });
        Button btnOpenRed = (Button) viewGroup.findViewById(R.id.btnOpenRed);
        EditText tvWeiChat = (EditText) viewGroup.findViewById(R.id.tvWeiChat);
        setupOpenRedBtn(tvWeiChat, btnOpenRed);
        btnOpenRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = tvWeiChat.getText().toString();
                if (!Views.checkTextAndToast(Act, name, "请填写微信号")) {
                    return;
                }
                if (!name.matches(WXREG)) {
                    Toasts.show("请输入正确的微信号！");
                    return;
                }
                UserManager.getIns().bindPersonInfo(3, name, new UserManager.IUserInfoSyncCallBack() {
                    @Override
                    public void onSync(String msg) {
                        BindStatus.setBindWeChat(true);
                        hide(STEP_WECHAT);
                        if (isFinal) {
                            showFinalResult(alreadyMoney);
                        } else {
                            showStepMiddleResult(alreadyMoney, nextMoney);
                        }
                    }
                });
            }
        });

        MaterialDialog step2 = Dialogs.showCustomDialog(Act, viewGroup, true);
        step2.showWithAnim();
        MapDialog.put(STEP_WECHAT, step2);
    }


    public static void showStepMobile(boolean isFinal, String alreadyMoney, String nextMoney) {
        if (MapDialog.get(STEP_MOBILE) != null) {
            MapDialog.get(STEP_MOBILE).dismiss();
        }

        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(Act).inflate(R.layout.dialog_new_mission_step_phone, null);
        ImageView ivDeleteBtn = (ImageView) viewGroup.findViewById(R.id.ivDeleteBtn);
        Views.loadImageAny(Act, viewGroup.findViewById(R.id.ivTitle), QueueTitle.poll());
        ivDeleteBtn.setOnClickListener((view) -> {
            stop(false);
        });
        final boolean[] isSendCode = {false};
        Button btnOpenRed = (Button) viewGroup.findViewById(R.id.btnOpenRed);
        EditText etPhone = (EditText) viewGroup.findViewById(R.id.etPhone);
        TextView tvSmsCode = viewGroup.findViewById(R.id.tvSmsCode);
        EditText etSmsCode = viewGroup.findViewById(R.id.etSmsCode);
        smsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString();
                if (!Views.checkPhoneAndToast(Act, phone, "请输入正确手机号码!")) {
                    return;
                }
                isSendCode[0] = true;
                final AbCallback successCallback = new AbCallback<String>() {
                    @Override
                    public boolean callback(String obj) {
                        tvSmsCode.setText("剩余60秒");
                        tvSmsCode.setBackgroundResource(R.drawable.sh_bd_gray_gray_oval);
                        if (Timer == null) {
                            Timer = new CountDownTimer(60 * 1000, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {
                                    tvSmsCode.setText("剩余" + millisUntilFinished / 1000 + "秒");
                                    tvSmsCode.setTextColor(Color.parseColor("#A8A8A8"));
                                    tvSmsCode.setOnClickListener(null);
                                }

                                @Override
                                public void onFinish() {
                                    tvSmsCode.setText("获取验证码");
                                    tvSmsCode.setTextColor(Views.fromColors(R.color.colorPrimary));
                                    tvSmsCode.setOnClickListener(smsClickListener);
                                    tvSmsCode.setBackgroundResource(R.drawable.sh_bd_rec_red_white_oval);
                                    Timer = null;
                                }

                            }.start();
                        }
                        return true;
                    }
                };

                final AbCallback failureCallback = new AbCallback<String>() {
                    @Override
                    public boolean callback(String obj) {
                        //下次使用
                        if (obj != null) {
                            smsPhotoVerify = obj;
                            DialogsTouCai.hideVerifyPhotoDialog(Act);
                            UserManager.getIns(UserManagerTouCai.class).getSmsCodeWithTimer(Act, true, phone, smsPhotoVerify, successCallback, this);
                        }
                        tvSmsCode.setOnClickListener(smsClickListener);
                        return true;
                    }
                };

                UserManager.getIns(UserManagerTouCai.class).getSmsCodeWithTimer(Act, true, phone, null, successCallback, failureCallback);

            }
        };
        etSmsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnOpenRed.setEnabled(isSendCode[0] && s.length() > 0);
            }
        });
        tvSmsCode.setOnClickListener(smsClickListener);
        btnOpenRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString();
                if (!Views.checkPhoneAndToast(Act, phone, "请输入正确手机号码!")) {
                    return;
                }
                String smsVerifyCode = Views.getText(etSmsCode);
                if (!Views.checkTextAndToast(Act, smsVerifyCode, "请输入短信验证码!")) {
                    return;
                }

                HttpAction.bindPhone(Act, phone, smsVerifyCode, new AbHttpResult() {
                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        if (code == 0 && error == 0) {
                            Toasts.show(Act, msg, true);
                            BindStatus.setBindPhone(true);
                            UserManager.getIns().setIsBindCellphone(true);
                            UserManager.getIns().setInfoCellphone(phone);
                            hide(STEP_MOBILE);
                            if (isFinal) {
                                showFinalResult(alreadyMoney);
                            } else {
                                showStepMiddleResult(alreadyMoney, nextMoney);
                            }
                        } else {
                            Toasts.show(Act, msg, false);
                        }
                        return true;
                    }

                });
            }
        });

        MaterialDialog step3 = Dialogs.showCustomDialog(Act, viewGroup, true);
        step3.showWithAnim();
        MapDialog.put(STEP_MOBILE, step3);
    }

    public static void showStepMiddleResult(String alreadyMoney, String nextMoney) {
        if (MapDialog.get(STEP_MIDDLE_RESULT) != null) {
            MapDialog.get(STEP_MIDDLE_RESULT).dismiss();
        }

        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(Act).inflate(R.layout.dialog_new_mission_step_middle_result, null);
        ((TextView) viewGroup.findViewById(R.id.tvAlready)).setText(String.format("恭喜您已获取%s红包", Views.fromStrings(R.string.money) + Strs.of(alreadyMoney)));
        ((Button) viewGroup.findViewById(R.id.btnMore)).setText(String.format("再领%s", Views.fromStrings(R.string.money) + Strs.of(nextMoney)));
        ImageView ivDeleteBtn = (ImageView) viewGroup.findViewById(R.id.ivDeleteBtn);
        ivDeleteBtn.setOnClickListener((view) -> {
            stop(false);
        });
        Button btnUse = (Button) viewGroup.findViewById(R.id.btnUse);
        btnUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        addMission();
                CtxLotteryTouCai.launchLotteryPlay(Act, 11);
                stop(false);

            }
        });
        Button btnMore = (Button) viewGroup.findViewById(R.id.btnMore);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide(STEP_MIDDLE_RESULT);
                //       addMission();
                next();
            }
        });

        MaterialDialog step_result = Dialogs.showCustomDialog(Act, viewGroup, true);
        step_result.showWithAnim();
        MapDialog.put(STEP_MIDDLE_RESULT, step_result);
    }

    private static void addMission() {
        HttpActionTouCai.getTotalPrizeAmount(null, Mission.id, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", TotalPrize.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    TotalPrize prize = getFieldObject(extra, "data", null);
                    Logger.d("prize ===> " + prize.getTotalPrizeAmount());
                } else {
                    Toasts.show(Act, msg, false);
                }
                return true;
            }

        });
    }

    public static void showFinalResult(String alreadMoney) {
        if (MapDialog.get(STEP_FINAL_RESULT) != null) {
            MapDialog.get(STEP_FINAL_RESULT).dismiss();
        }

        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(Act).inflate(R.layout.dialog_new_mission_step_final_result, null);
        ((TextView) viewGroup.findViewById(R.id.tvAlready)).setText(String.format("恭喜您已获取%s红包", Views.fromStrings(R.string.money) + Strs.of(alreadMoney)));
        ImageView ivDeleteBtn = (ImageView) viewGroup.findViewById(R.id.ivDeleteBtn);
        ivDeleteBtn.setOnClickListener((view) -> {
            stop(true);
        });
        Button btnUse = (Button) viewGroup.findViewById(R.id.btnUse);
        btnUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CtxLotteryTouCai.launchLotteryPlay(Act, 11);
                stop(true);
            }
        });
        TextView tvUseInfo1 = (TextView) viewGroup.findViewById(R.id.tvUseInfo1);

        HttpActionTouCai.getTotalPrizeAmount(null, Mission.id, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", TotalPrize.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    TotalPrize prize = getFieldObject(extra, "data", null);
                    tvUseInfo1.setText(String.format("您已总共获得%s红包!", Views.fromStrings(R.string.money_sign) + prize.getTotalPrizeAmount()));
                } else {
                    Toasts.show(Act, msg, false);
                }
                return true;
            }

        });

        MaterialDialog step_final_result = Dialogs.showCustomDialog(Act, viewGroup, true);
        step_final_result.showWithAnim();
        MapDialog.put(STEP_FINAL_RESULT, step_final_result);
    }

    private static void setupOpenRedBtn(EditText editText, Button button) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                button.setEnabled(s.length() > 0);
            }
        });
    }


    public interface MissionCallBack {
        void onStart();

        void onStop(boolean finished);
    }
}
