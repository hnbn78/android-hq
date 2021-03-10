package com.ab.http;

import com.ab.debug.AbDebug;
import com.ab.util.Strs;

import java.util.HashMap;

import okhttp3.Request;


/**
 * http访问结果处理, 或文件上传下载回调
 * 所有的onSuccess的resultCode必为1;
 * @author Leo
 *
 */
public class AbHttpResult {
    private static HashMap<String, String> mapLatestRespBody = new HashMap<>();
    
    //设置解析实体类的对象和字段
    public void setupEntity(AbHttpRespEntity entity) {
    }
  
    public void onBefore(Request request, int id, String host, String funcName){
    
    }
    
    protected static boolean checkIsTheSameWithLatest(String url, String resp) {
        if (Strs.isNotEmpty(resp) && resp.equals(mapLatestRespBody.get(url))) {
            return true;
        } else {
            mapLatestRespBody.put(url, resp);
            return false;
        }
    }
    
    public static void cleanCache() {
        mapLatestRespBody.clear();
    }
    
    protected <T> T getField(HashMap<String, Object> map, String name, T def) {
        T value = def;
        Object obj = null;
        if (map != null) {
            obj = map.get(name);
            if (obj != null) {
                value = (T) obj;
            }
        }
        return value;
    }
    
    protected <T> T getFieldObject(HashMap<String, Object> map, String name, Class<T> clazz) {
        T value = null;
        if (map != null) {
            try {
                value = (T) map.get(name);
            } catch (Exception e) {
                AbDebug.error(AbDebug.TAG_HTTP, Thread.currentThread(), e, "http回调处理,数据转型失败或空!!");
            }
        }
        return value;
    }
    
    //获取数据对象会调用.
	public boolean onSuccessGetObject(int code, int error, String msg,  HashMap<String, Object> extra){
        return false;
    };
    
	//获取字符串调用.里面为服务端返回的字符串, 返回true自行处理, 返回false继续调用 onSuccessGetObject
	public boolean onGetString(String str){ return false; };
    
	
	//可能是状态码, 失败调用. 返回true则拦截错误处理, false交给公共错误处理.
	public boolean onError(int status, String content){ return false; };
    
	//文件上传下载进度回调,其他无效
    public void onProgress(long progress, long total) {};

    /**
      *失败，成功, 完成后调用，
     */
    public void onFinish(){};
    
    public void onAfter(int id){
    
    }
}
