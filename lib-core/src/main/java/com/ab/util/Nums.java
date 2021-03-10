package com.ab.util;

import android.content.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Nums {
    public static <T> boolean contains(T[] array, T value) {
        for (T t : array) {
            if (t.equals(value)) {
                return true;
            }
        }
        return false;
    }


    public static double roundDouble(double num) {
        double res = 0;
        BigDecimal bd = new BigDecimal(num);
        res = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return res;
    }

    public static double formatTriple(double num) {
        double res = 0;
        BigDecimal bd = new BigDecimal(num);
        res = bd.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        return res;
    }


    public static boolean checkPositiveAndToast(Context ctx, long num, String info) {
        boolean isValid = false;
        if (num > 0) {
            isValid = true;
        } else {
            Toasts.show(ctx, info);
        }
        return isValid;
    }

    public static <T> boolean equal(Object ob1, T ob2) {
        if (ob1 == null || ob2 == null) {
            return false;
        } else if (ob1 != null) {
            return ob1.equals(ob2);
        } else if (ob2 != null) {
            return ob2.equals(ob1);
        } else {
            return false;
        }

    }


    public static <T> boolean isNullOrZero(T obj) {
        if (obj == null || obj.equals(0)) {
            return true;
        }
        return false;
    }

    public static <T> T parse(String str, T def) {
        return Strs.parse(str, def);
    }

    public static <T> T getValue(Object obj, T def) {
        if (obj == null) {
            return def;
        } else {
            return (T) obj;
        }
    }

    /**
     * @param objValue       传入的参数
     * @param fractionDigits 保留多少位
     * @return
     */
    public static final String formatDecimal(Object objValue, int fractionDigits) {
        Double value = null;
        if (objValue instanceof String) {
            value = Double.valueOf((String) objValue);
        } else if (objValue instanceof Integer || objValue instanceof Float || objValue instanceof Double) {
            value = ((Number) objValue).doubleValue();
        } else if (objValue instanceof BigDecimal) {
            value = ((BigDecimal) objValue).doubleValue();
        } else {
            value = 0.00d;
        }
        BigDecimal doubleValue = new BigDecimal(value);
        String format = "#0.";
        for (int i = 0; i < fractionDigits; i++) {
            format += "0";
        }
        DecimalFormat df = new DecimalFormat(format);
        return df.format(doubleValue);
    }

    public static boolean isNoMoreFractionDigits(String str, int fractionLimit) {
        try {
            double num = Double.valueOf(str);//把字符串强制转换为数字
            if (str.trim().indexOf(".") == -1) {
                return true;
            }
            int fractionDigits = str.trim().length() - str.trim().indexOf(".") - 1;
            if (fractionDigits > fractionLimit) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;//如果抛出异常，返回False
        }
    }

    /**
     * @param openCode 对开奖号码进行格式化，小于10 的号码前面加0显示
     */
    public static String formatOpenCode(String openCode) {
        try {
            String[] codes = openCode.split(",");
            StringBuilder sb = new StringBuilder();
            for (String val : codes) {
                int newVal = Integer.parseInt(val);
                if (newVal > 0 && newVal < 10) {
                    String str = "0" + String.valueOf(newVal) + "\t";
                    sb.append(str);
                } else {
                    String str = String.valueOf(newVal) + "\t";
                    sb.append(str);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isFractionDigits(String str, int fractionLimit) {
        try {
            double num = Double.valueOf(str);//把字符串强制转换为数字
            if (str.trim().indexOf(".") == -1) {
                return false;
            }
            int fractionDigits = str.trim().length() - str.trim().indexOf(".") - 1;
            if (fractionDigits == fractionLimit) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;//如果抛出异常，返回False
        }
    }
}
