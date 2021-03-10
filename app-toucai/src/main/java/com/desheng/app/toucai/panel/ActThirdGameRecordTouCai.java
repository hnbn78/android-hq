package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.ThirdGameBean;
import com.desheng.base.model.ThirdGameType;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.pearl.view.rmondjone.locktableview.LockTableView;
import com.pearl.view.rmondjone.locktableview.ProgressStyle;
import com.pearl.view.rmondjone.locktableview.XRecyclerView;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActThirdGameRecordTouCai extends AbAdvanceActivity implements View.OnClickListener {

    private TextView tv_all_third, tv_all_game, tvStartDate, tvEndDate;
    private RadioGroup rgDate;
    private RadioButton rbYesterday, rbDay, rbWeek, rbMonth;
    private Button btnSearch;

    private LinearLayout contentView;
    private List<ThirdGameBean.ListBean> listGame = new ArrayList<>();
    private ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();
    private LockTableView mLockTableView;
    private int totalCount;
    private XRecyclerView mXRecyclerView;

    private long startMills = 0;
    private long stopMills = 0;
    private int currPage = 0;

    private Date currDate;
    private String gameType;

    public static final String THIRD_TYPE_KY = "ky";
    public static final String THIRD_TYPE_IM = "im";
    public static final String THIRD_TYPE_AGIN = "agin";
    public static final String THIRD_TYPE_GM = "gm";
    private String current_third_type = "";
    private ListMenuPopupWindow gamePopup = null;
    private String arrayGames[] = null;
    private String arrayThird[] = {"KY棋牌", "AGIN", "IM体育","德胜棋牌"};
    private String arrayKeys[] = {THIRD_TYPE_KY, THIRD_TYPE_AGIN, THIRD_TYPE_IM,THIRD_TYPE_GM};
    private ListMenuPopupWindow thirdPopup = null;
    private ArrayList<ThirdGameType> thirdGameTypeList;
    private boolean isSearching = false;
    private int searchCnt = 0;

    public static void launch(Activity ctx) {
        simpleLaunch(ctx, ActThirdGameRecordTouCai.class);
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_third_game_record_toucai;
    }
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "第三方游戏报表");
        setStatusBarTranslucentAndLightContentWithPadding();

        currDate = new Date();
        String start = Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
        String end = Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
        startMills = Dates.getMillisOfStr(start, Dates.dateFormatYMD);
        stopMills = Dates.getMillisOfStr(end, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
        contentView = (LinearLayout) findViewById(R.id.contentView);
        tv_all_third = (TextView) findViewById(R.id.tv_all_third);
        rgDate = (RadioGroup) findViewById(R.id.rgDate);
        rbYesterday = (RadioButton) findViewById(R.id.rbYesterday);
        rbDay = (RadioButton) findViewById(R.id.rbToday);
        rbWeek = (RadioButton) findViewById(R.id.rbWeek);
        rbMonth = (RadioButton) findViewById(R.id.rbMonth);
        tv_all_game = (TextView) findViewById(R.id.tv_all_game);
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        thirdPopup = new ListMenuPopupWindow(ActThirdGameRecordTouCai.this, AbDevice.SCREEN_WIDTH_PX, arrayThird);
        initListener();

        tv_all_third.setText(arrayThird[0]);
        current_third_type= "";
        gameType="";
        //getGameType(current_third_type);
        getGameReport(gameType, startMills, startMills, current_third_type, currPage, 50);
    }

    private void initListener() {
        String currDateString = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);

        tvStartDate.setText(currDateString);
        tvEndDate.setText(currDateString);

        startMills = Dates.getMillisOfStr(currDateString, Dates.dateFormatYMD);
        stopMills = Dates.getMillisOfStr(currDateString, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;

        rgDate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String start = "";
                String end = "";
                
                if (checkedId == R.id.rbToday) {
                    start = Dates.getStringByOffset(currDate, Dates.dateFormatYMD, Calendar.DAY_OF_YEAR, 0);
                    end = Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
                    tvEndDate.setText(end);
                    tvStartDate.setText(start);
                    getGameReport(gameType, startMills, stopMills, current_third_type, currPage, 10);
                } else if (checkedId == R.id.rbYesterday) {
                    start = Dates.getStringByOffset(currDate, Dates.dateFormatYMD, Calendar.DAY_OF_YEAR, -1);
                    end = Dates.getStringByOffset(currDate, Dates.dateFormatYMD, Calendar.DAY_OF_YEAR, -1);
                    tvEndDate.setText(end);
                    tvStartDate.setText(start);
                    getGameReport(gameType, startMills, stopMills, current_third_type, currPage, 10);
                } else if (checkedId == R.id.rbWeek) {
                    start =Dates.getFirstDayOfWeek(Dates.dateFormatYMD);
                    end = Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
                    tvStartDate.setText(start);
                    tvEndDate.setText(end);
                    getGameReport(gameType, startMills, stopMills, current_third_type, currPage, 10);
                } else if (checkedId == R.id.rbMonth) {
                    start =Dates.getFirstDayOfMonth(Dates.dateFormatYMD);
                    end =  Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
                    tvStartDate.setText(start);
                    tvEndDate.setText(end);
                    getGameReport(gameType, startMills, stopMills, current_third_type, currPage, 10);
                }
            }
        });

        tv_all_game.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        tv_all_third.setOnClickListener(this);

        thirdPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                String lotteryString = arrayThird[position];
                tv_all_third.setText(lotteryString);
                current_third_type = arrayKeys[position];
                getGameType(current_third_type);
            }
        });

        initLockTable();
    }

    @Override
    public void onClick(View v) {

        if (v == tv_all_third) {
            if (null != thirdPopup) {
                thirdPopup.showAsDropDown(tv_all_third, 0, 0);//显示在rl_spinner的下方
            }
        } else if (v == tv_all_game) {
            if (gamePopup != null) {
                gamePopup.showAsDropDown(tv_all_game, 0, 0);//显示在rl_spinner的下方
            }
        } else if (v == tvStartDate) {
            Calendar calendar = Calendar.getInstance();
            showDateDialog(tvStartDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));

        } else if (v == tvEndDate) {
            Calendar calendar = Calendar.getInstance();
            showDateDialog(tvEndDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));

        } else if (v == btnSearch) {

            getGameReport(gameType, startMills, stopMills, current_third_type, currPage, 50);
        }
    }

    private void initLockTable() {
        addTableHeader();
        mLockTableView = new LockTableView(this, contentView, mTableDatas);
        mLockTableView.setLockFristColumn(true) //是否锁定第一列
                .setLockFristRow(true) //是否锁定第一行
                .setMaxColumnWidth(80) //列最大宽度
                .setMinColumnWidth(80) //列最小宽度
                .setMinRowHeight(40)//行最小高度
                .setMaxRowHeight(40)//行最大高度
                .setTextViewSize(12) //单元格字体大小
                .setFristRowBackGroudColor(R.color.colorPrimaryInverse)//表头背景色
                .setTableHeadTextColor(R.color.black)//表头字体颜色
                .setTableContentTextColor(R.color.black)//单元格字体颜色
                .setNullableString("") //空值替换值
                .setTableViewListener(new LockTableView.OnTableViewListener() {
                    @Override
                    public void onTableViewScrollChange(int x, int y) {
//                        Log.e("滚动值","["+x+"]"+"["+y+"]");
                    }
                })//设置横向滚动回调监听
                .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
                    @Override
                    public void onLeft(HorizontalScrollView view) {
//                        Log.e("滚动边界","滚动到最左边");
                    }

                    @Override
                    public void onRight(HorizontalScrollView view) {
//                        Log.e("滚动边界","滚动到最右边");
                    }
                })//设置横向滚动边界监听
                .setOnLoadingListener(new LockTableView.OnLoadingListener() {
                    @Override
                    public void onRefresh(XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        ActThirdGameRecordTouCai.this.mXRecyclerView = mXRecyclerView;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                currPage = 0;

                                getGameReport(gameType, startMills, stopMills, current_third_type, currPage, 10);
                            }
                        }, 1000);
                    }

                    @Override
                    public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        ActThirdGameRecordTouCai.this.mXRecyclerView = mXRecyclerView;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                currPage ++;

                                getGameReport(gameType, startMills, stopMills, current_third_type, currPage, 10);

                            }
                        }, 1000);
                    }
                })
                .setOnItemClickListenter(new LockTableView.OnItemClickListenter() {
                    @Override
                    public void onItemClick(View item, int position) {
                    }
                })
                .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
                    @Override
                    public void onItemLongClick(View item, int position) {
                    }
                })
                .setOnItemSeletor(R.color.dashline_color)//设置Item被选中颜色
                .show(); //显示表格,此方法必须调用
        mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
        mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.BallRotate);
    }


    public void addTableHeader() {
        ArrayList<String> mfristData = new ArrayList<String>();

        //标题
        mfristData.add("游戏名称");
        mfristData.add("投注时间");
        mfristData.add("投注金额");
        mfristData.add("派彩");
        mfristData.add("状态");

        mTableDatas.add(mfristData);
    }

    private void getGameType(final String thirdtype) {
        HttpAction.getGameType(null, thirdtype, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
                entity.putField("data", new TypeToken<ArrayList<ThirdGameType>>() {
                }.getType());

            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && getField(extra, "data", null) != null) {
                    thirdGameTypeList = getFieldObject(extra, "data", ArrayList.class);

                    ThirdGameType gameTypeBean= new ThirdGameType();
                    gameTypeBean.setKey("");
                    gameTypeBean.setValue("全部游戏");
                    thirdGameTypeList.add(0,gameTypeBean);

                    arrayGames = new String[thirdGameTypeList.size()];

                    for (int i = 0; i < arrayGames.length; i++) {
                        arrayGames[i] = thirdGameTypeList.get(i).getValue();
                    }

                    tv_all_game.setText(arrayGames[0]);
                    tv_all_game.setTag(0);
                    gamePopup = new ListMenuPopupWindow(ActThirdGameRecordTouCai.this, AbDevice.SCREEN_WIDTH_PX, arrayGames);

                    gamePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
                        @Override
                        public void click(int position, View view) {
                            String lotteryString = arrayGames[position];
                            tv_all_game.setText(lotteryString);
                            tv_all_game.setTag(thirdGameTypeList.get(position).getKey());
                            gameType = thirdGameTypeList.get(position).getKey();
                        }
                    });
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }
    
    /**
     * 获取第三方游戏记录
     *
     * @param gameType
     * @param betDateBegin
     * @param betDateEnd
     * @param page
     * @param size
     */
    private void getGameReport(String gameType, long betDateBegin, long betDateEnd, String game, final int page, int size) {
        if (isSearching) {
            return;
        }
        isSearching = true;
        searchCnt = 0;
        String startTime = Dates.getStringByFormat(betDateBegin, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(betDateEnd, Dates.dateFormatYMDHMS);
        mTableDatas.clear();
        mLockTableView.setTableDatas(mTableDatas);
        addTableHeader();
        for (int i = 0; i < arrayKeys.length; i++) {
            doGetGameReport(gameType, arrayKeys[i], page, size, startTime, stopTime);
        }
    }
    
    //做"异步加载"哦
    private void doGetGameReport(String gameType, String game, final int page, int size, String startTime, String stopTime) {
        HttpActionTouCai.getThirdReport(null, gameType, startTime, stopTime, page, size, game, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActThirdGameRecordTouCai.this, "");
                    }
                });
            }
    
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", ThirdGameBean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                listGame.clear();
                if (code == 0 && error == 0) {
                    ThirdGameBean thirdGameBean = getFieldObject(extra, "data", ThirdGameBean.class);
                    if (thirdGameBean != null) {
                        if (page == 0) {
                            //listGame.clear();
                        }
                        totalCount = thirdGameBean.getTotalCount();
                        listGame.addAll(thirdGameBean.getList());
                        for (int i = 0; i < listGame.size(); i++) {
                            ArrayList<String> mRowDatas = new ArrayList<String>();
                            ThirdGameBean.ListBean listItemBean = listGame.get(i);
                            
                            mRowDatas.add(listItemBean.getGameName());
                            try {
                                mRowDatas.add(Dates.getStringByFormat(Long.parseLong(listItemBean.getDate()),Dates.dateFormatYMD));
                            }catch (NumberFormatException nfexception){
                                mRowDatas.add(listItemBean.getDate() );
                            }
                            
                            mRowDatas.add(Nums.formatDecimal(listItemBean.getConfirmAmount(),3));
                            mRowDatas.add(Nums.formatDecimal(listItemBean.getAwardAmount(),3));
                            mRowDatas.add("无");
                            mTableDatas.add(mRowDatas);
                        }

                        mLockTableView.setTableDatas(mTableDatas);
                    } else {
                        Toasts.show(ActThirdGameRecordTouCai.this, msg, false);
                    }
                }

                return true;
            }
            
            @Override
            public void onFinish() {
                super.onFinish();
                if (ActThirdGameRecordTouCai.this.mXRecyclerView != null) {
                    ActThirdGameRecordTouCai.this.mXRecyclerView.refreshComplete();
                    ActThirdGameRecordTouCai.this.mXRecyclerView.loadMoreComplete();
                }
                searchCnt ++;
                if (searchCnt >= 3) {
                    getLayout().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DialogsTouCai.hideProgressDialog(ActThirdGameRecordTouCai.this);
                        }
                    }, 500);
                    isSearching = false;
                }
            }
        });
    }
    
    private void showDateDialog(final View view, List<Integer> date) {
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(this);
        builder.setSelectYear(date.get(0) - 1)
                .setSelectMonth(date.get(1) - 1)
                .setSelectDay(date.get(2) - 1)
                .setOnDateSelectedListener(new DatePickerDialog.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int[] dates) {
                        String text = dates[0] + "-" + (dates[1] > 9 ? dates[1] : ("0" + dates[1])) + "-"
                                + (dates[2] > 9 ? dates[2] : ("0" + dates[2]));
                        if (view == tvStartDate) {
                            startMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD);
                            tvStartDate.setText(text);

                        } else if (view == tvEndDate) {
                            stopMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
                            tvEndDate.setText(text);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });

        builder.setMaxYear(DateUtil.getYear());
        builder.setMaxMonth(DateUtil.getDateForString(DateUtil.getToday()).get(1));
        builder.setMaxDay(DateUtil.getDateForString(DateUtil.getToday()).get(2));
        DatePickerDialog dateDialog = builder.create();
        dateDialog.show();
    }

}
