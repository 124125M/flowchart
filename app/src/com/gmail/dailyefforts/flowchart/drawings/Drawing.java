package com.gmail.dailyefforts.flowchart.drawings;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.gmail.dailyefforts.flowchart.ResizeType;
import com.gmail.dailyefforts.flowchart.config.Config;
import com.gmail.dailyefforts.flowchart.tools.Brush;

/**
 * Abstract shape. All the shapes in this application extends this class.
 */
public abstract class Drawing {
	protected float left;
	protected float top;
	protected float right;
	protected float bottom;
	protected Status mStatus = Status.RESIZING;

	public enum Status {
		IDLE, RESIZING, DONE, MOVING
	}

	public void setStatus(Status status) {
		mStatus = status;
	}

	public void done() {
		mStatus = Status.DONE;
	}

	public void rewind() {
		mStatus = Status.RESIZING;
	}

	public boolean isResizing() {
		return Status.RESIZING.equals(mStatus);
	}
	
	public boolean isActive() {
		return isResizing();
	}
	
	public boolean isMoving() {
		return Status.MOVING.equals(mStatus);
	}

	protected String mText;

	public float centerX() {
		return (left + right) * 0.5f;
	}

	public float height() {
		return bottom - top;
	}

	public float width() {
		return right - left;
	}

	public float centerY() {
		return (top + bottom) * 0.5f;
	}

	public void align(int x) {
		final float w = width();
		left = x - w / 2;
		right = x + w / 2;
	}

	protected int mDefaultWidth = 300;
	protected int mDefaultHeight = 200;

	protected Paint mAnchorPaint;
	protected Paint mTextPaint;

	protected Drawing() {
		mAnchorPaint = new Paint(Brush.getInstance());
		mAnchorPaint.setStyle(Paint.Style.STROKE);
		mAnchorPaint.setColor(Color.RED);
		mAnchorPaint.setStrokeWidth(6);
		mTextPaint = new Paint();
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTextSize(50);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
	}

	private boolean near(float x, float y, float centerX, float centerY) {
		final int v = Config.MOVE / 2;
		return (x > centerX - v && x < centerX + v && y > centerY - v && y < centerY
				+ v);
	}

	public ResizeType getResizeType(final int x, final int y) {
		ResizeType type = ResizeType.NONE;
		float middleX = left + width() / 2;
		float middleY = top + height() / 2;
		if (near(x, y, left, top)) {
			type = ResizeType.POINT_LEFT_TOP;
		} else if (near(x, y, left, bottom)) {
			type = ResizeType.POINT_LEFT_BOTTOM;
		} else if (near(x, y, right, top)) {
			type = ResizeType.POINT_RIGHT_TOP;
		} else if (near(x, y, right, bottom)) {
			type = ResizeType.POINT_RIGHT_BOTTOM;
		} else if (near(x, y, left, middleY)) {
			type = ResizeType.EDGE_LEFT;
		} else if (near(x, y, middleX, top)) {
			type = ResizeType.EDGE_TOP;
		} else if (near(x, y, right, middleY)) {
			type = ResizeType.EDGE_RIGHT;
		} else if (near(x, y, middleX, bottom)) {
			type = ResizeType.EDGE_BOTTOM;
		} else if (contains(x, y)) {
			type = ResizeType.MOVING;
		}
		return type;
	}

	public boolean contains(final int x, final int y) {
		return x > left && x < right && y > top && y < bottom;
	}

	public void resize(int x, int y) {

	}

	public void drawControlNodes(Canvas canvas) {
		int r = Config.MOVE / 3;
		canvas.drawCircle(left, top, r, mAnchorPaint);
		canvas.drawCircle(left, bottom, r, mAnchorPaint);
		canvas.drawCircle(right, top, r, mAnchorPaint);
		canvas.drawCircle(right, bottom, r, mAnchorPaint);
		canvas.drawCircle(left, top + height() / 2, r, mAnchorPaint);
		canvas.drawCircle(left + width() / 2, top, r, mAnchorPaint);
		canvas.drawCircle(right, top + height() / 2, r, mAnchorPaint);
		canvas.drawCircle(left + width() / 2, bottom, r, mAnchorPaint);
	}

	public void reset() {
		left = 0;
		top = 0;
		right = 0;
		bottom = 0;
	}

	/**
	 * A abstract method, that all the shapes must implement.
	 * 
	 * @param canvas
	 *            A canvas to draw on.
	 * @param paint
	 */
	public abstract void draw(Canvas canvas, Paint paint);

	public void down(float x, float y, Canvas canvas) {
		left = x;
		top = y;
		right = x + mDefaultWidth;
		bottom = y + mDefaultHeight;
	}

	public void move(float x, float y, Canvas canvas, ResizeType type) {
		float maxX = right - Config.MINI_WIDTH;
		float minX = left + Config.MINI_WIDTH;
		float minY = top + Config.MINI_HEIGHT;
		float maxY = bottom - Config.MINI_HEIGHT;
		if (this instanceof StraightLine) {
			maxX = right;
			minX = left;
			minY = top;
			maxY = bottom;
		}
		switch (type) {
		case NONE:
			break;
		case POINT_LEFT_TOP:
			left = x < maxX ? x : maxX;
			top = y < maxY ? y : maxY;
			break;
		case POINT_LEFT_BOTTOM:
			left = x < maxX ? x : maxX;
			bottom = y > minY ? y : minY;
			break;
		case POINT_RIGHT_TOP:
			right = x > minX ? x : minX;
			top = y < maxY ? y : maxY;
			break;
		case POINT_RIGHT_BOTTOM:
			right = x > minX ? x : minX;
			bottom = y > minY ? y : minY;
			break;
		case EDGE_LEFT:
			left = x < maxX ? x : maxX;
			break;
		case EDGE_TOP:
			top = y < maxY ? y : maxY;
			break;
		case EDGE_RIGHT:
			right = x > minX ? x : minX;
			break;
		case EDGE_BOTTOM:
			bottom = y > minY ? y : minY;
			break;
		case MOVING:
			final float halfWidth = width() / 2;
			final float halfHeight = height() / 2;
			left = x - halfWidth;
			right = x + halfWidth;
			top = y - halfHeight;
			bottom = y + halfHeight;
			break;
		default:
			break;
		}
	}

	public void up(float x, float y, Canvas canvas) {
		// draw(canvas, Brush.getInstance());
	}
}
