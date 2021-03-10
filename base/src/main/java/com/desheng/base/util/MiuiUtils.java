package com.desheng.base.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

import com.desheng.base.util.QikuUtils;

import java.lang.reflect.Method;

/**
 * Created by user on 2018/3/26.
 */

public class MiuiUtils {
    private static final String TAG = "MiuiUtils";

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    public static int getMiuiVersion() {
        String version = QikuUtils.RomUtils.getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
                Log.e(TAG, "get miui version code error, version : " + version);
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return -1;
    }

    /**
     * 检测 miui 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        } else {
//            if ((context.getApplicationInfo().flags & 1 << 27) == 1) {
//                return true;
//            } else {
//                return false;
//            }
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            Log.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }

    /**
     * 小米 ROM 权限申请
     */
    public static void applyMiuiPermission(Context context) {
        int versionCode = getMiuiVersion();
        if (versionCode == 5) {
            goToMiuiPermissionActivity_V5(context);
        } else if (versionCode == 6) {
            goToMiuiPermissionActivity_V6(context);
        } else if (versionCode == 7) {
            goToMiuiPermissionActivity_V7(context);
        } else if (versionCode == 8) {
            goToMiuiPermissionActivity_V8(context);
        } else {
            Log.e(TAG, "this is a special MIUI rom version, its version code " + versionCode);
        }
    }

    private static boolean isIntentAvailable(Intent intent, Context context) {
        if (intent == null) {
            return false;
        }
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * 小米 V5 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V5(Context context) {
        Intent intent = null;
        String packageName = context.getPackageName();
        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "intent is not available!");
        }

