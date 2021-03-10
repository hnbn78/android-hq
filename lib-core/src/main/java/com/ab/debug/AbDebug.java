package com.ab.debug;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ab.global.Global;
import com.ab.util.JsonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * 调试输出类 局部调试覆盖全局调试 用于目标工程的输出
 * @author Leo
 *
 */
public class AbDebug {
    public static final String TAG_APP = "tag_app";
    public static final String TAG_THREAD = "tag_thread";
    public static final String TAG_UI = "tag_ui";
    public static final String TAG_DB = "tag_db";
    public static final String TAG_HTTP = "tag_http";
    public static final String TAG_DATA = "tag_data";
    public static final String TAG_PUSH = "tag_push";
    public static final String TAG_RXJAVA = "tag_rxjava";
    
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    //Control global debug switch;
    public static boolean isDevDebug;
    
    //局部调试
    private static boolean isLocalDebug;
    
    //正在调试的标签
    private static List<String> arrLocalTags = new ArrayList<>();
    public static String strTag = "DEBUG";
    public static long iMarkTime = 0;
    
    public static HashMap<String, String> mapTagToLocal = new HashMap<>();
    
    public static HashMap<String, Integer>  mapTraceLeveledOfTag = new HashMap<>();
    
    public static void setDevDebug(boolean isDevDebug, String tag){
        AbDebug.isDevDebug = isDevDebug;
        strTag = tag;
    }
    
    public static void putTraceTagLevel(String fullClassName, int level){
        mapTraceLeveledOfTag.put(fullClassName, level);
    }
    
    public static void startLocalDebug(String ... arrLocalDebugFlag){
        arrLocalTags.addAll(Arrays.asList(arrLocalDebugFlag));
        for (String localTag: arrLocalDebugFlag){
            mapTagToLocal.put(localTag, localTag);
        }
        isLocalDebug = true;
        iMarkTime = System.currentTimeMillis();
        for (String strTag: arrLocalDebugFlag) {
            if(isDevDebug ){
                Log.i(strTag, "\n************local debug begin***time start:[" + SDF.format(iMarkTime) + "]************\n");
            }
        }
    }
    
    
    public static void setLocalDebug(String tag, String localDebugFlag){
        mapTagToLocal.put(tag, localDebugFlag);
    }
    
    
    public static void stopLocalDebug(String ... arrLocalDebugFlag){
        arrLocalTags.removeAll(Arrays.asList(arrLocalDebugFlag));
        isLocalDebug = false;
        iMarkTime = 0;
    }
    
