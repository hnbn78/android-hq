package com.desheng.base.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.util.Strs;
import com.ab.util.Views;
import com.desheng.base.event.EventBase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * MVP, MVVM架构模式.
 * Controller基类
 * Activity,view使用Controller与业务层解耦, 使用WeakHashMap来保存view, event集合,
 * 虽然会自动清除失效条目, 建议在Controller的清理方法中调用WeakHashMap的clean方法.
 * Created by lee on 2017/3/6 0006.
 */
public abstract class ControllerBase {
    
    
    private Context ctx;
    private IControlled controlled;

    //通用的view, id绑定, 方便子类保存view的引用, 同时不影响释放
    protected WeakHashMap<View, Integer> mapViewIds = new WeakHashMap<>();

    //通用的event, view绑定, 子类可实现自己独有功能. 在Machine门户中注册可实现统一的遍历与回调.
    protected WeakHashMap<View, Class<? extends EventBase>> mapViewEvents = new WeakHashMap<>();
    
    //通用的runnable, view绑定, 子类可实现自己独有功能. 在Machine门户中注册可实现统一的遍历与回调.
    //注意runnable用匿名内部类实现的话会保存父对象强引用, 要及时清除.
    protected HashMap<Runnable, Class<? extends EventBase>> mapRunnableEvents = new HashMap<>();
    
    public ControllerBase(Context ctx) {
        this.ctx = ctx;
    }
    
    public Context getCtx() {
        return ctx;
    }
    
//*****mark 通用View id绑定相关.

    //add进去就是唯一的. 重复会被替换.
    public  void addViewIdPair(View v, int id){
        mapViewIds.put(v, id);
    }
    
    public  void addViewIdPair(View v){
        mapViewIds.put(v, v.getId());
    }

    public  void removeViewIdPair(View v){
        mapViewIds.remove(v);
    }

    public View getViewById(int id){
        View view = null;
        for (Map.Entry<View, Integer> entry: mapViewIds.entrySet()) {
            if(entry.getValue() != null && entry.getValue().equals(id)){
                view = entry.getKey();
                break;
            }
        }
        return view;
    }

    @Nullable
    public TextView getTextViewById(int id){
        View v = getViewById(id);
        if(v != null){
            return (TextView)v;
        }else{
            return null;
        }
    }

    @Nullable
    public ImageView getImageViewById(int id){
        View v = getViewById(id);
        if(v != null){
            return (ImageView)v;
        }else{
            return null;
        }
    }

    public <T> T getViewTextValue(int viewId, T def){
        View view = getTextViewById(viewId);
        if(view == null || !(view instanceof TextView)){
            return def;
        }
        String str =  Views.getText((TextView) view);
        if(Strs.isEmpty(str)){
            return def;
        }
        return Strs.parse(str, def);
    }

    public void setClickToView(int id, View.OnClickListener listener){
        View v = getViewById(id);
        if(v != null){
            v.setOnClickListener(listener);
        }
    }

    public void setText(int id, String text){
        TextView v = getTextViewById(id);
        if(v != null && text != null){
            v.setText(text);
        }
    }
    
    public void setInvisible(int id){
        View v = getViewById(id);
        if(v != null){
            v.setVisibility(View.INVISIBLE);
        }
    }
    
    public void setGone(int id){
        View v = getViewById(id);
        if(v != null){
            v.setVisibility(View.GONE);
        }
    }

    public void setImageToView(int id, Drawable drawable){
        ImageView v = getImageViewById(id);
        if(v != null && drawable != null){
            v.setImageDrawable(drawable);
        }
    }

    public void setImageToView(int id, int resId){
        ImageView v = getImageViewById(id);
        if(v != null && resId > 0){
            v.setImageResource(resId);
        }
    }

//*****mark 通用ViewEvents绑定相关.

    //add进去就是唯一的.
    public  synchronized  void addViewEventPair(View v, Class<? extends EventBase> e){
        mapViewEvents.put(v, e);
    }

    public  synchronized void removeViewEventPair(View v){
        mapViewEvents.remove(v);
    }


