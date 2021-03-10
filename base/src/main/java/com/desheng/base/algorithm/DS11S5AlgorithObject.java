package com.desheng.base.algorithm;//
//  DS11S5AlgorithObject.m
//  DSBet
//
//  Created by Selena on 2018/1/19.
//  Copyright © 2018年 Bill. All rights reserved.
//

import com.ab.debug.AbDebug;
import com.ab.util.ArraysAndLists;
import com.ab.util.Strs;
import com.desheng.base.context.CtxLottery;

import java.util.ArrayList;


public class DS11S5AlgorithObject extends DSAlgorithmObject{

//输入
public static long getBetNumPutIn(String betNumStr, int needNum)
    
    {
        long betCount = 0;
        if (betNumStr.contains(","))
        {
            String [] list = betNumStr.split(",");
            for (String  str : list){
            if (str.length() != needNum) {
                return CtxLottery.BET_NUM_ERROE;
            }
            //            else if(isSameNum:str]){
            //                return CtxLottery.BET_NUM_ERROE;
            //                break;
            //            }
        }
            return list.length;
            
        }else{
        if (betNumStr.length() != needNum) {
            return CtxLottery.BET_NUM_ERROE;
        } else {
            return 1;
            //            if(isSameNum(betNumStr]){
            //                return CtxLottery.BET_NUM_ERROE;
            //            }else{
            //                return 1;
            //            }
            
        }
    }
    }
    
public static boolean isSameNum(String numStr)
    
