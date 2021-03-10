package com.desheng.base.panel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ab.global.AbDevice;
import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.controller.FloatActionController;
import com.desheng.base.event.CloseFloatEvent;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.model.UserCenterInfo;
import com.desheng.base.util.FingerprintUtil;
import com.desheng.base.util.SpUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;

/**
 * 设置界面
 * Created by user on 2018/3/2.
 */

public class ActSetting extends AbAdvanceActivity {
    
    private UserCenterInfo userCenterInfo;
    private TextView tvVersion;
    private UserManager.EventReceiver receiver;
    private RelativeLayout vgScanQR;
    private MaterialDialog materialDialog;
    
    public static void launch(Context act) {
        Intent itt = new Intent(act, ActSetting.class);
        act.startActivity(itt);
    }
    
    private RelativeLayout VersionUpdate, AboutUs, Help_Center, Share_app, ModfiyFunds, ModfiyLogin;
    private Button Bt_exit;
    private TextView current_text;
    private ToggleButton tgFloat, tgFinger;
    private Context mContext;
    private int postion = 0;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_seting;
    }
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "设置");
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
        mContext = this;
        
        //getUserAllData();
        EventBus.getDefault().register(this);
        receiver = new UserManager.EventReceiver().register(this, new UserManager.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (UserManager.EVENT_USER_LOGOUTED.equals(eventName)) {
                    finish();
                }
            }
        });
    }
    
    /**
     * 初始化所有控件
     */
    private void initView() {
        tgFloat = (ToggleButton) findViewById(R.id.line_switch);
        tgFloat.setChecked(UserManager.getIns().isShowFloat());
        tgFloat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserManager.getIns().setShowFloat(isChecked);
                if (isChecked) {
                    FloatActionController.getInstance().show(ActSetting.this);
                } else {
                    FloatActionController.getInstance().hide();
                }
            }
        });
        tgFinger = (ToggleButton) findViewById(R.id.finger_switch);
        tgFinger.setChecked(UserManager.getIns().isUserFingerPrint());
        registFingerToggle(true);
    
        Bt_exit = (Button) findViewById(R.id.exit_bt);
        Bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitlogin();
                
            }
        });
        VersionUpdate = (RelativeLayout) findViewById(R.id.vgVersion);
        VersionUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVersionUpdate();
            }
        });
        AboutUs = (RelativeLayout) findViewById(R.id.eight_layout);
        AboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ActSetting.this, ActAboutUs.class);
                startActivity(intent);
            }
        });
        Help_Center = (RelativeLayout) findViewById(R.id.seven_layout);
        Help_Center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ActSetting.this, ActHelpCenter.class);
                startActivity(intent);
            }
        });
        Share_app = (RelativeLayout) findViewById(R.id.six_layout);
        Share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMsg(ActSetting.this, null, Views.fromStrings(R.string.app_name), ENV.curr.host + "/static/update/install.html", null);
                
            }
        });
        
        
        ModfiyFunds = (RelativeLayout) findViewById(R.id.third_layout);
        ModfiyFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBankBindAndGotoChangeFundPassword();
                
            }
        });
        ModfiyLogin = (RelativeLayout) findViewById(R.id.second__layout);
        ModfiyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ActSetting.this, ActChangeLoginPassword.class);
                startActivity(intent);
            }
        });
        vgScanQR = (RelativeLayout) findViewById(R.id.first__layout);
        vgScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasts.show(ActSetting.this, "即将推出, 敬请期待", true);
            }
        });
    
        tvVersion = (TextView)findViewById(R.id.tvVersion);
        tvVersion.setText(AbDevice.appVersionName);
    }
    
    private void registFingerToggle(boolean isNeed) {
        if(isNeed){
            tgFinger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onFingerprintClick(isChecked);
                }
            });
        }else{
            tgFinger.setOnCheckedChangeListener(null);
        }
    }
    
    
    public void onFingerprintClick(final boolean isChecked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            FingerprintUtil.callFingerPrint(new FingerprintUtil.OnCallBackListenr() {
                AlertDialog dialog;
                
                @Override
                public void onSupportFailed() {
                    registFingerToggle(false);
                    tgFinger.setChecked(!isChecked);
                    registFingerToggle(true);
                    Toasts.show(ActSetting.this, "当前设备不支持指纹");
                }
                
                @Override
                public void onInsecurity() {
                    registFingerToggle(false);
                    tgFinger.setChecked(!isChecked);
                    registFingerToggle(true);
                    Toasts.show(ActSetting.this, "请设置指纹解锁");
                }
                
                @Override
                public void onEnrollFailed() {
                    registFingerToggle(false);
                    tgFinger.setChecked(!isChecked);
                    registFingerToggle(true);
                    Toasts.show(ActSetting.this, "请到设置中设置指纹");
                }
                
                @Override
                public void onAuthenticationStart() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    View view = LayoutInflater.from(mContext).inflate(R.layout.layout_fingerprint, null);
                    initfinget(view);
                    builder.setView(view);
                    builder.setCancelable(false);
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.removeMessages(0);
                            registFingerToggle(false);
                            tgFinger.setChecked(!isChecked);
                            registFingerToggle(true);
                            FingerprintUtil.cancel();
                        }
                    });
                    
                    dialog = builder.create();
                    dialog.show();
                }
                
                @Override
                public void onAuthenticationError(int errMsgId, CharSequence errString) {
                    Toasts.show(ActSetting.this, errString.toString(), false);
                    registFingerToggle(false);
                    tgFinger.setChecked(!isChecked);
                    registFingerToggle(true);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                        handler.removeMessages(0);
                    }
                    FingerprintUtil.cancel();
                }
                
                @Override
                public void onAuthenticationFailed() {
                    registFingerToggle(false);
                    tgFinger.setChecked(!isChecked);
                    registFingerToggle(true);
                    Toasts.show(ActSetting.this, "设置失败", false);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                        handler.removeMessages(0);
                    }
                    FingerprintUtil.cancel();
                }
                
                @Override
                public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                    Toasts.show(ActSetting.this, helpString.toString());
                }
                
                @Override
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    Toasts.show(ActSetting.this, "设置成功", true);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                        handler.removeMessages(0);
                    }
                    UserManager.getIns().setUserFingerPrint(isChecked);
                }
            });
        }else{
            registFingerToggle(false);
            tgFinger.setChecked(!isChecked);
            registFingerToggle(true);
            Toasts.show(ActSetting.this, "当前设备系统版本过低!", false);
        }
    }
   
    
    /**
     * 分享
     *
     * @param context
     * @param activityTitle
     * @param msgTitle
     * @param msgText
     * @param imgPath
     */
    public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText,
                                String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
        
    }
 
    @Override
    protected void onStart() {
        super.onStart();
        FloatActionController.checkAndShowFloat(ActSetting.this);
    }
    
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseFloatEvent(CloseFloatEvent event) {
        tgFloat.setChecked(false);
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (receiver != null) {
            receiver.unregister(this);
        }
    }
    
    
    private Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                int i = postion % 5;
                if (i == 0) {
                    tv[4].setBackground(null);
                    tv[i].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    tv[i].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv[i - 1].setBackground(null);
                }
                postion++;
                handler.sendEmptyMessageDelayed(0, 100);
            }
        }
    };
    TextView[] tv = new TextView[5];
    
    private void initfinget(View view) {
        postion = 0;
        tv[0] = (TextView) view.findViewById(R.id.tv_1);
        tv[1] = (TextView) view.findViewById(R.id.tv_2);
        tv[2] = (TextView) view.findViewById(R.id.tv_3);
        tv[3] = (TextView) view.findViewById(R.id.tv_4);
        tv[4] = (TextView) view.findViewById(R.id.tv_5);
        handler.sendEmptyMessageDelayed(0, 100);
    }
    
    
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    
    /**
     * 退出登录
     */
    
    public void exitlogin() {
        String pwdStr = "";
        if(UserManager.getIns().isRememberedPassword() && Strs.isNotEmpty(UserManager.getIns().getPassword())){
            pwdStr = UserManager.getIns().getPassword();
        }
        final String finalPwd = pwdStr;


        UserManager.getIns().logout(new UserManager.IUserLogoutCallback() {
            @Override
            public void onBefore() {
                Dialogs.showProgressDialog(ActSetting.this, "");
            }
            
            @Override
            public void onUserLogouted() {
                if (Strs.isNotEmpty(finalPwd)) {
                    UserManager.getIns().reLogin(ActSetting.this, UserManager.getIns().getMainUserName(), UserManager.getIns().getPassword());
                }else{
                    UserManager.getIns().reLogin(ActSetting.this, UserManager.getIns().getMainUserName());
                }

                finish();
            }
            
            @Override
            public void onUserLogoutFailed(String msg) {
                Toasts.show(ActSetting.this, msg, false);
            }
            
            @Override
            public void onAfter() {
                Dialogs.hideProgressDialog(ActSetting.this);
            }
        });
        
        
    }
    
    public void showCheckBankDialog() {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
        materialDialog = new MaterialDialog(ActSetting.this);
        materialDialog.setMessage("亲，先绑定取款人");
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                ActBindBankCard.launch(ActSetting.this, true);
            }
        });
        materialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }
    
    /**
     * 验证用户是否绑定资料
     */
    public void checkBankBindAndGotoChangeFundPassword() {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {
        
            }
    
            @Override
            public void onUserBindChecked(UserBindStatus status) {
                if(status.isBindWithdrawPassword()){
                    ActChangeFundPassword.launch(ActSetting.this);
                }else{
                    showCheckBankDialog();
                }
            }
    
            @Override
            public void onUserBindCheckFailed(String msg) {
        
            }
    
            @Override
            public void onAfter() {
        
            }
        });
    }
    
    /**
     * 用户中心获取用户所有资料接口
     */
    
    
    public void getUserAllData() {
        HttpAction.getUserCenterInfo(ActSetting.this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", UserCenterInfo.class);
            }
            
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    userCenterInfo = (UserCenterInfo)getField(extra, "data", null);
                }else{
                    Toasts.show(ActSetting.this, msg, false);
                }
                 return true;
            }
           
        });
    }
    
    
    /**
     * 版本更新
     */
    public void getVersionUpdate() {
        if(!Config.custom_flag.contains("demo")){
            UserManager.getIns().checkLatestVersion(ActSetting.this, new UserManager.IVersionUpdateCallback() {
                @Override
                public void noNeedUpdate() {
                    Toasts.show(ActSetting.this, "已是最新版本!", true);
                }

                @Override
                public void notTipUpdate() {

                }
            });
        }
    }
    
}
