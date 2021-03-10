package com.desheng.app.toucai.panel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.ab.util.ArraysAndLists;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.pearl.act.base.AbBaseFragment;
import com.shark.tc.R;

public class FragBetLottery extends AbBaseFragment {

    public static Fragment newIns(int kind) {
        FragBetLottery fragBetLottery = new FragBetLottery();
        Bundle arg = new Bundle();
        arg.putInt("kind", kind);
        fragBetLottery.setArguments(arg);
        return fragBetLottery;
    }

    private Fragment curr;
    private int id;

    @Override
    public int getLayoutId() {
        return R.layout.frag_bet_lottery;
    }

    @Override
    public void init(View root) {
        id = getArguments().getInt("kind");
        loadLottery(id);
    }

    public void loadLottery(int lotteryID) {
        ILotteryKind kind = CtxLottery.getIns().findLotteryKind(lotteryID);

        if (kind == null)
            return;

        if (getFragmentManager() == null) {
            return;
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (curr != null) {
            transaction.remove(curr);
            curr = null;
        }

        if (ArraysAndLists.findIndexWithEqualsOfArray(kind.getOriginType().getCode(), new String[]{"jd"}) != -1) {
            curr = FragLotteryPlayMainJD.newIns(kind.getId(), (kind1, method, name) -> {
                FragBetLottery.this.id = kind1.getId();
                ((ActLotteryMain) context).onLotteryMethodChanged(kind1, method, name);
            }, id -> {
                loadLottery(id);
            });
        } else if (ArraysAndLists.findIndexWithEqualsOfArray(kind.getOriginType().getCode(), new String[]{"lhc"}) != -1) {
            curr = FragLotteryPlayMainLHC.newIns(kind.getId(), (kind1, method, name) -> {
                FragBetLottery.this.id = kind1.getId();
                ((ActLotteryMain) context).onLotteryMethodChanged(kind1, method, name);
            });
        } else {
            curr = FragLotteryPlayMain.newIns(kind.getId(), new OnLotteryMethodChanged() {
                @Override
                public void onLotteryMethodChanged(ILotteryKind kind1, String method, String name) {
                    FragBetLottery.this.id = kind1.getId();
                    ((ActLotteryMain) context).onLotteryMethodChanged(kind1, method, name);
                }
            }, new OnLotteryKindChanged() {
                @Override
                public void onLotteryKindChanged(int id) {
                    loadLottery(id);
                }
            });
        }

        transaction.replace(R.id.layout_frag_bet_lottery, curr);
        transaction.commitAllowingStateLoss();

    }

    public void showMenu() {
        if (curr instanceof LotteryPlayPanelBase)
            ((LotteryPlayPanelBase) curr).showMenu();
    }

    public void setDefaultPlay() {
        if (curr instanceof LotteryPlayPanelBase){
            ((LotteryPlayPanelBase) curr).setDefaultPlay();
        }
    }

    interface OnLotteryKindChanged {
        void onLotteryKindChanged(int id);
    }

    interface OnLotteryMethodChanged {
        void onLotteryMethodChanged(ILotteryKind kind, String method, String methodName);
    }
}
