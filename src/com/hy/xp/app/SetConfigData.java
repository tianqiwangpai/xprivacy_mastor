package com.hy.xp.app;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.CallLog;

import com.google.gson.Gson;
import com.hy.xp.app.task.PhoneDataBean;
import com.hy.xp.app.task.XpHelper;

public class SetConfigData
{

	@SuppressLint("SimpleDateFormat")
	public static void SetDataByfile(Context mContext, PhoneDataBean mPhoneDataBean)
	{

		if(mPhoneDataBean != null){
			Intent changeIntent = new Intent();
	        changeIntent.setClass(ApplicationEx.getContextObject(), UpdateService.class);
	        changeIntent.putExtra("Action", UpdateService.cActionReady);
	        ApplicationEx.getContextObject().startService(changeIntent);
		}else{
			return;
		}
		
		String s = Environment.getExternalStorageState();
		String path = null;
		if (Environment.MEDIA_MOUNTED.equals(s)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			path = sdcardDir.getPath() + "/xp_datafile/";
			File tmp = new File(path);
			if(!tmp.exists()){
				tmp.mkdir();
			}
		}
		Gson mGson = new Gson();
		mGson.toJson(mPhoneDataBean);
		XpHelper.CreateTaskNameDataFile(mContext, mGson.toJson(mPhoneDataBean), path, "setting");
	}
	
	
	@SuppressLint("SimpleDateFormat")
	public static void SetDataByfile_app(Context mContext, List<Applist> applist)
	{

		if(applist != null){
			Intent changeIntent = new Intent();
	        changeIntent.setClass(ApplicationEx.getContextObject(), UpdateService.class);
	        changeIntent.putExtra("Action", UpdateService.cActionReady);
	        ApplicationEx.getContextObject().startService(changeIntent);
		}else{
			return;
		}
		
		String s = Environment.getExternalStorageState();
		String path = null;
		if (Environment.MEDIA_MOUNTED.equals(s)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			path = sdcardDir.getPath() + "/xp_datafile/";
			File tmp = new File(path);
			if(!tmp.exists()){
				tmp.mkdir();
			}
		}
		Gson mGson = new Gson();
		mGson.toJson(applist);
		XpHelper.CreateTaskNameDataFile(mContext, mGson.toJson(applist), path, "setting_applist");
	}
	
	@SuppressLint("SimpleDateFormat")
	public static void SetDataByfile_contacts(Context mContext, List<Contacts> mcontacts)
	{

		if(mcontacts != null){
			Intent changeIntent = new Intent();
	        changeIntent.setClass(ApplicationEx.getContextObject(), UpdateService.class);
	        changeIntent.putExtra("Action", UpdateService.cActionReady);
	        ApplicationEx.getContextObject().startService(changeIntent);
		}else{
			return;
		}
		
		String s = Environment.getExternalStorageState();
		String path = null;
		if (Environment.MEDIA_MOUNTED.equals(s)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			path = sdcardDir.getPath() + "/xp_datafile/";
			File tmp = new File(path);
			if(!tmp.exists()){
				tmp.mkdir();
			}
		}
		Gson mGson = new Gson();
		mGson.toJson(mcontacts);
		XpHelper.CreateTaskNameDataFile(mContext, mGson.toJson(mcontacts), path, "setting_contacts");
	}
	
	
	@SuppressLint("SimpleDateFormat")
	public static void SetDataByfile_calllog(Context mContext, List<Calllog> mcalllog)
	{

		if(mcalllog != null){
			Intent changeIntent = new Intent();
	        changeIntent.setClass(ApplicationEx.getContextObject(), UpdateService.class);
	        changeIntent.putExtra("Action", UpdateService.cActionReady);
	        ApplicationEx.getContextObject().startService(changeIntent);
		}else{
			return;
		}
		
		String s = Environment.getExternalStorageState();
		String path = null;
		if (Environment.MEDIA_MOUNTED.equals(s)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			path = sdcardDir.getPath() + "/xp_datafile/";
			File tmp = new File(path);
			if(!tmp.exists()){
				tmp.mkdir();
			}
		}
		Gson mGson = new Gson();
		mGson.toJson(mcalllog);
		XpHelper.CreateTaskNameDataFile(mContext, mGson.toJson(mcalllog), path, "setting_calllog");
	}
}
