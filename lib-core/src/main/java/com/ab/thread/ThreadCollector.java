package com.ab.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.ab.debug.AbDebug;
import com.ab.util.Strs;


/**
 * 线程安全单例模式, 用于管理所有线程
 * Created by Administrator on 2017/3/4 0004.
 */
public class ThreadCollector {
    public static final String TAG = ThreadCollector.class.getName();
    public static final int HANLER_STOP_THREAD = -1;
    public static final int HANLER_RUN_SAFELY = 1;

    private volatile static ThreadCollector ins;

    /**设备控制相关线程*/
    private ThreadGroup deviceGroup = new ThreadGroup("DeviceGroup");

    /**除串口之外线程组*/
    private ThreadGroup appGroup = new ThreadGroup("AppGroup");

    private Handler mainHandler = null;
    private Thread workerThread = null;
    private WorkThreadHandler workerHandler = null;
    private long workTotalCount = 0;
    private long workDoneCount = 0;

    public static ThreadCollector getIns(){
        if(ins == null){
            synchronized (ThreadCollector.class){
                if(ins == null){
                    AbDebug.setLocalDebug(TAG, AbDebug.TAG_THREAD);
                    ins = new ThreadCollector();
                }
            }
        }
        return ins;
    }

    public void startDeviceThread(String name, Runnable runnable){
        Thread thread = new Thread(deviceGroup, runnable, name);
        thread.start();
    }

    public void startAppThread(String name, Runnable runnable){
        Thread t = new Thread(appGroup, runnable, name);
        t.start();
    }
    
