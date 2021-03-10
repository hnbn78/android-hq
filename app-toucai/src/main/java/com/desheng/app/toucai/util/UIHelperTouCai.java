package com.desheng.app.toucai.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ab.global.Global;
import com.ab.util.Views;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

/**
 * Created by lee on 2018/3/5.
 */

public class UIHelperTouCai extends UIHelper{
    
    static {
        colorPrimary = Views.fromColors(R.color.colorPrimary);
        colorPrimaryId = R.color.colorAccent;
        final int resourceId = Global.app.getResources().getIdentifier("toolbar_bg", "drawable",
                Global.app.getPackageName());
        if(resourceId > 0){
            toolbarBgResId = resourceId;
        }
    }
    
    /**
     * 简化方法
     * @param toolbar
     * @param title
     */
    @Override
    public void simpleToolbar(Toolbar toolbar, String title) {
        simpleToolbarTitleCenterDefault(toolbar, title);
        setToolbarLeftButtonVisiblity(toolbar, View.INVISIBLE);
    }
    
    /**
     * 简化方法
     * @param toolbar
     * @param title
     */
    @Override
    public  void simpleToolbarWithBackBtn(Toolbar toolbar, String title) {
        simpleToolbarTitleCenterDefault(toolbar, title);
        setToolbarLeftBtn((ImageButton) toolbar.findViewById(R.id.ibLeftBtn), UIHelper.BACK_WHITE_CIRCLE_ARROW);
        
    }

    @Override
    public void setToolbarTitleCenterEndDrawable(Toolbar toolbar, Drawable drawable, View.OnClickListener onClickListener) {
        // FIXME: 2018/6/29/029 空实现
    }

    /**
     * 简化方法
     * 设置中间标题
     * @param toolbar
     * @param str
     */
    @Override
    public  void simpleToolbarTitleCenterDefault(Toolbar toolbar, String str){
        setToolbarTitleCenter(toolbar, str, R.dimen.tool_bar_text_title, R.color.white);
        if (toolbarBgResId > 0) {
            setToolbarBgImage(toolbar, toolbarBgResId);
        }else{
            setToolbarBgColor(toolbar, colorPrimaryId);
        }
    }
    
