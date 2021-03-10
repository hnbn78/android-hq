package com.desheng.base.panel;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ab.debug.AbDebug;
import com.ab.global.Config;
import com.ab.http.AbHttpAO;
import com.ab.util.AbLogUtil;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.orhanobut.logger.Logger;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import okhttp3.HttpUrl;

/**
 * 通用网页
 * Created by Administrator on 2017/8/1 0001.
 */
public class ActWeb extends AbAdvanceActivity {

    public static void launch(Context act, String url) {
        Intent itt = new Intent(act, ActWeb.class);
        itt.putExtra("url", url);
        act.startActivity(itt);
    }

    /**
     * @param isCanGoBack 返回键是否处理H5的返回
     */
    public static void launch(Context act, String url, boolean isCanGoBack, String title) {
        Intent itt = new Intent(act, ActWeb.class);
        itt.putExtra("url", url);
        itt.putExtra("isCanGoBack", isCanGoBack);
        itt.putExtra("title", title);
        act.startActivity(itt);
    }

    public static void launch(Context act, String title, String url, boolean syncCookie) {
        Intent itt = new Intent(act, ActWeb.class);
        itt.putExtra("title", title);
        itt.putExtra("url", url);
        itt.putExtra("syncCookie", syncCookie);
        act.startActivity(itt);
    }

    public static void launch(Context act, String title, String content) {
        Intent itt = new Intent(act, ActWeb.class);
        itt.putExtra("title", title);
        itt.putExtra("content", content);
        act.startActivity(itt);
    }

    public static void launchOutside(Context act, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        act.startActivity(intent);
    }

    public static void launchCustomService(Context act) {
        Intent itt = new Intent(act, ActWeb.class);
        String url = BaseConfig.CustomServiceLink;
        if (Strs.isNotEmpty(UserManager.getIns().getCustomServiceLink())) {
            url = UserManager.getIns().getCustomServiceLink();
        }
        itt.putExtra("url", url);
        act.startActivity(itt);
    }

    public static void launchAndFinish(Activity act, String url) {
        Intent itt = new Intent(act, ActWeb.class);
        itt.putExtra("url", url);
        act.startActivity(itt);
        act.finish();
    }


    private WebView webView;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private boolean isCanGoBack;

    @Override
    public int getLayoutId() {
        return R.layout.act_web;
    }


    public String getUrl() {
        if (getIntent() != null) {
            return getIntent().getStringExtra("url");
        }
        return null;
    }

    public String getTitleStr() {
        if (getIntent() != null) {
            String title = getIntent().getStringExtra("title");

            if (!Strs.isEmpty(title) && title.length() > 20)
                title = title.substring(0, 20) + "...";

            return title;
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
            boolean isSyncCookie = getIntent().getBooleanExtra("syncCookie", false);
            return isSyncCookie;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isCanGoBack && webView.canGoBack()) {
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    private void onBack() {
        if (isCanGoBack && webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public void onLeftButtonClick() {
        onBack();
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void init() {
        AbDebug.log(AbDebug.TAG_APP, "ActWeb initView");
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "");
        setStatusBarTranslucentAndLightContentWithPadding();
        isCanGoBack = getIntent().getBooleanExtra("isCanGoBack", false);
        webView = (WebView) findViewById(R.id.vWebview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(Config.isDebug());
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setBlockNetworkImage(false);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        // webView.addJavascriptInterface(new InJavaScriptLocalObj(), "HtmlViewer");
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");//设置默认为utf-8

        webView.setWebChromeClient(new WebChromeClient() {
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                //Log.d(TAG, "openFileChoose(ValueCallback<Uri> uploadMsg)");
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                ActWeb.this.startActivityForResult(Intent.createChooser(i, "File Chooser"),
                        FILECHOOSER_RESULTCODE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                //Log.d(TAG, "openFileChoose( ValueCallback uploadMsg, String acceptType )");
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                ActWeb.this.startActivityForResult(Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                //Log.d(TAG, "openFileChoose(ValueCallback<Uri> uploadMsg, String acceptType, String capture)");
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                ActWeb.this.startActivityForResult(Intent.createChooser(i, "File Browser"),
                        ActWeb.FILECHOOSER_RESULTCODE);

            }

            // For Android 5.0+
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                //Log.d(TAG, "onShowFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)");
                mUploadCallbackAboveL = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                ActWeb.this.startActivityForResult(Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
                return true;
            }
        });
        String title = getTitleStr();
        if (!Strs.isEmpty(title)) {
            UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), title);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                HttpUrl httpUrl = HttpUrl.parse(url);
                if (null != httpUrl && !Strs.isEmpty(httpUrl.host()) && !Strs.isEmpty(url)) {
                    syncCookieWithHttp(httpUrl.host());
                    view.loadUrl(url);
                }

                return true;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                if(isCanGoBack){
//                    view.loadUrl("javascript:window.HtmlViewer.showHTML" +
//                            "(document.getElementById('app-title').innerText);");
//                }

            }
        });

        if (isSyncCookie()) {
            syncCookieWithHttp(getUrl());
        }
        if (Strs.isNotEmpty(getUrl())) {
            webView.loadUrl(getUrl().startsWith("http") ? getUrl() : "http://" + getUrl());
        } else {
            if (Strs.isNotEmpty(getTitleStr())) {
                UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), getTitleStr());
            }
            if (Strs.isNotEmpty(getContentStr())) {
                webView.loadData(getContentStr(), "text/html; charset=UTF-8", null);
            }
        }
    }

    /**
     * 将cookie同步到WebView
     *
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    public boolean syncCookieWithHttp(String url) {
        try {
            CookieSyncManager.createInstance(ActWeb.this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeAllCookie();//移除 JSESSIONID=0CE0A3E0562CBB0D10FBC22290B0B404;sso_session_uid=67724d04-3481-4057-8844-ee40745d5622;sso_session_uid_sign=d486e693b827708425aabfd18f3c10e9_-_Njc3MjRkMDQtMzQ4MS00MDU3LTg4NDQtZWU0MDc0NWQ1NjIyXzE1Mjc1NjUwNDc5MjU%3D
            String lastCookie = AbHttpAO.getIns().getLastCookie();
            String[] arrCookie = lastCookie.split(";");
            for (int i = 0; i < arrCookie.length; i++) {
                if (Strs.isNotEmpty(arrCookie[i]) && !arrCookie[i].contains("JSESSIONID")) {
                    cookieManager.setCookie(url.replace("http://", "").replace("https://", ""), arrCookie[i] + ";");//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
                }
            }
            if (Build.VERSION.SDK_INT < 21) {
                com.tencent.smtt.sdk.CookieSyncManager.getInstance().sync();
            } else {
                com.tencent.smtt.sdk.CookieManager.getInstance().flush();
            }
            String newCookie = cookieManager.getCookie(url);
            return TextUtils.isEmpty(newCookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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
            webView.destroy();
        }
    }

//    public final class InJavaScriptLocalObj {
//        @JavascriptInterface
//        public void showHTML(String html) {
//            AbLogUtil.d("html", "html===> " + html);
//            Toasts.show(html,true);
//            if (isCanGoBack) UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), html);
//        }
//    }
}
