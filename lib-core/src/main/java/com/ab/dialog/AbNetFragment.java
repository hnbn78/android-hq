package com.ab.dialog;


import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.AbConstants;
import com.ab.global.Global;
import com.ab.util.AbAnimationUtil;
import com.ab.util.AbViewUtil;
/**
 * © 2012 amsoft.cn
 * 名称：AbLoadDialogFragment.java 
 * 描述：弹出加载框
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2014-07-30 下午16:00:52
 */
public class AbNetFragment extends Fragment {

	private int mLoadDrawable;
	private int mRefreshDrawable;
    private int mWirelessDrawable;
	public String mLoadMessage  = "正在查询,请稍候";
	public String mRefreshMessage = "暂无数据,请重试";
    public String mWirelessMessage = "暂无网络,请连接后重试";
	private int mTextSize = 15;
	private int mTextColor = Color.WHITE;
	private RelativeLayout rootView = null;
	private View mContentView;
	private LinearLayout mLoadView = null;
	private LinearLayout mRefreshView = null;
    private LinearLayout mWirelessView = null;
	private TextView mLoadTextView = null;
	private ImageView mLoadImageView = null;
	private TextView mRefreshTextView = null;
	private ImageView mRefreshImageView = null;
    private TextView mWirelessTextView = null;
	private ImageView mWirelessImageView = null;
	private View mIndeterminateView = null;
    private boolean isUseLoadStyle = false;
	private AbFragmentOnLoadListener mAbFragmentOnLoadListener = null;
    private boolean isSmallLayout = false;
	
