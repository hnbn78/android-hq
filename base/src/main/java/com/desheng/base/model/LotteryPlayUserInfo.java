package com.desheng.base.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by lee on 2018/3/15.
 */

public class LotteryPlayUserInfo implements Serializable{
    
    private HashMap<String, MathodBean> method;
    private InfoBean info;
    private int gameId;
    private int code;
    private double point;
    private ConfigBean config;

    @Override
    public String toString() {
        return "LotteryPlayUserInfo{" +
                "gameId=" + gameId +
                ", code=" + code +
                '}';
    }

    public InfoBean getInfo() {
        return info;
    }
    
    public void setInfo(InfoBean info) {
        this.info = info;
    }
    
    public int getGameId() {
        return gameId;
    }
    
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public double getPoint() {
        return point;
    }
    
    public void setPoint(double point) {
        this.point = point;
    }
    
    public ConfigBean getConfig() {
        return config;
    }
    
    public void setConfig(ConfigBean config) {
        this.config = config;
    }
    
    public HashMap<String, MathodBean> getMethod() {
        return method;
    }
    
    
    public static class MathodBean implements Serializable{
        /**
         * name : 后三特殊号
         * iminRecord : 0
         * maxRecord : 0
         * totalRecord : 0
         * status : 0
         * bonus : 1900.0
         * oooNums : 0
         * oooBonus : 20000
         * methodHelp : null
         * methodExample : null
         * x : 2000.00000
         */
        
        private String name;
        private int iminRecord;
        private int maxRecord;
        private int totalRecord;
        private int status;
        private String bonus;
        private int oooNums;
        private int oooBonus;
        private Object methodHelp;
        private Object methodExample;
        private String x;
        private int longhuType;//1:龙虎，2:新龙虎

        public int getLonghuType() {
            return longhuType;
        }

        public void setLonghuType(int longhuType) {
            this.longhuType = longhuType;
        }

        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public int getIminRecord() {
            return iminRecord;
        }
        
        public void setIminRecord(int iminRecord) {
            this.iminRecord = iminRecord;
        }
        
        public int getMaxRecord() {
            return maxRecord;
        }
        
        public void setMaxRecord(int maxRecord) {
            this.maxRecord = maxRecord;
        }
        
        public int getTotalRecord() {
            return totalRecord;
        }
        
        public void setTotalRecord(int totalRecord) {
            this.totalRecord = totalRecord;
        }
        
        public int getStatus() {
            return status;
        }
        
        public void setStatus(int status) {
            this.status = status;
        }
        
        public String getBonus() {
            return bonus;
        }
        
        public void setBonus(String bonus) {
            this.bonus = bonus;
        }
        
        public int getOooNums() {
            return oooNums;
        }
        
        public void setOooNums(int oooNums) {
            this.oooNums = oooNums;
        }
        
        public int getOooBonus() {
            return oooBonus;
        }
        
        public void setOooBonus(int oooBonus) {
            this.oooBonus = oooBonus;
        }
        
        public Object getMethodHelp() {
            return methodHelp;
        }
        
        public void setMethodHelp(Object methodHelp) {
            this.methodHelp = methodHelp;
        }
        
        public Object getMethodExample() {
            return methodExample;
        }
        
        public void setMethodExample(Object methodExample) {
            this.methodExample = methodExample;
        }
        
        public String getX() {
            return x;
        }
        
        public void setX(String x) {
            this.x = x;
        }
    }
    
    
    public static class InfoBean implements Serializable {
        /**
         * id : 0
         * shortName : XJSSC
         * showName : 新疆时时彩
         * frequency : high
         * type : 1
         * times : 120
         * stopDelay : 0
         * downCode : 0
         * fenDownCode : 0
         * liDownCode : 0
         * maxBonus : 400000
         * sort : 0
         * status : 0
         * description : null
         */
        
        private int id;
        private String shortName;
        private String showName;
        private String frequency;
        private int type;
        private int times;
        private int stopDelay;
        private int downCode;
        private int fenDownCode;
        private int liDownCode;
        private int maxBonus;
        private int sort;
        private int status;
        private Object description;

