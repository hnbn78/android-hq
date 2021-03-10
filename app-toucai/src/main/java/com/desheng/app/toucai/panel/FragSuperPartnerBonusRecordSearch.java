package com.desheng.app.toucai.panel;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.util.Dates;
import com.ab.util.Toasts;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 投注记录
 */
public class FragSuperPartnerBonusRecordSearch extends AbBaseFragment implements View.OnClickListener {

    private static final int PAGE_SIZE = 20;
    private TextView tvStartDate, tvEndDate;
    private TextView btnFastChoose;
    private RelativeLayout vgBonusType;
    private RelativeLayout vgAllState;
    private ArrayList<LotteryInfo> listAllLottery;

    private String arrayBonus[] = new String[]{"全部", "盈亏/投注奖励", "单次推荐奖励"};
    private String arrayStatus[] = new String[]{"全部", "成功", "已拒绝", "待审核"};
    private TextView tvAllBonusType, tvBonusTypeName;
    private TextView tvAllState, tvStateName;

    private ListMenuPopupWindow statePopup = null;
    private ListMenuPopupWindow dateChoosePopup = null;
    private ListMenuPopupWindow gamePopup = null;
    private ListMenuPopupWindow bonusPopup = null;

    private String arrayDateLength[] = {"今日", "三日", "七日"};

    private long startMills = 0;
    private long stopMills = 0;

    private Integer status;
    private String gameCode;
    private String lotteryType;
    private String dateLength;

    private LinearLayout layout_search_view;
    private TextView tv_search;

    private int date_length = 0;
    private View emptyView;
    private String beginDate, overDate;
    private TextView tv_amount;
    Integer bonusType = null;
    Integer bonusState = null;
    private OnSearchListener onSearchListener;

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    @Override

    public int getLayoutId() {
        return R.layout.frag_super_partner_bonus_record_search;
    }

    @Override
    public void init(View root) {
        emptyView = root.findViewById(R.id.layout_nodata);
        layout_search_view = root.findViewById(R.id.layout_search_view);
        tv_search = root.findViewById(R.id.tv_search);
        tv_amount = root.findViewById(R.id.tv_amount);
        tvAllBonusType = root.findViewById(R.id.tvAllBonusType);
        tvBonusTypeName = root.findViewById(R.id.tvBonusTypeName);
        tvAllState = root.findViewById(R.id.tvAllState);
        tvStateName = root.findViewById(R.id.tvStateName);
        vgAllState = root.findViewById(R.id.vgAllState);
        vgBonusType = root.findViewById(R.id.vgBonusType);
        btnFastChoose = root.findViewById(R.id.btnFastChoose);

        initPopup();

        String currDate = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);
        tvStartDate = root.findViewById(R.id.tvStartDate);
        tvStartDate.setText(currDate);
        startMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD);

        tvEndDate = root.findViewById(R.id.tvEndDate);
        tvEndDate.setText(currDate);
        stopMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;

        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnFastChoose.setOnClickListener(this);
        vgAllState.setOnClickListener(this);
        vgBonusType.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        overDate = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
    }

    private void initPopup() {
        dateChoosePopup = new ListMenuPopupWindow(getActivity(), AbDevice.SCREEN_WIDTH_PX / 2, arrayDateLength);
        dateChoosePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                dateLength = arrayDateLength[position];
                long currenttime = System.currentTimeMillis();
                switch (position) {
                case 0:
                    date_length = 1;
                    String start = Dates.getStringByFormat(currenttime, Dates.dateFormatYMD);
                    tvStartDate.setText(start);
                    start = start + " 00:00:00";
                    startMills = Dates.getMillisOfStr(start, Dates.dateFormatYMDHMS);
                    beginDate = start;
                    break;
                case 1:
                    date_length = 2;
                    String startTime = Dates.getStringByFormat(currenttime - date_length * 24 * 60 * 60 * 1000, Dates.dateFormatYMD);
                    tvStartDate.setText(startTime);
                    startTime = startTime + " 00:00:00";
                    startMills = Dates.getMillisOfStr(startTime, Dates.dateFormatYMDHMS);
                    beginDate = startTime;
                    break;
                case 2:
                    date_length = 6;//当前的也算是一天
                    String startDate = Dates.getStringByFormat(currenttime - date_length * 24 * 60 * 60 * 1000, Dates.dateFormatYMD);
                    tvStartDate.setText(startDate);
                    startDate = startDate + " 00:00:00";
                    startMills = Dates.getMillisOfStr(startDate, Dates.dateFormatYMDHMS);
                    beginDate = startDate;
                    break;
                }

            }
        });

        statePopup = new ListMenuPopupWindow(getActivity(), AbDevice.SCREEN_WIDTH_PX, arrayStatus);
        statePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                if (position == 0) {
                    status = null;
                } else {
                    status = position;
                }
                tvStateName.setText(arrayStatus[position]);
            }
        });

        bonusPopup = new ListMenuPopupWindow(getActivity(), AbDevice.SCREEN_WIDTH_PX, arrayBonus);


        bonusPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                switch (position) {
                case 0:
                    bonusType = null;
                    break;
                case 1:
                    bonusType = 0;
                    break;
                case 2:
                    bonusType = 1;
                    break;
                }

                tvBonusTypeName.setText(arrayBonus[position]);
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v == vgAllState) {
            if (statePopup != null) {
                statePopup.showAsDropDown(vgAllState, 0, 0);
            }
        } else if (v == vgBonusType) {
            if (bonusPopup != null) {
                bonusPopup.showAsDropDown(vgBonusType, 0, 0);
            }
        } else if (v == tvStartDate) {
            Calendar calendar = Calendar.getInstance();
            showDateDialog(tvStartDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        } else if (v == tvEndDate) {
            Calendar calendar = Calendar.getInstance();
            showDateDialog(tvEndDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        } else if (v == btnFastChoose) {
            if (dateChoosePopup != null) {
                dateChoosePopup.showAsDropDown(btnFastChoose, 0, 0);
            }
        } else if (v == tv_search) {
            if (stopMills < startMills) {
                Toasts.show("结束时间不能小于起始时间", false);
                return;
            }

            if (Dates.getOffsetDay(startMills, stopMills) > 7) {
                Toasts.show("查询的时间跨度不能超过7天", false);
                return;
            }

            String start = Dates.getStringByFormat(startMills, Dates.dateFormatYMD);
            start = start + " 00:00:00";
            String stop = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
            beginDate = start;
            overDate = stop;
            searchBonusRecord(start, stop);
        }
    }

    public void searchBonusRecord(String startDate, String endDate) {
        if (onSearchListener != null) {
            onSearchListener.searchBonusRecord(bonusType, startDate, endDate, status);
            Log.w("xxxx",bonusType+"---------");
            Log.w("xxxx",startDate+"---------");
            Log.w("xxxx",endDate+"---------");
            Log.w("xxxx",status+"---------");
        }
    }

    private void showDateDialog(final View view, List<Integer> date) {
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(getActivity());
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

    public interface OnSearchListener {
        void searchBonusRecord(Integer type, String startDate, String endDate, Integer status);
    }
}