package com.hy.xp.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.hy.xp.app.task.ActivityModel;
import com.hy.xp.app.task.DBMgr;
import com.hy.xp.app.task.InitData;
import com.hy.xp.app.task.PhoneDataBean;
import com.hy.xp.app.task.TaskAttribute;
import com.hy.xp.app.task.XpHelper;
import com.hy.xp.app.task.xpmodel;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;

public class ManagerCertermActivity extends Activity {
	
	private Spinner staskname=null;
	
	private List<View> listview = new ArrayList<View>();
	private Activity mcontext = null;
	
	private ListView appset = null;
	private Button hidapplist = null;
	
	private EditText et_taskname = null;	
	private EditText et_datanew = null;
	
	//单日回访
	private EditText et_datastaylv = null;
	private Spinner sp_datastayway = null;
	
	//单日递减
	private CheckBox ck_datalowflag = null;
	private EditText et_datalowlv = null;
	private EditText et_datalowest	= null;
	
	//隔日信息
	private CheckBox ck_nextdayflag = null;
	private EditText et_nextdayinterval = null;
	private EditText et_nextdayintervalcount = null;
	
	private EditText et_nextdaystaylv = null;
	private Spinner sp_nextdaystayway = null;
	
	private CheckBox ck_nextdatalowflag = null;
	private EditText et_nextdatalowlv = null;
	private EditText et_nextdatalowest	= null;
	
	//隔周信息
	private CheckBox ck_nextweekflag = null;
	private EditText et_nextweekstaylv = null;
	private Spinner sp_nextweekstayway = null;
	
	private CheckBox ck_nextweeklowflag = null;
	private EditText et_nextweeklowlv = null;
	private EditText et_nextweeklowest	= null;
	
	//隔月信息
	private CheckBox ck_nextmonthflag = null;
	private EditText et_nextmonthstaylv = null;
	private Spinner sp_nextmonthstayway = null;
		
	private CheckBox ck_nextmonthlowflag = null;
	private EditText et_nextmonthlowlv = null;
	private EditText et_nextmonthlowest	= null;
	
	private CheckBox ck_voice = null;
	
	//二次激活
	private CheckBox ck_secondeliveflag = null;	
	private EditText et_secondlivef = null;
	private EditText et_secondlives = null;
	
	private RadioGroup getway = null;
	private RadioGroup filter = null;
	
	private Button createtask = null;
	private Button editetask = null;
	private Button deltetask = null;
	private Button starttask = null;
	private Button importdata = null;
	private Button appsetting = null;
	
	private Button oldversion = null;
	
	private Button softwareusertime = null;
	private Button createphoneinfo = null;
	private Button importmachine = null;
	
	private TextView newdatashow = null;
	private TextView backdatashow = null;
	
	private TextView nowdate = null;
	
	//标示 编辑按钮 和 保存按钮 两种状态
	private static int editstat = bstat.Edite.ordinal();
	private static int createstat = bstat.Edite.ordinal();
			
	private static enum bstat {
		Edite,
		Save
	}
	private final int CREATE = 0;
	private final int EDITE = 1;
	
	private DBMgr dbmgr;
	
	private String mFileName = "";
	private static final int REQUEST_CHOOSER = 1234;
	
