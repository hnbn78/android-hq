package com.desheng.app.toucai.model;

import java.io.Serializable;

/**
 * Created by o on 2018/4/9.
 */

public class MessageShowBean implements Serializable  {


      public String time;
    public String title;
    public String content;

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
}
