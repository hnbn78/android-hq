//
//  DS11S5LotteryNumAlg.h
//  DSBet
//
//  Created by Selena on 2018/1/19.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DS11S5AlgorithObject.h"

@interface DS11S5LotteryNumAlg : DS11S5AlgorithObject

//获取注数
+ (NSInteger)getBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID;

//获取注数  输入
+ (NSInteger)getPutInBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID needNum:(NSInteger)needNum;

//自动过滤正确的投注  输入
+ (NSString *)get11S5CorrectStrFromStr:(NSString *)numStr lotteryID:(NSInteger)lotteryID;

//获取正确的投注  最后的处理
+ (NSString *)getCorrectBetNumStr:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID;

//获取正确的投注  最后的处理  输入
+ (NSString *)getPutCorrectBetNumStr:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID needNum:(NSInteger)needNum;

@end
