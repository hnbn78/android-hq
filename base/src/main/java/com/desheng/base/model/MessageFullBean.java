package com.desheng.base.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by o on 2018/4/5.
 */

public class MessageFullBean implements Parcelable {
    protected MessageFullBean(Parcel in) {
        time = in.readString();
        title = in.readString();
        content = in.readString();
        text = in.readString();
        html = in.readString();
    }

    public static final Creator<MessageFullBean> CREATOR = new Creator<MessageFullBean>() {
        @Override
        public MessageFullBean createFromParcel(Parcel in) {
            return new MessageFullBean(in);
        }

        @Override
        public MessageFullBean[] newArray(int size) {
            return new MessageFullBean[size];
        }
    };



    public String time;
    public String title;
    public String content;
    public String text;
    public String html;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getHtml() {
        return html;
    }
    
    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(text);
        dest.writeString(html);
    }
}
