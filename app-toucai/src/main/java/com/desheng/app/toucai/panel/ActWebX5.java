package com.desheng.app.toucai.panel;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.debug.AbDebug;
import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.global.Global;
import com.ab.http.AbHttpAO;
import com.ab.module.MM;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import okhttp3.HttpUrl;

/**
 * 通用网页
 * Created by Administrator on 2017/8/1 0001.
 */
public class ActWebX5 extends AbAdvanceActivity {

    private ImageView backBtn;

    public static void launch(Context act, String url) {
        Intent itt = new Intent(act, ActWebX5.class);
        if (Strs.isNotEmpty(url) && !url.startsWith("http")) {
            url = ENV.curr.host + url;
        }
        itt.putExtra("url", url);
        act.startActivity(itt);
    }

    public static void launch(Context act, String url, boolean syncCookie) {
        Intent itt = new Intent(act, ActWebX5.class);
        if (Strs.isNotEmpty(url) && !url.startsWith("http")) {
            url = ENV.curr.host + url;
        }
        itt.putExtra("url", url);
        itt.putExtra("syncCookie", syncCookie);
        act.startActivity(itt);
    }

    public static void launch(Context act, String title, String url, boolean syncCookie) {
        Intent itt = new Intent(act, ActWebX5.class);
        itt.putExtra("title", title);
        if (Strs.isNotEmpty(url) && !url.startsWith("http")) {
            url = ENV.curr.host + url;
        }

        Log.d("ActWebX5", url);

        itt.putExtra("url", url);
        itt.putExtra("syncCookie", syncCookie);
        act.startActivity(itt);
    }

    public static void launchForSubject(Context act, String subject, String title, String url, boolean syncCookie) {
        Intent itt = new Intent(act, ActWebX5.class);
        itt.putExtra("title", title);
        if (Strs.isNotEmpty(url) && !url.startsWith("http")) {
            url = ENV.curr.host + url;
        }
        itt.putExtra("url", url);
        itt.putExtra("syncCookie", syncCookie);
        itt.putExtra("subject", subject);
        act.startActivity(itt);
    }

    public static void launch(Context act, String title, String content) {
        Intent itt = new Intent(act, ActWebX5.class);
        itt.putExtra("title", title);
        itt.putExtra("content", content);
        act.startActivity(itt);
    }

    public static void launchOutside(Context act, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        if (Strs.isNotEmpty(url) && !url.startsWith("http")) {
            url = ENV.curr.host + url;
        }
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        act.startActivity(intent);
    }

    public static void launchAndFinish(Activity act, String url) {
        Intent itt = new Intent(act, ActWebX5.class);
        if (Strs.isNotEmpty(url) && !url.startsWith("http")) {
            url = ENV.curr.host + url;
        }
        itt.putExtra("url", url);
        act.startActivity(itt);
        act.finish();
    }

    private WebView webView;

    @Override
    public int getLayoutId() {
        return com.desheng.base.R.layout.act_web_x5;
    }


    public String getSubject() {
        if (getIntent() != null) {
            return getIntent().getStringExtra("subject");
        }
        return null;
    }

    public String getUrl() {
        if (getIntent() != null) {
            if (Strs.isNotEmpty(getIntent().getStringExtra("pushContentMode"))) {
                String url = getIntent().getStringExtra("pushContentMode");
                if (Strs.isNotEmpty(url) && !url.startsWith("http")) {
                    url = ENV.curr.host + url;
                }
                return url;
            } else {
                String tempUrl = "";//注意后台传过来的url字符可能被转义，处理下
                if (Strs.isNotEmpty(getIntent().getStringExtra("url")) && getIntent().getStringExtra("url").contains("&amp;")) {
                    tempUrl = getIntent().getStringExtra("url").replace("&amp;", "&");
                } else {
                    tempUrl = getIntent().getStringExtra("url");
                }
                return tempUrl;
            }
        }
        return null;
    }

    public String getTitleStr() {
        if (getIntent() != null) {
            return getIntent().getStringExtra("title");
        }
        return null;
    }

    public String getContentStr() {
        if (getIntent() != null) {
            String content = getIntent().getStringExtra("content");
            content = Strs.toUTF8(content);
            return content;
        }
        return null;
    }