    public synchronized LinkedList<View> getViewListByEvent(Class<? extends EventBase> e){
        LinkedList<View> list = new LinkedList<View>();
        Iterator it = mapViewEvents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<View, ?> en = (Map.Entry<View, ?>)it.next();
            if(en!= null && en.getValue()!=null && en.getValue().equals(e)){
                list.add(en.getKey());
            }
        }
        return list;
    }
    
    public synchronized LinkedList<Runnable> getRunnableListByEvent(Class<? extends EventBase> e){
        LinkedList<Runnable> list = new LinkedList<Runnable>();
        Iterator it = mapRunnableEvents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Runnable, ?> en = (Map.Entry<Runnable, ?>)it.next();
            if(en!= null && en.getValue()!=null && en.getValue().equals(e)){
                list.add(en.getKey());
            }
        }
        return list;
    }

    /**
     * 使用event枚举所有绑定的view
     * @param event
     * @param method
     */
    protected synchronized void enumViewsByEvent(EventBase event, EnumViewEventMethod method){
        LinkedList<View> list = getViewListByEvent(event.getClass());
        if(list != null){
            for (View v: list) {
                method.execute(v, event);
            }
        }
    }
    
    public  interface EnumViewEventMethod <T extends EventBase>{
         void execute(View v, T e);
    }
    
//*******通用event runnable绑定相关

    public synchronized void addEventRunnablePair(Class<? extends EventBase> e, Runnable runnable){
        mapRunnableEvents.put(runnable, e);
    }
        
    public synchronized void removeEventRunnablePair(Runnable runnable){
        mapRunnableEvents.remove(runnable);
    }
    
    /**
     * 把event class对应的所有Runnable移除
     * @param e
     */
    public synchronized void removeRunnableByEventClass(Class<? extends EventBase> e){
        Iterator<Map.Entry<Runnable, Class<? extends EventBase>>> iterator =
                                                    mapRunnableEvents.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<Runnable, Class<? extends EventBase>> next = iterator.next();
            if(next.getValue() == e){
                iterator.remove();
            }
        }
    }
    
    /**
     * 顺序运行所有event对应的runnable
     */
    protected synchronized void runOrderedByEvent(Class<? extends EventBase> e){
        LinkedList<Runnable> list = getRunnableListByEvent(e);
        if(list != null){
            for (Runnable v: list) {
                v.run();
            }
        }
    }
    
    
    //******mark controlled相关,
    public IControlled getControlled() {
        return controlled;
    }

    public void setControlled(IControlled controlled) {
        this.controlled = controlled;
    }

    /**
     * 初始化方法.
     * 重写方法最后调用super.started(isSuccess);
     * 无controlled则不会回调controlled.onControllerStarted.
     */
    public abstract void start(IControlled controlled);

    /**
     * 初始化完成, 同步/异步可用
     * 成功返回true,
     * @param isSuccess
     */
    protected void started(boolean isSuccess){
        if(controlled != null){
           controlled.onControllerStarted(this, isSuccess);
        }
    }

    /**
     * 结束方法
     * 重写方法最前调用super.stopping();
     */
    public abstract void stop();


    protected void stopping(){
        mapViewEvents.clear();
        mapViewIds.clear();
        mapRunnableEvents.clear();
        if(controlled != null){
            controlled.onControllerStoping();
        }
    }
    
    /**
     * 标记CtrlListener
     */
    public interface ICtrlListenerBase{
        
    }
//******mark find/set ***********

    @Nullable
    public Context getContext(){
        return ctx;
    }

    public <T extends Context> T getContext(Class<T> clazz){
        return (T)ctx;
    }

    public boolean hasContext() {
        return ctx != null;
    }
    
    
    @Override
    public String toString() {
        return "ControllerBase{" +
                "ctx=" + ctx +
                ", controlled=" + controlled +
                ", mapViewIds=" + mapViewIds +
                ", mapViewEvents=" + mapViewEvents +
                ", mapRunnableEvents=" + mapRunnableEvents +
                '}';
    }
}
