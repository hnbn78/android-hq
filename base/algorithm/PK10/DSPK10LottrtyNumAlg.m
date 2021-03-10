//
//  DSPK10LottrtyNumAlg.m
//  DSBet
//
//  Created by Selena on 2018/1/22.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DSPK10LottrtyNumAlg.h"

@implementation DSPK10LottrtyNumAlg

//获取注数
+ (NSInteger)getBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID
{
    NSInteger betNum = 0;
    if(betNumStr.length ==0 ){
        return BET_NUM_ERROE;
    }
    switch (lotteryID) {
        case 5001:
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            if(num == 0){
                return BET_NUM_ERROE;
            }else{
                return num;
            }
            break;
        }
        case 5002:  //前二复式
        case 5004:  //前三复式
        case 5006:  //前四复式
        case 5008:  //前五复式
        {
            NSInteger betNum = [self getBetNotRepeatNumFromStr:betNumStr];
            return betNum;
            break;
        }
        case 5010:  //定位胆第1~5名
        case 5011:  //定位胆第6~10名
        {
            betNumStr = [betNumStr stringByReplacingOccurrencesOfString:@"-" withString:@""];
            betNumStr = [betNumStr stringByReplacingOccurrencesOfString:@"," withString:@""];
            betNumStr = [betNumStr stringByReplacingOccurrencesOfString:@" " withString:@""];
            NSInteger num = betNumStr.length;
            if(num == 0){
                return BET_NUM_ERROE;
            }else{
                return (num/2);
            }
            break;
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
        case 5022:
        {
            NSString *betStr = betNumStr;
            betStr = [betStr stringByReplacingOccurrencesOfString:COMMA_UTIL withString:@""];
            betStr = [betStr stringByReplacingOccurrencesOfString:@" " withString:@""];
            betNum = betStr.length;
            break;
        }
            
            
            
        default:
            break;
    }
    
    
    NSLog(@"注数为:%ld",(long)betNum);
    return betNum;
}

//获取注数  输入
+ (NSInteger)getPutInBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID needNum:(NSInteger)needNum
{
    NSInteger betNum = 0;
    if(betNumStr.length ==0 ){
        return BET_NUM_ERROE;
    }
    betNum = [self getBetNumPutIn:betNumStr needNum:needNum];
    NSLog(@"注数为:%ld",(long)betNum);
    return betNum;
}

//自动过滤正确的投注
+ (NSString *)getPKCorrectStrFromStr:(NSString *)numStr lotteryID:(NSInteger)lotteryID
{
    NSString *betNumStr = numStr;
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSMutableString *reStr = [[NSMutableString alloc] init] ;
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        for(NSInteger i = 0; i < (list.count - 1); i ++){
            NSString *str = [list objectAtIndex:i];
            BOOL isSame = NO;
            for(NSInteger j = (i + 1);j < list.count;j ++){
                NSString *str2 = [list objectAtIndex:j];
                if([str isEqualToString:str2]){
                    isSame = YES;
                    break;
                }else if([self isBeyond:str])
                {
                    isSame = YES;
                    break;
                }
            }
            if((!isSame) && ([self isPassPaozi:str playId:lotteryID]))
            {
                [reStr appendString:str];
                [reStr appendString:@","];
            }
        }
        NSString *lastStr = [list lastObject];
        
        if((![self isBeyond:lastStr]) && ([self isPassPaozi:lastStr playId:lotteryID])){
            [reStr appendString:lastStr];
            betNumStr = reStr;
        }else{
            betNumStr = reStr;
        }
        NSLog(@"betNumStr过滤前%@",betNumStr);
        betNumStr = [self isPassZuXuan:betNumStr playId:lotteryID];
    }else{
        if([self isBeyond:betNumStr]){
            return @"";
        }
    }
    return betNumStr;
}

