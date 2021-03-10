package com.desheng.app.toucai.context;

import android.content.Context;

import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.LotteryInfoCustom;
import com.desheng.app.toucai.panel.ActLotteryMain;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.context.ILotteryType;
import com.desheng.base.model.LotteryInfo;

/**
 * Created by lee on 2018/3/5.
 */

public class CtxLotteryTouCai extends CtxLottery{
    
    static {
        CtxLottery.setIns(new CtxLotteryTouCai());
    }
    
    public static void launchLotteryPlay(Context ctx, int id) {
        ILotteryKind lotteryKind = CtxLotteryTouCai.getIns().findLotteryKind(id);
        launchLotteryPlay(ctx, lotteryKind);
    }

    public static void launchLotteryPlay(Context ctx, ILotteryKind kind) {
//        if (ArraysAndLists.findIndexWithEqualsOfArray(kind.getOriginType().getCode(), new String[]{"jd"}) != -1) {
//            ActLotteryPlayJD.launch((Activity) ctx, kind.getId());
//        } else if (ArraysAndLists.findIndexWithEqualsOfArray(kind.getOriginType().getCode(), new String[]{"lhc"}) != -1) {
//            ActLotteryPlayLHC.launch((Activity) ctx, kind.getId());
//        } else {
//            ActLotteryPlay.launch((Activity) ctx, kind.getId());
//        }
        ActLotteryMain.launcher(ctx, kind.getId(), false);
        //统计用户常玩彩种
        //UserManagerTouCai.getIns().setUserChangwanGameData(kind.getId());
    }

    public static void launchLotteryPlay(Context ctx, LotteryInfoCustom kind) {
//        if (ArraysAndLists.findIndexWithEqualsOfArray(kind.getOriginType().getCode(), new String[]{"jd"}) != -1) {
//            ActLotteryPlayJD.launch((Activity) ctx, kind.getId());
//        } else if (ArraysAndLists.findIndexWithEqualsOfArray(kind.getOriginType().getCode(), new String[]{"lhc"}) != -1) {
//            ActLotteryPlayLHC.launch((Activity) ctx, kind.getId());
//        } else {
//            ActLotteryPlay.launch((Activity) ctx, kind.getId());
//        }
        ActLotteryMain.launcher(ctx, kind.getId(), false);
        //统计用户常玩彩种
        //UserManagerTouCai.getIns().setUserChangwanGameData(kind);
    }

    public static void launchLotteryPlay(Context ctx, LotteryInfo kind) {
//        if (ArraysAndLists.findIndexWithEqualsOfArray(kind.getOriginType().getCode(), new String[]{"jd"}) != -1) {
//            ActLotteryPlayJD.launch((Activity) ctx, kind.getId());
//        } else if (ArraysAndLists.findIndexWithEqualsOfArray(kind.getOriginType().getCode(), new String[]{"lhc"}) != -1) {
//            ActLotteryPlayLHC.launch((Activity) ctx, kind.getId());
//        } else {
//            ActLotteryPlay.launch((Activity) ctx, kind.getId());
//        }
        ActLotteryMain.launcher(ctx, kind.getId(), false);
        //统计用户常玩彩种
        //UserManagerTouCai.getIns().setUserChangwanGameData(kind);
    }
    
    @Override
    public ILotteryKind findLotteryKind(int id) {
        return LotteryKind.find(id);
    }
    
    @Override
    public ILotteryKind lotteryKind(String value) {
        return LotteryKind.valueOf(value);
    }
    
    @Override
    public ILotteryType findLotteryType(String code) {
        return LotteryType.find(code);
    }
    
    @Override
    public ILotteryType lotteryType(String value) {
        return LotteryType.valueOf(value);
    }
    
 
    
   
}
