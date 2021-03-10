package com.desheng.app.toucai.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.desheng.app.toucai.adapter.ListViewAdapter;
import com.shark.tc.R;

import java.util.List;

/**
 * Created by user on 2018/3/20.
 */

public class DropDownList extends android.support.v7.widget.AppCompatButton {

    private PopupWindow mPopupWindow;
    private Context context;
    //显示数据的集合
    private List<CharSequence> mList;
    //默认显示的值
    private int mCurrentItem = 0;
    //指示图标图片
    private Drawable offIcon,onIcon;
    //没下拉和下拉时背景图片
    private Drawable offBackground,onBackground;
    //下拉框背景
    private Drawable dropBackground;
    //下拉item字体设置
    private float itemTextSize;
    private int itemTextColor;
    private int itemPaddingLeft;
    //下拉框长度
    private int dropListLength;
    //指示图标的位置
    private String direction;
    private static final String LEFT= "0";
    private static final String RIGHT= "1";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public DropDownList(Context context) {
        this(context,null);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public DropDownList(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public DropDownList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.dropdownlist);
        offIcon = mTypedArray.getDrawable(R.styleable.dropdownlist_offIcon);
        onIcon = mTypedArray.getDrawable(R.styleable.dropdownlist_onIcon);
        offBackground = mTypedArray.getDrawable(R.styleable.dropdownlist_offBackground);
        onBackground = mTypedArray.getDrawable(R.styleable.dropdownlist_onBackground);
        dropBackground = mTypedArray.getDrawable(R.styleable.dropdownlist_dropBackground);
        itemTextSize = mTypedArray.getDimension(R.styleable.dropdownlist_itemTextSize, 20);
        itemTextColor = mTypedArray.getColor(R.styleable.dropdownlist_itemTextColor, 0xfff);
        itemPaddingLeft = (int) mTypedArray.getDimension(R.styleable.dropdownlist_itemPaddingLeft, 30);
        dropListLength = mTypedArray.getDimensionPixelOffset(R.styleable.dropdownlist_dropListLength, 250);
        direction = mTypedArray.getString(R.styleable.dropdownlist_direction);

        mTypedArray.recycle();

        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {
        //设置图标向右
        offIcon.setBounds(0, 0, offIcon.getMinimumWidth(), offIcon.getMinimumHeight());
        if (LEFT.equals(direction)) {
            this.setCompoundDrawables(offIcon, null, null, null);
        }else if (RIGHT.equals(direction)) {
            this.setCompoundDrawables(null, null, offIcon, null);
        }
        this.setBackground(offBackground);
        //设置监听器
        this.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                startDropDownList();
            }
        });
    }

    /**
     * 初始化
     * @param context
     * @param list
     */
    public void init(Context context,List<CharSequence> list) {
        this.context = context;
        this.mList = list;
        this.setText(mList.get(mCurrentItem));
    }
    /**
     * 初始化PopupWindow
     * @param button
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initPopupWindow(final Button button) {
        //设置图标向下
        onIcon.setBounds(0, 0, onIcon.getMinimumWidth(), onIcon.getMinimumHeight());
        if (LEFT.equals(direction)) {
            button.setCompoundDrawables(onIcon, null, null, null);
        }else if (RIGHT.equals(direction)) {
            button.setCompoundDrawables(null, null, onIcon, null);
        }
        button.setBackground(onBackground);

        View pv_layout = View.inflate(context, R.layout.popupwindow_layout, null);
        //通过view 和宽·高，构造PopopWindow
        mPopupWindow = new PopupWindow(pv_layout, button.getWidth(), dropListLength, true);
        //此处为popwindow 设置背景
        mPopupWindow.setBackgroundDrawable(dropBackground);
        //设置PopupWindow不获取焦点，其父容器获取焦点
        mPopupWindow.setFocusable(true);
        //设置popupWindow以外的区域可以触摸
        mPopupWindow.setOutsideTouchable(false);
        //设置显示位置
        mPopupWindow.showAsDropDown(button);
        //消失监听事件
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                if (mPopupWindow != null) {
                    mPopupWindow = null;
                }
                //设置图标向右
                offIcon.setBounds(0, 0, offIcon.getMinimumWidth(), offIcon.getMinimumHeight());
                if (LEFT.equals(direction)) {
                    button.setCompoundDrawables(offIcon, null, null, null);
                }else if (RIGHT.equals(direction)) {
                    button.setCompoundDrawables(null, null, offIcon, null);
                }
                button.setBackground(offBackground);
            }
        });

        ListView lv = (ListView) pv_layout.findViewById(R.id.lvPopupWindow);
        ListViewAdapter mAdapter = new ListViewAdapter(context, mList);
        mAdapter.setTextSize(itemTextSize);
        mAdapter.setTextColor(itemTextColor);
        mAdapter.setTextPaddingLeft(itemPaddingLeft);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ((Button) button).setText(mList.get(position));
                mCurrentItem = position;
                button.setText(mList.get(position));
                //关闭PopupWindow
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        });
    }

    /**
     * 打开下拉框
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startDropDownList() {
        initPopupWindow(this);
    }
    /**
     * 获取当前Item
     */
    public int getCurrentItem() {
        return mCurrentItem;
    }
    /**
     * 设置当前Item
     */
    public void setCurrentItem(int currentItem) {
        this.mCurrentItem = currentItem;
    }

}