	private static ExecutorService mExecutor = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors(),
			new PriorityThreadFactory());

	private static class PriorityThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.managercertermview);
		mcontext = this;
		
		Meta.annotate(getResources());
		
		TabHost th = (TabHost)findViewById(R.id.tabhost);
        th.setup();
        th.addTab(th.newTabSpec("tab1").setIndicator("单日").setContent(R.id.tab1));
        th.addTab(th.newTabSpec("tab2").setIndicator("隔日").setContent(R.id.tab2));
        th.addTab(th.newTabSpec("tab3").setIndicator("隔周").setContent(R.id.tab3));
        th.addTab(th.newTabSpec("tab4").setIndicator("隔月").setContent(R.id.tab4));
		
        updatelist();
        
		initUI();
		initData();
		initPrivacyManager();
	}
	
	private void updatelist(){
		if(DBMgr.getListapp() != null){
			AppAdapte.dataselected = DBMgr.getListapp();			
		}else{
			AppAdapte.dataselected = new ArrayList<String>();
		}
	}
	
	private void initPrivacyManager() {
        if(!PrivacyService.checkClient()) {
            return;
        }
        PrivacyManager.setSetting(0x0, "OnDemand", Boolean.toString(false));
        try {
            String s = Environment.getExternalStorageState();
            if("mounted".equals(s)) {
                File sdcardDir = Environment.getExternalStorageDirectory();
                String path = sdcardDir+"/xp_log/";
                File logDir = new File(path);
                if(!logDir.exists()) {
                    logDir.mkdirs();
                    new File("/mnt/sdcard/xp_datafile/").mkdir();
                }
                Runtime.getRuntime().exec(path);
                return;
            }
        } catch(IOException e1) {
            e1.printStackTrace();
        }
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		List<xpmodel> list = DBMgr.getInstance(ManagerCertermActivity.this).getXpmpdellist();
		if(list == null || list.size() < 1){
			createphoneinfo.setEnabled(false);
		}else{
			createphoneinfo.setEnabled(true);
		}
		inittaskprocess();
	}

	public void inittaskprocess(){
		int[] backrst = dbmgr.getbackdatacount();
		int taskid = dbmgr.getLastnewCord(DBMgr.getCurrentTaskname(), AppAdapte.dataselected)[1];
		int[] newrst = dbmgr.getnewdatacount(taskid);
		int newdata = newrst[0] -1>=0?newrst[0]-1:0;
		newdatashow.setText(newdata+"/"+newrst[1]);
		backdatashow.setText(backrst[0]+"/"+backrst[1]);
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("",Locale.SIMPLIFIED_CHINESE); 
		sdf.applyPattern("yyyy年MM月dd日"); 
		nowdate.setText(dbmgr.getdayinfo(taskid)+"天/"+sdf.format(date));
	}

	@Override
	protected void onPause() {
		super.onPause();
		stor_secondlive(ck_secondeliveflag.isChecked());
	}

	private void initUI(){
		if(listview.size() > 0){
			return;
		}
		
		newdatashow = (TextView) findViewById(R.id.newdatashow);
		backdatashow = (TextView) findViewById(R.id.backdatashow);
		
		staskname = (Spinner) findViewById(R.id.taskname);
		staskname.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				try{
					opionTaskselect(parent.getItemAtPosition(position).toString());
					initData();
				}catch(Exception e){
					
				}				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
			
		});
		
		dbmgr = DBMgr.getInstance(this);
		String taskname = DBMgr.getCurrentTaskname();
		
		et_taskname = (EditText) findViewById(R.id.et_taskname);
		listview.add(et_taskname);
		
		editetask = (Button) findViewById(R.id.edittask);
		deltetask = (Button) findViewById(R.id.deletetask);
		starttask = (Button) findViewById(R.id.starttask);
		importdata = (Button) findViewById(R.id.importdata);
		appsetting = (Button) findViewById(R.id.appsetting);
		createtask = (Button) findViewById(R.id.createtask);
		softwareusertime = (Button) findViewById(R.id.softwareusertime);
		oldversion = (Button) findViewById(R.id.oldsetting);
		createphoneinfo = (Button) findViewById(R.id.createphoneinfo);
		importmachine = (Button) findViewById(R.id.importmachine);
		
		nowdate = (TextView) findViewById(R.id.nowdate);
		
		if(taskname == null){
			staskname.setVisibility(View.GONE);
			et_taskname.setVisibility(View.VISIBLE);
			editetask.setEnabled(false);
		}else{
			et_taskname.setVisibility(View.GONE);
			staskname.setVisibility(View.VISIBLE);
			
			//初始化任务名称列表		
			List<String> taskstrings = dbmgr.getTaskNameList();
			SpinnerAdapter adapter = new ArrayAdapter<String>(this, R.layout.myspanner, dbmgr.getTaskNameList());
			staskname.setAdapter(adapter);				
			staskname.setSelection(taskstrings.indexOf(taskname));
		}
		
		et_datanew = (EditText) findViewById(R.id.datanew);
		listview.add(et_datanew);
		
		et_datastaylv = (EditText) findViewById(R.id.datastaylv);
		listview.add(et_datastaylv);
		sp_datastayway = (Spinner) findViewById(R.id.datastayway);
		listview.add(sp_datastayway);
		
		ck_datalowflag = (CheckBox) findViewById(R.id.datalowflag);
		listview.add(ck_datalowflag);
		et_datalowlv = (EditText) findViewById(R.id.datalowlv);
		listview.add(et_datalowlv);
		et_datalowest = (EditText) findViewById(R.id.datalowest);
		listview.add(et_datalowest);
		
		ck_voice = (CheckBox) findViewById(R.id.hasvoice);
		ck_voice.setOnCheckedChangeListener(new OnCheckedChangerListener());
		if(!DBMgr.isallowvoice()){
			ck_voice.setChecked(false);
		}
				
		hiddatalow(ck_datalowflag.isChecked());
		ck_datalowflag.setOnCheckedChangeListener(new OnCheckedChangerListener());
		
		//隔日
		ck_nextdayflag = (CheckBox) findViewById(R.id.nextdayflag);
		et_nextdayinterval = (EditText) findViewById(R.id.nextdayinterval);
		et_nextdayintervalcount = (EditText) findViewById(R.id.nextdayintervalcount);
		
		listview.add(ck_nextdayflag);
		listview.add(et_nextdayinterval);
		listview.add(et_nextdayintervalcount);
		
		et_nextdaystaylv = (EditText) findViewById(R.id.nextdaystaylv);
		sp_nextdaystayway = (Spinner) findViewById(R.id.nextdaystayway);
		
		listview.add(et_nextdaystaylv);
		listview.add(sp_nextdaystayway);
		
		ck_nextdatalowflag = (CheckBox) findViewById(R.id.nextdaydatalowflag);
		et_nextdatalowlv = (EditText) findViewById(R.id.nextdaydatalowlv);
		et_nextdatalowest = (EditText) findViewById(R.id.nextdaydatalowest);
	
		listview.add(ck_nextdatalowflag);
		listview.add(et_nextdatalowlv);
		listview.add(et_nextdatalowest);
		
		//隔周
		ck_nextweekflag = (CheckBox) findViewById(R.id.nextweekflag);
		
		listview.add(ck_nextweekflag);
		
		et_nextweekstaylv = (EditText) findViewById(R.id.nextweekstaylv);
		sp_nextweekstayway = (Spinner) findViewById(R.id.nextweekstayway);
		
		listview.add(et_nextweekstaylv);
		listview.add(sp_nextweekstayway);
		
		ck_nextweeklowflag = (CheckBox) findViewById(R.id.nextweekdatalowflag);
		et_nextweeklowlv = (EditText) findViewById(R.id.nextweekdatalowlv);
		et_nextweeklowest = (EditText) findViewById(R.id.nextweekdatalowest);
	
		listview.add(ck_nextweeklowflag);
		listview.add(et_nextweeklowlv);
		listview.add(et_nextweeklowest);
		
		//隔月		
		ck_nextmonthflag = (CheckBox) findViewById(R.id.nextmonthflag);
		
		listview.add(ck_nextmonthflag);
		
		et_nextmonthstaylv = (EditText) findViewById(R.id.nextmonthstaylv);
		sp_nextmonthstayway = (Spinner) findViewById(R.id.nextmonthstayway);
		
		listview.add(et_nextmonthstaylv);
		listview.add(sp_nextmonthstayway);
		
		ck_nextmonthlowflag = (CheckBox) findViewById(R.id.nextmonthdatalowflag);
		et_nextmonthlowlv = (EditText) findViewById(R.id.nextmonthdatalowlv);
		et_nextmonthlowest = (EditText) findViewById(R.id.nextmonthdatalowest);
	
		listview.add(ck_nextmonthlowflag);
		listview.add(et_nextmonthlowlv);
		listview.add(et_nextmonthlowest);
		
		//二次激活
		ck_secondeliveflag = (CheckBox) findViewById(R.id.secondeliveflag);
	
		et_secondlivef = (EditText) findViewById(R.id.secondlivef);
		et_secondlives = (EditText) findViewById(R.id.secondlives);
	
		hiddataseconde(ck_secondeliveflag.isChecked());
		ck_secondeliveflag.setOnCheckedChangeListener(new OnCheckedChangerListener());
		
		getway = (RadioGroup) findViewById(R.id.getway);
		getway.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				int value = 0;
				switch (arg1) {
				case R.id.xlstayflag:
					value = 0;
					break;
				case R.id.lxstayflag:
					value = 1;
					break;
				case R.id.randomstayflag:
					value = 2;
					break;
				case R.id.onlystayflag:
					value = 3;
					break;
				default:
					break;
				}
				
				stor_getstayway(value);
			}
		});
		
		filter = (RadioGroup) findViewById(R.id.filter);
		filter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				int value = 0;
				switch (arg1) {
					case R.id.filter_user:
						value = 0;
						filter_value = 0;
						break;
					case R.id.filter_system:
						value = 1;
						filter_value = 1;
						break;
				}
				updatelistview(value);
			}
		});
		
		
		int id = R.id.xlstayflag;
		switch (DBMgr.getGetStayway()) {
			case 0:
				id = R.id.xlstayflag;
				break;
			case 1:
				id = R.id.lxstayflag;	
				break;
			case 2:
				id = R.id.randomstayflag;
				break;
			case 3:
				id = R.id.onlystayflag;
				break;
			default:
				break;
		}
		((RadioButton)getway.findViewById(id)).setChecked(true);
		
		appset = (ListView) findViewById(R.id.appset);
		hidapplist = (Button) findViewById(R.id.hidapplist);
		
		hidapplist.setOnClickListener(new OnClikedListener());

		editetask.setOnClickListener(new OnClikedListener());
		deltetask.setOnClickListener(new OnClikedListener());
		starttask.setOnClickListener(new OnClikedListener());
		importdata.setOnClickListener(new OnClikedListener());
		appsetting.setOnClickListener(new OnClikedListener());
		createtask.setOnClickListener(new OnClikedListener());
		softwareusertime.setOnClickListener(new OnClikedListener());
		oldversion.setOnClickListener(new OnClikedListener());
		importmachine.setOnClickListener(new OnClikedListener());
		createphoneinfo.setOnClickListener(new OnClikedListener());
		
		List<xpmodel> list = DBMgr.getInstance(ManagerCertermActivity.this).getXpmpdellist();
		if(list == null || list.size() < 1){
			createphoneinfo.setEnabled(false);
		}else{
			createphoneinfo.setEnabled(true);
		}
		
		DisableView();
	}
	
	private void updatelistview(int value) {
		try{
			SimpleAdapter mAppAdapter = null;
			if(value == 0){
				mAppAdapter = new AppAdapte(mcontext, 
	            		list_user, 
	            		R.layout.listviewcontent, 
	            		new String[] { "name", "icon" }, 
	            		new int[] {R.id.appname, R.id.appicon});
			}else{
				mAppAdapter = new AppAdapte(mcontext, 
	            		list_system, 
	            		R.layout.listviewcontent, 
	            		new String[] { "name", "icon" }, 
	            		new int[] {R.id.appname, R.id.appicon});
			}
			
			appset.setAdapter(mAppAdapter);
			mAppAdapter.notifyDataSetChanged();
		}catch(Exception e){
			
		}
	}
	
	private void initData(){
		String name = DBMgr.getCurrentTaskname();
		if(name != null && !"".equals(name))
		{
			List<TaskAttribute> listattribute  = dbmgr.getTaskAttributeByTaskName(name);
			if(listattribute.size()>0){
				TaskAttribute taskattribute = listattribute.get(0);
				
				inittaskprocess();
				
				et_taskname.setVisibility(View.GONE);
				
				et_datanew.setText(taskattribute.getTaskNewdata()+"");
				et_datastaylv.setText((int)taskattribute.getTaskReturnratio()+"");
				sp_datastayway.setSelection(taskattribute.getTaskStayWay());
				
				if(taskattribute.isTaskDeclineFlag()){
					ck_datalowflag.setChecked(true);
					et_datalowlv.setText((int)taskattribute.getTaskDecilneRatio()+"");
					et_datalowest.setText(taskattribute.getTaskDecilneMin()+"");
				}else{
					ck_datalowflag.setChecked(false);
				}
				
				if(taskattribute.isTaskNextDayFlag()){
					ck_nextdayflag.setChecked(true);
					
					et_nextdayinterval.setText(taskattribute.getTaskNextDayVisitInterval()+"");
					et_nextdayintervalcount.setText(taskattribute.getTaskNextDayVisitIntervalCount()+"");
					
					et_nextdaystaylv.setText((int)taskattribute.getTaskNextDayVisitIntervalReturnRatio()+"");
					sp_nextdaystayway.setSelection(taskattribute.getTaskNextDayVisitStayWay());
					
					if(taskattribute.isTaskNextDayVisitDeclineFlag()){
						ck_nextdatalowflag.setChecked(true);
						et_nextdatalowlv.setText((int)taskattribute.getTaskNextDayVisitDecilneRatio()+"");
						et_nextdatalowest.setText(taskattribute.getTaskNextDayVisitDecilneMin()+"");
					}else{
						ck_nextdatalowflag.setChecked(false);
					}
					
				}else{
					ck_nextdayflag.setChecked(false);
				}
				
				if(taskattribute.isTaskNextWeekFlag()){
					ck_nextweekflag.setChecked(true);
					
					et_nextweekstaylv.setText((int)taskattribute.getTaskNextWeekVisitIntervalReturnRatio()+"");
					sp_nextweekstayway.setSelection(taskattribute.getTaskNextWeekVisitStayWay());
					
					if(taskattribute.isTaskNextWeekVisitDeclineFlag()){
						ck_nextweeklowflag.setChecked(true);
						et_nextweeklowlv.setText((int)taskattribute.getTaskNextWeekVisitDecilneRatio()+"");
						et_nextweeklowest.setText(taskattribute.getTaskNextWeekVisitDecilneMin()+"");
					}else{
						ck_nextweeklowflag.setChecked(false);
					}
					
				}else{
					ck_nextweekflag.setChecked(false);
				}
				
				if(taskattribute.isTaskNextMonthFlag()){
					ck_nextmonthflag.setChecked(true);
					
					et_nextmonthstaylv.setText((int)taskattribute.getTaskNextMonthVisitIntervalReturnRatio()+"");
					sp_nextmonthstayway.setSelection(taskattribute.getTaskNextMonthVisitStayWay());
					
					if(taskattribute.isTaskNextMonthVisitDeclineFlag()){
						ck_nextmonthlowflag.setChecked(true);
						et_nextmonthlowlv.setText((int)taskattribute.getTaskNextMonthVisitDecilneRatio()+"");
						et_nextmonthlowest.setText(taskattribute.getTaskNextMonthVisitDecilneMin()+"");
					}else{
						ck_nextmonthlowflag.setChecked(false);
					}
					
				}else{
					ck_nextmonthflag.setChecked(false);
				}	
			}
			
			if(DBMgr.issecondelive()){
				ck_secondeliveflag.setChecked(true);
				et_secondlivef.setText(DBMgr.getsecondef()+"");
				et_secondlives.setText(DBMgr.getsecondes()+"");
			}else{
				ck_secondeliveflag.setChecked(false);
			}
		}
	}
	
	private void hiddatalow(boolean flag){
		if(flag){
			et_datalowlv.setVisibility(View.VISIBLE);
			et_datalowest.setVisibility(View.VISIBLE);
		}else{
			et_datalowlv.setVisibility(View.GONE);
			et_datalowest.setVisibility(View.GONE);
		}
	}
	
	private void inittasknamespinner(){		
		List<String> taskstrings = dbmgr.getTaskNameList();
		if(taskstrings == null || taskstrings.size() == 0){
			staskname.setVisibility(View.GONE);
			et_taskname.setVisibility(View.VISIBLE);
			et_taskname.setEnabled(false);
			editetask.setEnabled(false);
			return;
		}
		SpinnerAdapter adapter = new ArrayAdapter<String>(this, R.layout.myspanner, taskstrings);
		try{
			staskname.setAdapter(adapter);	
		}catch(Exception e){
			return;
		}			
		String name = DBMgr.getCurrentTaskname();
		if(name != null && !name.endsWith("")){
			if(taskstrings.indexOf(name)>-1){
				staskname.setSelection(taskstrings.indexOf(name));				
			}else{
				opionTaskselect(taskstrings.get(0));
				staskname.setSelection(0);	
			}
		}else{
			if(taskstrings.size() > 0){
				opionTaskselect(taskstrings.get(0));
				staskname.setSelection(0);				
			}
		}
	}
	
	class OnCheckedChangerListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.datalowflag:
				hiddatalow(isChecked);
				break;
			case R.id.secondeliveflag:
				hiddataseconde(isChecked);
				break;
			case R.id.hasvoice:
				DBMgr.setallowvoice(isChecked);
				break;
			default:
				break;
			}
		}
	}
	
	class OnClikedListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.starttask:
				starttask();
				break;
			case R.id.edittask:
				edit_or_save(EDITE);
				break;
			case R.id.deletetask:
				deletetask();
				break;
			case R.id.importdata:
				importdata();
				break;
			case R.id.appsetting:
				appsetting();
				break;
			case R.id.createtask:
				edit_or_save(CREATE);
				break;
			case R.id.hidapplist:
				appset.setVisibility(View.GONE);
				v.setVisibility(View.GONE);
				Saveapplist(AppAdapte.dataselected);
				inittaskprocess();
				break;
			case R.id.softwareusertime:
				optionApptime();
				break;
			case R.id.oldsetting:
				Intent it = new Intent(ManagerCertermActivity.this, ActivityMain.class);
				startActivity(it);
				break;
			case R.id.importmachine:
				optionModel();
				break;
			case R.id.createphoneinfo:
				//TODO 生成数据
				optioncreateinfo();
				break;
			default:
				break;
			}
		}
	}
	
	private void stor_getstayway(int way) {
		DBMgr.setGetStayway(way);
	}
	
	private void stor_secondlive(boolean ischecked){
		DBMgr.setsecondelive(ischecked);
		if(ischecked){
			try{
				DBMgr.setsecondlivef(Integer.valueOf(et_secondlivef.getText().toString().trim()));
				DBMgr.setsecondlives(Integer.valueOf(et_secondlives.getText().toString().trim()));
			}catch(Exception e){
				
			}
		}
	}
	
	private void appsetting() {
		AppListTask appListTask = new AppListTask();
		appListTask.executeOnExecutor(mExecutor, (Object) null);		
	}

	private void importdata() {
		showChooser();
	}
	
	public static boolean ischanged(List<String> applist) {
        List<String> listapp = DBMgr.getListapp();
        if(applist.size() > 0) {
            if(listapp == null) {
                return true;
            }
            if(applist.size() == listapp.size()) {
            	boolean isexists = true;
                Collections.sort(applist);
                Collections.sort(listapp);
                if(applist.equals(listapp)){
                   isexists = false;
                }
                return isexists;
            }
        }
        return true;
    }
	
	private void optioncreateinfo(){
		//TODO 加入导入机型文件的提示信息
		List<xpmodel> list = DBMgr.getInstance(ManagerCertermActivity.this).getXpmpdellist();
		if(list == null || list.size() < 1){
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(ManagerCertermActivity.this);
			mBuilder.setTitle("请先导入机型文件!");
			mBuilder.setPositiveButton(ManagerCertermActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			mBuilder.show();
			return;
		}
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mView = inflater.inflate(R.layout.apptime, null);
		((TextView )mView.findViewById(R.id.apptime_readme)).setVisibility(View.GONE);
		final EditText etAppusetime = (EditText) mView.findViewById(R.id.apptime_edit);
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(ManagerCertermActivity.this);
		String str = PrivacyManager.getSetting(0, PrivacyManager.cSettingTimeApp, "30");
		etAppusetime.setText(str);
		mBuilder.setView(mView);
		mBuilder.setTitle("选择生成数量");
		mBuilder.setIcon(R.drawable.ic_launcher);
		mBuilder.setPositiveButton(ManagerCertermActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				InitPhoneInfoTable iasyntask = new InitPhoneInfoTable();
				iasyntask.executeOnExecutor(mExecutor, etAppusetime.getText().toString());
			}
		});

		mBuilder.setNegativeButton(ManagerCertermActivity.this.getString(android.R.string.cancel), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});
		AlertDialog mAlertDialog = mBuilder.create();
		mAlertDialog.show();
	}
	
	 private void optionApptime()
		{
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View mView = inflater.inflate(R.layout.apptime, null);
			final EditText etAppusetime = (EditText) mView.findViewById(R.id.apptime_edit);
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(ManagerCertermActivity.this);
			String str = PrivacyManager.getSetting(0, PrivacyManager.cSettingTimeApp, "30");
			etAppusetime.setText(str);
			mBuilder.setView(mView);
			mBuilder.setTitle(R.string.menu_apptime);
			mBuilder.setIcon(R.drawable.ic_launcher);
			mBuilder.setPositiveButton(ManagerCertermActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					PrivacyManager.setSetting(0, PrivacyManager.cSettingTimeApp, etAppusetime.getText().toString());
				}
			});

			mBuilder.setNegativeButton(ManagerCertermActivity.this.getString(android.R.string.cancel), new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{

				}
			});
			AlertDialog mAlertDialog = mBuilder.create();
			mAlertDialog.show();
		}

	
	public static void Saveapplist(List<String> applist) {
    	if (ischanged(applist)){
    		  //TODO 不必清空数据库改用MD5值来存储
    	      //DBMgr.getInstance(ApplicationEx.getContextObject()).clean_task_cache(DBMgr.getCurrentTaskname());
    	    if(applist == null){
    	    	AppAdapte.dataselected = new ArrayList<String>();
    	    }else{
    	    	AppAdapte.dataselected = applist;
    	    }    		
    	}
    	  try
    	    {
    	      BufferedWriter localBufferedWriter = new BufferedWriter(new OutputStreamWriter(ApplicationEx.getContextObject().openFileOutput("applistselect", Context.MODE_PRIVATE)));
    	      StringBuffer localStringBuffer = new StringBuffer();
    	     
    	      for(int i=0; i<applist.size()-1; i++){    
    	    	  if(applist.size() == 1){
    	    		  break;
    	    	  }
    	    	  localStringBuffer.append(applist.get(i)).append("|");
    	      }
    	      if(applist.size() != 0){
    	    	  localStringBuffer.append(applist.get(applist.size() - 1));
    	      }
    	      localBufferedWriter.write(localStringBuffer.toString());
    	      localBufferedWriter.flush();
    	      localBufferedWriter.close();
    	    }
    	    catch (FileNotFoundException localFileNotFoundException)
    	    {
    	      localFileNotFoundException.printStackTrace();
    	      return;
    	    }
    	    catch (IOException localIOException)
    	    {
    	      localIOException.printStackTrace();
    	    }
    	return;
    }
	
	private void showChooser() {
		FileUtils.createGetContentIntent();
		Intent localIntent = new Intent(this, FileChooserActivity.class);
		try {
			startActivityForResult(localIntent, REQUEST_CHOOSER);
			return;
		} catch (ActivityNotFoundException localActivityNotFoundException) {
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CHOOSER) {
			if (resultCode == RESULT_OK) {
				final Uri uri = data.getData();

				String path = FileUtils.getPath(this, uri);

				if (path != null && FileUtils.isLocal(path)) {
					File file = new File(path);
					mFileName = file.getPath();
					showFileName();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}
	
	private void showFileName() {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		localBuilder.setTitle("导入提醒");
		localBuilder.setIcon(R.drawable.ic_chooser);
		localBuilder.setMessage(mFileName);
		localBuilder
				.setNegativeButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface paramAnonymousDialogInterface,
							int paramAnonymousInt) {
						Add_TaskAsyncTask mAsyncTask = new Add_TaskAsyncTask();
						mAsyncTask.executeOnExecutor(mExecutor, null);
					}
				})
				.setNeutralButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface paramAnonymousDialogInterface,
							int paramAnonymousInt) {
						paramAnonymousDialogInterface.cancel();
					}
				}).create().show();
	}

	private void deletetask() {
		Object item = staskname.getSelectedItem();
		if(item != null && !item.equals("")){
			optionTaskdelete(item.toString());
		}		
	}
	private void edit_or_save(int iscreate) {
			if(iscreate == CREATE){
				if(createstat == bstat.Edite.ordinal()){					
					//开启编辑模式
					switchtaskname(true, true);
					EnableView();
					//将按钮设置为保存
					createtask.setText("保存");
					createstat = bstat.Save.ordinal();
					editetask.setEnabled(false);
				}else{
					String value = et_taskname.getText().toString();
					if(value == null || "".equals(value) || value.equals(DBMgr.getCurrentTaskname())){
						Toast.makeText(mcontext, "任务名称冲突或者为空", Toast.LENGTH_LONG).show();
						return;
					}
					
					//开启编辑模式
					switchtaskname(true, false);
					DisableView();
					//将按钮设置为保存
					createtask.setText("创建任务");
					createstat = bstat.Edite.ordinal();
					
					option_savetask(true);
					editetask.setEnabled(true);
				}
			}else{
				if(editstat == bstat.Edite.ordinal()){
					//开启编辑模式
					switchtaskname(false, true);
					EnableView();
					et_taskname.setEnabled(false);
					//将按钮设置为保存
					editetask.setText("保存");
					editstat = bstat.Save.ordinal();
					createtask.setEnabled(false);
				}else{
					//开启编辑模式
					switchtaskname(true, false);
					DisableView();
					//将按钮设置为保存
					editetask.setText("编辑任务");
					editstat = bstat.Edite.ordinal();
					
					option_savetask(false);
					createtask.setEnabled(true);
				}
			}
	}
	private void option_savetask(boolean iscreate) {
		ContentValues values = new ContentValues();
		
		values.put(TaskAttribute.TASKNAME, getEditeValue(R.id.et_taskname));

		values.put(TaskAttribute.TASKNEWDATA, getEditeValue(R.id.datanew));
		values.put(TaskAttribute.TASKRETURNRATIO, getEditeValue(R.id.datastaylv));
		values.put(TaskAttribute.TASKSTAYWAY, getSpinnerIValue(R.id.datastayway));
		
		if(getCheckboxvalue(R.id.datalowflag)){
			values.put(TaskAttribute.TASKDECLINEFLAG, true);//TODO 需要确认是用1/0还是 true/false
			values.put(TaskAttribute.TASKDECILNERATIO, getEditeValue(R.id.datalowlv));
			values.put(TaskAttribute.TASKDECILNEMIN, getEditeValue(R.id.datalowest));
		}else{
			values.put(TaskAttribute.TASKDECLINEFLAG, false);
			values.put(TaskAttribute.TASKDECILNERATIO, "0");
			values.put(TaskAttribute.TASKDECILNEMIN, "0");
		}
		
		if(getCheckboxvalue(R.id.nextdayflag)){
			values.put(TaskAttribute.TASKNEXTDAYFLAG, true);
			
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVAL, getEditeValue(R.id.nextdayinterval));
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALCOUNT, getEditeValue(R.id.nextdayintervalcount));
			values.put(TaskAttribute.TASKNEXTDAYVISITINTERVALRETURNRATIO, getEditeValue(R.id.nextdaystaylv));
			values.put(TaskAttribute.TASKNEXTDAYVISITSTAYWAY, getSpinnerIValue(R.id.nextdaystayway));
			
			if(getCheckboxvalue(R.id.nextdaydatalowflag)){
				values.put(TaskAttribute.TASKNEXTDAYVISITDECLINEFLAG, true);
				
				values.put(TaskAttribute.TASKNEXTDAYVISITDECILNERATIO, getEditeValue(R.id.nextdaydatalowlv));
				values.put(TaskAttribute.TASKNEXTDAYVISITDECILNEMIN, getEditeValue(R.id.nextdaydatalowest));
			}else{
				values.put(TaskAttribute.TASKNEXTDAYVISITDECLINEFLAG, false);
			}
		}else{
			values.put(TaskAttribute.TASKNEXTDAYFLAG, false);
		}
		
		//周存留
		if(getCheckboxvalue(R.id.nextweekflag)){
			values.put(TaskAttribute.TASKNEXTWEEKFLAG, true);
			
			values.put(TaskAttribute.TASKNEXTWEEKVISITINTERVALRETURNRATIO, getEditeValue(R.id.nextweekstaylv));
			values.put(TaskAttribute.TASKNEXTWEEKVISITSTAYWAY, getSpinnerIValue(R.id.nextweekstayway));
			
			if(getCheckboxvalue(R.id.nextweekdatalowflag)){
				values.put(TaskAttribute.TASKNEXTWEEKVISITDECLINEFLAG, true);
				
				values.put(TaskAttribute.TASKNEXTWEEKVISITDECILNERATIO, getEditeValue(R.id.nextweekdatalowlv));
				values.put(TaskAttribute.TASKNEXTWEEKVISITDECILNEMIN, getEditeValue(R.id.nextweekdatalowest));
			}else{
				values.put(TaskAttribute.TASKNEXTWEEKVISITDECLINEFLAG, false);
			}
		}else{
			values.put(TaskAttribute.TASKNEXTWEEKFLAG, false);
		}
		
		//月存留
		if (getCheckboxvalue(R.id.nextmonthflag)) {
			values.put(TaskAttribute.TASKNEXTMONTHFLAG, true);

			values.put(TaskAttribute.TASKNEXTMONTHVISITINTERVALRETURNRATIO,
					getEditeValue(R.id.nextmonthstaylv));
			values.put(TaskAttribute.TASKNEXTMONTHVISITSTAYWAY,
					getSpinnerIValue(R.id.nextmonthstayway));

			if (getCheckboxvalue(R.id.nextmonthdatalowflag)) {
				values.put(TaskAttribute.TASKNEXTMONTHVISITDECLINEFLAG, true);

				values.put(TaskAttribute.TASKNEXTMONTHVISITDECILNERATIO,
						getEditeValue(R.id.nextmonthdatalowlv));
				values.put(TaskAttribute.TASKNEXTMONTHVISITDECILNEMIN,
						getEditeValue(R.id.nextmonthdatalowest));
			} else {
				values.put(TaskAttribute.TASKNEXTMONTHVISITDECLINEFLAG, false);
			}
		} else {
			values.put(TaskAttribute.TASKNEXTMONTHFLAG, false);
		}
		
		if(iscreate){
			dbmgr.add_task(values);
		}else{
			dbmgr.update_task(values);
		}
		opionTaskselect(values.get(TaskAttribute.TASKNAME).toString());
		inittasknamespinner();
		initData();
	}
	
	private void opionTaskselect(String taskname) {
		SharedPreferences preferences = ApplicationEx
				.getContextObject().getSharedPreferences(
						"task", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("currenttask",
				taskname).commit();
	}

	private void starttask(){
		InitData initdata = new InitData();
		initdata.setThis(this);
        SharedPreferences preferences = ApplicationEx.getContextObject().getSharedPreferences("task", Context.MODE_PRIVATE);
        String currenttaskname = preferences.getString("currenttask", null);
        if(currenttaskname != null) {
            try {
                initdata.execute((TaskAttribute)DBMgr.getInstance(this).getTaskAttributeByTaskName(currenttaskname).get(0));
            } catch(Exception localException1) {
            }
        }
        ApplicationInfoEx.resetseconde();
	}
	
	private void hiddataseconde(boolean isChecked) {
		/*if(!isChecked){
			et_secondlivef.setVisibility(View.GONE);
			et_secondlives.setVisibility(View.GONE);
		}else{
			et_secondlivef.setVisibility(View.VISIBLE);
			et_secondlives.setVisibility(View.VISIBLE);
		}	*/	
	}
	
	private void EnableView(){
		if(listview.size() > 0){
			for(View view:listview){
				view.setEnabled(true);
			}
		}
	}
	
	private void DisableView(){
		if(listview.size() > 0){
			for(View view:listview){
				view.setEnabled(false);
			}
		}
	}
	
	private void switchtaskname(boolean iscreate, boolean edite){
		if(iscreate && edite){
			staskname.setVisibility(View.GONE);
			et_taskname.setVisibility(View.VISIBLE);
			et_taskname.setEnabled(true);
		}else if(!iscreate && edite){
			staskname.setVisibility(View.GONE);
			et_taskname.setVisibility(View.VISIBLE);
			et_taskname.setText(DBMgr.getCurrentTaskname());
			et_taskname.setEnabled(false);
		}else if(!edite){
			staskname.setVisibility(View.VISIBLE);
			staskname.setSelection(0); //TODO 此处有待斟酌
			et_taskname.setVisibility(View.GONE);
			et_taskname.setEnabled(false);
		}
	}
	
	private String getEditeValue(int _id){
		return ((EditText)findViewById(_id)).getText().toString().trim();
	}
	
	private String getSpinnerSValue(int _id){
		return ((Spinner)findViewById(_id)).getSelectedItem().toString();
	}
	
	private int getSpinnerIValue(int _id){
		return ((Spinner)findViewById(_id)).getSelectedItemPosition();
	}	
	
	private boolean getCheckboxvalue(int _id){
		return ((CheckBox)findViewById(_id)).isChecked();
	}

	List<Map<String, ?>> list_user = null;
	List<Map<String, ?>> list_system = null;
	private int filter_value = 0;
	class AppListTask extends AsyncTask <Object, Integer, List<ApplicationInfoEx>>{
        private ProgressDialog mProgressDialog;
                
        protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(ManagerCertermActivity.this);
			mProgressDialog.setMessage(getString(R.string.msg_loading));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
        }
        
        @SuppressWarnings({ "unchecked", "unchecked", "unchecked" })
		protected void onPostExecute(List<ApplicationInfoEx> listApp) {        	
        	if (!ManagerCertermActivity.this.isFinishing()) {
				// Display app list
        		list_user = new ArrayList<Map<String,?>>();
        		list_system = new ArrayList<Map<String,?>>();
        		for(ApplicationInfoEx appinfo : listApp){
        			HashMap<String, Object> localHashMap = new HashMap<String, Object>();
                    localHashMap.put("name", appinfo.getApplicationName().get(0));
                    localHashMap.put("icon", appinfo.getIcon(ManagerCertermActivity.this));
                    localHashMap.put("uuid", Integer.valueOf(((ApplicationInfoEx)appinfo).getUid()));
                    localHashMap.put("packagename", ((ApplicationInfoEx)appinfo).getPackageName().get(0));
                    if ((localHashMap.get("name") != null) && (!"".equals(localHashMap.get("name")))){
                    	if(appinfo.isSystem()){
                    		list_system.add(localHashMap);
                    	}else{
                    		list_user.add(localHashMap);
                    	}
                    }                    	
        		}
        		SimpleAdapter mAppAdapter = null;
				if(filter_value == 0){
					mAppAdapter = new AppAdapte(mcontext, 
	                		list_user, 
	                		R.layout.listviewcontent, 
	                		new String[] { "name", "icon" }, 
	                		new int[] {R.id.appname, R.id.appicon});
				}else{
					mAppAdapter = new AppAdapte(mcontext, 
	                		list_system, 
	                		R.layout.listviewcontent, 
	                		new String[] { "name", "icon" }, 
	                		new int[] {R.id.appname, R.id.appicon});
				}
				
        		appset.setAdapter(mAppAdapter);
        		
        		appset.setVisibility(View.VISIBLE);
        		hidapplist.setVisibility(View.VISIBLE);
				
				if (mProgressDialog.isShowing())
					try {
						mProgressDialog.dismiss();
					} catch (IllegalArgumentException ignored) {
					}
			}

			super.onPostExecute(listApp);      	
        }

		@Override
		protected List<ApplicationInfoEx> doInBackground(Object... params) {
			return ApplicationInfoEx.getXApplicationList(ManagerCertermActivity.this, mProgressDialog);
		}
    }
	
	public class Add_TaskAsyncTask extends AsyncTask<Object, Integer, Object> {
		ProgressDialog mProgressDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(ManagerCertermActivity.this);
			mProgressDialog.setMessage("正在执行操作...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				File localFile = new File(ManagerCertermActivity.this.mFileName);
				List<PhoneDataBean> localList = Util.readcurray(
						localFile.getParent(), localFile.getName());
				DBMgr.getInstance(ApplicationEx.getContextObject()).plInsert(
						localList);
				Integer localInteger = Integer.valueOf(1);
				return localInteger;
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if (!ManagerCertermActivity.this.isFinishing()) {
				this.mProgressDialog.dismiss();
				if (result == null)
					Toast.makeText(ManagerCertermActivity.this, "无效的文件！", 1)
							.show();
				else{
					Toast.makeText(ManagerCertermActivity.this, "数据插入成功！", 1)
					.show();
				}
			}
		}
	}
	
	private void optionModel()
	{
		startActivity(new Intent(ManagerCertermActivity.this, ActivityModel.class));
	}
	
	private void optionTaskdelete(String taskname) {
		final String taskanem = taskname;
		String currenttaskname = DBMgr.getCurrentTaskname();
		if(currenttaskname != null && currenttaskname.equals(taskname)){
			opionTaskselect(null);
		}
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setTitle("删除提醒");
		mBuilder.setIcon(R.drawable.ic_launcher);
		mBuilder.setMessage("当前任务正在使用，确定删除" + taskanem + "任务？" + "自动备份任务!");
		mBuilder.setNegativeButton(this.getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// 先删除表中的数据
						dbmgr.clean_task(taskanem);
						inittasknamespinner();
					}
				});
		mBuilder.setNeutralButton(this.getString(android.R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.cancel();
					}
				});
		mBuilder.create().show();
	}
	
	public List<PhoneDataBean> CreateTaskDataFile(int tasknewdata, ProgressDialog dialog) {
		List<PhoneDataBean> mList = new ArrayList<PhoneDataBean>();
		Random r = new Random();
		if (dialog != null)
			dialog.setMax(tasknewdata);
		for (int i = 0; i < tasknewdata; i++) {
			if (dialog != null)
				dialog.setProgress(i + 1);
			PhoneDataBean mPhoneDataBean = new PhoneDataBean();
			// 根据运营商进行判断
			String str = XpHelper.arrayzz[r.nextInt(XpHelper.arrayzz.length)];
			if ("中国电信".equals(str)) {
				mPhoneDataBean.setOperator("CHINA TELECOM");
				mPhoneDataBean.setMnc("03");
				mPhoneDataBean.setMcc("460");
				// 手机号
				mPhoneDataBean.setPhoneNumber(PrivacyManager
						.getRandomProp("TELECOM"));
				// IMSI
				mPhoneDataBean.setImsi(PrivacyManager
						.getRandomProp("TELECOM IMSI"));
				// ICCID
				mPhoneDataBean.setIccId(PrivacyManager
						.getRandomProp("TELECOM ICCID"));
			}
			if ("中国移动".equals(str)) {
				mPhoneDataBean.setOperator("CHINA MOBILE");
				mPhoneDataBean.setMnc("00");
				mPhoneDataBean.setMcc("460");
				// 手机号
				mPhoneDataBean.setPhoneNumber(PrivacyManager
						.getRandomProp("MOBILE"));
				// IMSI
				mPhoneDataBean.setImsi(PrivacyManager
						.getRandomProp("MOBILE IMSI"));
				mPhoneDataBean.setIccId(PrivacyManager
						.getRandomProp("MOBILE ICCID"));
			}

			if ("中国联通".equals(str)) {
				mPhoneDataBean.setOperator("CHINA UNICOM");
				mPhoneDataBean.setMnc("01");
				mPhoneDataBean.setMcc("460");
				// 手机号
				mPhoneDataBean.setPhoneNumber(PrivacyManager
						.getRandomProp("UNICOM"));
				// IMSI
				mPhoneDataBean.setImsi(PrivacyManager
						.getRandomProp("UNICOM IMSI"));
				// ICCID
				mPhoneDataBean.setIccId(PrivacyManager
						.getRandomProp("UNICOM ICCID"));
			}

			// Serial
			mPhoneDataBean.setSerial(PrivacyManager.getRandomProp("SERIAL"));
			// Latitude
			mPhoneDataBean.setLatitude(PrivacyManager.getRandomProp("LAT"));
			// Longitude
			mPhoneDataBean.setLongitude(PrivacyManager.getRandomProp("LON"));
			// Altitude
			mPhoneDataBean.setAltitude(PrivacyManager.getRandomProp("ALT"));
			// MacAddress
			mPhoneDataBean.setMacAddress(PrivacyManager.getRandomProp("MAC"));
			// BMacAddress
			mPhoneDataBean.setBMacAddress(PrivacyManager.getRandomProp("BMAC"));			
			// IpAddress
			mPhoneDataBean.setIpAddress(PrivacyManager
					.getRandomProp("IPADDRESS"));
			// Imei
			mPhoneDataBean.setImei(PrivacyManager.getRandomProp("IMEI"));

			// AndroidID
			mPhoneDataBean.setAndroidID(PrivacyManager
					.getRandomProp("ANDROID_ID"));

			// GsfId
			mPhoneDataBean.setGsfId(PrivacyManager.getRandomProp("GSF_ID"));
			// AdvertisementID
			mPhoneDataBean.setAdvertisementID(PrivacyManager
					.getRandomProp("AdvertisementID"));
			// Country
			mPhoneDataBean.setCountry("CN");
			// GsmCallID
			mPhoneDataBean.setGsmCallID(PrivacyManager
					.getRandomProp("GsmCallID"));
			// GsmLac
			mPhoneDataBean.setGsmLac(PrivacyManager.getRandomProp("GsmCallID"));

			// Ua
			mPhoneDataBean.setUa(PrivacyManager.getRandomProp("Ua"));
			// 后续添加 手机型号，制造商
			// datatype
			mPhoneDataBean
					.setDatatype(PrivacyManager.getRandomProp("DATATYPE"));

			// etAndroidCode
			mPhoneDataBean.setAndroidCode(PrivacyManager
					.getRandomProp("ANDROIDCODE"));

			// etSystemCode
			mPhoneDataBean.setSystemCode(PrivacyManager.getRandomProp("MODEL"));

			List<xpmodel> xpmodelsList = DBMgr.getInstance(
					ManagerCertermActivity.this).getXpmpdellist();
			if (xpmodelsList.size() > 0) {
				xpmodel mXpmodel = xpmodelsList.get(r.nextInt(xpmodelsList
						.size()));
				// model
				mPhoneDataBean.setModel(mXpmodel.getModel().toString());
				// 制造商
				mPhoneDataBean.setManufacturer(mXpmodel.getManufacturer()
						.toString());

				// 设备商
				mPhoneDataBean.setProduct(mXpmodel.getProduct().toString());
				mPhoneDataBean.setDensity(mXpmodel.getDensity().toString());
			} else {
				// model
				mPhoneDataBean.setModel(PrivacyManager.getRandomProp("MODEL"));
				// 制造商
				mPhoneDataBean.setManufacturer(PrivacyManager
						.getRandomProp("MODEL"));

				// 设备商
				mPhoneDataBean
						.setProduct(PrivacyManager.getRandomProp("MODEL"));
				mPhoneDataBean.setDensity(PrivacyManager
						.getRandomProp("DENSITY"));

			}

			mPhoneDataBean.setTimeapp(PrivacyManager.getRandomProp("TIMEAPP"));

			mPhoneDataBean.setSsid(PrivacyManager.getRandomProp("SSID"));
			mList.add(mPhoneDataBean);
		}
		return mList;
	}
	
	class InitPhoneInfoTable extends AsyncTask <Object, Integer, Void>{
        private ProgressDialog mProgressDialog;
        private int num = 10;
        
        protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(ManagerCertermActivity.this);
			mProgressDialog.setMessage(getString(R.string.msg_loading));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
        }

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Toast.makeText(mcontext, "数据库新增"+num+"条手机信息", Toast.LENGTH_LONG).show();
			mProgressDialog.dismiss();
		}

		@Override
		protected Void doInBackground(Object... params) {
			String snum = params[0].toString();
			if(!"".equals(snum)){
				num = Integer.valueOf(snum);
			}
			List<PhoneDataBean> listphones = CreateTaskDataFile(num, mProgressDialog);
			DBMgr.getInstance(ApplicationEx.getContextObject()).plInsert(
					listphones);
			return null;
		}
        
	}
}
