package com.desheng.app.toucai.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.callback.AbCallback;
import com.ab.global.ENV;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.adapter.SimpleListviewAdapter;
import com.desheng.app.toucai.adapter.WheelAdapter;
import com.desheng.app.toucai.adapter.ZhongjiangResultAapter;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.app.toucai.model.BonusPoolNoetWinMode;
import com.desheng.app.toucai.model.BonusPoolWinMode;
import com.desheng.app.toucai.model.CommonPaymentEntity;
import com.desheng.app.toucai.model.PaymentCategoryInfo;
import com.desheng.app.toucai.model.QrCodeListBean;
import com.desheng.app.toucai.model.RedPacketGetDetail;
import com.desheng.app.toucai.model.ThridListBean;
import com.desheng.app.toucai.model.TransferListBean;
import com.desheng.app.toucai.panel.ActDeposit;
import com.desheng.app.toucai.panel.ActRechargePay;
import com.desheng.app.toucai.panel.ActThirdGameMain;
import com.desheng.app.toucai.panel.ActXunibiCharge;
import com.desheng.app.toucai.view.MultiScrollNumber;
import com.desheng.app.toucai.view.RoundBackgroundColorSpan;
import com.desheng.base.manager.UserManager;
import com.desheng.base.util.ResUtil;
import com.google.gson.Gson;
import com.pearl.act.AbActManager;
import com.pearl.view.FrameAnimation;
import com.shark.tc.R;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lee on 2017/9/27.
 */

public class DialogsTouCai {

    private static View progressView;
    public static Dialog progressFragment = null;
    public static MaterialDialog verifyPhotoDialog;
    private static MaterialDialog dialogChangPwdSuccess;
    private static MaterialDialog dialogRegistSuccess;
    private static MaterialDialog tipDialog;
    private static CountDownTimer timer;

    /**
     * @param act
     */
    public static Dialog showProgressDialog(final Activity act, String msg) {
        if (progressFragment == null && act != null) {
            if (progressView == null) {
                TypedArray typedArray = act.getResources().obtainTypedArray(R.array.toucai_progress);
                int len = typedArray.length();
                int[] resId = new int[len];
                for (int i = 0; i < len; i++) {
                    resId[i] = typedArray.getResourceId(i, -1);
                }
                typedArray.recycle();
                progressView = LayoutInflater.from(act).inflate(R.layout.dialog_progress_toucai, null);
                ImageView image = (ImageView) progressView.findViewById(R.id.image);

                FrameAnimation frameAnimation = new FrameAnimation(image, resId, 300, true);
                frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
                    @Override
                    public void onAnimationStart() {
                    }

                    @Override
                    public void onAnimationEnd() {
                    }

                    @Override
                    public void onAnimationRepeat() {
                    }
                });
            }
            progressFragment = new Dialog(act, R.style.progress_dialog_toucai);
            if (progressView.getParent() != null) {
                ((ViewGroup) progressView.getParent()).removeView(progressView);
            }
            progressFragment.setContentView(progressView);
            progressFragment.setCancelable(true);

            TextView tvMsg = (TextView) progressFragment.findViewById(R.id.id_tv_loadingmsg);
            if (Strs.isNotEmpty(msg)) {
                tvMsg.setText(msg);
            } else {
                tvMsg.setVisibility(View.GONE);
            }
            progressFragment.setCanceledOnTouchOutside(false);
            try {
                progressFragment.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return progressFragment;
    }


    /**
     * 当确定按钮点击时调用callback.stringCallback;
     */
    public static void updateProgressDialog(String msg) {
        if (progressFragment != null) {
            progressFragment.setTitle(msg);
        }
    }

