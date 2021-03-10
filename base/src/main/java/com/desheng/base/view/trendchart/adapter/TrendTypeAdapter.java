package com.desheng.base.view.trendchart.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.desheng.base.R;
import com.desheng.base.view.MyGridView;
import com.desheng.base.view.trendchart.TrendTypeBean;

import java.util.ArrayList;
import java.util.List;

public class TrendTypeAdapter extends BaseAdapter {
    private List<TrendTypeBean> arrayList = new ArrayList();
//    private ConstManage constManage;
    private Context context;
    private ViewHolder holder;
    private int id;
    private LayoutInflater mInflater;
    private SharedPreferences userSetting;

    public TrendTypeAdapter(Context paramContext, List<TrendTypeBean> paramList, int paramInt) {
        this.context = paramContext;
        this.arrayList = paramList;
        this.mInflater = LayoutInflater.from(this.context);
//        this.constManage = new ConstManage();
        this.id = paramInt;
        paramContext = this.context;
//        paramList = new StringBuilder();
//        paramList.append("trendSetting");
//        paramList.append(paramInt);
        this.userSetting = paramContext.getSharedPreferences(paramList.toString(), 0);
    }

    private void init(final int paramInt) {
        TrendTypeBean trendTypeBean =  this.arrayList.get(paramInt);
        if (trendTypeBean != null) {
            StringBuilder sb = new StringBuilder();
            ( sb).append(( trendTypeBean).getName());
            ( sb).append(":");
           String str = ( sb).toString();
            this.holder.name.setText(str);
            TrendTypeListAdapter  typeListAdapter = new TrendTypeListAdapter(this.context, ( trendTypeBean).getIndex_list(), this.id, paramInt);
            this.holder.listView.setAdapter( typeListAdapter);
            this.holder.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong) {
                    TrendTypeAdapter.this.userSetting.edit().putInt("pos", paramInt).commit();
                    TrendTypeAdapter.this.userSetting.edit().putInt("itemPos", paramAnonymousInt).commit();
//                    paramAnonymousAdapterView = new Intent(TrendTypeAdapter.this.constManage.TrendTypeItemCLICK);
//                    LocalBroadcastManager.getInstance(TrendTypeAdapter.this.context).sendBroadcast(paramAnonymousAdapterView);
                }
            });
        }
    }

    public void DataSetChanged(List<TrendTypeBean> paramList) {
        this.arrayList = paramList;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.arrayList.size();
    }

    public Object getItem(int paramInt) {
        return this.arrayList.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(int paramInt, View convertView, ViewGroup paramViewGroup) {
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.layout_trend_type_item, paramViewGroup, false);
            this.holder = new ViewHolder(convertView);
            convertView.setTag(this.holder);
        } else {
            this.holder = ((ViewHolder) convertView.getTag());
        }
        init(paramInt);
        return convertView;
    }

    static class ViewHolder {
        private MyGridView listView;
        private TextView name;

        public ViewHolder(View view){
            listView = view.findViewById(R.id.trend_type_item_list);
            name = view.findViewById(R.id.trend_type_item_name);
        }
    }
}
