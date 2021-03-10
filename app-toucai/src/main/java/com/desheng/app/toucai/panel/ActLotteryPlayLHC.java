package com.desheng.app.toucai.panel;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ListPopupWindow;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.desheng.app.toucai.model.LotteryOpenNext;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.LHCJellyLayout;
import com.desheng.app.toucai.view.PlaySnakeViewLHC;
import com.desheng.base.action.DBAction;
import com.desheng.base.event.DeleteFromCartEvent;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryOpenHistory;
import com.desheng.base.model.LotteryPlayLHCCategory;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.desheng.base.model.LotteryPlayUserInfoLHC;
import com.desheng.base.panel.ActLotteryOpenHC;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.FragmentsHelper;
import com.pearl.view.SimpleCollapse.SimpleNestedScrollView;
import com.pearl.view.jellyrefresh.PullToRefreshLayout;
import com.shark.tc.R;

import net.simonvt.menudrawer.MenuDrawer;

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

/**
 * 彩票玩法页面
 * Created by lee on 2018/3/8.
 */
public class ActLotteryPlayLHC extends AbAdvanceActivity implements View.OnClickListener, LotteryPlayPanelLHC {
    public static final String[] arrRefresh = {UserManagerTouCai.EVENT_USER_LOGINED,
            UserManagerTouCai.EVENT_USER_INFO_UNPDATED};
    public static final int REFRESH_INTERAL = 10;
    private LotteryKind lotteryKind;
    private ViewGroup vgToolbarGroup;
    private MenuDrawer mDrawer;

    /**
     * @param activity
     * @param lotteryId 彩票id
     */
    public static void launch(Activity activity, int lotteryId) {
        Intent itt = new Intent(activity, ActLotteryPlayLHC.class);
        itt.putExtra("lotteryId", lotteryId);
        activity.startActivity(itt);
        DBAction.addOrUpdateUserPlayedLottery(lotteryId);
    }

    private FragLotteryPlayContainerLHC fragContainer;
    private TextView tvIssueDeadLine;
    private TextView tvDeadLine;
    private TextView tvIssueDate;
    private TextView[] tvZodicBallNum = new TextView[7];
    private TextView[] tvZodicBallName = new TextView[7];
    private ViewGroup vgBottomGroup;
    private View tvClear;
    private EditText etMoney;
    private TextView tvBuy;
    private LinearLayout lvOpenCode;
    private PullToRefreshLayout mJellyLayout;
    private LHCJellyLayout mjellyPull;
    private ViewGroup flQuickPlay;
    private SimpleNestedScrollView scrollView;
    private PlaySnakeViewLHC snackView;
    private View vRegist;
    private ImageView ivOpenning;
    private View layoutOpenCode;

    private int lotteryId;
    private LotteryPlayLHCUI selectedLotteryPlay;
    private LotteryPlayLHCCategory selectedLotteryPlayCategory;
    private LotteryPlayUserInfoLHC userPlayInfo;
    private ArrayList<HashMap<String, Object>> listNumAddedCart;

