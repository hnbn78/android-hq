package com.desheng.base.util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.global.Config;
import com.ab.http.AbHttpResult;
import com.ab.util.Toasts;
import com.desheng.base.action.HttpAction;
import com.desheng.base.action.ILoadDataCallback;
import com.desheng.base.manager.UserManager;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;

import java.util.Arrays;
import java.util.HashMap;

/**
 * 虚拟币的配置
 */
public class CoinHelper {

    public static final int RMB = 1;
    public static final int DC = 6;
    public static final int ETH = 2;
    public static final int BCB = 3;
    public static final int USDX = 4;
    public int currentCurrency = RMB;
    /*是否有虚拟币的功能*/
    public boolean hasCoin; // virtual currency
    public static String[] otcArgs = null;

    private CoinHelper() {
        hasCoin = hasCoin();
    }

    private static class CurrencyHolder {
        private static final CoinHelper INSTANCE = new CoinHelper();
    }

    public static CoinHelper getInstance() {
        return CurrencyHolder.INSTANCE;
    }


    public static void setOtcArgs(String[] args) {
        otcArgs = args;
    }

    /**
     * @return 是否有虚拟币的功能
     * 要增加 虚拟币功能，只需增加对应的flag
     * FIX 如果虚拟币要推广到所有平台，则配置应转移到ConfigZhongDa文件中
     */
    private static boolean hasCoin() {//type1: 完全开放虚拟币,金源模板
        String[] flags =
                {
//                        BaseConfig.FLAG_JINYUAN,
//                        BaseConfig.FLAG_HUAYI
                };
        return Arrays.asList(flags).contains(Config.custom_flag);
    }

    //虚拟币充值记录
    public static boolean hasRecord() {//type2: 隐藏虚拟币,可以虚拟币充值,有虚拟币充值记录
        String[] flags =
                {
//                        BaseConfig.FLAG_A20,
//                        BaseConfig.FLAG_ANTAI,
//                        BaseConfig.FLAG_JINYANG,
//                        BaseConfig.FLAG_QIFEI,
//                        BaseConfig.FLAG_HUAYI,
//                        BaseConfig.FLAG_WANSHANG,
//                        BaseConfig.FLAG_YINZHU,
//                        BaseConfig.FLAG_ZHONGDA,
//                        BaseConfig.FLAG_1980,
//                        BaseConfig.FLAG_JINAO,
//                        BaseConfig.FLAG_JINFENG
                };
        return Arrays.asList(flags).contains(Config.custom_flag);
    }

    public void resetCurrency(int type, ILoadDataCallback callback) {
        if (currentCurrency == type) return;
        currentCurrency = type;
        switchCoin(currentCurrency, callback);
    }

    private void switchCoin(final int id, final ILoadDataCallback callback) {
        HttpAction.switchCoin(this, id, new AbHttpResult() {
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    if (callback != null)
                        callback.onLoadDone();
                } else
                    Toasts.show(msg, false);
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(content, false);
                return super.onError(status, content);
            }
        });
    }

    public static double getBalanceWithId(int id) {
        switch (id) {
//            case DC:
//                return UserManager.getIns().getDCBalance();
//            case ETH:
//                return UserManager.getIns().getETHBalance();
//            case BCB:
//                return UserManager.getIns().getBCBBalance();
//            case USDX:
//                return UserManager.getIns().getUSDXBalance();
            default:
                return UserManager.getIns().getLotteryAvailableBalance();
        }
    }

    /**
     * @return 根据ID获取币种的单位
     */
    public static String getUnitWithId(int id) {
        switch (id) {
            case DC:
                //   return "\uD83D\uDC8E DC分";
                return "DC分";
            case ETH:
                return "ETH";
            case BCB:
                return "BCB";
            case USDX:
                return "USDX";
            default:
                return "元";
        }
    }

    public static String getCoinCode(int id) {
        switch (id) {
            case DC:
                return "DC";
            case ETH:
                return "ETH";
            case BCB:
                return "BCB";
            case USDX:
                return "USDX";
            default:
                return "RMB";
        }
    }

    public static String getCoinName(String code) {
        switch (code) {
            case "DC":
                return "钻石币";
            case "ETH":
                return "莱特币";
            case "BCB":
                return "比特币";
            case "USDX":
                return "美元币";
            default:
                return "";
        }
    }

    public static Drawable getCoinDrawable(String code) {
        switch (code) {
//            case "DC":
//                return ResUtil.getDrawable(R.mipmap.ic_bc);
//            case "BCB":
//                return ResUtil.getDrawable(R.mipmap.ic_bcb);
//            case "USDX":
//                return ResUtil.getDrawable(R.mipmap.ic_usdx);
//            case "ETH":
//                return ResUtil.getDrawable(R.mipmap.ic_eth);
            default:
                return null;
        }
    }


    /**
     * @param bitView  bitTextView
     * @param callback 回调接口
     *                 增加币种切换只需调用此函数，传入bit视图
     */

    public static void createCoinPopup(final TextView bitView, final ICallbackCoinType callback) {
        bitView.setVisibility(View.VISIBLE);
        if (CoinHelper.otcArgs == null) {
            String[] args = {"人民币"};
            CoinHelper.setOtcArgs(args);
        }
        final ListMenuPopupWindow coinPopup = new ListMenuPopupWindow((Activity) bitView.getContext(), AbDevice.SCREEN_WIDTH_PX, CoinHelper.otcArgs);
        coinPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                String coin = CoinHelper.otcArgs[position];
                bitView.setText(coin);
                String otcType = coin.equals("人民币") ? "RMB" : coin;
                if (callback != null) {
                    callback.callback(otcType);
                }
            }
        });
        bitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coinPopup.showAsDropDown(bitView, 0, 0);//显示在rl_spinner的下方
            }
        });
    }

    public interface ICallbackCoinType {
        void callback(String otcType);
    }
}

