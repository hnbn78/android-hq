//
//  DS11S5AlgorithObject.m
//  DSBet
//
//  Created by Selena on 2018/1/19.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DS11S5AlgorithObject.h"

@implementation DS11S5AlgorithObject

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
            //            else if([self isSameNum:str]){
            //                return BET_NUM_ERROE;
            //                break;
            //            }
        }
        return list.count;
        
    }else{
        if(betNumStr.length != needNum){
            return BET_NUM_ERROE;
        }else{
            return 1;
            //            if([self isSameNum:betNumStr]){
            //                return BET_NUM_ERROE;
            //            }else{
            //                return 1;
            //            }
            
        }
    }
    return betCount;
}
+ (BOOL)isSameNum:(NSString *)numStr
{
    if(numStr > 0){
        if(numStr.length%2 != 0){
            return YES;
        }else{
            NSInteger length = numStr.length;
            NSMutableArray *numArray = [NSMutableArray new];
            for(NSInteger i = 0; i < (length/2); i ++){
                NSString *str = [numStr substringWithRange:NSMakeRange(i * 2, 2)];
                [numArray addObject:str];
            }
            for(NSInteger i = 0; i < (numArray.count - 1); i++){
                NSString *str1 = [numArray objectAtIndex:i];
                for(NSInteger j = (i + 1); j < numArray.count; j++){
                    NSString *str2 = [numArray objectAtIndex:j];
                    if([str1 isEqualToString:str2]){
                        return YES;
                        break;
                    }
                }
            }
        }
    }
    return NO;
}

#pragma mark  - 11选5
//胆码
+ (NSInteger)getBetNumStrDanTuo:(NSString *)betNumStr fristNum:(NSInteger)fristNum selectNum:(NSInteger)selectNum
{
    NSInteger betNum = 0;
    NSInteger num = [self getNumFromRow:1 withStr:betNumStr];
    
    betNum = [self getBetNumCTotalNum:num withNum:(selectNum - fristNum)];
    
    
    NSLog(@"注数为:%ld",(long)betNum);
    return betNum;
}



//直选复式
+ (NSInteger)getBetNumStrZhixuan:(NSString *)betNumStr num:(NSInteger)needNum
{
    if([betNumStr rangeOfString:GANG_UTIL].location !=NSNotFound)
    {
        return BET_NUM_ERROE;
    }
    NSInteger betCount = 0;
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        if(list.count != needNum){
            return BET_NUM_ERROE;
        }
        NSMutableArray *fristArray = [NSMutableArray new];
        NSMutableArray *secArray = [NSMutableArray new];
        NSMutableArray *thrArray = [NSMutableArray new];
        for(NSInteger i = 0;i < list.count ;i ++){
            NSString *betStr = [list objectAtIndex:i];
            if([betStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
            {
                NSArray *arrayList = [betStr componentsSeparatedByString:SPACE_UTIL];
                switch (i) {
                    case 0:
                    {
                        [fristArray addObjectsFromArray:arrayList];
                        break;
                    }
                    case 1:
                    {
                        [secArray addObjectsFromArray:arrayList];
                        break;
                    }
                    case 2:
                    {
                        [thrArray addObjectsFromArray:arrayList];
                        break;
                    }
                        
                    default:
                        break;
                }
            }else{
                switch (i) {
                    case 0:
                    {
                        [fristArray addObject:betStr];
                        break;
                    }
                    case 1:
                    {
                        [secArray addObject:betStr];
                        break;
                    }
                    case 2:
                    {
                        [thrArray addObject:betStr];
                        break;
                    }
                        
                    default:
                        break;
                }
            }
            
        }
        //筛选
        if(needNum == 3){
            for(NSString *str1 in fristArray){
                for(NSString *str2 in secArray){
                    for(NSString *str3 in thrArray){
                        if(![str1 isEqualToString:str2]){
                            if(![str2 isEqualToString:str3]){
                                if(![str1 isEqualToString:str3]){
                                    betCount ++;
                                }
                            }
                        }
                    }
                }
            }
        }else if(needNum == 2){
            for(NSString *str1 in fristArray){
                for(NSString *str2 in secArray){
                    if(![str1 isEqualToString:str2]){
                        betCount ++;
                    }
                }
            }
        }
        //筛选结束
        
    }else{
        return BET_NUM_ERROE;
    }
    
    return betCount;
}


//输入
+ (NSInteger)get11Select5BetNumPutIn:(NSString *)betNumStr needNum:(NSInteger)needNum
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
            return list.count;
        }
        
    }else{
        if(betNumStr.length != 2*needNum){
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
            if(sameCount == 1){
                if(moreCount > 1){
                    betCount = betCount + secondCount * [self getBetNumCTotalNum:moreCount withNum:2];
                    if(secondCount > 1){
                        betCount = betCount + (secondCount - 1) * moreCount;
                    }
                    
                }
                
            }else{
                betCount = betCount + secondCount * [self getBetNumCTotalNum:moreCount withNum:2];
                betCount = betCount + (secondCount - 2)* [self getBetNumCTotalNum:sameCount withNum:2];
                if(moreCount == 1){
                    betCount = betCount + (sameCount - 1)*(secondCount - 1 );
                }else{
                    betCount = betCount + (sameCount)*(moreCount)*(secondCount - 1 );
                }
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
