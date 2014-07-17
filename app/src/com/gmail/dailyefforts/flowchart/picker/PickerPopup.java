package com.gmail.dailyefforts.flowchart.picker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.gmail.dailyefforts.flowchart.MainActivity;
import com.gmail.dailyefforts.flowchart.R;

/**
 * Created by os on 14-7-9.
 */
public class PickerPopup {
    private static PickerPopup sInstance = new PickerPopup();
    private static PopupWindow sPopup = new PopupWindow();

    public static PickerPopup getInstance() {
        return sInstance;
    }

    public static void show(View anchor, MainActivity activity) {
        LayoutInflater inflater = (LayoutInflater) anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PickerView view = (PickerView)inflater.inflate(R.layout.picker, null);
        sPopup.dismiss();
        sPopup.setBackgroundDrawable(new ColorDrawable(Color.argb(60, 0, 255, 0)));
        sPopup.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        sPopup.showAtLocation(anchor, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
        sPopup.showAsDropDown(anchor, 0, 0);
        sPopup.setFocusable(true);
        sPopup.setTouchable(true);
        sPopup.setContentView(view);
        view.setLis(activity);
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
