package com.ab.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.ab.global.AbAppConfig;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ab.util.Dates.paddingMonth;


public class Strs {
    public static boolean isEmpty(String str) {
        boolean isEmpty = false;
        if (str == null || ("").equals(str.trim()) || ("null").equalsIgnoreCase(str.trim())) {
            isEmpty = true;
        }
        return isEmpty;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmptyOrZero(String str) {
        boolean isEmpty = false;
        if (str == null || ("").equals(str.trim()) || parse(str, 0D) == 0) {
            isEmpty = true;
        }
        return isEmpty;
    }

    public static boolean isEqual(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    @SuppressWarnings("unchecked")
    public static <T> T parse(String str, T def) {
        Object value = def;
        if (!isEmpty(str)) {
            try {
                str = str.trim();
                if (def instanceof Short) {
                    value = Short.parseShort(str);
                } else if (def instanceof Integer) {
                    value = Integer.parseInt(str);
                } else if (def instanceof Long) {
                    value = Long.parseLong(str);
                } else if (def instanceof Double) {
                    value = Double.parseDouble(str);
                } else if (def instanceof String) {
                    value = str;
                } else if (def instanceof Boolean) {
                    value = Boolean.parseBoolean(str);
                } else if (def instanceof Float) {
                    value = Float.parseFloat(str);
                }
            } catch (Exception e) {

            }

        }
        return (T) value;
    }

    public static String toStr(Object o) {
        if (o == null) {
            return "";
        } else {
            return String.valueOf(o);
        }
    }

    public static String toStr(String devider, Object[] objs) {
        StringBuilder builder = new StringBuilder();
        String string = "";
        for (int i = 0; i < objs.length; i++) {
            if (objs[i] == null) {
                string = "";
            } else {
                string = String.valueOf(objs[i]);
            }
            builder.append(builder.length() == 0 ? string : devider + string);
        }
        return builder.toString();
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isAllChinese(String string) {
        String reg = "[\\u4e00-\\u9fa5]+";
        return string.matches(reg);
    }

    public static boolean isAllChineseWithDot(String string) {
//        String reg = "[\\u4e00-\\u9fa5]+·*•*[\\u4e00-\\u9fa5]+";
        String reg = "[\\u4e00-\\u9fa5·•]+";
        return string.matches(reg);
    }

    /**
     * 从任意字符串中提取电话号码
     *
     * @param str
     * @return 任何非法电话号码返回""
     */
    public static String getPhoneNumber(String str) {
        String numStr = "";
        str = str.trim();
        long number = parse(str, 0L);
        if (number != 0 && str.length() == AbAppConfig.PHONE_NUMBER_LENGTH) {
            numStr = String.valueOf(number);
        }
        return numStr;
    }

    /**
     * 替换str中所有的?
     *
     * @param str
     * @param args
     * @return
     */
    public static String genFromHolders(String str, String holder, String... args) {
        String newStr = str;
        for (int i = 0; i < args.length; i++) {
            if (holder.equals("?")) {
                newStr = newStr.replaceFirst("\\?", args[i]);
            } else if (holder.equals("{?}")) {
                newStr = newStr.replaceFirst("\\{\\?\\}", args[i]);
            }
        }
        return newStr;
    }

    public static String fromRes(Context ctx, int resId) {
        String res = ctx.getResources().getString(resId);
        return res;
    }

    public static String setDefaultEmpty(String str) {
        if (isEmpty(str)) {
            str = "";
        }
        return str;
    }

    public static String setDefault(String str, String def) {
        if (isEmpty(str)) {
            str = def;
        }
        return str;
    }

    public static String setDefaultNoZero(String str, String def) {
        if (isEmpty(str) || Strs.parse(str, 0.0) == 0) {
            str = def;
        }
        return str;
    }

    /**
     * 获取5***2@XX.XXX类似的字符串
     * 非法返回"";
     * TODO.
     *
     * @return
     */
    public static String getEmailObscuredMiddle(String email) {
        int index = email.indexOf("@", 0);
        String sub = "";
        if (index >= 1) {
            sub = email.substring(index - 1);
            sub = email.substring(0, 1) + "****" + sub;
        }
        return sub;
    }

    /**
     * 获取532**@XX.XXX类似的字符串
     * 非法返回"";
     * TODO.
     *
     * @return
     */
    public static String getEmailObscuredEnd(String email) {
        int index = email.indexOf("@", 0);
        int endIndex = index;
        String sub = "";
        if (index < 0) {
            return "";
        } else if (index > 3) {
            endIndex = 3;
        }
        sub = email.substring(0, endIndex) + "****" + email.substring(index);
        return sub;
    }

    @SuppressLint("SimpleDateFormat")
    public static String setDefaultDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (isEmpty(str) || Strs.parse(str, 0L) == 0) {
            Date data = new Date();
            str = format.format(data);
        } else {
            Date data = new Date(Strs.parse(str, 0L));
            str = format.format(data);
        }
        return str;
    }


    /**
     * 校验是否存在到此范围
     *
     * @param lowerLimit
     * @param limit
     * @param value
     * @return
     */
    public static boolean verifyRange(Integer lowerLimit, Integer limit, Integer value) {
        if (lowerLimit == null || limit == null || value == null) {
            return false;
        }
        if (value > lowerLimit && value < limit) {
            return true;
        }
        return false;
    }

    /**
     * 验证身份证号
     *
     * @param numberId
     * @return
     */
    public static boolean verifyIdCard(String numberId) {
        if (TextUtils.isEmpty(numberId)) {
            return false;
        }
        return verify(numberId);
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean verifyEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        Pattern p1 = null;
        Matcher m = null;
        p1 = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
        m = p1.matcher(email);
        return m.matches();
    }

    // 判断，返回布尔值
    public static boolean isPhoneValid(String input) {
        return verifyMobile(input) || verifyPhone(input);
    }

    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean verifyPhone(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7}|0\\d{2} \\d{8})|(0\\d{3} \\d{7}|0\\d{2}~\\d{8})|(0\\d{3}~\\d{7})$"); // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // 验证没有区号的

        if (str.contains("-") || str.trim().contains(" ") || str.contains("~")) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean verifyMobile(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^1[3,4,5,6,7,8,9][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 简单IP验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean verifyIP(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        m = p.matcher(str);
        b = m.matches();
        return b;
    }


    /**
     * @param @param  number
     * @param @return 设定文件
     * @return boolean 返回类型
     * @throws
     * @Title: isNum
     * @Description: TODO(校验6位数数字)
     */
    public static boolean isNum(String number) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^[0-9]{6}$");
            Matcher m = p.matcher(number);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean isPassword(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        String trim = content.trim();
        return trim.length() >= 6 && trim.length() <= 16;
    }


    public static boolean containQQ(String txt) {
        String qqRegex = ".*Q*q*.*[0-9]{7,}.*";
        String qqRev = ".*[0-9]{7,}Q*q*.*";
        return txt.matches(qqRegex) || txt.matches(qqRev);
    }

    public static boolean containWeiXin(String txt) {
        String wxRegex = ".*((微+.*信+)|((wei)+.*(xin)+))+.*[a-z|A-Z|0-9]{6,20}.*";
        String wxRev = ".*[a-z|A-Z|0-9]{6,20}.*((微+.*信+)|((wei)+.*(xin)+))+.*";
        return txt.matches(wxRegex) | txt.matches(wxRev);
    }

    public static boolean containPhone(String txt) {
        return txt.matches(".*[1][3,4,5,8,7][0-9]{9}.*");
    }

    public static void main(String[] args) {
        paddingMonth(1);

        String qq = "微 信asdqfqq2341223asdfas";
        String wx = "asdqfq2341223asdfas";
        String qqRegex = ".*Q*q*.*[0-9]{7,}.*Q*q*.*";//第一位1-9之间的数字，第二位0-9之间的数字，数字范围4-14个之间
        //String wxRegex = "[a-z|A-Z|0-9]{6,20}";
        String wxRegex = ".*(((微+.*信+)|((wei)+.*(xin)+)))+.*[a-z|A-Z|0-9]{6,20}.*";
        String wxRev = ".*[a-z|A-Z|0-9]{6,20}.*((微+.*信+)|((wei)+.*(xin)+))+";
        //String phone = 15268536559
        //String regex2 = "[1-9]\\d{4,14}";//此句也可以
        boolean flag = qq.matches(wxRegex);
        if (flag)
            System.out.println("包含qq");
        else
            System.out.println("QQ号错误");
    }


    /*** 校验身份证号 begin*/
    // wi =2(n-1)(mod 11);加权因子
    private static final int[] wi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1};
    // 校验码
    private static final int[] vi = {1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2};
    private static int[] ai = new int[18];

    // 校验身份证的校验码
    private static boolean verify(String idcard) {
        if (idcard.length() == 15) {
            idcard = uptoeighteen(idcard);
        }
        if (idcard.length() != 18) {
            return false;
        }
        String verify = idcard.substring(17, 18);
        if (verify.equals(getVerify(idcard))) {
            return true;
        }
        return false;
    }

    // 15位转18位
    private static String uptoeighteen(String fifteen) {
        StringBuffer eighteen = new StringBuffer(fifteen);
        eighteen = eighteen.insert(6, "19");
        return eighteen.toString();
    }

    // 计算最后一位校验值
    private static String getVerify(String eighteen) {
        int remain = 0;
        if (eighteen.length() == 18) {
            eighteen = eighteen.substring(0, 17);
        }
        if (eighteen.length() == 17) {
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                String k = eighteen.substring(i, i + 1);
                ai[i] = Integer.valueOf(k);
            }
            for (int i = 0; i < 17; i++) {
                sum += wi[i] * ai[i];
            }
            remain = sum % 11;
        }
        return remain == 2 ? "X" : String.valueOf(vi[remain]);
    }

    public static String of(Object o) {
        if (o == null) {
            return "";
        } else {
            return String.valueOf(o);
        }
    }


    public static ArrayList<String> split(String str, String devider) {
        return ArraysAndLists.asList(str.split(devider));
    }

    public static boolean startWithCharacter(String s) {
        if (s != null && s.length() > 0) {
            String start = s.trim().substring(0, 1);
            Pattern pattern = Pattern.compile("^[A-Za-z]+$");
            return pattern.matcher(start).matches();
        } else {
            return false;
        }
    }

    public static String toUTF8(String content) {
        String string = "";
        try {
            string = new String(content.getBytes(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * @param list
     * @return
     */
    public static List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }


    public static List<String> removeDuplicateinOrder(List<String> list) {
        Set<String> set = new HashSet<String>();
        List<String> newList = new ArrayList<String>();
        for (Iterator<String> iter = list.iterator(); iter.hasNext(); ) {
            String element = iter.next();
            if (set.add(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    /**
     * 除String 小数点后多余的0
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s){
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 限制输入 小数点后几位
     * @param s
     * @param etReturn
     */
    public static void inputLimitCount(CharSequence s, EditText etReturn, int count) {
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > count) {
                s = s.toString().subSequence(0,
                        s.toString().indexOf(".") + count+1);
                etReturn.setText(s);
                etReturn.setSelection(s.length());
            }
        }
        if (s.toString().trim().equals(".")) {
            s = "0" + s;
            etReturn.setText(s);
            etReturn.setSelection(count);
        }

        if (s.toString().startsWith("0")
                && s.toString().trim().length() > 1) {
            if (!s.toString().substring(1, 2).equals(".")) {
                etReturn.setText(s.subSequence(0, 1));
                etReturn.setSelection(1);
                return;
            }
        }

    }

}
