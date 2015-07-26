package com.hy.xp.app.task;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hy.xp.app.ActivityBase;
import com.hy.xp.app.PrivacyService;
import com.hy.xp.app.R;
import com.hy.xp.app.Util;

/**
 * 生成数据
 * 
 * @author Administrator
 */
public class ActivityTaskCurrfile extends ActivityBase
{
	private ListView mListViewDatafile;
	private String[] array = { "顺序抽取", "随机抽取" };
	private String config_shunxuOrramoder = "顺序抽取";
	TaskAttribute mTaskAttribute;
	List<TaskAttribute> mAttributesList = null;
	int mPostion;
	List<PhoneDataBean> mNewBases = new ArrayList<PhoneDataBean>();
	List<PhoneDataBean> mTmpBases = new ArrayList<PhoneDataBean>();
	List<PhoneDataBean> mDataSum = new ArrayList<PhoneDataBean>();
	List<PhoneDataBean> mDataSum_To_File = new ArrayList<PhoneDataBean>();
	private List<PhoneDataBean> mNewBases_Geri = new LinkedList<PhoneDataBean>();
	private List<PhoneDataBean> mTmpBases_Geri = new LinkedList<PhoneDataBean>();
	private Random mRandom = new Random();
	List<DataBase> mListDataFile;
	private ProgressDialog mProgressDialog;