    /**
     * 左侧按钮 与右侧标题带退出功能
     */
    @Override
    public  void simpleToolbarLeftBackAndRightTitle(final Context ctx, final Toolbar toolbar, String title) {
        ImageButton ibLeftBtn = (ImageButton) toolbar.findViewById(R.id.ibLeftBtn);
        TextView tvRightBtn = (TextView) toolbar.findViewById(R.id.tvRightBtn);
        UIHelper.setToolbarLeftBtn(ibLeftBtn, UIHelper.BACK_WHITE_CIRCLE_ARROW);
        if (toolbarBgResId > 0) {
            setToolbarBgImage(toolbar, toolbarBgResId);
        }else{
            setToolbarBgColor(toolbar, colorPrimaryId);
        }
        UIHelper.setToolbarButtonRightText(tvRightBtn, title);
        UIHelper.setToolbarButtonRightTextSize(tvRightBtn, 16);
        UIHelper.setToolbarButtonRightTextColor(tvRightBtn, R.color.white);
        UIHelper.setToolbarRightButtonGroupClickListener(toolbar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)ctx).finish();
            }
        });
    }
    
    /**
     * 左侧退出, 中间标题, 右侧退出
     * @param ctx
     * @param toolbar
     * @param centerTitle
     * @param rightTitle
     */
    @Override
    public void simpleToolbarLeftBackAndCenterTitleAndeRightTitle(final Context ctx, Toolbar toolbar, String centerTitle, String rightTitle) {
        ImageButton ibLeftBtn = (ImageButton) toolbar.findViewById(R.id.ibLeftBtn);
        TextView tvRightBtn = (TextView) toolbar.findViewById(R.id.tvRightBtn);
        UIHelper.setToolbarLeftBtn(ibLeftBtn, UIHelper.BACK_WHITE_CIRCLE_ARROW);
        if (toolbarBgResId > 0) {
            setToolbarBgImage(toolbar, toolbarBgResId);
        }else{
            setToolbarBgColor(toolbar, colorPrimaryId);
        }
        UIHelper.setToolbarButtonRightText(tvRightBtn, rightTitle);
        UIHelper.setToolbarButtonRightTextSize(tvRightBtn, 16);
        UIHelper.setToolbarTitleCenter(toolbar, centerTitle, R.dimen.tool_bar_text_title, R.color.white);
        UIHelper.setToolbarButtonRightTextColor(tvRightBtn, R.color.white);
        UIHelper.setToolbarRightButtonGroupClickListener(toolbar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)ctx).finish();
            }
        });
    }
    
    /**
     * 左侧退出, 中间标题, 右侧文字按钮
     * @param ctx
     * @param toolbar
     * @param centerTitle
     * @param rightBtn
     * @param rightListener
     */
    @Override
    public void simpleToolbarLeftBackAndCenterTitleAndeRightTextBtn(final Context ctx, Toolbar toolbar, String centerTitle, String rightBtn, View.OnClickListener rightListener) {
        ImageButton ibLeftBtn = (ImageButton) toolbar.findViewById(R.id.ibLeftBtn);
        TextView tvRightBtn = (TextView) toolbar.findViewById(R.id.tvRightBtn);
        UIHelper.setToolbarLeftBtn(ibLeftBtn, UIHelper.BACK_WHITE_CIRCLE_ARROW);
        UIHelper.setToolbarBgColor(toolbar, R.color.colorAccent);
        UIHelper.setToolbarButtonRightText(tvRightBtn, rightBtn);
        UIHelper.setToolbarButtonRightTextSize(tvRightBtn, 16);
        UIHelper.setToolbarTitleCenter(toolbar, centerTitle, R.dimen.tool_bar_text_title, R.color.white);
        UIHelper.setToolbarButtonRightTextColor(tvRightBtn, R.color.white);
        UIHelper.setToolbarRightButtonGroupClickListener(toolbar, rightListener);
    }
    
    /**
     * 左侧文本按钮, 中间标题, 右侧文字按钮
     * @param ctx
     * @param toolbar
     * @param centerTitle
     * @param rightBtn
     * @param rightListener
     */
    @Override
    public void simpleToolbarLeftTextAndCenterTitleAndeRightTextBtn(final Context ctx, Toolbar toolbar,  String leftBtn,  View.OnClickListener leftListener, String centerTitle, String rightBtn, View.OnClickListener rightListener) {
        if (toolbarBgResId > 0) {
            setToolbarBgImage(toolbar, toolbarBgResId);
        }else{
            setToolbarBgColor(toolbar, colorPrimaryId);
        }
        UIHelper.setToolbarTitleCenter(toolbar, centerTitle, R.dimen.text_title, R.color.white);
        
        ImageButton ibLeftBtn = (ImageButton) toolbar.findViewById(R.id.ibLeftBtn);
        ibLeftBtn.setVisibility(View.GONE);
        
        TextView tvLeftBtn = (TextView) toolbar.findViewById(R.id.tvTitleLeft);
        tvLeftBtn.setText(leftBtn);
        tvLeftBtn.setTextSize(15);
        tvLeftBtn.setTextColor(Color.WHITE);
        tvLeftBtn.setOnClickListener(leftListener);
        
        TextView tvRightBtn = (TextView) toolbar.findViewById(R.id.tvRightBtn);
        UIHelper.setToolbarButtonRightText(tvRightBtn, rightBtn);
        UIHelper.setToolbarButtonRightTextSize(tvRightBtn, 16);
        UIHelper.setToolbarButtonRightTextColor(tvRightBtn, R.color.white);
        UIHelper.setToolbarRightButtonGroupClickListener(toolbar, rightListener);
    }
    
}
