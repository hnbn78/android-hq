package com.desheng.app.toucai.model;

import java.util.List;

public class PtGameInfoMode {

    private List<LotteryInfoCustom> hotThirdGameList;
    private List<LotteryInfoCustom> thirdGameTypeList;
    private List<PtGameTypeEntity> ptGameType;
    private List<PtGameTypeEntity> cqGameType;

    public List<PtGameTypeEntity> getCqGameType() {
        return cqGameType;
    }

    private int totalCount;

    public void setHotThirdGameList(List<LotteryInfoCustom> hotThirdGameList) {
        this.hotThirdGameList = hotThirdGameList;
    }

    public void setThirdGameTypeList(List<LotteryInfoCustom> thirdGameTypeList) {
        this.thirdGameTypeList = thirdGameTypeList;
    }

    public void setPtGameType(List<PtGameTypeEntity> ptGameType) {
        this.ptGameType = ptGameType;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<LotteryInfoCustom> getHotThirdGameList() {
        return hotThirdGameList;
    }

    public List<LotteryInfoCustom> getThirdGameTypeList() {
        return thirdGameTypeList;
    }

    public List<PtGameTypeEntity> getPtGameType() {
        return ptGameType;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public class PtGameTypeEntity {
        /**
         * label : 桌面游戏
         * value : 7
         */
        private String label;
        private String value;

        public void setLabel(String label) {
            this.label = label;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }
    }
}
