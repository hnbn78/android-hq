package com.desheng.app.toucai.global;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ab.global.Global;
import com.ab.http.AbHttpHandler;
import com.ab.http.AbHttpReqEntity;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.CODEC_HTTP;
import com.ab.util.Maps;
import com.ab.util.Toasts;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.panel.ActLoading;
import com.desheng.app.toucai.panel.ActLoginPasswordTouCai;
import com.desheng.app.toucai.panel.ActMain;
import com.desheng.app.toucai.panel.FragTabHome;
import com.desheng.base.manager.UserManager;
import com.pearl.act.AbActManager;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import java.util.Map;

/**
 * Created by lee on 2017/10/2.
 */
public class AppHttpHandler implements AbHttpHandler {

    @Override
    public AbHttpReqEntity genRequestEntity() {
        AbHttpReqEntity entity = new AbHttpReqEntity();
        return entity;
    }

    @Override
    public Map<String, Object> genUninParam() {
        Maps.gen();

        return Maps.get();
    }

    @Override
    public boolean isResponceForUserInvalid(String resp) {
        return resp.contains("<!DOCTYPE html>")
                || resp.contains("<meta http-equiv=\"refresh\" content=\"0;url=/login\">")
                || resp.trim().equals("-1")
                || resp.contains("\"isLogin\" : false");
    }

    @Override
    public void processUserInvalid() {
        Log.e("AppHttpHandler", "processUserInvalid()");
        Activity peelActivity = AbActManager.getPeelActivity();
        //Log.e(TAG, "peelActivity:" + peelActivity);
        if (peelActivity == null || peelActivity instanceof ActLoading) {
            return;
        }

        if (peelActivity == null || peelActivity instanceof ActMain) {
            ActMain actMain = (ActMain) peelActivity;
            int currentTab = actMain.getTabLayout().getCurrentTab();
            if (currentTab == 0) {
                UserManagerTouCai.getIns().setAccount("");
                FragTabHome fragTabHome = (FragTabHome) actMain.mFragments.get(0);
                if (fragTabHome != null) {
                    fragTabHome.updateLoginBtn();
                }
                if (fragTabHome != null && fragTabHome.srlRefresh.getState() == RefreshState.Refreshing) {
                    fragTabHome.srlRefresh.finishRefresh();
                    //UserManagerTouCai.getIns().redirectToLoginForRecheck();
                    return;
                } else {
                    return;
                }
            }
        }
        UserManagerTouCai.getIns().redirectToLoginForRecheck();
    }

    @Override
    public void processUserModefyPwd() {
        UserManager.getIns().redirectToModifyPwd(Global.app);
    }


    @Override
    public void processCommonFailure(AbHttpRespEntity resp, int code, String msg) {
        if (CODEC_HTTP.find(code) != CODEC_HTTP.UNKNOWN) {
            CODEC_HTTP codec = CODEC_HTTP.find(code);
            Log.e("AppHttpHandler", "AppHttpHandler-----processCommonFailure---" + CODEC_HTTP.getDesc(code));
            switch (codec) {
                case ERROR_CODE_LOGIN:
                    UserManagerTouCai.getIns().redirectToLoginForRecheck();
                    break;
                case ERROR_CODE_NEED_MODIFY_PWD:
                    UserManager.getIns().redirectToModifyPwd(Global.app);
                    break;
                case ERROR_CODE_BUSY:
                    Toasts.show(Global.app, "服务器正忙, 请稍候重试!", false);
                    break;
                default:
                    if (codec == CODEC_HTTP.SERVER_EXCEPTION && codec.code >= 500 && codec.code != 509) {
                        Toasts.show(Global.app, "用户登录失效, 请重新登录(" + code + ")!", false);
                        processUserInvalid();
                    }
            }
        } else {
            Toasts.show(Global.app, CODEC_HTTP.getDesc(code), false);
        }
    }
}
