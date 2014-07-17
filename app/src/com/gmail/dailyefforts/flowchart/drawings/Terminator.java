package com.gmail.dailyefforts.flowchart.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.gmail.dailyefforts.flowchart.tools.Brush;

/**
 * A rectangle.
 */
public class Terminator extends Process {
    public Terminator() {
        mText = "Terminator";
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        assert (canvas != null);
        canvas.drawRoundRect(new RectF(left, top, right, bottom), width() / 4, height() / 2, Brush.getInstance());
        Rect rect = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), rect);
        if (rect.width() < width() && rect.height() < height()) {
            canvas.drawText(mText, centerX(), centerY() - rect.top - rect.height() / 2, mTextPaint);
        }
    }
}
