package com.desheng.app.toucai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shark.tc.R;

import java.util.List;

/**
 * Created by user on 2018/3/20.
 */

public class ListViewAdapter extends BaseAdapter {


    Context context;
    List<CharSequence> mList;
    private float mTextSize;
    private int mTextColor;
    private int mPadddintLeft;

    public ListViewAdapter(Context context, List<CharSequence> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        TextView mTextView = null;
        if (convertView == null) {
            mTextView = new TextView(context);
            convertView = View.inflate(context, R.layout.listview_item, null);
            mTextView = (TextView) convertView.findViewById(R.id.tvPopupWindowListViewItem);
            convertView.setTag(mTextView);
        }else {
            mTextView = (TextView)convertView.getTag();
        }
        mTextView.setText(mList.get(position));
        mTextView.setTextSize(mTextSize);
        mTextView.setTextColor(mTextColor);
        mTextView.setPadding(mPadddintLeft, 0, 0, 0);

        return convertView;
    }

    public void setTextSize(float textsize) {
        this.mTextSize = textsize;
    }
    public void setTextColor(int textcolor) {
        this.mTextColor = textcolor;
    }
    public void setTextPaddingLeft(int paddingleft) {
        this.mPadddintLeft = paddingleft;
    }

}
