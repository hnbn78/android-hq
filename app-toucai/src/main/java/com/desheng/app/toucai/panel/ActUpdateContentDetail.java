package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.ab.http.AbHttpAO;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class ActUpdateContentDetail extends AbAdvanceActivity {

    private TextView tv_sender_name;
    private TextView tv_subject_name;
    private WebView webView;

    public static void launch(Activity ctx, String date, String theme, String content) {

        Intent intent = new Intent(ctx, ActUpdateContentDetail.class);
        intent.putExtra("date", date);
        intent.putExtra("theme", theme);
        intent.putExtra("content", content);
        ctx.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_update_content_detail;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "消息详情");
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_sender_name = (TextView) findViewById(R.id.tv_sender_name);
        tv_subject_name = (TextView) findViewById(R.id.tv_subject_name);
        webView = (WebView) findViewById(R.id.vWebview);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
        settings.setUserAgent(AbHttpAO.getUserAgent());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Activity act, View webView, String subject, String url
                view.loadUrl(url); //加载此url
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });

        String date = getIntent().getStringExtra("date");
        String theme = getIntent().getStringExtra("theme");
        String content = getIntent().getStringExtra("content");
        tv_sender_name.setText(date);
        tv_subject_name.setText(theme);
        webView.loadData(content, "text/html; charset=UTF-8", null);
    }

}
