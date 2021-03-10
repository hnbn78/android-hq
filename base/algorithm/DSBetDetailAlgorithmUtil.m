//
//  DSBetDetailAlgorithmUtil.m
//  DSBet
//
//  Created by Selena on 2018/1/15.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DSBetDetailAlgorithmUtil.h"
#import "DSSSCLotteryNumAlg.h"
#import "DS11S5LotteryNumAlg.h"
#import "DSK3LotteryNumAlg.h"
#import "DSFFCAlgorithmObject.h"
#import "DSPK10LottrtyNumAlg.h"

@implementation DSBetDetailAlgorithmUtil
#pragma mark - 获取注数  Cell

+ (NSInteger)getBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID
{
    NSInteger betNum = 0;
    switch (ticketID) {
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
        case 42:
        {
            betNum = [DSSSCLotteryNumAlg getBetNoteFromBetNum:betNumStr lotteryID:lotteryID];
            break;
        }
        case 24:
        case 21:
        case 23:
        case 22:
        case 26:
        case 28:
        {
            betNum = [DS11S5LotteryNumAlg getBetNoteFromBetNum:betNumStr lotteryID:lotteryID];
            break;
        }
        case 31:
        case 32:
        case 33:
        case 35:
        case 36:
        {
            betNum = [DSK3LotteryNumAlg getBetNoteFromBetNum:betNumStr lotteryID:lotteryID];
            break;
        }
        case 41:
        {
            betNum = [DSFFCAlgorithmObject getBetNoteFromBetNum:betNumStr lotteryID:lotteryID];
            break;
        }
        case 43:
        case 47:
        case 110:
        case 204:
        {
            betNum = [DSPK10LottrtyNumAlg getBetNoteFromBetNum:betNumStr lotteryID:lotteryID];
            break;
        }
        default:
            break;
    }
    
    
    return betNum;
}
#pragma mark - 获取注数  输入

+ (NSInteger)getPutInBetNoteFromBetNum:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID needNum:(NSInteger)needNum
{
    NSInteger betNum = 0;
    switch (ticketID) {
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
        case 42:
        {
            betNum = [DSSSCLotteryNumAlg getPutInBetNoteFromBetNum:betNumStr lotteryID:lotteryID needNum:needNum];
            break;
        }
        case 24:
        case 21:
        case 23:
        case 22:
        case 26:
        case 28:
        {
            betNum = [DS11S5LotteryNumAlg getPutInBetNoteFromBetNum:betNumStr lotteryID:lotteryID needNum:needNum];
            break;
        }
        case 31:
        case 32:
        case 33:
        case 35:
        case 36:
        {
            betNum = [DS11S5LotteryNumAlg getPutInBetNoteFromBetNum:betNumStr lotteryID:lotteryID needNum:needNum];
            break;
        }
        case 41:
        {
            betNum = [DSFFCAlgorithmObject getPutInBetNoteFromBetNum:betNumStr lotteryID:lotteryID needNum:needNum];
            break;
        }
        case 43:
        case 47:
        case 110:
        case 204:
        {
            betNum = [DSPK10LottrtyNumAlg getPutInBetNoteFromBetNum:betNumStr lotteryID:lotteryID needNum:needNum];
            break;
        }
            
            
        default:
            break;
    }
    
    
    return betNum;
}

#pragma mark - 单式获得正确的投注号

+ (NSArray *)getCorrectArray:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID;
{
    NSArray *betArray = nil;
    switch (ticketID) {
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
        case 42:
        {
            betArray = [DSSSCLotteryNumAlg putInCorrectArray:betNumStr lotteryID:lotteryID];
            break;
        }
        case 24:
        case 21:
        case 23:
        case 22:
        case 26:
        case 28:
        {
            betArray = [DS11S5LotteryNumAlg putInCorrectArray:betNumStr lotteryID:lotteryID];
            break;
        }
        case 41:
        {
            betArray = [DSFFCAlgorithmObject putInCorrectArray:betNumStr lotteryID:lotteryID];
            break;
        }
        case 43:
        case 47:
        case 110:
        case 204:
        {
            betArray = [DSPK10LottrtyNumAlg putInCorrectArray:betNumStr lotteryID:lotteryID];
            break;
        }
        default:
            break;
    }
    
    
    return nil;
}

#pragma mark - 自动过滤正确的投注

+ (NSString *)getCorrectStr:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID
{
    NSString *numStr = betNumStr;
    switch (ticketID) {
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
        case 42:
        {
            numStr = [DSSSCLotteryNumAlg getSSCCorrectStrFromStr:betNumStr lotteryID:lotteryID];
            break;
        }
        case 24:
        case 21:
        case 23:
        case 22:
        case 26:
        case 28:
        {
            numStr = [DS11S5LotteryNumAlg get11S5CorrectStrFromStr:betNumStr lotteryID:lotteryID];
            break;
        }
        case 41:
        {
            numStr = [DSFFCAlgorithmObject getFFCCorrectStrFromStr:betNumStr lotteryID:lotteryID];
            break;
        }
        case 43:
        case 47:
        case 110:
        case 204:
        {
            numStr = [DSPK10LottrtyNumAlg getPKCorrectStrFromStr:betNumStr lotteryID:lotteryID];
            break;
        }
            
        default:
            break;
    }
    return numStr;
}



#pragma mark - 获取正确的投注
+ (NSString *)getCorrectBetNumStr:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID
{
    NSString *numStr = betNumStr;
    switch (ticketID) {
            
            //SSC
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
        case 42:
        {
            numStr = [numStr stringByReplacingOccurrencesOfString:SPACE_UTIL withString:@""];
            break;
        }
            
            
            
            //11选5
        case 24:
        case 21:
        case 23:
        case 22:
        case 26:
        case 28:
        {
      
            break;
        }
        case 41:
        {
            numStr = [numStr stringByReplacingOccurrencesOfString:SPACE_UTIL withString:@""];
            break;
        }
      
        default:
            break;
    }
    
    return numStr;
}
//获取正确的投注  最后的处理  输入
+ (NSString *)getPutCorrectBetNumStr:(NSString *)betNumStr lotteryID:(NSInteger)lotteryID ticketID:(NSInteger)ticketID needNum:(NSInteger)needNum
{
    NSString *numStr = betNumStr;
    switch (ticketID) {
            //11选5
        case 24:
        case 21:
        case 23:
        case 22:
        case 26:
        case 28:
            
            //PK
        case 43:
        case 47:
        case 110:
        case 204:
        {
            numStr = [DS11S5LotteryNumAlg getPutCorrectBetNumStr:betNumStr lotteryID:lotteryID ticketID:ticketID needNum:needNum];
            break;
        }
            
        default:
            break;
    }
    
    return numStr;
    
}







@end
