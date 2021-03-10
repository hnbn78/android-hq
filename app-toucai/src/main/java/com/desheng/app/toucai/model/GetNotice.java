package com.desheng.app.toucai.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 2018/3/1.
 */

public class GetNotice implements Serializable {

   public String code;
   public String error;
   public  String message;
   public List<Data> dataList;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }
}
