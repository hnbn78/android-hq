package com.desheng.app.toucai.panel;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.desheng.app.toucai.controller.CtrlPlay_Dummy;
import com.desheng.app.toucai.controller.CtrlPlay_LongHuHe_AllInOne;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.view.PlayBoardJDView;
import com.desheng.app.toucai.view.PlayDigitView;
import com.desheng.app.toucai.view.PlayDigitsJDView;
import com.desheng.app.toucai.view.PlayLongHuHeView;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayConfigCategoryTouCai;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 彩票玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayFragmentLongHuHeAllInOne extends BaseLotteryPlayFragment {

    private int[] arrDefGroup = new int[]{R.id.vgDigit0, R.id.vgDigit1, R.id.vgDigit2, R.id.vgDigit3, R.id.vgDigit4,
            R.id.vgDigit5, R.id.vgDigit6, R.id.vgDigit7, R.id.vgDigit8, R.id.vgDigit9};
    private ArrayList<PlayLongHuHeView> listBallGroups = new ArrayList<>();
    List<LotteryPlayConfigCategoryTouCai.CatBean.DataBean> lotteryPlayList;
    List<LotteryPlayUIConfig> longhuheConfigs = new ArrayList<>();
    private String[] lhhBalls = {"龙", "虎", "和"};

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_longhuhe;
    }

    @Override
    protected void createCtrlPlay() {

        if (userPlayInfo == null) {
            setCtrlPlay(new CtrlPlay_Dummy(getActivity(), this, getLotteryInfo(), super.getUiConfig(), super.getLotteryPlay()));
        } else {
            setCtrlPlay(new CtrlPlay_LongHuHe_AllInOne(getActivity(), this, getLotteryInfo(), super.getUiConfig(), super.getLotteryPlay(), longhuheConfigs, lotteryPlayList));
        }

    }

    @Override
    public LotteryPlay getLotteryPlay() {
        return UserManagerTouCai.getIns().getLotteryPlay(super.getLotteryPlay().category, lotteryPlayList.get(0).getLotteryCode());
    }

    @Override
    public void createPlayModel(View root) {
        if (userPlayInfo == null)
            return;

        lotteryPlayList = new ArrayList<>();
        List<LotteryPlayConfigCategoryTouCai.CatBean.DataBean> dataBeans = UserManagerTouCai.getIns().getAllInOne().get(super.getLotteryPlay().category).get(super.getLotteryPlay().lotteryCode);
        for (LotteryPlayConfigCategoryTouCai.CatBean.DataBean bean : dataBeans) {

            if (userPlayInfo != null && userPlayInfo.getMethod().get(bean.getLotteryCode()) != null) {
                lotteryPlayList.add(bean);
            }

            String lotteryCode = bean.getLotteryCode();
            if (lotteryCode.contains("gflonghuhe")) {
                if (userPlayInfo.getMethod().get(lotteryCode + "_long") != null || userPlayInfo.getMethod().get(lotteryCode + "_hu") != null
                        || userPlayInfo.getMethod().get(lotteryCode + "_he") != null) {
                    lotteryPlayList.add(bean);
                }
            }
        }

        for (int i = 0; i < lotteryPlayList.size(); i++) {
            LotteryPlayConfigCategoryTouCai.CatBean.DataBean d = lotteryPlayList.get(i);
            longhuheConfigs.add(UserManagerTouCai.getIns().getLotteryPlayUIConfigWithCategoryMap().get(super.getLotteryPlay().category).get(d.getLotteryCode()));
        }

        //配置号码组
        listBallGroups = new ArrayList<>();
        for (int i = 0; i < lotteryPlayList.size(); i++) {
            PlayLongHuHeView digitGroup = (PlayLongHuHeView) root.findViewById(arrDefGroup[i]);
            digitGroup.setVisibility(View.VISIBLE);
            digitGroup.init();
            LotteryPlayUIConfig lotteryPlayUIConfig = longhuheConfigs.get(i);
            LotteryPlayUIConfig.LayoutBean layoutBean = longhuheConfigs.get(i).getLayout().get(0);
            boolean isNewLongHu = lotteryPlayUIConfig.getLotteryID() > 1111 && lotteryPlayUIConfig.getLotteryID() <= 1121;
            if (isNewLongHu) {
                List<String> balls = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    LotteryPlayUserInfo.MathodBean methodBean = getUserPlayInfo().getMethod().get(layoutBean.getBallCode().get(j));
                    if (methodBean != null) {
                        balls.add(lhhBalls[j]);
                    }
                }
                layoutBean.setBalls(balls);
            }
            digitGroup.setConfig(layoutBean);
            listBallGroups.add(digitGroup);
            if (i % 2 == 1) {
                ((View) digitGroup.getParent()).findViewById(R.id.v_divider).setVisibility(View.VISIBLE);
                ((LinearLayout) digitGroup.getParent()).setWeightSum(2);

                LinearLayout.LayoutParams pa = (LinearLayout.LayoutParams) ((View) digitGroup.getParent().getParent()).getLayoutParams();
                pa.weight = 2;
                ((View) digitGroup.getParent().getParent()).setLayoutParams(pa);
                digitGroup.showTiger();
            } else {
                digitGroup.showDragon();
            }
        }
    }

    @Override
    public void setCurrentUserPlayInfo(Object userPlayInfo) {
        super.setCurrentUserPlayInfo(userPlayInfo);

        if (listBallGroups == null) {
            getCtrlPlay().stop();
            createPlayModel(getContentView());
            setCtrlPlay(new CtrlPlay_LongHuHe_AllInOne(getActivity(), this, getLotteryInfo(), super.getUiConfig(), super.getLotteryPlay(), longhuheConfigs, lotteryPlayList));
            getCtrlPlay().start(null);
        }
    }

    @Override
    public ArrayList<PlayDigitView> getListBallGroups() {
        return null;
    }

    @Override
    public ArrayList<PlayLongHuHeView> getListLHHGroups() {
        return listBallGroups;
    }

    @Override
    public ArrayList<PlayDigitsJDView> getListBallGroupsJD() {
        return null;
    }

    @Override
    public ArrayList<PlayBoardJDView> getListBoardGroupsJD() {
        return null;
    }

    @Override
    public HashMap<String, View> getInputGroup() {
        return null;
    }

    @Override
    public ArrayList<ViewGroup> getPowerGroup() {
        return null;
    }

    @Override
    public ArrayList<CheckedTextView> getPowerBtnList() {
        return null;
    }

    @Override
    public int getMode() {
        return 0;
    }
}