        @Override
        public String toString() {
            return "InfoBean{" +
                    "id=" + id +
                    ", shortName='" + shortName + '\'' +
                    ", showName='" + showName + '\'' +
                    ", frequency='" + frequency + '\'' +
                    ", type=" + type +
                    ", times=" + times +
                    ", stopDelay=" + stopDelay +
                    ", downCode=" + downCode +
                    ", fenDownCode=" + fenDownCode +
                    ", liDownCode=" + liDownCode +
                    ", maxBonus=" + maxBonus +
                    ", sort=" + sort +
                    ", status=" + status +
                    ", description=" + description +
                    '}';
        }

        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public String getShortName() {
            return shortName;
        }
        
        public void setShortName(String shortName) {
            this.shortName = shortName;
        }
        
        public String getShowName() {
            return showName;
        }
        
        public void setShowName(String showName) {
            this.showName = showName;
        }
        
        public String getFrequency() {
            return frequency;
        }
        
        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }
        
        public int getType() {
            return type;
        }
        
        public void setType(int type) {
            this.type = type;
        }
        
        public int getTimes() {
            return times;
        }
        
        public void setTimes(int times) {
            this.times = times;
        }
        
        public int getStopDelay() {
            return stopDelay;
        }
        
        public void setStopDelay(int stopDelay) {
            this.stopDelay = stopDelay;
        }
        
        public int getDownCode() {
            return downCode;
        }
        
        public void setDownCode(int downCode) {
            this.downCode = downCode;
        }
        
        public int getFenDownCode() {
            return fenDownCode;
        }
        
        public void setFenDownCode(int fenDownCode) {
            this.fenDownCode = fenDownCode;
        }
        
        public int getLiDownCode() {
            return liDownCode;
        }
        
        public void setLiDownCode(int liDownCode) {
            this.liDownCode = liDownCode;
        }
        
        public int getMaxBonus() {
            return maxBonus;
        }
        
        public void setMaxBonus(int maxBonus) {
            this.maxBonus = maxBonus;
        }
        
        public int getSort() {
            return sort;
        }
        
        public void setSort(int sort) {
            this.sort = sort;
        }
        
        public int getStatus() {
            return status;
        }
        
        public void setStatus(int status) {
            this.status = status;
        }
        
        public Object getDescription() {
            return description;
        }
        
        public void setDescription(Object description) {
            this.description = description;
        }
    }
    
    public static class ConfigBean implements Serializable{
        /**
         * sysCode : 1910
         * sysPoint : 0.5
         * sysUnitMoney : 1
         * maxBetCode : 1910
         * minBetCode : 1900
         * baseBetCode : 1900
         * sysQuotaRange : null
         * sysAmountRange : null
         */
        
        private int sysCode;
        private double sysPoint;
        private int sysUnitMoney;
        private int maxBetCode;
        private int minBetCode;
        private int baseBetCode;
        private Object sysQuotaRange;
        private Object sysAmountRange;
        
        public int getSysCode() {
            return sysCode;
        }
        
        public void setSysCode(int sysCode) {
            this.sysCode = sysCode;
        }
        
        public double getSysPoint() {
            return sysPoint;
        }
        
        public void setSysPoint(double sysPoint) {
            this.sysPoint = sysPoint;
        }
        
        public int getSysUnitMoney() {
            return sysUnitMoney;
        }
        
        public void setSysUnitMoney(int sysUnitMoney) {
            this.sysUnitMoney = sysUnitMoney;
        }
        
        public int getMaxBetCode() {
            return maxBetCode;
        }
        
        public void setMaxBetCode(int maxBetCode) {
            this.maxBetCode = maxBetCode;
        }
        
        public int getMinBetCode() {
            return minBetCode;
        }
        
        public void setMinBetCode(int minBetCode) {
            this.minBetCode = minBetCode;
        }
        
        public int getBaseBetCode() {
            return baseBetCode;
        }
        
        public void setBaseBetCode(int baseBetCode) {
            this.baseBetCode = baseBetCode;
        }
        
        public Object getSysQuotaRange() {
            return sysQuotaRange;
        }
        
        public void setSysQuotaRange(Object sysQuotaRange) {
            this.sysQuotaRange = sysQuotaRange;
        }
        
        public Object getSysAmountRange() {
            return sysAmountRange;
        }
        
        public void setSysAmountRange(Object sysAmountRange) {
            this.sysAmountRange = sysAmountRange;
        }
    }
}
