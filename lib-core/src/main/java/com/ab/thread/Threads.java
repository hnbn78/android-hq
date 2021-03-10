package com.ab.thread;

import android.os.SystemClock;

/**
 * Created by lee on 2017/4/13 0013.
 */

public class Threads {
    
    public static <T> T tryForTimeout(long durationMillis,long sleepMillis, ValueRunnable<T> runnable) {
        long currMillis = System.currentTimeMillis();
        long endMillis = currMillis + durationMillis;
        while (currMillis < endMillis && !runnable.willBreak()){
            runnable.run();
            SystemClock.sleep(sleepMillis);
            currMillis = System.currentTimeMillis();
        }
        if(currMillis >= endMillis){
            runnable.timeout();
        }
        return runnable.getValue();
    }
    
    public static <T> T tryForTimes(long maxCount, long sleepMillis, ValueRunnable<T> runnable) {
        int time = 0;
        for (int i = 0; i < maxCount; i++) {
            runnable.run();
            if(runnable.willBreak()){
                break;
            }
            SystemClock.sleep(sleepMillis);
            time = i;
        }
        if(time == maxCount){
            runnable.timeout();
        }
        return runnable.getValue();
    }
}
