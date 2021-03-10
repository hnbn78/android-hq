package com.desheng.app.toucai.model;

public class DajiangPushEventModel {
    /**
     * cn : x03x03
     * content : 恭喜x03x03 喜中多伦多30秒，中奖3920元
     * userId : 46496
     * channelType : 0
     * deviceType : 0
     * messageType : 3
     * title : 中大奖啦
     */

    private String cn;
    private String content;
    private int userId;
    private int channelType;
    private int deviceType;
    private String messageType;
    private String title;

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getChannelType() {
        return channelType;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
