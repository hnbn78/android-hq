package com.desheng.base.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.ab.util.Views;
import com.desheng.base.R;

/**
 * Created by user on 2018/3/13.
 */

public class ListMenuPopupWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    private String[] mItems;
    private ListMenuPopupWindow mWindow;
    private SpinnerListAdapter.onItemClickListener mListener;

    public ListMenuPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListMenuPopupWindow(Activity activity, int width, String[] items){
        LayoutInflater inflater=activity.getLayoutInflater();
        View contentView=inflater.inflate(R.layout.view_menu_popupwindow, null);
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
        param.height = Views.dp2px(300);
        listView.setLayoutParams(param);
        mWindow=this;
        SpinnerListAdapter adapter=new SpinnerListAdapter(mWindow,activity, mItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        ListMenuPopupWindow.this.dismiss();
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    public void close(){
        this.dismiss();
    }

    public int position(){
        return 0;
    }

    public void setOnItemClickListener(SpinnerListAdapter.onItemClickListener listener){
        this.mListener=listener;
    }

    public SpinnerListAdapter.onItemClickListener getListener(){
        //可以通过this的实例来获取设置好的listener
        return mListener;
    }
}


