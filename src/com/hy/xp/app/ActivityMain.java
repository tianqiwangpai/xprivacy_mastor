package com.hy.xp.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.hy.xp.app.clear.ClearFileAcitivity;
import com.hy.xp.app.task.DBMgr;
import com.hy.xp.app.task.PreferenceUtils;
import com.hy.xp.app.task.TaskAttribute;
import com.hy.xp.app.task.TaskListActivity;
import com.hy.xp.app.R;

public class ActivityMain extends ActivityBase implements OnItemSelectedListener
{
	private AppListAdapter mAppAdapter = null;

	private String searchQuery = "";
	private int mSortMode;
	private boolean mSortInvert;
	private int mProgressWidth = 0;
	private int mProgress = 0;

	private static final int SORT_BY_NAME = 0;
	private static final int SORT_BY_UID = 1;
	private static final int SORT_BY_INSTALL_TIME = 2;
	private static final int SORT_BY_UPDATE_TIME = 3;
	private static final int SORT_BY_MODIFY_TIME = 4;
	private static final int SORT_BY_STATE = 5;

	public static final String cAction = "Action";
	public static final int cActionRefresh = 1;

	private static ExecutorService mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new PriorityThreadFactory());

	private static class PriorityThreadFactory implements ThreadFactory
	{
		@Override
		public Thread newThread(Runnable r)
		{
			Thread t = new Thread(r);
			t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}

	private Comparator<ApplicationInfoEx> mSorter = new Comparator<ApplicationInfoEx>()
	{
		@Override
		public int compare(ApplicationInfoEx appInfo0, ApplicationInfoEx appInfo1)
		{
			int sortOrder = mSortInvert ? -1 : 1;
			switch (mSortMode) {
			case SORT_BY_NAME:
				return sortOrder * appInfo0.compareTo(appInfo1);
			case SORT_BY_UID:
				// Default lowest first
				return sortOrder * (appInfo0.getUid() - appInfo1.getUid());
			case SORT_BY_INSTALL_TIME:
				// Default newest first
				Long iTime0 = appInfo0.getInstallTime(ActivityMain.this);
				Long iTime1 = appInfo1.getInstallTime(ActivityMain.this);
				return sortOrder * iTime1.compareTo(iTime0);
			case SORT_BY_UPDATE_TIME:
				// Default newest first
				Long uTime0 = appInfo0.getUpdateTime(ActivityMain.this);
				Long uTime1 = appInfo1.getUpdateTime(ActivityMain.this);
				return sortOrder * uTime1.compareTo(uTime0);
			case SORT_BY_MODIFY_TIME:
				// Default newest first
				Long mTime0 = appInfo0.getModificationTime(ActivityMain.this);
				Long mTime1 = appInfo1.getModificationTime(ActivityMain.this);
				return sortOrder * mTime1.compareTo(mTime0);
			case SORT_BY_STATE:
				Integer state0 = appInfo0.getState(ActivityMain.this);
				Integer state1 = appInfo1.getState(ActivityMain.this);
				if (state0.compareTo(state1) == 0)
					return sortOrder * appInfo0.compareTo(appInfo1);
				else
					return sortOrder * state0.compareTo(state1);
			}
			return 0;
		}
	};

	private boolean mPackageChangeReceiverRegistered = false;

	private BroadcastReceiver mPackageChangeReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			ActivityMain.this.recreate();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final int userId = Util.getUserId(Process.myUid());

		// Check privacy service client
		if (!PrivacyService.checkClient())
			return;

		PrivacyManager.setSetting(0, PrivacyManager.cSettingOnDemand, Boolean.toString(false));

		// Set layout
		setContentView(R.layout.mainlist);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Set sub title
		getActionBar().setSubtitle(R.string.menu_pro);

		// Annotate
		Meta.annotate(this.getResources());

		// Get localized restriction name
		List<String> listRestrictionName = new ArrayList<String>(PrivacyManager.getRestrictions(this).navigableKeySet());
		listRestrictionName.add(0, getString(R.string.menu_all));

		// Build spinner adapter
		SpinnerAdapter spAdapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item);
		spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spAdapter.addAll(listRestrictionName);

		// Setup sort
		mSortMode = Integer.parseInt(PrivacyManager.getSetting(userId, PrivacyManager.cSettingSortMode, "0"));
		mSortInvert = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingSortInverted, false);

		// Start task to get app list
		AppListTask appListTask = new AppListTask();
		appListTask.executeOnExecutor(mExecutor, (Object) null);

		// Check environment
		Requirements.check(this);

		// Listen for package add/remove
		IntentFilter iff = new IntentFilter();
		iff.addAction(Intent.ACTION_PACKAGE_ADDED);
		iff.addAction(Intent.ACTION_PACKAGE_REMOVED);
		iff.addDataScheme("package");
		registerReceiver(mPackageChangeReceiver, iff);
		mPackageChangeReceiverRegistered = true;

		// Legacy
		if (!PrivacyManager.cVersion3) {
			long now = new Date().getTime();
			String legacy = PrivacyManager.getSetting(userId, PrivacyManager.cSettingLegacy, null);
			if (legacy == null || now > Long.parseLong(legacy) + 7 * 24 * 60 * 60 * 1000L) {
				PrivacyManager.setSetting(userId, PrivacyManager.cSettingLegacy, Long.toString(now));
			}
		}
		try {
			String s = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(s)) {
				File sdcardDir = Environment.getExternalStorageDirectory();
				String path = sdcardDir.getPath() + "/xp_log/";
				File logDir = new File(path);
				if (!logDir.exists()) {
					logDir.mkdirs();				
				}				
				Runtime.getRuntime().exec("chmod 777 " + path);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// Update list
		if (mAppAdapter != null)
			mAppAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		// Handle clear XPrivacy data (needs UI refresh)
		Bundle extras = intent.getExtras();
		if (extras != null && extras.containsKey(cAction) && extras.getInt(cAction) == cActionRefresh)
			recreate();
		else {
			// Refresh application list
			if (mAppAdapter != null)
				mAppAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (mPackageChangeReceiverRegistered) {
			unregisterReceiver(mPackageChangeReceiver);
			mPackageChangeReceiverRegistered = false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent)
	{

	}

	// Filtering

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		selectRestriction(pos);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		selectRestriction(0);
	}

	private void selectRestriction(int pos)
	{
		if (mAppAdapter != null) {
			int userId = Util.getUserId(Process.myUid());
			String restrictionName = (pos == 0 ? null : (String) PrivacyManager.getRestrictions(this).values().toArray()[pos - 1]);
			mAppAdapter.setRestrictionName(restrictionName);
			PrivacyManager.setSetting(userId, PrivacyManager.cSettingSelectedCategory, restrictionName);
			applyFilter();
		}
	}

	private void applyFilter()
	{
		if (mAppAdapter != null) {
			// ProgressBar pbFilter = (ProgressBar) findViewById(R.id.pbFilter);
			// TextView tvStats = (TextView) findViewById(R.id.tvStats);
			// TextView tvState = (TextView) findViewById(R.id.tvState);

			// Get settings
			int userId = Util.getUserId(Process.myUid());
			boolean fUsed = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFUsed, false);
			boolean fInternet = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFInternet, false);
			boolean fRestriction = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFRestriction, false);
			boolean fRestrictionNot = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFRestrictionNot, false);
			boolean fPermission = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFPermission, true);
			boolean fOnDemand = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFOnDemand, false);
			boolean fOnDemandNot = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFOnDemandNot, false);
			boolean fUser = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFUser, true);
			boolean fSystem = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFSystem, false);

			String filter = String.format("%s\n%b\n%b\n%b\n%b\n%b\n%b\n%b\n%b\n%b", searchQuery, fUsed, fInternet, fRestriction, fRestrictionNot, fPermission, fOnDemand, fOnDemandNot, fUser, fSystem);
			// pbFilter.setVisibility(ProgressBar.VISIBLE);
			// tvStats.setVisibility(TextView.GONE);

			// Adjust progress state width
			// RelativeLayout.LayoutParams tvStateLayout =
			// (RelativeLayout.LayoutParams) tvState.getLayoutParams();
			// tvStateLayout.addRule(RelativeLayout.LEFT_OF, R.id.pbFilter);

			mAppAdapter.getFilter().filter(filter);
		}
	}

	private void applySort()
	{
		if (mAppAdapter != null)
			mAppAdapter.sort();
	}

	// Options

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		if (inflater != null && PrivacyService.checkClient()) {
			// Inflate menu
			inflater.inflate(R.menu.main, menu);

			return true;
		} else
			return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		int userId = Util.getUserId(Process.myUid());
		// menu.findItem(R.id.menu_dump).setVisible(Util.isDebuggable(this));
		// Update filter count

		// Get settings
		boolean fUsed = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFUsed, false);
		boolean fInternet = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFInternet, false);
		boolean fRestriction = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFRestriction, false);
		boolean fPermission = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFPermission, true);
		boolean fOnDemand = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFOnDemand, false);
		boolean fUser = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFUser, true);
		boolean fSystem = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFSystem, false);

		// Count number of active filters
		int numberOfFilters = 0;
		if (fUsed)
			numberOfFilters++;
		if (fInternet)
			numberOfFilters++;
		if (fRestriction)
			numberOfFilters++;
		if (fPermission)
			numberOfFilters++;
		if (fOnDemand)
			numberOfFilters++;
		if (fUser)
			numberOfFilters++;
		if (fSystem)
			numberOfFilters++;

		if (numberOfFilters > 0) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_filter).copy(Bitmap.Config.ARGB_8888, true);

			Paint paint = new Paint();
			paint.setStyle(Style.FILL);
			paint.setColor(Color.GRAY);
			paint.setTextSize(bitmap.getWidth() / 3);
			paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

			String text = Integer.toString(numberOfFilters);

			Canvas canvas = new Canvas(bitmap);
			canvas.drawText(text, bitmap.getWidth() - paint.measureText(text), bitmap.getHeight(), paint);

			MenuItem fMenu = menu.findItem(R.id.menu_filter);
			fMenu.setIcon(new BitmapDrawable(getResources(), bitmap));
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		try {
			switch (item.getItemId()) {
			/*
			 * case R.id.menu_sort: optionSort(); return true;
			 */
			case R.id.menu_filter:
				optionFilter();
				return true;
				/*
				 * case R.id.menu_usage: optionUsage(); return true;
				 */
				/*
				 * case R.id.menu_template: optionTemplate(); return true;
				 */
			case R.id.menu_settings:
				optionSettings();
				return true;
			case R.id.menu_apptime:
				optionApptime();
				return true;
				/*
				 * case R.id.menu_dump: optionDump(); return true;
				 */
			case R.id.menu_task:
				optionTask();
				return true;

			case R.id.menu_currer:
				optionCurrer();
				return true;

			case R.id.menu_clear:
				optionClear();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		} catch (Throwable ex) {
			Util.bug(null, ex);
			return true;
		}
	}

	private void optionClear()
	{
		// TODO Auto-generated method stub
		Intent mIntent = new Intent(ActivityMain.this, ClearFileAcitivity.class);
		startActivity(mIntent);

	}

	private void optionCurrer()
	{
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.layout_read_file_count, null);

		TextView tvFileName = (TextView) view.findViewById(R.id.read_file_count_name);
		TextView tvFileCurrCount = (TextView) view.findViewById(R.id.read_file_count);

		String fileTask = PreferenceUtils.getParam(ActivityMain.this, "taskname", "").toString();
		List<TaskAttribute> mAttributes = DBMgr.getInstance(ActivityMain.this).getTaskAttributeByTaskName(fileTask);
		TaskAttribute mAttribute = null;
		if (mAttributes.size() > 0) {
			mAttribute = mAttributes.get(0);
		}

		tvFileName.setText("执行文件：" + PreferenceUtils.getParam(ActivityMain.this, fileTask, "curl_file_name", "").toString());
		tvFileCurrCount.setText("读取数量：" + PreferenceUtils.getParam(ActivityMain.this, fileTask, "curl_file_count", 0).toString());

		TextView tvMode = (TextView) view.findViewById(R.id.read_file_mode);
		tvMode.setText("文件读取模式：" + PreferenceUtils.getParam(ActivityMain.this, fileTask, "curl_file_read_mode", "默认"));

		if (mAttribute != null) {

			TextView tvTaskName = (TextView) view.findViewById(R.id.tvTaskName);
			tvTaskName.setText(mAttribute.getTaskName().toString() != null ? "任务名称：" + mAttribute.getTaskName() : "任务名称：" + "暂无数据");

			TextView tvTaskDesc = (TextView) view.findViewById(R.id.tvTaskDesc);
			tvTaskDesc.setText(mAttribute.getTaskDesc().toString() != null ? "任务说明：" + mAttribute.getTaskDesc() : "任务说明：" + "暂无数据");

			TextView tvTaskNewdata = (TextView) view.findViewById(R.id.tvTaskNewdata);
			tvTaskNewdata.setText(mAttribute.getTaskNewdata() + "" != null ? "新增数据：" + mAttribute.getTaskNewdata() : "新增数据：" + "暂无数据");

			TextView tvTaskNumber = (TextView) view.findViewById(R.id.tvTaskNumber);
			tvTaskNumber.setText(mAttribute.getTaskNumber() + "" != null ? "任务天数：" + mAttribute.getTaskNumber() : "任务天数：" + "暂无数据");

			TextView tvTaskReturnratio = (TextView) view.findViewById(R.id.tvTaskReturnratio);
			tvTaskReturnratio.setText(mAttribute.getTaskReturnratio() + "" != null ? "回放比率：" + mAttribute.getTaskReturnratio() : "回放比率：" + "暂无数据");

			TextView tvTaskDeclineFlag = (TextView) view.findViewById(R.id.tvTaskDeclineFlag);
			tvTaskDeclineFlag.setText(mAttribute.isTaskDeclineFlag() != false ? "是否递减：" + "是" : "是否递减：" + "否");

			TextView tvTaskDecilneRatio = (TextView) view.findViewById(R.id.tvTaskDecilneRatio);
			tvTaskDecilneRatio.setText(mAttribute.getTaskDecilneRatio() + "" != null ? "递减比率：" + mAttribute.getTaskDecilneRatio() : "递减比率：" + "暂无数据");

			TextView tvTaskDecilneMin = (TextView) view.findViewById(R.id.tvTaskDecilneMin);
			tvTaskDecilneMin.setText(mAttribute.getTaskDecilneMin() + "" != null ? "递减最小值：" + mAttribute.getTaskDecilneMin() : "递减最小值：" + "暂无数据");

			TextView tvTaskNextDayFlag = (TextView) view.findViewById(R.id.tvTaskNextDayFlag);
			tvTaskNextDayFlag.setText(mAttribute.isTaskNextDayFlag() != false ? "隔日递减：" + "是" : "隔日递减：" + "否");

			TextView tvTaskNextDayVisitInterval = (TextView) view.findViewById(R.id.tvTaskNextDayVisitInterval);
			tvTaskNextDayVisitInterval.setText(mAttribute.getTaskNextDayVisitInterval() + "" != null ? "间隔天数：" + mAttribute.getTaskNextDayVisitInterval() : "间隔天数：" + "暂无数据");

			TextView tvTaskNextDayVisitIntervalReturnRatio = (TextView) view.findViewById(R.id.tvTaskNextDayVisitIntervalReturnRatio);
			tvTaskNextDayVisitIntervalReturnRatio.setText(mAttribute.getTaskNextDayVisitDecilneRatio() + "" != null ? "隔日回放比率：" + mAttribute.getTaskNextDayVisitDecilneRatio() : "隔日回放比率：" + "暂无数据");

			TextView tvTaskNextDayVisitIntervalCount = (TextView) view.findViewById(R.id.tvTaskNextDayVisitIntervalCount);
			tvTaskNextDayVisitIntervalCount.setText(mAttribute.getTaskNextDayVisitIntervalCount() + "" != null ? "隔日次数：" + mAttribute.getTaskNextDayVisitIntervalCount() : "隔日次数：" + "暂无数据");

			TextView tvTaskNextDayVisitDeclineFlag = (TextView) view.findViewById(R.id.tvTaskNextDayVisitDeclineFlag);
			tvTaskNextDayVisitDeclineFlag.setText(mAttribute.isTaskNextDayVisitDeclineFlag() != false ? "隔日递减状态：" + "是" : "隔日递减状态：" + "否");

			TextView tvTaskNextDayVisitDecilneRatio = (TextView) view.findViewById(R.id.tvTaskNextDayVisitDecilneRatio);
			tvTaskNextDayVisitDecilneRatio.setText(mAttribute.getTaskNextDayVisitDecilneRatio() + "" != null ? "隔日递减比率：" + mAttribute.getTaskNextDayVisitDecilneRatio() : "隔日递减比率：" + "暂无数据");

			TextView tvTaskNextDayVisitDecilneMin = (TextView) view.findViewById(R.id.tvTaskNextDayVisitDecilneMin);
			tvTaskNextDayVisitDecilneMin.setText(mAttribute.getTaskNextDayVisitDecilneMin() + "" != null ? "隔日递减最小值：" + mAttribute.getTaskNextDayVisitDecilneMin() : "隔日递减最小值：" + "暂无数据");
		}
		// Build dialog
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityMain.this);
		alertDialogBuilder.setTitle("当前任务状态");
		alertDialogBuilder.setView(view);
		alertDialogBuilder.setPositiveButton(ActivityMain.this.getString(android.R.string.ok), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub

			}
		});

		// Show dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

	private void optionApptime()
	{
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mView = inflater.inflate(R.layout.apptime, null);
		final EditText etAppusetime = (EditText) mView.findViewById(R.id.apptime_edit);
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivityMain.this);
		String str = PrivacyManager.getSetting(0, PrivacyManager.cSettingTimeApp, "30");
		etAppusetime.setText(str);
		mBuilder.setView(mView);
		mBuilder.setTitle(R.string.menu_apptime);
		mBuilder.setIcon(R.drawable.ic_launcher);
		mBuilder.setPositiveButton(ActivityMain.this.getString(android.R.string.ok), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				PrivacyManager.setSetting(0, PrivacyManager.cSettingTimeApp, etAppusetime.getText().toString());
			}
		});

		mBuilder.setNegativeButton(ActivityMain.this.getString(android.R.string.cancel), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub

			}
		});
		AlertDialog mAlertDialog = mBuilder.create();
		mAlertDialog.show();
	}

	private void optionTask()
	{
		// TODO Auto-generated method stub
		Intent mIntent = new Intent(ActivityMain.this, TaskListActivity.class);
		startActivity(mIntent);
	}

	@SuppressLint("InflateParams")
	private void optionSort()
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.sort, null);
		final RadioGroup rgSMode = (RadioGroup) view.findViewById(R.id.rgSMode);
		final CheckBox cbSInvert = (CheckBox) view.findViewById(R.id.cbSInvert);

		// Initialise controls
		switch (mSortMode) {
		case SORT_BY_NAME:
			rgSMode.check(R.id.rbSName);
			break;
		case SORT_BY_UID:
			rgSMode.check(R.id.rbSUid);
			break;
		case SORT_BY_INSTALL_TIME:
			rgSMode.check(R.id.rbSInstalled);
			break;
		case SORT_BY_UPDATE_TIME:
			rgSMode.check(R.id.rbSUpdated);
			break;
		case SORT_BY_MODIFY_TIME:
			rgSMode.check(R.id.rbSModified);
			break;
		case SORT_BY_STATE:
			rgSMode.check(R.id.rbSState);
			break;
		}
		cbSInvert.setChecked(mSortInvert);

		// Build dialog
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityMain.this);
		alertDialogBuilder.setTitle(R.string.menu_sort);
		alertDialogBuilder.setIcon(getThemed(R.attr.icon_launcher));
		alertDialogBuilder.setView(view);
		alertDialogBuilder.setPositiveButton(ActivityMain.this.getString(android.R.string.ok), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (rgSMode.getCheckedRadioButtonId()) {
				case R.id.rbSName:
					mSortMode = SORT_BY_NAME;
					break;
				case R.id.rbSUid:
					mSortMode = SORT_BY_UID;
					break;
				case R.id.rbSInstalled:
					mSortMode = SORT_BY_INSTALL_TIME;
					break;
				case R.id.rbSUpdated:
					mSortMode = SORT_BY_UPDATE_TIME;
					break;
				case R.id.rbSModified:
					mSortMode = SORT_BY_MODIFY_TIME;
					break;
				case R.id.rbSState:
					mSortMode = SORT_BY_STATE;
					break;
				}
				mSortInvert = cbSInvert.isChecked();

				int userId = Util.getUserId(Process.myUid());
				PrivacyManager.setSetting(userId, PrivacyManager.cSettingSortMode, Integer.toString(mSortMode));
				PrivacyManager.setSetting(userId, PrivacyManager.cSettingSortInverted, Boolean.toString(mSortInvert));

				applySort();
			}
		});

		// Show dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@SuppressLint("InflateParams")
	private void optionFilter()
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.filters, null);
		// final CheckBox cbFUsed = (CheckBox) view.findViewById(R.id.cbFUsed);
		// final CheckBox cbFInternet = (CheckBox)
		// view.findViewById(R.id.cbFInternet);
		// final CheckBox cbFPermission = (CheckBox)
		// view.findViewById(R.id.cbFPermission);
		// final CheckBox cbFRestriction = (CheckBox)
		// view.findViewById(R.id.cbFRestriction);
		// final CheckBox cbFRestrictionNot = (CheckBox)
		// view.findViewById(R.id.cbFRestrictionNot);
		// final CheckBox cbFOnDemand = (CheckBox)
		// view.findViewById(R.id.cbFOnDemand);
		// final CheckBox cbFOnDemandNot = (CheckBox)
		// view.findViewById(R.id.cbFOnDemandNot);
		final CheckBox cbFUser = (CheckBox) view.findViewById(R.id.cbFUser);
		final CheckBox cbFSystem = (CheckBox) view.findViewById(R.id.cbFSystem);
		// final Button btnDefault = (Button)
		// view.findViewById(R.id.btnDefault);

		// Get settings
		final int userId = Util.getUserId(Process.myUid());
		boolean fUser = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFUser, true);
		boolean fSystem = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingFSystem, false);

		// Setup checkboxes
		// cbFUsed.setChecked(fUsed);
		// cbFInternet.setChecked(fInternet);
		// cbFPermission.setChecked(fPermission);
		// cbFRestriction.setChecked(fRestriction);
		// cbFRestrictionNot.setChecked(fRestrictionNot);
		// cbFOnDemand.setChecked(fOnDemand && ondemand);
		// cbFOnDemandNot.setChecked(fOnDemandNot && ondemand);
		cbFUser.setChecked(fUser);
		cbFSystem.setChecked(fSystem);

		// cbFRestrictionNot.setEnabled(fRestriction);

		// cbFOnDemand.setEnabled(ondemand);
		// cbFOnDemandNot.setEnabled(fOnDemand && ondemand);

		// Manage user/system filter exclusivity
		OnCheckedChangeListener checkListener = new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (buttonView == cbFUser) {
					if (isChecked)
						cbFSystem.setChecked(false);
				} else if (buttonView == cbFSystem) {
					if (isChecked)
						cbFUser.setChecked(false);
				}
				// else if (buttonView == cbFRestriction)
				// cbFRestrictionNot.setEnabled(cbFRestriction.isChecked());
				// else if (buttonView == cbFOnDemand)
				// cbFOnDemandNot.setEnabled(cbFOnDemand.isChecked());
			}
		};
		cbFUser.setOnCheckedChangeListener(checkListener);
		cbFSystem.setOnCheckedChangeListener(checkListener);
		// cbFRestriction.setOnCheckedChangeListener(checkListener);
		// cbFOnDemand.setOnCheckedChangeListener(checkListener);

		// Clear button
		// btnDefault.setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View arg0)
		// {
		// cbFUsed.setChecked(false);
		// cbFInternet.setChecked(false);
		// cbFPermission.setChecked(true);
		// cbFRestriction.setChecked(false);
		// cbFRestrictionNot.setChecked(false);
		// cbFOnDemand.setChecked(false);
		// cbFOnDemandNot.setChecked(false);
		// cbFUser.setChecked(true);
		// cbFSystem.setChecked(false);
		// }
		// });

		// Build dialog
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityMain.this);
		alertDialogBuilder.setTitle(R.string.menu_filter);
		alertDialogBuilder.setIcon(getThemed(R.attr.icon_launcher));
		alertDialogBuilder.setView(view);
		alertDialogBuilder.setPositiveButton(ActivityMain.this.getString(android.R.string.ok), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// PrivacyManager.setSetting(userId,
				// PrivacyManager.cSettingFUsed,
				// Boolean.toString(cbFUsed.isChecked()));
				// PrivacyManager.setSetting(userId,
				// PrivacyManager.cSettingFInternet,
				// Boolean.toString(cbFInternet.isChecked()));
				// PrivacyManager.setSetting(userId,
				// PrivacyManager.cSettingFRestriction,
				// Boolean.toString(cbFRestriction.isChecked()));
				// PrivacyManager.setSetting(userId,
				// PrivacyManager.cSettingFRestrictionNot,
				// Boolean.toString(cbFRestrictionNot.isChecked()));
				// PrivacyManager.setSetting(userId,
				// PrivacyManager.cSettingFPermission,
				// Boolean.toString(cbFPermission.isChecked()));
				// PrivacyManager.setSetting(userId,
				// PrivacyManager.cSettingFOnDemand,
				// Boolean.toString(cbFOnDemand.isChecked()));
				// PrivacyManager.setSetting(userId,
				// PrivacyManager.cSettingFOnDemandNot,
				// Boolean.toString(cbFOnDemandNot.isChecked()));
				PrivacyManager.setSetting(userId, PrivacyManager.cSettingFUser, Boolean.toString(cbFUser.isChecked()));
				PrivacyManager.setSetting(userId, PrivacyManager.cSettingFSystem, Boolean.toString(cbFSystem.isChecked()));

				invalidateOptionsMenu();
				applyFilter();
			}
		});
		alertDialogBuilder.setNegativeButton(ActivityMain.this.getString(android.R.string.cancel), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});

		// Show dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@SuppressLint("InflateParams")
	private void optionTemplate()
	{
		final int userId = Util.getUserId(Process.myUid());

		// Build view
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.template, null);
		final Spinner spTemplate = (Spinner) view.findViewById(R.id.spTemplate);
		Button btnRename = (Button) view.findViewById(R.id.btnRename);
		ExpandableListView elvTemplate = (ExpandableListView) view.findViewById(R.id.elvTemplate);

		// Template selector
		final SpinnerAdapter spAdapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item);
		spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		String defaultName = PrivacyManager.getSetting(userId, Meta.cTypeTemplateName, "0", getString(R.string.title_default));
		spAdapter.add(defaultName);
		for (int i = 1; i <= 4; i++) {
			String alternateName = PrivacyManager.getSetting(userId, Meta.cTypeTemplateName, Integer.toString(i), getString(R.string.title_alternate) + " " + i);
			spAdapter.add(alternateName);
		}
		spTemplate.setAdapter(spAdapter);

		// Template definition
		final TemplateListAdapter templateAdapter = new TemplateListAdapter(this, view, R.layout.templateentry);
		elvTemplate.setAdapter(templateAdapter);
		elvTemplate.setGroupIndicator(null);

		spTemplate.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				templateAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				templateAdapter.notifyDataSetChanged();
			}
		});

		btnRename.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				final int templateId = spTemplate.getSelectedItemPosition();
				if (templateId == AdapterView.INVALID_POSITION)
					return;

				AlertDialog.Builder dlgRename = new AlertDialog.Builder(spTemplate.getContext());
				dlgRename.setTitle(R.string.title_rename);

				final String original = (templateId == 0 ? getString(R.string.title_default) : getString(R.string.title_alternate) + " " + templateId);
				dlgRename.setMessage(original);

				final EditText input = new EditText(spTemplate.getContext());
				dlgRename.setView(input);

				dlgRename.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						String name = input.getText().toString();
						if (TextUtils.isEmpty(name)) {
							PrivacyManager.setSetting(userId, Meta.cTypeTemplateName, Integer.toString(templateId), null);
							name = original;
						} else {
							PrivacyManager.setSetting(userId, Meta.cTypeTemplateName, Integer.toString(templateId), name);
						}
						spAdapter.remove(spAdapter.getItem(templateId));
						spAdapter.insert(name, templateId);
						spAdapter.notifyDataSetChanged();
					}
				});

				dlgRename.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						// Do nothing
					}
				});

				dlgRename.create().show();
			}
		});

		// Build dialog
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(R.string.menu_template);
		alertDialogBuilder.setIcon(getThemed(R.attr.icon_launcher));
		alertDialogBuilder.setView(view);
		alertDialogBuilder.setPositiveButton(getString(R.string.msg_done), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// Do nothing
			}
		});

		// Show dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void optionSettings()
	{
		Intent intent = new Intent(this, ActivitySettings.class);
		startActivity(intent);
	}

	// Tasks

	private class AppListTask extends AsyncTask<Object, Integer, List<ApplicationInfoEx>>
	{
		private String mRestrictionName;
		private ProgressDialog mProgressDialog;

		@Override
		protected List<ApplicationInfoEx> doInBackground(Object... params)
		{
			mRestrictionName = null;

			// Delegate
			return ApplicationInfoEx.getXApplicationList(ActivityMain.this, mProgressDialog);
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			// Show progress dialog
			mProgressDialog = new ProgressDialog(ActivityMain.this);
			mProgressDialog.setMessage(getString(R.string.msg_loading));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setProgressNumberFormat(null);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected void onPostExecute(List<ApplicationInfoEx> listApp)
		{
			if (!ActivityMain.this.isFinishing()) {
				// Display app list
				mAppAdapter = new AppListAdapter(ActivityMain.this, R.layout.mainentry, listApp, mRestrictionName);
				ListView lvApp = (ListView) findViewById(R.id.lvApp);
				lvApp.setAdapter(mAppAdapter);

				// Dismiss progress dialog
				if (mProgressDialog.isShowing())
					try {
						mProgressDialog.dismiss();
					} catch (IllegalArgumentException ignored) {
					}

				// Restore state
				ActivityMain.this.selectRestriction(0);
			}

			super.onPostExecute(listApp);
		}
	}

	// Adapters

	private class SpinnerAdapter extends ArrayAdapter<String>
	{
		public SpinnerAdapter(Context context, int textViewResourceId)
		{
			super(context, textViewResourceId);
		}
	}

	@SuppressLint("DefaultLocale")
	private class TemplateListAdapter extends BaseExpandableListAdapter
	{
		private View mView;
		private Spinner mSpinner;
		private List<String> listRestrictionName;
		private List<String> listLocalizedTitle;
		private boolean ondemand;
		private Version version;
		private LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		public TemplateListAdapter(Context context, View view, int resource)
		{
			mView = view;
			mSpinner = (Spinner) view.findViewById(R.id.spTemplate);

			// Get restriction categories
			TreeMap<String, String> tmRestriction = PrivacyManager.getRestrictions(context);
			listRestrictionName = new ArrayList<String>(tmRestriction.values());
			listLocalizedTitle = new ArrayList<String>(tmRestriction.navigableKeySet());

			int userId = Util.getUserId(Process.myUid());
			ondemand = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingOnDemand, true);
			version = new Version(Util.getSelfVersionName(context));
		}

		private String getTemplate()
		{
			if (mSpinner.getSelectedItemPosition() == 0)
				return Meta.cTypeTemplate;
			else
				return Meta.cTypeTemplate + mSpinner.getSelectedItemPosition();
		}

		private class ViewHolder
		{
			private View row;
			public ImageView imgIndicator;
			public ImageView imgInfo;
			public TextView tvRestriction;
			public ImageView imgUnsafe;
			public ImageView imgCbRestrict;
			public ImageView imgCbAsk;
			public boolean restricted;
			public boolean asked;

			public ViewHolder(View theRow)
			{
				row = theRow;
				imgIndicator = (ImageView) row.findViewById(R.id.imgIndicator);
				imgInfo = (ImageView) row.findViewById(R.id.imgInfo);
				tvRestriction = (TextView) row.findViewById(R.id.tvRestriction);
				imgUnsafe = (ImageView) row.findViewById(R.id.imgUnsafe);
				imgCbRestrict = (ImageView) row.findViewById(R.id.imgCbRestrict);
				imgCbAsk = (ImageView) row.findViewById(R.id.imgCbAsk);
			}
		}

		@Override
		public Object getGroup(int groupPosition)
		{
			return listRestrictionName.get(groupPosition);
		}

		@Override
		public int getGroupCount()
		{
			return listRestrictionName.size();
		}

		@Override
		public long getGroupId(int groupPosition)
		{
			return groupPosition;
		}

		@Override
		@SuppressLint("InflateParams")
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
		{
			final ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.templateentry, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			// Get entry
			final String restrictionName = (String) getGroup(groupPosition);

			// Get info
			final int userId = Util.getUserId(Process.myUid());
			String value = PrivacyManager.getSetting(userId, getTemplate(), restrictionName, Boolean.toString(!ondemand) + "+ask");
			holder.restricted = value.contains("true");
			holder.asked = (!ondemand || value.contains("asked"));

			boolean partialRestricted = false;
			boolean partialAsked = false;
			if (holder.restricted || !holder.asked)
				for (Hook hook : PrivacyManager.getHooks(restrictionName, version)) {
					String settingName = restrictionName + "." + hook.getName();
					String childValue = PrivacyManager.getSetting(userId, getTemplate(), settingName, null);
					if (childValue == null)
						childValue = Boolean.toString(holder.restricted && !hook.isDangerous()) + (holder.asked || (hook.isDangerous() && hook.whitelist() == null) ? "+asked" : "+ask");
					if (!childValue.contains("true"))
						partialRestricted = true;
					if (childValue.contains("asked"))
						partialAsked = true;
				}

			Bitmap bmRestricted = (holder.restricted ? partialRestricted ? getHalfCheckBox() : getFullCheckBox() : getOffCheckBox());
			Bitmap bmAsked = (holder.asked ? getOffCheckBox() : partialAsked ? getHalfCheckBox() : getOnDemandCheckBox());

			// Indicator state
			holder.imgIndicator.setImageResource(getThemed(isExpanded ? R.attr.icon_expander_maximized : R.attr.icon_expander_minimized));
			holder.imgIndicator.setVisibility(View.VISIBLE);
			holder.imgInfo.setVisibility(View.GONE);
			holder.imgUnsafe.setVisibility(View.GONE);

			// Set data
			holder.tvRestriction.setTypeface(null, Typeface.BOLD);
			holder.tvRestriction.setText(listLocalizedTitle.get(groupPosition));
			holder.imgCbRestrict.setImageBitmap(bmRestricted);
			holder.imgCbAsk.setImageBitmap(bmAsked);
			holder.imgCbAsk.setVisibility(ondemand ? View.VISIBLE : View.GONE);

			holder.imgCbRestrict.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					// Update setting
					holder.restricted = !holder.restricted;
					PrivacyManager.setSetting(userId, getTemplate(), restrictionName, (holder.restricted ? "true" : "false") + "+" + (holder.asked ? "asked" : "ask"));
					notifyDataSetChanged(); // update childs
				}
			});

			holder.imgCbAsk.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					// Update setting
					holder.asked = (!ondemand || !holder.asked);
					PrivacyManager.setSetting(userId, getTemplate(), restrictionName, (holder.restricted ? "true" : "false") + "+" + (holder.asked ? "asked" : "ask"));
					notifyDataSetChanged(); // update childs
				}
			});

			return convertView;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition)
		{
			return PrivacyManager.getHooks((String) getGroup(groupPosition), version).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition)
		{
			return childPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition)
		{
			return PrivacyManager.getHooks((String) getGroup(groupPosition), version).size();
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return false;
		}

		@Override
		@SuppressLint("InflateParams")
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
		{
			final ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.templateentry, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			// Get entry
			final int userId = Util.getUserId(Process.myUid());
			final String restrictionName = (String) getGroup(groupPosition);
			final Hook hook = (Hook) getChild(groupPosition, childPosition);
			final String settingName = restrictionName + "." + hook.getName();

			// Get parent info
			String parentValue = PrivacyManager.getSetting(userId, getTemplate(), restrictionName, Boolean.toString(!ondemand) + "+ask");
			boolean parentRestricted = parentValue.contains("true");
			boolean parentAsked = (!ondemand || parentValue.contains("asked"));

			// Get child info
			String value = PrivacyManager.getSetting(userId, getTemplate(), settingName, null);
			// This is to circumvent caching problems
			// The child value depends on the parent value
			if (value == null)
				value = Boolean.toString(parentRestricted && !hook.isDangerous()) + (parentAsked || (hook.isDangerous() && hook.whitelist() == null) ? "+asked" : "+ask");
			holder.restricted = value.contains("true");
			holder.asked = (!ondemand || value.contains("asked"));
			Bitmap bmRestricted = (parentRestricted && holder.restricted ? getFullCheckBox() : getOffCheckBox());
			Bitmap bmAsked = (parentAsked || holder.asked ? getOffCheckBox() : getOnDemandCheckBox());

			// Set indicator
			holder.imgIndicator.setVisibility(View.INVISIBLE);

			// Function help
			if (hook.getAnnotation() == null)
				holder.imgInfo.setVisibility(View.GONE);
			else {
				holder.imgInfo.setVisibility(View.VISIBLE);
				holder.imgInfo.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						ActivityApp.showHelp(ActivityMain.this, mView, hook);
					}
				});
			}
			holder.imgUnsafe.setVisibility(hook.isUnsafe() ? View.VISIBLE : View.GONE);

			// Set data
			if (hook.isDangerous())
				holder.row.setBackgroundColor(getResources().getColor(getThemed(hook.isDangerousDefined() ? R.attr.color_dangerous : R.attr.color_dangerous_user)));
			else
				holder.row.setBackgroundColor(hook.isDangerousDefined() ? getResources().getColor(getThemed(R.attr.color_dangerous_off)) : Color.TRANSPARENT);
			holder.tvRestriction.setText(hook.getName());
			holder.imgCbRestrict.setEnabled(parentRestricted);
			holder.imgCbRestrict.setImageBitmap(bmRestricted);
			holder.imgCbAsk.setEnabled(!parentAsked);
			holder.imgCbAsk.setImageBitmap(bmAsked);
			holder.imgCbAsk.setVisibility(ondemand ? View.VISIBLE : View.GONE);

			// Listen for long press
			if (Util.getUserId(Process.myUid()) == 0)
				holder.tvRestriction.setOnLongClickListener(new View.OnLongClickListener()
				{
					@Override
					public boolean onLongClick(View view)
					{
						hook.toggleDangerous();

						// Change background color
						if (hook.isDangerous())
							holder.row.setBackgroundColor(getResources().getColor(getThemed(hook.isDangerousDefined() ? R.attr.color_dangerous : R.attr.color_dangerous_user)));
						else
							holder.row.setBackgroundColor(hook.isDangerousDefined() ? getResources().getColor(getThemed(R.attr.color_dangerous_off)) : Color.TRANSPARENT);

						notifyDataSetChanged();

						return true;
					}
				});

			holder.imgCbRestrict.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					// Update setting
					holder.restricted = !holder.restricted;
					PrivacyManager.setSetting(userId, getTemplate(), settingName, (holder.restricted ? "true" : "false") + "+" + (holder.asked ? "asked" : "ask"));
					notifyDataSetChanged(); // update parent
				}
			});

			holder.imgCbAsk.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					// Update setting
					holder.asked = !holder.asked;
					PrivacyManager.setSetting(userId, getTemplate(), settingName, (holder.restricted ? "true" : "false") + "+" + (holder.asked ? "asked" : "ask"));
					notifyDataSetChanged(); // update parent
				}
			});

			return convertView;
		}

		@Override
		public boolean hasStableIds()
		{
			return true;
		}
	}

	@SuppressLint("DefaultLocale")
	private class AppListAdapter extends ArrayAdapter<ApplicationInfoEx>
	{
		private Context mContext;
		private boolean mSelecting = false;
		private List<ApplicationInfoEx> mListAppAll;
		private List<ApplicationInfoEx> mListAppSelected = new ArrayList<ApplicationInfoEx>();
		private String mRestrictionName;
		private Version mVersion;
		private LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		private AtomicInteger mFiltersRunning = new AtomicInteger(0);
		private int mHighlightColor;

		public AppListAdapter(Context context, int resource, List<ApplicationInfoEx> objects, String initialRestrictionName)
		{
			super(context, resource, objects);
			mContext = context;
			mListAppAll = new ArrayList<ApplicationInfoEx>();
			mListAppAll.addAll(objects);
			mRestrictionName = initialRestrictionName;
			mVersion = new Version(Util.getSelfVersionName(context));

			TypedArray ta1 = context.getTheme().obtainStyledAttributes(new int[] { android.R.attr.colorPressedHighlight });
			mHighlightColor = ta1.getColor(0, 0xFF00FF);
			ta1.recycle();
		}

		public void setRestrictionName(String restrictionName)
		{
			mRestrictionName = restrictionName;
		}

		public String getRestrictionName()
		{
			return mRestrictionName;
		}

		public void showStats()
		{
			// TextView tvStats = (TextView) findViewById(R.id.tvStats);
			String stats = String.format("%d/%d", this.getCount(), mListAppAll.size());
			if (mListAppSelected.size() > 0)
				stats += String.format(" (%d)", mListAppSelected.size());
			// tvStats.setText(stats);
		}

		@Override
		public Filter getFilter()
		{
			return new AppFilter();
		}

		private class AppFilter extends Filter
		{
			public AppFilter()
			{
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				int userId = Util.getUserId(Process.myUid());

				int filtersRunning = mFiltersRunning.addAndGet(1);
				FilterResults results = new FilterResults();

				// Get arguments
				String[] components = ((String) constraint).split("\\n");
				String fName = components[0];
				boolean fUsed = Boolean.parseBoolean(components[1]);
				boolean fInternet = Boolean.parseBoolean(components[2]);
				boolean fRestricted = Boolean.parseBoolean(components[3]);
				boolean fRestrictedNot = Boolean.parseBoolean(components[4]);
				boolean fPermission = Boolean.parseBoolean(components[5]);
				boolean fOnDemand = Boolean.parseBoolean(components[6]);
				boolean fOnDemandNot = Boolean.parseBoolean(components[7]);
				boolean fUser = Boolean.parseBoolean(components[8]);
				boolean fSystem = Boolean.parseBoolean(components[9]);

				// Match applications
				int current = 0;
				int max = AppListAdapter.this.mListAppAll.size();
				List<ApplicationInfoEx> lstApp = new ArrayList<ApplicationInfoEx>();
				for (ApplicationInfoEx xAppInfo : AppListAdapter.this.mListAppAll) {
					// Check if another filter has been started
					if (filtersRunning != mFiltersRunning.get())
						return null;

					// Send progress info to main activity
					current++;
					if (current % 5 == 0) {
						final int position = current;
						final int maximum = max;
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								setProgress(getString(R.string.msg_applying), position, maximum);
							}
						});
					}

					// Get if name contains
					boolean contains = false;
					if (!fName.equals(""))
						contains = (xAppInfo.toString().toLowerCase().contains(((String) fName).toLowerCase()));

					// Get if used
					boolean used = false;
					if (fUsed)
						used = (PrivacyManager.getUsage(xAppInfo.getUid(), mRestrictionName, null) != 0);

					// Get if internet
					boolean internet = false;
					if (fInternet)
						internet = xAppInfo.hasInternet(mContext);

					// Get some restricted
					boolean someRestricted = false;
					if (fRestricted)
						for (PRestriction restriction : PrivacyManager.getRestrictionList(xAppInfo.getUid(), mRestrictionName))
							if (restriction.restricted) {
								someRestricted = true;
								break;
							}

					// Get Android permission
					boolean permission = false;
					if (fPermission)
						if (mRestrictionName == null)
							permission = true;
						else if (PrivacyManager.hasPermission(mContext, xAppInfo, mRestrictionName, mVersion) || PrivacyManager.getUsage(xAppInfo.getUid(), mRestrictionName, null) > 0)
							permission = true;

					// Get if onDemand
					boolean onDemand = false;
					boolean isApp = PrivacyManager.isApplication(xAppInfo.getUid());
					boolean odSystem = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingOnDemandSystem, false);
					boolean gondemand = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingOnDemand, true);
					if (fOnDemand && (isApp || odSystem) && gondemand) {
						onDemand = PrivacyManager.getSettingBool(-xAppInfo.getUid(), PrivacyManager.cSettingOnDemand, false);
						if (onDemand && mRestrictionName != null)
							onDemand = !PrivacyManager.getRestrictionEx(xAppInfo.getUid(), mRestrictionName, null).asked;
					}

					// Get if user
					boolean user = false;
					if (fUser)
						user = !xAppInfo.isSystem();

					// Get if system
					boolean system = false;
					if (fSystem)
						system = xAppInfo.isSystem();

					// Apply filters
					if ((fName.equals("") ? true : contains) && (fUsed ? used : true) && (fInternet ? internet : true) && (fRestricted ? (fRestrictedNot ? !someRestricted : someRestricted) : true) && (fPermission ? permission : true) && (fOnDemand ? (fOnDemandNot ? !onDemand : onDemand) : true) && (fUser ? user : true) && (fSystem ? system : true))
						lstApp.add(xAppInfo);
				}

				// Check again whether another filter has been started
				if (filtersRunning != mFiltersRunning.get())
					return null;

				// Apply current sorting
				Collections.sort(lstApp, mSorter);

				// Last check whether another filter has been started
				if (filtersRunning != mFiltersRunning.get())
					return null;

				synchronized (this) {
					results.values = lstApp;
					results.count = lstApp.size();
				}

				return results;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				if (results != null) {
					clear();
					// TextView tvStats = (TextView) findViewById(R.id.tvStats);
					// TextView tvState = (TextView) findViewById(R.id.tvState);
					// ProgressBar pbFilter = (ProgressBar)
					// findViewById(R.id.pbFilter);
					// pbFilter.setVisibility(ProgressBar.GONE);
					// tvStats.setVisibility(TextView.VISIBLE);

					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							setProgress(getString(R.string.title_restrict), 0, 1);
						}
					});

					// Adjust progress state width
					// RelativeLayout.LayoutParams tvStateLayout =
					// (RelativeLayout.LayoutParams) tvState.getLayoutParams();
					// tvStateLayout.addRule(RelativeLayout.LEFT_OF,
					// R.id.tvStats);

					if (results.values == null)
						notifyDataSetInvalidated();
					else {
						addAll((ArrayList<ApplicationInfoEx>) results.values);
						notifyDataSetChanged();
					}
					AppListAdapter.this.showStats();
				}
			}
		}

		public void sort()
		{
			sort(mSorter);
		}

		private class ViewHolder
		{
			private View row;
			private int position;
			public View vwState;
			public LinearLayout llAppType;
			public ImageView imgIcon;
			public ImageView imgSettings;
			public LinearLayout llName;
			public TextView tvName;
			public ImageView imgCbRestricted;
			public ProgressBar pbRunning;
			// public ImageView imgCbAsk;
			public CheckBox mCheckBoxClear;

			public ViewHolder(View theRow, int thePosition)
			{
				row = theRow;
				position = thePosition;
				vwState = (View) row.findViewById(R.id.vwState);
				llAppType = (LinearLayout) row.findViewById(R.id.llAppType);
				imgIcon = (ImageView) row.findViewById(R.id.imgIcon);

				imgSettings = (ImageView) row.findViewById(R.id.imgSettings);
				llName = (LinearLayout) row.findViewById(R.id.llName);
				tvName = (TextView) row.findViewById(R.id.tvName);
				imgCbRestricted = (ImageView) row.findViewById(R.id.imgCbRestricted);
				pbRunning = (ProgressBar) row.findViewById(R.id.pbRunning);
				// imgCbAsk = (ImageView) row.findViewById(R.id.imgCbAsk);
				mCheckBoxClear = (CheckBox) row.findViewById(R.id.imgClear);

			}
		}

		private class HolderTask extends AsyncTask<Object, Object, Object>
		{
			private int position;
			private ViewHolder holder;
			private ApplicationInfoEx xAppInfo = null;
			private int state;
			private boolean used;
			private boolean enabled;
			private boolean settings;
			private RState rstate;
			private boolean gondemand;
			private boolean ondemand;
			private boolean can;
			private boolean methodExpert;

			public HolderTask(int thePosition, ViewHolder theHolder, ApplicationInfoEx theAppInfo)
			{
				position = thePosition;
				holder = theHolder;
				xAppInfo = theAppInfo;
			}

			@Override
			protected Object doInBackground(Object... params)
			{
				if (xAppInfo != null) {
					int userId = Util.getUserId(Process.myUid());

					// Get state
					state = xAppInfo.getState(ActivityMain.this);

					// Get if used
					used = (PrivacyManager.getUsage(xAppInfo.getUid(), mRestrictionName, null) != 0);

					// Get if enabled
					enabled = PrivacyManager.getSettingBool(xAppInfo.getUid(), PrivacyManager.cSettingRestricted, true);

					// Get if on demand
					gondemand = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingOnDemand, true);
					boolean isApp = PrivacyManager.isApplication(xAppInfo.getUid());
					boolean odSystem = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingOnDemandSystem, false);
					ondemand = (isApp || odSystem);
					if (ondemand && mRestrictionName != null)
						ondemand = PrivacyManager.getSettingBool(-xAppInfo.getUid(), PrivacyManager.cSettingOnDemand, false);

					if (mRestrictionName != null)
						if (!PrivacyManager.hasPermission(ActivityMain.this, xAppInfo, mRestrictionName, mVersion)) {
						}

					// Get if application settings
					settings = PrivacyManager.hasSpecificSettings(xAppInfo.getUid());

					// Get restriction/ask state
					rstate = new RState(xAppInfo.getUid(), mRestrictionName, null, mVersion);

					// Get can restrict
					can = PrivacyManager.canRestrict(rstate.mUid, Process.myUid(), rstate.mRestrictionName, rstate.mMethodName, true);
					methodExpert = (mRestrictionName == null || PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingMethodExpert, false));

					return holder;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Object result)
			{
				if (holder.position == position && result != null) {
					String strss = (String) PreferenceUtils.getParam(ActivityMain.this, "xp_clear", xAppInfo.getPackageName().get(0), "");
					if (!strss.equals("")) {
						holder.mCheckBoxClear.setChecked(true);
					} else {
						holder.mCheckBoxClear.setChecked(false);
					}

					// Set background color
					if (xAppInfo.isSystem())
						holder.llAppType.setBackgroundColor(getResources().getColor(getThemed(R.attr.color_dangerous)));
					else
						holder.llAppType.setBackgroundColor(Color.TRANSPARENT);

					// Display state
					if (state == ApplicationInfoEx.STATE_ATTENTION)
						holder.vwState.setBackgroundColor(getResources().getColor(getThemed(R.attr.color_state_attention)));
					else if (state == ApplicationInfoEx.STATE_SHARED)
						holder.vwState.setBackgroundColor(getResources().getColor(getThemed(R.attr.color_state_shared)));
					else
						holder.vwState.setBackgroundColor(getResources().getColor(getThemed(R.attr.color_state_restricted)));

					// Display icon
					holder.imgIcon.setImageDrawable(xAppInfo.getIcon(ActivityMain.this));
					holder.imgIcon.setVisibility(View.VISIBLE);

					// Display on demand
					// if (gondemand)
					// {
					// if (ondemand)
					// {
					// holder.imgCbAsk.setImageBitmap(getAskBoxImage(rstate,
					// methodExpert));
					// holder.imgCbAsk.setVisibility(View.VISIBLE);
					// } else
					// //holder.imgCbAsk.setVisibility(View.INVISIBLE);
					// } else
					// holder.imgCbAsk.setVisibility(View.GONE);

					// Display usage
					holder.tvName.setTypeface(null, used ? Typeface.BOLD_ITALIC : Typeface.NORMAL);

					// Display if settings
					holder.imgSettings.setVisibility(settings ? View.VISIBLE : View.GONE);

					// Display restriction
					holder.imgCbRestricted.setImageBitmap(getCheckBoxImage(rstate, methodExpert));
					holder.imgCbRestricted.setVisibility(View.VISIBLE);

					// Display enabled state
					holder.tvName.setEnabled(enabled && can);
					holder.imgCbRestricted.setEnabled(enabled && can);
					// holder.imgCbAsk.setEnabled(enabled && can);

					// Display selection
					if (mListAppSelected.contains(xAppInfo))
						holder.row.setBackgroundColor(mHighlightColor);
					else
						holder.row.setBackgroundColor(Color.TRANSPARENT);

					// Listen for multiple select
					holder.llName.setOnLongClickListener(new View.OnLongClickListener()
					{
						@Override
						public boolean onLongClick(View view)
						{
							if (mListAppSelected.contains(xAppInfo)) {
								mSelecting = false;
								mListAppSelected.clear();
								mAppAdapter.notifyDataSetChanged();
							} else {
								mSelecting = true;
								mListAppSelected.add(xAppInfo);
								holder.row.setBackgroundColor(mHighlightColor);
							}
							showStats();
							return true;
						}
					});

					holder.mCheckBoxClear.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View arg0)
						{
							// TODO Auto-generated method stub
							if (holder.mCheckBoxClear.isChecked()) {
								PreferenceUtils.setParam(ActivityMain.this, "xp_clear", xAppInfo.getPackageName().get(0), xAppInfo.getPackageName().get(0));
								// PreferenceUtils.setParam(ActivityMain.this,
								// "appid", xAppInfo.getPackageName().get(0),
								// xAppInfo.getUid());

								SharedPreferences prefs = getApplicationContext().getSharedPreferences("ModSettings", Context.MODE_WORLD_READABLE);
								Editor e = prefs.edit();
								e.putBoolean(xAppInfo.getPackageName().get(0) + "/" + "mdatatype", true).commit();
								e.putBoolean(xAppInfo.getPackageName().get(0) + "/" + "updateDisplayInfoLocked", true).commit();
								e.putBoolean(xAppInfo.getPackageName().get(0) + "/" + "timeMachine", true).commit();
								e.putInt(xAppInfo.getPackageName().get(0) + Common.PREF_RECENTS_MODE, 2).commit();

							} else {
								PreferenceUtils.clearKey(ActivityMain.this, "xp_clear", xAppInfo.getPackageName().get(0));
								// PreferenceUtils.clearKey(ActivityMain.this,
								// "appid", xAppInfo.getPackageName().get(0));
								// PrivacyManager.setSetting(0,
								// PrivacyManager.cSettingClearAppid, "-100");

								SharedPreferences prefs = getApplicationContext().getSharedPreferences("ModSettings", Context.MODE_WORLD_READABLE);
								Editor e = prefs.edit();
								e.remove(xAppInfo.getPackageName().get(0) + "/" + "mdatatype");
								e.remove(xAppInfo.getPackageName().get(0) + "/" + "updateDisplayInfoLocked");
								e.remove(xAppInfo.getPackageName().get(0) + "/" + "timeMachine");
								e.remove(xAppInfo.getPackageName().get(0) + Common.PREF_RECENTS_MODE);
								e.commit();
							}

						}
					});
					// Listen for application selection
					holder.llName.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(final View view)
						{
							if (mSelecting) {
								if (mListAppSelected.contains(xAppInfo)) {
									mListAppSelected.remove(xAppInfo);
									holder.row.setBackgroundColor(Color.TRANSPARENT);
									if (mListAppSelected.size() == 0)
										mSelecting = false;
								} else {
									mListAppSelected.add(xAppInfo);
									holder.row.setBackgroundColor(mHighlightColor);
								}
								showStats();
							} else {
								Intent intentSettings = new Intent(ActivityMain.this, ActivityApp.class);
								intentSettings.putExtra(ActivityApp.cUid, xAppInfo.getUid());
								intentSettings.putExtra(ActivityApp.cRestrictionName, mRestrictionName);
								ActivityMain.this.startActivity(intentSettings);
							}
						}
					});

					// Listen for restriction changes
					holder.imgCbRestricted.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							if (mRestrictionName == null && rstate.restricted != false) {
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityMain.this);
								alertDialogBuilder.setTitle(R.string.menu_clear_all);
								alertDialogBuilder.setMessage(R.string.msg_sure);
								alertDialogBuilder.setIcon(getThemed(R.attr.icon_launcher));
								alertDialogBuilder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog, int which)
									{
										deleteRestrictions();
									}
								});
								alertDialogBuilder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog, int which)
									{
									}
								});
								AlertDialog alertDialog = alertDialogBuilder.create();
								alertDialog.show();
							} else
								toggleRestrictions();
						}
					});

				}
			}

			private void deleteRestrictions()
			{
				holder.imgCbRestricted.setVisibility(View.GONE);
				holder.pbRunning.setVisibility(View.VISIBLE);
				new AsyncTask<Object, Object, Object>()
				{
					private List<Boolean> oldState;

					@Override
					protected Object doInBackground(Object... arg0)
					{
						// Update restriction
						oldState = PrivacyManager.getRestartStates(xAppInfo.getUid(), mRestrictionName);
						PrivacyManager.deleteRestrictions(xAppInfo.getUid(), null, true);
						PrivacyManager.setSetting(xAppInfo.getUid(), PrivacyManager.cSettingOnDemand, Boolean.toString(true));
						// PrivacyManager.setSetting(0,
						// PrivacyManager.cSettingClearAppid, null);
						// DBMgr.getInstance(ActivityMain.this).appuid_delete(xAppInfo.getUid()
						// + "");
						return null;
					}

					@Override
					protected void onPostExecute(Object result)
					{
						// Update visible state
						holder.vwState.setBackgroundColor(getResources().getColor(getThemed(R.attr.color_state_attention)));

						// Update stored state
						rstate = new RState(xAppInfo.getUid(), mRestrictionName, null, mVersion);
						holder.imgCbRestricted.setImageBitmap(getCheckBoxImage(rstate, methodExpert));

						// Notify restart
						if (oldState.contains(true))
							Toast.makeText(ActivityMain.this, getString(R.string.msg_restart), Toast.LENGTH_LONG).show();

						// Display new state
						showState();

						holder.pbRunning.setVisibility(View.GONE);
						holder.imgCbRestricted.setVisibility(View.VISIBLE);
					}
				}.executeOnExecutor(mExecutor);
			}

			private void toggleRestrictions()
			{
				holder.imgCbRestricted.setVisibility(View.GONE);
				holder.pbRunning.setVisibility(View.VISIBLE);
				new AsyncTask<Object, Object, Object>()
				{
					private List<Boolean> oldState;
					private List<Boolean> newState;

					@Override
					protected Object doInBackground(Object... arg0)
					{
						// Change restriction
						oldState = PrivacyManager.getRestartStates(xAppInfo.getUid(), mRestrictionName);
						rstate.toggleRestriction();
						newState = PrivacyManager.getRestartStates(xAppInfo.getUid(), mRestrictionName);
						// PrivacyManager.setSetting(0,
						// PrivacyManager.cSettingClearAppid, xAppInfo.getUid()
						// + "");
						// ContentValues mValues = new ContentValues();
						// mValues.put("t_appid", xAppInfo.getUid() + "");
						// DBMgr.getInstance(ActivityMain.this).appuid_insert(mValues);
						return null;
					}

					@Override
					protected void onPostExecute(Object result)
					{
						// Update restriction display
						rstate = new RState(xAppInfo.getUid(), mRestrictionName, null, mVersion);
						holder.imgCbRestricted.setImageBitmap(getCheckBoxImage(rstate, methodExpert));

						// Notify restart
						if (!newState.equals(oldState))
							Toast.makeText(ActivityMain.this, getString(R.string.msg_restart), Toast.LENGTH_LONG).show();

						// Display new state
						showState();

						holder.pbRunning.setVisibility(View.GONE);
						holder.imgCbRestricted.setVisibility(View.VISIBLE);
					}
				}.executeOnExecutor(mExecutor);
			}

			private void showState()
			{
				state = xAppInfo.getState(ActivityMain.this);
				if (state == ApplicationInfoEx.STATE_ATTENTION)
					holder.vwState.setBackgroundColor(getResources().getColor(getThemed(R.attr.color_state_attention)));
				else if (state == ApplicationInfoEx.STATE_SHARED)
					holder.vwState.setBackgroundColor(getResources().getColor(getThemed(R.attr.color_state_shared)));
				else
					holder.vwState.setBackgroundColor(getResources().getColor(getThemed(R.attr.color_state_restricted)));
			}
		}

		@Override
		@SuppressLint("InflateParams")
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.mainentry, null);
				holder = new ViewHolder(convertView, position);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
				holder.position = position;
			}

			// Get info
			final ApplicationInfoEx xAppInfo = getItem(holder.position);

			// Handle details click
			holder.imgIcon.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					Intent intentSettings = new Intent(ActivityMain.this, ActivityApp.class);
					intentSettings.putExtra(ActivityApp.cUid, xAppInfo.getUid());
					intentSettings.putExtra(ActivityApp.cRestrictionName, mRestrictionName);
					ActivityMain.this.startActivity(intentSettings);
				}
			});

			// Set data
			holder.row.setBackgroundColor(Color.TRANSPARENT);
			holder.vwState.setBackgroundColor(Color.TRANSPARENT);
			holder.llAppType.setBackgroundColor(Color.TRANSPARENT);
			holder.imgIcon.setVisibility(View.INVISIBLE);
			// holder.tvName.setText(xAppInfo.getApplicationName().get(0).);
			holder.tvName.setText(xAppInfo.toString());
			holder.tvName.setTypeface(null, Typeface.NORMAL);
			holder.imgSettings.setVisibility(View.GONE);
			holder.imgCbRestricted.setVisibility(View.INVISIBLE);
			holder.tvName.setEnabled(false);
			holder.imgCbRestricted.setEnabled(false);

			// Async update
			new HolderTask(position, holder, xAppInfo).executeOnExecutor(mExecutor, (Object) null);

			return convertView;
		}
	}

	// Helper methods

	private void setProgress(String text, int progress, int max)
	{
		// Set up the progress bar
		if (mProgressWidth == 0) {
			// final View vProgressEmpty = (View)
			// findViewById(R.id.vProgressEmpty);
			// mProgressWidth = vProgressEmpty.getMeasuredWidth();
		}
		// Display stuff
		// TextView tvState = (TextView) findViewById(R.id.tvState);
		// if (text != null)
		// tvState.setText(text);
		if (max == 0)
			max = 1;
		mProgress = (int) ((float) mProgressWidth) * progress / max;

		// View vProgressFull = (View) findViewById(R.id.vProgressFull);
		// vProgressFull.getLayoutParams().width = mProgress;
	}

	private int getSelectedCategory(final int userId)
	{
		int pos = 0;
		String restrictionName = PrivacyManager.getSetting(userId, PrivacyManager.cSettingSelectedCategory, null);
		if (restrictionName != null)
			for (String restriction : PrivacyManager.getRestrictions(this).values()) {
				pos++;
				if (restrictionName.equals(restriction))
					break;
			}
		return pos;
	}

}
