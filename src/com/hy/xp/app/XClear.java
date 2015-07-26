package com.hy.xp.app;

import java.util.Map;
import java.util.Map.Entry;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.hy.xp.app.clear.AppCleanManager;
import com.hy.xp.app.task.PreferenceUtils;

public class XClear extends AppWidgetProvider
{
	private static final String UPDATE_ACTION = "com.hy.xp.app.UPDATE_ACTION";
	private SharedPreferences sp;

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		// ////System.out.println("DesktopClean onUpdate");

		Intent intent = new Intent();
		intent.setAction(UPDATE_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, -1, intent, 0);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.xclear);
		remoteViews.setOnClickPendingIntent(R.id.ib_cleandata, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	public void onDeleted(Context context, int[] appWidgetIds)
	{
		super.onDeleted(context, appWidgetIds);
	}

	@SuppressWarnings("unchecked")
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		if (UPDATE_ACTION.equals(action)) {
			sp = PreferenceUtils.getSharedPreferences(context, "xp_clear");
			Map<String, String> datas = (Map<String, String>) sp.getAll();
			// System.out.println(sp.toString());
			if (null != datas && datas.size() > 0) {
				for (Entry<String, String> entity : datas.entrySet()) {
					AppCleanManager.cleanAppDataByPackageName(context, context.getPackageManager(), entity.getKey());
				}

				Toast.makeText(context, "制定的数据清理任务执行完毕！", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "当前没有可执行的任务，请添加任务！", Toast.LENGTH_SHORT).show();
			}

		}
		super.onReceive(context, intent);
	}

}
