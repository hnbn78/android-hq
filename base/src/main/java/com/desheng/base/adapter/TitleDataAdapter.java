package com.desheng.base.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.hubert.guide.util.ScreenUtils;
import com.desheng.base.R;
import com.desheng.base.util.ResUtil;

import java.util.List;

/**
 * 走势 顶部标题适配器
 */
public class TitleDataAdapter extends BaseAdapter {

    private List listData = null;
    private int textColor = 0;
    private int layoutId;
    private int mCellWidth;
    private double widthMulti = 1;
    private Context context;

    public TitleDataAdapter(Context context, int widthMulti, int textColor, int countPerGroup) {
        layoutId = R.layout.item_trend_chart_title;
        int temp = (ScreenUtils.getScreenWidth(context) - context.getResources().getDimensionPixelSize(R.dimen.trend_issuno_withd)) / countPerGroup;
        mCellWidth = temp;
        this.widthMulti = widthMulti;
        this.textColor = textColor;
        this.context = context;
    }

    public void bindData(List data) {
        this.listData = data;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        }
        final TextView tvContent = (TextView) convertView.findViewById(R.id.tvContent);
        View line = convertView.findViewById(R.id.vline);
            /*if(position == 0){
                line.setVisibility(View.INVISIBLE);
            }*/
        View line1 = convertView.findViewById(R.id.vline1);
        View line2 = convertView.findViewById(R.id.vline2);
        ViewGroup.LayoutParams vP1 = line1.getLayoutParams();
        vP1.height = (int) (getScreenDenisty() * 0.6f / 160);
        line1.setLayoutParams(vP1);
        line2.setLayoutParams(vP1);
        int diffHeight = mCellWidth;
        int diffWidth;
        if (widthMulti == 3) {
            diffWidth = context.getResources().getDimensionPixelSize(R.dimen.trend_issuno_withd);
        } else {
            diffWidth = (int) (mCellWidth * widthMulti);
        }
        //item尺寸
        ViewGroup.LayoutParams convertParams = convertView.getLayoutParams();
        if (widthMulti == 3) {
            convertParams.width = diffWidth;
            convertParams.height = diffHeight;
        } else {
            convertParams.width = (int) (mCellWidth * widthMulti);
            convertParams.height = context.getResources().getDimensionPixelSize(R.dimen.item_whx2) - diffHeight;
        }

        convertView.setLayoutParams(convertParams);


        //textview容器尺寸
        ViewGroup vgContent = convertView.findViewById(R.id.vgContent);
        ViewGroup.LayoutParams vgParams = vgContent.getLayoutParams();
        vgParams.width = diffWidth;
        if (widthMulti == 3) {
            vgParams.height = diffHeight;
        } else {
            vgParams.height = context.getResources().getDimensionPixelSize(R.dimen.item_whx2) - diffHeight;
        }
        vgContent.setLayoutParams(vgParams);

        tvContent.setTextColor(textColor);
        tvContent.setText(listData.get(position).toString());
        return convertView;
    }

    private int getScreenDenisty() {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.densityDpi;
    }
}
