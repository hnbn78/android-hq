package com.ab.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * © 2012 amsoft.cn
 * 名称：AbLoadDialogFragment.java 
 * 描述：弹出的进度框
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2014-07-29 上午9:00:52
 */
public class AbProgressDialogFragment extends DialogFragment {
	
	int mIndeterminateDrawable;
	String mMessage;
    private ProgressDialog mProgressDialog;
    
    /**
	 * Create a new instance of AbProgressDialogFragment.
	 */
	public static AbProgressDialogFragment newInstance(int indeterminateDrawable,String message) {
		AbProgressDialogFragment f = new AbProgressDialogFragment();
		Bundle args = new Bundle();
		args.putInt("indeterminateDrawable", indeterminateDrawable);
		args.putString("message", message);
		f.setArguments(args);

		return f;
	}
    
    @Override
    public void onStart() {
        super.onStart();
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }
    
    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIndeterminateDrawable = getArguments().getInt("indeterminateDrawable");
		mMessage = getArguments().getString("message");
        
        mProgressDialog = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
		if(mIndeterminateDrawable > 0){
			mProgressDialog.setIndeterminateDrawable(getActivity().getResources().getDrawable(mIndeterminateDrawable));
		}
		
		if(mMessage != null){
			mProgressDialog.setMessage(mMessage);
		}
		
	    return mProgressDialog;
	}
	
	public void setMessage(String text){
        mMessage = text;
        mProgressDialog.setMessage(text);
    }
	
}
