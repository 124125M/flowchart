package com.gmail.dailyefforts.flowchart.drawings;

        import android.graphics.Canvas;
        import android.graphics.Paint;
        import android.graphics.RectF;

public class Oval extends Drawing {
    private RectF mRectF = new RectF();

    @Override
    public void draw(Canvas canvas, Paint paint) {
        mRectF.left = left;
        mRectF.right = right;
        mRectF.top = top;
        mRectF.bottom = bottom;
        canvas.drawOval(mRectF, paint);
    }
}
