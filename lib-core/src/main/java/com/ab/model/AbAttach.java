package com.ab.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lee on 2017/9/24.
 */

public class AbAttach extends AbKeepBaseEntity implements Serializable{
    public static final String TYPE_MARK = "ATTACH";
    public static final String UPGRADE_SALE_CLIENT = "UPGRADE_SALE_CLIENT";
    private ArrayList<String> sql;
    private String tip;
    private HashMap<String, String> cmd;
    private HashMap<String, String> cfg;
    private HashMap<String, String> opt;
    private HashMap<String, String> rpt;
    
    public ArrayList<String> getSql() {
        return sql;
    }
    
    public void setSql(ArrayList<String> sql) {
        this.sql = sql;
    }
    
    public String getTip() {
        return tip;
    }
    
    public void setTip(String tip) {
        this.tip = tip;
    }
    
    public HashMap<String, String> getCmd() {
        return cmd;
    }
    
    public void setCmd(HashMap<String, String> cmd) {
        this.cmd = cmd;
    }
    
    public HashMap<String, String> getCfg() {
        return cfg;
    }
    
    public void setCfg(HashMap<String, String> cfg) {
        this.cfg = cfg;
    }
    
    public HashMap<String, String> getOpt() {
        return opt;
    }
    
    public void setOpt(HashMap<String, String> opt) {
        this.opt = opt;
    }
    
    public HashMap<String, String> getRpt() {
        return rpt;
    }
    
    public void setRpt(HashMap<String, String> rpt) {
        this.rpt = rpt;
    }
    
    public String toJsonStr(){
        return new Gson().toJson(this, AbAttach.class);
    }
    
    public static AbAttach fromJsonStr(String content){
        return new Gson().fromJson(content, AbAttach.class);
    }
}
