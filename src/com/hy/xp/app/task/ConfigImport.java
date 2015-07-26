package com.hy.xp.app.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ConfigImport
{

	private Context mContext;
	ProgressDialog mProgressDialog;

	public ConfigImport(Context mContext)
	{
		this.mContext = mContext;
	}

	public void Import_Task(ExecutorService mExecutor)
	{
		CurlFileTask mCurlFileTask = new CurlFileTask();
		mCurlFileTask.executeOnExecutor(mExecutor, null);
	}

	/* ??版?????浠? */
	private void createfile_data_file() throws Exception
	{
		// TODO Auto-generated method stub
		String state = Environment.getExternalStorageState();
		String path = null;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			path = sdcardDir.getPath() + "/xp_datafile/";
		}
		List<DataBase> mBeans = readcurray_DataBase(path, "task_data_file.json");
		for (int i = 0; i < mBeans.size(); i++) {
			// System.out.println(mBeans.get(i).toString());
			ContentValues values = new ContentValues();
			values.put(DataBase.TASK_NAME, mBeans.get(i).getTask_name());
			values.put(DataBase.TASK_DATA_FILE, mBeans.get(i).getTask_date_file());
			DBMgr.getInstance(mContext).add_task_data_file_improt(mContext, values);
		}

	}

	/* 璇诲?????浠跺????? */
	private void createfile_attribute() throws Exception
	{
		// TODO Auto-generated method stub
		String state = Environment.getExternalStorageState();
		String path = null;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			path = sdcardDir.getPath() + "/xp_datafile/";
		}
		List<TaskAttribute> mBeans = readcurray(path, "task_attribute.json");
		for (int i = 0; i < mBeans.size(); i++) {
			// System.out.println(mBeans.get(i).toString());
			ContentValues values = new ContentValues();
			values.put(TaskAttribute.TASKNAME, mBeans.get(i).getTaskName());
			values.put(TaskAttribute.TASKDESC, mBeans.get(i).getTaskDesc());
			values.put(TaskAttribute.TASKNEWDATA, mBeans.get(i).getTaskNewdata());
			values.put(TaskAttribute.TASKNUMBER, mBeans.get(i).getTaskNumber());
			values.put(TaskAttribute.TASKRETURNRATIO, mBeans.get(i).getTaskReturnratio());
			values.put(TaskAttribute.TASKDECLINEFLAG, mBeans.get(i).isTaskDeclineFlag());
			values.put(TaskAttribute.TASKDECILNERATIO, mBeans.get(i).getTaskDecilneRatio());
			values.put(TaskAttribute.TASKDECILNEMIN, mBeans.get(i).getTaskDecilneMin());
			values.put(TaskAttribute.TASKNEXTDAYFLAG, mBeans.get(i).isTaskNextDayFlag());
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVAL, mBeans.get(i).getTaskNextDayVisitInterval());
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALRETURNRATIO, mBeans.get(i).getTaskNextDayVisitIntervalReturnRatio());
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALCOUNT, mBeans.get(i).getTaskNextDayVisitIntervalCount());
			values.put(TaskAttribute.TASKNEXTDAYVISITDECLINEFLAG, mBeans.get(i).isTaskNextDayVisitDeclineFlag());
			values.put(TaskAttribute.TASKNEXTDAYVISITDECILNERATIO, mBeans.get(i).getTaskNextDayVisitDecilneRatio());
			values.put(TaskAttribute.TASKNEXTDAYVISITDECILNEMIN, mBeans.get(i).getTaskNextDayVisitDecilneMin());
			DBMgr.getInstance(mContext).add_task_attribute_improt(mContext, values);

		}
	}

	public static List<DataBase> readcurray_DataBase(String path, String filename) throws Exception
	{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String mPath = path + "/" + filename;
			String result = BufferedReaderJSON(mPath);

			if (null != result) {
				Gson mGson = new Gson();
				List<DataBase> mBases = mGson.fromJson(result, new TypeToken<List<DataBase>>()
				{
				}.getType());
				return mBases;
			}
		}
		return null;
	}

	public static List<TaskAttribute> readcurray(String path, String filename) throws Exception
	{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String mPath = path + "/" + filename;
			String result = BufferedReaderJSON(mPath);

			if (null != result) {
				Gson mGson = new Gson();
				List<TaskAttribute> mBases = mGson.fromJson(result, new TypeToken<List<TaskAttribute>>()
				{
				}.getType());
				return mBases;
			}
		}
		return null;
	}

	private class CurlFileTask extends AsyncTask<Void, Void, String>
	{
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("正在导入任务！");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params)
		{
			// TODO Auto-generated method stub
			try {
				createfile_attribute();
				createfile_data_file();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String result)
		{
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mProgressDialog.dismiss();
		}
	}

	private static String BufferedReaderJSON(String str) throws Exception
	{
		File file = new File(str);
		if (!file.exists() || file.isDirectory()) {
			// throw new FileNotFoundException();
			return null;
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
