package com.desheng.app.toucai.model;

import java.util.List;

public class UpdateContentList {
    private long totalCount;
    private List<UpdateContent> list;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<UpdateContent> getList() {
        return list;
    }

    public void setList(List<UpdateContent> list) {
        this.list = list;
    }

    public class UpdateContent {
        /**
         * id : 46
         * title : 更新内容列表2
         * typeCode : updateContent
         * displayTerminal : {"pcPictureUrl":"","mobileShow":"true","mobilePictureUrl":""}
         * startTime : 1538323200000
         * endTime : 1666108800000
         * state : 0
         * createTime : 1539939845000
         * deleteFlag : 0
         * content : <p>更新内容列表2更新内容列表2更新内容列表2更新内容列表2更新内容列表2更新内容列表2更新内容列表2更新内容列表2更新内容列表2更新内容列表2更新内容列表2更新内容列表2更新内容列表2更新内容列表2</p>
         * remark :
         * displayMap : null
         * pcCheck : null
         * mobileCheck : null
         * pcPictureUrl :
         * mobilePictureUrl :
         * typeName : 更新内容列表
         */

        private String id;
        private String title;
        private String typeCode;
        private String displayTerminal;
        private long startTime;
        private long endTime;
        private int state;
        private long createTime;
        private int deleteFlag;
        private String content;
        private String remark;
        private Object displayMap;
        private Object pcCheck;
        private Object mobileCheck;
        private String pcPictureUrl;
        private String mobilePictureUrl;
        private String typeName;

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

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public String getDisplayTerminal() {
            return displayTerminal;
        }

        public void setDisplayTerminal(String displayTerminal) {
            this.displayTerminal = displayTerminal;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getDeleteFlag() {
            return deleteFlag;
        }

        public void setDeleteFlag(int deleteFlag) {
            this.deleteFlag = deleteFlag;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public Object getDisplayMap() {
            return displayMap;
        }

        public void setDisplayMap(Object displayMap) {
            this.displayMap = displayMap;
        }

        public Object getPcCheck() {
            return pcCheck;
        }

        public void setPcCheck(Object pcCheck) {
            this.pcCheck = pcCheck;
        }

        public Object getMobileCheck() {
            return mobileCheck;
        }

        public void setMobileCheck(Object mobileCheck) {
            this.mobileCheck = mobileCheck;
        }

        public String getPcPictureUrl() {
            return pcPictureUrl;
        }

        public void setPcPictureUrl(String pcPictureUrl) {
            this.pcPictureUrl = pcPictureUrl;
        }

        public String getMobilePictureUrl() {
            return mobilePictureUrl;
        }

        public void setMobilePictureUrl(String mobilePictureUrl) {
            this.mobilePictureUrl = mobilePictureUrl;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }
}