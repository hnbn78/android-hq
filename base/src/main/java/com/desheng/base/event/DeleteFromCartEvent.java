package com.desheng.base.event;

/**
 * 删除玩法页面保存的号码
 * Created by lee on 2018/3/19.
 */
public class DeleteFromCartEvent extends EventBase {
    public boolean isAllDelete;
    public int[] arrDeleteIndex;
    
    public DeleteFromCartEvent(boolean isAllDelete, int[] arrDeleteIndex) {
        this.isAllDelete = isAllDelete;
        this.arrDeleteIndex = arrDeleteIndex;
    }
}
