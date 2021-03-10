package com.pearl.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;


public class MoveImageView extends android.support.v7.widget.AppCompatImageView {
	
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	private WindowManager mWindowManager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	// 当前触摸点距屏幕左上角X坐标
	private float mRawX;
	// 当前触摸点距屏幕坐上角Y坐标
	private float mRawY;
	// 手指刚触摸屏幕时，针对其父视图X轴坐标
	private float mStartX;
	// 手指刚触摸屏幕时，针对其父视图Y轴坐标
	private float mStartY;
	private FloatViewParamsListener mListener;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public MoveImageView(Context context) {
		super(context);
	}

	public MoveImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MoveImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	// ===========================================================
	// Public Methods
	// ===========================================================

	/**
	 * 设置监听器，用于向当前ImageView传递参数
	 * 
	 * @param listener
	 */
	public void setFloatViewParamsListener(FloatViewParamsListener listener) {
		mListener = listener;
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int titleHeight = 0;
		if (mListener != null) {
			titleHeight = mListener.getTitleHeight();
		}
		
		// 当前值以屏幕左上角为原点
		mRawX = event.getRawX();
		mRawY = event.getRawY() - titleHeight;
		
		final int action = event.getAction();
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// 以当前view左上角为原点
			mStartX = mRawX;
			mStartY = mRawY;
			
			break;

		case MotionEvent.ACTION_MOVE:
			updateWindowPosition();
			break;
			
		case MotionEvent.ACTION_UP:
			updateWindowPosition();
			break;	
		}
		
		// 不消耗触摸事件
		return super.onTouchEvent(event);
	}
	
	// ===========================================================
	// Private Methods
	// ===========================================================

	/**
	 * 更新窗口参数，控制浮动窗口移动
	 */
	private void updateWindowPosition() {
		if (mListener != null) {
			// 更新root坐标
			LayoutParams layoutParams = mListener.getLayoutParams();
			layoutParams.x = (int)(layoutParams.x + mRawX - mStartX);
			layoutParams.y = (int)(layoutParams.y + mRawY - mStartY);
			
			// 使参数生效
			mWindowManager.updateViewLayout(mListener.getRoot(), layoutParams);
			
			//每次更新，就将start设为上次的。
			mStartX = mRawX;
			mStartY = mRawY;
		}
	}
	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================


	/**
	 *	当前视图用于获取参数
	 */
	public interface FloatViewParamsListener {
		
		/**
		 * 获取标题栏高度
		 * 		因为需要通过Window对象获取，所以使用此办法
		 * 
		 * @return
		 */
		public int getTitleHeight();
		
		
		/**
		 * 获取当前WindowManager.LayoutParams 对象
		 * 
		 * @return
		 */
		public LayoutParams getLayoutParams();
		
		public View getRoot();
	}



}
