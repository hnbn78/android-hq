package com.desheng.base.algorithm;//

import com.ab.debug.AbDebug;
import com.ab.util.AbStrUtil;
import com.ab.util.Strs;
import com.desheng.base.context.CtxLottery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

//  DSAlgorithmObject.m
//  DSBet
//
//  Created by Selena on 2018/1/11.
//  Copyright © 2018年 Bill. All rights reserved.
//
public class DSAlgorithmObject {
    
    /**
     * mark -1 2,3,4,4......或者  -,1错误模式
     */
    public static long getBetNumFromBetStrModeOne(String BetStr)
    {
        long betNum = 0;
        String[] list = BetStr.split(",");
        long totalMon = 1;
        for (String unitNum : list) {
            if ((unitNum.length() > 0) && unitNum.indexOf("-") == -1) {
                if (unitNum.indexOf(" ") != -1) {
                    String[] listCount = unitNum.split(" ");
                    long count = listCount.length;
                    totalMon = totalMon * count;
                }
                
            } else {
                totalMon = CtxLottery.BET_NUM_ERROE;
            }
            
        }
        betNum = totalMon;
        
        return betNum;
    }
    
    /**
     * pragma mark -单列数选号码
     */
    public static long getBetNumStr(String betNumStr, int selectNum)
    {
        String[] list = betNumStr.split(" ");
        int totalNum = list.length;
        long betNum = 0;
        betNum = getBetNumCTotalNum(totalNum, selectNum);
        return betNum;
    }
    
    
    /**
     * 组合数计算 从N项中选出M项
     * pragma mark -
     * <p>
     * C(n, m)
     * <p>
     * pragma mark -单列数选号码
     */
    public static long getBetNumCTotalNum(int n, int m)
    {
        if (m > n) {
            return CtxLottery.BET_NUM_ERROE;
        }
        long factorialNum = 1;
        for (int i = n; i >= 1; i--) {
            factorialNum = i * factorialNum;
            
        }
        
        long factorialNum1 = 1;
        for (int i = m; i >= 1; i--) {
            factorialNum1 = i * factorialNum1;
            
        }
        
        long factorialNum2 = 1;
        for (int i = (n - m); i >= 1; i--) {
            factorialNum2 = i * factorialNum2;
            
        }
        
        return (long) (factorialNum / (factorialNum2 * factorialNum1));
        
        
    }
    
    /**
     * pragma mark -
     * <p>
     * 阶乘 A(x, y)
     */
    public static long getFactorialTotal(long sumNum, long number)
    {
        if (number > sumNum) {
            return -1;
        }
        long num = sumNum;
        long factorialNum = sumNum;
        for (int i = 1; i < number; i++) {
            factorialNum = factorialNum * (num - i);
        }
        AbDebug.log(AbDebug.TAG_APP, String.format("factorialNum = %d", factorialNum));
        return (long) factorialNum;
        
    }
    
