package com.desheng.app.toucai.model;

public class GetDesignHostMode {

    /**
     * prefix : toucai-appJson4
     * name : 头彩json4
     * shortname : 头彩json4
     * api : http://tc305.com
     * ipapi : http://tc305.com
     * isip : 0
     * urls : http://tc305.com
     * kefu : http://v88.live800.com/live800/chatClient/chatbox.jsp?companyID=925834&configID=8015&jid=8547731414&skillId=700
     * demoUrl : http://vip.tc508.com/yx/rgv/demo
     * androidQRCode : /upload/qrCode/android_qr_code_3.png
     * iosQRCode : /upload/qrCode/ios_qr_code_3.png
     * logo : /data/upload/logo.png
     * logo_small : /data/upload/logo_small.png
     * logo_white : /data/upload/logo_white.png;
     * isDowntime : false
     * beforeDowntime :
     * afterDowntime :
     * ios : {"app":"itms-services://?action=download-manifest&url=https://download.xikoumedia.com/vip400.plist","build":"4.0.0","note":"本次更新内容：&lt;br&gt;1. 添加更优秀，体验更好的德胜棋牌，满足您的二次元赚钱之路；&lt;br&gt;2. 新增中奖金额大于2000元，全平台公告祝贺；&lt;br&gt;3. 程序猿帅哥修复已知BUG；&lt;br&gt;4. 程序猿帅哥增加更多精彩等着您来发现；","size":"63.61MB","update":"2018-12-19 00:00:00","url":"itms-services://?action=download-manifest&url=https://download.xikoumedia.com/vip400.plist","version":"4.0.0"}
     * android : {"app":"https://download.xikoumedia.com/tcvip_latest_400.apk","build":"4.0.0","note":"本次更新内容：&lt;br&gt;1. 添加更优秀，体验更好的德胜棋牌，满足您的二次元赚钱之路；&lt;br&gt;2. 新增中奖金额大于2000元，全平台公告祝贺；&lt;br&gt;3. 程序猿帅哥修复已知BUG；&lt;br&gt;4. 程序猿帅哥增加更多精彩等着您来发现；","size":"31.44MB","update":"2018-12-19 00:00:00","url":"https://download.xikoumedia.com/tcvip_latest_400.apk","version":"4.0.0"}
     */

    private String prefix;
    private String name;
    private String shortname;
    private String api;
    private String ipapi;
    private int isip;
    private String urls;
    private String kefu;
    private String demoUrl;
    private String androidQRCode;
    private String iosQRCode;
    private String logo;
    private String logo_small;
    private String logo_white;
    private boolean isDowntime;
    private String beforeDowntime;
    private String afterDowntime;
    private IosBean ios;
    private AndroidBean android;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getIpapi() {
        return ipapi;
    }

    public void setIpapi(String ipapi) {
        this.ipapi = ipapi;
    }

    public int getIsip() {
        return isip;
    }

    public void setIsip(int isip) {
        this.isip = isip;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getKefu() {
        return kefu;
    }

    public void setKefu(String kefu) {
        this.kefu = kefu;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }

    public String getAndroidQRCode() {
        return androidQRCode;
    }

    public void setAndroidQRCode(String androidQRCode) {
        this.androidQRCode = androidQRCode;
    }

    public String getIosQRCode() {
        return iosQRCode;
    }

    public void setIosQRCode(String iosQRCode) {
        this.iosQRCode = iosQRCode;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo_small() {
        return logo_small;
    }

    public void setLogo_small(String logo_small) {
        this.logo_small = logo_small;
    }

    public String getLogo_white() {
        return logo_white;
    }

    public void setLogo_white(String logo_white) {
        this.logo_white = logo_white;
    }

    public boolean isIsDowntime() {
        return isDowntime;
    }

    public void setIsDowntime(boolean isDowntime) {
        this.isDowntime = isDowntime;
    }

    public String getBeforeDowntime() {
        return beforeDowntime;
    }

    public void setBeforeDowntime(String beforeDowntime) {
        this.beforeDowntime = beforeDowntime;
    }

    public String getAfterDowntime() {
        return afterDowntime;
    }

    public void setAfterDowntime(String afterDowntime) {
        this.afterDowntime = afterDowntime;
    }

    public IosBean getIos() {
        return ios;
    }

    public void setIos(IosBean ios) {
        this.ios = ios;
    }

    public AndroidBean getAndroid() {
        return android;
    }

    public void setAndroid(AndroidBean android) {
        this.android = android;
    }

    public static class IosBean {
    }

    public static class AndroidBean {
        /**
         * app : https://download.xikoumedia.com/tcvip_latest_400.apk
         * build : 4.0.0
         * note : 本次更新内容：&lt;br&gt;1. 添加更优秀，体验更好的德胜棋牌，满足您的二次元赚钱之路；&lt;br&gt;2. 新增中奖金额大于2000元，全平台公告祝贺；&lt;br&gt;3. 程序猿帅哥修复已知BUG；&lt;br&gt;4. 程序猿帅哥增加更多精彩等着您来发现；
         * size : 31.44MB
         * update : 2018-12-19 00:00:00
         * url : https://download.xikoumedia.com/tcvip_latest_400.apk
         * version : 4.0.0
         */

        private String url;
        private String version;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
