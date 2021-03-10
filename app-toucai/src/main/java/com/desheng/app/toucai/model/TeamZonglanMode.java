package com.desheng.app.toucai.model;

import java.util.List;

public class TeamZonglanMode {

    /**
     * precondition : 3
     * sixActivityRule : [{"agency":"1000","copywriting":"本月个人再投注8000.0元，即可继续享受大股东会员权限","generalAgent":"2000","majorShareholder":"8000","shareholder":"4000","sortNo":"1","type":"effectivePersonalBets"},{"agency":"5000","copywriting":"本月团队再投注40000.0元，即可继续享受大股东会员权限","generalAgent":"10000","majorShareholder":"40000","shareholder":"20000","sortNo":"2","type":"effectiveTeamBets"},{"agency":"6000","copywriting":"本月团队或个人再投注48000.0元，即可继续享受大股东会员权限","generalAgent":"12000","majorShareholder":"48000","shareholder":"24000","sortNo":"3","type":"totalEffectiveBets"},{"agency":"3","copywriting":"本月团队活跃用户为0人，在邀请12人投注即可继续享受大股东会员权益","generalAgent":"5","majorShareholder":"12","shareholder":"9","sortNo":"4","type":"activeNumber"},{"agency":"3","copywriting":"本月团队新增有效用户为0人，在邀请12人投注即可继续享受大股东会员权益","generalAgent":"5","majorShareholder":"12","shareholder":"9","sortNo":"5","type":"numberOfNewUsers"}]
     * userInfo : {"activeCount":0,"addCount":0,"allProceeds":0,"dayProceeds":0,"personalConfirmAmount":0,"point":0.02,"teamConfirmAmount":0,"teamCount":1,"userAgentType":"大股东会员"}
     */

    private int precondition;
    private UserInfoBean userInfo;
    private List<SixActivityRuleBean> sixActivityRule;

    public int getPrecondition() {
        return precondition;
    }

    public void setPrecondition(int precondition) {
        this.precondition = precondition;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public List<SixActivityRuleBean> getSixActivityRule() {
        return sixActivityRule;
    }

    public void setSixActivityRule(List<SixActivityRuleBean> sixActivityRule) {
        this.sixActivityRule = sixActivityRule;
    }

    public static class UserInfoBean {
        /**
         * activeCount : 0
         * addCount : 0
         * allProceeds : 0.0
         * dayProceeds : 0.0
         * personalConfirmAmount : 0.0
         * point : 0.02
         * teamConfirmAmount : 0.0
         * teamCount : 1
         * userAgentType : 大股东会员
         */

        private int activeCount;
        private int addCount;
        private double allProceeds;
        private double dayProceeds;
        private double personalConfirmAmount;
        private double point;
        private double teamConfirmAmount;
        private int teamCount;
        private String userAgentType;

        public int getActiveCount() {
            return activeCount;
        }

        public void setActiveCount(int activeCount) {
            this.activeCount = activeCount;
        }

        public int getAddCount() {
            return addCount;
        }

        public void setAddCount(int addCount) {
            this.addCount = addCount;
        }

        public double getAllProceeds() {
            return allProceeds;
        }

        public void setAllProceeds(double allProceeds) {
            this.allProceeds = allProceeds;
        }

        public double getDayProceeds() {
            return dayProceeds;
        }

        public void setDayProceeds(double dayProceeds) {
            this.dayProceeds = dayProceeds;
        }

        public double getPersonalConfirmAmount() {
            return personalConfirmAmount;
        }

        public void setPersonalConfirmAmount(double personalConfirmAmount) {
            this.personalConfirmAmount = personalConfirmAmount;
        }

        public double getPoint() {
            return point;
        }

        public void setPoint(double point) {
            this.point = point;
        }

        public double getTeamConfirmAmount() {
            return teamConfirmAmount;
        }

        public void setTeamConfirmAmount(double teamConfirmAmount) {
            this.teamConfirmAmount = teamConfirmAmount;
        }

        public int getTeamCount() {
            return teamCount;
        }

        public void setTeamCount(int teamCount) {
            this.teamCount = teamCount;
        }

        public String getUserAgentType() {
            return userAgentType;
        }

        public void setUserAgentType(String userAgentType) {
            this.userAgentType = userAgentType;
        }
    }

}
