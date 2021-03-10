package com.pearl.view.Keyboard;

/**
 * 自定义接口，用于给密码输入完成添加回掉事件
 */
public interface OnPasswordInput {
    void onInputFinish(String password);
    void onClearHistory();
}