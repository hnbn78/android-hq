package com.desheng.base.model;

/**
 * Created by lee on 2018/4/4.
 */

public class VersionUpdateInfo {
    
    public int isip;
    public String urls;
    public String prefix;
    public String hostUserPrefix;
    public AndroidBean android;
    public String name;
    public String api;
    public String kefu;
    public IosBean ios;
    public String shortname;
    public String ipapi;
    public boolean isDowntime;
    public boolean isOpenChat;
    public String beforeDowntime;
    public String afterDowntime;
    public String apurls;
    public String sockio_url;

    
    public static class AndroidBean {
        
        public String app;
        public String build;
        public String note;
        public String size;
        public String update;
        public String url;
        public String version;

        @Override
        public String toString() {
            return "AndroidBean{" +
                    "app='" + app + '\'' +
                    ", build='" + build + '\'' +
                    ", note='" + note + '\'' +
                    ", size='" + size + '\'' +
                    ", update='" + update + '\'' +
                    ", url='" + url + '\'' +
                    ", version='" + version + '\'' +
                    '}';
        }
    }
    
    public static class IosBean {
               
        public String app;
        public String build;
        public String note;
        public String size;
        public String update;
        public String url;
        public String version;
        
    }
    
    @Override
    public String toString() {
        return "VersionUpdateInfo{" +
                "isip=" + isip +
                ", urls='" + urls + '\'' +
                ", prefix='" + prefix + '\'' +
                ", hostUserPrefix='" + hostUserPrefix + '\'' +
                ", android=" + android +
                ", name='" + name + '\'' +
                ", api='" + api + '\'' +
                ", kefu='" + kefu + '\'' +
                ", ios=" + ios +
                ", shortname='" + shortname + '\'' +
                ", ipapi='" + ipapi + '\'' +
                '}';
    }
}
