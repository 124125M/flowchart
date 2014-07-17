package com.gmail.dailyefforts.flowchart.setting;

import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import com.gmail.dailyefforts.flowchart.R;
import com.gmail.dailyefforts.flowchart.helper.ColorPickerDialog;
import com.gmail.dailyefforts.flowchart.tools.Brush;

/**
 * When the user select "Setting" menu, jump to this Activity. This class
 * implements OnPreferenceClickListener interface, when the user click
 * Preference the onPreferenceClick() will be called.
 */
public class SettingsActivity extends PreferenceActivity implements
        OnPreferenceClickListener, ColorPickerDialog.OnColorChangedListener {
    private SharedPreferences mPrefs;
    private Preference mPenWidthPref;
    private Preference mPenColorPref;
    private Preference mCanvasBgPref;
    private String pen_width_key;
    private String pen_color_key;
    private CheckBoxPreference pen_style;

    private Preference versionName;
    private Preference lastBuildTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Inflates the settings.xml to this activity.
         */
        addPreferencesFromResource(R.xml.settings);

        pen_width_key = getResources().getString(R.string.pen_width_key);
        pen_color_key = getResources().getString(R.string.pen_color_key);

        /**
         * Find the target to be handled.
         */
        mPenWidthPref = (Preference) findPreference(getResources().getString(
                R.string.pen_width_key));
        mPenColorPref = (Preference) findPreference(getString(R.string.pen_color_key));

        versionName = (Preference) findPreference(getString(R.string.setting_about_version_key));
        lastBuildTime = (Preference) findPreference(getString(R.string.setting_about_build_key));

        versionName.setSummary(getVersionName());
        lastBuildTime.setSummary(getLastBuiltTime());

        /**
         * Register the listeners
         */
        mPenWidthPref.setOnPreferenceClickListener(this);
        mPenColorPref.setOnPreferenceClickListener(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getLastBuiltTime() {
        String lastBuiltTimeString = "Unknown";

        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(
                    getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            lastBuiltTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new java.util.Date(time));
        } catch (Exception e) {
            // Just leave the last built time to be "unknown"
        }

        return lastBuiltTimeString;
    }

    private String getVersionName() {
        String versionName = "Unknown";
        try {
            versionName = this.getPackageManager().getPackageInfo(
                    getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            // Just leave the versionName to be "Unknown"
        }
        return versionName;
    }

    /**
     * Handle the event that the preference is clicked.
     */
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(pen_color_key)) {
            String str = getResources().getString(R.string.tip_choose_color);
            new ColorPickerDialog(this, this, Brush.getInstance().getColor(),
                    str).show();
        } else if (preference.getKey().equals(pen_width_key)) {
            showSetBrushWidthDialog();
        }
        return true;
    }

    /**
     * Pop up a dialog with SeekBar to control the width of the brush.
     */
    private void showSetBrushWidthDialog() {
        SeekBarDialog seekBarDialog = new SeekBarDialog(this);
        seekBarDialog.setTitle(Brush.getInstance().getStrokeWidth() + " pixel");
        seekBarDialog.setButton(
                getResources().getString(R.string.alert_dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        );
        seekBarDialog.show();
    }

    /**
     * When brush's color is changed, this method will be called.
     */
    public void onColorChanged(int color) {
        Brush pen = Brush.getInstance();
        pen.setColor(color);
    }
}