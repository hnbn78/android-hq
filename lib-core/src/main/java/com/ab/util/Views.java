package com.ab.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.core.R;
import com.ab.debug.AbDebug;
import com.ab.global.AbDevice;
import com.ab.global.ENV;
import com.ab.global.Global;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

public class Views {
    /**
     * Special value for the height or width requested by a View.
     * MATCH_PARENT means that the view wants to be as big as its parent,
     * minus the parent's padding, if any. Introduced in API Level 8.
     */
    public static final int MATCH_PARENT = -1;
    
    /**
     * Special value for the height or width requested by a View.
     * WRAP_CONTENT means that the view wants to be just large enough to fit
     * its own internal content, taking its own padding into account.
     */
    public static final int WRAP_CONTENT = -2;
    
    public static void setHeightByRatio(View view, double ratio) {
        int widthPixels = AbDevice.SCREEN_WIDTH_PX;
        setHeightByRatio(view, widthPixels, ratio);
    }
    
    public static void setHeightByRatio(View view, int width, double ratio) {
        int widthPixels = width;
        int heightPixels = (int) (widthPixels * ratio);
        // 不能这样, 转型错误
        // ViewGroup.LayoutParams params = new LayoutParams(width, height);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            AbDebug.error(AbDebug.TAG_APP, "setViewHeightByRatio出错,如果是代码new出来的View，需要设置一个适合的LayoutParams");
            return;
        }
        if (ratio == 0) {
            AbDebug.error(AbDebug.TAG_APP, "setViewHeightByRatio出错,ratio为0");
            return;
        }
        
