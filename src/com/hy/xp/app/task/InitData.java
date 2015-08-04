package com.hy.xp.app.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.widget.Toast;

import com.hy.xp.app.AppAdapte;
import com.hy.xp.app.ApplicationEx;
import com.hy.xp.app.ManagerCertermActivity;
import com.hy.xp.app.SetConfigData;
import com.hy.xp.app.UpdateService;

public class InitData extends AsyncTask<TaskAttribute, Integer, Void> {    
	private int flag = 0;
	private ManagerCertermActivity mcontext = null;
	
	public void setThis(ManagerCertermActivity mcontext){
		this.mcontext = mcontext;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if(flag == 1){
    		Toast.makeText(mcontext, "�������������Ѿ����ɹ��������ٴ�����", Toast.LENGTH_LONG).show();
    	}else{
    		Toast.makeText(ApplicationEx.getContextObject(), "���������Ѿ���", Toast.LENGTH_LONG).show();
            mcontext.inittaskprocess();
    	}
	}

	@Override
	protected Void doInBackground(TaskAttribute... params) {		
		TaskAttribute localTaskAttribute = null;
		if (params != null) {
			if (params.length > 0)
				localTaskAttribute = params[0];
		}

		flag = 0;

		DBMgr localDBMgr = DBMgr.getInstance(ApplicationEx.getContextObject());
		
		localDBMgr.addtaskapp(localTaskAttribute.getTaskName(),
				AppAdapte.dataselected.toArray());

		int[] arrayOfInt = localDBMgr.getLastnewCord(
				localTaskAttribute.getTaskName(),
				AppAdapte.dataselected.toArray());
		
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("",Locale.SIMPLIFIED_CHINESE); 
		sdf.applyPattern("yyyy��MM��dd��"); 
		if(DBMgr.getTaskstartime(arrayOfInt[1]) != null && sdf.format(date).equals(DBMgr.getTaskstartime(arrayOfInt[1]))){
			//������������ɣ���������ټ���
			flag = 1;
			return null;
		}
		
		//�ж�����״����������粻ͨ������ʾ�û�
        ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationEx.getContextObject().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != State.CONNECTED
        		&& connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != State.CONNECTED){
        	Intent changeIntent = new Intent();
	        changeIntent.setClass(ApplicationEx.getContextObject(), UpdateService.class);
	        changeIntent.putExtra("Action", UpdateService.cActionNoNetwork);  
	        PendingIntent pi = PendingIntent.getService(ApplicationEx.getContextObject(), 0, changeIntent, PendingIntent.FLAG_UPDATE_CURRENT);  
	        AlarmManager manager = (AlarmManager)ApplicationEx.getContextObject().getSystemService(Context.ALARM_SERVICE);  
	        manager.set(AlarmManager.RTC_WAKEUP, 5*60*1000, pi); 
        }
		
		DBMgr.setTaskstarttime(sdf.format(date), arrayOfInt[1]);
		
		localDBMgr.initnewdatatable(arrayOfInt[1], arrayOfInt[0],
				localTaskAttribute.getTaskNewdata());
		
		localDBMgr.updateLastnewCord(localTaskAttribute.getTaskName(),
				localTaskAttribute.getTaskNewdata());
		
		localDBMgr.deletebackrecordtable();
		
		//���յݼ�
		localDBMgr.initnextdaydatatable(arrayOfInt[1], 0, -1,
				localTaskAttribute.getTaskReturnratio(),
				localTaskAttribute.getTaskStayWay(),
				localTaskAttribute.getTaskDecilneRatio(),
				localTaskAttribute.getTaskDecilneMin(),
				localTaskAttribute.isTaskDeclineFlag());
		
		//���յݼ�
		if (localTaskAttribute.isTaskNextDayFlag())
			localDBMgr
					.initnextdaydatatable(arrayOfInt[1], localTaskAttribute
							.getTaskNextDayVisitInterval(), localTaskAttribute
							.getTaskNextDayVisitIntervalCount(),
							localTaskAttribute
									.getTaskNextDayVisitIntervalReturnRatio(),
							localTaskAttribute.getTaskNextDayVisitStayWay(),
							localTaskAttribute
									.getTaskNextDayVisitDecilneRatio(),
							localTaskAttribute.getTaskNextDayVisitDecilneMin(),
							localTaskAttribute.isTaskNextDayVisitDeclineFlag());
		
		localDBMgr.initnextweekdatatable(arrayOfInt[1],
				localTaskAttribute.getTaskNextWeekVisitIntervalReturnRatio(),
				localTaskAttribute.getTaskNextWeekVisitStayWay(),
				localTaskAttribute.getTaskNextWeekVisitDecilneRatio(),
				localTaskAttribute.getTaskNextWeekVisitDecilneMin(),
				localTaskAttribute.isTaskNextWeekVisitDeclineFlag());
		
		localDBMgr.initnextmonthdatatable(arrayOfInt[1],
				localTaskAttribute.getTaskNextMonthVisitIntervalReturnRatio(),
				localTaskAttribute.getTaskNextMonthVisitStayWay(),
				localTaskAttribute.getTaskNextMonthVisitDecilneRatio(),
				localTaskAttribute.getTaskNextMonthVisitDecilneMin(),
				localTaskAttribute.isTaskNextMonthVisitDeclineFlag());
		
		SetConfigData.SetDataByfile(ApplicationEx.getContextObject(),
				localDBMgr.getNextData(0));
		return null;
	}
}
