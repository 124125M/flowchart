package com.gmail.dailyefforts.flowchart.drawings;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.gmail.dailyefforts.flowchart.ResizeType;

/**
 * An earser, drawing the track line with the color of the bitmap's background
 * color.
 */
public class Eraser extends Drawing {
	private Path mPath;
	private float mX;
	private float mY;
	private static final float TOUCH_TOLERANCE = 4;
	private Paint mEraserPaint;

	public Eraser() {
		mPath = new Path();
		mEraserPaint = new Paint();
		mEraserPaint.setColor(Color.WHITE);
		mEraserPaint.setStrokeWidth(5f);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		System.out.println("drawing earser");
		canvas.drawPath(mPath, mEraserPaint);
	}

	@Override
	public void down(float x, float y, Canvas canvas) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	@Override
	public void move(float x, float y, Canvas canvas, ResizeType type) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
		draw(canvas, mEraserPaint);
	}

	@Override
	public void up(float x, float y, Canvas canvas) {
		mPath.lineTo(mX, mY);
		draw(canvas, mEraserPaint);
		mPath.reset();
	}
}
