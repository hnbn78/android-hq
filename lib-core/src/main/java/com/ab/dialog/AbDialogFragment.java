package com.ab.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.Animation;

import com.ab.util.AbAnimationUtil;
import com.ab.util.AbDialogUtil;
/**
 * © 2012 amsoft.cn
 * 名称：AbDialogFragment.java 
 * 描述：弹出框的父类
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2014-07-30 下午16:00:52
 */
public class AbDialogFragment extends DialogFragment {
public static final String DIALOG_LOAD = "DIALOG_LOAD";
	private View mIndeterminateView = null;
	public String mMessage;
    
    
    
    private DialogInterface.OnShowListener mOnShowListener = null;
	private DialogInterface.OnCancelListener mOnCancelListener = null;
	private DialogInterface.OnDismissListener mOnDismissListener = null;
	private AbDialogOnLoadListener mAbDialogOnLoadListener = null;

	public AbDialogFragment() {
		super();
	}
	

	@Override
	public void onCancel(DialogInterface dialog) {
		// 用户中断
		if (mOnCancelListener != null) {
			mOnCancelListener.onCancel(dialog);
		}

		super.onCancel(dialog);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// 用户隐藏
		if (mOnDismissListener != null) {
			mOnDismissListener.onDismiss(dialog);
		}
		super.onDismiss(dialog);
	}
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(mOnShowListener);
        return dialog;
    }
    
    public DialogInterface.OnCancelListener getOnCancelListener() {
		return mOnCancelListener;
	}

	public void setOnCancelListener(
			DialogInterface.OnCancelListener onCancelListener) {
		this.mOnCancelListener = onCancelListener;
	}

	public DialogInterface.OnDismissListener getOnDismissListener() {
		return mOnDismissListener;
	}

	public void setOnDismissListener(
			DialogInterface.OnDismissListener onDismissListener) {
		this.mOnDismissListener = onDismissListener;
	}

	
	/**
	 * 加载调用
	 */
	public void load(View v){
		if(mAbDialogOnLoadListener!=null){
			mAbDialogOnLoadListener.onLoad();
		}
		mIndeterminateView = v;
		AbAnimationUtil.playRotateAnimation(mIndeterminateView, 300, Animation.INFINITE,
				Animation.RESTART);
	}

	/**
	 * 加载成功调用
	 */
	public void loadFinish(){
		//停止动画
		loadStop();
		AbDialogUtil.removeDialog(this.getActivity(), DIALOG_LOAD);
	}
	
	/**
	 * 加载结束
	 */
	public void loadStop(){
		//停止动画
		mIndeterminateView.postDelayed(new Runnable(){

			@Override
			public void run() {
				mIndeterminateView.clearAnimation();
			}
			
		}, 200);
	}
    
    public DialogInterface.OnShowListener getOnShowListener() {
        return mOnShowListener;
    }
    
    public void setOnShowListener(DialogInterface.OnShowListener mOnShowListener) {
        this.mOnShowListener = mOnShowListener;
    }
	public AbDialogOnLoadListener getAbDialogOnLoadListener() {
		return mAbDialogOnLoadListener;
	}

	public void setAbDialogOnLoadListener(
			AbDialogOnLoadListener abDialogOnLoadListener) {
		this.mAbDialogOnLoadListener = abDialogOnLoadListener;
	}
	
	public String getMessage() {
		return mMessage;
	}


	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}


	/**
	 * 加载事件的接口.
	 */
	public interface AbDialogOnLoadListener {

		/**
		 * 加载
		 */
		public void onLoad();
		
	}

}