    /**
     * 检查同名线程是否已存在
     * @param name
     */
    public boolean isThreadDuplicate(String name){
        boolean isHave = false;
        Thread[] threads = new Thread[deviceGroup.activeCount()];
        deviceGroup.enumerate(threads);
        for (int i = 0; i < deviceGroup.activeCount(); i++) {
            try{
                if(threads[i].getName().equals(name)){
                    isHave = true;
                    break;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(!isHave){
            threads = new Thread[appGroup.activeCount()];
            appGroup.enumerate(threads);
            for (int i = 0; i < appGroup.activeCount(); i++) {
                try{
                    if(threads[i].getName().equals(name)){
                        isHave = true;
                        break;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return isHave;
    }
    
    public Thread getThread(String threadName){
        Thread thread = null;
        Thread[] threads = new Thread[appGroup.activeCount()];
        appGroup.enumerate(threads);
        for (int i = 0; i < appGroup.activeCount(); i++) {
            try{
                if(threads[i].getName().equals(threadName)){
                    thread = threads[i];
                    break;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(thread == null){
            threads = new Thread[deviceGroup.activeCount()];
            deviceGroup.enumerate(threads);
            for (int i = 0; i < deviceGroup.activeCount(); i++) {
                try{
                    if(threads[i].getName().equals(threadName)){
                        thread = threads[i];
                        break;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return thread;
    }
    
    public void shutdownThread(String threadName){
        if(Strs.isEmpty(threadName)){
            return;
        }
        Thread thread = getThread(threadName);
        if(thread != null){
            thread.interrupt();
        }
    }


    /**t
     * 主线程初始化
     */
    public void init(Context ctx){
        mainHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                workerThread = Thread.currentThread();
                Looper.prepare();
                workerHandler = new WorkThreadHandler();
                Looper.loop();
            }
        }).start();
    }

    public void clear(){
        if(mainHandler != null){
            mainHandler.removeCallbacksAndMessages(null);
        }
        if(workerHandler != null){
            workerHandler.removeCallbacksAndMessages(null);
        }
        deviceGroup.interrupt();
        SystemClock.sleep(100);
        appGroup.interrupt();
        SystemClock.sleep(100);
        int count = 0;
       /* while (deviceGroup.activeCount() != 0){
            count ++;
            SystemClock.sleep(100);
            if(count < 100){ //10秒退不出就结束进程
                AbDebug.log(TAG, "device线程组退出延迟:" + info(deviceGroup) + "\n" + ((100-count)/10 + 1) + "秒后杀死进程");
            }else{
                System.exit(0);
            }
        }
        count = 0;
        while (appGroup.activeCount() != 0){
            count ++;
            SystemClock.sleep(100);
            if(count < 100){//10秒退不出就结束进程
                AbDebug.log(TAG, "app线程组退出延迟:" + info(appGroup) + "\n" + ((100-count)/10 + 1) + "秒后杀死进程");
            }else{
                System.exit(0);
            }
        }*/
    }

    /**
     *子线程往主线程发消息
     */
    public void runOnUIThread(Runnable runnable){
        if(mainHandler != null){
            mainHandler.post(runnable);
        }else{
            AbDebug.error(TAG, "runOnUIThread 失败！ ThreadCollector 未在主线程初始化！！！！");
        }
    }

    /**
     *子线程往主线程发消息
     */
    public void postDelayOnUIThread(long millis, Runnable runnable){
        if(mainHandler != null){
            mainHandler.postDelayed(runnable, millis);
        }else{
            AbDebug.error(TAG, "runOnUIThread 失败！ ThreadCollector 未在主线程初始化！！！！");
        }
    }

    /**
     *子线程往主线程发消息
     */
    public void runOnWorkerThread(final Runnable runnable){
        if(workerHandler != null){
            workTotalCount ++;
            workerHandler.post(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                    workDoneCount ++;
                }
            });
        }else{
            AbDebug.error(TAG, "runOnWorkerThread 失败！ WorkerThread 未初始化！！！！");
        }
    }
    
    /**
     *子线程往主线程发消息
     */
    public void runOnWorkerThreadSafely(Runnable runnable){
        if(workerHandler != null){
            workTotalCount ++;
            Message msg = workerHandler.obtainMessage(HANLER_RUN_SAFELY, runnable);
            workerHandler.sendMessage(msg);
        }else{
            AbDebug.error(TAG, "runOnWorkerThread 失败！ WorkerThread 未初始化！！！！");
        }
    }

    /**
     *子线程往主线程发消息
     */
    public void postDelayOnWorkerThread(long millis, final Runnable runnable){
        if(workerHandler != null){
            workTotalCount ++;
            workerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                    workDoneCount ++;
                }
            }, millis);
        }else{
            AbDebug.error(TAG, "runOnWorkerThread 失败！ WorkerThread 未初始化！！！！");
        }
    }
    
    /**
     *子线程往主线程发消息
     */
    public void postDelayOnWorkerThread(String token, long millis, final Runnable runnable){
        if(workerHandler != null){
            workTotalCount ++;
            workerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                    workDoneCount ++;
                }
            }, millis);
        }else{
            AbDebug.error(TAG, "runOnWorkerThread 失败！ WorkerThread 未初始化！！！！");
        }
    }
    
    /**
     *子线程往主线程发消息, 吃掉任何异常
     */
    public void postDelayOnWorkerThreadSafely(long millis, Runnable runnable){
        if(workerHandler != null){
            workTotalCount ++;
            Message msg = workerHandler.obtainMessage(HANLER_RUN_SAFELY, runnable);
            workerHandler.sendMessageDelayed(msg, millis);
        }else{
            AbDebug.error(TAG, "runOnWorkerThread 失败！ WorkerThread 未初始化！！！！");
        }
    }
    
    public void clearMessageOnWorkerThread(int what){
        if (workerHandler != null) {
            workerHandler.removeMessages(what);
        }
    }
    
    public void clearAllOnWorkerThread(Object token){
        if (workerHandler != null) {
            workerHandler.removeCallbacksAndMessages(token);
        }
    }
    
    public void clearAllOnUIThread(Object token) {
        if(mainHandler != null){
            mainHandler.removeCallbacksAndMessages(token);
        }
    }

    public void print(){
        AbDebug.log(AbDebug.TAG_THREAD, info(deviceGroup) + "\n" +  info(appGroup));
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append("********Thread Collector********\n")
                .append("*worker 进行任务数: " + (workTotalCount - workDoneCount) + "\n")
                .append("*worker 总任务数: " + workTotalCount +"\n")
                .append("*worker 完成数:" + workDoneCount + "\n")
                .append(info(appGroup))
                .append(info(deviceGroup))
                .toString();
    }
    
    
    public class WorkThreadHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANLER_RUN_SAFELY:
                    try{
                        ((Runnable)msg.obj).run();
                    }catch(Exception e){
                        e.printStackTrace();
                        AbDebug.error(TAG, "WORKER线程发生异常， 异常被吃掉！！！");
                    }finally {
                        workDoneCount ++;
                    }
                    break;
                case HANLER_STOP_THREAD:
                    removeCallbacksAndMessages(null);
                    getLooper().quit();
                    workerThread = null;
                    workerHandler = null;
                    break;

            };
        }
    }
    
    public String info(ThreadGroup group){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("*Number of Threads: %d\n", group.activeCount()));
        //复制group里面的thread信息
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads);
        for (int i = 0; i < group.activeCount(); i++) {
            try{
                sb.append(String.format("*Thread [%s]: %d, %s\n", threads[i].getName(),threads[i].getPriority(), threads[i].getState()));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    public WorkThreadHandler getWorkerHandler() {
        return workerHandler;
    }
}
