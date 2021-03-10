package com.desheng.app.toucai.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.desheng.app.toucai.panel.FragLotteryPlayFragmentDigitsCombind;
import com.desheng.app.toucai.view.PlayDigitView;
import com.desheng.base.algorithm.DSBetDetailAlgorithmUtil;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayUIConfig;

import java.util.ArrayList;
import java.util.Random;

/**
 * 五星直选玩法controller
 * <p>
 * 说明:"从万位、千位、百位、十位、个位中选择一个5位数号码组成一注，所选号码与开奖号码全部相同，且顺序一致，即为中奖。"
 * Created by lee on 2018/3/13.
 */
public class CtrlPlay_DigitsCombind extends BaseCtrlPlay implements PlayDigitView.OnBallsChangeListener {
    int needBall;
    int needWei;
    String regex;
    
    private ArrayList<ViewGroup> listPowerGroup;
    private ArrayList<CheckedTextView> listToggleBtns;
    private boolean [] arrPowerSelected;
    private String [] arrPowerValue;
    
    ArrayList<PlayDigitView> listBallGroup;
    //所有选择的球组 万 -> 个
    private boolean[][] arrAllBallsSelected = null;
    //所有选择求组value 万 -> 个
    private String[][] arrAllBallsValue = null;
    
    
    public CtrlPlay_DigitsCombind(Context ctx, FragLotteryPlayFragmentDigitsCombind frag, LotteryInfo info, LotteryPlayUIConfig uiConfig, LotteryPlay play) {
        super(ctx, frag, info, uiConfig, play);
        regex = "(.{"+needBall+"})";
        needWei = getUiConfig().getNeedWei();
        needBall = getUiConfig().getNeedBall();
    }
    
