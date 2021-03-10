package com.desheng.app.toucai.panel;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.callback.AbCallback;
import com.ab.debug.AbDebug;
import com.ab.dialog.AbSampleDialogFragment;
import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.MD5;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.ScreenUtils;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.panel.ActBindBankCard;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import java.util.HashMap;

import okhttp3.Request;

/**
 * 游戏大厅主界面
 */
public class ActThirdGameMain extends AbAdvanceActivity {

    private int platformId;
    private int port1land0sensor4;
    private ThirdGamePlatform thirdGamePlatform;
    private WebView webView;
    private ViewGroup vgHome, vgRecharge, vgTransfer, vgReturn;
    private MaterialDialog materialDialog;
    private AbSampleDialogFragment rechargeDialog;
    private MenuDrawer mDrawer;
    private String gameUrl;
    private CheckBox mCheckBox;
    private LinearLayout mvgBottomGroup;
    private RelativeLayout mLlnavigation;
    private View mIvhome;
    private View mIvRecharge;
    private View mIvZhuanzhang;
    private View mIvTrunBack;

    public static void launch(Activity act, int platformId, int port1land0sensor4) {
        Intent itt = new Intent(act, ActThirdGameMain.class);
        itt.putExtra("platformId", platformId);
        itt.putExtra("port1land0sensor4", port1land0sensor4);
        act.startActivity(itt);
    }

    public static void launch(Activity act, int platformId, int port1land0sensor4, String gameUrl) {
        Intent itt = new Intent(act, ActThirdGameMain.class);
        itt.putExtra("platformId", platformId);
        itt.putExtra("port1land0sensor4", port1land0sensor4);
        itt.putExtra("gameUrl", gameUrl);
        act.startActivity(itt);
    }

