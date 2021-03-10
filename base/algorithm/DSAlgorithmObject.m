//
//  DSAlgorithmObject.m
//  DSBet
//
//  Created by Selena on 2018/1/11.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DSAlgorithmObject.h"
#import "DSBetDetailUIInfo.h"

@implementation DSAlgorithmObject

#pragma mark -   1 2,3,4,4......  或者  -,1 错误模式

+ (NSInteger)getBetNumFromBetStrModeOne:(NSString *)BetStr
{
    NSInteger betNum = 0;
    NSArray *list = [BetStr componentsSeparatedByString:@","];
    NSInteger totalMon = 1;
    for(NSString *unitNum in list){
        if((unitNum.length > 0) && [unitNum rangeOfString:@"-"].location == NSNotFound){
            if([unitNum rangeOfString:@" "].location !=NSNotFound)
            {
                NSArray *listCount = [unitNum componentsSeparatedByString:@" "];
                NSInteger count = [listCount count];
                totalMon =  totalMon * count;
            }
            
        }else{
            totalMon = BET_NUM_ERROE;
        }
        
    }
    betNum = totalMon;
    
    return betNum;
}

#pragma mark -  单列数选号码

+ (NSInteger)getBetNumStr:(NSString *)betNumStr selectNum:(NSInteger)selectNum
{
    NSArray *list = [betNumStr componentsSeparatedByString:@" "];
    NSInteger totalNum = [list count];
    NSInteger betNum = 0;
    betNum = [self getBetNumCTotalNum:totalNum withNum:selectNum];
    return betNum;
}


#pragma mark -   C(n,m)

+ (NSInteger)getBetNumCTotalNum:(NSInteger )n withNum:(NSInteger)m
{
    if(m > n){
        return BET_NUM_ERROE;
    }
    long factorialNum = 1;
    for(NSInteger i = n; i >= 1;i--)
    {
        factorialNum = i * factorialNum;
        
    }
    
    long factorialNum1 = 1;
    for(NSInteger i = m; i >= 1;i--)
    {
        factorialNum1 = i * factorialNum1;
        
    }
    
    long factorialNum2 = 1;
    for(NSInteger i = (n - m); i >= 1;i--)
    {
        factorialNum2 = i * factorialNum2;
        
    }
    
    return (long)(factorialNum/(factorialNum2 * factorialNum1));
    
    
}

#pragma mark - 阶乘 A(x,y)
+(long)getFactorialTotal:(NSInteger)sumNum selectNum:(NSInteger)number
{
    if(number > sumNum){
        return -1;
    }
    long num = sumNum;
    long factorialNum = sumNum;
    for(NSInteger i = 1; i < number;i++)
    {
        factorialNum = factorialNum * (num - i);
    }
    NSLog(@"factorialNum = %ld",factorialNum);
    return factorialNum;
    
}

#pragma mark - 获取3和值注数
+(long)getAndNumStr:(NSInteger)number
{
    NSInteger sum = 0;
    NSInteger MAX = 10;
    if(number < 10){
        MAX = (number + 1);
    }
    for (NSInteger i = 0; i < MAX; i++) {
        for (NSInteger j = 0; j < MAX; j++) {
            NSInteger m = number - i - j;
            if((m < MAX)&&(m >= 0)){
                sum ++;
            }
        }
    }
    NSLog(@"sum == %ld",(long)sum);
    return sum;
}
#pragma mark - 获取和3值注数  不限顺序
+(long)getAndNumStr2:(NSInteger)number
{
    NSInteger sum = 0;
    NSInteger MAX = 10;
    if(number < 10){
        MAX = (number + 1);
    }
    for (NSInteger i = 0; i < MAX; i++) {
        for (NSInteger j = 0; j < MAX; j++) {
            if(i <= j){
                NSInteger m = number - i - j;
                if((m < MAX)&&(m >= j)){
                    sum ++;
                }
            }
        }
    }
    
    NSLog(@"sum == %ld",(long)sum);
    return sum;
    
}

#pragma mark - 获取2和值注数
+(long)get2AndNumStr:(NSInteger)number
{
    NSInteger sum = 0;
    if(number < 10){
        sum = (number + 1);
    }else{
        sum = (19 - number);
    }
    NSLog(@"sum == %ld",(long)sum);
    return sum;
}
#pragma mark - 获取2和值注数  不限顺序
+(long)get2AndNumStr2:(NSInteger)number
{
    NSInteger sum = 0;
    if(number == 0){
        return (-1);
    }
    if(number < 10){
        sum = (NSInteger)(round((CGFloat)number/2));
    }else{
        sum = (9 - (NSInteger)((CGFloat)number/2));
    }
    
    NSLog(@"sum == %ld",(long)sum);
    return sum;
    
}


