//
//  DSAlgorithmObject.h
//  DSBet
//
//  Created by Selena on 2018/1/11.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FEAppUtil.h"
@class DSBetDetailUIInfo;

//错误码
#define BET_NUM_ERROE    -1

@interface DSAlgorithmObject : NSObject

#pragma mark -   1 2,3,4,4......  或者  -,1 错误模式

+ (NSInteger)getBetNumFromBetStrModeOne:(NSString *)BetStr;

#pragma mark -  单列数选号码  字符串选n个数

+ (NSInteger)getBetNumStr:(NSString *)betNumStr selectNum:(NSInteger)selectNum;

#pragma mark -   算法
#pragma mark -   C(n,m)   组合

+ (NSInteger)getBetNumCTotalNum:(NSInteger )n withNum:(NSInteger)m;

#pragma mark - 阶乘 A(x,y)
+(long)getFactorialTotal:(NSInteger)sumNum selectNum:(NSInteger)number;

#pragma mark - 阶乘  n!
+(long)getFactorialStr:(NSInteger)number;

#pragma mark - 获取3和值注数  有顺序
+(long)getAndNumStr:(NSInteger)number;

#pragma mark - 获取3和值注数  不限顺序
+(long)getAndNumStr2:(NSInteger)number;

#pragma mark - 获取2和值注数
+(long)get2AndNumStr:(NSInteger)number;
#pragma mark - 获取2和值注数  不限顺序
+(long)get2AndNumStr2:(NSInteger)number;

#pragma mark - 获取获得等差增长比值  1  3 6 10  之和
+(long)getLineNumStr:(NSInteger)number;

#pragma mark - 获取获得等差增长  2  6  12  20
+(long)getZuLineNumStr:(NSInteger)number;

#pragma mark - 获取时间倒计时  交易页面
+ (NSString *)getGameTimeStr:(NSInteger)seconds;

#pragma mark - 重置投注号码
+ (NSString *)reSetBetNum:(NSString *)betNum  infoUI:(DSBetDetailUIInfo *)info;

#pragma mark - 获取直选跨度
+(NSInteger)getCrossValueStr:(NSInteger)number;
#pragma mark - 获取直选跨度  二直选跨度
+(NSInteger)getCrossValueStr2:(NSInteger)number;

#pragma mark - 任选二
+(NSInteger)getAny2SelectStr:(NSString *)betNumStr;

#pragma mark - 任选三
+(NSInteger)getAny3SelectStr:(NSString *)betNumStr;
#pragma mark - 任选四
+(NSInteger)getAny4SelectStr:(NSString *)betNumStr;
#pragma mark - 剔除袍子 单数
+ (BOOL)isAllSameNum:(NSString *)numStr;

#pragma mark - 剔除袍子 双数
+ (BOOL)isAllSameNum2:(NSString *)numStr;

#pragma mark - 剔除混合组选
+ (NSString *)isZuXuanNum:(NSString *)numStr isDouble:(BOOL)isDouble;

#pragma mark - 多次使用方法

#pragma mark  - 获得投注行的个数
+ (NSInteger)getNumFromRow:(NSInteger)row withStr:(NSString *)betNumStr;


//过滤单式
+ (NSArray *)putInCorrectArray:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID;
+ (NSInteger)getPutInBetNoteFromLotteryID:(long)lotteryID;

//通过id活动玩法类型
+ (NSString *)getBetTypeNameFromID:(NSInteger)gameId;

@end
