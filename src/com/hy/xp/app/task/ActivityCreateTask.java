package com.hy.xp.app.task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hy.xp.app.ActivityBase;
import com.hy.xp.app.PrivacyManager;
import com.hy.xp.app.PrivacyService;
import com.hy.xp.app.R;

/**
 * 创建任务对话框
 * 
 * @author Administrator
 */
public class ActivityCreateTask extends ActivityBase {
	static String spinnerdata = "";
	static ProgressDialog mProgressDialog;
	List<xpmodel> xpmodelsList;
	private Random mRandom = new Random();

	private static ExecutorService mExecutor = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors(),
			new PriorityThreadFactory());

	CheckBox cbTaskDeclineFlag;
	CheckBox cbTaskNextDayFlag;
	CheckBox cbTaskNextDayVisitDeclineFlag;
	CheckBox cbTaskNextMonthVisitDeclineFlag;
	CheckBox cbTaskNextWeekVisitDeclineFlag;

	Spinner spOperator;
	Spinner spTaskNextDayStayWay;
	Spinner spTaskNextMonthStayWay;
	Spinner spTaskNextWeekStayWay;
	Spinner spTaskStayWay;

	EditText etAdvertisementID;
	EditText etAltitude;
	EditText etAndroidCode;
	EditText etAndroidID;
	EditText etCountry;
	EditText etDataType;
	EditText etGsfId;
	EditText etGsmCallID;
	EditText etGsmLac;
	EditText etIMSI;
	EditText etIccId;
	EditText etImei;
	EditText etIpAddress;
	EditText etLatitude;
	EditText etLongitude;
	EditText etMacAddress;
	EditText etManufacturet;
	EditText etMcc;
	EditText etMnc;
	EditText etModel;
	EditText etPhoneNumber;
	EditText etProduct;
	EditText etSSID;
	EditText etSerial;
	EditText etSystemCode;
	EditText etTaskDecilneMin;
	EditText etTaskDecilneRatio;
	EditText etTaskDesc;
	EditText etTaskName;
	EditText etTaskNewdata;
	EditText etTaskNextDayVisitDecilneMin;
	EditText etTaskNextDayVisitDecilneRatio;
	EditText etTaskNextDayVisitInterval;
	EditText etTaskNextDayVisitIntervalCount;
	EditText etTaskNextDayVisitIntervalReturnRatio;
	EditText etTaskNextMonthVisitDecilneMin;
	EditText etTaskNextMonthVisitDecilneRatio;
	EditText etTaskNextMonthVisitIntervalReturnRatio;
	EditText etTaskNextWeekVisitDecilneMin;
	EditText etTaskNextWeekVisitDecilneRatio;
	EditText etTaskNextWeekVisitIntervalReturnRatio;
	EditText etTaskNumber;
	EditText etTaskReturnratio;
	EditText etTaskSecondActiveF;
	EditText etTaskSecondActiveS;
	EditText etUa;

	static int spinnerstayway = 0;
	static int spinnernextdaystayway = 0;
	static int spinnernextweekstayway = 0;
	static int spinnernextmonthstayway = 0;

	// 敞亮
	public static Random r = new Random();
	List<PhoneDataBean> mDataBeanLists = new ArrayList<PhoneDataBean>();
	List<PhoneDataBean> mDataBeanListsTmp = new ArrayList<PhoneDataBean>();

	private static class PriorityThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_create);
		init();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (inflater != null && PrivacyService.checkClient()) {
			inflater.inflate(R.menu.settings, menu);
			return true;
		} else
			return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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

	private void optionCancel() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ActivityCreateTask.this);
		alertDialogBuilder.setTitle("确认操作？");
		alertDialogBuilder.setMessage("确定放弃该操作码");
		alertDialogBuilder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				ActivityCreateTask.this.finish();
			}
		});
		alertDialogBuilder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		// Show dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void optionSave() {
		// TODO Auto-generated method stub
		if ("".equals(etTaskName.getText().toString().trim())
				|| etTaskName.getText().toString().trim() == null) {
			Toast.makeText(ActivityCreateTask.this, "任务名称不能为空！",
					Toast.LENGTH_SHORT).show();
			etTaskName.requestFocus();// 获取焦点
			return;
		}
		if ("".equals(etTaskDesc.getText().toString().trim())
				|| etTaskDesc.getText().toString().trim() == null) {
			Toast.makeText(ActivityCreateTask.this, "任务说明不能为空！",
					Toast.LENGTH_SHORT).show();
			etTaskDesc.requestFocus();// 获取焦点
			return;
		}
		if ("".equals(etTaskNewdata.getText().toString().trim())
				|| etTaskNewdata.getText().toString().trim() == null) {
			Toast.makeText(ActivityCreateTask.this, "新增数据不能为空！",
					Toast.LENGTH_SHORT).show();
			etTaskNewdata.requestFocus();// 获取焦点
			return;
		}
		if ("".equals(etTaskNumber.getText().toString().trim())
				|| etTaskNumber.getText().toString().trim() == null) {
			Toast.makeText(ActivityCreateTask.this, "任务天数不能为空！",
					Toast.LENGTH_SHORT).show();
			etTaskNumber.requestFocus();// 获取焦点
			return;
		}
		if ("".equals(etTaskReturnratio.getText().toString().trim())
				|| etTaskReturnratio.getText().toString().trim() == null) {
			Toast.makeText(ActivityCreateTask.this, "回访比例不能为空！",
					Toast.LENGTH_SHORT).show();
			etTaskReturnratio.requestFocus();// 获取焦点
			return;
		}
		// 数据监测结束

		xpmodelsList = DBMgr.getInstance(ActivityCreateTask.this)
				.getXpmpdellist();

		// 执行异步任务
		Add_TaskAsyncTask mAsyncTask = new Add_TaskAsyncTask();
		mAsyncTask.executeOnExecutor(mExecutor, null);
	}

	public void init() {

		// find by view id
		ArrayAdapter<String> mStaywayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] { "随机抽取",
						"固定抽取" });

		// 任务属性组件
		etTaskName = (EditText) findViewById(R.id.et_task_name);
		etTaskDesc = (EditText) findViewById(R.id.et_task_desc);
		etTaskNewdata = (EditText) findViewById(R.id.et_task_newdata);
		etTaskNumber = (EditText) findViewById(R.id.et_task_number);
		etTaskReturnratio = (EditText) findViewById(R.id.et_task_return_ratio);
		cbTaskDeclineFlag = (CheckBox) findViewById(R.id.cb_task_flag_decline_flag);

		spTaskStayWay = (Spinner) findViewById(R.id.et_task_stay_way);
		spTaskStayWay.setAdapter(mStaywayAdapter);
		spTaskStayWay.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				spinnerstayway = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		etTaskDecilneRatio = (EditText) findViewById(R.id.et_task_decilne_ratio);
		etTaskDecilneMin = (EditText) findViewById(R.id.et_task_decilne_min);
		cbTaskNextDayFlag = (CheckBox) findViewById(R.id.cb_task_flag_next_day_visit_flag);
		etTaskNextDayVisitInterval = (EditText) findViewById(R.id.et_task_next_day_visit_interval);
		etTaskNextDayVisitIntervalReturnRatio = (EditText) findViewById(R.id.et_task_next_day_visit_interval_return_ratio);
		etTaskNextDayVisitIntervalCount = (EditText) findViewById(R.id.et_task_next_day_visit_interval_count);
		spTaskNextDayStayWay = (Spinner) findViewById(R.id.et_task_next_day_visit_stay_way);
		cbTaskNextDayVisitDeclineFlag = (CheckBox) findViewById(R.id.cb_task_next_day_visit_flag_decline_yes);
		etTaskNextDayVisitDecilneRatio = (EditText) findViewById(R.id.et_task_next_day_visit_decilne_ratio);
		etTaskNextDayVisitDecilneMin = (EditText) findViewById(R.id.et_task_next_day_visit_decilne_min);

		spTaskNextDayStayWay.setAdapter(mStaywayAdapter);
		spTaskNextDayStayWay
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						spinnernextdaystayway = position;
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		etTaskNextWeekVisitIntervalReturnRatio = (EditText) findViewById(R.id.et_task_next_week_visit_interval_return_ratio);
		spTaskNextWeekStayWay = (Spinner) findViewById(R.id.et_task_next_week_visit_stay_way);
		cbTaskNextWeekVisitDeclineFlag = (CheckBox) findViewById(R.id.cb_task_next_week_visit_flag_decline_yes);
		etTaskNextWeekVisitDecilneRatio = (EditText) findViewById(R.id.et_task_next_week_visit_decilne_ratio);
		etTaskNextWeekVisitDecilneMin = (EditText) findViewById(R.id.et_task_next_week_visit_decilne_min);

		spTaskNextWeekStayWay.setAdapter(mStaywayAdapter);
		spTaskNextWeekStayWay
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						spinnernextweekstayway = position;
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		etTaskNextMonthVisitIntervalReturnRatio = (EditText) findViewById(R.id.et_task_next_month_visit_interval_return_ratio);
		spTaskNextMonthStayWay = (Spinner) findViewById(R.id.et_task_next_month_visit_stay_way);
		cbTaskNextMonthVisitDeclineFlag = (CheckBox) findViewById(R.id.cb_task_next_month_visit_flag_decline_yes);
		etTaskNextMonthVisitDecilneRatio = (EditText) findViewById(R.id.et_task_next_month_visit_decilne_ratio);
		etTaskNextMonthVisitDecilneMin = (EditText) findViewById(R.id.et_task_next_month_visit_decilne_min);

		spTaskNextMonthStayWay.setAdapter(mStaywayAdapter);
		spTaskNextMonthStayWay
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						spinnernextmonthstayway = position;
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		etTaskSecondActiveF = (EditText) findViewById(R.id.et_task_second_active_f);
		etTaskSecondActiveS = (EditText) findViewById(R.id.et_task_second_active_s);

		// 数据模板组件
		etSerial = (EditText) findViewById(R.id.et_datafile_serial);
		etLatitude = (EditText) findViewById(R.id.et_datafile_latitude);
		etLongitude = (EditText) findViewById(R.id.et_datafile_longitude);
		etAltitude = (EditText) findViewById(R.id.et_datafile_altitude);
		etMacAddress = (EditText) findViewById(R.id.et_datafile_macAddress);
		etIpAddress = (EditText) findViewById(R.id.et_datafile_IpAddress);
		etImei = (EditText) findViewById(R.id.et_datafile_Imei);
		etPhoneNumber = (EditText) findViewById(R.id.et_datafile_PhoneNumber);
		etAndroidID = (EditText) findViewById(R.id.et_datafile_AndroidID);
		etGsfId = (EditText) findViewById(R.id.et_datafile_GsfId);
		etAdvertisementID = (EditText) findViewById(R.id.et_datafile_AdvertisementID);
		etMcc = (EditText) findViewById(R.id.et_datafile_Mcc);
		etMnc = (EditText) findViewById(R.id.et_datafile_Mnc);
		etCountry = (EditText) findViewById(R.id.et_datafile_Country);
		final Spinner spOperator = (Spinner) findViewById(R.id.sp_data_file_Operator);

		ArrayAdapter<String> mOPeratorAdapter = new ArrayAdapter<String>(
				ActivityCreateTask.this, android.R.layout.simple_spinner_item,
				XpHelper.array);
		mOPeratorAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spOperator.setAdapter(mOPeratorAdapter);
		spOperator.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				spinnerdata = (String) parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		etIccId = (EditText) findViewById(R.id.et_datafile_IccId);
		etGsmCallID = (EditText) findViewById(R.id.et_datafile_GsmCallID);// Cell
		etGsmLac = (EditText) findViewById(R.id.et_datafile_GsmLac);// LAC
		etIMSI = (EditText) findViewById(R.id.et_datafile_IMSI);
		etSSID = (EditText) findViewById(R.id.et_datafile_SSID);
		etUa = (EditText) findViewById(R.id.et_datafile_Ua);
		etModel = (EditText) findViewById(R.id.et_datafile_Model);
		etProduct = (EditText) findViewById(R.id.et_datafile_Product);
		etManufacturet = (EditText) findViewById(R.id.et_datafile_Manufacturer);
		etSystemCode = (EditText) findViewById(R.id.et_datafile_systemcode);
		etDataType = (EditText) findViewById(R.id.et_datafile_datatype);
		etAndroidCode = (EditText) findViewById(R.id.et_datafile_androidcode);

		// Handle Check
		// 基本任务属性：是否需要递减
		cbTaskDeclineFlag
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							etTaskDecilneRatio.setEnabled(true);
							etTaskDecilneMin.setEnabled(true);
						} else {
							etTaskDecilneRatio.setEnabled(false);
							etTaskDecilneRatio.setText("");
							etTaskDecilneMin.setEnabled(false);
							etTaskDecilneMin.setText("");
						}
					}
				});
		// 是否开启隔日回访
		cbTaskNextDayFlag
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							etTaskNextDayVisitInterval.setEnabled(true);
							etTaskNextDayVisitIntervalReturnRatio
									.setEnabled(true);
							etTaskNextDayVisitIntervalCount.setEnabled(true);
							cbTaskNextDayVisitDeclineFlag.setEnabled(true);
						} else {
							etTaskNextDayVisitInterval.setEnabled(false);
							etTaskNextDayVisitInterval.setText("");
							etTaskNextDayVisitIntervalReturnRatio
									.setEnabled(false);
							etTaskNextDayVisitIntervalReturnRatio.setText("");
							etTaskNextDayVisitIntervalCount.setEnabled(false);
							etTaskNextDayVisitIntervalCount.setText("");
							cbTaskNextDayVisitDeclineFlag.setChecked(false);
							cbTaskNextDayVisitDeclineFlag.setEnabled(false);
						}
					}
				});
		// 隔日回访是否需要递减
		cbTaskNextDayVisitDeclineFlag
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							etTaskNextDayVisitDecilneRatio.setEnabled(true);
							etTaskNextDayVisitDecilneMin.setEnabled(true);
						} else {
							etTaskNextDayVisitDecilneRatio.setEnabled(false);
							etTaskNextDayVisitDecilneRatio.setText("");
							etTaskNextDayVisitDecilneMin.setEnabled(false);
							etTaskNextDayVisitDecilneMin.setText("");
						}
					}
				});

	}

	public class Add_TaskAsyncTask extends AsyncTask<Object, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(ActivityCreateTask.this);
			mProgressDialog.setMessage("正在执行操作...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// mProgressDialog.setProgressNumberFormat(null);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub

			return (int) insert_data();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!ActivityCreateTask.this.isFinishing()) {
				mProgressDialog.dismiss();
				if (result != -1) {
					Toast.makeText(ActivityCreateTask.this, "任务添加成功！", 1)
							.show();

					ActivityCreateTask.this.finish();
				} else {
					// 执行删除工作
					Toast.makeText(ActivityCreateTask.this, "任务添加失败！请查看帮助信息", 1)
							.show();
					DBMgr.getInstance(ActivityCreateTask.this).task_delete(
							etTaskName.getText().toString().trim());

				}
			}
		}
	}

	public List<PhoneDataBean> CreateTaskDataFile(int tasknewdata) {
		List<PhoneDataBean> mList = new ArrayList<PhoneDataBean>();
		for (int i = 0; i < tasknewdata; i++) {
			PhoneDataBean mPhoneDataBean = new PhoneDataBean();
			// 根据运营商进行判断
			if ("其他".equals(spinnerdata)) {
				String str = XpHelper.arrayzz[r
						.nextInt(XpHelper.arrayzz.length)];
				if ("中国电信".equals(str)) {
					mPhoneDataBean.setOperator("CHINA TELECOM");
					mPhoneDataBean.setMnc("03");
					mPhoneDataBean.setMcc("460");
					// 手机号
					if (TextUtils.isEmpty(etPhoneNumber.getText().toString()
							.trim())) {
						mPhoneDataBean.setPhoneNumber(PrivacyManager
								.getRandomProp("TELECOM"));
					} else {
						// 增加指定号段
						if (etPhoneNumber.getText().toString().trim().length() > 0) {
							mPhoneDataBean.setPhoneNumber(PrivacyManager
									.ParagraphPhoneNumber(etPhoneNumber
											.getText().toString().trim()
											.toString()));
						} else {
							mPhoneDataBean.setPhoneNumber(etPhoneNumber
									.getText().toString().trim().toString());
						}
					}
					// IMSI
					if (TextUtils.isEmpty(etIMSI.getText().toString().trim())) {
						mPhoneDataBean.setImsi(PrivacyManager
								.getRandomProp("TELECOM IMSI"));
					} else {
						mPhoneDataBean.setImsi(etIMSI.getText().toString()
								.trim());
					}
					// ICCID
					if (TextUtils.isEmpty(etIccId.getText().toString().trim())) {
						mPhoneDataBean.setIccId(PrivacyManager
								.getRandomProp("TELECOM ICCID"));
					} else {
						mPhoneDataBean.setIccId(etIccId.getText().toString()
								.trim());
					}
				}
				if ("中国移动".equals(str)) {
					mPhoneDataBean.setOperator("CHINA MOBILE");
					mPhoneDataBean.setMnc("00");
					mPhoneDataBean.setMcc("460");
					// 手机号
					if (TextUtils.isEmpty(etPhoneNumber.getText().toString()
							.trim())) {
						mPhoneDataBean.setPhoneNumber(PrivacyManager
								.getRandomProp("MOBILE"));
					} else {
						// 增加指定号段
						if (etPhoneNumber.getText().toString().trim().length() > 0) {
							mPhoneDataBean.setPhoneNumber(PrivacyManager
									.ParagraphPhoneNumber(etPhoneNumber
											.getText().toString().trim()
											.toString()));
						} else {
							mPhoneDataBean.setPhoneNumber(etPhoneNumber
									.getText().toString().trim().toString());
						}
					}
					// IMSI
					if (TextUtils.isEmpty(etIMSI.getText().toString().trim())) {
						mPhoneDataBean.setImsi(PrivacyManager
								.getRandomProp("MOBILE IMSI"));
					} else {
						mPhoneDataBean.setImsi(etIMSI.getText().toString()
								.trim());
					}
					// ICCID
					if (TextUtils.isEmpty(etIccId.getText().toString().trim())) {
						mPhoneDataBean.setIccId(PrivacyManager
								.getRandomProp("MOBILE ICCID"));
					} else {
						mPhoneDataBean.setIccId(etIccId.getText().toString()
								.trim());
					}
				}

				if ("中国联通".equals(str)) {
					mPhoneDataBean.setOperator("CHINA UNICOM");
					mPhoneDataBean.setMnc("01");
					mPhoneDataBean.setMcc("460");
					// 手机号
					if (TextUtils.isEmpty(etPhoneNumber.getText().toString()
							.trim())) {
						mPhoneDataBean.setPhoneNumber(PrivacyManager
								.getRandomProp("UNICOM"));
					} else {
						// 增加指定号段
						if (etPhoneNumber.getText().toString().trim().length() > 0) {
							mPhoneDataBean.setPhoneNumber(PrivacyManager
									.ParagraphPhoneNumber(etPhoneNumber
											.getText().toString().trim()
											.toString()));
						} else {
							mPhoneDataBean.setPhoneNumber(etPhoneNumber
									.getText().toString().trim().toString());
						}
					}
					// IMSI
					if (TextUtils.isEmpty(etIMSI.getText().toString().trim())) {
						mPhoneDataBean.setImsi(PrivacyManager
								.getRandomProp("UNICOM IMSI"));
					} else {
						mPhoneDataBean.setImsi(etIMSI.getText().toString()
								.trim());
					}
					// ICCID
					if (TextUtils.isEmpty(etIccId.getText().toString().trim())) {
						mPhoneDataBean.setIccId(PrivacyManager
								.getRandomProp("UNICOM ICCID"));
					} else {
						mPhoneDataBean.setIccId(etIccId.getText().toString()
								.trim());
					}
				}
			}

			if ("中国移动".equals(spinnerdata)) {
				mPhoneDataBean.setOperator("CHINA MOBILE");
				mPhoneDataBean.setMnc("00");
				mPhoneDataBean.setMcc("460");
				// 手机号
				if (TextUtils
						.isEmpty(etPhoneNumber.getText().toString().trim())) {
					mPhoneDataBean.setPhoneNumber(PrivacyManager
							.getRandomProp("MOBILE"));
				} else {
					// 增加指定号段
					if (etPhoneNumber.getText().toString().trim().length() > 0) {
						mPhoneDataBean.setPhoneNumber(PrivacyManager
								.ParagraphPhoneNumber(etPhoneNumber.getText()
										.toString().trim().toString()));
					} else {
						mPhoneDataBean.setPhoneNumber(etPhoneNumber.getText()
								.toString().trim().toString());
					}
				}
				// IMSI
				if (TextUtils.isEmpty(etIMSI.getText().toString().trim())) {
					mPhoneDataBean.setImsi(PrivacyManager
							.getRandomProp("MOBILE IMSI"));
				} else {
					mPhoneDataBean.setImsi(etIMSI.getText().toString().trim());
				}
				// ICCID
				if (TextUtils.isEmpty(etIccId.getText().toString().trim())) {
					mPhoneDataBean.setIccId(PrivacyManager
							.getRandomProp("MOBILE ICCID"));
				} else {
					mPhoneDataBean
							.setIccId(etIccId.getText().toString().trim());
				}
			}
			if ("中国联通".equals(spinnerdata)) {
				mPhoneDataBean.setOperator("CHINA UNICOM");
				mPhoneDataBean.setMnc("01");
				mPhoneDataBean.setMcc("460");
				// 手机号
				if (TextUtils
						.isEmpty(etPhoneNumber.getText().toString().trim())) {
					mPhoneDataBean.setPhoneNumber(PrivacyManager
							.getRandomProp("UNICOM"));
				} else {
					// 增加指定号段
					if (etPhoneNumber.getText().toString().trim().length() > 0) {
						mPhoneDataBean.setPhoneNumber(PrivacyManager
								.ParagraphPhoneNumber(etPhoneNumber.getText()
										.toString().trim().toString()));
					} else {
						mPhoneDataBean.setPhoneNumber(etPhoneNumber.getText()
								.toString().trim().toString());
					}
				}
				// IMSI
				if (TextUtils.isEmpty(etIMSI.getText().toString().trim())) {
					mPhoneDataBean.setImsi(PrivacyManager
							.getRandomProp("UNICOM IMSI"));
				} else {
					mPhoneDataBean.setImsi(etIMSI.getText().toString().trim());
				}
				// ICCID
				if (TextUtils.isEmpty(etIccId.getText().toString().trim())) {
					mPhoneDataBean.setIccId(PrivacyManager
							.getRandomProp("UNICOM ICCID"));
				} else {
					mPhoneDataBean
							.setIccId(etIccId.getText().toString().trim());
				}
			}
			if ("中国电信".equals(spinnerdata)) {
				mPhoneDataBean.setOperator("CHINA TELECOM");
				mPhoneDataBean.setMnc("03");
				mPhoneDataBean.setMcc("460");
				// 手机号
				if (TextUtils
						.isEmpty(etPhoneNumber.getText().toString().trim())) {
					mPhoneDataBean.setPhoneNumber(PrivacyManager
							.getRandomProp("TELECOM"));
				} else {
					// 增加指定号段
					if (etPhoneNumber.getText().toString().trim().length() > 0) {
						mPhoneDataBean.setPhoneNumber(PrivacyManager
								.ParagraphPhoneNumber(etPhoneNumber.getText()
										.toString().trim().toString()));
					} else {
						mPhoneDataBean.setPhoneNumber(etPhoneNumber.getText()
								.toString().trim().toString());
					}
				}
				// IMSI
				if (TextUtils.isEmpty(etIMSI.getText().toString().trim())) {
					mPhoneDataBean.setImsi(PrivacyManager
							.getRandomProp("TELECOM IMSI"));
				} else {
					mPhoneDataBean.setImsi(etIMSI.getText().toString().trim());
				}
				// ICCID
				if (TextUtils.isEmpty(etIccId.getText().toString().trim())) {
					mPhoneDataBean.setIccId(PrivacyManager
							.getRandomProp("TELECOM ICCID"));
				} else {
					mPhoneDataBean
							.setIccId(etIccId.getText().toString().trim());
				}
			}

			// Serial
			if (TextUtils.isEmpty(etSerial.getText().toString().trim())) {
				mPhoneDataBean
						.setSerial(PrivacyManager.getRandomProp("SERIAL"));
			} else {
				mPhoneDataBean.setSerial(etSerial.getText().toString().trim());
			}
			// Latitude
			if (TextUtils.isEmpty(etLatitude.getText().toString().trim())) {
				mPhoneDataBean.setLatitude(PrivacyManager.getRandomProp("LAT"));
			} else {
				mPhoneDataBean.setLatitude(etLatitude.getText().toString()
						.trim());
			}
			// Longitude
			if (TextUtils.isEmpty(etLongitude.getText().toString().trim())) {
				mPhoneDataBean
						.setLongitude(PrivacyManager.getRandomProp("LON"));
			} else {
				mPhoneDataBean.setLongitude(etLongitude.getText().toString()
						.trim());
			}
			// Altitude
			if (TextUtils.isEmpty(etAltitude.getText().toString().trim())) {
				mPhoneDataBean.setAltitude(PrivacyManager.getRandomProp("ALT"));
			} else {
				mPhoneDataBean.setAltitude(etAltitude.getText().toString()
						.trim());
			}
			// MacAddress
			if (TextUtils.isEmpty(etMacAddress.getText().toString().trim())) {
				mPhoneDataBean.setMacAddress(PrivacyManager
						.getRandomProp("MAC"));
			} else {
				mPhoneDataBean.setMacAddress(etMacAddress.getText().toString()
						.trim());
			}
			// IpAddress
			if (TextUtils.isEmpty(etIpAddress.getText().toString())) {
				mPhoneDataBean.setIpAddress(PrivacyManager
						.getRandomProp("IPADDRESS"));
			} else {
				mPhoneDataBean.setIpAddress(etIpAddress.getText().toString());
			}
			// Imei
			if (TextUtils.isEmpty(etImei.getText().toString())) {
				mPhoneDataBean.setImei(PrivacyManager.getRandomProp("IMEI"));
			} else {
				// 指定生成imei
				if (etImei.getText().toString().trim().length() > 0) {
					mPhoneDataBean.setImei(PrivacyManager.ParagraphImei(etImei
							.getText().toString().trim()));
				} else {
					mPhoneDataBean.setImei(etImei.getText().toString().trim());
				}

			}

			// AndroidID
			if (TextUtils.isEmpty(etAndroidID.getText().toString().trim())) {
				mPhoneDataBean.setAndroidID(PrivacyManager
						.getRandomProp("ANDROID_ID"));
			} else {
				mPhoneDataBean.setAndroidID(etAndroidID.getText().toString()
						.trim());
			}

			// GsfId
			if (TextUtils.isEmpty(etGsfId.getText().toString().trim())) {
				mPhoneDataBean.setGsfId(PrivacyManager.getRandomProp("GSF_ID"));
			} else {
				mPhoneDataBean.setGsfId(etGsfId.getText().toString().trim());
			}
			// AdvertisementID
			if (TextUtils
					.isEmpty(etAdvertisementID.getText().toString().trim())) {
				mPhoneDataBean.setAdvertisementID(PrivacyManager
						.getRandomProp("AdvertisementID"));
			} else {
				mPhoneDataBean.setAdvertisementID(etAdvertisementID.getText()
						.toString().trim());
			}
			// Country
			if (TextUtils.isEmpty(etCountry.getText().toString().trim())) {
				mPhoneDataBean.setCountry("CN");
			} else {
				mPhoneDataBean
						.setCountry(etCountry.getText().toString().trim());
			}
			// GsmCallID
			if (TextUtils.isEmpty(etGsmCallID.getText().toString().trim())) {
				mPhoneDataBean.setGsmCallID(PrivacyManager
						.getRandomProp("GsmCallID"));
			} else {
				mPhoneDataBean.setGsmCallID(etGsmCallID.getText().toString()
						.trim());
			}
			// GsmLac

			if (TextUtils.isEmpty(etGsmLac.getText().toString().trim())) {
				mPhoneDataBean.setGsmLac(PrivacyManager
						.getRandomProp("GsmCallID"));
			} else {
				mPhoneDataBean.setGsmLac(etGsmLac.getText().toString().trim());
			}

			// Ua
			if (TextUtils.isEmpty(etUa.getText().toString().trim())) {
				mPhoneDataBean.setUa(PrivacyManager.getRandomProp("Ua"));
			} else {
				mPhoneDataBean.setUa(etUa.getText().toString().trim());
			}
			// 后续添加 手机型号，制造商
			// datatype
			if (TextUtils.isEmpty(etDataType.getText().toString().trim())) {
				mPhoneDataBean.setDatatype(PrivacyManager
						.getRandomProp("DATATYPE"));
			} else {
				mPhoneDataBean.setDatatype(etDataType.getText().toString()
						.trim());
			}

			// etAndroidCode
			if (TextUtils.isEmpty(etAndroidCode.getText().toString().trim())) {
				mPhoneDataBean.setAndroidCode(PrivacyManager
						.getRandomProp("ANDROIDCODE"));
			} else {
				mPhoneDataBean.setAndroidCode(etAndroidCode.getText()
						.toString().trim());
			}

			// etSystemCode
			if (TextUtils.isEmpty(etSystemCode.getText().toString().trim())) {
				mPhoneDataBean.setSystemCode(PrivacyManager
						.getRandomProp("MODEL"));
			} else {
				mPhoneDataBean.setSystemCode(etSystemCode.getText().toString()
						.trim());
			}

			if (xpmodelsList.size() > 0) {
				xpmodel mXpmodel = xpmodelsList.get(mRandom
						.nextInt(xpmodelsList.size()));
				// model
				if (TextUtils.isEmpty(etModel.getText().toString().trim())) {
					mPhoneDataBean.setModel(mXpmodel.getModel().toString());
				} else {
					mPhoneDataBean
							.setModel(etModel.getText().toString().trim());
				}
				// 制造商
				if (TextUtils.isEmpty(etManufacturet.getText().toString()
						.trim())) {
					mPhoneDataBean.setManufacturer(mXpmodel.getManufacturer()
							.toString());
				} else {
					mPhoneDataBean.setManufacturer(etManufacturet.getText()
							.toString().trim());
				}

				// 设备商
				if (TextUtils.isEmpty(etProduct.getText().toString().trim())) {
					mPhoneDataBean.setProduct(mXpmodel.getProduct().toString());
				} else {
					mPhoneDataBean.setProduct(etProduct.getText().toString()
							.trim());
				}
				mPhoneDataBean.setDensity(mXpmodel.getDensity().toString());
			} else {
				// model
				if (TextUtils.isEmpty(etModel.getText().toString().trim())) {
					mPhoneDataBean.setModel(PrivacyManager
							.getRandomProp("MODEL"));
				} else {
					mPhoneDataBean
							.setModel(etModel.getText().toString().trim());
				}
				// 制造商
				if (TextUtils.isEmpty(etManufacturet.getText().toString()
						.trim())) {
					mPhoneDataBean.setManufacturer(PrivacyManager
							.getRandomProp("MODEL"));
				} else {
					mPhoneDataBean.setManufacturer(etManufacturet.getText()
							.toString().trim());
				}

				// 设备商
				if (TextUtils.isEmpty(etProduct.getText().toString().trim())) {
					mPhoneDataBean.setProduct(PrivacyManager
							.getRandomProp("MODEL"));
				} else {
					mPhoneDataBean.setProduct(etProduct.getText().toString()
							.trim());
				}
				mPhoneDataBean.setDensity(PrivacyManager
						.getRandomProp("DENSITY"));

			}

			mPhoneDataBean.setTimeapp(PrivacyManager.getRandomProp("TIMEAPP"));

			if (TextUtils.isEmpty(etSSID.getText().toString().trim())) {
				mPhoneDataBean.setSsid(PrivacyManager.getRandomProp("SSID"));
			} else {
				mPhoneDataBean.setSsid(etProduct.getText().toString().trim());
			}
			mDataBeanLists.add(mPhoneDataBean);
		}
		return mDataBeanLists;

	}

	@SuppressLint("SimpleDateFormat")
	private long insert_data() {
		ContentValues values = new ContentValues();
		values.put(TaskAttribute.TASKNAME, etTaskName.getText().toString()
				.trim());
		values.put(TaskAttribute.TASKDESC, etTaskDesc.getText().toString()
				.trim());
		values.put(TaskAttribute.TASKNEWDATA, etTaskNewdata.getText()
				.toString().trim());
		values.put(TaskAttribute.TASKNUMBER, etTaskNumber.getText().toString()
				.trim());
		values.put(TaskAttribute.TASKRETURNRATIO, etTaskReturnratio.getText()
				.toString().trim());

		values.put("tb_taskstayway", Integer.valueOf(spinnerstayway));
		// 需要递减
		if (cbTaskDeclineFlag.isChecked()) {
			values.put(TaskAttribute.TASKDECLINEFLAG, true);
			String str1 = etTaskDecilneRatio.getText().toString().trim();
			if ("".equals(str1)) {
				values.put(TaskAttribute.TASKDECILNERATIO, "0");
			} else {
				values.put(TaskAttribute.TASKDECILNERATIO, etTaskDecilneRatio
						.getText().toString().trim());
			}

			String str2 = etTaskDecilneMin.getText().toString().trim();
			if ("".equals(str2)) {
				values.put(TaskAttribute.TASKDECILNEMIN, "0");
			} else {
				values.put(TaskAttribute.TASKDECILNEMIN, etTaskDecilneMin
						.getText().toString().trim());
			}

		} else {
			values.put(TaskAttribute.TASKDECLINEFLAG, false);
			values.put(TaskAttribute.TASKDECILNERATIO, "0");
			values.put(TaskAttribute.TASKDECILNEMIN, "0");
		}

		// 是否开启隔日回访
		if (cbTaskNextDayFlag.isChecked()) {
			values.put(TaskAttribute.TASKNEXTDAYFLAG, true);
			String s = etTaskNextDayVisitInterval.getText().toString().trim();
			if ("".equals(s)) {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVAL, "0");
			} else {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVAL,
						etTaskNextDayVisitInterval.getText().toString().trim());
			}

			String ss = etTaskNextDayVisitIntervalReturnRatio.getText()
					.toString().trim();
			if ("".equals(ss)) {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALRETURNRATIO,
						"0");
			} else {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALRETURNRATIO,
						ss);
			}

			String zz = etTaskNextDayVisitIntervalCount.getText().toString()
					.trim();
			if ("".equals(zz)) {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALCOUNT, "0");
			} else {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALCOUNT,
						etTaskNextDayVisitIntervalCount.getText().toString()
								.trim());
			}
		} else {
			values.put(TaskAttribute.TASKNEXTDAYFLAG, false);
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVAL, "0");
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALRETURNRATIO, "0");
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALCOUNT, "0");
		}

		values.put(TaskAttribute.TASKNEXTDAYVISITSTAYWAY, spinnernextdaystayway);
		// 隔日递减是否

		if (cbTaskNextDayVisitDeclineFlag.isChecked()) {
			values.put(TaskAttribute.TASKNEXTDAYVISITDECLINEFLAG, true?1:0);
			String s = etTaskNextDayVisitDecilneRatio.getText().toString()
					.trim();
			if ("".equals(s)) {
				values.put(TaskAttribute.TASKNEXTDAYVISITDECILNERATIO, "0");
			} else {
				values.put(TaskAttribute.TASKNEXTDAYVISITDECILNERATIO,
						etTaskNextDayVisitDecilneRatio.getText().toString()
								.trim());
			}

			String ss = etTaskNextDayVisitDecilneMin.getText().toString()
					.trim();
			if ("".equals(ss)) {
				values.put(TaskAttribute.TASKNEXTDAYVISITDECILNEMIN, "0");
			} else {
				values.put(TaskAttribute.TASKNEXTDAYVISITDECILNEMIN,
						etTaskNextDayVisitDecilneMin.getText().toString()
								.trim());
			}

		} else {
			values.put(TaskAttribute.TASKNEXTDAYVISITDECLINEFLAG, false?1:0);
			values.put(TaskAttribute.TASKNEXTDAYVISITDECILNERATIO, "0");
			values.put(TaskAttribute.TASKNEXTDAYVISITDECILNEMIN, "0");
		}

		String ss = etTaskNextWeekVisitIntervalReturnRatio.getText().toString()
				.trim();
		if ("".equals(ss)) {
			values.put("tb_TaskNextWeekVisitIntervalReturnRatio", 0);
		} else {
			values.put("tb_TaskNextWeekVisitIntervalReturnRatio",
					Double.valueOf(ss));
		}
		values.put("tb_TaskNextWeekVisitStayWay",
				Integer.valueOf(spinnernextweekstayway));
		if (cbTaskNextWeekVisitDeclineFlag.isChecked()) {
			values.put("tb_TaskNextWeekVisitDeclineFlag", true?1:0);
			ss = etTaskNextWeekVisitDecilneRatio.getText().toString().trim();
			if ("".equals(ss)) {
				values.put("tb_TaskNextWeekVisitDecilneRatio", "0");
			} else {
				values.put("tb_TaskNextWeekVisitDecilneRatio", ss);
			}
			ss = etTaskNextWeekVisitDecilneMin.getText().toString().trim();
			if ("".equals(ss)) {
				values.put("tb_TaskNextWeekVisitDecilneMin", false?1:0);
			} else {
				values.put("tb_TaskNextWeekVisitDecilneMin",
						Integer.valueOf(ss));
			}
		} else {
			values.put("tb_TaskNextWeekVisitDeclineFlag", Integer.valueOf(0x0));
			values.put("tb_TaskNextWeekVisitDecilneRatio", Integer.valueOf(0x0));
			values.put("tb_TaskNextWeekVisitDecilneMin", Integer.valueOf(0x0));
		}
		ss = etTaskNextMonthVisitIntervalReturnRatio.getText().toString()
				.trim();
		if ("".equals(ss)) {
			values.put("tb_TaskNextMonthVisitIntervalReturnRatio", 0);
		} else {
			values.put("tb_TaskNextMonthVisitIntervalReturnRatio",
					Double.valueOf(ss));
		}
		values.put("tb_TaskNextMonthVisitStayWay",
				Integer.valueOf(spinnernextmonthstayway));
		if (cbTaskNextMonthVisitDeclineFlag.isChecked()) {
			values.put("tb_TaskNextMonthVisitDeclineFlag",true?1:0);
			ss = etTaskNextMonthVisitDecilneRatio.getText().toString().trim();
			if ("".equals(ss)) {
				values.put("tb_TaskNextMonthVisitDecilneRatio", 0);
			} else {
				values.put("tb_TaskNextMonthVisitDecilneRatio",
						Double.valueOf(ss));
			}
			ss = etTaskNextMonthVisitDecilneMin.getText().toString().trim();
			if ("".equals(ss)) {
				values.put("tb_TaskNextMonthVisitDecilneMin", 0);
			} else {
				values.put("tb_TaskNextMonthVisitDecilneMin",
						Integer.valueOf(ss));
			}
		} else {
			values.put("tb_TaskNextMonthVisitDeclineFlag", false?1:0);
			values.put("tb_TaskNextMonthVisitDecilneRatio",
					Integer.valueOf(0x0));
			values.put("tb_TaskNextMonthVisitDecilneMin", Integer.valueOf(0x0));
		}
		values.put("tb_TaskSecondActiveF", Integer.valueOf(etTaskSecondActiveF
				.getText().toString().trim()));
		values.put("tb_TaskSecondActiveS", Integer.valueOf(etTaskSecondActiveS
				.getText().toString().trim()));

		// 创建任务是否成功
		long state = DBMgr.getInstance(ActivityCreateTask.this)
				.add_task(values);
		if (state != -1) {
			// 1、创建任务数据目录
			String path = null;
			if (XpHelper.CreateTaskNameDirs(etTaskName.getText().toString()
					.trim())) {
				// 2、创建任务下面的数据文件
				String s = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(s)) {
					File sdcardDir = Environment.getExternalStorageDirectory();
					path = sdcardDir.getPath() + "/xp_datafile/"
							+ etTaskName.getText().toString().trim();
					File afile = new File(sdcardDir.getPath() + "/xp_datafile/");
					if(!afile.exists()){
						afile.mkdir();
					}
				}
				// System.err.println("path=" + path);
				Date d = new Date();
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				int count = Integer.valueOf(etTaskNumber.getText().toString()
						.trim());
				int datasize = Integer.valueOf(etTaskNewdata.getText()
						.toString().trim());
				// System.err.println("创建文件count=" + count);
				// System.err.println("创建文件datasize=" + datasize);
				for (int i = 0; i < count; i++) {
					mDataBeanListsTmp = CreateTaskDataFile(datasize);
					ContentValues mValues = new ContentValues();
					String flieName = "";
					Calendar ca = Calendar.getInstance();
					ca.add(Calendar.DAY_OF_MONTH, i);//
					d = ca.getTime();
					flieName = etTaskNewdata.getText().toString().trim() + "-"
							+ etTaskName.getText().toString().trim() + "-"
							+ format.format(d);
					mValues.put(DataBase.TASK_NAME, etTaskName.getText()
							.toString().trim());
					mValues.put(DataBase.TASK_DATA_FILE, flieName);
					long s1 = DBMgr.getInstance(ActivityCreateTask.this)
							.add_task_data_file(ActivityCreateTask.this,
									mValues);
					if (s1 != -1) {
						if (mDataBeanListsTmp != null) {
							Gson mGson = new Gson();
							mGson.toJson(mDataBeanListsTmp);
							XpHelper.CreateTaskNameDataFile(
									ActivityCreateTask.this,
									mGson.toJson(mDataBeanListsTmp), path,
									flieName);
							s1 = s1 + 1;
						}

					}
					mDataBeanListsTmp.clear();
					// System.err.println("s1=" + s1);
				}
				return state;
			}
		}
		return -1;
	}
}
