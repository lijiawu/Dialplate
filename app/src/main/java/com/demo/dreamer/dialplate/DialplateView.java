package com.demo.dreamer.dialplate;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.dreamer.myapplication.R;

/**
 * Created by dreamer on 15-11-1.
 */
public class DialplateView extends TextView {

    private static final String TAG = "DynamicCircleView";
    private static final String STRING_FORMAI_DEFAULT = "%s";
    private static final long ANIMATION_DUR = 1000L;
    /**条纹距离progressbar边缘的距离*/
    private static final float STROPE_PADDING = 6f;
    /**每条条纹之间的距离*/
    private static final float STROPE_MARGIN = 5f;
    /**
     * 最大范围
     */
    private static final float THE_LARGEST_SWEEP_ANGLE = 300f;
    /**
     * 范围开始的距离0度偏移量
     */
    private static final float SWEEP_START_OFFSET = 120;
    /**
     * 文字的大小
     */
    private float mTextSize;
    /**
     * progressbar的宽度
     */
    private float mProgressbarWidth;
    /**
     * progressbar距离边界的距离
     */
    private float mProgressPadding = 0;
    private int mTextColor;
    private int mProgressbarColor;
    private int mProgressbarBgColor;
    private int mProgressbarStripeColor;
    private float mProgressbarStripeWidth;

    private Paint mPaint = null;
    private int mWidth;
    private int mHeight;
    private RectF mRectF = new RectF();

    /**
     * 扇形开始的角度
     */
    private float mStartAngle = SWEEP_START_OFFSET;
    /**
     * 扇形扫过的最大角度
     */
    private float mSweepAngleMax = THE_LARGEST_SWEEP_ANGLE;
    private float mSweepAngle = 0;

    // 显示的百分数
    private float mValue = 0.9f;
    private float mCurValue;
    private String mCurString;
    private String mCurFormatString = "%s";
    private float mTextX;
    private float mTextY;
    private Shader mShader = null;

    private ValueAnimator anim = null;

    public DialplateView(Context context) {
        super(context);
    }

