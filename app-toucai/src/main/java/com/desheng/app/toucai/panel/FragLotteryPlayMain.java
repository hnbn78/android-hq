package com.desheng.app.toucai.panel;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.helper.BonusPoolActivityHelper;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.AwardPoolDetailoMode;
import com.desheng.app.toucai.model.LotteryCodeUpdateBean;
import com.desheng.app.toucai.model.LotteryJump;
import com.desheng.app.toucai.model.LotteryOpenNext;
import com.desheng.app.toucai.model.NewUserMission;
import com.desheng.app.toucai.model.eventmode.BonusPoolEventMode;
import com.desheng.app.toucai.model.eventmode.BonusPoolFreshEventMode;
import com.desheng.app.toucai.util.CodeXingtaiUtil;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.Utils;
import com.desheng.app.toucai.view.AwardpoolViewModule;
import com.desheng.app.toucai.view.ImageViewScrollView;
import com.desheng.app.toucai.view.PlayFootView;
import com.desheng.app.toucai.view.PlaySnakeView;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.event.DeleteFromCartEvent;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryOpenHistory;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayCategory;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.desheng.base.panel.ActWebX5;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noober.background.view.BLTextView;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.act.util.FragmentsHelper;
import com.shark.tc.R;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.zhy.view.flowlayout.FlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;

public class FragLotteryPlayMain extends AbBaseFragment implements LotteryPlayPanel, View.OnClickListener {

    private AnimationDrawable mIvOpenningDrawableAnimate;
    private WebView kaijiangWebview;
    private LinearLayout kaijiangAnimate;
    private TextView tvTip;
    private RelativeLayout container;
    private AwardpoolViewModule awardPool;
    private ImageView ivcoins;
    private RecyclerView mRcRecentLotteryOpen;
    private TextView mTvRecentLotteryOpen;
    private BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder> mRcRecentOpenAdapter;
    private BLTextView goOfficeOpenSite;
    private String currentPlayCode;
    private TextView tvjixuan;

    public static Fragment newIns(int id, FragBetLottery.OnLotteryMethodChanged onLotteryMethodChanged, FragBetLottery.OnLotteryKindChanged onLotteryKindChanged) {
        FragLotteryPlayMain fragLotteryPlayMain = new FragLotteryPlayMain();
        fragLotteryPlayMain.lotteryId = id;
        fragLotteryPlayMain.onLotteryMethodChanged = onLotteryMethodChanged;
        fragLotteryPlayMain.onLotteryKindChanged = onLotteryKindChanged;
        return fragLotteryPlayMain;
    }

    public static final int REFRESH_INTERAL = 5;
    public static boolean visible;
    //private LotteryInfo lotteryInfo;
    private LotteryOpenHistory firstOpenHistory;
    private ViewGroup vgTextHistory;
    private LinearLayout lvOpenCode;
    private ImageViewScrollView scrollView;
    private PlaySnakeView snackView;
    private PlayFootView footView;
    private TextView tvDeadLine;
    private TextView tvIssueDeadLine;
    private TextView tvIssueDate;
    private TextView[] tvOpenBallNum = new TextView[10];
    private TextView tvCounter;
    private ImageView ivLotteryLogo;
    private ImageView ivJump;

    private ImageView ivOpenning;
    private View layoutOpenCode;
    private int defaultSound = 0;


    private FragLotteryPlayContainer fragContainer;
    private ViewGroup vgBottomGroup;
    private TextView tvAddNum;
    private TextView tvShoppingCar;
    private TextView tvBuy;
    private ImageView ivHeadUp;

    private int lotteryId;
    private LotteryKind lotteryKind;
    private LotteryPlay selectedLotteryPlay;
    private LotteryPlayUserInfo userPlayInfo;
    private ArrayList<HashMap<String, Object>> listNumAddedCart;

    private String category = "";
    private LotteryOpenNext nextIssue;
    private long refreshMark = 0;
    private long refreshHistoryMark;
    private Disposable mDisposable;

    private FragBetLottery.OnLotteryMethodChanged onLotteryMethodChanged;
    private FragBetLottery.OnLotteryKindChanged onLotteryKindChanged;

