package com.pearl.act.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.global.Global;
import com.ab.util.Views;
import com.pearl.act.R;

/**
 * Created by lee on 2018/4/11.
 */

public abstract class UIHelper {
    //无
    public static final int BACK_NONE = 0;
    //白色左箭头, 圆阴影
    public static final int BACK_WHITE_ARROR_SHADOW_CIRCLE = 1;
    //灰色删除
    public static final int BACK_GRAY_DELETE = 2;
    //白色左箭头
    public static final int BACK_WHITE_ARROR = 3;
    //黑色左箭头
    public static final int BACK_BLACK_ARROR = 4;
    //白色带圈左箭头
    public static final int BACK_WHITE_CIRCLE_ARROW = 5;

    private static UIHelper uiHelper;


    public static int colorPrimary;
    public static int colorPrimaryId;
    public static int toolbarBgResId;

    public static void initUiHelper(UIHelper helper) {
        uiHelper = helper;
    }

    public static UIHelper getIns() {
        return uiHelper;
    }

    public static void setToolbarLeftBtn(ImageButton ibLeftBtn, int type) {
        if (ibLeftBtn == null) {
            return;
        }
        ibLeftBtn.setVisibility(View.VISIBLE);
        switch (type) {
            case BACK_NONE:
                ibLeftBtn.setVisibility(View.INVISIBLE);
                break;
            case BACK_WHITE_ARROR_SHADOW_CIRCLE:
                ibLeftBtn.setImageResource(R.drawable.ic_back_white_arror_shadow_circle);
                break;
            case BACK_GRAY_DELETE:
                ibLeftBtn.setImageResource(R.drawable.ic_back_gray_delete);
                break;
            case BACK_WHITE_ARROR:
                ibLeftBtn.setImageResource(R.drawable.ic_back_white_arrow);
                break;
            case BACK_WHITE_CIRCLE_ARROW:
                ibLeftBtn.setImageResource(R.drawable.ic_back_circle_arrow_white);
                int pix = Views.dp2px(7);
                ibLeftBtn.setPadding(pix, pix, pix, pix);
                break;
            case BACK_BLACK_ARROR:
                ibLeftBtn.setImageResource(R.drawable.ic_back_black_arror);
                break;
        }
    }

    public abstract void simpleToolbarTitleCenterDefault(Toolbar toolbar, String str);

    public abstract void simpleToolbarWithBackBtn(Toolbar toolbar, String title);

    public abstract void setToolbarTitleCenterEndDrawable(Toolbar toolbar, Drawable drawable, View.OnClickListener onClickListener);

    public abstract void simpleToolbarLeftBackAndRightTitle(Context ctx, Toolbar toolbar, String title);

    public abstract void simpleToolbarLeftBackAndCenterTitleAndeRightTitle(Context ctx, Toolbar toolbar, String centerTitle, String rightTitle);

    public abstract void simpleToolbar(Toolbar toolbar, String title);

    /**
     * 设置中间标题
     *
     * @param toolbar
     * @param str
     */
    public static void setToolbarTitleCenter(Toolbar toolbar, String str, int dimensId, int colorResId) {
        TextView  tvTitleCenter = (TextView) toolbar.findViewById(com.pearl.act.R.id.tvTitleCenter);
        tvTitleCenter.setText(str);
        tvTitleCenter.setTextSize(TypedValue.COMPLEX_UNIT_PX, Views.fromDimens(dimensId));
        tvTitleCenter.setTextColor(Views.fromColors(colorResId));
    }




    /**
     * 设置中间图片标题
     *
     * @param toolbar
     */
    public static void showTitleImageView(Toolbar toolbar) {
        ImageView iv_title_view = (ImageView) toolbar.findViewById(R.id.iv_title_view);
        TextView tvTitleCenter = (TextView) toolbar.findViewById(com.pearl.act.R.id.tvTitleCenter);
        iv_title_view.setVisibility(View.VISIBLE);
        tvTitleCenter.setText("");
    }

    /**
     * 设置返回图标
     */
    public static void setToolbarLeftButtonVisiblity(Toolbar toolbar, int visiblity) {
        ImageButton ibBackBtn = (ImageButton) toolbar.findViewById(com.pearl.act.R.id.ibLeftBtn);
        ibBackBtn.setVisibility(visiblity);
    }


    /**
     * 设置左侧按钮
     */
    public static void setToolbarLeftButtonImg(Toolbar toolbar, int imgResId, int dbWidth, int dbHeight, View.OnClickListener listener) {
        ImageButton ibBtn = (ImageButton) toolbar.findViewById(R.id.ibLeftBtn);
        ibBtn.setVisibility(View.VISIBLE);
        ibBtn.setImageResource(imgResId);
        ibBtn.getLayoutParams().width = Views.dp2px(dbWidth);
        ibBtn.getLayoutParams().height = Views.dp2px(dbHeight);
        ibBtn.setOnClickListener(listener);
    }

