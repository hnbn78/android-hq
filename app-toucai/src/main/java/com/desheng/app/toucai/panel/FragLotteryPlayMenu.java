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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ab.dialog.AbBaseFullScreenDialogFragment;
import com.ab.global.AbDevice;
import com.ab.util.Strs;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.base.model.LotteryPlayConfigCategoryTouCai;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.shark.tc.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 彩票菜单fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayMenu extends AbBaseFullScreenDialogFragment {
    private TableLayout flMine;
    private View vLeft;
    private ViewGroup secondaryContainer;
    private TextView tvChoose;
    private TextView tvCancell;
    private LotteryPlayUserInfo userPlayInfo;
    private List<LotteryPlayConfigCategoryTouCai> listPlayCategory;
    private String category;
    private String playcode;
    private int lotteryId;
    private LotteryPlayConfigCategoryTouCai currCatgory;
    private int currLotteryId;
    private LotteryPlayConfigCategoryTouCai.CatBean.DataBean currPlay;
    private LotteryPlayPanel lotteryPlayPanel;

    private Map<LotteryPlayConfigCategoryTouCai.CatBean, View> catViewCache = new HashMap<>();
    private Map<LotteryPlayConfigCategoryTouCai.CatBean.DataBean, View> playViewMap = new HashMap<>();
    private Map<LotteryPlayConfigCategoryTouCai.CatBean, View> groupCatViewMap = new HashMap<>();

    public static FragLotteryPlayMenu newIns(AppCompatActivity context, String category, int lotteryId, String playCode, LotteryPlayUserInfo userPlayInfo, LotteryPlayPanel lotteryPlayPanel) {
        Fragment old = context.getSupportFragmentManager().findFragmentByTag("FragLotteryPlayMenu");

        FragLotteryPlayMenu fragment = new FragLotteryPlayMenu();
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putInt("lotteryId", lotteryId);
        bundle.putString("playCode", playCode);
        bundle.putSerializable("playInfo", userPlayInfo);
        fragment.setArguments(bundle);
        fragment.lotteryPlayPanel = lotteryPlayPanel;

        if (old != null) {
            context.getSupportFragmentManager().beginTransaction()
                    .add(fragment, "FragLotteryPlayMenu")
                    .remove(old)
                    .commitAllowingStateLoss();
        } else {
            context.getSupportFragmentManager().beginTransaction()
                    .add(fragment, "FragLotteryPlayMenu")
                    .commitAllowingStateLoss();
        }

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        category = getArguments().getString("category");
        lotteryId = getArguments().getInt("lotteryId");
        playcode = getArguments().getString("playCode");
        userPlayInfo = (LotteryPlayUserInfo) getArguments().getSerializable("playInfo");
        currLotteryId = lotteryId;

        View root = inflater.inflate(R.layout.frag_lottery_play_menu, null);
        listPlayCategory = UserManagerTouCai.getIns().getCategorys().get(category);

        secondaryContainer = root.findViewById(R.id.layout_secondary_container);
        //我的玩法
        flMine = root.findViewById(R.id.flMine);
        resetPlay();
        updateMyPlay();
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
                if (currCatgory == null || (!currCatgory.isAllInOne() && currPlay == null))
                    return;

                try {
                    if (currCatgory.isAllInOne()) {
                        lotteryPlayPanel.showPlayAndHideLotteryMenu(category, currCatgory.getCat().get(0).getData().get(0).getLotteryCode());
                    } else {
                        lotteryPlayPanel.showPlayAndHideLotteryMenu(category, currPlay.getLotteryCode());
                    }

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
                resetPlay();
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
                    FragLotteryPlayMenu.super.dismiss();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    FragLotteryPlayMenu.super.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        });
    }

    private void updateMyPlay() {
        flMine.removeAllViews();
        int indexOfView = 0;
        TableRow tableRow = null;
        for (int i = 0; i < listPlayCategory.size(); i++) {
            if (indexOfView % 3 == 0) {
                tableRow = new TableRow(getActivity());
                flMine.addView(tableRow);
            }

            if (!checkHasOtherPlay(listPlayCategory.get(i)))
                continue;

            String categorylottery = listPlayCategory.get(i).getTitleName();
            TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_lottery_play_menu_choose_item, tableRow, false);
            tv.setText(categorylottery);
            tableRow.addView(tv);
            indexOfView++;
            tv.setTag(listPlayCategory.get(i));
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LotteryPlayConfigCategoryTouCai category = (LotteryPlayConfigCategoryTouCai) v.getTag();
                    if (currCatgory != category) {
                        currCatgory = category;
                        currPlay = null;
                        updateOtherPlay();
                        updateSelected();
                    }
                }
            });

        }
    }

    private boolean checkHasOtherPlay(LotteryPlayConfigCategoryTouCai lotteryPlayConfigCategoryTouCai) {
        if (lotteryPlayConfigCategoryTouCai.isAllInOne()) {
            List<LotteryPlayConfigCategoryTouCai.CatBean.DataBean> beans = UserManagerTouCai.getIns().getAllInOne()
                    .get(category).get(lotteryPlayConfigCategoryTouCai.getCat().get(0).getData().get(0).getLotteryCode());

            if (beans != null) {
                for (LotteryPlayConfigCategoryTouCai.CatBean.DataBean b : beans) {

                    String lotteryCode = b.getLotteryCode();
                    if (lotteryCode.contains("gflonghuhe")) {
                        if (userPlayInfo.getMethod().get(lotteryCode + "_long") != null
                                || userPlayInfo.getMethod().get(lotteryCode + "_hu") != null
                                || userPlayInfo.getMethod().get(lotteryCode + "_he") != null) {
                            return true;
                        }
                    }

                    if (userPlayInfo.getMethod().get(b.getLotteryCode()) != null)
                        return true;
                }
            }
        } else {
            for (LotteryPlayConfigCategoryTouCai.CatBean cat : lotteryPlayConfigCategoryTouCai.getCat()) {
                for (int i = 0; i < cat.getData().size(); i++) {
                    LotteryPlayConfigCategoryTouCai.CatBean.DataBean dataBean = cat.getData().get(i);

                    if (userPlayInfo.getMethod().get(dataBean.getLotteryCode()) != null)
                        return true;
                }
            }
        }

        return false;
    }

    private void updateOtherPlay() {
        secondaryContainer.removeAllViews();

        if (currCatgory == null)
            return;

        if (currCatgory.isAllInOne())
            return;

        if (currCatgory.isCatGroup()) {
            updateGroupCat();
        } else {
            updateDividCat();
        }


    }

    private void updateGroupCat() {
        ViewGroup root = (ViewGroup) getLayoutInflater().inflate(R.layout.frag_lottery_play_menu_secondary, secondaryContainer, false);
        TextView title = root.findViewById(R.id.tv_category);
        title.setText("位置");
        TableLayout container = root.findViewById(R.id.flOther);
        ViewGroup dataContainer = root.findViewById(R.id.layout_data_container);
        int indexOfView = 0;
        TableRow tableRow = null;
        groupCatViewMap.clear();
        for (int i = 0; i < currCatgory.getCat().size(); i++) {
            LotteryPlayConfigCategoryTouCai.CatBean cat = currCatgory.getCat().get(i);
            if (indexOfView % 3 == 0) {
                tableRow = new TableRow(getActivity());
                container.addView(tableRow);
            }

            if (getFirstAvailableData(cat) == null)
                continue;

            TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_lottery_play_menu_choose_item, tableRow, false);
            tv.setText(cat.getTitleName());
            tableRow.addView(tv);
            indexOfView++;
            tv.setTag(cat);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup layout = createGroupCatData(cat);
                    currPlay = getFirstAvailableData(cat);
                    dataContainer.removeAllViews();
                    dataContainer.addView(layout);
                    updateSelected();
                }
            });
            groupCatViewMap.put(cat, tv);

            if (currPlay == null)
                currPlay = getFirstAvailableData(cat);
        }

        if (currPlay != null) {
            for (int i = 0; i < currCatgory.getCat().size(); i++) {
                for (int j = 0; j < currCatgory.getCat().get(i).getData().size(); j++) {
                    if (currPlay == currCatgory.getCat().get(i).getData().get(j)) {
                        dataContainer.removeAllViews();
                        dataContainer.addView(createGroupCatData(currCatgory.getCat().get(i)));
                    }
                }
            }
        }

        secondaryContainer.addView(root, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private ViewGroup createGroupCatData(LotteryPlayConfigCategoryTouCai.CatBean cat) {
        ViewGroup root = (ViewGroup) getLayoutInflater().inflate(R.layout.frag_lottery_play_menu_secondary, secondaryContainer, false);
        TextView title = root.findViewById(R.id.tv_category);
        title.setText(cat.getTitleName());
        TableLayout container = root.findViewById(R.id.flOther);
        int indexOfView = 0;
        TableRow tableRow = null;
        for (int i = 0; i < cat.getData().size(); i++) {
            LotteryPlayConfigCategoryTouCai.CatBean.DataBean dataBean = cat.getData().get(i);

            if (indexOfView % 3 == 0) {
                tableRow = new TableRow(getActivity());
                container.addView(tableRow);
            }

            if (userPlayInfo.getMethod().get(dataBean.getLotteryCode()) == null)
                continue;

            TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_lottery_play_menu_choose_item, tableRow, false);
            tv.setText(dataBean.getName());
            tableRow.addView(tv);
            indexOfView++;
            tv.setTag(dataBean);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currPlay = (LotteryPlayConfigCategoryTouCai.CatBean.DataBean) v.getTag();
                    updateSelected();
                }
            });

            playViewMap.put(dataBean, tv);

            if (currPlay == null) {
                try {
                    currPlay = dataBean;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return root;
    }

    private void updateDividCat() {
        for (LotteryPlayConfigCategoryTouCai.CatBean cat : currCatgory.getCat()) {
            if (catViewCache.get(cat) == null) {
                View root = getLayoutInflater().inflate(R.layout.frag_lottery_play_menu_secondary, secondaryContainer, false);
                root.setTag(cat);
                TextView title = root.findViewById(R.id.tv_category);
                title.setText(cat.getTitleName());
                TableLayout container = root.findViewById(R.id.flOther);
                int indexOfView = 0;
                TableRow tableRow = null;
                for (int i = 0; i < cat.getData().size(); i++) {
                    LotteryPlayConfigCategoryTouCai.CatBean.DataBean dataBean = cat.getData().get(i);

                    if (indexOfView % 3 == 0) {
                        tableRow = new TableRow(getActivity());
                        container.addView(tableRow);
                    }

                    if (userPlayInfo.getMethod().get(dataBean.getLotteryCode()) == null)
                        continue;

                    TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_lottery_play_menu_choose_item, tableRow, false);
                    tv.setText(dataBean.getName());
                    tableRow.addView(tv);
                    indexOfView++;
                    tv.setTag(dataBean);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currPlay = (LotteryPlayConfigCategoryTouCai.CatBean.DataBean) v.getTag();
                            updateSelected();
                        }
                    });

                    playViewMap.put(dataBean, tv);

                    if (currPlay == null) {
                        try {
                            currPlay = dataBean;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (indexOfView == 0)
                    continue;

                catViewCache.put(cat, root);
            } else {
                for (int i = 0; i < cat.getData().size(); i++) {
                    LotteryPlayConfigCategoryTouCai.CatBean.DataBean dataBean = cat.getData().get(i);
                    if (userPlayInfo.getMethod().get(dataBean.getLotteryCode()) == null)
                        continue;

                    if (currPlay == null) {
                        try {
                            currPlay = dataBean;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            int count = 0;
            for (int i = 0; i < cat.getData().size(); i++) {
                LotteryPlayConfigCategoryTouCai.CatBean.DataBean dataBean = cat.getData().get(i);
                if (userPlayInfo.getMethod().get(dataBean.getLotteryCode()) != null)
                    count++;
            }

//            if (count > 1 || currCatgory.getCat().size() > 1) {
            if (!cat.getTitleName().equals("定位胆")) {
                View root = catViewCache.get(cat);
                if (root != null)
                    secondaryContainer.addView(root, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    private LotteryPlayConfigCategoryTouCai.CatBean.DataBean getFirstAvailableData(LotteryPlayConfigCategoryTouCai.CatBean cat) {
        for (int i = 0; i < cat.getData().size(); i++) {
            LotteryPlayConfigCategoryTouCai.CatBean.DataBean dataBean = cat.getData().get(i);
            if (userPlayInfo.getMethod().get(dataBean.getLotteryCode()) != null)
                return dataBean;
        }

        return null;
    }

    private void updateSelected() {
        for (int i = 0; i < flMine.getChildCount(); i++) {
            TableRow row = (TableRow) flMine.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                View child = row.getChildAt(j);
                if (currCatgory != null && child.getTag() == currCatgory) {
                    child.setSelected(true);
                } else {
                    child.setSelected(false);
                }
            }
        }

        for (Map.Entry<LotteryPlayConfigCategoryTouCai.CatBean.DataBean, View> set : playViewMap.entrySet()) {
            set.getValue().setSelected(set.getKey() == currPlay);
        }

        for (Map.Entry<LotteryPlayConfigCategoryTouCai.CatBean, View> set : groupCatViewMap.entrySet()) {
            for (LotteryPlayConfigCategoryTouCai.CatBean.DataBean dataBean : set.getKey().getData()) {
                if (currPlay == dataBean) {
                    set.getValue().setSelected(true);
                    break;
                } else {
                    set.getValue().setSelected(false);
                }
            }
        }
    }

    private void resetPlay() {
        currPlay = null;
        currCatgory = null;

        for (LotteryPlayConfigCategoryTouCai cat1 : listPlayCategory) {
            for (LotteryPlayConfigCategoryTouCai.CatBean cat2 : cat1.getCat()) {
                for (LotteryPlayConfigCategoryTouCai.CatBean.DataBean data : cat2.getData()) {
                    if (Strs.isEqual(data.getLotteryCode(), playcode)) {
                        currCatgory = cat1;
                        currPlay = data;
                        break;
                    }
                }
            }
        }
    }
}
