//
//  DSSSCAlgorithmObject.m
//  DSBet
//
//  Created by Selena on 2018/1/15.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DSSSCAlgorithmObject.h"

@implementation DSSSCAlgorithmObject

#pragma mark  - 工具

//输入
+ (NSInteger)getBetNumPutIn:(NSString *)betNumStr needNum:(NSInteger)needNum
{
    NSInteger betCount = 0;
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        for(NSString *str in list){
            if(str.length != needNum){
                return BET_NUM_ERROE;
                break;
            }
        }
        return list.count;
        
    }else{
        if(betNumStr.length != needNum){
            return BET_NUM_ERROE;
        }else{
            return 1;
        }
    }
    return betCount;
}


//组六
+ (NSInteger)getZuLiuBetNum:(NSString *)betNumStr
{
    NSInteger betCount = 0;
    if([betNumStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:SPACE_UTIL];
        if(list.count < 3){
            return BET_NUM_ERROE;
        }
        betCount = [self getBetNumCTotalNum:list.count withNum:3];
        
    }else{
        return BET_NUM_ERROE;
    }
    
    return betCount;
}
//组三
+ (NSInteger)getZuSanBetNum:(NSString *)betNumStr
{
    NSInteger betCount = 0;
    if([betNumStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:SPACE_UTIL];
        betCount = [self getZuLineNumStr:list.count];
        
    }else{
        return BET_NUM_ERROE;
    }
    
    return betCount;
}

//跨度
+ (NSInteger)getNumCrossValue:(NSString *)betNumStr isThreeNum:(BOOL)isThreeNum
{
    NSInteger betCount = 0;
    if([betNumStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:SPACE_UTIL];
        NSInteger sumCount = 0;
        for(NSInteger i = 0;i < list.count; i ++){
            NSString *numStr = [list objectAtIndex:i];
            NSInteger num = [numStr integerValue];
            if(isThreeNum){
                betCount = [self getCrossValueStr:num];
            }else{
                betCount = [self getCrossValueStr2:num];
            }
            sumCount = sumCount + betCount;
        }
        betCount = sumCount;
        
    }else{
        NSInteger num = [betNumStr integerValue];
        if(isThreeNum){
            betCount = [self getCrossValueStr:num];
        }else{
            betCount = [self getCrossValueStr2:num];
        }
        return betCount;
    }
    
    return betCount;
}

//3和值
+ (NSInteger)getNumAndValues:(NSString *)betNumStr sort:(BOOL)isSort
{
    NSInteger betCount = 0;
    if([betNumStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:SPACE_UTIL];
        NSInteger sumCount = 0;
        for(NSInteger i = 0;i < list.count; i ++){
            NSString *numStr = [list objectAtIndex:i];
            NSInteger num = [numStr integerValue];
            if(isSort){
                betCount = [self getAndNumStr:num];
            }else{
                betCount = [self getAndNumStr2:num];
                if(num%3==0){
                    betCount --;
                }
            }
            sumCount = sumCount + betCount;
        }
        betCount = sumCount;
        
    }else{
        NSInteger num = [betNumStr integerValue];
        if(isSort){
            betCount = [self getAndNumStr:num];
        }else{
            betCount = [self getAndNumStr2:num];
            if(num%3==0){
                betCount --;
            }
        }
        
        return betCount;
    }
    
    return betCount;
}


+ (NSInteger)get2NumAndValues:(NSString *)betNumStr sort:(BOOL)isSort
{
    NSInteger betCount = 0;
    if([betNumStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:SPACE_UTIL];
        NSInteger sumCount = 0;
        for(NSInteger i = 0;i < list.count; i ++){
            NSString *numStr = [list objectAtIndex:i];
            NSInteger num = [numStr integerValue];
            if(isSort){
                betCount = [self get2AndNumStr:num];
            }else{
                betCount = [self get2AndNumStr2:num];
            }
            sumCount = sumCount + betCount;
        }
        betCount = sumCount;
        
    }else{
        NSInteger num = [betNumStr integerValue];
        if(isSort){
            betCount = [self get2AndNumStr:num];
        }else{
            betCount = [self get2AndNumStr2:num];
        }
        
        return betCount;
    }
    
    return betCount;
}

