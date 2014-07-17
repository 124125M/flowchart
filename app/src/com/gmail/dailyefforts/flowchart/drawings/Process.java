package com.gmail.dailyefforts.flowchart.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.gmail.dailyefforts.flowchart.tools.Brush;

/**
 * A rectangle.
 */
public class Process extends Drawing {
    public Process() {
        mText = "Process";
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        assert (canvas != null);
        canvas.drawRect(left, top, right, bottom, Brush.getInstance());
        Rect rect = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), rect);
        if (rect.width() < width() && rect.height() < height()) {
            canvas.drawText(mText, centerX(), centerY() - rect.top - rect.height() / 2, mTextPaint);
        }
    }
}
