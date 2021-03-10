package com.desheng.app.toucai.controller;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ab.util.Toasts;
import com.desheng.app.toucai.panel.FragLotteryPlayFragmentZodiacLHC;
import com.shark.tc.R;

public class LHCZodiacQuickPlayController extends LHCQuickPlayController implements View.OnClickListener {
    int[] ids = {
            R.id.btn_da,
            R.id.btn_xiao,
            R.id.btn_nan,
            R.id.btn_nv,
            R.id.btn_jimei,
            R.id.btn_xiongchou,
            R.id.btn_yin,
            R.id.btn_yang,
            R.id.btn_yewai,
            R.id.btn_jianei,
            R.id.btn_wuxing_jin,
            R.id.btn_wuxing_mu,
            R.id.btn_wuxing_shui,
            R.id.btn_wuxing_huo,
            R.id.btn_wuxing_tu
    };
    Button[] btns = new Button[ids.length];
    int[][] selections = {
//            R.id.btn_da,
            {
                    1, 1, 1, 1, 1, 1,
                    0, 0, 0, 0, 0, 0,
            },
//            R.id.btn_xiao,
            {
                    0, 0, 0, 0, 0, 0,
                    1, 1, 1, 1, 1, 1,
            },
//            R.id.btn_nan,
            {
                    1, 1, 1, 0, 1, 0,
                    1, 0, 1, 0, 1, 0,
            },
//            R.id.btn_nv,
            {
                    0, 0, 0, 1, 0, 1,
                    0, 1, 0, 1, 0, 1
            },
//            R.id.btn_jimei,
            {
                    0, 0, 0, 1, 1, 1,
                    1, 1, 0, 1, 0, 0
            },
//            R.id.btn_xiongchou,
            {
                    1, 1, 1, 0, 0, 0,
                    0, 0, 1, 0, 1, 1
            },
//            R.id.btn_yin,
            {
                    1, 0, 0, 0, 1, 1,
                    1, 0, 0, 0, 1, 1
            },
//            R.id.btn_yang,
            {
                    0, 1, 1, 1, 0, 0,
                    0, 1, 1, 1, 0, 0
            },
//            R.id.btn_yewai,
            {
                    1, 0, 1, 1, 1, 1,
                    0, 0, 1, 0, 0, 0
            },
//            R.id.btn_jianei,
            {
                    0, 1, 0, 0, 0, 0,
                    1, 1, 0, 1, 1, 1
            },
//            R.id.btn_wuxing_jin,
            {
                    0, 0, 0, 0, 0, 0,
                    0, 0, 1, 1, 0, 0
            },
//            R.id.btn_wuxing_mu,
            {
                    0, 0, 1, 1, 0, 0,
                    0, 0, 0, 0, 0, 0
            },
//            R.id.btn_wuxing_shui,
            {
                    1, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 1
            },
//            R.id.btn_wuxing_huo,
            {
                    0, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 0
            },
//            R.id.btn_wuxing_tu
            {
                    0, 1, 0, 0, 1, 0,
                    0, 1, 0, 0, 1, 0
            },
    };

    FragLotteryPlayFragmentZodiacLHC frag;

    int currBtnPos = -1;

    public LHCZodiacQuickPlayController(FragLotteryPlayFragmentZodiacLHC fragment) {
        super(fragment);
        this.frag = fragment;
    }

    @Override
    public void onCancel() {
        try {
            btns[currBtnPos].setSelected(false);
        } catch (Exception e) {e.printStackTrace();}
        currBtnPos = -1;
    }

    @Override
    public boolean onConfirm() {
        if (currBtnPos < 0) {
            Toasts.show(frag.getContext(), "请选择快捷方式", false);
            return false;
        }

        boolean[] arrBallSelected = frag.getPlayZodiacLHCView().getArrBallSelected();
        try {
            for (int i = 0; i < arrBallSelected.length; i++) {
                arrBallSelected[i] = selections[currBtnPos][i] == 1;
            }
        } catch (Exception e) {e.printStackTrace();}
        frag.syncSelection();
        btns[currBtnPos].setSelected(false);
        currBtnPos = -1;
        return true;
    }

    @Override
    public void buildUI(ViewGroup parent) {
        View root = View.inflate(parent.getContext(), R.layout.view_lhc_quick_play_zodiac, null);

        for (int i = 0; i < ids.length; i++) {
            btns[i] = root.findViewById(ids[i]);
        }
        for (int i = 0; i < ids.length; i++) {
            final int pos = i;
            btns[i].setOnClickListener(this);
        }

        parent.addView(root);
    }


    @Override
    public void onClick(View v) {
        for (Button b : btns) {
            if (v != b)
                b.setSelected(false);
            else
                b.setSelected(!b.isSelected());
        }
        if (v.isSelected()) {
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == v.getId()) {
                    currBtnPos = i;
                    break;
                }
            }
        } else {
            currBtnPos = -1;
        }
    }
}
