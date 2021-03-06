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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.LotteryJump;
import com.desheng.app.toucai.model.LotteryOpenNext;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.LHCJellyLayout;
import com.desheng.app.toucai.view.PlaySnakeViewJD;
import com.desheng.base.action.DBAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.event.DeleteFromCartEvent;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryOpenHistory;
import com.desheng.base.model.LotteryPlayJD;
import com.desheng.base.model.LotteryPlayUserInfoJD;
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
 * ??????????????????
 * Created by lee on 2018/3/8.
 */
public class ActLotteryPlayJD extends AbAdvanceActivity implements View.OnClickListener, LotteryPlayPanelJD {
    public static final int REFRESH_INTERAL = 10;
    private ArrayList<LotteryPlayJD> listPlayJD;
    private LotteryKind lotteryKind;

    /**
     * @param activity
     * @param lotteryId ??????id
     */
    public static void launch(Activity activity, int lotteryId, boolean jumpToFinish) {
        Intent itt = new Intent(activity, ActLotteryPlayJD.class);
        itt.putExtra("lotteryId", lotteryId);
        itt.putExtra("jumpToFinish", jumpToFinish);
        activity.startActivity(itt);
        DBAction.addOrUpdateUserPlayedLottery(lotteryId);
    }

    public static void launch(Activity activity, int lotteryId) {
        Intent itt = new Intent(activity, ActLotteryPlayJD.class);
        itt.putExtra("lotteryId", lotteryId);
        activity.startActivity(itt);
        DBAction.addOrUpdateUserPlayedLottery(lotteryId);
    }

    public static final int[] arrTvOpenDateIds = new int[]{R.id.tvOpenDate1, R.id.tvOpenDate2, R.id.tvOpenDate3, R.id.tvOpenDate4, R.id.tvOpenDate5};
    public static final int[] arrTvIssueNoIds = new int[]{R.id.tvIssueNo1, R.id.tvIssueNo2, R.id.tvIssueNo3, R.id.tvIssueNo4, R.id.tvIssueNo5};
    public static final int[] arrTvOpenNumIds = new int[]{R.id.tvOpenNum1, R.id.tvOpenNum2, R.id.tvOpenNum3, R.id.tvOpenNum4, R.id.tvOpenNum5};

    private FragLotteryPlayContainerJD fragContainer;
    private TextView tvIssueDeadLine;
    private ViewGroup vgToolbarGroup;
    private ViewGroup vgBottomGroup;
    private ImageButton tvClear;
    private EditText etMoney;
    private TextView tvBuy;
    private LinearLayout lvOpenCode;
    private PullToRefreshLayout mJellyLayout;
    private LotteryOpenHistory firstOpenHistory;
    private LHCJellyLayout mjellyPull;
    private SimpleNestedScrollView scrollView;
    private TextView tvDeadLine;
    private TextView tvTitle;
    private TextView tvIssueDate;
    private TextView[] tvOpenBallNum = new TextView[10];
    private ImageView ivLotteryLogo;
    private PlaySnakeViewJD snackView;
    private View vRegist;
    private ImageView ivJump;
    private ImageView ivOpenning;
    private View layoutOpenCode;

    private int lotteryId;
    private LotteryPlayJD selectedLotteryPlay;
    private LotteryPlayUserInfoJD userPlayInfo;
    private ArrayList<HashMap<String, Object>> listNumAddedCart;

