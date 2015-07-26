package com.hy.xp.app.file;

public class FileInfo
{
	public String fileName;
	public String filePath;

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	@Override
	public String toString()
	{
		return "FileInfo [fileName=" + fileName + ", filePath=" + filePath + "]";
	}

}
