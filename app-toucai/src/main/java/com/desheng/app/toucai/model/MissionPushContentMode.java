package com.desheng.app.toucai.model;

public class MissionPushContentMode {

    /**
     * content : 消息推送消息推送消息推送消息推送消息推送
     * linkUrl : {"linkUrl":"/activity","type":2}
     */

    private String content;
    private LinkUrlBean linkUrl;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LinkUrlBean getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(LinkUrlBean linkUrl) {
        this.linkUrl = linkUrl;
    }

    public static class LinkUrlBean {
        /**
         * linkUrl : /activity
         * type : 2
         */

        private String linkUrl;
        private int type;

        public String getLinkUrl() {
            return linkUrl;
        }

        public void setLinkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
