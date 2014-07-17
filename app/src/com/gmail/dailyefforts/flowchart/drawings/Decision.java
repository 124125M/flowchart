package com.gmail.dailyefforts.flowchart.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.gmail.dailyefforts.flowchart.tools.Brush;

/**
 * A rectangle.
 */
public class Decision extends Drawing {
    public Decision() {
        mText = "Decision";
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        assert (canvas != null);
        Path path = new Path();
        path.reset();
        path.moveTo(left + (right - left) / 2, top);
        path.lineTo(left, top + (bottom - top) / 2);
        path.lineTo(left + (right - left) / 2, bottom);
        path.lineTo(right, top + (bottom - top) /2);
        path.lineTo(left + (right - left) / 2, top);
        canvas.drawPath(path, Brush.getInstance());

        Rect rect = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), rect);
        if (rect.width() < width() && rect.height() < height()) {
            canvas.drawText(mText, centerX(), centerY() - rect.top - rect.height() / 2, mTextPaint);
        }
    }
}
