package com.desheng.app.toucai.adapter;

import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.model.Params;
import com.shark.tc.R;

import java.util.List;

public class ItemReportAdapter extends BaseQuickAdapter<Params, BaseViewHolder> {

    ItemReportAdapter(@Nullable List<Params> data) {
        super(R.layout.item_team_report, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Params item) {
        TextView textView = (TextView) helper.itemView;
        if (TextUtils.isEmpty(item.key))
            textView.setText(null);
        else {
            String str = item.key + ": \t" + item.value;
            textView.setText(Html.fromHtml(str));
        }
    }
}
