package com.desheng.app.toucai.model;

/**
 * Created by lee on 2018/3/6.
 */
public class BannerBean {
    public String id;
    public String title;
    public String imgUrl;
    public int sort;
    public String alt;
    public String linkUrl;
    public int linkType;
    public int width;
    public int height;

    @Override
    public String toString() {
        return "BannerBean{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", sort=" + sort +
                ", alt='" + alt + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
