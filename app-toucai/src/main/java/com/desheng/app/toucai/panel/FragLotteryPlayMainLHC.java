package com.desheng.app.toucai.panel;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Dates;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.helper.BonusPoolActivityHelper;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.AwardPoolDetailoMode;
import com.desheng.app.toucai.model.LotteryOpenNext;
import com.desheng.app.toucai.model.NewUserMission;
import com.desheng.app.toucai.model.eventmode.BonusPoolEventMode;
import com.desheng.app.toucai.model.eventmode.BonusPoolFreshEventMode;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.view.AwardpoolViewModule;
import com.desheng.app.toucai.view.PlaySnakeViewLHC;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.event.DeleteFromCartEvent;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryOpenHistory;
import com.desheng.base.model.LotteryPlayLHCCategory;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.desheng.base.model.LotteryPlayUserInfoLHC;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noober.background.view.BLTextView;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.act.util.FragmentsHelper;
import com.pearl.view.SimpleCollapse.SimpleNestedScrollView;
import com.shark.tc.R;

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

public class FragLotteryPlayMainLHC extends AbBaseFragment implements LotteryPlayPanelLHC, View.OnClickListener {
    private RelativeLayout container;
    private AwardpoolViewModule awardPool;
    private ImageView ivcoins;
    private RecyclerView recycleViewLotteryMethod;
    private BaseQuickAdapter<LotteryPlayLHCCategory, BaseViewHolder> mWnafaAdapter;
    private TextView tv_wanfa;

    public static Fragment newIns(int id, FragBetLottery.OnLotteryMethodChanged onLotteryMethodChanged) {
        FragLotteryPlayMainLHC fragLotteryPlay = new FragLotteryPlayMainLHC();
        fragLotteryPlay.lotteryId = id;
        fragLotteryPlay.onLotteryMethodChanged = onLotteryMethodChanged;
        return fragLotteryPlay;
    }

    public static final String[] arrRefresh = {UserManagerTouCai.EVENT_USER_LOGINED,
            UserManagerTouCai.EVENT_USER_INFO_UNPDATED};
    public static final int REFRESH_INTERAL = 10;
    private LotteryKind lotteryKind;


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
    private ViewGroup flQuickPlay;
    private SimpleNestedScrollView scrollView;
    private PlaySnakeViewLHC snackView;
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

