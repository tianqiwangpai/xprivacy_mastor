package com.hy.xp.app.task;


public class AppUid
{
	private int appUid;

	public int getAppUid()
	{
		return appUid;
	}

	public void setAppUid(int appUid)
	{
		this.appUid = appUid;
	}

	public AppUid(int appUid)
	{
		super();
		this.appUid = appUid;
	}

	public AppUid()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString()
	{
		return "AppUid [appUid=" + appUid + "]";
	}

}
