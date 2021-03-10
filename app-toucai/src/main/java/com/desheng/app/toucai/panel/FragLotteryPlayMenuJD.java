package com.desheng.app.toucai.panel;

import android.animation.Animator;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ab.dialog.AbBaseFullScreenDialogFragment;
import com.ab.global.AbDevice;
import com.ab.util.Strs;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.base.model.LotteryPlayJD;
import com.shark.tc.R;

import java.util.ArrayList;

/**
 * 彩票菜单fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayMenuJD extends AbBaseFullScreenDialogFragment {
    private TableLayout flMine;
    private ArrayList<LotteryPlayJD> listPlayCategory;
    private View vLeft;
    private TextView tvChoose;
    private TextView tvCancell;
    private String category;
    private LotteryPlayJD orignalPlay;
    private LotteryPlayJD currPlay;
    private  LotteryPlayPanelJD lotteryPlayPanel;

    public static FragLotteryPlayMenuJD newIns(AppCompatActivity context, String category, String playCode, LotteryPlayPanelJD lotteryPlayPanel) {
        Fragment old = context.getSupportFragmentManager().findFragmentByTag("FragLotteryPlayMenuJD");

        FragLotteryPlayMenuJD fragment = new FragLotteryPlayMenuJD();
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("playCode", playCode);
        fragment.setArguments(bundle);
        fragment.lotteryPlayPanel = lotteryPlayPanel;

        if (old != null) {
            context.getSupportFragmentManager().beginTransaction()
                    .add(fragment, "FragLotteryPlayMenuJD")
                    .remove(old)
                    .commitAllowingStateLoss();
        } else {
            context.getSupportFragmentManager().beginTransaction()
                    .add(fragment, "FragLotteryPlayMenuJD")
                    .commitAllowingStateLoss();
        }

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        category = getArguments().getString("category");
        String playcode = getArguments().getString("playCode");
        listPlayCategory = UserManagerTouCai.getIns().getLotteryPlayJDMap().get(category);

        for (LotteryPlayJD item : listPlayCategory) {
            if (Strs.isEqual(item.getShowName(), playcode)) {
                orignalPlay = item;
                break;
            }
        }

        if (orignalPlay == null && listPlayCategory.size() > 0)
            orignalPlay = listPlayCategory.get(0);

        currPlay = orignalPlay;
        View root = inflater.inflate(R.layout.frag_lottery_play_menu_jd, null);

        //我的玩法
        flMine = root.findViewById(R.id.flMine);
        updateMyPlay();
        updateSelected();

        //左侧空白
        vLeft = root.findViewById(R.id.vLeft);
        vLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvChoose = root.findViewById(R.id.tvChoose);
        tvChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    lotteryPlayPanel.showPlayAndHideLotteryMenu(category, currPlay.getShowName());
                    dismiss();

                    if (((ActLotteryMain) getActivity()) != null && ((ActLotteryMain) getActivity()).tabLayout != null) {
                        ((ActLotteryMain) getActivity()).tabLayout.getTabAt(1).select();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvCancell = root.findViewById(R.id.tvCancell);
        tvCancell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPlay = orignalPlay;
                updateSelected();
            }
        });

        View b = root.findViewById(R.id.background);
        b.setAlpha(0);
        b.animate().alpha(1);
        View c = root.findViewById(R.id.layout_container);
        c.setTranslationX(AbDevice.SCREEN_WIDTH_PX);
        c.animate().translationX(0);

        return root;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getContext(), getTheme()) {
            @Override
            public void cancel() {
                getView().post(() -> {
                    getView().findViewById(R.id.background).animate().alpha(0);
                    View c = getView().findViewById(R.id.layout_container);
                    c.setTranslationX(0);
                    c.animate().translationX(getView().getWidth()).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            getDialog().dismiss();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            getDialog().dismiss();
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                });
            }
        };
    }

    @Override
    public void dismiss() {
        getView().post(() -> {
            getView().findViewById(R.id.background).animate().alpha(0);
            View c = getView().findViewById(R.id.layout_container);
            c.setTranslationX(0);
            c.animate().translationX(getView().getWidth()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    FragLotteryPlayMenuJD.super.dismiss();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    FragLotteryPlayMenuJD.super.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        });
    }
    
    private void updateMyPlay() {
        for (int i = 0; i < listPlayCategory.size(); i += 3) {
            TableRow tableRow = new TableRow(getActivity());
            for (int j = 0; j < 3 && i + j < listPlayCategory.size(); j++) {
                String categorylottery = listPlayCategory.get(i + j).getShowName();
                TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_lottery_play_menu_choose_item, tableRow, false);
                tv.setText(categorylottery);
                tableRow.addView(tv);
                tv.setTag(listPlayCategory.get(i + j));
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currPlay = (LotteryPlayJD) v.getTag();
                        updateSelected();
                    }
                });
            }
            flMine.addView(tableRow);
        }
    }

    private void updateSelected() {
        for (int i = 0; i < flMine.getChildCount(); i++) {
            TableRow row = (TableRow) flMine.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                View child = row.getChildAt(j);
                if (currPlay != null && child.getTag() == currPlay) {
                    child.setSelected(true);
                } else {
                    child.setSelected(false);
                }
            }
        }
    }
}