#pragma mark - 获取获得等差增长比值  1  3  6 10 15 21
+(long)getLineNumStr:(NSInteger)number
{
    NSInteger lineNum = 0;
    lineNum = number * (number + 1)/2;
    return lineNum;
}
#pragma mark - 获取获得等差增长  2  6  12  20
+(long)getZuLineNumStr:(NSInteger)number
{
    NSInteger betNum = 0;
    if(number == 2){
        return 2;
    }else{
        number --;
        betNum = (number + 2)*(number - 1) + 2;
    }
    return betNum;
}

#pragma mark - 阶乘
+(long)getFactorialStr:(NSInteger)number
{
    long factorialNum = 1;
    for(NSInteger i = number; i >= 1;i--)
    {
        factorialNum = i * factorialNum;
        
    }
    return factorialNum;
}

#pragma mark - 获取时间倒计时  交易页面
+ (NSString *)getGameTimeStr:(NSInteger)seconds
{
    NSString *timeStr = @"正在开奖.";
    //NSString *hourStr = @"00";
    NSString *minStr = @"00";
    NSString *secStr = @"00";
    
    //NSInteger hourInt = 0;
    NSInteger minInt = 0;
    NSInteger seconds2 = seconds;
    //时
//    hourInt = (NSInteger)(seconds/3600);
//    if(hourInt > 9){
//        hourStr = [NSString stringWithFormat:@"%ld",(long)hourInt];
//    }else{
//        hourStr = [NSString stringWithFormat:@"0%ld",(long)hourInt];
//    }
    
    seconds = seconds;//(NSInteger)(seconds - hourInt * 3600);
    minInt = (NSInteger)(seconds/60);
    if(minInt > 9){
        minStr = [NSString stringWithFormat:@"%ld",(long)minInt];
    }else{
        minStr = [NSString stringWithFormat:@"0%ld",(long)minInt];
    }
    
    seconds = (NSInteger)(seconds - minInt * 60);
    if(seconds > 9){
        secStr = [NSString stringWithFormat:@"%ld",(long)seconds];
    }else{
        secStr = [NSString stringWithFormat:@"0%ld",(long)seconds];
    }
    if(seconds2 > 0){
        //timeStr = [NSString stringWithFormat:@"%@:%@:%@",hourStr,minStr,secStr];
        timeStr = [NSString stringWithFormat:@"%@:%@",minStr,secStr];
    }
    
    return timeStr;
}


#pragma mark - 重置投注号码
+ (NSString *)reSetBetNum:(NSString *)betNum  infoUI:(DSBetDetailUIInfo *)info
{
    if(!info.isAddNum){
        if(info.isDelePace){
            NSString * reBNum = [betNum stringByReplacingOccurrencesOfString:@" " withString:@""];
            return reBNum;
        }
        return betNum;
    }
    
    NSLog(@"info = %@",info.layout);
    NSArray *list = info.layout;
    NSInteger firstInt = info.fromNum;
    if(list.count < 5){
        NSMutableString *reBetNum = [[NSMutableString alloc] init];
        if(firstInt == 1){//前三或者前二 前三
            NSInteger lessNum = 5 - list.count;
            [reBetNum appendString:betNum];
            for (NSInteger i = 0; i < lessNum ; i ++) {
                [reBetNum appendString:@",-"];
            }
            return reBetNum;
        }
        if(firstInt == 2){
            if(list.count == 4){
                //四星
                [reBetNum appendString:@"-,"];
                [reBetNum appendString:betNum];
            }else{
                //中三
                [reBetNum appendString:@"-,"];
                [reBetNum appendString:betNum];
                [reBetNum appendString:@",-"];
            }
        }
        if(firstInt == 3){//后三
            [reBetNum appendString:@"-,"];
            [reBetNum appendString:@"-,"];
            [reBetNum appendString:betNum];
        }
        if(firstInt == 4){//后二
            [reBetNum appendString:@"-,"];
            [reBetNum appendString:@"-,"];
            [reBetNum appendString:@"-,"];
            [reBetNum appendString:betNum];
        }
        return reBetNum;
        
    }else{
        return betNum;
    }
    return nil;
}


