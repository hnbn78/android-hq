package com.desheng.app.toucai.model;

public class TrendTypeIndexBean {
    private String name;
    private String pos;

    public TrendTypeIndexBean(String name, String pos) {
        this.name = name;
        this.pos = pos;
    }

    public String getName() {
        return this.name;
    }

    public String getPos() {
        return this.pos;
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public void setPos(String paramString) {
        this.pos = paramString;
    }
}
