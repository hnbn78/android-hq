package com.desheng.app.toucai.panel;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.action.HttpAction;
import com.desheng.base.adapter.BonusManageAdapter;
import com.desheng.base.model.BonusPallBean;
import com.desheng.base.model.BonusRuleBean;
import com.desheng.base.model.UserPoints;
import com.desheng.base.model.XjBonusBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noober.background.view.BLEditText;
import com.noober.background.view.BLTextView;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActAgentDividentManagent extends AbAdvanceActivity implements View.OnClickListener {
    private static final int PAGE_SIZE = 20;
    private TextView tv_title;
    private RecyclerView recyclerView, bonusDetailRecycler;
    private BaseQuickAdapter<XjBonusBean.ListBean, BaseViewHolder> adapter;
    private BLTextView mStartTime, mStopTime, mSearch, mBonusPercent;
    private BLEditText mUsername;
    private long startMills = 0;
    private long stopMills = 0;
    private TextView mDayMoneyAtLeast, mUsertype, mBonusdownLine, huizongFenpei, teamYinkuiTotal;
    private BaseQuickAdapter<TempEntity, BaseViewHolder> adapterBounsDetail;
    private int currentPage = 1;
    private List<XjBonusBean.ListBean> listBeans = new ArrayList<>();
    private BonusRuleBean mangeBean;
    private RelativeLayout emptyView;

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_dividend_manage;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("分红管理");
        initView();
    }

    private void initView() {
        recyclerView = ((RecyclerView) findViewById(R.id.recycleView));
        bonusDetailRecycler = ((RecyclerView) findViewById(R.id.bonusDetailRecycler));
        emptyView= ((RelativeLayout) findViewById(R.id.emptyView));
        mStartTime = ((BLTextView) findViewById(R.id.startTime));
        mStopTime = ((BLTextView) findViewById(R.id.stopTime));
        mUsername = ((BLEditText) findViewById(R.id.username));
        mSearch = ((BLTextView) findViewById(R.id.search));
        mDayMoneyAtLeast = ((TextView) findViewById(R.id.dayMoneyAtLeast));
        mUsertype = ((TextView) findViewById(R.id.usertype));
        mBonusdownLine = ((TextView) findViewById(R.id.bonusdownLine));
        mBonusPercent = ((BLTextView) findViewById(R.id.bonusPercent));
        huizongFenpei = ((TextView) findViewById(R.id.huizongFenpei));
        teamYinkuiTotal = ((TextView) findViewById(R.id.teamYinkuiTotal));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<XjBonusBean.ListBean, BaseViewHolder>(R.layout.item_agent_dividend_manage) {
            @Override
            protected void convert(BaseViewHolder helper, XjBonusBean.ListBean item) {
                helper.setText(R.id.time, item.getEndDay());
                helper.setText(R.id.teamYinkui, Nums.formatDecimal(item.getProfitAmount(), 4));
                helper.setText(R.id.username, item.getUserName());
                helper.setText(R.id.bonus, Nums.formatDecimal(item.getBonus(), 4));
                helper.setText(R.id.bonusRay, String.valueOf(item.getPercent()));
                helper.setText(R.id.status, item.getStatusStr());
            }
        };
        recyclerView.setAdapter(adapter);


        bonusDetailRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapterBounsDetail = new BaseQuickAdapter<TempEntity, BaseViewHolder>(R.layout.inneritem_agent_team_bonus_manage) {
            @Override
            protected void convert(BaseViewHolder helper, TempEntity item) {
                helper.setText(R.id.name, item.key);
                helper.setText(R.id.value, item.value);

                if (Strs.isEqual("未派发分红：", item.key)) {
                    helper.getView(R.id.bonusSend).setVisibility(View.VISIBLE);
                    helper.getView(R.id.bonusSend).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MaterialDialog dialog = new MaterialDialog(ActAgentDividentManagent.this);
                            dialog.setTitle("确定现在派发？");
                            dialog.setMessage("金额不足时只会进行部分派发！");
                            dialog.setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pallBonus();
                                    dialog.dismiss();
                                }
                            });

                            dialog.setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    });
                } else {
                    helper.getView(R.id.bonusSend).setVisibility(View.GONE);
                }
            }
        };
        bonusDetailRecycler.setAdapter(adapterBounsDetail);

        startMills = Dates.getTimeDayStart().getTime();
        stopMills = Dates.getTimeDayStop().getTime();
        mStartTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
        mStopTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
        getBonusRule();
        getXjBonusBeanList();
        mStartTime.setOnClickListener(this);
        mStopTime.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mBonusPercent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.search:
                currentPage = 1;
                getXjBonusBeanList();
                break;
            case R.id.startTime:
                Calendar calendarStart = Calendar.getInstance();
                showDateDialog(mStartTime, Arrays.asList(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH) + 1,
                        calendarStart.get(Calendar.DAY_OF_MONTH)));
                break;
            case R.id.stopTime:
                Calendar calendarStop = Calendar.getInstance();
                showDateDialog(mStopTime, Arrays.asList(calendarStop.get(Calendar.YEAR), calendarStop.get(Calendar.MONTH) + 1,
                        calendarStop.get(Calendar.DAY_OF_MONTH)));
                break;
            case R.id.bonusPercent:
                if (null != mangeBean && !Strs.isEmpty(mangeBean.getMixPercents())) {
                    ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.dialog_bonus_show, null);
                    RecyclerView lv_list = viewGroup.findViewById(com.desheng.base.R.id.lv_list);
                    lv_list.setLayoutManager(Views.genLinearLayoutManagerV(ActAgentDividentManagent.this));
                    Button btnClose = viewGroup.findViewById(com.desheng.base.R.id.btnClose);
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialogs.dissmisCustomDialog();
                        }
                    });

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<UserPoints>>() {
                    }.getType();

                    userPointList = gson.fromJson(mangeBean.getMixPercents(), listType);
                    if (null != userPointList) {
                        BonusManageAdapter manageAdapter = new BonusManageAdapter(this, userPointList);
                        manageAdapter.setNewData(userPointList);
                        lv_list.setAdapter(manageAdapter);

                        Dialogs.showCustomDialog(this, viewGroup, true);
                    }

                } else {
                    Toasts.show(this, "没有分红数据", false);
                }

                break;
            default:
        }
    }

    private List<UserPoints> userPointList;

    private void getBonusRule() {
        HttpAction.Zj_Bonus(null, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BonusRuleBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    mangeBean = getField(extra, "data", null);
                    String strStart, strEnd;
                    strStart = mangeBean.getStartDay();
                    strEnd = mangeBean.getEndDay();
                    if (Strs.isEmpty(strStart)) {
                        strStart = "--";
                    }
                    if (Strs.isEmpty(strEnd)) {
                        strEnd = "--";
                    }
                    List<TempEntity> keyValueLists = new ArrayList<>();
                    keyValueLists.add(new TempEntity("团队盈亏金额：", Nums.formatDecimal(mangeBean.getProfitAmount(), 4)));
                    keyValueLists.add(new TempEntity("应得分红：", Nums.formatDecimal(mangeBean.getDeservedBonus(), 4)));
                    keyValueLists.add(new TempEntity("已收到分红：", Nums.formatDecimal(mangeBean.getReceivedBonus(), 4)));
                    keyValueLists.add(new TempEntity("分红时间：", strStart + "~" + strEnd));
                    keyValueLists.add(new TempEntity("应派发分红：", Nums.formatDecimal(mangeBean.getShouleDistributBonus(), 4)));
                    keyValueLists.add(new TempEntity("已派发分红：", Nums.formatDecimal(mangeBean.getAlreadyDistributBonus(), 4)));
                    keyValueLists.add(new TempEntity("未派发分红：", Nums.formatDecimal(mangeBean.getYetDistributBonus(), 4)));
                    adapterBounsDetail.setNewData(keyValueLists);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    /**
     * 获取分红列表
     */
    private void getXjBonusBeanList() {
        String username = mUsername.getText().toString();
        String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);

        HttpAction.Xj_Bonus(null, username, startTime, stopTime, currentPage, PAGE_SIZE, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", XjBonusBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                Dialogs.showProgressDialog(ActAgentDividentManagent.this, "加载中...");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {

                    XjBonusBean bonusBean = (XjBonusBean) extra.get("data");
                    if (currentPage == 1) {
                        listBeans.clear();
                    }

                    List<XjBonusBean.ListBean> templist = bonusBean.getList();

                    if (templist != null) {
                        if (templist.size() > 0) {
                            XjBonusBean.ListBean huizongbonus = templist.get(templist.size() - 1);
                            templist.remove(huizongbonus);
                            teamYinkuiTotal.setText(Nums.formatDecimal(huizongbonus.getProfitAmount(), 4));
                            huizongFenpei.setText(Nums.formatDecimal(huizongbonus.getBonusSum(), 4));
                        }
                        listBeans.addAll(templist);
                    } else {
                        listBeans.addAll(new ArrayList<XjBonusBean.ListBean>(0));
                    }

                    adapter.setNewData(listBeans);

                    int TOTAL_COUNTER = bonusBean.getTotalCount();

                    if (PAGE_SIZE * currentPage >= TOTAL_COUNTER) {
                        adapter.loadMoreEnd(true);
                        //adapter.disableLoadMoreIfNotFullPage();
                        adapter.setEnableLoadMore(false);
                    } else {
                        currentPage++;
                    }

                    if (listBeans.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                    }

                } else {
                    Toasts.show(ActAgentDividentManagent.this, msg);
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActAgentDividentManagent.this, content);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                Dialogs.hideProgressDialog(ActAgentDividentManagent.this);
            }
        });

    }

    private void pallBonus() {
        HttpAction.pallBonus(null, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);

                entity.putField("data", BonusPallBean.class);

            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (error == 0 && code == 0) {
                    BonusPallBean pallBean = (BonusPallBean) extra.get("data");
                    Toasts.show(ActAgentDividentManagent.this, "派发成功", true);
                    getBonusRule();
                    currentPage = 1;
                    getXjBonusBeanList();
                } else {
                    Toasts.show(ActAgentDividentManagent.this, msg, false);
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public void onFinish() {
                super.onFinish();
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
                        if (view == mStartTime) {
                            startMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD);
                            mStartTime.setText(text);
                        } else if (view == mStopTime) {
                            stopMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
                            mStopTime.setText(text);
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

    static class TempEntity {
        String key;
        String value;

        public TempEntity(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
