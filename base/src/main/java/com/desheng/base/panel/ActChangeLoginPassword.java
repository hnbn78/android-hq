package com.desheng.base.panel;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by user on 2018/3/5.
 */

public class ActChangeLoginPassword extends AbAdvanceActivity implements View.OnClickListener {
    private EditText current_password_et, new_password_et, aggin_et, zijingPwd;
    String currentpwd;
    String newpwd;
    String againPassword;
    private TextView save_tv;
    private ImageView imageView;
    private boolean redirectToModifyPwd;
    @Override
    protected int getLayoutId() {
        return R.layout.act_modify_password;
    }

    @Override
    public void init() {

        UIHelper.getIns().simpleToolbarLeftBackAndCenterTitleAndeRightTitle(this, getToolbar(), "修改登录密码", "确认修改");
        setStatusBarTranslucentAndLightContentWithPadding();

        initView();
        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentpwd = current_password_et.getText().toString();
                if (null == currentpwd || "".equals(currentpwd)) {
                    Toast.makeText(ActChangeLoginPassword.this, "请输入当前密码", Toast.LENGTH_LONG).show();
                    return;
                }

                newpwd = new_password_et.getText().toString();
                if (null == newpwd || "".equals(newpwd)) {
                    Toast.makeText(ActChangeLoginPassword.this, "请输入新密码", Toast.LENGTH_LONG).show();
                    return;
                }
                Pattern pattern = Pattern.compile("[0-9A-Z_a-z]{6,24}");
                if(!pattern.matcher(newpwd).matches()){
                    Toasts.show(ActChangeLoginPassword.this, "密码为6到24位字母和数字组合", false);
                    return;
                }
                Pattern pattern2 = Pattern.compile("[0-9]+"); //必须
                if(!pattern2.matcher(newpwd).find()){
                    Toasts.show(ActChangeLoginPassword.this, "密码为大小写字母开头的6到24位字母和数字组合, 必须同时包含字符,数字", false);
                    return;
                }

                againPassword = aggin_et.getText().toString();
                if (null==againPassword||"".equals(againPassword)){
                    Toast.makeText(ActChangeLoginPassword.this, "请再次输入密码", Toast.LENGTH_LONG).show();
                    return;

                }
                if (!newpwd.equals(againPassword)) {
                    Toast.makeText(ActChangeLoginPassword.this, "两次输入的密码要保持一致！！！", Toast.LENGTH_LONG).show();
                    return;
                }
                String zijingmima = zijingPwd.getText().toString().trim();
                if (redirectToModifyPwd) {
                    if (Strs.isEmpty(zijingmima)) {
                        Toast.makeText(ActChangeLoginPassword.this, "请输入资金密码！", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                modifyLoginPassword(redirectToModifyPwd, zijingmima);
            }
        });

        redirectToModifyPwd = getIntent().getBooleanExtra("redirectToModifyPwd", false);
        zijingPwd.setVisibility(redirectToModifyPwd ? View.VISIBLE : View.GONE);

    }

    private void initView (){
        current_password_et =(EditText) findViewById(R.id.current_password_et);
        new_password_et =(EditText) findViewById(R.id.new_password_et);
        aggin_et =(EditText) findViewById(R.id.aggin_et);
        zijingPwd = findViewById(R.id.zijingPwd);
    }



    public static void launch(Activity act) {
        Intent itt = new Intent(act, ActChangeLoginPassword.class);
        act.startActivity(itt);
        act.finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 修改登录密码
     */
    private void modifyLoginPassword(boolean isFromRedirectToModifyPwd, String zijingmima) {
     HttpAction.modifyLoginPassword(currentpwd,newpwd, isFromRedirectToModifyPwd, zijingmima,  new AbHttpResult(){

         @Override
         public void setupEntity(AbHttpRespEntity entity) {
             super.setupEntity(entity);
         }


         @Override
         public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
              if(code==0&&error==0){
                  Toasts.show(ActChangeLoginPassword.this,"修改成功!", true);
                  UserManager.getIns().logout(new UserManager.IUserLogoutCallback() {
    
                      @Override
                      public void onBefore() {
        
                      }
    
                      @Override
                      public void onUserLogouted() {
                          UserManager.getIns().reLogin(ActChangeLoginPassword.this, UserManager.getIns().getMainUserName());
                          finish();
                      }
        
                      @Override
                      public void onUserLogoutFailed(String msg) {
                          Toasts.show(ActChangeLoginPassword.this, msg, false);
                      }
    
                      @Override
                      public void onAfter() {
        
                      }
    
                  });
                  finish();
              }else {
                  Toasts.show(ActChangeLoginPassword.this, msg);
              }
              
             return  true;
         };

         @Override
         public boolean onError(int status, String content) {

             return false;
         }



     });



    }


}
