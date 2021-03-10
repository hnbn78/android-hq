package com.desheng.app.toucai.panel;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.callback.AbCallback;
import com.ab.debug.AbDebug;
import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
import com.ab.thread.ThreadCollector;
import com.ab.thread.ValueRunnable;
import com.ab.util.AbDateUtil;
import com.ab.util.ArraysAndLists;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.view.MaterialDialog;
import com.bumptech.glide.Glide;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.event.MainTabEvent;
import com.desheng.app.toucai.function.RedPacketMode;
import com.desheng.app.toucai.global.App;
import com.desheng.app.toucai.global.AppUmengPushHandler;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.AdvManageConfigInfo;
import com.desheng.app.toucai.model.AvailableRedPacketInfo;
import com.desheng.app.toucai.model.ContactsMode;
import com.desheng.app.toucai.model.NewUserMission;
import com.desheng.app.toucai.model.RedPacketDetail;
import com.desheng.app.toucai.model.RedPacketMission;
import com.desheng.app.toucai.model.eventmode.MissionindexMode;
import com.desheng.app.toucai.service.SocketIOKeppService;
import com.desheng.app.toucai.util.DateTool;
import com.desheng.app.toucai.util.DialogsNewMission;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.controller.ControllerBase;
import com.desheng.base.controller.FloatActionController;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.TabEntity;
import com.desheng.base.panel.ActBroadList;
import com.desheng.base.panel.ActSetting;
import com.desheng.base.panel.ActWeb;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.base.AbBaseActivity;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.act.base.AbSlidingFragmentActivity;
import com.pearl.act.util.StatusBarHelper;
import com.pearl.act.util.UIHelper;
import com.pearl.push.IPushUmeng;
import com.pearl.slidingmenu.BarUtils;
import com.pearl.slidingmenu.lib.app.SlidingFragmentActivity;
import com.pearl.view.ComplexTabLayout;
import com.shark.tc.R;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.socket.client.Socket;

/**
 * 主界面
 */
public class ActMain extends AbSlidingFragmentActivity {
    public static final String[] arrRefresh = {UserManagerTouCai.EVENT_USER_LOGINED,
            UserManagerTouCai.EVENT_USER_LOGOUTED};
    public static final double BANNER_RADIO = 290.0 / 710;

    //private SlidingMenu slidingMenu;
    public static boolean mainKillSwitch = true;
    public static boolean mainVisible = true;
    public static int navigatePage = -1;
    public AbBaseFragment[] arrFrags = new AbBaseFragment[5];
    public RelativeLayout mRlAdvRightPop;
    public ImageView mIvRightAdvPic;
    public ImageView mBtnRightAdvPopCancel;
    public RelativeLayout mRlAdvLeftPop;
    public ImageView mIvLeftAdvPic;
    public ImageView mBtnLeftAdvPopCancel;
    List<AdvManageConfigInfo> mAdvConfigList;
    private HashMap<String, ControllerBase> mapContrllers = new HashMap<String, ControllerBase>();
    private LinearLayout vgLoginTool;
    private ImageView ivContactBtn;
    private ImageView ivChargeRecode;
    private View vgPerson;
    private ImageView ivMessage, ivSet;
    private ComplexTabLayout tabLayout;//底部tab控件
    private String[] mTitles = new String[]{"首页", "开奖", "充值", "活动", "我的"};//底部每个菜单tab名称
    //未选中
    private int[] mIconUnselectIds;
    //选中
    private int[] mIconSelectIds;
    //private FragTabDepositOld fragTabDepositOld;
    //private FragTabDepositIndexNew fragTabDepositIndex;
    // private FragTabWithdrawTouCai fragTabWithdraw;
    //private FragTabOpen fragTabOpen;
    //private FragTabHome fragTabHome;
    //    private FragTabOpen fragTabOpen;
    //private FragTabActivity fragTabActivity;
    //private FragTabPersonNew fragTabPerson;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private CompositeDisposable mCompositeDisposable;
    //private FragSlidingMenu fragSlidingMenu;
    private Toolbar toolbar;
    private View vgToolbarGroup;
    private ImageView ivLogo;
    private TextView tvMsgCount;
    private UserManagerTouCai.EventReceiver receiver;
    private MaterialDialog materialDialog;
    private LinearLayout vgSlideTop;
    private boolean haveShownMissioon = false;
    private int mSkinSetting;
    private Dialog mAdvDialog;
    //记录用户首次点击返回键的时间
    private long firstTime = 0;
    private FrameLayout framRedPacket;
    private RedPacketMode mRedPacketMode;
    private TextView msgNotRead;
    Intent mSocketIOKeppIntend;

    public static void launch(Context act, int toTabIndex) {
        Intent intent = new Intent(act, ActMain.class);
        intent.putExtra("toTabIndex", toTabIndex);
        act.startActivity(intent);
    }

    public static void launch(Context act) {
        Intent intent = new Intent(act, ActMain.class);
        act.startActivity(intent);
    }

    public static void launch(Context act, boolean finish) {
        Intent intent = new Intent(act, ActMain.class);
        act.startActivity(intent);
        if (finish) {
            ((ActGuidePageTouCai) act).finish();
        }
    }