    private static final int HIDE_THRESHOLD = 20; //移动多少距离后显示隐藏
    private int scrolledDistance = 0; //移动的中距离
    private boolean controlsVisible = true; //显示或隐藏
    private boolean showRecentOpenList = false;

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_main;
    }

    private boolean isShow = true;
    float tvTipy = 0;
    float stoptvTipy = 0;

    @Override
    public void init(View root) {
        container = ((RelativeLayout) root.findViewById(R.id.container));
        awardPool = ((AwardpoolViewModule) root.findViewById(R.id.award));
        ivcoins = ((ImageView) root.findViewById(R.id.ivcoins));
        lotteryKind = (LotteryKind) CtxLotteryTouCai.getIns().findLotteryKind(lotteryId);
        category = lotteryKind.getPlayCategory();
        listNumAddedCart = new ArrayList<>();
        lvOpenCode = root.findViewById(R.id.lv_open_code);
        scrollView = root.findViewById(R.id.sv_scroller);
        snackView = root.findViewById(R.id.vSnakeBar);
        footView = root.findViewById(R.id.vFootView);
        tvjixuan = root.findViewById(R.id.tvjixuan);
        tvjixuan.setOnClickListener(this::onClick);
        footView.setSnakeView(snackView);
        tvDeadLine = root.findViewById(R.id.tv_deadline);
        tvCounter = root.findViewById(R.id.tv_order_count);
        ivLotteryLogo = root.findViewById(R.id.iv_lottery_logo);
        root.findViewById(R.id.tvClear).setOnClickListener(this);
        ivOpenning = root.findViewById(R.id.iv_openning);
        layoutOpenCode = root.findViewById(R.id.layout_open_code);
        ivJump = root.findViewById(R.id.iv_jump);
        goOfficeOpenSite = root.findViewById(R.id.goOfficeOpenSite);

        mRcRecentLotteryOpen = root.findViewById(R.id.rc_recent_lottery_open);
        mTvRecentLotteryOpen = root.findViewById(R.id.tv_recent_lottery_open);
        mTvRecentLotteryOpen.setOnClickListener(this);
        //处理北京pk10开奖动画
        kaijiangWebview = ((WebView) root.findViewById(R.id.kaijiangWebview));
        kaijiangAnimate = root.findViewById(R.id.kaijiangAnimate);
        tvTip = root.findViewById(R.id.tvTip);
        if (CommonConsts.setBetNeedMvConfig(lotteryKind.getCode(), false)) {
            kaijiangAnimate.setVisibility(View.VISIBLE);
            //kaijiangWebview.setVisibility(View.VISIBLE);
            WebSettings settings = kaijiangWebview.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDisplayZoomControls(false);
            settings.setSupportZoom(false);
            settings.setDomStorageEnabled(true);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
            settings.setUserAgent(AbHttpAO.getUserAgent());
            loadUrl(lotteryKind.getId());

            kaijiangWebview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    ActWebX5.syncCookieWithHttp(url);
                    if (url != null) {
                        view.loadUrl(url);
                    }
                    return true;
                }
            });

            tvTipy = tvTip.getY();
            scrollView.setOnScrollListener(new ImageViewScrollView.OnScrollListener() {
                @Override
                public void onScroll(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    //上滑 并且 正在显示底部栏
                    stoptvTipy = tvTip.getY();
                }

                @Override
                public void onDownScroll() {//下拉
                    int scrollY = scrollView.getScrollY();
                    if (scrollY == 0 && isShow) {
                        isShow = false;
                        kaijiangWebview.setVisibility(View.VISIBLE);
                        tvTipy = tvTip.getY();
                        tvTip.setText("上拉隐藏");
                        //kaijiangWebview.animate().translationY(kaijiangWebview.getHeight());
                    }
                }

                @Override
                public void onUpScroll() {
                    if (Math.abs(stoptvTipy - tvTipy) > 50 && !isShow) {
                        isShow = true;
                        kaijiangWebview.setVisibility(View.GONE);
                        tvTip.setText("下拉展示开奖动画");
                        //kaijiangWebview.animate().translationY(0);
                    }
                }
            });


        } else {
            kaijiangWebview.setVisibility(View.GONE);
            kaijiangAnimate.setVisibility(View.GONE);
        }

        // load lottery logo
        Glide.with(this).load(R.mipmap.openning).asGif().into(ivOpenning);
        //ivOpenning.setImageResource(R.drawable.lottery_play_kaijiang_animate);
        //mIvOpenningDrawableAnimate = ((AnimationDrawable) ivOpenning.getDrawable());
        Glide.with(this).load("file:///android_asset/lottery_kind/logo/" + lotteryId + ".png").into(ivLotteryLogo);

        LotteryJump j = UserManagerTouCai.getIns().getJumpInfo().get(String.valueOf(lotteryId));

        if (j == null)
            ivJump.setVisibility(View.INVISIBLE);

        ivJump.setImageResource(R.mipmap.ic_lottery_play_jump_right);
        ivJump.setOnClickListener(v -> {
            onLotteryKindChanged.onLotteryKindChanged(j.getId());
        });

        //初始化发布
        tvIssueDeadLine = (TextView) root.findViewById(R.id.tvIssueDeadLine);
        tvIssueDate = root.findViewById(R.id.tv_issue_title);
        tvOpenBallNum[0] = root.findViewById(R.id.tv_num1);
        tvOpenBallNum[1] = root.findViewById(R.id.tv_num2);
        tvOpenBallNum[2] = root.findViewById(R.id.tv_num3);
        tvOpenBallNum[3] = root.findViewById(R.id.tv_num4);
        tvOpenBallNum[4] = root.findViewById(R.id.tv_num5);
        tvOpenBallNum[5] = root.findViewById(R.id.tv_num6);
        tvOpenBallNum[6] = root.findViewById(R.id.tv_num7);
        tvOpenBallNum[7] = root.findViewById(R.id.tv_num8);
        tvOpenBallNum[8] = root.findViewById(R.id.tv_num9);
        tvOpenBallNum[9] = root.findViewById(R.id.tv_num10);

        String string = String.format("<font color=#ff2b65>%s</font>", Nums.formatDecimal(UserManagerTouCai.getIns().getLotteryAvailableBalance(), 3)) + "元";
        snackView.setAccountAmount(string);
        initLotteryOpen();

        if (CommonConsts.needOpenLotterySite(lotteryId)) {
            goOfficeOpenSite.setVisibility(View.VISIBLE);
            goOfficeOpenSite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActWebX5.launch(context, "开奖官网",
                            CommonConsts.setOpenLotterySiteConfig(lotteryId), true);
                }
            });
        } else {
            goOfficeOpenSite.setVisibility(View.GONE);
        }

        //初始化底部菜单
        vgBottomGroup = (ViewGroup) root.findViewById(R.id.vgBottomGroup);
        tvAddNum = (TextView) root.findViewById(R.id.tvAddNum);
        tvAddNum.setOnClickListener(this);
        tvShoppingCar = root.findViewById(R.id.tvShoppingCart);
        tvShoppingCar.setOnClickListener(this);
        //controller控制直接生成

        //controller控制直接购买
        tvBuy = (TextView) root.findViewById(R.id.tvBuy);

        //直接初始化container
        fragContainer = (FragLotteryPlayContainer) FragmentsHelper.replaceFragments(getFragmentManager(), R.id.vgContent, FragLotteryPlayContainer.newIns(lotteryId, category, this));

        EventBus.getDefault().register(this);

        setDefaultPlay();
        //初始化彩票用户数据
        initLotteryPlayInfo();

        //initAwardPool();

        //更新近期开奖数据
        mRcRecentLotteryOpen.setLayoutManager(new LinearLayoutManager(context));
        mRcRecentOpenAdapter = new BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder>(R.layout.item_recent_lottery_opens_layout) {

            @Override
            protected void convert(BaseViewHolder helper, LotteryOpenHistory item) {
                helper.setText(R.id.issue_no, item.getIssue());
                if (Strs.isEqual("SSC", category)) {
                    helper.setText(R.id.tv_xingtai, Strs.isEmpty(item.getCode()) ? "开奖中..." : CodeXingtaiUtil.getXingtai(item.getCode()));
                    helper.setVisible(R.id.tv_xingtai, true);
                }

                FlowLayout flowLayout = (FlowLayout) helper.getView(R.id.llBallsBlue);
                flowLayout.removeAllViews();

                if (Strs.isEmpty(item.getCode())) {
                    helper.setVisible(R.id.loading, true);
                    helper.setText(R.id.loading, "\t\t\t\t\t\t\t等待开奖...⏳ ");
                    flowLayout.setVisibility(View.GONE);
                    return;
                }

                helper.setVisible(R.id.loading, false);
                helper.setVisible(R.id.llBallsBlue, true);

                //开奖号球
                String[] arrCode = item.getCode().split(",");
                TextView tvNum = null;
                int length = arrCode.length;
                for (int i = 0; i < length; i++) {
                    if (Strs.isEqual("PK10", lotteryKind.getPlayCategory())) {
                        tvNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_small_2, flowLayout, false);

                        GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                        if (i == 0) {
                            bg.setColor(context.getResources().getColor(R.color.orange_f98f36));
                        } else if (i == 1) {
                            bg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                        } else if (i == 2) {
                            bg.setColor(context.getResources().getColor(R.color.orange_ED5735));
                        } else {
                            bg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                        }
                    } else {
                        tvNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_small_3, flowLayout, false);

                        GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                        bg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                    }

                    tvNum.setText(arrCode[i]);
                    flowLayout.addView(tvNum);
                }
                flowLayout.requestLayout();
            }
        };
        mRcRecentLotteryOpen.setAdapter(mRcRecentOpenAdapter);


        ((ActLotteryMain) context).mChangwanTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectMethod = (String) tab.getTag();
                if (Strs.isEmpty(selectMethod)) {
                    return;
                }
                fragContainer.showPlay(category, selectMethod);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (!((ActLotteryMain) context).useNewBetUIConfig()) {
            ((ActLotteryMain) context).tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentToActPlayMethod();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        visible = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMissionList();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (kaijiangWebview != null) {
            kaijiangWebview.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        visible = false;
    }

    private LotteryPlayUIConfig getCurrentUiConfig() {
        return fragContainer.getCurrentPlayFrag().getUiConfig();
    }

    private void initLotteryOpen() {
        HttpActionTouCai.getLotteryOepnTime(this, lotteryKind.getCode(), new AbHttpResult() {
            @Override
            public boolean onGetString(String str) {
                nextIssue = new Gson().fromJson(str, LotteryOpenNext.class);
                if (nextIssue.getStopTime() == null || nextIssue.getIssue() == null ||
                        Dates.getDateByFormat(nextIssue.getStopTime(), "yyyy-MM-dd HH:mm:ss").getTime() <= System.currentTimeMillis()) {
                    //正在开奖, 系统延迟, 继续刷新
                    nextIssue = null;
                    return true;
                }
                String issue = nextIssue.getIssue();
                String issuno = issue.contains("-") ? issue.split("-")[1] : issue;
                String latest = String.format("NO.%s 投注截止时间", issuno, Dates.dateDiffHHMMSS(nextIssue.getSurplusTime()));
                tvIssueDeadLine.setText(Html.fromHtml(latest));
                tvDeadLine.setText(Dates.dateDiffHHMMSS(nextIssue.getSurplusTime()));
                return true;
            }
        });

        updateStaticOpen();

        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
        mDisposable = Flowable.interval(1, TimeUnit.SECONDS)
                .onBackpressureLatest()
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) {
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (nextIssue != null) {
                            String issue = nextIssue.getIssue();
                            String issuno = issue.contains("-") ? issue.split("-")[1] : issue;
                            if (nextIssue.getSurplusTime() - aLong >= 0) {
                                String latest = String.format("NO.%s 投注截止时间", issuno);
                                tvIssueDeadLine.setText(latest);

                                String date = Dates.dateDiffHHMMSS(nextIssue.getSurplusTime() - aLong);
                                SpannableStringBuilder style = new SpannableStringBuilder(date);

                                style.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.white)),
                                        0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                style.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.white)), 5, 7, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                style.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.white)), 10, 12, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                tvDeadLine.setText(style);

                            } else if (nextIssue.getSurplusTime() - aLong < 0) {
                                String latest = String.format("NO.%s", issue);
                                tvIssueDeadLine.setText(latest);
                                tvDeadLine.setText("正在开奖");
                                nextIssue = null;  //截止日期后定时刷新
                                firstOpenHistory = null;
                                refreshHistoryMark = 0;
                                refreshMark = 0;
                                Toasts.show(getActivity(), issue + "期已截止，投注时请注意期号！", true);
                            }
                        } else {
                            //无值则一直刷新
                            refreshMark++;
                            if (refreshMark > REFRESH_INTERAL) {
                                initLotteryOpen();
                                refreshMoney();
                                refreshMark = 0;
                            }
                        }


                        if (firstOpenHistory == null) {
                            //无值则一直刷新
                            refreshHistoryMark++;
                            if (refreshHistoryMark > REFRESH_INTERAL) {
                                initLotteryOpen();
                                refreshMoney();
                                refreshHistoryMark = 0;
                            }
                        }
                    }
                });
    }

    private void refreshMoney() {
        UserManager.getIns().initUserDataNew(new UserManager.IUserDataSyncCallback() {

            @Override
            public void onUserDataInited() {

            }

            @Override
            public void afterUserDataInited() {
                String money = Utils.getDoubleNotRound2Str(UserManagerTouCai.getIns().getLotteryAvailableBalance());
                if (fragContainer.getCurrentPlayFrag() != null && fragContainer.getCurrentPlayFrag().getHead() != null) {
                    fragContainer.getCurrentPlayFrag().getHead().setMoney(money);
                }
                String string = String.format("<font color=#ff2b65>%s</font>", Nums.formatDecimal(money, 3)) + "元";
                snackView.setAccountAmount(string);
            }

            @Override
            public void onUserDataInitFaild() {

            }

            @Override
            public void onAfter() {
            }
        });
    }

    private void updateStaticOpen() {
        HttpActionTouCai.getLotteryOpenCodeHistory(this, lotteryKind.getCode(), true, new AbHttpResult() {
            @Override
            public boolean onGetString(String str) {
                try {
                    ArrayList<LotteryOpenHistory> listOpen = new Gson().fromJson(str, new TypeToken<ArrayList<LotteryOpenHistory>>() {
                    }.getType());
                    initOpenCodeCard(listOpen);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        });
    }

    private void initOpenCodeCard(ArrayList<LotteryOpenHistory> listOpen) {

        if (listOpen != null && listOpen.size() > 0) {
            String issue = listOpen.get(0).getIssue();
            String code = listOpen.get(0).getCode();
//            String issuno = issue.contains("-") ? issue.split("-")[1] : issue;
            tvIssueDate.setText(Html.fromHtml(String.format("NO.<font color=\"#02A8F3\">%s</font>开奖结果", issue)));

            if (Strs.isNotEmpty(code)) {
                Log.d("FragLotteryPlayMain", "issue: " + issue + "----code:" + code);
                EventBus.getDefault().post(new LotteryCodeUpdateBean());
            }
            if (mRcRecentOpenAdapter != null) {
                mRcRecentOpenAdapter.setNewData(listOpen.size() >= 8 ? listOpen.subList(0, 8) : listOpen);
            }

        } else {
            tvIssueDate.setText("");
        }

        updateOpenCode(listOpen.size() > 0 ? listOpen.get(0) : null);
    }

    String[] codes;

    private void updateOpenCode(LotteryOpenHistory history) {
        int count = 6;
        if (history != null && history.getCode() != null) {
            String[] arr = history.getCode().split(",");
            codes = new String[arr.length];
            count = Math.min(arr.length, 10);
            for (int i = 0; i < count; i++) {
                if (arr.length > i && !Strs.isEmpty(arr[i])) {
                    codes[i] = arr[i];
                }
            }
            ivOpenning.setVisibility(View.INVISIBLE);
            //mIvOpenningDrawableAnimate.stop();
            layoutOpenCode.setVisibility(View.VISIBLE);
        } else {
            ivOpenning.setVisibility(View.VISIBLE);
            //mIvOpenningDrawableAnimate.start();
            layoutOpenCode.setVisibility(View.INVISIBLE);
            return;
        }

        for (int i = 0; i < 10; i++) {
            if (i > count - 1) {
                tvOpenBallNum[i].setVisibility(View.GONE);
            } else {
                tvOpenBallNum[i].setVisibility(View.VISIBLE);
                tvOpenBallNum[i].setText(codes[i]);
            }
        }

        if (count > 7) {
            for (int i = 0; i < 10; i++) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvOpenBallNum[i].getLayoutParams();
                params.width = Views.dp2px(20);
                params.height = Views.dp2px(20);
                params.leftMargin = Views.dp2px(2);
                params.rightMargin = Views.dp2px(2);
                tvOpenBallNum[i].setLayoutParams(params);
                tvOpenBallNum[i].setTextSize(12);
            }
        } else {
            for (int i = 0; i < 10; i++) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvOpenBallNum[i].getLayoutParams();
                params.width = Views.dp2px(27.5f);
                params.height = Views.dp2px(27.5f);
                params.leftMargin = Views.dp2px(4);
                params.rightMargin = Views.dp2px(4);
                tvOpenBallNum[i].setLayoutParams(params);
                tvOpenBallNum[i].setTextSize(15);
            }
        }

        layoutOpenCode.requestLayout();
    }

    private void initLotteryPlayInfo() {
        HttpActionTouCai.getLotteryPlayInfo(this, lotteryKind.getId(), new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                /*slidingDrawer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showLoading();
                    }
                }, 100);*/

            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", LotteryPlayUserInfo.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0) {
                    userPlayInfo = getFieldObject(extra, "data", LotteryPlayUserInfo.class);
                    setupTab(true);
                } else {
                    Toasts.show(getActivity(), Strs.isEmpty(msg) ? "获取数据失败!" : msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvAddNum:
                if (userPlayInfo == null) {
                    return;
                }

                if (fragContainer == null) {
                    return;
                }

                if (fragContainer.getCurrentPlayFrag() == null) {
                    return;
                }

                if (fragContainer.getCurrentPlayFrag().getCtrlPlay() == null) {
                    return;
                }

                if (!fragContainer.getCurrentPlayFrag().getCtrlPlay().addOrder(listNumAddedCart, this)) {
                    return;
                }

                tvCounter.setVisibility(View.VISIBLE);
                tvCounter.setText(Strs.of(listNumAddedCart.size()));

                Toasts.show(getActivity(), "添加号码成功!", true);
                resetCurrentPlay();
                break;
            case R.id.tvShoppingCart:
                if (listNumAddedCart.size() == 0) {
                    DialogsTouCai.showDialog(getActivity(), "温馨提示", "请先选择号码", view -> {
                    });
                    return;
                }

                ActLotteryCartTouCai.launch(getActivity(), lotteryId, listNumAddedCart);
                break;

            case R.id.tvClear:
                fragContainer.getCurrentPlayFrag().getCtrlPlay().reset();
                break;
            case R.id.tv_recent_lottery_open:
                if (showRecentOpenList) {
                    mRcRecentLotteryOpen.setVisibility(View.GONE);
                    showRecentOpenList = false;
                } else {
                    mRcRecentLotteryOpen.setVisibility(View.VISIBLE);
                    showRecentOpenList = true;
                }

                break;
            case R.id.tvjixuan:
                if (fragContainer == null) {
                    return;
                }
                if (fragContainer.getCurrentPlayFrag() == null) {
                    return;
                }
                if (fragContainer.getCurrentPlayFrag().getCtrlPlay() == null) {
                    return;
                }
                fragContainer.getCurrentPlayFrag().getCtrlPlay().onAutoGenerate();
                break;
            default:

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteFromCartEvent(DeleteFromCartEvent event) {
        if (event.isAllDelete) {
            listNumAddedCart.clear();
        } else {
            ArrayList<HashMap<String, Object>> removeList = new ArrayList<>();
            for (int i = 0; i < event.arrDeleteIndex.length; i++) {
                removeList.add(listNumAddedCart.get(event.arrDeleteIndex[i]));
            }
            listNumAddedCart.removeAll(removeList);
        }

        if (listNumAddedCart.size() == 0)
            tvCounter.setVisibility(View.INVISIBLE);
        else
            tvCounter.setVisibility(View.VISIBLE);

        tvCounter.setText(Strs.of(listNumAddedCart.size()));
    }

    CountDownTimer countDownTimer;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBuyedInCartEvent(BuyedLotteryEvent event) {
        /** 倒计时60秒，一次1秒 */
        if (awardPool != null && awardPool.getVisibility() == View.VISIBLE) {
            countDownTimer = new CountDownTimer(1600, 160) {
                @Override
                public void onTick(long millisUntilFinished) {
                    addGoodToCar(ivcoins);
                }

                @Override
                public void onFinish() {
                    getMissionList();
                }
            }.start();
        }
    }

    @Override
    public long getCurrentHit() {
        return snackView.getHitCount();
    }

    @Override
    public void setCurrentUserPlayInfo(Object userPlayInfo) {
        //无动态刷新
    }

    @Override
    public LotteryPlayUserInfo getLotteryPlayUserInfo() {
        return userPlayInfo;
    }

    public void resetCurrentPlay() {
        snackView.clearAll();
        footView.reset();
        fragContainer.getCurrentPlayFrag().getCtrlPlay().reset();
    }

    @Override
    public BaseLotteryPlayFragment getCurrentPlay() {
        return fragContainer.getCurrentPlayFrag();
    }

    @Override
    public String getCurrentModel() {
        return footView.getModel();
    }

    public int getCurrentNumTimes() {
        return footView.getNumTimes();
    }

    public double getAward() {
        return getSnakeBar().getRewards();
    }

    @Override
    public int getCurrentPoint() {
        return footView.getCurrentPoint();
    }

    @Override
    public boolean hasCurrentRequestText() {
        return Strs.isNotEmpty(getCurrentRequestText());
    }

    @Override
    public String getCurrentRequestText() {
        return fragContainer.getCurrentPlayFrag().getCtrlPlay().getRequestText();
    }

    @Override
    public String getCurrentPlayId() {
        return fragContainer.getCurrentPlayFrag().getPlayId();
    }

    @Override
    public String getCurrentPlayShortName() {
        return fragContainer.getCurrentPlayFrag().getPlayShortName();
    }

    @Override
    public String getCurrentPlayFullName() {
        return fragContainer.getCurrentPlayFrag().getPlayFullName();
    }

    @Override
    public LotteryOpenNext getNextIssue() {
        return nextIssue;
    }

    @Override
    public void setDefaultMenu() {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                fragContainer.getPlayContainer().setVisibility(View.GONE);
            }
        });
    }

    String defaultPlayCode = "";

    @Override
    public void setDefaultPlay() {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                defaultPlayCode = "";
                if ("SSC".equals(category)) {
                    defaultPlayCode = "sxzhixfsh";
                } else if ("11X5".equals(category)) {
                    defaultPlayCode = "sanmzhixfsq";
                } else if ("K3".equals(category)) {
                    defaultPlayCode = "ebthdx";
                } else if ("PK10".equals(category)) {
                    defaultPlayCode = "qianyi";
                } else if ("3DFC".equals(category)) {
                    defaultPlayCode = "sanxzhixfs";
                } else if ("pl3".equals(category)) {
                    defaultPlayCode = "sxzhixfsh";
                }

//                List<String> list = UserManagerTouCai.getIns().getlastLotteryPlayNew(lotteryId);
//                if (list != null) {
//                    defaultPlayCode = list.get(0);
//                }
//
//                selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlay(category, defaultPlayCode);
//                fragContainer.showPlay(category, defaultPlayCode);
//
//                if (selectedLotteryPlay != null) {//context
//                    ((ActLotteryMain) context).setHeadPlaysTabLayout(list);
//                }
//                fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showLoading() {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                DialogsTouCai.showProgressDialog(getActivity(), "");
            }
        });

    }

    @Override
    public void hideLoading() {
        DialogsTouCai.hideProgressDialog(getActivity());
    }

    @Override
    public void showMenu() {

        if (userPlayInfo != null)
            FragLotteryPlayMenu.newIns((AppCompatActivity) getActivity(), category, lotteryId, selectedLotteryPlay.lotteryCode, userPlayInfo, this);
    }

    @Override
    public void hideMenu() {
    }

    @Override
    public NestedScrollView getScrollView() {
        return scrollView;
    }

    @Override
    public PlaySnakeView getSnakeBar() {
        return snackView;
    }

    @Override
    public PlayFootView getFootView() {
        return footView;
    }

    @Override
    public void showPlay() {
        showLoading();
        fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
        ThreadCollector.getIns().postDelayOnUIThread(400, new Runnable() {
            @Override
            public void run() {
                getSnakeBar().setVisibility(View.VISIBLE);
                hideLoading();
                scrollView.scrollTo(0, 0);
            }
        });
    }

    @Override
    public void hidePlay() {
        fragContainer.getPlayContainer().setVisibility(View.GONE);
    }

    @Override
    public void showPlayAndHideLotteryMenu(final String category, final String selectedPlayCode) {
        footView.reset();
        snackView.clearAll();
        DialogsTouCai.showProgressDialog(getActivity(), "");

        //fragContainer.removeCurrentPlay();

        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlay(category, selectedPlayCode);
                //AbDebug.log(AbDebug.TAG_APP, "selectedLotteryPlay" );
                fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
                Log.d("FragLotteryPlayMain", "category:" + category + "------selectedPlayCode:" + selectedPlayCode);
                fragContainer.showPlay(category, selectedPlayCode);
                //AbDebug.log(AbDebug.TAG_APP, "showPlay" );

                if (userPlayInfo != null)
                    footView.setLotteryInfo(selectedLotteryPlay, userPlayInfo);

                onLotteryMethodChanged.onLotteryMethodChanged(lotteryKind, selectedPlayCode, selectedLotteryPlay.showName);
            }
        });

        ThreadCollector.getIns().postDelayOnUIThread(300, new Runnable() {
            @Override
            public void run() {
                hideMenu();
            }
        });

        ThreadCollector.getIns().postDelayOnUIThread(500, new Runnable() {
            @Override
            public void run() {
                hideLoading();

                getSnakeBar().setVisibility(View.VISIBLE);
                //AbDebug.log(AbDebug.TAG_APP, "hideLoading" );
                scrollView.scrollTo(0, 0);
                //AbDebug.log(AbDebug.TAG_APP, "scrollTo" );
            }
        });
    }

    public int getLotteryId() {
        return lotteryId;
    }

    @Override
    public ViewGroup getBottomGroup() {
        return vgBottomGroup;
    }

    @Override
    public EditText getMoneyInput() {
        return null;
    }

    public TextView getBottomAddNumBtn() {
        return tvAddNum;
    }

    @Override
    public TextView getBottomBuyBtn() {
        return tvBuy;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }

        if (kaijiangWebview != null) {
            kaijiangWebview.destroy();
        }

        EventBus.getDefault().unregister(this);

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void loadUrl(int gamecodeID) {
        ActWebX5.syncCookieWithHttp(getUrl(gamecodeID));
        if (Strs.isNotEmpty(getUrl(gamecodeID))) {
            kaijiangWebview.loadUrl(getUrl(gamecodeID).startsWith("http") ? getUrl(gamecodeID) : "http://" + getUrl(gamecodeID));
        }

    }

    private String getUrl(int gamecodeID) {
        return Consitances.PK10_ANIMATE + gamecodeID;
    }


    private PathMeasure mPathMeasure;
    private float[] mCurrentPosition = new float[2];

    private void addGoodToCar(ImageView imageView) {
        final ImageView view = new ImageView(context);
        view.setImageResource(R.mipmap.coins);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        container.addView(view, layoutParams);

        //二、计算动画开始/结束点的坐标的准备工作
        //得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
        int[] parentLoc = new int[2];
        container.getLocationInWindow(parentLoc);

        //得到商品图片的坐标（用于计算动画开始的坐标）
        int startLoc[] = new int[2];
        imageView.getLocationInWindow(startLoc);

        //得到购物车图片的坐标(用于计算动画结束后的坐标)
        int endLoc[] = new int[2];
        awardPool.getLocationInWindow(endLoc);

        float startX = startLoc[0] - parentLoc[0] + imageView.getWidth() / 2;
        float startY = startLoc[1] - parentLoc[1];

        //商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
        float toX = endLoc[0] - parentLoc[0] + awardPool.getWidth() / 2;
        float toY = endLoc[1] - parentLoc[1] + awardPool.getHeight() / 2;

        //开始绘制贝塞尔曲线
        Path path = new Path();
        path.moveTo(startX, startY);
        //使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY / 2, toX, toY);
        //path.lineTo((startX + toX) / 2, startY, toX, toY);
        mPathMeasure = new PathMeasure(path, false);

        //属性动画
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mPathMeasure.getPosTan(value, mCurrentPosition, null);
                view.setTranslationX(mCurrentPosition[0]);
                view.setTranslationY(mCurrentPosition[1]);
            }
        });

        valueAnimator.start();

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 把移动的图片imageview从父布局里移除
                container.removeView(view);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void initAwardPool() {
        awardPool.setiAwardPoolChoujiang(new AwardpoolViewModule.IAwardPoolChoujiangListner() {
            @Override
            public void onAwardPoolChoujiangClick() {
                if (UserManager.getIns().isLogined()) {
                    getBonusPoolDoChoujiang();
                } else {
                    UserManager.getIns().redirectToLogin();
                }
            }

            @Override
            public void onAwardPoolNoCountClick(int taketype, double nextAmountSub) {
                if (UserManager.getIns().isLogined()) {
                    DialogsTouCai.stopLaohujilaohuji(context, taketype, nextAmountSub, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (taketype) {
                                case 0:
                                    ActDeposit.launch(context);
                                    break;
                                case 1:
                                    //CtxLotteryTouCai.launchLotteryPlay(context, LotteryKind.find("菲律宾2分彩").getId());
                                    break;
                            }
                        }
                    });
                } else {
                    UserManager.getIns().redirectToLogin();
                }
            }
        });
    }

    AwardPoolDetailoMode mPoolDetailo;

    private void getMissionList() {

        BonusPoolActivityHelper.getMissionList(new BonusPoolActivityHelper.IBonusPoolCallback() {
            @Override
            public void onBonusPoolIDExist(List<NewUserMission> listMission) {
                if (listMission != null && listMission.size() > 0) {
                } else {
                    awardPool.setVisibility(View.GONE);
                    EventBus.getDefault().post(new BonusPoolFreshEventMode(false));
                }
            }

            @Override
            public void onGetBonusPoolConfig(AwardPoolDetailoMode poolDetail) {
                mPoolDetailo = poolDetail;
                if (Strs.isEqual("-1", poolDetail.getActivityIssueNo())) {
                    awardPool.setVisibility(View.GONE);
                    EventBus.getDefault().post(new BonusPoolFreshEventMode(false));
                } else {
                    awardPool.setVisibility(View.VISIBLE);
                    awardPool.setAwardPool(poolDetail.getPoolBonus());
                    awardPool.setDayTime(poolDetail.getTimeSub());
                    awardPool.setAwardCount(poolDetail.getTakeCount());
                    awardPool.setAwardPoolTakeType(poolDetail.getTakeType());
                    awardPool.setNextAmountSub(poolDetail.getNextAmountSub());
                }
            }

            @Override
            public void onGetBonusPoolConfigFaild() {
                EventBus.getDefault().post(new BonusPoolFreshEventMode(false));
                awardPool.setVisibility(View.GONE);
            }
        });
    }

    private void getBonusPoolDoChoujiang() {
        BonusPoolActivityHelper.getBonusPoolDoChoujiang(((ActLotteryMain) context), new BonusPoolActivityHelper.IBonusPoolChoujiangCallback() {
            @Override
            public void onBonusPoolChoujiangOver() {
                getMissionList();
            }
        }, mPoolDetailo);
    }

    //定义处理接收的方法,处理大奖推送的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bonusPoolEventBus(BonusPoolEventMode bonusPoolEventMode) {
        getMissionList();
    }

    private boolean canSubmitOrder() {
        return fragContainer.getCurrentPlayFrag().getCtrlPlay().canSubmitOrder();
    }


    private List<LotteryPlay> listLotteryPlay = new ArrayList<>();
    private ArrayList<LotteryPlayCategory> listPlayCategory = new ArrayList<>();

    /**
     * @param isChangeTab 是否需要改tab
     */
    private void setupTab(boolean isChangeTab) {
        //tabLayout.removeAllTabs();
        listLotteryPlay.clear();
        listPlayCategory.clear();
        List<LotteryPlayCategory> localCategories = UserManager.getIns().getLotteryPlayCategoryListMap().get(category);
        for (LotteryPlayCategory category : localCategories) {
            ArrayList<LotteryPlay> reList = new ArrayList<>();
            LotteryPlayCategory item = null;
            for (LotteryPlay bean : category.getData()) {
                //  LotteryPlayUserInfo.MathodBean methodBean = userPlayInfo.getMethod().get(bean.lotteryCode);
                LotteryPlayUserInfo.MathodBean methodBean = CtxLottery.getMethodBean(category.getCategoryCode(), category.getTitleName(), bean.lotteryCode, userPlayInfo);

                if (methodBean != null)/*过滤掉已关闭的玩法*/
                    reList.add(bean);
                if (reList.size() > 0) {
                    item = new LotteryPlayCategory();
                    item.setData(reList);
                    item.setTitleName(category.getTitleName());
                    item.setCategoryCode(category.getCategoryCode());
                }
            }
            if (item != null)
                listPlayCategory.add(item);
        }
        List<String> playMethods = UserManagerTouCai.getIns().getUserPlayedLotterys();
        for (LotteryPlayCategory playCategory : listPlayCategory) {
            for (LotteryPlay lotteryPlay : playCategory.getData()) {
                lotteryPlay.isEnable = true;
                if (!playMethods.isEmpty()) {
                    for (String val : playMethods) {
                        if ((lotteryPlay.category + "#" + lotteryPlay.lotteryCode).equals(val)) {
                            listLotteryPlay.add(lotteryPlay);
                            lotteryPlay.isEnable = false;
                        }
                    }
                }
            }
        }

        //处理用户没有选择的时候，默认玩法取 后台开启的第一个玩法作为默认玩法
        boolean isFirstDdefaultPlayID = false;//标记第一个开启的玩法作为默认玩法
        for (LotteryPlayCategory playCategory : listPlayCategory) {
            for (LotteryPlay lotteryPlay : playCategory.getData()) {
                if (lotteryPlay != null && listLotteryPlay.isEmpty() && !isFirstDdefaultPlayID) {
                    ((ActLotteryMain) context).setHeadPlaysTabLayoutNew(lotteryPlay);
                    currentPlayCode = lotteryPlay.getPlayId();
                    isFirstDdefaultPlayID = true;
                    Log.d("FragmentBetLottery", lotteryPlay.getPlayId());
                    break;
                }

            }

            if (isFirstDdefaultPlayID) {
                break;
            }
        }

        if (!listLotteryPlay.isEmpty()) {
            ((ActLotteryMain) context).setHeadPlaysTabLayoutNew(listLotteryPlay);
            currentPlayCode = listLotteryPlay.get(0).getPlayId();
        }

        selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlay(category, currentPlayCode);

        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                footView.setLotteryInfo(selectedLotteryPlay, userPlayInfo);
                fragContainer.setUserPlayInfo(userPlayInfo);
                //fragContainer.showPlay(category, defaultPlayCode);
                if (isChangeTab) {
                    fragContainer.showPlay(category, currentPlayCode);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 111)
            setupTab(false);
    }

    public void intentToActPlayMethod() {
        Intent intent = new Intent(context, ActPlayMethod.class);
        intent.putExtra("listPlayCategory", listPlayCategory);
        intent.putExtra("playInfo", userPlayInfo);
        startActivityForResult(intent, 0);
    }

}
