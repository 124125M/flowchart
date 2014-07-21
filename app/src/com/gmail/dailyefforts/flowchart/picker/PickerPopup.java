package com.gmail.dailyefforts.flowchart.picker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.gmail.dailyefforts.flowchart.MainActivity;
import com.gmail.dailyefforts.flowchart.R;

public class PickerPopup {
	private static PickerPopup sInstance = new PickerPopup();
	private static PopupWindow sPopup = new PopupWindow();

	public static PickerPopup getInstance() {
		return sInstance;
	}

	public static void show(View anchor, MainActivity activity) {
		LayoutInflater inflater = (LayoutInflater) anchor.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		PickerView view = (PickerView) inflater.inflate(R.layout.picker, null);
		sPopup.dismiss();
		sPopup.setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
		sPopup.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		sPopup.showAtLocation(anchor, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
		sPopup.setFocusable(true);
		sPopup.setTouchable(true);
		sPopup.setContentView(view);
		sPopup.setAnimationStyle(R.style.PopupAnim);
		view.setLis(activity);
		view.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				sPopup.dismiss();
				return true;
			}
		});
	}

	private PickerPopup() {
	}

	public boolean isShowing() {
		return sPopup.isShowing();
	}

	public void dismiss() {
		sPopup.dismiss();
	}
}
