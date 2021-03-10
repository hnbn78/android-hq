package com.desheng.base.view.trendchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.ab.util.Strs;
import com.desheng.base.R;
import com.desheng.base.util.ChangdiptopxUtil;
import com.desheng.base.util.LogUtil;
import com.desheng.base.util.TrendTypeIdUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrendView extends View {

    private ChangdiptopxUtil changdiptopxUtil;
    private String[] headNum;
    private int issueColor;
    public float leftTextWidth;
    private List<TrendModel> trendModelList = new ArrayList();
    private Paint mBallPaint = null;
    private int mBallWH = 0;
    private int mBlueNum = 0;
    private float mDeltaX;
    private float mDeltaY;
    private int mHeight;
    private Paint mLinkLine = null;
    private Paint mPaintCXCSText = null;
    private Paint mPaintLeftText = null;
    private Paint mPaintLine = null;
    private Paint mPaintOtherText = null;
    private Paint mPaintPJYLText = null;
    private Paint mPaintText = null;
    private Paint mPaintZDLCText = null;
    private Paint mPaintZDYLText = null;
    private String[] mSaveBlueNum;
    private Paint mSmallBallPaint = null;
    private Paint mSmallPaintText = null;
    private int mWidth;
    private List<int[]> pos;
    private int showList;
    private String typeName;
    private int lotteryType;
    private float widthMax;
    private String[] saveBlueNums;

    public static final int LEFT_WIDTH = 59;
    public static final int WIDTH_MAX = 261;
    public static final int HEIGHT = 29;


    public TrendView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet, 0);
    }

    private int[] ballNum(int position) {
        int j = this.trendModelList.size();
        if (position < 4) {
            return new int[]{12};
        }

        if (Strs.isNotEmpty(mSaveBlueNum[position - 4])) {
            if (this.mSaveBlueNum[position - 4].contains(",")) {
                saveBlueNums = this.mSaveBlueNum[position - 4].split(",");
            } else {
                saveBlueNums = new String[]{this.mSaveBlueNum[position - 4]};
            }
        }

        ArrayList<Integer> intList = new ArrayList();

        if (saveBlueNums != null) {
            for (int i = 0; i < saveBlueNums.length; i++) {
                intList.add(Integer.valueOf(Integer.parseInt(saveBlueNums[i].trim())));
            }
        }

        Collections.sort(intList);
        int[] balls = new int[intList.size()];

        for (int i = 0; i < intList.size(); i++) {
            balls[i] = (intList.get(i)).intValue();
        }


        return balls;
    }

    /**
     * @param canvas
     */
    private void chooseTypeDraw(Canvas canvas) {
        //LogUtil.logE("开始画.....", typeName);
        switch (TrendTypeIdUtil.getType(typeName)) {
            case 13:
                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYKl8Line(canvas);
                        initMultipleData();
                        drawLinkLine(canvas);
                        drawSquareKl8Color(canvas);
                }

                return;
            case 12:

                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLine(canvas);
                        initMultipleData();
                        drawLinkLine(canvas);
                        drawSquareColor(canvas);
                }

                return;
            case 11:
                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLine(canvas);
                        initMultipleData();
                        drawLinkLine(canvas);
                        drawSquareColor(canvas);
                }

                return;
            case 10:

                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLine(canvas);
                        initMultipleData();
//                drawLinkLine(canvas);
                        drawSquareColor(canvas);
                }

                return;
            case 9:
                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLine(canvas);
                        initMultipleData();
//                drawLinkLine(canvas);
                        drawSquareColor(canvas);
                }
                return;
            case 8:

                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLineSame(canvas);
                        initMultipleData();
//                drawLinkLine(canvas);
                        drawSquareSameColor(canvas);
                }

                return;
            case 7:

                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLineLHH(canvas);
                        initMultipleData();
//                drawLinkLine(canvas);
                        drawSquarelhhColor(canvas);
                }
                return;
            case 6:
                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLine(canvas);
                        initMultipleData();
//                drawLinkLine(canvas);
                        drawSquareColorNoHave(canvas);
                }

                return;
            case 5:

                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLine(canvas);
                        //initMultipleData();
                        initMultipleDataHZ(5, lotteryType);
                        drawSquareColorNoHave(canvas);
                }

                return;
            case 4:

                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLine(canvas);
                        initMultipleData();
//                drawLinkLine(canvas);
                        drawSquareColor(canvas);
                }

                return;
            case 3:
                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLine(canvas);
                        initMultipleData();
//                drawLinkLine(canvas);
                        drawSquareColor(canvas);
                }
                return;
            case 2:

                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLine(canvas);
                        initMultipleData();
//                drawLinkLine(canvas);
                        drawSquareColor(canvas);
                }

                return;
            case 1:

                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLine(canvas);
                        initMultipleData(1, lotteryType);
//                drawLinkLine(canvas);
                        drawBlueBall(canvas);
                }

                return;

            case 0:
                switch (lotteryType) {
                    case TrendTypeIdUtil.TYPE_11X5:
                        drawXYLine(canvas);
                        init11X5();
                        drawLinkLine(canvas);
                        drawBlueBall(canvas);
                        break;

                    case TrendTypeIdUtil.TYPE_PK10:
                        drawXYLine(canvas);
                        init11X5();
                        drawLinkLine(canvas);
                        drawBlueBall(canvas);
                        break;
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        drawXYLine(canvas);
                        initMultipleData();
                        drawLinkLine(canvas);
                        drawBlueBall(canvas);
                        break;
                }
                return;
            default:
                drawXYLine(canvas);
                initMultipleData();