    {
        if (numStr != null) {
            if (numStr.length() % 2 != 0) {
                return true;
            } else {
                long length = numStr.length();
                ArrayList<String> numArray = new ArrayList<>();
                for (int i = 0; i < (length / 2); i++) {
                    String  str = numStr.substring(i * 2, i * 2+ 2);
                    numArray.add(str);
                }
                for (int i = 0; i < (numArray.size() - 1); i++) {
                    String  str1 = numArray.get(i);
                    for (int j = (i + 1); j < numArray.size(); j++) {
                        String  str2 = numArray.get(j);
                        if (str1.equals(str2)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

/**
 * 11选5
 */
//胆码
public static long getBetNumStrDanTuo(String betNumStr, int fristNum, int selectNum)
    {
        long betNum = 0;
        int num = getNumFromRow(1, betNumStr);
        
        betNum = getBetNumCTotalNum(num ,(selectNum - fristNum));
        
        
        AbDebug.log(AbDebug.TAG_APP, String.format( "注数为:%d", (long) betNum));
        return betNum;
    }


//直选复式
public static long getBetNumStrZhixuan(String betNumStr, long needNum)
    
    {
        if (betNumStr.contains("-"))
        {
            return CtxLottery.BET_NUM_ERROE;
        }
        long betCount = 0;
        if (betNumStr.contains(","))
        {
            String [] list = betNumStr.split(",");
            if (list.length != needNum) {
                return CtxLottery.BET_NUM_ERROE;
            }
            ArrayList<String> fristArray = new ArrayList<>();
            ArrayList<String> secArray = new ArrayList<>();
            ArrayList<String> thrArray = new ArrayList<>();
            for (int i = 0; i < list.length; i++) {
                String  betStr = list[i];
                if (betStr.contains(" "))
                {
                    String [] arrayList = betStr.split(" ");
                    switch (i) {
                        case 0: {
                        fristArray.addAll(ArraysAndLists.asList(arrayList));
                            break;
                        }
                        case 1: {
                        secArray.addAll(ArraysAndLists.asList(arrayList));
                            break;
                        }
                        case 2: {
                        thrArray.addAll(ArraysAndLists.asList(arrayList));
                            break;
                        }
                        
                        default:
                            break;
                    }
                }else{
                    switch (i) {
                        case 0: {
                        fristArray.add(betStr);
                            break;
                        }
                        case 1: {
                        secArray.add(betStr);
                            break;
                        }
                        case 2: {
                        thrArray.add(betStr);
                            break;
                        }
                        
                        default:
                            break;
                    }
                }
                
            }
            //筛选
            if (needNum == 3) {
                for (String  str1 : fristArray){
                    for (String  str2 : secArray){
                        for (String  str3 : thrArray){
                            if (!str1.equals(str2)){
                                if (!str2.equals(str3)){
                                    if (!str1.equals(str3)){
                                        betCount++;
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (needNum == 2) {
                for (String  str1 : fristArray){
                    for (String  str2 : secArray){
                        if (!str1.equals(str2)){
                            betCount++;
                        }
                    }
                }
            }
            //筛选结束
            
        }else{
        return CtxLottery.BET_NUM_ERROE;
    }
        
        return betCount;
    }


//输入
public static long get11Select5BetNumPutIn(String betNumStr, int needNum)
    
    {
        long betCount = 0;
        if (betNumStr.contains(","))
        {
            String [] list = betNumStr.split(",");
            for (String  str : list){
            if (str.length() != needNum) {
                return CtxLottery.BET_NUM_ERROE;
            }
            return list.length;
        }
        
        }else{
        if (betNumStr.length() != 2 * needNum) {
            return CtxLottery.BET_NUM_ERROE;
        } else {
            return 1;
        }
    }
        return betCount;
    }


//组六
public static long getZuLiuBetNum(String betNumStr)
    
    {
        long betCount = 0;
        if (betNumStr.contains(" "))
        {
            String [] list = betNumStr .split(" ");
            if (list.length < 3) {
                return CtxLottery.BET_NUM_ERROE;
            }
            betCount = getBetNumCTotalNum(list.length, 3);
            
        }else{
        return CtxLottery.BET_NUM_ERROE;
    }
        
        return betCount;
    }
//组三
public static long getZuSanBetNum(String betNumStr)
    
    {
        long betCount = 0;
        if (betNumStr.contains(" "))
        {
            String [] list = betNumStr .split(" ");
            betCount = getZuLineNumStr(list.length);
            
        }else{
        return CtxLottery.BET_NUM_ERROE;
    }
        
        return betCount;
    }

//跨度
public static long getNumCrossValue(String betNumStr, boolean isThreeNum)
    
    {
        long betCount = 0;
        if (betNumStr.contains(" "))
        {
            String [] list = betNumStr .split(" ");
            long sumCount = 0;
            for (int i = 0; i < list.length; i++) {
                String  numStr = list[i];
                long num = Strs.parse(numStr, -1);
                if (isThreeNum) {
                    betCount = getCrossValueStr(num);
                } else {
                    betCount = getCrossValueStr2(num);
                }
                sumCount = sumCount + betCount;
            }
            betCount = sumCount;
            
        }else{
        long num = Strs.parse(betNumStr, -1);
        if (isThreeNum) {
            betCount = getCrossValueStr(num);
        } else {
            betCount = getCrossValueStr2(num);
        }
        return betCount;
    }
        
        return betCount;
    }

//3和值
public static long getNumAndValues(String betNumStr, boolean isSort)
    
    {
        long betCount = 0;
        if (betNumStr.contains(" "))
        {
            String [] list = betNumStr .split(" ");
            long sumCount = 0;
            for (int i = 0; i < list.length; i++) {
                String  numStr = list[i];
                long num = Strs.parse(numStr, -1);
                if (isSort) {
                    betCount = getAndNumStr(num);
                } else {
                    betCount = getAndNumStr2(num);
                }
                sumCount = sumCount + betCount;
            }
            betCount = sumCount;
            
        }else{
        long num = Strs.parse(betNumStr, -1);
        if (isSort) {
            betCount = getAndNumStr(num);
        } else {
            betCount = getAndNumStr2(num);
        }
        
        return betCount;
    }
        
        return betCount;
    }


public static long get2NumAndValues(String betNumStr, boolean isSort)
    
    {
        long betCount = 0;
        if (betNumStr.contains(" "))
        {
            String [] list = betNumStr .split(" ");
            long sumCount = 0;
            for (int i = 0; i < list.length; i++) {
                String  numStr = list[i];
                long num = Strs.parse(numStr ,-1);
                if (isSort) {
                    betCount = get2AndNumStr(num);
                } else {
                    betCount = get2AndNumStr2(num);
                }
                sumCount = sumCount + betCount;
            }
            betCount = sumCount;
            
        }else{
        long num = Strs.parse(betNumStr ,-1);
        if (isSort) {
            betCount = get2AndNumStr(num);
        } else {
            betCount = get2AndNumStr2(num);
        }
        
        return betCount;
    }
        
        return betCount;
    }

//二重号位  选60
public static long getNumErChong(String betNumStr)
    
    {
        long betCount = 0;
        if (betNumStr.contains(","))
        {
            String [] list = betNumStr.split(",");
            String  fristStr = list[0];
            String  secondStr = list[1];
            if ((fristStr.length() == 0) && (secondStr.length() == 0)) {
                return CtxLottery.BET_NUM_ERROE;
            }
    
            int fristCount;
            int secondCount;
            ArrayList<String> fristArray = new ArrayList<>();
            ArrayList<String> secondArray = new ArrayList<>();
            
            if (fristStr.contains(" "))
            {
                String [] listFrist = fristStr .split(" ");
                fristCount = listFrist.length;
            fristArray.addAll(ArraysAndLists.asList(listFrist));
            
            }else{
            fristCount = 1;
            fristArray.add(fristStr);
        }
            if (secondStr.contains(" "))
            {
                String [] listSecond = secondStr .split(" ");
                secondCount = listSecond.length;
                if (secondCount < 3) {
                    return CtxLottery.BET_NUM_ERROE;
                }
            secondArray.addAll(ArraysAndLists.asList(listSecond));
            }else{
            return CtxLottery.BET_NUM_ERROE;
        }
            long sameCount = 0;
            for (int i = 0; i < fristCount; i++) {
                String  str1 = fristArray.get(i);
                for (int j = 0; j < secondCount; j++) {
                    String  str2 = secondArray.get(j);
                    if (str1.equals(str2)){
                        sameCount++;
                    }
                }
            }
            if (sameCount > 0) {
                long moreCount = fristCount - sameCount;
                if (moreCount > 0) {
                    betCount = betCount + moreCount * getBetNumCTotalNum(secondCount,3);
                    if ((secondCount - 1) >= 3) {
                        betCount = betCount + sameCount * getBetNumCTotalNum(secondCount - 1,3);
                    }
                    
                    
                } else {
                    if ((secondCount - 1) >= 3) {
                        betCount = betCount + sameCount * getBetNumCTotalNum(secondCount - 1, 3);
                    }
                }
                
            } else {
                betCount = betCount + fristCount * getBetNumCTotalNum(secondCount,3);
                
            }
            
        }else{
        return CtxLottery.BET_NUM_ERROE;
    }
        if (betCount == 0) {
            return CtxLottery.BET_NUM_ERROE;
        }
        
        return betCount;
    }
/**
 * 二重号位 选30
 */
    
public static long getNum30ErChong(String betNumStr)
    
    {
        long betCount = 0;
        if (betNumStr.contains(","))
        {
            String [] list = betNumStr.split(",");
            String  fristStr = list[0];
            String  secondStr = list[1];
            if ((fristStr.length() == 0) && (secondStr.length() == 0)) {
                return CtxLottery.BET_NUM_ERROE;
            }
            
            int fristCount;
            int secondCount;
            ArrayList<String> fristArray = new ArrayList<>();
            ArrayList<String> secondArray = new ArrayList<>();
            
            if (fristStr.contains(" "))
            {
                String [] listFrist = fristStr .split(" ");
                fristCount = listFrist.length;
                if (fristCount < 2) {
                    return CtxLottery.BET_NUM_ERROE;
                }
            fristArray.addAll(ArraysAndLists.asList(listFrist));
            
            }else{
            return CtxLottery.BET_NUM_ERROE;
        }
            if (secondStr.contains(" "))
            {
                String [] listSecond = secondStr .split(" ");
                secondCount = listSecond.length;
            secondArray.addAll(ArraysAndLists.asList(listSecond));
            }else{
            secondCount = 1;
            secondArray.add(secondStr);
        }
            
            //比较相同的
            int sameCount = 0;
            for (int i = 0; i < fristCount; i++) {
                String  str1 = fristArray.get(i);
                for (int j = 0; j < secondCount; j++) {
                    String  str2 = secondArray.get(j);
                    if (str1.equals(str2)){
                        sameCount++;
                    }
                }
            }
            if (sameCount > 0) {
                int moreCount = fristCount - sameCount;
                if (sameCount == 1) {
                    if (moreCount > 1) {
                        betCount = betCount + secondCount * getBetNumCTotalNum(moreCount,2);
                        if (secondCount > 1) {
                            betCount = betCount + (secondCount - 1) * moreCount;
                        }
                        
                    }
                    
                } else {
                    betCount = betCount + secondCount * getBetNumCTotalNum(moreCount,2);
                    betCount = betCount + (secondCount - 2) * getBetNumCTotalNum(sameCount,2);
                    if (moreCount == 1) {
                        betCount = betCount + (sameCount - 1) * (secondCount - 1);
                    } else {
                        betCount = betCount + (sameCount) * (moreCount) * (secondCount - 1);
                    }
                }
                
            } else {
                betCount = betCount + secondCount * getBetNumCTotalNum(fristCount,2);
                
            }
            
        }else{
        return CtxLottery.BET_NUM_ERROE;
    }
        if (betCount == 0) {
            return CtxLottery.BET_NUM_ERROE;
        }
        
        return betCount;
    }

//二重号位  选20
public static long getNum20ErChong(String betNumStr)
    
    {
        long betCount = 0;
        if (betNumStr.contains(","))
        {
            String [] list = betNumStr.split(",");
            String  fristStr = list[0];
            String  secondStr = list[1];
            if ((fristStr.length() == 0) && (secondStr.length() == 0)) {
                return CtxLottery.BET_NUM_ERROE;
            }
            
            int fristCount;
            int secondCount;
            ArrayList<String> fristArray = new ArrayList<>();
            ArrayList<String> secondArray = new ArrayList<>();
            
            if (fristStr.contains(" "))
            {
                String [] listFrist = fristStr .split(" ");
                fristCount = listFrist.length;
            fristArray.addAll(ArraysAndLists.asList(listFrist));
            
            }else{
            fristCount = 1;
            fristArray.add(fristStr);
        }
            if (secondStr.contains(" "))
            {
                String [] listSecond = secondStr .split(" ");
                secondCount = listSecond.length;
                if (secondCount < 2) {
                    return CtxLottery.BET_NUM_ERROE;
                }
            secondArray.addAll(ArraysAndLists.asList(listSecond));
            }else{
            return CtxLottery.BET_NUM_ERROE;
        }
            long sameCount = 0;
            for (int i = 0; i < fristCount; i++) {
                String  str1 = fristArray.get(i);
                for (int j = 0; j < secondCount; j++) {
                    String  str2 = secondArray.get(j);
                    if (str1.equals(str2)){
                        sameCount++;
                    }
                }
            }
            if (sameCount > 0) {
                long moreCount = fristCount - sameCount;
                if (moreCount > 0) {
                    betCount = betCount + moreCount * getBetNumCTotalNum(secondCount ,2)
                    ;
                    if ((secondCount - 1) >= 2) {
                        betCount = betCount + sameCount * getBetNumCTotalNum(secondCount - 1, 2);
                    }
                    
                    
                } else {
                    if ((secondCount - 1) >= 2) {
                        betCount = betCount + sameCount * getBetNumCTotalNum(secondCount - 1, 2);
                    }
                }
                
            } else {
                betCount = betCount + fristCount * getBetNumCTotalNum(secondCount,2);
                
            }
            
        }else{
        return CtxLottery.BET_NUM_ERROE;
    }
        if (betCount == 0) {
            return CtxLottery.BET_NUM_ERROE;
        }
        
        return betCount;
    }
//二重号位  选10
public static long getNum10BetNumStr(String betNumStr)
    
    {
        long betCount = 0;
        if (betNumStr.contains(","))
        {
            String [] list = betNumStr.split(",");
            String  fristStr = list[0];
            String  secondStr = list[1];
            if ((fristStr.length() == 0) && (secondStr.length() == 0)) {
                return CtxLottery.BET_NUM_ERROE;
            }
            
            long fristCount;
            long secondCount;
            ArrayList<String> fristArray = new ArrayList<>();
            ArrayList<String> secondArray = new ArrayList<>();
            
            if (fristStr.contains(" "))
            {
                String [] listFrist = fristStr .split(" ");
                fristCount = listFrist.length;
            fristArray.addAll(ArraysAndLists.asList(listFrist));
            
            }else{
            fristCount = 1;
            fristArray.add(fristStr);
        }
            if (secondStr.contains(" "))
            {
                String [] listSecond = secondStr .split(" ");
                secondCount = listSecond.length;
            secondArray.addAll(ArraysAndLists.asList(listSecond));
            }else{
            secondCount = 1;
            secondArray.add(secondStr);
        }
            
            //比较相同的
            long sameCount = 0;
            for (int i = 0; i < fristCount; i++) {
                String  str1 = fristArray.get(i);
                for (int j = 0; j < secondCount; j++) {
                    String  str2 = secondArray.get(j);
                    if (str1.equals(str2)){
                        sameCount++;
                    }
                }
            }
            if (sameCount > 0) {
                long moreCount = fristCount - sameCount;
                betCount = betCount + moreCount * secondCount;
                betCount = betCount + sameCount * (secondCount - 1);
                
            } else {
                betCount = betCount + fristCount * secondCount;
            }
            
        }else{
        return CtxLottery.BET_NUM_ERROE;
    }
        if (betCount == 0) {
            return CtxLottery.BET_NUM_ERROE;
        }
        
        return betCount;
    }
}
