package com.ab.model;

import java.util.Map;

/**
 * Created by lee on 2017/9/24.
 */

public class AbCommand extends AbKeepBaseEntity{
    private String name;
    private Map<String, String> param;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
