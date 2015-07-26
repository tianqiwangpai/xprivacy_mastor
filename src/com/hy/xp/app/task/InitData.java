package com.hy.xp.app.task;

import android.content.Intent;
import android.os.AsyncTask;

import com.hy.xp.app.AppAdapte;
import com.hy.xp.app.ApplicationEx;
import com.hy.xp.app.SetConfigData;
import com.hy.xp.app.UpdateService;

public class InitData extends AsyncTask<TaskAttribute, Integer, Void> {    
    protected void onPostExecute(String result) {
        Intent changeIntent = new Intent();
        changeIntent.setClass(ApplicationEx.getContextObject(), UpdateService.class);
        changeIntent.putExtra("Action", UpdateService.cActionReady);
        ApplicationEx.getContextObject().startService(changeIntent);
    }

	@Override
	protected Void doInBackground(TaskAttribute... params) {		
		TaskAttribute localTaskAttribute = null;
		if (params != null) {
			if (params.length > 0)
				localTaskAttribute = params[0];
		}

		DBMgr localDBMgr = DBMgr.getInstance(ApplicationEx.getContextObject());
		
		localDBMgr.addtaskapp(localTaskAttribute.getTaskName(),
				AppAdapte.dataselected.toArray());

		int[] arrayOfInt = localDBMgr.getLastnewCord(
				localTaskAttribute.getTaskName(),
				AppAdapte.dataselected.toArray());
		
		localDBMgr.initnewdatatable(arrayOfInt[1], arrayOfInt[0],
				localTaskAttribute.getTaskNewdata());
		
		localDBMgr.updateLastnewCord(localTaskAttribute.getTaskName(),
				localTaskAttribute.getTaskNewdata());
		
		localDBMgr.deletebackrecordtable();
		
		//µ±»’µ›ºı
		localDBMgr.initnextdaydatatable(arrayOfInt[1], 0, -1,
				localTaskAttribute.getTaskReturnratio(),
				localTaskAttribute.getTaskStayWay(),
				localTaskAttribute.getTaskDecilneRatio(),
				localTaskAttribute.getTaskDecilneMin(),
				localTaskAttribute.isTaskDeclineFlag());
		
		//∏Ù»’µ›ºı
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