    public static void launch(Context act, String extras) {
        Intent intent = new Intent(act, ActMain.class);
        intent.putExtra("extras", extras);
        act.startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ((App) getApplication()).actmainCreate = true;
        int toTabIndex = getIntent().getIntExtra("toTabIndex", 0);
        String extrasRun = getIntent().getStringExtra("extras");

        //mSocketIOKeppIntend = new Intent(this, SocketIOKeppService.class);
        //bindService(mSocketIOKeppIntend, serviceConnection, BIND_AUTO_CREATE);

        //皮肤配置
        mSkinSetting = UserManager.getIns().getSkinSetting();
        mIconUnselectIds = CommonConsts.setMainButtomNavSkinUnselect(mSkinSetting);
        mIconSelectIds = CommonConsts.setMainButtomNavSkinSelect(mSkinSetting);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        UIHelper.getIns().simpleToolbar(toolbar, "");

        //右悬浮广告
        mRlAdvRightPop = ((RelativeLayout) findViewById(R.id.RlAdvRightPop));
        mIvRightAdvPic = ((ImageView) findViewById(R.id.IvRightAdvPic));
        mBtnRightAdvPopCancel = ((ImageView) findViewById(R.id.BtnRightAdvPopCancel));
        //左悬浮广告
        mRlAdvLeftPop = ((RelativeLayout) findViewById(R.id.RlAdvLeftPop));
        mIvLeftAdvPic = ((ImageView) findViewById(R.id.IvLeftAdvPic));
        mBtnLeftAdvPopCancel = ((ImageView) findViewById(R.id.BtnLeftAdvPopCancel));

        //未读消息（tablayout自带的红点消息view无法显示两位数，不好修改）
        msgNotRead = ((TextView) findViewById(R.id.msgNotRead));

        //首页
        vgSlideTop = (LinearLayout) findViewById(R.id.vgSlideTop);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        vgLoginTool = (LinearLayout) findViewById(R.id.vgLoginTool);
        ivContactBtn = (ImageView) findViewById(R.id.ivContactBtn);
        ivChargeRecode = (ImageView) findViewById(R.id.ivChargeRecode);
        framRedPacket = ((FrameLayout) findViewById(R.id.framRedPacket));
        ivContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActWeb.launchCustomService(ActMain.this);
            }
        });
        ivChargeRecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActUserMsgList.launchForStage1(ActMain.this);
                ActHomeNotice.launch(ActMain.this);
            }
        });

        //我的
        vgPerson = findViewById(R.id.vgPerson);
        ivSet = (ImageView) findViewById(R.id.ivSet);
        ivMessage = (ImageView) findViewById(R.id.ivMessage);
        tvMsgCount = (TextView) findViewById(R.id.tvMsgCount);

        mCompositeDisposable = new CompositeDisposable();

        initTabs();
        //set the Behind View
        setSlidiingMenu();

        checkRedPacketMission(0);

        //initAdvManagment(Consitances.AdvertisementSet.TABHOME, 0);
        //底部菜单
        //initTabView(0);

        if (mainKillSwitch) {
            AbDebug.error(AbDebug.TAG_APP, "main 非正常流程启动, 退出中...");
            finish();
            return;
        }

        setStatus();

        //注册事件
        EventBus.getDefault().register(this);

        //用户事件监听
        receiver = new UserManagerTouCai.EventReceiver().register(this, new UserManagerTouCai.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (ArraysAndLists.findIndexWithEqualsOfArray(eventName, arrRefresh) != -1) {
                    if (!ActMain.this.isFinishing()) {
                        if (UserManagerTouCai.EVENT_USER_LOGOUTED.equals(eventName)) {
                            haveShownMissioon = false;
                        }
                        refresh(eventName, bundle);
                    }
                }
            }
        });

        AppUmengPushHandler.CustomMsg jo = (AppUmengPushHandler.CustomMsg) getIntent().getSerializableExtra("pushCustomMsg");
        if (jo != null) {
            ((UserManagerTouCai) UserManager.getIns()).setLatestPushMsg(jo);
            AbDebug.log(AbDebug.TAG_PUSH, "设置推送" + jo.toString());
        }

        //处理导航栏皮肤()
        tabLayout.setIconHeight(CommonConsts.setMainButtomNavIconSize(mSkinSetting));
        tabLayout.setIconWidth(CommonConsts.setMainButtomNavIconSize(mSkinSetting));
        tabLayout.setTextsize(CommonConsts.setMainButtomNavTextSize(mSkinSetting));

        checkNewMission();

        if (Strs.isNotEmpty(extrasRun)) {
            if (UserManagerTouCai.getIns().isLogined()) {
                ActWebX5.launch(this, extrasRun, true);
            } else {
                ActLoginPasswordTouCai.launch(this, extrasRun);
            }
        }

        if (toTabIndex == 3) {
            tabLayout.setCurrentTab(3);
            updatePositionUI(3);
        }
    }

    /**
     * 判断版本Ac
     */
    public void setStatus() {
        StatusBarHelper.setStatusBar(this, false, false);
        StatusBarHelper.setStatusTextColor(false, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 改变titlebar的高度
            int statusbarHeight = BarUtils.getStatusBarHeight(this);
            vgToolbarGroup = findViewById(R.id.vgToolbarGroup);
            vgToolbarGroup.setBackgroundResource(R.drawable.toolbar_bg);
            ViewGroup.LayoutParams layoutParams1 = (ViewGroup.LayoutParams) vgToolbarGroup.getLayoutParams();
            layoutParams1.height = layoutParams1.height + statusbarHeight;
            vgToolbarGroup.setPadding(0, statusbarHeight, 0, 0);
            vgToolbarGroup.setLayoutParams(layoutParams1);
        }
    }

    /**
     * 初始化侧滑菜单布局
     */
    public void setSlidiingMenu() {
        setBehindContentView(R.layout.menu_frame);
        getSlidingMenu().setSlidingEnabled(false);
        /*FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        fragSlidingMenu = new FragSlidingMenu();
        t.replace(R.id.menu_frame, fragSlidingMenu);
        t.commit();
        slidingMenu = getSlidingMenu();
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.15f);
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);*/
    }

    public ArrayList<Fragment> mFragments;

    /**
     * 初始化数据
     */
    private void initTabs() {
        mFragments = new ArrayList<>();
        mFragments.add(new FragTabHome());
        mFragments.add(new FragTabOpen());
        mFragments.add(new FragTabDepositIndexNew());
        mFragments.add(new FragTabActivity());
        mFragments.add(new FragTabPersonNew());

        switchFragmentPosition(0);
        updatePositionUI(0);

        tabLayout = (ComplexTabLayout) findViewById(R.id.tlBottom);

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        //以fragment的方式加载，不用viewpager
        tabLayout.setTabData(mTabEntities);

        tabLayout.setOnTabSelectListener(new ComplexTabLayout.OnComplexTabSelectListener() {
            @Override
            public boolean preTabSelect(int position) {
                if (ArraysAndLists.containsWithEqualsOfArray(position, new Integer[]{1, 2, 4}) && UserManager.getIns().isNotLogined()) {
                    UserManager.getIns().redirectToLogin(ActMain.this);
                    return false; //截获点击切换
                } else {
                    return false;
                }
            }

            @Override
            public void onTabSelect(int position) {
                updatePositionUI(position);
                switchFragmentPosition(position);
                if (position == 0) {
                    ((FragTabHome) mFragments.get(0)).updateLoginBtn();
                }

                if (position == 2) {
//                    ((FragTabDepositIndexNew) mFragments.get(2)).getAllPaymentCategory();
//                    ((FragTabDepositIndexNew) mFragments.get(2)).getAllThirdPayment();
                    ((FragTabDepositIndexNew) mFragments.get(2)).checkBindStatus();
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        tabLayout.setCurrentTab(0);
    }

    private int mPrevIndex = 0;

    private void switchFragmentPosition(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = mFragments.get(position);
        Fragment prevFragment = mFragments.get(mPrevIndex);
        mPrevIndex = position;
        ft.hide(prevFragment);
        if (!currentFragment.isAdded()) {
            ft.add(R.id.flMain, currentFragment);
        }
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();
    }

    private void initTabView(int index) {
        tabLayout = (ComplexTabLayout) findViewById(R.id.tlBottom);

        ArrayList<Fragment> mFragments = new ArrayList<Fragment>(Arrays.asList(arrFrags));
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        //以fragment的方式加载，不用viewpager
        tabLayout.setTabData(mTabEntities, this, R.id.flMain, mFragments);

        tabLayout.setOnTabSelectListener(new ComplexTabLayout.OnComplexTabSelectListener() {
            @Override
            public boolean preTabSelect(int position) {
                if (ArraysAndLists.containsWithEqualsOfArray(position, new Integer[]{1, 2, 4}) && UserManager.getIns().isNotLogined()) {
                    UserManager.getIns().redirectToLogin(ActMain.this);
                    return true; //截获点击切换
                } else {
                    return false;
                }
            }

            @Override
            public void onTabSelect(int position) {
                updatePositionUI(position);
//                //刷新内部
                for (int i = 0; i < arrFrags.length; i++) {
                    if (arrFrags[i] instanceof IRefreshableFragment && position == i) {
                        ((IRefreshableFragment) arrFrags[i]).refresh("", null);
                        Log.e("ActMain", "onTabSelect---刷新内部--  i:" + i);
                    }

                }
//
                if (position == 2) {
                    // fragTabDepositIndex.getAllPaymentCategory();
                    //fragTabDepositIndex.getAllThirdPayment();
                }

                if (position == 4) {
                    //fragTabPerson.getQuerySalaryBonus();
                }

                if (position == 0) {
                    if (UserManager.getIns().isLogined()) {
//                        tabLayout.setEnabled(false);
//                        fragTabHome.setupGuidePage(() -> {
                        if (!haveRedpacketMission) {
                            checkNewMission();
                        }
//                            tabLayout.setEnabled(true);
//                        });
//                        haveShownMissioon = true;
                    }
                    //fragTabHome.updateLoginBtn();
                }
//                mRlAdvRightPop.setVisibility(View.GONE);
//                mRlAdvLeftPop.setVisibility(View.GONE);
//                //更新广告view信息
//                switch (position) {
//                    case 0:
//                        initAdvManagment(Consitances.AdvertisementSet.TABHOME, position);
//                        break;
//                    case 1:
//                        initAdvManagment(Consitances.AdvertisementSet.TABLOBBY, position);
//                        break;
//                    case 2:
//                        initAdvManagment(Consitances.AdvertisementSet.TABDEPOSIT, position);
//                        break;
//                    case 3:
//                        initAdvManagment(Consitances.AdvertisementSet.TABACTIVITY, position);
//                        fragTabActivity.getActivityData();
//                        break;
//                    case 4:
//                        initAdvManagment(Consitances.AdvertisementSet.TABPERSONNEW, position);
//                        break;
//                }
//
//                checkRedPacketMission(position);

            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        //getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        tabLayout.setCurrentTab(index);
        updatePositionUI(index);
        //fragSlidingMenu.refresh();
    }

    /**
     * 首页广告管理配置初始化
     */
    private void initAdvManagment(int advType, int position) {
//        HttpActionTouCai.getObtainAdvsManagment(FragTabHome.class, advType, new AbHttpResult() {
//            @Override
//            public void setupEntity(AbHttpRespEntity entity) {
//                entity.putField("data", new TypeToken<ArrayList<AdvManageConfigInfo>>() {
//                }.getType());
//            }
//
//            @Override
//            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
//                if (code == 0 && error == 0) {
//                    mAdvConfigList = getField(extra, "data", null);
//                    if (mAdvConfigList != null && mAdvConfigList.size() > 0) {
//                        updateAdvView(mAdvConfigList, advType, position);
//                    }
//                }
//                return true;
//            }
//        });
    }

    /**
     * 根据最新的广告配置进行更新广告模块信息
     */
    private void updateAdvView(List<AdvManageConfigInfo> advConfigList, int index, int position) {

        for (AdvManageConfigInfo configInfo : advConfigList) {
            switch (configInfo.getAdvertisementType()) {
                case 1:
                    boolean advflag = ((App) getApplication()).advPopShownFlags[position][0];
                    if (advflag) {
                        mRlAdvLeftPop.setVisibility(UserManagerTouCai.getIns().isLogined() ? View.VISIBLE : View.GONE);
                        Glide.with(this).load(ENV.curr.host + configInfo.getAppImgUrl()).into(mIvLeftAdvPic);
                        mBtnLeftAdvPopCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((App) getApplication()).advPopShownFlags[position][0] = false;
                                mRlAdvLeftPop.setVisibility(View.GONE);
                            }
                        });
                        mRlAdvLeftPop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {//点击图片 链接跳转
                                if (configInfo.getLinkType() == 1) {//跳转外部链接
                                    ActWebX5.launch(ActMain.this, configInfo.getLinkUrl(), true);

                                } else if (configInfo.getLinkType() == 2) {//跳转app内页--活动列表页
                                    //并且展开活动页对应的条目
                                    getTabLayout().setCurrentTab(3);
                                    EventBus.getDefault().post(new MissionindexMode(configInfo.getLinkUrl()));
                                } else if (configInfo.getLinkType() == 3) {//内部跳转，超级合伙人
                                    String linkUrl = configInfo.getLinkUrl();
                                    String[] split = linkUrl.split("#");
                                    if (split != null && split.length == 2 && Strs.isEqual("superPartner", split[0])) {
                                        ActShareAndSuperPartner.launch(ActMain.this, Integer.parseInt(split[1]));
                                    }
                                }
                            }
                        });
                    }
                    break;
                case 2:
                    boolean advflag2 = ((App) getApplication()).advPopShownFlags[position][1];
                    if (advflag2) {
                        mRlAdvRightPop.setVisibility(UserManagerTouCai.getIns().isLogined() ? View.VISIBLE : View.GONE);
                        Glide.with(this).load(ENV.curr.host + configInfo.getAppImgUrl()).into(mIvRightAdvPic);
                        mBtnRightAdvPopCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((App) getApplication()).advPopShownFlags[position][1] = false;
                                mRlAdvRightPop.setVisibility(View.GONE);
                            }
                        });
                        mRlAdvRightPop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {//点击图片 链接跳转

                                if (configInfo.getLinkType() == 1) {//跳转外部链接
                                    ActWebX5.launch(ActMain.this, configInfo.getLinkUrl(), true);

                                } else if (configInfo.getLinkType() == 2) {//跳转app内页--活动列表页
                                    //并且展开活动页对应的条目
                                    getTabLayout().setCurrentTab(3);
                                    EventBus.getDefault().post(new MissionindexMode(configInfo.getLinkUrl()));
                                } else if (configInfo.getLinkType() == 3) {//内部跳转，超级合伙人
                                    String linkUrl = configInfo.getLinkUrl();
                                    String[] split = linkUrl.split("#");
                                    if (split != null && split.length == 2 && Strs.isEqual("superPartner", split[0])) {
                                        ActShareAndSuperPartner.launch(ActMain.this, Integer.parseInt(split[1]));
                                    }
                                }
                            }
                        });
                    }
                    break;
                case 3:
                    boolean advflag3 = ((App) getApplication()).advPopShownFlags[position][2];
                    if (advflag3 && UserManagerTouCai.getIns().isLogined()) {
                        showAdvDialog(ActMain.this, ENV.curr.host + configInfo.getAppImgUrl(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {//点击图片 链接跳转

                                if (configInfo.getLinkType() == 1) {//跳转外部链接

                                    if (UserManagerTouCai.getIns().isLogined()) {
                                        ActWebX5.launch(ActMain.this, configInfo.getTitle(), configInfo.getLinkUrl(), true);
                                    } else {
                                        UserManagerTouCai.getIns().redirectToLogin();
                                    }

                                } else if (configInfo.getLinkType() == 2) {//跳转app内页--活动列表页
                                    getTabLayout().setCurrentTab(3);
                                    EventBus.getDefault().post(new MissionindexMode(configInfo.getLinkUrl()));
                                }
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((App) getApplication()).advPopShownFlags[position][2] = false;
                            }
                        });
                    }

                    break;
                default:
            }
        }
    }


    public void showAdvDialog(Context context, String picUrl, final View.OnClickListener onConfirm, final View.OnClickListener onCancel) {
        mAdvDialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_adv, null);
        ImageView picContainer = ((ImageView) root.findViewById(R.id.ivAdvPic));

        Glide.with(context).load(picUrl).asBitmap().fitCenter().into(picContainer);

        root.findViewById(R.id.btnCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancel != null) {
                    onCancel.onClick(v);
                }
                mAdvDialog.dismiss();
            }
        });

        picContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirm != null)
                    onConfirm.onClick(v);
                mAdvDialog.dismiss();
            }
        });
        mAdvDialog.setCanceledOnTouchOutside(false);
        mAdvDialog.setContentView(root);
        if (!mAdvDialog.isShowing()) {
            mAdvDialog.show();
        }
    }

    private void refresh(String eventName, Bundle bundle) {
        //刷新界面等
        updatePositionUI(tabLayout.getCurrentTab());

        //内部页面自己捕获消息刷新

        //刷新内部实现refreshablefragment且正在显示的页面
        /*for (int i = 0; i < arrFrags.length; i++) {
            if(arrFrags[i] instanceof IRefreshableFragment && tabLayout.getCurrentTab() == i){
                ((IRefreshableFragment)arrFrags[i]).refresh(eventName, bundle);
            }
        }*/
    }
    
    /*private void checkUserBankCard() {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {
            
            }
        
            @Override
            public void onUserBindChecked(UserBindStatus status) {
                if (!status.isBindWithdrawPassword()) {
                    showCheckBankDialog(new AbCallback<Object>() {
                        @Override
                        public boolean callback(Object obj) {
                            ActBindBankCard.launch(ActMain.this, true);
                            return true;
                        }
                    });
                }
            }
        
            @Override
            public void onUserBindCheckFailed(String msg) {
            
            }
        
            @Override
            public void onAfter() {
            
            }
        });
    }*/

    public void updatePositionUI(int position) {
        ivChargeRecode.setVisibility(View.GONE);
        ivLogo.setVisibility(View.INVISIBLE);
        vgPerson.setVisibility(View.INVISIBLE);
        switch (position) {
            case 0:
                setToolBarHomeStyle();
                break;
            case 1:
                setToolBarSingleStyle(mTitles[position]);
                break;
            case 2: //首页
                if (insistToHome()) return;
                setToolBarSingleStyle(mTitles[position]);
                break;
            case 3:
                if (insistToHome()) return;
                setToolBarSingleStyle(mTitles[position]);
                break;
            case 4: //每次点击我的页面时，都去查询账户的个人余额
                if (insistToHome()) return;
                setToolBarPersonStyle();
                break;
            default:
                break;
        }
    }

    public boolean insistToHome() {
        if (UserManagerTouCai.getIns().isNotLogined()) {
            setToolBarHomeStyle();
            navigatePage = 0;
            return true;
        } else {
            return false;
        }
    }

    private void setToolBarSingleStyle(String str) {
        vgSlideTop.setVisibility(View.VISIBLE);
        vgPerson.setVisibility(View.INVISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        UIHelper.getIns().simpleToolbar(toolbar, str);
        if (str.equals("彩厅")) {
            if (UserManagerTouCai.getIns().isLogined()) {
                ivChargeRecode.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setToolBarKaijiangStyle() {
        UIHelper.getIns().simpleToolbar(toolbar, "开奖");
        vgPerson.setVisibility(View.VISIBLE);
        vgSlideTop.setVisibility(View.GONE);
        ivLogo.setVisibility(View.INVISIBLE);
        toolbar.setVisibility(View.GONE);
    }

    private void setToolBarPersonStyle() {
        UIHelper.getIns().simpleToolbar(toolbar, "我的");
        vgPerson.setVisibility(View.VISIBLE);
        vgSlideTop.setVisibility(View.GONE);
        ivLogo.setVisibility(View.INVISIBLE);

        toolbar.setVisibility(View.GONE);

        ivSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ActMain.this, ActSetting.class);
                startActivity(intent);
            }
        });

        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent1 = new Intent();
//                intent1.setClass(ActMain.this, ActBroadList.class);
//                startActivity(intent1);
                ActHomeNotice.launch(ActMain.this);
            }
        });
    }

    public void setToolBarHomeStyle() {
        UIHelper.getIns().simpleToolbar(toolbar, "");
        toolbar.setVisibility(View.VISIBLE);
        vgSlideTop.setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.ibLeftBtn).setVisibility(View.INVISIBLE);
        if (UserManagerTouCai.getIns().isLogined()) { //已登录
            //slidingMenu.setSlidingEnabled(true);
            //toolbar.findViewById(R.id.ibLeftBtn).setVisibility(View.VISIBLE);
            //((ImageButton) toolbar.findViewById(R.id.ibLeftBtn)).setImageResource(R.mipmap.ic_cjhhr);
            //((ImageButton) toolbar.findViewById(R.id.ibLeftBtn)).setOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //        ActShareAndSuperPartner.launch(ActMain.this);
            //    }
            //});
            /*UIHelperTouCai.setToolbarLeftButtonImg(toolbar, R.mipmap.btn_side_menu, 37, 37, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSlidingMenu().showMenu(true);
                }
            });*/
        } else { //未登录
            //noting
        }

        ivLogo.setVisibility(View.VISIBLE);
        if (UserManagerTouCai.getIns().isLogined()) {
            ivChargeRecode.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (mRedPacketMode != null) {
            mRedPacketMode.onDestory();
        }

        if (CommonConsts.setContactsConfig(BaseConfig.custom_flag)) {
            //initStatistics();
        }

        if (!UserManagerTouCai.getIns().isLogined()) {
            mRlAdvLeftPop.setVisibility(View.GONE);
            mRlAdvRightPop.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        int toTabIndex = getIntent().getIntExtra("toTabIndex", 0);
        checkRedPacketMission(tabLayout.getCurrentTab());
        if (mRedPacketMode != null) {
            mRedPacketMode.onDestory();
        }
        if (tabLayout.getCurrentTab() == 0) {
            checkNewMission();
            initAdvManagment(Consitances.AdvertisementSet.TABHOME, 0);
        }

        if (tabLayout.getCurrentTab() == 2 && mFragments != null) {
            ((FragTabDepositIndexNew) mFragments.get(2)).getAllPaymentCategory();
            ((FragTabDepositIndexNew) mFragments.get(2)).getAllThirdPayment();
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        if (mRedPacketMode != null) {
            mRedPacketMode.onDestory();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainVisible = true;
//        if (navigatePage != -1) {
//            tabLayout.setCurrentTab(navigatePage);
//            updatePositionUI(navigatePage);
//            navigatePage = -1;
//        }
        FloatActionController.checkAndShowFloat(this);
//        if (!BaseConfig.FLAG_TOUCAI_DEMO.equals(Config.custom_flag)) {
//            UserManager.getIns().checkLatestVersion(ActMain.this, new UserManager.IVersionUpdateCallback() {
//                @Override
//                public void noNeedUpdate() {
//                    tabLayout.requestLayout();
//                    tabLayout.postDelayed(() -> {
//                        if (UserManager.getIns().isLogined() && !haveShownMissioon) {
//                            tabLayout.setEnabled(false);
//                            fragTabHome.setupGuidePage(() -> {
//                                if (!haveRedpacketMission) {
//                                    checkNewMission();
//                                }
//                                tabLayout.setEnabled(true);
//                            });
//                            haveShownMissioon = true;
//                        }
//                    }, 300);
//                }
//            });
//        }

        //处理loading接收到的
        checkPushMsg();
        //}

        App application = (App) getApplication();
        if (application != null && application.needUpdateTip) {
            UserManager.getIns().checkLatestVersion(ActMain.this, new UserManager.IVersionUpdateCallback() {
                @Override
                public void noNeedUpdate() {

                }

                @Override
                public void notTipUpdate() {
                    application.needUpdateTip = false;
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainVisible = false;
        DialogsTouCai.hideProgressDialog(this);
        if (mRedPacketMode != null) {
            mRedPacketMode.onDestory();
        }
    }

    private void checkPushMsg() {
        AppUmengPushHandler.CustomMsg latestPushMsg = ((UserManagerTouCai) UserManager.getIns()).getLatestPushMsg();
        AbDebug.log(AbDebug.TAG_PUSH, "处理最新推送" + latestPushMsg.toString());
        if (latestPushMsg.getCode() != -1) {
            tabLayout.postDelayed(new ValueRunnable(latestPushMsg) {
                @Override
                public void run() {
                    ((IPushUmeng) MM.push).getUmengPushCallback().doMsg(getValue());
                }
            }, 300);
            ((UserManagerTouCai) UserManager.getIns()).setLatestPushMsg(null);
        }
    }

    public void checkNewMission() {
        haveShownMissioon = false;
//        HttpActionTouCai.getMissionList(this, 10, new AbHttpResult() {
//            @Override
//            public void onBefore(Request request, int id, String host, String funcName) {
//                DialogsTouCai.showProgressDialog(ActMain.this, "");
//            }
//
//            @Override
//            public void setupEntity(AbHttpRespEntity entity) {
//                entity.putField("data", new TypeToken<ArrayList<NewUserMission>>() {
//                }.getType());
//            }
//
//            @Override
//            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
//                if (code == 0 && error == 0) {
//                    List<NewUserMission> listMission = getField(extra, "data", null);
//                    if (listMission.size() > 0) {
//                        NewUserMission finalMission = listMission.get(0);
//                        HttpAction.getUserBindStatus(new AbHttpResult() {
//                            @Override
//                            public void onBefore(Request request, int id, String host, String funcName) {
//
//                            }
//
//                            @Override
//                            public void setupEntity(AbHttpRespEntity entity) {
//                                entity.putField("data", new TypeToken<UserBindStatus>() {
//                                }.getType());
//                            }
//
//                            @Override
//                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
//                                if (code == 0 && error == 0) {
//                                    UserBindStatus status = getFieldObject(extra, "data", UserBindStatus.class);
//                                    if (status != null) {
//                                        if (DialogsNewMission.start(ActMain.this, finalMission, status, new DialogsNewMission.MissionCallBack() {
//                                            @Override
//                                            public void onStart() {
//                                                fragTabHome.newMissionIconOut(true);
//                                            }
//
//                                            @Override
//                                            public void onStop(boolean finished) {
//                                                if (finished) {
//                                                    fragTabHome.showNewMissionIcon(false);
//                                                } else {
//                                                    fragTabHome.newMissionIconOut(false);
//                                                }
////                                                haveShownMissioon = false;
//                                            }
//                                        }))
//                                            fragTabHome.showNewMissionIcon(true);
//                                        haveShownMissioon = true;
//                                    }
//                                }
//                                return true;
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                DialogsTouCai.hideProgressDialog(ActMain.this);
//
//                                if (!haveShownMissioon)
//                                    fragTabHome.showNewMissionIcon(false);
//                            }
//                        });
//                    } else {
//                        DialogsTouCai.hideProgressDialog(ActMain.this);
//                        fragTabHome.showNewMissionIcon(false);
//                    }
//                } else {
//                    DialogsTouCai.hideProgressDialog(ActMain.this);
//                }
//                return true;
//            }
//
//            @Override
//            public boolean onError(int status, String content) {
//                DialogsTouCai.hideProgressDialog(ActMain.this);
//                fragTabHome.showNewMissionIcon(false);
//                return true;
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                DialogsTouCai.hideProgressDialog(ActMain.this);
//            }
//        });
    }

    /**
     * 获取红包雨活动
     * 约定红包雨的bizType=21
     */
    private void checkRedPacketMission(int tabposition) {
//        HttpActionTouCai.getMissionList(this, 14, new AbHttpResult() {
//
//            @Override
//            public void setupEntity(AbHttpRespEntity entity) {
//                entity.putField("data", new TypeToken<ArrayList<NewUserMission>>() {
//                }.getType());
//            }
//
//            @Override
//            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
//                if (code == 0 && error == 0) {
//                    List<NewUserMission> listMission = getField(extra, "data", null);
//                    if (listMission.size() > 0) {
//                        NewUserMission latestMission = listMission.get(0);//默认取最新活动
//                        String missionId = listMission.get(0).id;
//                        if (!(UserManagerTouCai.getIns().isRedPacketAwardPoolOver() && missionId.equals(UserManagerTouCai.getIns().getRedPacketMissionID()))) {
//                            //判断当前是否在红包活动有效期内
//                            getRedPacketActvityDetail(latestMission.id, tabposition, missionId);
//                        }
//                    }
//                }
//                return true;
//            }
//        });
    }

    /**
     * 获取红包活动配置
     *
     * @param id
     */
    private void getRedPacketActvityDetail(String id, int tabposition, String missionId) {
        HttpActionTouCai.getActivityConfigDetail(this, id, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<RedPacketMission>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    RedPacketMission redPacketMission = getFieldObject(extra, "data", RedPacketMission.class);
                    if (redPacketMission == null) {
                        return false;
                    }

                    if (DateTool.isWithinSpecifiedTime(redPacketMission.getStartTime(), redPacketMission.getEndTime(), AbDateUtil.dateFormatYMDHMS)) {
                        //开启一个定时器循环检测红包活动
                        getAvailableRedPacketsNum(id, tabposition, missionId);
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

        });
    }

    private void getAvailableRedPacketsNum(String id, int tabposition, String missionId) {
        HttpActionTouCai.getObtainDrawCount(this, id, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<AvailableRedPacketInfo>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    AvailableRedPacketInfo redPacketInfo = getFieldObject(extra, "data", AvailableRedPacketInfo.class);
                    if (redPacketInfo.getCount() > 0) {
                        showRedPacketLingquDetail(id, redPacketInfo, tabposition, missionId);
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

        });
    }

    boolean haveRedpacketMission = false;

    private void showRedPacketLingquDetail(String id, AvailableRedPacketInfo redPacketInfo, int tabposition, String missionId) {
        HttpActionTouCai.getActivityCheckout(this, id, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<RedPacketDetail>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    RedPacketDetail redPacketDetail = getFieldObject(extra, "data", RedPacketDetail.class);
                    if (redPacketDetail != null) {
                        showRedPacketMission(redPacketDetail.getTimes(), redPacketInfo.getCount(), Double.parseDouble(Nums.formatDecimal(redPacketInfo.getNextAmountSub(), 2)), id, tabposition, missionId);
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

        });
    }

    private void showRedPacketMission(int timesHaveget, int redPacketCount, double amountSub, String id, int tabposition, String missionId) {
        String content = "";
        int picRes = 0;
        String textOk = "";
        String textTtile = "";
        if (tabposition == 0) {
            if (timesHaveget > 0) {
                content = "你还剩余" + "<font color='#FF0000'>" + redPacketCount + "</font>" + "次红包领取机会，\n请继续领取!";
                picRes = R.mipmap.to_receive_a_red_envelope;
                textOk = "领取红包";
                textTtile = "红包雨";
            } else {
                content = "红包雨即将开始，请玩家\n做好准备!";
                picRes = R.mipmap.activities_began;
                textOk = "参加活动";
                textTtile = "红包雨";
            }
        } else {
            content = "恭喜你获得<font color='#FF0000'>" + "【天降红包】" + "</font>活动的参加资格，快去首页领取活动奖励";
            picRes = R.mipmap.heaven_a_red_envelope;
            textOk = "参加活动";
            textTtile = "天降红包";
        }

        String finalTextTtile = textTtile;
        String finalContent = content;
        String finalTextOk = textOk;
        int finalPicRes = picRes;

        if (tabLayout.getCurrentTab() == tabposition) {
            if (tabposition == 0) {
                vgSlideTop.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showRedPacketMissionTipDialog(ActMain.this, finalTextTtile, tabposition, finalContent, finalTextOk, finalPicRes, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (tabposition == 0) {
                                    View redpacketView = LayoutInflater.from(ActMain.this).inflate(R.layout.act_red_packet_mission_layout, null, false);
                                    framRedPacket.addView(redpacketView);
                                    if (haveShownMissioon) {
                                        DialogsNewMission.stop(false);
                                    }
                                    framRedPacket.setVisibility(View.VISIBLE);
                                    mRedPacketMode = new RedPacketMode(ActMain.this, framRedPacket, redpacketView);
                                    mRedPacketMode.initView(redPacketCount, amountSub, id, missionId);
                                } else {
                                    getTabLayout().setCurrentTab(0);
                                    updatePositionUI(0);
                                    checkRedPacketMission(0);
                                }
                            }
                        }, null);
                    }
                }, 1000);
            } else {
                DialogsTouCai.showRedPacketMissionTipDialog(ActMain.this, finalTextTtile, tabposition, finalContent, finalTextOk, finalPicRes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (tabposition == 0) {
                            View redpacketView = LayoutInflater.from(ActMain.this).inflate(R.layout.act_red_packet_mission_layout, null, false);
                            framRedPacket.addView(redpacketView);
                            framRedPacket.setVisibility(View.VISIBLE);
                            mRedPacketMode = new RedPacketMode(ActMain.this, framRedPacket, redpacketView);
                            mRedPacketMode.initView(redPacketCount, amountSub, id, missionId);
                        } else {
                            getTabLayout().setCurrentTab(0);
                            updatePositionUI(0);
                            checkRedPacketMission(0);
                        }
                    }
                }, null);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeMainTab(MainTabEvent mainTabEvent) {
        getTabLayout().setCurrentTab(3);
        tabLayout.notifyDataSetChanged();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int toTabIndex = getIntent().getIntExtra("toTabIndex", 0);
        Log.d("ActMain", "toTabIndex:" + toTabIndex);
        if (toTabIndex == 3) {
            getTabLayout().setCurrentTab(3);
            updatePositionUI(3);
        }

        String extrasRun = intent.getStringExtra("pushContentMode");
        if (Strs.isNotEmpty(extrasRun)) {
            if (UserManagerTouCai.getIns().isLogined()) {
                ActWebX5.launch(this, extrasRun, true);
            } else {
                ActLoginPasswordTouCai.launch(this, extrasRun);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(ActMain.this, "再次点击退出", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                //unbindService(serviceConnection);
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    public void showCheckBankDialog(final AbCallback<Object> callback) {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
        materialDialog = new MaterialDialog(ActMain.this);
        materialDialog.setMessage("亲，先绑定取款人");
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                callback.callback(null);
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

    @Override
    public boolean isDestroyed() {
        return super.isDestroyed();
    }

    @Override
    protected void onDestroy() {
//        if (mX5WebView != null) {
//            mX5WebView.destroy();
//        }
        super.onDestroy();
        if (mSocketIOKeppIntend != null) {
            //stopService(mSocketIOKeppIntend);
        }
        if (mRedPacketMode != null) {
            //mRedPacketMode.onDestory();
        }

        if (mAdvDialog != null) {
            mAdvDialog.cancel();
            mAdvDialog = null;
        }

        for (Map.Entry<String, ControllerBase> entry :
                mapContrllers.entrySet()) {
            entry.getValue().stop();
        }
        MM.http.cancellAllByTag(null);
        ThreadCollector.getIns().clear();
        mCompositeDisposable.clear();
        EventBus.getDefault().unregister(this);
        ((App) getApplication()).actmainCreate = false;
    }


    public ComplexTabLayout getTabLayout() {
        return tabLayout;
    }

//    //showMsg
//    //有新的消息
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void MessageEventBus(ContactsBackMsgMode backmessageInfo) {
//        if (backmessageInfo == null) {
//            return;
//        }
//        //updateNotReadMsgCount();
//    }

    private void updateNotReadMsgCount() {
        int msgNotReadCountSum = UserManagerTouCai.getIns().getMsgNotReadCountSum();
        if (msgNotReadCountSum > 0) {
            //tabLayout.showMsg(4, msgNotReadSum);
            msgNotRead.setVisibility(View.VISIBLE);
            msgNotRead.setText(msgNotReadCountSum > 99 ? "99+" : String.valueOf(msgNotReadCountSum));
        } else {
            //tabLayout.hideMsg(4);
            msgNotRead.setVisibility(View.GONE);
        }
    }

    public void initStatistics() {
        HttpActionTouCai.getcontactsList(this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<List<ContactsMode>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    List<ContactsMode> contactslist = getField(extra, "data", null);

                    UserManagerTouCai.getIns().setNotReadMsgPool(contactslist);

                    int msgNotReadSum = 0;
                    if (contactslist.size() > 0) {
                        for (ContactsMode contactsMode : contactslist) {
                            msgNotReadSum += contactsMode.getUnReadCount();
                        }
                    }

                    if (msgNotReadSum > 0) {
                        //tabLayout.showMsg(4, msgNotReadSum);
                        msgNotRead.setVisibility(View.VISIBLE);
                        msgNotRead.setText(msgNotReadSum > 99 ? "99+" : String.valueOf(msgNotReadSum));
                    } else {
                        //tabLayout.hideMsg(4);
                        msgNotRead.setVisibility(View.GONE);
                    }

                }
                return true;
            }
        });

    }

    SocketIOKeppService.SocketConnectBinder msocketConectBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            msocketConectBinder = (SocketIOKeppService.SocketConnectBinder) iBinder;
            msocketConectBinder.reConnect();
            SocketIOKeppService service = msocketConectBinder.getService();
            service.setmISocketDisconnect(new SocketIOKeppService.ISocketDisconnect() {
                @Override
                public void onSocketDisconnect(Socket socket) {
                    Log.e("ActMain", "==========" + "onSocketDisconnect");
                    if (socket == null) {
                        return;
                    }
                    socket.connect();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("ActMain", "==========" + "onServiceDisconnected");
            msocketConectBinder.disConnect();
        }
    };

}