	Spinner mSpinner;
	CheckBox mCheckBox;

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
		setContentView(R.layout.layout_curr_file);

		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Toast.makeText(ActivityTaskCurrfile.this, "请选择任务！", 1).show();
			finish();
			return;
		}

		String tn = extras.getString(XpHelper.Tn);
		int d = extras.getInt("index");
		PreferenceUtils.setParam(ActivityTaskCurrfile.this, "position", d);
		// 1、获取数据文件
		mListViewDatafile = (ListView) findViewById(R.id.listview_data_file_item);
		mListDataFile = DBMgr.getInstance(ActivityTaskCurrfile.this).getDataFileByTaskName(tn);
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(ActivityTaskCurrfile.this, android.R.layout.simple_list_item_single_choice);
		for (DataBase mBase : mListDataFile)
			mAdapter.add(mBase.getTask_date_file());
		mListViewDatafile.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mListViewDatafile.setAdapter(mAdapter);
		mAttributesList = DBMgr.getInstance(ActivityTaskCurrfile.this).getTaskAttributeByTaskName(tn);
		if (mAttributesList.size() <= 0) {
			return;
		}
		// 获取该任务的属性
		mTaskAttribute = mAttributesList.get(0);
		mListViewDatafile.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// TODO Auto-generated method stub
				mPostion = position;
			}
		});

		mSpinner = (Spinner) findViewById(R.id.sp_spinner);
		mCheckBox = (CheckBox) findViewById(R.id.cb_ischeckbox);
		ArrayAdapter<String> mOPeratorAdapter = new ArrayAdapter<String>(ActivityTaskCurrfile.this, android.R.layout.simple_spinner_item, array);
		mOPeratorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(mOPeratorAdapter);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				config_shunxuOrramoder = (String) arg0.getItemAtPosition(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub

			}
		});
		config_shunxuOrramoder = config_shunxuOrramoder;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		if (inflater != null && PrivacyService.checkClient()) {
			inflater.inflate(R.menu.settings, menu);
			return true;
		} else
			return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		case R.id.menu_cancel:
			optionCancel();
			return true;
		case R.id.menu_save:
			optionSave();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void optionSave()
	{
		// TODO Auto-generated method stub
		CurlFileTask mCurlFileTask = new CurlFileTask();
		mCurlFileTask.executeOnExecutor(mExecutor, null);
	}

	private void optionCancel()
	{
		// TODO Auto-generated method stub
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityTaskCurrfile.this);
		alertDialogBuilder.setTitle("确认操作？");
		alertDialogBuilder.setMessage("确定放弃该操作码");
		alertDialogBuilder.setPositiveButton("确定", new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				ActivityTaskCurrfile.this.finish();
			}
		});
		alertDialogBuilder.setNegativeButton("取消", new OnClickListener()
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

	public void createfile() throws Exception
	{
		String state = Environment.getExternalStorageState();
		String path = null;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			path = sdcardDir.getPath() + "/xp_datafile/" + mTaskAttribute.getTaskName();
		}
		System.out.println(mCheckBox.isChecked());
		if (!mCheckBox.isChecked()) {
			if ("顺序抽取".equals(config_shunxuOrramoder)) {
				/* 判断是否需要隔日 */
				boolean flag = mTaskAttribute.isTaskNextDayFlag();
				if (flag) { /* 需要隔日 */
					/* 1、判断是否需要隔日递减 */
					boolean NextDayVisitDeclineFlag = mTaskAttribute.isTaskNextDayVisitDeclineFlag();
					if (!NextDayVisitDeclineFlag) { /* 不需要隔日递减 */
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval();// 隔日天数
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();// 隔日次数
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();
						mNewBases_Geri.clear();
						// 当次数为0
						// 根据当前坐标和间隔天数，求出次数
						if (TaskNextDayVisitIntervalCount != 0) {
							int sssss = TaskNextDayVisitIntervalCount + 1;
							// System.out.println("---------------sssss----------"
							// + sssss);
							for (int i = 1; i < sssss; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("----------------i----------" + i);
								try {
									mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;
								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;
								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
							}
						} else {
							// 为零
							System.out.println("???????????????????");
							int d = 1;
							while ((mPostion - ((TaskNextDayVisitInterval + 1)) * d) > 0) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * d;
								try {
									mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;
								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;

								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
								d++;
							}

							for (int i = 1; i < mPostion; i++) {

							}
						}
					} else {
						mNewBases_Geri.clear();
						double TaskNextDayVisitDecilneMin = mTaskAttribute.getTaskNextDayVisitDecilneMin(); // 隔日中递减最小值
						double NextDayVisitDecilneRatio = mTaskAttribute.getTaskNextDayVisitDecilneRatio(); // 隔日中哦过递减比率
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();// 隔日中的回访比率
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval(); // 故而诶间隔天数
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();//
						double tmp_bilv;
						int ll = 0;
						int s = 0;
						if (TaskNextDayVisitIntervalCount != 0) {
							// 根据隔日次数来进行取数据
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								// 包含当前文件的数据
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;
								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;
								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
								ll++;
							}
						} else {
							// 为0\\\\\\\\\\\\\\\\\\\\\
							System.out.println("???????????????????");
							int d = 1;
							while (true) {
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * d;
								if (index < 0) {
									break;
								}
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;
								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;
								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
								ll++;
								d++;

							}
						}

					}
				}

				/* 普通回访 */
				/* 1、普通回访是否需要递减 */
				boolean DeclineFlag = mTaskAttribute.isTaskDeclineFlag();
				if (!DeclineFlag) {// 普通不需要递减
									// System.out.println("------------------------------------");
					mNewBases.clear();
					try {
						mNewBases = Util.readcurray(path, mListDataFile.get(mPostion).getTask_date_file());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					double TaskReturnratio = mTaskAttribute.getTaskReturnratio();
					PhoneDataBean mPhoneDataBean = null;
					// (int i = 0; i < arg2;// i++)
					// 802 for (int i = mPostion - 1; i > 0; i--)
					for (int i = mPostion - 1; i >= 0; i--) {
						System.out.println("802");
						try {
							mTmpBases = Util.readIsDijianShunxu(path, mListDataFile.get(i).getTask_date_file(), TaskReturnratio);
							System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// 去重复
						int data_index[] = new int[mTmpBases.size()];
						int n = mTmpBases.size();
						boolean[] bool = new boolean[n];
						int randInt = 0;
						for (int j = 0; j < mTmpBases.size(); j++) {
							do {
								randInt = mRandom.nextInt(mTmpBases.size());
							} while (bool[randInt]);
							bool[randInt] = true;
							data_index[j] = randInt;
						}
						for (int j = 0; j < data_index.length; j++) {
							mNewBases.add(mTmpBases.get(data_index[j]));
						}
						mTmpBases.clear();
					}
				} else {/* 普通需要递减 */
					// ////System.out.println("需要递减");
					mNewBases.clear();
					try {
						mNewBases = Util.readcurray(path, mListDataFile.get(mPostion).getTask_date_file());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					double TaskDecilneMin = mTaskAttribute.getTaskDecilneMin();
					double TaskDecilneRatio = mTaskAttribute.getTaskDecilneRatio();
					double TaskReturnratio = mTaskAttribute.getTaskReturnratio();
					double tmp_bilv;
					int ll = 0;
					PhoneDataBean mPhoneDataBean = null;
					// for (int i = 0; i < arg2; i++)
					for (int i = mPostion - 1; i >= 0; i--) {
						tmp_bilv = TaskReturnratio - TaskDecilneRatio * ll;
						System.out.println("tmp_bilv\t" + tmp_bilv);
						if (tmp_bilv <= TaskDecilneMin) {
							tmp_bilv = TaskDecilneMin;
							mTmpBases = Util.readIsDijianShunxu(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
							System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
						} else {
							mTmpBases = Util.readIsDijianShunxu(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
							System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
							tmp_bilv = tmp_bilv - TaskDecilneRatio;
						}

						// 去重复
						int data_index[] = new int[mTmpBases.size()];
						int n = mTmpBases.size();
						boolean[] bool = new boolean[n];
						int randInt = 0;

						for (int j = 0; j < mTmpBases.size(); j++) {
							do {
								randInt = mRandom.nextInt(mTmpBases.size());
							} while (bool[randInt]);
							bool[randInt] = true;
							data_index[j] = randInt;
						}
						for (int j = 0; j < data_index.length; j++) {
							mNewBases.add(mTmpBases.get(data_index[j]));
						}
						mTmpBases.clear();
						ll++;
					}
				}

				/* 数据合并 */
				// 组拼数据
				mDataSum.clear();
				if (mNewBases_Geri != null) {

					mDataSum.addAll(mNewBases);
					mDataSum.addAll(mNewBases_Geri);
				} else {
					mDataSum.addAll(mNewBases);

				}
				System.out.println("隔日读取数据和普通读取数据合并个数：" + mDataSum.size());

			}
			if ("随机抽取".equals(config_shunxuOrramoder)) {

				/* 判断是否需要隔日 */
				boolean flag = mTaskAttribute.isTaskNextDayFlag();
				if (flag) { /* 需要隔日 */
					/* 1、判断是否需要隔日递减 */
					boolean NextDayVisitDeclineFlag = mTaskAttribute.isTaskNextDayVisitDeclineFlag();
					if (!NextDayVisitDeclineFlag) {/* 不需要隔日递减 */
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval();// 隔日天数
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();// 隔日次数
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();
						mNewBases_Geri.clear();
						if (TaskNextDayVisitIntervalCount != 0) {
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								try {
									mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;

								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;

								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}

								mTmpBases_Geri.clear();
							}
						} else {
							// 为0
							// System.out.println("???????????????????");
							for (int i = 1; i < mPostion; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								try {
									mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;
								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;
								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
							}
						}

					} else {
						mNewBases_Geri.clear();
						double TaskNextDayVisitDecilneMin = mTaskAttribute.getTaskNextDayVisitDecilneMin(); // 隔日中递减最小值
						double NextDayVisitDecilneRatio = mTaskAttribute.getTaskNextDayVisitDecilneRatio(); // 隔日中哦过递减比率
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();// 隔日中的回访比率
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval(); // 故而诶间隔天数
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();//
						double tmp_bilv;
						int s = 0;
						int ll = 0;
						if (TaskNextDayVisitIntervalCount != 0) {
							// 根据隔日次数来进行取数据
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {

								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								// 包含当前文件的数据
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}

								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;

								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;
								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
								ll++;
							}
						} else {
							// 为0
							System.out.println("???????????????????");
							int d = 1;
							while (true) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * d;
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								if (index < 0) {
									break;
								}
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;
								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;
								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
								ll++;
								d++;
							}
						}
					}
				}

				/* 普通回访 */
				/* 1、普通回访是否需要递减 */
				boolean DeclineFlag = mTaskAttribute.isTaskDeclineFlag();
				if (!DeclineFlag) {// 普通不需要递减
					System.out.println("-----普通不需要递减-------------------------------");
					mNewBases.clear();
					try {
						mNewBases = Util.readcurray(path, mListDataFile.get(mPostion).getTask_date_file());
					} catch (Exception e) {
						e.printStackTrace();
					}
					double TaskReturnratio = mTaskAttribute.getTaskReturnratio();
					PhoneDataBean mPhoneDataBean = null;
					// for (int i = 0; i < arg2; i++)
					// 802 //802 for (int i = mPostion - 1; i > 0; i--)
					for (int i = mPostion - 1; i >= 0; i--) {
						// ////System.out.println("802");
						try {
							mTmpBases = Util.readIsDijianRandom(path, mListDataFile.get(i).getTask_date_file(), TaskReturnratio);
							System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
						} catch (Exception e) {
							e.printStackTrace();
						}
						// 去重复
						int data_index[] = new int[mTmpBases.size()];
						int n = mTmpBases.size();
						boolean[] bool = new boolean[n];
						int randInt = 0;
						for (int j = 0; j < mTmpBases.size(); j++) {
							do {
								randInt = mRandom.nextInt(mTmpBases.size());
							} while (bool[randInt]);
							bool[randInt] = true;
							data_index[j] = randInt;

						}
						for (int j = 0; j < data_index.length; j++) {
							mNewBases.add(mTmpBases.get(data_index[j]));
						}
						mTmpBases.clear();
					}
				} else {/* 普通需要递减 */
					mNewBases.clear();
					System.out.println("普通需要递减-------------------");
					try {
						mNewBases = Util.readcurray(path, mListDataFile.get(mPostion).getTask_date_file());
					} catch (Exception e) {
						e.printStackTrace();
					}
					double TaskDecilneMin = mTaskAttribute.getTaskDecilneMin();
					double TaskDecilneRatio = mTaskAttribute.getTaskDecilneRatio();
					double TaskReturnratio = mTaskAttribute.getTaskReturnratio();
					double tmp_bilv;
					int ll = 0;
					PhoneDataBean mPhoneDataBean = null;
					// for (int i = 0; i < arg2; i++)
					for (int i = mPostion - 1; i >= 0; i--) {
						tmp_bilv = TaskReturnratio - TaskDecilneRatio * ll;
						System.out.println("tmp_bilv\t" + tmp_bilv);
						if (tmp_bilv <= TaskDecilneMin) {
							tmp_bilv = TaskDecilneMin;
							try {
								mTmpBases = Util.readIsDijianRandom(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
								System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							try {
								mTmpBases = Util.readIsDijianRandom(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
								System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							tmp_bilv = tmp_bilv - TaskDecilneRatio;
						}

						// 去重复
						int data_index[] = new int[mTmpBases.size()];
						int n = mTmpBases.size();
						boolean[] bool = new boolean[n];
						int randInt = 0;

						for (int j = 0; j < mTmpBases.size(); j++) {
							do {
								randInt = mRandom.nextInt(mTmpBases.size());
							} while (bool[randInt]);
							bool[randInt] = true;
							data_index[j] = randInt;

						}
						for (int j = 0; j < data_index.length; j++) {
							mNewBases.add(mTmpBases.get(data_index[j]));
						}
						mTmpBases.clear();
						ll++;
					}
				}

				/* 数据合并 */
				// 组拼数据
				mDataSum.clear();
				if (mNewBases_Geri != null) {

					mDataSum.addAll(mNewBases);
					mDataSum.addAll(mNewBases_Geri);
				} else {
					mDataSum.addAll(mNewBases);

				}
				System.out.println("隔日读取数据和普通读取数据合并个数：" + mDataSum.size());

			}
			/* 将新集合打乱 */
			// 去重复
			mDataSum_To_File.clear();
			int data_index[] = new int[mDataSum.size()];
			int n = mDataSum.size();
			boolean[] bool = new boolean[n];
			int randInt = 0;

			for (int j = 0; j < mDataSum.size(); j++) {
				do {
					randInt = mRandom.nextInt(mDataSum.size());
				} while (bool[randInt]);
				bool[randInt] = true;
				data_index[j] = randInt;

			}
			for (int j = 0; j < data_index.length; j++) {
				mDataSum_To_File.add(mDataSum.get(data_index[j]));
			}

			// 创建文件
			Gson mGson = new Gson();
			String fileName = mListDataFile.get(mPostion).getTask_date_file().toString();
			String a[] = fileName.split("-");
			a[0] = mDataSum_To_File.size() + "";
			String temp = "执行文件" + "-" + a[0] + "-" + a[1] + "-" + a[2];

			long isCurl = DBMgr.getInstance(ActivityTaskCurrfile.this).findCurl(temp);
			if (isCurl > 0) {
				// Toast.makeText(ActivityTaskCurrfile.this,
				// "当日文件以创建，请不要重复创建单日文件", 1).show();
			} else {
				// 添加数据表
				String create_state = Util.createRandomcodeFile(ActivityTaskCurrfile.this, mGson.toJson(mDataSum_To_File), path, temp);
				ContentValues mValues = new ContentValues();
				mValues.put(TaskCurlFile.TASK_NAME, mTaskAttribute.getTaskName());
				mValues.put(TaskCurlFile.TASK_CURL_FILE, temp);
				DBMgr.getInstance(ActivityTaskCurrfile.this).add_culr_file(mValues);

			}
		} else {

			System.out.println("xuanzhong-------------------------------------------------------------------------------------------------------");
			if ("顺序抽取".equals(config_shunxuOrramoder)) {

				/* 判断是否需要隔日 */
				boolean flag = mTaskAttribute.isTaskNextDayFlag();
				if (flag) { /* 需要隔日 */
					/* 1、判断是否需要隔日递减 */
					boolean NextDayVisitDeclineFlag = mTaskAttribute.isTaskNextDayVisitDeclineFlag();
					if (!NextDayVisitDeclineFlag) {/* 不需要隔日递减 */
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval();// 隔日天数
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();// 隔日次数
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();
						mNewBases_Geri.clear();

						if (TaskNextDayVisitIntervalCount != 0) {
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {

								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								try {
									mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;

								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;

								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}

								mTmpBases_Geri.clear();
							}
						} else {
							// 为0
							System.out.println("???????????????????");
							for (int i = 1; i < mPostion; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;

								try {
									mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;
								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;
								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
							}
						}
					} else {
						mNewBases_Geri.clear();
						double TaskNextDayVisitDecilneMin = mTaskAttribute.getTaskNextDayVisitDecilneMin(); // 隔日中递减最小值
						double NextDayVisitDecilneRatio = mTaskAttribute.getTaskNextDayVisitDecilneRatio(); // 隔日中哦过递减比率
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();// 隔日中的回访比率
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval(); // 故而诶间隔天数
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();//
						double tmp_bilv;
						int s = 0;
						int ll = 0;

						if (TaskNextDayVisitIntervalCount != 0) {
							// 根据隔日次数来进行取数据
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								// 包含当前文件的数据
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}

								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;

								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;

								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
								ll++;
							}
						} else {
							// 为0
							System.out.println("???????????????????");
							int d = 1;
							while (true) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * d;
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								if (index < 0) {
									break;
								}
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;
								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;
								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
								ll++;
								d++;
							}
						}
					}
				}

				/* 普通回访 */
				/* 1、普通回访是否需要递减 */
				boolean DeclineFlag = mTaskAttribute.isTaskDeclineFlag();
				if (!DeclineFlag) {// 普通不需要递减
									// //System.out.println("------------------------------------");
					mNewBases.clear();
					// try {
					// mNewBases = Util.readcurray(path,
					// mListDataFile.get(mPostion).getTask_date_file());
					// } catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					double TaskReturnratio = mTaskAttribute.getTaskReturnratio();
					PhoneDataBean mPhoneDataBean = null;
					// for (int i = 0; i < arg2; i++)
					// 802 for (int i = mPostion - 1; i > 0; i--)
					for (int i = mPostion - 1; i >= 0; i--) {
						// //System.out.println("802");
						try {
							mTmpBases = Util.readIsDijianShunxu(path, mListDataFile.get(i).getTask_date_file(), TaskReturnratio);
							System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// 去重复
						int data_index[] = new int[mTmpBases.size()];
						int n = mTmpBases.size();
						boolean[] bool = new boolean[n];
						int randInt = 0;
						for (int j = 0; j < mTmpBases.size(); j++) {
							do {
								randInt = mRandom.nextInt(mTmpBases.size());
							} while (bool[randInt]);
							bool[randInt] = true;
							data_index[j] = randInt;

						}
						for (int j = 0; j < data_index.length; j++) {
							mNewBases.add(mTmpBases.get(data_index[j]));
						}
						mTmpBases.clear();
					}
				} else {/* 普通需要递减 */
					mNewBases.clear();
					// try {
					// mNewBases = Util.readcurray(path,
					// mListDataFile.get(mPostion).getTask_date_file());
					// } catch (Exception e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					double TaskDecilneMin = mTaskAttribute.getTaskDecilneMin();
					double TaskDecilneRatio = mTaskAttribute.getTaskDecilneRatio();
					double TaskReturnratio = mTaskAttribute.getTaskReturnratio();
					double tmp_bilv;
					int ll = 0;
					PhoneDataBean mPhoneDataBean = null;
					// for (int i = 0; i < arg2; i++)
					for (int i = mPostion - 1; i >= 0; i--) {
						tmp_bilv = TaskReturnratio - TaskDecilneRatio * ll;
						if (tmp_bilv <= TaskDecilneMin) {
							tmp_bilv = TaskDecilneMin;
							try {
								mTmpBases = Util.readIsDijianShunxu(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
								System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							try {
								mTmpBases = Util.readIsDijianShunxu(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
								System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							tmp_bilv = tmp_bilv - TaskDecilneRatio;
						}

						// 去重复
						int data_index[] = new int[mTmpBases.size()];
						int n = mTmpBases.size();
						boolean[] bool = new boolean[n];
						int randInt = 0;

						for (int j = 0; j < mTmpBases.size(); j++) {
							do {
								randInt = mRandom.nextInt(mTmpBases.size());
							} while (bool[randInt]);
							bool[randInt] = true;
							data_index[j] = randInt;

						}
						for (int j = 0; j < data_index.length; j++) {
							mNewBases.add(mTmpBases.get(data_index[j]));
						}
						mTmpBases.clear();
						ll++;
					}
				}

				/* 数据合并 */
				// 组拼数据
				mDataSum.clear();
				if (mNewBases_Geri != null) {

					mDataSum.addAll(mNewBases);
					mDataSum.addAll(mNewBases_Geri);
				} else {
					mDataSum.addAll(mNewBases);

				}
				System.out.println("隔日读取数据和普通读取数据合并个数：" + mDataSum.size());

			}
			if ("随机抽取".equals(config_shunxuOrramoder)) {

				/* 判断是否需要隔日 */
				boolean flag = mTaskAttribute.isTaskNextDayFlag();
				if (flag) { /* 需要隔日 */
					/* 1、判断是否需要隔日递减 */
					boolean NextDayVisitDeclineFlag = mTaskAttribute.isTaskNextDayVisitDeclineFlag();
					if (!NextDayVisitDeclineFlag) {/* 不需要隔日递减 */
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval();// 隔日天数
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();// 隔日次数
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();
						mNewBases_Geri.clear();
						if (TaskNextDayVisitIntervalCount != 0) {
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {

								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("----------------i----------" + i);
								System.out.println("-------------------index---" + index);
								try {
									mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;

								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;
								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
							}
						} else {
							// 为0
							// //System.out.println("???????????????????");
							for (int i = 1; i < mPostion; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								try {
									mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;
								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;
								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
							}
						}

					} else {
						mNewBases_Geri.clear();
						double TaskNextDayVisitDecilneMin = mTaskAttribute.getTaskNextDayVisitDecilneMin(); // 隔日中递减最小值
						double NextDayVisitDecilneRatio = mTaskAttribute.getTaskNextDayVisitDecilneRatio(); // 隔日中哦过递减比率
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();// 隔日中的回访比率
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval(); // 故而诶间隔天数
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();//
						double tmp_bilv;
						int s = 0;
						int ll = 0;
						if (TaskNextDayVisitIntervalCount != 0) {
							// 根据隔日次数来进行取数据
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								// 包含当前文件的数据
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										System.out.println(e.getMessage());
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										System.out.println(e.getMessage());
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}

								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;

								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;

								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
								ll++;
							}
						} else {
							// 为0
							// //System.out.println("???????????????????");
							int d = 1;
							while (true) { // for (int i = mPostion; i > 0; i--)
											// {//for (int i = mPostion - 1; i >
											// 0; i--) { //for (int i = 0; i
											// <mPostion; i++) { //for (int i =
											// mPostion - 1; i >= 0; i--) {
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * d;
								if (index < 0) {
									break;
								}
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										System.out.println(e.getMessage());
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("隔日文件名：" + mListDataFile.get(index).getTask_date_file() + "\t抽取个数：" + mTmpBases_Geri.size());
									} catch (Exception e) {
										System.out.println(e.getMessage());
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}
								// 去重复
								int data_index[] = new int[mTmpBases_Geri.size()];
								int n = mTmpBases_Geri.size();
								boolean[] bool = new boolean[n];
								int randInt = 0;
								for (int j = 0; j < mTmpBases_Geri.size(); j++) {
									do {
										randInt = mRandom.nextInt(mTmpBases_Geri.size());
									} while (bool[randInt]);
									bool[randInt] = true;
									data_index[j] = randInt;
								}
								for (int j = 0; j < data_index.length; j++) {
									mNewBases_Geri.add(mTmpBases_Geri.get(data_index[j]));
								}
								mTmpBases_Geri.clear();
								ll++;
								d++;
							}
						}

					}
				}

				/* 普通回访 */
				/* 1、普通回访是否需要递减 */
				boolean DeclineFlag = mTaskAttribute.isTaskDeclineFlag();
				if (!DeclineFlag) {
					// //System.out.println("------------------------------------");
					mNewBases.clear();
					// try {
					// mNewBases = Util.readcurray(path,
					// mListDataFile.get(mPostion).getTask_date_file());
					// } catch (Exception e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					double TaskReturnratio = mTaskAttribute.getTaskReturnratio();
					PhoneDataBean mPhoneDataBean = null;
					// for (int i = 0; i < arg2; i++)
					// 802 for (int i = mPostion - 1; i > 0; i--)
					for (int i = mPostion - 1; i >= 0; i--) {
						// //System.out.println("802");
						try {
							mTmpBases = Util.readIsDijianRandom(path, mListDataFile.get(i).getTask_date_file(), TaskReturnratio);
							System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println(e.getMessage());
						}
						// 去重复
						int data_index[] = new int[mTmpBases.size()];
						int n = mTmpBases.size();
						boolean[] bool = new boolean[n];
						int randInt = 0;
						for (int j = 0; j < mTmpBases.size(); j++) {
							do {
								randInt = mRandom.nextInt(mTmpBases.size());
							} while (bool[randInt]);
							bool[randInt] = true;
							data_index[j] = randInt;

						}
						for (int j = 0; j < data_index.length; j++) {
							mNewBases.add(mTmpBases.get(data_index[j]));
						}
						mTmpBases.clear();
					}
				} else {/* 普通需要递减 */
					mNewBases.clear();

					double TaskDecilneMin = mTaskAttribute.getTaskDecilneMin();
					double TaskDecilneRatio = mTaskAttribute.getTaskDecilneRatio();
					double TaskReturnratio = mTaskAttribute.getTaskReturnratio();
					double tmp_bilv;
					int ll = 0;
					PhoneDataBean mPhoneDataBean = null;
					// for (int i = 0; i < arg2; i++)
					for (int i = mPostion - 1; i >= 0; i--) {
						tmp_bilv = TaskReturnratio - TaskDecilneRatio * ll;
						if (tmp_bilv <= TaskDecilneMin) {
							tmp_bilv = TaskDecilneMin;
							try {
								mTmpBases = Util.readIsDijianRandom(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
								System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								System.out.println(e.getMessage());
							}
						} else {
							try {
								mTmpBases = Util.readIsDijianRandom(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
								System.out.println("文件名：" + mListDataFile.get(i).getTask_date_file() + "\t抽取个数：" + mTmpBases.size());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								System.out.println(e.getMessage());
							}
							tmp_bilv = tmp_bilv - TaskDecilneRatio;
						}

						// 去重复
						int data_index[] = new int[mTmpBases.size()];
						int n = mTmpBases.size();
						boolean[] bool = new boolean[n];
						int randInt = 0;

						for (int j = 0; j < mTmpBases.size(); j++) {
							do {
								randInt = mRandom.nextInt(mTmpBases.size());
							} while (bool[randInt]);
							bool[randInt] = true;
							data_index[j] = randInt;

						}
						for (int j = 0; j < data_index.length; j++) {
							mNewBases.add(mTmpBases.get(data_index[j]));
						}
						mTmpBases.clear();
						ll++;
					}
				}

				/* 数据合并 */
				// 组拼数据
				mDataSum.clear();
				if (mNewBases_Geri != null) {

					mDataSum.addAll(mNewBases);
					mDataSum.addAll(mNewBases_Geri);
				} else {
					mDataSum.addAll(mNewBases);

				}
				System.out.println("隔日读取数据和普通读取数据合并个数：" + mDataSum.size());

			}

			// 创建文件
			/* 将新集合打乱 */
			// 去重复
			mDataSum_To_File.clear();
			int data_index[] = new int[mDataSum.size()];
			int n = mDataSum.size();
			boolean[] bool = new boolean[n];
			int randInt = 0;

			for (int j = 0; j < mDataSum.size(); j++) {
				do {
					randInt = mRandom.nextInt(mDataSum.size());
				} while (bool[randInt]);
				bool[randInt] = true;
				data_index[j] = randInt;

			}
			for (int j = 0; j < data_index.length; j++) {
				mDataSum_To_File.add(mDataSum.get(data_index[j]));
			}
			// 创建数据文件
			Gson mGson = new Gson();
			String fileName = mListDataFile.get(mPostion).getTask_date_file().toString();
			String a[] = fileName.split("-");
			a[0] = mDataSum.size() + "";
			String temp = "只回访不新增" + "-" + a[0] + "-" + a[1] + "-" + a[2];

			long isCurl = DBMgr.getInstance(ActivityTaskCurrfile.this).findCurl(temp);
			if (isCurl > 0) {
				// Toast.makeText(CreateFileActivity.this,
				// "当日文件以创建，请不要重复创建单日文件", 1).show();
			} else {
				// 添加数据表
				String create_state = Util.createRandomcodeFile(ActivityTaskCurrfile.this, mGson.toJson(mDataSum), path, temp);
				ContentValues mValues = new ContentValues();
				mValues.put(TaskCurlFile.TASK_NAME, mTaskAttribute.getTaskName());
				mValues.put(TaskCurlFile.TASK_CURL_FILE, temp);
				DBMgr.getInstance(ActivityTaskCurrfile.this).add_culr_file(mValues);

			}
		}
	}

	private class CurlFileTask extends AsyncTask<Void, Void, Void>
	{
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(ActivityTaskCurrfile.this);
			mProgressDialog.setMessage("正在创建文件！");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			// TODO Auto-generated method stub
			try {
				createfile();
			} catch (Exception e) {
				// TODO Auto-generated catch block

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			ActivityTaskCurrfile.this.finish();
		}

	}

	public void createfile_0625()
	{

	}

}
