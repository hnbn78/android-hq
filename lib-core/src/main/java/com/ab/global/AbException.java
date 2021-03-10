/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ab.global;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import com.ab.debug.AbDebug;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Date;

// TODO: Auto-generated Javadoc

/**
 * © 2012 amsoft.cn
 * 名称：AbAppException.java 
 * 描述：公共异常类.
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2013-10-16 下午1:33:39
 */
public class AbException extends Exception implements UncaughtExceptionHandler {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1;

	/** 定义异常类型 */
	public final static byte TYPE_NETWORK = 0x01;
	public final static byte TYPE_SOCKET = 0x02;
	public final static byte TYPE_HTTP_CODE = 0x03;
	public final static byte TYPE_HTTP_ERROR = 0x04;
	public final static byte TYPE_XML = 0x05;
	public final static byte TYPE_IO = 0x06;
	public final static byte TYPE_RUN = 0x07;
	
	private byte type;
	private int code;
	/** 系统默认的UncaughtException处理类 */
	private static UncaughtExceptionHandler mDefaultHandler;
    
    private static ErrorReportHandler errorReportHandler;
    
	/** 异常消息. */
	private String msg = null;

    public static void setGlobalExceptionCaught(){
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //全局线程异常处理
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                //特殊处理.
                try {
                   handleException(thread, throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                //交给系统处理, 崩溃, 几秒后由ThreadCollector杀死进程
                if(Config.isDebug()){
                    mDefaultHandler.uncaughtException(thread, throwable);
                }
                
                //直接杀死
                System.exit(0);
            }
        });
    }


	/**
	 * 只用于异常处理。
	 */
	private AbException(){
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}
    
    public static void setErrorReportHandler(ErrorReportHandler handler) {
        errorReportHandler = handler;
    }
	
	/**
	 * 用一个消息构造异常类.
	 *
	 * @param message 异常的消息
	 */
	public AbException(String message) {
		super(message);
		msg = message;
	}
	
	private AbException(byte type, int code, Exception excp) {
		super(excp);
		this.type = type;
		this.code = code;
		if (AbDebug.isDevDebug) {
			saveErrorLog(excp);
		}
	}
	
	public int getCode() {
		return this.code;
	}

	public int getType() {
		return this.type;
	}
	
	/**
	 * 提示友好的错误信息
	 * 
	 * @param ctx
	 */
	public void makeToast(Context ctx) {
		switch (this.getType()) {
		case TYPE_HTTP_CODE:
			break;
		case TYPE_HTTP_ERROR:
			break;
		case TYPE_SOCKET:
			break;
		case TYPE_NETWORK:
			break;
		case TYPE_XML:
			break;
		case TYPE_IO:
			break;
		case TYPE_RUN:
			break;
		}
	}

	/**
	 * 描述：获取异常信息.
	 *
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return msg;
	}
	
	/**
	 * 保存异常日志
	 * 
	 * @param excp
	 */
	public static void saveErrorLog(Exception excp) {
		saveErrorLog(excp.getLocalizedMessage());
	}

	/**
	 * 保存异常日志
	 * 
	 */
	public static void saveErrorLog(String excpMessage) {
		String errorlog = "crash.log";
		String savePath = "";
		String logFilePath = "";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			// 判断是否挂载了SD卡
			String storageState = Environment.getExternalStorageState();
			
            savePath = AbDevice.getCachePath();
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            logFilePath = savePath + errorlog;
			
			// 没有挂载SD卡，无法写文件
			if (logFilePath == "") {
				return;
			}
			File logFile = new File(logFilePath);
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			fw = new FileWriter(logFile, true);
			pw = new PrintWriter(fw);
			pw.println("--------------------"
					+ (DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date()))
					+ "---------------------");
			pw.println(excpMessage);
			pw.close();
			fw.close();
		} catch (Exception e) {
			Log.e("AppException", "[Exception]" + e.getLocalizedMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
		}
	}
	public static AbException http(int code) {
		return new AbException(TYPE_HTTP_CODE, code, null);
	}

	public static AbException http(Exception e) {
		return new AbException(TYPE_HTTP_ERROR, 0, e);
	}

	public static AbException socket(Exception e) {
		return new AbException(TYPE_SOCKET, 0, e);
	}

	public static AbException io(Exception e) {
		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AbException(TYPE_NETWORK, 0, e);
		} else if (e instanceof IOException) {
			return new AbException(TYPE_IO, 0, e);
		}
		return run(e);
	}


	public static AbException run(Exception e) {
		return new AbException(TYPE_RUN, 0, e);
	}

	/**
	 * 获取APP异常崩溃处理对象
	 *
	 * @return
	 */
	public static AbException getAppExceptionHandler() {
		return new AbException();
	}

	//暂不用
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		AbDebug.error(AbDebug.TAG_APP, "!!!!!!!!!!!!!!!!!!!!!!未捕获异常发生, App异常捕获!!!!!!!!!!!!!!!!!!!!!!!");
		handleException(thread, ex);
    }

	/**
	 * 自定义异常处理:收集错误信息&发送错误报告
	 * 
	 * @param ex
	 * @return true:处理了该异常信息;否则返回false
	 */
	private static boolean handleException(Thread thread, Throwable ex) {
		if (ex == null) {
			return false;
		}
        
		//打印一次
		AbDebug.error(AbDebug.TAG_APP, Thread.currentThread(), ex, "异常捕获");
        
        final StringBuffer crashReport = getCrashReport(ex);
        
        //交给外部处理一次
        if (errorReportHandler != null) {
            errorReportHandler.onGetErrorReport(crashReport);
        }
		
		// 显示异常信息&发送报告
		sendAppCrashReport(crashReport.toString());
		// 保存错误日志
        if (AbDebug.isDevDebug) {
            saveErrorLog(crashReport.toString());
        }
        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
//        System.exit(0);
		return true;
	}

	/**
	 * 发送给助手应用。
	 * TODO.
	 * @param crashReport
	 */
	private static void sendAppCrashReport(String crashReport) {
		Intent itt = new Intent("event.com.pearl.girl.error.msg");
		itt.putExtra("error_msg", crashReport);
		Global.app.sendBroadcast(itt);
	}

	/**
	 * 获取APP崩溃异常报告
	 * 
	 * @param ex
	 * @return
	 */
	public static StringBuffer getCrashReport(Throwable ex) {
		StringBuffer exceptionStr = new StringBuffer();
		
		exceptionStr.append("Version: " + Global.appVersionName + "("
				+ Global.appVersionCode + ")\n");
		exceptionStr.append("Android: " + Build.VERSION.RELEASE
				+ "(" + Build.MODEL + ")\n");
		exceptionStr.append("Device Info:" + collectDeviceInfo()
				+ "\n");
		exceptionStr.append("System Info:" + collectOsInfo() + "\n");
		exceptionStr.append("Exception:" + ex.getMessage() + "\n");
		exceptionStr.append("Exception stack："
				+ getTraceInfo(ex) + "\n");

		return exceptionStr;
	}

	/**
	 * 收集设备参数信息
	 * 
	 */
	public static String collectDeviceInfo() {
		JSONObject activePackageJson = new JSONObject();
		try {
			activePackageJson.put("isExternalStorageAvailable", AbDevice.isExternalStorageAvailable);
			activePackageJson.put("screenHeightPx", AbDevice.SCREEN_HEIGHT_PX);
			activePackageJson.put("screenWidthPx", AbDevice.SCREEN_WIDTH_PX);
			activePackageJson.put("mobileModel", AbDevice.MOBILE_MODEL);
			activePackageJson.put("versionCode", "" + Global.appVersionCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return activePackageJson.toString();
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
	 * 获取手机的硬件信息
	 * 
	 * @return
	 */
	public static String collectOsInfo() {
		JSONObject osJson = new JSONObject();
		// 通过反射获取系统的硬件信息

		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				osJson.put(field.getName(), field.get(null).toString());
				Log.d("AppException", field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e("AppException",
						"an error occured when collect crash info", e);
			}
		}

		return osJson.toString();
	}
	
	public interface ErrorReportHandler{
       void onGetErrorReport (StringBuffer report);
    }
}
