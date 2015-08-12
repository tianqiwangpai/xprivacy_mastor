package com.hy.xp.app.task;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

import com.hy.xp.app.ApplicationEx;
import com.hy.xp.app.ApplicationInfoEx;
import com.hy.xp.app.UpdateService;
import com.hy.xp.app.Util;

@SuppressLint("UseValueOf")
public class DBMgr {
	private static DBMgr instance;
	SQLiteDatabase db;

	private DBMgr(Context context) {
		db = new DBHelper(context).getWritableDatabase();
	}

	public static DBMgr getInstance(Context context) {
		if (instance == null) {
			instance = new DBMgr(context);
		}
		return instance;
	}

	public static DBMgr get(Context context) {
		if (instance == null) {
			instance = new DBMgr(context);
		}
		return instance;
	}

	/**
	 * appuid add
	 * 
	 * @Title: appuid_insert
	 * @Description: TODO
	 * @param @param values
	 * @param @return
	 * @return long
	 * @throws
	 */
	public long appuid_insert(ContentValues values) {
		String appuid = (String) values.get("t_appid");
		String sql = "select * from appid where t_appid = '" + appuid + "'";
		Cursor mCursor = db.rawQuery(sql, null);
		try {
			if (!mCursor.moveToFirst())
				return db.insert("appid", null, values);

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mCursor.close();
		}
		return -1;
	}

	/**
	 * appuid delete
	 * 
	 * @Title: appuid_delete
	 * @Description: TODO
	 * @param @param appuid
	 * @param @return
	 * @return int
	 * @throws
	 */
	public int appuid_delete(String appuid) {
		return db.delete("appid", "t_appid" + "= ?", new String[] { appuid });
	}

