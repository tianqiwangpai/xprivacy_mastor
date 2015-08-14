package com.hy.xp.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.hy.xp.app.task.DBMgr;

public class UpdateService extends Service
{
	public static final String cAction = "Action";
	public static final int cActionBoot = 1;
	public static final int cActionUpdated = 2;
	
	public static final int cActionFinished = 3;
	public static final int cActionReady = 4;
	public static final int cActionDataNull = 5;

	public static final int cActionNoNetwork = 6;
	public static final int cActionNoData = 7;
	
	public static final String cFlush = "com.hy.xp.app.action.FLUSH";
	public static final String cUpdate = "com.hy.xp.app.action.UPDATE";

	private static Thread mWorkerThread;
	
	public static boolean currenttaskfinished = false;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// Check if work
		if (intent == null) {
			stopSelf();
			return 0;
		}

		// Flush
		if (cFlush.equals(intent.getAction())) {
			try {
				PrivacyService.getClient().flush();
				XApplication.manage(this, 0, XApplication.cActionFlush);
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
			stopSelf();
			return 0;
		}

		// Check action
		Bundle extras = intent.getExtras();
		if (extras.containsKey(cAction)) {
			final int action = extras.getInt(cAction);
			Util.log(null, Log.WARN, "Service received action=" + action + " flags=" + flags);

			// Check service
			if (PrivacyService.getClient() == null) {
				Util.log(null, Log.ERROR, "Service not available");
				stopSelf();
				return 0;
			}

			if(action == cActionFinished){
				currenttaskfinished = true;
				notifynormalmessage(this, Util.NOTIFY_MIGRATE, "任务已完成");
				Toast.makeText(ApplicationEx.getContextObject(), "任务已完成", Toast.LENGTH_SHORT).show();
				return 0;
			}else if(action == cActionReady){	
				currenttaskfinished = false;
				DBMgr dbmgr = DBMgr.getInstance(this);
				int[] backrst = dbmgr.getbackdatacount();
				int taskid = dbmgr.getLastnewCord(dbmgr.getCurrentTaskname(), dbmgr.getListapp())[1];
				int[] newrst = dbmgr.getnewdatacount(taskid);
				int rst = backrst[0]+newrst[0];
				//notifynormalmessage(this, Util.NOTIFY_MIGRATE, "第"+rst+"个，数据设置成功");
				Toast.makeText(ApplicationEx.getContextObject(), "第"+rst+"个，数据设置成功", Toast.LENGTH_SHORT).show();
				resetpendingintent(1);
				return 0;
			}else if(action == cActionNoData){
				if(!currenttaskfinished){
					notifynormalmessage(this, Util.NOTIFY_MIGRATE, "5分钟未抽取数据！！！！");
					resetpendingintent(1);
				}
			}else if(action == cActionNoNetwork){
				if(!currenttaskfinished){
					//判断网络状况，如果网络不通，则提示用户
			        ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationEx.getContextObject().getSystemService(Context.CONNECTIVITY_SERVICE);
			        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != State.CONNECTED
			        		&& connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != State.CONNECTED){
			        	notifynormalmessage(this, Util.NOTIFY_MIGRATE, "警告，网络5分钟内未连接!");
						return 0;
			        }
				}				
			}else if(action == cActionDataNull)
			{
				notifynormalmessage(this, Util.NOTIFY_MIGRATE, "手机信息数据库已经耗完，请插入新数据!");
				return 0;
			}else{
				// Start foreground service
				NotificationCompat.Builder builder = new NotificationCompat.Builder(UpdateService.this);
				builder.setSmallIcon(R.drawable.ic_launcher);
				builder.setContentTitle(getString(R.string.app_name));
				builder.setContentText(getString(R.string.msg_service));
				builder.setWhen(System.currentTimeMillis());
				builder.setAutoCancel(false);
				builder.setOngoing(true);
				Notification notification = builder.build();
				startForeground(Util.NOTIFY_SERVICE, notification);
			}

			// Start worker
			mWorkerThread = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try {
						// Check action
						if (action == cActionBoot) {
							// Boot received
							migrate(UpdateService.this);
							upgrade(UpdateService.this);
							randomize(UpdateService.this);

						} else if (action == cActionUpdated) {
							// Self updated
							upgrade(UpdateService.this);

						} else
							Util.log(null, Log.ERROR, "Unknown action=" + action);

						// Done
						stopForeground(true);
						stopSelf();
					} catch (Throwable ex) {
						Util.bug(null, ex);
						// Leave service running
					}
				}

			});
			mWorkerThread.start();
		} else
			Util.log(null, Log.ERROR, "Action missing");

		return START_STICKY;
	}

	private static void migrate(Context context) throws IOException, RemoteException
	{
		int first = 0;
		String format = context.getString(R.string.msg_migrating);
		List<ApplicationInfo> listApp = context.getPackageManager().getInstalledApplications(0);

		// Start migrate
		PrivacyProvider.migrateLegacy(context);

		// Migrate global settings
		PrivacyManager.setSettingList(PrivacyProvider.migrateSettings(context, 0));
		PrivacyProvider.finishMigrateSettings(0);

		// Migrate application settings/restrictions
		for (int i = 1; i <= listApp.size(); i++) {
			int uid = listApp.get(i - 1).uid;
			// Settings
			List<PSetting> listSetting = PrivacyProvider.migrateSettings(context, uid);
			PrivacyManager.setSettingList(listSetting);
			PrivacyProvider.finishMigrateSettings(uid);

			// Restrictions
			List<PRestriction> listRestriction = PrivacyProvider.migrateRestrictions(context, uid);
			PrivacyManager.setRestrictionList(listRestriction);
			PrivacyProvider.finishMigrateRestrictions(uid);

			if (first == 0)
				if (listSetting.size() > 0 || listRestriction.size() > 0)
					first = i;
			if (first > 0 && first < listApp.size())
				notifyProgress(context, Util.NOTIFY_MIGRATE, format, 100 * (i - first) / (listApp.size() - first));
		}
		if (first == 0)
			Util.log(null, Log.WARN, "Nothing to migrate");

		// Complete migration
		int userId = Util.getUserId(Process.myUid());
		PrivacyService.getClient().setSetting(new PSetting(userId, "", PrivacyManager.cSettingMigrated, Boolean.toString(true)));
	}

	private static void upgrade(Context context) throws NameNotFoundException
	{
		// Get previous version number
		int userId = Util.getUserId(Process.myUid());
		Version currentVersion = new Version(Util.getSelfVersionName(context));
		Version storedVersion = new Version(PrivacyManager.getSetting(userId, PrivacyManager.cSettingVersion, "0.0"));
		boolean dangerous = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingDangerous, false);

		// Check if upgrade needed
		if (storedVersion.compareTo(new Version("0.0")) != 0 && currentVersion.compareTo(storedVersion) > 0) {
			Util.log(null, Log.WARN, "Starting upgrade from version " + storedVersion + " to version " + currentVersion + " dangerous=" + dangerous);

			// Upgrade packages
			int first = 0;
			String format = context.getString(R.string.msg_upgrading);
			List<ApplicationInfo> listApp = context.getPackageManager().getInstalledApplications(0);

			for (int i = 1; i <= listApp.size(); i++) {
				int uid = listApp.get(i - 1).uid;
				List<PRestriction> listRestriction = getUpgradeWork(storedVersion, uid, dangerous);
				PrivacyManager.setRestrictionList(listRestriction);

				// Reset on demand for system applications
				if (new ApplicationInfoEx(context, listApp.get(i - 1).uid).isSystem())
					if (storedVersion.compareTo(new Version("2.0.38")) < 0)
						if (PrivacyManager.getSettingBool(listApp.get(i - 1).uid, PrivacyManager.cSettingOnDemand, false)) {
							Util.log(null, Log.WARN, "Disabling on demand for uid=" + listApp.get(i - 1).uid);
							PrivacyManager.setSetting(listApp.get(i - 1).uid, PrivacyManager.cSettingOnDemand, null);
						}

				if (first == 0)
					if (listRestriction.size() > 0)
						first = i;
				if (first > 0 && first < listApp.size())
					notifyProgress(context, Util.NOTIFY_UPGRADE, format, 100 * (i - first) / (listApp.size() - first));
			}
			if (first == 0)
				Util.log(null, Log.WARN, "Nothing to upgrade from version " + storedVersion + " to " + currentVersion);

			// Remove legacy setting
			if (dangerous)
				PrivacyManager.setSetting(userId, PrivacyManager.cSettingDangerous, null);

			// Resolve quirk
			if (storedVersion.compareTo(new Version("2.99.28")) < 0)
				if (!PrivacyManager.getSettingBool(0, PrivacyManager.cSettingNoResolve, false)) {
					Util.log(null, Log.WARN, "Enabling quirk resolve");
					PrivacyManager.setSetting(0, PrivacyManager.cSettingResolve, Boolean.toString(true));
				}

			// Wipe template
			if (storedVersion.compareTo(new Version("2.0.34")) < 0)
				for (PSetting setting : PrivacyManager.getSettingList(0, null))
					if (Meta.cTypeTemplate.equals(setting.type)) {
						Util.log(null, Log.WARN, "Deleting " + setting);
						PrivacyManager.setSetting(setting.uid, setting.type, setting.name, null);
					}
		} else
			Util.log(null, Log.WARN, "No upgrade from version " + storedVersion + " to " + currentVersion);

		// Set new version number
		if (currentVersion.compareTo(storedVersion) > 0)
			PrivacyManager.setSetting(userId, PrivacyManager.cSettingVersion, currentVersion.toString());

		// Cleanup
		PrivacyManager.removeLegacySalt(userId);
	}

	private static void randomize(Context context)
	{
		int first = 0;
		String format = context.getString(R.string.msg_randomizing);
		List<ApplicationInfo> listApp = context.getPackageManager().getInstalledApplications(0);

		// Randomize global
		int userId = Util.getUserId(Process.myUid());
		PrivacyManager.setSettingList(getRandomizeWork(context, userId));

		// Randomize applications
		for (int i = 1; i <= listApp.size(); i++) {
			int uid = listApp.get(i - 1).uid;
			List<PSetting> listSetting = getRandomizeWork(context, uid);
			PrivacyManager.setSettingList(listSetting);

			if (first == 0)
				if (listSetting.size() > 0)
					first = i;
			if (first > 0 && first < listApp.size())
				notifyProgress(context, Util.NOTIFY_RANDOMIZE, format, 100 * (i - first) / (listApp.size() - first));
		}
		if (first == 0)
			Util.log(null, Log.WARN, "Nothing to randomize");
	}

	private static List<PSetting> getRandomizeWork(Context context, int uid)
	{
		List<PSetting> listWork = new ArrayList<PSetting>();

		if (PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingRandom, false)) {
			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingLatitude))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingLatitude, PrivacyManager.getRandomProp("LAT")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingLongitude))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingLongitude, PrivacyManager.getRandomProp("LON")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingAltitude))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingAltitude, PrivacyManager.getRandomProp("ALT")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingSerial))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingSerial, PrivacyManager.getRandomProp("SERIAL")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingMac))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingMac, PrivacyManager.getRandomProp("MAC")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingPhone))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingPhone, PrivacyManager.getRandomProp("PHONE")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingImei))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingImei, PrivacyManager.getRandomProp("IMEI")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingId))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingId, PrivacyManager.getRandomProp("ANDROID_ID")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingGsfId))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingGsfId, PrivacyManager.getRandomProp("GSF_ID")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingAdId))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingAdId, PrivacyManager.getRandomProp("AdvertisingId")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingCountry))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingCountry, PrivacyManager.getRandomProp("ISO3166")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingSubscriber))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingSubscriber, PrivacyManager.getRandomProp("SubscriberId")));

			if (!hasRandomOnAccess(uid, PrivacyManager.cSettingSSID))
				listWork.add(new PSetting(uid, "", PrivacyManager.cSettingSSID, PrivacyManager.getRandomProp("SSID")));
		}

		return listWork;
	}

	private static boolean hasRandomOnAccess(int uid, String setting)
	{
		return PrivacyManager.cValueRandom.equals(PrivacyManager.getSetting(uid, setting, null));
	}

	private static List<PRestriction> getUpgradeWork(Version sVersion, int uid, boolean dangerous)
	{
		List<PRestriction> listWork = new ArrayList<PRestriction>();

		for (String restrictionName : PrivacyManager.getRestrictions()) {
			boolean restricted = PrivacyManager.getRestrictionEx(uid, restrictionName, null).restricted;

			for (Hook hook : PrivacyManager.getHooks(restrictionName, null)) {
				// Disable new dangerous restrictions
				if (hook.getFrom() != null) {
					if (sVersion.compareTo(hook.getFrom()) < 0) {
						if (hook.isDangerous()) {
							Util.log(null, Log.WARN, "Upgrading dangerous " + hook + " from=" + hook.getFrom() + " uid=" + uid);
							PRestriction restriction = new PRestriction(uid, hook.getRestrictionName(), hook.getName(), false, true);
							listWork.add(restriction);
						}

						// Restrict replaced methods
						if (hook.getReplacedMethod() != null) {
							PRestriction restriction = PrivacyManager.getRestrictionEx(uid, hook.getReplacedRestriction(), hook.getReplacedMethod());
							listWork.add(new PRestriction(uid, hook.getRestrictionName(), hook.getName(), restriction.restricted, restriction.asked));
							Util.log(null, Log.WARN, "Replacing " + hook.getReplacedRestriction() + "/" + hook.getReplacedMethod() + " by " + hook + " from=" + hook.getFrom() + " uid=" + uid);
						}
					}
				}

				// Restrict dangerous
				if (dangerous && restricted && hook.isDangerous()) {
					PRestriction restriction = new PRestriction(uid, hook.getRestrictionName(), hook.getName(), true, hook.whitelist() == null);
					if (PrivacyManager.isRestrictionSet(restriction))
						Util.log(null, Log.WARN, "Restrict dangerous set restriction=" + restriction);
					else {
						Util.log(null, Log.WARN, "Restrict dangerous setting restriction=" + restriction);
						listWork.add(restriction);
					}
				}
			}
		}

		return listWork;
	}

	private static void notifyProgress(Context context, int id, String format, int percentage)
	{
		String message = String.format(format, String.format("%d %%", percentage));
		Util.log(null, Log.WARN, message);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle(context.getString(R.string.app_name));
		builder.setContentText(message);
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(percentage == 100);
		builder.setOngoing(percentage < 100);
		Notification notification = builder.build();
		notificationManager.notify(id, notification);
	}
	
	public static NotificationManager notificationManager = null;
	
	
	public static NotificationCompat.Builder builder = null;
	
	private static void notifynormalmessage(Context context, int id, String message){
		if(notificationManager == null){
			notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		if(builder == null){
			builder = new NotificationCompat.Builder(context);
		}	
		notificationManager.cancelAll();
		
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle(context.getString(R.string.app_name));
		builder.setContentText(message);
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(true);
		if(DBMgr.isallowvoice()){
			builder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
		}else{
			builder.setDefaults(0);
		}
		Notification notification = builder.build();
		notificationManager.notify(id, notification);
	}
	static PendingIntent pi_network = null;
	static PendingIntent pi_data = null;
	public static void resetpendingintent(int type){
		AlarmManager manager = (AlarmManager)ApplicationEx.getContextObject().getSystemService(Context.ALARM_SERVICE);  
		if(type == 0){
			//网络
			if(pi_network == null){
				Intent changeIntent = new Intent();
		        changeIntent.setClass(ApplicationEx.getContextObject(), UpdateService.class);
		        changeIntent.putExtra("Action", UpdateService.cActionNoNetwork);  
		        pi_network = PendingIntent.getService(ApplicationEx.getContextObject(), 0, changeIntent, PendingIntent.FLAG_UPDATE_CURRENT);  
			}else{
				manager.cancel(pi_network);
			}
			manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5*60*1000,5*60*1000, pi_network); 
		}else{
			if(pi_data == null){
				Intent changeIntent = new Intent();
		        changeIntent.setClass(ApplicationEx.getContextObject(), UpdateService.class);
		        changeIntent.putExtra("Action", UpdateService.cActionNoData);  
		        pi_data = PendingIntent.getService(ApplicationEx.getContextObject(), 0, changeIntent, PendingIntent.FLAG_UPDATE_CURRENT);  
			}else{
				manager.cancel(pi_data);
			}
			manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5*60*1000+3*1000,5*60*1000+3*1000, pi_data); 
		}
	}
}
