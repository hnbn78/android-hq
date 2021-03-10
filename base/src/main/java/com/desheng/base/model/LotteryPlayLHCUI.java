package com.desheng.base.model;

import java.util.List;

/**
 * Created by lee on 2018/4/6.
 */

public class LotteryPlayLHCUI {

    /**
     * gameIntroduce : 当期开出的最后一位号码为特码。当开出特码与投注号码一致、即视为中奖（其余情况则视为不中奖）。
     * cellUI : [{"lhcID":"25000","lhcIDReq":"j25000","gname":"特码","showNums":["01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49"],"showColors":[1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3]}]
     */

    private String gameIntroduce;
    private List<CellUIBean> cellUI;

    public String getGameIntroduce() {
        return gameIntroduce;
    }

    public void setGameIntroduce(String gameIntroduce) {
        this.gameIntroduce = gameIntroduce;
    }

    public List<CellUIBean> getCellUI() {
        return cellUI;
    }

    public void setCellUI(List<CellUIBean> cellUI) {
        this.cellUI = cellUI;
    }

    public static class CellUIBean {
        /**
         * lhcID : 25000
         * lhcIDReq : j25000
         * gname : 特码
         * showNums : ["01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49"]
         * showColors : [1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3,1,1,2,2,3,3]
         */

        private String lhcID;
        private String lhcIDReq;
        private String gname;
        private String BetContext;
        private List<String> showNums;
        private List<Integer> showColors;

        public String getLhcID() {
            return lhcID;
        }

        public void setLhcID(String lhcID) {
            this.lhcID = lhcID;
        }

        public String getLhcIDReq() {
            return lhcIDReq;
        }

        public void setLhcIDReq(String lhcIDReq) {
            this.lhcIDReq = lhcIDReq;
        }

        public String getGname() {
            return gname;
        }

        public void setGname(String gname) {
            this.gname = gname;
        }

        public List<String> getShowNums() {
            return showNums;
        }

        public void setShowNums(List<String> showNums) {
            this.showNums = showNums;
        }

        public List<Integer> getShowColors() {
            return showColors;
        }

        public void setShowColors(List<Integer> showColors) {
            this.showColors = showColors;
        }

        public String getBetContext() {
            return BetContext;
        }

        public void setBetContext(String betContext) {
            BetContext = betContext;
        }
    }
}
