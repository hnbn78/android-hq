package com.desheng.app.toucai.model;

public class LatestMsgComeRefreshMode {

    int newMsgNotReadCount;
    private String text;
    private String sendInviteCode;
    private long createTimeStr;
    private int msgType;
    private int id;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSendInviteCode() {
        return sendInviteCode;
    }

    public void setSendInviteCode(String sendInviteCode) {
        this.sendInviteCode = sendInviteCode;
    }

    public long getCreateTime() {
        return createTimeStr;
    }

    public void setCreateTime(long createTime) {
        this.createTimeStr = createTime;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNewMsgNotReadCount() {
        return newMsgNotReadCount;
    }

    public void setNewMsgNotReadCount(int newMsgNotReadCount) {
        this.newMsgNotReadCount = newMsgNotReadCount;
    }
}
