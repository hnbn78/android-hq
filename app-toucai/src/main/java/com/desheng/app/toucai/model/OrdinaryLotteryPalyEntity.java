package com.desheng.app.toucai.model;

import java.util.List;

public class OrdinaryLotteryPalyEntity {

    /**
     * commonBetPlay : ["dddss","dddss","dddss"]
     * id : 111
     */

    private int id;
    private List<String> commonBetPlay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getCommonBetPlay() {
        return commonBetPlay;
    }

    public void setCommonBetPlay(List<String> commonBetPlay) {
        this.commonBetPlay = commonBetPlay;
    }
}
