package com.desheng.base.time;

/**
 * Created by lee on 2017/4/23 0023.
 */
public interface ITimeEngine {
    /**
     * 注册时生成一个事件对象标本, timeEngine结束时自动移除.
     * 注册之后 @Subscribe(threadMode = ThreadMode.MAIN)参数为clazz类型的方法会被每秒回调,
     * 当count==0后, 回调结束. 必须在 @Subscribe中判断tag是否是自己放出的事件, 如果其他订阅者也订阅
     * 了事件,则发布者发布的事件会通知到所有订阅者
     *
     * @param seconds
     * @param tag     在注册时必须唯一, 如有重复, 则注册不上. 防止重复注册.
     */
    void registTimeCountdown(String tag, int seconds);
    
    void unregistTimeCountdownByTag(String tag);
    
    int getCountLeft(String tag);
     
    void shutdownEngine();
}
