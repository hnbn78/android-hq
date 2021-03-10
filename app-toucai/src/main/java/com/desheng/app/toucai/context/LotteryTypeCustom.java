package com.desheng.app.toucai.context;

import com.ab.util.Strs;
import com.desheng.app.toucai.model.LotteryInfoCustom;
import com.desheng.base.context.ILotteryType;

import java.util.ArrayList;

/**
 * 彩种栏目信息
 * 固定栏目废弃, 采用网络获取的动态栏目 customMap.
 * Created by lee on 2018/3/8.
 */
public class LotteryTypeCustom implements ILotteryType {
    
    /**
     * id : 5
     * lotteryShowType : 3
     * lotteryName : 的范德萨发
     * lotteryCount : 10
     * sort : 14
     * showTerminalType : 2
     * status : 1
     * meno : 22
     * imgUrl : 222
     * lotteryList : [{"code":"GD11Y","showName":"广东11选5","adver":0,"hot":0,"jumpUrl":null,"appIcon":null,"des":null,"desIcon":null,"pcIcon":null,"showType":3,"id":24,"adverIcon":null,"favorite":0},{"code":"ZY11Y","showName":"槟城11选5","adver":0,"hot":0,"jumpUrl":null,"appIcon":null,"des":null,"desIcon":null,"pcIcon":null,"showType":3,"id":21,"adverIcon":null,"favorite":0},{"code":"JX11Y","showName":"江西11选5","adver":0,"hot":0,"jumpUrl":null,"appIcon":null,"des":null,"desIcon":null,"pcIcon":null,"showType":3,"id":23,"adverIcon":null,"favorite":0},{"code":"SD11Y","showName":"山东11选5","adver":0,"hot":0,"jumpUrl":null,"appIcon":null,"des":null,"desIcon":null,"pcIcon":null,"showType":3,"id":22,"adverIcon":null,"favorite":0},{"code":"LN11Y","showName":"辽宁11选5","adver":0,"hot":0,"jumpUrl":null,"appIcon":null,"des":null,"desIcon":null,"pcIcon":null,"showType":3,"id":25,"adverIcon":null,"favorite":0},{"code":"SH11Y","showName":"上海11选5","adver":0,"hot":0,"jumpUrl":null,"appIcon":null,"des":null,"desIcon":null,"pcIcon":null,"showType":3,"id":26,"adverIcon":null,"favorite":0},{"code":"JS11Y","showName":"江苏11选5","adver":0,"hot":0,"jumpUrl":null,"appIcon":null,"des":null,"desIcon":null,"pcIcon":null,"showType":3,"id":28,"adverIcon":null,"favorite":0}]
     */
    
    private int id;
    private int lotteryShowType;
    private String lotteryName;
    private int lotteryCount;
    private int sort;
    private int showTerminalType;
    private int status;
    private String meno;
    private String imgUrl;
    private ArrayList<LotteryInfoCustom> lotteryList;
    
    public int getId() {
        return id;
    }
    
    public int getLotteryShowType() {
        return lotteryShowType;
    }
    
    public void setLotteryShowType(int lotteryShowType) {
        this.lotteryShowType = lotteryShowType;
    }
    
    public String getLotteryName() {
        return lotteryName;
    }
    
    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }
    
    public int getLotteryCount() {
        return lotteryCount;
    }
    
    public void setLotteryCount(int lotteryCount) {
        this.lotteryCount = lotteryCount;
    }
    
    public int getSort() {
        return sort;
    }
    
    public void setSort(int sort) {
        this.sort = sort;
    }
    
    public int getShowTerminalType() {
        return showTerminalType;
    }
    
    public void setShowTerminalType(int showTerminalType) {
        this.showTerminalType = showTerminalType;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getMeno() {
        return meno;
    }
    
    public void setMeno(String meno) {
        this.meno = meno;
    }
    
    public String getImgUrl() {
        return imgUrl;
    }
    
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    
    public ArrayList<LotteryInfoCustom> getLotteryList() {
        return lotteryList;
    }
    
    public void setLotteryList(ArrayList<LotteryInfoCustom> lotteryList) {
        this.lotteryList = lotteryList;
    }
    
    @Override
    public String getShowName() {
        return lotteryName;
    }
    
    @Override
    public void setShowName(String showName) {
        this.lotteryName = showName;
    }
    
    @Override
    public String getCode() {
        return Strs.of(id);
    }
    
    @Override
    public void setCode(String code) {
        this.id = Strs.parse(code, 0);
    }
    
    @Override
    public String getImage() {
        return imgUrl;
    }

    @Override
    public String toString() {
        return "LotteryTypeCustom{" +
                "id=" + id +
                ", lotteryShowType=" + lotteryShowType +
                ", lotteryName='" + lotteryName + '\'' +
                ", lotteryCount=" + lotteryCount +
                ", sort=" + sort +
                ", showTerminalType=" + showTerminalType +
                ", status=" + status +
                ", meno='" + meno + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