#pragma mark - 获取直选跨度
+(NSInteger)getCrossValueStr:(NSInteger)number
{
    NSInteger betNum = 0;
    NSInteger p = 0;
    p = 10 - number;
    if(number == 0){
        return p;
    }else{
        betNum = p * 6 + p * 6 *(number - 1);
    }
    return betNum;
}

#pragma mark - 获取直选跨度  二直选跨度
+(NSInteger)getCrossValueStr2:(NSInteger)number
{
    NSInteger betNum = 0;
    NSInteger p = 0;
    p = 10 - number;
    if(number == 0){
        return 10;
    }else{
        betNum = 2 * p;
    }
    return betNum;
}

#pragma mark - 任选二
+(NSInteger)getAny2SelectStr:(NSString *)betNumStr
{
    NSInteger betCount = 0;
    NSInteger a,b,c,d,e;
    a= b = c = d =e = 0;
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        NSInteger count = list.count;
        if(count < 2){
            return BET_NUM_ERROE;
        }
        for(NSInteger i = 0; i < count;i ++){
            NSString *numStr = [list objectAtIndex:i];
            NSInteger num = 0;
            if([numStr rangeOfString:SPACE_UTIL].location !=NSNotFound){
                NSArray *listNum = [numStr componentsSeparatedByString:SPACE_UTIL];
                num = listNum.count;
            }else{
                if([numStr isEqualToString:@"-"]){
                    num = 0;
                }else{
                    num = 1;
                }
            }
            switch (i) {
                case 0:
                {
                    a = num;
                    break;
                }
                case 1:
                {
                    b = num;
                    break;
                }
                case 2:
                {
                    c = num;
                    break;
                }
                case 3:
                {
                    d = num;
                    break;
                }
                case 4:
                {
                    e = num;
                    break;
                }
                    
                default:
                    break;
            }
        }
        betCount = a*(b + c + d + e) + b*(c + d + e) + c*(d + e) + d * e;
        
    }else{
        return BET_NUM_ERROE;
    }
    return betCount;
}
#pragma mark - 任选三
+(NSInteger)getAny3SelectStr:(NSString *)betNumStr
{
    NSInteger betCount = 0;
    NSInteger a,b,c,d,e;
    a= b = c = d =e = 0;
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        NSInteger count = list.count;
        if(count < 3){
            return BET_NUM_ERROE;
        }
        for(NSInteger i = 0; i < count;i ++){
            NSString *numStr = [list objectAtIndex:i];
            NSInteger num = 0;
            if([numStr rangeOfString:SPACE_UTIL].location !=NSNotFound){
                NSArray *listNum = [numStr componentsSeparatedByString:SPACE_UTIL];
                num = listNum.count;
            }else{
                if([numStr isEqualToString:@"-"]){
                    num = 0;
                }else{
                    num = 1;
                }
            }
            switch (i) {
                case 0:
                {
                    a = num;
                    break;
                }
                case 1:
                {
                    b = num;
                    break;
                }
                case 2:
                {
                    c = num;
                    break;
                }
                case 3:
                {
                    d = num;
                    break;
                }
                case 4:
                {
                    e = num;
                    break;
                }
                    
                default:
                    break;
            }
        }
        betCount = a*(b*c + b*d + b*e + c*d + c*e + d*e) + b*(c*d + d*e + c * e) + c*d*e;
        
    }else{
        return BET_NUM_ERROE;
    }
    return betCount;
}
#pragma mark - 任选四
+(NSInteger)getAny4SelectStr:(NSString *)betNumStr
{
    NSInteger betCount = 0;
    NSInteger a,b,c,d,e;
    a= b = c = d =e = 0;
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        NSInteger count = list.count;
        if(count < 4){
            return BET_NUM_ERROE;
        }
        for(NSInteger i = 0; i < count;i ++){
            NSString *numStr = [list objectAtIndex:i];
            NSInteger num = 0;
            if([numStr rangeOfString:SPACE_UTIL].location !=NSNotFound){
                NSArray *listNum = [numStr componentsSeparatedByString:SPACE_UTIL];
                num = listNum.count;
            }else{
                if([numStr isEqualToString:@"-"]){
                    num = 0;
                }else{
                    num = 1;
                }
            }
            switch (i) {
                case 0:
                {
                    a = num;
                    break;
                }
                case 1:
                {
                    b = num;
                    break;
                }
                case 2:
                {
                    c = num;
                    break;
                }
                case 3:
                {
                    d = num;
                    break;
                }
                case 4:
                {
                    e = num;
                    break;
                }
                    
                default:
                    break;
            }
        }
        betCount = a*(b*c*d + b*c*e + b*e*d + c*d*e) + b*c*d*e;
        
    }else{
        return BET_NUM_ERROE;
    }
    return betCount;
}

