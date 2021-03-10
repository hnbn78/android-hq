package com.desheng.app.toucai.controller;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ab.util.Toasts;
import com.desheng.app.toucai.panel.BaseLotteryPlayFragmentLHC;
import com.shark.tc.R;

public class LHCZhengMaQuickPlayController extends LHCQuickPlayController implements View.OnClickListener {
    private int[] ids = {
            R.id.btn_da,
            R.id.btn_xiao,
            R.id.btn_dan,
            R.id.btn_shuang,
            R.id.btn_shu,
            R.id.btn_long,
            R.id.btn_hou,
            R.id.btn_niu,
            R.id.btn_she,
            R.id.btn_ji,
            R.id.btn_hu,
            R.id.btn_ma,
            R.id.btn_gou,
            R.id.btn_tu,
            R.id.btn_yang,
            R.id.btn_zhu,
    };
    int[][][] selections = {
//            R.id.btn_da,
            {
                    {
                            25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49
                    },
            },
//            R.id.btn_xiao,
            {
                    {
                            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24
                    },
            },
//            R.id.btn_dan,
            {
                    {
                            1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35, 37, 39, 41, 43, 45, 47, 49
                    },
            },
//            R.id.btn_shuang,
            {
                    {
                            2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48
                    },
            },
//            R.id.btn_shu,
            {
                    {
                            12,24,36,48
                    },
            },
//            R.id.btn_long,
            {
                    {
                            8,20,32,44
                    },
            },
//            R.id.btn_hou,
            {
                    {
                            4,16,28,40
                    },
            },
//            R.id.btn_niu,
            {
                    {
                            11,23,35,47
                    },
            },
//            R.id.btn_she,
            {
                    {
                            7,19,31,43
                    },
            },
//            R.id.btn_ji,
            {
                    {
                            3,15,27,39
                    },
            },
//            R.id.btn_hu,
            {
                    {
                            10,22,34,46
                    },
            },
//            R.id.btn_ma,
            {
                    {
                            6,18,30,42
                    },
            },
//            R.id.btn_gou,
            {
                    {
                            2,14,26,38
                    },
            },
//            R.id.btn_tu,
            {
                    {
                            9,21,33,45
                    },
            },
//            R.id.btn_yang,
            {
                    {
                            5,17,29,41
                    },
            },
//            R.id.btn_zhu,
            {
                    {
                            1,13,25,37,49
                    },
            },
    };
    Button[] btns = new Button[ids.length];
    int currBtnPos = -1;

    public LHCZhengMaQuickPlayController(BaseLotteryPlayFragmentLHC fragment) {
        super(fragment);
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

        for (int i = 0; i < frag.getListBallGroupsLHC().size(); i++) {
            boolean[] arrBallSelected = frag.getListBallGroupsLHC().get(i).getArrBallSelected();

            for (int j = 0; j < arrBallSelected.length; j++) {
                arrBallSelected[j] = false;
            }

            try {
                for (int j = 0; j < selections[currBtnPos][i].length; j++) {
                    arrBallSelected[selections[currBtnPos][i][j] - 1] = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        frag.syncSelection();
        btns[currBtnPos].setSelected(false);
        currBtnPos = -1;
        return true;
    }

    @Override
    public void buildUI(ViewGroup parent) {
        View root = View.inflate(parent.getContext(), R.layout.view_lhc_quick_play_zhengma, null);

        for (int i = 0; i < ids.length; i++) {
            btns[i] = root.findViewById(ids[i]);
        }

        for (int i = 0; i < btns.length; i++) {
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