    /*
     * 记录日志
     */
    public static void write(String fileName, String msg){
        File file = new File(fileName);
        PrintWriter pw = null;
        try {
            msg = format(System.currentTimeMillis())+":" + msg;
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));
            pw.println(msg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(pw != null){
                pw.close();
            }
        }
    }
    
    /*
     * 只输出信息
     */
    public static void log(String tag, String msg){
        if(isDevDebug){
            if(isLocalDebug &&
                    (mapTagToLocal.get(tag) != null && arrLocalTags.contains(mapTagToLocal.get(tag)) ||
                            arrLocalTags.contains(tag))){
                long now = System.currentTimeMillis();
                String stackTrace = "";
                if(mapTraceLeveledOfTag.containsKey(tag)){
                    stackTrace = generateCallInfo(mapTraceLeveledOfTag.get(tag));
                }else{
                    stackTrace = generateCallInfo(0);
                }
                Log.i(mapTagToLocal.get(tag), "*\n**local log***time["+format(now)+"] " + " past:[" + (now - iMarkTime) + "]***\n*stacktrace:["+ stackTrace +"]\n*msg:[" + msg + "]\n*");
                iMarkTime = now;
                return;
            }
            Log.d(strTag, msg);
        }
    }
    
    /*
     * 只输出信息
     */
    public static void logDetail(String tag, String msg){
        if(isDevDebug){
            if(isLocalDebug &&
                    (mapTagToLocal.get(tag) != null && arrLocalTags.contains(mapTagToLocal.get(tag)) ||
                            arrLocalTags.contains(tag))){
                long now = System.currentTimeMillis();
                String stackTrace = "";
                if(mapTraceLeveledOfTag.containsKey(tag)){
                    stackTrace = generateCallInfo(mapTraceLeveledOfTag.get(tag));
                }else{
                    stackTrace = generateCallInfo(0);
                }
                Log.d(mapTagToLocal.get(tag), "*\n**local log detail***time["+format(now)+"] " + " past:[" + (now - iMarkTime) + "]***\n*stacktrace:["+ stackTrace +"]\n*msg:[" + msg + "]\n*");
                iMarkTime = now;
                return;
            }
            Log.d(strTag, msg);
        }
    }
    
    
    /*
     * 只输出信息
     */
    public static void error(String tag, String msg){
        if(isDevDebug){
            if(isLocalDebug &&
                    (mapTagToLocal.get(tag) != null && arrLocalTags.contains(mapTagToLocal.get(tag)) ||
                            arrLocalTags.contains(tag))){
                long now = System.currentTimeMillis();
                String stackTrace = "";
                if(mapTraceLeveledOfTag.containsKey(tag)){
                    stackTrace = generateCallInfo(mapTraceLeveledOfTag.get(tag));
                }else{
                    stackTrace = generateCallInfo(0);
                }
                Log.e(mapTagToLocal.get(tag), "*\n**local error***time["+format(now)+"] " + " past:[" + (now - iMarkTime) + "]***\n*stacktrace:[" + stackTrace + "]\n*msg:[" + msg + "]\n*");
                sendExceptionReport(msg);
                iMarkTime = now;
                return;
            }
            Log.e(strTag, msg);
            sendExceptionReport(msg);
        }
    }
    
    /*
     * 只输出信息
     */
    public static void error(String tag, Thread thread, Throwable ex){
        if(isDevDebug){
            if(isLocalDebug &&
                    (mapTagToLocal.get(tag) != null && arrLocalTags.contains(mapTagToLocal.get(tag)) ||
                            arrLocalTags.contains(tag))){
                long now = System.currentTimeMillis();
                String errorStr = "*\n**local error***time["+format(now)+"] " + " past:[" + (now - iMarkTime) + "]***\n" +
                        "*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!EXCEPTION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                        "*thread:[" + thread.getName() + "]\n" +
                        "*msg:[" + getTraceInfo(ex) + "]\n*";
                Log.e(mapTagToLocal.get(tag), errorStr);
                sendExceptionReport(errorStr);
                iMarkTime = now;
                return;
            }
            String errorStr = "*\n*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!EXCEPTION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                    "*thread:[" + thread.getName() + "]" +
                    "*msg:[" + getTraceInfo(ex) + "]\n*";
            Log.e(strTag, errorStr);
            sendExceptionReport(errorStr);
        }
    }
    
    /*
     * 只输出信息
     */
    public static void error(String tag, Thread thread, Throwable ex, String extra){
        if(isDevDebug){
            if(isLocalDebug &&
                    (mapTagToLocal.get(tag) != null && arrLocalTags.contains(mapTagToLocal.get(tag)) ||
                            arrLocalTags.contains(tag))){
                long now = System.currentTimeMillis();
                String errorStr = "*\n**local error***time["+format(now)+"] " + " past:[" + (now - iMarkTime) + "*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!EXCEPTION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                        "*extra:[" + extra + "]\n" +
                        "]***\n"+
                        "*thread:[" + thread.getName() + "]\n" +
                        "*msg:[" + getTraceInfo(ex) + "]\n*";
                Log.e(mapTagToLocal.get(tag),errorStr);
                sendExceptionReport(errorStr);
                iMarkTime = now;
                return;
            }
            String errorStr =  "*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!EXCEPTION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                    "*extra:[" + extra + "]\n" +
                    "*thread:[" + thread.getName() + "]\n" +
                    "*msg:[" + getTraceInfo(ex) + "]";
            Log.e(strTag, errorStr);
            sendExceptionReport(errorStr);
        }
    }
    
    /*
     * 显示并输出信息
     */
    public static void toast(String tag, String msg){
        if (isDevDebug) {
            if((isLocalDebug &&
                    (mapTagToLocal.get(tag) != null && arrLocalTags.contains(mapTagToLocal.get(tag)) ||
                            arrLocalTags.contains(tag)))){
                long now = System.currentTimeMillis();
                Toast.makeText(Global.app, "[Local Debug]" + "["+ mapTagToLocal.get(tag) +"]:" + msg, Toast.LENGTH_LONG).show();
                String stackTrace = "";
                if(mapTraceLeveledOfTag.containsKey(tag)){
                    stackTrace = generateCallInfo(mapTraceLeveledOfTag.get(tag));
                }else{
                    stackTrace = generateCallInfo(0);
                }
                Log.i(mapTagToLocal.get(tag), "***local log***time["+format(now)+"] " + " past:[" + (now - iMarkTime) + "]***\n*stacktrace:["+ stackTrace +"]\n*msg:[" + msg + "]");
                iMarkTime = now;
                return;
            }
            Toast.makeText(Global.app, "[Debug]:" + msg, Toast.LENGTH_LONG).show();
            Log.d(strTag, msg);
        }
    }
    
    private static String format(long millis){
        return SDF.format(new Date(millis));
    }
    
    /**
     * 本层调用
     * @return
     */
    private static String generateCallInfo(int level) {
        if(level == 0){
            level = 4;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[level];
        String tag = "%s.%s(Line:%d)"; // 占位符
        
        String callerClazzName = caller.getClassName(); // 获取到类名
        
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        
        tag = String.format(tag, callerClazzName, caller.getMethodName(),caller.getLineNumber()); // 替换
        
        return tag;
    }
    
    
    public static String getTraceInfo(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        
        sb.append(ex.toString() + "\n");
        StackTraceElement[] stacks = ex.getStackTrace();
        for (int i = 0; i < stacks.length; i++) {
            sb.append("class: ").append(stacks[i].getClassName())
                    .append("; method: ").append(stacks[i].getMethodName())
                    .append("; line: ").append(stacks[i].getLineNumber())
                    .append(";\n");
        }
        
        if(ex.getCause()!= null){
            sb.append("caused by:");
            sb.append(getTraceInfo(ex.getCause()));
        }
        return sb.toString();
    }
    
    /**
     * 发送给助手应用。
     * TODO.
     * @param crashReport
     */
    public static void sendExceptionReport(String crashReport) {
        Intent itt = new Intent("event.com.pearl.boy.error.msg");
        itt.putExtra("error_msg", crashReport);
        Global.app.sendBroadcast(itt);
    }
    
    /**
     * 发送给助手应用。
     * TODO.
     */
    public static void sendInfoReport(HashMap<String, Object> params) {
        Intent itt = new Intent("event.com.pearl.boy.error.msg");
        itt.putExtra("info_msg", JsonUtils.mapToJsonObj(params).toString());
        Global.app.sendBroadcast(itt);
    }
    
    
    /**
     * 发送给助手应用。
     * TODO.
     */
    public static void sendLogReport(String log) {
        Intent itt = new Intent("event.com.pearl.boy.error.msg");
        itt.putExtra("log_msg", log);
        Global.app.sendBroadcast(itt);
    }
}

