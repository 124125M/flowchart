package com.gmail.dailyefforts.flowchart.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * A circle.
 */
public class Circle extends Drawing {
	@Override
	public void draw(Canvas canvas, Paint paint) {
		assert (canvas != null);
		canvas.drawCircle(left + (right - left) / 2,
				top + (bottom - top) / 2,
				Math.abs(left - right) / 2, paint);
	}
}
