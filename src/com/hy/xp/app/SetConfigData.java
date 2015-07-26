package com.hy.xp.app;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.google.gson.Gson;
import com.hy.xp.app.task.PhoneDataBean;
import com.hy.xp.app.task.XpHelper;

public class SetConfigData
{

	@SuppressLint("SimpleDateFormat")
	public static void SetDataByfile(Context mContext, PhoneDataBean mPhoneDataBean)
	{

		if(mPhoneDataBean == null){
			Intent localIntent = new Intent();
		    localIntent.setClass(ApplicationEx.getContextObject(), UpdateService.class);
		    localIntent.putExtra("Action", UpdateService.cActionDataNull);
		    ApplicationEx.getContextObject().startService(localIntent);
		}
		
		String s = Environment.getExternalStorageState();
		String path = null;
		if (Environment.MEDIA_MOUNTED.equals(s)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			path = sdcardDir.getPath() + "/xp_datafile/";
		}
		Gson mGson = new Gson();
		mGson.toJson(mPhoneDataBean);
		XpHelper.CreateTaskNameDataFile(mContext, mGson.toJson(mPhoneDataBean), path, "setting");
	}
}
