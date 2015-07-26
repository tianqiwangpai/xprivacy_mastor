package com.hy.xp.app.task;

import java.io.File;

public final class OperationFileHelper
{

	/**
	 * ?????дц??ф╗?
	 * 
	 * @param file
	 */

	public static void RecursionDeleteFile(File file)
	{
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}
}
