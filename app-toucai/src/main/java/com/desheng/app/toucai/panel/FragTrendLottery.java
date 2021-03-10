package com.desheng.app.toucai.panel;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.ArraysAndLists;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.app.hubert.guide.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.helper.RecyclerViewPageChangeListenerHelper;
import com.desheng.app.toucai.view.CanOnScrollLinstenScrollView;
import com.desheng.base.action.HttpAction;
import com.desheng.base.adapter.CellDataAdapter;
import com.desheng.base.adapter.TitleDataAdapter;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.trendchart.HeadCustomGridView;
import com.pearl.view.trendchart.HeaderHorizontalScrollView;
import com.pearl.view.trendchart.LeftNumberCustomListView;
import com.pearl.view.trendchart.LeftNumberSynchScrollView;
import com.pearl.view.trendchart.ScrollChangeCallback;
import com.pearl.view.trendchart.TrendScrollViewWidget;
import com.pearl.view.trendchart.TrendView;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 趋势
 */
public class FragTrendLottery extends AbBaseFragment implements ScrollChangeCallback {

    private HorizontalScrollView trend_RadioGroup_header_scroll;

    @Override
    public int getLayoutId() {
        return R.layout.act_lottery_trand;
    }

    //容器区域
    private LeftNumberSynchScrollView mLeftScroll;
    private TrendScrollViewWidget mContentScroll;
    private HeaderHorizontalScrollView mHeadScroll;
    //数据区域
    private LeftNumberCustomListView mListView;
    private GridView mHeadGridView1;

    private List mList = null;
    private List mHeadData1 = null;
    private List mHeadData2 = null;

    //当前手机屏幕的密度:基准mdpi
    private int mDenisty = 160;

    //key为item中设置背景色view的hashCode,唯一;
    //value为-1的时候是未选中;
    //value为1的时候是选中;
    private HashMap<Object, Integer> mContainer = new HashMap<>();
    //选择了多少个球
    private String[] mSelectData = new String[49];
    private HeadCustomGridView mHeadGridView2;

    private TrendView vTrend;
    private ILotteryKind lottery;
    private String[] arrIssue;
    private String[] arrBalls;
    private RadioGroup mTrendRadioGroup;
    private int mHeadRadioGrouptemp;
    private RecyclerView trenRecyclerview;

    @Override
    public void init(View root) {
        mDenisty = getScreenDenisty();
        mListView = root.findViewById(com.desheng.base.R.id.lv_number);
        mHeadGridView1 = root.findViewById(com.desheng.base.R.id.grid_trend_header1);
        mHeadGridView2 = root.findViewById(com.desheng.base.R.id.grid_trend_header2);
        mTrendRadioGroup = root.findViewById(com.desheng.base.R.id.trend_RadioGroup);
        trenRecyclerview = root.findViewById(com.desheng.base.R.id.trenRecyclerview);
        trend_RadioGroup_header_scroll = root.findViewById(com.desheng.base.R.id.trend_RadioGroup_header_scroll);

        mLeftScroll = root.findViewById(com.desheng.base.R.id.scroll_left);
        mContentScroll = root.findViewById(com.desheng.base.R.id.scroll_content);
        mHeadScroll = root.findViewById(com.desheng.base.R.id.trend_header_scroll);
        //左边期号的监听器
        mLeftScroll.setScrollViewListener(this);
        //中间走势图的监听器
        mContentScroll.setScrollViewListener(this);
        //走势图顶部的监听器
        mHeadScroll.setScrollViewListener(this);

        trenRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        vTrend = root.findViewById(com.desheng.base.R.id.vTrend);
        getTrendData();
    }

