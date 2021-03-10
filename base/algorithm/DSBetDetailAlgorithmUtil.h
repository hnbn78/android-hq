//
//  DSBetDetailAlgorithmUtil.h
//  DSBet
//
//  Created by Selena on 2018/1/15.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DSAlgorithmObject.h"

@interface DSBetDetailAlgorithmUtil : DSAlgorithmObject

//获取注数     
+ (NSInteger)getBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID;


#pragma mark - 获取选位数

//+ (NSInteger)getBetBitsFromPlayID:(long)playId ticketID:(NSInteger)ticketID;

#pragma mark - 单式获得正确的投注号
+ (NSArray *)getCorrectArray:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID;


//获取注数  输入
+ (NSInteger)getPutInBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID needNum:(NSInteger)needNum;

//自动过滤正确的投注  输入
+ (NSString *)getCorrectStr:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID;

//获取正确的投注  最后的处理
+ (NSString *)getCorrectBetNumStr:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID;


//获取正确的投注  最后的处理  输入
+ (NSString *)getPutCorrectBetNumStr:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID needNum:(NSInteger)needNum;

@end