	/**
	 * 创建
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		
		rootView = new RelativeLayout(this.getActivity());
		mContentView = onCreateContentView(inflater,container,savedInstanceState);
        isUseLoadStyle = setResource();
		if(isUseLoadStyle){
			//先显示load
			showLoadView();
		}else{
            //直接加入root
			rootView.addView(mContentView, getContentLayoutParams());
		}
		return rootView;
	} 
	
	/**
	 * 显示View的方法（需要实现）
	 * @return
	 */
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		return null;
	}

    public void setSmallFragment(){
        isSmallLayout = true;
    }

    private RelativeLayout.LayoutParams getContentLayoutParams(){
        if(isSmallLayout){
            return new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        }else{
            return new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        }
    }


    /**
	 * 重写本方法返回true使用AbFragment自带load方式, 返回false就自己定制
     * 重写时要在方法中设置加载用的资源。
	 * TODO.
	 * @return
	 */
	public boolean setResource() {
		return false;
	}
	
	/**
	 * 初始化加载View
	 */
	public void initLoadView() {
		
		mLoadView = new LinearLayout(this.getActivity());
		mLoadView.setGravity(Gravity.CENTER);
		mLoadView.setOrientation(LinearLayout.VERTICAL);
		mLoadView.setPadding(20, 20, 20, 20);
		mLoadView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		mLoadImageView = new ImageView(this.getActivity());
		mLoadImageView.setImageResource(mLoadDrawable);
		mLoadImageView.setScaleType(ScaleType.MATRIX);

		mLoadTextView = new TextView(this.getActivity());
		mLoadTextView.setText(mLoadMessage);
		mLoadTextView.setTextColor(mTextColor);
		mLoadTextView.setTextSize(mTextSize);
		mLoadTextView.setPadding(5, 5, 5, 5);

		mLoadView.addView(mLoadImageView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mLoadView.addView(mLoadTextView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		mLoadImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 执行刷新
                load(v);
            }

        });
		
	}
	
	/**
	 * 初始化刷新View
	 */
	public void initRefreshView() {

		mRefreshView = new LinearLayout(this.getActivity());
		mRefreshView.setGravity(Gravity.CENTER);
		mRefreshView.setOrientation(LinearLayout.VERTICAL);
		mRefreshView.setPadding(20, 20, 20, 20);
		mRefreshView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		mRefreshImageView = new ImageView(this.getActivity());
		mRefreshImageView.setImageResource(mRefreshDrawable);
		mRefreshImageView.setScaleType(ScaleType.MATRIX);

		mRefreshTextView = new TextView(this.getActivity());
		mRefreshTextView.setText(mRefreshMessage);
		mRefreshTextView.setTextColor(mTextColor);
		mRefreshTextView.setTextSize(mTextSize);
		mRefreshTextView.setPadding(5, 5, 5, 5);

		mRefreshView.addView(mRefreshImageView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRefreshView.addView(mRefreshTextView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRefreshImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					// 执行刷新
					load(v);
			}

		});

	}

    /**
	 * 初始化刷新View
	 */
	public void initWirelessView() {

		mWirelessView = new LinearLayout(this.getActivity());
		mWirelessView.setGravity(Gravity.CENTER);
		mWirelessView.setOrientation(LinearLayout.VERTICAL);
		mWirelessView.setPadding(20, 20, 20, 20);
		mWirelessView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

		mWirelessImageView = new ImageView(this.getActivity());
		mWirelessImageView.setImageResource(mWirelessDrawable);
		mWirelessImageView.setScaleType(ScaleType.MATRIX);

		mWirelessTextView = new TextView(this.getActivity());
		mWirelessTextView.setText(mWirelessMessage);
		mWirelessTextView.setTextColor(mTextColor);
		mWirelessTextView.setTextSize(mTextSize);
		mWirelessTextView.setPadding(5, 5, 5, 5);

		mWirelessView.addView(mWirelessImageView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mWirelessView.addView(mWirelessTextView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mWirelessImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					// 执行刷新
					load(v);
			}

		});

	}

	/**
	 * 显示加载View
	 */
	public void showLoadView() {
		if(rootView.getChildCount() > 0){
			if(mLoadView == rootView.getChildAt(0)){
				return;
			}
		}
	
		rootView.removeAllViews();
		if(mLoadView == null){
			initLoadView();
		}
		AbViewUtil.removeSelfFromParent(mLoadView);
		rootView.addView(mLoadView);
		// 执行加载
	    load(mLoadImageView);
	}

	/**
	 * 显示刷新View
	 */
	public void showRefreshView() {
		if(rootView.getChildCount() > 0){
			if(mRefreshView == rootView.getChildAt(0)){
				loadStop(mRefreshImageView);
				return;
			}
		}
		
		rootView.removeAllViews();
		if(mRefreshView == null){
			initRefreshView();
		}
		AbViewUtil.removeSelfFromParent(mRefreshView);
		rootView.addView(mRefreshView);
	}

    /**
	 * 显示刷新View
	 */
	public void showWirelessView() {
		if(rootView.getChildCount() > 0){
			if(mRefreshView == rootView.getChildAt(0)){
				loadStop(mWirelessImageView);
				return;
			}
		}

		rootView.removeAllViews();
		if(mWirelessView == null){
			initRefreshView();
		}
		AbViewUtil.removeSelfFromParent(mWirelessView);
		rootView.addView(mWirelessView);
	}


	/**
	 * 显示内容View
	 */
	public void showContentView() {
		if(rootView.getChildCount() > 0){
			if(mContentView == rootView.getChildAt(0)){
				return;
			}
		}
		
		rootView.removeAllViews();
		AbViewUtil.removeSelfFromParent(mContentView);
		rootView.addView(mContentView, getContentLayoutParams());
	}
	
	/**
	 * 显示内容View
	 */
	public void showContentView(View view) {
		rootView.removeAllViews();
		AbViewUtil.removeSelfFromParent(mContentView);
		mContentView  = view;
		rootView.addView(mContentView, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	/**
	 * 加载完成调用
	 */
	public void loadFinish(){
		//停止动画
		loadStop(mIndeterminateView);
	}
	
	/**
	 * 加载结束
	 */
	public void loadStop(final View view){
		if(view == null){
			return;
		}
		//停止动画
		view.postDelayed(new Runnable() {

            @Override
            public void run() {
                view.clearAnimation();
            }

        }, 200);

	}
	
	/**
	 * 加载调用
	 */
	public void load(View v){
        if(isConnected() < 0){
            showWirelessView();
        };
		if(mAbFragmentOnLoadListener!=null){
			mAbFragmentOnLoadListener.onLoad();
		}
		mIndeterminateView = v;
		AbAnimationUtil.playRotateAnimation(mIndeterminateView, 300, Animation.INFINITE,
                Animation.RESTART);
	}
	
    /**
     * 获取内容View
     * @return
     */
	public View getContentView() {
		return mContentView;
	}
	
	/**
	 * 获取加载View文字的尺寸
	 * @return
	 */
	public int getTextSize() {
		return mTextSize;
	}

	/**
	 * 设置加载View文字的尺寸
	 * @return
	 */
	public void setTextSize(int textSize) {
		this.mTextSize = textSize;
	}

	public int getTextColor() {
		return mTextColor;
	}

	public void setTextColor(int textColor) {
		this.mTextColor = textColor;
	}
	
	public void setLoadMessage(String message) {
		this.mLoadMessage = message;
		if(mLoadTextView!=null){
			mLoadTextView.setText(mLoadMessage);
		}
	}
	
	public void setRefreshMessage(String message) {
		this.mRefreshMessage = message;
		if(mRefreshTextView!=null){
			mRefreshTextView.setText(mRefreshMessage);
		}
	}

    public void setWirelessMessage(String message) {
		this.mWirelessMessage = message;
		if(mWirelessTextView!=null){
			mWirelessTextView.setText(mWirelessMessage);
		}
	}

	public int getLoadDrawable() {
		return mLoadDrawable;
	}

	public void setLoadDrawable(int resid) {
		this.mLoadDrawable = resid;
		if(mLoadImageView != null){
			mLoadImageView.setImageResource(resid);
		}
	}
	
	public int getRefreshDrawable() {
		return mRefreshDrawable;
	}

	public void setRefreshDrawable(int resid) {
		this.mRefreshDrawable = resid;
		if(mRefreshImageView != null){
			mRefreshImageView.setImageResource(resid);
		}
	}

    public int getWirelessDrawable() {
		return mRefreshDrawable;
	}

	public void setWirelessDrawable(int resid) {
		this.mRefreshDrawable = resid;
		if(mRefreshImageView != null){
			mRefreshImageView.setImageResource(resid);
		}
	}

	public void setBackgroundColor(int backgroundColor) {
		rootView.setBackgroundColor(backgroundColor);
	}
	
	public RelativeLayout getRootView(){
		return rootView;
	}
	
	public AbFragmentOnLoadListener getAbFragmentOnLoadListener() {
		return mAbFragmentOnLoadListener;
	}

	public void setAbFragmentOnLoadListener(
			AbFragmentOnLoadListener abFragmentOnLoadListener) {
		this.mAbFragmentOnLoadListener = abFragmentOnLoadListener;
	}
	
	/**
	 * 加载事件的接口.
	 */
	public interface AbFragmentOnLoadListener {

		/**
		 * 加载
		 */
		public void onLoad();
		
	}
    
    /**
     * 检测网络是否连接
     * @return 状态符
     */
    private  int isConnected(){
        ConnectivityManager cm = (ConnectivityManager) Global.app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        int  netType = AbConstants.ConnectionType.NO_CONNECTION; //-1
        if(info == null || !info.isConnected()){
            
        }else if(info.getType() == ConnectivityManager.TYPE_MOBILE){
            netType = AbConstants.ConnectionType.IS_GPRS;
        }else if(info.getType() == ConnectivityManager.TYPE_WIFI){
            netType = AbConstants.ConnectionType.IS_WIFI;
        }else {
            netType = AbConstants.ConnectionType.UNKONWN;
        }
        return netType;
    }
	
}
