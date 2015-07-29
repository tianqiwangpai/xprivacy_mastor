package com.hy.xp.app.task;

import android.os.AsyncTask;
import android.widget.Toast;

import com.hy.xp.app.AppAdapte;
import com.hy.xp.app.ApplicationEx;
import com.hy.xp.app.ManagerCertermActivity;
import com.hy.xp.app.SetConfigData;

public class InitData extends AsyncTask<TaskAttribute, Integer, Void> {    
    protected void onPostExecute(String result) {
        Toast.makeText(ApplicationEx.getContextObject(), "任务数据已就绪", Toast.LENGTH_LONG).show();
        if(ManagerCertermActivity.uihandler != null){
        	ManagerCertermActivity.uihandler.sendEmptyMessage(0);
        }
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
