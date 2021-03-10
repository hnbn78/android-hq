package com.desheng.base.context;

/**
 * Created by lee on 2018/5/2.
 */

public enum BetStatus {
    NORMAL(0, "未开奖"),
    NON_WIN(1, "未中奖"),
    AWARDED(2, "已派奖"),
    WAIT_AWARD(3, "等待派奖"),
    CANCELED(4, "已撤销"),
    CANCELD_SYSTEM(5, "系统撤销"),
    ORDER_REFUND(6, "已退款"),
    WIN(7, "已中奖"),
    UNKNOWN(8, "异常状态");
    
    private int code;
    private String desc;
    
    BetStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public static BetStatus find(int code) {
        BetStatus value = UNKNOWN;
        for (BetStatus b :
                BetStatus.values()) {
            if (b.getCode() == code) {
                value = b;
                break;
            }
        }
        return value;
    }
    
    public static BetStatus find(String desc) {
        BetStatus value = UNKNOWN;
        for (BetStatus b :
                BetStatus.values()) {
            if (b.getDesc().equals(desc)) {
                value = b;
                break;
            }
        }
        return value;
    }
}
