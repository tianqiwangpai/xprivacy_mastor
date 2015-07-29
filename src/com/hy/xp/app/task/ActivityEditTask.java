package com.hy.xp.app.task;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hy.xp.app.ActivityBase;
import com.hy.xp.app.PrivacyService;
import com.hy.xp.app.R;

public class ActivityEditTask extends ActivityBase
{

	TaskAttribute mTaskAttribute = null;
	EditText etTaskName;
	EditText etTaskDesc;
	EditText etTaskNewdata;
	EditText etTaskNumber;
	EditText etTaskReturnratio;
	CheckBox cbTaskDeclineFlag;

	EditText etTaskDecilneRatio;
	EditText etTaskDecilneMin;
	CheckBox cbTaskNextDayFlag;
	EditText etTaskNextDayVisitInterval;
	EditText etTaskNextDayVisitIntervalReturnRatio;
	EditText etTaskNextDayVisitIntervalCount;
	CheckBox cbTaskNextDayVisitDeclineFlag;
	EditText etTaskNextDayVisitDecilneRatio;
	EditText etTaskNextDayVisitDecilneMin;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_create);
		// Get arguments
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			finish();
			return;
		}

		mTaskAttribute = (TaskAttribute) extras.get("mTaskAttribute");
		TextView mTextView = (TextView) findViewById(R.id.data_textview_readme);
		mTextView.setVisibility(View.GONE);
		LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.data_LinearLayout);
		mLinearLayout.setVisibility(View.GONE);

		etTaskName = (EditText) findViewById(R.id.et_task_name);
		etTaskName.setText(mTaskAttribute.getTaskName() + "");
		etTaskName.setEnabled(false);

		etTaskDesc = (EditText) findViewById(R.id.et_task_desc);
		etTaskDesc.setText(mTaskAttribute.getTaskDesc() + "");
		etTaskDesc.setEnabled(false);

		etTaskNewdata = (EditText) findViewById(R.id.et_task_newdata);
		etTaskNewdata.setText(mTaskAttribute.getTaskNewdata() + "");
		etTaskNewdata.setEnabled(false);

		etTaskNumber = (EditText) findViewById(R.id.et_task_number);
		etTaskNumber.setText(mTaskAttribute.getTaskNumber() + "");
		etTaskNumber.setEnabled(false);

		etTaskReturnratio = (EditText) findViewById(R.id.et_task_return_ratio);
		etTaskReturnratio.setText(mTaskAttribute.getTaskReturnratio() + "");
		etTaskReturnratio.setEnabled(false);

		cbTaskDeclineFlag = (CheckBox) findViewById(R.id.cb_task_flag_decline_flag);
		cbTaskDeclineFlag.setChecked(mTaskAttribute.isTaskDeclineFlag());
		etTaskDecilneRatio = (EditText) findViewById(R.id.et_task_decilne_ratio);
		etTaskDecilneMin = (EditText) findViewById(R.id.et_task_decilne_min);
		if (cbTaskDeclineFlag.isChecked()) {
			etTaskDecilneRatio.setText(mTaskAttribute.getTaskDecilneRatio() + "");
			etTaskDecilneMin.setText(mTaskAttribute.getTaskDecilneMin() + "");
			etTaskDecilneRatio.setEnabled(true);
			etTaskDecilneMin.setEnabled(true);
		} else {
			// etTaskDecilneRatio.setEnabled(true);
			// etTaskDecilneMin.setEnabled(true);
		}

		cbTaskNextDayFlag = (CheckBox) findViewById(R.id.cb_task_flag_next_day_visit_flag);
		cbTaskNextDayFlag.setChecked(mTaskAttribute.isTaskNextDayFlag());

		etTaskNextDayVisitInterval = (EditText) findViewById(R.id.et_task_next_day_visit_interval);
		etTaskNextDayVisitIntervalReturnRatio = (EditText) findViewById(R.id.et_task_next_day_visit_interval_return_ratio);
		etTaskNextDayVisitIntervalCount = (EditText) findViewById(R.id.et_task_next_day_visit_interval_count);

		cbTaskNextDayVisitDeclineFlag = (CheckBox) findViewById(R.id.cb_task_next_day_visit_flag_decline_yes);
		etTaskNextDayVisitDecilneRatio = (EditText) findViewById(R.id.et_task_next_day_visit_decilne_ratio);
		etTaskNextDayVisitDecilneMin = (EditText) findViewById(R.id.et_task_next_day_visit_decilne_min);

		if (cbTaskNextDayFlag.isChecked()) {
			etTaskNextDayVisitInterval.setText(mTaskAttribute.getTaskNextDayVisitInterval() + "");
			etTaskNextDayVisitInterval.setEnabled(true);

			etTaskNextDayVisitIntervalReturnRatio.setText(mTaskAttribute.getTaskNextDayVisitIntervalReturnRatio() + "");
			etTaskNextDayVisitIntervalReturnRatio.setEnabled(true);

			etTaskNextDayVisitIntervalCount.setText(mTaskAttribute.getTaskNextDayVisitIntervalCount() + "");
			etTaskNextDayVisitIntervalCount.setEnabled(true);

			cbTaskNextDayVisitDeclineFlag.setChecked(mTaskAttribute.isTaskNextDayVisitDeclineFlag());
			if (cbTaskNextDayVisitDeclineFlag.isChecked()) {
				etTaskNextDayVisitDecilneRatio.setText(mTaskAttribute.getTaskNextDayVisitDecilneRatio() + "");
				etTaskNextDayVisitDecilneRatio.setEnabled(true);

				etTaskNextDayVisitDecilneMin.setText(mTaskAttribute.getTaskNextDayVisitDecilneMin() + "");
				etTaskNextDayVisitDecilneMin.setEnabled(true);

			} else {
				// etTaskNextDayVisitDecilneRatio.setEnabled(false);
				// etTaskNextDayVisitDecilneMin.setEnabled(false);
			}
		} else {
			// etTaskNextDayVisitInterval.setEnabled(false);
			// etTaskNextDayVisitIntervalReturnRatio.setEnabled(false);
			// etTaskNextDayVisitIntervalCount.setEnabled(false);
			// etTaskNextDayVisitDecilneRatio.setEnabled(false);
			// etTaskNextDayVisitDecilneMin.setEnabled(false);
			// cbTaskNextDayVisitDeclineFlag.setEnabled(false);
		}
		initdata();
	}

	private void initdata()
	{
		// TODO Auto-generated method stub
		// 基本任务属性：是否需要递减
		cbTaskDeclineFlag.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				// TODO Auto-generated method stub
				if (isChecked) {
					etTaskDecilneRatio.setEnabled(true);
					etTaskDecilneMin.setEnabled(true);
				} else {
					etTaskDecilneRatio.setEnabled(false);
					etTaskDecilneRatio.setText("");
					etTaskDecilneMin.setEnabled(false);
					etTaskDecilneMin.setText("");
				}
			}
		});
		// 是否开启隔日回访
		cbTaskNextDayFlag.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				// TODO Auto-generated method stub
				if (isChecked) {
					etTaskNextDayVisitInterval.setEnabled(true);
					etTaskNextDayVisitIntervalReturnRatio.setEnabled(true);
					etTaskNextDayVisitIntervalCount.setEnabled(true);
					cbTaskNextDayVisitDeclineFlag.setEnabled(true);
				} else {
					etTaskNextDayVisitInterval.setEnabled(false);
					etTaskNextDayVisitInterval.setText("");
					etTaskNextDayVisitIntervalReturnRatio.setEnabled(false);
					etTaskNextDayVisitIntervalReturnRatio.setText("");
					etTaskNextDayVisitIntervalCount.setEnabled(false);
					etTaskNextDayVisitIntervalCount.setText("");
					cbTaskNextDayVisitDeclineFlag.setChecked(false);
					cbTaskNextDayVisitDeclineFlag.setEnabled(false);
				}
			}
		});
		// 隔日回访是否需要递减
		cbTaskNextDayVisitDeclineFlag.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				// TODO Auto-generated method stub
				if (isChecked) {
					etTaskNextDayVisitDecilneRatio.setEnabled(true);
					etTaskNextDayVisitDecilneMin.setEnabled(true);
				} else {
					etTaskNextDayVisitDecilneRatio.setEnabled(false);
					etTaskNextDayVisitDecilneRatio.setText("");
					etTaskNextDayVisitDecilneMin.setEnabled(false);
					etTaskNextDayVisitDecilneMin.setText("");
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		if (inflater != null && PrivacyService.checkClient()) {
			inflater.inflate(R.menu.settings, menu);
			return true;
		} else
			return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		case R.id.menu_cancel:
			finish();
			return true;
		case R.id.menu_save:
			optionSave();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void optionSave()
	{
		// TODO Auto-generated method stub

		ContentValues values = new ContentValues();
		values.put(TaskAttribute.TASKNAME, etTaskName.getText().toString().trim());
		values.put(TaskAttribute.TASKDESC, etTaskDesc.getText().toString().trim());
		values.put(TaskAttribute.TASKNEWDATA, etTaskNewdata.getText().toString().trim());
		values.put(TaskAttribute.TASKNUMBER, etTaskNumber.getText().toString().trim());
		values.put(TaskAttribute.TASKRETURNRATIO, etTaskReturnratio.getText().toString().trim());

		// 需要递减
		if (cbTaskDeclineFlag.isChecked()) {
			values.put(TaskAttribute.TASKDECLINEFLAG, true);
			String str1 = etTaskDecilneRatio.getText().toString().trim();
			if ("".equals(str1)) {
				values.put(TaskAttribute.TASKDECILNERATIO, "0");
			} else {
				values.put(TaskAttribute.TASKDECILNERATIO, etTaskDecilneRatio.getText().toString().trim());
			}

			String str2 = etTaskDecilneMin.getText().toString().trim();
			if ("".equals(str2)) {
				values.put(TaskAttribute.TASKDECILNEMIN, "0");
			} else {
				values.put(TaskAttribute.TASKDECILNEMIN, etTaskDecilneMin.getText().toString().trim());
			}

		} else {
			values.put(TaskAttribute.TASKDECLINEFLAG, false);
			values.put(TaskAttribute.TASKDECILNERATIO, "0");
			values.put(TaskAttribute.TASKDECILNEMIN, "0");
		}

		// 是否开启隔日回访
		if (cbTaskNextDayFlag.isChecked()) {
			values.put(TaskAttribute.TASKNEXTDAYFLAG, true);
			String s = etTaskNextDayVisitInterval.getText().toString().trim();
			if ("".equals(s)) {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVAL, "0");
			} else {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVAL, etTaskNextDayVisitInterval.getText().toString().trim());
			}

			String ss = etTaskNextDayVisitIntervalReturnRatio.getText().toString().trim();
			if ("".equals(ss)) {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALRETURNRATIO, "0");
			} else {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALRETURNRATIO, ss);
			}

			String zz = etTaskNextDayVisitIntervalCount.getText().toString().trim();
			if ("".equals(zz)) {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALCOUNT, "0");
			} else {
				values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALCOUNT, etTaskNextDayVisitIntervalCount.getText().toString().trim());
			}
		} else {
			values.put(TaskAttribute.TASKNEXTDAYFLAG, false);
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVAL, "0");
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALRETURNRATIO, "0");
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALCOUNT, "0");
		}

		// 隔日递减是否

		if (cbTaskNextDayVisitDeclineFlag.isChecked()) {
			values.put(TaskAttribute.TASKNEXTDAYVISITDECLINEFLAG, true);
			String s = etTaskNextDayVisitDecilneRatio.getText().toString().trim();
			if ("".equals(s)) {
				values.put(TaskAttribute.TASKNEXTDAYVISITDECILNERATIO, "0");
			} else {
				values.put(TaskAttribute.TASKNEXTDAYVISITDECILNERATIO, etTaskNextDayVisitDecilneRatio.getText().toString().trim());
			}

			String ss = etTaskNextDayVisitDecilneMin.getText().toString().trim();
			if ("".equals(ss)) {
				values.put(TaskAttribute.TASKNEXTDAYVISITDECILNEMIN, "0");
			} else {
				values.put(TaskAttribute.TASKNEXTDAYVISITDECILNEMIN, etTaskNextDayVisitDecilneMin.getText().toString().trim());
			}

		} else {
			values.put(TaskAttribute.TASKNEXTDAYVISITDECLINEFLAG, false);
			values.put(TaskAttribute.TASKNEXTDAYVISITDECILNERATIO, "0");
			values.put(TaskAttribute.TASKNEXTDAYVISITDECILNEMIN, "0");
		}
		// 创建任务是否成功
		long state = DBMgr.getInstance(ActivityEditTask.this).update_task(values);
		if (state > 0) {
			Toast.makeText(ActivityEditTask.this, "操作成功！", 1).show();
			finish();
		}
	}

}
