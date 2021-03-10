//
//  DSFFCAlgorithmObject.m
//  DSBet
//
//  Created by Selena on 2018/1/22.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DSFFCAlgorithmObject.h"

@implementation DSFFCAlgorithmObject


//获取注数
+ (NSInteger)getBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID
{
    NSInteger betNum = 0;
    if(betNumStr.length ==0 ){
        return BET_NUM_ERROE;
    }
    
    switch (lotteryID) {
        case 4001:    //三码直选复式
        case 4008:
        case 4010:
        case 4017:
        case 4018:
        case 4019:
        case 4012:
        case 4016:
        {
            betNum = [self getBetNumFromBetStrModeOne:betNumStr];
            break;
        }
        case 4003:  //三码直选和值
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            if(num == 0){
                return BET_NUM_ERROE;
            }
            betNum = [self getNumAndValues:betNumStr sort:YES];
            break;
        }
        case 4004:  //三码直选和值
        {
            betNum = [self getZuSanBetNum:betNumStr];
            break;
        }
        case 4005:  //三码组选组六
        {
            betNum = [self getZuLiuBetNum:betNumStr];
            break;
        }
        case 4007:  //三码组选和值
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            if(num == 0){
                return BET_NUM_ERROE;
            }
            betNum = [self getNumAndValues:betNumStr sort:NO];
            break;
        }
        case 4014://定位胆
        case 4015://一码不定胆
        {
            NSString    *replacedStr = [betNumStr stringByReplacingOccurrencesOfString:SPACE_UTIL withString:@""];
            replacedStr = [replacedStr stringByReplacingOccurrencesOfString:COMMA_UTIL withString:@""];
            replacedStr = [replacedStr stringByReplacingOccurrencesOfString:@"-" withString:@""];
            betNum = replacedStr.length;
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
+ (NSString *)getFFCCorrectStrFromStr:(NSString *)numStr lotteryID:(NSInteger)lotteryID;
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
/*
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
 */
+ (BOOL)isPassPaozi:(NSString *)betStr playId:(long)playId
{
    switch (playId) {
            //单式
        case 4002: //直选单式
        case 4009:
        case 4010:
        case 4011:
        case 4013:
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
        case 4002: //直选单式
        case 4009:
        case 4010:
        case 4011:
        case 4013:
            
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
