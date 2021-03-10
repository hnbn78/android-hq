package com.desheng.app.toucai.util;

import android.util.Log;

import com.ab.util.AbDateUtil;
import com.ab.util.Dates;

public class DateTool {

    static class DateToolHelper {
        static DateTool instance = new DateTool();
    }

    public static DateTool getInstance() {
        return DateToolHelper.instance;
    }

    private long stopTimeMillis = System.currentTimeMillis();

    public String getStopDate() {
        return Dates.getStringByFormat(stopTimeMillis, Dates.dateFormatYMDHMS);
    }

    public String getStartDate(int day) {
        long startTimeMillis = stopTimeMillis - (1000 * 60 * 60 * 24 * day);
        return Dates.getStringByFormat(startTimeMillis, Dates.dateFormatYMDHMS);
    }

    /**
     * 判断当前时间是否在时间段内
     *
     * @param beginDateStr
     * @param endDateStr
     * @param Dateformat
     * @return
     */
    public static boolean isWithinSpecifiedTime(String beginDateStr, String endDateStr, String Dateformat) {
        long nowTimel = System.currentTimeMillis();
        long beginDateLong = AbDateUtil.string2Long(beginDateStr, Dateformat);
        long endDateLong = AbDateUtil.string2Long(endDateStr, Dateformat);
        Log.d("DateTool", "nowTimel:" + nowTimel);
        Log.d("DateTool", "beginDateLong:" + beginDateLong);
        Log.d("DateTool", "endDateLong:" + endDateLong);
        Log.d("DateTool", "nowTimel > beginDateLong && nowTimel < endDateLong:" + (nowTimel > beginDateLong && nowTimel < endDateLong));
        return (nowTimel > beginDateLong && nowTimel < endDateLong);
    }

}
