package com.pearl.view;

import android.content.Context;
import android.util.AttributeSet;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

public class GuideView extends Banner{
    public GuideView(Context context) {
        super(context);
        init();
    }
    
    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public GuideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    private void init() {
        setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        setIndicatorGravity(BannerConfig.CENTER);
        isAutoPlay(false);
    }
    
    @Override
    public void onPageScrollStateChanged(int state) {
    
    }
}
