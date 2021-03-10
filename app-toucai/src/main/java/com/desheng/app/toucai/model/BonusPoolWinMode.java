package com.desheng.app.toucai.model;

public class BonusPoolWinMode {


    /**
     * level : 3
     * bonusAmount : 50
     * hasPrize : true
     * bonusName : 一个大西瓜
     * bonusType : 2
     */

    private int level;
    private String bonusAmount;
    private boolean hasPrize;
    private String bonusName;
    private int bonusType;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(String bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public boolean isHasPrize() {
        return hasPrize;
    }

    public void setHasPrize(boolean hasPrize) {
        this.hasPrize = hasPrize;
    }

    public String getBonusName() {
        return bonusName;
    }

    public void setBonusName(String bonusName) {
        this.bonusName = bonusName;
    }

    public int getBonusType() {
        return bonusType;
    }

    public void setBonusType(int bonusType) {
        this.bonusType = bonusType;
    }
}