    public DialplateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //解析xml属性
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.DialplateView);

        mProgressbarColor = a.getColor(R.styleable.DialplateView_progressbar_color,
                Color.RED);
        mProgressbarBgColor = a.getColor(R.styleable.DialplateView_progressbar_bg_color, Color.GRAY);
        mProgressbarWidth = a.getDimension(R.styleable.DialplateView_progressbar_width, 10);
        mProgressPadding = a.getDimension(R.styleable.DialplateView_padding, 0);
        mProgressbarStripeColor = a.getColor(R.styleable.DialplateView_progressbar_stripe_color, Color.WHITE);
        mProgressbarStripeWidth = a.getDimension(R.styleable.DialplateView_progressbar_stripe_width, 10);
        //计算边界要计算一下progressbar的宽度
        mProgressPadding += mProgressbarWidth;

        mTextColor = a.getColor(R.styleable.DialplateView_progressbar_bg_color, Color.BLACK);
        mTextSize = a.getDimension(R.styleable.DialplateView_textSize, 15);

        mCurFormatString = a.getString(R.styleable.DialplateView_stringformat);
        if (TextUtils.isEmpty(mCurFormatString)) {
            mCurFormatString = STRING_FORMAI_DEFAULT;
        }
        mCurString = String.format(mCurFormatString, 0);
        mPaint = getPaint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        a.recycle();
    }

    /**
     * 设置当前值.
     * <p>
     *     根据当前值,生成从0到当前值的动画对象
     * </p>
     * @param value
     */
    public void setValue(float value) {
        this.mValue = value;
        anim = ValueAnimator.ofFloat(0, mValue);
        anim.setDuration(ANIMATION_DUR);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator anim) {
                // TODO Auto-generated method stub
                mCurValue = (Float) anim.getAnimatedValue();
                if (mCurValue > mValue)
                    return;
                mSweepAngle = toSweepAngle(mCurValue);
                mCurString = String.format(mCurFormatString, (int)(mCurValue*100));
                invalidate();
            }
        });
    }

    /**
     * 设置表盘属性.
     * <p>
     *     设置表盘数值(根据该数值生成从0到该数值的动画对象),表盘文字颜色,表盘开始过渡颜色,表盘结尾过渡颜色
     * </p>
     * @param value 表盘数值
     * @param textColor 表盘文字颜色
     * @param startColor 表盘开始颜色
     * @param endColor 表盘终止颜色
     */
    public void setValue(int value,int textColor,int startColor,int endColor){

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        Log.d(TAG, "onLayout()");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow()");
    }

    public void startAnimation() {
        anim.start();
    }

    /***
     * 将数值转化为对应的角度.
     *
     * @param mValue
     * @return
     */
    private float toSweepAngle(float mValue) {
        return mValue * mSweepAngleMax;
    }

    public void setTextAlinInCenter() {
        float textWidth = mPaint.measureText(mCurString);
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        float baselineCenterVerticalY = mHeight / 2 - fm.descent
                + (fm.descent - fm.ascent) / 2;
        mTextX = mWidth / 2 - textWidth / 2;
        mTextY = baselineCenterVerticalY;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
    }

    /**
     * 根据view的长宽,更新绘制的有效区域.
     * <p>
     * 取较短的一边作为rectF正方形区域的边长
     * </p>
     *
     * @param viewWidth  view的宽
     * @param viewHeight view的高
     */
    private void updateValidRectF(int viewWidth, int viewHeight) {
        float minBetweenHW = viewWidth < viewHeight ? viewWidth : viewHeight;// 去较小的一边作为选取区域的参照
        float rectFWidth = minBetweenHW - 2 * mProgressPadding;
        float left = (viewWidth - rectFWidth) / 2;
        float top = (viewHeight - rectFWidth) / 2;
        float right = left + rectFWidth;
        float bottom = top + rectFWidth;
        mRectF.set(left, top, right, bottom);
    }

    @Override
    public void onDraw(Canvas canvas) {
        doAnimationFrame(canvas);
    }

    /**
     * 动画绘制执行方法.
     * <p>
     * 根据绘制顺序,形成最终的效果
     * <ul>
     * <li>1.绘制中间文字</li>
     * <li>2.绘制progressbar轨道背景</li>
     * <li>3.绘制progressbar</li>
     * <li>4.绘制progressbar上的条纹:计算方法:绘制progressbar上的条纹,<br/>
     * 计算方法就是计算指定角度上外部圆上的点及内部圆上的点,并为了使<br/>
     * 条纹不接触到progressbar的边,所以需要增加一下内部圆的半径,减少<br/>
     * 一下外部圆的半径</li>
     * </ul>
     * </p>
     *
     * @param canvas view中对应的画布
     */
    protected void doAnimationFrame(Canvas canvas) {
        mWidth = getWidth();
        mHeight = getHeight();
        updateValidRectF(mWidth, mHeight);

        //绘制中间文字
        setTextAlinInCenter();
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(mCurString, mTextX, mTextY, mPaint);

        //绘制progressbar的轨道背景
        mPaint.setStrokeWidth(mProgressbarWidth);
        mPaint.setColor(mProgressbarBgColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(mRectF, mStartAngle, mSweepAngleMax, false, mPaint);

        //绘制progressbar
        mPaint.setColor(mProgressbarColor);
        canvas.drawArc(mRectF, mStartAngle, mSweepAngle, false, mPaint);

        //绘制progressbar上的条纹,计算方法就是计算指定角度上外部圆上的点及内部圆上的点,并为了
        //使条纹不接触到progressbar的边,所以需要增加一下内部圆的半径,减少一下外部圆的半径
        mPaint.setColor(mProgressbarStripeColor);
        mPaint.setStrokeWidth(mProgressbarStripeWidth);
        //圆心
        float centerX = mRectF.centerX();
        float centerY = mRectF.centerY();

        float rInnerCircle = mRectF.width() / 2 - mProgressbarWidth / 2+STROPE_PADDING;
        float rOutCircle = mRectF.width() / 2 + mProgressbarWidth / 2-STROPE_PADDING;

        for(float i = mStartAngle;i<mStartAngle+mSweepAngleMax;i+=STROPE_MARGIN){
            float innerCircleEdgePointX = (float) (rInnerCircle * Math.cos(Math.toRadians(i)) + centerX);
            float innerCircleEdgePointY = (float) (rInnerCircle * Math.sin(Math.toRadians(i)) + centerY);

            float outCircleEdgePointX = (float) (rOutCircle * Math.cos(Math.toRadians(i)) + centerX);
            float outCircleEdgePointY = (float) (rOutCircle * Math.sin(Math.toRadians(i)) + centerY);
            canvas.drawLine(outCircleEdgePointX, outCircleEdgePointY, innerCircleEdgePointX, innerCircleEdgePointY, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setValue(mValue);
                startAnimation();
                break;
        }
        return super.onTouchEvent(event);
    }
}
