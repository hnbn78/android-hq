package com.desheng.app.toucai.panel;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ListPopupWindow;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.global.Global;
import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Dates;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.bumptech.glide.Glide;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.LotteryJump;
import com.desheng.app.toucai.model.LotteryOpenNext;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.LHCJellyLayout;
import com.desheng.app.toucai.view.PlayFootView;
import com.desheng.app.toucai.view.PlaySnakeView;
import com.desheng.base.action.DBAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.event.DeleteFromCartEvent;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryOpenHistory;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.desheng.base.panel.ActLotteryOpen;
import com.desheng.base.panel.ActLotteryTrend;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.FragmentsHelper;
import com.pearl.view.SimpleCollapse.SimpleNestedScrollView;
import com.pearl.view.jellyrefresh.PullToRefreshLayout;
import com.shark.tc.R;
import com.tencent.smtt.sdk.CookieManager;

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


/**
 * 彩票玩法页面
 * Created by lee on 2018/3/8.
 */
public class ActLotteryPlay extends AbAdvanceActivity implements View.OnClickListener, LotteryPlayPanel {
    public static final int REFRESH_INTERAL = 10;
    public static boolean visible;
    //private LotteryInfo lotteryInfo;
    private LotteryOpenHistory firstOpenHistory;
    private ViewGroup vgTextHistory;
    private ViewGroup vgToolbarGroup;
    private LinearLayout lvOpenCode;
    private ViewGroup flQuickPlay;
    private SimpleNestedScrollView scrollView;
    private PlaySnakeView snackView;
    private PlayFootView footView;
    private TextView tvDeadLine;
    private TextView tvIssueDeadLine;
    private TextView tvIssueDate;
    private TextView[] tvOpenBallNum = new TextView[10];
    private TextView tvTitle;
    private TextView tvCounter;
    private ImageView ivLotteryLogo;
    private View vRegist;
    private ImageView ivJump;
    private PullToRefreshLayout mJellyLayout;
    private LHCJellyLayout mjellyPull;

    private ImageView ivOpenning;
    private View layoutOpenCode;
    private int defaultSound = 0;

    /**
     * @param activity
     * @param lotteryId 彩票id
     */
    public static void launch(Activity activity, int lotteryId, boolean jumpToFinish) {
        Intent itt = new Intent(activity, ActLotteryPlay.class);
        itt.putExtra("lotteryId", lotteryId);
        itt.putExtra("jumpToFinish", jumpToFinish);
        DBAction.addOrUpdateUserPlayedLottery(lotteryId);
        activity.startActivity(itt);
    }

    public static void launch(Activity activity, int lotteryId) {
        Intent itt = new Intent(activity, ActLotteryPlay.class);
        itt.putExtra("lotteryId", lotteryId);
        DBAction.addOrUpdateUserPlayedLottery(lotteryId);
        activity.startActivity(itt);
    }

    private FragLotteryPlayContainer fragContainer;
    private ViewGroup vgBottomGroup;
    private TextView tvAddNum;
    private ImageView tvShoppingCar;
    private TextView tvBuy;
    private ImageView ivHeadUp;

    private int lotteryId;
    private LotteryKind lotteryKind;
    private LotteryPlay selectedLotteryPlay;
    private LotteryPlayUserInfo userPlayInfo;
    private ArrayList<HashMap<String, Object>> listNumAddedCart;