        //设置页面在应用详情页面
//        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//        PackageInfo pInfo = null;
//        try {
//            pInfo = context.getPackageManager().getPackageInfo
//                    (HostInterfaceManager.getHostInterface().getApp().getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            AVLogUtils.e(TAG, e.getMessage());
//        }
//        intent.setClassName("com.android.settings", "com.miui.securitycenter.permission.AppPermissionsEditor");
//        intent.putExtra("extra_package_uid", pInfo.applicationInfo.uid);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (isIntentAvailable(intent, context)) {
//            context.startActivity(intent);
//        } else {
//            AVLogUtils.e(TAG, "Intent is not available!");
//        }
    }

    /**
     * 小米 V6 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V6(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "Intent is not available!");
        }
    }

    /**
     * 小米 V7 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V7(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "Intent is not available!");
        }
    }

    /**
     * 小米 V8 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V8(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
//        intent.setPackage("com.miui.securitycenter");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setPackage("com.miui.securitycenter");
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Log.e(TAG, "Intent is not available!");
            }
        }
    }


    /**
     * Created by user on 2018/3/11.
     */

    public static class ViewUtils {


        public static void fadeOut(final View view, int duration, final boolean gone,
                                   final Runnable nextRunnable) {
            if (view.getVisibility() != View.VISIBLE || view.getAlpha() == 0) {
                // Cancel any starting animation.
                view.animate()
                        .alpha(0)
                        .setDuration(0)
                        .start();
                view.setVisibility(gone ? View.GONE : View.INVISIBLE);
                if (nextRunnable != null) {
                    nextRunnable.run();
                }
                return;
            }
            view.animate()
                    .alpha(0)
                    .setDuration(duration)
                    .setInterpolator(new FastOutLinearInInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        private boolean mCanceled = false;

                        @Override
                        public void onAnimationCancel(Animator animator) {
                            mCanceled = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (!mCanceled) {
                                view.setVisibility(gone ? View.GONE : View.INVISIBLE);
                                if (nextRunnable != null) {
                                    nextRunnable.run();
                                }
                            }
                        }
                    })
                    .start();
        }

        public static void fadeOut(View view, int duration, boolean gone) {
            fadeOut(view, duration, gone, null);
        }

        public static void fadeOut(View view, boolean gone) {
            fadeOut(view, getShortAnimTime(view), gone);
        }

        public static void fadeOut(View view) {
            fadeOut(view, true);
        }

        public static void fadeIn(View view, int duration) {
            if (view.getVisibility() == View.VISIBLE && view.getAlpha() == 1) {
                // Cancel any starting animation.
                view.animate()
                        .alpha(1)
                        .setDuration(0)
                        .start();
                return;
            }
            view.setAlpha(0);
            view.setVisibility(View.VISIBLE);
            view.animate()
                    .alpha(1)
                    .setDuration(duration)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    // NOTE: We need to remove any previously set listener or Android will reuse it.
                    .setListener(null)
                    .start();
        }

        public static void fadeIn(View view) {
            fadeIn(view, getShortAnimTime(view));
        }

        public static void fadeToVisibility(View view, boolean visible, boolean gone) {
            if (visible) {
                fadeIn(view);
            } else {
                fadeOut(view, gone);
            }
        }

        public static void fadeToVisibility(View view, boolean visible) {
            fadeToVisibility(view, visible, true);
        }

        public static void crossfade(View fromView, View toView, int duration, boolean gone) {
            fadeOut(fromView, duration, gone);
            fadeIn(toView, duration);
        }

        public static void crossfade(View fromView, View toView, boolean gone) {
            crossfade(fromView, toView, getShortAnimTime(fromView), gone);
        }

        public static void crossfade(View fromView, View toView) {
            crossfade(fromView, toView, true);
        }

        public static void fadeOutThenFadeIn(final View fromView, final View toView, final int duration,
                                             final boolean gone) {
            fadeOut(fromView, duration, gone, new Runnable() {
                @Override
                public void run() {
                    fadeIn(toView, duration);
                }
            });
        }

        public static void fadeOutThenFadeIn(View fromView, View toView, boolean gone) {
            fadeOutThenFadeIn(fromView, toView, getShortAnimTime(fromView), gone);
        }

        public static void fadeOutThenFadeIn(final View fromView, final View toView) {
            fadeOutThenFadeIn(fromView, toView, true);
        }

        public static float dpToPx(float dp, Context context) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
        }

        public static int dpToPxInt(float dp, Context context) {
            return Math.round(dpToPx(dp, context));
        }

        public static int getColorFromAttrRes(int attrRes, int defValue, Context context) {
            int[] attrs = new int[]{attrRes};
            TypedArray a = context.obtainStyledAttributes(attrs);
            int color = a.getColor(0, defValue);
            a.recycle();
            return color;
        }

        public static ColorStateList getColorStateListFromAttrRes(int attrRes, Context context) {
            int[] attrs = new int[]{attrRes};
            TypedArray a = context.obtainStyledAttributes(attrs);
            ColorStateList colorStateList = a.getColorStateList(0);
            a.recycle();
            return colorStateList;
        }

        public static Drawable getDrawableFromAttrRes(int attrRes, Context context) {
            int[] attrs = new int[]{attrRes};
            TypedArray a = context.obtainStyledAttributes(attrs);
            Drawable drawable = a.getDrawable(0);
            a.recycle();
            return drawable;
        }

        public static int getShortAnimTime(Resources resources) {
            return resources.getInteger(android.R.integer.config_shortAnimTime);
        }

        public static int getShortAnimTime(View view) {
            return getShortAnimTime(view.getResources());
        }

        public static int getShortAnimTime(Context context) {
            return getShortAnimTime(context.getResources());
        }

        public static int getMediumAnimTime(Resources resources) {
            return resources.getInteger(android.R.integer.config_mediumAnimTime);
        }

        public static int getMediumAnimTime(View view) {
            return getMediumAnimTime(view.getResources());
        }

        public static int getMediumAnimTime(Context context) {
            return getMediumAnimTime(context.getResources());
        }

        public static int getLongAnimTime(Resources resources) {
            return resources.getInteger(android.R.integer.config_longAnimTime);
        }

        public static int getLongAnimTime(View view) {
            return getLongAnimTime(view.getResources());
        }

        public static int getLongAnimTime(Context context) {
            return getLongAnimTime(context.getResources());
        }

        private static float getScreenSmallestWidthDp(Context context) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int smallestWidth = Math.min(metrics.widthPixels, metrics.heightPixels);
            return pxToDp(smallestWidth, context);
        }

        public static boolean hasSw600dp(Context context) {
            return getScreenSmallestWidthDp(context) > 600;
        }

        public static void hideTextInputLayoutErrorOnTextChange(EditText editText, final TextInputLayout textInputLayout) {

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    textInputLayout.setError(null);
                }
            });
        }

        public static View inflate(int resource, ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        }

        public static boolean isInLandscape(Context context) {
            return context.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE;
        }

        public static boolean isVisible(View view) {
            return view.getVisibility() == View.VISIBLE;
        }

        public static void postOnPreDraw(final View view, final Runnable runnable) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    runnable.run();
                    return true;
                }
            });
        }

        public static float pxToDp(float px, Context context) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return px / metrics.density;
        }

        public static int pxToDpInt(float px, Context context) {
            return Math.round(pxToDp(px, context));
        }

        public static void replaceChild(ViewGroup viewGroup, View oldChild, View newChild) {
            int index = viewGroup.indexOfChild(oldChild);
            viewGroup.removeViewAt(index);
            viewGroup.addView(newChild, index);
        }

        public static void setHeight(View view, int height) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams.height == height) {
                return;
            }
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }

        public static void setSize(View view, int size) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams.width == size && layoutParams.height == size) {
                return;
            }
            layoutParams.width = size;
            layoutParams.height = size;
            view.setLayoutParams(layoutParams);
        }

        public static void setTextViewBold(TextView textView, boolean bold) {

            Typeface typeface = textView.getTypeface();
            if (typeface.isBold() == bold) {
                return;
            }

            int style = textView.getTypeface().getStyle();
            if (bold) {
                style |= Typeface.BOLD;
            } else {
                style &= ~Typeface.BOLD;
            }
            // Workaround insane behavior in TextView#setTypeface(Typeface, int).
            if (style > 0) {
                textView.setTypeface(typeface, style);
            } else {
                textView.setTypeface(Typeface.create(typeface, style), style);
            }
        }

        public static void setTextViewItalic(TextView textView, boolean italic) {

            Typeface typeface = textView.getTypeface();
            if (typeface.isItalic() == italic) {
                return;
            }

            int style = textView.getTypeface().getStyle();
            if (italic) {
                style |= Typeface.ITALIC;
            } else {
                style &= ~Typeface.ITALIC;
            }
            // Workaround insane behavior in TextView#setTypeface(Typeface, int).
            if (style > 0) {
                textView.setTypeface(typeface, style);
            } else {
                textView.setTypeface(Typeface.create(typeface, style), style);
            }
        }

        public static void setVisibleOrGone(View view, boolean visible) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        public static void setVisibleOrInvisible(View view, boolean visible) {
            view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }

        public static void setWidth(View view, int width) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams.width == width) {
                return;
            }
            layoutParams.width = width;
            view.setLayoutParams(layoutParams);
        }



    }
}
