package com.gmail.dailyefforts.flowchart.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.gmail.dailyefforts.flowchart.MoveType;
import com.gmail.dailyefforts.flowchart.tools.Brush;

/**
 * Track the finger's movement on the screen.
 */
public class ArrowLine extends StraightLine {

    public ArrowLine() {
        mText = "";
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        super.draw(canvas, paint);
        float h = Brush.getInstance().getStrokeWidth() * 3;
        float phi = (float) Math.atan2(bottom - top, right - left);
        float angle1 = (float) (phi - Math.PI / 6);
        float angle2 = (float) (phi + Math.PI / 6);
        float x3 = (float) (right - h * Math.cos(angle1));
        float x4 = (float) (right - h * Math.cos(angle2));
        float y3 = (float) (bottom - h * Math.sin(angle1));
        float y4 = (float) (bottom - h * Math.sin(angle2));
        canvas.drawLine(left, top, right, bottom, Brush.getInstance());
        canvas.drawLine(right, bottom, x3, y3, Brush.getInstance());
        canvas.drawLine(right, bottom, x4, y4, Brush.getInstance());
    }
}
