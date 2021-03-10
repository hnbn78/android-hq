package com.desheng.base.algorithm.ssc;//
//  DSSSCLotteryNumAlg.m
//  DSBet
//
//  Created by Selena on 2018/1/15.
//  Copyright © 2018年 Bill. All rights reserved.
//

import com.ab.debug.AbDebug;
import com.ab.util.ArraysAndLists;
import com.desheng.base.algorithm.DSAlgorithmObject;
import com.desheng.base.algorithm.DSSSCAlgorithmObject;
import com.desheng.base.context.CtxLottery;

public class DSSSCLotteryNumAlg extends DSSSCAlgorithmObject {
    
    //获取注数
    public static long getBetNoteFromBetNum(String betNumStr, int lotteryID) {
        long betNum = 0;
        if (betNumStr.length() == 0) {
            return CtxLottery.BET_NUM_ERROE;
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
                betNum = getBetNumFromBetStrModeOne(betNumStr);
                break;
            }
            case 1055:  //后二组选复式
            case 1064:  //前二组选复式
            {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 2);
                break;
            }
            
            case 1003:
                // 五星直选组合
            {
                betNum = getBetNumFromBetStrModeOne(betNumStr) * 5;
                break;
            }
            case 1013:  //后四直选组合
            {
                betNum = getBetNumFromBetStrModeOne(betNumStr) * 4;
                break;
            }
            case 1004://"五星组选120"
            {
                betNum = getBetNumStr(betNumStr, 5);
                break;
            }
            case 1005://"五星组选60"
            {
                long secondRow = DSAlgorithmObject.getNumFromRow(1, betNumStr);
                if (secondRow < 3) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = getNumErChong(betNumStr);
                break;
            }
            case 1006://"五星组选30"
            {
                long fristNum = getNumFromRow(0, betNumStr);
                if (fristNum < 2) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = getNum30ErChong(betNumStr);
                break;
            }
            case 1007://"五星组选20"
            {
                long secondRow = getNumFromRow(1, betNumStr);
                if (secondRow < 2) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = getNum20ErChong(betNumStr);
                break;
            }
            case 1008://"五星组选10"
            case 1009://"五星组选5"
            case 1017:  //后四 组选4
            case 1097:  //任四组选4
            {
                long fristRow = getNumFromRow(0, betNumStr);
                if (fristRow < 1) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                long secondRow = getNumFromRow(1, betNumStr);
                if (secondRow < 1) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = getNum10BetNumStr(betNumStr);
                
                break;
            }
            case 1010://大小单双
            case 1027://前三特殊号
            case 1037://中三特殊号
            case 1048://后三特殊号
            case 1028://前三和值尾数
            case 1038://中三和值尾数
            case 1049://后三和值尾数
            {
                String[] list = betNumStr.split(",");
                betNum = list.length;
                break;
            }
            case 1014:  //后四 组选24   C(n,4)
            case 1094:  //任四组选24
            {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 4);
                break;
            }
            case 1015:  //后四 组选12
            case 1095:  //任四组选12
            {
                long secondRow = getNumFromRow(1, betNumStr);
                if (secondRow < 2) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = getNum20ErChong(betNumStr);
                
                break;
            }
            case 1016:  //后四 组选6
            case 1096:  //任四组选6
            {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 2);
                
                break;
            }
            case 1061:  //前二直选和值
            case 1052:  //后二直选和值
            {
                long num = getNumFromRow(0, betNumStr);
                if (num == 0) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = get2NumAndValues(betNumStr, true);
                break;
            }
            case 1020:  //前三直选和值
            case 1031:  //中三直选和值
            case 1041:  //后三直选和值
            case 1090:  //任三直选和值
            
            {
                long num = getNumFromRow(0, betNumStr);
                if (num == 0) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = getNumAndValues(betNumStr, true);
                break;
            }
            case 1066:  //前二组选和值
            case 1057:  //后二组选和值
            {
                long num = getNumFromRow(0, betNumStr);
                if (num == 0) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                        betNum = get2NumAndValues(betNumStr, false);
                break;
            }
            case 1046:  //后三组选和值
            case 1025:  //前三组选和值
            case 1091:  //任三组选和值
            {
                long num = getNumFromRow(0, betNumStr);
                if (num == 0) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = getNumAndValues(betNumStr, false);
                break;
            }
            
            case 1021:  //前三直选跨度
            case 1032:  //中三直选跨度
            case 1042:  //后三直选跨度
            {
                betNum = getNumCrossValue(betNumStr, true);
                break;
            }
            case 1054:  //后二直选跨度
            case 1063:  //前二直选跨度
            {
                betNum = getNumCrossValue(betNumStr, false);
                break;
            }
            case 1022:  //组三
            case 1033:  //中三组选组三
            case 1043:  //后三组选组三
            case 1087:  //任三组三
            {
                betNum = getZuSanBetNum(betNumStr);
                break;
            }
            case 1023:  //组六
            case 1034:  //中三组选组六
            case 1044:  //后三组选组六
            case 1088:  //任三组六
            {
                betNum = getZuLiuBetNum(betNumStr);
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
            case 1112:
            case 1113:
            case 1114:
            case 1115:
            case 1116:
            case 1117:
            case 1118:
            case 1119:
            case 1120:
            case 1121:
                {
                String replacedStr = betNumStr.replace(" ", "");
                replacedStr = replacedStr.replace(",", "");
                replacedStr = replacedStr.replace("-", "");
                betNum = replacedStr.length();
                break;
            }
            case 1070:  //五星二码
            case 1073:  //前四二码
            case 1075:  //后四二码
            case 1077:  //后三二码
            case 1079:  //前三二码
            {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 2);
                break;
            }
            case 1071:  //五星三码
            {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 3);
                break;
            }
            case 1080:  //任二复式
            {
                betNum = getAny2SelectStr(betNumStr);
                break;
            }
            case 1082:  //任二组选     C(n,2)
            {
                int num = getNumFromRow(0, betNumStr);
                betNum = getBetNumCTotalNum(num, 2);
                break;
            }
            case 1083:  //任二直选和值
            {
                long num = getNumFromRow(0, betNumStr);
                if (num == 0) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = get2NumAndValues(betNumStr, true);
                break;
            }
            case 1084:  //任二组选和值
            {
                long num = getNumFromRow(0, betNumStr);
                if (num == 0) {
                    return CtxLottery.BET_NUM_ERROE;
                }
                betNum = get2NumAndValues(betNumStr, false);
                break;
            }
            case 1085:  //任三复式
            {
                betNum = getAny3SelectStr(betNumStr);
                break;
            }
            case 1092://任四直选复式
            {
                betNum = getAny4SelectStr(betNumStr);
                break;
            }
            default:
                break;
        }
        
        
        AbDebug.log(AbDebug.TAG_APP, String.format("注数为:%d", (long) betNum));
        return betNum;
    }
    
    
    //获取注数  输入
    public static long getPutInBetNoteFromBetNum(String betNumStr, int lotteryID, int needNum) {
        long betNum = 0;
        if (betNumStr.length() == 0) {
            return CtxLottery.BET_NUM_ERROE;
        }
        betNum = getBetNumPutIn(betNumStr, needNum);
        AbDebug.log(AbDebug.TAG_APP, String.format("注数为:%d", (long) betNum));
        return betNum;
    }
    //自动过滤正确的投注
    public static String getSSCCorrectStrFromStr(String numStr, int lotteryID) {
        String betNumStr = numStr;
        if (betNumStr.contains(",")) {
            StringBuffer reStr = new StringBuffer();
            String[] list = betNumStr.split(",");
            for (int i = 0; i < (list.length - 1); i++) {
                String str = list[i];
                boolean isSame = false;
                for (int j = (i + 1); j < list.length; j++) {
                    String str2 = list[j];
                    if (str.equals(str2)) {
                        isSame = true;
                        break;
                    }
                }
                if ((!isSame) && (isPassPaozi(str, lotteryID))) {
                    reStr.append(str);
                    reStr.append(",");
                }
            }
            String lastStr = ArraysAndLists.lastObject(list);
            if (isPassPaozi(lastStr, lotteryID)) {
                reStr.append(lastStr);
            }
            AbDebug.log(AbDebug.TAG_APP, String.format("组选过滤前%s", reStr));
            String reStr2 = isPassZuXuan(reStr.toString(), lotteryID);
            return reStr2;
        } else {
            if (!isPassPaozi(betNumStr, lotteryID)) {
                return "";
            }
            betNumStr = isPassZuXuan(betNumStr, lotteryID);
        }
        return betNumStr;
    }
    
    /**
     * 过滤字符串
     */
    public static boolean isPassPaozi(String betStr, int playId) {
        switch (playId) {
            //单式
            case 1002: //五星直选单式
            case 1012:  //后四直选单式
            case 1019:  //前三直选单式
            case 1030:  //中三直选单式
            case 1040:  //后三直选单式
            case 1051:  //后二直选单式
            case 1060:  //前二直选单式
            case 1081:  //任二单式
            case 1086:  //任三单式
            case 1093:  //任四单式
            {
                return true;
            }
            //组选   任选
            
            default: {
                boolean isAllSame = isAllSameNum(betStr);
                if (isAllSame) {
                    return false;
                }
                break;
            }
        }
        return true;
    }
    
    public static String isPassZuXuan(String betStr, int playId) {
        switch (playId) {
            //单式
            case 1002: //五星直选单式
            case 1012:  //后四直选单式
            case 1019:  //前三直选单式
            case 1030:  //中三直选单式
            case 1040:  //后三直选单式
            case 1051:  //后二直选单式
            case 1060:  //前二直选单式
            case 1086:  //任三单式
            case 1093:  //任四单式
            case 1081:
            
            {
                return betStr;
            }
            //组选   任选
            
            default: {
                String reStr = isZuXuanNum(betStr, false);
                return reStr;
            }
        }
    }
    
    
}
