package com.desheng.app.toucai.model;

public class ContactsHistoryMode {

    /**
     * createTime : 2019-02-15 10:59:24
     * id : 94
     * msgType : 0
     * status : 0
     * text : 榻榻米白金卡跨服咯
     */

    private long createTime;
    private int id;
    private int msgType;
    private int status;
    private String text;
    private int isSelf;

    public int getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(int isSelf) {
        this.isSelf = isSelf;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
