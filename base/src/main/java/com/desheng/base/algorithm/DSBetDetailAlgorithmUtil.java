package com.desheng.base.algorithm;//
//  DSBetDetailAlgorithmUtil.m
//  DSBet
//
//  Created by Selena on 2018/1/15.
//  Copyright © 2018年 Bill. All rights reserved.
//

import com.desheng.base.algorithm.D3DFFC.DSFFCAlgorithmObject;
import com.desheng.base.algorithm.d11s5.DS11S5LotteryNumAlg;
import com.desheng.base.algorithm.kuai3.DSK3LotteryNumAlg;
import com.desheng.base.algorithm.pk10.DSPK10LottrtyNumAlg;
import com.desheng.base.algorithm.ssc.DSSSCLotteryNumAlg;


public class DSBetDetailAlgorithmUtil {


    /***
     * 获取注数 cell
     * */
    public static long getBetNoteFromBetNum(String betNumStr, int lotteryID, int ticketID) {
        long betNum = 0;
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

                //v2.3新增（9个）
            case 210:
            case 213:
            case 217:
            case 212:
            case 218:
            case 215:
            case 214:
            case 216:
            case 211:

            case 307:
            case 315:
            case 318:

            case 300:
            case 301:
            case 302:
            case 303:
            case 308:
            case 319:
            case 320:
            case 321:
            case 322:
            case 325:
            case 326:
            case 327:
            case 350:
            case 351:
            case 352:
            case 353:
            case 354:
            case 355:

//            case 60:
            case 42: {
                betNum = DSSSCLotteryNumAlg.getBetNoteFromBetNum(betNumStr, lotteryID);//时时彩
                break;
            }
            case 24:
            case 21:
            case 23:
            case 22:
            case 25:
            case 26:
            case 28:
            case 53:
            case 55: {
                betNum = DS11S5LotteryNumAlg.getBetNoteFromBetNum(betNumStr, lotteryID);//11选5
                break;
            }
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 54:
            case 56: {
                betNum = DSK3LotteryNumAlg.getBetNoteFromBetNum(betNumStr, lotteryID);//快三
                break;
            }
            case 41: {
                betNum = DSFFCAlgorithmObject.getBetNoteFromBetNum(betNumStr, lotteryID);
                break;
            }
            case 43:
            case 47:
            case 110:

            case 116:
            case 112:
            case 310:
            case 311:
            case 312:
            case 313:
            case 330:
            case 331:

            case 204: {
                betNum = DSPK10LottrtyNumAlg.getBetNoteFromBetNum(betNumStr, lotteryID);//pk10
                break;
            }
            case 1000: {

            }

            case 9999: {

                break;
            }
            case 7777: {

                break;
            }
            default:
                break;
        }


