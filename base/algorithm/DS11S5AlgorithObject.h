//
//  DS11S5AlgorithObject.h
//  DSBet
//
//  Created by Selena on 2018/1/19.
//  Copyright © 2018年 Bill. All rights reserved.
//

#import "DSAlgorithmObject.h"

@interface DS11S5AlgorithObject : DSAlgorithmObject

//输入
+ (NSInteger)getBetNumPutIn:(NSString *)betNumStr needNum:(NSInteger)needNum;

+ (BOOL)isSameNum:(NSString *)numStr;


#pragma mark  - 11选5
//胆码
+ (NSInteger)getBetNumStrDanTuo:(NSString *)betNumStr fristNum:(NSInteger)fristNum selectNum:(NSInteger)selectNum;


//直选复式
+ (NSInteger)getBetNumStrZhixuan:(NSString *)betNumStr num:(NSInteger)needNum;



//输入
+ (NSInteger)get11Select5BetNumPutIn:(NSString *)betNumStr needNum:(NSInteger)needNum;
//组六
+ (NSInteger)getZuLiuBetNum:(NSString *)betNumStr;
//组三
+ (NSInteger)getZuSanBetNum:(NSString *)betNumStr;
//跨度
+ (NSInteger)getNumCrossValue:(NSString *)betNumStr isThreeNum:(BOOL)isThreeNum;
//3和值
+ (NSInteger)getNumAndValues:(NSString *)betNumStr sort:(BOOL)isSort;
+ (NSInteger)get2NumAndValues:(NSString *)betNumStr sort:(BOOL)isSort;
//二重号位  选60
+ (NSInteger)getNumErChong:(NSString *)betNumStr;
#pragma mark  - 二重号位  选30
+ (NSInteger)getNum30ErChong:(NSString *)betNumStr;
//二重号位  选20
+ (NSInteger)getNum20ErChong:(NSString *)betNumStr;
//二重号位  选10
+ (NSInteger)getNum10BetNumStr:(NSString *)betNumStr;
#pragma mark  - 特殊

#pragma mark  - 获得投注行的个数
+ (NSInteger)getSpecialNumFromRow:(NSInteger)row withStr:(NSString *)betNumStr remove:(NSInteger)removeRow;


@end
