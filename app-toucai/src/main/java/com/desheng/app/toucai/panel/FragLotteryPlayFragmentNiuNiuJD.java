package com.desheng.app.toucai.panel;

import android.view.View;

import com.shark.tc.R;

/**
 * 彩票玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayFragmentNiuNiuJD extends FragLotteryPlayFragmentDigitsJD {
    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_niuniu_jd;
    }

    @Override
    public void createPlayModel(View root) {
        mode = MODE_Dual_2;
        super.createPlayModel(root);
    }
}
