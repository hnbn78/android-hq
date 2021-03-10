package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.ab.global.Global;
import com.ab.util.Strs;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.base.model.MessageFullBean;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.ArrayList;

public class ActHomeNoticeDetail extends AbAdvanceActivity {

    private ArrayList<MessageFullBean> mListMessage;
    private TextView tv_title;
    private MessageFullBean messageFullBean;
    private TextView tvTitle;
    private TextView tvDate;

    @Override
    protected int getLayoutId() {
        return R.layout.act_home_notice_detail;
    }

    public static void launch(Activity act, MessageFullBean messageFullBean) {
        Intent intent = new Intent(act, ActHomeNoticeDetail.class);
        intent.putExtra("messageFullBean", messageFullBean);
        act.startActivity(intent);
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "公告");
        setStatusBarTranslucentAndLightContentWithPadding();
        tvTitle = ((TextView) findViewById(R.id.tvTitle));
        tvDate = ((TextView) findViewById(R.id.tvDate));
        messageFullBean = getIntent().getParcelableExtra("messageFullBean");

        WebView web_content = (WebView) findViewById(R.id.content);
        web_content.setBackgroundColor(Color.parseColor("#00000000"));
        WebSettings settings = web_content.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = Global.app.getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        web_content.setVerticalScrollBarEnabled(true);
        web_content.setHorizontalScrollBarEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");//设置默认为utf-8

        if (messageFullBean != null) {
            tvTitle.setText(messageFullBean.getTitle());
            tvDate.setText(messageFullBean.getTime());

            String content = messageFullBean.getContent();
            StringBuilder sb = new StringBuilder();

            String s = CtxLotteryTouCai.transHtmlFrag(content);
            web_content.loadData(s, "text/html; charset=UTF-8", null);
        }
    }

}
