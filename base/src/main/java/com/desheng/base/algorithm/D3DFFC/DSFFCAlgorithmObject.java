package com.desheng.base.algorithm.D3DFFC;//
//  DSFFCAlgorithmObject.m
//  DSBet
//
//  Created by Selena on 2018/1/22.
//  Copyright © 2018年 Bill. All rights reserved.
//

import com.ab.debug.AbDebug;
import com.ab.util.ArraysAndLists;
import com.desheng.base.algorithm.DSSSCAlgorithmObject;
import com.desheng.base.context.CtxLottery;

public class DSFFCAlgorithmObject extends DSSSCAlgorithmObject {
    
    
    //获取注数
    public static long getBetNoteFromBetNum(String betNumStr, int lotteryID) {
        long betNum = 0;
        if (betNumStr.length() == 0) {
            return CtxLottery.BET_NUM_ERROE;
        }
        
        switch (lotteryID) {
            case 4001:    //三码直选复式
            case 4008:
            case 4010:
            case 4017:
            case 4018:
            {
                betNum = getBetNumFromBetStrModeOne(betNumStr);
                break;
            }
            case 4019://后二组选复式
            case 4012://前二组选复式
            case 4016:
            {
                int num = getNumFromRow(0,betNumStr);
                betNum = getBetNumCTotalNum(num,2);
                break;
    
            }
            case 4003:  //三码直选和值
            {
                long num = getNumFromRow(0, betNumStr);
                if (num == 0) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = getNumAndValues(betNumStr, true);
                break;
            }
            case 4004:  //三码直选和值
            {
                betNum = getZuSanBetNum(betNumStr);
                break;
            }
            case 4005:  //三码组选组六
            {
                betNum = getZuLiuBetNum(betNumStr);
                break;
            }
            case 4007:  //三码组选和值
            {
                long num = getNumFromRow(0, betNumStr);
                if (num == 0) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = getNumAndValues(betNumStr, false);
                break;
            }
            case 4014://定位胆
            case 4015://一码不定胆
            {
                String replacedStr = betNumStr.replace(" ", "");
                replacedStr = replacedStr.replace(",", "");
                replacedStr = replacedStr.replace("-", "");
                betNum = replacedStr.length();
                break;
            }
            
            
            default:
                break;
        }
        
        
        AbDebug.log(AbDebug.TAG_APP, String.format("注数为:%d", (long) betNum));
        return betNum;
    }
    
    
    //获取注数  输入
    public static long getPutInBetNoteFromBetNum(String betNumStr, int lotteryID, long needNum) {
        long betNum = 0;
        if (betNumStr.length() == 0) {
            return CtxLottery.BET_NUM_ERROE;
        }
        betNum = getBetNumPutIn(betNumStr, needNum);
        AbDebug.log(AbDebug.TAG_APP, String.format("注数为:%d", (long) betNum));
        return betNum;
    }
    
    //自动过滤正确的投注
    public static String getFFCCorrectStrFromStr(String numStr, int lotteryID)
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
                    }
                }
                if ((!isSame) && (isPassPaozi(str, lotteryID))) {
                    reStr.append(str);
                    reStr.append(",");
                }
            }
            String lastStr = ArraysAndLists.lastObject(list);
            if (isPassPaozi(lastStr, lotteryID)) {
                reStr.append(lastStr);
            }
            AbDebug.log(AbDebug.TAG_APP, String.format("组选过滤前%s", reStr));
            String reStr2 = isPassZuXuan(reStr.toString(), lotteryID);
            return reStr2;
        } else {
            if (!isPassPaozi(betNumStr, lotteryID)) {
                return "";
            }
            betNumStr = isPassZuXuan(betNumStr, lotteryID);
        }
        return betNumStr;
    }
    
    /*
    pragma mark -过滤字符串
    + (String)getSSCCorrectStrFromStr(numStr, int playId
    {
        StringbetNumStr = numStr;
        if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
        {
            StringBufferreStr = [[NSMutableString alloc] init] ;
            String []list = [betNumStr .split(",")];
            for(long i = 0; i < (list.length - 1); i ++){
                Stringstr = list[i];
                boolean isSame = false;
                for(long j = (i + 1);j < list.length;j ++){
                    Stringstr2 = list[j];
                    if([str.equals(str2]){
                        isSame = true;
                        break;
                    }
                }
                if((!isSame) && (isPassPaozi(str playId:playId]))
                {
                    [reStr.append(str];
                    [reStr.append(@","];
                }
            }
            StringlastStr = ArraysAndLists.lastObject(list);
            if(isPassPaozi(lastStr playId:playId]){
                [reStr.append(lastStr];
            }
            NSLog(@"组选过滤前%s",reStr);
            String reStr2 = isPassZuXuan:reStr playId:playId];
            return reStr2;
        }else{
            if(!isPassPaozi(betNumStr playId:playId]){
                return @"";
            }
            betNumStr = isPassZuXuan(betNumStr playId:playId];
        }
        return betNumStr;
    }
     */
    public static boolean isPassPaozi(String betStr, int playId)
    
    {
        switch (playId) {
            //单式
            case 4002: //直选单式
            case 4009:
            case 4010:
            case 4011:
            case 4013: {
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
            case 4002: //直选单式
            case 4009:
            case 4011:
            
            {
                return betStr;
            }
            //组选   任选
            
            default: {
                String reStr = isZuXuanNum(betStr, false);
                return reStr;
            }
        }
    }
}