//                drawLinkLine(canvas);
                drawBlueBall(canvas);
                return;
        }

    }

    private void drawBlueBall(Canvas canvas) {
        int j = this.changdiptopxUtil.dip2pxInt(15) / 10;
        Rect rect = new Rect();

        for (int i = 0; i < trendModelList.size(); i++) {
            if (i > 3) {
                drawBallColor(canvas, j, rect, i, this.mBallPaint, this.mPaintText, this.mPaintOtherText, this.mPaintLeftText);
            } else if (i == 0) {
                drawBallColor(canvas, j, rect, i, this.mBallPaint, this.mPaintCXCSText, this.mPaintCXCSText, this.mPaintCXCSText);
            } else if (i == 1) {
                drawBallColor(canvas, j, rect, i, this.mBallPaint, this.mPaintZDLCText, this.mPaintZDLCText, this.mPaintZDLCText);
            } else if (i == 2) {
                drawBallColor(canvas, j, rect, i, this.mBallPaint, this.mPaintZDYLText, this.mPaintZDYLText, this.mPaintZDYLText);
            } else if (i == 3) {
                drawBallColor(canvas, j, rect, i, this.mBallPaint, this.mPaintPJYLText, this.mPaintPJYLText, this.mPaintPJYLText);
            }
        }

        LogUtil.logE("绘画结束.....", "-----------------------------------------------------------");
    }

    private void drawBallColor(Canvas canvas, int margin, Rect originRect, int position, Paint ballPaint, Paint paint1, Paint paint2, Paint paint3) {
        if (this.trendModelList.get(position).getBlueMiss() != null) {
            String[] arrayOfString = (this.trendModelList.get(position)).getBlueMiss().split("[,+]");
            String issueNo = (this.trendModelList.get(position)).getIssueNo();
            int[] ballNum = ballNum(position);
            int j = 0;

            for (int i = 0; i < this.mBlueNum; i++) {
                float[] points;
                float f1;
                float f2;
                float f3;
                Rect rect1;
                Paint.FontMetricsInt localFontMetricsInt;
                int k;
                int m;

                if (headNum != null && headNum.length > 0 && ballNum != null && ballNum.length > 0) {
                    if (ballNum[j] == Integer.parseInt(this.headNum[i].trim())) {
                        points = translateBallXY(position, i);
                        f1 = points[0];
                        f2 = margin;
                        originRect.left = ((int) (f1 + f2));
                        originRect.top = ((int) (points[1] + f2));
                        f1 = originRect.top;
                        f2 = this.mDeltaY;
                        f3 = margin * 2;
                        originRect.bottom = ((int) (f1 + f2 - f3));
                        originRect.right = ((int) (originRect.left + this.mDeltaX - f3));
                        canvas.drawOval(new RectF(this.leftTextWidth + originRect.left, originRect.top, this.leftTextWidth + originRect.right, originRect.bottom), ballPaint);
                        rect1 = new Rect((int) (this.leftTextWidth + originRect.left), originRect.top, (int) (this.leftTextWidth + originRect.right), originRect.bottom);
                        localFontMetricsInt = paint1.getFontMetricsInt();
                        k = ((rect1).bottom + (rect1).top - localFontMetricsInt.bottom - localFontMetricsInt.top) / 2;
                        paint1.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText(this.headNum[i], (rect1).centerX(), k, paint1);
                        k = backNum(j, ballNum);
                        j = backPos(j, ballNum);
                        m = this.mBallWH * 16 / 20;
                        if (k > 1) {
                            originRect.top = ((int) points[1]);
                            f1 = points[0];
                            f2 = m;
                            originRect.left = ((int) (f1 + f2));
                            originRect.right = ((int) (originRect.left + this.mDeltaX - f2));
                            originRect.bottom = ((int) (originRect.top + this.mDeltaY - f2));
                            canvas.drawOval(new RectF(this.leftTextWidth + originRect.left, originRect.top, this.leftTextWidth + originRect.right, originRect.bottom), this.mSmallBallPaint);
                            Rect rect = new Rect((int) (this.leftTextWidth + originRect.left), originRect.top, (int) (this.leftTextWidth + originRect.right), originRect.bottom);
                            Paint.FontMetricsInt metrics = this.mSmallPaintText.getFontMetricsInt();
                            m = ((rect).bottom + (rect).top - (metrics).bottom - (metrics).top) / 2;
                            this.mSmallPaintText.setTextAlign(Paint.Align.CENTER);
                            canvas.drawText(String.valueOf(k), (rect).centerX(), m, this.mSmallPaintText);
                        }
                    } else {
                        k = Integer.parseInt(arrayOfString[i].trim());
                        points = translateBallXY(position, i);
                        f1 = points[0];
                        f2 = margin;
                        originRect.left = ((int) (f1 + f2));
                        originRect.top = ((int) (points[1] + f2));
                        f1 = originRect.top;
                        f2 = this.mDeltaY;
                        f3 = margin * 2;
                        originRect.bottom = ((int) (f1 + f2 - f3));
                        originRect.right = ((int) (originRect.left + this.mDeltaX - f3));
                        rect1 = new Rect((int) (this.leftTextWidth + originRect.left), originRect.top, (int) (this.leftTextWidth + originRect.right), originRect.bottom);
                        Paint paint = paint2;
                        localFontMetricsInt = paint2.getFontMetricsInt();
                        m = ((rect1).bottom + (rect1).top - localFontMetricsInt.bottom - localFontMetricsInt.top) / 2;
                        (paint).setTextAlign(Paint.Align.CENTER);
                        canvas.drawText(String.valueOf(k), (rect1).centerX(), m, paint);
                    }
                }
            }

            issueText(originRect, canvas, issueNo, position, paint3);
        }
    }

    private void drawLinkLine(Canvas canvas) {
        mLinkLine.setColor(Color.parseColor("#FF0000"));
        for (int j = 4; j < mSaveBlueNum.length + 4; j++) {
            float[] lastXy = translateBallXY(4, reallyPos(this.mSaveBlueNum[0]));

            for (int i = 4; i < this.mSaveBlueNum.length + 4; i++) {
                float[] xy = translateBallXY(i, reallyPos(this.mSaveBlueNum[i - 4]));
                canvas.drawLine(this.leftTextWidth + lastXy[0] + this.mDeltaX * 0.5F, lastXy[1] + this.mDeltaY * 0.5F, this.leftTextWidth + xy[0] + this.mDeltaX * 0.5F, xy[1] + this.mDeltaY * 0.5F, this.mLinkLine);
                lastXy[0] = xy[0];
                lastXy[1] = xy[1];
            }

        }
    }

    private int backNum(int paramInt, int[] paramArrayOfInt) {
        int i = paramInt;
        int j = 0;
        while ((i < paramArrayOfInt.length) && (paramArrayOfInt[paramInt] == paramArrayOfInt[i])) {
            j += 1;
            i += 1;
        }

        return j;
    }

    private int backPos(int paramInt, int[] paramArrayOfInt) {
        int j = paramInt;
        int i = paramInt;
        int k;
        for (paramInt = j; i < paramArrayOfInt.length; paramInt = k) {
            j = i + 1;
            k = paramInt;
            if (j < paramArrayOfInt.length) {
                if (paramArrayOfInt[j] == paramArrayOfInt[i]) {
                    k = j;
                } else {
                    return paramInt + 1;
                }
            }
            i = j;
        }
        return paramInt;
    }

    private void drawSquareColor(Canvas canvas) {
        Rect localRect = new Rect();

        for (int i = 0; i < trendModelList.size(); i++) {
            String str1 = (this.trendModelList.get(i)).getIssueNo();

            for (int j = 0; j < mBlueNum; j++) {
                if ((this.trendModelList.get(i)).getTrendColorBean().getTrendName().size() >= this.mBlueNum) {
                    String str2 = (this.trendModelList.get(i)).getTrendColorBean().getTrendName().get(j);
                    float[] ballxy = translateBallXY(i, j);
                    localRect.left = ((int) ballxy[0]);
                    localRect.top = ((int) ballxy[1]);
                    localRect.bottom = ((int) (localRect.top + this.mDeltaY));
                    localRect.right = ((int) (localRect.left + this.mDeltaX));
                    RectF rectf = new RectF(this.leftTextWidth + localRect.left, localRect.top, this.leftTextWidth + localRect.right, localRect.bottom);
                    this.mBallPaint.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendBgColor().get(j)).intValue());
                    this.mPaintText.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().get(j)).intValue());
                    canvas.drawRect(rectf, this.mBallPaint);
                    Rect rect = new Rect((int) (this.leftTextWidth + localRect.left), localRect.top, (int) (this.leftTextWidth + localRect.right), localRect.bottom);
                    Paint.FontMetricsInt localFontMetricsInt = this.mPaintText.getFontMetricsInt();
                    int k = ((rect).bottom + (rect).top - localFontMetricsInt.bottom - localFontMetricsInt.top) / 2;
                    this.mPaintText.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(str2, (rect).centerX(), k, this.mPaintText);
                }
            }

            if ((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().size() >= 1) {
                this.mPaintText.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().get(0)).intValue());

            }

            issueText(localRect, canvas, str1, i, this.mPaintText);
        }
    }

    private void drawSquareColorNoHave(Canvas paramCanvas) {
        Rect localRect = new Rect();

        for (int i = 4; i < trendModelList.size(); i++) {
            String str1 = (this.trendModelList.get(i)).getIssueNo();
            for (int j = 0; j < this.mBlueNum; j++) {
                String str2 = trendModelList.get(i).getNativeCodeInt().get(j).toString();
                float[] points = translateBallXY(i - 4, j);
                localRect.left = ((int) points[0]);
                localRect.top = ((int) points[1]);
                localRect.bottom = ((int) (localRect.top + this.mDeltaY));
                localRect.right = ((int) (localRect.left + this.mDeltaX));
                RectF rectF = new RectF(this.leftTextWidth + localRect.left, localRect.top, this.leftTextWidth + localRect.right, localRect.bottom);
                this.mBallPaint.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendBgColor().get(j)).intValue());
                this.mPaintText.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().get(j)).intValue());
                paramCanvas.drawRect(rectF, this.mBallPaint);
                Rect rect = new Rect((int) (this.leftTextWidth + localRect.left), localRect.top, (int) (this.leftTextWidth + localRect.right), localRect.bottom);
                Paint.FontMetricsInt localFontMetricsInt = this.mPaintText.getFontMetricsInt();
                int k = (rect.bottom + rect.top - localFontMetricsInt.bottom - localFontMetricsInt.top) / 2;
                this.mPaintText.setTextAlign(Paint.Align.CENTER);
                paramCanvas.drawText(str2, rect.centerX(), k, this.mPaintText);
            }
            this.mPaintText.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().get(0)).intValue());
            issueText(localRect, paramCanvas, str1, i - 4, this.mPaintText);
        }
    }

    private void drawSquareKl8Color(Canvas paramCanvas) {
        Rect localrect = new Rect();

        for (int i = 0; i < trendModelList.size(); i++) {
            String str1 = (this.trendModelList.get(i)).getIssueNo();
            int j = 0;
            float f2;
            for (float f1 = 0.0F; j < this.mBlueNum; f1 = f2) {
                String str2 = (this.trendModelList.get(i)).getTrendColorBean().getTrendName().get(j);
                float[] points = translateBallXY(i, j);
                if (j != 1) {
                    f2 = f1;
                    if (j != 5) {
                    }
                } else {
                    f2 = f1 + this.mDeltaX;
                }
                localrect.left = ((int) (points[0] + f2));
                localrect.top = ((int) points[1]);
                localrect.bottom = ((int) (localrect.top + this.mDeltaY));
                localrect.right = ((int) (localrect.left + this.mDeltaX));

                RectF rectF = new RectF(this.leftTextWidth + localrect.left, localrect.top, this.leftTextWidth + localrect.right, localrect.bottom);
                this.mBallPaint.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendBgColor().get(j)).intValue());
                this.mPaintText.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().get(j)).intValue());
                paramCanvas.drawRect(rectF, this.mBallPaint);
                Rect rect = new Rect((int) (this.leftTextWidth + localrect.left), localrect.top, (int) (this.leftTextWidth + localrect.right), localrect.bottom);
                if ((j == 0) || (j == 4)) {
                    rect = new Rect((int) (this.leftTextWidth + localrect.left), localrect.top, (int) (this.leftTextWidth + localrect.right + this.mDeltaX), localrect.bottom);
                }
                Paint.FontMetricsInt localFontMetricsInt = this.mPaintText.getFontMetricsInt();
                int k = (rect.bottom + rect.top - localFontMetricsInt.bottom - localFontMetricsInt.top) / 2;
                this.mPaintText.setTextAlign(Paint.Align.CENTER);
                paramCanvas.drawText(str2, rect.centerX(), k, this.mPaintText);
                j += 1;
            }
            this.mPaintText.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().get(0)).intValue());
            issueText(localrect, paramCanvas, str1, i, this.mPaintText);
        }
    }

    private void drawSquareSameColor(Canvas paramCanvas) {
        Rect localRect = new Rect();

        for (int i = 0; i < trendModelList.size(); i++) {
            String str1 = (this.trendModelList.get(i)).getIssueNo();
            String str2;
            float[] points;
            Paint.FontMetricsInt localFontMetricsInt;
            int k;

            for (int j = 0; j < this.mBlueNum - 6; j++) {
                str2 = (this.trendModelList.get(i)).getTrendColorBean().getTrendName().get(j);
                points = translateBallXY(i, j);
                localRect.left = ((int) points[0]);
                localRect.top = ((int) points[1]);
                localRect.bottom = ((int) (localRect.top + this.mDeltaY));
                localRect.right = ((int) (localRect.left + this.mDeltaX));
                RectF rectf = new RectF(this.leftTextWidth + localRect.left, localRect.top, this.leftTextWidth + localRect.right, localRect.bottom);
                this.mBallPaint.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendBgColor().get(j)).intValue());
                this.mPaintText.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().get(j)).intValue());
                paramCanvas.drawRect(rectf, this.mBallPaint);
                Rect rect = new Rect((int) (this.leftTextWidth + localRect.left), localRect.top, (int) (this.leftTextWidth + localRect.right), localRect.bottom);
                localFontMetricsInt = this.mPaintText.getFontMetricsInt();
                k = (rect.bottom + rect.top - localFontMetricsInt.bottom - localFontMetricsInt.top) / 2;
                this.mPaintText.setTextAlign(Paint.Align.CENTER);
                paramCanvas.drawText(str2, rect.centerX(), k, this.mPaintText);
            }

            for (int j = 0; j < 3; j++) {
                k = this.mBlueNum + j - 6;
                str2 = (this.trendModelList.get(i)).getTrendColorBean().getTrendName().get(k);
                points = translateBallXY(i, k);
                localRect.left = ((int) (points[0] + j * this.mDeltaX));
                localRect.top = ((int) points[1]);
                localRect.bottom = ((int) (localRect.top + this.mDeltaY));
                localRect.right = ((int) (localRect.left + this.mDeltaX + this.mDeltaX));
                RectF rectF = new RectF(this.leftTextWidth + localRect.left, localRect.top, this.leftTextWidth + localRect.right, localRect.bottom);
                this.mBallPaint.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendBgColor().get(k)).intValue());
                this.mPaintText.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().get(k)).intValue());
                paramCanvas.drawRect(rectF, this.mBallPaint);
                Rect rect = new Rect((int) (this.leftTextWidth + localRect.left), localRect.top, (int) (this.leftTextWidth + localRect.right), localRect.bottom);
                localFontMetricsInt = this.mPaintText.getFontMetricsInt();
                k = (rect.bottom + rect.top - localFontMetricsInt.bottom - localFontMetricsInt.top) / 2;
                this.mPaintText.setTextAlign(Paint.Align.CENTER);
                paramCanvas.drawText(str2, rect.centerX(), k, this.mPaintText);
            }

            this.mPaintText.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().get(0)).intValue());
            issueText(localRect, paramCanvas, str1, i, this.mPaintText);
        }
    }

    private void drawSquarelhhColor(Canvas paramCanvas) {
        Rect localRect = new Rect();

        for (int i = 0; i < trendModelList.size(); i++) {
            String str1 = (this.trendModelList.get(i)).getIssueNo();
            String str2;
            float[] points;
            Paint.FontMetricsInt localFontMetricsInt;
            int k;

            for (int j = 0; j < this.mBlueNum; j++) {
                str2 = (this.trendModelList.get(i)).getTrendColorBean().getTrendName().get(j);
                points = translateBallXY(i, j);
                localRect.left = ((int) points[0]);
                localRect.top = ((int) points[1]);
                localRect.bottom = ((int) (localRect.top + this.mDeltaY));
                localRect.right = ((int) (localRect.left + this.mDeltaX));
                RectF rectf = new RectF(this.leftTextWidth + localRect.left, localRect.top, this.leftTextWidth + localRect.right, localRect.bottom);
                this.mBallPaint.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendBgColor().get(j)).intValue());
                this.mPaintText.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().get(j)).intValue());
                paramCanvas.drawRect(rectf, this.mBallPaint);
                Rect rect = new Rect((int) (this.leftTextWidth + localRect.left), localRect.top, (int) (this.leftTextWidth + localRect.right), localRect.bottom);
                localFontMetricsInt = this.mPaintText.getFontMetricsInt();
                k = (rect.bottom + rect.top - localFontMetricsInt.bottom - localFontMetricsInt.top) / 2;
                this.mPaintText.setTextAlign(Paint.Align.CENTER);
                paramCanvas.drawText(str2, rect.centerX(), k, this.mPaintText);
            }

            this.mPaintText.setColor(((this.trendModelList.get(i)).getTrendColorBean().getTrendTextColor().get(0)).intValue());
            issueText(localRect, paramCanvas, str1, i, this.mPaintText);
        }
    }

    private void drawXYKl8Line(Canvas paramCanvas) {
        float f1 = 0.0F;

        for (int i = 0; i < trendModelList.size(); i++) {
            f1 = this.mDeltaY;
            float f2 = i;
            float f3 = this.mDeltaX;
            float f4 = this.mBlueNum + 2;
            paramCanvas.drawLine(0.0F, f1 * f2, this.leftTextWidth + f3 * f4, this.mDeltaY * f2, this.mPaintLine);
        }

        for (int i = 0; i < mBlueNum; i++) {
            if (i == 0) {
                f1 = 0.0F;
            } else if (i == 1) {
                f1 += this.mDeltaX * 2.0F;
            } else if (i == 5) {
                f1 += this.mDeltaX * 2.0F;
            } else {
                f1 += this.mDeltaX;
            }
            paramCanvas.drawLine(this.leftTextWidth + f1, 0.0F, this.leftTextWidth + f1, this.mHeight, this.mPaintLine);
        }
    }

    private void drawXYLine(Canvas paramCanvas) {
        float f1;
        float f2;
        float f3;
        for (int i = 0; i < trendModelList.size(); i++) {

            f1 = this.mDeltaY;
            f2 = i;
            f3 = this.mDeltaX;
            float f4 = this.mBlueNum;
            paramCanvas.drawLine(0.0F, f1 * f2, this.leftTextWidth + f3 * f4, this.mDeltaY * f2, this.mPaintLine);
        }


        for (int j = 0; j < mBlueNum; j++) {
            if (j == 0) {
                f1 = this.leftTextWidth;
                f2 = this.mDeltaX;
                f3 = j;
                paramCanvas.drawLine(f1 + f2 * f3, 0.0F, this.leftTextWidth + this.mDeltaX * f3, this.mHeight, this.mPaintLine);
            } else {
                f1 = this.leftTextWidth;
                f2 = this.mDeltaX;
                f3 = j;
                paramCanvas.drawLine(f1 + f2 * f3, 0.0F, this.leftTextWidth + this.mDeltaX * f3, this.mHeight, this.mPaintLine);
            }
        }
    }

    private void drawXYLineSame(Canvas paramCanvas) {
        float f1;
        float f2;
        for (int i = 0; i < trendModelList.size(); i++) {

            f1 = this.mDeltaY;
            f2 = i;
            paramCanvas.drawLine(0.0F, f1 * f2, this.mWidth, this.mDeltaY * f2, this.mPaintLine);
        }

        for (int j = 0; j < mBlueNum - 3; j++) {
            if (j > 4) {
                f1 = this.leftTextWidth;
                f2 = j * 2 - 4;
                paramCanvas.drawLine(f1 + this.mDeltaX * f2, 0.0F, this.leftTextWidth + f2 * this.mDeltaX, this.mHeight, this.mPaintLine);
            } else {
                f1 = this.leftTextWidth;
                f2 = this.mDeltaX;
                float f3 = j;
                paramCanvas.drawLine(f1 + f2 * f3, 0.0F, this.leftTextWidth + this.mDeltaX * f3, this.mHeight, this.mPaintLine);
            }
        }
    }

    private void drawXYLineLHH(Canvas paramCanvas) {
        float f1;
        float f2;
        float f3;
        for (int i = 0; i < trendModelList.size(); i++) {

            f1 = this.mDeltaY;
            f2 = i;
            paramCanvas.drawLine(0.0F, f1 * f2, this.mWidth, this.mDeltaY * f2, this.mPaintLine);
        }

        for (int j = 0; j < mBlueNum; j++) {
            if (j == 0) {
                f1 = this.leftTextWidth;
                f2 = this.mDeltaX;
                f3 = j;
                paramCanvas.drawLine(f1 + f2 * f3, 0.0F, this.leftTextWidth + this.mDeltaX * f3, this.mHeight, this.mPaintLine);
            } else {
                f1 = this.leftTextWidth;
                f2 = this.mDeltaX;
                f3 = j;
                paramCanvas.drawLine(f1 + f2 * f3, 0.0F, this.leftTextWidth + this.mDeltaX * f3, this.mHeight, this.mPaintLine);
            }
        }
    }

    private void initMultipleData() {
        if (trendModelList == null || trendModelList.size() < 4) {
            return;
        }
        mSaveBlueNum = new String[trendModelList.size() - 4];

        for (int i = 4; i < trendModelList.size(); i++) {
            String[] winNums = (trendModelList.get(i)).getWinNumber().split("[,+]");
            if (winNums.length > 0) {
                mSaveBlueNum[i - 4] = "";
                List<Integer> winNumList;
                StringBuilder sbNums;
                if (pos.size() > 1) {
                    if ((pos.get(0)).length != (pos.get(1)).length) {
                        winNumList = new ArrayList();

                        for (int j = 0; j < (pos.get(0)).length; j++) {
                            winNumList.add(Integer.valueOf(Integer.parseInt(winNums[j].trim())));
                        }

                        Collections.sort(winNumList);
                        String[] ballNums = mSaveBlueNum;
                        sbNums = new StringBuilder();
                        sbNums.append((winNumList).get((pos.get(1))[0]));
                        ballNums[i - 4] = (sbNums).toString();
                    }
                } else {

                    for (int j = 0; j < pos.get(0).length; j++) {

                        if (j == pos.get(0).length - 1) {
                            sbNums = new StringBuilder();
                            String[] ballNums = mSaveBlueNum;
                            sbNums.append(ballNums[i - 4]);
                            String singleball = "";
                            if (winNums.length == 10 || winNums.length == 20) {
                                singleball = winNums[2 * j] + winNums[2 * j + 1];
                            } else {
                                singleball = winNums[pos.get(0)[j]];
                            }
                            sbNums.append(String.valueOf(Integer.parseInt(singleball)));
                            ballNums[i - 4] = sbNums.toString();
                        } else {
                            sbNums = new StringBuilder();
                            String[] ballNums = mSaveBlueNum;
                            sbNums.append(ballNums[i - 4]);
                            String singleball = "";
                            if (winNums.length == 10 || winNums.length == 20) {
                                singleball = winNums[2 * j] + winNums[2 * j + 1];
                            } else {
                                singleball = winNums[pos.get(0)[j]];
                            }
                            sbNums.append(String.valueOf(Integer.parseInt(singleball)));
                            sbNums.append(",");
                            ballNums[i - 4] = sbNums.toString();

                        }
                    }
                }
            }
        }
    }

    private void initMultipleData(int type1, int type2) {
        if (trendModelList == null || trendModelList.size() < 4) {
            return;
        }
        mSaveBlueNum = new String[trendModelList.size() - 4];

        for (int i = 4; i < trendModelList.size(); i++) {
            String[] winNums = (trendModelList.get(i)).getWinNumber().split("[,+]");
            if (winNums.length > 0) {
                mSaveBlueNum[i - 4] = "";
                List<Integer> winNumList;
                StringBuilder sbNums;
                if (pos.size() > 1) {
                    if ((pos.get(0)).length != (pos.get(1)).length) {
                        winNumList = new ArrayList();

                        for (int j = 0; j < (pos.get(0)).length; j++) {
                            winNumList.add(Integer.valueOf(Integer.parseInt(winNums[j].trim())));
                        }

                        Collections.sort(winNumList);
                        String[] ballNums = mSaveBlueNum;
                        sbNums = new StringBuilder();
                        sbNums.append((winNumList).get((pos.get(1))[0]));
                        ballNums[i - 4] = (sbNums).toString();
                    }
                } else {

                    for (int j = 0; j < pos.get(0).length; j++) {

                        if (j == pos.get(0).length - 1) {
                            sbNums = new StringBuilder();
                            String[] ballNums = mSaveBlueNum;
                            sbNums.append(ballNums[i - 4]);
                            String singleball = "";
                            if (winNums.length == 10 || winNums.length == 20) {

                                switch (type1) {
                                    case 1:
                                        if (type2 == TrendTypeIdUtil.TYPE_PK10) {
                                            singleball = winNums[2 * pos.get(0)[j]] + winNums[2 * pos.get(0)[j] + 1];
                                        } else {
                                            singleball = winNums[2 * j] + winNums[2 * j + 1];
                                        }
                                        break;
                                    case 5:

                                        break;
                                    default:
                                        singleball = winNums[2 * j] + winNums[2 * j + 1];
                                        break;
                                }
                            } else {
                                singleball = winNums[pos.get(0)[j]];
                            }
                            sbNums.append(String.valueOf(Integer.parseInt(singleball)));
                            ballNums[i - 4] = sbNums.toString();
                        } else {
                            sbNums = new StringBuilder();
                            String[] ballNums = mSaveBlueNum;
                            sbNums.append(ballNums[i - 4]);
                            String singleball = "";
                            if (winNums.length == 10 || winNums.length == 20) {
                                switch (type1) {
                                    case 1:
                                        if (type2 == TrendTypeIdUtil.TYPE_PK10) {
                                            singleball = winNums[2 * pos.get(0)[j]] + winNums[2 * pos.get(0)[j] + 1];
                                        } else {
                                            singleball = winNums[2 * j] + winNums[2 * j + 1];
                                        }
                                        break;
                                    case 5:

                                        break;
                                    default:
                                        singleball = winNums[2 * j] + winNums[2 * j + 1];
                                        break;
                                }
                            } else {
                                singleball = winNums[pos.get(0)[j]];

                            }
                            sbNums.append(String.valueOf(Integer.parseInt(singleball)));
                            sbNums.append(",");
                            ballNums[i - 4] = sbNums.toString();

                        }
                    }
                }
            }
        }
    }

    private void initMultipleDataHZ(int type1, int type2) {

        if (trendModelList == null || trendModelList.size() < 4) {
            return;
        }

        mSaveBlueNum = new String[trendModelList.size() - 4];

        for (int i = 4; i < trendModelList.size(); i++) {
            //String[] winNums = (trendModelList.get(i)).getWinNumber().split("[,+]");
            String[] MissNums = (trendModelList.get(i)).getBlueMiss().split("[,+]");
            if (MissNums.length > 0) {
                mSaveBlueNum[i - 4] = "";
                List<Integer> MissNumList;
                StringBuilder sbNums;
                if (pos.size() > 1) {
                    if ((pos.get(0)).length != (pos.get(1)).length) {
                        MissNumList = new ArrayList();

                        for (int j = 0; j < (pos.get(0)).length; j++) {
                            MissNumList.add(Integer.valueOf(Integer.parseInt(MissNums[j].trim())));
                        }

                        Collections.sort(MissNumList);
                        String[] ballNums = mSaveBlueNum;
                        sbNums = new StringBuilder();
                        sbNums.append((MissNumList).get((pos.get(1))[0]));
                        ballNums[i - 4] = (sbNums).toString();
                    }
                } else {

                    for (int j = 0; j < MissNums.length; j++) {

                        if (j == pos.get(0).length - 1) {
                            sbNums = new StringBuilder();
                            String[] ballNums = mSaveBlueNum;
                            sbNums.append(ballNums[i - 4]);
                            String singleball = "";
                            if (MissNums.length == 10 || MissNums.length == 20) {

                                switch (type1) {
                                    case 1:

                                        break;
                                    case 5:
                                        //singleball = MissNums[pos.get(0).length + j];
                                        singleball = MissNums[j];
                                        break;
                                    default:
                                        //singleball = MissNums[pos.get(0).length + j];
                                        singleball = MissNums[j];
                                        break;
                                }
                            } else {
                                //singleball = MissNums[pos.get(0).length + j];
                                singleball = MissNums[j];
                            }
                            sbNums.append(String.valueOf(Integer.parseInt(singleball)));
                            ballNums[i - 4] = sbNums.toString();
                        } else {
                            sbNums = new StringBuilder();
                            String[] ballNums = mSaveBlueNum;
                            sbNums.append(ballNums[i - 4]);
                            String singleball = "";
                            if (MissNums.length >= 10) {
                                switch (type1) {
                                    case 1:
                                        break;
                                    case 5:
                                        singleball = MissNums[j];
                                        break;
                                    default:
                                        singleball = MissNums[j];
                                        break;
                                }
                            } else {
                                singleball = MissNums[j];
                            }
                            sbNums.append(String.valueOf(Integer.parseInt(singleball)));
                            sbNums.append(",");
                            ballNums[i - 4] = sbNums.toString();

                        }
                        mSaveBlueNum[i - 4] = sbNums.toString();
                    }
                }
            }
        }
    }

    private void init11X5() {
        if (trendModelList == null || trendModelList.size() < 4) {
            return;
        }
        mSaveBlueNum = new String[trendModelList.size() - 4];
        for (int i = 4; i < trendModelList.size(); i++) {
            String[] winNums = trendModelList.get(i).getBlueMiss().split("[,+]");
            for (int k = 0; k < winNums.length; k++) {
                if (Strs.isEqual("0", winNums[k])) {
                    mSaveBlueNum[i - 4] = String.valueOf(k + 1);
                }
            }
        }
    }

    //设置画笔
    private void initPaint(Context paramContext) {
        changdiptopxUtil = new ChangdiptopxUtil();
        mBallWH = changdiptopxUtil.dip2pxInt(20);

        int i = getScreenDenisty();
        int j = changdiptopxUtil.getFontSizeInt(10);
        int k = changdiptopxUtil.getFontSizeInt(9);
        leftTextWidth = changdiptopxUtil.dip2pxInt(LEFT_WIDTH);
        widthMax = changdiptopxUtil.dip2pxInt(WIDTH_MAX);
        mPaintLine = new Paint();
        mPaintLine.setColor(ContextCompat.getColor(paramContext, R.color.trend_line));
        mPaintLine.setAntiAlias(true);
        Paint localPaint = mPaintLine;
        float f1 = i;
        localPaint.setStrokeWidth(0.6F * f1 / 160.0F);
        mPaintText = new Paint();
        mPaintText.setColor(ContextCompat.getColor(paramContext, R.color.white));
        localPaint = mPaintText;
        float f2 = j;
        localPaint.setTextSize(f2);
        mPaintText.setAntiAlias(true);
        mPaintText.setStrokeWidth(2.0F);
        mSmallPaintText = new Paint();
        mSmallPaintText.setColor(ContextCompat.getColor(paramContext, R.color.white));
        mSmallPaintText.setTextSize(k);
        mSmallPaintText.setAntiAlias(true);
        mSmallPaintText.setStrokeWidth(2.0F);
        mPaintOtherText = new Paint();
        mPaintOtherText.setColor(ContextCompat.getColor(paramContext, R.color.orange));
        mPaintOtherText.setTextSize(f2);
        mPaintOtherText.setAntiAlias(true);
        mPaintOtherText.setStrokeWidth(2.0F);
        mPaintCXCSText = new Paint();
        mPaintCXCSText.setColor(ContextCompat.getColor(paramContext, R.color.yellow));
        mPaintCXCSText.setTextSize(f2);
        mPaintCXCSText.setAntiAlias(true);
        mPaintCXCSText.setStrokeWidth(2.0F);
        mPaintZDLCText = new Paint();
        mPaintZDLCText.setColor(ContextCompat.getColor(paramContext, R.color.gray));
        mPaintZDLCText.setTextSize(f2);
        mPaintZDLCText.setAntiAlias(true);
        mPaintZDLCText.setStrokeWidth(2.0F);
        mPaintZDYLText = new Paint();
        mPaintZDYLText.setColor(ContextCompat.getColor(paramContext, R.color.text_golden));
        mPaintZDYLText.setTextSize(f2);
        mPaintZDYLText.setAntiAlias(true);
        mPaintZDYLText.setStrokeWidth(2.0F);
        mPaintPJYLText = new Paint();
        mPaintPJYLText.setColor(ContextCompat.getColor(paramContext, R.color.red_dark));
        mPaintPJYLText.setTextSize(f2);
        mPaintPJYLText.setAntiAlias(true);
        mPaintPJYLText.setStrokeWidth(2.0F);
        mPaintLeftText = new Paint();
        mPaintLeftText.setColor(ContextCompat.getColor(paramContext, R.color.blue_light));
        mPaintLeftText.setTextSize(f2);
        mPaintLeftText.setAntiAlias(true);
        mPaintLeftText.setStrokeWidth(2.0F);
        mLinkLine = new Paint();
        mLinkLine.setColor(ContextCompat.getColor(paramContext, R.color.green_bg_10531M));
        mLinkLine.setAntiAlias(true);
        mLinkLine.setStrokeWidth(f1 * 1.2F / 160.0F);
        mBallPaint = new Paint();
        mBallPaint.setAntiAlias(true);
        mBallPaint.setColor(ContextCompat.getColor(paramContext, R.color.red));
        mSmallBallPaint = new Paint();
        mSmallBallPaint.setAntiAlias(true);
        mSmallBallPaint.setColor(ContextCompat.getColor(paramContext, R.color.blue_light));
        mDeltaX = (widthMax / 10.0F);
        mDeltaY = changdiptopxUtil.dip2pxInt(26);
        if (mBlueNum == 11) {
            mDeltaX = (widthMax / 11.0F);
            mDeltaY = (changdiptopxUtil.dip2pxInt(2363) / 100.0F);
        } else if (mBlueNum == 9) {
            mDeltaX = changdiptopxUtil.dip2pxInt(29);
            mDeltaY = changdiptopxUtil.dip2pxInt(29);
        }
        if (showList == 9) {
            mDeltaX = (changdiptopxUtil.dip2pxInt(435) / 10.0F);
        } else if (showList == 10) {
            mDeltaX = (widthMax / 10.0F);
        }
        issueColor = ContextCompat.getColor(paramContext, R.color.black);
    }

    private int reallyPos(String paramString) {

        for (int i = 0; i < headNum.length; i++) {
            if (Strs.isNotEmpty(paramString) && Strs.isNotEmpty(headNum[i])) {
                if (Integer.parseInt(paramString.trim()) == Integer.parseInt(headNum[i].trim())) {
                    return i;
                }
            }
        }

        return 0;
    }

    private float[] translateBallXY(int x, int y) {
        return new float[]{mDeltaX * y, mDeltaY * x};
    }

    public int getScreenDenisty() {
        return getResources().getDisplayMetrics().densityDpi;
    }

    public void initData(List<TrendModel> trendModels) {
        trendModelList.clear();
        trendModelList.addAll(trendModels);
        requestLayout();
    }

    public boolean isNumeric(String paramString) {
        int i = paramString.length();
        boolean bool = true;
        if (i <= 0) {
            return true;
        }
        if (!Character.isDigit(paramString.charAt(0))) {
            bool = false;
        }
        return bool;
    }

    public void issueText(Rect rect, Canvas canvas, String issue, int point, Paint paint) {

        if (String.valueOf(issue).trim().contains("期")) {
            paint.setColor(issueColor);
        }
        float[] points = translateBallXY(point, 0);
        rect.left = ((int) points[0]);
        rect.top = ((int) points[1]);
        rect.bottom = ((int) (rect.top + mDeltaY));
        rect.right = ((int) (rect.left + leftTextWidth));
        rect = new Rect(rect.left, rect.top, rect.right, rect.bottom);

        Paint.FontMetricsInt metricsInt = paint.getFontMetricsInt();
        point = (rect.bottom + rect.top - (metricsInt).bottom - (metricsInt).top) / 2;
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(issue), rect.centerX(), point, paint);
    }

    @Override
    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        if (mBlueNum > 0) {
            chooseTypeDraw(paramCanvas);
        }
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        if (mBlueNum > 0) {
            setMeasuredDimension((int) (widthMax + leftTextWidth), (int) (trendModelList.size() * mDeltaY));
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
            invalidate();
            return;
        }
        super.onMeasure(paramInt1, paramInt2);
    }

    public void setTrendData(List<TrendModel> trendModels, Context context, List<int[]> posList, TrendTypeBean trendTypeBean) {
        pos = posList;
        headNum = trendTypeBean.getHeadNum();
        typeName = trendTypeBean.getType();
        mBlueNum = headNum.length;
        lotteryType = trendTypeBean.getLotteryType();
        showList = trendTypeBean.getShowList();
        initPaint(context);
        initData(trendModels);
    }

}
