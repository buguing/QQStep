package com.wellee.dynamicarc;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class DynamicArc extends View {

    private int mBorderWidth;
    private int mInnerColor;
    private int mOuterColor;
    private int mTextSize;
    private int mTextColor;
    private RectF mRect;

    private int mMaxStep;
    private int mCurrentStep;
    private Paint mTextPaint;
    private Rect mTextBounds;
    /**
     * width and height
     */
    private int mSize;
    private Paint mOuterPaint;
    private Paint mInnerPaint;

    public DynamicArc(Context context) {
        this(context, null);
    }

    public DynamicArc(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicArc(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DynamicArc);
        mBorderWidth = array.getDimensionPixelSize(R.styleable.DynamicArc_borderWidth, dp2px(context, 10));
        mInnerColor = array.getColor(R.styleable.DynamicArc_innerColor, ContextCompat.getColor(context, R.color.colorAccent));
        mOuterColor = array.getColor(R.styleable.DynamicArc_outerColor, ContextCompat.getColor(context, R.color.colorPrimary));
        mTextSize = array.getDimensionPixelSize(R.styleable.DynamicArc_centerTextSize, dp2px(context, 18));
        mTextColor = array.getColor(R.styleable.DynamicArc_centerTextColor, ContextCompat.getColor(context, R.color.colorAccent));
        mMaxStep = array.getInt(R.styleable.DynamicArc_maxStep, 1000);
        array.recycle();
    }

    private void initPaint() {
        initInnerPaint();
        initOuterPaint();
        initTextPaint();
    }

    private void initInnerPaint() {
        mInnerPaint = initCirclePaint();
        mInnerPaint.setColor(mInnerColor);
    }

    private void initOuterPaint() {
        mOuterPaint = initCirclePaint();
        mOuterPaint.setColor(mOuterColor);
    }

    private Paint initCirclePaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mBorderWidth);
        return paint;
    }

    private void initTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int width, height;
        //如果是精确测量 则直接返回值 相当于match_parent或者直接精确的数值
        if (modeWidth == MeasureSpec.EXACTLY) {
            width = sizeWidth;
        } else {
            if (modeWidth == MeasureSpec.AT_MOST) {
                //相当于wrap_content
                width = sizeWidth;
            } else {
                // unspecified 取最大值
                width = Math.max(dp2px(getContext(), 100), sizeWidth);
            }
        }
        if (modeHeight == MeasureSpec.EXACTLY) {
            height = sizeHeight;
        } else {
            if (modeHeight == MeasureSpec.AT_MOST) {
                height = sizeHeight;
            } else {
                height = Math.max(dp2px(getContext(), 100), sizeHeight);
            }
        }
        mSize = Math.min(width, height);
        setMeasuredDimension(mSize, mSize);

        initRect();
    }

    private void initRect() {
        mRect = new RectF();
        int center = mSize / 2;
        int radius = center - mBorderWidth / 2;
        mRect.top = center - radius;
        mRect.bottom = center + radius;
        mRect.left = center - radius;
        mRect.right = center + radius;

        mTextBounds = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画外圆弧
        canvas.drawArc(mRect, 135, 270, false, mOuterPaint);
        // 画内圆弧
        float percent = (float) mCurrentStep / mMaxStep;
        canvas.drawArc(mRect, 135, percent * 270, false, mInnerPaint);
        // 画文字
        drawCenterText(canvas);
    }

    private void drawCenterText(Canvas canvas) {
        String stepText = String.valueOf(mCurrentStep);
        mTextPaint.getTextBounds(stepText, 0, stepText.length(), mTextBounds);
        int dx = mSize / 2 - mTextBounds.width() / 2;
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int dy = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        int baseLine = mSize / 2 + dy;
        canvas.drawText(stepText, dx, baseLine, mTextPaint);
    }

    public void setMaxStep(int maxStep) {
        this.mMaxStep = maxStep;
    }

    public synchronized void setCurrentStep(int currentStep) {
        this.mCurrentStep = currentStep;
        invalidate();
    }

    private int dp2px(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp);
    }


}
