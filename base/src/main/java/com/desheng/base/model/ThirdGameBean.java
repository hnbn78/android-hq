package com.desheng.base.model;

import java.util.List;

public class ThirdGameBean {



    /**
     * totalCount : 1
     * list : [{"userId":10,"userType":null,"gameType":"IMSB","userName":"cslee01","agentName":"cesh0001","topAgentName":null,"betDateBegin":null,"betDateEnd":null,"isCount":0,"page":0,"size":10,"itemStart":null,"agentId":2,"topAgentId":null,"date":1527782400000,"confirmAmount":20,"awardAmount":73.4,"rebateAmount":0,"bonusAmount":0,"profitAmount":53.4,"transferInAmount":30,"transferOutAmount":0,"agentLinks":null,"gameName":"IMSB"}]
     */

    private int totalCount;
    private List<ListBean> list;
    /**
     * list : [{"agentId":2475,"agentName":"tc888888","awardAmount":2578.3,"bonusAmount":0,"calcTime":1546012800000,"confirmAmount":3104.15,"date":"2018-12-29","gameName":"炸金花","gameType":"1001","id":10,"profitAmount":-525.85,"rebateAmount":67.85,"topAgentId":2475,"topAgentName":"tc888888","transferInAmount":1080,"transferOutAmount":0,"userId":46496,"userName":"x03x03","userType":0},{"agentId":2475,"agentName":"tc888888","awardAmount":5424.5,"bonusAmount":0,"calcTime":1546012800000,"confirmAmount":5492.25,"date":"2018-12-29","gameName":"二十一点","gameType":"27001","id":16,"profitAmount":-67.75,"rebateAmount":142.75,"topAgentId":2475,"topAgentName":"tc888888","transferInAmount":1080,"transferOutAmount":0,"userId":46496,"userName":"x03x03","userType":0},{"agentId":2475,"agentName":"tc888888","awardAmount":57,"bonusAmount":0,"calcTime":1546012800000,"confirmAmount":28.5,"date":"2018-12-29","gameName":"斗地主","gameType":"9001","id":22,"profitAmount":28.5,"rebateAmount":1.5,"topAgentId":2475,"topAgentName":"tc888888","transferInAmount":1080,"transferOutAmount":0,"userId":46496,"userName":"x03x03","userType":0}]
     * statistics : {"awardAmount":8059.8,"betAmount":8624.9,"confirmAmount":8624.9}
     */

    private StatisticsBean statistics;

    public StatisticsBean getStatistics() {
        return statistics;
    }

    public void setStatistics(StatisticsBean statistics) {
        this.statistics = statistics;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * userId : 10
         * userType : null
         * gameType : IMSB
         * userName : cslee01
         * agentName : cesh0001
         * topAgentName : null
         * betDateBegin : null
         * betDateEnd : null
         * isCount : 0
         * page : 0
         * size : 10
         * itemStart : null
         * agentId : 2
         * topAgentId : null
         * date : 1527782400000
         * confirmAmount : 20.0 //有效投注
         * awardAmount : 73.4
         * rebateAmount : 0.0
         * bonusAmount : 0.0
         * profitAmount : 53.4 //盈亏
         * transferInAmount : 30.0//转入
         * transferOutAmount : 0.0//转出
         * agentLinks : null
         * gameName : IMSB
         *
         */

        private int userId;
        private String userType;
        private String gameType;
        private String userName;
        private String agentName;
        private String topAgentName;
        private String betDateBegin;
        private String betDateEnd;
        private String isCount;
        private int page;
        private int size;
        private String itemStart;
        private int agentId;
        private String topAgentId;
        private String date;
        private String calcTime;
        private double confirmAmount;
        private double awardAmount;
        private double rebateAmount;
        private double bonusAmount;
        private double profitAmount;
        private double transferInAmount;
        private double transferOutAmount;
        private String agentLinks;
        private String gameName;

        public String getCalcTime() {
            return calcTime;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getGameType() {
            return gameType;
        }

        public void setGameType(String gameType) {
            this.gameType = gameType;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public String getTopAgentName() {
            return topAgentName;
        }

        public void setTopAgentName(String topAgentName) {
            this.topAgentName = topAgentName;
        }

        public String getBetDateBegin() {
            return betDateBegin;
        }

        public void setBetDateBegin(String betDateBegin) {
            this.betDateBegin = betDateBegin;
        }

        public String getBetDateEnd() {
            return betDateEnd;
        }

        public void setBetDateEnd(String betDateEnd) {
            this.betDateEnd = betDateEnd;
        }

        public String getIsCount() {
            return isCount;
        }

        public void setIsCount(String isCount) {
            this.isCount = isCount;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getItemStart() {
            return itemStart;
        }

        public void setItemStart(String itemStart) {
            this.itemStart = itemStart;
        }

        public int getAgentId() {
            return agentId;
        }

        public void setAgentId(int agentId) {
            this.agentId = agentId;
        }

        public String getTopAgentId() {
            return topAgentId;
        }

        public void setTopAgentId(String topAgentId) {
            this.topAgentId = topAgentId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getConfirmAmount() {
            return confirmAmount;
        }

        public void setConfirmAmount(double confirmAmount) {
            this.confirmAmount = confirmAmount;
        }

        public double getAwardAmount() {
            return awardAmount;
        }

        public void setAwardAmount(double awardAmount) {
            this.awardAmount = awardAmount;
        }

        public double getRebateAmount() {
            return rebateAmount;
        }

        public void setRebateAmount(double rebateAmount) {
            this.rebateAmount = rebateAmount;
        }

        public double getBonusAmount() {
            return bonusAmount;
        }

        public void setBonusAmount(double bonusAmount) {
            this.bonusAmount = bonusAmount;
        }

        public double getProfitAmount() {
            return profitAmount;
        }

        public void setProfitAmount(double profitAmount) {
            this.profitAmount = profitAmount;
        }

        public double getTransferInAmount() {
            return transferInAmount;
        }

        public void setTransferInAmount(double transferInAmount) {
            this.transferInAmount = transferInAmount;
        }

        public double getTransferOutAmount() {
            return transferOutAmount;
        }

        public void setTransferOutAmount(double transferOutAmount) {
            this.transferOutAmount = transferOutAmount;
        }

        public String getAgentLinks() {
            return agentLinks;
        }

        public void setAgentLinks(String agentLinks) {
            this.agentLinks = agentLinks;
        }

        public String getGameName() {
            return gameName;
        }

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }
    }

    public static class StatisticsBean {
        /**
         * awardAmount : 8059.8
         * betAmount : 8624.9
         * confirmAmount : 8624.9
         */

        private double awardAmount;
        private double betAmount;
        private double confirmAmount;

        public double getAwardAmount() {
            return awardAmount;
        }

        public void setAwardAmount(double awardAmount) {
            this.awardAmount = awardAmount;
        }

        public double getBetAmount() {
            return betAmount;
        }

        public void setBetAmount(double betAmount) {
            this.betAmount = betAmount;
        }

        public double getConfirmAmount() {
            return confirmAmount;
        }

        public void setConfirmAmount(double confirmAmount) {
            this.confirmAmount = confirmAmount;
        }
    }
}
