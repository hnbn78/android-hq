package com.ab.http;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;

/**
 * Created by lee on 2017/10/26.
 */
public class AbHttpTicket {
    public static final int RESULT_CANCELLED = -1;
    public static final int RESULT_FAIL = 0;
    public static final int RESULT_SUCCESS = 1;
    
    /**
     * tag是否已取消
     */
    private static ConcurrentHashMap<String, Boolean> mapAllTicket = new ConcurrentHashMap<>();
    
    private Call call;
    private Object tag;
    private String token;
    
    // -1 cancelled 0 fail 1 success
    private int result = 0;
    private Object data = null;
    
    public AbHttpTicket(Object tag) {
        this.tag = tag;
        token = UUID.randomUUID().toString() + "_" + tag.toString();
        mapAllTicket.put(token, false);
    }
    
    
    public void setCall(Call call) {
        this.call = call;
    }
    
    
    public void setResult(int result) {
        this.result = result;
    }
    
    public boolean isSuccess(){
        return result == RESULT_SUCCESS;
    }
    
    public boolean isFail(){
        return result == RESULT_FAIL;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public void cancel() {
        if (mapAllTicket.get(token) != null) {
            mapAllTicket.put(token, true);
        }
        if (call != null) {
            call.cancel();
        }
        result = RESULT_CANCELLED;
    }
    
    /**
     * 错误发生时, 如果是非正常返回错误码,或其他线程之类的取消了请求, 则次数返回true;
     * 调用onSuceess, onFailure等结果处理时会检查isCancelled, 如发现是已被取消的,则不调用.
     * 如果正常返回, 不管错误码是对是错,都返回false, 保留ticket至相同tag下次请求刷新.
     */
    public boolean isCancelled() {
        return token == null || mapAllTicket.get(token) == null ? true : mapAllTicket.get(token);
    }
    
    public void clear(){
        mapAllTicket.remove(token);
        if (call != null) {
            call.cancel();
        }
        call = null;
        token = null;
        tag = null;
    }
    
    public static void clearAll(Object tag) {
        if(tag != null){
            ArrayList<String> deleteList = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry:
                 mapAllTicket.entrySet()) {
                if(entry.getKey().contains(tag.toString())){
                    deleteList.add(entry.getKey());
                }
            }
            for (int i = 0; i < deleteList.size(); i++) {
                mapAllTicket.remove(deleteList.get(i));
            }
        }else{
            mapAllTicket.clear();
        }
    }
}
