package com.desheng.base.algorithm.d11s5;//
//  DS11S5LotteryNumAlg.m
//  DSBet
//
//  Created by Selena on 2018/1/19.
//  Copyright © 2018年 Bill. All rights reserved.
//

import com.ab.debug.AbDebug;
import com.ab.util.ArraysAndLists;
import com.ab.util.Strs;
import com.desheng.base.algorithm.DS11S5AlgorithObject;
import com.desheng.base.context.CtxLottery;

public class DS11S5LotteryNumAlg extends DS11S5AlgorithObject {
    
    /**
     * 获取注数
     */
    public static long getBetNoteFromBetNum(String
                                                    betNumStr, int lotteryID)
    
    {
        long betNum = 0;
        if (betNumStr.length() == 0) {
            return CtxLottery.BET_NUM_ERROE;
        }
        
        switch (lotteryID) {
            case 2001: //前三直选复式
            {
                betNum = getBetNumStrZhixuan(betNumStr, 3);
                break;
                
            }
            case 2003: //前三组选复式      //C(n,3)
            case 2013:  //三中三
            {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 3);
                
                break;
                
            }
            case 2005:  //前二直选复式
            {
                betNum = getBetNumStrZhixuan(betNumStr, 2);
                break;
            }
            case 2007:  //前二组选复式   //C(n,2)
            case 2012:  //复式二中二
            {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 2);
                break;
            }
            case 2009:  //不位胆
            case 2011:  //复式一中一
            {
                int num = getNumFromRow(0, betNumStr);
                betNum = num;
                break;
            }
            case 2010:  //定位胆
            {
                String replacedStr = betNumStr.replace(",", " ");
                String[] list = replacedStr.split(" ");
                long count = 0;
                for (int i = 0; i < list.length; i++) {
                    String str = list[i];
                    if (!str.equals("-")) {
                        count++;
                    }
                }
                betNum = count;
                break;
            }
            case 2014: {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 4);
                break;
            }
            case 2015: {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 5);
                break;
            }
            case 2016: {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 6);
                break;
            }
            case 2017: {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 7);
                break;
            }
            case 2018: {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 8);
                break;
            }
            
            default:
                break;
        }
        
        return betNum;
    }
    
    //获取注数  输入
    public static long getPutInBetNoteFromBetNum(String betNumStr, int lotteryID, int needNum)
    
    {
        long betNum = 0;
        if (betNumStr.length() == 0) {
            return CtxLottery.BET_NUM_ERROE;
        }
        
        betNum = getBetNumPutIn(betNumStr, needNum);
        AbDebug.log(AbDebug.TAG_APP, String.format("注数为:%d", (long) betNum));
        return betNum;
    }
    
    //获取正确的投注  最后的处理  输入
    public static String getPutCorrectBetNumStr(String betNumStr, int lotteryID, int ticketID, int needNum)
    
    {
        String betStr = betNumStr;
        
        betStr = betStr.replace(",", "");
        betStr = betStr.replace(";", "");
        betStr = betStr.replace(" ", "");
        StringBuffer muStr = new StringBuffer();
        if (betStr.length() > 0) {
            if (betStr.length() % 2 != 0) {
                return betStr;
            } else {
                long length = betStr.length();
                for (int i = 0; i < (length / 2); i++) {
                    String str = betStr.substring(i * 2, i * 2 + 2);
                    muStr.append(str);
                    if (i != ((length / 2) - 1)) {
                        if (((i + 1)*2)%needNum == 0) {
                            muStr.append(";");
                        } else {
                            muStr.append(" ");
                        }
                    }
                }
                return muStr.toString();
            }
        }
        
        return betStr;
    }
    
    
    /**
     * -过滤字符串
     */
    public static String get11S5CorrectStrFromStr(String numStr, int lotteryID)
    
    {
        String betNumStr = numStr;
        if (betNumStr.contains(",")) {
            StringBuffer reStr = new StringBuffer();
            String[] list = betNumStr.split(",");
            for (int i = 0; i < list.length - 1; i++) {
                String str = list[i];
                boolean isSame = false;
                for (int j = (i + 1); j < list.length; j++) {
                    String str2 = list[j];
                    if (str.equals(str2)) {
                        isSame = true;
                        break;
                    } else if (isBeyond(str)) {
                        isSame = true;
                        break;
                    }
                }
                if ((!isSame) && (isPassPaozi(str, lotteryID))) {
                    reStr.append(str);
                    reStr.append(",");
                }
            }
            String lastStr = ArraysAndLists.lastObject(list);
            
            if (!isBeyond(lastStr) && (isPassPaozi(lastStr, lotteryID))) {
                reStr.append(lastStr);
                betNumStr = reStr.toString();
            } else {
                betNumStr = reStr.toString();
            }
            AbDebug.log(AbDebug.TAG_APP, String.format("11组选过滤前%s", betNumStr));
            betNumStr = isPassZuXuan(betNumStr, lotteryID);
        } else {
            if (isBeyond(betNumStr)) {
                return "";
            }
        }
        return betNumStr;
    }
    
    public static boolean isBeyond(String numStr)
    
    {
        boolean isBeyond = false;
        AbDebug.log(AbDebug.TAG_APP, String.format("过滤"));
        if (numStr.length() > 0) {
            if (numStr.length() % 2 != 0) {
                return true;
            } else {
                long length = numStr.length();
                for (int i = 0; i < (length / 2); i++) {
                    String str = numStr.substring(i * 2, i * 2 + 2);
                    int num = Strs.parse(str, -1);
                    if ((num <= 0) || (num >= 12)) {
                        isBeyond = true;
                        break;
                    }
                }
            }
        }
        return isBeyond;
    }
    
    /**
     * pragma mark -获取正确的投注
     */
    public static String getCorrectBetNumStr(String betNumStr, int playId)
    
    {
        String betStr = betNumStr;
        if (betNumStr.contains(",")) {
            String[] list = betNumStr.split(",");
            StringBuffer numStrNum = new StringBuffer();
            for (String str : list) {
                long length = str.length();
                if (length % 2 != 0) {
                    return null;
                }
                
                for (int i = 0; i < (length / 2); i++) {
                    String str2 = str.substring(i * 2, i * 2 + 2);
                    if (i != (length / 2 - 1)) {
                        str2 = String.format("%s", str2);
                    }
                    numStrNum.append(str2);
                }
                numStrNum.append(",");
            }
            betStr = numStrNum.substring(0, numStrNum.length() - 1);
            
        } else {
            StringBuffer numStrNum = new StringBuffer();
            for (int i = 0; i < (betNumStr.length() / 2); i++) {
                String str2 = betNumStr.substring(i * 2, i * 2 + 2);
                if (i != (betNumStr.length() / 2 - 1)) {
                    str2 = String.format("%s ", str2);
                }
                numStrNum.append(str2);
            }
            betStr = numStrNum.toString();
        }
        return betStr;
    }
    
    public static String isPassZuXuan(String betStr, int playId)
    
    {
        switch (playId) {
            //单式
            case 2002:
            case 2006:
            case 2019:
            case 2020:
            case 2021:
            case 2022:
            case 2023:
            case 2024:
            case 2025:
            case 2026:
            
            {
                return betStr;
            }
            //组选   任选
            
            default: {
                String reStr = isZuXuanNum(betStr, true);
                return reStr;
            }
        }
    }
    
    public static boolean isPassPaozi(String betStr, long playId)
    
    {
        boolean isAllSame = isAllSameNum2(betStr);
        if (isAllSame) {
            return false;
        }
        return true;
    }
    
    
}
