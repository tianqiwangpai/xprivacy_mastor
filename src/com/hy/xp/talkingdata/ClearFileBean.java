package com.hy.xp.talkingdata;

/**
 * 
 * @ClassName: ClearFileBean
 * @Description: TODO
 * @date Mar 22, 2015 3:53:36 PM
 * 
 */
public class ClearFileBean implements Comparable
{
	private String mFilePath;

	public String getmFilePath()
	{
		return mFilePath;
	}

	public void setmFilePath(String mFilePath)
	{
		this.mFilePath = mFilePath;
	}

	public ClearFileBean(String mFilePath)
	{
		super();
		this.mFilePath = mFilePath;
	}

	public ClearFileBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString()
	{
		return "ClearFileBean [mFilePath=" + mFilePath + "]";
	}

	@Override
	public int compareTo(Object another)
	{
		// TODO Auto-generated method stub
		ClearFileBean mBean = (ClearFileBean) another;
		return this.mFilePath.compareTo(mBean.mFilePath);
	}

}
