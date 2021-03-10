package com.desheng.app.toucai.util;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.dialog.AbSampleDialogFragment;
import com.ab.global.Config;
import com.ab.global.Global;
import com.ab.http.AbHttpAO;
import com.ab.util.FileUtils;
import com.ab.util.Strs;
import com.shark.tc.R;

import okhttp3.HttpUrl;


public class DialogDepositHelp2 extends AbSampleDialogFragment {
    public static DialogDepositHelp2 curr = null;
    private static String title;
    private ImageView ivClose;
    private WebView webView;
    private TextView tvTitle;

    public static void showIns(AppCompatActivity ctx, String code) {
        if (curr != null) {
            curr.dismiss();
        }
        title = code;
        curr = new DialogDepositHelp2();
        curr.show(ctx.getSupportFragmentManager(), "DialogDepositHelp");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.dialog_deposite_help2, null);
        webView = root.findViewById(R.id.web_guide);
        tvTitle = root.findViewById(R.id.tv_title);
        ivClose = root.findViewById(R.id.iv_close);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        tvTitle.setText(title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Config.isDebug()) {
            WebView.setWebContentsDebuggingEnabled(Config.isDebug());
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setSupportZoom(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);


        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                syncCookieWithHttp(url);
                //Activity act, View webView, String subject, String url
                view.loadUrl(url); //加载此url
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                view.loadUrl("javascript:(function() { " +
                        "var videos = document.getElementsByTagName('video');" +
                        " for(var i=0;i<videos.length;i++){videos[i].play();}})()");

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

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (myCallback != null) {
                    myCallback.onCustomViewHidden();
                    myCallback = null;
                    return;
                }

                long id = Thread.currentThread().getId();

                ViewGroup parent = (ViewGroup) webView.getParent();
                String s = parent.getClass().getName();
                parent.removeView(webView);
                parent.addView(view);
                myView = view;
                myCallback = callback;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);

                intent.setDataAndType(Uri.parse(getVideoUrl()), "video/*");
                startActivity(intent);

            }

            private View myView = null;
            private CustomViewCallback myCallback = null;

            public void onHideCustomView() {

                if (myView != null) {

                    if (myCallback != null) {
                        myCallback.onCustomViewHidden();
                        myCallback = null;
                    }

                    ViewGroup parent = (ViewGroup) myView.getParent();
                    parent.removeView(myView);
                    parent.addView(webView);
                    myView = null;
                }
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

        if (Strs.isNotEmpty(getUrl())) {
            webView.loadData(getUrl(), "text/html; charset=UTF-8", null);
        }

        return root;
    }

    @Nullable
    private String getUrl() {
        String url = null;
        if (title.equals("微信扫码")) {
            url = FileUtils.getStringFormAsset(getActivity(), "weChatScan.html");
        } else if (title.equals("微信转账")) {
            url = FileUtils.getStringFormAsset(getActivity(), "weChatToCard.html");
        } else if (title.equals("支付宝扫码")) {
            url = FileUtils.getStringFormAsset(getActivity(), "aliPayScan.html");
        } else if (title.equals("支付宝转账")) {
            url = FileUtils.getStringFormAsset(getActivity(), "aliPayToCard.html");
        } else if (title.equals("QQ扫码")) {
            url = FileUtils.getStringFormAsset(getActivity(), "QQScan.html");
        } else if (title.equals("QQ转账")) {
            url = FileUtils.getStringFormAsset(getActivity(), "QQAliPay.html");
        }
        return url;
    }


    @Nullable
    private String getVideoUrl() {
        String url = null;
        if (title.equals("微信扫码")) {
            url = "http://video.wotuguanjia.com/%E5%BE%AE%E4%BF%A1%E6%89%AB%E7%A0%81.mp4";
        } else if (title.equals("微信转账")) {
            url = "http://video.wotuguanjia.com/%E6%94%AF%E4%BB%98%E5%AE%9D%E8%BD%AC%E9%93%B6%E8%A1%8C%E5%8D%A1.mp4";
        } else if (title.equals("支付宝扫码")) {
            url = "http://video.wotuguanjia.com/%E6%94%AF%E4%BB%98%E5%AE%9D%E6%89%AB%E4%B8%80%E6%89%AB.mp4";
        } else if (title.equals("支付宝转账")) {
            url = "http://video.wotuguanjia.com/%E6%94%AF%E4%BB%98%E5%AE%9D%E8%BD%AC%E9%93%B6%E8%A1%8C%E5%8D%A1.mp4";
        } else if (title.equals("QQ扫码")) {
            url = "http://video.wotuguanjia.com/qq%E6%89%AB%E4%B8%80%E6%89%AB.mp4";
        } else if (title.equals("QQ转账")) {
            url = "http://video.wotuguanjia.com/pc%E6%94%AF%E4%BB%98%E5%AE%9D%E6%89%AB%E7%A0%81.mp4";
        }
        return url;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }


    }

    /**
     * 将cookie同步到WebView
     *
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    public static boolean syncCookieWithHttp(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        CookieSyncManager.createInstance(Global.app);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();//移除
        if (Strs.isNotEmpty(url)) {
            String lastCookie = AbHttpAO.getIns().getLastCookie();
            String[] arrCookie = lastCookie.split(";");
            for (int i = 0; i < arrCookie.length; i++) {
                if (Strs.isNotEmpty(arrCookie[i]) && com.desheng.base.manager.UserManager.getIns().isLogined()) {
                    cookieManager.setCookie(httpUrl.host(), arrCookie[i]);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
                }
            }
        }

        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }
        String newCookie = cookieManager.getCookie(url);
        return TextUtils.isEmpty(newCookie);
    }

}
