package com.desheng.base.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;

import java.util.List;

public class NameChainAdapter extends BaseQuickAdapter<String, NameChainAdapter.ViewHolder> {

    private Context ctx;
    private OnItemClickListener onItemClickListener;

    public NameChainAdapter(Context ctx, List<String> data,OnItemClickListener listener) {
        super(R.layout.item_name_chain, data);
        this.ctx = ctx;
        onItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        holder.tv_item_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    protected void convert(NameChainAdapter.ViewHolder viewHolder, String item) {

        viewHolder.tv_item_name.setText(item);
    }

    class ViewHolder extends BaseViewHolder {
        TextView tv_item_name;

        public ViewHolder(View view) {
            super(view);
            tv_item_name = view.findViewById(R.id.tv_item_name);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
