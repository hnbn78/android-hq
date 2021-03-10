package com.desheng.base.algorithm.kuai3;//
//  DSK3LotteryNumAlg.m
//  DSBet
//
//  Created by Selena on 2018/1/22.
//  Copyright © 2018年 Bill. All rights reserved.
//

//import "DSK3LotteryNumAlg.h"

import com.ab.debug.AbDebug;
import com.ab.util.ArraysAndLists;
import com.desheng.base.algorithm.DSSSCAlgorithmObject;
import com.desheng.base.context.CtxLottery;

public class DSK3LotteryNumAlg extends DSSSCAlgorithmObject {


//获取注数
public static long getBetNoteFromBetNum(String betNumStr , int lotteryID)
    
    {
        long betNum = 0;
        if (betNumStr.length() == 0) {
            return CtxLottery.BET_NUM_ERROE;
        }
        
        switch (lotteryID) {
            case 3001:  //二不同号标准
            {
                int num = getNumFromRow(0 , betNumStr);
                betNum = getBetNumCTotalNum(num, 2);
                break;
            }
            case 3002:  //二同号单选
            {
                int num = getNumFromRow(0 , betNumStr);
                int num2 = getNumFromRow(1 , betNumStr);
                betNum = num * num2;
                break;
            }
            case 3003:  //二同号复选
            {
                int num = getNumFromRow(0 , betNumStr);
                betNum = num;
                break;
            }
            case 3004:  //三不同号标准
            {
                int num = getNumFromRow(0 , betNumStr);
                betNum = getBetNumCTotalNum(num ,3);
                break;
            }
            case 3005:  //三同号单选
            {
                int num = getNumFromRow(0 , betNumStr);
                betNum = num;
                break;
            }
            case 3006:  //三同号通选
            case 3007: {
                betNum = 1;
                break;
            }
            case 3009:  //和值
            case 3010:
            case 3011:
            case 3008: {
                int num = getNumFromRow(0 , betNumStr);
                betNum = num;
                break;
            }
            
            
            default:
                break;
        }
        
        
        AbDebug.log(AbDebug.TAG_APP, String.format("注数为:%d", (long) betNum));
        return betNum;
    }


//获取注数  输入
public static long getPutInBetNoteFromBetNum(String betNumStr , int lotteryID , int needNum)
    
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
public static String getSSCCorrectStrFromStr(String numStr , int lotteryID)
    
    {
        String  betNumStr = numStr;
        if (betNumStr .contains(","))
        {
            StringBuffer reStr = new StringBuffer();
            String [] list = betNumStr .split(",");
            for (int i = 0; i < (list.length - 1); i++) {
                String  str = list[i];
                boolean isSame = false;
                for (int j = (i + 1); j < list.length; j++) {
                    String  str2 = list[j];
                    if (str.equals(str2)){
                        isSame = true;
                        break;
                    }
                }
                if ((!isSame) && (isPassPaozi(str,lotteryID)))
                {
                reStr.append(str);
                reStr.append(",");
                }
            }
            String  lastStr = ArraysAndLists.lastObject(list);
            if (isPassPaozi(lastStr ,lotteryID)){
            reStr.append(lastStr);
        }
            AbDebug.log(AbDebug.TAG_APP, String.format("组选过滤前%s", reStr));
            String  reStr2 = isPassZuXuan(reStr.toString() ,lotteryID);
            return reStr2;
        }else{
        if (!isPassPaozi(betNumStr ,lotteryID)){
            return "";
        }
        betNumStr = isPassZuXuan(betNumStr ,lotteryID);
    }
        return betNumStr;
    }


/** *
 * -工具 过滤字符串
 *//*
public static String getSSCCorrectStrFromStr(String numStr ,int playId)
    
    {
        String  betNumStr = numStr;
        if (betNumStr .contains(","))
        {
            StringBuffer reStr = new StringBuffer();
            String [] list = betNumStr .split(",");
            for (int i = 0; i < (list.length - 1); i++) {
                String  str = list[i];
                boolean isSame = false;
                for (int j = (i + 1); j < list.length; j++) {
                    String  str2 = list[j];
                    if (str.equals(str2)){
                        isSame = true;
                        break;
                    }
                }
                if ((!isSame) && (isPassPaozi(
                str ,playId)))
                {
                reStr.append(str);
                reStr.append( ",");
                }
            }
            String  lastStr = ArraysAndLists.lastObject(list);
            if (isPassPaozi(lastStr, playId)){
            reStr.append(lastStr);
        }
            AbDebug.log(AbDebug.TAG_APP, String.format("组选过滤前%@", reStr));
            String  reStr2 = isPassZuXuan(reStr.toString() ,playId);
            return reStr2;
        }else{
        if (!isPassPaozi(betNumStr, playId)){
            return "";
        }
        betNumStr = isPassZuXuan(betNumStr, playId);
    }
        return betNumStr;
    }*/

public static boolean isPassPaozi(String betStr, int playId)
{
        switch (playId) {
            //单式
            case 1002: //五星直选单式
            case 1012:  //后四直选单式
            case 1019:  //前三直选单式
            case 1030:  //中三直选单式
            case 1040:  //后三直选单式
            case 1051:  //后二直选单式
            case 1060:  //前二直选单式
            case 1081:  //任二单式
            case 1086:  //任三单式
            case 1093:  //任四单式
            {
                return true;
            }
            //组选   任选
            
            default: {
                boolean isAllSame = isAllSameNum(betStr);
                if (isAllSame) {
                    return false;
                }
                break;
            }
        }
        return true;
    }

public static String isPassZuXuan(String betStr, int playId)
    
    {
        switch (playId) {
            //单式
            case 1002: //五星直选单式
            case 1012:  //后四直选单式
            case 1019:  //前三直选单式
            case 1030:  //中三直选单式
            case 1040:  //后三直选单式
            case 1051:  //后二直选单式
            case 1060:  //前二直选单式
            case 1086:  //任三单式
            case 1093:  //任四单式
            {
                return betStr;
            }
            //组选   任选
            
            default: {
                String  reStr = isZuXuanNum(betStr , false);
                return reStr;
            }
        }
    }
    
}
