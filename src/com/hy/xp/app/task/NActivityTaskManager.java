package com.hy.xp.app.task;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.hy.xp.app.ActivityBase;
import com.hy.xp.app.ActivityMain;
import com.hy.xp.app.ApplicationEx;
import com.hy.xp.app.PrivacyService;
import com.hy.xp.app.R;
import com.hy.xp.app.Util;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;

public class NActivityTaskManager extends ActivityBase {
	private SwipeMenuListView mListTaskManager;
	List<TaskAttribute> mTaskDescs = null;
	ArrayAdapter<String> mSpListTaskAdapter = null;
	private static ExecutorService mExecutor = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors(),
			new PriorityThreadFactory());
	private TaskAdapter mAdapter;

	private static class PriorityThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mTaskDescs = DBMgr.getInstance(NActivityTaskManager.this).getTaskList();
		mAdapter = new TaskAdapter();
		mListTaskManager.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_task_mananger);

		mListTaskManager = (SwipeMenuListView) findViewById(R.id.listview_task_manager_item);
		mTaskDescs = DBMgr.getInstance(NActivityTaskManager.this).getTaskList();
		mAdapter = new TaskAdapter();
		mListTaskManager.setAdapter(mAdapter);

		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				// set item width
				openItem.setWidth(dp2px(90));
				// set item title
				openItem.setTitle("编辑");
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);

				// create "select" item
				SwipeMenuItem selectItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				selectItem.setBackground(new ColorDrawable(Color.rgb(0xC9,
						0xC9, 0xCE)));
				// set item width
				selectItem.setWidth(dp2px(90));
				// set item title
				selectItem.setTitle("选择");
				// set item title fontsize
				selectItem.setTitleSize(18);
				// set item title font color
				selectItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(selectItem);
			}
		};
		// set creator
		mListTaskManager.setMenuCreator(creator);

		// step 2. listener item click event
		mListTaskManager
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						// ApplicationInfo item = mAppList.get(position);
						TaskAttribute mTaskAttribute = mTaskDescs.get(position);

						switch (index) {
						case 0:
							// open
							optionTaskedit(mTaskAttribute);
							mAdapter.notifyDataSetChanged();
							break;
						case 1:
							// delete
							optionTaskdelete(mTaskAttribute);
							//mTaskDescs.remove(position);
							mAdapter.notifyDataSetChanged();
							break;
						case 2:
							opionTaskselect(mTaskAttribute);
							break;
						}
					}

					private void opionTaskselect(TaskAttribute mTaskAttribute) {
						SharedPreferences preferences = ApplicationEx
								.getContextObject().getSharedPreferences(
										"task", Context.MODE_PRIVATE);
						Editor editor = preferences.edit();
						editor.putString("currenttask",
								mTaskAttribute.getTaskName()).commit();
					}

				});

		// set SwipeListener
		mListTaskManager.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		// test item long click
		mListTaskManager
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// Toast.makeText(getApplicationContext(), position +
						// " long click", 0).show();
						return false;
					}
				});

	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	private void optionTaskedit(TaskAttribute mTaskAttribute) {
		Intent mIntent = new Intent(NActivityTaskManager.this,
				ActivityEditTask.class);
		mIntent.putExtra("mTaskAttribute", mTaskAttribute);
		startActivity(mIntent);
	}

	private void optionTaskdelete(TaskAttribute mTaskAttribute) {
		// TODO Auto-generated method stub
		final String taskanem = mTaskAttribute.getTaskName();
		String CurlTask = PreferenceUtils.getParam(NActivityTaskManager.this,
				"taskname", "").toString();
		if (taskanem.equals(CurlTask)) {
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(
					NActivityTaskManager.this);
			mBuilder.setTitle("删除提醒");
			mBuilder.setIcon(R.drawable.ic_launcher);
			mBuilder.setMessage("当前任务正在使用，确定删除" + taskanem + "任务？" + "自动备份任务!");
			mBuilder.setNegativeButton(
					NActivityTaskManager.this.getString(android.R.string.ok),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							// 先删除表中的数据
							DBMgr.getInstance(NActivityTaskManager.this)
									.clean_task(taskanem);
							// DBMgr.getInstance(NActivityTaskManager.this).delet_task(taskanem);
							String state = Environment
									.getExternalStorageState();
							String path = null;
							if (Environment.MEDIA_MOUNTED.equals(state)) {
								if (taskanem != null) {
									File sdcardDir = Environment
											.getExternalStorageDirectory();
									path = sdcardDir.getPath()
											+ "/xp_datafile/" + taskanem;
									File mFile = new File(path);
									OperationFileHelper
											.RecursionDeleteFile(mFile);
								}
							}
							// updata
							// mSpListTaskAdapter.clear();
							// mTaskDescs =
							// DBMgr.getInstance(NActivityTaskManager.this).getTaskList();
							// mSpListTaskAdapter = new
							// ArrayAdapter<String>(NActivityTaskManager.this,
							// android.R.layout.simple_list_item_1);
							// mTaskDescs =
							// DBMgr.getInstance(NActivityTaskManager.this).getTaskList();
							// for (TaskAttribute mDesc : mTaskDescs)
							// mSpListTaskAdapter.add(mDesc.getTaskName());
							// mListTaskManager.setAdapter(mSpListTaskAdapter);
							// mSpListTaskAdapter.notifyDataSetChanged();
							// export
							if (mTaskDescs.size() > 0) {
								new ConfigExport(NActivityTaskManager.this)
										.export_Task(mExecutor);
							} else {
								if (Environment.MEDIA_MOUNTED.equals(state)) {
									File sdcardDir = Environment
											.getExternalStorageDirectory();
									path = sdcardDir.getPath()
											+ "/xp_datafile/";
								}
								File mFile = new File(path
										+ "task_attribute.json");
								mFile.delete();
								File mFiles = new File(path
										+ "task_data_file.json");
								mFiles.delete();
							}
							// delete sharedprefs file
							File shared = new File("/data/data/"
									+ getPackageName().toString()
									+ "/shared_prefs", taskanem + ".xml");
							if (shared.exists()) {
								shared.delete();
								PreferenceUtils.setParam(
										NActivityTaskManager.this, "taskname",
										"");
							}
							PreferenceUtils.setParam(NActivityTaskManager.this,
									"position", 0);
						}
					});
			mBuilder.setNeutralButton(NActivityTaskManager.this
					.getString(android.R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
						}
					});
			mBuilder.create().show();
		} else {
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(
					NActivityTaskManager.this);
			mBuilder.setTitle("删除提醒");
			mBuilder.setIcon(R.drawable.ic_launcher);
			mBuilder.setMessage("确定删除" + taskanem + "任务？" + "自动备份任务!");
			mBuilder.setNegativeButton(
					NActivityTaskManager.this.getString(android.R.string.ok),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							// 先删除表中的数据
							DBMgr.getInstance(NActivityTaskManager.this)
									.clean_task(taskanem);
							// DBMgr.getInstance(NActivityTaskManager.this).delet_task(taskanem);
							// 再删除文件
							String state = Environment
									.getExternalStorageState();
							String path = null;
							if (Environment.MEDIA_MOUNTED.equals(state)) {
								File sdcardDir = Environment
										.getExternalStorageDirectory();
								path = sdcardDir.getPath() + "/xp_datafile/"
										+ taskanem;
								File mFile = new File(path);
								OperationFileHelper.RecursionDeleteFile(mFile);
							}
							// update view
							// mSpListTaskAdapter.clear();
							// mTaskDescs =
							// DBMgr.getInstance(NActivityTaskManager.this).getTaskList();
							// mSpListTaskAdapter = new
							// ArrayAdapter<String>(NActivityTaskManager.this,
							// android.R.layout.simple_list_item_1);
							// mTaskDescs =
							// DBMgr.getInstance(NActivityTaskManager.this).getTaskList();
							// for (TaskAttribute mDesc : mTaskDescs)
							// mSpListTaskAdapter.add(mDesc.getTaskName());
							// mListTaskManager.setAdapter(mSpListTaskAdapter);
							// mSpListTaskAdapter.notifyDataSetChanged();

							// export
							if (mTaskDescs.size() > 0) {
								new ConfigExport(NActivityTaskManager.this)
										.export_Task(mExecutor);
							} else {
								if (Environment.MEDIA_MOUNTED.equals(state)) {
									File sdcardDir = Environment
											.getExternalStorageDirectory();
									path = sdcardDir.getPath()
											+ "/xp_datafile/";
								}
								File mFile = new File(path
										+ "task_attribute.json");
								mFile.delete();
								File mFiles = new File(path
										+ "task_data_file.json");
								mFiles.delete();
							}
							// delete sharedprefs file
							File shared = new File("/data/data/"
									+ getPackageName().toString()
									+ "/shared_prefs", taskanem + ".xml");
							if (shared.exists()) {
								shared.delete();
								PreferenceUtils.setParam(
										NActivityTaskManager.this, "taskname",
										"");
							}
							PreferenceUtils.setParam(NActivityTaskManager.this,
									"position", 0);

						}
					});
			mBuilder.setNeutralButton(NActivityTaskManager.this
					.getString(android.R.string.cancel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							arg0.cancel();
						}
					});
			mBuilder.create().show();
		}

	}

	class TaskAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mTaskDescs.size();
		}

		@Override
		public TaskAttribute getItem(int position) {
			return mTaskDescs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_list_app, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			TaskAttribute item = getItem(position);
			holder.tv_name.setText(item.getTaskName());
			return convertView;
		}

		class ViewHolder {
			TextView tv_name;

			public ViewHolder(View view) {
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				view.setTag(this);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (inflater != null && PrivacyService.checkClient()) {
			inflater.inflate(R.menu.task, menu);
			return true;
		} else
			return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			Intent it = null;
			switch (item.getItemId()) {
			case R.id.task_menu_create:
				it = new Intent(NActivityTaskManager.this,
						ActivityCreateTask.class);
				startActivity(it);
				finish();
				return true;
			case R.id.task_import_file:
				showChooser();
				return true;
			case R.id.task_permission_manager:
				it = new Intent(NActivityTaskManager.this, ActivityMain.class);
				startActivity(it);
				finish();
				break;
			default:
				return super.onOptionsItemSelected(item);
			}
		} catch (Throwable ex) {
			Util.bug(null, ex);
			return true;
		}
		return false;
	}

	private static final int REQUEST_CHOOSER = 1234;

	private void showChooser() {
		FileUtils.createGetContentIntent();
		Intent localIntent = new Intent(this, FileChooserActivity.class);
		try {
			startActivityForResult(localIntent, REQUEST_CHOOSER);
			return;
		} catch (ActivityNotFoundException localActivityNotFoundException) {
		}
	}

	private String mFileName = "";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CHOOSER) {
			if (resultCode == RESULT_OK) {
				final Uri uri = data.getData();

				String path = FileUtils.getPath(this, uri);

				if (path != null && FileUtils.isLocal(path)) {
					File file = new File(path);
					mFileName = file.getPath();
					showFileName();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void showFileName() {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		localBuilder.setTitle("导入提醒");
		localBuilder.setIcon(R.drawable.ic_chooser);
		localBuilder.setMessage(mFileName);
		localBuilder
				.setNegativeButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface paramAnonymousDialogInterface,
							int paramAnonymousInt) {
						Add_TaskAsyncTask mAsyncTask = new Add_TaskAsyncTask();
						mAsyncTask.executeOnExecutor(mExecutor, null);
					}
				})
				.setNeutralButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface paramAnonymousDialogInterface,
							int paramAnonymousInt) {
						paramAnonymousDialogInterface.cancel();
					}
				}).create().show();
	}

	public class Add_TaskAsyncTask extends AsyncTask<Object, Integer, Object> {
		ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(NActivityTaskManager.this);
			mProgressDialog.setMessage("正在执行操作...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				File localFile = new File(NActivityTaskManager.this.mFileName);
				List<PhoneDataBean> localList = Util.readcurray(
						localFile.getParent(), localFile.getName());
				DBMgr.getInstance(ApplicationEx.getContextObject()).plInsert(
						localList);
				Integer localInteger = Integer.valueOf(1);
				return localInteger;
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if (!NActivityTaskManager.this.isFinishing()) {
				this.mProgressDialog.dismiss();
				if (result == null)
					Toast.makeText(NActivityTaskManager.this, "无效的文件！", 1)
							.show();
				else{
					Toast.makeText(NActivityTaskManager.this, "数据插入成功！", 1)
					.show();
				}
			}
		}

	}

}
