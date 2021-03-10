package com.desheng.base.time;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ab.debug.AbDebug;
import com.ab.util.Strs;
import com.ab.global.Config;
import com.desheng.base.event.TimeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;




/**
 * Cron线程服务.
 * 通过EventBus来发送TimeOut事件
 *
 * Created by lee on 2017/3/6 0006.
 */
public class SerTimeEngine extends Service {
    public static final String TAG = SerTimeEngine.class.getName();
    
    private volatile boolean timeThreadisRuning;

    private final TimeEvent.TicSecond TIC_SECOND = new TimeEvent.TicSecond();
    private final TimeEvent.TicMinute TIC_MINUTE = new TimeEvent.TicMinute();
    private final TimeEvent.TicHour TIC_HOUR = new TimeEvent.TicHour();

    /**倒计时超时事件保存*/
    private LinkedList<TimeEvent.TimeCountdown> listTimeoutEvent ;
    private LinkedList<String> listTimeoutTags;

    private TimeEngineBinder binder;
    private Thread tictacThread;

    @Override
    public void onCreate() {
        super.onCreate();
        AbDebug.setLocalDebug(TAG, Config.TAG_TIME);
        //启动前台服务
      /* Notification.Builder builder = new Notification.Builder(this)
                .setContentText("咖啡机正在运行");
        startForeground(R.id.id_forgroud_service_notification_yile, builder.build());*/
    
        binder = new TimeEngineBinder();
        listTimeoutTags = new LinkedList<String>();
        listTimeoutEvent = new LinkedList<TimeEvent.TimeCountdown>();
        
        //启动计时线程
        timeThreadisRuning = true;
        tictacThread = new Thread(new TicRunnable());
        tictacThread.start();
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * @param intent
     * @return false 则重新绑定时执行onBind, true 执行 onRebind
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }


    public class TimeEngineBinder extends Binder implements ITimeEngine {

        @Override
        public void registTimeCountdown(String tag, int seconds) {
            if(seconds <= 0){
                AbDebug.error(TAG, "countdown tag [" + tag + "] seconds 不能为0");
                return;
            }
            if(tag == null || (listTimeoutEvent!= null && listTimeoutTags.contains(tag))){
                AbDebug.error(TAG,"countdown tag [" + tag + "] 为空或已注册");
                return;
            }
            synchronized (listTimeoutEvent) {
                TimeEvent.TimeCountdown obj = new TimeEvent.TimeCountdown();
                //剩下cronMillis由事件发送时提供;
                obj.setTotalSeconds(seconds);
                obj.setCount(seconds);
                obj.setTag(tag);
                listTimeoutEvent.add(obj);
                listTimeoutTags.add(tag);
                AbDebug.log(TAG, "倒计时添加: TAG:(" + obj.getTag() + ") count:(" + seconds + ")");
            }
        }

        @Override
        public void unregistTimeCountdownByTag(String tag) {
            if(tag == null || tag.isEmpty() || listTimeoutEvent == null){
                return;
            }
            synchronized (listTimeoutEvent) {
                for (Iterator<TimeEvent.TimeCountdown> iterator = listTimeoutEvent.iterator(); iterator.hasNext(); ) {
                    TimeEvent.TimeCountdown next = iterator.next();
                    if(next != null && tag.equals(next.getTag())){
                        listTimeoutTags.remove(next.getTag());
                        iterator.remove();
                    }
                }
            }
        }
        
        public int getCountLeft(String tag){
            if(Strs.isEmpty(tag) || listTimeoutEvent == null){
               return  -1;
            }
            synchronized (listTimeoutEvent) {
                for (Iterator<TimeEvent.TimeCountdown> iterator = listTimeoutEvent.iterator(); iterator.hasNext(); ) {
                    TimeEvent.TimeCountdown next = iterator.next();
                    if(next != null && tag.equals(next.getTag())){
                       return next.getCount();
                    }
                }
            }
            return -1;
        }

        @Override
        public void shutdownEngine() {
            timeThreadisRuning = false;
            stopSelf();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        timeThreadisRuning = false;
        tictacThread.interrupt();
        binder = null;
        listTimeoutTags = null;
        listTimeoutEvent = null;
    }

    public class TicRunnable implements Runnable {

        private long currMills;
        private long lastMills;
        private Calendar calendar;

        private int prevMinute;

        private int prevHour;

        @Override
        public void run() {
            AbDebug.log(TAG,"开始时间线程");
            calendar = Calendar.getInstance();
            //时序循环
            while(timeThreadisRuning && !tictacThread.isInterrupted()){
                
                try{
                    // 整秒开始刷新1次, 必须放头, 执行过程时间短, 必然在睡眠后执行.
                    // 如时间超过一秒(lastMills-currMills>1000),则连续执行,不睡眠
                    Thread.sleep(lastMills-currMills>1000 ? 0 : 1000 - System.currentTimeMillis()%1000);
                    //System.out.println("校准时间:" + new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()));
    
                    //本次读秒戳
                    currMills = System.currentTimeMillis();
                    calendar.setTimeInMillis(currMills);
    
                    //发送读秒事件. 同一个事件重复利用
                    EventBus.getDefault().post(TIC_SECOND.updateMillis(currMills));
                    //发送读分钟时间, 同一个事件重复利用
                    int currMinute = calendar.get(Calendar.MINUTE);
                    if(currMinute - prevMinute >=1 || currMinute - prevMinute < 0){
                        EventBus.getDefault().post(TIC_MINUTE.updateMillis(currMills));
                        prevMinute = currMinute;
                    }
                    //发送读小时事件, 同一个事件重复利用
                    int currHour = calendar.get(Calendar.HOUR);
                    if(currHour - prevHour >=1 || currHour - prevHour < 0){
                        EventBus.getDefault().post(TIC_HOUR.updateMillis(currMills));
                        prevHour = currHour;
                    }
    
                    //发送倒计时, 发送clone对象
                    //从加入时刻起下一秒开头就开始记时, 并且发第一个事件,
                    //count==0时事件超时, 发送事件并移除对象, 不再发送
                    synchronized (listTimeoutEvent){
                        for (Iterator<TimeEvent.TimeCountdown> iterator = listTimeoutEvent.iterator(); iterator.hasNext(); ) {
                            TimeEvent.TimeCountdown next = iterator.next();
                            TimeEvent.TimeCountdown clone = null;
                            if(next != null){
                                try {
                                    clone = new TimeEvent.TimeCountdown();
                                    clone.setCount(next.getCount());
                                    clone.setCronMillis(currMills);
                                    clone.setTag(next.getTag());
                                    clone.setTotalSeconds(next.getTotalSeconds());
                                    EventBus.getDefault().post(clone);
                                    AbDebug.log(TAG, "倒计时通知: TAG:(" + clone.getTag() + ") count:" + clone.getCount());
                                    if(next.getCount() == 0){
                                        //事件移除
                                        AbDebug.log(TAG, "倒计时移除: TAG:(" + clone.getTag() + ")");
                                        listTimeoutTags.remove(next.getTag());
                                        iterator.remove();
                                    }else{
                                        next.setCount(next.getCount() - 1);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
    
                    lastMills = System.currentTimeMillis();
                }catch (InterruptedException e){
                    break; //程序退出, 打断
                } catch(Exception e){
                    AbDebug.error(TAG, Thread.currentThread(), e);
                }
            }
            
            try{
                listTimeoutEvent.clear();
                listTimeoutTags.clear();
            }catch(Exception e){
                AbDebug.error(TAG, Thread.currentThread(), e);
            }
   
        }
    }
}
