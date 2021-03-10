package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.ab.http.AbHttpAO;
import com.desheng.app.toucai.model.UserPushMsg;
import com.desheng.base.R;
import com.desheng.base.context.CtxLottery;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

@Deprecated
public class ActUserMsgDetail extends AbAdvanceActivity {

    private TextView tv_sender_name;
    private TextView tv_subject_name;
    private WebView webView;
    private UserPushMsg messageInfo;

    public static void launch(Activity ctx, UserPushMsg item) {

        Intent intent = new Intent(ctx, ActUserMsgDetail.class);
        intent.putExtra("message_detail", item);
        ctx.startActivity(intent);
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "消息详情");
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_sender_name = (TextView) findViewById(R.id.tv_sender_name);
        tv_subject_name = (TextView) findViewById(R.id.tv_subject_name);
        webView = (WebView) findViewById(R.id.vWebview);
        messageInfo = (UserPushMsg) getIntent().getSerializableExtra("message_detail");

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

        if (null != messageInfo) {
            tv_sender_name.setText(messageInfo.pushTime);
            tv_subject_name.setText(messageInfo.title);
            String content = CtxLottery.transHtmlFrag(messageInfo.content.replace("\n", "<br/>"));
            webView.loadData(content, "text/html; charset=UTF-8", null);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_message_detail;
    }


}
