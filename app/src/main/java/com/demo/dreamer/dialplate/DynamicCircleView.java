package com.demo.dreamer.dialplate;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.dreamer.myapplication.R;

public class DynamicCircleView extends TextView {
	private static final String TAG = "DynamicCircleView";
	private static final int padding = 10;
	private static final long ANIMATION_DUR = 1000;
	private static final float strokeWidthForCircle = 5;

	private float strokeWidthForText;
	private int textColor;
	private int circleColor;

	private Paint mPaint = null;
	private int mWidth;
	private int mHeight;
	private RectF rectF = new RectF();

	private float startAngle = -90;
	private float sweepAngle = 0;

	// 显示的百分数
	private float value = 0.9f;
	private float curValue;
	private String accuracyString = 0 + "%";
	private float textX;
	private float textY;
	private Shader mShader = null;
	private int shaderStartColor = Color.RED;
	private int shaderEndColor = Color.GREEN;

	private ValueAnimator anim = null;

	public DynamicCircleView(Context context) {
		super(context);
	}

	public DynamicCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.DynamicCircleView);
		circleColor = a.getColor(R.styleable.DynamicCircleView_circleColor,
				Color.RED);
		anim = ValueAnimator.ofFloat(0, value);
		anim.setDuration(ANIMATION_DUR);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator anim) {
				// TODO Auto-generated method stub
				curValue = (Float) anim.getAnimatedValue();
				if (curValue > value)
					return;
				sweepAngle = toSweepAngle(curValue);
				accuracyString = (int) (curValue * 100) + "%";
				invalidate();
			}
		});
		mPaint = getPaint();
		strokeWidthForText = mPaint.getStrokeWidth();
		textColor = mPaint.getColor();
		mPaint.setStyle(Style.STROKE);
		a.recycle();
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		Log.d(TAG, "onLayout()");
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		// startAnimation();
		Log.d(TAG, "onAttachedToWindow()");
	}

	public void startAnimation() {
		anim.start();
	}

	private float toSweepAngle(float value) {
		return value * 360;
	}

	public void setTextAlinInCenter() {
		float textWidth = mPaint.measureText(accuracyString);
		FontMetrics fm = mPaint.getFontMetrics();
		float baselineCenterVerticalY = mHeight / 2 - fm.descent
				+ (fm.descent - fm.ascent) / 2;
		textX = mWidth / 2 - textWidth / 2;
		textY = baselineCenterVerticalY;
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.d(TAG,"onConfigurationChanged");
	}

	/**
	 * 根据view的长宽,更新绘制的有效区域.
	 * <p>
	 *     取较短的一边作为rectF正方形区域的边长
	 * </p>
	 * @param viewWidth view的宽
	 * @param viewHeight view的高
	 */
	private void updateValidRectF(int viewWidth,int viewHeight){
		float minBetweenHW = viewWidth < viewHeight ? viewWidth : viewHeight;// 去较小的一边作为选取区域的参照
		float rectFWidth = minBetweenHW - 2 * padding;
		float left = (viewWidth - rectFWidth) / 2;
		float top = (viewHeight - rectFWidth) / 2;
		float right = left + rectFWidth;
		float bottom = top + rectFWidth;
		rectF.set(left,top,right,bottom);
	}
	// 获取描画圆的区域
	private RectF getRectFByWidthAndHeight(int width, int height) {
		float minBetweenHW = width < height ? width : height;// 去较小的一边作为选取区域的参照
		float rectFWidth = minBetweenHW - 2 * padding;
		float left = (width - rectFWidth) / 2;
		float top = (height - rectFWidth) / 2;
		float right = left + rectFWidth;
		float bottom = top + rectFWidth;
		RectF rf = new RectF(left, top, right, bottom);

		// 同时计算shader
		float radius = rectFWidth / 2;
		float perimeter = radius * 3.14f;
		mShader = new LinearGradient(right, height / 2, left, height / 2,
				shaderStartColor, shaderEndColor, TileMode.CLAMP);
		return rf;
	}

	@Override
	public void onDraw(Canvas canvas) {
		doAnimationFrame(canvas);
	}

	protected void doAnimationFrame(Canvas canvas) {
		mWidth = getWidth();
		mHeight = getHeight();
		updateValidRectF(mWidth,mHeight);
		setTextAlinInCenter();
		mPaint.setColor(textColor);
		mPaint.setStrokeWidth(strokeWidthForText);
		canvas.drawText(accuracyString, textX, textY, mPaint);
		mPaint.setStrokeWidth(strokeWidthForCircle);
		mPaint.setColor(Color.BLACK);
		canvas.drawArc(rectF, 0, 360, false, mPaint);
		mPaint.setColor(circleColor);
		canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			startAnimation();
			break;
		}
		return super.onTouchEvent(event);
	}

}
