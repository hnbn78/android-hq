package com.pearl.act.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.Views;
import com.githang.statusbar.StatusBarCompat;
import com.pearl.act.R;
import com.pearl.act.util.IOnKeyBoadListener;
import com.pearl.act.util.StatusBarHelper;
import com.pearl.act.util.UIHelper;

/**
 * Created by lee on 2017/12/29.
 */

public abstract class  AbAdvanceActivity<SV extends ViewDataBinding> extends AbBaseActivity implements View.OnClickListener, IOnKeyBoadListener {
    
    // 布局view
    protected SV mLayoutBinding;
    public SV B;
    public AbAdvanceActivity Act;
    protected ViewGroup mLayout;
    
    protected ViewDataBinding mBaseBinding;
    protected ViewGroup vgRoot;
    
    //状态栏假背景
    private View vFakeStatusBg;
    
    //标题栏
    private Toolbar toolbar;
    private TextView tvTitleLeft;
    private TextView tvTitleCenter;
    private TextView tvRightBtn;
    private ImageButton ibLeftBtn;
    private ImageButton ibRightBtn;
    private ViewGroup vgRightButtons;
    private ImageView iv_title_view;
    
    protected int baseLayoutId = R.layout.ab_activity_base_linear;
    
    private int naviHeight = 0;
    private int statusHeight = 0;
    private RelativeLayout vgToolbarContent;
    
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //StatusBarHelper.getStatusBarHeight(this);
        Act = this;
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        
        //初始化基础布局
        setBaseContentView();
    
        //初始化工具栏
        initToolbar();
        
        statusHeight = StatusBarHelper.getStatusBarHeight(this);
        if(StatusBarHelper.checkDeviceHasNavigationBar(this)){
            naviHeight = StatusBarHelper.getNavigationBarHeight(this);
        }
        
