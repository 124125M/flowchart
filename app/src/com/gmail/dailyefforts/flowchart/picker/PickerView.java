package com.gmail.dailyefforts.flowchart.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.TableLayout;

import com.gmail.dailyefforts.flowchart.MainActivity;
import com.gmail.dailyefforts.flowchart.R;

public class PickerView extends TableLayout implements View.OnClickListener {
    private MainActivity mActivity;
    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLis(MainActivity activity) {
        View row =  findViewById(R.id.tb);
        Button btnDecision = (Button) row.findViewById(R.id.btn_decision);
        Button btnTerminator = (Button) row.findViewById(R.id.btn_terminator);
        Button btnProcess = (Button) row.findViewById(R.id.btn_process);
        btnDecision.setOnClickListener(this);
        btnTerminator.setOnClickListener(this);
        btnProcess.setOnClickListener(this);
        mActivity = activity;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_terminator:
                break;
            case R.id.btn_decision:
                break;
            case R.id.btn_process:
                break;
            default:
                break;
        }
        if (mActivity != null) {
            mActivity.onClick(v);
        }
        PickerPopup.getInstance().dismiss();
    }
}
