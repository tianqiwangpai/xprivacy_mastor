package com.hy.xp.app;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("Wakelock")
public class ActivityShare extends ActivityBase
{
	private int mActionId;
	private AppListAdapter mAppAdapter;
	private SparseArray<AppState> mAppsByUid;
	private boolean mRunning = false;
	private boolean mAbort = false;
	private int mProgressCurrent;
	private int mProgressWidth = 0;
	private String mFileName = null;
	private boolean mInteractive = false;

	private static final int STATE_WAITING = 0;
	private static final int STATE_RUNNING = 1;
	private static final int STATE_SUCCESS = 2;
	private static final int STATE_FAILURE = 3;

	private static final int ACTIVITY_IMPORT_SELECT = 0;

	public static final String cUidList = "UidList";
	public static final String cRestriction = "Restriction";
	public static final String cInteractive = "Interactive";
	public static final String cChoice = "Choice";
	public static final String cFileName = "FileName";
	public static final String HTTP_BASE_URL = "";
	public static final String HTTPS_BASE_URL = "";

	public static final int cSubmitLimit = 10;
	public static final int cProtocolVersion = 4;

	public static final String ACTION_EXPORT = "com.hy.xp.app.action.EXPORT";
	public static final String ACTION_IMPORT = "com.hy.xp.app.action.IMPORT";
	public static final String ACTION_FETCH = "com.hy.xp.app.action.FETCH";
	public static final String ACTION_SUBMIT = "com.hy.xp.app.action.SUBMIT";
	public static final String ACTION_TOGGLE = "com.hy.xp.app.action.TOGGLE";

	public static final int CHOICE_CLEAR = 1;
	public static final int CHOICE_TEMPLATE = 2;

	public static final int TIMEOUT_MILLISEC = 45000;

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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Check privacy service client
		if (!PrivacyService.checkClient())
			return;

		// Get data
		int userId = Util.getUserId(Process.myUid());
		final Bundle extras = getIntent().getExtras();
		final String action = getIntent().getAction();
		final int[] uids = (extras != null && extras.containsKey(cUidList) ? extras.getIntArray(cUidList) : new int[0]);
		final String restrictionName = (extras != null ? extras.getString(cRestriction) : null);
		int choice = (extras != null && extras.containsKey(cChoice) ? extras.getInt(cChoice) : -1);
		if (action.equals(ACTION_EXPORT))
			mFileName = (extras != null && extras.containsKey(cFileName) ? extras.getString(cFileName) : null);

		// Registration check
		if (action.equals(ACTION_SUBMIT))
		{
			finish();
			return;
		}

		// Check whether we need a user interface
		if (extras != null && extras.containsKey(cInteractive) && extras.getBoolean(cInteractive, false))
			mInteractive = true;

		// Set layout
		setContentView(R.layout.sharelist);

		// Reference controls
		final TextView tvDescription = (TextView) findViewById(R.id.tvDescription);
		final ScrollView svToggle = (ScrollView) findViewById(R.id.svToggle);
		final RadioGroup rgToggle = (RadioGroup) findViewById(R.id.rgToggle);
		final Spinner spRestriction = (Spinner) findViewById(R.id.spRestriction);
		RadioButton rbClear = (RadioButton) findViewById(R.id.rbClear);
		RadioButton rbTemplateFull = (RadioButton) findViewById(R.id.rbTemplateFull);
		RadioButton rbODEnable = (RadioButton) findViewById(R.id.rbEnableOndemand);
		RadioButton rbODDisable = (RadioButton) findViewById(R.id.rbDisableOndemand);
		final Spinner spTemplate = (Spinner) findViewById(R.id.spTemplate);
		final CheckBox cbClear = (CheckBox) findViewById(R.id.cbClear);
		final Button btnOk = (Button) findViewById(R.id.btnOk);
		final Button btnCancel = (Button) findViewById(R.id.btnCancel);

		// Set title
		if (action.equals(ACTION_TOGGLE))
		{
			mActionId = R.string.menu_toggle;
			setTitle(R.string.menu_toggle);
		} else
		{
			finish();
			return;
		}

