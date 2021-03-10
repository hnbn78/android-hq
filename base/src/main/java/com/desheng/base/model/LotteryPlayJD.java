package com.desheng.base.model;

import java.util.List;

/**
 * Created by lee on 2018/4/6.
 */

public class LotteryPlayJD {
    
    private String showName;
    private int type;
    private List<LayoutBean> layout;
    private String playId;
    private String introduction;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getShowName() {
        return showName;
    }
    
    public void setShowName(String showName) {
        this.showName = showName;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public List<LayoutBean> getLayout() {
        return layout;
    }
    
    public void setLayout(List<LayoutBean> layout) {
        this.layout = layout;
    }
    
    public String getPlayId() {
        return playId;
    }
    
    public void setPlayId(String playId) {
        this.playId = playId;
    }
    
    public static class LayoutBean {
        /**
         * title : 第一球
         * num : ["大","小","单","双"]
         * balls : ["j735","j736","j737","j738"]
         */
        
        private String title;
        private List<String> num;
        private List<String> balls;
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public List<String> getNum() {
            return num;
        }
        
        public void setNum(List<String> num) {
            this.num = num;
        }
        
        public List<String> getBalls() {
            return balls;
        }
        
        public void setBalls(List<String> balls) {
            this.balls = balls;
        }
    }
}