    /**
     * 设置右侧按钮
     */
    public static void setToolbarRightButtonImg(Toolbar toolbar, int imgResId, int dbWidth, int dbHeight, View.OnClickListener listener) {
        ImageButton ibBtn = (ImageButton) toolbar.findViewById(R.id.ibRightBtn);
        toolbar.findViewById(R.id.vgRightButton).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.tvRightBtn).setVisibility(View.GONE);
        ibBtn.setVisibility(View.VISIBLE);
        ibBtn.setImageResource(imgResId);
        ibBtn.getLayoutParams().width = Views.dp2px(dbWidth);
        ibBtn.getLayoutParams().height = Views.dp2px(dbHeight);
        ibBtn.setOnClickListener(listener);

    }

    /**
     * 隐藏右侧活动按钮
     * @param toolbar
     */
    public static void hideRoolbarRightButton(Toolbar toolbar){
        toolbar.findViewById(R.id.ibRightBtn).setVisibility(View.GONE);
        toolbar.findViewById(R.id.vgRightButton).setVisibility(View.GONE);
        toolbar.findViewById(R.id.tvRightBtn).setVisibility(View.GONE);
    }

    public static void setToolbarButtonRightText(TextView tvRightBtn, CharSequence title) {
        if (tvRightBtn != null) {
            tvRightBtn.setVisibility(View.VISIBLE);
            tvRightBtn.setText(title);
        }
    }

    public static void setToolbarButtonRightTextSize(TextView tvRightBtn, int sp) {
        tvRightBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }

    public static void setToolbarButtonRightTextColor(TextView tvRightBtn, int colorResId) {
        tvRightBtn.setTextColor(Global.app.getResources().getColor(colorResId));
    }

    public static void setToolbarRightButtonGroupClickListener(Toolbar toolbar, View.OnClickListener onClickListener) {
        toolbar.findViewById(R.id.ibRightBtn).setClickable(false);
        toolbar.findViewById(R.id.tvRightBtn).setClickable(false);
        toolbar.findViewById(R.id.vgRightButton).setOnClickListener(onClickListener);
    }

    /**
     * 设置背景颜色
     */
    public static void setToolbarBgColor(Toolbar toolbar, int colorResId) {
        if (toolbar != null) {
            toolbar.setBackgroundColor(Views.fromColors(colorResId));
        }
    }

    /**
     * 设置背景图片
     */
    public static void setToolbarBgImage(Toolbar toolbar, int drawableResId) {
        if (toolbar != null) {
            toolbar.setBackgroundResource(drawableResId);
        }
    }

    /**
     * 设置toolbar上间距
     *
     * @param toolbar
     */
    public static void paddingToolbar(View toolbar) {
        ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
        layoutParams.height = (int) (Views.fromDimens(com.pearl.act.R.dimen.toolbar_height) + StatusBarHelper.getStatusBarHeight(toolbar.getContext()));
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        toolbar.setLayoutParams(layoutParams);
        toolbar.setPadding(0, StatusBarHelper.getStatusBarHeight(toolbar.getContext()), 0, 0);
    }

    /**
     * 获取球的默认颜色
     *
     * @param num
     * @return
     */
    public static int getNumBgColor(int num) {
        int color = 0;
        switch (num) {
            case 1: {
                color = Color.parseColor("#E6DE00");
                break;
            }
            case 2: {
                color = Color.parseColor("#0092DD");
                break;
            }
            case 3: {
                color = Color.parseColor("#4B4B4B");
                break;
            }
            case 4: {
                color = Color.parseColor("#FF7600");
                break;
            }
            case 5: {
                color = Color.parseColor("#17E2E5");
                break;
            }
            case 6: {
                color = Color.parseColor("#5234FF");
                break;
            }
            case 7: {
                color = Color.parseColor("#BFBFBF");
                break;
            }
            case 8: {
                color = Color.parseColor("#FF2600");
                break;
            }
            case 9: {
                color = Color.parseColor("#780B00");
                break;
            }
            case 10: {
                color = Color.parseColor("#07BF00");
                break;
            }

            default: {
                color = colorPrimary;
            }
        }
        return color;
    }

    public abstract void simpleToolbarLeftBackAndCenterTitleAndeRightTextBtn(Context ctx, Toolbar toolbar, String centerTitle, String rightBtn, View.OnClickListener rightListener);
    
    public abstract void simpleToolbarLeftTextAndCenterTitleAndeRightTextBtn(Context ctx, Toolbar toolbar, String leftBtn, View.OnClickListener leftListener, String centerTitle, String rightBtn, View.OnClickListener rightListener);
}
