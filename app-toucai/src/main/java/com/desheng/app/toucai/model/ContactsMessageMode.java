package com.desheng.app.toucai.model;

public class ContactsMessageMode {
    String sendUid;
    String targetUin;
    String toParent;
    int msgType;
    String message;

    public ContactsMessageMode(String sendUid, String inviteCode, String toParent, int msgType, String message) {
        this.sendUid = sendUid;
        this.targetUin = inviteCode;
        this.toParent = toParent;
        this.msgType = msgType;
        this.message = message;
    }

    public String getSendUid() {
        return sendUid;
    }

    public void setSendUid(String sendUid) {
        this.sendUid = sendUid;
    }

    public String getInviteCode() {
        return targetUin;
    }

    public void setInviteCode(String inviteCode) {
        this.targetUin = inviteCode;
    }

    public String getToParent() {
        return toParent;
    }

    public void setToParent(String toParent) {
        this.toParent = toParent;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
