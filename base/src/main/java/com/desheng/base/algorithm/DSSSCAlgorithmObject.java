package com.desheng.base.algorithm;//
//  DSSSCAlgorithmObject.m
//  DSBet
//
//  Created by Selena on 2018/1/15.
//  Copyright © 2018年 Bill. All rights reserved.
//

import com.ab.util.ArraysAndLists;
import com.ab.util.Strs;
import com.desheng.base.context.CtxLottery;

import java.util.ArrayList;

public class DSSSCAlgorithmObject extends DSAlgorithmObject{

/**
 * //输入
 */
public static int getBetNumPutIn(String betNumStr , long needNum)
{
    int betCount = 0;
    if(betNumStr.indexOf(",") != -1){
        String [] list = betNumStr.split(",");
        for(String str : list){
            if(str.length() != needNum){
                return -1;
            }
        }
        return list.length;
        
    }else{
        if(betNumStr.length() != needNum){
            return CtxLottery.BET_NUM_ERROE;
        }else{
            return 1;
        }
    }
}


//组六
public static long getZuLiuBetNum(String betNumStr)
{
    long betCount = 0;
    if(betNumStr .indexOf(" ") !=-1)
    {
        String []  list = betNumStr.split(" ");
        if(list.length < 3){
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
    if(betNumStr .indexOf(" ") !=-1)
    {
        String []  list = betNumStr .split(" ");
        betCount = getZuLineNumStr(list.length);
    }else{
        return CtxLottery.BET_NUM_ERROE;
    }
    
    return betCount;
}

//跨度
public static long getNumCrossValue(String betNumStr ,boolean isThreeNum)
{
    long betCount = 0;
    if(betNumStr .indexOf(" ") !=-1)
    {
        String []  list = betNumStr .split(" ");
        long sumCount = 0;
        for(int i = 0;i < list.length; i ++){
            String numStr = list[i];
            int num = Strs.parse(numStr, -1);
            if(isThreeNum){
                betCount = getCrossValueStr(num);
            }else{
                betCount = getCrossValueStr2(num);
            }
            sumCount = sumCount + betCount;
        }
        betCount = sumCount;
        
    }else{
        int num = Strs.parse(betNumStr, -1);
        if(isThreeNum){
            betCount = getCrossValueStr(num);
        }else{
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
    if(betNumStr .indexOf(" ") !=-1)
    {
        String []  list = betNumStr .split(" ");
        long sumCount = 0;
        for(int i = 0;i < list.length; i ++){
            String numStr = list[i];
            int num = Strs.parse(numStr, -1);
            if(isSort){
                betCount =getAndNumStr(num);
            }else{
                betCount = getAndNumStr2(num);
                if(num%3==0){
                    betCount --;
                }
            }
            sumCount = sumCount + betCount;
        }
        betCount = sumCount;
        
    }else{
        int num = Strs.parse(betNumStr, -1);
        if(isSort){
            betCount = getAndNumStr(num);
        }else{
            betCount = getAndNumStr2(num);
            if(num%3==0){
                betCount --;
            }
        }
        
        return betCount;
    }
    
    return betCount;
}


public static long get2NumAndValues(String betNumStr, boolean isSort)
{
    long betCount = 0;
    if(betNumStr .indexOf(" ") !=-1)
    {
        String []  list = betNumStr .split(" ");
        long sumCount = 0;
        for(int i = 0;i < list.length; i ++){
            String numStr = list[i];
            int num = Strs.parse(numStr, -1);
            if(isSort){
                betCount = get2AndNumStr(num);
            }else{
                betCount = get2AndNumStr2(num);
            }
            sumCount = sumCount + betCount;
        }
        betCount = sumCount;
        
    }else{
        int num = Strs.parse(betNumStr, -1);
        if(isSort){
            betCount = get2AndNumStr(num);
        }else{
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
    if(betNumStr.indexOf("-") !=-1)
    {
        return CtxLottery.BET_NUM_ERROE;
    }
    if(betNumStr.indexOf(",") !=-1)
    {
        String []  list = betNumStr .split(",");
        String fristStr = list[0];
        String secondStr = list[1];
        if((fristStr.length() == 0 ) && (secondStr.length() == 0)){
            return CtxLottery.BET_NUM_ERROE;
        }
        
        int fristCount ;
        int secondCount ;
        ArrayList<String> fristArray = new ArrayList<String>() ;
        ArrayList<String> secondArray = new ArrayList<String>() ;
        
        if(fristStr .indexOf(" ") !=-1)
        {
            String []  listFrist = fristStr .split(" ");
            fristCount = listFrist.length;
            fristArray.addAll(ArraysAndLists.asList(listFrist));
        }else{
            fristCount = 1;
            fristArray.add(fristStr);
        }
        if(secondStr .indexOf(" ") !=-1)
        {
            String []  listSecond = secondStr .split(" ");
            secondCount = listSecond.length;
            if(secondCount < 3){
                return CtxLottery.BET_NUM_ERROE;
            }
            secondArray.addAll(ArraysAndLists.asList(listSecond));
        }else{
            return CtxLottery.BET_NUM_ERROE;
        }
        int sameCount = 0;
        for(int i = 0; i < fristCount; i ++){
            String str1 = fristArray.get(i);
            for(int j = 0; j < secondCount; j ++){
                String str2 = secondArray.get(j);
                if(str1.equals(str2)){
                    sameCount ++;
                }
            }
        }
        if(sameCount > 0){
            int moreCount = fristCount - sameCount;
            if(moreCount > 0){
                betCount = betCount + moreCount * getBetNumCTotalNum(secondCount,3);
                if((secondCount - 1) >= 3){
                    betCount = betCount + sameCount * getBetNumCTotalNum((secondCount - 1),3);
                }
                
                
            }else{
                if((secondCount - 1) >= 3){
                    betCount = betCount + sameCount * getBetNumCTotalNum((secondCount - 1),3);
                }
            }
            
        }else{
            betCount = betCount + fristCount * getBetNumCTotalNum(secondCount, 3);
            
        }
        
    }else{
        return CtxLottery.BET_NUM_ERROE;
    }
    if(betCount == 0){
        return CtxLottery.BET_NUM_ERROE;
    }
    
    return betCount;
}
/**
 * 二重号位  选30
 */
public static long getNum30ErChong(String betNumStr)
{
    long betCount = 0;
    if(betNumStr.indexOf("-") !=-1)
    {
        return CtxLottery.BET_NUM_ERROE;
    }
    if(betNumStr.indexOf(",") !=-1)
    {
        String []  list = betNumStr .split(",");
        if (list.length < 2) {
            return CtxLottery.BET_NUM_ERROE;
        }
        String fristStr = list[0];
        String secondStr = list[1];
        if((fristStr.length() == 0 ) && (secondStr.length() == 0)){
            return CtxLottery.BET_NUM_ERROE;
        }
        
        int fristCount ;
        int secondCount ;
        ArrayList<String> fristArray = new ArrayList<String>() ;
        ArrayList<String> secondArray = new ArrayList<String>() ;
        
        if(fristStr .indexOf(" ") !=-1)
        {
            String []  listFrist = fristStr .split(" ");
            fristCount = listFrist.length;
            if(fristCount < 2){
                return CtxLottery.BET_NUM_ERROE;
            }
            fristArray.addAll(ArraysAndLists.asList(listFrist));
            
        }else{
            return CtxLottery.BET_NUM_ERROE;
        }
        if(secondStr.indexOf(" ") !=-1)
        {
            String []  listSecond = secondStr .split(" ");
            secondCount = listSecond.length;
            secondArray.addAll(ArraysAndLists.asList(listSecond));
        }else{
            if(secondStr.equals("-"))
            {
                return CtxLottery.BET_NUM_ERROE;
            }else{
                secondCount = 1;
                secondArray.add(secondStr);
            }
        }
        
        //比较相同的
        int sameCount = 0;
        for(int i = 0; i < fristCount; i ++){
            String str1 = fristArray.get(i);
            for(int j = 0; j < secondCount; j ++){
                String str2 = secondArray.get(j);
                if(str1.equals(str2)){
                    sameCount ++;
                }
            }
        }
        
        if(sameCount > 0){
            int moreCount = fristCount - sameCount;
            if(moreCount > 0){
                if(moreCount == 0){
                    betCount = betCount + (secondCount - 2)* getBetNumCTotalNum(sameCount, 2);
                }else if (moreCount == 1){
                    if(sameCount == 1){
                        betCount = betCount + (secondCount - 1);
                    }else
                    {
                        betCount = betCount + (secondCount - 2)* getBetNumCTotalNum((fristCount - 1), 2) + (secondCount - 1)*(fristCount - 1);
                    }
                }else
                {
                    if(sameCount == 1){
                        betCount = betCount + secondCount *  getBetNumCTotalNum(moreCount, 2);
                        if(secondCount > 1){
                            betCount = betCount + (secondCount - 1) * moreCount;
                        }
                    }else{
                        betCount = betCount + secondCount* getBetNumCTotalNum(fristCount, 2) - 2 * getBetNumCTotalNum(sameCount, 2) - moreCount*sameCount;
                    }
                }
                
            }else{
                betCount = betCount + secondCount* getBetNumCTotalNum(fristCount, 2) - 2* getBetNumCTotalNum(sameCount, 2);
            }
            
        }else{
            betCount = betCount + secondCount * getBetNumCTotalNum(fristCount, 2);
            
        }
        
        
    }else{
        return CtxLottery.BET_NUM_ERROE;
    }
    if(betCount == 0){
        return CtxLottery.BET_NUM_ERROE;
    }
    
    return betCount;
}

//二重号位  选20
public static long getNum20ErChong(String betNumStr)
{
    long betCount = 0;
    if(betNumStr.indexOf("-") !=-1)
    {
        return CtxLottery.BET_NUM_ERROE;
    }
    
    if(betNumStr.indexOf(",") !=-1)
    {
        String []  list = betNumStr .split(",");
        String fristStr = list[0];
        String secondStr = list[1];
        if((fristStr.length() == 0 ) && (secondStr.length() == 0)){
            return CtxLottery.BET_NUM_ERROE;
        }
        
        int fristCount ;
        int secondCount ;
        ArrayList<String> fristArray = new ArrayList<String>() ;
        ArrayList<String> secondArray = new ArrayList<String>() ;
        
        if(fristStr .indexOf(" ") !=-1)
        {
            String []  listFrist = fristStr .split(" ");
            fristCount = listFrist.length;
            fristArray.addAll(ArraysAndLists.asList(listFrist));
            
        }else{
            fristCount = 1;
            fristArray.add(fristStr);
        }
        if(secondStr .indexOf(" ") !=-1)
        {
            String []  listSecond = secondStr .split(" ");
            secondCount = listSecond.length;
            if(secondCount < 2){
                return CtxLottery.BET_NUM_ERROE;
            }
            secondArray.addAll(ArraysAndLists.asList(listSecond));
        }else{
            return CtxLottery.BET_NUM_ERROE;
        }
        int sameCount = 0;
        for(int i = 0; i < fristCount; i ++){
            String str1 = fristArray.get(i);
            for(int j = 0; j < secondCount; j ++){
                String str2 = secondArray.get(j);
                if(str1.equals(str2)){
                    sameCount ++;
                }
            }
        }
        if(sameCount > 0){
            int moreCount = fristCount - sameCount;
            if(moreCount > 0){
                betCount = betCount + moreCount * getBetNumCTotalNum(secondCount ,2);
                if((secondCount - 1) >= 2){
                    betCount = betCount + sameCount * getBetNumCTotalNum((secondCount - 1) ,2);
                }
                
                
            }else{
                if((secondCount - 1) >= 2){
                    betCount = betCount + sameCount * getBetNumCTotalNum((secondCount - 1) ,2);
                }
            }
            
        }else{
            betCount = betCount + fristCount * getBetNumCTotalNum(secondCount ,2);
            
        }
        
    }else{
        return CtxLottery.BET_NUM_ERROE;
    }
    if(betCount == 0){
        return CtxLottery.BET_NUM_ERROE;
    }
    
    return betCount;
}
//二重号位  选10
public static int getNum10BetNumStr(String betNumStr)
{
    int betCount = 0;
    if(betNumStr.indexOf("-") !=-1)
    {
        return CtxLottery.BET_NUM_ERROE;
    }
    if(betNumStr.indexOf(",") !=-1)
    {
        String []  list = betNumStr .split(",");
        String fristStr = list[0];
        String secondStr = list[1];
        if((fristStr.length() == 0 ) && (secondStr.length() == 0)){
            return CtxLottery.BET_NUM_ERROE;
        }
        
        int fristCount ;
        int secondCount ;
        ArrayList<String> fristArray = new ArrayList<String>() ;
        ArrayList<String> secondArray = new ArrayList<String>() ;
        
        if(fristStr .indexOf(" ") !=-1)
        {
            String []  listFrist = fristStr .split(" ");
            fristCount = listFrist.length;
            fristArray.addAll(ArraysAndLists.asList(listFrist));
            
        }else{
            fristCount = 1;
            fristArray.add(fristStr);
        }
        if(secondStr .indexOf(" ") !=-1)
        {
            String []  listSecond = secondStr .split(" ");
            secondCount = listSecond.length;
            secondArray.addAll(ArraysAndLists.asList(listSecond));
        }else{
            secondCount = 1;
            secondArray.add(secondStr);
        }
        
        //比较相同的
        int sameCount = 0;
        for(int i = 0; i < fristCount; i ++){
            String str1 = fristArray.get(i);
            for(int j = 0; j < secondCount; j ++){
                String str2 = secondArray.get(j);
                if(str1.equals(str2)){
                    sameCount ++;
                }
            }
        }
        if(sameCount > 0){
            int moreCount = fristCount - sameCount;
            betCount = betCount + moreCount * secondCount;
            betCount = betCount + sameCount * (secondCount - 1);
            
        }else{
            betCount = betCount + fristCount * secondCount;
        }
        
    }else{
        return CtxLottery.BET_NUM_ERROE;
    }
    if(betCount == 0){
        return CtxLottery.BET_NUM_ERROE;
    }
    
    return betCount;
}


 /**获得投注2个关联注数    secondNum必须多数
 * 
 */
 /*
 public static int getNumFristSelectNum(int fristNum, int secondSelectNum, String betNumStr)
 {
 int numRow = 0;
 if(betNumStr.indexOf(",") !=-1)
 {
 String []  list = betNumStr .split(",");
 String fristStr = list[0];
 String secondStr = list[1];
 if(fristStr .indexOf(" ") !=-1)
 {
 String []  listFrist = fristStr .split(" ");
 if(listFrist.length < fristNum){
 return CtxLottery.BET_NUM_ERROE;
 }
 int fristCount = listFrist.length;
 ArrayList<String> fristArray = new ArrayList<>(ArraysAndLists.asList(listFrist));
 
 //第二
 if(secondStr .indexOf(" ") !=-1)
 {
 String []  listSecond = secondStr .split(" ");
 if(listSecond.length < secondSelectNum){
 return CtxLottery.BET_NUM_ERROE;
 }
 int secondCount = listSecond.length;
 ArrayList<String> secondArray = new ArrayList<>(ArraysAndLists.asList(listFrist));
 int sameCount;
 for()
 
 
 
 }else{
 return CtxLottery.BET_NUM_ERROE;
 }
 
 }else{
 
 
 }
 
 
 }else{
 return CtxLottery.BET_NUM_ERROE;
 }
 
 return numRow;
 }
 */


/**
 * - 特殊
 - 获得投注行的个数
 */
public static long getSpecialNumFromRow(int row, String betNumStr, int removeRow)
{
    long numRow = 0;
    if(betNumStr.indexOf(",") !=-1)
    {
        String []  list = betNumStr .split(",");
        if(list.length < (row + 1)){
            return CtxLottery.BET_NUM_ERROE;
        }
        if(list.length < (removeRow + 1)){
            return CtxLottery.BET_NUM_ERROE;
        }
        String unitNum = list[row];
        String removeNum = list[removeRow];
        if(removeNum.length() > unitNum.length()){
            return CtxLottery.BET_NUM_ERROE;
        }
        if(unitNum.length() == 0){
            return CtxLottery.BET_NUM_ERROE;
        }
        ArrayList<String> removeArray = new ArrayList<String>() ;
        if(removeNum .indexOf(" ") !=-1)
        {
            String []  listCount = unitNum .split(" ");
            removeArray.addAll(ArraysAndLists.asList(listCount));
            
        }else{
            removeArray.add(removeNum);
        }
        
        ArrayList<String> numArray = new ArrayList<String>() ;
        if(unitNum .indexOf(" ") !=-1)
        {
            String []  listCount = unitNum .split(" ");
            numArray.addAll(ArraysAndLists.asList(listCount));
            int count  = numArray.size();
            for(String removeStr : removeArray){
                for(String numStr : numArray)
                {
                    if(removeStr.equals(numStr)){
                        count --;
                    }
                }
            }
            return count;
            
        }else{
            return CtxLottery.BET_NUM_ERROE;
        }
        
    }else{
        return CtxLottery.BET_NUM_ERROE;
    }
}
    
    
    
}
