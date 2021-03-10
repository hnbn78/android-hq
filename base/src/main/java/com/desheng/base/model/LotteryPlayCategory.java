package com.desheng.base.model;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * Created by lee on 2018/3/12.
 */

public class LotteryPlayCategory implements Serializable {
    
    /**
     * titleName : 五星
     * data : [{"showName":"五星直选复式","name":"直选复式","lotteryCode":"wxzhixfs"},{"showName":"五星直选单式","name":"直选单式","lotteryCode":"wxzhixds"},{"showName":"五星直选组合","name":"直选组合","lotteryCode":"wxzhixzh"},{"showName":"五星组选120","name":"组选120","lotteryCode":"wxzux120"},{"showName":"五星组选60","name":"组选60","lotteryCode":"wxzux60"},{"showName":"五星组选30","name":"组选30","lotteryCode":"wxzux30"},{"showName":"五星组选20","lotteryCode":"wxzux20","name":"组选20"},{"showName":"五星组选10","lotteryCode":"wxzux10","name":"组选10"},{"showName":"五星组选5","lotteryCode":"wxzux5","name":"组选5"},{"showName":"五星大小单双","name":"总和大小单双","lotteryCode":"ssc5x_sumdxds"}]
     */
    
    //需要赋值
    private String categoryCode;
    private String titleName;
    private ArrayList<LotteryPlay> data;
    
    public LotteryPlayCategory() {
    
    }

    public LotteryPlayCategory(LotteryPlayCategory ori) {
        this.categoryCode = ori.categoryCode;
        this.titleName = ori.titleName;
        this.data = new ArrayList<>(ori.data);
    }

    public LotteryPlayCategory(String categoryCode) {
        this.categoryCode = categoryCode;
    }
    
    public String getCategoryCode() {
        return categoryCode;
    }
    
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
    
    public String getTitleName() {
        return titleName;
    }
    
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
    
    public ArrayList<LotteryPlay> getData() {
        return data;
    }
    
    public void setData(ArrayList<LotteryPlay> data) {
        this.data = data;
    }
}
