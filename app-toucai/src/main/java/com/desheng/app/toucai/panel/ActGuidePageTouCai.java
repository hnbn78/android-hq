package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.view.GlideImageLoaderNoRound;
import com.desheng.app.toucai.view.GlideImageLoaderNoRoundLocal;
import com.desheng.base.manager.UserManager;
import com.pearl.act.base.AbBaseActivity;
import com.shark.tc.R;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActGuidePageTouCai extends AbBaseActivity {

    private static final String KEY_GUIDEPICS = "guidePics";
    private Integer[] imageIds;
    private List<String> guidePics;
    private boolean needLoadWebPics = false;
    private Banner vBanner;
    private Button btn_Taste;

    public static void launchAndFinish(Activity act) {
        Intent itt = new Intent(act, ActGuidePageTouCai.class);
        act.startActivity(itt);
        act.finish();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.act_guide_page);
        vBanner = ((Banner) findViewById(R.id.vBanner));
        btn_Taste = ((Button) findViewById(R.id.btn_Taste));

        //获取数据
        guidePics = UserManagerTouCai.getIns().getGuideOnlineBg();

        //设置banner样式
        vBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);

        //设置自动轮播，默认为true
        vBanner.isAutoPlay(false);
        //设置指示器位置（当banner模式中有指示器时）
        vBanner.setIndicatorGravity(BannerConfig.CENTER);

        int skinSetting = UserManager.getIns().getSkinSetting();

        if (guidePics == null || guidePics.size() == 0) {
            //如果启动页未加载好引导页数据，那么再在当前页请求一次
            needLoadWebPics = false;
            imageIds = CommonConsts.setActGuideBg(skinSetting);
            //设置图片加载器
            vBanner.setImageLoader(new GlideImageLoaderNoRoundLocal());

            vBanner.setImages(Arrays.asList(imageIds));
            vBanner.start();

            vBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == imageIds.length - 1) {
                        btn_Taste.setVisibility(View.VISIBLE);
                    } else {
                        btn_Taste.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } else {
            needLoadWebPics = true;
            //设置图片加载器
            vBanner.setImageLoader(new GlideImageLoaderNoRound());
            vBanner.setImages(guidePics);
            vBanner.start();

            vBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == guidePics.size() - 1) {
                        btn_Taste.setVisibility(View.VISIBLE);
                    } else {
                        btn_Taste.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        btn_Taste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager.getIns().setHideGuide(true);
                gotoMain();
            }
        });

    }

    //跳转主页
    public void gotoMain() {
        ActMain.mainKillSwitch = false;
        ActMain.launch(this,true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageIds != null) {
            imageIds = null;
        }
    }

}
