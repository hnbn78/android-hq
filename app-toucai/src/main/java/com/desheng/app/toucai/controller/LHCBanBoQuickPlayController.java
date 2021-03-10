package com.desheng.app.toucai.controller;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ab.util.Toasts;
import com.desheng.app.toucai.panel.FragLotteryPlayFragmentBoardLHC;
import com.shark.tc.R;

public class LHCBanBoQuickPlayController extends LHCQuickPlayController implements View.OnClickListener {
    int[] ids = {
            R.id.btn_da,
            R.id.btn_xiao,
            R.id.btn_dan,
            R.id.btn_shuang,
            R.id.btn_hedan,
            R.id.btn_heshuang,
            R.id.btn_hongbo,
            R.id.btn_lvbo,
            R.id.btn_lanbo
    };
    int[][] selections = {
//            R.id.btn_da,
            {
                    1,0,0,0,0,0,
                    1,0,0,0,0,0,
                    1,0,0,0,0,0
            },
//            R.id.btn_xiao,
            {
                    0,1,0,0,0,0,
                    0,1,0,0,0,0,
                    0,1,0,0,0,0
            },
//            R.id.btn_dan,
            {
                    0,0,1,0,0,0,
                    0,0,1,0,0,0,
                    0,0,1,0,0,0
            },
//            R.id.btn_shuang,
            {
                    0,0,0,1,0,0,
                    0,0,0,1,0,0,
                    0,0,0,1,0,0
            },
//            R.id.btn_hedan,
            {
                    0,0,0,0,1,0,
                    0,0,0,0,1,0,
                    0,0,0,0,1,0
            },
//            R.id.btn_heshuang,
            {
                    0,0,0,0,0,1,
                    0,0,0,0,0,1,
                    0,0,0,0,0,1
            },
//            R.id.btn_hongbo,
            {
                    1,1,1,1,1,1,
                    0,0,0,0,0,0,
                    0,0,0,0,0,0
            },
//            R.id.btn_lvbo,
            {
                    0,0,0,0,0,0,
                    1,1,1,1,1,1,
                    0,0,0,0,0,0
            },
//            R.id.btn_lanbo
            {
                    0,0,0,0,0,0,
                    0,0,0,0,0,0,
                    1,1,1,1,1,1
            },
    };

    Button[] btns = new Button[ids.length];
    int currBtnPos = -1;

    private FragLotteryPlayFragmentBoardLHC frag;
    public LHCBanBoQuickPlayController(FragLotteryPlayFragmentBoardLHC fragment) {
        super(fragment);
        frag = fragment;
    }

    @Override
    public void onCancel() {
        try {
            btns[currBtnPos].setSelected(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        currBtnPos = -1;
    }

    @Override
    public boolean onConfirm() {
        if (currBtnPos < 0) {
            Toasts.show(frag.getContext(), "请选择快捷方式", false);
            return false;
        }

        boolean[] arrBallSelected = frag.getBoardGroup().getArrBallSelected();
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
        View root = View.inflate(parent.getContext(), R.layout.view_lhc_quick_play_banbo, null);

        for (int i = 0; i < ids.length; i++) {
            btns[i] = root.findViewById(ids[i]);
        }

        for (int i = 0; i < btns.length; i++) {
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
