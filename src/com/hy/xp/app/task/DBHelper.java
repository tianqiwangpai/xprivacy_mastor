package com.hy.xp.app.task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{

	public DBHelper(Context context)
	{
		super(context, "task.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// 任务附加属性表	
		StringBuilder sb = new StringBuilder(" create table ").append(TaskAttribute.table_name).append(" ( ")
				.append(TaskAttribute.TASKNAME).append(" text, ").append(TaskAttribute.TASKDESC).append(" text, ")
				.append(TaskAttribute.TASKNEWDATA).append(" text, ").append(TaskAttribute.TASKNUMBER).append(" text, ")
				.append(TaskAttribute.TASKRETURNRATIO).append(" text, ").append(TaskAttribute.TASKDECLINEFLAG).append(" text, ")
				.append(TaskAttribute.TASKDECILNERATIO).append(" text, ").append(TaskAttribute.TASKDECILNEMIN).append(" text, ")
				.append(TaskAttribute.TASKNEXTDAYFLAG).append(" text, ").append(TaskAttribute.TASKNEXTDAYVISITINTERVAL).append(" text, ")
				.append(TaskAttribute.TASKNEXTDAYVISITINTERVALRETURNRATIO).append(" text, ")
				.append(TaskAttribute.TASKNEXTDAYVISITINTERVALCOUNT).append(" text, ")
				.append(TaskAttribute.TASKNEXTDAYVISITDECLINEFLAG).append(" int, ")
				.append(TaskAttribute.TASKNEXTDAYVISITDECILNERATIO).append(" text, ")
				.append(TaskAttribute.FILENAME).append(" text,").append(TaskAttribute.TASKNEXTDAYVISITDECILNEMIN).append(" int,")
				.append(TaskAttribute.TASKSTAYWAY).append(" int,").append(TaskAttribute.TASKNEXTDAYVISITSTAYWAY).append(" int,")
				
				.append(TaskAttribute.TASKNEXTWEEKFLAG).append(" int,")
				.append(TaskAttribute.TASKNEXTWEEKVISITINTERVALRETURNRATIO).append(" double,")
				.append(TaskAttribute.TASKNEXTWEEKVISITDECLINEFLAG).append(" int,")
				.append(TaskAttribute.TASKNEXTWEEKVISITDECILNERATIO).append(" double,")
				.append(TaskAttribute.TASKNEXTWEEKVISITDECILNEMIN).append(" int,")
				.append(TaskAttribute.TASKNEXTWEEKVISITSTAYWAY).append(" int,")
				
				.append(TaskAttribute.TASKNEXTMONTHFLAG).append(" int,")
				.append(TaskAttribute.TASKNEXTMONTHVISITINTERVALRETURNRATIO).append(" double,")
				.append(TaskAttribute.TASKNEXTMONTHVISITDECLINEFLAG).append(" int,")
				.append(TaskAttribute.TASKNEXTMONTHVISITDECILNERATIO).append(" double,")
				.append(TaskAttribute.TASKNEXTMONTHVISITDECILNEMIN).append(" int,")
				.append(TaskAttribute.TASKNEXTMONTHVISITSTAYWAY).append(" int);");
		db.execSQL(sb.toString());

		// 生成的数据文件
		StringBuilder curl = new StringBuilder(" create table ").append(TaskCurlFile.TABLE_NAME).append(" ( ").append(TaskCurlFile.TASK_NAME).append(" text, ").append(TaskCurlFile.TASK_CURL_FILE).append(" text ); ");
		db.execSQL(curl.toString());

		// 任务基本属性表
		StringBuilder datafile = new StringBuilder(" create table ").append(TaskDesc.TABLE_NAME).append(" ( ").append(TaskDesc.TASK_NAME).append(" text, ").append(TaskDesc.TASK_DESC).append(" text ); ");
		db.execSQL(datafile.toString());

		// 创建每日新增数据文件表
		StringBuilder dd = new StringBuilder(" create table ").append(DataBase.TABLE_NAME).append(" ( ").append(DataBase.TASK_NAME).append(" text, ").append(DataBase.TASK_DATA_FILE).append(" text ); ");
		db.execSQL(dd.toString());

		StringBuilder model = new StringBuilder(" create table ").append(xpmodel.TABLE).append(" ( ").append(xpmodel.MODEL).append(" text, ").append(xpmodel.MANUFACTURER).append(" text, ").append(xpmodel.FALG).append(" text, ").append(xpmodel.PRODUCT).append(" text, ").append(xpmodel.DENSITY).append(" text  ); ");
		db.execSQL(model.toString());

		StringBuilder phone = new StringBuilder(" create table ").append(PhoneDataBean.table_name).append(" ( ").append("_id").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ").append(PhoneDataBean.SERIAL).append(" text, ").append(PhoneDataBean.LATITUDE).append(" text, ").append(PhoneDataBean.LONGITUDE).append(" text, ").append(PhoneDataBean.ALTITUDE).append(" text, ")
				.append(PhoneDataBean.MACADDRESS).append(" text, ").append(PhoneDataBean.IPADDRESS).append(" text, ").append(PhoneDataBean.IMEI).append(" text, ").append(PhoneDataBean.PHONENUMBER).append(" text, ").append(PhoneDataBean.ANDROIDID).append(" text, ").append(PhoneDataBean.GSFID).append(" text, ").append(PhoneDataBean.ADVERTISEMENTID).append(" text, ").append(PhoneDataBean.MCC)
				.append(" text, ").append(PhoneDataBean.MNC).append(" text, ").append(PhoneDataBean.COUNTRY).append(" text, ").append(PhoneDataBean.OPERATOR).append(" text, ").append(PhoneDataBean.ICCID).append(" text, ").append(PhoneDataBean.GSMCALLID).append(" text, ").append(PhoneDataBean.GSMLAC).append(" text, ").append(PhoneDataBean.IMSI).append(" text, ").append(PhoneDataBean.SSID)
				.append(" text, ").append(PhoneDataBean.UA).append(" text, ").append(PhoneDataBean.MODEL).append(" text, ").append(PhoneDataBean.MANUFACTURER).append(" text, ").append(PhoneDataBean.PRODUCT).append(" text, ").append(PhoneDataBean.ANDROIDCODE).append(" text, ").append(PhoneDataBean.SYSTEMCODE).append(" text, ").append(PhoneDataBean.DATATYPE).append(" text, ")
				.append(PhoneDataBean.DENSITY).append(" text, ").append(PhoneDataBean.BMACADDRESS).append(" text  ); ");
		db.execSQL(phone.toString());

		
		// TODO 新增任务表 new
		StringBuilder ndd = new StringBuilder(
				"create table newdata(_id integer primary key autoincrement, taskid int, startdataid int, enddataid int, day int, hour int, current int);");
		db.execSQL(ndd.toString());

		// TODO 任务应用关系表
		StringBuilder taskapp = new StringBuilder(
				"create table taskapp(_id integer primary key autoincrement, taskname text, packagename text);");
		db.execSQL(taskapp.toString());

		// TODO 回访表
		StringBuilder backrecord = new StringBuilder(
				"create table backrecord(dataid int primary key, visted int);");
		db.execSQL(backrecord.toString());
		
		// TODO 应用程序列表
		StringBuilder applist = new StringBuilder(
				"create table applist(dataid integer primary key autoincrement, pkgname text, appname text, appicon text)");
		db.execSQL(applist.toString());
		
		// TODO 应用程序列表
		StringBuilder contact = new StringBuilder(
				"create table contact(dataid integer primary key autoincrement, name text, telephone text)");
		db.execSQL(contact.toString());
				
		// TODO 应用程序列表
		StringBuilder contact_call = new StringBuilder(
				"create table contact_call(dataid integer primary key autoincrement, telephone text, datetime long)");
		db.execSQL(contact_call.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(" drop table " + TaskAttribute.table_name + " if exits; ");
		db.execSQL(" drop table " + TaskCurlFile.TABLE_NAME + " if exits; ");
		db.execSQL(" drop table " + TaskDesc.TABLE_NAME + " if exits; ");
		db.execSQL(" drop table " + xpmodel.TABLE + " if exits; ");
		// db.execSQL(" drop table " + "appid" + " if exits; ");

		onCreate(db);
	}

}