    /**
     * pragma mark -获取3和值注数
     */
    public static long getAndNumStr(long number)
    {
        long sum = 0;
        long MAX = 10;
        if (number < 10) {
            MAX = (number + 1);
        }
        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
                long m = number - i - j;
                if ((m < MAX) && (m >= 0)) {
                    sum++;
                }
            }
        }
        AbDebug.log(AbDebug.TAG_APP, String.format("sum == %d", (long) sum));
        return sum;
    }
    
    /**
     * pragma mark -
     * 获取和3值注数 不限顺序
     */
    public static long getAndNumStr2(long number)
    {
        long sum = 0;
        long MAX = 10;
        if (number < 10) {
            MAX = (number + 1);
        }
        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
                if (i <= j) {
                    long m = number - i - j;
                    if ((m < MAX) && (m >= j)) {
                        sum++;
                    }
                }
            }
        }
        
        AbDebug.log(AbDebug.TAG_APP, String.format("sum == %d", (long) sum));
        return sum;
        
    }
    
    /**
     * pragma mark -获取2和值注数
     */
    public static long get2AndNumStr(long number)
    {
        long sum = 0;
        if (number < 10) {
            sum = (number + 1);
        } else {
            sum = (19 - number);
        }
        AbDebug.log(AbDebug.TAG_APP, String.format("sum == %d", (long) sum));
        return sum;
    }
    
    /**
     * pragma mark -
     * 获取2和值注数 不限顺序
     */
    public static long get2AndNumStr2(long number)
    {
        long sum = 0;
        if (number == 0) {
            return (-1);
        }
        if (number < 10) {
            sum = (long) (Math.round(number / 2.0));
        } else {
            sum = (9 - (long) (number / 2.0));
        }
        
        AbDebug.log(AbDebug.TAG_APP, String.format("sum == %d", (long) sum));
        return sum;
        
    }
    
    
    /**
     * pragma mark -获取获得等差增长比值  1 3 6 10 15 21
     */
    public static long getLineNumStr(long number)
    {
        long lineNum = 0;
        lineNum = number * (number + 1) / 2;
        return lineNum;
    }
    
    /**
     * pragma mark -获取获得等差增长  2 6 12 20
     */
    public static long getZuLineNumStr(long number) {
        long betNum = 0;
        if (number == 2) {
            return 2;
        } else {
            number--;
            betNum = (number + 2) * (number - 1) + 2;
        }
        return betNum;
    }
    
    /**
     * mark -阶乘
     */
    public static long getFactorialStr(int number)
    {
        long factorialNum = 1;
        for (int i = number; i >= 1; i--) {
            factorialNum = i * factorialNum;
            
        }
        return factorialNum;
    }
    
    /**
     * 获取时间倒计时 交易页面
     */
    public static String getGameTimeStr(long seconds)
    {
        String timeStr = "正在开奖.";
        //String hourStr = @"00";
        String minStr = "00";
        String secStr = "00";
        
        //long hourlong = 0;
        long minInt = 0;
        long seconds2 = seconds;
        //时
//    hourInt = (int)(seconds/3600);
//    if(hourInt > 9){
//        hourStr =String.format(@"%d",(long)hourInt];
//    }else{
//        hourStr =String.format(@"0%d",(long)hourInt];
//    }
        
        seconds = seconds;//(int)(seconds - hourInt * 3600);
        minInt = (long) (seconds / 60);
        if (minInt > 9) {
            minStr = String.format("%d", (long) minInt);
        } else {
            minStr = String.format("0%d", (long) minInt);
        }
        
        seconds = (long) (seconds - minInt * 60);
        if (seconds > 9) {
            secStr = String.format("%d", (long) seconds);
        } else {
            secStr = String.format("0%d", (long) seconds);
        }
        if (seconds2 > 0) {
            //timeStr =String.format(@"%@:%@:%@",hourStr,minStr,secStr];
            timeStr = String.format("%s:%s", minStr, secStr);
        }
        
        return timeStr;
    }
    
    
    /**
     * pragma mark -重置投注号码
     */
    public static String reSetBetNum(String betNum, int isAddNum, int isDelePace, String[] list, int fromNum )
    {
        if (!(isAddNum > 0)) {
            if (isDelePace > 0) {
                String reBNum = betNum.replace(" ", "");
                return reBNum;
            }
            return betNum;
        }
        
        long firstInt = fromNum;
        if (list.length < 5) {
            StringBuffer reBetNum = new StringBuffer();
            if (firstInt == 1) {//前三或者前二 前三
                int lessNum = 5 - list.length;
                reBetNum.append(betNum);
                for (int i = 0; i < lessNum; i++) {
                    reBetNum.append(",-");
                }
                return reBetNum.toString();
            }
            if (firstInt == 2) {
                if (list.length == 4) {
                    //四星
                    reBetNum.append("-,");
                    reBetNum.append(betNum);
                } else {
                    //中三
                    reBetNum.append("-,");
                    reBetNum.append(betNum);
                    reBetNum.append(",-");
                }
            }
            if (firstInt == 3) {//后三
                reBetNum.append("-,");
                reBetNum.append("-,");
                reBetNum.append(betNum);
            }
            if (firstInt == 4) {//后二
                reBetNum.append("-,");
                reBetNum.append("-,");
                reBetNum.append("-,");
                reBetNum.append(betNum);
            }
            return reBetNum.toString();
            
        } else {
            return betNum;
        }
    }
    
    
    /**
     * pragma mark -获取直选跨度
     */
    public static long getCrossValueStr(long number)
    {
        long betNum = 0;
        long p = 0;
        p = 10 - number;
        if (number == 0) {
            return p;
        } else {
            betNum = p * 6 + p * 6 * (number - 1);
        }
        return betNum;
    }
    
    /**
     * 获取直选跨度 二直选跨度
     */
    
    public static long getCrossValueStr2(long number)
    {
        long betNum = 0;
        long p = 0;
        p = 10 - number;
        if (number == 0) {
            return 10;
        } else {
            betNum = 2 * p;
        }
        return betNum;
    }
    
    /**
     * 任选二
     */
    public static long getAny2SelectStr(String betNumStr)
    {
        long betCount = 0;
        long a, b, c, d, e;
        a = b = c = d = e = 0;
        if (betNumStr.indexOf(",") != -1) {
            String[] list = betNumStr.split(",");
            long count = list.length;
            if (count < 2) {
                return CtxLottery.BET_NUM_ERROE;
            }
            for (int i = 0; i < count; i++) {
                String numStr = list[i];
                long num = 0;
                if (numStr.indexOf(" ") != -1) {
                    String[] listNum = numStr.split(" ");
                    num = listNum.length;
                } else {
                    if (numStr.equals("-")) {
                        num = 0;
                    } else {
                        num = 1;
                    }
                }
                switch (i) {
                    case 0: {
                        a = num;
                        break;
                    }
                    case 1: {
                        b = num;
                        break;
                    }
                    case 2: {
                        c = num;
                        break;
                    }
                    case 3: {
                        d = num;
                        break;
                    }
                    case 4: {
                        e = num;
                        break;
                    }
                    
                    default:
                        break;
                }
            }
            betCount = a * (b + c + d + e) + b * (c + d + e) + c * (d + e) + d * e;
            
        } else {
            return CtxLottery.BET_NUM_ERROE;
        }
        return betCount;
    }
    
    /**
     * 任选三
     */
    public static long getAny3SelectStr(String betNumStr)
    {
        long betCount = 0;
        long a, b, c, d, e;
        a = b = c = d = e = 0;
        if (betNumStr.indexOf(",") != -1) {
            String[] list = betNumStr.split(",");
            long count = list.length;
            if (count < 3) {
                return CtxLottery.BET_NUM_ERROE;
            }
            for (int i = 0; i < count; i++) {
                String numStr = list[i];
                long num = 0;
                if (numStr.indexOf(" ") != -1) {
                    String[] listNum = numStr.split(" ");
                    num = listNum.length;
                } else {
                    if (numStr.equals("-")) {
                        num = 0;
                    } else {
                        num = 1;
                    }
                }
                switch (i) {
                    case 0: {
                        a = num;
                        break;
                    }
                    case 1: {
                        b = num;
                        break;
                    }
                    case 2: {
                        c = num;
                        break;
                    }
                    case 3: {
                        d = num;
                        break;
                    }
                    case 4: {
                        e = num;
                        break;
                    }
                    
                    default:
                        break;
                }
            }
            betCount = a * (b * c + b * d + b * e + c * d + c * e + d * e) + b * (c * d + d * e + c * e) + c * d * e;
            
        } else {
            return CtxLottery.BET_NUM_ERROE;
        }
        return betCount;
    }
    
    /**
     * 任选四
     */
    public static long getAny4SelectStr(String betNumStr)
    {
        long betCount = 0;
        long a, b, c, d, e;
        a = b = c = d = e = 0;
        if (betNumStr.indexOf(",") != -1) {
            String[] list = betNumStr.split(",");
            long count = list.length;
            if (count < 4) {
                return CtxLottery.BET_NUM_ERROE;
            }
            for (int i = 0; i < count; i++) {
                String numStr = list[i];
                long num = 0;
                if (numStr.indexOf(" ") != -1) {
                    String[] listNum = numStr.split(" ");
                    num = listNum.length;
                } else {
                    if (numStr.equals("-")) {
                        num = 0;
                    } else {
                        num = 1;
                    }
                }
                switch (i) {
                    case 0: {
                        a = num;
                        break;
                    }
                    case 1: {
                        b = num;
                        break;
                    }
                    case 2: {
                        c = num;
                        break;
                    }
                    case 3: {
                        d = num;
                        break;
                    }
                    case 4: {
                        e = num;
                        break;
                    }
                    
                    default:
                        break;
                }
            }
            betCount = a * (b * c * d + b * c * e + b * e * d + c * d * e) + b * c * d * e;
            
        } else {
            return CtxLottery.BET_NUM_ERROE;
        }
        return betCount;
    }
    
    /**
     * 剔除豹子 单数
     */
    
    public static boolean isAllSameNum(String numStr) {
        int needNum = 1;
        if (Strs.isNotEmpty(numStr)) {
            long length = numStr.length();
            ArrayList<String> numArray = new ArrayList<>();
            for (int i = 0; i < (length / needNum); i++) {
                String str = numStr.substring(i * needNum, i * needNum + needNum);//[numStr substringWithRange:NSMakeRange(i * needNum, needNum)];
                numArray.add(str);
            }
            for (int i = 0; i < (numArray.size() - 1); i++) {
                String str1 = numArray.get(i);
                for (int j = (i + 1); j < numArray.size(); j++) {
                    String str2 = numArray.get(j);
                    if (!str1.equals(str2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * 剔除豹子 双数
     */
    public static boolean isAllSameNum2(String numStr)
    {
        int needNum = 2;
        if (Strs.isNotEmpty(numStr)) {
            int length = numStr.length();
            ArrayList<String> numArray = new ArrayList<>();
            for (int i = 0; i < (length / needNum); i++) {
                String str = numStr.substring(i * needNum, i * needNum + needNum);
                numArray.add(str);
            }
            for (int i = 0; i < (numArray.size() - 1); i++) {
                String str1 = numArray.get(i);
                for (int j = (i + 1); j < numArray.size(); j++) {
                    String str2 = numArray.get(j);
                    if (str1.equals(str2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * 剔除混合组选  010 010相等
     */
    public static String isZuXuanNum(String numStr, boolean isDouble)
    {
        if (numStr.indexOf(",") != -1) {
            StringBuilder reStr = new StringBuilder();
            ArrayList<String> reArray = new ArrayList<String>();
            String[] list = numStr.split(",");
            for (int i = 0; i < list.length; i++) {
                String str = list[i];
                String reSet = reZuXuanListStr(str, isDouble);
                reArray.add(reSet);
            }
            if (list.length != reArray.size()) {
                AbDebug.log(AbDebug.TAG_APP, String.format("错误"));
                return "";
            }
            
            for (int i = 0; i < (list.length - 1); i++) {
                String str = reArray.get(i);
                boolean isSame = false;
                for (int j = (i + 1); j < list.length; j++) {
                    String str2 = reArray.get(j);
                    if (str.equals(str2)) {
                        isSame = true;
                        break;
                    }
                }
                if ((!isSame)) {
                    reStr.append(str);
                    reStr.append(",");
                }
            }
            String lastStr = reArray.get(reArray.size() - 1);
            reStr.append(lastStr);
            return reStr.toString();
            
        } else {
            String reSet = reZuXuanListStr(numStr, isDouble);
            return reSet;
        }
    }
    
    public static String reZuXuanListStr(String str, boolean isDouble)
    {
        String reStr = str;
        int needNum = 1;
        if (isDouble) {
            //        reStr = [reStr stringByReplacingOccurrencesOfString:@"0" withString:@"2"];
            needNum = 2;
        }
        int length = reStr.length();
        
        ArrayList<String> numArray = new ArrayList<>();
        for (int i = 0; i < (length / needNum); i++) {
            String str2 = reStr.substring(i * needNum, i * needNum + needNum);
            numArray.add(str2);
        }
        String[] comparatorSortedArray = new String[numArray.size()];
        for (int i = 0; i < comparatorSortedArray.length; i++) {
            comparatorSortedArray[i] = numArray.get(i);
        }
        Arrays.sort(comparatorSortedArray,
                new Comparator<String>() {
                    @Override
                    public int compare(String obj1, String obj2) {
                        int v1 = Strs.parse(obj1, 0);
                        int v2 = Strs.parse(obj2, 0);
                        return v1 - v2;
                    }
                });
        
        StringBuilder reMuStr = new StringBuilder();
        for (String str3 : comparatorSortedArray) {
            reMuStr.append(str3);
        }
        if (reMuStr.length() > 0) {
            AbDebug.log(AbDebug.TAG_APP, String.format("str3 = %s", reMuStr));
            return reMuStr.toString();
        }
        return "";
    }
    
    /**
     * 多次使用方法
     * -获得投注行的个数
     */
    
    public static int getNumFromRow(int row, String betNumStr)
    {
        if (betNumStr.indexOf(",") != -1) {
            String[] list = betNumStr.split(",");
            if (list.length < (row + 1)) {
                return CtxLottery.BET_NUM_ERROE;
            }
            String unitNum = list[row];
            if (unitNum.length() == 0) {
                return CtxLottery.BET_NUM_ERROE;
            }
            if (unitNum.indexOf(" ") != -1) {
                String[] listCount = unitNum.split(" ");
                int count = listCount.length;
                return count;
            } else {
                if (Strs.isEmpty(unitNum)) {
                    return CtxLottery.BET_NUM_ERROE;
                } else {
                    if (Strs.isEmpty(betNumStr)) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            }
            
        } else if (row == 0) {
            if (betNumStr.indexOf(" ") != -1) {
                String[] listCount = betNumStr.split(" ");
                return listCount.length;
            } else {
                if (Strs.isNotEmpty(betNumStr)) {
                    return 1;
                } else {
                    return 0;
                }
            }
            
        } else {
            return CtxLottery.BET_NUM_ERROE;
        }
    }
    
    //过滤单式
    public static String[] putInCorrectArray(String betNumStr, int lotteryID, int needNum)
    {
        ArrayList<String> betStrArray = new ArrayList<String>();
        ArrayList<String> betStrNotArray = new ArrayList<String>();
        if (betNumStr.indexOf(",") != -1 && betNumStr.length() != 1) {
            String[] listBetStr = betNumStr.split(",");
            for (int i = 0; i < listBetStr.length; i++) {
                String betStr1 = listBetStr[i];
                if ((betStr1.length() == needNum) && AbStrUtil.isNumber(
                        betStr1)) {
                    betStrArray.add(betStr1);
                } else {
                    betStrNotArray.add(betStr1);
                }
            }
            StringBuilder muStr1 = new StringBuilder();
            StringBuilder muStr2 = new StringBuilder();
            for (int j = 0; j < betStrArray.size(); j++) {
                String str = betStrArray.get(j);
                muStr1.append(str);
                muStr1.append(",");
            }
            for (int k = 0; k < betStrNotArray.size(); k++) {
                String str = betStrNotArray.get(k);
                muStr2.append(str);
                muStr2.append(",");
            }
            if (muStr1.lastIndexOf(",") == muStr1.length() - 1) {
                String sub = muStr1.substring(0, muStr1.length() - 1);
                muStr1 = new StringBuilder(sub);
            }
            String[] arrayBet = new String[]{muStr1.toString(), muStr2.toString()};
            return arrayBet;
            
        } else {
            String[] arrayBet = null;
            if((betNumStr.length() == needNum) && AbStrUtil.isNumber(betNumStr)){
                arrayBet = new String[]{betNumStr, ""};
            }else{
                arrayBet = new String[]{"", betNumStr};
            }
            return arrayBet;
        }
        
    }
    
   
    //通过id活动玩法类型
    public static String getBetTypeNameFromID(int gameId)
    {
        String betName = "SSC";
        switch (gameId) {
            case 11:
            case 50:
            case 61:
            case 151:
            case 119:
            case 161:
            case 191:
            case 51:
            case 601:
            case 811:
            case 911:
            case 46:
            case 200:
            case 201:
            case 202:
            case 203:
            case 6:
            case 205:
            case 206:
            case 60:
            case 80:  //经典重庆时时彩
        case 85:  //经典菲律宾1.5分彩
        case 86:  //经典菲律宾2分彩
        case 87:  //经典菲律宾5分彩
            case 711: {
                betName = "SSC";
                break;
            }
            
            case 24:
            case 25:
            case 21:
            case 23:
            case 22:
            case 26:
            case 28:
            case 100:  //经典广东11选5
            {
                betName = "11X5";
                break;
            }
            case 31:
            case 32:
            case 33:
            case 35:
            case 36:
            case 90:  //经典江苏快3
            {
                betName = "K3";
                break;
            }
            case 42:
            {
                betName = "pl3";
                break;
            }
            
            case 41:
            {
                betName = "3DFC";
                break;
            }
            case 43:
            case 47:
            case 204:
            case 110:  //经典北京PK10
            {
                betName = "PK10";
                break;
            }
            default:
                break;
        }
        
        return betName;
    }
    
    
}

