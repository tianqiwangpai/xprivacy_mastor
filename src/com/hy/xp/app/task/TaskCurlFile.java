package com.hy.xp.app.task;

import java.io.Serializable;

public class TaskCurlFile implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "tb_task_curl_file";
	public static final String TASK_NAME = "task_name";
	public static final String TASK_CURL_FILE = "task_curl_file";

	private String task_name;
	private String task_curl_file;

	public String getTask_name()
	{
		return task_name;
	}

	public void setTask_name(String task_name)
	{
		this.task_name = task_name;
	}

	public String getTask_curl_file()
	{
		return task_curl_file;
	}

	public void setTask_curl_file(String task_curl_file)
	{
		this.task_curl_file = task_curl_file;
	}

	@Override
	public String toString()
	{
		return "TaskCurlFile [task_name=" + task_name + ", task_curl_file=" + task_curl_file + "]";
	}

}
