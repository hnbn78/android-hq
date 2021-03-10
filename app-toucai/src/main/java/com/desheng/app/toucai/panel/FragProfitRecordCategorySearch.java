package com.desheng.app.toucai.panel;

import android.view.View;

import com.airsaid.pickerviewlibrary.adapter.WheelAdapter;
import com.airsaid.pickerviewlibrary.widget.wheelview.WheelView;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FragProfitRecordCategorySearch extends AbBaseFragment {
    WheelView wheelView;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
    DateAdapter dateAdapter = new DateAdapter(new Date(0), new Date());
    OnSearchListener listener;

    public void setListener(OnSearchListener listener) {
        this.listener = listener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_profit_record_search;
    }

    @Override
    public void init(View root) {
        wheelView = root.findViewById(R.id.wv_date);
        wheelView.setAdapter(dateAdapter);
        wheelView.setTextSize(18);
        wheelView.setCyclic(false);
        wheelView.setCurrentItem(dateAdapter.data.size() - 1);
        root.findViewById(R.id.tv_search).setOnClickListener(v -> {
            if (listener != null) {
                try {
                    Date date = simpleDateFormat.parse(dateAdapter.getItem(wheelView.getCurrentItem()));
                    listener.onSearch(DateUtil.formatDate(date.getTime(), DateUtil.ymdhms));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class DateAdapter implements WheelAdapter<String> {
        List<String> data;

        public DateAdapter(Date start, Date end) {
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.setTime(start);
            from.set(Calendar.DAY_OF_MONTH, 1);
            from.set(Calendar.HOUR_OF_DAY, 0);
            from.set(Calendar.MINUTE, 0);
            from.set(Calendar.SECOND, 0);
            from.set(Calendar.MILLISECOND, 0);
            to.setTime(end);
            to.set(Calendar.DAY_OF_MONTH, 1);
            to.set(Calendar.HOUR_OF_DAY, 0);
            to.set(Calendar.MINUTE, 0);
            to.set(Calendar.SECOND, 0);
            to.set(Calendar.MILLISECOND, 0);

            data = new ArrayList<>();
            Calendar curr = from;
            while (curr.before(to)) {
                data.add(simpleDateFormat.format(curr.getTime()));
                curr.add(Calendar.MONTH, 1);
            }
            data.add(simpleDateFormat.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

        @Override
        public int getItemsCount() {
            return data.size();
        }

        @Override
        public String getItem(int index) {
            return data.get(index);
        }

        @Override
        public int indexOf(String o) {
            try {
                return data.indexOf(o);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    public interface OnSearchListener {
        void onSearch(String date);
    }
}
