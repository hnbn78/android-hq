package com.desheng.app.toucai.controller;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;

import com.ab.util.Strs;
import com.desheng.app.toucai.panel.FragLotteryPlayFragmentDigits;
import com.desheng.app.toucai.view.PlayDigitView;
import com.desheng.base.algorithm.DSBetDetailAlgorithmUtil;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.pearl.view.SimpleCollapse.SimpleNestedScrollView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * 五星直选玩法controller
 * <p>
 * 说明:"从万位、千位、百位、十位、个位中选择一个5位数号码组成一注，所选号码与开奖号码全部相同，且顺序一致，即为中奖。"
 * Created by lee on 2018/3/13.
 */
public class CtrlPlay_Digits extends BaseCtrlPlay implements PlayDigitView.OnBallsChangeListener {

    ArrayList<PlayDigitView> listBallGroup;

    //所有选择的球组 万 -> 个
    private boolean[][] arrAllBallsSelected = null;
    //所有选择求组value 万 -> 个
    private String[][] arrAllBallsValue = null;


    public CtrlPlay_Digits(Context ctx, FragLotteryPlayFragmentDigits frag, LotteryInfo info, LotteryPlayUIConfig uiConfig, LotteryPlay play) {
        super(ctx, frag, info, uiConfig, play);

    }

    //设置号码组
    @Override
    public void setContentGroup() {
        listBallGroup = frag.getListBallGroups();
        arrAllBallsSelected = new boolean[listBallGroup.size()][];
        arrAllBallsValue = new String[listBallGroup.size()][];
        for (int i = 0; i < listBallGroup.size(); i++) {
            PlayDigitView playDigitView = listBallGroup.get(i);

            if (playDigitView.getLayoutBean().getMaxShowBtn() == 1) {
                playDigitView.setBallSingleSelection();
            } else {
                playDigitView.setBallMultiSelection();
            }

            if (playDigitView.getLayoutBean().getIsAll() == 1) {
                playDigitView.setBallMonoSelection();
            }

            playDigitView.setOnBallsChangeListener(this);
            arrAllBallsSelected[i] = playDigitView.getArrBallSelected();
            arrAllBallsValue[i] = playDigitView.getArrBallValue();
        }
    }

    public void onBallsChanged(String power, String[] arrAllValue, ArrayList<String> selectedValue, boolean[] arrBallSelected, int ballCount) {
        //每次变化都扫下所有球组, 如果发现全部都有选定项, 则计算注数, 否则注数清0
        long count = 0;
        if (getUiConfig().getIsExclusion() > 0) { //二, 三同号与不同号排斥
            String fly = listBallGroup.get(0).getSingleSelectedValue();
            if (Strs.isNotEmpty(fly)) {
                char single = fly.charAt(0);
                boolean isChangBall = false;
                for (int i = 0; i < arrAllBallsSelected[1].length; i++) {
                    if (arrAllBallsSelected[1][i] && arrAllBallsValue[1][i].equals(String.valueOf(single))) {
                        arrAllBallsSelected[1][i] = false;
                        isChangBall = true;
                    }
                }
                //有球改, 那么返回, 等二次回调
                if (isChangBall) {
                    listBallGroup.get(1).syncSelection();
                    return;
                }

                //继续计算注数
                if (isAllGroupReady()) { //都选择了球
                    String betNumStr = getNumFromCell();
                    count = DSBetDetailAlgorithmUtil.getBetNoteFromBetNum(betNumStr, getUiConfig().getLotteryID(), getLotteryInfo().getId());

                }
                if (count < 0) {
                    count = 0;
                }
            }
        } else {
            if (isAllGroupReady()) { //都选择了球
                String betNumStr = getNumFromCell();
                count = DSBetDetailAlgorithmUtil.getBetNoteFromBetNum(betNumStr, getUiConfig().getLotteryID(), getLotteryInfo().getId());

            }
            if (count < 0) {
                count = 0;
            }
        }

        setHitCount(count);
        getLotteryPanel().getFootView().refreshBonus();
    }


