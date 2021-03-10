package com.desheng.app.toucai.panel;

import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.debug.AbDebug;
import com.ab.global.Config;
import com.ab.global.Global;
import com.ab.http.AbHttpAO;
import com.ab.util.Strs;
import com.pearl.act.base.AbBaseFragment;
import com.shark.tc.R;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import okhttp3.HttpUrl;

public class FragX5WebView extends AbBaseFragment {
    protected WebView webView;
    private View progress;
    @Override
    public int getLayoutId() {
        return R.layout.frag_x5_webview;
    }


    public String getSubject() {
        if (getArguments() != null) {
            return getArguments().getString("subject");
        }
        return null;
    }

    public String getUrl() {
        if (getArguments() != null) {
            return getArguments().getString("url");
        }
        return null;
    }

    public String getTitleStr() {
        if (getArguments() != null) {
            return getArguments().getString("title");
        }
        return null;
    }

    public String getContentStr() {
        if (getArguments() != null) {
            String content = getArguments().getString("content");
            content = Strs.toUTF8(content);
            return content;
        }
        return null;
    }

    public boolean isSyncCookie() {
        if (getArguments() != null) {
            boolean isSyncCookie = getArguments().getBoolean("syncCookie", false);
            return isSyncCookie;
        }
        return false;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                onLeftButtonClick();
//                break;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void init(View root) {
        progress = root.findViewById(R.id.llProgress);
        webView = (WebView) root.findViewById(com.desheng.base.R.id.vWebview);
        AbDebug.log(AbDebug.TAG_APP, webView.getX5WebViewExtension() == null ? "SysWeb initView*********" : "X5Web initView**********");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Config.isDebug()) {
            WebView.setWebContentsDebuggingEnabled(Config.isDebug());
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
        settings.setUserAgent("Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 ToucaiH5");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                syncCookieWithHttp(url);
                view.loadUrl(url); //加载此url
                showProgress(true);
                return true;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                showProgress(false);
            }
        });



        //设置响应js 的Alert()函数
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
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
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
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
                final View v = View.inflate(getActivity(), com.desheng.base.R.layout.webview_prompt_dialog, null);
                ((TextView) v.findViewById(com.desheng.base.R.id.prompt_message_text)).setText(message);
                ((EditText) v.findViewById(com.desheng.base.R.id.prompt_input_field)).setText(defaultValue);
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
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
        });

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
    }

    protected void showProgress(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected void loadUrl() {
        if (isSyncCookie()) {
            syncCookieWithHttp(getUrl());
        }
        if (Strs.isNotEmpty(getUrl())) {
            webView.loadUrl(getUrl().startsWith("http") ? getUrl() : "http://" + getUrl());
        }else{
            if (Strs.isNotEmpty(getContentStr())) {
                webView.loadData(getContentStr(), "text/html; charset=UTF-8", null);
            }
        }

        showProgress(true);
    }

    public void reload() {
        webView.reload();
    }

    /**
     * 将cookie同步到WebView
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    public static boolean syncCookieWithHttp(String url) {
        HttpUrl httpUrl =  HttpUrl.parse(url);
        com.tencent.smtt.sdk.CookieSyncManager.createInstance(Global.app);
        com.tencent.smtt.sdk.CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();//移除
        if (Strs.isNotEmpty(url)) {
            String lastCookie = AbHttpAO.getIns().getLastCookie();
            String [] arrCookie = lastCookie.split(";");
            for (int i = 0; i < arrCookie.length; i++) {
                if (Strs.isNotEmpty(arrCookie[i]) && com.desheng.base.manager.UserManager.getIns().isLogined()) {
                    cookieManager.setCookie(httpUrl.host(), arrCookie[i]);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
                }
            }
        }

        if (Build.VERSION.SDK_INT < 21) {
            com.tencent.smtt.sdk.CookieSyncManager.getInstance().sync();
        } else {
            com.tencent.smtt.sdk.CookieManager.getInstance().flush();
        }
        String newCookie = cookieManager.getCookie(url);
        return TextUtils.isEmpty(newCookie);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }}




