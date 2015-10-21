package com.hy.xp.app;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Parcel;
import android.util.Log;

public class XActivityManager extends XHook
{
	private Methods mMethod;
	private String mClassName;
	private static final String cClassName = "android.app.ActivityManager";
	private static Map<String, String> mapIntentRestriction = new HashMap<String, String>();

	static
	{
		mapIntentRestriction.put(Intent.ACTION_VIEW, PrivacyManager.cView);
	}

	private XActivityManager(Methods method, String restrictionName, String className)
	{
		super(restrictionName, method.name().replace("Srv_", ""), method.name());
		mMethod = method;
		if (className == null)
			mClassName = "com.android.server.am.ActivityManagerService";
		else
			mClassName = className;
	}

	public String getClassName()
	{
		return mClassName;
	}

	// @formatter:off

	// public List<RecentTaskInfo> getRecentTasks(int maxNum, int flags)
	// public List<RunningAppProcessInfo> getRunningAppProcesses()
	// public List<RunningServiceInfo> getRunningServices(int maxNum)
	// public List<RunningTaskInfo> getRunningTasks(int maxNum)
	// frameworks/base/core/java/android/app/ActivityManager.java
	// http://developer.android.com/reference/android/app/ActivityManager.html

	// public List<ActivityManager.RecentTaskInfo> getRecentTasks(int maxNum,
	// int flags, int userId)
	// public List<ActivityManager.RunningAppProcessInfo>
	// getRunningAppProcesses()
	// public List<ActivityManager.RunningServiceInfo> getServices(int maxNum,
	// int flags)
	// public List<RunningTaskInfo> getTasks(int maxNum, int flags,
	// IThumbnailReceiver receiver)

	// public int startActivities(IApplicationThread caller, String
	// callingPackage, Intent[] intents, String[] resolvedTypes, IBinder
	// resultTo, Bundle options, int userId)
	// public int startActivity(IApplicationThread caller, String
	// callingPackage, Intent intent, String resolvedType, IBinder resultTo,
	// String resultWho, int requestCode, int flags, String profileFile,
	// ParcelFileDescriptor profileFd, Bundle options)
	// public int startActivityAsUser(IApplicationThread caller, String
	// callingPackage, Intent intent, String resolvedType, IBinder resultTo,
	// String resultWho, int requestCode, int flags, String
	// profileFile,ParcelFileDescriptor profileFd, Bundle options, int userId)
	// public WaitResult startActivityAndWait(IApplicationThread caller, String
	// callingPackage, Intent intent, String resolvedType, IBinder resultTo,
	// String resultWho, int requestCode, int flags, String profileFile,
	// ParcelFileDescriptor profileFd, Bundle options, int userId)
	// public int startActivityWithConfig(IApplicationThread caller, String
	// callingPackage, Intent intent, String resolvedType, IBinder resultTo,
	// String resultWho, int requestCode, int startFlags, Configuration
	// newConfig, Bundle options, int userId)

	// http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.4.2_r1/com/android/server/am/ActivityManagerService.java

	// @formatter:on

	// @formatter:off
	private enum Methods
	{
		getRecentTasks, getRunningAppProcesses, getRunningServices, getRunningTasks, Srv_getRecentTasks, Srv_getRunningAppProcesses, Srv_getServices, Srv_getTasks, Srv_startActivities, Srv_startActivity, Srv_startActivityAsUser, Srv_startActivityAndWait, Srv_startActivityWithConfig
	};

	// @formatter:on

	public static List<XHook> getInstances(String className)
	{
		List<XHook> listHook = new ArrayList<XHook>();
		if (!cClassName.equals(className))
		{
			if (className == null)
				className = cClassName;

			for (Methods act : Methods.values())
				if (act.name().startsWith("Srv_"))
					if (act.name().startsWith("Srv_start"))
						listHook.add(new XActivityManager(act, null, null));
					else
						listHook.add(new XActivityManager(act, PrivacyManager.cSystem, null));
				else
					listHook.add(new XActivityManager(act, PrivacyManager.cSystem, className));
		}
		return listHook;
	}