        params.width = widthPixels;
        params.height = heightPixels;
        view.setLayoutParams(params);
    }
    
    public static void setWidthByRatio(View view, int height, double ratio) {
        int heightPixels = height;
        int widthPixels = (int) (height * ratio);
        // 不能这样, 转型错误
        // ViewGroup.LayoutParams params = new LayoutParams(width, height);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            AbDebug.error(AbDebug.TAG_APP, "setViewHeightByRatio出错,如果是代码new出来的View，需要设置一个适合的LayoutParams");
            return;
        }
        if (ratio == 0) {
            AbDebug.error(AbDebug.TAG_APP, "setViewHeightByRatio出错,ratio为0");
            return;
        }
        
        params.width = widthPixels;
        params.height = heightPixels;
        view.setLayoutParams(params);
    }
    
    /**
     * /**
     * MATCH_PARENT = -1; WRAP_CONTENT = -2
     */
    public static LinearLayout.LayoutParams genLLParamsByDP(int width, int height) {
        int pxWidth = width;
        int pxHeight = height;
        if (pxWidth > 0) {
            pxWidth = (int) dp2px((float) width);
        }
        if (pxHeight > 0) {
            pxHeight = (int) dp2px((float) height);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pxWidth, pxHeight);
        return params;
    }
    
    /**
     * MATCH_PARENT = -1; WRAP_CONTENT = -2
     */
    public static LinearLayout.LayoutParams genLLParamsByPX(int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        return params;
    }
    
    /**
     * MATCH_PARENT = -1; WRAP_CONTENT = -2
     */
    public static RelativeLayout.LayoutParams genRLParamsDP(int width, int height) {
        int pxWidth = width;
        int pxHeight = height;
        if (pxWidth > 0) {
            pxWidth = (int) dp2px((float) width);
        }
        if (pxHeight > 0) {
            pxHeight = (int) dp2px((float) height);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pxWidth, pxHeight);
        return params;
    }
    
    /**
     * MATCH_PARENT = -1; WRAP_CONTENT = -2
     */
    public static RelativeLayout.LayoutParams genRLParamsPX(int width, int height) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        return params;
    }
    
    public static void addTitleBar(Activity activity) {
    
    }
    
    /**
     * 描述：dip转换为px.
     *
     * @param dipValue the dip value
     * @return px值
     */
    public static int dp2px(float dipValue) {
        DisplayMetrics mDisplayMetrics = AbDevice.getDM();
        return (int) applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, mDisplayMetrics);
    }
    
    /**
     * 描述：px转换为dip.
     *
     * @param pxValue the px value
     * @return dip值
     */
    public static int px2dp(float pxValue) {
        DisplayMetrics mDisplayMetrics = AbDevice.getDM();
        return (int) (pxValue / mDisplayMetrics.density);
    }
    
    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue) {
        final float fontScale = Global.app.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
    
    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = Global.app.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    
    
    /**
     * TypedValue官方源码中的算法，任意单位转换为PX单位
     *
     * @param unit    TypedValue.COMPLEX_UNIT_DIP
     * @param value   对应单位的值
     * @param metrics 密度
     * @return px值
     */
    public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f / 72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f / 25.4f);
        }
        return 0;
    }
    
    /**
     * 获取尺寸像素值
     */
    public static float fromDimens(int resId) {
        float dimens = 0f;
        try {
            dimens = Global.app.getResources().getDimension(resId);
        } catch (Exception e) {
            dimens = 0f;
        }
        return dimens;
    }
    
    /**
     * 获取颜色值
     */
    public static int fromColors(int resId) {
        // 默认值
        int res = 0;
        try {
            res = Global.app.getResources().getColor(resId);
        } catch (Exception e) {
            res = Global.app.getResources().getColor(android.R.color.black);
        }
        return res;
    }
    
    public static String fromStrings(int resId) {
        // 默认值
        String res = "";
        try {
            res = Global.app.getResources().getString(resId);
        } catch (Exception e) {
            res = "";
        }
        return res;
    }
    
    public static Drawable fromDrawables(int resId) {
        Drawable drawable = null;
        try {
            drawable = Global.app.getResources().getDrawable(resId);
        } catch (Exception e) {
            drawable = null;
        }
        return drawable;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getTag(View view, int tag, T def) {
        T res = def;
        Object obj = view.getTag(tag);
        if (obj != null) {
            try {
                res = (T) obj;
            } catch (Exception e) {
                res = def;
            }
        }
        return res;
    }
    
    public static void freezeViewByTime(final View view, int mills) {
        view.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setClickable(true);
            }
        }, mills);
    }
    
    public static <T extends Object> T getValue(TextView et, T def) {
        Object res = def;
        try {
            String str = et.getText().toString().trim();
            res = Strs.parse(str, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) res;
    }
    
    public static String getText(TextView et) {
        return et.getText().toString().trim();
    }
    
    public static boolean checkTextAndToast(Context ctx, String str, int infoResId) {
        boolean isValid = false;
        if (!Strs.isEmpty(str)) {
            isValid = true;
        } else {
            Toasts.show(ctx, infoResId);
        }
        return isValid;
    }
    
    public static boolean checkNumberAndToast(Context ctx, String str, int infoResId) {
        boolean isValid = false;
        if (!Strs.isEmpty(str) && Strs.parse(str, 0.0) > 0) {
            isValid = true;
        } else {
            Toasts.show(ctx, infoResId);
        }
        return isValid;
    }
    
    public static boolean checkTextAndToast(Context ctx, String str, String info) {
        boolean isValid = false;
        if (!Strs.isEmpty(str)) {
            isValid = true;
        } else {
            Toasts.show(ctx, info);
        }
        return isValid;
    }
    
    public static boolean extractTextAndCheck(Context ctx, TextView tv, String info) {
        boolean isValid = false;
        String str = getText(tv);
        if (!Strs.isEmpty(str)) {
            isValid = true;
        } else {
            Toasts.show(ctx, info);
        }
        return isValid;
    }
    
    public static boolean checkNumberAndToast(Context ctx, String str, String info) {
        boolean isValid = false;
        if (!Strs.isEmpty(str) && Strs.parse(str, 0.0) > 0) {
            isValid = true;
        } else {
            Toasts.show(ctx, info);
        }
        return isValid;
    }
    
    public static boolean checkPhoneAndToast(Context ctx, String str, String info) {
        boolean isValid = false;
        if (!Strs.isEmpty(str) && Strs.isPhoneValid(str) && str.length() >= 11) {
            isValid = true;
        } else {
            Toasts.show(ctx, info, false);
        }
        return isValid;
    }
    
    public static boolean checkEmailAndToast(Context ctx, String str, String info) {
        boolean isValid = false;
        if (!Strs.isEmpty(str) && Strs.verifyEmail(str)) {
            isValid = true;
        } else {
            Toasts.show(ctx, info);
        }
        return isValid;
    }
    
    public static boolean checkPasswordAndToast(Context ctx, String str, String info) {
        boolean isValid = false;
        if (!Strs.isEmpty(str) && Strs.isPassword(str)) {
            isValid = true;
        } else {
            Toasts.show(ctx, info);
        }
        return isValid;
    }
    
    public static void setListViewFullHeight(ListView lv) {
        ListAdapter la = lv.getAdapter();
        if (null == la) {
            return;
        }
        // calculate height of all items.
        int h = 0;
        final int cnt = la.getCount();
        for (int i = 0; i < cnt; i++) {
            View item = la.getView(i, null, lv);
            item.measure(0, 0);
            h += item.getMeasuredHeight();
        }
        // reset ListView height
        ViewGroup.LayoutParams lp = lv.getLayoutParams();
        lp.height = h + (lv.getDividerHeight() * (cnt - 1));
        lv.setLayoutParams(lp);
    }
    
    public static void setEditorEnable(EditText editText, boolean isEnable) {
        if (!isEnable) { // disable editing password
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            editText.setClickable(false); // user navigates with wheel and selects widget
        } else { // enable editing of password
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setClickable(true);
        }
    }
    
    
    public static void setHeight(View view, int heightInPx) {
        ViewGroup.LayoutParams param = view.getLayoutParams();
        param.height = heightInPx;
        view.setLayoutParams(param);
    }
    
    public static void setWidthAndHeight(View view, int widthInPx, int heightInPx) {
        ViewGroup.LayoutParams param = view.getLayoutParams();
        if (param == null) {
            throw new RuntimeException("layoutparam 为空");
        }
        param.height = heightInPx;
        param.width = widthInPx;
        view.setLayoutParams(param);
    }
    
    public static void setMargin(View view, int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.leftMargin = left;
        params.topMargin = top;
        params.rightMargin = right;
        params.bottomMargin = bottom;
    }
    
    public static LinearLayoutManager genLinearLayoutManagerH(Context ctx) {
        LinearLayoutManager manager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        return manager;
    }
    
    public static LinearLayoutManager genLinearLayoutManagerV(Context ctx) {
        LinearLayoutManager manager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        return manager;
    }
    
    public static GridLayoutManager genGridLayoutManager(Context ctx, int columnCount) {
        GridLayoutManager manager = new GridLayoutManager(ctx, columnCount);
        return manager;
    }
    
    public static void loadImage(final ImageView view, File img, int widthDp, int heightDp) {
        Glide.with(view.getContext())
                .load(img)
                .placeholder(R.drawable.ab_img_def)
                .override(Views.dp2px(widthDp), Views.dp2px(heightDp)) // resizes the image to these dimensions (in pixel)
                .centerCrop()
                .crossFade()
                .into(view);
    }
    
    public static void setShapeColorOfSelector(StateListDrawable drawable, int colorResId) {
        ((GradientDrawable) (drawable).getCurrent()).setColor(Views.fromColors(colorResId));
    }
    
    public static GridLayout.LayoutParams genGridLayoutItemParam() {
        GridLayout.Spec rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f);
        GridLayout.Spec columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
        layoutParams.height = 0;
        layoutParams.width = 0;
        return layoutParams;
    }
    
    public static void setAppBarScrollEnable(final AppBarLayout appBar, boolean isEnable) {
        if (isEnable) {
            if (ViewCompat.isLaidOut(appBar)) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                behavior.setDragCallback(null);
            } else {
                appBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            appBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            appBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
                        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                        behavior.setDragCallback(null);
                    }
                });
            }
        } else {
            if (ViewCompat.isLaidOut(appBar)) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                    @Override
                    public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                        return false;
                    }
                });
            } else {
                appBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            appBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            appBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
                        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                            
                            @Override
                            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                                return false;
                            }
                        });
                    }
                });
            }
        }
    }
    
    public static TextWatcher setTextDecimalCountWatcher(final EditText editText, final int decimalCount) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > decimalCount) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + decimalCount + 1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        editText.addTextChangedListener(watcher);
        return watcher;
    }
    
    /**
     * EditText获取焦点并显示软键盘
     */
    public static void setEditTextFocus(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.requestFocusFromTouch();
        InputUtils.showKeyboard(editText);
    }
    
    public static void setText(View textView, int resId) {
        if (resId > 0) {
            String string = fromStrings(resId);
            setText(textView, string);
        }
    }
    
    public static void setText(View root, int tvId, int resId) {
        if (resId > 0 && tvId > 0) {
            String string = fromStrings(resId);
            TextView textView = root.findViewById(tvId);
            setText(textView, string);
        }
    }
    
    public static void setText(View root, int tvId, CharSequence charSequence) {
        if (tvId > 0) {
            TextView textView = root.findViewById(tvId);
            setText(textView, charSequence);
        }
    }
    
    public static void setText(View textView, CharSequence charSequence) {
        if (textView != null && textView instanceof TextView) {
            ((TextView) textView).setText(charSequence);
        }
    }
    
    public static void loadImage(Context ctx, View imageView, String uri) {
        if (imageView != null && imageView instanceof ImageView) {
            String uriTag = (String) imageView.getTag(R.id.id_img_url_tag);
            if (Strs.isNotEmpty(uriTag) && uriTag.equals(uri)) {
                return;
            }
            imageView.setTag(R.id.id_img_url_tag, uri);
            if (Strs.isNotEmpty(uri) && uri.startsWith("file://")) {
                Glide.with(ctx).load(Uri.parse(uri)).placeholder(R.drawable.ab_sh_bg_rect_trans).into((ImageView) imageView);
            } else {
                if (Strs.isNotEmpty(uri) && !uri.startsWith("http")) {
                    uri = ENV.curr.host + uri;
                }
                Glide.with(ctx).load(uri).placeholder(R.drawable.ab_sh_bg_rect_trans).into((ImageView) imageView);
            }
        }
    }
    
    public static void loadImage(Context ctx, final View imageView, String uri, final int desireWith) {
        if (imageView != null && imageView instanceof ImageView) {
            String uriTag = (String) imageView.getTag(R.id.id_img_url_tag);
            if (Strs.isNotEmpty(uriTag) && uriTag.equals(uri)) {
                return;
            }
            imageView.setTag(R.id.id_img_url_tag, uri);
            if (Strs.isNotEmpty(uri) && uri.startsWith("file://")) {
                Glide.with(ctx).load(Uri.parse(uri)).asBitmap().placeholder(R.drawable.ab_sh_bg_rect_trans).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Views.setHeightByRatio(imageView, desireWith, 1.0 * resource.getHeight() / resource.getWidth());
                        ((ImageView) imageView).setScaleType(ImageView.ScaleType.FIT_XY);
                        ((ImageView) imageView).setImageBitmap(resource);
                    }
                });
            } else {
                if (Strs.isNotEmpty(uri) && !uri.startsWith("http")) {
                    uri = ENV.curr.host + uri;
                }
                Glide.with(ctx).load(uri).asBitmap().placeholder(R.drawable.ab_sh_bg_rect_trans).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Views.setHeightByRatio(imageView, desireWith, 1.0 * resource.getHeight() / resource.getWidth());
                        ((ImageView) imageView).setScaleType(ImageView.ScaleType.FIT_XY);
                        ((ImageView) imageView).setImageBitmap(resource);
                    }
                });
            }
        }
    }
    
    
    public static void loadImage(Context ctx, View root, int ivId, String uri) {
        if (ivId > 0) {
            ImageView imageView = root.findViewById(ivId);
            loadImage(ctx, imageView, uri);
        }
    }
    
    
    public static void loadImage(Context ctx, View imageView, int srcId) {
        if (imageView != null && imageView instanceof ImageView) {
            Glide.with(ctx).load(srcId).placeholder(R.drawable.ab_sh_bg_rect_trans).into((ImageView) imageView);
        }
    }
    
    public static void loadImageAny(Context ctx, View imageView, Object obj) {
        if (obj instanceof Integer || obj instanceof Long) {
            loadImage(ctx, imageView, (Integer) obj);
        } else if (obj instanceof String) {
            loadImage(ctx, imageView, (String) obj);
        } else {
            Glide.with(ctx).load("").placeholder(R.drawable.ab_sh_bg_rect_trans).into((ImageView) imageView);
        }
    }
    
    public static void loadImageAnyWithWidth(Context ctx, final View imageView, Object obj, final int desireWith) {
        if (obj instanceof Integer || obj instanceof Long) {
            loadImage(ctx, imageView, (Integer) obj);
        } else if (obj instanceof String) {
            loadImage(ctx, imageView, (String) obj, desireWith);
        } else {
            Glide.with(ctx).load("").asBitmap().placeholder(R.drawable.ab_sh_bg_rect_trans).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Views.setHeightByRatio(imageView, desireWith, 1.0 * resource.getHeight() / resource.getWidth());
                    ((ImageView) imageView).setScaleType(ImageView.ScaleType.FIT_XY);
                    ((ImageView) imageView).setImageBitmap(resource);
                }
            });
        }
    }
    
    public static void loadBackgroudAny(final Context ctx, final View view, final Object obj) {
        if (obj instanceof Integer || obj instanceof Long) {
            view.setBackgroundResource((Integer) obj);
        } else if (obj instanceof String) {
            String uri = (String) obj;
            if (Strs.isNotEmpty(uri) && uri.startsWith("file://")) {
                Glide.with(ctx).load(Uri.parse(uri)).asBitmap().placeholder(R.drawable.ab_sh_bg_rect_trans).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        view.setBackgroundDrawable(new BitmapDrawable(ctx.getResources(), resource));
                    }
                });
            } else {
                if (Strs.isNotEmpty(uri) && !uri.startsWith("http")) {
                    uri = ENV.curr.host + uri;
                }
                Glide.with(ctx).load(uri).asBitmap().placeholder(R.drawable.ab_sh_bg_rect_trans).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        view.setBackgroundDrawable(new BitmapDrawable(ctx.getResources(), resource));
                    }
                });
            }
        }
    }
}
