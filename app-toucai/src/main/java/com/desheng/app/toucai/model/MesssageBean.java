package com.desheng.app.toucai.model;

import java.io.Serializable;

/**
 * Created by user on 2018/3/27.
 */

public class MesssageBean   implements Serializable{
      private String id;

      private String title;
      private String content;
      private String msgTypeStr;
      private String stateStr;
      private String remark;
      private String userLevelStr;
      private String userMessageId;
      private String createUser;
      private String lastUpdateTimeString;
      private String createDateString;
      private String readDateString;
      private String readStateStr;

      public String getId() {
            return id;
      }

      public void setId(String id) {
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

      public String getRemark() {
            return remark;
      }

      public void setRemark(String remark) {
            this.remark = remark;
      }

      public String getUserLevelStr() {
            return userLevelStr;
      }

      public void setUserLevelStr(String userLevelStr) {
            this.userLevelStr = userLevelStr;
      }

      public String getUserMessageId() {
            return userMessageId;
      }

      public void setUserMessageId(String userMessageId) {
            this.userMessageId = userMessageId;
      }

      public String getCreateUser() {
            return createUser;
      }

      public void setCreateUser(String createUser) {
            this.createUser = createUser;
      }

      public String getLastUpdateTimeString() {
            return lastUpdateTimeString;
      }

      public void setLastUpdateTimeString(String lastUpdateTimeString) {
            this.lastUpdateTimeString = lastUpdateTimeString;
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
}
