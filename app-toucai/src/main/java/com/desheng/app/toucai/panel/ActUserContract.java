package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;

import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

public class ActUserContract extends AbAdvanceActivity {

    public static void launch(Context act) {
        Intent itt = new Intent(act, ActUserContract.class);
        act.startActivity(itt);
    }

    WebView webView;

    @Override
    protected int getLayoutId() {
        return R.layout.act_user_contract;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "用户使用协议");
        setStatusBarTranslucentAndLightContentWithPadding();
        webView = findViewById(R.id.web_content);
//        String content = Files.readAssets("user_contract.html");
//        webView.getSettings().setDefaultTextEncodingName("UTF-8");
//        webView.loadData(content, "text/html; charset=UTF-8", null);
        webView.loadUrl("file:///android_asset/user_contract.html");
        webView.getSettings().setTextZoom(70);
    }
}