    //设置号码组
    @Override
    public void setContentGroup() {
        //位
        listPowerGroup = frag.getPowerGroup();
        listToggleBtns = frag.getPowerBtnList();
        arrPowerSelected = new boolean[listPowerGroup.size()];
        arrPowerValue = new String[listPowerGroup.size()];
        for (int i = 0; i < frag.getPowerGroup().size(); i++) {
            frag.getPowerGroup().get(i).setTag(i);
            frag.getPowerGroup().get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int  index = (int) v.getTag();
                    arrPowerSelected[index] = !arrPowerSelected[index];
                    listToggleBtns.get(index).setChecked(arrPowerSelected[index]);
                    
                    if(getPowerBetStr().split(",").length >= needWei){
                        caculateHit();
                    }else{
                        setHitCount(0);
                    }
                }
            });
        }
        for (int i = 0; i < getUiConfig().getValue().size(); i++) {
            arrPowerValue[i] = getUiConfig().getValue().get(i);
        }
        
        //球
        listBallGroup = frag.getListBallGroups();
        arrAllBallsSelected = new boolean[listBallGroup.size()][];
        arrAllBallsValue = new String[listBallGroup.size()][];
        for (int i = 0; i < listBallGroup.size(); i++) {
            PlayDigitView playDigitView = listBallGroup.get(i);
            if(getUiConfig().getIsExclusion() == 1){
                playDigitView.setBallSingleSelection();
            }else{
                playDigitView.setBallMultiSelection();
            }
            playDigitView.setOnBallsChangeListener(this);
            arrAllBallsSelected[i] = playDigitView.getArrBallSelected();
            arrAllBallsValue[i] = playDigitView.getArrBallValue();
        }
    }
    
  
    
    public void onBallsChanged(String power, String[] arrAllValue, ArrayList<String> selectedValue, boolean[] arrBallSelected, int ballCount) {
        //每次变化都扫下所有球组, 如果发现全部都有选定项, 则计算注数, 否则注数清0
        int count = 0;
        if (isAllGroupReady()) { //都选择了球
            caculateHit();
        }else{
           setHitCount(0);
        }
    }
    
    private void caculateHit() {
        String betNumStr = getNumFromCell();
        long count = DSBetDetailAlgorithmUtil.getBetNoteFromBetNum(betNumStr, getUiConfig().getLotteryID(), getLotteryInfo().getId());
        if(count < 0){
            count = 0;
        }
        long powerCount = DSBetDetailAlgorithmUtil.getBetTimesFromPower(getPowerBetStr(), getUiConfig().getLotteryID(), getLotteryInfo().getId(), needWei);
        if(powerCount < 0){
            powerCount = 0;
        }
      
        setHitCount(count * powerCount);
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
        getLotteryPanel().getFootView().refreshBonus();;
    }
    

    @Override
    public void autoGenerate() {
        //随机各组
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < arrPowerSelected.length; i++) {
            arrPowerSelected[i] = false;
        }
        for (int i = 0; i < needWei; i++) {
            arrPowerSelected[nextInstinct(random, arrPowerSelected.length, arrPowerSelected)] = true;
        }
        syncPowerSelection();
    
        for (int i = 0; i < arrAllBallsSelected.length ; i++) {
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                arrAllBallsSelected[i][j] = false;
            }
            for (int k = 0; k < needBall; k++) {
                arrAllBallsSelected[i][nextInstinct(random, arrAllBallsSelected[i].length, arrAllBallsSelected[i])] = true;
        
            }
            listBallGroup.get(i).syncSelection();
        }
       
    }
    
    private void syncPowerSelection() {
        for (int i = 0; i < listToggleBtns.size(); i++) {
            listToggleBtns.get(i).setChecked(arrPowerSelected[i]);
        }
    }
    
    public static int nextInstinct(Random random, int lenght, boolean [] array){
        int index = random.nextInt(lenght);
        if(array[index]){
            return nextInstinct(random, lenght, array);
        }else{
            return index;
        }
    }
    
    @Override
    public boolean isAllGroupReady(){
        int groupHasSelectionCount = 0;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            for (int j = 0; j < arrAllBallsSelected[0].length; j++) {
                if (arrAllBallsSelected[i][j]) {
                    groupHasSelectionCount++;
                }
            }
        }
        
        return groupHasSelectionCount >= needBall && getPowerBetStr().length() >= needWei;
    }
    
    @Override
    public void refreshCurrentUserPlayInfo(Object userPlayInfo) {
        //一般玩法无刷新
    }
    
    public String getNumFromCell()
    {
        StringBuilder numBetStr = new StringBuilder();
        boolean isGotValidNum = true;
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            StringBuffer rowStr = new StringBuffer();
            boolean lineHaveBall = false;
            for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                if(arrAllBallsSelected[i][j]){
                    rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[i][j] : " " + arrAllBallsValue[i][j]);
                    if(!lineHaveBall){
                        lineHaveBall = true;
                    }
                }
            }
            if(!lineHaveBall){
                isGotValidNum = false;
                break;
            }
            numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
        }
        if(isGotValidNum){
            return numBetStr.toString();
        }else{
            return "";
        }
    }
    
    @Override
    public String getRequestText() {
        if(getLotteryPanel().getCurrentHit() <= 0){
            return "";
        }
        String powerStr = "[";
        for (int i = 0; i < arrPowerSelected.length; i++) {
            String check = (arrPowerSelected[i] ? "√" : "-");
            powerStr += (powerStr.equals("[") ? check : "," + check);
        }
        powerStr += "]";
        return powerStr + getBallStr();
    }

    @Override
    public boolean canSubmitOrder() {
        return true;
    }

    @Override
    public void reset() {
        clearAllSelected();
    }
    
    public String getBallStr()
    {
        if("".equals(getNumFromCell())){
            return "";
        }
        
        StringBuilder numBetStr = new StringBuilder();
        
        for (int i = 0; i < arrAllBallsSelected.length; i++) {
            StringBuffer rowStr = new StringBuffer();
            if("columnNone".equals(getUiConfig().getLayout().get(i).getRequestType())){
                for (int j = 0; j < arrAllBallsSelected[i].length; j++) {
                    if (arrAllBallsSelected[i][j]) {
                        rowStr.append(arrAllBallsValue[i][j]);
                    }
                }
            }else{
                for (int j = 0; j < arrAllBallsSelected[0].length; j++) {
                    if(arrAllBallsSelected[0][j]){
                        rowStr.append(rowStr.length() == 0 ? arrAllBallsValue[0][j] : "," + arrAllBallsValue[0][j]);
                    }
                }
            }
            
            numBetStr.append(numBetStr.length() == 0 ? rowStr.toString() : "," + rowStr.toString());
        }
        
        return numBetStr.toString();
    }
    
    
    public String getPowerBetStr(){
        String res = "";
        for (int i = 0; i < arrPowerSelected.length; i++) {
            if(arrPowerSelected[i]){
                res += (res.isEmpty() ? arrPowerValue[i] : "," + arrPowerValue[i]);
            }
        }
        return res;
    }
    
    /*public static void main ( String[] args )
        {
            String input = "abcdefghijk";
            String regex = "(.{3})";
            input = input.replaceAll (regex, "$1,");
            System.out.println (input);
        }*/
}
