package com.desheng.base.adapter;

import android.content.Context;
import android.graphics.LinearGradient;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.global.Config;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.model.UserPoints;

import java.util.List;

public class BonusManageAdapter extends BaseQuickAdapter<UserPoints, BonusManageAdapter.ViewHolder> {

    private Context ctx;

    public BonusManageAdapter(Context ctx, List<UserPoints> data) {
        super(R.layout.item_bonus_point, data);
        this.ctx = ctx;
    }

    @Override
    protected void convert(BonusManageAdapter.ViewHolder viewHolder, final UserPoints item) {

        if (Config.custom_flag.equals(BaseConfig.FLAG_LONGTENG)) {
            viewHolder.tv_time_length.setText("周销量(元)>=:");
            viewHolder.tv_profit_length.setText("周累计亏损:");
        } else {
            viewHolder.tv_time_length.setText("半月总销量(元)>=:");
            viewHolder.tv_profit_length.setText("半月累计亏损:");
        }

        viewHolder.etLost.setText("" + item.getProfit());
        viewHolder.etPercent.setText("" + item.getPercent());
        viewHolder.etAmount.setText("" + item.getAmount());

        if(null==item.getActivity()){
            viewHolder.etActivity.setText("0");
        }else{
            viewHolder.etActivity.setText("" + item.getActivity());
        }

        viewHolder.etLost.setEnabled(false);
        viewHolder.etPercent.setEnabled(false);
        viewHolder.etAmount.setEnabled(false);
        viewHolder.etActivity.setEnabled(false);

    }

    class ViewHolder extends BaseViewHolder {
        EditText etAmount;
        EditText etLost;
        EditText etPercent;
        EditText etActivity;
        TextView tv_time_length;
        TextView tv_profit_length;
        LinearLayout layout_activity;

        public ViewHolder(View view) {
            super(view);
            tv_time_length = view.findViewById(R.id.tv_time_length);
            tv_profit_length =view.findViewById(R.id.tv_profit_length);
            etAmount = view.findViewById(R.id.etAllSale);
            etLost = view.findViewById(R.id.etLost);
            etPercent = view.findViewById(R.id.etBonus);
            etActivity = view.findViewById(R.id.etActivity);
            layout_activity = view.findViewById(R.id.layout_activity);
        }
    }
}