	/**
	 * appuid list
	 * 
	 * @Title: appuid_list
	 * @Description: TODO
	 * @param @return
	 * @return List<AppUid>
	 * @throws
	 */
	public List<AppUid> appuid_list() {
		List<AppUid> mAppUids = new ArrayList<AppUid>();
		String sql = "select * from appid";
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor == null)
			return null;
		while (mCursor.moveToNext()) {
			AppUid mAppUid = new AppUid();
			mAppUid.setAppUid(mCursor.getInt(mCursor
					.getColumnIndex(TaskAttribute.TASKNAME)));
			mAppUids.add(mAppUid);
		}
		return mAppUids;
	}

	public long update_task(ContentValues values) {
		String whereClause = (String) values.get(TaskAttribute.TASKNAME);
		return db.update(TaskAttribute.table_name, values,
				TaskAttribute.TASKNAME + "=?", new String[] { whereClause });
	}

	// 添加新任务
	public long add_task(ContentValues values) {
		String taskName = (String) values.get(TaskAttribute.TASKNAME);
		String sql = "select * from tb_task_attribute where tb_TaskName = '"
				+ taskName + "'";
		Cursor mCursor = db.rawQuery(sql, null);
		try {
			if (!mCursor.moveToFirst())
				return db.insert(TaskAttribute.table_name, null, values);

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mCursor.close();
		}
		return -1;
	}

	// 删除任务
	public int task_delete(String taskname) {
		return db.delete(TaskAttribute.table_name, TaskAttribute.TASKNAME
				+ "= ?", new String[] { taskname });
	}

	// 获取任务基本属性列表
	public List<TaskAttribute> getTaskList() {
		List<TaskAttribute> mTaskBeansList = new ArrayList<TaskAttribute>();
		String sql = "select * from tb_task_attribute";
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor == null)
			return null;
		while (mCursor.moveToNext()) {
			TaskAttribute mBean = new TaskAttribute();
			mBean.setTaskName(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNAME)));
			mBean.setTaskDesc(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKDESC)));
			mBean.setTaskNewdata(new Integer(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEWDATA))));
			mBean.setTaskNumber(new Integer(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNUMBER))));
			mBean.setTaskReturnratio(Double.valueOf(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKRETURNRATIO))));
			int TaskDeclineFlag = new Integer(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKDECLINEFLAG)));
			if (TaskDeclineFlag > 0) {
				mBean.setTaskDeclineFlag(true);
			} else {
				mBean.setTaskDeclineFlag(false);
			}

			mBean.setTaskDecilneRatio(Double.valueOf(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKDECILNERATIO))));
			mBean.setTaskDecilneMin(Integer.valueOf(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKDECILNEMIN))));
			// mBean.setTaskNextDayFlag(new
			// Boolean(mCursor.getString(mCursor.getColumnIndex(TaskAttribute.TASKNEXTDAYFLAG))));
			int TaskNextDayFlag = new Integer(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEXTDAYFLAG)));
			if (TaskNextDayFlag > 0) {
				mBean.setTaskNextDayFlag(true);
			} else {
				mBean.setTaskNextDayFlag(false);
			}

			mBean.setTaskNextDayVisitInterval(new Integer(
					mCursor.getString(mCursor
							.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITINTERVAL))));
			mBean.setTaskNextDayVisitIntervalReturnRatio(Double.valueOf(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITINTERVALRETURNRATIO))));
			mBean.setTaskNextDayVisitIntervalCount(new Integer(
					mCursor.getString(mCursor
							.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITINTERVALCOUNT))));

			// mBean.setTaskNextDayVisitDeclineFlag(new
			// Boolean(mCursor.getString(mCursor.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITDECLINEFLAG))));
			int TaskNextDayVisitDeclineFlag = new Integer(
					mCursor.getString(mCursor
							.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITDECLINEFLAG)));
			if (TaskNextDayVisitDeclineFlag > 0) {
				mBean.setTaskNextDayVisitDeclineFlag(true);
			} else {
				mBean.setTaskNextDayVisitDeclineFlag(false);
			}
			mBean.setTaskNextDayVisitDecilneRatio(Double.valueOf(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITDECILNERATIO))));
			mBean.setTaskNextDayVisitDecilneMin(Integer.valueOf(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITDECILNEMIN))));
			mTaskBeansList.add(mBean);
		}
		return mTaskBeansList;
	}
	
	// 获取任务基本属性列表
	public List<String> getTaskNameList() {
		List<String> tasknamelist = new ArrayList<String>();
		String sql = "select tb_TaskName from tb_task_attribute";
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor == null || mCursor.getCount()<1)				
			return null;
		mCursor.moveToFirst();
		do{
			tasknamelist.add(mCursor.getString(
					mCursor.getColumnIndex(TaskAttribute.TASKNAME)));
		}while (mCursor.moveToNext());
		return tasknamelist;
	}

	/**
	 * 根据任务名称获取任务属性
	 * 
	 * @param TaskName
	 * @return
	 */
	public List<TaskAttribute> getTaskAttributeByTaskName(String TaskName) {
		List<TaskAttribute> mTaskBeansList = new ArrayList<TaskAttribute>();
		String sql = "select * from tb_task_attribute where tb_TaskName = '"
				+ TaskName + "'";

		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor == null)
			return null;
		while (mCursor.moveToNext()) {
			TaskAttribute mBean = new TaskAttribute();
			mBean.setTaskName(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNAME)));
			mBean.setTaskDesc(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKDESC)));
			mBean.setTaskNewdata(new Integer(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEWDATA))));
			String num = mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNUMBER));
			if(num != null){
				mBean.setTaskNumber(new Integer(num));
			}			
			mBean.setTaskReturnratio(Double.valueOf(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKRETURNRATIO))));
			int TaskDeclineFlag = new Integer(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKDECLINEFLAG)));
			
			if (TaskDeclineFlag > 0) {
				mBean.setTaskDeclineFlag(true);
				mBean.setTaskDecilneRatio(Double.valueOf(mCursor.getString(mCursor
						.getColumnIndex(TaskAttribute.TASKDECILNERATIO))));
				mBean.setTaskDecilneMin(Integer.valueOf(mCursor.getString(mCursor
						.getColumnIndex(TaskAttribute.TASKDECILNEMIN))));
			} else {
				mBean.setTaskDeclineFlag(false);
			}
			
			mBean.setTaskStayWay(new Integer(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKSTAYWAY))));
			String nextdayvisitstayway = mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITSTAYWAY));
			mBean.setTaskNextDayVisitStayWay(new Integer(nextdayvisitstayway==null?"0":nextdayvisitstayway));
			
			String nextweekvisitstayway = mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEXTWEEKVISITSTAYWAY));
			
			mBean.setTaskNextWeekVisitStayWay(new Integer(nextweekvisitstayway==null?"0":nextweekvisitstayway));
			
			String nextmonthvisitstayway = mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEXTMONTHVISITSTAYWAY));
			mBean.setTaskNextMonthVisitStayWay(new Integer(nextmonthvisitstayway==null?"0":nextmonthvisitstayway));
			
			int TaskNextDayFlag = new Integer(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEXTDAYFLAG)));
			if (TaskNextDayFlag > 0) {
				mBean.setTaskNextDayFlag(true);
				mBean.setTaskNextDayVisitInterval(new Integer(
						mCursor.getString(mCursor
								.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITINTERVAL))));
				mBean.setTaskNextDayVisitIntervalReturnRatio(Double.valueOf(mCursor.getString(mCursor
						.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITINTERVALRETURNRATIO))));
				mBean.setTaskNextDayVisitIntervalCount(new Integer(
						mCursor.getString(mCursor
								.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITINTERVALCOUNT))));
				int TaskNextDayVisitDeclineFlag = new Integer(
						mCursor.getString(mCursor
								.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITDECLINEFLAG)));
				if (TaskNextDayVisitDeclineFlag > 0) {
					mBean.setTaskNextDayVisitDeclineFlag(true);
					mBean.setTaskNextDayVisitDecilneRatio(Double.valueOf(mCursor.getString(mCursor
							.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITDECILNERATIO))));
					mBean.setTaskNextDayVisitDecilneMin(Integer.valueOf(mCursor.getString(mCursor
							.getColumnIndex(TaskAttribute.TASKNEXTDAYVISITDECILNEMIN))));
				} else {
					mBean.setTaskNextDayVisitDeclineFlag(false);
				}
			} else {
				mBean.setTaskNextDayFlag(false);
			}
						
			int TaskNextWeekFlag = mCursor.getInt(mCursor
											.getColumnIndex(TaskAttribute.TASKNEXTWEEKFLAG));
			if (TaskNextWeekFlag > 0) {
				mBean.setTaskNextWeekFlag(true);
				mBean.setTaskNextWeekVisitIntervalReturnRatio(Double.valueOf(mCursor.getString(mCursor
						.getColumnIndex(TaskAttribute.TASKNEXTWEEKVISITINTERVALRETURNRATIO))));
				mBean.setTaskNextWeekVisitStayWay(Integer.valueOf(mCursor.getString(mCursor
						.getColumnIndex(TaskAttribute.TASKNEXTWEEKVISITSTAYWAY))));
				
				if(Integer.valueOf(mCursor.getString(mCursor
						.getColumnIndex(TaskAttribute.TASKNEXTWEEKVISITDECLINEFLAG))) == 1){
					mBean.setTaskNextWeekVisitDeclineFlag(true);
					mBean.setTaskNextWeekVisitDecilneMin(Integer.valueOf(mCursor.getString(mCursor
							.getColumnIndex(TaskAttribute.TASKNEXTWEEKVISITDECILNEMIN))));
					mBean.setTaskNextWeekVisitDecilneRatio(Double.valueOf(mCursor.getString(mCursor
							.getColumnIndex(TaskAttribute.TASKNEXTWEEKVISITDECILNERATIO))));
				}else{
					mBean.setTaskNextWeekVisitDeclineFlag(false);
				}
			}else{
				mBean.setTaskNextWeekFlag(false);
			}
			
			int TaskNextMonthFlag = new Integer(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEXTWEEKFLAG)));
			if (TaskNextMonthFlag > 0) {
				mBean.setTaskNextMonthFlag(true);
				mBean.setTaskNextMonthVisitIntervalReturnRatio(Double.valueOf(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEXTMONTHVISITINTERVALRETURNRATIO))));
				mBean.setTaskNextMonthVisitStayWay(Integer.valueOf(mCursor.getString(mCursor
					.getColumnIndex(TaskAttribute.TASKNEXTMONTHVISITSTAYWAY))));
			
				if(Integer.valueOf(mCursor.getString(mCursor
						.getColumnIndex(TaskAttribute.TASKNEXTMONTHVISITDECLINEFLAG))) == 1){
					mBean.setTaskNextMonthVisitDeclineFlag(true);
					mBean.setTaskNextMonthVisitDecilneMin(Integer.valueOf(mCursor.getString(mCursor
							.getColumnIndex(TaskAttribute.TASKNEXTMONTHVISITDECILNEMIN))));
					mBean.setTaskNextMonthVisitDecilneRatio(Double.valueOf(mCursor.getString(mCursor
							.getColumnIndex(TaskAttribute.TASKNEXTMONTHVISITDECILNERATIO))));
				}else{
					mBean.setTaskNextMonthVisitDeclineFlag(false);
					mBean.setTaskNextMonthVisitDecilneMin(0);
					mBean.setTaskNextMonthVisitDecilneRatio(0);
				}
			}else{
				mBean.setTaskNextMonthFlag(false);
			}
			mTaskBeansList.add(mBean);
		}
		return mTaskBeansList;
	}

	// 根据任务名称获取，所生成要执行任务的文件
	public List<TaskCurlFile> getTaskCurlFileByTaskName(String TaskName) {
		List<TaskCurlFile> mList = new ArrayList<TaskCurlFile>();
		String sql = "select * from tb_task_curl_file where task_name = '"
				+ TaskName + "'";
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor == null) {
			return null;
		}
		while (mCursor.moveToNext()) {
			TaskCurlFile mTaskCurlFile = new TaskCurlFile();
			mTaskCurlFile.setTask_curl_file(mCursor.getString(mCursor
					.getColumnIndex(TaskCurlFile.TASK_CURL_FILE)));
			mList.add(mTaskCurlFile);
		}
		return mList;
	}

	// 根据任务名称。获取生成的新数据文件名称
	public List<DataBase> getDataFileByTaskName(String taskName) {
		List<DataBase> mList = new ArrayList<DataBase>();
		String sql = "select * from tb_data_file where task_name = '"
				+ taskName + "'";
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor == null) {
			return null;
		}
		while (mCursor.moveToNext()) {
			DataBase mTaskCurlFile = new DataBase();
			mTaskCurlFile.setTask_date_file(mCursor.getString(mCursor
					.getColumnIndex(DataBase.TASK_DATA_FILE)));
			mList.add(mTaskCurlFile);
		}
		return mList;
	}

	// 创建每日新增文件
	public long add_task_data_file(Context mContext, ContentValues values) {
		return db.insert(DataBase.TABLE_NAME, null, values);
	}

	public long findCurl(String filename) {
		Cursor cursor = db.rawQuery(
				"SELECT * FROM tb_task_curl_file where task_curl_file = '"
						+ filename + "'", null);
		return cursor.getCount();
	}

	// 添加数据文件
	public long add_culr_file(ContentValues values) {
		return db.insert("tb_task_curl_file", null, values);
	}

	public void delet_task(String taskName) {
		// 删除task_attribute
		db.delete(TaskAttribute.table_name, TaskAttribute.TASKNAME + "= ?",
				new String[] { taskName });
		db.delete(TaskCurlFile.TABLE_NAME, TaskCurlFile.TASK_NAME + "= ?",
				new String[] { taskName });
		db.delete(DataBase.TABLE_NAME, DataBase.TASK_NAME + "= ?",
				new String[] { taskName });

	}

	/* 添加task_data_file */
	public long add_task_data_file_improt(Context mContext, ContentValues values) {
		String taskName = (String) values.get(DataBase.TASK_DATA_FILE);
		String sql = "select * from tb_data_file where task_data_file = '"
				+ taskName + "'";
		Cursor mCursor = db.rawQuery(sql, null);
		try {
			if (!mCursor.moveToNext())
				return db.insert(DataBase.TABLE_NAME, null, values);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mCursor.close();
		}
		return -1;

	}

	/* 添加机型文件 */
	public long add_xp_model_improt(Context mContext, ContentValues values) {
		String flag = (String) values.get(xpmodel.FALG);
		String sql = "select * from tb_xp_model where flag = '" + flag + "'";
		Cursor mCursor = db.rawQuery(sql, null);
		try {
			if (!mCursor.moveToNext())
				return db.insert(xpmodel.TABLE, null, values);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mCursor.close();
		}
		return -1;
	}

	/* 添加tb_task_attribute */
	public long add_task_attribute_improt(Context mContext, ContentValues values) {
		String taskName = (String) values.get(TaskAttribute.TASKNAME);
		String sql = "select * from tb_task_attribute where tb_TaskName = '"
				+ taskName + "'";
		Cursor mCursor = db.rawQuery(sql, null);
		try {
			if (!mCursor.moveToNext())
				return db.insert(TaskAttribute.table_name, null, values);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mCursor.close();
		}
		return -1;
	}

	public List<DataBase> getPhoneDataBeanListToImport() {
		List<DataBase> mList = new ArrayList<DataBase>();
		String sql = "select * from tb_data_file ";
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor == null)
			return null;
		while (mCursor.moveToNext()) {
			DataBase mCurlFile = new DataBase();
			mCurlFile.setTask_name(mCursor.getString(mCursor
					.getColumnIndex(DataBase.TASK_NAME)));
			mCurlFile.setTask_date_file(mCursor.getString(mCursor
					.getColumnIndex(DataBase.TASK_DATA_FILE)));
			mList.add(mCurlFile);
		}
		return mList;
	}

	// 根据任务名称，获取执行文件列表
	// 根据任务名称。获取生成的新数据文件名称
	public List<TaskCurlFile> getCurrFileByTaskName(String taskName) {
		List<TaskCurlFile> mList = new ArrayList<TaskCurlFile>();
		String sql = "select * from tb_task_curl_file where task_name = '"
				+ taskName + "'";
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor == null) {
			return null;
		}
		while (mCursor.moveToNext()) {
			TaskCurlFile mTaskCurlFile = new TaskCurlFile();
			mTaskCurlFile.setTask_curl_file(mCursor.getString(mCursor
					.getColumnIndex(TaskCurlFile.TASK_CURL_FILE)));
			mTaskCurlFile.setTask_name(mCursor.getString(mCursor
					.getColumnIndex(TaskCurlFile.TASK_NAME)));
			mList.add(mTaskCurlFile);
		}
		return mList;
	}

	// 获取机型数据
	public List<xpmodel> getXpmpdellist() {
		List<xpmodel> mList = new ArrayList<xpmodel>();
		String sql = "select * from tb_xp_model ";
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor == null) {
			return null;
		}
		while (mCursor.moveToNext()) {
			xpmodel mTaskCurlFile = new xpmodel();
			mTaskCurlFile.setModel(mCursor.getString(mCursor
					.getColumnIndex(xpmodel.MODEL)));
			mTaskCurlFile.setProduct(mCursor.getString(mCursor
					.getColumnIndex(xpmodel.PRODUCT)));
			mTaskCurlFile.setManufacturer(mCursor.getString(mCursor
					.getColumnIndex(xpmodel.MANUFACTURER)));
			mTaskCurlFile.setDensity(mCursor.getString(mCursor
					.getColumnIndex(xpmodel.DENSITY)));
			mList.add(mTaskCurlFile);
		}
		return mList;
	}

	// 删除当日文件
	public long deleteCurlFileByCurlFile(String CurlFile) {
		// String sql =
		// "select * from tb_task_curl_file where task_curl_file = '" + CurlFile
		// + "'";
		return db.delete(TaskCurlFile.TABLE_NAME, TaskCurlFile.TASK_CURL_FILE
				+ "= ?", new String[] { CurlFile });
	}

	public int del_xp_model() {
		return db.delete("tb_xp_model", null, null);
	}

	public void plInsert(List<PhoneDataBean> mBeans) {
		String str = "tb_phone";
		String sql_del_updat = "DELETE FROM sqlite_sequence WHERE name = '"
				+ str + "'";
		db.delete("tb_phone", null, null);
		db.execSQL(sql_del_updat);
		String sql = "insert into tb_phone(Serial," + "Latitude,"
				+ "Longitude," + "Altitude," + "MacAddress,"+ "IpAddress,"
				+ "Imei," + "PhoneNumber," + "AndroidID," + "GsfId,"
				+ "AdvertisementID," + "Mcc," + "Mnc," + "Country,"
				+ "Operator," + "IccId," + "GsmCallID," + "GsmLac," + "IMSI,"
				+ "SSID," + "Ua," + "Model," + "Manufacturer," + "Product,"
				+ "AndroidCode," + "SystemCode," + "datatype," + "density,"+"BMacAddress" 
				+ ") " + "values(?,"
				+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		SQLiteStatement stat = db.compileStatement(sql);
		db.beginTransaction();
		// start
		for (PhoneDataBean mBean : mBeans) {
			stat.bindString(1, mBean.getSerial());
			stat.bindString(2, mBean.getLatitude());
			stat.bindString(3, mBean.getLongitude());
			stat.bindString(4, mBean.getAltitude());
			stat.bindString(5, mBean.getMacAddress());
			stat.bindString(6, mBean.getIpAddress());
			stat.bindString(7, mBean.getImei());
			stat.bindString(8, mBean.getPhoneNumber());
			stat.bindString(9, mBean.getAndroidID());
			stat.bindString(10, mBean.getGsfId());
			stat.bindString(11, mBean.getAdvertisementID());
			stat.bindString(12, mBean.getMcc());
			stat.bindString(13, mBean.getMnc());
			stat.bindString(14, mBean.getCountry());
			stat.bindString(15, mBean.getOperator());
			stat.bindString(16, mBean.getIccId());
			stat.bindString(17, mBean.getGsmCallID());
			stat.bindString(18, mBean.getGsmLac());
			stat.bindString(19, mBean.getImsi());
			stat.bindString(20, mBean.getSsid());
			stat.bindString(21, mBean.getUa());
			stat.bindString(22, mBean.getModel());
			stat.bindString(23, mBean.getManufacturer());
			stat.bindString(24, mBean.getProduct());
			stat.bindString(25, mBean.getAndroidCode());
			stat.bindString(26, mBean.getSystemCode());
			stat.bindString(27, mBean.getDatatype());
			stat.bindString(28, mBean.getDensity());
			stat.bindString(29, mBean.getBMacAddress());
			stat.executeInsert();
		}
		// end
		db.setTransactionSuccessful();
		db.endTransaction();
		// db.close();

	}

	public PhoneDataBean getPhoneDataBeanBySuji(int id) {
		// System.out.println("id--->" + id);
		String sql = "select * from tb_phone where _id = '" + id + "'";
		System.out.println("sql---->" + sql);
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor == null) {
			return null;
		}
		PhoneDataBean mPhoneDataBean = null;
		while (mCursor.moveToNext()) {
			mPhoneDataBean = new PhoneDataBean();
			String serial = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.SERIAL)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.SERIAL))
					: "";
			mPhoneDataBean.setSerial(serial);

			String latitude = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.LATITUDE)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.LATITUDE))
					: "";
			mPhoneDataBean.setLatitude(latitude);

			String longitude = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.LONGITUDE)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.LONGITUDE))
					: "";
			mPhoneDataBean.setLongitude(longitude);

			String altitude = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.ALTITUDE)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.ALTITUDE))
					: "";
			mPhoneDataBean.setAltitude(altitude);

			String macAddress = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.MACADDRESS)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.MACADDRESS))
					: "";
			mPhoneDataBean.setMacAddress(macAddress);

			String ipAddress = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.IPADDRESS)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.IPADDRESS))
					: "";
			mPhoneDataBean.setIpAddress(ipAddress);

			String imei = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.IMEI)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.IMEI)) : "";
			mPhoneDataBean.setImei(imei);

			String phoneNumber = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.PHONENUMBER)).length() > 0 ? mCursor
					.getString(mCursor
							.getColumnIndex(PhoneDataBean.PHONENUMBER)) : "";
			mPhoneDataBean.setPhoneNumber(phoneNumber);

			String advertisementID = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.ADVERTISEMENTID))
					.length() > 0 ? mCursor.getString(mCursor
					.getColumnIndex(PhoneDataBean.ADVERTISEMENTID)) : "";
			mPhoneDataBean.setAdvertisementID(advertisementID);

			String gsfId = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.GSFID)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.GSFID))
					: "";
			mPhoneDataBean.setGsfId(gsfId);

			String androidID = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.ANDROIDID)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.ANDROIDID))
					: "";
			mPhoneDataBean.setAndroidID(androidID);

			String mnc = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.MNC)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.MNC)) : "";
			mPhoneDataBean.setMnc(mnc);

			String mcc = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.MCC)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.MCC)) : "";
			mPhoneDataBean.setMcc(mcc);

			String country = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.COUNTRY)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.COUNTRY))
					: "";
			mPhoneDataBean.setCountry(country);

			String operator = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.OPERATOR)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.OPERATOR))
					: "";
			mPhoneDataBean.setOperator(operator);

			String iccId = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.ICCID)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.ICCID))
					: "";
			mPhoneDataBean.setIccId(iccId);

			String gsmCallID = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.GSMCALLID)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.GSMCALLID))
					: "";
			mPhoneDataBean.setGsmCallID(gsmCallID);

			String gsmLac = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.GSMLAC)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.GSMLAC))
					: "";
			mPhoneDataBean.setGsmLac(gsmLac);

			String iMSI = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.IMSI)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.IMSI)) : "";
			mPhoneDataBean.setImsi(iMSI);

			String ssid = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.IMSI)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.IMSI)) : "";
			mPhoneDataBean.setSsid(ssid);

			String ua = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.UA)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.UA)) : "";
			mPhoneDataBean.setUa(ua);

			String model = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.MODEL)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.MODEL))
					: "";
			mPhoneDataBean.setModel(model);

			String manufacturer = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.MANUFACTURER))
					.length() > 0 ? mCursor.getString(mCursor
					.getColumnIndex(PhoneDataBean.MANUFACTURER)) : "";
			mPhoneDataBean.setManufacturer(manufacturer);

			String product = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.PRODUCT)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.PRODUCT))
					: "";
			mPhoneDataBean.setProduct(product);

			String androidCode = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.ANDROIDCODE)).length() > 0 ? mCursor
					.getString(mCursor
							.getColumnIndex(PhoneDataBean.ANDROIDCODE)) : "";
			mPhoneDataBean.setAndroidCode(androidCode);

			String Density = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.DENSITY)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.DENSITY))
					: "";
			mPhoneDataBean.setDensity(Density);

			String systemCode = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.SYSTEMCODE)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.SYSTEMCODE))
					: "";
			mPhoneDataBean.setSystemCode(systemCode);

			String datatype = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.DATATYPE)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.DATATYPE))
					: "";
			mPhoneDataBean.setDatatype(datatype);

		}
		return mPhoneDataBean;

	}

	public PhoneDataBean getPhoneDataBeanById(int id) {
		// System.out.println("id--->" + id);
		String sql = "select * from tb_phone where _id = '" + id + "'";
		// System.out.println("sql---->" + sql);
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor == null) {
			return null;
		}
		PhoneDataBean mPhoneDataBean = null;
		while (mCursor.moveToNext()) {
			mPhoneDataBean = new PhoneDataBean();
			String serial = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.SERIAL)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.SERIAL))
					: "";
			mPhoneDataBean.setSerial(serial);

			String latitude = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.LATITUDE)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.LATITUDE))
					: "";
			mPhoneDataBean.setLatitude(latitude);

			String longitude = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.LONGITUDE)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.LONGITUDE))
					: "";
			mPhoneDataBean.setLongitude(longitude);

			String altitude = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.ALTITUDE)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.ALTITUDE))
					: "";
			mPhoneDataBean.setAltitude(altitude);

			String macAddress = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.MACADDRESS)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.MACADDRESS))
					: "";
			mPhoneDataBean.setMacAddress(macAddress);
			
			String bmacAddress = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.BMACADDRESS)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.BMACADDRESS))
					: "";
			mPhoneDataBean.setBMacAddress(bmacAddress);

			String ipAddress = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.IPADDRESS)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.IPADDRESS))
					: "";
			mPhoneDataBean.setIpAddress(ipAddress);

			String imei = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.IMEI)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.IMEI)) : "";
			mPhoneDataBean.setImei(imei);

			String phoneNumber = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.PHONENUMBER)).length() > 0 ? mCursor
					.getString(mCursor
							.getColumnIndex(PhoneDataBean.PHONENUMBER)) : "";
			mPhoneDataBean.setPhoneNumber(phoneNumber);

			String advertisementID = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.ADVERTISEMENTID))
					.length() > 0 ? mCursor.getString(mCursor
					.getColumnIndex(PhoneDataBean.ADVERTISEMENTID)) : "";
			mPhoneDataBean.setAdvertisementID(advertisementID);

			String gsfId = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.GSFID)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.GSFID))
					: "";
			mPhoneDataBean.setGsfId(gsfId);

			String androidID = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.ANDROIDID)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.ANDROIDID))
					: "";
			mPhoneDataBean.setAndroidID(androidID);

			String mnc = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.MNC)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.MNC)) : "";
			mPhoneDataBean.setMnc(mnc);

			String mcc = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.MCC)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.MCC)) : "";
			mPhoneDataBean.setMcc(mcc);

			String country = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.COUNTRY)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.COUNTRY))
					: "";
			mPhoneDataBean.setCountry(country);

			String operator = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.OPERATOR)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.OPERATOR))
					: "";
			mPhoneDataBean.setOperator(operator);

			String iccId = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.ICCID)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.ICCID))
					: "";
			mPhoneDataBean.setIccId(iccId);

			String gsmCallID = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.GSMCALLID)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.GSMCALLID))
					: "";
			mPhoneDataBean.setGsmCallID(gsmCallID);

			String gsmLac = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.GSMLAC)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.GSMLAC))
					: "";
			mPhoneDataBean.setGsmLac(gsmLac);

			String iMSI = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.IMSI)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.IMSI)) : "";
			mPhoneDataBean.setImsi(iMSI);

			String ssid = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.SSID)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.SSID)) : "";
			mPhoneDataBean.setSsid(ssid);

			String ua = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.UA)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.UA)) : "";
			mPhoneDataBean.setUa(ua);

			String model = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.MODEL)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.MODEL))
					: "";
			mPhoneDataBean.setModel(model);

			String manufacturer = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.MANUFACTURER))
					.length() > 0 ? mCursor.getString(mCursor
					.getColumnIndex(PhoneDataBean.MANUFACTURER)) : "";
			mPhoneDataBean.setManufacturer(manufacturer);

			String product = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.PRODUCT)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.PRODUCT))
					: "";
			mPhoneDataBean.setProduct(product);

			String androidCode = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.ANDROIDCODE)).length() > 0 ? mCursor
					.getString(mCursor
							.getColumnIndex(PhoneDataBean.ANDROIDCODE)) : "";
			mPhoneDataBean.setAndroidCode(androidCode);

			String Density = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.DENSITY)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.DENSITY))
					: "";
			mPhoneDataBean.setDensity(Density);

			String systemCode = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.SYSTEMCODE)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.SYSTEMCODE))
					: "";
			mPhoneDataBean.setSystemCode(systemCode);

			String datatype = mCursor.getString(
					mCursor.getColumnIndex(PhoneDataBean.DATATYPE)).length() > 0 ? mCursor
					.getString(mCursor.getColumnIndex(PhoneDataBean.DATATYPE))
					: "";
			mPhoneDataBean.setDatatype(datatype);

		}
		return mPhoneDataBean;
	}

	public int[] getLastnewCord(String taskname,List<String> packagename) {
		SharedPreferences appcord = ApplicationEx.getContextObject()
				.getSharedPreferences("appcord", Context.MODE_PRIVATE);
		String packmd5 = Util.MD5(packagename);
		String sql = "select _id from taskapp where taskname='"+taskname+"'"
					+" AND packagename='"+packmd5+"'";
		int rst[] = {1, 0};
		Cursor mCursor = db.rawQuery(sql, null);
        if((mCursor != null) && (mCursor.getCount() > 0)) {
            mCursor.moveToFirst();
            int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            rst[1] = id;            
        }
        
        rst[0] = appcord.getInt(packmd5+"_"+taskname, 1);
        //TODO 修改任务和应用关联关系
        /*if(packagename != null && packagename.size()>0){
            int maxid = 0;         
        	for(String temp:packagename){  	
            	int currentid = appcord.getInt(temp, 1);
            	maxid = maxid > currentid ? maxid : currentid;
            	rst[0] = maxid;
            }        
        }  */
		return rst;
	}
	
	public void updateLastnewCord(String taskname, int newadd) {
        SharedPreferences appcord = ApplicationEx.getContextObject().getSharedPreferences("appcord", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appcord.edit();
        String sql = "select packagename from taskapp where taskname='"+taskname+"'";
        Cursor mCursor = db.rawQuery(sql, null);
        if((mCursor != null) && (mCursor.getCount() > 0)) {
        	mCursor.moveToFirst();
        	do{
        		String appname = mCursor.getString(mCursor.getColumnIndex("packagename"));
                int tmp = appcord.getInt(appname+"_"+taskname, 1) + newadd;
                editor.putInt(appname+"_"+taskname, tmp);
                editor.commit();
        	}while(mCursor.moveToNext());
        }
        
    }
	
	public void cleandatabaserecord(String appname){
	        SharedPreferences appcord = ApplicationEx.getContextObject().getSharedPreferences("appcord", Context.MODE_PRIVATE);
	        SharedPreferences.Editor editor = appcord.edit();
	        editor.putInt(appname, 0);
            editor.commit();
	}
	
	public void addtaskapp(String taskname, List<String> packagename) {
		String sql = taskname;
		String temp = Util.MD5(packagename);
		sql = "select * from taskapp where packagename='" + temp
				+ "' AND taskname='" + taskname + "'";
		Cursor rst = db.rawQuery(sql, null);
		if ((rst != null) && (rst.getCount() > 0)) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put("taskname", taskname);
		values.put("packagename", temp);
		db.insert("taskapp", null, values);
    }

	public int gethour(int taskid, int leave) {
		String sql = "select hour from newdata where taskid="+taskid + " order by day DESC";
		Cursor cursor = db.rawQuery(sql, null);
		if ((cursor == null) || (cursor.getCount() <= 0)) {
			return 0;
		}
		cursor.moveToFirst();
		int time = 0;
		if (leave < 24) {
			time = new Date().getHours()
					- cursor.getInt(cursor.getColumnIndex("hour"));
		}
		else if ((leave < 48) && (cursor.moveToNext())) {
			time = (new Date().getHours() + 24)
					- cursor.getInt(cursor.getColumnIndex("hour"));
		}
		else if ((leave < 72) && (cursor.moveToNext())) {
			if (cursor.moveToNext()) {
				return time = (new Date().getHours() + 48)
						- cursor.getInt(cursor.getColumnIndex("hour"));
			}
		}
		return time;
	}

	public void initnewdatatable(int taskid, int startid, int newadd) {
		String sql = "select MAX(day) as mday from newdata where taskid="+taskid;
		Cursor mCursor = db.rawQuery(sql, null);
		int value = 0;
		if ((mCursor != null) && (mCursor.getCount() > 0)) {
			mCursor.moveToFirst();
			value = mCursor.getInt(mCursor.getColumnIndex("mday"));
		}
		ContentValues values = new ContentValues();
		values.put("taskid", taskid);
		values.put("startdataid", startid);
		values.put("enddataid", (startid + newadd));
		values.put("day", value + 1);
		values.put("hour", new Date().getHours());
		values.put("current", startid);
		db.insert("newdata", null, values);
	}

	public void initseconddatatable(int timevalue, int taskid) {
		String sql = "select startdataid, enddataid, current from newdata where taskid=" + taskid + " order by day DESC";
		Cursor cursor = db.rawQuery(sql, null);
		if ((cursor == null) || (cursor.getCount() < 1)) {
			return;
		}
		cursor.moveToFirst();
		int enddataid = 0;
		int startdataid = 0;
		int current = 0;
		if (timevalue < 24) {
			startdataid = cursor.getInt(cursor.getColumnIndex("startdataid"));
			enddataid = cursor.getInt(cursor.getColumnIndex("enddataid"));
			current = cursor.getInt(cursor.getColumnIndex("current"));
			setbacktable(startdataid, enddataid, current);
		}else if (timevalue < 48) {
			if (cursor.moveToNext()) {
				startdataid = cursor.getInt(cursor
						.getColumnIndex("startdataid"));
				enddataid = cursor.getInt(cursor.getColumnIndex("enddataid"));
				current = cursor.getInt(cursor.getColumnIndex("current"));
				setbacktable(startdataid, enddataid, current);
			}
		}else if (timevalue < 72) {
			if (cursor.moveToNext()) {
				if (cursor.moveToNext()) {
					startdataid = cursor.getInt(cursor
							.getColumnIndex("startdataid"));
					enddataid = cursor.getInt(cursor
							.getColumnIndex("enddataid"));
					current = cursor.getInt(cursor.getColumnIndex("current"));
					setbacktable(startdataid, enddataid, current);
				}
			}
		}
	}

	private void setbacktable(int start, int end, int current) {
		for(int i=start; i<end; i++){
			ContentValues cv = new ContentValues();
			cv.put("dataid", i);
			cv.put("visted", 0);
			try{
				db.insert("backrecord", null, cv);
			}catch(Exception e){
				
			}
			
		}
	}

	public Random myrandom = null;

	public void deletebackrecordtable() {
		db.delete("backrecord", null, null);
	}

	public static String getCurrentTaskname() {
		SharedPreferences preferences = ApplicationEx.getContextObject()
				.getSharedPreferences("task", Context.MODE_PRIVATE);
		return preferences.getString("currenttask", null);
	}
	
	public static String getTaskstartime(int id) {
		SharedPreferences preferences = ApplicationEx.getContextObject()
				.getSharedPreferences("task", Context.MODE_PRIVATE);
		return preferences.getString("taskstarttime"+id, null);
	}
	
	public static boolean isallowvoice() {
		String filepath = Environment.getExternalStorageDirectory().getPath()+File.separator+"allowvoice";
		File file = new File(filepath);
		if(file.exists()){
			try {
				DataInputStream dis = new DataInputStream(new FileInputStream(file));
				boolean rst = dis.readBoolean();
				dis.close();
				return rst;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	public static void setallowvoice(boolean allow) {
		String filepath = Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "allowvoice";
		File file = new File(filepath);
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(
					file));
			dos.writeBoolean(allow);
			dos.flush();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setTaskstarttime(String value, int id){
		SharedPreferences preferences = ApplicationEx.getContextObject()
				.getSharedPreferences("task", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("taskstarttime"+id, value).commit();
	}
	
	public static void setGetStayway(int getway){
		SharedPreferences preferences = ApplicationEx.getContextObject()
				.getSharedPreferences("task", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("getway", getway);
		editor.commit();
	}
	
	public static int getGetStayway(){
		SharedPreferences preferences = ApplicationEx.getContextObject()
				.getSharedPreferences("task", Context.MODE_PRIVATE);
		return preferences.getInt("getway", 0);
	}
	
	public static void setsecondelive(boolean on){
		SharedPreferences preferences = ApplicationEx.getContextObject()
				.getSharedPreferences("task", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("secondliveflag", on);
		editor.commit();
	}

	public static boolean issecondelive(){
		SharedPreferences preferences = ApplicationEx.getContextObject()
				.getSharedPreferences("task", Context.MODE_PRIVATE);
		return preferences.getBoolean("secondliveflag", false);
	}
	
	public static void setsecondlivef(int value){
		SharedPreferences preferences = ApplicationEx.getContextObject()
				.getSharedPreferences("task", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("secondlivef", value);
		editor.commit();
	}
	public static void setsecondlives(int value){
		SharedPreferences preferences = ApplicationEx.getContextObject()
				.getSharedPreferences("task", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("secondlives", value);
		editor.commit();
	}
	public static int getsecondef(){
		SharedPreferences preferences = ApplicationEx.getContextObject()
				.getSharedPreferences("task", Context.MODE_PRIVATE);
		return preferences.getInt("secondlivef", 10);
	}
	
	public static int getsecondes(){
		SharedPreferences preferences = ApplicationEx.getContextObject()
				.getSharedPreferences("task", Context.MODE_PRIVATE);
		return preferences.getInt("secondlives", 50);
	}
	
	
	private void secondtest() {
		if ((ApplicationInfoEx.secondef) && (ApplicationInfoEx.secondes)) {
			return;
		}

		if (!issecondelive()) {
			return;
		}
		String currenttask = getCurrentTaskname();
		int taskid = getLastnewCord(currenttask,
				getListapp())[1];

		int first = getsecondef();
		//int seconde = getsecondes();

		//修改为只有一个时间的模式 
		if ((!ApplicationInfoEx.secondef) && (first > 48)&& (gethour(taskid, 60) >= first)) {
			ApplicationInfoEx.secondef = true;
			initseconddatatable(60, taskid);
		} else if ((!ApplicationInfoEx.secondef)&& (first > 24) && (gethour(taskid, 30) >= first)) {
			ApplicationInfoEx.secondef = true;
			initseconddatatable(20, taskid);
		} else if ((!ApplicationInfoEx.secondef) && (first > 3) && (gethour(taskid, 10) >= first)) {
			initseconddatatable(4, taskid);
			ApplicationInfoEx.secondef = true;
		}
		/*if ((!ApplicationInfoEx.secondes) && (seconde > 48)
				&& (gethour(taskid, 60) >= seconde)) {
			ApplicationInfoEx.secondes = true;
			initseconddatatable(60, taskid);
		} else if ((!ApplicationInfoEx.secondes) && (seconde > 24)
				&& (gethour(taskid, 30) >= seconde)) {
			ApplicationInfoEx.secondes = true;
			initseconddatatable(20, taskid);
		}*/
	}

	public static List<String> getListapp() {
		try {
			String[] arrayOfString = new BufferedReader(new InputStreamReader(
					ApplicationEx.getContextObject().openFileInput("applistselect"))).readLine().split("[|]");
			List<String> list = new ArrayList<String>();
			if(arrayOfString.length>0){
				for(String temp:arrayOfString){
					list.add(temp);
				}
			}
			return list;
		} catch (Exception localException) {
		}
		return null;
    }
	
	private PhoneDataBean getfromnewdata() {
		PhoneDataBean data = null;
		int taskid = getLastnewCord(getCurrentTaskname(),getListapp())[1];
		String sql = "select enddataid, current, day from newdata where taskid="+ taskid + " order by day DESC";
		Cursor cursor = db.rawQuery(sql, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
			int endid = cursor.getInt(cursor.getColumnIndex("enddataid"));
			int id = cursor.getInt(cursor.getColumnIndex("current"));
			int day = cursor.getInt(cursor.getColumnIndex("day"));
			if (id <= endid) {
				data = getPhoneDataBeanById(id);
				if(data == null){
					Util.error_code = Util.error.NoPhoneInfo.ordinal();
					return null;
				}
				ContentValues values = new ContentValues();
				values.put("current", id + 1);
				db.update("newdata", values, "day=?", new String[] { day + "" });
			}
		}else{
			Util.error_code = Util.error.FinishedNEWDATA.ordinal();
		}
		return data;
	}

	private PhoneDataBean getfrombackrecord() {
		String sql = "select dataid from backrecord where visted=0";
		PhoneDataBean data = null;
		Cursor cursor = db.rawQuery(sql, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
			int _id = cursor.getInt(cursor.getColumnIndex("dataid"));
			data = getPhoneDataBeanById(_id);
			if(data == null){
				Util.error_code = Util.error.NoPhoneInfo.ordinal();
				return null;
			}
			ContentValues value = new ContentValues();
			value.put("visted", 1);
			db.update("backrecord", value, "dataid=?",new String[] { _id + "" });
		}else{
			Util.error_code = Util.error.FinishedBackrecord.ordinal();
		}
		return data;
	}

	static boolean isfollownewdata = true;
	
	public void clean_task(String taskname) {
		String str1 = "delete from newdata where taskid IN (select _id from taskapp where taskname='"
				+ taskname + "')";
		this.db.execSQL(str1);
		String str2 = "delete from taskapp where taskname='" + taskname
				+ "'";
		this.db.execSQL(str2);
		String str3 = "delete from tb_task_attribute where tb_TaskName='"
				+ taskname + "'";
		this.db.execSQL(str3);
	}

	public void clean_task_cache(String taskname) {
		String str1 = "delete from newdata where taskid IN (select _id from taskapp where taskname='"
				+ taskname + "')";
		this.db.execSQL(str1);
		String str2 = "delete from taskapp where taskname='" + taskname
				+ "'";
		this.db.execSQL(str2);
		this.db.execSQL("delete from backrecord");
	}


	public PhoneDataBean getNextData(int stayway) {
		secondtest();
		Util.error_code = Util.error.Normal.ordinal();
		
		stayway = getGetStayway();
		
		PhoneDataBean data = null;
		if (stayway == 0) {
			data = getfromnewdata();
			if (data == null) {
				data = getfrombackrecord();
			}
		}else if (stayway == 0x1) {
			data = getfrombackrecord();			
			if (data == null) {
				data = getfromnewdata();
			}
		} else if (stayway == 0x2) {
			if (getRandomboolean(getnewdatacount(getLastnewCord(getCurrentTaskname(), getListapp())[1])[1], getbackdatacount()[1])) {
				data = getfromnewdata();
				if (data == null) {
					data = getfrombackrecord();
				}
			} else {
				data = getfrombackrecord();
				if (data == null) {
					data = getfromnewdata();
				}
			}
		} else if (stayway == 0x3) {
			data = getfrombackrecord();
		} else if (stayway == 0x4) {
			if (isfollownewdata) {
				data = getfromnewdata();
				if (data == null) {
					data = getfrombackrecord();
				}
				isfollownewdata = false;
			} else {
				data = getfrombackrecord();
				if (data == null) {
					data = getfromnewdata();
				}
				isfollownewdata = true;
			}
		}
		
		//TODO 提示信息
		if(data == null){
			Intent localIntent = new Intent();
	        localIntent.setClass(ApplicationEx.getContextObject(), UpdateService.class);
	        int action = 0;
	        if(Util.error_code == Util.error.FinishedBackrecord.ordinal()
	        		|| Util.error_code == Util.error.FinishedNEWDATA.ordinal()){
	        	action = UpdateService.cActionFinished;
	        }else if(Util.error_code == Util.error.NoPhoneInfo.ordinal()){
	        	action = UpdateService.cActionDataNull;
	        }
	        
	        localIntent.putExtra("Action", action);
	        ApplicationEx.getContextObject().startService(localIntent);
		}
		return data;
	}
	
	private boolean getRandomboolean(int a, int b){
		int max,min;
		boolean isnormal = true;
		boolean rst = false;
		if(a > b){
			max=a;
			min=b;
		}{
			isnormal = false;
			max=b;
			min=a;
		}
		
		Random random = new Random();
		int randomint = random.nextInt(max+min);
		if(randomint >= min){
			rst = true;
		}else{
			rst = false;
		}
		if(!isnormal){
			rst = !rst;
		}
		return rst;
	}
	
	private void insertback(int id){
		ContentValues cv = new ContentValues();
		cv.put("dataid", id);
		cv.put("visted", 0);
		try{
			db.insert("backrecord", null, cv);
		}catch(Exception e){
			
		}
	}
	
	public void initnextdaydatatable(int taskid, int margeday, int times,
			double stay, int stayway, double lowlv, int lowest, boolean tobelow) {
		
		String sql = "select startdataid, enddataid, current from newdata where taskid="+taskid+" order by day DESC";
		Cursor cursor = db.rawQuery(sql, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			double staylv = stay/100;
			int listentimes = 0;
			if(stayway == 0){
				//随机抽取
				if(myrandom == null){
					myrandom = new Random();
				}
			}
			cursor.moveToFirst();
			while(cursor.moveToNext()){				
				
				int start=0;
				int end = 0;
				
				if((times > 0) && (listentimes >= times)){
					break;
				}
				
				for(int i=0; i<margeday; i++){
					if(!cursor.moveToNext()){
						return;
					}
				}
				
				start = cursor.getInt(0);
				end = cursor.getInt(1);
				
				int count = end - start;
				int num = (int) (count * staylv);
				/*if(tobelow){
					num = num > lowest? num : lowest;
				}*/
				int breakcount = 0;
				for(int t = start; t < end; t++){					
					if(stayway == 0){
						//随机抽取
						if(myrandom.nextBoolean()
								|| breakcount >= count - num){
							if(t - start -breakcount >= num){
								break;
							}
							insertback(t);
						}else{
							breakcount++;
						}
					}else{
						//顺序抽取
						setbacktable(start, start+num, start);
					}
				}				
				if(tobelow){
					double newlv = staylv * (lowlv/100);
					if(newlv > lowest/100){
						staylv = newlv;
					}else{
						staylv = lowest/100;
					}
				}
				if((times != -1)){
					listentimes++;		
				}						
			}			
		}
	}

	public void initnextweekdatatable(int taskid, double stay, int stayway,
			double lowlv, int lowest, boolean tobelow) {
		initnextdaydatatable(taskid, 7, -1, stay, stayway, lowlv, lowest,
				tobelow);
	}

	public void initnextmonthdatatable(int taskid, double stay, int stayway,
			double lowlv, int lowest, boolean tobelow) {
		initnextdaydatatable(taskid, 30, -1, stay, stayway, lowlv, lowest,
				tobelow);
	}

	public int getTaskLastDay(String taskname, List<String> packagename) {
		int taskid = getLastnewCord(taskname, packagename)[0x1];
		String sql = "select day from newdata where taskid="+taskid+" order by _id DESC";
		Cursor cursor = db.rawQuery(sql, null);
		if ((cursor == null) || (cursor.getCount() < 1)) {
			return 0;
		}
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex("day"));
	}
	
	public int[] getnewdatacount(int taskid){
		int[] rst = {0,0};
		String sql = "select startdataid, enddataid, current from newdata where taskid="+taskid+" order by _id DESC";
		Cursor cursor = db.rawQuery(sql, null);
		if ((cursor == null) || (cursor.getCount() < 1)) {
			return rst;
		}
		cursor.moveToFirst();
		rst[1] = cursor.getInt(cursor.getColumnIndex("enddataid")) - 
							cursor.getInt(cursor.getColumnIndex("startdataid"));
		rst[0] = cursor.getInt(cursor.getColumnIndex("current")) - 
				cursor.getInt(cursor.getColumnIndex("startdataid"));
		return rst;
	}
	
	public int[] getbackdatacount(){
		int[] rst = {0,0};
		String sql = "select COUNT(*) as count from backrecord";
		String sql2 = "select COUNT(*) as count from backrecord where visted=1";
		Cursor cursor = db.rawQuery(sql, null);
		if ((cursor == null) || (cursor.getCount() < 1)) {
			return rst;
		}
		cursor.moveToFirst();
		rst[1] = cursor.getInt(cursor.getColumnIndex("count"));
		
		cursor = db.rawQuery(sql2, null);
		if ((cursor == null) || (cursor.getCount() < 1)) {
			return rst;
		}
		cursor.moveToFirst();
		rst[0] = cursor.getInt(cursor.getColumnIndex("count"));
		return rst;
	}
	
	public int getdayinfo(int taskid){
		int rst = 0;
		String sql = "select MAX(day) as mday from newdata where taskid="+taskid;
		Cursor cursor = db.rawQuery(sql, null);
		if ((cursor == null) || (cursor.getCount() < 1)) {
			return rst;
		}
		cursor.moveToFirst();
		rst = cursor.getInt(cursor.getColumnIndex("mday"));
		return rst;
	}

}
