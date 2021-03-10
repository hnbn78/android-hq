package com.desheng.app.toucai.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class NewUserMission {

    /**
     * startTime : 2018-09-04 00:00:00
     * noCycleActivityRule : [{"sortNo":"1","eventCode":"bindName","prizeAmount":"500","conTimes":"200","desc":"30000"},{"sortNo":"2","eventCode":"bindBankCard","prizeAmount":"600","conTimes":"300","desc":"2215611"}]
     * endTime : 2018-09-19 00:00:00
     * title : 新手活动2
     * content : <p><em>阿斯蒂芬阿斯蒂</em>芬阿斯<strong>蒂芬阿斯蒂芬阿斯蒂芬阿斯蒂芬阿撒</strong>地方撒地方安抚</p>
     */

    public String currentTime;
    public String bizType;
    public String imagePath;
    public String startTime;
    public String noCycleActivityRule;
    public List<MissionStep> listSteps;
    public String endTime;
    public String title;
    public int receiveType;
    public String content;
    public String id;
    public String outLink; // (专题页链接)

    @Override
    public String toString() {
        return "NewUserMission{" +
                "currentTime='" + currentTime + '\'' +
                ", bizType='" + bizType + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", startTime='" + startTime + '\'' +
                ", noCycleActivityRule='" + noCycleActivityRule + '\'' +
                ", listSteps=" + listSteps +
                ", endTime='" + endTime + '\'' +
                ", title='" + title + '\'' +
                ", receiveType=" + receiveType +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", outLink='" + outLink + '\'' +
                '}';
    }

    public void parseStepList() {
        listSteps = new Gson().fromJson(noCycleActivityRule, new TypeToken<ArrayList<MissionStep>>() {
        }.getType());
    }

    public static class MissionStep {

        /**
         * sortNo : 1
         * eventCode : bindName
         * prizeAmount : 500
         * conTimes : 200
         * desc : 30000
         */

        public String sortNo;
        public String eventCode;
        public String prizeAmount;
        public String conTimes;
        public String desc;

    }
}
