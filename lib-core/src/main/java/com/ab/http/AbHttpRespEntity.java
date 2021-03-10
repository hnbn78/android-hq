package com.ab.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 2017/9/8.
 */

public class AbHttpRespEntity {
    private String str;
    private int code = Integer.MIN_VALUE;
    private int error = 0;
    private String msg;

    /**
     * 字段名与字段类型
     */
    public HashMap<String, Type> types = new HashMap<>();

    /**
     * 特殊字段保存
     */
    public HashMap<String, Object> extras = new HashMap<>();

    public AbHttpRespEntity(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public HashMap<String, Object> getExtras() {
        return extras;
    }

    public AbHttpRespEntity putField(String str, Type type) {
        types.put(str, type);
        return this;
    }

    public void parseFields(String jsonStr) {
        JsonElement jsonResp = new JsonParser().parse(jsonStr);
        if (jsonResp.isJsonObject()) {
            JsonObject obj = jsonResp.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                if ("code".equals(entry.getKey())) {
                    code = obj.get("code").getAsInt();
                } else if ("msg".equals(entry.getKey()) || "message".equals(entry.getKey())) {
                    if (obj.get("msg") != null) {
                        msg = obj.get("msg").getAsString();
                    } else if (obj.get("message") != null) {
                        msg = obj.get("message").getAsString();
                    }
                } else if ("error".equals(entry.getKey())) {
                    error = obj.get("error").getAsInt();
                } else {
                    if (types.containsKey(entry.getKey())) {
                        try {
                            Object valueObj = new Gson().fromJson(entry.getValue(), types.get(entry.getKey()));
                            extras.put(entry.getKey(), valueObj);
                        } catch (JsonSyntaxException e) {
                            extras.put(entry.getKey(),entry.getValue());
                        }
                    }
                }
            }
        }
    }



    @Override
    public String toString() {
        return "AbHttpRespEntity{" +
                "str='" + str + '\'' +
                ", code=" + code +
                ", error=" + error +
                ", msg='" + msg + '\'' +
                ", types=" + types +
                ", extras=" + extras +
                '}';
    }


    /*public static void main(String [] args){
        String json = "{\"integer\":1, \"double\":1.1, \"string\":\"i fuck!\", \"boolean\":true, \"array\":[" +
                "{\"integer\":1, \"double\":1.1, \"string\":\"i fuck!\", \"boolean\":true, \"array\":[]}," +
                "{\"integer\":1, \"double\":1.1, \"string\":\"i fuck!\", \"boolean\":true, \"array\":[]}," +
                "{\"integer\":1, \"double\":1.1, \"string\":\"i fuck!\", \"boolean\":true, \"array\":[]}]}";
        AbHttpEntity entity =  new AbHttpEntity("anything")
                .putField("integer", Integer.TYPE)
                .putField("double", Double.TYPE)
                .putField("string", String.class)
                .putField("boolean", Boolean.TYPE)
                .putField("array", new TypeToken<ArrayList<AbHttpEntity>>(){}.getType());
        entity.parseObject(json);
        System.out.println(entity);
    }*/
}