    public static void hideProgressDialog(final Activity act) {
        if (progressFragment != null) {
            try {
                progressFragment.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressFragment = null;
        }
    }

    public static void showBindDialog(Activity act, String tips1, String tips2, String left, String right, boolean cancelTouchOutside, AbCallback<Object> callback) {
        if (tipDialog != null) {
            tipDialog.dismiss();
        }

        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.dialog_tip_bind, null);
        ((TextView) viewGroup.findViewById(R.id.tvTips01)).setText(tips1);
        ((TextView) viewGroup.findViewById(R.id.tvTips02)).setText(tips2);


        TextView btnConfirm = viewGroup.findViewById(R.id.btnConfirm);
        TextView btnCancel = viewGroup.findViewById(R.id.btnCancel);

        if (Strs.isNotEmpty(left)) {
            btnCancel.setText(left);
        }

        if (Strs.isNotEmpty(right)) {
            btnConfirm.setText(right);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != callback) {
                    callback.callback(null);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
            }
        });
        tipDialog = Dialogs.showCustomDialog(act, viewGroup, cancelTouchOutside);

    }

    public static void showUpdateDialog(Activity act, String tips2, String left, String right,
                                        boolean cancelTouchOutside, AbCallback<Object> leftcallback, AbCallback<Object> rightcallback) {
        if (tipDialog != null) {
            tipDialog.dismiss();
        }

        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.dialog_tip_bind, null);
        ((TextView) viewGroup.findViewById(R.id.tvTips01)).setVisibility(View.GONE);
        ((TextView) viewGroup.findViewById(R.id.tvTips02)).setText(tips2);


        TextView btnConfirm = viewGroup.findViewById(R.id.btnConfirm);
        TextView btnCancel = viewGroup.findViewById(R.id.btnCancel);

        if (Strs.isNotEmpty(left)) {
            btnCancel.setText(left);
        }

        if (Strs.isNotEmpty(right)) {
            btnConfirm.setText(right);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != rightcallback) {
                    rightcallback.callback(null);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != leftcallback) {
                    leftcallback.callback(null);
                }
                tipDialog.dismiss();
            }
        });
        tipDialog = Dialogs.showCustomDialog(act, viewGroup, cancelTouchOutside);

    }

    public static void showBindDialog(Activity act, String tips1, String tips2, String left, String right,
                                      boolean cancelTouchOutside, AbCallback<Object> leftcallback, AbCallback<Object> rightcallback) {
        if (tipDialog != null) {
            tipDialog.dismiss();
        }

        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.dialog_tip_bind, null);
        ((TextView) viewGroup.findViewById(R.id.tvTips01)).setText(tips1);
        ((TextView) viewGroup.findViewById(R.id.tvTips02)).setText(tips2);


        TextView btnConfirm = viewGroup.findViewById(R.id.btnConfirm);
        TextView btnCancel = viewGroup.findViewById(R.id.btnCancel);

        if (Strs.isNotEmpty(left)) {
            btnCancel.setText(left);
        }

        if (Strs.isNotEmpty(right)) {
            btnConfirm.setText(right);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != rightcallback) {
                    rightcallback.callback(null);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != leftcallback) {
                    leftcallback.callback(null);
                }
                tipDialog.dismiss();
            }
        });
        tipDialog = Dialogs.showCustomDialog(act, viewGroup, cancelTouchOutside);

    }

    public static MaterialDialog getTipDialog() {
        return tipDialog;
    }

    public static void showSuccessDialog(Activity act, int bgid, String tip_success, String text_finish) {
        if (tipDialog != null) {
            tipDialog.dismiss();
        }

        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.dialog_tip_success, null);

        ImageView iv_bg_success = viewGroup.findViewById(R.id.iv_bg_success);
        TextView tv_tip_success = viewGroup.findViewById(R.id.tv_tip_success);
        TextView tv_finish = viewGroup.findViewById(R.id.tv_finish);

        if (bgid != 0) {
            iv_bg_success.setImageResource(bgid);
        }

        if (Strs.isNotEmpty(tip_success)) {
            tv_tip_success.setText(tip_success);
        }

        if (Strs.isNotEmpty(text_finish)) {
            tv_finish.setText(text_finish);
        }

        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
                act.finish();
            }
        });

        tipDialog = Dialogs.showCustomDialog(act, viewGroup, true);

    }


    public static void showSuccessDialog(Activity act, int bgid, String tip_success) {
        if (tipDialog != null) {
            tipDialog.dismiss();
        }
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.dialog_tip_success, null);
        ImageView iv_bg_success = viewGroup.findViewById(R.id.iv_bg_success);
        TextView tv_tip_success = viewGroup.findViewById(R.id.tv_tip_success);
        TextView tv_top_up = viewGroup.findViewById(R.id.tv_top_up);
        TextView tv_finish = viewGroup.findViewById(R.id.tv_finish);
        tv_top_up.setVisibility(View.VISIBLE);
        if (bgid != 0) {
            iv_bg_success.setImageResource(bgid);
        }

        if (Strs.isNotEmpty(tip_success)) {
            tv_tip_success.setText(tip_success);
        }
        tv_finish.setText("确定");
        tv_finish.setTextColor(ResUtil.getColor(R.color.text_666));
        tv_top_up.setText("充值");
        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
            }
        });
        tv_top_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
                ActDeposit.launch(act);
            }
        });
        tipDialog = Dialogs.showCustomDialog(act, viewGroup, true);
    }

    public static void hideBindTipDialog() {
        if (tipDialog != null) {
            try {
                tipDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            tipDialog = null;
        }
    }

    public static void showPicVerifyDialog(Activity act, final AbCallback<String> callback) {
        if (verifyPhotoDialog != null) {
            verifyPhotoDialog.dismiss();
        }
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.dialog_pic_verify, null);

        final ImageView ivVerify = (ImageView) viewGroup.findViewById(R.id.ivVercify);

        final EditText etVerify = (EditText) viewGroup.findViewById(R.id.etVerify);
        TextView btnConfirm = (TextView) viewGroup.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = etVerify.getText().toString();
                if (Strs.isEmpty(pwd)) {
                    Toast.makeText(act, "请输入验证码", Toast.LENGTH_LONG).show();
                    return;
                }
                if (callback != null) {
                    callback.callback(pwd);
                }
            }
        });
        TextView btnCancel = (TextView) viewGroup.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.callback(null);
                }
                verifyPhotoDialog.dismiss();
            }
        });
        verifyPhotoDialog = Dialogs.showCustomDialogAnother(act, viewGroup, true);

        UserManager.getIns().getVerifyImage(act, ivVerify);
    }

    public static void hideVerifyPhotoDialog(final Activity act) {
        if (verifyPhotoDialog != null) {
            try {
                verifyPhotoDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            verifyPhotoDialog = null;
        }
    }

    public static void showChangPwdSuccessDialog(Activity act, final AbCallback callback) {
        if (timer != null) {
            timer.cancel();
        }
        if (dialogChangPwdSuccess != null) {
            dialogChangPwdSuccess.dismiss();
        }
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.dialog_change_pwd_success, null);

        TextView tvInfo = (TextView) viewGroup.findViewById(R.id.tvBottomInfo);

        dialogChangPwdSuccess = Dialogs.showCustomDialog(act, viewGroup, true);

        timer = new CountDownTimer(3000, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                tvInfo.setText("完成并登录" + " " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                hideChangePWdSuccessDialog(act);
                if (callback != null) {
                    callback.callback(null);
                }
            }
        }.start();
    }

    public static void hideChangePWdSuccessDialog(final Activity act) {
        if (dialogChangPwdSuccess != null) {
            try {
                dialogChangPwdSuccess.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialogChangPwdSuccess = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static void showDialog(Context context, String title, String content, final View.OnClickListener onConfirm) {
        showDialog(context, title, content, null, onConfirm);
    }

    public static void showSimpleDialog(Context context, String title, String content, String txtCancel) {
        showDialog(context, title, content, null, txtCancel, null, null, false, true, true);
    }

    public static void showDialog(Context context, String title, String content, final View.OnClickListener onConfirm, boolean cancelOnTouchOutSide) {
        showDialog(context, title, content, null, null, onConfirm, null, true, true, cancelOnTouchOutSide);
    }

    public static void showDialog(Context context, String title, String content, String txtOk, final View.OnClickListener onConfirm) {
        showDialog(context, title, content, txtOk, null, onConfirm, null, true, true, true);
    }

    public static void showDialog(Context context, String title, String content, String txtOk, String txtCancel, final View.OnClickListener onConfirm) {
        showDialog(context, title, content, txtOk, txtCancel, onConfirm, true);
    }

    public static void showDialog(Context context, String title, String content, String txtOk, String txtCancel, final View.OnClickListener onConfirm, boolean cancelOnTouchOutSide) {
        showDialog(context, title, content, txtOk, txtCancel, onConfirm, null, true, true, cancelOnTouchOutSide);
    }

    public static void showDialog(Context context, String title, String content, String txtOk, String txtCancel,
                                  final View.OnClickListener onConfirm, final View.OnClickListener onCancel, boolean cancelOnTouchOutSide) {
        showDialog(context, title, content, txtOk, txtCancel, onConfirm, onCancel, true, true, cancelOnTouchOutSide);
    }

    public static void showDialogOk(Context context, String content) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_msg_weihuing, null);
        TextView container = root.findViewById(R.id.tvTimeWeihu);
        container.setText(content);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(root);
        dialog.show();
    }

    public static void showRedPacketMissionTipDialog(Context context, String title, String content, String txtOk, int picRes, final View.OnClickListener onConfirm) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_red_pocket_tip, null);
        TextView tv_content = root.findViewById(R.id.tv_content);
        TextView tv_title1 = root.findViewById(R.id.tv_title1);
        ImageView redpacketActPic = root.findViewById(R.id.redpacketActPic);
        redpacketActPic.setImageResource(picRes);
        tv_content.setText(content);
        tv_title1.setText(title);

        if (!Strs.isEmpty(txtOk)) {
            ((Button) root.findViewById(R.id.btn_ok)).setText(txtOk);
        }

        /** 倒计时60秒，一次1秒 */
        CountDownTimer countDownTimer = new CountDownTimer(3 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                tv_content.setText(Html.fromHtml(content + "  (<font color='#FF0000'>" + (millisUntilFinished / 1000 + 1) + "s</font>)"));
            }

            @Override
            public void onFinish() {
                root.findViewById(R.id.btn_ok).performClick();
            }
        }.start();

        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirm != null)
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                onConfirm.onClick(v);
                dialog.dismiss();
            }
        });

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(root);
        dialog.show();
    }

    public static void showAwardDialog(Context context, String content, int skinResId, final View.OnClickListener onConfirm) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(skinResId, null);
        ((TextView) root.findViewById(R.id.content)).setText(Html.fromHtml(content));
        TextView cancel = root.findViewById(R.id.cancel);

        CountDownTimer countDownTimer = new CountDownTimer(3 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                cancel.setText("取消(" + (millisUntilFinished / 1000) + "s)");
            }

            @Override
            public void onFinish() {
                cancel.performClick();
            }
        }.start();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                dialog.dismiss();
            }
        });

        root.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirm != null)
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                onConfirm.onClick(v);
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(root);
        dialog.show();
    }

    public static void showDialogOk(Context context, String title, String content, final View.OnClickListener onConfirm) {
        showDialog(context, title, content, null, null, onConfirm, null, true, false, true);
    }

    public static void showDialog(Context context, String title, String content, String txtOk, String txtCancel, final View.OnClickListener onConfirm, final View.OnClickListener onCancel, boolean showOk, boolean showCancel, boolean cancelOnTouchOutSide) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_msg, null);
        ((TextView) root.findViewById(R.id.tv_title1)).setText(title);
        TextView container = root.findViewById(R.id.tv_content);
        container.setText(content);

        if (onConfirm == null) {
            root.findViewById(R.id.btn_ok).setVisibility(View.GONE);
            root.findViewById(R.id.horizontal_divider).setVisibility(View.GONE);
        }

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancel != null) {
                    onCancel.onClick(v);
                }
                dialog.dismiss();
            }
        });

        if (!Strs.isEmpty(txtOk))
            ((Button) root.findViewById(R.id.btn_ok)).setText(txtOk);

        if (!Strs.isEmpty(txtCancel)) {
            ((Button) root.findViewById(R.id.btn_cancel)).setText(txtCancel);
        }

        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirm != null)
                    onConfirm.onClick(v);
                dialog.dismiss();
            }
        });

        if (!showOk) {
            root.findViewById(R.id.btn_ok).setVisibility(View.GONE);
            root.findViewById(R.id.horizontal_divider).setVisibility(View.GONE);
        }

        if (!showCancel) {
            root.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            root.findViewById(R.id.horizontal_divider).setVisibility(View.GONE);
        }

        dialog.setCanceledOnTouchOutside(cancelOnTouchOutSide);

        if (!showOk && !showCancel) {
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
        }

        dialog.setContentView(root);
        dialog.show();
    }

    public static void showPasswordErrorDialog(Context context, final View.OnClickListener onConfirm) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_login_error_count_5_msg, null);

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirm != null)
                    onConfirm.onClick(v);
                dialog.dismiss();
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }

    public static void showInputVerifyCodeDialog(Activity act, final AbCallback callback) {
        if (timer != null) {
            timer.cancel();
        }
        if (dialogChangPwdSuccess != null) {
            dialogChangPwdSuccess.dismiss();
        }
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.dialog_change_pwd_success, null);

        TextView tvInfo = (TextView) viewGroup.findViewById(R.id.tvInfo);

        dialogChangPwdSuccess = Dialogs.showCustomDialog(act, viewGroup, true);

        timer = new CountDownTimer(3000, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                tvInfo.setText("完成并登录" + " " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                hideChangePWdSuccessDialog(act);
                if (callback != null) {
                    callback.callback(null);
                }
            }
        }.start();
    }

    public static void showLotteryBuyConfirmDialog(Context context, String issue, String hitcount, String money, String orderCount, Runnable runnable) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        final ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_lottery_buy_confirm, null);
        ((TextView) root.findViewById(R.id.tv_title1)).setText(String.format("确认加入第%s期？", issue));
        final ViewGroup container = root.findViewById(R.id.ll_content);
        ((TextView) container.findViewById(R.id.tv_total_bet_count)).setText(hitcount);
        ((TextView) container.findViewById(R.id.tv_sum_bet_money)).setText(String.format("%s元", money));
        ((TextView) container.findViewById(R.id.tv_order_count)).setText(orderCount);
        root.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        root.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            if (runnable != null)
                runnable.run();
            dialog.dismiss();
        });

        dialog.setContentView(root);
        dialog.show();
    }

    public static Dialog showLotteryListBuyConfirmDialog(Context context, String issue, String hitcount, String money, String orderCount, String maxBonus, BaseAdapter adapter, Runnable runnable) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        final ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_lottery_list_buy_confirm, null);
        ((TextView) root.findViewById(R.id.tv_title1)).setText(String.format("确认加入第%s期？", issue));
        ((TextView) root.findViewById(R.id.tv_title2)).setText(String.format("共计:¥%s元/%s注", money, hitcount));
        ((TextView) root.findViewById(R.id.tv_hint)).setText(Html.fromHtml(String.format("<font color=#ff2b65>温馨提示:</font>本期最高奖金限额%s元,请会员谨慎投注", maxBonus)));
        ListView listView = root.findViewById(R.id.list);
        listView.setAdapter(adapter);
        root.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        root.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            if (runnable != null)
                runnable.run();
            dialog.dismiss();
        });

        dialog.setContentView(root);
        dialog.show();
        return dialog;
    }

    public static void showLotteryBuyOkDialog(Context context) {
        showLotteryBuyOkDialog(context, null);
    }

    public static void showLotteryBuyOkDialog(Context context, Runnable run) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_lottery_play_buy_ok, null);
        root.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (run != null)
                    run.run();
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }

    public static Dialog showFunOkDialog(Context context, int bg_id, String content) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_lottery_play_buy_ok, null);
        FrameLayout ll_title = root.findViewById(R.id.ll_title);
        TextView tv_content = root.findViewById(R.id.tv_content);

        if (bg_id != 0) {
            ll_title.setBackgroundResource(bg_id);
        }

        if (Strs.isNotEmpty(content)) {
            tv_content.setText(content);
        }

        root.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(root);
        dialog.show();
        return dialog;
    }

    public static void showBalanceNotEnough(Context context) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_lhc_msg, null);
        ((TextView) root.findViewById(R.id.tv_title1)).setText("温馨提醒");
        TextView container = root.findViewById(R.id.tv_content);
        container.setText("您账号余额不足，请确认是否立即前往充值？");

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) root.findViewById(R.id.btn_ok)).setText("立即充值");
        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ActDeposit.launch(context);
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }


    public static void showInstructionDialog(Context context, String content) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_lhc_play_instruction, null);
        TextView container = root.findViewById(R.id.tv_content);
        container.setText(Strs.isNotEmpty(content) ? content : "无");
        dialog.setContentView(root);
        dialog.show();
    }

    public static void showTransferBalanceNotEnough(Context context) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_lhc_msg, null);
        ((TextView) root.findViewById(R.id.tv_title1)).setText("温馨提醒");
        TextView container = root.findViewById(R.id.tv_content);
        container.setText("您账号余额不足，请确认是否立即前往充值？");

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) root.findViewById(R.id.btn_ok)).setText("立即充值");
        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ActDeposit.launch(context);
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }

    public static void showTransferToThirdGameSuccess(Context context, String cbid) {
        ThirdGamePlatform platform = ThirdGamePlatform.findByCbId(cbid);
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_transfer_third_success, null);
        TextView container = root.findViewById(R.id.tv_content);
        container.setText(String.format("是否立即进入%s？", platform.getTitle()));

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ActThirdGameMain.launch((Activity) context, platform.getPlatformId(), platform.getScreenOrintation());
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }

    public static void showTransferToWalletSuccess(Context context) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_transfer_third_success, null);
        TextView container = root.findViewById(R.id.tv_content);
        container.setText("是否立即进入彩票游戏平台？");

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                CtxLotteryTouCai.launchLotteryPlay(context, LotteryKind.Tencent_FFC);
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }

    public static void showTransferThirdSuccess(Context context, String cbIdIn) {
        if (ThirdGamePlatform.findByCbId(cbIdIn) == null)
            DialogsTouCai.showTransferToWalletSuccess(context);
        else
            DialogsTouCai.showTransferToThirdGameSuccess(context, cbIdIn);

    }

    public static void showMaintenance(Context context, String startDate, String endDate) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_maintenance, null);
        TextView tvDate = root.findViewById(R.id.tv_date);
        tvDate.setText("(" + startDate + "-" + endDate + ")");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(root);
        dialog.show();
        dialog.setOnDismissListener(dialog1 -> {
            AbActManager.exitAndKill();
        });
    }

    public static void showCreateRecommendLink(Context context, String title, String recomendLink, String inviteCode,
                                               boolean cancelOnTouchOutSide) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_recommendlink, null);
        ((TextView) root.findViewById(R.id.tv_title1)).setText(title);
        TextView inviteCodeTv = root.findViewById(R.id.inviteCode);
        TextView recomendLinkTv = root.findViewById(R.id.recomendLink);
        TextView tv_Link_copy = root.findViewById(R.id.tv_Link_copy);
        TextView tv_inviteCode_copy = root.findViewById(R.id.tv_inviteCode_copy);

        tv_Link_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("recomendLink", recomendLink);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });

        tv_inviteCode_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("inviteCode", inviteCode);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });

        inviteCodeTv.setText(inviteCode);
        recomendLinkTv.setText(recomendLink);

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(cancelOnTouchOutSide);

        dialog.setContentView(root);
        dialog.show();
    }

    /**
     * 红包活动提示
     *
     * @param context
     * @param title
     * @param content
     * @param txtOk
     * @param picRes
     * @param onConfirm
     */
    public static void showRedPacketMissionTipDialog(Context context, String title, int tabposition,
                                                     String content, String txtOk, int picRes, final View.OnClickListener onConfirm, final View.OnClickListener onCacel) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_red_pocket_tip, null);
        TextView tv_content = root.findViewById(R.id.tv_content);
        TextView tv_title1 = root.findViewById(R.id.tv_title1);
        ImageView redpacketActPic = root.findViewById(R.id.redpacketActPic);
        redpacketActPic.setImageResource(picRes);
        tv_content.setText(Html.fromHtml(content));
        tv_title1.setText(title);

        if (!Strs.isEmpty(txtOk)) {
            ((Button) root.findViewById(R.id.btn_ok)).setText(txtOk);
        }

        CountDownTimer countDownTimer = null;

        if (tabposition == 0) {
            /** 倒计时60秒，一次1秒 */
            countDownTimer = new CountDownTimer(3 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub
                    tv_content.setText(Html.fromHtml(content + "  (<font color='#FF0000'>" + (millisUntilFinished / 1000) + "s</font>)"));
                }

                @Override
                public void onFinish() {
                    root.findViewById(R.id.btn_ok).performClick();
                }
            }.start();
        }

        CountDownTimer finalCountDownTimer = countDownTimer;
        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirm != null)
                    if (finalCountDownTimer != null) {
                        finalCountDownTimer.cancel();
                    }
                onConfirm.onClick(v);
                dialog.dismiss();
            }
        });

        CountDownTimer finalCountDownTimer1 = countDownTimer;
        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalCountDownTimer1 != null) {
                    finalCountDownTimer1.cancel();
                }

                if (onCacel != null) {
                    onCacel.onClick(v);
                }

                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(root);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }


    /**
     * 红包结算
     *
     * @param context
     * @param redPacketGetDetailList
     * @param packetGetCount
     * @param needChargeMoney
     * @param onBtnLeft
     * @param onBtnRight
     */
    public static void showRedPacketMissionCheckout(Context context, List<RedPacketGetDetail> redPacketGetDetailList, int packetGetCount, double needChargeMoney,
                                                    final View.OnClickListener onBtnLeft, final View.OnClickListener onBtnRight, final View.OnClickListener onBtnCancle) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_red_pocket_chekout, null);
        ImageView btnLeft = root.findViewById(R.id.btnLeft);
        ImageView btnRight = root.findViewById(R.id.btnRight);

        RelativeLayout RL_RedPacket_one = (RelativeLayout) root.findViewById(R.id.RL_RedPacket_one);
        TextView one_get_money = (TextView) root.findViewById(R.id.one_get_money);
        TextView one_need_money = (TextView) root.findViewById(R.id.one_need_money);

        RelativeLayout RL_RedPacket_tow = (RelativeLayout) root.findViewById(R.id.RL_RedPacket_tow);
        TextView tow_get_total_money = (TextView) root.findViewById(R.id.tow_get_total_money);
        TextView tow_need_money = (TextView) root.findViewById(R.id.tow_need_money);
        TextView tow_red_1 = (TextView) root.findViewById(R.id.tow_red_1);
        TextView tow_red_2 = (TextView) root.findViewById(R.id.tow_red_2);

        RelativeLayout RL_RedPacket_four = (RelativeLayout) root.findViewById(R.id.RL_RedPacket_four);
        TextView four_get_total_money = (TextView) root.findViewById(R.id.four_get_total_money);
        TextView four_need_money = (TextView) root.findViewById(R.id.four_need_money);
        TextView four_red_1 = (TextView) root.findViewById(R.id.four_red_1);
        TextView four_red_2 = (TextView) root.findViewById(R.id.four_red_2);
        TextView four_red_3 = (TextView) root.findViewById(R.id.four_red_3);
        TextView four_red_4 = (TextView) root.findViewById(R.id.four_red_4);

        switch (packetGetCount) {
            case 1:
                RL_RedPacket_one.setVisibility(View.VISIBLE);
                RL_RedPacket_tow.setVisibility(View.GONE);
                RL_RedPacket_four.setVisibility(View.GONE);
                if (redPacketGetDetailList != null && redPacketGetDetailList.size() == 1) {
                    one_get_money.setText("¥" + Nums.formatDecimal(String.valueOf(redPacketGetDetailList.get(0).getBonusAmount()), 2));
                }
                if (needChargeMoney == 0) {
                    one_need_money.setVisibility(View.GONE);
                    btnLeft.setVisibility(View.GONE);
                } else {
                    btnLeft.setVisibility(View.VISIBLE);
                    one_need_money.setVisibility(View.VISIBLE);
                    one_need_money.setText(Html.fromHtml("再次充值<font color='#F0AF15'>" + needChargeMoney + "</font>元，可再次领取红包奖励"));
                }
                break;
            case 2:
                RL_RedPacket_one.setVisibility(View.GONE);
                RL_RedPacket_tow.setVisibility(View.VISIBLE);
                RL_RedPacket_four.setVisibility(View.GONE);
                if (redPacketGetDetailList != null && redPacketGetDetailList.size() == 2) {
                    tow_red_1.setText("¥" + Nums.formatDecimal(String.valueOf(redPacketGetDetailList.get(0).getBonusAmount()), 2));
                    tow_red_2.setText("¥" + Nums.formatDecimal(String.valueOf(redPacketGetDetailList.get(1).getBonusAmount()), 2));
                    double total = redPacketGetDetailList.get(0).getBonusAmount() + redPacketGetDetailList.get(1).getBonusAmount();
                    tow_get_total_money.setText("¥" + Nums.formatDecimal(String.valueOf(total), 2));
                }

                if (needChargeMoney == 0) {
                    tow_need_money.setVisibility(View.GONE);
                    btnLeft.setVisibility(View.GONE);
                } else {
                    btnLeft.setVisibility(View.VISIBLE);
                    tow_need_money.setVisibility(View.VISIBLE);
                    tow_need_money.setText(Html.fromHtml("再次充值<font color='#F0AF15'>" + needChargeMoney + "</font>元，可再次领取红包奖励"));
                }
                break;
            case 3:
            case 4:
                RL_RedPacket_one.setVisibility(View.GONE);
                RL_RedPacket_tow.setVisibility(View.GONE);
                RL_RedPacket_four.setVisibility(View.VISIBLE);
                int size = redPacketGetDetailList.size();
                if (redPacketGetDetailList != null && size > 2) {
                    four_red_1.setText("¥" + Nums.formatDecimal(String.valueOf(redPacketGetDetailList.get(0).getBonusAmount()), 2));
                    four_red_2.setText("¥" + Nums.formatDecimal(String.valueOf(redPacketGetDetailList.get(1).getBonusAmount()), 2));
                    four_red_3.setText("¥" + Nums.formatDecimal(String.valueOf(redPacketGetDetailList.get(2).getBonusAmount()), 2));
                    if (size == 3) {
                        double total = redPacketGetDetailList.get(0).getBonusAmount() + redPacketGetDetailList.get(1).getBonusAmount() + redPacketGetDetailList.get(2).getBonusAmount();
                        four_get_total_money.setText("¥" + Nums.formatDecimal(String.valueOf(total), 2));
                    }
                    four_red_4.setVisibility(View.GONE);

                    if (size == 4) {
                        double total = redPacketGetDetailList.get(0).getBonusAmount() + redPacketGetDetailList.get(1).getBonusAmount() + redPacketGetDetailList.get(2).getBonusAmount() + redPacketGetDetailList.get(3).getBonusAmount();
                        four_get_total_money.setText("¥" + Nums.formatDecimal(String.valueOf(total), 2));
                        four_red_4.setVisibility(View.VISIBLE);
                        four_red_4.setText("¥" + Nums.formatDecimal(String.valueOf(redPacketGetDetailList.get(3).getBonusAmount()), 2));
                    }
                }

                if (needChargeMoney == 0) {
                    btnLeft.setVisibility(View.GONE);
                    four_need_money.setVisibility(View.GONE);
                } else {
                    four_need_money.setVisibility(View.VISIBLE);
                    btnLeft.setVisibility(View.VISIBLE);
                    four_need_money.setText(Html.fromHtml("再次充值<font color='#F0AF15'>" + needChargeMoney + "</font>元，可再次领取红包奖励"));
                }
                break;
            default:
                break;
        }


        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBtnLeft != null)
                    onBtnLeft.onClick(v);
                dialog.dismiss();
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBtnRight != null)
                    onBtnRight.onClick(v);
                dialog.dismiss();
            }
        });

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onBtnCancle != null)
                    onBtnCancle.onClick(v);
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(root);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }


    public static void showDSchessAcountCheckDialog(Context context, double qipaiAccountstr, double mainAcountstr, final View.OnClickListener onCharge
            , final IChargeAndGameListener onChargeAndGame, final View.OnClickListener onGoToGame, final View.OnClickListener oncancle, String gameTitle) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_ds_chess_acount_check, null);

        TextView QipaiAccount = (TextView) root.findViewById(R.id.QipaiAccount);
        EditText editMoney = (EditText) root.findViewById(R.id.editMoney);
        Button num10 = (Button) root.findViewById(R.id.num10);
        Button num100 = (Button) root.findViewById(R.id.num100);
        Button num1000 = (Button) root.findViewById(R.id.num1000);
        Button all = (Button) root.findViewById(R.id.all);
        TextView mainAcount = (TextView) root.findViewById(R.id.mainAcount);
        EditText et_transfer_pwd = (EditText) root.findViewById(R.id.et_transfer_pwd);
        TextView recharge = (TextView) root.findViewById(R.id.recharge);
        Button gotoGame = (Button) root.findViewById(R.id.gotoGame);
        Button chargegotoGame = (Button) root.findViewById(R.id.chargegotoGame);
        TextView TVgameType = (TextView) root.findViewById(R.id.TVgameType);
        TVgameType.setText(gameTitle + "账户余额(元)");
        QipaiAccount.setText(String.valueOf(qipaiAccountstr));
        mainAcount.setText(String.valueOf(mainAcountstr));

        gotoGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGoToGame != null) {
                    onGoToGame.onClick(v);
                }
                dialog.dismiss();
            }
        });

        chargegotoGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChargeAndGame != null) {
                    String money = editMoney.getText().toString().trim();
                    String pwd = et_transfer_pwd.getText().toString().trim();
                    if (Strs.isNotEmpty(money) && Strs.isNotEmpty(pwd)) {
                        boolean result = onChargeAndGame.onChargeAndGameListener(v, Integer.parseInt(money), pwd, dialog);
                        if (result) {
                            dialog.dismiss();
                        }
                    } else if (Strs.isEmpty(pwd)) {
                        Toasts.show(context, "请输入资金密码", false);
                    } else if (Strs.isEmpty(money)) {
                        Toasts.show(context, "请输入转账金额", false);
                    }
                }
            }
        });

        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCharge != null) {
                    onCharge.onClick(v);
                }
            }
        });


        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //全部转入
                String result = String.valueOf(((int) Math.floor(mainAcountstr)));
                editMoney.setText(result);
            }
        });


        root.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oncancle != null) {
                    oncancle.onClick(v);
                }
                dialog.dismiss();
            }
        });

        num10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMoney.setText("10");
            }
        });

        num100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMoney.setText("100");
            }
        });

        num1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMoney.setText("1000");
            }
        });


        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(root);
        dialog.show();

    }

    IChargeAndGameListener iChargeAndGameListener;

    public void setiChargeAndGameListener(IChargeAndGameListener iChargeAndGameListener) {
        this.iChargeAndGameListener = iChargeAndGameListener;
    }

    public interface IChargeAndGameListener {
        boolean onChargeAndGameListener(View v, int chargeMoney, String pwd, Dialog dialog);
    }

    public static void showBonusPoolNotWin(Context context, String jsonStr, double poolBonus) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_bonuspool_notwin, null);
        ImageView cancel = (ImageView) root.findViewById(R.id.cancel);
        TextView tvFirstName = (TextView) root.findViewById(R.id.tvFirstName);//有无
        TextView tvsecondName = (TextView) root.findViewById(R.id.tvsecondName);//有无
        TextView tvfirst = (TextView) root.findViewById(R.id.tv2);
        TextView tvSecond = (TextView) root.findViewById(R.id.tv3);
        RecyclerView rcSecondNames = (RecyclerView) root.findViewById(R.id.rcSecondNames);
        RecyclerView rcfirstNames = (RecyclerView) root.findViewById(R.id.rcfirstNames);
        TextView leftBonus = (TextView) root.findViewById(R.id.leftBonus);
        TextView leftBonusTips = (TextView) root.findViewById(R.id.leftBonusTips);
        List<String> firstdata = new ArrayList<>(0);
        List<String> secondData = new ArrayList<>(0);

        leftBonus.setText("¥ " + String.valueOf(poolBonus));

        try {
            BonusPoolNoetWinMode bonusPoolNoetWinMode = new Gson().fromJson(jsonStr, BonusPoolNoetWinMode.class);
            if (bonusPoolNoetWinMode != null) {
                firstdata.addAll(bonusPoolNoetWinMode.getFirstLevelUsers());
                secondData.addAll(bonusPoolNoetWinMode.getSecondLevelUsers());
                tvfirst.setText("一等奖: ¥ " + Nums.formatDecimal(bonusPoolNoetWinMode.getFirstLevelAmount(), 2) + "  ( " + firstdata.size() + " 名 )");
                tvSecond.setText("二等奖: ¥ " + Nums.formatDecimal(bonusPoolNoetWinMode.getSecondLevelAmount(), 2) + " ( " + secondData.size() + " 名 )");

                if (firstdata.size() == 0 && secondData.size() == 0) {
                    leftBonusTips.setText("本期无人获奖，剩余奖金将累积到下一期。");
                } else {
                    leftBonusTips.setText("本期派发奖励 ¥ " + Nums.formatDecimal(bonusPoolNoetWinMode.getDistributeAmount(), 2) + " 元，剩余奖金将累积到下一期。");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvFirstName.setVisibility(firstdata.size() == 0 ? View.VISIBLE : View.GONE);
        tvsecondName.setVisibility(secondData.size() == 0 ? View.VISIBLE : View.GONE);

        if (firstdata.size() == 1) {
            rcfirstNames.setLayoutManager(new LinearLayoutManager(context));
        } else {
            rcfirstNames.setLayoutManager(new GridLayoutManager(context, 2));
        }

        if (secondData.size() == 1) {
            rcSecondNames.setLayoutManager(new LinearLayoutManager(context));
        } else {
            rcSecondNames.setLayoutManager(new GridLayoutManager(context, 2));
        }

        rcfirstNames.setAdapter(new ZhongjiangResultAapter(context, firstdata, 0));
        rcSecondNames.setAdapter(new ZhongjiangResultAapter(context, secondData, 1));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(root);
        dialog.show();
    }

    public static void showBonusPoolWin(Context context, String jsonStr) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_bonuspool_win, null);
        ImageView cancel = (ImageView) root.findViewById(R.id.cancel);
        TextView tv = (TextView) root.findViewById(R.id.tv);
        TextView tvBonusAmant = (TextView) root.findViewById(R.id.tvBonusAmant);
        try {
            BonusPoolWinMode bonusPoolWinMode = new Gson().fromJson(jsonStr, BonusPoolWinMode.class);
            if (bonusPoolWinMode != null) {
                if (bonusPoolWinMode.getLevel() > 0) {
                    tv.setText(Consitances.IbonusLevelArray[bonusPoolWinMode.getLevel() - 1] + "等奖");
                }
                if (bonusPoolWinMode.getBonusType() == 2) {//钱，2是实物奖励
                    tvBonusAmant.setText(bonusPoolWinMode.getBonusName());
                } else {
                    tvBonusAmant.setText("( ¥ " + Nums.formatDecimal(bonusPoolWinMode.getBonusAmount(), 2) + " )");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(root);
        dialog.show();
    }

    public static void showChoujianngDialog(Context context, List<String> choujiangCodes, String qihaoCurrent, final View.OnClickListener ongomore) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_choujiang, null);
        ImageView cancel = (ImageView) root.findViewById(R.id.cancel);
        TextView gomore = (TextView) root.findViewById(R.id.TvseeMore);
        TextView qihapxinxi = (TextView) root.findViewById(R.id.qihapxinxi);
        Button btnNoAnimate = (Button) root.findViewById(R.id.btnNoAnimate);
        ListView listviewChoujiang = (ListView) root.findViewById(R.id.listviewChoujiang);
        List<WheelView> wheelViewList = new ArrayList<>();
        List<MultiScrollNumber> multiScrollNumbers = new ArrayList<>();
        List<ImageView> imageViews = new ArrayList<>();

        ImageView iv1_num1 = (ImageView) root.findViewById(R.id.iv1_num1);
        ImageView iv2_num1 = (ImageView) root.findViewById(R.id.iv2_num1);
        ImageView iv3_num1 = (ImageView) root.findViewById(R.id.iv3_num1);
        ImageView iv4_num1 = (ImageView) root.findViewById(R.id.iv4_num1);
        ImageView iv5_num1 = (ImageView) root.findViewById(R.id.iv5_num1);


        WheelView wheelView11 = (WheelView) root.findViewById(R.id.wheel11);
        WheelView wheelView22 = (WheelView) root.findViewById(R.id.wheel22);
        WheelView wheelView33 = (WheelView) root.findViewById(R.id.wheel33);
        WheelView wheelView44 = (WheelView) root.findViewById(R.id.wheel44);
        WheelView wheelView55 = (WheelView) root.findViewById(R.id.wheel55);

        MultiScrollNumber scrollNumber1 = (MultiScrollNumber) root.findViewById(R.id.scroll_number1);
        MultiScrollNumber scrollNumber2 = (MultiScrollNumber) root.findViewById(R.id.scroll_number2);
        MultiScrollNumber scrollNumber3 = (MultiScrollNumber) root.findViewById(R.id.scroll_number3);
        MultiScrollNumber scrollNumber4 = (MultiScrollNumber) root.findViewById(R.id.scroll_number4);
        MultiScrollNumber scrollNumber5 = (MultiScrollNumber) root.findViewById(R.id.scroll_number5);

        imageViews.add(iv1_num1);
        imageViews.add(iv2_num1);
        imageViews.add(iv3_num1);
        imageViews.add(iv4_num1);
        imageViews.add(iv5_num1);

        multiScrollNumbers.add(scrollNumber1);
        multiScrollNumbers.add(scrollNumber2);
        multiScrollNumbers.add(scrollNumber3);
        multiScrollNumbers.add(scrollNumber4);
        multiScrollNumbers.add(scrollNumber5);

        wheelViewList.add(wheelView11);
        wheelViewList.add(wheelView22);
        wheelViewList.add(wheelView33);
        wheelViewList.add(wheelView44);
        wheelViewList.add(wheelView55);

        for (int i = 0; i < wheelViewList.size(); i++) {
            wheelViewList.get(i).setWheelAdapter(new WheelAdapter(context));
            wheelViewList.get(i).setWheelData(Arrays.asList(Consitances.nums));
            wheelViewList.get(i).setLoop(true);
            wheelViewList.get(i).setSelection(i + 1);
        }
        for (MultiScrollNumber scrollNumber : multiScrollNumbers) {
            scrollNumber.setTextSize(35);
            scrollNumber.setInterpolator(new DecelerateInterpolator());
            scrollNumber.setScrollVelocity(50);
        }

        qihapxinxi.setText("本期开奖号码为重庆时时彩第" + qihaoCurrent + "期开奖号码");
        List<String> codeLists = new ArrayList<>();
        SimpleListviewAdapter adapter = new SimpleListviewAdapter(context, codeLists);
        listviewChoujiang.setAdapter(adapter);
        CountDownTimer countDownTimer = new CountDownTimer((choujiangCodes.size()) * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int index = (int) (millisUntilFinished / 1000);
                Log.d("DialogsTouCai", "index:" + index);
                codeLists.add(0, choujiangCodes.get(index));
                adapter.notifyDataSetChanged();

                for (int i = 0; i < wheelViewList.size(); i++) {
                    wheelViewList.get(i).smoothScrollBy(30000 + 50 * i, 1500);
                }
            }

            @Override
            public void onFinish() {
                codeLists.clear();
                codeLists.addAll(choujiangCodes);
                adapter.notifyDataSetChanged();
                stopLaohujilaohuji(wheelViewList, multiScrollNumbers, imageViews, choujiangCodes.get(0));
            }
        }.start();

        btnNoAnimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                codeLists.clear();
                codeLists.addAll(choujiangCodes);
                adapter.notifyDataSetChanged();
                stopLaohujilaohuji(wheelViewList, multiScrollNumbers, imageViews, choujiangCodes.get(0));
                btnNoAnimate.setEnabled(false);
            }
        });

        gomore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ongomore != null) {
                    ongomore.onClick(v);
                }
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(root);
        dialog.show();
    }

    public static void stopLaohujilaohuji(List<WheelView> wheelViewList, List<MultiScrollNumber> multiScrolls,
                                          List<ImageView> imageViews, String code) {
        if (Strs.isEmpty(code)) {
            return;
        }

        for (WheelView wheelView : wheelViewList) {
            wheelView.setVisibility(View.GONE);
        }

        char[] numbers = code.toCharArray();

        for (int i = 0; i < multiScrolls.size(); i++) {
            if (Integer.parseInt(String.valueOf(numbers[i])) != 0) {
                int parseInt = Integer.parseInt(String.valueOf(numbers[i]));
                multiScrolls.get(i).setNumber(parseInt - 1, parseInt);
            } else {
                imageViews.get(i).setVisibility(View.VISIBLE);
            }
        }
    }


    public static void stopLaohujilaohuji(Context context, int taketype, double nextAmountSub, final View.OnClickListener btnClick) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_choujiang_btn_guide_touzhu, null);

        TextView btnGo = (TextView) root.findViewById(R.id.btn);
        String nextAmountSubFormatStr = Nums.formatDecimal(nextAmountSub, 2);
        String contentStr2 = "您当前还需" + (taketype == 0 ? "充值" : "投注") + nextAmountSubFormatStr + "元即可获得抽奖机会,每" + (taketype == 0 ? "充值" : "投注") + "≥1000元,随机送出抽奖码,无限领取";
        int startindex = contentStr2.indexOf(nextAmountSubFormatStr);
        int endindex = startindex + nextAmountSubFormatStr.length();
        SpannableStringBuilder style = new SpannableStringBuilder(contentStr2);
        style.setSpan(new RoundBackgroundColorSpan(Color.parseColor("#21ED5735"), Color.parseColor("#e71c14"), Utils.dp2px(context, 18)),
                startindex, endindex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((TextView) root.findViewById(R.id.tvContentInfo)).setText(style);


        root.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnGo.setText(taketype == 0 ? "立即\n充值" : "立即\n投注");

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnClick != null) {
                    btnClick.onClick(v);
                }
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(root);
        dialog.show();
    }

    public static void showPaymentDetailDialog(Context context, ArrayList<CommonPaymentEntity> contentList, PaymentCategoryInfo categoryInfo, String usdtRate) {
        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_payment_detail_zhongda, null);
        TextView ivcancel = (TextView) root.findViewById(R.id.ivcancel);
        ImageView ivLogo = (ImageView) root.findViewById(R.id.ivLogo);
        TextView tvName = (TextView) root.findViewById(R.id.name);
        Views.loadImage(context, ivLogo, ENV.curr.host + categoryInfo.getMobileLogoPicture());
        tvName.setText(categoryInfo.getCategoryName());
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        BaseQuickAdapter<CommonPaymentEntity, BaseViewHolder> adapter =
                new BaseQuickAdapter<CommonPaymentEntity, BaseViewHolder>(R.layout.item_payment_category_layout_small, contentList) {
                    @Override
                    protected void convert(BaseViewHolder helper, CommonPaymentEntity item) {
                        helper.setText(R.id.paymentCategory, item.getShowName());
                        ImageView pamentLogo = (ImageView) helper.getView(R.id.paymentIcon);
                        Views.loadImage(context, pamentLogo, item.getIcon());
                        if (Strs.isNotEmpty(item.getShowName()) && item.getShowName().toLowerCase().contains("usdt")) {
                            helper.setText(R.id.huilv, "1USDT = " + usdtRate + "CNY");
                            helper.setVisible(R.id.huilv, true);
                        } else {
                            helper.setVisible(R.id.huilv, false);
                        }
                    }
                };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CommonPaymentEntity commonPaymentEntity = (CommonPaymentEntity) adapter.getData().get(position);

                int type = commonPaymentEntity.getType();
                switch (type) {
                    case 1:
                        QrCodeListBean qrCodeListBean = commonPaymentEntity.getQrCodeListBean();
                        ActRechargePay.launchQrPayment(context, qrCodeListBean, ActRechargePay.PAY_TYPE_QR
                                , categoryInfo.getPayHint(), categoryInfo.getRechargeHint());
                        break;
                    case 2:
                        if (Strs.isNotEmpty(commonPaymentEntity.getShowName()) && commonPaymentEntity.getShowName().toLowerCase().contains("usdt")) {
//                            ThridListBean thridListBean = commonPaymentEntity.getThridListBean();
//                            if (thridListBean == null) {
//                                ActXunibiCharge.launch(context, String.valueOf(commonPaymentEntity.getThridListBean().getId()), usdtRate);
//                            } else {
//                                ActXunibiCharge.launch(context, String.valueOf(commonPaymentEntity.getThridListBean().getId()), usdtRate,
//                                        String.valueOf(thridListBean.getMinUnitRecharge()), String.valueOf(thridListBean.getMaxUnitRecharge()));
//                            }
                            ThridListBean thridListBean = commonPaymentEntity.getThridListBean();
                            //Log.e("DialogsZhongda", "size():" + thridListBean.getBanklist().size());
                            ActRechargePay.launchThird(context, thridListBean, thridListBean.getBanklist().size() > 0 ? ActRechargePay.PAY_TYPE_BANK : ActRechargePay.PAY_TYPE_OTHER
                                    , categoryInfo.getPayHint(), categoryInfo.getRechargeHint(),true,String.valueOf(commonPaymentEntity.getThridListBean().getId()),usdtRate);
                        } else {
                            ThridListBean thridListBean = commonPaymentEntity.getThridListBean();
                            ActRechargePay.launchThird(context, thridListBean, thridListBean.getBanklist().size() > 0 ? ActRechargePay.PAY_TYPE_BANK : ActRechargePay.PAY_TYPE_OTHER
                                    , categoryInfo.getPayHint(), categoryInfo.getRechargeHint());
                        }
                        break;
                    case 3:
                        TransferListBean transferListBean = commonPaymentEntity.getTransferListBean();
                        ActRechargePay.launchTransfer(context, transferListBean, ActRechargePay.PAY_TYPE_TRANSFER
                                , categoryInfo.getPayHint(), categoryInfo.getRechargeHint());
                        break;
                }

            }
        });

        ivcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(root);
        dialog.show();
    }

//    public static void showSelectImgaeDialog(Context context,View.OnClickListener onTakePhonto,View.OnClickListener onSelectPhontos) {
//        Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
//        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_slect_pht, null);
//        ImageView cancel = (ImageView) root.findViewById(R.id.cancel);
//        TextView tv = (TextView) root.findViewById(R.id.tv);
//        TextView tvBonusAmant = (TextView) root.findViewById(R.id.tvBonusAmant);
//        try {
//            BonusPoolWinMode bonusPoolWinMode = new Gson().fromJson(jsonStr, BonusPoolWinMode.class);
//            if (bonusPoolWinMode != null) {
//                if (bonusPoolWinMode.getLevel() > 0) {
//                    tv.setText(Consitances.IbonusLevelArray[bonusPoolWinMode.getLevel() - 1] + "等奖");
//                }
//                if (bonusPoolWinMode.getBonusType() == 2) {//钱，2是实物奖励
//                    tvBonusAmant.setText(bonusPoolWinMode.getBonusName());
//                } else {
//                    tvBonusAmant.setText("( ¥ " + Nums.formatDecimal(bonusPoolWinMode.getBonusAmount(), 2) + " )");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setContentView(root);
//        dialog.show();
//    }
}
