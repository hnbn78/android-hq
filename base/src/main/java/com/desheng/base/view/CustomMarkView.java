package com.desheng.base.view;

import android.content.Context;
import android.widget.TextView;

import com.desheng.base.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CustomMarkView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    private TextView tvValue, tvDate;
    private String key;
    private List<String> list = new ArrayList<>();

    public CustomMarkView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvValue = findViewById(R.id.tvValue);
        tvDate = findViewById(R.id.tvDate);
    }

    @Override
    public void refreshContent(com.github.mikephil.charting.data.Entry e, Highlight highlight) {
        String val = key + ": " + Utils.formatNumber(e.getY(), 1, true);
        tvValue.setText(val);
        int position = (int) (e.getX() / 10);
        tvDate.setText(list.get(position));
        super.refreshContent(e, highlight);
    }

    public void clearDate() {
        list.clear();
    }

    public void addDate(String key) {
        list.add(key);
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
