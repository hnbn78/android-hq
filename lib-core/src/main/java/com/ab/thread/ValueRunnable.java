package com.ab.thread;

/**
 * Created by lee on 2017/3/28 0028.
 */

public class ValueRunnable<T> implements Runnable{
    T value;
    private boolean willBreak = false;

    //value 用于接收参数
    public ValueRunnable(T value) {
        this.value = value;
    }
    
    //value 用于返回参数
    public ValueRunnable(){
        
    }

    @Override
    public void run() {

    }
    
    /**
     * 超时
     */
    public void timeout(){
        
    }
    
    /**
     * 告诉外部循环终端
     * @return
     */
    public boolean willBreak() {
        return willBreak;
    }
    
    /**
     * 终端外部调用的循环
     * @return
     */
    public void breakCircle(){
        willBreak = true;
    }
    
    public T getValue(){
        return (T)value;
    }
    
    public void setValue(T v){
        value = v;
    }
}