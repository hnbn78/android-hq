package com.desheng.app.toucai.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

public class MyTextView extends android.support.v7.widget.AppCompatTextView {
    int lineCount = 0;

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int height = 0, width = 0;

        CharSequence charSequence = getText();
        lineCount = 0;
        int start = 0,end = 0, top = getCompoundPaddingTop();
        do {
            for (end = start; end <= charSequence.length(); end++) {
                float strwidth = getPaint().measureText(charSequence, start, end);

                if (strwidth > widthSize - getCompoundPaddingLeft() - getCompoundPaddingRight()) {
                    end--;
                    break;
                }
                if (end == charSequence.length()) {
                    break;
                }
            }

            top += getLineHeight();
            start = end;
            end = start;
            lineCount++;
            if (top >= heightSize - getCompoundPaddingBottom())
                break;
            if (start >= charSequence.length())
                break;
        } while (true);

        if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            width = widthSize;
        } else {
            if (lineCount > 1) {
                width = widthSize;
            } else {
                width = (int) getPaint().measureText(charSequence.toString()) + getCompoundPaddingLeft() + getCompoundPaddingRight();
            }

            if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                width = Math.min(width, widthSize);
            } else {
                width = Math.max(width, widthSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            height = heightSize;
        } else {
            if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = Math.min(lineCount * getLineHeight(), heightSize);
            } else {
                height = Math.max(lineCount * getLineHeight(), heightSize);

            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//  super.onDraw(canvas);
        CharSequence charSequence = getText();

        int start = 0,end = 0, top = getCompoundPaddingTop();

        if ((getGravity() & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP) {
            top = getCompoundPaddingTop();
        } else if ((getGravity() & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
            top = getMeasuredHeight() - getCompoundPaddingBottom() - lineCount * getLineHeight();
        } else if ((getGravity() & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
            top = getMeasuredHeight() - getCompoundPaddingBottom() - getCompoundPaddingTop() - lineCount * getLineHeight();
            top /= 2;
            top += getCompoundPaddingTop();
        }

        do {
            float textWidth = 0;
            for (end = start; end <= charSequence.length(); end++) {
                float width = getPaint().measureText(charSequence, start, end);

                if (width > getMeasuredWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight()) {
                    end--;
                    break;
                }
                textWidth = Math.max(textWidth, width);
                if (end == charSequence.length()) {
                    break;
                }
            }

            int drawTextX = 0;
            if ((getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT) {
                drawTextX = getCompoundPaddingLeft();
            } else if ((getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.RIGHT) {
                drawTextX = (int) (getMeasuredWidth() - getCompoundPaddingRight() - textWidth);
            } else if ((getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
                drawTextX = (int) (getCompoundPaddingLeft() + textWidth / 2);
            }

            canvas.drawText(charSequence.subSequence(start, end).toString(), drawTextX, top + getLineHeight(), getPaint());
            top += getLineHeight();
            start = end;
            end = start;

            if (top >= getMeasuredHeight() - getCompoundPaddingBottom())
                break;
            if (start >= charSequence.length())
                break;
        } while (true);
    }

}