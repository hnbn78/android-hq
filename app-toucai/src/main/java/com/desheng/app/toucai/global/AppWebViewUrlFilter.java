package com.desheng.app.toucai.global;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.ab.util.Strs;
import com.ab.webview.IOverrideUrlFilter;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.manager.UserManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class AppWebViewUrlFilter implements IOverrideUrlFilter {

    @Override
    public boolean filterUrl(Activity act, View webView, String subject, String url) {
        if (url.contains("yx/home") || url.contains("/lottery") || url.contains("registerLogin/index.html")) {
            if (url.contains("registerLogin/index.html")) { // query写到#fragment后面了，过滤一下
                url = url.replace("#/login", ""); // url不能有#开头的路径
            }

            if (url.contains("Fregister")) {
                UserManager.getIns().redirectToRegist();
            }

            Uri totalUri = Uri.parse(url);

            String backUrl = totalUri.getQueryParameter("backUrl");
            if (backUrl != null && totalUri.getQueryParameter("app_cmd") == null) { // app_cmd写到backurl后面，导致签到url，app_cmd被解析到嵌套的url里面了
                backUrl = backUrl.replace("#/", ""); // url不能有#开头的路径
                totalUri = Uri.parse(backUrl);
            }

            String app_cmd = totalUri.getQueryParameter("app_cmd");
            String cmdStr = null;
            try {
                if (Strs.isNotEmpty(app_cmd)) {
                    cmdStr = URLDecoder.decode(app_cmd, "utf-8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (Strs.isNotEmpty(cmdStr)) {
                Uri cmd = Uri.parse(cmdStr);
                if (cmd != null && cmd.getScheme() != null && cmd.getScheme().toLowerCase().equals("panel")) {
                    if ("login".equals(cmd.getHost())) {
                        if (UserManager.getIns().isNotLogined()) {
                            UserManager.getIns().redirectToLogin();
                        } else {
                            act.finish();
                        }
                    } else if ("lottery-play".equals(cmd.getHost())) {
                        String lotteryId = cmd.getQueryParameter("id");
                        ILotteryKind kind = LotteryKind.find(Strs.parse(lotteryId, 0));
                        CtxLotteryTouCai.launchLotteryPlay(act, kind);
                    }
                } else {
                    act.finish();
                }
            } else {
                act.finish();
            }
            return true;
        }
        return false;
    }
}
