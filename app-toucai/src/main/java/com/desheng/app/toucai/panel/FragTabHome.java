package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ab.callback.AbCallback;
import com.ab.global.AbDevice;
import com.ab.global.ENV;
import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.adapter.MyFragmentAdapter;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.fragment.BasePageFragment;
import com.desheng.app.toucai.global.App;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.AdvManageConfigInfo;
import com.desheng.app.toucai.model.BannerBean;
import com.desheng.app.toucai.model.Event;
import com.desheng.app.toucai.model.LotteryInfoCustom;
import com.desheng.app.toucai.model.NewUserMission;
import com.desheng.app.toucai.model.eventmode.MissionindexMode;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.Utils;
import com.desheng.app.toucai.view.AnimTextView;
import com.desheng.app.toucai.view.AnimTextViewBean;
import com.desheng.app.toucai.view.AutoHeightViewPager;
import com.desheng.app.toucai.view.CustomViewPager;
import com.desheng.app.toucai.view.GlideImageLoader;
import com.desheng.base.BuildConfig;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.event.TransferSuccessEvent;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.MessageFullBean;
import com.desheng.base.model.UserBindStatus;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shark.tc.R;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

import static com.desheng.app.toucai.panel.ActBindBankCardToucai.BIND_OK_RESULT_CODE;

/**
 * ?????????
 * Created by  on 17/9/5.
 */

public class FragTabHome extends AbBaseFragment implements View.OnClickListener, IRefreshableFragment {

