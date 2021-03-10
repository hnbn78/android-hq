package com.pearl.view.trendchart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.ab.util.Strs;
import com.app.hubert.guide.util.ScreenUtils;
import com.pearl.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuqiang on 2014/12/29.
 * 绘制的走势图View
 */
public class TrendView extends View {

    //非小球文字画笔
    private Paint mPaintNum = null;

    //小球文字画笔
    private Paint mPaintText = null;

    ;
    //小球画笔
    private Paint mBallPaint = null;

    //网格线画笔
    private Paint mPaintLine = null;

    //小球之间的连线画笔
    private Paint mLinkLine = null;

    //同组背景画笔
    private Paint mBackgroundRec = null;

    //红球
    private Bitmap mRedBitmapBall = null;

    //蓝球
    private Bitmap mBlueBitmapBall = null;

    //网格的水平间距
    private float mDeltaX;

    //网格垂直间距
    private float mDeltaY;

    //当前View的宽度
    private int mWidth;

    //当前View的高度
    private int mHeight;

    //存储当前蓝球,方便于小球之间画连线
    private int[][] mSaveRedNum;

    //存储当前蓝球,方便于小球之间画连线
    private int[][] mSaveBlueNum;

    //红球和篮球分布数据集合
    private List<TrendModel> mBallList = new ArrayList<>();

    private int mBallWH = 0;

    private int mGroupCount;
    //红球个数:8个
    private int mCellPerGroupCount = 1;

    private int mBallRowCount = 0;
    //Integer.MIN 标识相同的球, 其他使用正常数字
    private int[][] matrixData;

    private boolean isFromOne = false;
    private boolean isFixedBit = false;
    private Context mcontext;
    private int mIndex;