    private boolean jumpToFinish = false;
    private String category = "";
    private LotteryOpenNext nextIssue;
    private long refreshMark = 0;
    private long refreshHistoryMark;
    private Disposable mDisposable;


    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_play_jd;
    }

    @Override
    protected void init() {
        lotteryId = getIntent().getIntExtra("lotteryId", 0);
        jumpToFinish = getIntent().getBooleanExtra("jumpToFinish", false);
        lotteryKind = (LotteryKind) LotteryKind.find(lotteryId);
        category = lotteryKind.getPlayCategory();
        listPlayJD = UserManagerTouCai.getIns().getLotteryPlayJDMap().get(category);
        listNumAddedCart = new ArrayList<>();
        vgToolbarGroup = findViewById(R.id.vgToolbarGroup);
        lvOpenCode = findViewById(R.id.lv_open_code);
        scrollView = findViewById(R.id.sv_scroller);
        snackView = findViewById(R.id.vSnakeBar);
        tvDeadLine = findViewById(R.id.tv_deadline);
        tvTitle = findViewById(R.id.tvLotteryTitleCenter);
        ivLotteryLogo = findViewById(R.id.iv_lottery_logo);
        vRegist = findViewById(R.id.layout_regist);
        findViewById(R.id.tvClear).setOnClickListener(this);
        findViewById(R.id.iv_jd_play_indicator).setVisibility(View.VISIBLE);
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
                FragLotteryPlayMenuJD.newIns(ActLotteryPlayJD.this, category, selectedLotteryPlay.getShowName(), ActLotteryPlayJD.this);
            }
        });
        findViewById(R.id.ibLotteryPlayRightBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListPopupWindow popup = new ListPopupWindow(ActLotteryPlayJD.this);
                popup.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_popup_menu_lottery_play));
                popup.setAdapter(new PopupMenuAdapter(ActLotteryPlayJD.this));
                popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                //????????????
                                ActBetRecord.launch(ActLotteryPlayJD.this);
                                break;
                            case 1:
                                //????????????
                                ActLotteryOpen.launch(ActLotteryPlayJD.this, lotteryId);
                                break;
                            case 2:
                                //?????????
                                ActLotteryTrend.launch(ActLotteryPlayJD.this, lotteryId);
                                break;
                        }
                    }
                });
                popup.setVerticalOffset(Views.dp2px(1));
                popup.setContentWidth(Views.dp2px(114.5f));
                popup.setAnchorView(v);
                popup.show();
            }
        });

        LotteryJump j = UserManagerTouCai.getIns().getJumpInfo().get(String.valueOf(lotteryId));

        if (j == null || !CtxLottery.getIns().findLotteryKind(j.getId()).isEnabled())
            ivJump.setVisibility(View.INVISIBLE);

        ivJump.setImageResource(R.mipmap.ic_lottery_play_jump_left);
        ivJump.setOnClickListener(v -> {
            if (jumpToFinish) {
                finish();
                return;
            }

            ActLotteryPlay.launch(ActLotteryPlayJD.this, j.getId(), true);
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
                ActLotteryPlayLHCHistory.launch(ActLotteryPlayJD.this, lotteryId, lotteryKind.getShowName());
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


        tvIssueDeadLine = (TextView) findViewById(R.id.tvIssueDeadLine);

        //???????????????
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

        //?????????????????????
        vgBottomGroup = (ViewGroup) findViewById(R.id.vgBottomGroup);
        //controller????????????
        tvClear = findViewById(R.id.tvClear);
        //??????
        etMoney = (EditText) findViewById(R.id.etMoney);
        etMoney.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String s = etMoney.getText().toString();
                if (Strs.parse(s, 0) <= 0) {
                    etMoney.setText("1");
                }
            }
        });

        //controller??????????????????
        tvBuy = (TextView) findViewById(R.id.tvBuy);

        //???????????????container
        fragContainer = (FragLotteryPlayContainerJD) FragmentsHelper.replaceFragments(getSupportFragmentManager(), R.id.vgContent, FragLotteryPlayContainerJD.newIns(lotteryId, category, this));

        EventBus.getDefault().register(this);

        setDefaultPlay();
        //???????????????????????????
        initLotteryPlayInfo();
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // ????????????????????????????????????????????????TouchEvent???
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //????????????????????????location??????
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // ??????????????????????????????????????????EditText?????????
                return false;
            } else {
                //???EditText??????????????????????????????
                v.setFocusable(false);
//                v.setFocusable(true); //?????????????????????????????????????????????????????????????????????
                v.setFocusableInTouchMode(true);
                return true;
            }
        }
        return false;
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
                    //????????????, ????????????, ????????????
                    nextIssue = null;
                    return true;
                }
                String issue = nextIssue.getIssue();
                String issuno = issue.contains("-") ? issue.split("-")[1] : issue;
                String latest = String.format("NO.%s ??????????????????", issuno, Dates.dateDiffHHMMSS(nextIssue.getSurplusTime()));
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
                        if (nextIssue != null && nextIssue != null) {
                            String issue = nextIssue.getIssue();
                            String issuno = issue.contains("-") ? issue.split("-")[1] : issue;
                            if (nextIssue.getSurplusTime() - aLong >= 0) {
                                String latest = String.format("NO.%s ??????????????????", issuno);
                                tvIssueDeadLine.setText(latest);
                                tvDeadLine.setText(Html.fromHtml(Dates.dateDiffHHMMSS(nextIssue.getSurplusTime() - aLong).replace(":", "<font color=\"#888888\"> : </font>")));
                                tvIssueDeadLine.setText(Html.fromHtml(latest));
                            } else if (nextIssue.getSurplusTime() - aLong < 0) {
                                String latest = String.format("NO.%s", issue);
                                tvIssueDeadLine.setText(latest);
                                tvDeadLine.setText("????????????");
                                nextIssue = null;  //???????????????????????????
                                firstOpenHistory = null;
                                refreshHistoryMark = 0;
                                refreshMark = 0;
                                Toasts.show(ActLotteryPlayJD.this, issue + "??????????????????????????????????????????", true);
                            }
                        } else {
                            //?????????????????????
                            refreshMark++;
                            if (refreshMark > REFRESH_INTERAL) {
                                initLotteryOpen();
                                refreshMark = 0;
                            }
                        }


                        if (firstOpenHistory == null) {
                            //?????????????????????
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

                    if (updateList) {
                        Adapter a = new ActLotteryPlayJD.LotteryOpenHistoryAdapter(listOpen.subList(0, listOpen.size() < 5 ? listOpen.size() : 5));
                        for (int i = 0; i < a.getCount(); i++) {
                            lvOpenCode.addView(a.getView(i, null, lvOpenCode));
                        }
                        mJellyLayout.setRefreshing(false, false);
                    }
                } catch (Exception e) {
//                    Toasts.show(ActLotteryPlayJD.this, "??????????????????!", false);
                    e.printStackTrace();
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
            tvIssueDate.setText(Html.fromHtml(String.format("NO.<font color=\"#3e97fa\">%s</font>????????????", issue)));
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
            layoutOpenCode.setVisibility(View.VISIBLE);
        } else {
            ivOpenning.setVisibility(View.VISIBLE);
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
                    Toasts.show(ActLotteryPlayJD.this, "??????????????????!", false);
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


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvClear:
                resetCurrentPlay();
                break;
            default:

        }
    }

    public void resetCurrentPlay() {
        fragContainer.getCurrentPlayFrag().getCtrlPlay().reset();
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
    public Context getContext() {
        return this;
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
                    defaultPlayName = "?????????";
                } else if ("11X5".equals(category)) {
                    defaultPlayName = "?????????";
                } else if ("K3".equals(category)) {
                    defaultPlayName = "??????";
                } else if ("PK10".equals(category)) {
                    defaultPlayName = "??????";
                }
                if (Strs.isEmpty(defaultPlayName)) {
                    return;
                }
                selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlayJD(category, defaultPlayName);
                tvTitle.setText(selectedLotteryPlay.getShowName());
                fragContainer.showPlay(category, defaultPlayName);
                fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
            }
        });
    }

    public void showLoading() {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                DialogsTouCai.showProgressDialog(ActLotteryPlayJD.this, "");
            }
        });

    }

    public void hideLoading() {
        DialogsTouCai.hideProgressDialog(ActLotteryPlayJD.this);
    }

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
                tvTitle.setText(selectedLotteryPlay.getShowName());
                fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
                fragContainer.showPlay(category, selectedPlayCode);
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
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        EventBus.getDefault().unregister(this);

    }

    public static boolean syncCookieWithHttp(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        com.tencent.smtt.sdk.CookieSyncManager.createInstance(Global.app);
        com.tencent.smtt.sdk.CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();//??????
        String lastCookie = AbHttpAO.getIns().getLastCookie();
        String[] arrCookie = lastCookie.split(";");
        for (int i = 0; i < arrCookie.length; i++) {
            if (Strs.isNotEmpty(arrCookie[i])) {
                cookieManager.setCookie(httpUrl.host(), arrCookie[i]);//?????????????????????????????????????????????session id???"key=value"????????????cookie??????
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
                convertView.setTag(new LotteryOpenHistoryAdapter.ViewHolder(convertView));
            }

            LotteryOpenHistoryAdapter.ViewHolder vh = (LotteryOpenHistoryAdapter.ViewHolder) convertView.getTag();

            LotteryOpenHistory history = list.get(position);
            vh.issue.setText(String.format("???%s???", history.getIssue()));
//            vh.category.setText(history.getLottery());

            String[] codes;
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

        static int arr = R.array.lottery_play_menu_jd;
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
