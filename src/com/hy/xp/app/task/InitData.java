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
    		Toast.makeText(mcontext, "当日任务数据已经生成过，无需再次生成", Toast.LENGTH_LONG).show();
    	}else{
    		Toast.makeText(ApplicationEx.getContextObject(), "第1条任务数据已就绪", Toast.LENGTH_LONG).show();
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
				AppAdapte.dataselected);

		int[] arrayOfInt = localDBMgr.getLastnewCord(
				localTaskAttribute.getTaskName(),
				AppAdapte.dataselected);
		
		//TODO 初始化模拟数据
		//localDBMgr.inittestdata();
		
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("",Locale.SIMPLIFIED_CHINESE); 
		sdf.applyPattern("yyyy年MM月dd日"); 
		if(DBMgr.getTaskstartime(arrayOfInt[1]) != null && sdf.format(date).equals(DBMgr.getTaskstartime(arrayOfInt[1]))){
			//当日任务已完成，请等明天再继续
			flag = 1;
			return null;
		}
		
		//判断网络状况，如果网络不通，则提示用户
        ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationEx.getContextObject().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != State.CONNECTED
        		&& connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != State.CONNECTED){
        	UpdateService.resetpendingintent(0);
        }
		
		DBMgr.setTaskstarttime(sdf.format(date), arrayOfInt[1]);
		
		localDBMgr.initnewdatatable(arrayOfInt[1], arrayOfInt[0],
				localTaskAttribute.getTaskNewdata());
		
		localDBMgr.updateLastnewCord(localTaskAttribute.getTaskName(),
				localTaskAttribute.getTaskNewdata());
		
		localDBMgr.deletebackrecordtable();
		
		//当日递减
		localDBMgr.initnextdaydatatable(arrayOfInt[1], 0, -1,
				localTaskAttribute.getTaskReturnratio(),
				localTaskAttribute.getTaskStayWay(),
				localTaskAttribute.getTaskDecilneRatio(),
				localTaskAttribute.getTaskDecilneMin(),
				localTaskAttribute.isTaskDeclineFlag());
		
		//隔日递减
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
