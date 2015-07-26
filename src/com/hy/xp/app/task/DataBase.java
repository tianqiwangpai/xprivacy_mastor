package com.hy.xp.app.task;

import java.io.Serializable;

/**
 * ??°æ?????ä»¶ï??æ¯?ä¸?å¤©ç????°å?????ä»?
 * 
 * @author Administrator
 */
public class DataBase implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_data_file";
	public static final String TASK_NAME = "task_name";
	public static final String TASK_DATA_FILE = "task_data_file";

	private String task_name;
	private String task_date_file;

	public String getTask_name()
	{
		return task_name;
	}

	public void setTask_name(String task_name)
	{
		this.task_name = task_name;
	}

	public String getTask_date_file()
	{
		return task_date_file;
	}

	public void setTask_date_file(String task_date_file)
	{
		this.task_date_file = task_date_file;
	}

	@Override
	public String toString()
	{
		return "DataBase [task_name=" + task_name + ", task_date_file=" + task_date_file + "]";
	}

}
