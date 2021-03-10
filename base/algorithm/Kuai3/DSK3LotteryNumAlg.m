//
//  DSK3LotteryNumAlg.m
//  DSBet
//
//  Created by Selena on 2018/1/22.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DSK3LotteryNumAlg.h"

@implementation DSK3LotteryNumAlg


//获取注数
+ (NSInteger)getBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID
{
    NSInteger betNum = 0;
    if(betNumStr.length ==0 ){
        return BET_NUM_ERROE;
    }
    
    switch (lotteryID) {
        case 3001:  //二不同号标准
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:2];
            break;
        }
        case 3002:  //二同号单选
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            NSInteger num2 = [self getNumFromRow:1 withStr:betNumStr];
            betNum = num * num2;
            break;
        }
        case 3003:  //二同号复选
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = num;
            break;
        }
        case 3004:  //三不同号标准
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:3];
            break;
        }
        case 3005:  //三同号单选
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = num;
            break;
        }
        case 3006:  //三同号通选
        case 3007:
        {
            betNum = 1;
            break;
        }
        case 3009:  //和值
        case 3010:
        case 3011:
        case 3008:
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = num;
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
+ (NSString *)getSSCCorrectStrFromStr:(NSString *)numStr lotteryID:(NSInteger)lotteryID
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
                }
            }
            if((!isSame) && ([self isPassPaozi:str playId:lotteryID]))
            {
                [reStr appendString:str];
                [reStr appendString:@","];
            }
        }
        NSString *lastStr = [list lastObject];
        if([self isPassPaozi:lastStr playId:lotteryID]){
            [reStr appendString:lastStr];
        }
        NSLog(@"组选过滤前%@",reStr);
        NSString * reStr2 = [self isPassZuXuan:reStr playId:lotteryID];
        return reStr2;
    }else{
        if(![self isPassPaozi:betNumStr playId:lotteryID]){
            return @"";
        }
        betNumStr = [self isPassZuXuan:betNumStr playId:lotteryID];
    }
    return betNumStr;
}







#pragma mark  - 工具

#pragma mark - 过滤字符串
+ (NSString *)getSSCCorrectStrFromStr:(NSString *)numStr playID:(long)playId
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
                }
            }
            if((!isSame) && ([self isPassPaozi:str playId:playId]))
            {
                [reStr appendString:str];
                [reStr appendString:@","];
            }
        }
        NSString *lastStr = [list lastObject];
        if([self isPassPaozi:lastStr playId:playId]){
            [reStr appendString:lastStr];
        }
        NSLog(@"组选过滤前%@",reStr);
        NSString * reStr2 = [self isPassZuXuan:reStr playId:playId];
        return reStr2;
    }else{
        if(![self isPassPaozi:betNumStr playId:playId]){
            return @"";
        }
        betNumStr = [self isPassZuXuan:betNumStr playId:playId];
    }
    return betNumStr;
}
+ (BOOL)isPassPaozi:(NSString *)betStr playId:(long)playId
{
    switch (playId) {
            //单式
        case 1002: //五星直选单式
        case 1012:  //后四直选单式
        case 1019:  //前三直选单式
        case 1030:  //中三直选单式
        case 1040:  //后三直选单式
        case 1051:  //后二直选单式
        case 1056:  //后二组选单式
        case 1060:  //前二直选单式
        case 1086:  //任三单式
        case 1093:  //任四单式
        {
            return YES;
            break;
        }
            //组选   任选
            
        default:
        {
            BOOL isAllSame = [self isAllSameNum:betStr];
            if(isAllSame){
                return NO;
            }
            break;
        }
    }
    return YES;
}

+ (NSString *)isPassZuXuan:(NSString *)betStr playId:(long)playId
{
    switch (playId) {
            //单式
        case 1002: //五星直选单式
        case 1012:  //后四直选单式
        case 1019:  //前三直选单式
        case 1030:  //中三直选单式
        case 1040:  //后三直选单式
        case 1051:  //后二直选单式
        case 1056:  //后二组选单式
        case 1060:  //前二直选单式
        case 1086:  //任三单式
        case 1093:  //任四单式
            
        {
            return betStr;
            break;
        }
            //组选   任选
            
        default:
        {
            NSString *reStr = [self isZuXuanNum:betStr isDouble:NO];
            return reStr;
            break;
        }
    }
    return @"";
}


@end
