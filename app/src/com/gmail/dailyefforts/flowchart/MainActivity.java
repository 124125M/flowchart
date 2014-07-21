package com.gmail.dailyefforts.flowchart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.Paint.Style;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dailyefforts.flowchart.drawings.Drawing;
import com.gmail.dailyefforts.flowchart.drawings.DrawingFactory;
import com.gmail.dailyefforts.flowchart.drawings.Drawings;
import com.gmail.dailyefforts.flowchart.helper.ScreenInfo;
import com.gmail.dailyefforts.flowchart.picker.PickerPopup;
import com.gmail.dailyefforts.flowchart.setting.SettingsActivity;
import com.gmail.dailyefforts.flowchart.tools.Brush;

import java.io.File;

/**
 * The main Activity of the application.
 */
public class MainActivity extends Activity implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final boolean DEBUG = false;

	private DrawingPad mDrawingPad;
	private DrawingFactory mDrawingFactory;
	private boolean isFullScreen;

	// Define a Dialog id
	private static final int DIALOG_WHAT_TO_DRAW = 1;
	private static final int DIALOG_SAVE_IT_OR_NOT = 2;

	public static final int REQUEST_SETTING = 1;

	// A handle to an instance of SharedPreferences
	private SharedPreferences mPrefs;
	private int mCount;

	private MyHandler mMyHandler;

	private final int TIME_TO_START_OVER_AGAIN = 1000;
	private final int TIME_BEFORE_EXIT = TIME_TO_START_OVER_AGAIN + 500;

	private DrawerLayout mDrawerLayout;
	private ListView mListView;
	private ActionBarDrawerToggle mDrawerToggle;

	private static final String[] values = { "a", "b", "c" };
	private ShareActionProvider mShareActionProvider;

	private class DrawerListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Object getItem(int position) {
			return values[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view = (RelativeLayout) getLayoutInflater().inflate(
						R.layout.shape_item, null);
				ViewHolder holder = new ViewHolder();
				holder.imageView = (ImageView) view
						.findViewById(R.id.imageView);
				holder.textView = (TextView) view.findViewById(R.id.textView);
				view.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.textView.setText("Process");
			holder.imageView.setImageResource(R.drawable.icon);
			return view;
		}
	}

	private static class ViewHolder {
		ImageView imageView;
		TextView textView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.console);
		mDrawingPad = (DrawingPad) findViewById(R.id.paint_pad);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mListView = (ListView) findViewById(R.id.drawer_list);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mListView.setAdapter(new DrawerListAdapter());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		View content = findViewById(R.id.content);
		content.setOnDragListener(new View.OnDragListener() {

			@Override
			public boolean onDrag(View v, DragEvent event) {
				switch (event.getAction()) {
				case DragEvent.ACTION_DROP:
					System.out
							.println("MainActivity.onCreate(...) " + event.getLocalState() + ", " + event.getClipData());
					break;
				default:
					break;
				}
				return true;
			}
		});

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.open, R.string.close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("onDrawerOpened");
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				getActionBar().setTitle("onDrawerClosed");
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		setDefaultDrawing();
		Brush.getInstance().reset();

		// Get the handle to the instance of settings
		mPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		mPrefs.registerOnSharedPreferenceChangeListener(this);

		isFullScreen = mPrefs.getBoolean("check_full_screen", false);

		if (isFullScreen) {
			makeFullScreen();
		}
		ScreenInfo screen = new ScreenInfo(this);
		mMyHandler = new MyHandler();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Toast.makeText(this,
				getResources().getString(R.string.tip_touch_to_draw),
				Toast.LENGTH_SHORT).show();
	}

	private void makeFullScreen() {
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * Set the default drawing
	 */
	private void setDefaultDrawing() {
		mDrawingFactory = new DrawingFactory();
		final Drawing drawing = mDrawingFactory.createDrawing(Drawings.RECT);
		mDrawingPad.addDrawing(drawing);
	}

	/**
	 * When the up key is pressed, we pop up a dialog for the use to select
	 * shapes.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			showDialog(MainActivity.DIALOG_WHAT_TO_DRAW);
			return true;
		case KeyEvent.KEYCODE_BACK:
			// handleBackKeyDown();
			showDialog(MainActivity.DIALOG_SAVE_IT_OR_NOT);
			return true;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			mDrawingPad.saveBitmap();
			this.finish();
			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			mDrawingPad.clearCanvas();
			return true;
		}
		return false;
	}

	/**
	 * Handler back key down event. </br> If back key is pressed once in 1
	 * second, exit. Otherwise, save the bitmap before exiting.
	 */
	private void handleBackKeyDown() {
		if (mCount > 0) {
			mCount = 0;
			mDrawingPad.saveBitmap();
		} else {
			mCount++;

			Toast.makeText(
					getApplicationContext(),
					getResources()
							.getString(
									R.string.tip_press_again_to_save_bitmap_before_leaving),
					Toast.LENGTH_SHORT).show();

			Message msg_reset = Message.obtain();
			msg_reset.what = MyHandler.MSG_RESET;
			mMyHandler.sendMessageDelayed(msg_reset, TIME_TO_START_OVER_AGAIN);

			Message msg_exit = Message.obtain();
			msg_exit.what = MyHandler.MSG_EXIT;
			mMyHandler.sendMessageDelayed(msg_exit, TIME_BEFORE_EXIT);

		}

		// showDialog(DIALOG_WHAT_TO_DRAW);
	}

	private class MyHandler extends Handler {
		public static final int MSG_RESET = 0;
		public static final int MSG_EXIT = 1;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESET:
				mCount = 0;
				break;
			case MSG_EXIT:
				finish();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * @return A dialog contents all the shapes available.
	 */
	private Dialog showWhatToDrawDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		builder.setTitle(getResources().getString(
				R.string.dialog_title_what_to_draw));

		builder.setSingleChoiceItems(R.array.drawings, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						setWhatToDraw(Drawings.values()[which].ordinal());
						dialog.dismiss();
					}
				});

		builder.setNegativeButton(R.string.alert_dialog_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});

		AlertDialog alert = builder.create();
		return alert;
	}

	/**
	 * @return A dialog contents all the shapes available.
	 */
	private Dialog showSaveItOrNotDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		builder.setTitle(getResources().getString(
				R.string.dialog_title_save_it_or_not));

		builder.setPositiveButton(R.string.alert_dialog_ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDrawingPad.saveBitmap();
						finish();
					}
				});

		builder.setNegativeButton(R.string.alert_dialog_no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				});

		AlertDialog alert = builder.create();
		return alert;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case MainActivity.DIALOG_WHAT_TO_DRAW:
			return showWhatToDrawDialog();
		case MainActivity.DIALOG_SAVE_IT_OR_NOT:
			return showSaveItOrNotDialog();
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean("pen_style_key", false);
		editor.commit();
		super.onDestroy();
	}

	/**
	 * In this method, we call the factory method to create an instance of
	 * drawing.
	 * 
	 * @param which
	 *            The drawing's id, identify the current drawing.
	 */
	private void setWhatToDraw(int which) {
		String[] items = getResources().getStringArray(R.array.drawings);

		Toast.makeText(
				MainActivity.this,
				getResources().getString(R.string.tip_current_is_drawing)
						+ items[which], Toast.LENGTH_SHORT).show();

		final Drawing drawing = mDrawingFactory
				.createDrawing(Drawings.values()[which]);

		if (drawing != null) {
			mDrawingPad.addDrawing(drawing);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_paintpadactivity, menu);
		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.menu_item_share);
		// Fetch and store ShareActionProvider
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		// Note that you can set/change the intent any time,
		// say when the user has selected an image.
		return true;
	}

	// Call to update the share intent
	private void setShareIntent(Intent shareIntent) {
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(shareIntent);
		}
	}

	private Intent createShareIntent(File file) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		Uri uri = Uri.fromFile(file);
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		return shareIntent;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			System.out.println("Maint: drawer");
			return true;
		}
		final int id = item.getItemId();
		System.out.println("Maint: " + id);
		switch (id) {
		case R.id.menu_id_what_to_draw:
			showDialog(MainActivity.DIALOG_WHAT_TO_DRAW);
			break;
		case R.id.menu_id_setting:
			startSettingsActivity();
			break;
		case R.id.menu_id_save:
			File file = mDrawingPad.saveBitmap();
			mShareActionProvider.setShareIntent(createShareIntent(file));
			break;
		case R.id.menu_id_clear_screen:
			mDrawingPad.clearCanvas();
			break;
		case R.id.menu_item_share:
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_SETTING:
			break;
		}
	}

	/**
	 * To start the setting Activity, the provide some functions to setting the
	 * brush.
	 */
	private void startSettingsActivity() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), SettingsActivity.class);
		startActivityForResult(intent, MainActivity.REQUEST_SETTING);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("pen_style_key")) {
			setPenStyle(sharedPreferences.getBoolean("pen_style_key", false));
		} else if (key.equals("pen_antialias_key")) {
			Brush.getInstance().setAntiAlias(
					sharedPreferences.getBoolean(key, false));
		}
	}

	/**
	 * Set the property of the shapes.
	 * 
	 * @param flag
	 *            Fill the shapes or not
	 */
	public void setPenStyle(boolean flag) {
		if (flag) {
			Brush.getInstance().setStyle(Style.FILL);
		} else {
			Brush.getInstance().setStyle(Style.STROKE);
		}
	}

	public void onClick(View view) {
		final int id = view.getId();
		System.out.println("activity: " + view.toString());
		switch (id) {
		case R.id.btn_picker:
			final PickerPopup popup = PickerPopup.getInstance();
			if (popup.isShowing()) {
				popup.dismiss();
			} else {
				popup.show(view.getRootView(), this);
			}
			break;
		case R.id.btn_decision:
			mDrawingPad.addDrawing(mDrawingFactory
					.createDrawing(Drawings.DECISION));
			break;
		case R.id.btn_process:
			mDrawingPad
					.addDrawing(mDrawingFactory.createDrawing(Drawings.RECT));
			break;
		case R.id.btn_terminator:
			mDrawingPad.addDrawing(mDrawingFactory
					.createDrawing(Drawings.TERMINATOR));
			break;
		default:
			break;
		}
	}
}