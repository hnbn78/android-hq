package com.desheng.base.panel;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ab.module.MM;
import com.ab.thread.ThreadCollector;
import com.desheng.base.R;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.model.TabEntity;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 分析主界面
 */
public class ActAnalysisMain extends AbAdvanceActivity {
    
    public static void launch(Activity act, int lotteryId) {
        Intent itt = new Intent(act, ActAnalysisMain.class);
        itt.putExtra("lotteryId", lotteryId);
        act.startActivity(itt);
    }
    
    CommonTabLayout commonTabLayout;//底部tab控件
    
    String[] mTitles = new String[]{"历史开奖", "路珠分析", "冷热分析", "两面数据"};//底部每个菜单tab名称
    
    //未选中
    private int[] mIconUnselectIds = {R.mipmap.ic_mainnav_icon05_n, R.mipmap.ic_mainnav_icon06_n, R.mipmap.ic_mainnav_icon07_n, R.mipmap.ic_mainnav_icon08_n};
    //选中
    private int[] mIconSelectIds = {R.mipmap.ic_mainnav_icon05_p, R.mipmap.ic_mainnav_icon06_p, R.mipmap.ic_mainnav_icon07_p, R.mipmap.ic_mainnav_icon08_p};
    //启用
    private boolean[] arrEnable = {true, true, true, true};
    
    private FragTabAnalysisHistory fragTabHistory;
    private FragTabAnalysisLuZhu fragTabLuZhu;
    private FragTabAnalysisHotCold fragTabHotCold;
    private FragTabAnalysisSides fragTabSides;
    
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    
    private CompositeDisposable mCompositeDisposable;
    
    private ILotteryKind lotteryKind;
    private OnTitleRightBtnClickListener onTitleRightBtnClickListener;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_analysis_main;
    }
    
    @Override
    protected void init() {
        int lotteryId = getIntent().getIntExtra("lotteryId", 0);
        lotteryKind = CtxLottery.getIns().findLotteryKind(lotteryId);
    
        setToolbarLeftBtn(UIHelper.BACK_WHITE_ARROR);
        if (UIHelper.toolbarBgResId > 0) {
            setToolbarBgImage(UIHelper.toolbarBgResId);
        }else{
            setToolbarBgColor(UIHelper.colorPrimaryId);
        }
        setToolbarTitleCenter("查看详情");
        setToolbarTitleCenterColor(R.color.white);
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarButtonRightText("筛选");
        setToolbarButtonRightImageSize(21);
        setToolbarButtonRightTextColor(R.color.white);
        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTitleRightBtnClickListener.onRightBtnClick();
            }
        });
    
        mCompositeDisposable = new CompositeDisposable();
        initDatas();
        initTabView();
    
        //注册事件
        //EventBus.getDefault().register(this);
    }
    
    /**
     * 初始化数据
     */
    private void initDatas() {
        //"历史开奖"
        fragTabHistory =  FragTabAnalysisHistory.newIns(lotteryKind.getId());
        onTitleRightBtnClickListener = fragTabHistory;
        
        //路珠分析
        fragTabLuZhu = FragTabAnalysisLuZhu.newIns(lotteryKind.getId());
        
        //"冷热分析"
        fragTabHotCold = FragTabAnalysisHotCold.newIns(lotteryKind.getId());
        
        //"两面数据"
        if(lotteryKind.getOriginType() == CtxLottery.getIns().originLotteryType("T11S5")){
            arrEnable[3] = false;
        }else{
            fragTabSides = FragTabAnalysisSides.newIns(lotteryKind.getId());
        }
        
        
    }
    
    private void initTabView() {
        commonTabLayout = (CommonTabLayout) findViewById(R.id.tlBottom);
        
        ArrayList<Fragment> mFragments = new ArrayList<>();
        if(arrEnable[0]) {
            mFragments.add(fragTabHistory);
        }
        if(arrEnable[1]) {
            mFragments.add(fragTabLuZhu);
        }
        if(arrEnable[2]) {
            mFragments.add(fragTabHotCold);
        }
        if(arrEnable[3]) {
            mFragments.add(fragTabSides);
        }
        for (int i = 0; i < mTitles.length; i++) {
            if(arrEnable[i]){
                mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
            }
        }
        //以fragment的方式加载，不用viewpager
        commonTabLayout.setTabData(mTabEntities, this, R.id.flMain, mFragments);
        
        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                getToolbarRightButtonGroup().setVisibility(View.GONE);
                switch (position) {
                    case 0:
                        getToolbarRightButtonGroup().setVisibility(View.VISIBLE);
                        setToolbarButtonRightText("筛选");
                        break;
                    case 1:
                        
                        break;
                    case 2:
                        break;
                    case 3:
                        
                        break;
                    default:
                        break;
                }
            }
            
            @Override
            public void onTabReselect(int position) {
            }
        });
        commonTabLayout.setCurrentTab(0);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
     
    }
 
    @Override
    protected void onDestroy() {
        super.onDestroy();
      
        MM.http.cancellAllByTag(null);
        ThreadCollector.getIns().clear();
        mCompositeDisposable.clear();
        //EventBus.getDefault().unregister(this);
    }
    
    
    public interface OnTitleRightBtnClickListener {
        void onRightBtnClick();
    }
}


