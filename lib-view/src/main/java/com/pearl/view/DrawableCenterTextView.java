package com.pearl.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class DrawableCenterTextView extends android.support.v7.widget.AppCompatTextView {
    public DrawableCenterTextView(Context context) {
        super(context);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if(drawables != null){
            Drawable leftDrawable = drawables[0]; //drawableLeft
            Drawable rightDrawable = drawables[2];//drawableRight
            if(leftDrawable !=null || rightDrawable != null){
                //1,获取text的width
                float textWidth = getPaint().measureText(getText().toString());
                //2,获取padding
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth;
                float bodyWidth;
                if(leftDrawable !=null){
                    //3,获取drawable的宽度
                    drawableWidth = leftDrawable.getIntrinsicWidth();
                    //4,获取绘制区域的总宽度
                    bodyWidth= textWidth + drawablePadding + drawableWidth;
                }else{
                    drawableWidth = rightDrawable.getIntrinsicWidth();
                    bodyWidth= textWidth + drawablePadding + drawableWidth;
                    //图片居右设置padding
                    setPadding(0, 0, (int)(getWidth() - bodyWidth), 0);
                }
                canvas.translate((getWidth() - bodyWidth)/2,0);
            }
        }
        super.onDraw(canvas);
    }
}
