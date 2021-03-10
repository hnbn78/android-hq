//
//  DSSSCLotteryNumAlg.h
//  DSBet
//
//  Created by Selena on 2018/1/15.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DSSSCAlgorithmObject.h"

@interface DSSSCLotteryNumAlg : DSSSCAlgorithmObject

//获取注数
+ (NSInteger)getBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID;

//获取注数  输入
+ (NSInteger)getPutInBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID needNum:(NSInteger)needNum;

//自动过滤正确的投注
+ (NSString *)getSSCCorrectStrFromStr:(NSString *)numStr lotteryID:(NSInteger)lotteryID;

@end
