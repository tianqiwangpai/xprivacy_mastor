package com.hy.xp.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hy.xp.app.task.DBMgr;
import com.hy.xp.app.task.PreferenceUtils;

/**
 * 
 * @ClassName: PackageChange
 * @Description: TODO
 * @date Mar 17, 2015 9:11:21 PM
 * 
 */
public class PackageChange extends BroadcastReceiver
{
	@Override
	public void onReceive(final Context context, Intent intent)
	{
		try {
			// Check uri
			Uri inputUri = intent.getData();
			if (inputUri.getScheme().equals("package")) {
				// Get data
				int uid = intent.getIntExtra(Intent.EXTRA_UID, 0);
				int userId = Util.getUserId(uid);
				boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
				boolean ondemand = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingOnDemand, true);
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

				Util.log(null, Log.WARN, "Package change action=" + intent.getAction() + " replacing=" + replacing + " uid=" + uid);
				// Check action
				if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
					// Check privacy service
					boolean flag = false;
					if (PrivacyService.getClient() == null || flag)
						return;

					// Get data
					ApplicationInfoEx appInfo = new ApplicationInfoEx(context, uid);
					String packageName = inputUri.getSchemeSpecificPart();

					// Default deny new user apps
					if (appInfo.getPackageName().size() == 1) {
						if (replacing)
							PrivacyManager.clearPermissionCache(uid);
						else {
							// Delete existing restrictions
							PrivacyManager.deleteRestrictions(uid, null, true);
							PrivacyManager.deleteSettings(uid);
							PrivacyManager.deleteUsage(uid);
							PrivacyManager.clearPermissionCache(uid);

							//TODO 处理新增安装包
							List<String> liststr = new ArrayList<String>();
							String[] old = DBMgr.getListapp();
							boolean findit = false;
							if(old != null){
								for(int i=0; i<old.length; i++){
									Log.e("LTZ",old[i]+"O:O"+packageName);
									if(old[i].equals(packageName)){
										findit = true;
									}
									liststr.add(old[i]);
								}
							}							
							if(!findit){
								liststr.add(packageName);
								ManagerCertermActivity.Saveapplist(liststr);
							}				
							
							// Apply template
							PrivacyManager.applyTemplate(uid, Meta.cTypeTemplate, null, true, true, false);
							PreferenceUtils
								.setParam(context, "xp_clear", packageName, packageName);
							SharedPreferences prefs = context.getSharedPreferences("ModSettings",
									Context.MODE_WORLD_READABLE);
							SharedPreferences.Editor e = prefs.edit();
							e.putBoolean(packageName + "/" + "mdatatype", true).commit();
							e.putBoolean(packageName + "/" + "updateDisplayInfoLocked", true).commit();
							e.putBoolean(packageName + "/" + "timeMachine", true).commit();
							e.putInt(packageName + "/recents-mode", 0x2).commit();

							// Enable on demand
							if (ondemand)
								PrivacyManager.setSetting(uid, PrivacyManager.cSettingOnDemand, Boolean.toString(true));
						}
					}

					// Mark as new/changed
					PrivacyManager.setSetting(uid, PrivacyManager.cSettingState, Integer.toString(ApplicationInfoEx.STATE_ATTENTION));

					// New/update notification
					boolean notify = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingNotify, true);
					if (notify)
						notify = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingNotify, true);
					if (!replacing || notify) {
						Intent resultIntent = new Intent(context, ActivityApp.class);
						resultIntent.putExtra(ActivityApp.cUid, uid);

						// Build pending intent
						PendingIntent pendingIntent = PendingIntent.getActivity(context, uid, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

						// Build result intent settings
						Intent resultIntentSettings = new Intent(context, ActivityApp.class);
						resultIntentSettings.putExtra(ActivityApp.cUid, uid);
						resultIntentSettings.putExtra(ActivityApp.cAction, ActivityApp.cActionSettings);

						// Build pending intent settings
						PendingIntent pendingIntentSettings = PendingIntent.getActivity(context, uid - 10000, resultIntentSettings, PendingIntent.FLAG_UPDATE_CURRENT);

						// Build result intent clear
						Intent resultIntentClear = new Intent(context, ActivityApp.class);
						resultIntentClear.putExtra(ActivityApp.cUid, uid);
						resultIntentClear.putExtra(ActivityApp.cAction, ActivityApp.cActionClear);

						// Build pending intent clear
						PendingIntent pendingIntentClear = PendingIntent.getActivity(context, uid + 10000, resultIntentClear, PendingIntent.FLAG_UPDATE_CURRENT);

						// Title
						String title = String.format("%s %s %s", context.getString(replacing ? R.string.msg_update : R.string.msg_new), appInfo.getApplicationName(packageName), appInfo.getPackageVersionName(context, packageName));
						if (!replacing)
							title = String.format("%s %s", title, context.getString(R.string.msg_applied));
						/*
						// Build notification
						NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
						notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
						notificationBuilder.setContentTitle(context.getString(R.string.app_name));
						notificationBuilder.setContentText(title);
						notificationBuilder.setContentIntent(pendingIntent);
						notificationBuilder.setWhen(System.currentTimeMillis());
						notificationBuilder.setAutoCancel(true);

						// Actions
						notificationBuilder.addAction(android.R.drawable.ic_menu_edit, context.getString(R.string.menu_app_settings), pendingIntentSettings);
						notificationBuilder.addAction(android.R.drawable.ic_menu_delete, context.getString(R.string.menu_clear), pendingIntentClear);

						// Notify
						Notification notification = notificationBuilder.build();
						notificationManager.notify(appInfo.getUid(), notification);
						*/
					}

				} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
					Log.e("LTZ","ACTION_PACKAGE_REMOVED");
					// Check privacy service
					if (PrivacyService.getClient() == null)
						return;
					
