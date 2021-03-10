package com.desheng.base.panel;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.debug.AbDebug;
import com.ab.global.Config;
import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
import com.ab.thread.ThreadCollector;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.ThirdGamePlatform;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.HashMap;

import okhttp3.Request;


/**
 * 游戏大厅主界面
 */
public class ActThirdGameForAll extends AbAdvanceActivity {

    private int platformId;
    private ThirdGamePlatform thirdGamePlatform;
    private WebView webView;

    public static void launch(Activity act, int platformId) {
        if (BaseConfig.Org != null && !BaseConfig.Org.canPlayGame(UserManager.getIns().getMainUserLevel())) {
            Toasts.show(act, "当前用户为代理, 不可进行第三方游戏", false);
        }else{
            Intent itt = new Intent(act, ActThirdGameForAll.class);
            itt.putExtra("platformId", platformId);
            act.startActivity(itt);
        }
       
    }

    public static void launchOutside(final Activity act, int platformId) {
        if (BaseConfig.Org != null && !BaseConfig.Org.canPlayGame(UserManager.getIns().getMainUserLevel())) {
            Toasts.show(act, "当前用户为代理, 不可进行第三方游戏", false);
            return;
        }
        ThirdGamePlatform thirdGamePlatform = ThirdGamePlatform.findById(platformId);
        String subGame = "";
        if (thirdGamePlatform == ThirdGamePlatform.IM) {
            subGame = "sport";
        }
        HttpAction.getLoginUrl(act, thirdGamePlatform.getPlatformId(), subGame, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(act, "");
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
                        ActWeb.launchOutside(act, url);
                    }
                } else {
                  //  Toasts.show(act, msg, false);
                    Toasts.show(act, "即将推出，敬请期待!",true);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(act, content, false);
                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(act);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_third_game_for_all;
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndDarkContentWithPadding();
        platformId = getIntent().getIntExtra("platformId", 0);
        thirdGamePlatform = ThirdGamePlatform.findById(platformId);

        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), thirdGamePlatform.getTitle());
        /*UIHelper.getIns().setToolbarRightButtonImg(getToolbar(), R.mipmap.ic_zoom_out, 25, 25, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToolbar().setVisibility(View.GONE);
            }
        });*/
        initWebView();
        getThirdGameUrl();
        //注册事件
        //EventBus.getDefault().register(this);
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
                Dialogs.hideProgressDialog(ActThirdGameForAll.this);
            }
        });


        //设置响应js 的Alert()函数
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(ActThirdGameForAll.this);
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
                AlertDialog.Builder b = new AlertDialog.Builder(ActThirdGameForAll.this);
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
                final View v = View.inflate(ActThirdGameForAll.this, R.layout.webview_prompt_dialog, null);
                ((TextView) v.findViewById(R.id.prompt_message_text)).setText(message);
                ((EditText) v.findViewById(R.id.prompt_input_field)).setText(defaultValue);
                AlertDialog.Builder b = new AlertDialog.Builder(ActThirdGameForAll.this);
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
        Dialogs.showProgressDialog(ActThirdGameForAll.this, "");
    }

    public void getThirdGameUrl() {
        String subGame = "";
        if (thirdGamePlatform == ThirdGamePlatform.IM) {
            subGame = "sport";
        }
        HttpAction.getLoginUrl(ActThirdGameForAll.this, thirdGamePlatform.getPlatformId(), subGame, new AbHttpResult() {

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
                    Toasts.show(ActThirdGameForAll.this, msg, false);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActThirdGameForAll.this, content, false);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MM.http.cancellAllByTag(null);
        ThreadCollector.getIns().clear();
        //EventBus.getDefault().unregister(this);
    }


    public interface OnTitleRightBtnClickListener {
        void onRightBtnClick();
    }
}


