package com.hy.xp.app;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.hy.xp.app.clear.AppCleanManager;
import com.hy.xp.app.task.DBMgr;
import com.hy.xp.app.task.PhoneDataBean;
import com.hy.xp.app.task.PreferenceUtils;

public class XX extends AppWidgetProvider
{
	private static final String UPDATE_ACTION = "com.hy.xp.app.XX.UPDATE_ACTION";
	private SharedPreferences sp;
	int sum, settingReadFilesum;
	private SharedPreferences sp_suji;

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		Intent intent = new Intent();
		intent.setAction(UPDATE_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, -1, intent, 0);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.xx);
		remoteViews.setOnClickPendingIntent(R.id.imageView1, pendingIntent);
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
			Map<String, String> cleardatas = (Map<String, String>) sp.getAll();
			if (null != cleardatas && cleardatas.size() > 0) {
				for (Entry<String, String> entity : cleardatas.entrySet()) {
					AppCleanManager.cleanAppDataByPackageName(context, context.getPackageManager(), entity.getKey());
				}
				Toast.makeText(context, "�ƶ���������������ִ����ϣ�", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "��ǰû�п�ִ�е��������������", Toast.LENGTH_SHORT).show();
			}
			
			
			
			SetConfigData.SetDataByfile(ApplicationEx.getContextObject(), DBMgr.getInstance(context).getNextData(0));

			/*long start = System.currentTimeMillis();
			String fileTask = PreferenceUtils.getParam(context, "taskname", "").toString();
			String mode = PreferenceUtils.getParam(context, fileTask, "curl_file_read_mode", "").toString();
			sum = (Integer) PreferenceUtils.getParam(context, fileTask, "curl_file_count", 1);
			settingReadFilesum = (Integer) PreferenceUtils.getParam(context, fileTask, "cSettingReadFileSum", 1);
*/
			
			/*
			
			// ���ļ��ж�ȡ
			PhoneDataBean mBean = null;
			if ("�����ȡ".equals(mode)) {
				int index = 1;
				sp_suji = PreferenceUtils.getSharedPreferences(context, "suji");
				Map<String, String> datas = (Map<String, String>) sp_suji.getAll();
				if (null != datas && datas.size() > 0) {
					int ccc = 0;
					while (ccc < 1) {
						boolean flag = true;
						index = new Random().nextInt(settingReadFilesum) + 1;
						for (Entry<String, String> entity : datas.entrySet()) {
							int tmp = Integer.valueOf(entity.getKey());
							// System.out.println("tmp-->" + tmp);
							if (index == tmp) {
								flag = false;
								break;
							}
						}
						if (flag) {
							ccc++;
						}
					}
				}

				SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("suji", Context.MODE_WORLD_READABLE);
				Editor e = prefs.edit();
				e.putInt(index + "", index).commit();
				// System.out.println("��ȡ:\t" + index);
				mBean = DBMgr.getInstance(context).getPhoneDataBeanBySuji(index);
				if (mBean != null) {
					SetConfigData.SetDataByfile(context, mBean);
					Toast.makeText(context, "��" + sum + "�����������óɹ�", Toast.LENGTH_SHORT).show();
					sum = sum + 1;
					PreferenceUtils.setParam(context, fileTask, "curl_file_count", sum);
					if (sum > settingReadFilesum) {
						Toast.makeText(context, "����ִ���ļ��Ѷ�ȡ��ϣ���ȡ����Ϊ��" + (sum - 1) + "\t��ע����������ļ��������ٴζ�ȡ�������ļ�", Toast.LENGTH_SHORT).show();
						PreferenceUtils.setParam(context, fileTask, "curl_file_count", 1);
						// ������ʱ����
						SharedPreferences mSharedPreferences = context.getSharedPreferences("suji", Context.MODE_WORLD_READABLE);
						Editor editor = mSharedPreferences.edit();
						editor.clear();
						editor.commit();
					}
				} else {
					Toast.makeText(context, "�����������ļ�������鿴�����Ƿ���ȷ��", Toast.LENGTH_SHORT).show();
				}

			}
			if ("˳���ȡ".equals(mode)) {
				mBean = DBMgr.getInstance(context).getPhoneDataBeanById(sum);
				if (mBean != null) {
					SetConfigData.SetDataByfile(context, mBean);
					Toast.makeText(context, "��" + sum + "�����������óɹ�", Toast.LENGTH_SHORT).show();
					sum = sum + 1;
					PreferenceUtils.setParam(context, fileTask, "curl_file_count", sum);
					if (sum > settingReadFilesum) {
						Toast.makeText(context, "����ִ���ļ��Ѷ�ȡ��ϣ���ȡ����Ϊ��" + (sum - 1) + "\t��ע����������ļ��������ٴζ�ȡ�������ļ�", Toast.LENGTH_SHORT).show();
						PreferenceUtils.setParam(context, fileTask, "curl_file_count", 1);
					}
				} else {
					Toast.makeText(context, "�����������ļ�������鿴�����Ƿ���ȷ��", Toast.LENGTH_SHORT).show();
				}
			}*/

			//long ms = System.currentTimeMillis() - start;
			//Util.log(null, Log.WARN, String.format("С����--->:" + "%d ms", ms));

		}
		super.onReceive(context, intent);
	}

}
