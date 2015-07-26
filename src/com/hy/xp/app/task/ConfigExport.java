package com.hy.xp.app.task;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.gson.Gson;
import com.hy.xp.app.Util;

public class ConfigExport
{

	private Context mContext;
	ProgressDialog mProgressDialog;

	public ConfigExport(Context mContext)
	{
		this.mContext = mContext;
	}

	public void export_Task(ExecutorService mExecutor)
	{
		CurlFileTask mCurlFileTask = new CurlFileTask();
		mCurlFileTask.executeOnExecutor(mExecutor, null);
	}

	Gson mGson = new Gson();

	private class CurlFileTask extends AsyncTask<Void, Void, Void>
	{
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("正在导出任务！");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			// TODO Auto-generated method stub
			createfile();
			return null;
		}

		protected void onPostExecute(Void result)
		{
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mProgressDialog.dismiss();

		}

	}

	private void createfile()
	{
		String state = Environment.getExternalStorageState();
		String path = null;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			path = sdcardDir.getPath() + "/xp_datafile/";
		}

		String str = "";
		String str2 = "";
		// TODO Auto-generated method stub
		List<TaskAttribute> mTaskBeans = DBMgr.getInstance(mContext).getTaskList();
		List<DataBase> mDataBases = DBMgr.getInstance(mContext).getPhoneDataBeanListToImport();

		try {
			str = mGson.toJson(mTaskBeans);
			Util.createRandomcodeFile(mContext, str, path, "task_attribute.json");

			str2 = mGson.toJson(mDataBases);
			Util.createRandomcodeFile(mContext, str2, path, "task_data_file.json");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