    public static final double BANNER_RADIO = 290.0 / 710;
    private static final String GUIDETAG_ADVER = "adver";
    private static final String GUIDETAG_MF = "myf";
    private static final String GUIDETAG_PROM = "prom";
    private static final String GUIDETAG_Third = "third";
    private static final int KEY_GAME_TYPE = 9;
    private final int WITHDRAW_CLICK = 1;
    private final int BANKLIST_CLICK = 2;
    private final int RECHARGE_CLICK = 3;
    private final int INIT = 4;
    public TabLayout mTabLayout;
    public CustomViewPager mViewPager;
    public SmartRefreshLayout srlRefresh;
    View mRoot;
    List<BasePageFragment> mFragments = new ArrayList<>(0);
    private int requestCode = 0;
    private int WITHDRAW_REQUEST_CODE = 10;
    private int BANK_REQUEST_CODE = 11;
    private Banner vBanner;
    private UserManagerTouCai.EventReceiver receiver;
    private AnimTextView marqueeView;
    private ArrayList<MessageFullBean> listMessage;
    private ArrayList<BannerBean> listBanners;
    private View layoutNewMission;
    private Button mBtnLogin;
    private BaseQuickAdapter<ArrayList<LotteryInfoCustom>, BaseViewHolder> mGameHallRvAdapter;
    private boolean isrelaodBadForGonggao = false;
    private TextView moreNotices, tvusername, tvAccountYue;
    private MyFragmentAdapter mAdapter;
    private List<String> listTabTitle = new ArrayList<>(0);

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_home;
    }

    @Override
    public void init(View root) {
        mRoot = root;
        EventBus.getDefault().register(this);
        srlRefresh = (SmartRefreshLayout) root.findViewById(R.id.refresh_layout);
        marqueeView = (AnimTextView) root.findViewById(R.id.vMarquee);
        moreNotices = (TextView) root.findViewById(R.id.moreNotices);
        tvusername = (TextView) root.findViewById(R.id.tvusername);
        tvAccountYue = (TextView) root.findViewById(R.id.tvAccountYue);
        root.findViewById(R.id.IvZhuanzhang).setOnClickListener(this::onClick);
        root.findViewById(R.id.IvChongzhi).setOnClickListener(this::onClick);
        root.findViewById(R.id.IvTixian).setOnClickListener(this::onClick);

        mTabLayout = root.findViewById(R.id.tabLayout);
        mViewPager = root.findViewById(R.id.viewPager);

        layoutNewMission = root.findViewById(R.id.layout_new_misson);
        mBtnLogin = ((Button) root.findViewById(R.id.btnLogin));
        mBtnLogin.setVisibility(UserManager.getIns().isLogined() ? View.GONE : View.VISIBLE);
        mBtnLogin.setOnClickListener(this);
        layoutNewMission.setOnClickListener(this);
        //?????????banner
        initBanner();
        //????????????
        initGameHall();
        fetchData();
        srlRefresh.setEnableLoadMore(false);
        srlRefresh.setEnableRefresh(true);
        srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                fetchData();
                if (mTabLayout == null) {
                    return;
                }
                int selectedTabPosition = mTabLayout.getSelectedTabPosition();
                if (selectedTabPosition == 0 && mFragments.size() > 0) {
                    ((HomeLotteryFrag) mFragments.get(selectedTabPosition)).getOpenData();
                }
            }
        });

        marqueeView.setOnClickListener(new AnimTextView.OnClickListener() {
            @Override
            public void onClick(View v, int position) {
                if (listMessage != null) {
                    MessageFullBean messageFullBean = (MessageFullBean) listMessage.get(position);
                    if (messageFullBean == null) {
                        return;
                    }
                    ActHomeNoticeDetail.launch(context, messageFullBean);
                } else {
                    getMarqueeInfo();
                }
            }
        });
        moreNotices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listMessage != null) {
                    ActHomeNotice.launch(context, listMessage);
                }
            }
        });
    }

    private void fetchData() {
        initAdvManagment();
        getMarqueeInfo();
        updateUserInfo();
        checkActivityOpened();
    }

    @Override
    public void refresh(String eventName, Bundle newBundle) {
        if (eventName.equals("onRefresh")) {
            fetchData();
        } else if (eventName.equals("onShow")) {
            mBtnLogin.setVisibility(UserManager.getIns().isLogined() ? View.GONE : View.VISIBLE);
        }
    }

    public void initBanner() {
        listBanners = new ArrayList<>();
        vBanner = (Banner) mRoot.findViewById(R.id.vBanner);
        //??????banner??????
        vBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //?????????????????????
        vBanner.setImageLoader(new GlideImageLoader());
        //??????banner????????????
        vBanner.setBannerAnimation(Transformer.Default);
        //??????????????????????????????true
        vBanner.isAutoPlay(true);
        //???????????????????????????banner???????????????????????????
        vBanner.setIndicatorGravity(BannerConfig.CENTER);

        vBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (listBanners == null) {
                    return;
                }

                if (Strs.isNotEmpty(listBanners.get(position).linkUrl)) {

                    if (listBanners.get(position).linkType == 2) {//????????????--?????????
                        ((ActMain) context).getTabLayout().setCurrentTab(3);
                        EventBus.getDefault().post(new MissionindexMode(listBanners.get(position).linkUrl));
                    } else if (listBanners.get(position).linkType == 3) {//??????????????????????????????
                        String linkUrl = listBanners.get(position).linkUrl;
                        String[] split = linkUrl.split("#");
                        if (split != null && split.length == 2 && Strs.isEqual("superPartner", split[0])) {
                            if (UserManagerTouCai.getIns().isLogined()) {
                                ActShareAndSuperPartner.launch(context, Integer.parseInt(split[1]));
                            } else {
                                UserManagerTouCai.getIns().redirectToLogin();
                            }
                        }
                    } else {
                        String url = "";
                        if (listBanners.get(position).linkUrl.toLowerCase().startsWith("http")) {
                            url = listBanners.get(position).linkUrl;
                        } else {
                            url = ENV.curr.host + listBanners.get(position).linkUrl;
                        }

                        if (listBanners.get(position).title.contains("??????") && !UserManager.getIns().isLogined()) {
                            UserManager.getIns().redirectToLogin();
                        } else {
                            if (UserManagerTouCai.getIns().isLogined()) {
                                ActWebX5.launch(context, listBanners.get(position).title, url, true);
                            } else {
                                UserManagerTouCai.getIns().redirectToLogin();
                            }
                        }
                    }
                } else {
                    Toasts.show(context, "????????????, ????????????", true);
                }
            }

        });
    }

    public void setupBanner() {
        // ????????????
        //??????????????????
        vBanner.setImages(listBanners);
        //banner?????????????????????????????????????????????
        vBanner.start();
    }

    private void getMarqueeInfo() {
        HttpActionTouCai.getMessageCenter(context, 0, 10, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showLoadingDialog(context, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<MessageFullBean>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    listMessage = getField(extra, "data", null);
                    ArrayList<AnimTextViewBean> listMsg = new ArrayList<>();
                    if (listMessage != null && listMessage.size() > 0) {
                        for (int i = 0; i < listMessage.size(); i++) {
                            MessageFullBean currMessage = listMessage.get(i);
                            listMsg.add(new AnimTextViewBean(currMessage.getTitle(), currMessage.getTitle()));
                        }
                        if (listMsg != null) {
                            marqueeView.setDatas(listMsg);
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                if (!isrelaodBadForGonggao) {
                    isrelaodBadForGonggao = true;
                    getMarqueeInfo();
                }
                Dialogs.hideProgressDialog(context);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                Dialogs.hideProgressDialog(context);
                srlRefresh.finishRefresh();
            }
        });
    }

    /**
     * ?????????????????????????????????
     */
    private void initAdvManagment() {
        HttpActionTouCai.getObtainAdvsManagment(FragTabHome.class, Consitances.AdvertisementSet.TABHOME, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<AdvManageConfigInfo>>() {
                }.getType());
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(context, "");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    List<AdvManageConfigInfo> mAdvConfigList = getField(extra, "data", null);
                    if (mAdvConfigList != null && mAdvConfigList.size() > 0) {

                        if (listBanners == null) {
                            listBanners = new ArrayList<>();
                        }

                        listBanners.clear();

                        for (AdvManageConfigInfo configInfo : mAdvConfigList) {
                            //???????????????????????????????????????0???????????????????????????????????????3???
                            if (configInfo.getAdvertisementType() == 0 && configInfo.getShowTerminalTpye().contains("3")) {
                                BannerBean bannerBean = new BannerBean();
                                bannerBean.imgUrl = configInfo.getAppImgUrl();
                                bannerBean.linkUrl = configInfo.getLinkUrl();
                                bannerBean.title = configInfo.getTitle();
                                bannerBean.linkType = configInfo.getLinkType();
                                listBanners.add(bannerBean);
                            }
                        }

                        if (listBanners.size() == 0) {
                            BannerBean banner = new BannerBean();
                            banner.imgUrl = "file:///android_asset/img_home_banner_default.webp";
                            listBanners.add(banner);
                        }

                        for (int i = 0; i < listBanners.size(); i++) {
                            BannerBean bannerBean = listBanners.get(i);
                            bannerBean.linkUrl = CtxLottery.replaceHtmlSign(bannerBean.linkUrl);
                            bannerBean.width = (int) (AbDevice.SCREEN_WIDTH_PX - Views.dp2px(5));
                            bannerBean.height = (int) (bannerBean.width * BANNER_RADIO);
                        }

                        setupBanner();
                    } else {
                        if (listBanners == null) {
                            listBanners = new ArrayList<>();
                        }

                        listBanners.clear();

                        if (listBanners.size() == 0) {
                            BannerBean banner = new BannerBean();
                            banner.imgUrl = "file:///android_asset/img_home_banner_default.webp";
                            listBanners.add(banner);
                        }

                        for (int i = 0; i < listBanners.size(); i++) {
                            BannerBean bannerBean = listBanners.get(i);
                            bannerBean.linkUrl = CtxLottery.replaceHtmlSign(bannerBean.linkUrl);
                            bannerBean.width = (int) (AbDevice.SCREEN_WIDTH_PX - Views.dp2px(5));
                            bannerBean.height = (int) (bannerBean.width * BANNER_RADIO);
                        }

                        setupBanner();
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                if (listBanners == null) {
                    listBanners = new ArrayList<>();
                }

                if (listBanners == null || listBanners.size() == 0) {
                    BannerBean banner = new BannerBean();
                    banner.imgUrl = "file:///android_asset/img_home_banner_default.webp";
                    banner.width = (int) (AbDevice.SCREEN_WIDTH_PX - Views.dp2px(5));
                    banner.height = (int) (banner.width * BANNER_RADIO);
                    listBanners.add(banner);
                }

                setupBanner();
                return true;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(context);
                if (UserManager.getIns().isLogined()) {
                    Log.e("ActMain", "-----ActMain------getSessionID-------: " + getSessionID());
                    ((App) context.getApplication()).initMyIm(getSessionID());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (UserManager.getIns().isLogined()) {
                    mBtnLogin.setVisibility(View.GONE);
                    getMarqueeInfo();
                } else {
                    UserManager.getIns().redirectToLogin(context);
                }
                break;
            case R.id.layout_new_misson:
                ((ActMain) context).checkNewMission();
                break;
            case R.id.IvChongzhi:
                checkBindStatus(RECHARGE_CLICK, null);
                break;
            case R.id.IvTixian:
                requestCode = WITHDRAW_REQUEST_CODE;
                checkBindStatus(WITHDRAW_CLICK, null);
                break;
            case R.id.IvZhuanzhang:
                ActThirdTransferFastNew.launch(context);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        receiver = new UserManagerTouCai.EventReceiver().register(context, new UserManagerTouCai.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (UserManager.EVENT_USER_LOGINED.equals(eventName)) {
                    //onRefresh();
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        receiver.unregister(context);
        Dialogs.hideFullScreenDialog(context);
    }

    public void showNewMissionIcon(boolean show) {
        if (layoutNewMission != null) {
            layoutNewMission.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void initGameHall() {
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//??????tab????????????????????????????????????
        mFragments.clear();
        mTabLayout.removeAllTabs();
        listTabTitle.add("??????");
        listTabTitle.add("????????????");
        listTabTitle.add("????????????");
        listTabTitle.add("??????");
        listTabTitle.add("????????????");
        mFragments.add(HomeLotteryFrag.newInstance());
        mFragments.add(HomeThirdGameFrag.newInstance(1));
        mFragments.add(HomeThirdGameFrag.newInstance(2));
        mFragments.add(HomeThirdGameFrag.newInstance(3));
        mFragments.add(HomeThirdGameFrag.newInstance(4));
        mAdapter = new MyFragmentAdapter(getFragmentManager(), mFragments, listTabTitle);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        //????????????TabLayout???????????????
        //??????ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(0);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void onShow() {
        super.onShow();
        if (!UserManagerTouCai.getIns().isLogined()) {
            showNewMissionIcon(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginBtn();
        updateUserInfo();
    }

    public void updateLoginBtn() {
        if (mBtnLogin == null) {
            return;
        }
        mBtnLogin.setVisibility(UserManager.getIns().isLogined() ? View.GONE : View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.LoginEvent event) {
        getMarqueeInfo();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //????????????
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (vBanner != null) {
            vBanner.stopAutoPlay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        DialogsTouCai.hideProgressDialog(context);
        if (vBanner != null) {
            vBanner.stopAutoPlay();
        }
    }

    private void checkActivityOpened() {
        HttpActionTouCai.getMissionList(this, 14, new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<NewUserMission>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    List<NewUserMission> listMission = getField(extra, "data", null);
                    if (listMission.size() > 0) {
                        NewUserMission latestMission = listMission.get(0);//?????????????????????
                        String missionId = listMission.get(0).id;
                        checkBindStatus(INIT, missionId);
                    }
                }
                return true;
            }
        });
    }

    private void checkBindStatus(int type, String missionId) {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {
                Dialogs.showProgressDialog(context, "");
            }

            @Override
            public void onUserBindChecked(UserBindStatus status) {
                Dialogs.hideProgressDialog(context);
                if (type == INIT) {
                    if (!status.isBindWithdrawName() || !status.isBindWithdrawPassword() || !status.isBindCard()) {
                        ActBindBankCardDialog.launch(context, true, missionId);
                    }
                } else {
                    if (!status.isBindWithdrawName() || !status.isBindWithdrawPassword() || !status.isBindCard()) {
                        showBindTip(status, "???????????????????????????????????????", "????????????????????????????????????????????????????????????????????????", "????????????", -1);
                    } else if (type == BANKLIST_CLICK) {
                        if (!status.isBindCard()) {
                            launchBindBankCardToucai(ActBindBankCardToucai.class);
                        } else {
                            ActBankCardListTouCai.launch(getActivity());
                        }
                    } else if (!status.isBindCard()) {
                        showBindTip(status, "???????????????????????????", "???????????????????????????????????????", "????????????", 2);
                    } else if (type == WITHDRAW_CLICK) {
                        if (!status.isBindCard()) {
                            showBindTip(status, "???????????????????????????", "???????????????????????????????????????", "????????????", 1);
                        } else {
                            if (UserManager.getIns().getLotteryAvailableBalance() == 0) {
                                Toasts.show("??????????????????????????????", false);
                            } else {
                                launcherAct(ActWithdrawalsList.class);
                            }
                        }
                    } else if (type == RECHARGE_CLICK) {
                        ActDeposit.launch(getActivity());
                    }
                }
            }

            @Override
            public void onUserBindCheckFailed(String msg) {
                Dialogs.hideProgressDialog(context);
            }

            @Override
            public void onAfter() {
                Dialogs.hideProgressDialog(context);
            }
        });
    }

    private void showBindTip(UserBindStatus status, String tip1, String tip2, String right, int type) {
        DialogsTouCai.showBindDialog(getActivity(), tip1, tip2, "", right, true, new AbCallback<Object>() {
            @Override
            public boolean callback(Object obj) {

                if (!status.isBindWithdrawName() || !status.isBindWithdrawPassword() || !status.isBindCard()) {
                    ActBindBankCardToucai.launch(context, !status.isBindWithdrawName());
                } else if (UserManager.getIns().getLotteryAvailableBalance() == 0) {
                    if (type == WITHDRAW_CLICK) {
                        Toasts.show("??????????????????????????????", false);
                    } else if (type == BANKLIST_CLICK) {
                        launcherAct(ActBankCardListTouCai.class);
                    }
                }
                DialogsTouCai.hideBindTipDialog();

                return true;
            }
        });
    }

    public void launchBindBankCardToucai(Class cls) {
        Intent itt = new Intent(context, cls);
        itt.putExtra("isFirstTime", true);
        startActivityForResult(itt, requestCode);
    }

    private void launcherAct(Class cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("isWithdraw", requestCode == WITHDRAW_REQUEST_CODE);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActWithdrawals.WITHDRAW_OK_RESULT_OK) {
            updateUserInfo();
        } else if (requestCode == WITHDRAW_REQUEST_CODE && resultCode == BIND_OK_RESULT_CODE
                || resultCode == ActEditAccount.RESULT_CODE) {
            this.requestCode = 0;
            if (UserManager.getIns().getIsBindPhone())
                launcherAct(ActWithdrawals.class);
        } else if (requestCode == BANK_REQUEST_CODE && resultCode == BIND_OK_RESULT_CODE) {
            this.requestCode = 0;
            launcherAct(ActBankCardListTouCai.class);
        }
    }

    private void updateUserInfo() {
        if (UserManager.getIns().isLogined()) {
            tvusername.setText(UserManagerTouCai.getIns().getMainUserName());
            tvAccountYue.setText(Utils.getDoubleNotRound2Str(UserManagerTouCai.getIns().getLotteryAvailableBalance()) + "???");
        } else {
            tvusername.setText("?????????");
        }
    }


    public String getSessionID() {
        String sso_session_uid = "";
        if (Strs.isNotEmpty(AbHttpAO.getIns().getLastCookie())) {
            String[] cookies = AbHttpAO.getIns().getLastCookie().split(";");
            for (String cookie : cookies) {
                if (cookie.contains("sso_session_uid") && !cookie.contains("sso_session_uid_sign")) {
                    sso_session_uid = cookie.split("=")[1];
                }
            }
            Log.e("acttalk", "????????????_session_uid:" + sso_session_uid);
            return sso_session_uid;
        }
        return null;
    }
}