    public TrendView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mcontext = context;
    }

    /***
     * 初始化资源
     */
    private void initSource() {
        int dpValue = getScreenDenisty();
        //网格线画笔
        mPaintLine = new Paint();
        mPaintLine.setColor(Color.GRAY);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(dpValue * 0.6f / 160);

        //非小球上文字画笔
        mPaintNum = new Paint();
        mPaintNum.setColor(Color.BLACK);
        mPaintNum.setTextSize((dpValue * 12 / 160));
        mPaintNum.setAntiAlias(true);
        mPaintNum.setStrokeWidth(2f);

        //小球上面的文字画笔
        mPaintText = new Paint();
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize((dpValue * 12 / 160));
        mPaintText.setAntiAlias(true);
        mPaintText.setStrokeWidth(2f);

        //小球之间连线画笔
        mLinkLine = new Paint();
        mLinkLine.setColor(Color.BLUE);
        mLinkLine.setAntiAlias(true);
        mLinkLine.setStrokeWidth(dpValue * 0.6f / 160);

        //小球画笔
        mBallPaint = new Paint();
        mBallPaint.setAntiAlias(true);
        mRedBitmapBall = BitmapFactory.decodeResource(getResources(), R.drawable.aicai_lottery_trend_red_down);
        mBlueBitmapBall = BitmapFactory.decodeResource(getResources(), R.drawable.aicai_lottery_trend_blue_down);

        //小球画笔
        mBackgroundRec = new Paint();
        mBackgroundRec.setAntiAlias(true);

        int singleBlankWidth = (ScreenUtils.getScreenWidth(mcontext) -
                getResources().getDimensionPixelSize(R.dimen.trend_issuno_withd)) / (mCellPerGroupCount * 2);
        mBallWH = singleBlankWidth;

        //设置单个网格的水平和垂直间距
        this.mDeltaY = mBallWH * 2f + dpValue * 0.42f / 160;

        Log.i("delta", "deltay:" + mDeltaY);//高度;50

        this.mDeltaX = this.mDeltaY;
    }

    /****
     * 初始化数据,取得红球和蓝球分布在网格的位置
     *
     * 出现总次数：统计期数内实际出现的次数。
     平均遗漏：统计期数内遗漏的平均值。（计算公式：平均遗漏＝统计期数/(出现次数+1)。）
     最大遗漏：统计期数内遗漏的最大值。
     最大连出值：统计期数内连续开出的最大值。
     */
    public void setData(ArrayList<String> listInfo, int ballRowCount, boolean isFromOne) {
        mBallRowCount = ballRowCount;
        this.isFromOne = isFromOne;
//        for (int i = 0; i < listInfo.size(); i++) {
//            mBallList.add(new TrendModel(Strs.of(i + 1), listInfo.get(i)));
//        }

        for (int i = 0; i < listInfo.size(); i++) {
            String[] split = listInfo.get(i).split(",");
            if (split.length > mIndex) {
                mBallList.add(new TrendModel(Strs.of(i + 1), split[mIndex]));
            }
        }

        matrixData = new int[listInfo.size()][(mCellPerGroupCount) * mGroupCount];
        for (int i = 0; i < matrixData.length; i++) {
            String[] arrSpan = mBallList.get(i).getWinNumber().split(",");
            for (int j = 0; j < matrixData[i].length; j++) {
                int insist = 1;
                if (i == 0) {
                    insist = 1;
                } else {
                    insist = matrixData[i - 1][j];
                    if (insist == Integer.MIN_VALUE) {
                        insist = 1;
                    } else {
                        insist++;
                    }
                }
                int value = 0;
                if (isFromOne) {
                    value = j % (mCellPerGroupCount) + 1;
                } else {
                    value = j % (mCellPerGroupCount);
                }
                if (arrSpan.length > j / mCellPerGroupCount) {
                    if (Strs.parse(arrSpan[j / mCellPerGroupCount], 0) == value) {
                        matrixData[i][j] = Integer.MIN_VALUE;
                    } else {
                        matrixData[i][j] = insist;
                    }
                }
            }
        }


        for (int j = 0; j < mCellPerGroupCount * mGroupCount; j++) {
            int cntAppear = 0;
            int cntAverLost = 0;
            int cntMaxLost = 0;
            int cntMaxShow = 0;
            int lostCnt = 0;
            int showCnt = 0;
            for (int i = 0; i < mBallRowCount; i++) {
                if (matrixData[i][j] == Integer.MIN_VALUE) {
                    cntAppear++;
                    showCnt++;
                    lostCnt = 0;
                    if (cntMaxShow < showCnt) {
                        cntMaxShow = showCnt;
                    }
                } else {
                    lostCnt++;
                    showCnt = 0;
                    if (cntMaxLost < lostCnt) {
                        cntMaxLost++;
                    }
                }
            }
            //统计期数/(出现次数+1);
            cntAverLost = (int) Math.round(mBallRowCount * 1.0 / (cntAppear + 1));
            matrixData[matrixData.length - 4][j] = cntAppear;
            matrixData[matrixData.length - 3][j] = cntAverLost;
            matrixData[matrixData.length - 2][j] = cntMaxLost;
            matrixData[matrixData.length - 1][j] = cntMaxShow;
        }

        Log.e("TrendView", "matrixData.length:" + matrixData.length);

        requestLayout();
    }

    public void setFixBitNum(boolean isFixedBit) {
        this.isFixedBit = isFixedBit;
    }

    /**
     * 设置红蓝球个数
     */
    public void setBallGroupCount(int groupCount, int countPerGroup, int index) {
        mIndex = index;
        mGroupCount = groupCount;
        mCellPerGroupCount = countPerGroup;
        initSource();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置测量View的大小:宽度和高度
        setMeasuredDimension((int) ((mCellPerGroupCount) * mGroupCount * mDeltaX), (int) (mBallList.size() * mDeltaY));
        //取得测量之后当前View的宽度
        this.mWidth = getMeasuredWidth();
        //取得测量之后当前View的高度
        this.mHeight = getMeasuredHeight();
        //重新绘制,不重绘,不会生效;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawXYLine(canvas);
        drawNumBall(canvas);
        drawRedBall(canvas);
        drawRedLinkLine(canvas);
    }

    /***
     * 绘制红球在网格中的分布图
     * @param canvas 画布
     */
    private void drawNumBall(Canvas canvas) {
        if (mIndex % 2 == 0) {
            mBallPaint.setARGB(255, 255, 68, 68);
        } else {
            mBallPaint.setARGB(255, 51, 181, 229);
        }
        //最外面控制期号
        for (int i = 0; i < matrixData.length; i++) {
            //test:04,05,08,11,21,27,08,01
            //源图矩形位置left,top,right,bottom
            Rect src = new Rect();
            //目标位置left,top,right,bottom
            Rect dst = new Rect();

            //每列开始遍历, 填入当前行号, 如果遇到有中的某行, 则重新计数
            String str;
            for (int j = 0; j < matrixData[i].length; j++) {
                float[] xy = this.translateBallXY(i, j);
                src.left = 0;
                src.top = 0;
                src.bottom = mBallWH;
                src.right = mBallWH;

                dst.left = (int) (xy[0] + this.mDeltaX * 0.1f);
                dst.top = (int) (xy[1] + this.mDeltaY * 0.1f);
                dst.bottom = (int) (dst.top + mDeltaY * 0.8f);
                dst.right = (int) (dst.left + mDeltaX * 0.8f);

                if (matrixData[i][j] == Integer.MIN_VALUE) {
                    continue;
                } else {
                    str = Strs.of(matrixData[i][j]);
                    if (matrixData[i][j] < 10) {
                        //居中显示:占一个字符
                        canvas.drawText("" + str, dst.left + src.right / 2, dst.top + this.mDeltaY * 0.6f, mPaintNum);
                    } else {
                        canvas.drawText(str, dst.left + src.right / 3, dst.top + this.mDeltaY * 0.6f, mPaintNum);
                    }
                }
            }
        }
    }


    /***
     * 绘制红球在网格中的分布图
     * @param canvas 画布
     */
    private void drawRedBall(Canvas canvas) {
        if (mIndex % 2 == 0) {
            mBallPaint.setARGB(255, 255, 68, 68);
        } else {
            mBallPaint.setARGB(255, 51, 181, 229);
        }

        //保存每期的开奖号码;蓝球
        mSaveRedNum = new int[mBallRowCount][mGroupCount];
        for (int i = 0; i < mSaveRedNum.length; i++) {
            for (int j = 0; j < mGroupCount; j++) {
                mSaveRedNum[i][j] = -1;
            }
        }
        //最外面控制期号
        for (int i = 0; i < mBallRowCount; i++) {
            //源图矩形位置left,top,right,bottom
            Rect src = new Rect();
            //目标位置left,top,right,bottom
            Rect dst = new Rect();
            //最里面控制（绘制）每期所在行,红球和蓝球分布位置
            for (int j = 0; j < matrixData[i].length; j++) {
                float[] xy = this.translateBallXY(i, j);
                src.left = 0;
                src.top = 0;
                src.bottom = mBallWH;
                src.right = mBallWH;

                dst.left = (int) (xy[0] + this.mDeltaX * 0.1f);
                dst.top = (int) (xy[1] + this.mDeltaY * 0.1f);
                dst.bottom = (int) (dst.top + mDeltaY * 0.8f);
                dst.right = (int) (dst.left + mDeltaX * 0.8f);
                RectF rf = new RectF(dst.left, dst.top, dst.right, dst.bottom);
                int value = matrixData[i][j];
                //单数组才画
                if (value == Integer.MIN_VALUE && j / mCellPerGroupCount % 2 == 0) {
                    if (isFromOne) {
                        value = j % mCellPerGroupCount + 1;
                    } else {
                        value = j % mCellPerGroupCount;
                    }
                    //这个是画圆
                    canvas.drawOval(rf, mBallPaint);
                    //如果有合适的小球图片,就用下面的方法...上面的去掉
                    //这个是画图片
                    //canvas.drawBitmap(mRedBitmapBall,src,dst,mBallPaint);

                    //显示的数字小于10
                    if (value < 10) {
                        if (isFixedBit) {
                            canvas.drawText("0" + String.valueOf(value), dst.left + src.right / 2, dst.top + this.mDeltaY * 0.55f, mPaintText);
                        } else {
                            //居中显示:占一个字符
                            canvas.drawText("" + String.valueOf(value), dst.left + src.right / 2, dst.top + this.mDeltaY * 0.55f, mPaintText);
                        }
                    } else {
                        canvas.drawText(String.valueOf(value), dst.left + src.right / 3, dst.top + this.mDeltaY * 0.55f, mPaintText);
                    }
                    mSaveRedNum[i][j / mCellPerGroupCount] = j;
                }
            }
        }
    }

    /**
     * 红球之间的连线
     *
     * @param canvas
     */
    private void drawRedLinkLine(Canvas canvas) {
        mLinkLine.setStrokeWidth(4);
        if (mIndex % 2 == 0) {
            mLinkLine.setARGB(130, 255, 68, 68);
            mBackgroundRec.setARGB(25, 255, 68, 68);
        } else {
            mLinkLine.setARGB(130, 51, 181, 229);
            mBackgroundRec.setARGB(25, 51, 181, 229);
        }
        for (int i = 0; i < mGroupCount; i++) {
            //先获取第一个球的x轴和y轴位置
            float[] lastXy = this.translateBallXY(0, mSaveRedNum[0][i]);
            for (int j = 0; j < mSaveRedNum.length; j++) {
                int value = mSaveRedNum[j][i];
                if (value > -1) {
                    float[] xy = this.translateBallXY(j, value);
                    //重新定位...同上;
                    xy[0] = xy[0];
                    //画两个球的中心点的连线...
                    canvas.drawLine(lastXy[0] + mDeltaX * 0.5f, lastXy[1] + mDeltaY * 0.5f, xy[0] + mDeltaX * 0.5f, xy[1] + mDeltaY * 0.5f, mLinkLine);
                    //赋值....
                    lastXy[0] = xy[0];
                    lastXy[1] = xy[1];
                }
            }

            //画背景
            if (i % 2 == 0) {
                float[] startConner = this.translateBallXY(0, i * mCellPerGroupCount);
                float[] lastConner = this.translateBallXY(mBallRowCount, (i + 1) * mCellPerGroupCount);
                RectF rect = new RectF(startConner[0], startConner[1], lastConner[0], lastConner[1]);
                canvas.drawRect(rect, mBackgroundRec);
            }
        }
    }


    /***
     * 绘制X轴和Y轴的网格线
     * @param canvas 画布
     */
    private void drawXYLine(Canvas canvas) {
        //期号数:X轴;含顶部和底部的边角线;
        for (int i = 0; i <= matrixData.length; i++) {
            canvas.drawLine(0, this.mDeltaY * i, this.mWidth, this.mDeltaY * i, mPaintLine);
        }
        //33个红球+16个篮球:Y轴;含最左边的边角和最右边的边角线;
        for (int i = 0; i <= matrixData[0].length; i++) {
            canvas.drawLine(this.mDeltaX * i, 0, this.mDeltaX * i, this.mHeight, mPaintLine);
        }
    }

    /**
     * 返回小球坐标
     *
     * @param rowIndex   行索引X轴;
     * @param valueIndex 当前中奖号码数字:相当于在哪个位置处:Y轴的索引.
     * @return
     */
    private float[] translateBallXY(int rowIndex, int valueIndex) {
        float[] xy = new float[2];
        xy[0] = this.mDeltaX * valueIndex;
        xy[1] = this.mDeltaY * rowIndex;
        return xy;
    }

    /**
     * 获取当前屏幕的密度
     *
     * @return
     */
    public int getScreenDenisty() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return dm.densityDpi;
    }
}