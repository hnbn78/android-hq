package com.desheng.app.toucai.panel;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.Global;
import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Dates;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.helper.BonusPoolActivityHelper;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.AwardPoolDetailoMode;
import com.desheng.app.toucai.model.LotteryJump;
import com.desheng.app.toucai.model.LotteryOpenNext;
import com.desheng.app.toucai.model.NewUserMission;
import com.desheng.app.toucai.model.eventmode.BonusPoolEventMode;
import com.desheng.app.toucai.model.eventmode.BonusPoolFreshEventMode;
import com.desheng.app.toucai.util.CodeXingtaiUtil;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.AwardpoolViewModule;
import com.desheng.app.toucai.view.ImageViewScrollView;
import com.desheng.app.toucai.view.PlaySnakeViewJD;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.event.DeleteFromCartEvent;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryOpenHistory;
import com.desheng.base.model.LotteryPlayJD;
import com.desheng.base.model.LotteryPlayUserInfoJD;
import com.desheng.base.panel.ActWebX5;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noober.background.view.BLLinearLayout;
import com.noober.background.view.BLTextView;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.act.util.FragmentsHelper;
import com.shark.tc.R;
import com.tencent.smtt.sdk.CookieManager;
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
import okhttp3.HttpUrl;
import okhttp3.Request;

public class FragLotteryPlayMainJD extends AbBaseFragment implements LotteryPlayPanelJD, View.OnClickListener {
    private AnimationDrawable mIvOpenningDrawableAnimate;
    private WebView kaijiangWebview;
    private LinearLayout kaijiangAnimate;
    private TextView tvTip;
    private RelativeLayout container;
    private AwardpoolViewModule awardPool;
    private ImageView ivcoins;
    private FrameLayout kaijiangHeadview;
    private RecyclerView recycleViewLotteryMethod;
    private RelativeLayout llWanfa;
    private View spacellWanfa;
    private BLLinearLayout lookLiveView;
    private TextView tvSeeLive;
    private TextView tv_wanfa;
    private BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder> mRcRecentOpenAdapter;

    public static Fragment newIns(int id, FragBetLottery.OnLotteryMethodChanged onLotteryMethodChanged, FragBetLottery.OnLotteryKindChanged onLotteryKindChanged) {
        FragLotteryPlayMainJD fragLotteryPlay = new FragLotteryPlayMainJD();
        fragLotteryPlay.lotteryId = id;
        fragLotteryPlay.onLotteryMethodChanged = onLotteryMethodChanged;
        fragLotteryPlay.onLotteryKindChanged = onLotteryKindChanged;
        return fragLotteryPlay;
    }

    public static final int REFRESH_INTERAL = 10;
    private ArrayList<LotteryPlayJD> listPlayJD;
    private LotteryKind lotteryKind;

    private FragLotteryPlayContainerJD fragContainer;
    private TextView tvIssueDeadLine;
    private ViewGroup vgBottomGroup;
    private ImageButton tvClear;
    private EditText etMoney;
    private TextView tvBuy;
    private LinearLayout lvOpenCode;
    private LotteryOpenHistory firstOpenHistory;
    private ImageViewScrollView scrollView;
    private TextView tvDeadLine;
    private TextView tvIssueDate;
    private TextView[] tvOpenBallNum = new TextView[10];
    private ImageView ivLotteryLogo;
    private PlaySnakeViewJD snackView;
    private ImageView ivJump;
    private ImageView ivOpenning;
    private View layoutOpenCode;

    private int lotteryId;
    private LotteryPlayJD selectedLotteryPlay;
    private LotteryPlayUserInfoJD userPlayInfo;
    private ArrayList<HashMap<String, Object>> listNumAddedCart;

    private String category = "";
    private LotteryOpenNext nextIssue;
    private long refreshMark = 0;
    private long refreshHistoryMark;
    private Disposable mDisposable;
    private RecyclerView mRcRecentLotteryOpen;
    private TextView mTvRecentLotteryOpen;

