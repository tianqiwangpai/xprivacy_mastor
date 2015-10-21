package com.hy.xp.app;

import java.util.Random;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import com.hy.xp.app.task.DBMgr;
import com.hy.xp.app.R;

public class XpWidget extends AppWidgetProvider
{
	private static RemoteViews rv;
	private static final String ACTION_CLICK_NAME2 = "zyf.temp.Service.START";
	public static Random r = new Random();
	int sum, settingReadFilesum;
	private SharedPreferences sp;
	private SharedPreferences sp_suji;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		rv = new RemoteViews(context.getPackageName(), R.layout.widget);
		Intent startServiceInten = new Intent();
		startServiceInten.setAction(ACTION_CLICK_NAME2);
		PendingIntent Pintent = PendingIntent.getBroadcast(context, 0, startServiceInten, 0);
		rv.setOnClickPendingIntent(R.id.imageView1, Pintent);
		ComponentName cmp = new ComponentName(context, XpWidget.class);
		AppWidgetManager myAppWidgetManager = AppWidgetManager.getInstance(context);
		myAppWidgetManager.updateAppWidget(cmp, rv);
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidget = appWidgetIds[i];
			updateAppwidget(context, myAppWidgetManager, appWidget);
		}
	}

	private void updateAppwidget(Context context, AppWidgetManager myAppWidgetManager, int appWidget)
	{
		rv = new RemoteViews(context.getPackageName(), R.layout.widget);
		Intent startServiceInten = new Intent();
		startServiceInten.setAction(ACTION_CLICK_NAME2);
		PendingIntent Pintent = PendingIntent.getBroadcast(context, 0, startServiceInten, 0);
		rv.setOnClickPendingIntent(R.id.imageView1, Pintent);
	}

	@SuppressLint("WorldReadableFiles")
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO Auto-generated method stub
		SetConfigData.SetDataByfile(ApplicationEx.getContextObject(), DBMgr.getInstance(context).getNextData(0));
		super.onReceive(context, intent);
		
		/*long start = System.currentTimeMillis();
		String fileTask = PreferenceUtils.getParam(context, "taskname", "").toString();
		String mode = PreferenceUtils.getParam(context, fileTask, "curl_file_read_mode", "").toString();
		sum = (Integer) PreferenceUtils.getParam(context, fileTask, "curl_file_count", 1);
		settingReadFilesum = (Integer) PreferenceUtils.getParam(context, fileTask, "cSettingReadFileSum", 1);*/

		/*if (intent.getAction().equals("com.sec.android.widgetapp.APPWIDGET_RESIZE") || intent.getAction().equals("com.hy.xp.app.update")) {
			rv = new RemoteViews(context.getPackageName(), R.layout.widget);
			Intent startServiceInten = new Intent();
			startServiceInten.setAction(ACTION_CLICK_NAME2);
			PendingIntent Pintent = PendingIntent.getBroadcast(context, 0, startServiceInten, 0);
			rv.setOnClickPendingIntent(R.id.imageView1, Pintent);
		}*/
		/*if (intent.getAction().equals(ACTION_CLICK_NAME2)) {
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
			}

			long ms = System.currentTimeMillis() - start;
			Util.log(null, Log.WARN, String.format("С����--->:" + "%d ms", ms));
		}
*/
	}
}
