package com.desheng.base.controller;

/**
 * 对实现了activity提供同步/异步成功回调.
 */
public interface IControlled {
    void onControllerStarted(ControllerBase controller, boolean isSuccess);
    void onControllerStoping();
    <T extends ControllerBase>  T getController(Class<T> clazz);
}
