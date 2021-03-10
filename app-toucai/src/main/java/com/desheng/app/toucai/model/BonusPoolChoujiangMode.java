package com.desheng.app.toucai.model;

import java.util.List;

public class BonusPoolChoujiangMode {

    /**
     * list : [{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"41183","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"32422","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"26608","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"48150","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"94704","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"34296","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"68258","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"47413","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"41162","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"01702","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"82386","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"06933","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"85673","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"44131","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"00795","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"38575","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"73416","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"17635","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"44729","status":0},{"createTime":1547639206000,"activityIssueNo":2019001,"activityNumber":"47490","status":0}]
     * totalCount : 65
     */

    private int totalCount;
    private List<ListBean> list;

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
         * createTime : 1547639206000
         * activityIssueNo : 2019001
         * activityNumber : 41183
         * status : 0
         */

        private long createTime;
        private int activityIssueNo;
        private String activityNumber;
        private int status;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getActivityIssueNo() {
            return activityIssueNo;
        }

        public void setActivityIssueNo(int activityIssueNo) {
            this.activityIssueNo = activityIssueNo;
        }

        public String getActivityNumber() {
            return activityNumber;
        }

        public void setActivityNumber(String activityNumber) {
            this.activityNumber = activityNumber;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
