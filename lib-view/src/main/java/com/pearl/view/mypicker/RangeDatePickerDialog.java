package com.pearl.view.mypicker;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pearl.view.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RangeDatePickerDialog extends Dialog {

    private Params params;

    public RangeDatePickerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private void setParams(RangeDatePickerDialog.Params params) {
        this.params = params;
    }

    public interface OnDateSelectedListener {
         void onDateSelected(int[] dates);
         void onCancel();
    }



    private static final class Params {
        private boolean shadow = true;
        private boolean canCancel = true;
        private LoopView loopYear, loopMonth, loopDay;
        private OnDateSelectedListener callback;
    }

    public static class Builder {
        private  int MIN_YEAR = 1900;
        private  int MAX_YEAR = 2100;

        private final Context context;
        private final RangeDatePickerDialog.Params params;
        private Integer minYear;
        private Integer maxYear;
        private Integer selectYear;
        private Integer selectMonth;
        private Integer selectDay;
        private Integer minMonth;
        private Integer maxMonth;
        private Integer minDay;
        private Integer maxDay;

        public Builder(Context context) {
            this.context = context;
            params = new RangeDatePickerDialog.Params();
        }

        public Builder setMaxDate(Calendar calendar) {
            setMaxYear(calendar.get(Calendar.YEAR));
            setMaxMonth(calendar.get(Calendar.MONTH) + 1);
            setMaxDay(calendar.get(Calendar.DAY_OF_MONTH));
            return this;
        }
        public Builder setMinDate(Calendar calendar) {
            setMinYear(calendar.get(Calendar.YEAR));
            setMinMonth(calendar.get(Calendar.MONTH) + 1);
            setMinDay(calendar.get(Calendar.DAY_OF_MONTH));
            return this;
        }

        private Builder setMinYear(int year){
            minYear=year;
            MIN_YEAR = year;
            return this;
        }

        private Builder setMaxYear(int year){
            maxYear=year;
            MAX_YEAR = year;
            return this;
        }

        private Builder setMinMonth(int month){
            minMonth=month;
            return this;
        }

        private Builder setMaxMonth(int month){
            maxMonth=month;
            return this;
        }

        private Builder setMinDay(int day){
            minDay=day;
            return this;
        }

        private Builder setMaxDay(int day){
            maxDay=day;
            return this;
        }

        public Builder setSelectYear(int year){
            this.selectYear=year;
            return this;
        }

        public Builder setSelectMonth(int month){
            this.selectMonth=month;
            return this;
        }

        public Builder setSelectDay(int day){
            this.selectDay=day;
            return this;
        }

        /**
         * 获取当前选择的日期
         *
         * @return int[]数组形式返回。例[1990,6,15]
         */
        private final int[] getCurrDateValues() {
            int currYear = Integer.parseInt(params.loopYear.getCurrentItemValue());
            int currMonth = Integer.parseInt(params.loopMonth.getCurrentItemValue());
            int currDay = Integer.parseInt(params.loopDay.getCurrentItemValue());
            return new int[]{currYear, currMonth, currDay};
        }

        public Builder setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
            params.callback = onDateSelectedListener;
            return this;
        }


        public RangeDatePickerDialog create() {
            final RangeDatePickerDialog dialog = new RangeDatePickerDialog(context, params.shadow ? R.style.Theme_Light_NoTitle_Dialog : R.style.Theme_Light_NoTitle_NoShadow_Dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.layout_picker_date, null);

            view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    params.callback.onCancel();
                }
            });

            Calendar c = Calendar.getInstance();
            final LoopView loopDay = (LoopView) view.findViewById(R.id.loop_day);
            loopDay.setCyclic(false);

            if (maxMonth != null && minMonth != null && maxMonth - minMonth == 0) {
                loopDay.setArrayList(d(minDay, maxDay - minDay + 1));
            } else {
                loopDay.setArrayList(d(1, 30));
            }

            if(selectDay!=null){
                loopDay.setCurrentItem(selectDay);
            }else{
                loopDay.setCurrentItem(c.get(Calendar.DATE));
            }
            loopDay.setNotLoop();

            final LoopView loopYear = (LoopView) view.findViewById(R.id.loop_year);
            loopYear.setCyclic(false);
            loopYear.setArrayList(d(MIN_YEAR, MAX_YEAR - MIN_YEAR + 1));
            if(selectYear!=null){
                loopYear.setCurrentItem(selectYear-MIN_YEAR+1);
            }else{
                loopYear.setCurrentItem(MAX_YEAR);
            }
            loopYear.setNotLoop();

            final LoopView loopMonth = (LoopView) view.findViewById(R.id.loop_month);
            loopMonth.setCyclic(false);

            if (MAX_YEAR - MIN_YEAR == 0) {
                loopMonth.setArrayList(d(minMonth, maxMonth - minMonth + 1));
            } else {
                loopMonth.setArrayList(d(1, 12));
            }

            if(selectMonth!=null){
                loopMonth.setCurrentItem(minMonth == null ? selectMonth : selectMonth - minMonth);
            }else{
                loopMonth.setCurrentItem(c.get(Calendar.MONTH) + 1);
            }
            loopMonth.setNotLoop();


            final LoopListener maxMonthSynczListener = new LoopListener() {
                @Override
                public void onItemSelect(int item) {
                    if(minYear!=null){
                        if(Integer.parseInt(loopYear.getCurrentItemValue())==minYear ){
                            if(minMonth!=null){
                                int endMonth = 12;

                                if (minYear.equals(maxYear))
                                    endMonth = maxMonth;

                                loopMonth.setArrayList(d(minMonth, endMonth - minMonth + 1));
                                if(Integer.parseInt(loopMonth.getCurrentItemValue())<minMonth){
                                    loopMonth.setCurrentItem(0);
                                }
                            }
                        }
                    }

                    if(maxYear!=null){
                        if(Integer.parseInt(loopYear.getCurrentItemValue())==maxYear ){
                            if(maxMonth!=null){
                                int startMonth = 1;

                                if (minYear.equals(maxYear))
                                    startMonth = minMonth;

                                loopMonth.setArrayList(d(startMonth, Math.min(maxMonth, 12) - startMonth + 1));
                                if(Integer.parseInt(loopMonth.getCurrentItemValue())>maxMonth){
                                    loopMonth.setCurrentItem(maxMonth);
                                }
                            }
                        }
                    }
                }
            };

            final LoopListener maxDaySyncListener = new LoopListener() {
                @Override
                public void onItemSelect(int item) {
                    Calendar c = Calendar.getInstance();
                    boolean needFixed=true;
                    c.set(Integer.parseInt(loopYear.getCurrentItemValue()), Integer.parseInt(loopMonth.getCurrentItemValue()) - 1, 1);
                    c.roll(Calendar.DATE, false);

                    if(needFixed){
                        int maxDayOfMonth = c.get(Calendar.DATE);
                        int fixedCurr = loopDay.getCurrentItem();

                        if (maxYear != null && maxMonth != null && maxDay != null
                                && Integer.parseInt(loopYear.getCurrentItemValue())==maxYear
                                && Integer.parseInt(loopMonth.getCurrentItemValue())==maxMonth) {
                            loopDay.setArrayList(d(1, maxDay));
                        } else if (minYear != null && minMonth != null && minDay != null
                                && Integer.parseInt(loopYear.getCurrentItemValue())==minYear
                                && Integer.parseInt(loopMonth.getCurrentItemValue())==minMonth) {
                            loopDay.setArrayList(d(minDay, maxDayOfMonth - minDay + 1));

                        } else {
                            loopDay.setArrayList(d(1, maxDayOfMonth));
                        }
                        // 修正被选中的日期最大值
                        if (fixedCurr > maxDayOfMonth) fixedCurr = maxDayOfMonth - 1;
                        loopDay.setCurrentItem(fixedCurr);
                    }
                }
            };

            final LoopListener dayLoopListener=new LoopListener() {
                @Override
                public void onItemSelect(int item) {
//                    if(minYear!=null && minMonth!=null && minDay!=null
//                            && Integer.parseInt(loopYear.getCurrentItemValue())==minYear
//                            && Integer.parseInt(loopMonth.getCurrentItemValue())==minMonth
//                            && Integer.parseInt(loopDay.getCurrentItemValue())<minDay
//                            ){
//                        loopDay.setCurrentItem(minDay-1);
//                    }
//
//                    if(maxYear!=null && maxMonth!=null && maxDay!=null
//                            && Integer.parseInt(loopYear.getCurrentItemValue())==maxYear
//                            && Integer.parseInt(loopMonth.getCurrentItemValue())==maxMonth
//                            && Integer.parseInt(loopDay.getCurrentItemValue())>maxDay
//                            ){
////                        loopDay.setCurrentItem(maxDay-1);
//                    }
                }
            };
            loopYear.setListener(maxMonthSynczListener);
            loopMonth.setListener(maxDaySyncListener);
            loopDay.setListener(dayLoopListener);

            view.findViewById(R.id.tx_finish).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    params.callback.onDateSelected(getCurrDateValues());
                }
            });

            Window win = dialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.Animation_Bottom_Rising);

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(params.canCancel);
            dialog.setCancelable(params.canCancel);

            params.loopYear = loopYear;
            params.loopMonth = loopMonth;
            params.loopDay = loopDay;
            dialog.setParams(params);

            return dialog;
        }

        /**
         * 将数字传化为集合，并且补充0
         *
         * @param startNum 数字起点
         * @param count    数字个数
         * @return
         */
        private static List<String> d(int startNum, int count) {
            String[] values = new String[count];
            for (int i = startNum; i < startNum + count; i++) {
                String tempValue = (i < 10 ? "0" : "") + i;
                values[i - startNum] = tempValue;
            }
            return Arrays.asList(values);
        }

    }
}
