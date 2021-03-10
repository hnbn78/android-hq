package com.desheng.base.panel;

import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpResult;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.model.ListMessageBean;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.HashMap;

import okhttp3.Request;

public class ActMessageDetail extends AbAdvanceActivity {

    private TextView tv_sender_name;
    private TextView tv_subject_name;
    private WebView webView;
    private ListMessageBean.ListBean messageInfo;
    private TextView textContent;

    public static void launch(Activity ctx, ListMessageBean.ListBean item) {

        Intent intent = new Intent(ctx, ActMessageDetail.class);
        intent.putExtra("message_detail", item);
        ctx.startActivity(intent);
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "消息详情");
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_sender_name = (TextView) findViewById(R.id.tv_sender_name);
        tv_subject_name = (TextView) findViewById(R.id.tv_subject_name);
        textContent = ((TextView) findViewById(R.id.textContent));
        textContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        webView = (WebView) findViewById(R.id.vWebview);
        messageInfo = (ListMessageBean.ListBean) getIntent().getSerializableExtra("message_detail");
        if (messageInfo != null) {
            setMessageStatus(messageInfo.getUserMessageId());
        }
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
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
            tv_sender_name.setText(messageInfo.getCreateDateString());
            tv_subject_name.setText(messageInfo.getTitle());
            String content = CtxLottery.transHtmlFrag(messageInfo.getContent().replace("&nbsp;", "<br/>"));
            //textContent.setText(messageInfo.getContent());
            webView.loadData(content, "text/html; charset=UTF-8", null);
        }
    }

    private void setMessageStatus(int mesgId) {
        HttpAction.setReadMessage(this, String.valueOf(mesgId), new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_message_detail;
    }


}