    private String lhcCategory = "";
    private String lhcPlayCode = "";
    private LotteryOpenNext nextIssue;
    private long refreshMark = 0;
    private long refreshHistoryMark;
    private Disposable mDisposable;
    private LotteryOpenHistory firstOpenHistory;
    private boolean isQuickContainerShow = false;

    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_play_lhc;
    }

    @Override
    protected void init() {
        lotteryId = getIntent().getIntExtra("lotteryId", 0);
        lotteryKind = (LotteryKind) LotteryKind.find(lotteryId);
        listNumAddedCart = new ArrayList<>();
        vgToolbarGroup = findViewById(R.id.vgToolbarGroup);
        lvOpenCode = findViewById(R.id.lv_open_code);
        scrollView = findViewById(R.id.sv_scroller);
        snackView = findViewById(R.id.vSnakeBar);
        tvDeadLine = findViewById(R.id.tv_deadline);
        vRegist = findViewById(R.id.layout_regist);
        ivOpenning = findViewById(R.id.iv_openning);
        layoutOpenCode = findViewById(R.id.layout_open_code);

        hideToolbar();
        setToolbarBgColor(R.color.colorPrimary);
        ViewGroup.LayoutParams layoutParams = vgToolbarGroup.getLayoutParams();
        layoutParams.height = getStatusHeight() + getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        vgToolbarGroup.setLayoutParams(layoutParams);
        vgToolbarGroup.setPadding(0, getStatusHeight(), 0, 0);
        setStatusBarTranslucentAndLightContent();

        Glide.with(this).load(R.mipmap.openning).asGif().into(ivOpenning);
        TextView title = findViewById(R.id.tvLotteryTitleCenter);
        title.setText(lotteryKind.getShowName());
        ((View) title.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragLotteryPlayMenuLHC.showIns(ActLotteryPlayLHC.this, lhcCategory, lhcPlayCode, ActLotteryPlayLHC.this);
            }
        });
        findViewById(R.id.ibLotteryPlayRightBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListPopupWindow popup = new ListPopupWindow(ActLotteryPlayLHC.this);
                popup.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_popup_menu_lottery_play));
                popup.setAdapter(new PopupMenuAdapter(ActLotteryPlayLHC.this));
                popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                //近期开奖
                                ActLotteryOpenHC.launch(ActLotteryPlayLHC.this, lotteryId);
                                break;
                            case 1:
                                ActBetRecord.launch(ActLotteryPlayLHC.this);
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
                ActLotteryPlayLHCHistory.launch(ActLotteryPlayLHC.this, lotteryId, "六合彩开奖");
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


        flQuickPlay = findViewById(R.id.fl_quick_play);
        flQuickPlay.findViewById(R.id.btn_quick_play_ctrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isQuickContainerShow = !isQuickContainerShow;

                if (isQuickContainerShow) {
                    fragContainer.getCurrentPlayFrag().getCtrlPlay().getQuickPlayController().onQuickPlayShow();
                    flQuickPlay.findViewById(R.id.fl_quick_play_container).setVisibility(View.VISIBLE);
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_lhc_quick_play__arrow_down);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), (int) (drawable.getMinimumHeight()));
                    ((Button) v).setCompoundDrawables(null, null, drawable, null);
                } else {
                    fragContainer.getCurrentPlayFrag().getCtrlPlay().getQuickPlayController().onQuickPlayHide();
                    flQuickPlay.findViewById(R.id.fl_quick_play_container).setVisibility(View.INVISIBLE);
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_lhc_quick_play__arrow_up);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), (int) (drawable.getMinimumHeight()));
                    ((Button) v).setCompoundDrawables(null, null, drawable, null);
                }
            }
        });

        flQuickPlay.findViewById(R.id.btn_quick_play_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragContainer.getCurrentPlayFrag().getCtrlPlay().getQuickPlayController().onConfirm()) {
                    isQuickContainerShow = false;
                    flQuickPlay.findViewById(R.id.fl_quick_play_container).setVisibility(View.INVISIBLE);
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_lhc_quick_play__arrow_up);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), (int) (drawable.getMinimumHeight()));
                    ((Button) flQuickPlay.findViewById(R.id.btn_quick_play_ctrl)).setCompoundDrawables(null, null, drawable, null);
                }
            }
        });

        flQuickPlay.findViewById(R.id.btn_quick_play_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isQuickContainerShow = false;
                flQuickPlay.findViewById(R.id.fl_quick_play_container).setVisibility(View.INVISIBLE);
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_lhc_quick_play__arrow_up);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), (int) (drawable.getMinimumHeight()));
                ((Button) flQuickPlay.findViewById(R.id.btn_quick_play_ctrl)).setCompoundDrawables(null, null, drawable, null);
                fragContainer.getCurrentPlayFrag().getCtrlPlay().getQuickPlayController().onCancel();
            }
        });

        tvIssueDeadLine = (TextView) findViewById(R.id.tvIssueDeadLine);
        tvIssueDate = findViewById(R.id.tv_issue_title);
        tvZodicBallNum[0] = findViewById(R.id.tv_zodic_num1);
        tvZodicBallNum[1] = findViewById(R.id.tv_zodic_num2);
        tvZodicBallNum[2] = findViewById(R.id.tv_zodic_num3);
        tvZodicBallNum[3] = findViewById(R.id.tv_zodic_num4);
        tvZodicBallNum[4] = findViewById(R.id.tv_zodic_num5);
        tvZodicBallNum[5] = findViewById(R.id.tv_zodic_num6);
        tvZodicBallNum[6] = findViewById(R.id.tv_zodic_num7);
        tvZodicBallName[0] = findViewById(R.id.tv_zodic_name1);
        tvZodicBallName[1] = findViewById(R.id.tv_zodic_name2);
        tvZodicBallName[2] = findViewById(R.id.tv_zodic_name3);
        tvZodicBallName[3] = findViewById(R.id.tv_zodic_name4);
        tvZodicBallName[4] = findViewById(R.id.tv_zodic_name5);
        tvZodicBallName[5] = findViewById(R.id.tv_zodic_name6);
        tvZodicBallName[6] = findViewById(R.id.tv_zodic_name7);

        //初始化开奖
        initLotteryOpen();

        //初始化底部菜单
        vgBottomGroup = (ViewGroup) findViewById(R.id.vgBottomGroup);
        //controller控制清除
        tvClear = findViewById(R.id.tvClear);
        //价钱
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
        etMoney.setSelection(1);
        //controller控制直接购买
        tvBuy = (TextView) findViewById(R.id.tvBuy);

        //直接初始化container
        fragContainer = (FragLotteryPlayContainerLHC) FragmentsHelper.replaceFragments(getSupportFragmentManager(), R.id.vgContent, FragLotteryPlayContainerLHC.newIns(lotteryId, this));

        EventBus.getDefault().register(this);

        //setDefaultPlay();
        //初始化彩票用户数据
        initLotteryPlayInfo();
