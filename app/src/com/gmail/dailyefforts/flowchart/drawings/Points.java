package com.gmail.dailyefforts.flowchart.drawings;

import com.gmail.dailyefforts.flowchart.MoveType;
import com.gmail.dailyefforts.flowchart.tools.Brush;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Some points.
 */
public class Points extends Drawing {
	private Paint mPaint;

	public Points() {
		mPaint = new Paint(Brush.getInstance());
		mPaint.setStyle(Paint.Style.FILL);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawCircle(right, bottom,
				Brush.getInstance().getStrokeWidth() + 1, mPaint);
	}

	@Override
	public void down(float x, float y, Canvas canvas) {
		canvas.drawCircle(x, y, Brush.getInstance().getStrokeWidth() + 1,
				mPaint);
	}

	@Override
	public void move(float x, float y, Canvas canvas, MoveType type) {
		canvas.drawCircle(x, y, Brush.getInstance().getStrokeWidth() + 1,
				mPaint);
	}
}