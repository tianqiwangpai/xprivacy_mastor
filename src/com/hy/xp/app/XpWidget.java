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
			// 从文件中读取
			PhoneDataBean mBean = null;
			if ("随机读取".equals(mode)) {
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
				// System.out.println("读取:\t" + index);
				mBean = DBMgr.getInstance(context).getPhoneDataBeanBySuji(index);
				if (mBean != null) {
					SetConfigData.SetDataByfile(context, mBean);
					Toast.makeText(context, "第" + sum + "个，数据设置成功", Toast.LENGTH_SHORT).show();
					sum = sum + 1;
					PreferenceUtils.setParam(context, fileTask, "curl_file_count", sum);
					if (sum > settingReadFilesum) {
						Toast.makeText(context, "当日执行文件已读取完毕！读取总数为：" + (sum - 1) + "\t请注意更换数据文件，否则将再次读取该数据文件", Toast.LENGTH_SHORT).show();
						PreferenceUtils.setParam(context, fileTask, "curl_file_count", 1);
						// 清空随机时设置
						SharedPreferences mSharedPreferences = context.getSharedPreferences("suji", Context.MODE_WORLD_READABLE);
						Editor editor = mSharedPreferences.edit();
						editor.clear();
						editor.commit();
					}
				} else {
					Toast.makeText(context, "请设置数据文件或者请查看数据是否正确！", Toast.LENGTH_SHORT).show();
				}

			}
			if ("顺序读取".equals(mode)) {
				mBean = DBMgr.getInstance(context).getPhoneDataBeanById(sum);
				if (mBean != null) {
					SetConfigData.SetDataByfile(context, mBean);
					Toast.makeText(context, "第" + sum + "个，数据设置成功", Toast.LENGTH_SHORT).show();
					sum = sum + 1;
					PreferenceUtils.setParam(context, fileTask, "curl_file_count", sum);
					if (sum > settingReadFilesum) {
						Toast.makeText(context, "当日执行文件已读取完毕！读取总数为：" + (sum - 1) + "\t请注意更换数据文件，否则将再次读取该数据文件", Toast.LENGTH_SHORT).show();
						PreferenceUtils.setParam(context, fileTask, "curl_file_count", 1);
					}
				} else {
					Toast.makeText(context, "请设置数据文件或者请查看数据是否正确！", Toast.LENGTH_SHORT).show();
				}
			}

			long ms = System.currentTimeMillis() - start;
			Util.log(null, Log.WARN, String.format("小部件--->:" + "%d ms", ms));
		}
*/
	}
}
