package com.pearl.data.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class TestModel{

    @Id
    private long _id;

    public String keyTag;
    
    public long get_id() {
        return _id;
    }
    
    public void set_id(long _id) {
        this._id = _id;
    }
}
