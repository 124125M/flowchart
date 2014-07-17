package com.gmail.dailyefforts.flowchart.drawings;

import com.gmail.dailyefforts.flowchart.tools.Brush;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * A straight line.
 */
public class StraightLine extends Drawing {
    @Override
    public void draw(Canvas canvas, Paint paint) {
        assert (canvas != null);
        canvas.drawLine(left, top, right, bottom, Brush.getInstance());
    }
}
