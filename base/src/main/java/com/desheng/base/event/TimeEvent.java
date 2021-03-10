package com.desheng.base.event;

/**
 * Created by lee on 2017/3/6 0006.
 */

public class TimeEvent {

    /**
     * 每秒事件. 使用中只有一个对象
     */
    public static class TicSecond extends EventBase{
        /**消息发出时间, 事件监听中只用不改*/
        private long cronMillis = -1;
        
        public TicSecond updateMillis(long millis){
            cronMillis = millis;
            return this;
        }

        public long getCronMillis() {
            return cronMillis;
        }
    }

    /**
     * 分钟开始
     * 每分钟事件. 使用中只有一个对象
     */
    public static class TicMinute extends EventBase{
        /**消息发出时间, 事件监听中只用不改*/
        private long cronMillis = -1;

        public TicMinute updateMillis(long millis){
            cronMillis = millis;
            return this;
        }

        public long getCronMillis() {
            return cronMillis;
        }

    }

    /**
     * 小时开始
     * 每小时事件. 使用中只有一个对象
     */
    public static class TicHour extends EventBase{
        /**消息发出时间, 事件监听中只用不改*/
        private long cronMillis = -1;

        public TicHour updateMillis(long millis){
            cronMillis = millis;
            return this;
        }

        public long getCronMillis() {
            return cronMillis;
        }
    }


    /**
     * 超时事件基类.按秒计.
     */
    public static class TimeCountdown extends EventBase{
        private long cronMillis = -1;
        private int count = -1;
        private int totalSeconds = -1;
        private String tag = null;


        public long getCronMillis() {
            return cronMillis;
        }

        public void setCronMillis(long cronMillis) {
            this.cronMillis = cronMillis;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getTotalSeconds() {
            return totalSeconds;
        }

        public void setTotalSeconds(int totalSeconds) {
            this.totalSeconds = totalSeconds;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        @Override
        public String toString() {
            return "TimeCountdownBase{" +
                    "cronMillis=" + cronMillis +
                    ", count=" + count +
                    ", totalSeconds=" + totalSeconds +
                    ", tag='" + tag + '\'' +
                    '}';
        }
    }
}