//        updateStaticOpen();
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
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                //使EditText触发一次失去焦点事件
                v.setFocusable(false);
//                v.setFocusable(true); //这里不需要是因为下面一句代码会同时实现这个功能
                v.setFocusableInTouchMode(true);
                return true;
            }
        }
        return false;
    }

    private LotteryPlayLHCUI getCurrentUiConfig() {
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
                String latest = String.format("NO.%s 投注截止时间", issuno);
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
                                String latest = String.format("NO.%s 投注截止时间", issuno);
                                tvIssueDeadLine.setText(latest);
                                tvDeadLine.setText(Dates.dateDiffHHMMSS(nextIssue.getSurplusTime() - aLong));
                            } else if (nextIssue.getSurplusTime() - aLong < 0) {
                                String latest = String.format("NO.%s", issuno);
                                tvIssueDeadLine.setText(latest);
                                tvDeadLine.setText("正在开奖");
                                nextIssue = null;  //截止日期后定时刷新
                                firstOpenHistory = null;
                                refreshHistoryMark = 0;
                                refreshMark = 0;
                                Toasts.show(ActLotteryPlayLHC.this, issuno + "期已截止，投注时请注意期号！", true);
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

                    if (updateList) {
                        Adapter a = new LotteryOpenHistoryAdapter(listOpen.subList(0, listOpen.size() < 5 ? listOpen.size() : 5));
                        for (int i = 0; i < a.getCount(); i++) {
                            lvOpenCode.addView(a.getView(i, null, lvOpenCode));
                        }
                        mJellyLayout.setRefreshing(false, false);
                    }
                } catch (Exception e) {
//                    Toasts.show(ActLotteryPlayLHC.this, "暂无开奖信息!", false);
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
        String currentIssue = String.valueOf(Strs.parse(nextIssue == null ? "0" : nextIssue.getIssue(), 0) - 1);
        if (nextIssue != null)
            tvIssueDate.setText(Html.fromHtml(String.format("第<font color=\"#4A90E2\">%s</font>期开奖结果", currentIssue)));
        else
            tvIssueDate.setText("");

        LotteryOpenHistory history = null;
        for (LotteryOpenHistory hi : listOpen) {
            if (nextIssue != null
                    && hi != null
                    && hi.getCode() != null
                    && Strs.isEqual(hi.getIssue(), currentIssue)) {
                history = hi;
                break;
            }
        }
        updateOpenCode(history);
    }

    private void updateOpenCode(LotteryOpenHistory history) {
        String[] codes = {"0", "0", "0", "0", "0", "0", "0",};
        String[] names = new String[7];
        if (history != null && history.getCode() != null) {
            String[] arr = history.getCode().split(",");

            for (int i = 0; i < 7; i++) {
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

        for (int i = 0; i < 7; i++) {
            names[i] = UserManager.getIns().getMapZodiacName().get(Strs.parse(codes[i], 0));
        }

        tvZodicBallNum[0].setText(codes[0]);
        tvZodicBallNum[1].setText(codes[1]);
        tvZodicBallNum[2].setText(codes[2]);
        tvZodicBallNum[3].setText(codes[3]);
        tvZodicBallNum[4].setText(codes[4]);
        tvZodicBallNum[5].setText(codes[5]);
        tvZodicBallNum[6].setText(codes[6]);

        tvZodicBallName[0].setText(names[0]);
        tvZodicBallName[1].setText(names[1]);
        tvZodicBallName[2].setText(names[2]);
        tvZodicBallName[3].setText(names[3]);
        tvZodicBallName[4].setText(names[4]);
        tvZodicBallName[5].setText(names[5]);
        tvZodicBallName[6].setText(names[6]);
    }

    private void initLotteryPlayInfo() {
        HttpActionTouCai.getLotteryPlayInfoLHC(this, lotteryKind.getCode(), new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                showLoading();
            }

            @Override
            public boolean onGetString(String str) {
                if (str != null) {
                    userPlayInfo = new Gson().fromJson(str, LotteryPlayUserInfoLHC.class);
                    fragContainer.setUserPlayInfo(userPlayInfo);
                    setDefaultPlay();
                } else {
                    Toasts.show(ActLotteryPlayLHC.this, "获取数据失败!", false);
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
                Toasts.show(this, "添加号码成功!", true);
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
    public Context getContext() {
        return this;
    }

    @Override
    public long getCurrentHit() {
        return getSnakeBar().getHitCount();
        //return fragContainer.getSnakeBar().getHitCount();
    }

    @Override
    public void setCurrentUserPlayInfo(Object userPlayInfo) {
        fragContainer.setUserPlayInfo((LotteryPlayUserInfoLHC) userPlayInfo);
    }

    @Override
    public BaseLotteryPlayFragmentLHC getCurrentPlay() {
        return fragContainer.getCurrentPlayFrag();
    }

    @Override
    public String getCurrentModel() {
        return fragContainer.getFootView().getModel();
    }

    @Override
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
        return null;
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
    public LotteryPlayLHCUI getSelectedLotteryPlay() {
        return selectedLotteryPlay;
    }

    @Override
    public void setDefaultMenu() {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                fragContainer.getPlayContainer().setVisibility(View.GONE);
                fragContainer.getMenuContainer().setVisibility(View.VISIBLE);
            }
        });
    }

    public void setDefaultPlay() {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                TextView title = findViewById(R.id.tvLotteryTitleCenter);
                title.setText("特码");

                lhcCategory = "特码";
                String defaultPlayName = "特码";
                selectedLotteryPlayCategory = UserManagerTouCai.getIns().getLotteryPlayLHCCategory(lhcCategory);
                selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlayLHCUI(lhcCategory, defaultPlayName);
                LotteryPlayLHCCategory.DataBean bean = UserManagerTouCai.getIns().getLotteryPlayLHCCategoryDataBean(lhcCategory, defaultPlayName);
                lhcPlayCode = bean.getLotteryCode();
                fragContainer.showPlay(lhcCategory, bean.getLotteryCode());
                fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
                fragContainer.getMenuContainer().setVisibility(View.GONE);
            }
        });
    }

    public void showLoading() {
        DialogsTouCai.showProgressDialog(ActLotteryPlayLHC.this, "");
    }

    public void hideLoading() {
        DialogsTouCai.hideProgressDialog(ActLotteryPlayLHC.this);
    }

    public void showMenu() {
        showLoading();

        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                fragContainer.getMenuContainer().setVisibility(View.VISIBLE);
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
        fragContainer.getMenuContainer().setVisibility(View.GONE);
    }

    public NestedScrollView getScrollView() {
        return scrollView;
    }

    public PlaySnakeViewLHC getSnakeBar() {
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
        lhcCategory = category;
        lhcPlayCode = selectedPlayCode;
        //AbDebug.log(AbDebug.TAG_APP, "before showLoading" );
        //getSnakeBar().clearAll();
//        showLoading();
        //AbDebug.log(AbDebug.TAG_APP, "after showLoading" );
//        hideMenu();
        //AbDebug.log(AbDebug.TAG_APP, "hideMenu" );
        fragContainer.removeCurrentPlay();
        // AbDebug.log(AbDebug.TAG_APP, "removeCurrentPlay" );
        flQuickPlay.setVisibility(View.GONE);
        isQuickContainerShow = false;
        flQuickPlay.findViewById(R.id.fl_quick_play_container).setVisibility(View.INVISIBLE);
        ((ViewGroup) flQuickPlay.findViewById(R.id.fl_quick_play_content)).removeAllViews();
        showLoading();

        ThreadCollector.getIns().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlayLHCMap().get(selectedPlayCode);
                selectedLotteryPlayCategory = UserManagerTouCai.getIns().getLotteryPlayLHCCategory(category);
                //AbDebug.log(AbDebug.TAG_APP, "selectedLotteryPlay" );
                fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
                fragContainer.showPlay(lhcCategory, selectedPlayCode);
                LotteryPlayLHCCategory.DataBean dataBean = UserManager.getIns().getLotteryPlayLHCCategoryDataBeanByPlayCode(category, selectedPlayCode);
                TextView title = findViewById(R.id.tvLotteryTitleCenter);
                if (Strs.isEqual(dataBean.getShowName(), selectedLotteryPlayCategory.getTitleName())) {
                    title.setText(selectedLotteryPlayCategory.getTitleName());
                } else {
                    title.setText(dataBean.getShowName());
                }
                etMoney.setText("1");
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

    public ViewGroup getBottomGroup() {
        return vgBottomGroup;
    }

    @Override
    public int getLotteryId() {
        return lotteryId;
    }

    @Override
    public ViewGroup getQuickPlay() {
        return flQuickPlay;
    }

    @Override
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

    private class LotteryOpenHistoryAdapter extends BaseAdapter {
        private int[] ballBg = {
                R.drawable.ic_lhc_ball_red, // not used
                R.drawable.ic_lhc_ball_red,
                R.drawable.ic_lhc_ball_blue,
                R.drawable.ic_lhc_ball_green,
        };
        private List<LotteryOpenHistory> list;

        public LotteryOpenHistoryAdapter(List<LotteryOpenHistory> list) {
            this.list = list;
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
                convertView = View.inflate(parent.getContext(), R.layout.item_lottery_play_lhc_open_record, null);
                convertView.setTag(new ViewHolder(convertView));
            }

            ViewHolder vh = (ViewHolder) convertView.getTag();

            LotteryOpenHistory history = list.get(position);
            vh.issue.setText(String.format("第%s期", history.getIssue()));
//            vh.category.setText(history.getLottery());

            String[] codes;
            if (history.getCode() == null) {
                codes = new String[]{"0", "0", "0", "0", "0", "0", "0"};
            } else {
                codes = history.getCode().split(",");
            }

            for (int i = 0; i < 10; i++) {
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
                R.drawable.ic_menu_lottery_play_recent,
                R.drawable.ic_menu_lottery_play_touzhu,
        };

        static int arr = R.array.lottery_play_menu_lhc;
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