#pragma mark - 剔除豹子 单数
+ (BOOL)isAllSameNum:(NSString *)numStr
{
    NSInteger needNum = 1;
    if(numStr > 0){
        NSInteger length = numStr.length;
        NSMutableArray *numArray = [NSMutableArray new];
        for(NSInteger i = 0; i < (length/needNum); i ++){
            NSString *str = [numStr substringWithRange:NSMakeRange(i * needNum, needNum)];
            [numArray addObject:str];
        }
        for(NSInteger i = 0; i < (numArray.count - 1); i++){
            NSString *str1 = [numArray objectAtIndex:i];
            for(NSInteger j = (i + 1); j < numArray.count; j++){
                NSString *str2 = [numArray objectAtIndex:j];
                if(![str1 isEqualToString:str2]){
                    return NO;
                    break;
                }
            }
        }
    }
    return YES;
}

#pragma mark - 剔除豹子 双数
+ (BOOL)isAllSameNum2:(NSString *)numStr
{
    NSInteger needNum = 2;
    if(numStr > 0){
        NSInteger length = numStr.length;
        NSMutableArray *numArray = [NSMutableArray new];
        for(NSInteger i = 0; i < (length/needNum); i ++){
            NSString *str = [numStr substringWithRange:NSMakeRange(i * needNum, needNum)];
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
    return NO;
}
#pragma mark - 剔除混合组选  010  010  相等
+ (NSString *)isZuXuanNum:(NSString *)numStr isDouble:(BOOL)isDouble
{
    if([numStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSMutableString *reStr = [[NSMutableString alloc] init] ;
        NSMutableArray *reArray = [NSMutableArray new];
        NSArray *list = [numStr componentsSeparatedByString:COMMA_UTIL];
        for(NSInteger i = 0; i < list.count; i ++){
            NSString *str = [list objectAtIndex:i];
            NSString *reSet = [self reZuXuanListStr:str isDouble:isDouble];
            [reArray addObject:reSet];
        }
        if(list.count != reArray.count){
            NSLog(@"错误");
            return @"";
        }
        
        for(NSInteger i = 0; i < (list.count - 1); i ++){
            NSString *str = [reArray objectAtIndex:i];
            BOOL isSame = NO;
            for(NSInteger j = (i + 1);j < list.count;j ++){
                NSString *str2 = [reArray objectAtIndex:j];
                if([str isEqualToString:str2]){
                    isSame = YES;
                    break;
                }
            }
            if((!isSame))
            {
                [reStr appendString:str];
                [reStr appendString:@","];
            }
        }
        NSString *lastStr = [reArray lastObject];
        [reStr appendString:lastStr];
        return reStr;
        
    }else{
        NSString *reSet = [self reZuXuanListStr:numStr isDouble:isDouble];
        return reSet;
    }
    return @"";
}

+ (NSString *)reZuXuanListStr:(NSString *)str isDouble:(BOOL)isDouble
{
    NSString *reStr = str;
    NSInteger needNum = 1;
    if(isDouble){
        //        reStr = [reStr stringByReplacingOccurrencesOfString:@"0" withString:@"2"];
        needNum = 2;
    }
    NSInteger length = reStr.length;
    
    NSMutableArray *numArray = [NSMutableArray new];
    for(NSInteger i = 0; i < (length/needNum); i ++){
        NSString *str2 = [reStr substringWithRange:NSMakeRange(i * needNum, needNum)];
        [numArray addObject:str2];
    }
    NSArray *comparatorSortedArray = [numArray sortedArrayUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
        NSInteger v1 = [obj1 integerValue];
        NSInteger v2 = [obj2 integerValue];
        if (v1 < v2) {
            return NSOrderedAscending;
        } else if (v1 > v2) {
            return NSOrderedDescending;
        } else {
            return NSOrderedSame;
        }
        return NSOrderedSame;
    }];
    NSMutableString *reMuStr = [[NSMutableString alloc] init] ;
    for(NSString *str3 in comparatorSortedArray)
    {
        [reMuStr appendString:str3];
    }
    if(reMuStr.length > 0){
        NSLog(@"str3 = %@",reMuStr);
        return reMuStr;
    }
    return @"";
}

#pragma mark - 多次使用方法

#pragma mark  - 获得投注行的个数

+ (NSInteger)getNumFromRow:(NSInteger)row withStr:(NSString *)betNumStr
{
    NSInteger numRow = 0;
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *list = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        if(list.count < (row + 1)){
            return BET_NUM_ERROE;
        }
        NSString *unitNum = [list objectAtIndex:row];
        if(unitNum.length == 0){
            return BET_NUM_ERROE;
        }
        if([unitNum rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listCount = [unitNum componentsSeparatedByString:SPACE_UTIL];
            NSInteger count = [listCount count];
            return count;
        }else{
            if([unitNum isEqualToString:GANG_UTIL])
            {
                return BET_NUM_ERROE;
            }else{
                if([betNumStr isEqualToString:GANG_UTIL]){
                    return 0;
                }else{
                    return 1;
                }
            }
        }
        
    }else if(row == 0){
        if([betNumStr rangeOfString:SPACE_UTIL].location !=NSNotFound)
        {
            NSArray *listCount = [betNumStr componentsSeparatedByString:SPACE_UTIL];
            return listCount.count;
        }else{
            if(![betNumStr isEqualToString:GANG_UTIL]){
                return 1;
            }else{
                return 0;
            }
        }
        
    }else{
        return BET_NUM_ERROE;
    }
    return numRow;
}

//过滤单式
+ (NSArray *)putInCorrectArray:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID
{
    NSArray *array = nil;
    NSInteger needNum = [self getPutInBetNoteFromLotteryID:lotteryID];
    NSMutableArray *betStrArray = [NSMutableArray new];
    NSMutableArray *betStrNotArray = [NSMutableArray new];
    if([betNumStr rangeOfString:COMMA_UTIL].location !=NSNotFound)
    {
        NSArray *listBetStr = [betNumStr componentsSeparatedByString:COMMA_UTIL];
        for(NSInteger i = 0;i < listBetStr.count ; i ++){
            NSString *betStr1 = [listBetStr objectAtIndex:i];
            if((betStr1.length == needNum) && ([FEAppUtil isNumber:betStr1])){
                [betStrArray addObject:betStr1];
            }else{
                [betStrNotArray addObject:betStr1];
            }
        }
        NSMutableString *muStr1 = [NSMutableString new];
        NSMutableString *muStr2 = [NSMutableString new];
        for(NSInteger j = 0; j < betStrArray.count ; j ++){
            NSString *str = [betStrArray objectAtIndex:j];
            [muStr1 appendString:str];
        }
        for(NSInteger k = 0; k < betStrNotArray.count ; k ++){
            NSString *str = [betStrNotArray objectAtIndex:k];
            [muStr2 appendString:str];
            [muStr2 appendString:@","];
        }
        NSArray *arrayBet = [[NSArray alloc] initWithObjects:muStr1,muStr2, nil];
        return arrayBet;
        
    }else{
        NSArray *arrayBet = [[NSArray alloc] initWithObjects:@"",betNumStr, nil];
        return arrayBet;
    }
    
    return array;
}
+ (NSInteger)getPutInBetNoteFromLotteryID:(long)lotteryID
{
    return 5;
}

//通过id活动玩法类型
+ (NSString *)getBetTypeNameFromID:(NSInteger)gameId
{
    NSString *betName = @"SSC";
    switch (gameId) {
        case 11:
        case 50:
        case 61:
        case 151:
        case 119:
        case 161:
        case 191:
        case 51:
        case 601:
        case 811:
        case 911:
        case 46:
        case 200:
        case 201:
        case 202:
        case 203:
        case 6:
        case 205:
        case 206:
        case 711:
        case 80:  //经典重庆时时彩
        case 85:  //经典菲律宾1.5分彩
        case 86:  //经典菲律宾2分彩
        case 87:  //经典菲律宾5分彩
        {
            betName = @"SSC";
            break;
        }
      
        case 24:
        case 21:
        case 23:
        case 22:
        case 26:
        case 28:
        case 100:  //经典广东11选5
        {
            betName = @"11X5";
            break;
        }
        case 31:
        case 32:
        case 33:
        case 35:
        case 36:
        case 90:  //经典江苏快3
        {
            betName = @"K3";
            break;
        }
        case 42:
        {
            betName = @"pl3";
            break;
        }
        
        case 41:
        {
            betName = @"3DFC";
            break;
        }
        case 43:
        case 47:
        case 204:
        case 110:  //经典北京PK10
        {
            betName = @"PK10";
            break;
        }
        default:
            break;
    }
    
    return betName;
}





@end