    private boolean jumpToFinish = false;
    private String category = "";
    private LotteryOpenNext nextIssue;
    private long refreshMark = 0;
    private long refreshHistoryMark;
    private Disposable mDisposable;

    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_play;
    }

    @Override
    protected void init() {
        lotteryId = getIntent().getIntExtra("lotteryId", 0);
        jumpToFinish = getIntent().getBooleanExtra("jumpToFinish", false);
        lotteryKind = (LotteryKind) CtxLotteryTouCai.getIns().findLotteryKind(lotteryId);
        category = lotteryKind.getPlayCategory();
        listNumAddedCart = new ArrayList<>();
        vgToolbarGroup = findViewById(R.id.vgToolbarGroup);
        lvOpenCode = findViewById(R.id.lv_open_code);
        scrollView = findViewById(R.id.sv_scroller);
        snackView = findViewById(R.id.vSnakeBar);
        footView = findViewById(R.id.vFootView);
        footView.setSnakeView(snackView);
        tvDeadLine = findViewById(R.id.tv_deadline);
        tvTitle = findViewById(R.id.tvLotteryTitleCenter);
        tvCounter = findViewById(R.id.tv_order_count);
        ivLotteryLogo = findViewById(R.id.iv_lottery_logo);
        vRegist = findViewById(R.id.layout_regist);
        findViewById(R.id.tvClear).setOnClickListener(this);
        ivOpenning = findViewById(R.id.iv_openning);
        layoutOpenCode = findViewById(R.id.layout_open_code);
        ivJump = findViewById(R.id.iv_jump);

        hideToolbar();
        setToolbarBgColor(R.color.colorPrimary);
        ViewGroup.LayoutParams layoutParams = vgToolbarGroup.getLayoutParams();
        layoutParams.height = getStatusHeight() + getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        vgToolbarGroup.setLayoutParams(layoutParams);
        vgToolbarGroup.setPadding(0, getStatusHeight(), 0, 0);
        setStatusBarTranslucentAndLightContent();

        // load lottery logo
        Glide.with(this).load(R.mipmap.openning).asGif().into(ivOpenning);
        Glide.with(this).load("file:///android_asset/lottery_kind/logo/" + lotteryId + ".png").into(ivLotteryLogo);

        TextView title = findViewById(R.id.tvLotteryTitleCenter);
        title.setText(lotteryKind.getShowName());
        ((View) title.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userPlayInfo != null)
                    FragLotteryPlayMenu.newIns(ActLotteryPlay.this, category, lotteryId, selectedLotteryPlay.lotteryCode, userPlayInfo, ActLotteryPlay.this);
            }
        });
        findViewById(R.id.ibLotteryPlayRightBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListPopupWindow popup = new ListPopupWindow(ActLotteryPlay.this);
                popup.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_popup_menu_lottery_play));
                popup.setAdapter(new PopupMenuAdapter(ActLotteryPlay.this));
                popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                ActBetRecord.launch(ActLotteryPlay.this);
                                break;
                            case 1:
                                //近期开奖
                                ActLotteryOpen.launch(ActLotteryPlay.this, lotteryId);
                                break;
                            case 2:
                                //走势图
                                ActLotteryTrend.launch(ActLotteryPlay.this, lotteryId);
                                break;
                        }
                    }
                });
                popup.setAnchorView(v);
                popup.setVerticalOffset(Views.dp2px(1));
                popup.setContentWidth(Views.dp2px(114.5f));
                popup.show();
            }
        });

        LotteryJump j = UserManagerTouCai.getIns().getJumpInfo().get(String.valueOf(lotteryId));

        if (j == null || !CtxLottery.getIns().findLotteryKind(j.getId()).isEnabled())
            ivJump.setVisibility(View.INVISIBLE);

        ivJump.setImageResource(R.mipmap.ic_lottery_play_jump_right);
        ivJump.setOnClickListener(v -> {
            if (jumpToFinish) {
                finish();
                return;
            }

            ActLotteryPlayJD.launch(ActLotteryPlay.this, j.getId(), true);
        });

        mJellyLayout = findViewById(R.id.jelly_refresh);
        mjellyPull = findViewById(R.id.jelly_layout);
        mJellyLayout.setPullToRefreshListener(new PullToRefreshLayout.PullToRefreshListener() {
            @Override
            public void onPrepareRefresh(PullToRefreshLayout pullToRefreshLayout) {
                mJellyLayout.setRefreshing(true, true);
                mjellyPull.showLoading(true);
            }

            @Override
            public void onTriggerRefresh(PullToRefreshLayout pullToRefreshLayout) {
                updateStaticOpen(true);
            }

            @Override
            public void onExtending(PullToRefreshLayout pullToRefreshLayout) {
                lvOpenCode.setVisibility(View.VISIBLE);
                lvOpenCode.measure(View.MeasureSpec.makeMeasureSpec(scrollView.getMeasuredWidth(), View.MeasureSpec.AT_MOST),
                        View.MeasureSpec.makeMeasureSpec(scrollView.getMeasuredWidth(), View.MeasureSpec.AT_MOST));
                scrollView.scrollTo(0, lvOpenCode.getMeasuredHeight());
                scrollView.realSmoothScrollTo(0, 300, null);
                mjellyPull.showLoading(false);
            }

            @Override
            public void onSecondaryExtending(PullToRefreshLayout pullToRefreshLayout) {
                ActLotteryPlayLHCHistory.launch(ActLotteryPlay.this, lotteryId, lotteryKind.getShowName());
            }

            @Override
            public void onCollasping(PullToRefreshLayout pullToRefreshLayout) {
                scrollView.realSmoothScrollTo(lvOpenCode.getBottom(), 300, new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        lvOpenCode.setVisibility(View.GONE);
                        scrollView.scrollTo(0, 0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        });
        mjellyPull.setHeaderHeight(Views.dp2px(80));
        mjellyPull.setmTriggerHeight(Views.dp2px(120));
        mJellyLayout.setPullingListener(new PullToRefreshLayout.PullToRefreshPullingListener() {
            @Override
            public void onPulling(float fraction, float pointXPosition) {
                mjellyPull.setPointX(pointXPosition);
            }

            @Override
            public void onTranslationYChanged(float translationY) {
                mjellyPull.setPullHeight(translationY);
            }

            @Override
            public void onRelease() {
                mjellyPull.onRelease();
            }
        });


        //初始化发布
        tvIssueDeadLine = (TextView) findViewById(R.id.tvIssueDeadLine);
        tvIssueDate = findViewById(R.id.tv_issue_title);
        tvOpenBallNum[0] = findViewById(R.id.tv_num1);
        tvOpenBallNum[1] = findViewById(R.id.tv_num2);
        tvOpenBallNum[2] = findViewById(R.id.tv_num3);
        tvOpenBallNum[3] = findViewById(R.id.tv_num4);
        tvOpenBallNum[4] = findViewById(R.id.tv_num5);
        tvOpenBallNum[5] = findViewById(R.id.tv_num6);
        tvOpenBallNum[6] = findViewById(R.id.tv_num7);
        tvOpenBallNum[7] = findViewById(R.id.tv_num8);
        tvOpenBallNum[8] = findViewById(R.id.tv_num9);
        tvOpenBallNum[9] = findViewById(R.id.tv_num10);


        initLotteryOpen();

        //初始化底部菜单
        vgBottomGroup = (ViewGroup) findViewById(R.id.vgBottomGroup);
        tvAddNum = (TextView) findViewById(R.id.tvAddNum);
        tvAddNum.setOnClickListener(this);
        tvShoppingCar = findViewById(R.id.tvShoppingCart);
        tvShoppingCar.setOnClickListener(this);
        //controller控制直接生成

        //controller控制直接购买
        tvBuy = (TextView) findViewById(R.id.tvBuy);

        //直接初始化container
        fragContainer = (FragLotteryPlayContainer) FragmentsHelper.replaceFragments(getSupportFragmentManager(), R.id.vgContent, FragLotteryPlayContainer.newIns(lotteryId, category, this));

        EventBus.getDefault().register(this);

        setDefaultPlay();
        //初始化彩票用户数据
        initLotteryPlayInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        visible = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserManagerTouCai.getIns().isLogined()) {
            vRegist.setVisibility(View.GONE);
        } else {
            vRegist.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
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

        updateStaticOpen(false);

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
                                tvDeadLine.setText(Html.fromHtml(Dates.dateDiffHHMMSS(nextIssue.getSurplusTime() - aLong).replace(":", "<font color=\"#888888\"> : </font>")));
                            } else if (nextIssue.getSurplusTime() - aLong < 0) {
                                String latest = String.format("NO.%s", issue);
                                tvIssueDeadLine.setText(latest);
                                tvDeadLine.setText("正在开奖");
                                nextIssue = null;  //截止日期后定时刷新
                                firstOpenHistory = null;
                                refreshHistoryMark = 0;
                                refreshMark = 0;
                                Toasts.show(ActLotteryPlay.this, issue + "期已截止，投注时请注意期号！", true);
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


    private void updateStaticOpen(final boolean updateList) {
        HttpActionTouCai.getLotteryOpenCodeHistory(this, lotteryKind.getCode(), true, new AbHttpResult() {
            @Override
            public boolean onGetString(String str) {
                if (updateList) {
                    lvOpenCode.removeAllViews();
                }
                try {
                    ArrayList<LotteryOpenHistory> listOpen = new Gson().fromJson(str, new TypeToken<ArrayList<LotteryOpenHistory>>() {
                    }.getType());
                    initOpenCodeCard(listOpen);

//                    ((LotteryPlayDropLayout) findViewById(R.id.dropDownList)).setHistoryData(listOpen);
                    if (updateList) {
                        Adapter a = new ActLotteryPlay.LotteryOpenHistoryAdapter(listOpen.subList(0, listOpen.size() < 5 ? listOpen.size() : 5));
                        for (int i = 0; i < a.getCount(); i++) {
                            lvOpenCode.addView(a.getView(i, null, lvOpenCode));
                        }
                        mJellyLayout.setRefreshing(false, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toasts.show(ActLotteryPlay.this, "暂无开奖信息!", false);
                    mJellyLayout.setCollapsed();
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
            tvIssueDate.setText(Html.fromHtml(String.format("NO.<font color=\"#3e97fa\">%s</font>开奖结果", issue)));
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
            layoutOpenCode.setVisibility(View.VISIBLE);
        } else {
            ivOpenning.setVisibility(View.VISIBLE);
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
                    ThreadCollector.getIns().runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            footView.setLotteryInfo(selectedLotteryPlay, userPlayInfo);
                            fragContainer.setUserPlayInfo(userPlayInfo);
                        }
                    });

                } else {
                    Toasts.show(ActLotteryPlay.this, Strs.isEmpty(msg) ? "获取数据失败!" : msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                /*slidingDrawer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                    }
                }, 500);*/
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

                Toasts.show(this, "添加号码成功!", true);
                resetCurrentPlay();
                break;
            case R.id.tvShoppingCart:
                if (listNumAddedCart.size() == 0) {
                    DialogsTouCai.showDialog(this, "温馨提示", "请先选择号码", view -> {
                    });
                    return;
                }

                ActLotteryCartTouCai.launch(this, lotteryId, listNumAddedCart);
                break;

            case R.id.tvClear:
                fragContainer.getCurrentPlayFrag().getCtrlPlay().reset();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBuyedInCartEvent(BuyedLotteryEvent event) {
    }

    @Override
    public Context getContext() {
        return this;
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

    @Override
    public void setDefaultPlay() {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                String defaultPlayCode = "";
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

                if (Strs.isNotEmpty(UserManagerTouCai.getIns().getlastLotteryPlay(lotteryId))) {
                    defaultPlayCode = UserManagerTouCai.getIns().getlastLotteryPlay(lotteryId);
                }

                selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlay(category, defaultPlayCode);
                tvTitle.setText(selectedLotteryPlay.showName);
                fragContainer.showPlay(category, defaultPlayCode);
                fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showLoading() {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                DialogsTouCai.showProgressDialog(ActLotteryPlay.this, "");
            }
        });

    }

    @Override
    public void hideLoading() {
        DialogsTouCai.hideProgressDialog(ActLotteryPlay.this);
    }

    @Override
    public void showMenu() {

        showLoading();

        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                //slidingDrawer.setState(SlidingDrawer.EXPANDED);
            }
        });
        scrollView.scrollTo(0, 0);
        ThreadCollector.getIns().postDelayOnUIThread(300, new Runnable() {
            @Override
            public void run() {
                hideLoading();
            }
        });
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
        DialogsTouCai.showProgressDialog(ActLotteryPlay.this, "");

        //fragContainer.removeCurrentPlay();

        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlay(category, selectedPlayCode);
                //AbDebug.log(AbDebug.TAG_APP, "selectedLotteryPlay" );
                tvTitle.setText(selectedLotteryPlay.showName);
                fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
                fragContainer.showPlay(category, selectedPlayCode);
                //AbDebug.log(AbDebug.TAG_APP, "showPlay" );

                if (userPlayInfo != null)
                    footView.setLotteryInfo(selectedLotteryPlay, userPlayInfo);
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
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }

        EventBus.getDefault().unregister(this);
    }

    /**
     * 将cookie同步到WebView
     *
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    public static boolean syncCookieWithHttp(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        com.tencent.smtt.sdk.CookieSyncManager.createInstance(Global.app);
        com.tencent.smtt.sdk.CookieManager cookieManager = CookieManager.getInstance();
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
            com.tencent.smtt.sdk.CookieManager.getInstance().flush();
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
                convertView.setTag(new ActLotteryPlay.LotteryOpenHistoryAdapter.ViewHolder(convertView));
            }

            ActLotteryPlay.LotteryOpenHistoryAdapter.ViewHolder vh = (ActLotteryPlay.LotteryOpenHistoryAdapter.ViewHolder) convertView.getTag();

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

    public static class PopupMenuAdapter extends BaseAdapter {
        static int[] drawables = new int[]{
                R.drawable.ic_menu_lottery_play_touzhu,
                R.drawable.ic_menu_lottery_play_recent,
                R.mipmap.ic_menu_lottery_play_zoushitu
        };

        static int arr = R.array.lottery_play_menu;
        private String[] strArr;

        public PopupMenuAdapter(@android.support.annotation.NonNull Context context) {
            strArr = context.getResources().getStringArray(arr);
        }

        @Override
        public int getCount() {
            return strArr.length;
        }

        @Override
        public Object getItem(int position) {
            return strArr[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @android.support.annotation.NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @android.support.annotation.NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popup_menu_lottery_play, parent, false);
            }

            TextView textView = convertView.findViewById(android.R.id.text1);
            ImageView imageView = convertView.findViewById(R.id.iv_icon);
            textView.setText(strArr[position]);
            imageView.setImageResource(drawables[position]);

            if (position + 1 == getCount())
                convertView.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
            else
                convertView.findViewById(R.id.divider).setVisibility(View.VISIBLE);

            return convertView;
        }
    }
}