		// Get localized restriction name
		List<String> listRestrictionName = new ArrayList<String>(PrivacyManager.getRestrictions(this).navigableKeySet());
		listRestrictionName.add(0, getString(R.string.menu_all));

		// Build restriction adapter
		SpinnerAdapter saRestriction = new SpinnerAdapter(this, android.R.layout.simple_spinner_item);
		saRestriction.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		saRestriction.addAll(listRestrictionName);

		// Setup restriction spinner
		int pos = 0;
		if (restrictionName != null)
			for (String restriction : PrivacyManager.getRestrictions(this).values())
			{
				pos++;
				if (restrictionName.equals(restriction))
					break;
			}

		spRestriction.setAdapter(saRestriction);
		spRestriction.setSelection(pos);

		// Build template adapter
		SpinnerAdapter spAdapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item);
		spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		String defaultName = PrivacyManager.getSetting(userId, Meta.cTypeTemplateName, "0", getString(R.string.title_default));
		spAdapter.add(defaultName);
		for (int i = 1; i <= 4; i++)
		{
			String alternateName = PrivacyManager.getSetting(userId, Meta.cTypeTemplateName, Integer.toString(i), getString(R.string.title_alternate) + " " + i);
			spAdapter.add(alternateName);
		}
		spTemplate.setAdapter(spAdapter);

		// Build application list
		AppListTask appListTask = new AppListTask();
		appListTask.executeOnExecutor(mExecutor, uids);

		// Import/export filename
		if (action.equals(ACTION_EXPORT) || action.equals(ACTION_IMPORT))
		{
			// Check for availability of sharing intent
			Intent file = new Intent(Intent.ACTION_GET_CONTENT);
			file.setType("file/*");
			boolean hasIntent = Util.isIntentAvailable(ActivityShare.this, file);

			// Get file name
			if (mFileName == null)
				if (action.equals(ACTION_EXPORT))
				{
					String packageName = null;
					if (uids.length == 1)
						try
						{
							ApplicationInfoEx appInfo = new ApplicationInfoEx(this, uids[0]);
							packageName = appInfo.getPackageName().get(0);
						} catch (Throwable ex)
						{
							Util.bug(null, ex);
						}
					mFileName = getFileName(this, hasIntent, packageName);
				} else
					mFileName = (hasIntent ? null : getFileName(this, false, null));

			if (mFileName == null)
				fileChooser();
			else
				showFileName();

			if (action.equals(ACTION_IMPORT))
				cbClear.setVisibility(View.VISIBLE);

		} else if (action.equals(ACTION_FETCH))
		{
			tvDescription.setText(getBaseURL());
			cbClear.setVisibility(View.VISIBLE);

		} else if (action.equals(ACTION_TOGGLE))
		{
			tvDescription.setText(R.string.menu_toggle);
			spRestriction.setVisibility(View.VISIBLE);
			svToggle.setVisibility(View.VISIBLE);

			// Listen for radio button
			rgToggle.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId)
				{
					btnOk.setEnabled(checkedId >= 0);
					spRestriction.setVisibility(checkedId == R.id.rbEnableOndemand || checkedId == R.id.rbDisableOndemand ? View.GONE : View.VISIBLE);

					spTemplate
							.setVisibility(checkedId == R.id.rbTemplateCategory || checkedId == R.id.rbTemplateFull || checkedId == R.id.rbTemplateMergeSet || checkedId == R.id.rbTemplateMergeReset ? View.VISIBLE
									: View.GONE);
				}
			});

			boolean ondemand = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingOnDemand, true);
			rbODEnable.setVisibility(ondemand ? View.VISIBLE : View.GONE);
			rbODDisable.setVisibility(ondemand ? View.VISIBLE : View.GONE);

			if (choice == CHOICE_CLEAR)
				rbClear.setChecked(true);
			else if (choice == CHOICE_TEMPLATE)
				rbTemplateFull.setChecked(true);

		} else
			tvDescription.setText(getBaseURL());

		if (mInteractive)
		{
			// Enable ok
			// (showFileName does this for export/import)
			if (action.equals(ACTION_SUBMIT) || action.equals(ACTION_FETCH))
				btnOk.setEnabled(true);

			// Listen for ok
			btnOk.setOnClickListener(new Button.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					btnOk.setEnabled(false);

					// Toggle
					if (action.equals(ACTION_TOGGLE))
					{
						mRunning = true;
						for (int i = 0; i < rgToggle.getChildCount(); i++)
							((RadioButton) rgToggle.getChildAt(i)).setEnabled(false);
						int pos = spRestriction.getSelectedItemPosition();
						String restrictionName = (pos == 0 ? null : (String) PrivacyManager.getRestrictions(ActivityShare.this).values().toArray()[pos - 1]);
						new ToggleTask().executeOnExecutor(mExecutor, restrictionName);

					}

				}
			});

		} else
			btnOk.setEnabled(false);

		// Listen for cancel
		btnCancel.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mRunning)
				{
					mAbort = true;
					Toast.makeText(ActivityShare.this, getString(R.string.msg_abort), Toast.LENGTH_LONG).show();
				} else
					finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent)
	{
		super.onActivityResult(requestCode, resultCode, dataIntent);

		// Import select
		if (requestCode == ACTIVITY_IMPORT_SELECT)
			if (resultCode == RESULT_CANCELED || dataIntent == null)
				finish();
			else
			{
				String fileName = dataIntent.getData().getPath();
				mFileName = fileName.replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar);
				showFileName();
			}
	}

	// State management

	public void setState(int uid, int state, String message)
	{
		final AppState app = mAppsByUid.get(uid);
		app.message = message;
		app.state = state;
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (mAppAdapter != null)
				{
					mAppAdapter.notifyDataSetChanged();

					int position = mAppAdapter.getPosition(app);
					if (position >= 0)
					{
						ListView lvShare = (ListView) findViewById(R.id.lvShare);
						lvShare.smoothScrollToPosition(position);
					}
				}
			}
		});
	}

	public void setState(int uid, int state)
	{
		AppState app = mAppsByUid.get(uid);
		app.state = state;
	}

	public void setMessage(int uid, String message)
	{
		AppState app = mAppsByUid.get(uid);
		app.message = message;
	}

	// App info and share state

	private class AppState implements Comparable<AppState>
	{
		public int state = STATE_WAITING;
		public String message = null;
		public ApplicationInfoEx appInfo;

		public AppState(int uid)
		{
			appInfo = new ApplicationInfoEx(ActivityShare.this, uid);
		}

		@Override
		public int compareTo(AppState other)
		{
			return this.appInfo.compareTo(other.appInfo);
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

	private class AppListAdapter extends ArrayAdapter<AppState>
	{
		private LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		public AppListAdapter(Context context, int resource, List<AppState> objects)
		{
			super(context, resource, objects);
		}

		public List<Integer> getListUid()
		{
			List<Integer> uids = new ArrayList<Integer>();
			for (int i = 0; i < this.getCount(); i++)
				uids.add(this.getItem(i).appInfo.getUid());
			return uids;
		}

		private class ViewHolder
		{
			private View row;
			private int position;
			public ImageView imgIcon;
			public ImageView imgInfo;
			public TextView tvName;
			public ImageView imgResult;
			public ProgressBar pbRunning;
			public TextView tvMessage;

			public ViewHolder(View theRow, int thePosition)
			{
				row = theRow;
				position = thePosition;
				imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
				imgInfo = (ImageView) row.findViewById(R.id.imgInfo);
				tvName = (TextView) row.findViewById(R.id.tvApp);
				imgResult = (ImageView) row.findViewById(R.id.imgResult);
				pbRunning = (ProgressBar) row.findViewById(R.id.pbRunning);
				tvMessage = (TextView) row.findViewById(R.id.tvMessage);
			}
		}

		@Override
		@SuppressLint("InflateParams")
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			if (convertView == null)
			{
				convertView = mInflater.inflate(R.layout.shareentry, null);
				holder = new ViewHolder(convertView, position);
				convertView.setTag(holder);
			} else
			{
				holder = (ViewHolder) convertView.getTag();
				holder.position = position;
			}

			// Get info
			final AppState xApp = getItem(holder.position);

			// Set background color
			if (xApp.appInfo.isSystem())
				holder.row.setBackgroundColor(getResources().getColor(getThemed(R.attr.color_dangerous)));
			else
				holder.row.setBackgroundColor(Color.TRANSPARENT);

			// Display icon
			holder.imgIcon.setImageDrawable(xApp.appInfo.getIcon(ActivityShare.this));
			holder.imgIcon.setVisibility(View.VISIBLE);

			holder.imgInfo.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					// Packages can be selected on the web site
					Util.viewUri(ActivityShare.this, Uri.parse(String.format(getBaseURL() + "?package_name=%s", xApp.appInfo.getPackageName().get(0))));
				}
			});

			// Set app name
			holder.tvName.setText(xApp.appInfo.toString());

			// Show app share state
			if (TextUtils.isEmpty(xApp.message))
				holder.tvMessage.setVisibility(View.GONE);
			else
			{
				holder.tvMessage.setVisibility(View.VISIBLE);
				holder.tvMessage.setText(xApp.message);
			}
			switch (xApp.state)
			{
			case STATE_WAITING:
				holder.imgResult.setVisibility(View.GONE);
				holder.pbRunning.setVisibility(View.GONE);
				break;
			case STATE_RUNNING:
				holder.imgResult.setVisibility(View.GONE);
				holder.pbRunning.setVisibility(View.VISIBLE);
				break;
			case STATE_SUCCESS:
				holder.imgResult.setBackgroundResource(R.drawable.btn_check_buttonless_on);
				holder.imgResult.setVisibility(View.VISIBLE);
				holder.pbRunning.setVisibility(View.GONE);
				break;
			case STATE_FAILURE:
				holder.imgResult.setBackgroundResource(R.drawable.indicator_input_error);
				holder.imgResult.setVisibility(View.VISIBLE);
				holder.pbRunning.setVisibility(View.GONE);
				break;
			default:
				Util.log(null, Log.ERROR, "Unknown state=" + xApp.state);
				break;
			}

			return convertView;
		}
	}

	// Tasks

	private class AppListTask extends AsyncTask<int[], Object, List<AppState>>
	{
		private ProgressDialog mProgressDialog;

		@Override
		protected List<AppState> doInBackground(int[]... params)
		{
			int[] uids = params[0];
			List<AppState> apps = new ArrayList<AppState>();
			mAppsByUid = new SparseArray<AppState>();

			if (!mInteractive)
			{
				// Build list of distinct uids for export
				List<Integer> listUid = new ArrayList<Integer>();
				for (PackageInfo pInfo : getPackageManager().getInstalledPackages(0))
					if (!listUid.contains(pInfo.applicationInfo.uid))
						listUid.add(pInfo.applicationInfo.uid);

				// Convert to primitive array
				uids = new int[listUid.size()];
				for (int i = 0; i < listUid.size(); i++)
					uids[i] = listUid.get(i);
			}

			mProgressDialog.setMax(uids.length);
			for (int i = 0; i < uids.length; i++)
			{
				mProgressDialog.setProgress(i);
				AppState app = new AppState(uids[i]);
				apps.add(app);
				mAppsByUid.put(uids[i], app);
			}

			Collections.sort(apps);
			return apps;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			// Show progress dialog
			mProgressDialog = new ProgressDialog(ActivityShare.this);
			mProgressDialog.setMessage(getString(R.string.msg_loading));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setProgressNumberFormat(null);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected void onPostExecute(List<AppState> listApp)
		{
			if (!ActivityShare.this.isFinishing())
			{
				// Display app list
				mAppAdapter = new AppListAdapter(ActivityShare.this, R.layout.shareentry, listApp);
				ListView lvShare = (ListView) findViewById(R.id.lvShare);
				lvShare.setAdapter(mAppAdapter);

				// Dismiss progress dialog
				if (mProgressDialog.isShowing())
					try
					{
						mProgressDialog.dismiss();
					} catch (IllegalArgumentException ignored)
					{
					}

			}

			super.onPostExecute(listApp);
		}
	}

	private class ToggleTask extends AsyncTask<String, Integer, Throwable>
	{
		@Override
		protected Throwable doInBackground(String... params)
		{
			// Get wakelock
			PowerManager pm = (PowerManager) ActivityShare.this.getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "XPrivacy.Toggle");
			wl.acquire();
			try
			{
				// Get data
				mProgressCurrent = 0;
				List<Integer> lstUid = mAppAdapter.getListUid();
				final String restrictionName = params[0];
				int actionId = ((RadioGroup) ActivityShare.this.findViewById(R.id.rgToggle)).getCheckedRadioButtonId();
				Spinner spTemplate = ((Spinner) ActivityShare.this.findViewById(R.id.spTemplate));
				String templateName = Meta.cTypeTemplate;
				if (spTemplate.getSelectedItemPosition() > 0)
					templateName = Meta.cTypeTemplate + spTemplate.getSelectedItemPosition();

				for (Integer uid : lstUid)
					try
					{
						if (mAbort)
							throw new AbortException(ActivityShare.this);

						// Update progess
						publishProgress(++mProgressCurrent, lstUid.size() + 1);
						setState(uid, STATE_RUNNING, null);

						List<Boolean> oldState = PrivacyManager.getRestartStates(uid, restrictionName);

						if (actionId == R.id.rbClear)
						{
							PrivacyManager.deleteRestrictions(uid, restrictionName, (restrictionName == null));
							if (restrictionName == null)
							{
								PrivacyManager.deleteUsage(uid);
								PrivacyManager.deleteSettings(uid);
							}
						}

						else if (actionId == R.id.rbRestrict)
						{
							PrivacyManager.setRestriction(uid, restrictionName, null, true, false);
							PrivacyManager.updateState(uid);
						}

						else if (actionId == R.id.rbTemplateCategory)
							PrivacyManager.applyTemplate(uid, templateName, restrictionName, false, true, false);

						else if (actionId == R.id.rbTemplateFull)
							PrivacyManager.applyTemplate(uid, templateName, restrictionName, true, true, false);

						else if (actionId == R.id.rbTemplateMergeSet)
							PrivacyManager.applyTemplate(uid, templateName, restrictionName, true, false, false);

						else if (actionId == R.id.rbTemplateMergeReset)
							PrivacyManager.applyTemplate(uid, templateName, restrictionName, true, false, true);

						else if (actionId == R.id.rbEnableOndemand)
						{
							PrivacyManager.setSetting(uid, PrivacyManager.cSettingOnDemand, Boolean.toString(true));
							PrivacyManager.setSetting(uid, PrivacyManager.cSettingNotify, Boolean.toString(false));

						} else if (actionId == R.id.rbDisableOndemand)
						{
							PrivacyManager.setSetting(uid, PrivacyManager.cSettingOnDemand, Boolean.toString(false));
							PrivacyManager.setSetting(uid, PrivacyManager.cSettingNotify, Boolean.toString(true));

						} else
							Util.log(null, Log.ERROR, "Unknown action=" + actionId);

						List<Boolean> newState = PrivacyManager.getRestartStates(uid, restrictionName);

						setState(uid, STATE_SUCCESS, !newState.equals(oldState) ? getString(R.string.msg_restart) : null);
					} catch (Throwable ex)
					{
						setState(uid, STATE_FAILURE, ex.getMessage());
						return ex;
					}
			} finally
			{
				wl.release();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			blueStreakOfProgress(values[0], values[1]);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Throwable result)
		{
			if (!ActivityShare.this.isFinishing())
				done(result);
			super.onPostExecute(result);
		}
	}

	// Helper methods

	private void blueStreakOfProgress(Integer current, Integer max)
	{
		// Set up the progress bar
		if (mProgressWidth == 0)
		{
			final View vShareProgressEmpty = (View) findViewById(R.id.vShareProgressEmpty);
			mProgressWidth = vShareProgressEmpty.getMeasuredWidth();
		}
		// Display stuff
		if (max == 0)
			max = 1;
		int width = (int) ((float) mProgressWidth) * current / max;

		View vShareProgressFull = (View) findViewById(R.id.vShareProgressFull);
		vShareProgressFull.getLayoutParams().width = width;
		vShareProgressFull.invalidate();
		vShareProgressFull.requestLayout();
	}

	private void done(Throwable ex)
	{
		String result = null;
		if (ex != null && !(ex instanceof AbortException))
			result = ex.getMessage();

		// Check result string and display toast with error
		if (result != null)
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();

		// Reset progress bar
		blueStreakOfProgress(0, 1);
		mRunning = false;

		// Update buttons
		final Button btnCancel = (Button) findViewById(R.id.btnCancel);
		final Button btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel.setEnabled(false);
		btnOk.setEnabled(true);

		// Handle close
		btnOk.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	public void fileChooser()
	{
		Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
		Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/.xprivacy/");
		chooseFile.setDataAndType(uri, "text/xml");
		Intent intent = Intent.createChooser(chooseFile, getString(R.string.app_name));
		startActivityForResult(intent, ACTIVITY_IMPORT_SELECT);
	}

	private void showFileName()
	{
		TextView tvDescription = (TextView) findViewById(R.id.tvDescription);
		tvDescription.setText(mFileName);
		Button btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setEnabled(true);
	}

	public static String getBaseURL()
	{
		int userId = Util.getUserId(Process.myUid());
		if (PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingHttps, true))
			return HTTPS_BASE_URL;
		else
			return HTTP_BASE_URL;
	}

	public static String getFileName(Context context, boolean multiple, String packageName)
	{
		File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".xprivacy");
		folder.mkdir();
		String fileName;
		if (multiple)
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT);
			fileName = String.format("%s_XPrivacy_%s_%s%s.xml", format.format(new Date()), Util.getSelfVersionName(context), Build.DEVICE, (packageName == null ? "" : "_" + packageName));
		} else
			fileName = "XPrivacy.xml";
		return new File(folder + File.separator + fileName).getAbsolutePath();
	}

	// Helper classes

	public static class AbortException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public AbortException(Context context)
		{
			super(context.getString(R.string.msg_aborted));
		}
	}

	public static class ServerException extends Exception
	{
		private int mErrorNo;
		private Context mContext = null;
		private static final long serialVersionUID = 1L;

		public final static int cErrorNotActivated = 206;
		public final static int cErrorNoRestrictions = 305;

		public ServerException(int errorno, String message)
		{
			super(message);
			mErrorNo = errorno;
		}

		public ServerException(Context context, int errorno, String message)
		{
			super(message);
			mErrorNo = errorno;
			mContext = context;
		}

		@Override
		@SuppressLint("DefaultLocale")
		public String getMessage()
		{
			if (mErrorNo == cErrorNoRestrictions && mContext != null)
				return mContext.getString(R.string.msg_no_restrictions);
			return String.format("Error %d: %s", mErrorNo, super.getMessage());
			// general:
			// 'errno' => 101, 'error' => 'Empty request'
			// 'errno' => 102, 'error' => 'Please upgrade to at least ...'
			// 'errno' => 103, 'error' => 'Error connecting to database'
			// 'errno' => 104, 'error' => 'Unknown action: ...'

			// submit:
			// 'errno' => 201, 'error' => 'Not authorized'
			// 'errno' => 202, 'error' => 'Android ID missing'
			// 'errno' => 203, 'error' => 'Package name missing'
			// 'errno' => 204, 'error' => 'Too many packages for application'
			// 'errno' => 205, 'error' => 'Error storing restrictions'
			// 'errno' => 206, 'error' => 'Device not activated'

			// fetch:
			// 'errno' => 301, 'error' => 'Not authorized'
			// 'errno' => 303, 'error' => 'Package name missing'
			// 'errno' => 304, 'error' => 'Too many packages for application'
			// 'errno' => 305, 'error' => 'No restrictions available'
			// 'errno' => 306, 'error' => 'Error retrieving restrictions'
			// 'errno' => 307, 'error' => 'There is a maximum of ...'
		}
	}
}