    private FragBetLottery.OnLotteryMethodChanged onLotteryMethodChanged;
    private FragBetLottery.OnLotteryKindChanged onLotteryKindChanged;

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_main_jd;
    }

    private boolean isShow = true;
    float tvTipy = 0;
    float stoptvTipy = 0;

    @Override
    public void init(View root) {

        container = ((RelativeLayout) root.findViewById(R.id.containerJd));
        awardPool = ((AwardpoolViewModule) root.findViewById(R.id.awardJd));
        ivcoins = ((ImageView) root.findViewById(R.id.ivcoinsJd));

        lotteryKind = (LotteryKind) LotteryKind.find(lotteryId);
        category = lotteryKind.getPlayCategory();
        listPlayJD = UserManagerTouCai.getIns().getLotteryPlayJDMap().get(category);
        listNumAddedCart = new ArrayList<>();
        lvOpenCode = root.findViewById(R.id.lv_open_code);
        scrollView = root.findViewById(R.id.sv_scroller);
        snackView = root.findViewById(R.id.vSnakeBar);
        //tvDeadLine = root.findViewById(R.id.tv_deadline);
        //ivLotteryLogo = root.findViewById(R.id.iv_lottery_logo);
        root.findViewById(R.id.tvClear).setOnClickListener(this);
        //root.findViewById(R.id.iv_jd_play_indicator).setVisibility(View.VISIBLE);
        //ivOpenning = root.findViewById(R.id.iv_openning);
        //layoutOpenCode = root.findViewById(R.id.layout_open_code);
        //ivJump = root.findViewById(R.id.iv_jump);
        kaijiangHeadview = root.findViewById(R.id.kaijiangHeadview);
        llWanfa = root.findViewById(R.id.llWanfa);
        spacellWanfa = root.findViewById(R.id.spacellWanfa);
        tv_wanfa = root.findViewById(R.id.tv_wanfa);
        tv_wanfa.setOnClickListener(this);
        recycleViewLotteryMethod = root.findViewById(R.id.recycleViewLotteryMethod);
        initkaijiangHeadview();

        //处理北京pk10开奖动画
        kaijiangWebview = root.findViewById(R.id.kaijiangWebview);
        kaijiangAnimate = root.findViewById(R.id.kaijiangAnimate);
        tvTip = root.findViewById(R.id.tvTip);
        if (CommonConsts.setBetNeedMvConfig(lotteryKind.getCode(), true)) {
            kaijiangAnimate.setVisibility(View.VISIBLE);
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

        ivJump.setImageResource(R.mipmap.ic_lottery_play_jump_left);
        ivJump.setOnClickListener(v -> {
            onLotteryKindChanged.onLotteryKindChanged(j.getId());
        });


        tvIssueDeadLine = (TextView) root.findViewById(R.id.tvIssueDeadLine);
        initLotteryOpen();
        //初始化底部菜单
        vgBottomGroup = (ViewGroup) root.findViewById(R.id.vgBottomGroup);
        //controller控制清除
        tvClear = root.findViewById(R.id.tvClear);
        //价钱
        etMoney = (EditText) root.findViewById(R.id.etMoney);
        etMoney.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String s = etMoney.getText().toString();
                if (Strs.parse(s, 0) <= 0) {
                    etMoney.setText("1");
                }
            }
        });

        //controller控制直接购买
        tvBuy = (TextView) root.findViewById(R.id.tvBuy);

        //直接初始化container
        fragContainer = (FragLotteryPlayContainerJD) FragmentsHelper.replaceFragments(getFragmentManager(), R.id.vgContent, FragLotteryPlayContainerJD.newIns(lotteryId, category, this));

        EventBus.getDefault().register(this);

        setDefaultPlay();
        //初始化彩票用户数据
        initLotteryPlayInfo();

        initAwardPool();

        //更新近期开奖数据
        if (mRcRecentLotteryOpen != null) {
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
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (kaijiangWebview != null) {
            kaijiangWebview.onPause();
        }
    }

    private void initkaijiangHeadview() {
        View kaijianghead;
        if (useNewBetUIConfig()) {
            llWanfa.setVisibility(View.VISIBLE);
            spacellWanfa.setVisibility(View.VISIBLE);
            kaijianghead = LayoutInflater.from(context).inflate(R.layout.view_lottery_pk10_play_open_card, null, false);
        } else {
            llWanfa.setVisibility(View.GONE);
            spacellWanfa.setVisibility(View.GONE);
            kaijianghead = LayoutInflater.from(context).inflate(R.layout.view_lottery_play_open_card, null, false);

        }
        kaijiangHeadview.addView(kaijianghead);

        mRcRecentLotteryOpen = kaijianghead.findViewById(R.id.rc_recent_lottery_open);
        mTvRecentLotteryOpen = kaijianghead.findViewById(R.id.tv_recent_lottery_open);
        mTvRecentLotteryOpen.setOnClickListener(this);

        tvIssueDate = kaijianghead.findViewById(R.id.tv_issue_title);
        ivLotteryLogo = kaijianghead.findViewById(R.id.iv_lottery_logo);
        ivOpenning = kaijianghead.findViewById(R.id.iv_openning);
        layoutOpenCode = kaijianghead.findViewById(R.id.layout_open_code);
        lookLiveView = kaijianghead.findViewById(R.id.lookLiveView);
        tvSeeLive = kaijianghead.findViewById(R.id.tvLive);

        tvOpenBallNum[0] = kaijianghead.findViewById(R.id.tv_num1);
        tvOpenBallNum[1] = kaijianghead.findViewById(R.id.tv_num2);
        tvOpenBallNum[2] = kaijianghead.findViewById(R.id.tv_num3);
        tvOpenBallNum[3] = kaijianghead.findViewById(R.id.tv_num4);
        tvOpenBallNum[4] = kaijianghead.findViewById(R.id.tv_num5);
        tvOpenBallNum[5] = kaijianghead.findViewById(R.id.tv_num6);
        tvOpenBallNum[6] = kaijianghead.findViewById(R.id.tv_num7);
        tvOpenBallNum[7] = kaijianghead.findViewById(R.id.tv_num8);
        tvOpenBallNum[8] = kaijianghead.findViewById(R.id.tv_num9);
        tvOpenBallNum[9] = kaijianghead.findViewById(R.id.tv_num10);

        tvIssueDeadLine = (TextView) kaijianghead.findViewById(R.id.tvIssueDeadLine);
        tvDeadLine = kaijianghead.findViewById(R.id.tv_deadline);
        ivJump = kaijianghead.findViewById(R.id.iv_jump);

        if (useNewBetUIConfig()) {
            if (lookLiveView == null) {
                return;
            }
            lookLiveView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShow) {
                        tvSeeLive.setText("关闭直播");
                        kaijiangWebview.setVisibility(View.VISIBLE);
                        tvTip.setText("上拉隐藏");
                        isShow = false;
                    } else {
                        tvSeeLive.setText("观看直播");
                        kaijiangWebview.setVisibility(View.GONE);
                        tvTip.setText("下拉展示开奖动画");
                        isShow = true;
                    }
                }
            });
        }
    }

    private void setWanfaRc() {
        LotteryPlayJD orignalPlay = null;
        LotteryPlayJD currPlay = null;
        ArrayList<LotteryPlayJD> listPlayCategory = UserManagerTouCai.getIns().getLotteryPlayJDMap().get(category);
        String playcode = "";
        if (selectedLotteryPlay != null) {
            playcode = selectedLotteryPlay.getShowName();
        }
        for (LotteryPlayJD item : listPlayCategory) {
            if (Strs.isEqual(item.getShowName(), playcode)) {
                orignalPlay = item;
                break;
            }
        }

        if (orignalPlay == null && listPlayCategory.size() > 0) {
            orignalPlay = listPlayCategory.get(0);
        }
        currPlay = orignalPlay;

        Log.e("FragLotteryPlayMainJD", "orignalPlay:" + orignalPlay.getShowName());

        for (LotteryPlayJD item : listPlayCategory) {

            Log.e("FragLotteryPlayMainJD", item.getShowName() + "----" + item.getPlayId());

            if (Strs.isEqual(item.getShowName(), currPlay.getShowName())) {
                Log.e("FragLotteryPlayMainJD", currPlay.getShowName());
                item.setChecked(true);
            } else {
                item.setChecked(false);
            }
        }

        recycleViewLotteryMethod.setLayoutManager(new LinearLayoutManager(context));
        recycleViewLotteryMethod.setAdapter(new BaseQuickAdapter<LotteryPlayJD, BaseViewHolder>(R.layout.item_simple_text, listPlayCategory) {
            @Override
            protected void convert(BaseViewHolder helper, LotteryPlayJD item) {
                helper.setText(R.id.betName, item.getShowName());
                ((BLTextView) helper.getView(R.id.betName)).setEnabled(!item.isChecked());

                ((BLTextView) helper.getView(R.id.betName)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (LotteryPlayJD lotteryPlayJD : listPlayCategory) {
                            lotteryPlayJD.setChecked(false);
                        }
                        item.setChecked(true);
                        notifyDataSetChanged();

                        showPlayAndHideLotteryMenu(category, item.getShowName());
                    }
                });
            }
        });
    }

    private LotteryPlayJD getCurrentUiConfig() {
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
                String latest = String.format("%s 期   倒计时", issuno, Dates.dateDiffHHMMSS(nextIssue.getSurplusTime()));
                tvIssueDeadLine.setText(Html.fromHtml(latest));
                tvDeadLine.setText("封盘   " + Dates.dateDiffHHMMSS(nextIssue.getSurplusTime()));
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
                                String latest = String.format("%s 期   倒计时", issuno);
                                tvIssueDeadLine.setText(latest);
                                String date = Dates.dateDiffHHMMSS(nextIssue.getSurplusTime() - aLong);
                                SpannableStringBuilder style = new SpannableStringBuilder(date);
                                style.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.white)),
                                        0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                style.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.white)), 5, 7, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                style.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.white)), 10, 12, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                tvDeadLine.setText("封盘   " + style);
                            } else if (nextIssue.getSurplusTime() - aLong < 0) {
                                String latest = String.format("%s 期   ", issue);
                                tvIssueDeadLine.setText(latest);
                                tvDeadLine.setText("正在开奖中...");
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
                                refreshMark = 0;
                            }
                        }


                        if (firstOpenHistory == null) {
                            //无值则一直刷新
                            refreshHistoryMark++;
                            if (refreshHistoryMark > REFRESH_INTERAL) {
                                initLotteryOpen();
                                refreshHistoryMark = 0;
                            }
                        }
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
//            String issuno = issue.contains("-") ? issue.split("-")[1] : issue;
            tvIssueDate.setText(Html.fromHtml(String.format("NO.<font color=\"#02A8F3\">%s</font>开奖结果", issue)));

            if (mRcRecentOpenAdapter != null) {
                mRcRecentOpenAdapter.setNewData(listOpen.size() >= 8 ? listOpen.subList(0, 8) : listOpen);
            }
        } else {
            tvIssueDate.setText("");
        }

        updateOpenCode(listOpen.size() > 0 ? listOpen.get(0) : null);
    }

    private void updateOpenCode(LotteryOpenHistory history) {
        int count = 6;
        String[] codes = {"0", "0", "0", "0", "0", "0"};
        if (history != null && history.getCode() != null) {
            String[] arr = history.getCode().split(",");
            count = Math.min(arr.length, 9);
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

        for (int i = 0; i < 9; i++) {
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
        HttpActionTouCai.getLotteryPlayInfoJD(this, lotteryKind.getCode(), new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                showLoading();
            }

            @Override
            public boolean onGetString(String str) {
                if (str != null) {
                    userPlayInfo = new Gson().fromJson(str, LotteryPlayUserInfoJD.class);
                    fragContainer.setUserPlayInfo(userPlayInfo);
                    setDefaultPlay();
                } else {
                    Toasts.show(getActivity(), "获取数据失败!", false);
                }
                return true;
            }


            @Override
            public void onAfter(int id) {
                vgBottomGroup.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                    }
                }, 500);
            }
        });
    }

    private boolean showRecentOpenList = false;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvClear:
                fragContainer.getCurrentPlayFrag().getCtrlPlay().reset();
                break;
            case R.id.tv_wanfa:
                DialogsTouCai.showInstructionDialog(getContext(),
                        fragContainer.getCurrentPlayFrag().getCtrlPlay().getUiConfig().getIntroduction());
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
            default:

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteFromCartEvent(DeleteFromCartEvent event) {
        if (event.isAllDelete) {
            listNumAddedCart.clear();
        } else {
            for (int i = 0; i < event.arrDeleteIndex.length; i++) {
                listNumAddedCart.remove(event.arrDeleteIndex[i]);
            }
        }
    }

    @Override
    public long getCurrentHit() {
        return 0;
        //return fragContainer.getSnakeBar().getHitCount();
    }

    @Override
    public void setCurrentUserPlayInfo(Object userPlayInfo) {
        fragContainer.setUserPlayInfo((LotteryPlayUserInfoJD) userPlayInfo);
    }

    @Override
    public BaseLotteryPlayFragmentJD getCurrentPlay() {
        return fragContainer.getCurrentPlayFrag();
    }

    @Override
    public String getCurrentModel() {
        return fragContainer.getFootView().getModel();
    }

    public int getCurrentNumTimes() {
        return fragContainer.getFootView().getNumTimes();
    }

    @Override
    public double getAward() {
        return getSnakeBar().getRewards();
    }

    @Override
    public int getCurrentPoint() {
        return fragContainer.getFootView().getCurrentPoint();
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
    public LotteryPlayJD getSelectedLotteryPlay() {
        return selectedLotteryPlay;
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

    public void setDefaultPlay() {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                String defaultPlayName = "";
                if ("SSC".equals(category)) {
                    defaultPlayName = "双面盘";
                } else if ("11X5".equals(category)) {
                    defaultPlayName = "两面盘";
                } else if ("K3".equals(category)) {
                    defaultPlayName = "和值";
                } else if ("PK10".equals(category)) {
                    defaultPlayName = "整合";
                }
                if (Strs.isEmpty(defaultPlayName)) {
                    return;
                }

                if (Strs.isNotEmpty(UserManagerTouCai.getIns().getlastLotteryPlay(lotteryId))) {
                    defaultPlayName = UserManagerTouCai.getIns().getlastLotteryPlay(lotteryId);
                }

                selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlayJD(category, defaultPlayName);
                fragContainer.showPlay(category, defaultPlayName);

                if (!useNewBetUIConfig()) {
                    ((ActLotteryMain) context).tvTitle.setText(defaultPlayName);
                }
                fragContainer.getPlayContainer().setVisibility(View.VISIBLE);

                if (useNewBetUIConfig()) {
                    setWanfaRc();
                }
            }
        });
    }

    public void showLoading() {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                DialogsTouCai.showProgressDialog(((ActLotteryMain) context), "");
            }
        });

    }

    public void hideLoading() {
        DialogsTouCai.hideProgressDialog(getActivity());
    }

    public void showMenu() {
        if (userPlayInfo != null)
            FragLotteryPlayMenuJD.newIns((AppCompatActivity) getActivity(), category, selectedLotteryPlay.getShowName(), this);
    }

    public void hideMenu() {
    }

    public NestedScrollView getScrollView() {
        return scrollView;
    }

    @Override
    public PlaySnakeViewJD getSnakeBar() {
        return snackView;
    }

    public void showPlay() {
        showLoading();
        fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
        ThreadCollector.getIns().postDelayOnUIThread(400, new Runnable() {
            @Override
            public void run() {
                hideLoading();
                scrollView.scrollTo(0, 0);
            }
        });
    }

    public void hidePlay() {
        fragContainer.getPlayContainer().setVisibility(View.GONE);
    }

    public void showPlayAndHideLotteryMenu(final String category, final String selectedPlayCode) {
        //AbDebug.log(AbDebug.TAG_APP, "before showLoading" );
        //getSnakeBar().clearAll();
        showLoading();
        //AbDebug.log(AbDebug.TAG_APP, "after showLoading" );
        hideMenu();
        //AbDebug.log(AbDebug.TAG_APP, "hideMenu" );
        fragContainer.removeCurrentPlay();
        // AbDebug.log(AbDebug.TAG_APP, "removeCurrentPlay" );
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlayJD(category, selectedPlayCode);
                //AbDebug.log(AbDebug.TAG_APP, "selectedLotteryPlay" );
                fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
                fragContainer.showPlay(category, selectedPlayCode);
                onLotteryMethodChanged.onLotteryMethodChanged(lotteryKind, selectedPlayCode, selectedLotteryPlay.getShowName());
                //AbDebug.log(AbDebug.TAG_APP, "showPlay" );
            }
        });
        ThreadCollector.getIns().postDelayOnUIThread(500, new Runnable() {
            @Override
            public void run() {
                hideLoading();
                //AbDebug.log(AbDebug.TAG_APP, "hideLoading" );
                scrollView.scrollTo(0, 0);
                //AbDebug.log(AbDebug.TAG_APP, "scrollTo" );
            }
        });
    }


    public ImageButton getBottomClearBtn() {
        return tvClear;
    }

    @Override
    public ViewGroup getBottomGroup() {
        return vgBottomGroup;
    }

    @Override
    public int getLotteryId() {
        return lotteryId;
    }

    public EditText getMoneyInput() {
        return etMoney;
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

    public static boolean syncCookieWithHttp(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        com.tencent.smtt.sdk.CookieSyncManager.createInstance(Global.app);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();//移除
        String lastCookie = AbHttpAO.getIns().getLastCookie();
        String[] arrCookie = lastCookie.split(";");
        for (int i = 0; i < arrCookie.length; i++) {
            if (Strs.isNotEmpty(arrCookie[i])) {
                cookieManager.setCookie(httpUrl.host(), arrCookie[i]);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
            }
        }

        if (Build.VERSION.SDK_INT < 21) {
            com.tencent.smtt.sdk.CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }
        String newCookie = cookieManager.getCookie(url);
        return TextUtils.isEmpty(newCookie);
    }

    private class LotteryOpenHistoryAdapter extends BaseAdapter {
        private int[] ballBg = {
                R.drawable.ic_lhc_ball_red, // not used
                R.drawable.ic_lhc_ball_red,
                R.drawable.ic_lhc_ball_blue,
                R.drawable.ic_lhc_ball_green,
        };
        private List<LotteryOpenHistory> list;
        int ballCount;

        public LotteryOpenHistoryAdapter(List<LotteryOpenHistory> list) {
            this.list = list;
            int ballCount = 6;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != null && list.get(i).getCode() != null) {
                    String[] arr = list.get(i).getCode().split(",");

                    if (arr != null && arr.length > 0) {
                        ballCount = arr.length;
                        break;
                    }
                }
            }
            this.ballCount = ballCount;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.item_lottery_play_open_record, null);
                convertView.setTag(new ViewHolder(convertView));
            }

            ViewHolder vh = (ViewHolder) convertView.getTag();

            LotteryOpenHistory history = list.get(position);
            vh.issue.setText(String.format("第%s期", history.getIssue()));
