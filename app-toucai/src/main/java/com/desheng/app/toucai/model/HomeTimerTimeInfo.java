package com.desheng.app.toucai.model;

public class HomeTimerTimeInfo {

    /**
     * next : {"periodNumber":"20181223-044","period":"20181223-044","delayTimeInterval":10,"awardTimeInterval":-165,"awardTime":"2018-12-23 01:28:00","periodDate":"20181223-044"}
     * current : {"awardNumbers":"02,06,05,03,01,04,09,07,10,08","periodNumber":"20181223-043","period":"20181223-043","awardTime":"2018-12-23 01:26:00","periodDate":"20181223-043"}
     * time : 1545499672
     */

    private NextBean next;
    private CurrentBean current;
    private int time;

    public NextBean getNext() {
        return next;
    }

    public void setNext(NextBean next) {
        this.next = next;
    }

    public CurrentBean getCurrent() {
        return current;
    }

    public void setCurrent(CurrentBean current) {
        this.current = current;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public static class NextBean {
        /**
         * periodNumber : 20181223-044
         * period : 20181223-044
         * delayTimeInterval : 10
         * awardTimeInterval : -165
         * awardTime : 2018-12-23 01:28:00
         * periodDate : 20181223-044
         */

        private String periodNumber;
        private String period;
        private int delayTimeInterval;
        private int awardTimeInterval;
        private String awardTime;
        private String periodDate;

        public String getPeriodNumber() {
            return periodNumber;
        }

        public void setPeriodNumber(String periodNumber) {
            this.periodNumber = periodNumber;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public int getDelayTimeInterval() {
            return delayTimeInterval;
        }

        public void setDelayTimeInterval(int delayTimeInterval) {
            this.delayTimeInterval = delayTimeInterval;
        }

        public int getAwardTimeInterval() {
            return awardTimeInterval;
        }

        public void setAwardTimeInterval(int awardTimeInterval) {
            this.awardTimeInterval = awardTimeInterval;
        }

        public String getAwardTime() {
            return awardTime;
        }

        public void setAwardTime(String awardTime) {
            this.awardTime = awardTime;
        }

        public String getPeriodDate() {
            return periodDate;
        }

        public void setPeriodDate(String periodDate) {
            this.periodDate = periodDate;
        }
    }

    public static class CurrentBean {
        /**
         * awardNumbers : 02,06,05,03,01,04,09,07,10,08
         * periodNumber : 20181223-043
         * period : 20181223-043
         * awardTime : 2018-12-23 01:26:00
         * periodDate : 20181223-043
         */

        private String awardNumbers;
        private String periodNumber;
        private String period;
        private String awardTime;
        private String periodDate;

        public String getAwardNumbers() {
            return awardNumbers;
        }

        public void setAwardNumbers(String awardNumbers) {
            this.awardNumbers = awardNumbers;
        }

        public String getPeriodNumber() {
            return periodNumber;
        }

        public void setPeriodNumber(String periodNumber) {
            this.periodNumber = periodNumber;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getAwardTime() {
            return awardTime;
        }

        public void setAwardTime(String awardTime) {
            this.awardTime = awardTime;
        }

        public String getPeriodDate() {
            return periodDate;
        }

        public void setPeriodDate(String periodDate) {
            this.periodDate = periodDate;
        }
    }
}
