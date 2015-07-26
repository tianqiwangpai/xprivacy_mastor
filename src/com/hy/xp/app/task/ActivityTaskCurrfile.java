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
 * ��������
 * 
 * @author Administrator
 */
public class ActivityTaskCurrfile extends ActivityBase
{
	private ListView mListViewDatafile;
	private String[] array = { "˳���ȡ", "�����ȡ" };
	private String config_shunxuOrramoder = "˳���ȡ";
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
			Toast.makeText(ActivityTaskCurrfile.this, "��ѡ������", 1).show();
			finish();
			return;
		}

		String tn = extras.getString(XpHelper.Tn);
		int d = extras.getInt("index");
		PreferenceUtils.setParam(ActivityTaskCurrfile.this, "position", d);
		// 1����ȡ�����ļ�
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
		// ��ȡ�����������
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
		alertDialogBuilder.setTitle("ȷ�ϲ�����");
		alertDialogBuilder.setMessage("ȷ�������ò�����");
		alertDialogBuilder.setPositiveButton("ȷ��", new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				ActivityTaskCurrfile.this.finish();
			}
		});
		alertDialogBuilder.setNegativeButton("ȡ��", new OnClickListener()
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
			if ("˳���ȡ".equals(config_shunxuOrramoder)) {
				/* �ж��Ƿ���Ҫ���� */
				boolean flag = mTaskAttribute.isTaskNextDayFlag();
				if (flag) { /* ��Ҫ���� */
					/* 1���ж��Ƿ���Ҫ���յݼ� */
					boolean NextDayVisitDeclineFlag = mTaskAttribute.isTaskNextDayVisitDeclineFlag();
					if (!NextDayVisitDeclineFlag) { /* ����Ҫ���յݼ� */
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval();// ��������
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();// ���մ���
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();
						mNewBases_Geri.clear();
						// ������Ϊ0
						// ���ݵ�ǰ����ͼ���������������
						if (TaskNextDayVisitIntervalCount != 0) {
							int sssss = TaskNextDayVisitIntervalCount + 1;
							// System.out.println("---------------sssss----------"
							// + sssss);
							for (int i = 1; i < sssss; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("----------------i----------" + i);
								try {
									mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
								// ȥ�ظ�
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
							// Ϊ��
							System.out.println("???????????????????");
							int d = 1;
							while ((mPostion - ((TaskNextDayVisitInterval + 1)) * d) > 0) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * d;
								try {
									mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
								// ȥ�ظ�
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
						double TaskNextDayVisitDecilneMin = mTaskAttribute.getTaskNextDayVisitDecilneMin(); // �����еݼ���Сֵ
						double NextDayVisitDecilneRatio = mTaskAttribute.getTaskNextDayVisitDecilneRatio(); // ������Ŷ���ݼ�����
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();// �����еĻطñ���
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval(); // �ʶ����������
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();//
						double tmp_bilv;
						int ll = 0;
						int s = 0;
						if (TaskNextDayVisitIntervalCount != 0) {
							// ���ݸ��մ���������ȡ����
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								// ������ǰ�ļ�������
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}
								// ȥ�ظ�
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
							// Ϊ0\\\\\\\\\\\\\\\\\\\\\
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
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}
								// ȥ�ظ�
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

				/* ��ͨ�ط� */
				/* 1����ͨ�ط��Ƿ���Ҫ�ݼ� */
				boolean DeclineFlag = mTaskAttribute.isTaskDeclineFlag();
				if (!DeclineFlag) {// ��ͨ����Ҫ�ݼ�
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
							System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// ȥ�ظ�
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
				} else {/* ��ͨ��Ҫ�ݼ� */
					// ////System.out.println("��Ҫ�ݼ�");
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
							System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
						} else {
							mTmpBases = Util.readIsDijianShunxu(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
							System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
							tmp_bilv = tmp_bilv - TaskDecilneRatio;
						}

						// ȥ�ظ�
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

				/* ���ݺϲ� */
				// ��ƴ����
				mDataSum.clear();
				if (mNewBases_Geri != null) {

					mDataSum.addAll(mNewBases);
					mDataSum.addAll(mNewBases_Geri);
				} else {
					mDataSum.addAll(mNewBases);

				}
				System.out.println("���ն�ȡ���ݺ���ͨ��ȡ���ݺϲ�������" + mDataSum.size());

			}
			if ("�����ȡ".equals(config_shunxuOrramoder)) {

				/* �ж��Ƿ���Ҫ���� */
				boolean flag = mTaskAttribute.isTaskNextDayFlag();
				if (flag) { /* ��Ҫ���� */
					/* 1���ж��Ƿ���Ҫ���յݼ� */
					boolean NextDayVisitDeclineFlag = mTaskAttribute.isTaskNextDayVisitDeclineFlag();
					if (!NextDayVisitDeclineFlag) {/* ����Ҫ���յݼ� */
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval();// ��������
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();// ���մ���
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();
						mNewBases_Geri.clear();
						if (TaskNextDayVisitIntervalCount != 0) {
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								try {
									mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// ȥ�ظ�
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
							// Ϊ0
							// System.out.println("???????????????????");
							for (int i = 1; i < mPostion; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								try {
									mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
								// ȥ�ظ�
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
						double TaskNextDayVisitDecilneMin = mTaskAttribute.getTaskNextDayVisitDecilneMin(); // �����еݼ���Сֵ
						double NextDayVisitDecilneRatio = mTaskAttribute.getTaskNextDayVisitDecilneRatio(); // ������Ŷ���ݼ�����
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();// �����еĻطñ���
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval(); // �ʶ����������
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();//
						double tmp_bilv;
						int s = 0;
						int ll = 0;
						if (TaskNextDayVisitIntervalCount != 0) {
							// ���ݸ��մ���������ȡ����
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {

								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								// ������ǰ�ļ�������
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}

								// ȥ�ظ�
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
							// Ϊ0
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
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}
								// ȥ�ظ�
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

				/* ��ͨ�ط� */
				/* 1����ͨ�ط��Ƿ���Ҫ�ݼ� */
				boolean DeclineFlag = mTaskAttribute.isTaskDeclineFlag();
				if (!DeclineFlag) {// ��ͨ����Ҫ�ݼ�
					System.out.println("-----��ͨ����Ҫ�ݼ�-------------------------------");
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
							System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
						} catch (Exception e) {
							e.printStackTrace();
						}
						// ȥ�ظ�
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
				} else {/* ��ͨ��Ҫ�ݼ� */
					mNewBases.clear();
					System.out.println("��ͨ��Ҫ�ݼ�-------------------");
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
								System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							try {
								mTmpBases = Util.readIsDijianRandom(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
								System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							tmp_bilv = tmp_bilv - TaskDecilneRatio;
						}

						// ȥ�ظ�
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

				/* ���ݺϲ� */
				// ��ƴ����
				mDataSum.clear();
				if (mNewBases_Geri != null) {

					mDataSum.addAll(mNewBases);
					mDataSum.addAll(mNewBases_Geri);
				} else {
					mDataSum.addAll(mNewBases);

				}
				System.out.println("���ն�ȡ���ݺ���ͨ��ȡ���ݺϲ�������" + mDataSum.size());

			}
			/* ���¼��ϴ��� */
			// ȥ�ظ�
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

			// �����ļ�
			Gson mGson = new Gson();
			String fileName = mListDataFile.get(mPostion).getTask_date_file().toString();
			String a[] = fileName.split("-");
			a[0] = mDataSum_To_File.size() + "";
			String temp = "ִ���ļ�" + "-" + a[0] + "-" + a[1] + "-" + a[2];

			long isCurl = DBMgr.getInstance(ActivityTaskCurrfile.this).findCurl(temp);
			if (isCurl > 0) {
				// Toast.makeText(ActivityTaskCurrfile.this,
				// "�����ļ��Դ������벻Ҫ�ظ����������ļ�", 1).show();
			} else {
				// ������ݱ�
				String create_state = Util.createRandomcodeFile(ActivityTaskCurrfile.this, mGson.toJson(mDataSum_To_File), path, temp);
				ContentValues mValues = new ContentValues();
				mValues.put(TaskCurlFile.TASK_NAME, mTaskAttribute.getTaskName());
				mValues.put(TaskCurlFile.TASK_CURL_FILE, temp);
				DBMgr.getInstance(ActivityTaskCurrfile.this).add_culr_file(mValues);

			}
		} else {

			System.out.println("xuanzhong-------------------------------------------------------------------------------------------------------");
			if ("˳���ȡ".equals(config_shunxuOrramoder)) {

				/* �ж��Ƿ���Ҫ���� */
				boolean flag = mTaskAttribute.isTaskNextDayFlag();
				if (flag) { /* ��Ҫ���� */
					/* 1���ж��Ƿ���Ҫ���յݼ� */
					boolean NextDayVisitDeclineFlag = mTaskAttribute.isTaskNextDayVisitDeclineFlag();
					if (!NextDayVisitDeclineFlag) {/* ����Ҫ���յݼ� */
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval();// ��������
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();// ���մ���
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();
						mNewBases_Geri.clear();

						if (TaskNextDayVisitIntervalCount != 0) {
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {

								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								try {
									mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// ȥ�ظ�
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
							// Ϊ0
							System.out.println("???????????????????");
							for (int i = 1; i < mPostion; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;

								try {
									mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
								// ȥ�ظ�
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
						double TaskNextDayVisitDecilneMin = mTaskAttribute.getTaskNextDayVisitDecilneMin(); // �����еݼ���Сֵ
						double NextDayVisitDecilneRatio = mTaskAttribute.getTaskNextDayVisitDecilneRatio(); // ������Ŷ���ݼ�����
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();// �����еĻطñ���
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval(); // �ʶ����������
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();//
						double tmp_bilv;
						int s = 0;
						int ll = 0;

						if (TaskNextDayVisitIntervalCount != 0) {
							// ���ݸ��մ���������ȡ����
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								// ������ǰ�ļ�������
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}

								// ȥ�ظ�
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
							// Ϊ0
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
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianShunxu(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										e.printStackTrace();
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}
								// ȥ�ظ�
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

				/* ��ͨ�ط� */
				/* 1����ͨ�ط��Ƿ���Ҫ�ݼ� */
				boolean DeclineFlag = mTaskAttribute.isTaskDeclineFlag();
				if (!DeclineFlag) {// ��ͨ����Ҫ�ݼ�
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
							System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// ȥ�ظ�
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
				} else {/* ��ͨ��Ҫ�ݼ� */
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
								System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							try {
								mTmpBases = Util.readIsDijianShunxu(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
								System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							tmp_bilv = tmp_bilv - TaskDecilneRatio;
						}

						// ȥ�ظ�
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

				/* ���ݺϲ� */
				// ��ƴ����
				mDataSum.clear();
				if (mNewBases_Geri != null) {

					mDataSum.addAll(mNewBases);
					mDataSum.addAll(mNewBases_Geri);
				} else {
					mDataSum.addAll(mNewBases);

				}
				System.out.println("���ն�ȡ���ݺ���ͨ��ȡ���ݺϲ�������" + mDataSum.size());

			}
			if ("�����ȡ".equals(config_shunxuOrramoder)) {

				/* �ж��Ƿ���Ҫ���� */
				boolean flag = mTaskAttribute.isTaskNextDayFlag();
				if (flag) { /* ��Ҫ���� */
					/* 1���ж��Ƿ���Ҫ���յݼ� */
					boolean NextDayVisitDeclineFlag = mTaskAttribute.isTaskNextDayVisitDeclineFlag();
					if (!NextDayVisitDeclineFlag) {/* ����Ҫ���յݼ� */
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval();// ��������
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();// ���մ���
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();
						mNewBases_Geri.clear();
						if (TaskNextDayVisitIntervalCount != 0) {
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {

								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("----------------i----------" + i);
								System.out.println("-------------------index---" + index);
								try {
									mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// ȥ�ظ�
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
							// Ϊ0
							// //System.out.println("???????????????????");
							for (int i = 1; i < mPostion; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								try {
									mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), TaskNextDayVisitIntervalReturnRatio);
									System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
								// ȥ�ظ�
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
						double TaskNextDayVisitDecilneMin = mTaskAttribute.getTaskNextDayVisitDecilneMin(); // �����еݼ���Сֵ
						double NextDayVisitDecilneRatio = mTaskAttribute.getTaskNextDayVisitDecilneRatio(); // ������Ŷ���ݼ�����
						double TaskNextDayVisitIntervalReturnRatio = mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio();// �����еĻطñ���
						int TaskNextDayVisitInterval = mTaskAttribute.getTaskNextDayVisitInterval(); // �ʶ����������
						int TaskNextDayVisitIntervalCount = mTaskAttribute.getTaskNextDayVisitIntervalCount();//
						double tmp_bilv;
						int s = 0;
						int ll = 0;
						if (TaskNextDayVisitIntervalCount != 0) {
							// ���ݸ��մ���������ȡ����
							for (int i = 1; i < TaskNextDayVisitIntervalCount + 1; i++) {
								int index = mPostion - ((TaskNextDayVisitInterval + 1)) * i;
								System.out.println("i=\t" + i);
								System.out.println("index=\t" + index);
								// ������ǰ�ļ�������
								tmp_bilv = TaskNextDayVisitIntervalReturnRatio - NextDayVisitDecilneRatio * ll;
								if (tmp_bilv <= TaskNextDayVisitDecilneMin) {
									tmp_bilv = TaskNextDayVisitDecilneMin;
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										System.out.println(e.getMessage());
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										System.out.println(e.getMessage());
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}

								// ȥ�ظ�
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
							// Ϊ0
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
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										System.out.println(e.getMessage());
									}
								} else {
									try {
										mTmpBases_Geri = Util.readIsDijianRandom(path, mListDataFile.get(index).getTask_date_file(), tmp_bilv);
										System.out.println("�����ļ�����" + mListDataFile.get(index).getTask_date_file() + "\t��ȡ������" + mTmpBases_Geri.size());
									} catch (Exception e) {
										System.out.println(e.getMessage());
									}
									tmp_bilv = tmp_bilv - NextDayVisitDecilneRatio;
								}
								// ȥ�ظ�
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

				/* ��ͨ�ط� */
				/* 1����ͨ�ط��Ƿ���Ҫ�ݼ� */
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
							System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println(e.getMessage());
						}
						// ȥ�ظ�
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
				} else {/* ��ͨ��Ҫ�ݼ� */
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
								System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								System.out.println(e.getMessage());
							}
						} else {
							try {
								mTmpBases = Util.readIsDijianRandom(path, mListDataFile.get(i).getTask_date_file(), tmp_bilv);
								System.out.println("�ļ�����" + mListDataFile.get(i).getTask_date_file() + "\t��ȡ������" + mTmpBases.size());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								System.out.println(e.getMessage());
							}
							tmp_bilv = tmp_bilv - TaskDecilneRatio;
						}

						// ȥ�ظ�
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

				/* ���ݺϲ� */
				// ��ƴ����
				mDataSum.clear();
				if (mNewBases_Geri != null) {

					mDataSum.addAll(mNewBases);
					mDataSum.addAll(mNewBases_Geri);
				} else {
					mDataSum.addAll(mNewBases);

				}
				System.out.println("���ն�ȡ���ݺ���ͨ��ȡ���ݺϲ�������" + mDataSum.size());

			}

			// �����ļ�
			/* ���¼��ϴ��� */
			// ȥ�ظ�
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
			// ���������ļ�
			Gson mGson = new Gson();
			String fileName = mListDataFile.get(mPostion).getTask_date_file().toString();
			String a[] = fileName.split("-");
			a[0] = mDataSum.size() + "";
			String temp = "ֻ�طò�����" + "-" + a[0] + "-" + a[1] + "-" + a[2];

			long isCurl = DBMgr.getInstance(ActivityTaskCurrfile.this).findCurl(temp);
			if (isCurl > 0) {
				// Toast.makeText(CreateFileActivity.this,
				// "�����ļ��Դ������벻Ҫ�ظ����������ļ�", 1).show();
			} else {
				// ������ݱ�
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
			mProgressDialog.setMessage("���ڴ����ļ���");
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
