package com.desheng.base.model;

import java.io.Serializable;
import java.util.List;

public class ListMessageBean {
    /**
     * totalCount : 2
     * list : [{"id":21,"title":"cghd","content":"dh","msgTypeStr":"用户消息","stateStr":"待审核","remark":null,"userLevelStr":"指定用户","userMessageId":22,"createUser":"test0001a","createDateString":"2018-03-04 13:04:00","readDateString":"2018-03-05 10:00:38","readStateStr":"已读","lastUpdateTimeString":"2018-03-04 13:04:00"},{"id":18,"title":"用户提现一审拒绝","content":"绝","msgTypeStr":"用户消息","stateStr":"已审核","remark":null,"userLevelStr":"指定用户","userMessageId":19,"createUser":"系统消息","createDateString":"2018-02-27 10:22:31","readDateString":"2018-02-27 11:10:34","readStateStr":"已读","lastUpdateTimeString":"2018-02-27 10:22:31"}]
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

    public static class ListBean implements Serializable{
        /**
         * id : 21
         * title : cghd
         * content : dh
         * msgTypeStr : 用户消息
         * stateStr : 待审核
         * remark : null
         * userLevelStr : 指定用户
         * userMessageId : 22
         * createUser : test0001a
         * createDateString : 2018-03-04 13:04:00
         * readDateString : 2018-03-05 10:00:38
         * readStateStr : 已读
         * lastUpdateTimeString : 2018-03-04 13:04:00
         */

        private int id;
        private String title;
        private String content;
        private String msgTypeStr;
        private String stateStr;
        private Object remark;
        private String userLevelStr;
        private int userMessageId;
        private String createUser;
        private String createDateString;
        private String readDateString;
        private String readStateStr;
        private String lastUpdateTimeString;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMsgTypeStr() {
            return msgTypeStr;
        }

        public void setMsgTypeStr(String msgTypeStr) {
            this.msgTypeStr = msgTypeStr;
        }

        public String getStateStr() {
            return stateStr;
        }

        public void setStateStr(String stateStr) {
            this.stateStr = stateStr;
        }

        public Object getRemark() {
            return remark;
        }

        public void setRemark(Object remark) {
            this.remark = remark;
        }

        public String getUserLevelStr() {
            return userLevelStr;
        }

        public void setUserLevelStr(String userLevelStr) {
            this.userLevelStr = userLevelStr;
        }

        public int getUserMessageId() {
            return userMessageId;
        }

        public void setUserMessageId(int userMessageId) {
            this.userMessageId = userMessageId;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getCreateDateString() {
            return createDateString;
        }

        public void setCreateDateString(String createDateString) {
            this.createDateString = createDateString;
        }

        public String getReadDateString() {
            return readDateString;
        }

        public void setReadDateString(String readDateString) {
            this.readDateString = readDateString;
        }

        public String getReadStateStr() {
            return readStateStr;
        }

        public void setReadStateStr(String readStateStr) {
            this.readStateStr = readStateStr;
        }

        public String getLastUpdateTimeString() {
            return lastUpdateTimeString;
        }

        public void setLastUpdateTimeString(String lastUpdateTimeString) {
            this.lastUpdateTimeString = lastUpdateTimeString;
        }
    }
}
