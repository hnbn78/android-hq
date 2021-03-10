package com.desheng.app.toucai.model;

public class ContactsBackMsgMode {

    /**
     * text : 哦哦噢噢噢哦哦
     * sendInviteCode : 1fzxsa
     * createTime : 1550157855568
     * msgType : 0
     */

    private String text;
    private String sendUin;
    private long createTime;
    private int msgType;
    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSendInviteCode() {
        return sendUin;
    }

    public void setSendInviteCode(String sendInviteCode) {
        this.sendUin = sendInviteCode;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    @Override
    public String toString() {
        return "ContactsBackMsgMode{" +
                "text='" + text + '\'' +
                ", sendInviteCode='" + sendUin + '\'' +
                ", createTime=" + createTime +
                ", msgType=" + msgType +
                '}';
    }
}
