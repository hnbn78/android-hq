package com.pearl.act.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ab.debug.AbDebug;
import com.pearl.act.AbActManager;
import com.pearl.act.ActModule;
import com.pearl.act.R;


/**
 * Created by hzp on 16/8/31.
 */

public  class AbBaseActivity extends AppCompatActivity implements AbActManager.IManagable {
    
    //快速启动
    public static void simpleLaunch(Activity ctx, Class clazz){
        Intent itt = new Intent(ctx, clazz);
        ctx.startActivity(itt);
    }
   
    
    //快速启动class, 结束自身
    public static void simpleLaunchAndFinish(Activity ctx, Class clazz){
        Intent itt = new Intent(ctx, clazz);
        ctx.startActivity(itt);
        ctx.finish();
    }
    
    //启动其他activity
    public void launchOther(Class<? extends Activity> clazz) {
        startActivity(new Intent(this, clazz));
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);//android.R.anim.fade_out
    }
    
    //启动其他activity, 并结束自身
    public void launchOtherAndFinish(Class<? extends Activity> clazz) {
        startActivity(new Intent(this, clazz));
        finish();
        //overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);//android.R.anim.fade_out
    }
    
   
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AbDebug.log(AbDebug.TAG_UI, "activity 启动: " + this.getClass().getSimpleName() +"#onCreate()!");
        //addSelf();
    }
    
    
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if(ActModule.isAnalyticsEnable) {
        
        }
        addSelf();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(ActModule.isAnalyticsEnable) {
        }
        removeSelf();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //removeSelf();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AbDebug.log(AbDebug.TAG_UI, "activity 销毁: " + this.getClass().getSimpleName() +"#onCreate()!");
        //removeSelf();
    }
    
    @Override
    public void addSelf() {
        AbActManager.add(this);
        AbActManager.info();
    }
    
    @Override
    public void removeSelf() {
        AbActManager.remove(this);
        AbActManager.info();
    }
    
    public void setText(int id, String text){
        TextView v = null;
        try{
            v = (TextView) findViewById(id);
        }catch (Exception e){
            AbDebug.error(AbDebug.TAG_UI, Thread.currentThread(), e);
        }
        
        if(v != null && text != null){
            v.setText(text);
        }
    }
    
    public void setText(TextView textView, String text){
        TextView v = textView;
        if(v != null && text != null){
            v.setText(text);
        }
    }
    
    public void setClickListner(int id, View.OnClickListener listener){
        View v = null;
        try{
            v = findViewById(id);
        }catch (Exception e){
            AbDebug.error(AbDebug.TAG_UI, Thread.currentThread(), e);
        }
        
        if(v != null){
            v.setOnClickListener(listener);
        }
    }
    
}
