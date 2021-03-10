package com.desheng.base.model;

import java.util.List;

/**
 * Created by lee on 2018/3/13.
 */

public class LotteryPlayUIConfig {
    
    /**
     * title : 五星直选复式
     * lotteryID : 1001
     * showType : 1
     * lotteryName : wxzhixfs
     * isFromOne : 0
     * isExclusion : 0
     * needNum : 5
     * help : 从万位、千位、百位、十位、个位中选择一个5位数号码组成一注，所选号码与开奖号码全部相同，且顺序一致，即为中奖。
     * layout : [{"title":"万位","balls":["0","1","2","3","4","5","6","7","8","9"],"tools":true},{"title":"千位","balls":["0","1","2","3","4","5","6","7","8","9"],"tools":true},{"title":"百位","balls":["0","1","2","3","4","5","6","7","8","9"],"tools":true},{"title":"十位","balls":["0","1","2","3","4","5","6","7","8","9"],"tools":true},{"title":"个位","balls":["0","1","2","3","4","5","6","7","8","9"],"tools":true}]
     */
    
    private String title;
    private int lotteryID;
    private int showType;
    private String lotteryName;
    private int inputNumDegits = 1; //默认1
    private int isFromOne;
    private int isExclusion;
    private int needNum;
    private int needBall;
    private int valueFrom;
    private int valueTo;
    private int needWei;
    private String help;
    private int isAddNum;
    private int isDelePace;
    private String gangType;
    private List<String> value;
    private List<LayoutBean> layout;
    private long fromNum;
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getLotteryID() {
        return lotteryID;
    }
    
    public void setLotteryID(int lotteryID) {
        this.lotteryID = lotteryID;
    }
    
    public int getShowType() {
        return showType;
    }
    
    public void setShowType(int showType) {
        this.showType = showType;
    }
    
    public String getLotteryName() {
        return lotteryName;
    }
    
    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }
    
    public int getIsFromOne() {
        return isFromOne;
    }
    
    public void setIsFromOne(int isFromOne) {
        this.isFromOne = isFromOne;
    }
    
    public int getIsExclusion() {
        return isExclusion;
    }
    
    public void setIsExclusion(int isExclusion) {
        this.isExclusion = isExclusion;
    }
    
    public int getNeedNum() {
        return needNum;
    }
    
    public void setNeedNum(int needNum) {
        this.needNum = needNum;
    }
    
    public String getHelp() {
        return help;
    }
    
    public void setHelp(String help) {
        this.help = help;
    }
    
    public List<LayoutBean> getLayout() {
        return layout;
    }
    
    public void setLayout(List<LayoutBean> layout) {
        this.layout = layout;
    }
    
    public int getIsAddNum() {
        return isAddNum;
    }
    
    public void setIsAddNum(int isAddNum) {
        this.isAddNum = isAddNum;
    }
    
    public int getIsDelePace() {
        return isDelePace;
    }
    
    public void setIsDelePace(int isDelePace) {
        this.isDelePace = isDelePace;
    }
    
    public long getFromNum() {
        return fromNum;
    }
    
    public void setFromNum(long fromNum) {
        this.fromNum = fromNum;
    }
    
    public List<String> getValue() {
        return value;
    }
    
    public void setValue(List<String> value) {
        this.value = value;
    }
    
    /**
     * 球和位数都需要的时候只使用needNum, 不一致时使用needBall
     * @return
     */
    public int getNeedBall() {
        if(needBall == 0){
            needBall = needNum;
        }
        return needBall;
    }
    
    public int getNeedWei() {
        if(needWei == 0){
            needWei = needNum;
        }
        return needWei;
    }
    
    public int getInputNumDegits() {
        return inputNumDegits;
    }
    
    public void setInputNumDegits(int inputNumDegits) {
        this.inputNumDegits = inputNumDegits;
    }
    
    public void setNeedWei(int needWei) {
        this.needWei = needWei;
    }
    
    public void setNeedBall(int needBall) {
        this.needBall = needBall;
    }
    
    public String getGangType() {
        return gangType;
    }
    
    public void setGangType(String gangType) {
        this.gangType = gangType;
    }
    
    public int getValueFrom() {
        return valueFrom;
    }
    
    public void setValueFrom(int valueFrom) {
        this.valueFrom = valueFrom;
    }
    
    public int getValueTo() {
        return valueTo;
    }
    
    public void setValueTo(int valueTo) {
        this.valueTo = valueTo;
    }
    
    public static class LayoutBean {
        /**
         * title : 万位
         * balls : ["0","1","2","3","4","5","6","7","8","9"]
         * tools : true
         */
        
        private String title;
        private boolean tools;
        private List<String> balls;
        private List<String> ballBets;
        private List<String> ballCode;
        private int isUseSpecialValue;
        private int isNotShowSelectView;
        private int fromNum;
        private int rollNeedNum = 1;
        private String requestType;
        private int maxShowBtn;
        private int isAll;

        public List<String> getBallCode() {
            return ballCode;
        }
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public boolean isTools() {
            return tools;
        }
        
        public void setTools(boolean tools) {
            this.tools = tools;
        }
        
        public List<String> getBalls() {
            return balls;
        }
        
        public void setBalls(List<String> balls) {
            this.balls = balls;
        }
    
        public int getIsUseSpecialValue() {
            return isUseSpecialValue;
        }
    
        public void setIsUseSpecialValue(int isUseSpecialValue) {
            this.isUseSpecialValue = isUseSpecialValue;
        }
    
        public int getIsNotShowSelectView() {
            return isNotShowSelectView;
        }
    
        public void setIsNotShowSelectView(int isNotShowSelectView) {
            this.isNotShowSelectView = isNotShowSelectView;
        }
    
        public int getFromNum() {
            return fromNum;
        }
    
        public void setFromNum(int fromNum) {
            this.fromNum = fromNum;
        }
    
        public List<String> getBallBets() {
            return ballBets;
        }
    
        public void setBallBets(List<String> ballBets) {
            this.ballBets = ballBets;
        }
    
        public String getRequestType() {
            return requestType;
        }
    
        public void setRequestType(String requestType) {
            this.requestType = requestType;
        }
    
        public boolean isShowTools() {
            return isTools() && (getIsNotShowSelectView() == 0);
        }
    
        public int getMaxShowBtn() {
            return maxShowBtn;
        }
    
        public void setMaxShowBtn(int maxShowBtn) {
            this.maxShowBtn = maxShowBtn;
        }
    
        public int getIsAll() {
            return isAll;
        }
    
        public void setIsAll(int isAll) {
            this.isAll = isAll;
        }
    
        public int getRollNeedNum() {
            return rollNeedNum;
        }
    
        public void setRollNeedNum(int rollNeedNum) {
            this.rollNeedNum = rollNeedNum;
        }
    }
}