    private FragBetLottery.OnLotteryMethodChanged onLotteryMethodChanged;

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_main_lhc;
    }

    @Override
    public void init(View root) {
        container = ((RelativeLayout) root.findViewById(R.id.containerLHc));
        awardPool = ((AwardpoolViewModule) root.findViewById(R.id.awardLhc));
        ivcoins = ((ImageView) root.findViewById(R.id.ivcoinsLhc));

        lotteryKind = (LotteryKind) LotteryKind.find(lotteryId);
        listNumAddedCart = new ArrayList<>();
        lvOpenCode = root.findViewById(R.id.lv_open_code);
        scrollView = root.findViewById(R.id.sv_scroller);
        snackView = root.findViewById(R.id.vSnakeBar);
        tvDeadLine = root.findViewById(R.id.tv_deadline);
        ivOpenning = root.findViewById(R.id.iv_openning);
        layoutOpenCode = root.findViewById(R.id.layout_open_code);
        recycleViewLotteryMethod = (RecyclerView) root.findViewById(R.id.recycleViewLotteryMethod);

        Glide.with(this).load(R.mipmap.openning).asGif().into(ivOpenning);
        tv_wanfa = root.findViewById(R.id.tv_wanfa);
        tv_wanfa.setOnClickListener(this);
        flQuickPlay = root.findViewById(R.id.fl_quick_play);
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

        tvIssueDeadLine = (TextView) root.findViewById(R.id.tvIssueDeadLine);
        tvIssueDate = root.findViewById(R.id.tv_issue_title);
        tvZodicBallNum[0] = root.findViewById(R.id.tv_zodic_num1);
        tvZodicBallNum[1] = root.findViewById(R.id.tv_zodic_num2);
        tvZodicBallNum[2] = root.findViewById(R.id.tv_zodic_num3);
        tvZodicBallNum[3] = root.findViewById(R.id.tv_zodic_num4);
        tvZodicBallNum[4] = root.findViewById(R.id.tv_zodic_num5);
        tvZodicBallNum[5] = root.findViewById(R.id.tv_zodic_num6);
        tvZodicBallNum[6] = root.findViewById(R.id.tv_zodic_num7);
        tvZodicBallName[0] = root.findViewById(R.id.tv_zodic_name1);
        tvZodicBallName[1] = root.findViewById(R.id.tv_zodic_name2);
        tvZodicBallName[2] = root.findViewById(R.id.tv_zodic_name3);
        tvZodicBallName[3] = root.findViewById(R.id.tv_zodic_name4);
        tvZodicBallName[4] = root.findViewById(R.id.tv_zodic_name5);
        tvZodicBallName[5] = root.findViewById(R.id.tv_zodic_name6);
        tvZodicBallName[6] = root.findViewById(R.id.tv_zodic_name7);

        //初始化开奖
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
        etMoney.setSelection(1);
        //controller控制直接购买
        tvBuy = (TextView) root.findViewById(R.id.tvBuy);

        //直接初始化container
        fragContainer = (FragLotteryPlayContainerLHC) FragmentsHelper.replaceFragments(getFragmentManager(), R.id.vgContent, FragLotteryPlayContainerLHC.newIns(lotteryId, this));

        EventBus.getDefault().register(this);

        //setDefaultPlay();
        //初始化彩票用户数据
        initLotteryPlayInfo();
//        updateStaticOpen();
        initAwardPool();
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
                String latest = String.format("%s 期   倒计时", issuno);
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
                                tvDeadLine.setText("封盘   " + Dates.dateDiffHHMMSS(nextIssue.getSurplusTime() - aLong));
                            } else if (nextIssue.getSurplusTime() - aLong < 0) {
                                String latest = String.format("%s 期   ", issuno);
                                tvIssueDeadLine.setText(latest);
                                tvDeadLine.setText("正在开奖中...");
                                nextIssue = null;  //截止日期后定时刷新
                                firstOpenHistory = null;
                                refreshHistoryMark = 0;
                                refreshMark = 0;
                                Toasts.show(getActivity(), issuno + "期已截止，投注时请注意期号！", true);
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
        String currentIssue = String.valueOf(Strs.parse(nextIssue == null ? "0" : nextIssue.getIssue(), 0) - 1);
       // if (nextIssue != null)
        //    tvIssueDate.setText(Html.fromHtml(String.format("第 <font color=\"#4A90E2\">%s</font> 期开奖结果", currentIssue)));
       // else
       //     tvIssueDate.setText("");

        LotteryOpenHistory history = null;
        for (LotteryOpenHistory hi : listOpen) {
            if (nextIssue != null
                    && hi != null
                    && hi.getCode() != null ) {
                history = hi;
                break;
            }
        }
        if(history!=null){
            tvIssueDate.setText(Html.fromHtml(String.format("第 <font color=\"#4A90E2\">%s</font> 期开奖结果", currentIssue)));
        }else{
            tvIssueDate.setText("");
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


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvClear:
                fragContainer.getCurrentPlayFrag().getCtrlPlay().reset();
                break;
            case R.id.tv_wanfa:
                LotteryPlayLHCUI lotteryPlay = fragContainer.getCurrentPlayFrag().getLotteryPlay();
                if (lotteryPlay==null) {
                    return;
                }
                DialogsTouCai.showInstructionDialog(context, lotteryPlay.getGameIntroduce());
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
        return getContext();
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
        return lhcPlayCode;
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
                lhcCategory = "特码";
                String defaultPlayName = "特码";

                String defaultPlayCode = "lhc_tema";

                if (Strs.isNotEmpty(UserManagerTouCai.getIns().getlastLotteryPlay(lotteryId))) {
                    defaultPlayCode = UserManagerTouCai.getIns().getlastLotteryPlay(lotteryId);
                    if (defaultPlayCode != null) {
                        if (defaultPlayCode.contains("lhc_tema")) {
                            lhcCategory = "特码";
                            getQuickPlay().setVisibility(View.VISIBLE);
                        } else if (defaultPlayCode.contains("lhc_zhengma")) {
                            lhcCategory = "正码";
                            getQuickPlay().setVisibility(View.VISIBLE);
                        } else if (defaultPlayCode.contains("lhc_BB")) {
                            lhcCategory = "半波";
                            getQuickPlay().setVisibility(View.VISIBLE);
                        } else if (defaultPlayCode.contains("lhc_SX")) {
                            lhcCategory = "生肖";
                            getQuickPlay().setVisibility(View.VISIBLE);
                        } else if (defaultPlayCode.contains("lhc_WH")) {
                            lhcCategory = "尾数";
                            getQuickPlay().setVisibility(View.VISIBLE);
                        } else if (defaultPlayCode.contains("lhc_ZF")) {
                            lhcCategory = "总分";
                            getQuickPlay().setVisibility(View.GONE);
                        } else if (defaultPlayCode.contains("lhc_BZ")) {
                            lhcCategory = "不中";
                            getQuickPlay().setVisibility(View.GONE);
                        }
                    }
                }

                selectedLotteryPlayCategory = UserManagerTouCai.getIns().getLotteryPlayLHCCategory(lhcCategory);

                selectedLotteryPlay = UserManagerTouCai.getIns().getLotteryPlayLHCUIByCode(lhcCategory, defaultPlayCode);

                LotteryPlayLHCCategory.DataBean dataBean = UserManagerTouCai.getIns().getLotteryPlayLHCCategoryDataBeanByPlayCode(lhcCategory, defaultPlayCode);
                lhcPlayCode = defaultPlayCode;
                fragContainer.showPlay(lhcCategory, defaultPlayCode);


//                if (dataBean != null && Strs.isNotEmpty(dataBean.getShowName())) {
//                    ((ActLotteryMain) getActivity()).tvTitle.setText(dataBean.getShowName());
//                }

                fragContainer.getPlayContainer().setVisibility(View.VISIBLE);
                fragContainer.getMenuContainer().setVisibility(View.GONE);

                initWanfaRcview();

            }
        });
    }

    public void showLoading() {
        DialogsTouCai.showProgressDialog(getActivity(), "");
    }

    public void hideLoading() {
        DialogsTouCai.hideProgressDialog(getActivity());
    }

    public void showMenu() {
        if (userPlayInfo != null) {
            FragLotteryPlayMenuLHC.showIns((AppCompatActivity) getActivity(), lhcCategory, lhcPlayCode, this);
        }
    }

    BaseQuickAdapter<LotteryPlayLHCCategory.DataBean, BaseViewHolder> innerWanfaAdapter;

    private void initWanfaRcview() {
        List<LotteryPlayLHCCategory> mWanfacategories = UserManager.getIns().getListLotteryPlayLHCCategory();
        LotteryPlayLHCCategory currCatgory = UserManager.getIns().getLotteryPlayLHCCategory(lhcCategory);
        LotteryPlayLHCCategory.DataBean currPlay = UserManager.getIns().getLotteryPlayLHCCategoryDataBeanByPlayCode(lhcCategory, lhcPlayCode);

        for (LotteryPlayLHCCategory wanfacategory : mWanfacategories) {
            if (Strs.isEqual(wanfacategory.getTitleName(), currCatgory.getTitleName())) {
                wanfacategory.setChecked(true);
            } else {
                wanfacategory.setChecked(false);
            }

            for (LotteryPlayLHCCategory.DataBean dataBean : wanfacategory.getData()) {
                if (Strs.isEqual(dataBean.getLotteryCode(), currPlay.getLotteryCode())) {
                    dataBean.setChecked(true);
                } else {
                    dataBean.setChecked(false);
                }
            }
        }

        recycleViewLotteryMethod.setLayoutManager(new LinearLayoutManager(context));
        mWnafaAdapter = new BaseQuickAdapter<LotteryPlayLHCCategory, BaseViewHolder>(R.layout.item_simple_text, mWanfacategories) {
            @Override
            protected void convert(BaseViewHolder helper, LotteryPlayLHCCategory item) {
                helper.setText(R.id.betName, item.getTitleName());
                ((BLTextView) helper.getView(R.id.betName)).setEnabled(!item.isChecked());

                if (item.isChecked()) {
                    RecyclerView recycleViewWanfaDetail = fragContainer.getCurrentPlayFrag().recycleViewWanfaDetail;
                    if (recycleViewWanfaDetail != null) {
                        recycleViewWanfaDetail.setLayoutManager(new GridLayoutManager(context, 3));
                        innerWanfaAdapter = new BaseQuickAdapter<LotteryPlayLHCCategory.DataBean,
                                BaseViewHolder>(R.layout.item_simple_text, item.getData()) {
                            @Override
                            protected void convert(BaseViewHolder helper, LotteryPlayLHCCategory.DataBean inneritem) {
                                helper.setText(R.id.betName, inneritem.getShowName());
                                ((BLTextView) helper.getView(R.id.betName)).setEnabled(!inneritem.isChecked());

                                ((BLTextView) helper.getView(R.id.betName)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        for (LotteryPlayLHCCategory.DataBean dataBean : item.getData()) {
                                            dataBean.setChecked(false);
                                        }

                                        inneritem.setChecked(true);
                                        innerWanfaAdapter.notifyDataSetChanged();

                                        showPlayAndHideLotteryMenu(item.getTitleName(), inneritem.getLotteryCode());
                                    }
                                });
                            }
                        };
                        View headview = LayoutInflater.from(context).inflate(R.layout.item_simple_text1, null, false);
                        innerWanfaAdapter.addHeaderView(headview);
                        recycleViewWanfaDetail.setAdapter(innerWanfaAdapter);

                    }
                }

                ((BLTextView) helper.getView(R.id.betName)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (LotteryPlayLHCCategory wanfacategory : mWanfacategories) {
                            wanfacategory.setChecked(false);
                        }
                        item.setChecked(true);
                        mWnafaAdapter.notifyDataSetChanged();

                        List<LotteryPlayLHCCategory.DataBean> tempData = item.getData();
                        for (LotteryPlayLHCCategory.DataBean tempDatum : tempData) {
                            tempDatum.setChecked(false);
                        }
                        tempData.get(0).setChecked(true);
                        innerWanfaAdapter.setNewData(tempData);
                        showPlayAndHideLotteryMenu(item.getTitleName(), tempData.get(0).getLotteryCode());
                    }
                });
            }
        };
        recycleViewLotteryMethod.setAdapter(mWnafaAdapter);

        fragContainer.getCurrentPlayFrag().setmPlayviewLoadcomplete(new BaseLotteryPlayFragmentLHC.IPlayviewLoadcomplete() {
            @Override
            public void onPlayviewLoadcomplete() {
                mWnafaAdapter.notifyDataSetChanged();
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
        //flQuickPlay.findViewById(R.id.rl_quick_play_ctrl).setVisibility(View.INVISIBLE);
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
                onLotteryMethodChanged.onLotteryMethodChanged(lotteryKind, selectedPlayCode, selectedLotteryPlayCategory.getTitleName());
                LotteryPlayLHCCategory.DataBean dataBean = UserManager.getIns().getLotteryPlayLHCCategoryDataBeanByPlayCode(category, selectedPlayCode);
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
                initWanfaRcview();
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
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }

        EventBus.getDefault().unregister(this);
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
}
