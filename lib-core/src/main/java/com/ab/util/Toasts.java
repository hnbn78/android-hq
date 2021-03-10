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
package com.ab.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.core.R;
import com.ab.global.Global;

import java.lang.ref.WeakReference;

import me.drakeet.support.toast.ToastCompat;

// TODO: Auto-generated Javadoc
/**
 * © 2012 amsoft.cn
 * 名称：AbToastUtil.java 
 * 描述：Toast工具类.
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2014-07-02 下午11:52:13
 */

public class Toasts {
    
    /** 显示Toast. */
    public static final int SHOW_TOAST = 0;
    
    /**
	 * 主要Handler类，在线程中可用
	 * what：0.提示文本信息
	 */
	private static Handler baseHandler = null;

    public static void init(){
        baseHandler = new ToastHanler();
    }


    public static void  show(String text){
    	show(Global.app,text);
	}

    /**
     * 描述：Toast提示文本.
     * @param text  文本
     */
	public static void show(Context context, String text) {
		if(!AbStrUtil.isEmpty(text)){
            ToastCompat.makeText(context,text, Toast.LENGTH_SHORT).show();
		}
	}


	public static void show(int resId){
		show(Global.app,resId);
	}

	/**
     * 描述：Toast提示文本.
     * @param resId  文本的资源ID
     */
	public static void show(Context context, int resId) {
		ToastCompat.makeText(context,""+context.getResources().getText(resId), Toast.LENGTH_SHORT).show();
	}

        /**
     * 描述：Toast提示文本.
     * @param text  文本
     */
	public static void showLong(Context context, String text) {
		if(!AbStrUtil.isEmpty(text)){
			ToastCompat.makeText(context,text, Toast.LENGTH_LONG).show();
		}
	}

	/**
     * 描述：Toast提示文本.
     * @param resId  文本的资源ID
     */
	public static void showLong(Context context, int resId) {
		ToastCompat.makeText(context,""+context.getResources().getText(resId), Toast.LENGTH_LONG).show();
	}


	/**
	 * 描述：在线程中提示文本信息.
	 */
	public static void showInUIThread(String text) {
		Message msg = baseHandler.obtainMessage(SHOW_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString("TEXT", text);
		msg.setData(bundle);
		baseHandler.sendMessage(msg);
	}

	public static WeakReference<ToastCompat> currToastRef = null;

	public static  void show(String text,boolean isYes){
		show(Global.app,text,isYes);
	}

	/**
	 *
	 * @param context
	 * @param text 12字以内小背景, 12字意外大背景, 36字以外被裁剪
	 * @param isYes
	 */

	public static void show(Context context, String text, boolean isYes){
        if(currToastRef != null && currToastRef.get() != null){
            currToastRef.get().cancel();
        }
        if(context == null){
            return;
        }
		LinearLayout toastRoot = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.ab_toast, null);
        ImageView ivIcon = (ImageView) toastRoot.findViewById(R.id.ivIcon);
        if (isYes) {
            ivIcon.setImageResource(R.drawable.ab_ic_right);
        } else {
            ivIcon.setImageResource(R.drawable.ab_ic_wrong);
        }
        TextView tvMsg = (TextView) toastRoot.findViewById(R.id.tvMsg);
		tvMsg.setText(text);
		ToastCompat toast= ToastCompat.makeText(context, text, ToastCompat.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
        currToastRef = new WeakReference<ToastCompat>(toast);
	}

	/**
	 * TODO.
	 * @param context
	 * @param resId 12字以内小背景, 12字意外大背景, 36字以外拉伸变形
	 * @param isYes
	 */
	@SuppressWarnings("deprecation")
	public static void show(Context context, int resId, boolean isYes){
		show(context, context.getResources().getString(resId), isYes);
	}

    public static class ToastHanler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_TOAST:
                    show(Global.app, msg.getData().getString("TEXT"));
                    break;
                default:
                    break;
            }
        }
    }
}
