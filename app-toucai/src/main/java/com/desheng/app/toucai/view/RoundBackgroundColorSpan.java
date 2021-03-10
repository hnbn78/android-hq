package com.desheng.app.toucai.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

public class RoundBackgroundColorSpan extends ReplacementSpan {
    private int bgColor;
    private int textColor;
    private float textSize;

    public RoundBackgroundColorSpan(int bgColor, int textColor,float textSize) {
        super();
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.textSize = textSize;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return ((int) paint.measureText(text, start, end) + 60);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color1 = paint.getColor();
        paint.setColor(this.bgColor);
        canvas.drawRoundRect(new RectF(x, top + 5, x + ((int) paint.measureText(text, start, end) + 60), bottom - 10), 12, 12, paint);
        paint.setColor(this.textColor);
        paint.setTextSize(textSize);
        canvas.drawText(text, start, end, x + 10, y+5, paint);
        paint.setColor(color1);
    }
}