    public boolean isSyncCookie() {
        if (getIntent() != null) {
            if (Strs.isNotEmpty(getIntent().getStringExtra("pushContentMode"))) {
                return true;
            } else {
                boolean isSyncCookie = getIntent().getBooleanExtra("syncCookie", false);
                return isSyncCookie;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                onLeftButtonClick();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void init() {
        backBtn = ((ImageView) findViewById(com.desheng.base.R.id.ivBack));

        if (Strs.isNotEmpty(getTitleStr())) {
//            if ("DS棋牌".equals(getTitleStr())) {
//                hideToolbar();
//                //backBtn.setVisibility(View.VISIBLE);
//            } else {
//                backBtn.setVisibility(View.GONE);
//            }
            UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), getTitleStr());
        } else {
            UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "");
        }
        setStatusBarTranslucentAndLightContentWithPadding();
        webView = (WebView) findViewById(com.desheng.base.R.id.vWebview);
        AbDebug.log(AbDebug.TAG_APP, webView.getX5WebViewExtension() == null ? "SysWeb initView*********" : "X5Web initView**********");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Config.isDebug()) {
            WebView.setWebContentsDebuggingEnabled(Config.isDebug());
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
        settings.setUserAgent(AbHttpAO.getUserAgent());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                syncCookieWithHttp(url);
                //Activity act, View webView, String subject, String url
                if (MM.webView.getFilter().filterUrl(ActWebX5.this, webView, getSubject(), url)) {
                    UrlLanjie(url);
                    return true; //webview什么也不做
                } else {
                    Log.e("ActWebX5", url);
                    if (url != null) {
                        if (url.contains("app_cmd=pane")) {
                            finish();
                        } else if (url.contains("app_cmd3=pane")) {

                        } else if (url.contains("app_cmd=pane")) {

                        } else {
                            view.loadUrl(url);
                        }
                    }
                }

                return true;
            }


            @Override
            public void onPageFinished(WebView view, String url) {

                view.loadUrl("javascript:window.java_obj.getSource('<head>'+" +
                        "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

                super.onPageFinished(view, url);
                if (Strs.isEmpty(getTitleStr())) {
                    UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), view.getTitle());
                }
                Dialogs.hideProgressDialog(ActWebX5.this);

            }
        });


        //设置响应js 的Alert()函数
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(ActWebX5.this);
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
                AlertDialog.Builder b = new AlertDialog.Builder(ActWebX5.this);
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
                final View v = View.inflate(ActWebX5.this, com.desheng.base.R.layout.webview_prompt_dialog, null);
                ((TextView) v.findViewById(com.desheng.base.R.id.prompt_message_text)).setText(message);
                ((EditText) v.findViewById(com.desheng.base.R.id.prompt_input_field)).setText(defaultValue);
                AlertDialog.Builder b = new AlertDialog.Builder(ActWebX5.this);
                b.setTitle("Prompt");
                b.setView(v);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = ((EditText) v.findViewById(com.desheng.base.R.id.prompt_input_field)).getText().toString();
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

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                ActWebX5.this.startActivityForResult(Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
                return true;
            }
        });

        //webView.addJavascriptInterface(new JsCallAndroidHelper(this),"toTouZhu");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void UrlLanjie(String url) {

        Log.e("ActWebX5", "url:" + url);

        String[] split = url.split("\\?");
        if (split.length > 1) {
            if (split[1].contains("&")) {

            } else {
                String[] temp = split[1].split("=");
                if (Strs.isEqual("lt", temp[0]) && Strs.isNotEmpty(temp[1])) {
                    CtxLotteryTouCai.launchLotteryPlay(ActWebX5.this, Integer.parseInt(temp[1]));
                    finish();
                } else if (Strs.isEqual("app_cmd", temp[0])) {

                    if (Strs.isEqual("lottery-play",temp[1])) {
                        CtxLotteryTouCai.launchLotteryPlay(ActWebX5.this, 201);
                    } else if (Strs.isEqual("recharge",temp[1])) {
                        ActDeposit.launch(ActWebX5.this);
                    }
                }
            }
        }
    }

    private android.webkit.ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private android.webkit.ValueCallback<Uri> mUploadMessage;


    @Override
    protected void onStart() {
        super.onStart();
        loadUrl();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadUrl();
    }

    private void loadUrl() {
        if (isSyncCookie()) {
            syncCookieWithHttp(getUrl());
        }
        if (Strs.isNotEmpty(getUrl())) {

            Log.d("ActWebX5", getUrl());
            Log.d("ActWebX5", getUrl().startsWith("http") ? getUrl() : "http://" + getUrl());

            webView.loadUrl(getUrl().startsWith("http") ? getUrl() : "http://" + getUrl());
        } else {
            if (Strs.isNotEmpty(getTitleStr())) {
                UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), getTitleStr());
            }
            if (Strs.isNotEmpty(getContentStr())) {
                webView.loadData(getContentStr(), "text/html; charset=UTF-8", null);
            }
        }

        Dialogs.showProgressDialog(ActWebX5.this, "");
    }

    /**
     * 将cookie同步到WebView
     *
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    public static boolean syncCookieWithHttp(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        com.tencent.smtt.sdk.CookieSyncManager.createInstance(Global.app);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();//移除
        if (Strs.isNotEmpty(url)) {
            String lastCookie = AbHttpAO.getIns().getLastCookie();
            String[] arrCookie = lastCookie.split(";");
            for (int i = 0; i < arrCookie.length; i++) {
                if (Strs.isNotEmpty(arrCookie[i]) && com.desheng.base.manager.UserManager.getIns().isLogined()) {
                    if (httpUrl != null) {
                        cookieManager.setCookie(httpUrl.host(), arrCookie[i]);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT < 21) {
            com.tencent.smtt.sdk.CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }
        String newCookie = cookieManager.getCookie(url);
        return TextUtils.isEmpty(newCookie);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE || mUploadCallbackAboveL == null) {
            return;
        }

        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {

            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.clearCache(true);
            webView.destroy();
        }
    }
}
