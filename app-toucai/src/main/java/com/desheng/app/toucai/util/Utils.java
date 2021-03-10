package com.desheng.app.toucai.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.util.Strs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者：Rance on 2016/12/20 16:41
 * 邮箱：rance935@163.com
 */
public class Utils {
    /**
     * dp转dip
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5F);
    }

    /**
     * 文本中的emojb字符处理为表情图片
     *
     * @param context
     * @param tv
     * @param source
     * @return
     */
    public static SpannableString getEmotionContent(final Context context, final TextView tv, String source) {
        SpannableString spannableString = new SpannableString(source);
        Resources res = context.getResources();

        String regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);

        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            // 利用表情名字获取到对应的图片
            Integer imgRes = EmotionUtils.EMOTION_STATIC_MAP.get(key);
            if (imgRes != null) {
                // 压缩表情图片
                int size = (int) tv.getTextSize() * 13 / 8;
                Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);

                ImageSpan span = new ImageSpan(context, scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    /**
     * 返回当前时间的格式为 yyyyMMddHHmmss
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(System.currentTimeMillis());
    }

    //毫秒转秒
    public static String long2String(long time) {
        //毫秒转秒
        int sec = (int) time / 1000;
        int min = sec / 60;    //分钟
        sec = sec % 60;        //秒
        if (min < 10) {    //分钟补0
            if (sec < 10) {    //秒补0
                return "0" + min + ":0" + sec;
            } else {
                return "0" + min + ":" + sec;
            }
        } else {
            if (sec < 10) {    //秒补0
                return min + ":0" + sec;
            } else {
                return min + ":" + sec;
            }
        }
    }

    /**
     * 毫秒转化时分秒毫秒
     *
     * @param ms
     * @return
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "d");
        }
        if (hour > 0) {
            sb.append(hour + "h");
        }
        if (minute > 0) {
            sb.append(minute + "′");
        }
        if (second > 0) {
            sb.append(second + "″");
        }
        return sb.toString();
    }

    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static String getDate(String formatStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static String getDate(long mminites, String formatStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
        Date curDate = new Date(mminites);
        return formatter.format(curDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(statusColor);
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    public static String stripHtml(String content) {
        // <p>段落替换为换行
        content = content.replaceAll("<p .*?>", "\r\n");
        // <br><br/>替换为换行
        content = content.replaceAll("<br\\s*/?>", "\r\n");
        // 去掉其它的<>之间的东西
        content = content.replaceAll("\\<.*?>", "");
        // 还原HTML
        // content = HTMLDecoder.decode(content);
        //&ldquo;&quot;&nbsp;
        content = content.replaceAll("&.dquo;", "\"");
        content = content.replaceAll("&nbsp;", " ");
        return content;
    }

    /**
     *      * 获取double数据1位小数点后两位不进行四舍五入
     *      * @param value
     *      * @return -1 , double
     */
    public static double getDoubleNotRound1(double value) {
        DecimalFormat formater = new DecimalFormat("#0.#");
        formater.setRoundingMode(RoundingMode.FLOOR);
        return Double.parseDouble(formater.format(value));
    }

    /**
     *      * 获取double数据2位小数点后两位不进行四舍五入
     *      * @param value
     *      * @return -1 , double
     */
    public static double getDoubleNotRound2(double value) {
        DecimalFormat formater = new DecimalFormat("#0.####");
        formater.setRoundingMode(RoundingMode.FLOOR);
        double v = Double.parseDouble(formater.format(value));
        BigDecimal bigDecimal = new BigDecimal(v);
        return bigDecimal.setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
    }

    public static String getDoubleNotRound2Str(double value) {
        DecimalFormat formater = new DecimalFormat("#0.####");
        formater.setRoundingMode(RoundingMode.FLOOR);
        double v = Double.parseDouble(formater.format(value));
        BigDecimal bigDecimal = new BigDecimal(v);
        return bigDecimal.setScale(4, BigDecimal.ROUND_DOWN).toPlainString();
    }

    /**
     * 判断单双
     *
     * @param value
     * @return
     */
    public static String getLotteryCodeTypeDS(String value) {
        try {
            int parseInt = Integer.parseInt(value);
            return parseInt % 2 == 0 ? "双" : "单";
        } catch (Exception e) {
            e.printStackTrace();
            return "?";
        }
    }

    /**
     * 判断大小
     *
     * @param value
     * @return
     */
    public static String getLotteryCodeTypeDX(String value, String lotterycategory) {
        try {
            int parseInt = Integer.parseInt(value);

            if (Strs.isEqual("11X5", lotterycategory) || Strs.isEqual("PK10", lotterycategory)) {
                return parseInt >= 6 ? "大" : "小";
            } else {
                return parseInt >= 5 ? "大" : "小";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "?";
        }
    }

    /**
     * 六合彩判特码大小
     *
     * @param value
     * @return
     */
    public static String getLotteryCodeTypeLhcDX(String value) {
        try {
            int parseInt = Integer.parseInt(value);
            return parseInt >= 25 ? "大" : "小";
        } catch (Exception e) {
            e.printStackTrace();
            return "?";
        }
    }

    /**
     * 六合彩判断合单双
     *
     * @param value
     * @return
     */
    public static String getLotteryCodeTypeLhcHDS(String value) {
        try {
            int parseInt = Integer.parseInt(value);
            return numberSum(parseInt) % 2 == 0 ? "双" : "单";
        } catch (Exception e) {
            e.printStackTrace();
            return "?";
        }
    }

    public static int numberSum(int a) {
        int n = a % 10;
        a = a - n;
        int m = a / 10;
        int sum = 0;
        sum = sum + n;

        while (m != 0) {
            sum = sum + m % 10;
            m = m / 10;
        }
        return sum;
    }

    /**
     * 六合彩判断合大小
     *
     * @param value
     * @return
     */
    public static String getLotteryCodeTypeLhcHDX(String value) {
        try {
            int parseInt = Integer.parseInt(value);
            return numberSum(parseInt) >= 7 ? "大" : "小";
        } catch (Exception e) {
            e.printStackTrace();
            return "?";
        }
    }

    /**
     * 六合彩判断尾大小
     *
     * @param value
     * @return
     */
    public static String getLotteryCodeTypeLhcWDX(String value) {
        try {
            int parseInt = Integer.parseInt(value);
            //比较个位上的数据
            return parseInt % 10 >= 5 ? "大" : "小";
        } catch (Exception e) {
            e.printStackTrace();
            return "?";
        }
    }

}
