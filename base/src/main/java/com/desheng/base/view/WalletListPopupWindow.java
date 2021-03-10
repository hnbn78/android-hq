package com.desheng.base.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.Strs;
import com.ab.util.Views;
import com.bumptech.glide.Glide;
import com.desheng.base.R;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.WithdrawInfo;

import java.util.List;

/**
 * Created by user on 2018/3/13.
 */

public class WalletListPopupWindow extends PopupWindow  {

    private List<String> mItems;
    private WalletListPopupWindow mWindow;
    private OnItemSelectedListener onItemSelectedListener;
    private  SpinnerListAdapter adapter;

    public WalletListPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WalletListPopupWindow(Activity activity, int width,  List<String> items, final OnItemSelectedListener onItemSelectedListener){
        this.onItemSelectedListener = onItemSelectedListener;
        LayoutInflater inflater=activity.getLayoutInflater();
        View contentView=inflater.inflate(R.layout.view_wallet_popupwindow, null);
        // 设置PopupWindow的View
        this.setContentView(contentView);
        // 设置PopupWindow弹出窗体的宽
        this.setWidth(width);
        // 设置PopupWindow弹出窗体的高
        this.setHeight(android.view.WindowManager.LayoutParams.WRAP_CONTENT);

        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        this.setBackgroundDrawable(dw);

        this.mItems=items;
        ListView listView=(ListView) contentView.findViewById(R.id.lv_list);
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) listView.getLayoutParams();
//        param.height = Views.dp2px(300);
        listView.setLayoutParams(param);
        mWindow=this;
        adapter=new SpinnerListAdapter(activity, mItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WalletListPopupWindow.this.dismiss();
                onItemSelectedListener.onItemSelected(position);
            }
        });

    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    public void close(){
        this.dismiss();
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    class SpinnerListAdapter   extends BaseAdapter {
        private List<String> mItems;
        private Activity mActivity;

        public SpinnerListAdapter( Activity activity, List<String> items) {
            this.mActivity = activity;
            this.mItems = items;
        }

        public int getCount() {
            return mItems.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        private class ViewHolder {
            public TextView text;
        }

        public View getView(final int arg0, final View arg1, ViewGroup arg2) {
            View view = arg1;
            SpinnerListAdapter.ViewHolder holder = null;
            if (view == null) {
                view = View.inflate(mActivity, R.layout.myspinner_list_item, null);
                holder = new SpinnerListAdapter.ViewHolder();
                holder.text = view.findViewById(R.id.tv_text);
                view.setTag(holder);
            } else {
                holder = (SpinnerListAdapter.ViewHolder) view.getTag();
            }
            holder.text.setText(mItems.get(arg0));

            return view;
        }
    }

}


