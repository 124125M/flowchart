package com.gmail.dailyefforts.flowchart.drawings;

import com.gmail.dailyefforts.flowchart.MoveType;
import com.gmail.dailyefforts.flowchart.tools.Brush;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Track the finger's movement on the screen.
 */
public class PathLine extends Drawing {
	private Path mPath;
	private float mX;
	private float mY;
	private static final float TOUCH_TOLERANCE = 4;

	public PathLine() {
		mPath = new Path();
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawPath(mPath, paint);
	}

	@Override
	public void down(float x, float y, Canvas canvas) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	@Override
	public void move(float x, float y, Canvas canvas, MoveType type) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);

		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
		draw(canvas, Brush.getInstance());
	}

	@Override
	public void up(float x, float y, Canvas canvas) {
		mPath.lineTo(mX, mY);
		draw(canvas, Brush.getInstance());
		mPath.reset();
	}
}