    @Override
    protected void clearAllSelected() {
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                arrAllBallsSelected[i][j] = false;
            }
        }
        for (int i = 0; i < listBallGroup.size(); i++) {
            listBallGroup.get(i).syncSelection();
        }
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Override
    public void reset() {
        clearAllSelected();
    }

    @Override
    public void autoGenerate() {
        //先滚到顶
        final SimpleNestedScrollView scrollView = (SimpleNestedScrollView) getLotteryPanel().getScrollView();
        scrollView.realSmoothScrollTo(0, 200, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //随机各组
                Random random = new Random(System.currentTimeMillis());
                if (getUiConfig().getLayout().size() == 1 && getUiConfig().getLayout().get(0).getIsAll() == 1) {
                    //全选
                    for (int j = 0; j < arrAllBallsSelected[0].length; j++) {
                        arrAllBallsSelected[0][j] = true;
                    }
                } else {
                    for (int i = 0; i < arrAllBallsSelected.length; i++) {
                        //单行需要多个
                        LotteryPlayUIConfig.LayoutBean layout = getUiConfig().getLayout().get(i);
                        int rowNeedNum = layout.getRollNeedNum();
//                        //再次随机额外
//                        int extra = (layout.getBalls().size() - rowNeedNum);
//                        if (extra >= 2) {
//                            rowNeedNum += random.nextInt(3);
//                        }
                        //单行排斥选择则只随机一个
                        if (layout.getMaxShowBtn() >= 1) {
                            rowNeedNum = layout.getMaxShowBtn();
                        }
                        if (rowNeedNum > 1) {
                            for (int j = 0; j < rowNeedNum; j++) {
                                if (j == 0) {
                                    randomSingle(random, i);
                                } else {
                                    randomMulti(random, i);
                                }
                            }
                        } else {
                            randomSingle(random, i);
                        }

                    }
                }
            }

            public void randomSingle(Random random, int rowIndex) {
                int ranInt = random.nextInt(arrAllBallsSelected[rowIndex].length);

                for (int j = 0; j < arrAllBallsSelected[rowIndex].length; j++) {
                    if (j == ranInt) {
                        arrAllBallsSelected[rowIndex][j] = true;
                    } else {
                        arrAllBallsSelected[rowIndex][j] = false;
                    }
                }
            }

            public void randomMulti(Random random, int rowIndex) {
                int ranInt = random.nextInt(arrAllBallsSelected[rowIndex].length);

                for (int j = 0; j < arrAllBallsSelected[rowIndex].length; j++) {
                    if (j == ranInt) {
                        if (arrAllBallsSelected[rowIndex][j]) {
                            randomMulti(random, rowIndex);
                        } else {
                            arrAllBallsSelected[rowIndex][j] = true;
                            return;
                        }
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //刷新各组
                if (mDisposable != null) {
                    mDisposable.dispose();
                }
                mDisposable = Flowable.interval(350, TimeUnit.MILLISECONDS)
                        .onBackpressureBuffer()
                        .doOnNext(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                                if (aLong >= arrAllBallsSelected.length) {
                                    mDisposable.dispose();
                                    return;
                                }
                                //快速返回bug
                                final PlayDigitView playDigitView = listBallGroup.get(aLong.intValue());
                                if (playDigitView == null || ((Activity) getCtx()).isFinishing()) {
                                    return;
                                }
                                scrollView.realSmoothScrollTo(playDigitView.getTopInScrollView(), 250, null);
                                if (playDigitView == null || ((Activity) getCtx()).isFinishing()) {
                                    return;
                                }
                                playDigitView.syncSelection();
                            }
                        });

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public boolean isAllGroupReady() {
        int groupHasSelectionCount = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    groupHasSelectionCount++;
                }
            }
        }
        boolean isAllGroupHasBall = groupHasSelectionCount >= getUiConfig().getNeedNum();
        return isAllGroupHasBall;
    }

    @Override
    public void refreshCurrentUserPlayInfo(Object userPlayInfo) {
        //一般玩法无刷新
    }

    public String getNumFromCell() {
        StringBuilder numBetStr = new StringBuilder();
        boolean isGotValidNum = true;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            StringBuilder rowStr = new StringBuilder();
            int lineBallNum = 0;
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    if (listBallGroup.get(i).isUseSpecialValue()) {
                        rowStr.append(arrAllBallsValue[i][j]);
                    } else {
                        rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[i][j] : " " + arrAllBallsValue[i][j]);
                    }
                    lineBallNum++;
                }
            }
            if (arrAllBallsSelected.length == 1 && lineBallNum < getUiConfig().getNeedBall()) {
                isGotValidNum = false;
                break;
            }
            if (Strs.isEmpty(rowStr.toString())) {
                rowStr.append("-");
            }
            numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
        }
        if (isGotValidNum) {
            return numBetStr.toString();
        } else {
            return "";
        }
    }

    private boolean hasRequestText() {
        return !"".equals(getNumFromCell());
    }

    @Override
    public String getRequestText() {
        if (!hasRequestText()) {
            return "";
        }
        if (getLotteryPanel().getCurrentHit() <= 0) {
            return "";
        }

        StringBuilder numBetStr = new StringBuilder();

        if (listBallGroup.size() == 1) { //单行拼接
            PlayDigitView digitView = listBallGroup.get(0);
            String requestType = digitView.getLayoutBean().getRequestType();
            if ("columnComma".equals(requestType)) {    //单行 拼成,号分割
                StringBuffer rowStr = new StringBuffer();
                for (int j = 0; j < arrAllBallsSelected[0].length; j++) {
                    if (arrAllBallsSelected[0][j]) {
                        rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[0][j] : "," + arrAllBallsValue[0][j]);
                    }
                }
                numBetStr.append(rowStr.toString());
            } else if ("columnDivider".equals(requestType)) {  //单行 ,分割的,"|"
                StringBuffer rowStr = new StringBuffer();
                for (int j = 0; j < arrAllBallsSelected[0].length; j++) {
                    if (arrAllBallsSelected[0][j]) {
                        rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[0][j].trim() : "|" + arrAllBallsValue[0][j].trim());
                    }
                }
                numBetStr.append(rowStr.toString());
            } else if ("columnCommaRemoveAllSpace".equals(requestType)) {  //单行 ,分割的, 去除球值中的" "
                StringBuffer rowStr = new StringBuffer();
                for (int j = 0; j < arrAllBallsSelected[0].length; j++) {
                    if (arrAllBallsSelected[0][j]) {
                        rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[0][j].trim() : "," + arrAllBallsValue[0][j].trim());
                    }
                }
                numBetStr.append(rowStr.toString());
            } else if ("columnNone".equals(requestType)) {  //单行 无分割的
                StringBuffer rowStr = new StringBuffer();
                for (int j = 0; j < arrAllBallsSelected[0].length; j++) {
                    if (arrAllBallsSelected[0][j]) {
                        rowStr.append(arrAllBallsValue[0][j]);
                    }
                }
                numBetStr.append(rowStr.toString());
            } else if ("columnNoneRemoveAllComma".equals(requestType)) {  //单行 无分割的, 去除球值中的","
                StringBuffer rowStr = new StringBuffer();
                for (int j = 0; j < arrAllBallsSelected[0].length; j++) {
                    if (arrAllBallsSelected[0][j]) {
                        rowStr.append(arrAllBallsValue[0][j]);
                    }
                }
                numBetStr.append(rowStr.toString().replace(",", ""));
            } else if ("columnNoneRemoveTail".equals(requestType)) {  //单行 值为,号分割的, 直接拼
                StringBuffer rowStr = new StringBuffer();
                for (int j = 0; j < arrAllBallsSelected[0].length; j++) {
                    if (arrAllBallsSelected[0][j]) {
                        rowStr.append(arrAllBallsValue[0][j]);
                    }
                }
                numBetStr.append(rowStr.toString().substring(0, rowStr.length() - 1));
            } else {
                StringBuffer rowStr = new StringBuffer();
                for (int j = 0; j < arrAllBallsSelected[0].length; j++) {
                    if (arrAllBallsSelected[0][j]) {
                        rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[0][j] : "," + arrAllBallsValue[0][j]);
                    }
                }
                numBetStr.append(rowStr.toString());
            }
        } else { //多行拼接
            String gangType = getUiConfig().getGangType();
            if ("qian3".equals(gangType)) { //无分割 补-
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
                numBetStr.append(",-,-");
            } else if ("qian3_rowComma_columnSpace".equals(gangType)) {
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[i][j] : " " + arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
                numBetStr.append(",-,-");
            } else if ("qian3_rowCommaGangDefault_columnSpace".equals(gangType)) {
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[i][j] : " " + arrAllBallsValue[i][j]);
                        }
                    }
                    if (rowStr.length() == 0) {
                        rowStr.append("-");
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
                numBetStr.append(",-,-");
            } else if ("zhong3".equals(gangType)) { //无分割 补-
                numBetStr.append("-");
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
                numBetStr.append(",-");
            } else if ("qian2_rowComma_columnSpace".equals(gangType)) {
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[i][j] : " " + arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
                numBetStr.append(",-,-,-");
            } else if ("hou2".equals(gangType)) { //无分割 补-
                numBetStr.append("-,-,-");
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
            } else if ("hou3".equals(gangType)) { //无分割 补-
                numBetStr.append("-,-");
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
            } else if ("qian4".equals(gangType)) { //无分割 补-
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
                numBetStr.append(",-");
            } else if ("hou4".equals(gangType)) { //无分割 补-
                numBetStr.append("-");
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
            } else if ("hou4_rowComma_columnNone".equals(gangType)) {
                numBetStr.append("-");
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
            } else if ("rowComma_columnNone".equals(gangType)) {
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
            } else if ("rowComma_columnSpace".equals(gangType)) {
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[i][j] : " " + arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
            } else if ("rowCommaDefaultGang_columnSpace".equals(gangType)) {
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[i][j] : " " + arrAllBallsValue[i][j]);
                        }
                    }
                    if (rowStr.length() == 0) {
                        rowStr.append("-");
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
            } else if ("rowSemicolon_columnSpace".equals(gangType)) {
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[i][j] : " " + arrAllBallsValue[i][j]);
                        }
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : ";" + rowStr.toString());
                }
            } else {
                for (int i = 0; i < arrAllBallsSelected.length; i++) {
                    StringBuffer rowStr = new StringBuffer();
                    for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                        if (arrAllBallsSelected[i][j]) {
                            rowStr.append(arrAllBallsValue[i][j]);
                        }
                    }
                    if (rowStr.length() == 0) {
                        rowStr.append("-");
                    }
                    numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
                }
            }
        }

        return numBetStr.toString();
    }

    @Override
    public boolean canSubmitOrder() {
        return true;
    }

    @Override
    public void setHitCount(long count) {
        super.setHitCount(count);
    }
}