//二重号位  选60
+ (NSInteger)getNumErChong:(NSString *)betNumStr
{
    NSInteger betCount = 0;
    if([betNumStr rangeOfString:GANG_UTIL].location !=NSNotFound)
    {
        return BET_NUM_ERROE;
    }
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        NSString *fristStr = [list objectAtIndex:0];
        NSString *secondStr = [list objectAtIndex:1];
        if((fristStr.length == 0 ) && (secondStr.length == 0)){
            return BET_NUM_ERROE;
        }
        
        NSInteger fristCount ;
        NSInteger secondCount ;
        NSMutableArray *fristArray = [NSMutableArray new];
        NSMutableArray *secondArray = [NSMutableArray new];
        
        if([fristStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listFrist = [fristStr componentsSeparatedByString:SPACE_UTIL];
            fristCount = listFrist.count;
            [fristArray addObjectsFromArray:listFrist];
            
        }else{
            fristCount = 1;
            [fristArray addObject:fristStr];
        }
        if([secondStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listSecond = [secondStr componentsSeparatedByString:SPACE_UTIL];
            secondCount = listSecond.count;
            if(secondCount < 3){
                return BET_NUM_ERROE;
            }
            [secondArray addObjectsFromArray:listSecond];
        }else{
            return BET_NUM_ERROE;
        }
        NSInteger sameCount = 0;
        for(NSInteger i = 0; i < fristCount; i ++){
            NSString *str1 = [fristArray objectAtIndex:i];
            for(NSInteger j = 0; j < secondCount; j ++){
                NSString *str2 = [secondArray objectAtIndex:j];
                if([str1 isEqualToString:str2]){
                    sameCount ++;
                }
            }
        }
        if(sameCount > 0){
            NSInteger moreCount = fristCount - sameCount;
            if(moreCount > 0){
                betCount = betCount + moreCount * [self getBetNumCTotalNum:secondCount withNum:3];
                if((secondCount - 1) >= 3){
                    betCount = betCount + sameCount * [self getBetNumCTotalNum:(secondCount - 1) withNum:3];
                }
                
                
            }else{
                if((secondCount - 1) >= 3){
                    betCount = betCount + sameCount * [self getBetNumCTotalNum:(secondCount - 1) withNum:3];
                }
            }
            
        }else{
            betCount = betCount + fristCount * [self getBetNumCTotalNum:secondCount withNum:3];
            
        }
        
    }else{
        return BET_NUM_ERROE;
    }
    if(betCount == 0){
        return BET_NUM_ERROE;
    }
    
    return betCount;
}
#pragma mark  - 二重号位  选30
+ (NSInteger)getNum30ErChong:(NSString *)betNumStr
{
    NSInteger betCount = 0;
    if([betNumStr rangeOfString:GANG_UTIL].location !=NSNotFound)
    {
        return BET_NUM_ERROE;
    }
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        NSString *fristStr = [list objectAtIndex:0];
        NSString *secondStr = [list objectAtIndex:1];
        if((fristStr.length == 0 ) && (secondStr.length == 0)){
            return BET_NUM_ERROE;
        }
        
        NSInteger fristCount ;
        NSInteger secondCount ;
        NSMutableArray *fristArray = [NSMutableArray new];
        NSMutableArray *secondArray = [NSMutableArray new];
        
        if([fristStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listFrist = [fristStr componentsSeparatedByString:SPACE_UTIL];
            fristCount = listFrist.count;
            if(fristCount < 2){
                return BET_NUM_ERROE;
            }
            [fristArray addObjectsFromArray:listFrist];
            
        }else{
            return BET_NUM_ERROE;
        }
        if([secondStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listSecond = [secondStr componentsSeparatedByString:SPACE_UTIL];
            secondCount = listSecond.count;
            [secondArray addObjectsFromArray:listSecond];
        }else{
            if([secondStr isEqualToString:GANG_UTIL])
            {
                return BET_NUM_ERROE;
            }else{
                secondCount = 1;
                [secondArray addObject:secondStr];
            }
        }
        
        //比较相同的
        NSInteger sameCount = 0;
        for(NSInteger i = 0; i < fristCount; i ++){
            NSString *str1 = [fristArray objectAtIndex:i];
            for(NSInteger j = 0; j < secondCount; j ++){
                NSString *str2 = [secondArray objectAtIndex:j];
                if([str1 isEqualToString:str2]){
                    sameCount ++;
                }
            }
        }
        
        if(sameCount > 0){
            NSInteger moreCount = fristCount - sameCount;
            if(moreCount > 0){
                if(moreCount == 0){
                    betCount = betCount + (secondCount - 2)*[self getBetNumCTotalNum:(sameCount) withNum:2];
                }else if (moreCount == 1){
                    if(sameCount == 1){
                        betCount = betCount + (secondCount - 1);
                    }else
                    {
                        betCount = betCount + (secondCount - 2)*[self getBetNumCTotalNum:(fristCount - 1) withNum:2] + (secondCount - 1)*(fristCount - 1);
                    }
                }else
                {
                    if(sameCount == 1){
                        betCount = betCount + secondCount * [self getBetNumCTotalNum:moreCount withNum:2];
                        if(secondCount > 1){
                            betCount = betCount + (secondCount - 1) * moreCount;
                        }
                    }else{
                        betCount = betCount + secondCount*[self getBetNumCTotalNum:(fristCount) withNum:2] - 2*[self getBetNumCTotalNum:(sameCount) withNum:2] - moreCount*sameCount;
                        
                    }
                    
                    
                    
                }
                
            }else{
                betCount = betCount + secondCount*[self getBetNumCTotalNum:(fristCount) withNum:2] - 2*[self getBetNumCTotalNum:(sameCount) withNum:2];
            }
            
        }else{
            betCount = betCount + secondCount * [self getBetNumCTotalNum:fristCount withNum:2];
            
        }
        
        
    }else{
        return BET_NUM_ERROE;
    }
    if(betCount == 0){
        return BET_NUM_ERROE;
    }
    
    return betCount;
}

//二重号位  选20
+ (NSInteger)getNum20ErChong:(NSString *)betNumStr
{
    NSInteger betCount = 0;
    if([betNumStr rangeOfString:GANG_UTIL].location !=NSNotFound)
    {
        return BET_NUM_ERROE;
    }
    
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        NSString *fristStr = [list objectAtIndex:0];
        NSString *secondStr = [list objectAtIndex:1];
        if((fristStr.length == 0 ) && (secondStr.length == 0)){
            return BET_NUM_ERROE;
        }
        
        NSInteger fristCount ;
        NSInteger secondCount ;
        NSMutableArray *fristArray = [NSMutableArray new];
        NSMutableArray *secondArray = [NSMutableArray new];
        
        if([fristStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listFrist = [fristStr componentsSeparatedByString:SPACE_UTIL];
            fristCount = listFrist.count;
            [fristArray addObjectsFromArray:listFrist];
            
        }else{
            fristCount = 1;
            [fristArray addObject:fristStr];
        }
        if([secondStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listSecond = [secondStr componentsSeparatedByString:SPACE_UTIL];
            secondCount = listSecond.count;
            if(secondCount < 2){
                return BET_NUM_ERROE;
            }
            [secondArray addObjectsFromArray:listSecond];
        }else{
            return BET_NUM_ERROE;
        }
        NSInteger sameCount = 0;
        for(NSInteger i = 0; i < fristCount; i ++){
            NSString *str1 = [fristArray objectAtIndex:i];
            for(NSInteger j = 0; j < secondCount; j ++){
                NSString *str2 = [secondArray objectAtIndex:j];
                if([str1 isEqualToString:str2]){
                    sameCount ++;
                }
            }
        }
        if(sameCount > 0){
            NSInteger moreCount = fristCount - sameCount;
            if(moreCount > 0){
                betCount = betCount + moreCount * [self getBetNumCTotalNum:secondCount withNum:2];
                if((secondCount - 1) >= 2){
                    betCount = betCount + sameCount * [self getBetNumCTotalNum:(secondCount - 1) withNum:2];
                }
                
                
            }else{
                if((secondCount - 1) >= 2){
                    betCount = betCount + sameCount * [self getBetNumCTotalNum:(secondCount - 1) withNum:2];
                }
            }
            
        }else{
            betCount = betCount + fristCount * [self getBetNumCTotalNum:secondCount withNum:2];
            
        }
        
    }else{
        return BET_NUM_ERROE;
    }
    if(betCount == 0){
        return BET_NUM_ERROE;
    }
    
    return betCount;
}
//二重号位  选10
+ (NSInteger)getNum10BetNumStr:(NSString *)betNumStr
{
    NSInteger betCount = 0;
    if([betNumStr rangeOfString:GANG_UTIL].location !=NSNotFound)
    {
        return BET_NUM_ERROE;
    }
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        NSString *fristStr = [list objectAtIndex:0];
        NSString *secondStr = [list objectAtIndex:1];
        if((fristStr.length == 0 ) && (secondStr.length == 0)){
            return BET_NUM_ERROE;
        }
        
        NSInteger fristCount ;
        NSInteger secondCount ;
        NSMutableArray *fristArray = [NSMutableArray new];
        NSMutableArray *secondArray = [NSMutableArray new];
        
        if([fristStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listFrist = [fristStr componentsSeparatedByString:SPACE_UTIL];
            fristCount = listFrist.count;
            [fristArray addObjectsFromArray:listFrist];
            
        }else{
            fristCount = 1;
            [fristArray addObject:fristStr];
        }
        if([secondStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listSecond = [secondStr componentsSeparatedByString:SPACE_UTIL];
            secondCount = listSecond.count;
            [secondArray addObjectsFromArray:listSecond];
        }else{
            secondCount = 1;
            [secondArray addObject:secondStr];
        }
        
        //比较相同的
        NSInteger sameCount = 0;
        for(NSInteger i = 0; i < fristCount; i ++){
            NSString *str1 = [fristArray objectAtIndex:i];
            for(NSInteger j = 0; j < secondCount; j ++){
                NSString *str2 = [secondArray objectAtIndex:j];
                if([str1 isEqualToString:str2]){
                    sameCount ++;
                }
            }
        }
        if(sameCount > 0){
            NSInteger moreCount = fristCount - sameCount;
            betCount = betCount + moreCount * secondCount;
            betCount = betCount + sameCount * (secondCount - 1);
            
        }else{
            betCount = betCount + fristCount * secondCount;
        }
        
    }else{
        return BET_NUM_ERROE;
    }
    if(betCount == 0){
        return BET_NUM_ERROE;
    }
    
    return betCount;
}

/*
 #pragma mark  - 获得投注2个关联注数    secondNum必须多数
 
 + (NSInteger)getNumFristSelectNum:(NSInteger)fristNum secondSelectNum:(NSInteger)secondNum withStr:(NSString *)betNumStr
 {
 NSInteger numRow = 0;
 if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
 {
 NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
 NSString *fristStr = [list objectAtIndex:0];
 NSString *secondStr = [list objectAtIndex:1];
 if([fristStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
 {
 NSArray *listFrist = [fristStr componentsSeparatedByString:SPACE_UTIL];
 if([listFrist count] < fristNum){
 return BET_NUM_ERROE;
 }
 NSInteger fristCount = [listFrist count];
 NSMutableArray *fristArray = [[NSMutableArray alloc] initWithArray:listFrist];
 
 //第二
 if([secondStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
 {
 NSArray *listSecond = [secondStr componentsSeparatedByString:SPACE_UTIL];
 if([listSecond count] < secondNum){
 return BET_NUM_ERROE;
 }
 NSInteger secondCount = [listSecond count];
 NSMutableArray *secondArray = [[NSMutableArray alloc] initWithArray:listFrist];
 NSInteger sameCount;
 for()
 
 
 
 }else{
 return BET_NUM_ERROE;
 }
 
 }else{
 
 
 }
 
 
 }else{
 return BET_NUM_ERROE;
 }
 
 return numRow;
 }
 */


#pragma mark  - 特殊

#pragma mark  - 获得投注行的个数


+ (NSInteger)getSpecialNumFromRow:(NSInteger)row withStr:(NSString *)betNumStr remove:(NSInteger)removeRow
{
    NSInteger numRow = 0;
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        if(list.count < (row + 1)){
            return BET_NUM_ERROE;
        }
        if(list.count < (removeRow + 1)){
            return BET_NUM_ERROE;
        }
        NSString *unitNum = [list objectAtIndex:row];
        NSString *removeNum = [list objectAtIndex:removeRow];
        if(removeNum.length > unitNum.length){
            return BET_NUM_ERROE;
        }
        if(unitNum.length == 0){
            return BET_NUM_ERROE;
        }
        NSMutableArray *removeArray = [NSMutableArray new];
        if([removeNum rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listCount = [unitNum componentsSeparatedByString:SPACE_UTIL];
            [removeArray addObjectsFromArray:listCount];
            
        }else{
            [removeArray addObject:removeNum];
        }
        
        NSMutableArray *numArray = [NSMutableArray new];
        if([unitNum rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listCount = [unitNum componentsSeparatedByString:SPACE_UTIL];
            [numArray addObjectsFromArray:listCount];
            NSInteger count  = [numArray count];
            for(NSString *removeStr in removeArray){
                for(NSString *numStr in numArray)
                {
                    if([removeStr isEqualToString:numStr]){
                        count --;
                    }
                }
            }
            return count;
            
        }else{
            return BET_NUM_ERROE;
        }
        
    }else{
        return BET_NUM_ERROE;
    }
    return numRow;
}



@end
