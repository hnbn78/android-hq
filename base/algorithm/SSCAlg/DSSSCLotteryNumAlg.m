//
//  DSSSCLotteryNumAlg.m
//  DSBet
//
//  Created by Selena on 2018/1/15.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DSSSCLotteryNumAlg.h"

@implementation DSSSCLotteryNumAlg

//获取注数
+ (NSInteger)getBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID
{
    NSInteger betNum = 0;
    if(betNumStr.length ==0 ){
        return BET_NUM_ERROE;
    }
    
    switch (lotteryID) {
        case 1001:
        case 1011:  //后四直选复式
        case 1018:  //前三直选复式
        case 1029:  //中三直选复式
        case 1039:  //后三直选复式
        case 1050:  //后二直选复式
        case 1053:  //后二大小单双
        case 1059:  //前二直选复式
        case 1062:  //前二大小单双
            
        {
            betNum = [self getBetNumFromBetStrModeOne:betNumStr];
            break;
        }
        case 1055:  //后二组选复式
        case 1064:  //前二组选复式
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:2];
            break;
        }
        case 1003:
            // 五星直选组合
        {
            betNum = [self getBetNumFromBetStrModeOne:betNumStr] * 5;
            break;
        }
        case 1013:  //后四直选组合
        {
            betNum = [self getBetNumFromBetStrModeOne:betNumStr] * 4;
            break;
        }
        case 1004://"五星组选120"
        {
            betNum = [self getBetNumStr:betNumStr selectNum:5];
            break;
        }
        case 1005://"五星组选60"
        {
            NSInteger secondRow = [self getNumFromRow:1 withStr:betNumStr];
            if(secondRow < 3){
                return BET_NUM_ERROE;
            }
            betNum = [self getNumErChong:betNumStr];
            break;
        }
        case 1006://"五星组选30"
        {
            NSInteger fristNum = [self getNumFromRow:0 withStr:betNumStr];
            if(fristNum < 2){
                return BET_NUM_ERROE;
            }
            betNum = [self getNum30ErChong:betNumStr];
            break;
        }
        case 1007://"五星组选20"
        {
            NSInteger secondRow = [self getNumFromRow:1 withStr:betNumStr];
            if(secondRow < 2){
                return BET_NUM_ERROE;
            }
            betNum = [self getNum20ErChong:betNumStr];
            break;
        }
        case 1008://"五星组选10"
        case 1009://"五星组选5"
        case 1017:  //后四 组选4
        case 1097:  //任四组选4
        {
            NSInteger fristRow = [self getNumFromRow:0 withStr:betNumStr];
            if(fristRow < 1){
                return BET_NUM_ERROE;
            }
            NSInteger secondRow = [self getNumFromRow:1 withStr:betNumStr];
            if(secondRow < 1){
                return BET_NUM_ERROE;
            }
            betNum = [self getNum10BetNumStr:betNumStr];
            
            break;
        }
        case 1010://大小单双
        case 1027://前三特殊号
        case 1028://前三和值尾数
        case 1037://中三特殊号
        case 1038://中三和值尾数
        case 1048://后三特殊号
        case 1049://后三和值尾数
        {
            NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
            betNum = list.count;
            break;
        }
        case 1014:  //后四 组选24   C(n,4)
        case 1094:  //任四组选24
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:4];
            break;
        }
        case 1015:  //后四 组选12
        case 1095:  //任四组选12
        {
            NSInteger secondRow = [self getNumFromRow:1 withStr:betNumStr];
            if(secondRow < 2){
                return BET_NUM_ERROE;
            }
            betNum = [self getNum20ErChong:betNumStr];
            
            break;
        }
        case 1016:  //后四 组选6
        case 1096:  //任四组选6
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:2];
            
            break;
        }
        case 1020:  //前三直选和值
        case 1031:  //中三直选和值
        case 1041:  //后三直选和值
        case 1052:  //后二直选和值
        case 1061:  //前二直选和值
        case 1090:  //任三直选和值
        
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            if(num == 0){
                return BET_NUM_ERROE;
            }
            betNum = [self getNumAndValues:betNumStr sort:YES];
            break;
        }
        case 1046:  //后三组选和值
        case 1025:  //前三组选和值
        case 1057:  //后二组选和值
        case 1066:  //前二组选和值
        case 1091:  //任三组选和值
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            if(num == 0){
                return BET_NUM_ERROE;
            }
            betNum = [self getNumAndValues:betNumStr sort:NO];
            break;
        }
        case 1021:  //前三直选跨度
        case 1032:  //中三直选跨度
        case 1042:  //后三直选跨度
        {
            betNum = [self getNumCrossValue:betNumStr isThreeNum:YES];
            break;
        }
        case 1054:  //后二直选跨度
        case 1063:  //前二直选跨度
        {
            betNum = [self getNumCrossValue:betNumStr isThreeNum:NO];
            break;
        }
        case 1022:  //组三
        case 1033:  //中三组选组三
        case 1043:  //后三组选组三
        case 1087:  //任三组三
        {
            betNum = [self getZuSanBetNum:betNumStr];
            break;
        }
        case 1023:  //组六
        case 1034:  //中三组选组六
        case 1044:  //后三组选组六
        case 1088:  //任三组六
        {
            betNum = [self getZuLiuBetNum:betNumStr];
            break;
        }
        case 1026:  //前三组选包胆
        case 1036:  //中三组选包胆
        case 1047:  //后三组选包胆
        {
            betNum = 54;
            break;
        }
        case 1058:  //后二组选包胆
        case 1067:  //前二组选包胆
        {
            betNum = 9;
            break;
        }
        case 1068:  //定位胆
        case 1069:  //五星一码
        case 1072:  //前四一码
        case 1074:  //后四一码
        case 1076:  //后三一码
        case 1078:  //前三一码
        case 1098:  //一帆风顺
        case 1099:
        case 1100:
        case 1101:
        case 1102:
        case 1103:
        case 1104:
        case 1105:
        case 1106:
        case 1107:
        case 1108:
        case 1109:
        case 1110:
        case 1111:
        {
            NSString    *replacedStr = [betNumStr stringByReplacingOccurrencesOfString:SPACE_UTIL withString:@""];
            replacedStr = [replacedStr stringByReplacingOccurrencesOfString:COMMA_UTIL withString:@""];
            replacedStr = [replacedStr stringByReplacingOccurrencesOfString:@"-" withString:@""];
            betNum = replacedStr.length;
            break;
        }
        case 1070:  //五星二码
        case 1073:  //前四二码
        case 1075:  //后四二码
        case 1077:  //后三二码
        case 1079:  //前三二码
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:2];
            break;
        }
        case 1071:  //五星三码
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:3];
            break;
        }
        case 1080:  //任二复式
        {
            betNum = [self getAny2SelectStr:betNumStr];
            break;
        }
        case 1082:  //任二组选     C(n,2)
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            betNum = [self getBetNumCTotalNum:num withNum:2];
            break;
        }
        case 1083:  //任二直选和值
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            if(num == 0){
                return BET_NUM_ERROE;
            }
            betNum = [self get2NumAndValues:betNumStr sort:YES];
            break;
        }
        case 1084:  //任二组选和值
        {
            NSInteger num = [self getNumFromRow:0 withStr:betNumStr];
            if(num == 0){
                return BET_NUM_ERROE;
            }
            betNum = [self get2NumAndValues:betNumStr sort:NO];
            break;
        }
        case 1085:  //任三复式
        {
            betNum = [self getAny3SelectStr:betNumStr];
            break;
        }
        case 1092://任四直选复式
        {
            betNum = [self getAny4SelectStr:betNumStr];
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