    private void getTrendData() {
        int id = 0;
        //经典菲律宾5分彩兼容
        if (lottery.getId() == CtxLottery.getIns().lotteryKind("FeiLvBin_5Fen_JD").getId()) {
            id = CtxLottery.getIns().lotteryKind("FeiLvBin_5FEN").getId();
        } else if (lottery.getId() == CtxLottery.getIns().lotteryKind("BeiJing_PK10_JD").getId()) {
            id = CtxLottery.getIns().lotteryKind("BeiJing_PK10").getId();
        } else {
            id = lottery.getId();
        }
        HttpAction.getTrend(this, id, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", String.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                ThreadCollector.getIns().postDelayOnUIThread(100, new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(((ActLotteryMain) context), "");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    String info = getField(extra, "data", "");
                    if (Strs.isNotEmpty(info)) {
                        //走势图顶部区域数据显示
                        int groupPerCount = 0;
                        int digitUnit = 1;
                        boolean isFromOne = false;
                        String typeCode = lottery.getOriginType().getCode();
                        if (typeCode.toLowerCase().contains("ssc") || typeCode.toLowerCase().contains("kuai3")) {
                            isFromOne = false;
                            groupPerCount = 10;
                        } else if (typeCode.toLowerCase().contains("11s5")) {
                            isFromOne = true;
                            digitUnit = 2;
                            groupPerCount = 11;
                        } else if (typeCode.toLowerCase().contains("other")) {
                            if (lottery.getCode().toLowerCase().endsWith("pk10")) {
                                isFromOne = true;
                                groupPerCount = 10;
                                digitUnit = 2;
                            } else {
                                isFromOne = false;
                                groupPerCount = 10;
                            }
                        } else if (typeCode.toLowerCase().contains("jd")) {
                            if (lottery.getCode().toLowerCase().contains("ssc")) {
                                isFromOne = false;
                                groupPerCount = 10;
                            } else if (lottery.getCode().toLowerCase().contains("11yx")) {
                                isFromOne = true;
                                digitUnit = 2;
                                groupPerCount = 11;
                            } else if (lottery.getCode().toLowerCase().contains("pk10")) {
                                isFromOne = true;
                                groupPerCount = 10;
                                digitUnit = 2;
                            } else {
                                isFromOne = false;
                                groupPerCount = 10;
                            }
                        }

                        String[] arr = info.split(",");
                        arrIssue = new String[arr.length + 4];
                        arrBalls = new String[arr.length + 4];
                        int groupCount = 0;
                        for (int i = 0; i < arr.length; i++) {
                            String[] strs = arr[i].split("\\|");
                            arrIssue[i] = strs[0];
                            groupCount = strs[1].length() / digitUnit;
                            arrBalls[i] = strs[1].replaceAll("(.{" + digitUnit + "})", "$1,");
                            arrBalls[i] = arrBalls[i].substring(0, arrBalls[i].length() - 1);
                        }

                        String tempStr = "";
                        for (int i = 0; i < groupCount; i++) {
                            tempStr += Strs.isEmpty(tempStr) ? "0" : ",0";
                        }
                        arrIssue[arr.length] = "出现总次数";
                        arrIssue[arr.length + 1] = "平均遗漏值";
                        arrIssue[arr.length + 2] = "最大遗漏值";
                        arrIssue[arr.length + 3] = "最大连出值";
                        arrBalls[arr.length] = tempStr;
                        arrBalls[arr.length + 1] = tempStr;
                        arrBalls[arr.length + 2] = tempStr;
                        arrBalls[arr.length + 3] = tempStr;

                        bindHeaderRadioGroup(groupPerCount, groupCount);
                        //头部第一条
                        bindHeaderData1(groupPerCount, groupCount);
                        //头部第二条
                        bindHeaderData2(groupPerCount, groupCount, isFromOne);
                        //bindFooterData();
                        //绑定显示期号数据
                        bindIssueData(groupPerCount);
                        //走势图内容
                        bindTrendData(groupCount, groupPerCount, isFromOne);
                    }
                } else {
                    Toasts.show(msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                ThreadCollector.getIns().postDelayOnUIThread(500, new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(context);
                    }
                });
            }
        });
    }

    HashMap<Integer, Integer> rbID = new HashMap<>();

    private void bindHeaderRadioGroup(int groupPerCount, int groupCount) {
        mHeadRadioGrouptemp = ScreenUtils.getScreenWidth(context) - getResources().getDimensionPixelSize(R.dimen.trend_issuno_withd);

        for (int i = 0; i < groupCount; i++) {
            RadioButton radiogbtn = new RadioButton(context);

            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, ScreenUtils.dp2px(context, 5), 0);
            radiogbtn.setLayoutParams(lp);

            radiogbtn.setBackgroundResource(R.drawable.trend_btn_selector);
            ColorStateList colorStateList = context.getResources().getColorStateList(R.color.trend_text_color);
            radiogbtn.setTextColor(colorStateList);
            radiogbtn.setPadding(ScreenUtils.dp2px(context, 5), ScreenUtils.dp2px(context, 3), ScreenUtils.dp2px(context, 5), ScreenUtils.dp2px(context, 3));
            radiogbtn.setButtonDrawable(0);
            radiogbtn.setText("第" + (i + 1) + "位");

            mTrendRadioGroup.addView(radiogbtn, i);
            rbID.put(radiogbtn.getId(), i);
        }

        ((RadioButton) mTrendRadioGroup.getChildAt(0)).setChecked(true);
        mTrendRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                trenRecyclerview.smoothScrollToPosition(rbID.get(checkedId));
                if (rbID.get(checkedId) > 4) {
                    trend_RadioGroup_header_scroll.smoothScrollTo(ScreenUtils.dp2px(context, 300), 0);
                } else {
                    trend_RadioGroup_header_scroll.smoothScrollTo(0, 0);
                }
            }
        });
    }


    private void bindTrendData(int groupCount, int countPerGroup, boolean isFromOne) {

//        vTrend.setBallGroupCount(groupCount, countPerGroup);
//        vTrend.setData(ArraysAndLists.asList(arrBalls), arrBalls.length - 4, isFromOne);

        ArrayList<Integer> data = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            data.add(i);
        }
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(trenRecyclerview);
        trenRecyclerview.setItemViewCacheSize(10);
        trenRecyclerview.setAdapter(new BaseQuickAdapter<Integer, BaseViewHolder>(R.layout.trend_lottery_layout, data) {
            @Override
            protected void convert(BaseViewHolder helper, Integer item) {
                TrendView trendView = (TrendView) helper.getView(R.id.vTrend);
                CanOnScrollLinstenScrollView scrollContent = (CanOnScrollLinstenScrollView) helper.getView(R.id.scroll_content);
                trendView.setBallGroupCount(groupCount, countPerGroup, item);
                ArrayList<String> asList = ArraysAndLists.asList(arrBalls);
                ArrayList<String> fourTongjiData = new ArrayList();
                int size = asList.size();
                fourTongjiData.add(asList.get(size - 1));
                fourTongjiData.add(asList.get(size - 2));
                fourTongjiData.add(asList.get(size - 3));
                fourTongjiData.add(asList.get(size - 4));
                asList.remove(size - 1);
                asList.remove(size - 2);
                asList.remove(size - 3);
                asList.remove(size - 4);
                Collections.reverse(asList);
                asList.addAll(fourTongjiData);
                trendView.setData(asList, arrBalls.length - 4, isFromOne);


                scrollContent.setOnScrollListener(new CanOnScrollLinstenScrollView.OnScrollListener() {
                    @Override
                    public void onScroll(int scrollY) {
                        mLeftScroll.scrollTo(0, scrollY);
                    }
                });

            }
        });

        trenRecyclerview.addOnScrollListener(new RecyclerViewPageChangeListenerHelper(snapHelper, new RecyclerViewPageChangeListenerHelper.OnPageChangeListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Zorro", "选中" + position);
                ((RadioButton) mTrendRadioGroup.getChildAt(position)).setChecked(true);
            }
        }));
    }

    /**
     * 测试期号的数据绑定显示
     */
    private void bindIssueData(int countPerGroup) {
        mList = new ArrayList();
        //新浪彩票的期号是获取前30期
        int length = arrIssue.length;
        for (int i = 0; i < length; i++) {
            mList.add(arrIssue[i]);
        }
        ArrayList fourTongjiData = new ArrayList();
        fourTongjiData.add(mList.get(length-4));
        fourTongjiData.add(mList.get(length-3));
        fourTongjiData.add(mList.get(length-2));
        fourTongjiData.add(mList.get(length-1));
        mList.remove(length-1);
        mList.remove(length-2);
        mList.remove(length-3);
        mList.remove(length-4);
        TitleDataAdapter adapter = new TitleDataAdapter(getContext(), 3, Color.BLACK, countPerGroup);
        Collections.reverse(mList);
        mList.addAll(fourTongjiData);
        adapter.bindData(mList);
        mListView.setAdapter(adapter);
    }

    /**
     * 绑定顶部数据显示:顶部数据只显示在一行;
     *
     * @param groupCount
     */
    private void bindHeaderData1(int groupColumnCount, int groupCount) {
        mHeadData1 = new ArrayList();
        for (int i = 1; i <= groupCount; i++) {
            mHeadData1.add("第" + i + "位");
        }

        TitleDataAdapter adapter = new TitleDataAdapter(getContext(), groupColumnCount, Color.WHITE, groupColumnCount);
        adapter.bindData(mHeadData1);
        int temp = (ScreenUtils.getScreenWidth(context) - getResources().getDimensionPixelSize(R.dimen.trend_issuno_withd)) / groupColumnCount;
        int deltaDp = temp * groupColumnCount;
        //下面的代码是重新定位布局参数;让gridView数据都显示在一行;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(adapter.getCount() * deltaDp,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mHeadGridView1.setLayoutParams(params);
        mHeadGridView1.setColumnWidth(deltaDp);//列宽
        mHeadGridView1.setStretchMode(GridView.NO_STRETCH);//伸展模式
        mHeadGridView1.setNumColumns(adapter.getCount());//共有多少列
        mHeadGridView1.setAdapter(adapter);
    }

    /**
     * 绑定顶部数据显示:顶部数据只显示在一行;
     *
     * @param groupColumnCount
     * @param isFromOne
     */
    private void bindHeaderData2(int groupColumnCount, int groupCount, boolean isFromOne) {
        mHeadData2 = new ArrayList();
        for (int i = 1; i <= groupCount; i++) {
            for (int j = 0; j < groupColumnCount; j++) {
                if (isFromOne) {
                    mHeadData2.add("" + (j + 1));
                } else {
                    mHeadData2.add("" + (j));
                }
            }
        }

        CellDataAdapter adapter = new CellDataAdapter(getContext(), mHeadData2, groupColumnCount);
        int temp = (ScreenUtils.getScreenWidth(context) - getResources().getDimensionPixelSize(R.dimen.trend_issuno_withd)) / groupColumnCount;
        int deltaDp = temp;
        //下面的代码是重新定位布局参数;让gridView数据都显示在一行;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(adapter.getCount() * deltaDp,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mHeadGridView2.setLayoutParams(params);
        mHeadGridView2.setColumnWidth(deltaDp);//列宽
        mHeadGridView2.setStretchMode(GridView.NO_STRETCH);//伸展模式
        mHeadGridView2.setNumColumns(adapter.getCount());//共有多少列
        mHeadGridView2.setAdapter(adapter);
    }

    @Override
    public void changeXScroll(int left) {
        //顶部和底部容器滑动的回调;
        //此时需要同步中间走势的View的位置;
        mContentScroll.scrollTo(left, mContentScroll.getScrollY());
        //同步顶部自身的位置;
        mHeadScroll.scrollTo(left, 0);
    }

    @Override
    public void changeYScoll(int top) {
        //中间走势View滑动位置的改变回调;
        //同步左边期号的Y轴的位置
        mLeftScroll.scrollTo(0, top);
        //同步中间走势View的位置
        mContentScroll.scrollTo(mContentScroll.getScrollX(), top);
        //有走势图头部...
        mHeadScroll.scrollTo(mContentScroll.getScrollX(), 0);
    }

    private int getScreenDenisty() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return dm.densityDpi;
    }

    public static Fragment newIns(int id) {

        FragTrendLottery fragTrendLottery = new FragTrendLottery();
        fragTrendLottery.lottery = CtxLotteryTouCai.getIns().findLotteryKind(id);
        return fragTrendLottery;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLotteryCodeUpdate(LotteryCodeUpdateBean event) {
//        if (event != null && ((ActLotteryMain) context).tabLayout.getSelectedTabPosition() == 3) {
//            getTrendData();
//            if (quickAdapter!=null) {
//                quickAdapter.notifyDataSetChanged();
//            }
//        }
//    }


}
