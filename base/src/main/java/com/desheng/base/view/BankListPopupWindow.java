package com.desheng.base.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.ab.util.Views;
import com.desheng.base.R;
import com.desheng.base.model.WithdrawInfo;

import java.util.List;

/**
 * Created by user on 2018/3/13.
 */

public class BankListPopupWindow extends PopupWindow  {

    private List<WithdrawInfo.AccountCardListBean> mItems;
    private BankListPopupWindow mWindow;
    private Button btnSearch;
    private OnItemSelectedListener onItemSelectedListener;

    public BankListPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BankListPopupWindow(Activity activity, int width, List<WithdrawInfo.AccountCardListBean> items, final OnItemSelectedListener onItemSelectedListener){
        this.onItemSelectedListener = onItemSelectedListener;
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
        BankListAdapter adapter=new BankListAdapter(activity, mItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BankListPopupWindow.this.dismiss();
                onItemSelectedListener.onItemSelected(position);
            }
        });

//        btnSearch=contentView.findViewById(R.id.btn_search);

    }

    public Button getBtnSearch() {
        return btnSearch;
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


}


