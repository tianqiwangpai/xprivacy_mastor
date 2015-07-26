package com.hy.xp.app.task;

import java.io.Serializable;

/**
 * 任务基本属性
 * 
 * @author Administrator
 */
public class TaskDesc implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "tb_task_desc";
	public static final String TASK_NAME = "task_name";
	public static final String TASK_DESC = "task_desc";

	private String task_name;
	private String task_desc;

	public String getTask_name()
	{
		return task_name;
	}

	public void setTask_name(String task_name)
	{
		this.task_name = task_name;
	}

	public String getTask_desc()
	{
		return task_desc;
	}

	public void setTask_desc(String task_desc)
	{
		this.task_desc = task_desc;
	}

	@Override
	public String toString()
	{
		return "TaskDesc [task_name=" + task_name + ", task_desc=" + task_desc + "]";
	}

}
