package com.desheng.base.algorithm.pk10;//
//  DSPK10LottrtyNumAlg.m
//  DSBet
//
//  Created by Selena on 2018/1/22.
//  Copyright © 2018年 Bill. All rights reserved.
//

import com.ab.debug.AbDebug;
import com.ab.util.ArraysAndLists;
import com.ab.util.Strs;
import com.desheng.base.algorithm.DSSSCAlgorithmObject;
import com.desheng.base.context.CtxLottery;

import java.util.ArrayList;

public class DSPK10LottrtyNumAlg extends DSSSCAlgorithmObject {
    
    //获取注数
    public static long getBetNoteFromBetNum(String betNumStr, int lotteryID)
    
    {
        long betNum = 0;
        if (betNumStr.length() == 0) {
            return CtxLottery.BET_NUM_ERROE;
        }
        switch (lotteryID) {
            case 5001: {
                long num = getNumFromRow(0, betNumStr);
                if (num == 0) {
                    return CtxLottery.BET_NUM_ERROE;
                } else {
                    return num;
                }
            }
            case 5002:  //前二复式
            case 5004:  //前三复式
            case 5006:  //前四复式
            case 5008:  //前五复式
            {
                betNum = getBetNotRepeatNumFromStr(betNumStr);
                return betNum;
            }
            case 5010:  //定位胆第1~5名
            case 5011:  //定位胆第6~10名
            {
                betNumStr = betNumStr.replace("-", "");
                betNumStr = betNumStr.replace(",", "");
                betNumStr = betNumStr.replace(" ", "");
                long num = betNumStr.length();
                if (num == 0) {
                    return CtxLottery.BET_NUM_ERROE;
                } else {
                    return (num / 2);
                }
            }
            case 5012:
            case 5013:
            case 5014:
            case 5015:
            case 5016:
            case 5017:
            case 5018:
            case 5019:
            case 5020:
            case 5021:
            case 5022: {
                String betStr = betNumStr;
                betStr = betStr.replace(",", "");
                betStr = betStr.replace(" ", "");
                betNum = betStr.length();
                break;
            }
            
            
            default:
                break;
        }
        
        
        AbDebug.log(AbDebug.TAG_APP, String.format("注数为:%d", (long) betNum));
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
    
    //自动过滤正确的投注
    public static String getPKCorrectStrFromStr(String numStr, int lotteryID)
    
    {
        String betNumStr = numStr;
        if (betNumStr.contains(",")) {
            StringBuffer reStr = new StringBuffer();
            String[] list = betNumStr.split(",");
            for (int i = 0; i < (list.length - 1); i++) {
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
                if ((!isSame) && (isPassPaozi(
                        str, lotteryID))) {
                    reStr.append(str);
                    reStr.append(",");
                }
            }
            String lastStr = ArraysAndLists.lastObject(list);
            
            if ((!isBeyond(lastStr)) && (isPassPaozi(
                    lastStr, lotteryID))) {
                reStr.append(lastStr);
                betNumStr = reStr.toString();
            } else {
                betNumStr = reStr.toString();
            }
            AbDebug.log(AbDebug.TAG_APP, String.format("betNumStr过滤前%s", betNumStr));
            betNumStr = isPassZuXuan(betNumStr, lotteryID);
        } else {
            if (isBeyond(betNumStr) || !isPassPaozi(betNumStr,lotteryID)) {
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
                    long num = Strs.parse(str, -1);
                    if ((num <= 0) || (num >= 11)) {
                        isBeyond = true;
                        break;
                    }
                }
            }
        }
        return isBeyond;
    }
    
    
    public static boolean isPassPaozi(String betStr, long playId)
    
    {
        boolean isAllSame = isAllSameNum2(betStr);
        if (isAllSame) {
            return false;
        }
        return true;
    }
    
    public static String isPassZuXuan(String betStr, long playId)
    
    {
        return betStr;
    }
    
    
    /**
     * -工具//不重复组数选择
     * *
     */
    public static long getBetNotRepeatNumFromStr(String numStr)
    
    {
        long betNum = 0;
        if (!numStr.contains("-")) {
            if (numStr.contains(",")) {
                String[] list = numStr.split(",");
                ArrayList<String[]> arrayList = new ArrayList<String[]>();
                for (String str1 : list) {
                    
                    if (str1.contains(" ")) {
                        String[] array = str1.split(" ");
                        arrayList.add(array);
                    } else {
                        String [] array = new String [1];
                        array[0] = str1;
                        arrayList.add(array);
                    }
                }
                
                int i = 0;
                String[] array1 = arrayList.get(0);
                String[] array2 = arrayList.get(++i);
                
                String[] array3 = null;
                String[] array4 = null;
                String[] array5 = null;
                if (i < (arrayList.size() - 1)) {
                    array3 = arrayList.get(++i);
                }
                if (i < (arrayList.size() - 1)) {
                    array4 = arrayList.get(++i);
                }
                if (i < (arrayList.size() - 1)) {
                    array5 = arrayList.get(++i);
                }
                //++++++++++++++++++++
                for (int j = 0; j < array1.length; j++) {
                    String str1 = array1[j];
                    for (int a = 0; a < array2.length; a++) {
                        String str2 = array2[a];
                        if (!str1.equals(str2)) {
                            if (array3 != null && array3.length > 0) {
                                for (int b = 0; b < array3.length;b++) {
                                    String str3 = array3[b];
                                    if (!str1.equals(str3) && !str2.equals(str3)) {
                                        if (array4 != null && array4.length > 0) {
                                            for (int c = 0; c < array4.length;
                                                 c++) {
                                                String str4 = array4[c];
                                                if (!str1.equals(str4) && !str2.equals(str4) && !str3.equals(str4)) {
                                                    if (array5 != null && array5.length > 0) {
                                                        for (int d = 0; d < array5.length;
                                                             d++) {
                                                            String str5 = array5[d];
                                                            if (!str1.equals(str5) && !str2.equals(str5) && !str3.equals(str5) && !str4.equals(str5)) {
                                                                betNum++;
                                                            }
                                                        }
                                                    } else {
                                                        betNum++;
                                                    }
                                                }
                                            }
                                            
                                        } else {
                                            betNum++;
                                        }
                                        
                                    }
                                }
                                
                            } else {
                                betNum++;
                            }
                        }
                        
                    }
                }
                
                //++++++++++++++++++++
                return betNum;
                
            } else {
                return 0;
            }
            
        } else {
            return 0;
        }
    
    }
    
}