					String packageName = inputUri.getSchemeSpecificPart();
					ApplicationInfoEx appInfo = new ApplicationInfoEx(context, uid);
					//TODO 处理卸载安装包
					List<String> liststr = new ArrayList<String>();
					String[] old = DBMgr.getListapp();
					if(old != null){
						for(int i=0; i<old.length; i++){
							Log.e("LTZ",old[i]+"-:-"+packageName);
							if(!old[i].equals(packageName)){
								liststr.add(old[i]);
								Log.e("LTZ",old[i]+":"+packageName);
							}
						}
					}					
					ManagerCertermActivity.Saveapplist(liststr);

					if (!replacing) {
						// Package removed
						notificationManager.cancel(uid);
						// Delete restrictions						
						if (appInfo.getPackageName().size() == 0) {
							Log.e("LTZ","CLEAN VALUE");
							PrivacyManager.deleteRestrictions(uid, null, false);
							PrivacyManager.deleteSettings(uid);
							PrivacyManager.deleteUsage(uid);
							PrivacyManager.clearPermissionCache(uid);
							
							DBMgr.getInstance(context).cleandatabaserecord(packageName);
							/**
							 * update : @date Mar 17, 2015 9:11:21 PM
							 */
							PreferenceUtils.clearKey(context, "xp_clear", packageName);
							PreferenceUtils.clearKey(context, "appid", packageName);
							// PrivacyManager.setSetting(0,
							// PrivacyManager.cSettingClearAppid, "-100");

							SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("ModSettings", Context.MODE_WORLD_READABLE);
							Editor e = prefs.edit();
							e.remove(packageName + "/" + "mdatatype");
							e.remove(packageName + "/" + "updateDisplayInfoLocked");
							e.remove(packageName + "/" + "timeMachine");
							e.remove(packageName + "/recents-mode");
							e.commit();
						}
					}

				} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
					// Notify reboot required
					String packageName = inputUri.getSchemeSpecificPart();
					if (packageName.equals(context.getPackageName())) {
						// Mark self as new/changed
						PrivacyManager.setSetting(uid, PrivacyManager.cSettingState, Integer.toString(ApplicationInfoEx.STATE_ATTENTION));

						// Start package update
						Intent changeIntent = new Intent();
						changeIntent.setClass(context, UpdateService.class);
						changeIntent.putExtra(UpdateService.cAction, UpdateService.cActionUpdated);
						context.startService(changeIntent);

						// Build notification
						NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
						notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
						notificationBuilder.setContentTitle(context.getString(R.string.app_name));
						notificationBuilder.setContentText(context.getString(R.string.msg_reboot));
						notificationBuilder.setWhen(System.currentTimeMillis());
						notificationBuilder.setAutoCancel(true);
						Notification notification = notificationBuilder.build();

						// Notify
						notificationManager.notify(Util.NOTIFY_RESTART, notification);
					}
				}
			}
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
	}
}
