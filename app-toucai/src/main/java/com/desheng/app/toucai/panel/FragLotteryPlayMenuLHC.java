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
import android.widget.TextView;

import com.ab.dialog.AbBaseFullScreenDialogFragment;
import com.ab.global.AbDevice;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryPlayLHCCategory;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.shark.tc.R;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 彩票菜单fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayMenuLHC extends AbBaseFullScreenDialogFragment {
    private View vLeft;
    private FlowLayout flMine;
    private LotteryPlayLHCUI listPlayCategory;
    private ArrayList<String> listPlayed;
    private String category;
    private String playCode;
    private FlowLayout flOther;
    private TextView tvChoose;
    private TextView tvCancell;
    private LotteryPlayLHCCategory currCatgory;
    private LotteryPlayLHCCategory.DataBean currPlay;
    private List<LotteryPlayLHCCategory> categories;
    private LotteryPlayPanelLHC lotteryPlayPanel;
    
    public static void showIns(AppCompatActivity ctx, String category, String playCode, LotteryPlayPanelLHC lotteryPlayPanel) {
        Fragment old = ctx.getSupportFragmentManager().findFragmentByTag("FragLotteryPlayMenuLHC");

        FragLotteryPlayMenuLHC curr = new FragLotteryPlayMenuLHC();
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("playCode", playCode);
        curr.setArguments(bundle);
        curr.lotteryPlayPanel = lotteryPlayPanel;
//        curr.show(ctx.getSupportFragmentManager(), "FragLotteryPlayMenuLHC");

        if (old != null) {
            ctx.getSupportFragmentManager().beginTransaction()
                    .add(curr, "FragLotteryPlayMenuLHC")
                    .remove(old)
                    .commitAllowingStateLoss();
        } else {
            ctx.getSupportFragmentManager().beginTransaction()
                    .add(curr, "FragLotteryPlayMenuLHC")
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getArguments().getString(("category"));
        playCode = getArguments().getString(("playCode"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        categories = UserManager.getIns().getListLotteryPlayLHCCategory();
        currCatgory = UserManager.getIns().getLotteryPlayLHCCategory(category);
        currPlay = UserManager.getIns().getLotteryPlayLHCCategoryDataBeanByPlayCode(category, playCode);

        View root = inflater.inflate(R.layout.frag_lottery_play_menu_lhc, null);
        //我的玩法
        flMine = (FlowLayout) root.findViewById(R.id.flMine);
        updateMyPlay();
        
        //玩法列表
        flOther = (FlowLayout) root.findViewById(R.id.flOther);
        updateOtherPlay();
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
                    lotteryPlayPanel.showPlayAndHideLotteryMenu(currCatgory.getTitleName(), currPlay.getLotteryCode());
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        tvCancell = root.findViewById(R.id.tvCancell);
        tvCancell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currCatgory = UserManager.getIns().getLotteryPlayLHCCategory(category);
                currPlay = UserManager.getIns().getLotteryPlayLHCCategoryDataBeanByPlayCode(category, playCode);
                updateOtherPlay();
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
                    FragLotteryPlayMenuLHC.super.dismiss();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    FragLotteryPlayMenuLHC.super.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        });
    }
    
    private void updateMyPlay() {
        flMine.removeAllViews();

        for (final LotteryPlayLHCCategory category : categories) {
                TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_lhc_play_choose_item, flMine, false);
                tv.setText(category.getTitleName());
                flMine.addView(tv);
                tv.setTag(category);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LotteryPlayLHCCategory category = (LotteryPlayLHCCategory) v.getTag();
                        currCatgory = category;
                        if (category.getData().size() > 0)
                            currPlay = category.getData().get(0);

                        updateOtherPlay();
                        updateSelected();
                    }
                });
        }
    }
    
    private void updateOtherPlay() {
        flOther.removeAllViews();

        if (currCatgory != null) {
            List<LotteryPlayLHCCategory.DataBean> dataBeans = currCatgory.getData();

            for (LotteryPlayLHCCategory.DataBean bean : dataBeans) {
                TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_lhc_play_choose_item, flOther, false);
                tv.setText(bean.getShowName());
                flOther.addView(tv);
                tv.setTag(bean);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LotteryPlayLHCCategory.DataBean bean = (LotteryPlayLHCCategory.DataBean) v.getTag();
                        currPlay = bean;
                        updateSelected();
                    }
                });
            }
        }
    }

    private void updateSelected() {
        for (int i = 0; i < flMine.getChildCount(); i++) {
            View child = flMine.getChildAt(i);
            if (currCatgory != null && child.getTag() == currCatgory) {
                child.setSelected(true);
            } else {
                child.setSelected(false);
            }
        }

        for (int i = 0; i < flOther.getChildCount(); i++) {
            View child = flOther.getChildAt(i);
            if (currPlay != null && child.getTag() == currPlay) {
                child.setSelected(true);
            } else {
                child.setSelected(false);
            }
        }
    }
}