        return betNum;
    }


    /**
     * 获取注数  输入
     */
    public static long getPutInBetNoteFromBetNum(String betNumStr, int lotteryID, int ticketID, int needNum) {
        long betNum = 0;
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

                //v2.3新增（9个）
            case 210:
            case 213:
            case 217:
            case 212:
            case 218:
            case 215:
            case 214:
            case 216:
            case 211:

            case 307:
            case 315:
            case 318:

            case 300:
            case 301:
            case 302:
            case 303:
            case 308:
            case 319:
            case 320:
            case 321:
            case 322:
            case 325:
            case 326:
            case 327:
            case 350:
            case 351:
            case 352:
            case 353:
            case 354:
            case 355:

            case 42: {
                betNum = DSSSCLotteryNumAlg.getPutInBetNoteFromBetNum(betNumStr, lotteryID, needNum);
                break;
            }
            case 24:
            case 25:
            case 21:
            case 23:
            case 22:
            case 26:
            case 28: {
                betNum = DS11S5LotteryNumAlg.getPutInBetNoteFromBetNum(betNumStr, lotteryID, needNum);
                break;
            }
            case 31:
            case 32:
            case 33:
            case 35:
            case 36: {
                betNum = DS11S5LotteryNumAlg.getPutInBetNoteFromBetNum(betNumStr, lotteryID, needNum);
                break;
            }
            case 41: {
                betNum = DSFFCAlgorithmObject.getPutInBetNoteFromBetNum(betNumStr, lotteryID, needNum);
                break;
            }
            case 43:
            case 47:
            case 110:

            case 116:
            case 112:
            case 310:
            case 311:
            case 312:
            case 313:
            case 330:
            case 331:

            case 204: {
                betNum = DSPK10LottrtyNumAlg.getPutInBetNoteFromBetNum(betNumStr, lotteryID, needNum);
                break;
            }


            default:
                break;
        }


        return betNum;
    }

    /**
     * ,分割value字符串.
     *
     * @param betNumStr
     * @param lotteryID
     * @param ticketID
     * @param needPowerNum
     * @return
     */
    public static long getBetTimesFromPower(String betNumStr, int lotteryID, int ticketID, int needPowerNum) {
        long betNum = 0;
        switch (ticketID) {
            case 204: {

                break;
            }
            default:
                int len = betNumStr.split(",").length;
                if (len < needPowerNum) {
                    betNum = 0;
                } else {
                    betNum = DSAlgorithmObject.getBetNumCTotalNum(len, needPowerNum);
                }
                break;
        }


        return betNum;
    }


    /**
     * 单式获得正确的投注号
     */
    public static String[] getCorrectArray(String betNumStr, int lotteryID, int ticketID, int needNum) {
        String[] betArray = null;
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

                //v2.3新增（9个）
            case 210:
            case 213:
            case 217:
            case 212:
            case 218:
            case 215:
            case 214:
            case 216:
            case 211:
            case 307:
            case 315:
            case 318:

            case 300:
            case 301:
            case 302:
            case 303:
            case 308:
            case 319:
            case 320:
            case 321:
            case 322:
            case 325:
            case 326:
            case 327:
            case 350:
            case 351:
            case 352:
            case 353:
            case 354:
            case 355:

            case 42: {
                betArray = DSSSCLotteryNumAlg.putInCorrectArray(betNumStr, lotteryID, needNum);
                break;
            }
            case 24:
            case 25:
            case 21:
            case 23:
            case 22:
            case 26:
            case 28: {
                betArray = DS11S5LotteryNumAlg.putInCorrectArray(betNumStr, lotteryID, needNum);
                break;
            }
            case 41: {
                betArray = DSFFCAlgorithmObject.putInCorrectArray(betNumStr, lotteryID, needNum);
                break;
            }
            case 43:
            case 47:
            case 110:

            case 116:
            case 112:
            case 310:
            case 311:
            case 312:
            case 313:
            case 330:
            case 331:
            case 204: {
                betArray = DSPK10LottrtyNumAlg.putInCorrectArray(betNumStr, lotteryID, needNum);
                break;
            }
            default:
                break;
        }


        return betArray;
    }

    /**
     * 自动过滤正确的投注
     */
    public static String getCorrectStr(String betNumStr, int lotteryID, int ticketID) {
        String numStr = betNumStr;
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
                //v2.3新增（9个）
            case 210:
            case 213:
            case 217:
            case 212:
            case 218:
            case 215:
            case 214:
            case 216:
            case 211:
            case 307:
            case 315:
            case 318:

            case 300:
            case 301:
            case 302:
            case 303:
            case 308:
            case 319:
            case 320:
            case 321:
            case 322:
            case 325:
            case 326:
            case 327:
            case 350:
            case 351:
            case 352:
            case 353:
            case 354:
            case 355:

            case 42: {
                numStr = DSSSCLotteryNumAlg.getSSCCorrectStrFromStr(betNumStr, lotteryID);
                break;
            }
            case 24:
            case 25:
            case 21:
            case 23:
            case 22:
            case 26:
            case 28: {
                numStr = DS11S5LotteryNumAlg.get11S5CorrectStrFromStr(betNumStr, lotteryID)
                ;
                break;
            }
            case 41: {
                numStr = DSFFCAlgorithmObject.getFFCCorrectStrFromStr(betNumStr, lotteryID)
                ;
                break;
            }
            case 43:
            case 47:
            case 110:

            case 116:
            case 112:
            case 310:
            case 311:
            case 312:
            case 313:
            case 330:
            case 331:
            case 204: {
                numStr = DSPK10LottrtyNumAlg.getPKCorrectStrFromStr(betNumStr, lotteryID);
                break;
            }

            default:
                break;
        }
        return numStr;
    }


    /**
     * 获取正确的投注
     */
    public static String getCorrectBetNumStr(String betNumStr, int lotteryID, int ticketID) {
        String numStr = betNumStr;
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

                //v2.3新增（9个）
            case 210:
            case 213:
            case 217:
            case 212:
            case 218:
            case 215:
            case 214:
            case 216:
            case 211:

            case 307:
            case 315:
            case 318:

            case 42: {
                numStr = numStr.replace(" ", "");
                break;
            }


            //11选5
            case 24:
            case 25:
            case 21:
            case 23:
            case 22:
            case 26:
            case 28: {

                break;
            }
            case 41: {
                numStr = numStr.replace(" ", "");
                break;
            }

            default:
                break;
        }

        return numStr;
    }


    //获取正确的投注  最后的处理  输入
    public static String getPutCorrectBetNumStr(String betNumStr, int lotteryID, int ticketID, int needNum) {
        String numStr = betNumStr;
        switch (ticketID) {
            //11选5
            case 24:
            case 25:
            case 21:
            case 23:
            case 22:
            case 26:
            case 28:

                //PK
            case 43:
            case 47:
            case 110:

            case 116:
            case 112:
            case 310:
            case 311:
            case 312:
            case 313:
            case 315:

            case 204: {
                numStr = DS11S5LotteryNumAlg.getPutCorrectBetNumStr(betNumStr, lotteryID, ticketID, needNum);
                break;
            }

            default:
                break;
        }

        return numStr;
    }


    public static void main(String[] args) {
        long i;
        i = getBetTimesFromPower("1,2,3", 0, 0, 3);
        i = getBetTimesFromPower("1,2,3,4", 0, 0, 3);
        i = getBetTimesFromPower("1,2,3,4,5,", 0, 0, 3);
    }
}


