package com.hy.xp.app.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hy.xp.app.ActivityBase;
import com.hy.xp.app.PrivacyService;
import com.hy.xp.app.R;
import com.hy.xp.app.Util;

/**
 * 数据任务主页
 * 
 * @author Administrator
 */
public class TaskListActivity extends ActivityBase
{
	static String spinnerdata = "";
	private Spinner spListTask;
	private ListView mListTaskCurrFile;
	ArrayAdapter<String> mSpListTaskAdapter = null;
	List<TaskAttribute> mTaskDescs = null;
	private int mSortMode = 1;
	private static final int CONFIG_BY_IMPORT = 1;
	private static final int CONFIG_BY_EXPORT = 0;
	int d;
	List<TaskCurlFile> mCurlFiles;
	ArrayAdapter<String> mCurlFileAdapter;

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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_list_task);
		spListTask = (Spinner) findViewById(R.id.sp_task);
		mListTaskCurrFile = (ListView) findViewById(R.id.list_task_curr_file_item);
		// 数据适配
		mSpListTaskAdapter = new ArrayAdapter<String>(TaskListActivity.this, android.R.layout.simple_spinner_item);
		mSpListTaskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mTaskDescs = DBMgr.getInstance(TaskListActivity.this).getTaskList();
		for (TaskAttribute mDesc : mTaskDescs)
			mSpListTaskAdapter.add(mDesc.getTaskName());
		int p = (Integer) PreferenceUtils.getParam(TaskListActivity.this, "position", 0);
		// System.out.println(p);
		spListTask.setAdapter(mSpListTaskAdapter);
		spListTask.setSelection(p, true);

		spListTask.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				// TODO Auto-generated method stub
				spinnerdata = (String) parent.getItemAtPosition(position);
				mCurlFiles = DBMgr.getInstance(TaskListActivity.this).getCurrFileByTaskName(spinnerdata);
				d = position;
				PreferenceUtils.setParam(TaskListActivity.this, "position", d);
				mCurlFileAdapter = new ArrayAdapter<String>(TaskListActivity.this, android.R.layout.simple_list_item_activated_1);
				mCurlFileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				for (TaskCurlFile mCurlFile : mCurlFiles)
					mCurlFileAdapter.add(mCurlFile.getTask_curl_file());
				mListTaskCurrFile.setAdapter(mCurlFileAdapter);

				mListTaskCurrFile.setOnItemClickListener(new OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						// TODO Auto-generated method stub
						LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View mView = inflater.inflate(R.layout.read_file_curl_setting, null);
						final RadioGroup rgSMode = (RadioGroup) mView.findViewById(R.id.rg_read_file_mode);
						switch (mSortMode) {
						case CONFIG_BY_EXPORT:
							rgSMode.check(R.id.rb_read_file_mode_shunxu);
							break;
						case CONFIG_BY_IMPORT:
							rgSMode.check(R.id.rb_read_file_mode_radmoe);
							break;
						}
						final String filename = mCurlFiles.get(position).getTask_curl_file();
						final String taskname = mCurlFiles.get(position).getTask_name();
						final TextView mTextView = (TextView) mView.findViewById(R.id.read_file_curl_setting_tv);
						AlertDialog.Builder mBuilder = new AlertDialog.Builder(TaskListActivity.this);

						mTextView.setText("读取的文件为 :" + filename);
						mBuilder.setTitle("文件设置");
						mBuilder.setIcon(R.drawable.ic_launcher);
						mBuilder.setView(mView);
						mBuilder.setPositiveButton(TaskListActivity.this.getString(android.R.string.ok), new OnClickListener()
						{

							@SuppressLint("WorldReadableFiles")
							@Override
							public void onClick(DialogInterface dialog, int which)
							{

								switch (rgSMode.getCheckedRadioButtonId()) {

								case R.id.rb_read_file_mode_radmoe:
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "curl_file_name", filename);
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "curl_file_count", 1);
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "curl_file_read_mode", "随机读取");
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "taskname", taskname);
									PreferenceUtils.setParam(TaskListActivity.this, "taskname", taskname);
									Toast.makeText(TaskListActivity.this, "操作成功", 1).show();

									break;
								case R.id.rb_read_file_mode_shunxu:
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "curl_file_name", filename);
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "curl_file_count", 1);
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "curl_file_read_mode", "顺序读取");
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "taskname", taskname);
									PreferenceUtils.setParam(TaskListActivity.this, "taskname", taskname);
									Toast.makeText(TaskListActivity.this, "操作成功", 1).show();
									break;
								default:
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "curl_file_name", filename);
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "curl_file_count", 1);
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "curl_file_read_mode", "顺序读取");
									PreferenceUtils.setParam(TaskListActivity.this, taskname, "taskname", taskname);
									PreferenceUtils.setParam(TaskListActivity.this, "taskname", taskname);
									Toast.makeText(TaskListActivity.this, "操作成功", 1).show();
								}

								// 清空随机时设置
								SharedPreferences prefs = TaskListActivity.this.getSharedPreferences("suji", Context.MODE_WORLD_READABLE);
								Editor editor = prefs.edit();
								editor.clear();
								editor.commit();
								// 开始准备数据
								String state = Environment.getExternalStorageState();
								String path = null;
								if (Environment.MEDIA_MOUNTED.equals(state)) {
									File sdcardDir = Environment.getExternalStorageDirectory();
									path = sdcardDir.getPath() + "/xp_datafile/" + taskname;
								}
								try {
									List<PhoneDataBean> mBases = readcurray(path, filename);
									if (mBases != null) {
										DBMgr.getInstance(TaskListActivity.this).plInsert(mBases);
										PreferenceUtils.setParam(TaskListActivity.this, taskname, "cSettingReadFileSum", mBases.size());
										Toast.makeText(TaskListActivity.this, "操作成功！", 1).show();
									} else {
										Toast.makeText(TaskListActivity.this, "操作失败，该文件不存在！", 1).show();
									}

								} catch (Exception e) {
									// TODO Auto-generated catch
									// block
									e.printStackTrace();
									Toast.makeText(TaskListActivity.this, e.getMessage(), 1).show();
								}

							}
						});
						mBuilder.setNegativeButton(TaskListActivity.this.getString(android.R.string.cancel), new OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								// TODO Auto-generated method
								// stub

							}
						});

						AlertDialog mAlertDialog = mBuilder.create();
						mAlertDialog.show();

					}
				});

				mListTaskCurrFile.setOnItemLongClickListener(new OnItemLongClickListener()
				{

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
					{
						// TODO Auto-generated method stub
						final String filename = mCurlFiles.get(arg2).getTask_curl_file();
						final String taskname = mCurlFiles.get(arg2).getTask_name();
						final String pefTaskName = PreferenceUtils.getParam(TaskListActivity.this, taskname, "taskname", "").toString();
						// System.out.println(pefTaskName);
						AlertDialog.Builder mBuilder = new AlertDialog.Builder(TaskListActivity.this);
						mBuilder.setTitle("删除提醒");
						mBuilder.setIcon(R.drawable.ic_launcher);
						mBuilder.setMessage("确定删除" + filename + "文件？");
						mBuilder.setNegativeButton(getString(android.R.string.ok), new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								// TODO Auto-generated method
								// stub
								// 删除数据库记录
								DBMgr.getInstance(TaskListActivity.this).deleteCurlFileByCurlFile(filename);
								Toast.makeText(TaskListActivity.this, "删除成功，请重新配置读取文件", 1).show();
								// 删除文件
								String state = Environment.getExternalStorageState();
								String path = null;
								if (Environment.MEDIA_MOUNTED.equals(state)) {
									File sdcardDir = Environment.getExternalStorageDirectory();
									path = sdcardDir.getPath() + "/xp_datafile/" + taskname + "/" + filename;
									File mFile = new File(path);
									if (mFile.exists()) {
										mFile.delete();
									}
								}
								mCurlFileAdapter.clear();
								mCurlFiles = DBMgr.getInstance(TaskListActivity.this).getCurrFileByTaskName(taskname);
								for (TaskCurlFile mCurlFile : mCurlFiles)
									mCurlFileAdapter.add(mCurlFile.getTask_curl_file());
								mListTaskCurrFile.setAdapter(mCurlFileAdapter);
								if (mCurlFileAdapter != null)
									mCurlFileAdapter.notifyDataSetChanged();

							}

						});
						mBuilder.setPositiveButton(getString(android.R.string.cancel), new OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								// TODO Auto-generated method
								// stub

							}
						});

						AlertDialog mAlertDialog = mBuilder.create();
						mAlertDialog.show();
						return false;
					}
				});
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				// TODO Auto-generated method stub

			}
		});

		Button mButton = (Button) findViewById(R.id.btnscfile);
		mButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent curr = new Intent(TaskListActivity.this, ActivityTaskCurrfile.class);
				curr.putExtra(XpHelper.Tn, spinnerdata);
				curr.putExtra("index", d);
				TaskListActivity.this.startActivity(curr);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		if (inflater != null && PrivacyService.checkClient()) {
			inflater.inflate(R.menu.task, menu);
			return true;
		} else
			return false;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		try {
			switch (item.getItemId()) {
			case R.id.task_menu_create:
				startActivity(new Intent(TaskListActivity.this, ActivityCreateTask.class));
				return true;

			/*case R.id.task_menu_manager:
				startActivity(new Intent(TaskListActivity.this, ActivityTaskManager.class));
				return true;

			case R.id.task_curr_file:
				Intent curr = new Intent(TaskListActivity.this, ActivityTaskCurrfile.class);
				curr.putExtra(XpHelper.Tn, spinnerdata);
				curr.putExtra("index", d);
				TaskListActivity.this.startActivity(curr);
				return true;

			case R.id.task_menu_model:
				optionModel();
				return true;

			case R.id.xp_read_file_count:
				optionReadFileCount();
				return true;*/
			default:
				return super.onOptionsItemSelected(item);
			}
		} catch (Throwable ex) {
			Util.bug(null, ex);
			return true;
		}
	}

	private void optionReadFileCount()
	{
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.layout_read_file_count, null);

		TextView tvFileName = (TextView) view.findViewById(R.id.read_file_count_name);
		TextView tvFileCurrCount = (TextView) view.findViewById(R.id.read_file_count);

		String fileTask = PreferenceUtils.getParam(TaskListActivity.this, "taskname", "").toString();

		tvFileName.setText("文件：" + PreferenceUtils.getParam(TaskListActivity.this, fileTask, "curl_file_name", "").toString());
		tvFileCurrCount.setText("读取数量：" + PreferenceUtils.getParam(TaskListActivity.this, fileTask, "curl_file_count", 0).toString());

		TextView tvMode = (TextView) view.findViewById(R.id.read_file_mode);
		tvMode.setText("文件模式：" + PreferenceUtils.getParam(TaskListActivity.this, fileTask, "curl_file_read_mode", "默认"));
		// Build dialog
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TaskListActivity.this);
		alertDialogBuilder.setTitle("数据说明");
		alertDialogBuilder.setView(view);
		alertDialogBuilder.setPositiveButton(TaskListActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener()
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

	private void optionModel()
	{
		// TODO Auto-generated method stub
		startActivity(new Intent(TaskListActivity.this, ActivityModel.class));
	}

	private static final int ACTIVITY_IMPORT_SELECT = 0;
	private String mFileName = null;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// Import select
		if (requestCode == ACTIVITY_IMPORT_SELECT)
			if (resultCode == RESULT_CANCELED || data == null)
				finish();
			else {
				String fileName = data.getData().getPath();
				mFileName = fileName.replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar);
				showFileName();
			}
	}

	private void showFileName()
	{
		// TODO Auto-generated method stub
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(TaskListActivity.this);
		mBuilder.setTitle("导入提醒");
		mBuilder.setIcon(R.drawable.ic_launcher);
		mBuilder.setMessage(mFileName);
		mBuilder.setNegativeButton(TaskListActivity.this.getString(android.R.string.ok), new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface arg0, int arg1)
			{
				// TODO Auto-generated method stub
				ModelAsyncTask mAsyncTask = new ModelAsyncTask();
				mAsyncTask.executeOnExecutor(mExecutor, null);

			}
		}).setNeutralButton(TaskListActivity.this.getString(android.R.string.cancel), new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface arg0, int arg1)
			{
				// TODO Auto-generated method stub
				arg0.cancel();

			}
		}).create().show();

	}

	public class ModelAsyncTask extends AsyncTask<Object, Integer, List<xpmodel>>
	{
		ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(TaskListActivity.this);
			mProgressDialog.setMessage("正在执行操作...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// mProgressDialog.setProgressNumberFormat(null);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected List<xpmodel> doInBackground(Object... arg0)
		{
			// TODO Auto-generated method stub
			try {
				return readcurray_DataBase(mFileName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(List<xpmodel> result)
		{
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!TaskListActivity.this.isFinishing()) {
				mProgressDialog.dismiss();
				if (result != null) {
					for (int i = 0; i < result.size(); i++) {
						ContentValues values = new ContentValues();
						values.put(xpmodel.MODEL, result.get(i).getModel());
						values.put(xpmodel.MANUFACTURER, result.get(i).getManufacturer());
						values.put(xpmodel.PRODUCT, result.get(i).getProduct());
						values.put(xpmodel.DENSITY, result.get(i).getDensity());
						values.put(xpmodel.FALG, result.get(i).getFlag());
						long s = DBMgr.getInstance(TaskListActivity.this).add_xp_model_improt(TaskListActivity.this, values);
					}

					optionView();
				} else {
					Toast.makeText(TaskListActivity.this, "无效的文件！", 1).show();
				}
			}
		}
	}

	private void optionView()
	{
		// TODO Auto-generated method stub
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TaskListActivity.this);
		alertDialogBuilder.setTitle("提示");
		alertDialogBuilder.setMessage("是否查看机型数据？");
		alertDialogBuilder.setPositiveButton(TaskListActivity.this.getString(android.R.string.ok), new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				viewModel();
			}
		});
		alertDialogBuilder.setNegativeButton(TaskListActivity.this.getString(android.R.string.cancel), new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		// Show dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private static XpmodeParser parser;

	public static List<xpmodel> readcurray_DataBase(String path) throws Exception
	{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(path);
			parser = new XpmodelByXml();
			if (parser != null) {
				return parser.parse(new FileInputStream(file));
			}
		}
		return null;
	}

	protected void onResume()
	{
		super.onResume();
		// Update list
		mSpListTaskAdapter.clear();
		mTaskDescs = DBMgr.getInstance(TaskListActivity.this).getTaskList();
		for (TaskAttribute mDesc : mTaskDescs)
			mSpListTaskAdapter.add(mDesc.getTaskName());
		spListTask.setAdapter(mSpListTaskAdapter);
		int s = (Integer) PreferenceUtils.getParam(TaskListActivity.this, "position", 0);
		// if (mTaskDescs.size() > 0 || s > mTaskDescs.size()) {
		spListTask.setSelection(s, true);
		// } else {
		// spListTask.setSelection(0, true);
		// }
		if (mSpListTaskAdapter != null)
			mSpListTaskAdapter.notifyDataSetChanged();

	}

	List<xpmodel> xpmodelsList;
	private ModelAdapter modelAdapter;

	private void viewModel()
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.model, null);
		ListView mListView = (ListView) view.findViewById(R.id.task_model_list);
		xpmodelsList = DBMgr.getInstance(TaskListActivity.this).getXpmpdellist();
		modelAdapter = new ModelAdapter(TaskListActivity.this, xpmodelsList);
		mListView.setAdapter(modelAdapter);
	}

	class ModelAdapter extends BaseAdapter
	{

		private Context mContext;
		private List<xpmodel> mXpmodels;

		public ModelAdapter(Context mContext, List<xpmodel> mXpmodels)
		{
			this.mContext = mContext;
			this.mXpmodels = mXpmodels;
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return mXpmodels.size();
		}

		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return mXpmodels.get(arg0);
		}

		@Override
		public long getItemId(int arg0)
		{
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2)
		{
			// TODO Auto-generated method stub
			ViewHolder mHolder;
			if (arg1 == null) {
				mHolder = new ViewHolder();
				arg1 = LayoutInflater.from(mContext).inflate(R.layout.model_item, null);
				mHolder.mtvmodel = (TextView) arg1.findViewById(R.id.tvmodel);
				mHolder.mtvproduct = (TextView) arg1.findViewById(R.id.tvproduct);
				mHolder.mtvmanufacturer = (TextView) arg1.findViewById(R.id.tvmanufacturer);
				mHolder.mtvdensity = (TextView) arg1.findViewById(R.id.tvdensity);
				arg1.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) arg1.getTag();
			}
			// set data
			xpmodel mXpmodel = mXpmodels.get(arg0);
			mHolder.mtvmodel.setText(mXpmodel.getModel());
			mHolder.mtvproduct.setText(mXpmodel.getProduct());
			mHolder.mtvmanufacturer.setText(mXpmodel.getManufacturer());
			mHolder.mtvdensity.setText(mXpmodel.getDensity());
			return arg1;
		}

		class ViewHolder
		{
			TextView mtvmodel;
			TextView mtvproduct;
			TextView mtvmanufacturer;
			TextView mtvdensity;
		}
	}

	public static List<PhoneDataBean> readcurray(String path, String task_date_file) throws Exception
	{
		// TODO Auto-generated method stub
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String mPath = path + "/" + task_date_file;
			String result = BufferedReaderJSON(mPath);

			if (null != result) {
				Gson mGson = new Gson();
				List<PhoneDataBean> mBases = mGson.fromJson(result, new TypeToken<List<PhoneDataBean>>()
				{
				}.getType());
				return mBases;
			}
		}
		return null;
	}

	public static String BufferedReaderJSON(String mPath) throws Exception
	{
		// TODO Auto-generated method stub
		File file = new File(mPath);
		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException();
			// return null;
		} else {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String temp = null;
			StringBuffer sb = new StringBuffer();

			while ((temp = br.readLine()) != null) {
				sb.append(temp + " ");
				temp = br.readLine();
			}
			br.close();
			return sb.toString();
		}
	}
}
