package com.desheng.app.toucai.model;

public class LastMessageImContactsMode {

    /**
     * inviteCode : 1fy7cd
     * lastMessage : {"createTime":"2019-02-15 14:20:24","id":103,"msgType":0,"status":0,"text":"two哦哦哦可哦路咯涂抹吐了咯图蹦吧"}
     */

    private String uin;
    private LastMessageBean lastMessage;

    public String getInviteCode() {
        return uin;
    }

    public void setInviteCode(String inviteCode) {
        this.uin = inviteCode;
    }

    public LastMessageBean getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(LastMessageBean lastMessage) {
        this.lastMessage = lastMessage;
    }

    public static class LastMessageBean {
        /**
         * createTime : 2019-02-15 14:20:24
         * id : 103
         * msgType : 0
         * status : 0
         * text : two哦哦哦可哦路咯涂抹吐了咯图蹦吧
         */

        private String createTime;
        private int id;
        private int msgType;
        private int status;
        private String text;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
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
}
