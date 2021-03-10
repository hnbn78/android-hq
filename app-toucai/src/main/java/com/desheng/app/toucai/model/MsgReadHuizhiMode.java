package com.desheng.app.toucai.model;

public class MsgReadHuizhiMode {

    /**
     * receiverInviteCode : 1fzxsa
     * endMsgId : 978
     */

    private String receiverUin;
    private int endMsgId;

    public String getReceiverInviteCode() {
        return receiverUin;
    }

    public void setReceiverInviteCode(String receiverInviteCode) {
        this.receiverUin = receiverInviteCode;
    }

    public int getEndMsgId() {
        return endMsgId;
    }

    public void setEndMsgId(int endMsgId) {
        this.endMsgId = endMsgId;
    }
}
