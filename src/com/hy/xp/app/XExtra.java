/**
 * ϵͳ��Ŀ����
 * com.hy.xp.app
 * XExtra.java
 * 
 * 2015��9��20��-����5:24:36
 * 2015����������ѯ���޹�˾-��Ȩ����
 *
 */
package com.hy.xp.app;

import java.util.Map;
import java.util.Map.Entry;

import com.hy.xp.app.clear.AppCleanManager;
import com.hy.xp.app.task.DBMgr;
import com.hy.xp.app.task.PreferenceUtils;
import com.hy.xp.app.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 *
 * @Class:XExtra
 * α����ϵ������/Ӧ���б�/ͨ����¼
 * @Author:Fellick
 * 2015��9��20�� ����5:24:36 * 
 * @version 1.0.0
 *
 */
public class XExtra extends AppWidgetProvider{
	private static final String UPDATE_ACTION = "com.hy.xp.app.XX.EXTRA_ACTION";
	private SharedPreferences sp;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		String action = intent.getAction();
		if (UPDATE_ACTION.equals(action)) {
			
			/*sp = PreferenceUtils.getSharedPreferences(context, "xp_clear");
			Map<String, String> cleardatas = (Map<String, String>) sp.getAll();
			if (null != cleardatas && cleardatas.size() > 0) {
				for (Entry<String, String> entity : cleardatas.entrySet()) {
					AppCleanManager.cleanAppDataByPackageName(context, context.getPackageManager(), entity.getKey());
				}
				Toast.makeText(context, "�ƶ���������������ִ����ϣ�", Toast.LENGTH_SHORT).show();
			} else {
				//Toast.makeText(context, "��ǰû�п�ִ�е��������������", Toast.LENGTH_SHORT).show();
			}*/
			
			SetConfigData.SetDataByfile_app(ApplicationEx.getContextObject(), DBMgr.getInstance(context).getRandomAppinfolist());
			SetConfigData.SetDataByfile_calllog(ApplicationEx.getContextObject(), DBMgr.getInstance(context).getRandomCalllog());
			SetConfigData.SetDataByfile_contacts(ApplicationEx.getContextObject(), DBMgr.getInstance(context).getRandomContacts());
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Intent intent = new Intent();
		intent.setAction(UPDATE_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, -1, intent, 0);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.xextra);
		remoteViews.setOnClickPendingIntent(R.id.ib_extradata, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}

}