//            vh.category.setText(history.getLottery());

            String[] codes = null;
            if (history.getCode() == null) {
                codes = new String[ballCount];
                for (int i = 0; i < ballCount; i++)
                    codes[i] = "0";
            } else {
                codes = history.getCode().split(",");
            }

            for (int i = 0; i < vh.num.length; i++) {
                if (codes == null || i > codes.length - 1) {
                    vh.num[i].setVisibility(View.GONE);
                } else {
                    vh.num[i].setVisibility(View.VISIBLE);
                    vh.num[i].setText(codes[i]);
                    try {
                        vh.num[i].setBackgroundResource(ballBg[UserManager.getIns().getListLHCBallColor().get(Strs.parse(codes[i], 1))]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        vh.num[i].setBackgroundResource(ballBg[0]);
                    }
                }
            }

            return convertView;
        }

        class ViewHolder {
            TextView issue;
            TextView[] num = new TextView[10];
            TextView category;

            ViewHolder(View view) {
                issue = view.findViewById(R.id.tv_issue);
                num[0] = view.findViewById(R.id.tv_ball_1);
                num[1] = view.findViewById(R.id.tv_ball_2);
                num[2] = view.findViewById(R.id.tv_ball_3);
                num[3] = view.findViewById(R.id.tv_ball_4);
                num[4] = view.findViewById(R.id.tv_ball_5);
                num[5] = view.findViewById(R.id.tv_ball_6);
                num[6] = view.findViewById(R.id.tv_ball_7);
                num[7] = view.findViewById(R.id.tv_ball_8);
                num[8] = view.findViewById(R.id.tv_ball_9);
                num[9] = view.findViewById(R.id.tv_ball_10);
                category = view.findViewById(R.id.tv_category);
            }
        }
    }

    private void loadUrl(int gamecodeID) {
        ActWebX5.syncCookieWithHttp(getUrl(gamecodeID));
        if (Strs.isNotEmpty(getUrl(gamecodeID))) {
            kaijiangWebview.loadUrl(getUrl(gamecodeID).startsWith("http") ? getUrl(gamecodeID) : "http://" + getUrl(gamecodeID));
        }

    }

    private String getUrl(int gamecodeID) {
        return Consitances.PK10_ANIMATE;
    }

    CountDownTimer countDownTimer;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBuyedEvent(BuyedLotteryEvent event) {
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

    @Override
    public void onResume() {
        super.onResume();
        getMissionList();
    }

    //定义处理接收的方法,处理大奖推送的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bonusPoolEventBus(BonusPoolEventMode bonusPoolEventMode) {
        getMissionList();
    }

    private boolean useNewBetUIConfig() {
        return CommonConsts.setBetNeedNewUIConfig(lotteryKind.getCode());
    }

}
