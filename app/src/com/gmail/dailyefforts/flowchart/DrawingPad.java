package com.gmail.dailyefforts.flowchart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.gmail.dailyefforts.flowchart.config.Config;
import com.gmail.dailyefforts.flowchart.drawings.Drawing;
import com.gmail.dailyefforts.flowchart.helper.ScreenInfo;
import com.gmail.dailyefforts.flowchart.tools.Brush;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This is the main View class.
 */
public class DrawingPad extends View {
	private static final String TAG = DrawingPad.class.getSimpleName();

	private float mX;
	private float mY;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private boolean mIsMoving;
	private Drawing mActiveDrawing;

	private List<Drawing> mList = new ArrayList<Drawing>();

	private List<Integer> mAnchors = new ArrayList<Integer>();

	public DrawingPad(Context context, AttributeSet attrs) {
		super(context, attrs);
		final ScreenInfo screenInfo = new ScreenInfo((Activity) context);
		/**
		 * Create a bitmap with the size of the screen.
		 */
		mBitmap = Bitmap.createBitmap(screenInfo.getWidthPixels(),
				screenInfo.getHeightPixels(), Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		// Set the background color
		mCanvas.drawColor(getResources().getColor(R.color.white));
		mIsMoving = false;

		mBaseLinePaint = new Paint();
		mBaseLinePaint.setStrokeWidth(Config.density);
		mBaseLinePaint.setColor(Color.argb(100, 0, 0, 255));
		mBaseLinePaint.setStyle(Paint.Style.STROKE);
		mBaseLinePaint.setDither(true);

		post(new Runnable() {
			@Override
			public void run() {
				mAnchors.clear();
				mAnchors.add(getWidth() / 4);
				mAnchors.add(getWidth() / 2);
				mAnchors.add(getWidth() * 3 / 4);
			}
		});
	}

	private Paint mBaseLinePaint;

	/**
	 * Set the shape that is drawing.
	 * 
	 * @param drawing
	 *            Which shape to drawing current.
	 */
	public void addDrawing(Drawing drawing) {
		mActiveDrawing = drawing;
		mList.add(drawing);
		fingerDown(200, 500);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);

		final int w = getWidth();
		final int h = getHeight();
		canvas.drawLine(w / 4, 0, w / 4, h, mBaseLinePaint);
		canvas.drawLine(w / 2, 0, w / 2, h, mBaseLinePaint);
		canvas.drawLine(3 * w / 4, 0, 3 * w / 4, h, mBaseLinePaint);

		// canvas.drawBitmap(mBitmap, 0, 0, null);
		for (Drawing drawing : mList) {
			drawing.draw(canvas, Brush.getInstance());
		}
		if (mActiveDrawing.isEditing()) {
			mActiveDrawing.drawControlNodes(canvas);
		}
	}

	MoveType mMoveType = MoveType.NONE;
	private boolean isResizing = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int x = (int) event.getX();
		final int y = (int) event.getY();
		final int action = event.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mActiveDrawing.isEditing()) {
				mMoveType = mActiveDrawing.isResizing(x, y);
				if (!mActiveDrawing.contains(x, y)
						&& mMoveType == MoveType.NONE) {
					isResizing = false;
					final float centerX = mActiveDrawing.centerX();
					for (Integer anchor : mAnchors) {
						if (centerX > anchor - Config.MOVE
								&& centerX < anchor + Config.MOVE) {
							mActiveDrawing.align(anchor);
							break;
						}
					}
					mActiveDrawing.done();
				}
			} else {
				for (Drawing drawing : mList) {
					mMoveType = drawing.isResizing(x, y);
					if (mMoveType != MoveType.NONE || drawing.contains(x, y)) {
						mActiveDrawing = drawing;
						mActiveDrawing.rewind();
						isResizing = true;
						break;
					}
				}
			}
			if (!mList.contains(mActiveDrawing)) {
				mList.add(mActiveDrawing);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			fingerMove(x, y);
			break;
		case MotionEvent.ACTION_UP:
			fingerUp(x, y);
			break;
		default:
			break;
		}
		invalidate();
		return true;
	}

	/**
	 * Handles the action of finger up.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 */
	private void fingerUp(float x, float y) {
		mX = 0;
		mY = 0;
		mActiveDrawing.up(x, y, mCanvas);
		mIsMoving = false;
	}

	/**
	 * Handles the action of finger Move.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 */
	private void fingerMove(float x, float y) {
		mX = x;
		mY = y;
		mIsMoving = true;
		System.out.println("DrawingPad.fingerMove() " + isResizing + ", " + mMoveType);
		if (isResizing) {
			mActiveDrawing.move(x, y, mCanvas, mMoveType);
		}
	}

	/**
	 * Handles the action of finger down.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 */
	private void fingerDown(float x, float y) {
		mIsMoving = false;
		mActiveDrawing.down(x, y, mCanvas);
		invalidate();
	}

	/**
	 * Check the sdcard is available or not.
	 */
	public File saveBitmap() {
		String state = Environment.getExternalStorageState();
		File file = null;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			file = saveToSdcard();
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			Toast.makeText(getContext(),
					getResources().getString(R.string.tip_sdcard_is_read_only),
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(
					getContext(),
					getResources().getString(
							R.string.tip_sdcard_is_not_available),
					Toast.LENGTH_SHORT).show();
		}
		return file;
	}

	/**
	 * Clear the drawing in the canvas.
	 */
	public void clearCanvas() {
		mList.clear();
		mCanvas.drawColor(getResources().getColor(R.color.white));
		invalidate();
	}

	/**
	 * Save the bitmap to sdcard.
	 */
	private File saveToSdcard() {
		final File picFolder = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if (!picFolder.exists()) {
			picFolder.mkdirs();
		}
		final SimpleDateFormat format = new SimpleDateFormat(
				Config.TIME_FORMAT, Locale.getDefault());
		final Date date = Calendar.getInstance().getTime();
		final String timeStamp = format.format(date);
		final String fileName = timeStamp + Config.SUFFIX_NAME;
		final File pngFile = new File(picFolder, fileName);

		for (Drawing drawing : mList) {
			drawing.draw(mCanvas, Brush.getInstance());
		}

		try {
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100,
					new FileOutputStream(pngFile));
			Toast.makeText(
					getContext(),
					getResources().getString(R.string.tip_save_to)
							+ pngFile.getAbsolutePath(), Toast.LENGTH_LONG)
					.show();
		} catch (FileNotFoundException e) {
			Toast.makeText(
					getContext(),
					getResources().getString(R.string.tip_sava_failed)
							+ fileName, Toast.LENGTH_LONG).show();
			Log.e(TAG, e.getMessage());
		} finally {
			return pngFile;
		}
	}
}
