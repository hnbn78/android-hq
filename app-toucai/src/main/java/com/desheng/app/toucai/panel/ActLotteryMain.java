package com.desheng.app.toucai.panel;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Strs;
import com.bumptech.glide.Glide;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.adapter.ViewPagerFragmentAdapter;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.global.App;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.AdvManageConfigInfo;
import com.desheng.app.toucai.model.DajiangPushEventModel;
import com.desheng.app.toucai.model.LotteryJump;
import com.desheng.app.toucai.util.Utils;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayCategory;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ActLotteryMain extends AbAdvanceActivity implements ViewPager.OnPageChangeListener, FragBetLottery.OnLotteryMethodChanged, FragBetLottery.OnLotteryKindChanged {

    public static final String GAME_ID_KEY = "game_code";
    public static final String GAME_OPEN_KEY = "game_open";
    public TextView tvTitle;
    public TabLayout tabLayout, mChangwanTab;
    private ViewPager viewPager;
    private int gameId;
    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] args = {"??????", "??????", "??????", "??????"};
    private ILotteryKind lotteryInfo;
    private boolean isOpen;
    private LotteryKind mLotteryKind;
    private String mCategory = "";
    private CheckBox mIvJump;
    ViewPagerFragmentAdapter adapter;

    public RelativeLayout mRlAdvRightPop;
    public ImageView mIvRightAdvPic;
    public ImageView mBtnRightAdvPopCancel;

    public RelativeLayout mRlAdvLeftPop;
    public ImageView mIvLeftAdvPic;
    public ImageView mBtnLeftAdvPopCancel;
    private Dialog mAdvDialog;
    private ViewFlipper mAnnouncementFilpper;
    private TextView mtvToGuanwang;

    /**
     * @param gameId ??????ID
     * @param isOpen ???????????????????????? false?????????
     */
    public static void launcher(Context act, int gameId, boolean isOpen) {
        Intent intent = new Intent(act, ActLotteryMain.class);
        intent.putExtra(GAME_ID_KEY, gameId);
        intent.putExtra(GAME_OPEN_KEY, isOpen);
        act.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_main;
    }

    @Override
    protected void init() {
        hideToolbar();
        UserManager.getIns().setHintTimes(1);//?????????
        EventBus.getDefault().register(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utils.setStatusBarColor(this, Color.parseColor("#36abf9"));
        }

        gameId = getIntent().getIntExtra(GAME_ID_KEY, 0);
        isOpen = getIntent().getBooleanExtra(GAME_OPEN_KEY, false);
        lotteryInfo = LotteryKind.find(gameId);

        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
        tvTitle = findViewById(R.id.tvTitle);
        mChangwanTab = findViewById(R.id.changwanTab);
        tabLayout = findViewById(R.id.tabLayout);
        mIvJump = findViewById(R.id.iv_jump);
        mtvToGuanwang = findViewById(R.id.tv_toGuanwang);
        viewPager = findViewById(R.id.viewPager);
        mAnnouncementFilpper = ((ViewFlipper) findViewById(R.id.filpper));

        //???????????????
        mRlAdvRightPop = ((RelativeLayout) findViewById(R.id.RlAdvRightPop));
        mIvRightAdvPic = ((ImageView) findViewById(R.id.IvRightAdvPic));
        mBtnRightAdvPopCancel = ((ImageView) findViewById(R.id.BtnRightAdvPopCancel));
        //???????????????
        mRlAdvLeftPop = ((RelativeLayout) findViewById(R.id.RlAdvLeftPop));
        mIvLeftAdvPic = ((ImageView) findViewById(R.id.IvLeftAdvPic));
        mBtnLeftAdvPopCancel = ((ImageView) findViewById(R.id.BtnLeftAdvPopCancel));

        fragmentList.add(FragOpenLottery.newInstance(lotteryInfo.getId()));
        fragmentList.add(FragBetLottery.newIns(gameId));
        fragmentList.add(FragRecordLottery.newIns());
        mLotteryKind = (LotteryKind) CtxLotteryTouCai.getIns().findLotteryKind(lotteryInfo.getId());
        mCategory = mLotteryKind.getPlayCategory();
        if (CtxLotteryTouCai.getIns().lotteryKind("LHC").getId() != lotteryInfo.getId()) {
            fragmentList.add(FragTrend.newInstance(String.valueOf(lotteryInfo.getId())));
            for (int i = 0; i < 4; i++) {
                tabLayout.addTab(tabLayout.newTab().setText(args[i]));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                tabLayout.addTab(tabLayout.newTab().setText(args[i]));
            }
        }

        adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), fragmentList, Arrays.asList(args));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(isOpen ? 0 : 1);

        if (useNewBetUIConfig()) {
            //tvTitle.setText("");
            tvTitle.setCompoundDrawables(null, null, null, null);
            mChangwanTab.addTab(mChangwanTab.newTab().setText(lotteryInfo.getShowName()));
        } else {
            //tvTitle.setOnClickListener(v -> ((FragBetLottery) fragmentList.get(1)).showMenu());
            //tvTitle.setOnClickListener(v -> ((FragBetLottery) fragmentList.get(1)).showMenu());
        }
        setDefaultPlay(lotteryInfo.getId());


        mIvJump.setBackgroundResource(R.mipmap.ic_lottery_play_jump_left_1);

        LotteryJump jumpInfo = UserManagerTouCai.getIns().getJumpInfo().get(String.valueOf(mLotteryKind.getId()));
        if (jumpInfo == null) {
            //mIvJump.setVisibility(View.GONE);
        } else {
            //mIvJump.setVisibility(View.VISIBLE);
        }

        mIvJump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((FragBetLottery) fragmentList.get(1)).loadLottery(jumpInfo.getId());
                    mIvJump.setBackgroundResource(R.mipmap.ic_lottery_play_jump_right_1);
                } else {
                    ((FragBetLottery) fragmentList.get(1)).loadLottery(gameId);
                    mIvJump.setBackgroundResource(R.mipmap.ic_lottery_play_jump_left_1);
                }
            }
        });
        initAdvManagment(Consitances.AdvertisementSet.TOUZHU);
    }

    public void setHeadPlaysTabLayoutNew(List<LotteryPlay> lotteryPlays) {
        if (mChangwanTab == null || lotteryPlays == null || lotteryPlays.size() == 0) {
            return;
        }
        mChangwanTab.removeAllTabs();
        for (LotteryPlay playtab : lotteryPlays) {
            LotteryPlay selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlay(mCategory, playtab.lotteryCode);
            TabLayout.Tab tab = mChangwanTab.newTab();
            tab.setText(selectedLotteryPlay.showName);
            tab.setTag(playtab.getPlayId());
            mChangwanTab.addTab(tab);
        }
    }

    public void setHeadPlaysTabLayoutNew(LotteryPlay lotteryPlay) {
        if (mChangwanTab == null || lotteryPlay == null) {
            return;
        }
        mChangwanTab.removeAllTabs();
        LotteryPlay selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlay(mCategory, lotteryPlay.lotteryCode);
        TabLayout.Tab tab = mChangwanTab.newTab();
        tab.setText(selectedLotteryPlay.showName);
        tab.setTag(lotteryPlay.getPlayId());
        mChangwanTab.addTab(tab);
    }

    public void setHeadPlaysTabLayout(List<String> playtabs) {
//        if (mChangwanTab == null || playtabs == null || playtabs.size() == 0) {
//            return;
//        }
//        mChangwanTab.removeAllTabs();
//        for (String playtab : playtabs) {
//            LotteryPlay selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlay(mCategory, playtab);
//            TabLayout.Tab tab = mChangwanTab.newTab();
//            tab.setText(selectedLotteryPlay.showName);
//            tab.setTag(playtab);
//            mChangwanTab.addTab(tab);
//        }
    }


    //???????????????????????????,???????????????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(DajiangPushEventModel pushEventModel) {
        if (TextUtils.isEmpty(pushEventModel.getContent())) {
            return;
        }

        mAnnouncementFilpper.setVisibility(View.VISIBLE);

        View childview1 = LayoutInflater.from(this).inflate(R.layout.layout_announcement_itemview, null);
        ((TextView) childview1.findViewById(R.id.tvcontent)).setText(pushEventModel.getContent());
        mAnnouncementFilpper.addView(childview1);

        if (mAnnouncementFilpper.getChildCount() == 1) {
            View childview = LayoutInflater.from(this).inflate(R.layout.layout_announcement_itemview, null);
            ((TextView) childview.findViewById(R.id.tvcontent)).setText(pushEventModel.getContent());
            mAnnouncementFilpper.addView(childview);
        }

        mAnnouncementFilpper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAnnouncementFilpper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAnnouncementFilpper.setVisibility(View.GONE);
                    }
                }, 5000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mAnnouncementFilpper.isFlipping()) {
            mAnnouncementFilpper.startFlipping();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAnnouncementFilpper.isFlipping()) {
            mAnnouncementFilpper.stopFlipping();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAnnouncementFilpper != null) {
            mAnnouncementFilpper.stopFlipping();
            mAnnouncementFilpper.removeAllViews();
        }
        if (fragmentList != null) {
            if (fragmentList.size() == 4) {
                FragTrend fragTrend = (FragTrend) fragmentList.get(3);
                if (fragTrend != null) {
                    fragTrend.destroy();
                }
            }
        }
        //????????????
        EventBus.getDefault().unregister(this);
    }

    /**
     * ?????????????????????????????????
     */
    private void initAdvManagment(int advType) {
        HttpActionTouCai.getObtainAdvsManagment(FragTabHome.class, advType, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<AdvManageConfigInfo>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    mAdvConfigList = getField(extra, "data", null);
                    if (mAdvConfigList != null && mAdvConfigList.size() > 0) {
                        updateAdvView(mAdvConfigList);
                    }
                }
                return true;
            }
        });
    }

    List<AdvManageConfigInfo> mAdvConfigList;

    /**
     * ?????????????????????????????????????????????????????????
     */
    private void updateAdvView(List<AdvManageConfigInfo> advConfigList) {
        for (AdvManageConfigInfo configInfo : advConfigList) {
            switch (configInfo.getAdvertisementType()) {
                case 0:
                    break;
                case 1:
                    boolean advflag = ((App) getApplication()).advPopShownFlags[5][0];
                    if (advflag) {
                        mRlAdvLeftPop.setVisibility(View.VISIBLE);
                        Glide.with(this).load(ENV.curr.host + configInfo.getAppImgUrl()).into(mIvLeftAdvPic);
                        mBtnLeftAdvPopCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((App) getApplication()).advPopShownFlags[5][0] = false;
                                mRlAdvLeftPop.setVisibility(View.GONE);
                            }
                        });
                        mRlAdvLeftPop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {//???????????? ????????????
                                if (configInfo.getLinkType() == 1) {//??????????????????
                                    ActWebX5.launch(ActLotteryMain.this, configInfo.getLinkUrl(), true);

                                } else if (configInfo.getLinkType() == 2) {//??????app??????--???????????????
                                    ActMain.launch(ActLotteryMain.this);
                                    //EventBus.getDefault().post(new MainTabEvent(3));
                                    //????????????????????????????????????
                                    finish();
                                } else if (configInfo.getLinkType() == 3) {//??????????????????????????????
                                    String linkUrl = configInfo.getLinkUrl();
                                    String[] split = linkUrl.split("#");
                                    if (split != null && split.length == 2 && Strs.isEqual("superPartner", split[0])) {
                                        ActShareAndSuperPartner.launch(ActLotteryMain.this, Integer.parseInt(split[1]));
                                    }
                                }
                            }
                        });
                    }
                    break;
                case 2:
                    boolean advflag2 = ((App) getApplication()).advPopShownFlags[5][1];
                    if (advflag2) {
                        mRlAdvRightPop.setVisibility(View.VISIBLE);
                        Glide.with(this).load(ENV.curr.host + configInfo.getAppImgUrl()).into(mIvRightAdvPic);
                        mBtnRightAdvPopCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((App) getApplication()).advPopShownFlags[5][1] = false;
                                mRlAdvRightPop.setVisibility(View.GONE);
                            }
                        });
                        mRlAdvRightPop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {//???????????? ????????????
                                if (configInfo.getLinkType() == 1) {//??????????????????
                                    ActWebX5.launch(ActLotteryMain.this, configInfo.getLinkUrl(), true);

                                } else if (configInfo.getLinkType() == 2) {//??????app??????--???????????????
                                    ActMain.launch(ActLotteryMain.this);
                                    //EventBus.getDefault().post(new MainTabEvent(3));
                                    finish();
                                    //????????????????????????????????????
                                } else if (configInfo.getLinkType() == 3) {//??????????????????????????????
                                    String linkUrl = configInfo.getLinkUrl();
                                    String[] split = linkUrl.split("#");
                                    if (split != null && split.length == 2 && Strs.isEqual("superPartner", split[0])) {
                                        ActShareAndSuperPartner.launch(ActLotteryMain.this, Integer.parseInt(split[1]));
                                    }
                                }
                            }
                        });
                    }
                    break;
                case 3:
                    boolean advflag3 = ((App) getApplication()).advPopShownFlags[5][2];
                    if (advflag3) {
                        showAdvDialog(this, ENV.curr.host + configInfo.getAppImgUrl(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {//???????????? ????????????
                                if (configInfo.getLinkType() == 1) {//??????????????????
                                    if (UserManagerTouCai.getIns().isLogined()) {
                                        ActWebX5.launch(ActLotteryMain.this, configInfo.getTitle(), configInfo.getLinkUrl(), true);
                                    } else {
                                        UserManagerTouCai.getIns().redirectToLogin();
                                    }

                                } else if (configInfo.getLinkType() == 2) {//??????app??????--???????????????
                                    ActMain.launch(ActLotteryMain.this);
                                    //EventBus.getDefault().post(new MainTabEvent(3));
                                    //????????????????????????????????????
                                    finish();
                                } else if (configInfo.getLinkType() == 3) {//??????????????????????????????
                                    String linkUrl = configInfo.getLinkUrl();
                                    String[] split = linkUrl.split("#");
                                    if (split != null && split.length == 2 && Strs.isEqual("superPartner", split[0])) {
                                        ActShareAndSuperPartner.launch(ActLotteryMain.this, Integer.parseInt(split[1]));
                                    }
                                }
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((App) getApplication()).advPopShownFlags[5][2] = false;
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

        Glide.with(context).load(picUrl).asBitmap().centerCrop().into(picContainer);

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

    private FragBetLottery.OnLotteryKindChanged onLotteryKindChanged;

    public ILotteryKind getLotteryInfo() {
        return lotteryInfo;
    }

    public void setDefaultPlay(int id) {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                String defaultPlayCode = "";
                if ("SSC".equals(mCategory)) {
                    defaultPlayCode = "sxzhixfsh";
                } else if ("11X5".equals(mCategory)) {
                    defaultPlayCode = "sanmzhixfsq";
                } else if ("K3".equals(mCategory)) {
                    defaultPlayCode = "ebthdx";
                } else if ("PK10".equals(mCategory)) {
                    defaultPlayCode = "qianyi";
                } else if ("3DFC".equals(mCategory)) {
                    defaultPlayCode = "sanxzhixfs";
                } else if ("pl3".equals(mCategory)) {
                    defaultPlayCode = "sxzhixfsh";
                } else if ("LHC".equals(mCategory)) {
                    //tvTitle.setText("??????");
                }

                if (UserManagerTouCai.getIns().getLotteryPlay(mCategory, defaultPlayCode) != null) {
                    if (useNewBetUIConfig()) {
                        //tvTitle.setText(lotteryInfo.getShowName());
                        mChangwanTab.addTab(mChangwanTab.newTab().setText(lotteryInfo.getShowName()));
                    } else {
                        //tvTitle.setText(UserManagerTouCai.getIns().getLotteryPlay(mCategory, defaultPlayCode).showName);
//                        List<String> list = UserManagerTouCai.getIns().getlastLotteryPlayNew(lotteryInfo.getId());
//                        if (list == null || list.size() == 1) {
//                            UserManagerTouCai.getIns().setlastLotteryPlayNew(lotteryInfo.getId(), defaultPlayCode);
//                            List<String> newlist = UserManagerTouCai.getIns().getlastLotteryPlayNew(lotteryInfo.getId());
//                            setHeadPlaysTabLayout(newlist);
//                            return;
//                        }
//
//                        List<String> newlist = UserManagerTouCai.getIns().getlastLotteryPlayNew(lotteryInfo.getId());
//                        setHeadPlaysTabLayout(newlist);


                    }
                }
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            ((FragOpenLottery) fragmentList.get(0)).onRefresh();
        } else if (position == 2) {
            ((FragRecordLottery) fragmentList.get(2)).refresh();
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onLotteryMethodChanged(ILotteryKind kind, String method, String methodName) {

        //UserManagerTouCai.getIns().setlastLotteryPlayNew(kind.getId(), method);
        Log.e("ActLotteryMain", "onLotteryMethodChanged");

        if (useNewBetUIConfig()) {
        } else {
            //tvTitle.setText(methodName);
            //List<String> list = UserManagerTouCai.getIns().getlastLotteryPlayNew(kind.getId());
            //setHeadPlaysTabLayout(list);
        }
    }

    @Override
    public void onLotteryKindChanged(int id) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public boolean useNewBetUIConfig() {
        return CommonConsts.setBetNeedNewUIConfig(lotteryInfo.getCode());
    }

}