+ (BOOL)isBeyond:(NSString *)numStr
{
    BOOL isBeyond = NO;
    NSLog(@"过滤");
    if(numStr.length > 0){
        if(numStr.length%2 != 0){
            return YES;
        }else{
            NSInteger length = numStr.length;
            for(NSInteger i = 0; i < (length/2); i ++){
                NSString *str = [numStr substringWithRange:NSMakeRange(i * 2, 2)];
                NSInteger num = [str integerValue];
                if((num <= 0) || (num >= 11)){
                    isBeyond = YES;
                    break;
                }
            }
        }
    }
    return isBeyond;
}



+ (BOOL)isPassPaozi:(NSString *)betStr playId:(long)playId
{
    BOOL isAllSame = [self isAllSameNum2:betStr];
    if(isAllSame){
        return NO;
    }
    return YES;
}
+ (NSString *)isPassZuXuan:(NSString *)betStr playId:(long)playId
{
    return betStr;
}



#pragma mark  - 工具

//不重复组数选择
+ (NSInteger)getBetNotRepeatNumFromStr:(NSString *)numStr
{
    NSInteger betNum = 0;
    if([numStr rangeOfString:GANG_UTIL].location ==NSNotFound)
    {
        if([numStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
        {
            NSArray *list = [numStr componentsSeparatedByString:COMMA_UTIL];
            NSMutableArray *arrayList = [NSMutableArray new];
            for(NSString * str1 in list){
                
                if([str1 rangeOfString:SPACE_UTIL].location !=NSNotFound){
                    NSArray *array = [str1 componentsSeparatedByString:SPACE_UTIL];
                    [arrayList addObject:array];
                }else{
                    NSMutableArray *array = [NSMutableArray new];
                    [array addObject:str1];
                    [arrayList addObject:array];
                }
            }
            
            NSInteger i = 0;
            NSArray *array1 = [arrayList objectAtIndex:0];
            NSArray *array2 = [arrayList objectAtIndex:(++i)];
            
            NSArray *array3 ;
            NSArray *array4 ;
            NSArray *array5 ;
            if(i < ([arrayList count] - 1)){
                array3 = [arrayList objectAtIndex:(++i)];
            }
            if(i < ([arrayList count] - 1)){
                array4 = [arrayList objectAtIndex:(++i)];
            }
            if(i < ([arrayList count] - 1)){
                array5 = [arrayList objectAtIndex:(++i)];
            }
            //++++++++++++++++++++
            for(NSInteger j = 0; j < [array1 count]; j ++){
                NSString *str1 = [array1 objectAtIndex:j];
                for(NSInteger a = 0; a < [array2 count]; a ++){
                    NSString *str2 = [array2 objectAtIndex:a];
                    if(![str1 isEqualToString:str2]){
                        if([array3 count] > 0){
                            for(NSInteger b = 0; b < [array3 count]; b ++){
                                NSString *str3 = [array3 objectAtIndex:b];
                                if((![str1 isEqualToString:str3])&&(![str2 isEqualToString:str3])){
                                    if([array4 count] > 0){
                                        for(NSInteger c = 0; c < [array4 count]; c ++){
                                            NSString *str4 = [array4 objectAtIndex:c];
                                            if((![str1 isEqualToString:str4])&&(![str2 isEqualToString:str4])&&(![str3 isEqualToString:str4])){
                                                if([array5 count] > 0){
                                                    for(NSInteger d = 0; d < [array5 count]; d ++){
                                                        NSString *str5 = [array5 objectAtIndex:d];
                                                        if((![str1 isEqualToString:str5])&&(![str2 isEqualToString:str5])&&(![str3 isEqualToString:str5])&&(![str4 isEqualToString:str5])){
                                                            betNum ++;
                                                        }
                                                    }
                                                }else{
                                                    betNum ++;
                                                }
                                            }
                                        }
                                        
                                    }else{
                                        betNum ++;
                                    }
                                    
                                }
                            }
                            
                        }else{
                            betNum ++;
                        }
                    }
                    
                }
            }
            
            //++++++++++++++++++++
            return betNum;
            
        }else{
            return 0;
        }
        
    }else{
        return 0;
    }
    
    
    return betNum;
}


@end