    @Override
    protected void setScreenOrientation() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        port1land0sensor4 = getIntent().getIntExtra("port1land0sensor4", 1);
        setRequestedOrientation(port1land0sensor4);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_third_game_for_all;
    }

    @Override
    protected void init() {
        hideToolbar();

        platformId = getIntent().getIntExtra("platformId", 0);
        gameUrl = getIntent().getStringExtra("gameUrl");

        thirdGamePlatform = ThirdGamePlatform.findByPlatformId(platformId);
        //UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), thirdGamePlatform.getTitle());

        initWebView();

        if (Strs.isEmpty(gameUrl)) {
            getThirdGameUrl();
        } else {
            if (!gameUrl.startsWith("http")) {
                gameUrl = ENV.curr.host + gameUrl;
            }
            webView.loadUrl(gameUrl);
        }

        //注册事件
        //EventBus.getDefault().register(this);

        mCheckBox = findViewById(R.id.checkbox);
        mLlnavigation = findViewById(R.id.llnavigation);

        mIvhome = findViewById(R.id.Ivhome);
        mIvhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mIvRecharge = findViewById(R.id.IvRecharge);
        mIvRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActDeposit.launch(ActThirdGameMain.this);
            }
        });

        mIvZhuanzhang = findViewById(R.id.IvZhuanzhang);
        mIvZhuanzhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRechargeDialog();
            }
        });

        mIvTrunBack = findViewById(R.id.IvTrunBack);
        mIvTrunBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    Toasts.show(ActThirdGameMain.this, "已经是首页啦!", true);
                }
            }
        });

        mCheckBox.setChecked(true);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                haveCheckByHand = false;
                startAnimate(mLlnavigation, isChecked);
            }
        });

        mCheckBox.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (haveCheckByHand) {
                    startAnimate(mLlnavigation, false);
                    mCheckBox.setChecked(false);
                }
            }
        }, 5000);
    }

    boolean haveCheckByHand = true;

    private void startAnimate(View view, boolean isChecked) {
        ObjectAnimator translationX = new ObjectAnimator().ofFloat(view, "translationX",
                isChecked ? ScreenUtils.dpToPixel(80) : 0, isChecked ? 0 : ScreenUtils.dpToPixel(80));
        translationX.setDuration(600);  //设置动画时间
        translationX.setInterpolator(new DecelerateInterpolator());
        translationX.start(); //启动
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.vWebview);
        AbDebug.log(AbDebug.TAG_APP, webView.getX5WebViewExtension() == null ? "SysWeb initView*********" : "X5Web initView**********");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Config.isDebug()) {
            WebView.setWebContentsDebuggingEnabled(Config.isDebug());
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
        settings.setUserAgent(AbHttpAO.getUserAgent());
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); //加载此url
                return true;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setToolbarTitleCenter(view.getTitle());
                DialogsTouCai.hideProgressDialog(ActThirdGameMain.this);
            }
        });


        //设置响应js 的Alert()函数
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(ActThirdGameMain.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            //设置响应js 的Confirm()函数
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(ActThirdGameMain.this);
                b.setTitle("Confirm");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                b.create().show();
                return true;
            }

            //设置响应js 的Prompt()函数
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                final View v = View.inflate(ActThirdGameMain.this, R.layout.webview_prompt_dialog, null);
                ((TextView) v.findViewById(R.id.prompt_message_text)).setText(message);
                ((EditText) v.findViewById(R.id.prompt_input_field)).setText(defaultValue);
                AlertDialog.Builder b = new AlertDialog.Builder(ActThirdGameMain.this);
                b.setTitle("Prompt");
                b.setView(v);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = ((EditText) v.findViewById(R.id.prompt_input_field)).getText().toString();
                        result.confirm(value);
                    }
                });
                b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                b.create().show();
                return false;
            }
        });
    }

    private static int count = 0;

    public void getThirdGameUrl() {
        String subGame = "";
        if (thirdGamePlatform == ThirdGamePlatform.IM) {
            subGame = "sport";
        }
        count++;
        HttpActionTouCai.getThirdGameLink(ActThirdGameMain.this, thirdGamePlatform.getPlatformId(), subGame, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActThirdGameMain.this, "");
                    }
                });
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", String.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    String url = getField(extra, "data", "");
                    if (Strs.isNotEmpty(url)) {
                        webView.loadUrl(url);
                    }
                } else {
                    processError(msg);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                processError(content);
                return true;
            }

            public void processError(String content) {
                Toasts.show(ActThirdGameMain.this, content, false);
                getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void checkTransfer() {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onUserBindChecked(UserBindStatus status) {
                if (!status.isBindWithdrawPassword()) {
                    showCheckBankDialog(new AbCallback<Object>() {
                        @Override
                        public boolean callback(Object obj) {
                            ActBindBankCard.launch(ActThirdGameMain.this, true);
                            return true;
                        }
                    });
                } else {
                    showFunPasswordDialog(new AbCallback<String>() {
                        @Override
                        public boolean callback(final String obj) {
                            HttpAction.modifyFundsPassword(obj, obj, new AbHttpResult() {

                                @Override
                                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                    if (msg.contains("密码相同")) {
                                        ActThirdTransferFast.launch(ActThirdGameMain.this);
                                    } else {
                                        Toasts.show(ActThirdGameMain.this, "请输入正确资金密码", false);
                                    }
                                    return true;
                                }

                                @Override
                                public boolean onError(int status, String content) {
                                    Toasts.show(ActThirdGameMain.this, content, false);
                                    return true;
                                }
                            });

                            return true;
                        }
                    });
                }
            }

            @Override
            public void onUserBindCheckFailed(String msg) {

            }

            @Override
            public void onAfter() {

            }
        });
    }

    public void showCheckBankDialog(final AbCallback<Object> callback) {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
        materialDialog = new MaterialDialog(ActThirdGameMain.this);
        materialDialog.setMessage("亲，先绑定取款人");
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                callback.callback(null);
            }
        });
        materialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    private void showFunPasswordDialog(final AbCallback<String> callback) {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(ActThirdGameMain.this).inflate(R.layout.dialog_fun_password, null);
        ImageView ivClose = viewGroup.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });

        final EditText etPoint = (EditText) viewGroup.findViewById(R.id.etPoint);
        Button btnConfirm = (Button) viewGroup.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = etPoint.getText().toString();
                if (Strs.isEmpty(pwd)) {
                    Toast.makeText(ActThirdGameMain.this, "请输入资金密码", Toast.LENGTH_LONG).show();
                    return;
                }
                callback.callback(pwd);
                materialDialog.dismiss();
            }
        });
        materialDialog = Dialogs.showCustomDialog(ActThirdGameMain.this, viewGroup, true);
    }

    private void showRechargeDialog() {
        if (rechargeDialog != null) {
            rechargeDialog.dismiss();
        }
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(ActThirdGameMain.this).inflate(R.layout.dialog_recharge, null);
        ImageView ivClose = viewGroup.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rechargeDialog.dismiss();
            }
        });

        TextView tvCanAccount = viewGroup.findViewById(R.id.tvCanAccount);
        tvCanAccount.setText(Nums.formatDecimal(UserManager.getIns().getLotteryAvailableBalance(), 3));

        final Button btnConfirm = (Button) viewGroup.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View contentView = rechargeDialog.getContentView();

                EditText etAmount = contentView.findViewById(R.id.etAmount);
                String amountStr = Views.getText(etAmount);
                final double amount = Nums.parse(amountStr, 0.0);
                if (amount <= 0) {
                    Toasts.show(ActThirdGameMain.this, "请输入正确金额", false);
                    return;
                }
                if (!Nums.isNoMoreFractionDigits(amountStr, 1)) {
                    Toasts.show(ActThirdGameMain.this, "转账支持最多一位小数", false);
                    return;
                }

                double outBalance = UserManager.getIns().getLotteryAvailableBalance();
                if (amount > outBalance) {
                    Toasts.show(ActThirdGameMain.this, "转账金额超过转出账户余额, 请重试", false);
                    return;
                }

                EditText etPassword = contentView.findViewById(R.id.etPassword);
                final String password = Views.getText(etPassword);
                if (Strs.isEmpty(password)) {
                    Toasts.show(ActThirdGameMain.this, "请输入资金密码", false);
                    return;
                }

                final String cbIdOut = CtxLottery.MAIN_POCKET_CBID;
                final String cbIdIn = thirdGamePlatform.getCbId();
                HttpAction.getPlayerTransferRefreshToken(ActThirdGameMain.this, new AbHttpResult() {
                    @Override
                    public void onBefore(Request request, int id, String host, String funcName) {
                        contentView.post(new Runnable() {
                            @Override
                            public void run() {
                                DialogsTouCai.showProgressDialog(ActThirdGameMain.this, "");
                            }
                        });
                    }

                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        entity.putField("data", String.class);
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        if (code == 0 && error == 0) {
                            String token = getField(extra, "data", "");
                            if (Strs.isNotEmpty(token)) {
                                HttpAction.commitPlayerTransfer(ActThirdGameMain.this, cbIdOut, cbIdIn, amount, password, token, new AbHttpResult() {
                                    @Override
                                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                        if (code == 0 && error == 0) {
                                            Toasts.show(ActThirdGameMain.this, "转账成功, 请稍候查询余额!", true);
                                            //同步用户数据
                                            UserManager.getIns().initUserData(new UserManager.IUserDataSyncCallback() {
                                                @Override
                                                public void onUserDataInited() {

                                                }

                                                @Override
                                                public void afterUserDataInited() {
                                                    //同步当前余额数据
                                                    //initPocketInfo();
                                                    if (Strs.isNotEmpty(gameUrl)) {
                                                        webView.reload();
                                                    }
                                                    //或关闭当前页面, 非实时可查到余额变化
                                                    rechargeDialog.dismiss();
                                                }

                                                @Override
                                                public void onUserDataInitFaild() {

                                                }

                                                @Override
                                                public void onAfter() {

                                                }
                                            });
                                        } else {
                                            Toasts.show(ActThirdGameMain.this, msg, false);
                                        }
                                        return true;
                                    }

                                    @Override
                                    public boolean onError(int status, String content) {
                                        DialogsTouCai.hideProgressDialog(ActThirdGameMain.this);
                                        Toasts.show(ActThirdGameMain.this, content, false);
                                        return true;
                                    }

                                    @Override
                                    public void onAfter(int id) {
                                        DialogsTouCai.hideProgressDialog(ActThirdGameMain.this);
                                    }
                                });
                            }
                        } else {

                        }
                        return true;
                    }

                    @Override
                    public boolean onError(int status, String content) {
                        DialogsTouCai.hideProgressDialog(ActThirdGameMain.this);
                        Toasts.show(ActThirdGameMain.this, content, false);
                        rechargeDialog.dismiss();
                        return true;
                    }
                });
            }
        });
        rechargeDialog = Dialogs.showFullScreenDialog(ActThirdGameMain.this, viewGroup);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (webView != null) {
            webView.destroy();
        }

        if (rechargeDialog != null) {
            rechargeDialog.dismiss();
        }

        if (materialDialog != null) {
            materialDialog.dismiss();
        }

        //EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            Toasts.show(ActThirdGameMain.this, "请点击右边菜单,点击首页返回!");
        }
    }

    public interface OnTitleRightBtnClickListener {
        void onRightBtnClick();
    }
}
