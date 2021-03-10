package com.pearl.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;

public class DrawableTextView extends android.support.v7.widget.AppCompatTextView {
    private int[] mHights = new int[4], mWidths = new int[4];
    private int space;
    private Bitmap[] mImages = new Bitmap[4];
    
    
    public DrawableTextView(Context context) {
        this(context, null);
    }
    
    public DrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DrawableTextView, defStyleAttr, 0);
        int n = a.getIndexCount();
        space = getCompoundDrawablePadding();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.DrawableTextView_drawableLeft) {
                mImages[0] = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
            } else if (attr == R.styleable.DrawableTextView_imageWidthLeft) {
                mWidths[0] = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.DrawableTextView_imageHightLeft) {
                mHights[0] = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            }
            
            if (attr == R.styleable.DrawableTextView_drawableTop) {
                mImages[1] = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
            } else if (attr == R.styleable.DrawableTextView_imageWidthTop) {
                mWidths[1] = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.DrawableTextView_imageHightTop) {
                mHights[1] = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            }
            
            if (attr == R.styleable.DrawableTextView_drawableRight) {
                mImages[2] = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
            } else if (attr == R.styleable.DrawableTextView_imageWidthRight) {
                mWidths[2] = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.DrawableTextView_imageHightRight) {
                mHights[2] = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            }
            
            if (attr == R.styleable.DrawableTextView_drawableBottom) {
                mImages[3] = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
            } else if (attr == R.styleable.DrawableTextView_imageWidthBottom) {
                mWidths[3] = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.DrawableTextView_imageHightBottom) {
                mHights[3] = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            }
    
            if (attr == R.styleable.DrawableTextView_space) {
                space = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            }
    
    }
        a.recycle();
        
        drawPicture();//设置图片方法
    }
    
    private void drawPicture() {
        setCompoundDrawablePadding(space);
        Drawable[] mDrawables = new Drawable[4];
        for (int i = 0; i < mImages.length; i++) {
            if (mHights[i] != 0 && mWidths[i] != 0) {
                mDrawables[i] = new BitmapDrawable(getResources(), getRealBitmap(mImages[i], mWidths[i], mHights[i]));
            } else if(mImages[i] != null){
                mDrawables[i] = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(mImages[i], mImages[i].getWidth(), mImages[i].getHeight(), true));
            }
        }
        this.setCompoundDrawablesWithIntrinsicBounds(mDrawables[0], mDrawables[1],
                mDrawables[2], mDrawables[3]);
    }
    
    private Bitmap getRealBitmap(Bitmap image, float mWidth, float mHight) {
        //根据需要Drawable原来的大小和目标宽高进行裁剪（缩放）
        int width = image.getWidth();// 获得图片的宽高
        int height = image.getHeight();
        // 取得想要缩放的matrix参数
        float scaleWidth = mWidth / width;
        float scaleHeight = mHight / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 返回新的Bitmap
        return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
        
    }
}