//
//  DS11S5LotteryNumAlg.m
//  DSBet
//
//  Created by Selena on 2018/1/19.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DS11S5LotteryNumAlg.h"

@implementation DS11S5LotteryNumAlg

//获取注数
+ (NSInteger)getBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID
{
    NSInteger betNum = 0;
    if(betNumStr.length ==0 ){
        return BET_NUM_ERROE;
    }
    
    switch (lotteryID) {
        case 2001: //前三直选复式
        {
            betNum = [self getBetNumStrZhixuan:betNumStr num:3];
            break;
            
            break;
        }
        case 2003: //前三组选复式      //C(n,3)
        case 2013:  //三中三
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:3];
            
            break;
            
            break;
        }
        case 2005:  //前二直选复式
        {
            betNum = [self getBetNumStrZhixuan:betNumStr num:2];
            break;
        }
        case 2007:  //前二组选复式   //C(n,2)
        case 2012:  //复式二中二
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:2];
            break;
        }
        case 2009:  //不位胆
        case 2011:  //复式一中一
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = num;
            break;
        }
        case 2010:  //定位胆
        {
            NSString    *replacedStr = [betNumStr stringByReplacingOccurrencesOfString:COMMA_UTIL withString:SPACE_UTIL];
            NSArray *list = [replacedStr componentsSeparatedByString:SPACE_UTIL];
            NSInteger count = 0;
            for(NSInteger i = 0; i < list.count; i++){
                NSString *str = [list objectAtIndex:i];
                if(![str isEqualToString:GANG_UTIL]){
                    count++;
                }
            }
            betNum = count;
            break;
        }
        case 2014:
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:4];
            break;
        }
        case 2015:
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:5];
            break;
        }
        case 2016:
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:6];
            break;
        }
        case 2017:
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:7];
            break;
        }
        case 2018:
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:8];
            break;
        }
   
        default:
            break;
    }

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
    
    return betNum;
}

//获取正确的投注  最后的处理  输入
+ (NSString *)getPutCorrectBetNumStr:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID needNum:(NSInteger)needNum
{
    NSString *betStr = betNumStr;
    
    betStr = [betStr stringByReplacingOccurrencesOfString:@"," withString:@""];
    betStr = [betStr stringByReplacingOccurrencesOfString:@";" withString:@""];
    betStr = [betStr stringByReplacingOccurrencesOfString:@" " withString:@""];
    NSMutableString *muStr = [NSMutableString new];
    if(betStr.length > 0){
        if(betStr.length%2 != 0){
            return betStr;
        }else{
            NSInteger length = betStr.length;
            for(NSInteger i = 0; i < (length/2); i ++){
                NSString *str = [betStr substringWithRange:NSMakeRange(i * 2, 2)];
                [muStr appendString:str];
                //TODO
                if(i != ((length/2) - 1)){
                    if(((i + 1)*2)%needNum == 0){
                        [muStr appendString:@";"];
                    }else{
                        [muStr appendString:@" "];
                    }
                    
                }
            }
            return muStr;
        }
    }

    return betStr;
}


#pragma mark  - 工具
#pragma mark - 过滤字符串
+ (NSString *)get11S5CorrectStrFromStr:(NSString *)numStr lotteryID:(NSInteger)lotteryID
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
        NSLog(@"11组选过滤前%@",betNumStr);
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
                if((num <= 0) || (num >= 12)){
                    isBeyond = YES;
                    break;
                }
            }
        }
    }
    return isBeyond;
}

#pragma mark - 获取正确的投注
+ (NSString *)getCorrectBetNumStr:(NSString *)betNumStr playID:(long)playId
{
    NSString *betStr = betNumStr;
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        NSMutableString *numStrNum = [NSMutableString new];
        for(NSString *str in list){
            NSInteger length = str.length;
            if(length%2 != 0){
                return nil;
                break;
            }
            
            for(NSInteger i = 0; i < (length/2); i ++){
                NSString *str2 = [str substringWithRange:NSMakeRange(i * 2, 2)];
                if(i != (length/2 - 1)){
                    str2 = [NSString stringWithFormat:@"%@ ",str2];
                }
                [numStrNum appendString:str2];
            }
            [numStrNum appendString:@","];
        }
        betStr = [numStrNum substringToIndex:(numStrNum.length - 1)];
        
    }else{
        NSMutableString *numStrNum = [NSMutableString new];
        for(NSInteger i = 0; i < (betNumStr.length/2); i ++){
            NSString *str2 = [betNumStr substringWithRange:NSMakeRange(i * 2, 2)];
            if(i != (betNumStr.length/2 - 1)){
                str2 = [NSString stringWithFormat:@"%@ ",str2];
            }
            [numStrNum appendString:str2];
        }
        betStr = numStrNum;
    }
    return betStr;
}
+ (NSString *)isPassZuXuan:(NSString *)betStr playId:(long)playId
{
    switch (playId) {
            //单式
        case 2002:
        case 2006:
        case 2019:
        case 2020:
        case 2021:
        case 2022:
        case 2023:
        case 2024:
        case 2025:
        case 2026:

        {
            return betStr;
            break;
        }
            //组选   任选
            
        default:
        {
            NSString *reStr = [self isZuXuanNum:betStr isDouble:YES];
            return reStr;
            break;
        }
    }
    return @"";
}

+ (BOOL)isPassPaozi:(NSString *)betStr playId:(long)playId
{
    BOOL isAllSame = [self isAllSameNum2:betStr];
    if(isAllSame){
        return NO;
    }
    return YES;
}


@end
