package com.desheng.app.toucai.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrendTypeBean {
    private String Type;
    private int[] blueMissBallNum;//遗漏值
    private String[] headNum;
    private List<TrendTypeIndexBean> index_list = new ArrayList();
    private String name;
    private int showList;
    private int typeId;
    private int lotteryType;

    public int[] getBlueMissBallNum() {
        return this.blueMissBallNum;
    }

    public String[] getHeadNum() {
        return this.headNum;
    }

    public List<TrendTypeIndexBean> getIndex_list() {
        return this.index_list;
    }

    public String getName() {
        return this.name;
    }

    public int getShowList() {
        return this.showList;
    }

    public String getType() {
        return this.Type;
    }

    public int getTypeId() {
        return this.typeId;
    }

    public void setBlueMissBallNum(int[] paramArrayOfInt) {
        this.blueMissBallNum = paramArrayOfInt;
    }

    public void setHeadNum(String[] paramArrayOfString) {
        this.headNum = paramArrayOfString;
    }

    public void setIndex_list(List<TrendTypeIndexBean> paramList) {
        this.index_list = paramList;
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public void setShowList(int paramInt) {
        this.showList = paramInt;
    }

    public void setType(String paramString) {
        this.Type = paramString;
    }

    public void setTypeId(int paramInt) {
        this.typeId = paramInt;
    }

    public int getLotteryType() {
        return lotteryType;
    }

    public void setLotteryType(int lotteryType) {
        this.lotteryType = lotteryType;
    }

    @Override
    public String toString() {
        return "TrendTypeBean{" +
                "Type='" + Type + '\'' +
                ", blueMissBallNum=" + Arrays.toString(blueMissBallNum) +
                ", headNum=" + Arrays.toString(headNum) +
                ", index_list=" + index_list +
                ", name='" + name + '\'' +
                ", showList=" + showList +
                ", typeId=" + typeId +
                '}';
    }
}