package com.pearl.data.model;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by  on 17/8/22.
 */
@Entity
public class NoteUserRelationBean {
    
    @Id
    private long _id;
    
    public String keyTag;
    
    /**
     * 笔记id.
     */
    public String notesId;
    
    /**
     * 收藏
     */
    public boolean collection;
    
    /**
     * 点赞
     */
    public boolean good;
    
    public long get_id() {
        return _id;
    }
    
    public void set_id(long _id) {
        this._id = _id;
    }
    
    public String getKeyTag() {
        return keyTag;
    }
    
    public void setKeyTag(String keyTag) {
        this.keyTag = keyTag;
    }
    
    public String getNotesId() {
        return notesId;
    }
    
    public void setNotesId(String notesId) {
        this.notesId = notesId;
    }
    
    public boolean isCollection() {
        return collection;
    }
    
    public void setCollection(boolean collection) {
        this.collection = collection;
    }
    
    public boolean isGood() {
        return good;
    }
    
    public void setGood(boolean good) {
        this.good = good;
    }
}