        //调用initView
        init();
    }
    
    //配置固定屏幕方向
    protected void setScreenOrientation() {
    
    }


    public ViewGroup getBaseLayout() {
        return (ViewGroup) mBaseBinding.getRoot();
    }
    
    public void setBaseContentView() {
        baseLayoutId = getBaseLayoutId();
        mBaseBinding = DataBindingUtil.setContentView(this, baseLayoutId);
    
        mLayoutBinding = DataBindingUtil.inflate(getLayoutInflater(), getLayoutId(), null, false);
        if (mLayoutBinding == null) {
            mLayout = (ViewGroup) LayoutInflater.from(this).inflate(getLayoutId(), null);
        }
        
        // content
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    
        if (mLayoutBinding != null) {
            mLayoutBinding.getRoot().setLayoutParams(params);
            mLayout = (ViewGroup) mLayoutBinding.getRoot();
            B = mLayoutBinding;
        } else {
            mLayout.setLayoutParams(params);
        }
        
        vFakeStatusBg = mBaseBinding.getRoot().findViewById(R.id.vFakeStatusBg);
        vgRoot = (ViewGroup) mBaseBinding.getRoot().findViewById(R.id.vgRoot);
        FrameLayout mContainer = (FrameLayout) mBaseBinding.getRoot().findViewById(R.id.flContainer);
        mContainer.addView(mLayout);
        StatusBarHelper.SoftHideKeyBoardUtil.assistActivity(this);
    }
    
  
    public SV getBinding() {
        return mLayoutBinding;
    }
    
    public ViewGroup getLayout() {
        return mLayout;
    }

    public ViewGroup getVgRoot() {
        return vgRoot;
    }
    
    /**
     * 设置titlebar
     */
    protected void initToolbar() {
        toolbar = (Toolbar) mBaseBinding.getRoot().findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        
        vgToolbarContent = (RelativeLayout) toolbar.findViewById(R.id.vgToolbarContent);
        ibLeftBtn = (ImageButton) toolbar.findViewById(R.id.ibLeftBtn);
        ibLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleLeftClick(v);
            }
        });
        tvTitleLeft = (TextView) toolbar.findViewById(R.id.tvTitleLeft);
        tvTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleLeftClick(v);
            }
        });
        
        ibRightBtn = (ImageButton) toolbar.findViewById(R.id.ibRightBtn);
        ibRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleRightClick(v);
            }
        });
       
        tvTitleCenter = (TextView) toolbar.findViewById(R.id.tvTitleCenter);
        iv_title_view = toolbar.findViewById(R.id.iv_title_view);
        vgRightButtons = (ViewGroup) toolbar.findViewById(R.id.vgRightButton);
        tvRightBtn = (TextView) toolbar.findViewById(R.id.tvRightBtn);
        tvRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleRightClick(v);
            }
        });
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }
    
    /**
     * 设置状态栏沉浸式, 同时增加自带标题栏上间距
     */
    public void setStatusBarTranslucentAndLightContentWithPadding(){
        //  1/2 初步设置状态栏
        StatusBarHelper.setStatusBar(this, false, false);
        StatusBarHelper.setStatusTextColor(false, this);
        vFakeStatusBg.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.toolbar_height) + statusHeight;
        toolbar.setLayoutParams(layoutParams);
        toolbar.setPadding(0, statusHeight, 0, 0);
    }
    
    /**
     * 引藏标题栏, 使用新原布局状态栏, 同时增加增加自带标题栏上间距
     */
    public void replaceToolbarWithTranslucentPadding(View barLayout) {
        hideToolbar();
        vFakeStatusBg.setVisibility(View.INVISIBLE);
        StatusBarHelper.setStatusBar(this, false, false);
        StatusBarHelper.setStatusTextColor(false, this);
        barLayout.setPadding(0, statusHeight, 0, 0);
        ViewGroup.LayoutParams layoutParams = barLayout.getLayoutParams();
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.toolbar_height) + statusHeight;
        barLayout.setLayoutParams(layoutParams);
    }
    
    /**
     * 引藏标题栏, 使用新原布局状态栏, 同时增加增加自带标题栏上间距
     */
    public void replaceToolbarWithShadowPadding(View barLayout) {
        hideToolbar();
        StatusBarHelper.setStatusBar(this, false, false);
        vFakeStatusBg.setVisibility(View.VISIBLE);
        Views.setHeight(vFakeStatusBg, statusHeight);
        StatusBarHelper.setStatusTextColor(false, this);
        barLayout.setPadding(0, statusHeight, 0, 0);
        ViewGroup.LayoutParams layoutParams = barLayout.getLayoutParams();
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.toolbar_height) + statusHeight;
        barLayout.setLayoutParams(layoutParams);
    }
    
    /**
     * 设置状态栏半透明, 内容夜色可选,
     */
    public void setStatusBarBackgroundAndContentWithPadding(int bgColor, float alpha, boolean isDarkContent){
        StatusBarHelper.setStatusBar(this, false, isDarkContent);
        Views.setHeight(vFakeStatusBg, statusHeight);
        vFakeStatusBg.setVisibility(View.VISIBLE);
        vFakeStatusBg.setBackgroundColor(bgColor);
        StatusBarHelper.setStatusTextColor(isDarkContent, this);
        ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.toolbar_height) + statusHeight;
        toolbar.setLayoutParams(layoutParams);
        toolbar.setPadding(0, statusHeight, 0, 0);
    }
    
    /**
     * 设置状态栏半透明, 内容为白色,
     */
    public void setStatusBarShadowWithPadding(){
        StatusBarHelper.setStatusBar(this, false, false);
        vFakeStatusBg.setVisibility(View.VISIBLE);
        Views.setHeight(vFakeStatusBg, statusHeight);
        StatusBarHelper.setStatusTextColor(false, this);
        ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.toolbar_height) + statusHeight;
        toolbar.setLayoutParams(layoutParams);
        toolbar.setPadding(0, statusHeight, 0, 0);
    }
    
    /**
     * 设置状态栏半透明, 内容为白色,
     */
    public void setStatusBarShadow(){
        StatusBarHelper.setStatusBar(this, false, false);
        vFakeStatusBg.setVisibility(View.VISIBLE);
        Views.setHeight(vFakeStatusBg, statusHeight);
        StatusBarHelper.setStatusTextColor(false, this);
    }
    
    
    /**
     * 设置状态栏透明, 内容为白色, 使用开源StatusBarCompat, 同时增加增加自带标题栏上间距
     */
    public void setStatusBarTranslucentCompatWithPadding(){
        StatusBarCompat.setTranslucent(getWindow(), true);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.transparent), true);
        ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.toolbar_height) + statusHeight;
        toolbar.setLayoutParams(layoutParams);
        toolbar.setPadding(0, statusHeight, 0, 0);
    }
    
    /**
     * 设置状态栏透明, 内容为黑, 使用开源StatusBarCompat, 同时增加增加自带标题栏上间距
     */
    public void setStatusBarTranslucentAndDarkContent(){
        vFakeStatusBg.setVisibility(View.INVISIBLE);
        StatusBarHelper.setStatusBar(this, false, false);
        StatusBarHelper.setStatusTextColor(true, this);
    }
    
    /**
     * 设置状态栏透明, 内容为黑, 使用开源StatusBarCompat, 同时增加增加自带标题栏上间距
     */
    public void setStatusBarTranslucentAndDarkContentWithPadding(){
        vFakeStatusBg.setVisibility(View.INVISIBLE);
        StatusBarHelper.setStatusBar(this, false, false);
        StatusBarHelper.setStatusTextColor(true, this);
        ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.toolbar_height) + statusHeight;
        toolbar.setLayoutParams(layoutParams);
        toolbar.setPadding(0, statusHeight, 0, 0);
    }
    
    
    public void setStatusBarDarkCompat(){
        StatusBarCompat.setTranslucent(getWindow(), false);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.dark_translucent), true);
    }
    
    
    public void setStatusBarTranslucentAndLightContent(){
        vFakeStatusBg.setVisibility(View.INVISIBLE);
        StatusBarHelper.setStatusBar(this, false, false);
        StatusBarHelper.setStatusTextColor(false, this);
    }
    
    public void setStatusBarTextLightCompat(){
        StatusBarCompat.setTranslucent(getWindow(), false);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.light_translucent), true);
    }
    
    public Toolbar getToolbar(){
        return  toolbar;
    }
    
    
    /**
     * 设置左侧按钮
     */
    public void setToolbarLeftButtonImg(int imgResId, int dbWidth, int dbHeight, View.OnClickListener listener){
        UIHelper.getIns().setToolbarLeftButtonImg(toolbar, imgResId,  dbWidth,  dbHeight, listener);
    }
    
    /**
     * 设置右侧按钮
     */
    public  void setToolbarRightButtonImg(int imgResId, int dbWidth, int dbHeight, View.OnClickListener listener){
        UIHelper.getIns().setToolbarRightButtonImg(toolbar,  imgResId,  dbWidth,  dbHeight, listener);
    }
    
    /**
     * 设置标题栏右侧文字
     * @param text
     */
    public void setToolbarButtonRightText(CharSequence text) {
        UIHelper.getIns().setToolbarButtonRightText(tvRightBtn, text);
    }
    
  
    
    public void setToolbarTitleLeft(CharSequence text) {
        if (tvTitleLeft != null) {
            tvTitleLeft.setText(text);
        }
    }
    
    public void setToolbarTitleCenter(CharSequence text) {
        if (toolbar != null && tvTitleCenter != null) {
            //取消状态栏
            toolbar.setTitle("");
            tvTitleCenter.setText(text);
            iv_title_view.setVisibility(View.GONE);
        }
    }

    public void showImageViewTitle(){
        if (toolbar != null && tvTitleCenter != null) {
            //取消状态栏
            toolbar.setTitle("");
            tvTitleCenter.setVisibility(View.GONE);
            iv_title_view.setVisibility(View.VISIBLE);
        }
    }

    public void setToolbarTitleCenterColor(int resId) {
        if (toolbar != null && tvTitleCenter != null) {
            //取消状态栏
            tvTitleCenter.setTextColor(Views.fromColors(resId));
        }
    }
    
    /**
     * 设置背景图片
     */
    public void setToolbarBgImage(int drawableResId){
        if (toolbar != null) {
            UIHelper.setToolbarBgImage(toolbar, drawableResId);
        }
    }
    

    
    /**
     * 设置背景颜色
     */
    
    public void setToolbarBgColor(int colorResId){
        if (toolbar != null) {
            UIHelper.setToolbarBgColor(toolbar, colorResId);
        }
    }
    
  
    /**
     * 设置背景颜色
     */
    
    public void setToolbarBgDrawable(Drawable drawable){
        if (toolbar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                toolbar.setBackground(drawable);
            }
        }
    }
    
    /**
     * 设置返回图标
     */
    public void setToolbarLeftBtn(int type){
        UIHelper.setToolbarLeftBtn(ibLeftBtn, type);
    }
    

    public void setToolbarBackBtnSize(int dpValue){
        Views.setWidthAndHeight(ibLeftBtn, Views.dp2px(dpValue), Views.dp2px(dpValue));
    }
    
    public void hideBackBtn() {
        ibLeftBtn.setVisibility(View.GONE);
    }
    
    /**
     * 引藏标题栏
     */
    public void hideToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
        }
    }

    public void showToolbar(){
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
        }
    }
    
    public void hideToolBarWithPadding(){
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
        }
        mLayout.setPadding(0, statusHeight, 0, 0);
    }
    
    public void hideToolBarWithViewPadding(View view){
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = layoutParams.height + statusHeight;
        view.setLayoutParams(layoutParams);
        view.setPadding(0, statusHeight, 0, 0);
    }
    
    public void hideBack(SV bindingView) {
        this.mLayoutBinding = bindingView;
        this.B = bindingView;
    }
    
    public void paddingHeadOfStatusHeight(View view){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = layoutParams.height + statusHeight;
        view.setLayoutParams(layoutParams);
        view.setPadding(0, statusHeight, 0, 0);
    }
    
    
    public void paddingHeadOfAllHeight(View view){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = layoutParams.height + Views.dp2px(48) + statusHeight;
        view.setLayoutParams(layoutParams);
        view.setPadding(0, Views.dp2px(48) + statusHeight, 0, 0);
    }
    
    /**
     * 标题栏右上角图标设置
     * @param iconRes
     */
    public void setToolbarButtonRightTextBg(int iconRes) {
        tvRightBtn.setVisibility(View.VISIBLE);
        tvRightBtn.setBackgroundDrawable(getResources().getDrawable(iconRes));
    }
    
    /**
     * 标题栏右颜色
     */
    public void setToolbarButtonRightTextColor(int colorResId) {
        UIHelper.setToolbarButtonRightTextColor(tvRightBtn, colorResId);
    }
    
   
    
    /**
     * 标题栏右上角文字
     */
    public void setToolbarButtonRightText(int resId) {
        tvRightBtn.setVisibility(View.VISIBLE);
        tvRightBtn.setText(resId);
    }
    
    /**
     * 标题栏右上角文字
     */
    public void setToolbarButtonRightText(String str) {
        tvRightBtn.setVisibility(View.VISIBLE);
        tvRightBtn.setText(str);
    }
    
    
    /**
     * 设置标题栏右上角图标设置
     */
    public void setToolbarButtonRightImage(int resId) {
        if (ibRightBtn != null) {
            ibRightBtn.setVisibility(View.VISIBLE);
            ibRightBtn.setBackgroundDrawable(getResources().getDrawable(resId));
        }
    }
    
    /**
     * 设置标题栏右上角图标设置
     */
    public void setToolbarRightButtonGroupClickListener(View.OnClickListener onClickListener) {
        UIHelper.getIns().setToolbarRightButtonGroupClickListener(getToolbar(), onClickListener);
    }
    

   
    
    /**
     * 获取标题栏右上角图标
     */
    public ImageButton getToolbarRightImageButton() {
        return ibRightBtn;
    }
    
    /**
     * 获取标题栏右上角图标
     */
    public TextView getToolbarRightTextButton() {
        return tvRightBtn;
    }
    
    /**
     * 获取标题栏右上角图标
     */
    public ImageButton getToolbarLeftImageButton() {
        return ibLeftBtn;
    }
    
    
    /**
     * 获取标题栏右上角图标
     */
    public ViewGroup getToolbarRightButtonGroup() {
        return vgRightButtons;
    }
    
    public void setToolbarButtonRightImageSize(int dpValue){
        Views.setWidthAndHeight(ibRightBtn, Views.dp2px(dpValue), Views.dp2px(dpValue));
    }
    
    
    public void setToolbarButtonRightTextSize(int sp) {
        UIHelper.getIns().setToolbarButtonRightTextSize(tvRightBtn, sp);
    }
    
 
    /**
     * 点击右侧按钮
     * @param view
     */
    public void titleRightClick(View view) {
        onRightButtonClick();
    }
    
    public void onRightButtonClick(){
    
    }
    
    public void titleLeftClick(View view) {
        onLeftButtonClick();
    }
    
    public void onLeftButtonClick(){
        finish();
    }
    
    @Override
    public void onKeyBoadShow(){
    
    }
    
    @Override
    public void onKeyBoadHide(){
    
    }
    
    public int getNaviHeight() {
        return naviHeight;
    }
    
    public int getStatusHeight() {
        return statusHeight;
    }

//    导致布局inflate过慢
//    @Override
//    public Resources getResources() {
//        Resources res = super.getResources();
//        Configuration config = new Configuration();
//        config.setToDefaults();
//        res.updateConfiguration(config, res.getDisplayMetrics());
//        return res;
//    }

    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    

    protected abstract int getLayoutId();

    protected abstract void init();


    protected int getBaseLayoutId() {
        return R.layout.ab_activity_base_linear;
    }
    
    @Override
    public void onClick(View v) {
    
    }
    
    public boolean isFinishingOrDestroyed() {
        return isFinishing() || getSupportFragmentManager().isDestroyed();
    }
    
    
}