	@Override
	protected void before(XParam param) throws Throwable
	{
		switch (mMethod)
		{
		case getRecentTasks:
		case getRunningAppProcesses:
		case getRunningServices:
		case getRunningTasks:
		case Srv_getRecentTasks:
		case Srv_getRunningAppProcesses:
		case Srv_getServices:
		case Srv_getTasks:
			break;

		case Srv_startActivities:
			if (param.args.length > 2 && param.args[2] instanceof Intent[])
			{
				List<Intent> listIntent = new ArrayList<Intent>();
				for (Intent intent : (Intent[]) param.args[2])
					if (!isRestricted(param, intent))
						listIntent.add(intent);
				if (listIntent.size() == 0)
					param.setResult(0); // ActivityManager.START_SUCCESS
				else
					param.args[2] = listIntent.toArray(new Intent[0]);
			}
			break;

		case Srv_startActivity:
		case Srv_startActivityAsUser:
		case Srv_startActivityWithConfig:
			if (param.args.length > 2 && param.args[2] instanceof Intent)
			{
				Intent intent = (Intent) param.args[2];
				if (isRestricted(param, intent))
					param.setResult(0); // ActivityManager.START_SUCCESS
			}
			break;

		case Srv_startActivityAndWait:
			if (param.args.length > 2 && param.args[2] instanceof Intent)
			{
				Intent intent = (Intent) param.args[2];
				if (isRestricted(param, intent))
				{
					Class<?> cWaitResult = Class.forName("android.app.IActivityManager.WaitResult");
					Field fWho = cWaitResult.getDeclaredField("who");
					Class<?> we = this.getClass();
					ComponentName component = new ComponentName(we.getPackage().getName(), we.getName());

					Object waitResult = cWaitResult.getConstructor().newInstance();
					fWho.set(waitResult, component);
					param.setResult(waitResult);
				}
			}
			break;
		}
	}

	@Override
	protected void after(XParam param) throws Throwable
	{
		switch (mMethod)
		{
		case getRecentTasks:
		case Srv_getRecentTasks:
			if (param.getResult() != null && isRestricted(param))
				param.setResult(new ArrayList<ActivityManager.RecentTaskInfo>());
			break;

		case getRunningAppProcesses:
		case Srv_getRunningAppProcesses:
			if (param.getResult() != null && isRestricted(param))
			{
				ActivityManager.RunningAppProcessInfo runappinfo = ((ArrayList<ActivityManager.RunningAppProcessInfo>)param.getResult()).get(0);
				ArrayList<ActivityManager.RunningAppProcessInfo> result = new ArrayList<ActivityManager.RunningAppProcessInfo>();
				List<Applist> value = (List<Applist>) PrivacyManager
						.getDefacedProp(Binder.getCallingUid(), "Appinfo");
				Random r = new Random();
				for(Applist temp:value){
					Log.w("LTZRunningApp", "***Srv_getRunningAppProcesses**"+temp.getPkgname());
					Parcel p = Parcel.obtain();
					runappinfo.writeToParcel(p, 0);
					p.setDataPosition(0);
					ActivityManager.RunningAppProcessInfo runningappinfo = runappinfo.CREATOR.createFromParcel(p);
				    runningappinfo.processName = temp.getPkgname();
				    Parcel p2 = Parcel.obtain();
				    runningappinfo.importanceReasonComponent.writeToParcel(p2, 0);
				    p2.setDataPosition(0);
				    runningappinfo.importanceReasonComponent = runappinfo.importanceReasonComponent.clone();
				    runningappinfo.pkgList = new String[]{temp.getPkgname()};
					result.add(runningappinfo);
				}
				param.setResult(result);
			}
			break;
		case getRunningServices:
		case Srv_getServices:
			if (param.getResult() != null && isRestricted(param))
			{
				ActivityManager.RunningServiceInfo runningserviceinfo1 = ((ArrayList<RunningServiceInfo>) param.getResult()).get(0);
				ArrayList<ActivityManager.RunningServiceInfo> result = new ArrayList<ActivityManager.RunningServiceInfo>();
				List<Applist> value = (List<Applist>) PrivacyManager
						.getDefacedProp(Binder.getCallingUid(), "Appinfo");
				Random r = new Random();
				for(Applist temp:value){					
					Log.w("LTZgetRunningServices", "***Srv_getRunningServices**"+temp.getPkgname());
					//runningserviceinfo = new ActivityManager.RunningServiceInfo();
					Parcel p = Parcel.obtain();
					runningserviceinfo1.writeToParcel(p, 0);
					p.setDataPosition(0);
					ActivityManager.RunningServiceInfo runningserviceinfo = runningserviceinfo1.CREATOR.createFromParcel(p);
					runningserviceinfo.process = temp.getPkgname();					
					result.add(runningserviceinfo);
				}
				param.setResult(result);
				break;
			}
			break;

		case getRunningTasks:
		case Srv_getTasks:
			if (param.getResult() != null && isRestricted(param))
				param.setResult(new ArrayList<ActivityManager.RunningTaskInfo>());
			break;

		case Srv_startActivities:
		case Srv_startActivity:
		case Srv_startActivityAsUser:
		case Srv_startActivityAndWait:
		case Srv_startActivityWithConfig:
			break;
		}
	}

	// Helper methods

	private boolean isRestricted(XParam param, Intent intent) throws Throwable
	{
		String action = intent.getAction();
		if (mapIntentRestriction.containsKey(action))
		{
			String restrictionName = mapIntentRestriction.get(action);
			if (Intent.ACTION_VIEW.equals(action))
			{
				Uri uri = intent.getData();
				if (uri != null)
					return isRestrictedExtra(param, restrictionName, "Srv_" + action, uri.toString());
			} else
				return isRestricted(param, restrictionName, "Srv_" + action);
		}

		return false;
	}

}
